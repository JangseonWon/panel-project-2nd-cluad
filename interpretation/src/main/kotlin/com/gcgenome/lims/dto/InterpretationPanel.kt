package com.gcgenome.lims.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.gcgenome.lims.constants.Clazz
import java.util.*
import java.util.stream.Collectors

data class InterpretationPanel (
    val result: String?,
    val resultText: String?,
    val reasonForReferral: String?,
    val clinicalInformation: String?,
    val abbreviationReference: String?,
    val abbreviationDisease: String?,
    val abbreviation: String?,
    val interpretation: String?,
    val meanDepth: String?,
    val coverage: String?,
    val variants: List<Variant>,
    val recommendation: String?,
    val addendum: InterpretationPanel?,
    val incidentalFindings: InterpretationPanel?,
    val authors: List<Author> = emptyList(),
    val revision: Boolean = false
) {
    open class InterpretationPanelBuilder {
        var result: String? = null
        var resultText: String? = null
        var reasonForReferral: String? = null
        var clinicalInformation: String? = null
        private val abbreviationReference = Collections.synchronizedList(ArrayList<String>())
        private val abbreviationDisease = Collections.synchronizedList(ArrayList<String>())
        private val abbreviationInheritance = Collections.synchronizedList(ArrayList<String>())
        private val abbreviationZygosity = Collections.synchronizedList(ArrayList<String>())
        private val abbreviationClass = Collections.synchronizedList(ArrayList<String>())
        var interpretationPrefix: String? = null
        private val interpretation = Collections.synchronizedList(ArrayList<String>())
        var meanDepth: String? = null
        var coverage: String? = null
        val variants = Collections.synchronizedList(ArrayList<Variant>())
        var recommendation: String? = null
        var addendum: InterpretationPanel? = null
        var incidentalFindings: InterpretationPanel? = null
        var authors: List<Author> = emptyList()
        var revision: Boolean = false

        fun appendReference(abbreviationReference: String?)             = this.also { if (abbreviationReference != null) this.abbreviationReference.add(abbreviationReference) }
        fun appendDisease(abbreviationDiseases: List<String>)           = this.also { if (abbreviationDiseases != null) this.abbreviationDisease.addAll(abbreviationDiseases) }
        fun appendInheritance(abbreviationInheritances: List<String>)   = this.also { if (abbreviationInheritances != null) this.abbreviationInheritance.addAll(abbreviationInheritances) }
        fun appendZygosity(abbreviationZygosity: String?)               = this.also { if (abbreviationZygosity != null) this.abbreviationZygosity.add(abbreviationZygosity) }
        fun appendClass(abbreviationClass: String?)                     = this.also { if (abbreviationClass != null) this.abbreviationClass.add(abbreviationClass) }
        fun appendInterpretation(interpretation: String?)               = this.also { if (interpretation != null) this.interpretation.add(interpretation) }
        fun appendVariant(variant: Variant?)                            = this.also { if (variant != null) variants.add(variant) }
        fun build(): InterpretationPanel {
            val abbreviationReference = abbreviationReference.stream().distinct().filter(String::isNotBlank).distinct().collect(Collectors.joining("; "))
            val abbreviationDisease = abbreviationDisease.stream().distinct().filter(String::isNotBlank).distinct().collect(Collectors.joining("; "))
            val abbreviation = (abbreviationZygosity + abbreviationInheritance + abbreviationClass).stream().filter(String::isNotBlank).distinct().collect(Collectors.joining("; "))
            val interpretation = (if(interpretationPrefix!=null) "$interpretationPrefix\n\n" else "") + interpretation.stream().distinct().filter(String::isNotBlank).collect(Collectors.joining("\n\n"))
            return InterpretationPanel(
                result=result, resultText=resultText, reasonForReferral=reasonForReferral, clinicalInformation=clinicalInformation,
                abbreviationReference=abbreviationReference, abbreviationDisease=abbreviationDisease, abbreviation=abbreviation,
                interpretation=interpretation,
                meanDepth=meanDepth, coverage=coverage,
                variants=variants,
                recommendation=recommendation,
                addendum=addendum, incidentalFindings=incidentalFindings,
                authors=authors, revision=revision
            )
        }
    }
    companion object {
        fun builder(): InterpretationPanelBuilder = InterpretationPanelBuilder()
        fun empty(): InterpretationPanel =  InterpretationPanel(
            null, null, null, null,
            null, null, null,
            null,
            null, null,
            emptyList(),
            null,
            null, null,
            emptyList(), false
        )
    }

    data class Variant(
        val snv: String?,
        val analysis: String?,
        val gene: String,
        val hgvsc: String?,
        val hgvsp: String?,
        @JsonProperty("origin_hgvsc")
        val originHgvsc: String?,
        @JsonProperty("origin_hgvsp")
        val originHgvsp: String?,
        val zygosity: String?,
        val disease: String?,
        val diseaseFullName: String?,
        val inheritance: String?,
        val interpretation: String?,
        val genPhenDb: String?,
        @JsonProperty("class")
        val clazz: Clazz
    )
    data class Author (
        val name: String,
        val comment: String?
    )
}
