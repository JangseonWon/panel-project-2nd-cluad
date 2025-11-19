package com.gcgenome.lims.analysis.actor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor
import org.apache.commons.io.monitor.FileAlterationMonitor
import org.apache.commons.io.monitor.FileAlterationObserver
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString


// Application이 주어진 Warden을 구독한다.
// 이벤트가 구독되면 이벤트를 JSON으로 변환하여 DB에 저장하고 카프카에 이벤트 메시지를 전달한다.
// 생성자 형식은 모두 다음과 동일하게 한다.
abstract class Warden (
    private val path: Path,
    interval: Long = 10000
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val monitor = FileAlterationMonitor(interval)
    suspend fun ward() {
        // 주어진 service와 path를 사용하여 지정된 Path를 모니터링한다.
        // 새로운 디렉토리가 감지되면 해당 경로를 감시하는 ward 함수를 호출한다.
        withContext(Dispatchers.IO) {
            runCatching {
                val service = FileAlterationObserver(path.absolutePathString())
                service.addListener(listener)
                monitor.addObserver(service)
            }
        }
        monitor.start()
        logger.info("Watcher starting for Path : $path")
    }
    private val listener = object: FileAlterationListenerAdaptor() {
        override fun onFileCreate(file: File) {
            logger.info("Create event detected at directory : ${file.path}")
            CoroutineScope(Dispatchers.IO).launch { create(file.toPath()) }
        }
    }
    abstract suspend fun create(path: Path)
}