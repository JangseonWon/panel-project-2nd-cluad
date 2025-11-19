package com.gcgenome.test

import com.gcgenome.lims.interpretable.ZygosityUtil
import com.gcgenome.lims.interpretable.impl.Sex
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
internal class ZygosityUtilTest {
    @Test
    @DisplayName("Zygosity Normalization Test")
    internal fun test() {
        assertEquals("Hemizygous", ZygosityUtil.normalize("Hem", Sex.M, "chrX"))
        assertEquals("Heterozygous", ZygosityUtil.normalize("Het", Sex.M, "chrX"))
        assertEquals("Homozygous", ZygosityUtil.normalize("Hom", Sex.M, "chrX"))

        assertEquals("Hemizygous", ZygosityUtil.normalize("0/1", Sex.M, "chrX"))
        assertEquals("Hemizygous", ZygosityUtil.normalize("0|1", Sex.M, "chrX"))
        assertEquals("Hemizygous", ZygosityUtil.normalize("1/0", Sex.M, "chrX"))
        assertEquals("Hemizygous", ZygosityUtil.normalize("1/0", Sex.M, "chrX"))
        assertEquals("Hemizygous", ZygosityUtil.normalize("1/1", Sex.M, "chrX"))
        assertEquals("Hemizygous", ZygosityUtil.normalize("1|1", Sex.M, "chrX"))

        assertEquals("Homozygous", ZygosityUtil.normalize("1/1", Sex.F, "chrX"))
        assertEquals("Homozygous", ZygosityUtil.normalize("1|1", Sex.M, "chr1"))
        assertEquals("Heterozygous", ZygosityUtil.normalize("0/1", Sex.F, "chrX"))
        assertEquals("Heterozygous", ZygosityUtil.normalize("0|1", Sex.M, "chr1"))
        assertEquals("Heterozygous", ZygosityUtil.normalize("1/0", Sex.F, "chr1"))
        assertEquals("Heterozygous", ZygosityUtil.normalize("1|0", Sex.M, "chr1"))
    }
}