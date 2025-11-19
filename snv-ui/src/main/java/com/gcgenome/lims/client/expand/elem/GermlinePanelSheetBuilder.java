package com.gcgenome.lims.client.expand.elem;

import elemental2.core.Global;
import jsinterop.base.Any;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.chart.Data;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.i18n.client.NumberFormat;
import elemental2.dom.DomGlobal;
import elemental2.dom.Window;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.ListElement;
import net.sayaya.ui.chart.Column;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.gcgenome.lims.client.expand.elem.SnvTableElement.*;

public class GermlinePanelSheetBuilder {
    public static SnvTableElement build() {
        return SnvTableElement.build(2, GermlinePanelSheetBuilder::map, colums);
    }
    private final static Column[] colums = new Column[] {
            dropdown("report", "D.class",
                            ListElement.singleLine().label("-"),
                            ListElement.singleLine().label("P"),
                            ListElement.singleLine().label("LP"),
                            ListElement.singleLine().label("VUS"),
                            ListElement.singleLine().label("LB"),
                            ListElement.singleLine().label("B"),
                            ListElement.singleLine().label("FP"),
                            ListElement.singleLine().label("*"),
                            ListElement.singleLine().label("+"),
                            ListElement.singleLine().label("?"),
                            ListElement.singleLine().label("."),
                            ListElement.singleLine().label("'")
                    ).pattern("-").than("", "")
                    .pattern("P").than("#FFFFFF", "#AD1742")
                    .pattern("LP").than("#FFFFFF", "#D26263")
                    .pattern("VUS").than( "#121212", "#FF8200")
                    .pattern("LB").than( "#FFFFFF", "#00AB84")
                    .pattern("B").than( "#FFFFFF", "#007B5F")
                    .pattern("FP").than( "#FFFFFF", "#DDDDDD")
                    .pattern("\\*").than("#FFFFFF", "#AD1742")
                    .pattern("\\+").than("#FFFFFF", "#D26263")
                    .pattern("\\?").than("#FFFFFF", "#FF8200")
                    .pattern("\\.").than("#FFFFFF", "#00AB84")
                    .pattern("\\'").than("#FFFFFF", "#007B5F")
                    .build(),
            text("tier", "Tier").build(),
            text("class", "Class").build(),
            snv("variant", "Id").build(),
            text("gene", "Gene").build(),
            link("pos_", "Pos", data->"http://localhost:60151\\goto?locus=" + data.get("pos_")).onClick(data->{
                Window popup = DomGlobal.window.open("http://localhost:60151\\goto?locus=" + data.get("pos_"), "_blank", "left=10000,top=100000,width=2,height=2,toolbar=0,resizable=0");
                Scheduler.get().scheduleFixedDelay(()->{
                    popup.close();
                    return false;
                }, 1000);
            }).build(),
            text("hgvsc", "HGVS.c").horizontal("left").build(),
            text("hgvsp", "HGVS.p").horizontal("left").build(),
            // ColumnSetBasic
            text("exon", "Exon").horizontal("left").build(),
            text("effect", "Effect").horizontal("center").build(),
            text("genotype", "Genotype").horizontal("center").font("JetBrains Mono").build(),
            number("depth", "Depth", NumberFormat.getFormat("0")).horizontal("right").font("JetBrains Mono").build(),
            number("vaf", "VAF", NumberFormat.getFormat("0.#####")).horizontal("right").font("JetBrains Mono").build(),
            text("filter", "Filter").horizontal("left").build(),
            text("sanger", "iSanger").horizontal("center").build(),
            text("mim.disease", "MIM_disease").horizontal("left").build(),
            text("mim.inheritance", "Inheritance").horizontal("left").build(),
            text("region_info", "Region info").horizontal("left").build(),
            // ColumnSetClass
            text("clinvar.class", "ClinVar Class").horizontal("left").build(),
            link("clinvarid", "ClinVar ID", data->"https://www.ncbi.nlm.nih.gov/clinvar/variation/" + data.get("clinvarid") + "/").onClick(data->{
                Window popup = DomGlobal.window.open("https://www.ncbi.nlm.nih.gov/clinvar/variation/" + data.get("clinvarid") + "/", "_blank", "left=10000,top=100000,width=2,height=2,toolbar=0,resizable=0");
                Scheduler.get().scheduleFixedDelay(()->{
                    popup.close();
                    return false;
                }, 1000);
            }).horizontal("center").build(),
            text("clinvar.updated_class", "ClinVar Class(Updated)").horizontal("left").build(),
            text("clinvar.updated_review", "ClinVar Review(Updated)").horizontal("left").build(),
            text("so", "Seq Ontology").horizontal("left").build(),
            text("intervar.class", "InterVar").horizontal("left").build(),
            text("intervar.evidence.pv", "InterVar PV").horizontal("left").build(),
            text("intervar.evidence.bv", "InterVar BV").horizontal("left").build(),
            text("hgmd.tag", "HGMD Tag").horizontal("left").build(),
            text("hgmd.pmid", "HGMD PMID").horizontal("left").build(),
            text("hgmd.web.tag", "HGMD Web Tag").horizontal("left").build(),
            text("qual", "Qual").horizontal("left").build(),
            // ColumnSetFreq
            text("gnomad_total_af", "gnomAD Total AF").horizontal("left").build(),
            text("gnomad_exome_all", "gnomAD_exomes_AF_ALL").horizontal("left").build(),
            text("gnomad_exomes_af_eas", "gnomAD_exomes_AF_EAS").horizontal("left").build(),
            text("gnomad_genomes_af_eas", "gnomAD_genomes_AF_EAS").horizontal("left").build(),
            text("gnomad_exomes_af_eas_kor", "gnomAD_exomes_AF_KOR").horizontal("left").build(),
            text("krgdb_af", "KRG_DB_AF").horizontal("left").build(),
            text("wes300_af", "WES300_freq").horizontal("left").build(),
            text("wes_als312_af", "WES_ALS312_AF").horizontal("left").build(),
            link("dbsnp", "dbSNP", data->"https://www.ncbi.nlm.nih.gov/snp/" + data.get("dbsnp")).onClick(data->{
                Window popup = DomGlobal.window.open("https://www.ncbi.nlm.nih.gov/snp/" + data.get("dbsnp"), "_blank", "left=10000,top=100000,width=2,height=2,toolbar=0,resizable=0");
                Scheduler.get().scheduleFixedDelay(()->{
                    popup.close();
                    return false;
                }, 1000);
            }).horizontal("left").build(),
            // ColumnSetPrediction
            text("sift.pred", "SIFT_pred").horizontal("left").build(),
            text("sift.score", "SIFT_score").horizontal("left").build(),
            text("polyphen.pred", "PolyPhen_pred").horizontal("left").build(),
            text("polyphen.score", "PolyPhen_score").horizontal("left").build(),
            text("mutationtaster.pred", "MutationTaster_pred").horizontal("left").build(),
            text("mutationtaster.score", "MutationTaster_score").horizontal("left").build(),
            text("ada_score", "ada_score").horizontal("left").build(),
            text("cadd.pred", "CADD_pred").horizontal("left").build(),
            text("dann.score", "DANN_score").horizontal("left").build(),
            text("gerp++.rs", "GERP++_RS").horizontal("left").build(),
            text("gerp++.gt2", "GERP++gt2").horizontal("left").build(),
            text("phylop20way_mammalian", "phyloP20way_mammalian").horizontal("left").build(),
            text("phastcons20way_mammalian", "phastCons20way_mammalian").horizontal("left").build(),
            text("siphy_29way_logodds", "SiPhy_29way_logOdds").horizontal("left").build(),
            text("apogee.pred", "APOGEE").horizontal("left").build(),
            text("apogee.score", "APOGEE Score").horizontal("left").build(),
            // ColumnSetDisease
            text("spliceai_ds_max", "SpliceAI_DS_Max").horizontal("left").build(),
            text("splicing_distance", "Splicing_distance").horizontal("left").build(),
            text("disease_description", "Disease_description").horizontal("left").build(),
            text("clinvar.clndbn", "ClinVar Disease").horizontal("left").build(),
            text("hgmd.web.literature", "HGMD_Web_Literature").horizontal("left").build(),
            text("hgmd.codon.disease", "HGMD_Codon_Disease").horizontal("left").build(),
    };

