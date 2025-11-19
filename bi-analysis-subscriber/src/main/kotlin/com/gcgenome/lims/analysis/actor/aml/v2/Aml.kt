package com.gcgenome.lims.analysis.actor.aml.v2

import com.gcgenome.lims.analysis.AbstractSnvNormalizer
import com.gcgenome.lims.analysis.FileReader
import java.nio.file.Path

interface Aml: FileReader, AbstractSnvNormalizer {
    fun batch(path: Path): String?
}