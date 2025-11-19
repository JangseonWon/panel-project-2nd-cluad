package com.gcgenome.lims.analysis

import com.gcgenome.lims.analysis.entity.Analysis
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.document.Document
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class SnvManager (private val op: ReactiveElasticsearchTemplate, private val mutex: Mutex) {
    fun find(analysis: Analysis, id: String): Mono<Document> {
        val batch = analysis.batch
        val index = IndexCoordinates.of(toIndex(batch))
        return op.get(id, Document::class.java, index)
    }
    fun merge(analysis: Analysis, annotations: Flux<Document>): Flux<Document> {
        val batch = analysis.batch
        val index = IndexCoordinates.of(toIndex(batch))
        val createIndexIfNotPresent = mutex.acquire(batch).then(op.indexOps(index).exists()).flatMap {
            if(it.not()) index.create()
            else Mono.just(true)
        }.doFinally{ mutex.release(batch) }.onErrorResume { _ ->Mono.just(true)}
        val cleanThenBulkSave = annotations.map { it.toCleanDocument() }.bulkSave(1000, index)
        return createIndexIfNotPresent.thenMany(cleanThenBulkSave)
    }
    private fun Flux<Document>.bulkSave(size: Int, index: IndexCoordinates): Flux<Document> = this.window(size).flatMap {
        it.collectList().flatMapMany { bulk -> op.saveAll(bulk, index).thenMany(Flux.fromIterable(bulk)) }
    }
    private fun IndexCoordinates.create(): Mono<Boolean> = op.indexOps(this).create(ANALYSIS_SETTING, Document.from(ANALYSIS_MAPPING))
    companion object {
        protected val prefix = "analysis-snv"
        internal fun toIndex(batch: String): String          = "$prefix-$batch".lowercase()
        internal val ANALYSIS_MAPPING: Map<String, Any>
        internal val ANALYSIS_SETTING: Map<String, Any>
        private val KEYWORD = mapOf(Pair("type", "keyword"))
        private val KEYWORD_GLOBAL: Map<String, Any> = mapOf(Pair("type", "keyword"), Pair("eager_global_ordinals", true))
        private val DATE: Map<String, String> = mapOf(Pair("type", "date"))
        private val BOOLEAN: Map<String, String> = mapOf(Pair("type", "boolean"))
        private val INTEGER: Map<String, String> = mapOf(Pair("type", "integer"))
        private val LONG: Map<String, String> = mapOf(Pair("type", "long"))
        private val DOUBLE: Map<String, String> = mapOf(Pair("type", "double"))
        private val SHORT: Map<String, String> = mapOf(Pair("type", "short"))
        private val TEXT: Map<String, Any> = mapOf(
            Pair("type", "text"),
            Pair("fields", mapOf(
                Pair("keyword", mapOf(
                    Pair("type", "text")))
            ))
        )
        private fun TEXT(length: Int): Map<String, Any> = mapOf(
            Pair("type", "text"),
            Pair("fields", mapOf(
                Pair("keyword", mapOf(
                    Pair("type", "keyword"),
                    Pair("ignore_above", length)
                ))
            ))
        )
        private fun TEXT(length: Int, analyzer: String): Map<String, Any> = mapOf(
            Pair("type", "text"),
            Pair("analyzer", analyzer),
            Pair("fields", mapOf(
                Pair("keyword", mapOf(
                    Pair("type", "keyword"),
                    Pair("ignore_above", length)
                ))
            ))
        )
        init {
            val properties: MutableMap<String, Any> = mutableMapOf (
                Pair("analysis", KEYWORD),
                Pair("create_at", DATE),
                Pair("reference", KEYWORD),
                Pair("chrom", KEYWORD),
                Pair("pos", LONG),
            )
            properties["ref"] = TEXT(1024)
            properties["alt"] = TEXT(1024)
            properties["sample"] = KEYWORD
            properties["snv"] = KEYWORD_GLOBAL
            properties["tags"] = KEYWORD
            properties["hgvsc"] = TEXT(256, "escape_special_characters_analyzer")
            properties["hgvsp"] = TEXT(256, "escape_special_characters_analyzer")
            properties["genotype"] = KEYWORD
            properties["qual"] = INTEGER
            properties["depth"] = INTEGER
            properties["vaf"] = DOUBLE
            properties["class"] = KEYWORD
            properties["class_order"] = SHORT
            properties["tier"] = KEYWORD
            properties["dbsnp"] = KEYWORD
            properties["effect_level"] = KEYWORD
            properties["essential_gene"] = KEYWORD
            properties["insilico_sanger"] = KEYWORD
            properties["intervar_"] = KEYWORD
            properties["known_rec_info"] = KEYWORD
            properties["effect_level"] = KEYWORD
            properties["gene"] = mapOf(
                Pair("properties", mapOf(
                    Pair("refgene",  KEYWORD),
                    Pair("full_name", TEXT(256))
                )))
            properties["organism"] = KEYWORD
            properties["exon"] = TEXT(256, "escape_slash_analyzer")
            properties["exon_in_hgmd"] = TEXT(256, "escape_slash_analyzer")
            properties["splicing_distance"] = LONG
            properties.put("hgmd", mapOf( Pair("properties", mapOf(
                Pair("pmid", KEYWORD),
                Pair("hgvsc", TEXT(256, "escape_special_characters_analyzer")),
                Pair("hgvsp", TEXT(256, "escape_special_characters_analyzer")),
                Pair("mut", KEYWORD),
                Pair("tag", KEYWORD),
                Pair("web", mapOf(
                    Pair("properties", mapOf(
                        Pair("tag",  KEYWORD),
                        Pair("disease", TEXT(256)),
                        Pair("literature", TEXT(256))
                    ))
                )),Pair("codon", mapOf(
                    Pair("properties", mapOf(
                        Pair("tag",  KEYWORD),
                        Pair("disease", TEXT(256))
                    ))
                ))
            ))))
            properties["clinvar"] = mapOf( Pair("properties", mapOf(
                Pair("id", KEYWORD),
                Pair("class", TEXT(256)),
                Pair("clndbn", TEXT(256)),
                Pair("clnrevstat", TEXT(256)),
                Pair("clnsig", TEXT(256)),
                Pair("updated_class", TEXT(256)),
                Pair("updated_review", TEXT(256))
            )))
            properties["mim"] = mapOf( Pair("properties", mapOf(
                Pair("phenotype_id", TEXT(256)),
                Pair("disease", TEXT),
                Pair("inheritance", TEXT(256))
            )))
            properties["intervar"] = mapOf(Pair("properties", mapOf(
                Pair("class", KEYWORD),
                Pair("evidence_", TEXT(256, "escape_special_characters_analyzer")),
                Pair("evidence", mapOf(Pair("properties", mapOf(
                    Pair("bv",  TEXT(256, "escape_special_characters_analyzer")),
                    Pair("pv", TEXT(256, "escape_special_characters_analyzer"))
                )))))
            ))
            properties["disease_description"] = TEXT
            properties["function_description"] = TEXT
            //properties["strand"] = BOOLEAN
            properties["filter"] = KEYWORD
            properties["codon"] = TEXT(256, "escape_slash_analyzer")
            properties["1000g"] = DOUBLE
            properties["krg_db_1100"] = DOUBLE
            properties["krgdb_af"] = DOUBLE
            properties["gnomad"] = mapOf(Pair("properties", mapOf(
                Pair("exome", mapOf(Pair("properties", mapOf(
                    Pair("all", DOUBLE),
                    Pair("afr", DOUBLE),
                    Pair("amr", DOUBLE),
                    Pair("asj", DOUBLE),
                    Pair("eas", DOUBLE),
                    Pair("eas_kor", DOUBLE),
                    Pair("fin", DOUBLE),
                    Pair("nfe", DOUBLE),
                    Pair("oth", DOUBLE),
                    Pair("sas", DOUBLE)
                )))), Pair("genome", mapOf(Pair("properties", mapOf(
                    Pair("all", DOUBLE),
                    Pair("afr", DOUBLE),
                    Pair("amr", DOUBLE),
                    Pair("asj", DOUBLE),
                    Pair("eas", DOUBLE),
                    Pair("eas_kor", DOUBLE),
                    Pair("fin", DOUBLE),
                    Pair("nfe", DOUBLE),
                    Pair("oth", DOUBLE),
                    Pair("sas", DOUBLE)
                )))), Pair("total", DOUBLE)
                )))
            properties["go"] = mapOf(Pair("properties", mapOf(
                Pair("biological_process", TEXT),
                Pair("cellular_component", TEXT),
                Pair("molecular_function", TEXT)
            )))
            properties["mgi_mouse"] = mapOf(Pair("properties", mapOf(
                Pair("gene", KEYWORD),
                Pair("phenotype", TEXT(256))
            )))
            properties["esp6500"] = mapOf(Pair("properties", mapOf(
                Pair("all", DOUBLE)
            )))
            properties["exac"] = mapOf(Pair("properties", mapOf(
                Pair("all", DOUBLE),
                Pair("eas", DOUBLE),
                Pair("sas", DOUBLE)
            )))
            properties["cadd"] = mapOf(Pair("properties", mapOf(
                Pair("pred", DOUBLE),
                Pair("raw", DOUBLE),
                Pair("result", TEXT(24))
            )))
            properties["dann"] = mapOf(Pair("properties", mapOf(
                Pair("score", DOUBLE)
            )))
            properties["gerp++"] = mapOf(Pair("properties", mapOf(
                Pair("gt2", DOUBLE),
                Pair("rs", DOUBLE)
            )))
            /*properties["sift"] = mapOf(Pair("properties", mapOf(
                Pair("pred", KEYWORD),
                Pair("score", DOUBLE)
            )))*/
            properties["provean"] = mapOf(Pair("properties", mapOf(
                Pair("pred", KEYWORD),
                Pair("score", DOUBLE)
            )))
            /*properties["polyphen"] = mapOf(Pair("properties", mapOf(
                Pair("pred", KEYWORD),
                Pair("score", DOUBLE)
            )))*/
            properties["polyphen2"] = mapOf(Pair("properties", mapOf(
                Pair("hdiv", mapOf(Pair("properties", mapOf(
                    Pair("pred", KEYWORD),
                    Pair("score", DOUBLE)
                )))), Pair("hvar", mapOf(Pair("properties", mapOf(
                    Pair("pred", KEYWORD),
                    Pair("score", DOUBLE)
                )))), Pair("score", TEXT(256))
            )))
            properties["mutationtaster"] = mapOf(Pair("properties", mapOf(
                Pair("pred", KEYWORD),
                Pair("score", KEYWORD)
            )))
            properties["mutationassessor"] = mapOf(Pair("properties", mapOf(
                Pair("pred", KEYWORD),
                Pair("score", KEYWORD)
            )))
            properties["m-cap"] = mapOf(Pair("properties", mapOf(
                Pair("pred", KEYWORD),
                Pair("score", DOUBLE)
            )))
            properties["lrt"] = mapOf(Pair("properties", mapOf(
                Pair("pred", KEYWORD),
                Pair("score", DOUBLE)
            )))
            properties["metasvm"] = mapOf(Pair("properties", mapOf(
                Pair("pred", KEYWORD),
                Pair("score", DOUBLE)
            )))
            properties["metalr"] = mapOf(Pair("properties", mapOf(
                Pair("pred", KEYWORD),
                Pair("score", DOUBLE)
            )))
            properties["dbscsnv1"] = mapOf(Pair("properties", mapOf(
                Pair("1_ada_score", DOUBLE),
                Pair("1_rf_score", DOUBLE)
            )))
            properties["fathmmw"] = mapOf(Pair("properties", mapOf(
                Pair("pred", TEXT(256)),
                Pair("score", TEXT(256))
            )))
            properties["ncc"] = mapOf(Pair("properties", mapOf(
                Pair("class", TEXT(256)),
                Pair("interpret", TEXT),
                Pair("sample", TEXT(256))
            )))
            properties["apogee"] = mapOf(Pair("properties", mapOf(
                Pair("prob", KEYWORD),
                Pair("score", KEYWORD)
            )))
            properties["rmsk"] = TEXT(256, "escape_special_characters_analyzer")
            properties["p(hi)"] = DOUBLE
            properties["p(rec)"] = DOUBLE
            properties["phastcons20way_mammalian"] = DOUBLE
            properties["phylop20way_mammalian"] = DOUBLE
            properties["siphy_29way_logodds"] = DOUBLE
            properties["vest3_score"] = DOUBLE
            properties["trait_association(gwas)"] = TEXT(512)
            ANALYSIS_MAPPING = mapOf(Pair("properties", properties))
            ANALYSIS_SETTING = mapOf(Pair("index", mapOf(
                Pair("number_of_shards", "1"),
                Pair("number_of_replicas", "2"),
                Pair("sort.field", listOf("snv","create_at")),
                Pair("sort.order", listOf("asc", "desc")),
                Pair("analysis", mapOf(
                    Pair("analyzer", mapOf(
                        Pair("escape_special_characters_analyzer", mapOf(
                            Pair("type", "custom"),
                            Pair("filter", arrayOf(
                                "lowercase",
                                "stop",
                                "snowball"
                            )), Pair("char_filter", arrayOf("special_character_filter")),
                            Pair("tokenizer", "whitespace")
                        )), Pair("escape_slash_analyzer", mapOf(
                            Pair("type", "custom"),
                            Pair("filter", arrayOf(
                                "lowercase",
                                "stop",
                                "snowball"
                            )), Pair("char_filter", arrayOf("slash_filter")),
                            Pair("tokenizer", "whitespace")
                        ))
                    )), Pair("char_filter", mapOf(
                        Pair("special_character_filter", mapOf(
                            Pair("type", "mapping"),
                            Pair("mappings", arrayOf(
                                ": => _doublecolon_",
                                "> => _greaterthan_",
                                "< => _lessthan_",
                                ". => _dot_",
                                "= => _equals_",
                                "+ => _plus_",
                                "- => _minus_",
                                "( => _openbracket_",
                                ") => _closebracket_"
                            ))
                        )), Pair("slash_filter", mapOf(
                            Pair("type", "mapping"),
                            Pair("mappings", arrayOf("/ => _slash_"))
                        ))
                    ))
                ))
            )))
        }
        fun Document.toCleanDocument(): Document = this.apply {
            entries.removeIf { entry -> entry.value==null }
            remove("")
        }
    }
}