package com.gcgenome.lims.domain

import java.io.Serializable
import java.util.*

@JvmRecord
data class BatchSequencing (
    val id: UUID?,
    val name: String,
    val date: Long,
    val reverseComplement: Boolean = false,
    val adaptor: Adaptor = Adaptor(),
    val readLength: ReadLength,
    val items: List<Work>
): Serializable {
    @JvmRecord
    data class Adaptor(
        val read1: String = "AGATCGGAAGAGCACACGTCTGAACTCCAGTCA",
        val read2: String = "AGATCGGAAGAGCGTCGTGTAGGGAAAGAGTGT"
    )
    @JvmRecord
    data class ReadLength(
        val read1: Short,
        val read2: Short
    )
}