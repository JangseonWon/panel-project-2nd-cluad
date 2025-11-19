package com.gcgenome.lims

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories

@SpringBootApplication
@EnableDiscoveryClient
@EnableReactiveElasticsearchRepositories
class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<Application>(*args)
        }
    }
}