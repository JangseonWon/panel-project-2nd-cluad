package com.gcgenome.lims.interpretable

import com.gcgenome.lims.dto.Disease
import com.gcgenome.lims.dto.InterpretationPanel
import com.gcgenome.lims.interpretable.variant.Snv
import com.gcgenome.lims.interpretable.variant.SnvImpl
import org.springframework.data.elasticsearch.core.document.Document
import java.util.stream.Collectors

object SnvUtil: DocumentUtil {
    fun toSnv(values: Triple<InterpretationPanel.Variant, List<Disease>, Document?>): Snv = toSnv(values.first, values.second, values.third)
    fun toSnv(variant: InterpretationPanel.Variant, diseases: List<Disease>, document: Document?): Snv {
        val gnomad          = document?.percentage("gnomad.total")
        val krgdb           = document?.percentage("krgdb_af")
        val sift            = document?.getStringUnwrap("sift.pred")
        val polyphen        = document?.getStringUnwrap("polyphen.pred")
        val mutationtaster  = document?.getStringUnwrap("mutationtaster.pred")
        val clinvarId       = document?.getStringUnwrap("clinvar.id")
        val clinvarClass    = document?.getStringUnwrap("clinvar.class")
        val hgmdDisease     = document?.getStringUnwrap("hgmd.codon.disease")
        val hgmdPmid        = document?.getStringUnwrap("hgmd.pmid")
        val disease         = diseases.stream().map(DiseaseUtil::map).collect(Collectors.toList())
        val interpretation  = variant.interpretation
        return SnvImpl(
            variant.gene,
            variant.hgvsc!!, variant.hgvsp,
            variant.originHgvsc!!, variant.originHgvsp,
            variant.zygosity, disease, variant.clazz,
            gnomad, krgdb,
            sift, polyphen, null, mutationtaster,
            clinvarId, clinvarClass,
            hgmdDisease, hgmdPmid,
            interpretation)
    }

    fun toSnvBrcaCancer(values: Triple<InterpretationPanel.Variant, List<Disease>, Document?>): Snv = toSnvBrcaCancer(values.first, values.second, values.third)
    fun toSnvBrcaCancer(variant: InterpretationPanel.Variant, diseases: List<Disease>, document: Document?): Snv {
        val gnomad          = document?.percentage("gnomad.total")
        val krgdb           = document?.percentage("krgdb_af")
        val sift            = document?.getStringUnwrap("sift.pred")
        val polyphen2       = document?.getStringUnwrap("polyphen.pred")
        val mutationtaster  = document?.getStringUnwrap("mutationtaster.pred")
        val clinvarId       = document?.getStringUnwrap("clinvar.id")
        val clinvarClass    = document?.getStringUnwrap("clinvar.class")
        //val hgmdDisease     = document?.getStringUnwrap("hgmd_codon_disease")
        //val hgmdPmid        = document?.getStringUnwrap("hgmd.pmid")
        val disease         = diseases.stream().map(DiseaseUtil::map).collect(Collectors.toList())
        val interpretation  = variant.interpretation
        return SnvImpl(
            gene=variant.gene,
            hgvsc=variant.hgvsc!!, hgvsp=variant.hgvsp,
            originHgvsc = variant.originHgvsc!!, originHgvsp = variant.originHgvsp,
            zygosity=variant.zygosity, disease=disease, clazz=variant.clazz,
            gnomad=gnomad, krgdb=krgdb,
            sift=sift, polyphen=null, polyphen2=polyphen2, mutationtaster=mutationtaster,
            clinvarId=clinvarId, clinvarClass=clinvarClass,
            /*hgmdDisease, hgmdPmid*/
            interpretation=interpretation)
    }
    fun toSnvBrcaCancerOld(values: Triple<InterpretationPanel.Variant, List<Disease>, Document?>): Snv = toSnvBrcaCancerOld(values.first, values.second, values.third)
    fun toSnvBrcaCancerOld(variant: InterpretationPanel.Variant, diseases: List<Disease>, document: Document?): Snv {
        val gnomad          = document?.percentage("gnomad.exome.all")
        val krgdb           = document?.percentage("krg_db_1100")
        val sift            = document?.getStringUnwrap("sift.pred")
        val polyphen2       = document?.getStringUnwrap("polyphen2.hvar.pred")
        val mutationtaster  = document?.getStringUnwrap("mutationtaster.pred")
        val clinvarId       = document?.getStringUnwrap("clinvar.id")
        val clinvarClass    = document?.getStringUnwrap("clinvar.class")
        //val hgmdDisease     = document?.getStringUnwrap("hgmd_codon_disease")
        //val hgmdPmid        = document?.getStringUnwrap("hgmd.pmid")
        val disease         = diseases.stream().map(DiseaseUtil::map).collect(Collectors.toList())
        val interpretation  = variant.interpretation
        return SnvImpl(
            gene=variant.gene,
            hgvsc=variant.hgvsc!!, hgvsp=variant.hgvsp,
            originHgvsc = variant.originHgvsc!!, originHgvsp = variant.originHgvsp,
            zygosity=variant.zygosity, disease=disease, clazz=variant.clazz,
            gnomad=gnomad, krgdb=krgdb,
            sift=sift, polyphen=null, polyphen2=polyphen2, mutationtaster=mutationtaster,
            clinvarId=clinvarId, clinvarClass=clinvarClass,
            /*hgmdDisease, hgmdPmid*/
            interpretation=interpretation)
    }
}