package com.gcgenome.lims.analysis.actor.hrd

import com.gcgenome.lims.analysis.AbstractSnvNormalizer
import com.gcgenome.lims.analysis.FileReader
import java.nio.file.Path

interface Hrd: FileReader, AbstractSnvNormalizer {
    fun batch(path: Path): String?
}