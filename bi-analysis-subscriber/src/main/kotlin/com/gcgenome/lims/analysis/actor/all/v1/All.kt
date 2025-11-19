package com.gcgenome.lims.analysis.actor.all.v1;

import com.gcgenome.lims.analysis.AbstractSnvNormalizer
import com.gcgenome.lims.analysis.FileReader
import java.nio.file.Path

interface All: FileReader, AbstractSnvNormalizer {
    fun batch(path: Path): String?
}
