package com.gcgenome.lims.analysis.actor.mm.v1

import com.gcgenome.lims.analysis.actor.Warden
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.moveTo

@Service("wardenMmV1")
class WardenMm (
    @Value("\${subscriber.mm.v1.path}") val path: Path,
    @Value("\${subscriber.mm.interval}") interval: Long = 10000,
    @Value("\${subscriber.processed}") val processed: Path,
    @Value("\${subscriber.error}") val error: Path,
    val readers: List<Mm>
) : Warden(path, interval) {
    private val logger = LoggerFactory.getLogger(javaClass)
    override suspend fun create(path: Path) = readers.filter { it.chkFormat(path) }.forEach {
        val batch = it.batch(path)
        try {
            val dest = if(batch!=null) Path.of(path.absolutePathString().replace(this.path.absolutePathString(), this.processed.resolve(batch).absolutePathString())) else null
            it.exec(path)
            if(dest!=null) {
                if (dest.parent.toFile().exists().not()) dest.parent.toFile().mkdirs()
                logger.info("move to: ${dest.absolutePathString()}")
                path.moveTo(dest)
            }
        } catch(e: Exception) {
            e.printStackTrace()
            val dest = if(batch!=null) Path.of(path.absolutePathString().replace(this.path.absolutePathString(), this.error.resolve(batch).absolutePathString())) else null
            if(dest!=null) {
                if (dest.parent.toFile().exists().not()) dest.parent.toFile().mkdirs()
                logger.info("move to: ${dest.absolutePathString()}")
                path.moveTo(dest)
            }
        }}
}