package com.gcgenome.lims.analysis.actor.hema.v1

import com.gcgenome.lims.analysis.AbstractSnvNormalizer
import com.gcgenome.lims.analysis.FileReader
import java.nio.file.Path

interface Hema: FileReader, AbstractSnvNormalizer {
    fun batch(path: Path): String?
}