    static Data map(JsPropertyMap<?> map) {
        String id = map.get("id").toString();
        Data data = Data.create(id);
        Map<String, String> fieldMapping = createFieldMapping();
        data.put("report", determineReport(String.valueOf(map.get("reported"))));
        map.forEach(key->data.initialize(key, map.get(key).toString()));
        for (Map.Entry<String, String> entry : fieldMapping.entrySet()) {
            data.initialize(entry.getKey(), unwrapOrReplaceWithBlankIfNull(map.get(entry.getValue())));
        }
        data.initialize("pos_", unwrapOrReplaceWithBlankIfNull(map.get("chrom") + ":" + Long.parseLong((String) map.get("pos"))));
        data.initialize("krgdb_af", data.get("krgdb_af") == null ? unwrapOrReplaceWithBlankIfNull(map.get("krg_db_1100")) : data.get("krgdb_af"));
        data.onValueChange(evt->{
            var list = snvChanged.getValue();
            if(evt.value().isChanged()) list.add(data);
            else list.remove(data);
            snvChanged.next(list);
        });
        return data;
    }
    private static Map<String, String> createFieldMapping(){
        return new HashMap<>() {{
            put("variant", "snv");
            put("hgvsp", "hgvsp_in_mane");
            put("hgvsc", "hgvsc_in_mane");
            put("chr", "chrom");
            put("pos", "pos");
            put("ref", "ref");
            put("alt", "alt");
            put("tier", "tier");
            put("class", "class");
            put("gene", "gene.refgene");
            put("exon", "exon_in_mane");
            put("effect", "effect_level");
            put("genotype", "genotype");
            put("depth", "depth");
            put("vaf", "vaf");
            put("filter", "filter");
            put("sanger", "insilico_sanger");
            put("mim.disease", "mim.disease");
            put("mim.inheritance", "mim.inheritance");
            put("region_info", "region_info");
            put("gnomad_total_af", "gnomad.total");
            put("gnomad_exome_all", "gnomad.exome.all");
            put("gnomad_exomes_af_eas", "gnomad.exome.eas");
            put("gnomad_genomes_af_eas", "gnomad.genome.eas");
            put("gnomad_exomes_af_eas_kor", "gnomad.exome.eas_kor");
            put("krgdb_af", "krgdb_af");
            put("wes300_af", "wes300_af");
            put("wes_als312_af", "wes_als312_af");
            put("dbsnp", "dbsnp");
            put("sift.pred", "sift.pred");
            put("sift.score", "sift.score");
            put("polyphen.pred", "polyphen.pred");
            put("polyphen.score", "polyphen.score");
            put("mutationtaster.pred", "mutationtaster.pred");
            put("mutationtaster.score", "mutationtaster.score");
            put("ada_score", "ada_score");
            put("cadd.pred", "cadd.pred");
            put("dann.score", "dann.score");
            put("gerp++.rs", "gerp++.rs");
            put("gerp++.gt2", "gerp++.gt2");
            put("phylop20way_mammalian", "phylop20way_mammalian");
            put("phastcons20way_mammalian", "phastcons20way_mammalian");
            put("siphy_29way_logodds", "siphy_29way_logodds");
            put("apogee.pred", "apogee.pred");
            put("apogee.score", "apogee.score");
            put("spliceai_ds_max", "spliceai_ds_max");
            put("splicing_distance", "splicing_distance");
            put("disease_description", "disease_description");
            put("clinvar.clndbn", "clinvar.clndbn");
            put("hgmd.codon.disease", "hgmd.codon.disease");
            put("hgmd.web.literature", "hgmd.web.literature");
            put("clinvar.class", "clinvar.class");
            put("clinvarid", "clinvar.id");
            put("clinvar.updated_class", "clinvar.updated_class");
            put("clinvar.updated_review", "clinvar.updated_review");
            put("intervar.class", "intervar.class");
            put("intervar.evidence.pv", "intervar.evidence.pv");
            put("intervar.evidence.bv", "intervar.evidence.bv");
            put("hgmd.tag", "hgmd.tag");
            put("hgmd.pmid", "hgmd.pmid");
            put("hgmd.web.tag", "hgmd.web.tag");
            put("so", "so_term");
            put("qual", "qual");
        }};

    }

    private static String determineReport(String reported) {
        if (reported != null && reported.trim().length() > 5) {
            if (reported.contains("=P")) return "*";
            else if (reported.contains("=LP")) return "+";
            else if (reported.contains("=VUS")) return "?";
            else if (reported.contains("=B")) return "'";
            else if (reported.contains("=LB")) return ".";
        }
        return "-";
    }
    private final static BehaviorSubject<List<Data>> snvChanged = com.gcgenome.lims.client.Data.snvChanged;
    private static String unwrapOrReplaceWithBlankIfNull(Object obj) {
        if (obj == null) return "";
        String p = (String) obj;
        if (p.startsWith("[") && p.endsWith("]")) try {
            Any[] arr = Js.asArray(Global.JSON.parse(p));
            return Arrays.stream(arr).map(s -> String.valueOf(s)).distinct().collect(Collectors.joining(", "));
        } catch (Exception e) {return p;}
        else return p;
    }
}
