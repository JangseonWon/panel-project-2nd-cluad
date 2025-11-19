package com.gcgenome.lims.testcontainer

import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.GenericContainer

class Zookeeper {
    private val zookeeper: GenericContainer<*> = GenericContainer("zookeeper:latest").withExposedPorts(ZOOKEEPER_PORT)
    init {
        zookeeper.start()
    }
    fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
        val zkConnectionString = "${zookeeper.host}:${zookeeper.getMappedPort(ZOOKEEPER_PORT)}"
        registry.add("spring.cloud.zookeeper.connect-string") { zkConnectionString }
    }
    companion object {
        const val ZOOKEEPER_PORT = 2181
    }
}