package com.gcgenome.lims.interpretable

import com.gcgenome.lims.dto.Disease
import java.util.*
import java.util.stream.Collectors

object InheritanceUtil {
    private fun abbreviation(inheritance: String): List<String> {
        return inheritance.split(",").stream().map(String::trim).distinct()
            .map {
                val short = it.trim()
                val extend = extended(short)

                if(short == extend) null
                else if("<?>" == extend) null
                else "$short, $extend"
            }.filter(Objects::nonNull)
            .collect(Collectors.toList<String>())
    }
    fun abbreviationShort(diseases: List<Disease>): String  = diseases.stream().map { it.inheritance }.flatMap(List<String>::stream).distinct().filter(String::isNotBlank).collect(Collectors.joining(", "))
    fun abbreviation(diseases: List<Disease>): List<String> = diseases.stream().map { it.inheritance }.flatMap { it.stream() }.map { abbreviation(it) }.flatMap(List<String>::stream).distinct().filter(String::isNotBlank).toList()
    private fun extended(inheritance: String): String {
        return if (inheritance.contains("AD")) "Autosomal dominant"
        else if (inheritance.contains("AR")) "Autosomal recessive"
        else if (inheritance.contains("XLR")) "X-linked recessive"
        else if (inheritance.contains("XR")) "X-linked recessive"
        else if (inheritance.contains("XLD")) "X-linked dominant"
        else if (inheritance.contains("XD")) "X-linked dominant"
        else if (inheritance.contains("XL")) "X-linked"
        else if (inheritance.contains("DD")) "Digenic dominant"
        else if (inheritance.contains("DR")) "Digenic recessive"
        else if (inheritance.contains("SMu")) "Somatic mutation"
        else "<?>"
    }
}