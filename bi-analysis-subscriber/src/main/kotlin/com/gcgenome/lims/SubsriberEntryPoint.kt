package com.gcgenome.lims

import com.gcgenome.lims.analysis.actor.Warden
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration

@Configuration
class SubsriberEntryPoint : CommandLineRunner {
    @Autowired(required = false)
    private lateinit var wardens: List<Warden>
    override fun run(vararg args: String?) {
        // 여러 액터가 정의되어 있으면, listen 함수를 통해 각각을 모든 와든에 연결한다
        runBlocking {
            wardens.map { launch{ it.ward() } }
        }
    }
}