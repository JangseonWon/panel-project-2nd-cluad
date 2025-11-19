package com.gcgenome.lims.analysis.actor.tso

import com.gcgenome.lims.analysis.AbstractSnvNormalizer
import com.gcgenome.lims.analysis.FileReader
import java.nio.file.Path

interface Tso: FileReader, AbstractSnvNormalizer {
    fun batch(path: Path): String?
}