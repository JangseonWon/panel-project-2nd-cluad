package com.gcgenome.lims.analysis.actor.mm.v2;

import com.gcgenome.lims.analysis.AbstractSnvNormalizer
import com.gcgenome.lims.analysis.FileReader
import java.nio.file.Path

interface Mm: FileReader, AbstractSnvNormalizer {
    fun batch(path: Path): String?
}
