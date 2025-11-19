package com.gcgenome.lims.analysis.actor.all.v2;

import com.gcgenome.lims.analysis.AbstractSnvNormalizer
import com.gcgenome.lims.analysis.FileReader
import java.nio.file.Path

interface All: FileReader, AbstractSnvNormalizer {
    fun batch(path: Path): String?
}
