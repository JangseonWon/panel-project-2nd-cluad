package com.gcgenome.lims.service

import com.gcgenome.lims.elasticsearch.ElasticsearchDao
import com.gcgenome.lims.elasticsearch.SearchableElasticsearchDao
import com.gcgenome.lims.entity.Analysis
import com.gcgenome.lims.search.PageReactive
import com.gcgenome.lims.search.SearchParam
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.document.Document
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class SnvDao(em: ReactiveElasticsearchTemplate): SearchableElasticsearchDao(em) {
    private fun toIndex(batch: String): IndexCoordinates = IndexCoordinates.of(("analysis-snv-$batch").lowercase())
    fun findById(batch: String, id: String): Mono<Document> = toIndex(batch).findById(id)
    fun findByAnalysis(analysis: Analysis, param: SearchParam): Mono<PageReactive<Document>> {
        val analysisId = "${analysis.sheet}:${analysis.batch}:${analysis.row.toString().padStart(3, '0')}"
        param.filters.add(SearchParam.Companion.Filter("analysis", analysisId))
        return search(analysis.batch, param)
    }
    private fun search(batch: String, param: SearchParam): Mono<PageReactive<Document>> = toIndex(batch).search(param)
    override fun ElasticsearchDao.CriteriaBuilderOperator.toCriteria(filter: SearchParam.Companion.Filter): ElasticsearchDao.CriteriaBuilderOperator =
        if(filter.key==null || filter.key!!.isBlank()) this["*"].contains(filter.value!!.trim())
        else when(filter.key!!.lowercase()) {
        "analysis"  -> this[filter.key!!.trim()].`is`(filter.value!!.trim())
        "gene"      -> this["symbol"].`is`(filter.value!!.trim())
        "hgvsc"     -> this["hgvsc_in_mane"].`is`(filter.value!!.trim())
        "hgvsp"     -> this["hgvsp_in_mane"].`is`(filter.value!!.trim())
        "genes"     -> {
            val genes = filter.value!!.split(",").map(String::trim)
            this.includes("symbol", *genes.toTypedArray())
        }
        "genes.refgene"     -> {
            val genes = filter.value!!.split(",").map(String::trim)
            this.includes("gene.refgene", *genes.toTypedArray())
        }
        "panel" -> {
            val panelId = filter.value!!.trim()
            TODO()
        }
        "codon", "exon", "exon_in_hgmd" -> {
            TODO()
        }
        else -> {
            if(filter.isVariantFilter()) {
              val key = filter.key!!.lowercase()
              val value = filter.value!!
              val op = value.substring(0, value.indexOf(",")).trim()
              val param = value.substring(value.indexOf(",")+1).trim()
              when(op) {
                  "includes" -> this.includes(key, *param.escape().split(",").toTypedArray())
                  else -> TODO()
              }
            } else this[filter.key!!.trim().lowercase()].contains(filter.value!!.trim())
        }
    }
    private fun SearchParam.Companion.Filter.isVariantFilter(): Boolean {
        if (value == null || value!!.contains(",").not()) return false
        val op = value!!.substring(0, value!!.indexOf(","))
        if ("=" == op) return true
        if ("<" == op) return true
        if ("<=" == op) return true
        if (">=" == op) return true
        if (">" == op) return true
        if ("includes".equals(op, ignoreCase = true)) return true
        if ("excludes".equals(op, ignoreCase = true)) return true
        return if ("is empty".equals(op, ignoreCase = true)) true else "has value".equals(op, ignoreCase = true)
    }
    private fun ElasticsearchDao.CriteriaBuilderOperator.includes(column: String, vararg within: String): ElasticsearchDao.CriteriaBuilderOperator = this[column.trim().lowercase()].`in`(*within)
    private fun String.escape() = replace("/", "_slash_")
}