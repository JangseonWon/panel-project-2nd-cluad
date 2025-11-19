package com.gcgenome.lims.interpretable

import com.gcgenome.lims.dto.Disease
import com.gcgenome.lims.interpretable.variant.DiseaseImpl
import java.util.stream.Collectors

object DiseaseUtil {
    fun abbreviationShort(diseases: List<Disease>): String  = diseases.stream().map { it.abbreviation }.distinct().collect(Collectors.joining(", "))
    fun abbreviation(diseases: List<Disease>): List<String> = diseases.stream().map{ abbreviation(it) }.distinct().toList()
    private fun abbreviation(disease: Disease): String      = if (disease.fullName == disease.abbreviation) "" else "${disease.abbreviation}, ${disease.fullName}"
    fun map(disease: Disease): com.gcgenome.lims.interpretable.variant.Disease =DiseaseImpl(disease.fullName, disease.abbreviation, disease.inheritance.stream().filter(String::isNotBlank).collect(Collectors.joining(", ")))
}