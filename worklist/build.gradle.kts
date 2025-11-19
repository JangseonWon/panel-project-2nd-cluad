plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.cloud.tools.jib") version "3.4.5"
    id("com.adarshr.test-logger") version "4.0.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
}
dependencies {
    implementation(project(":domain"))
    implementation(project(":authorization"))
    implementation("com.gcgenome:sample-data:2025.08.25-2")
    implementation("com.gcgenome:search-r2dbc:2025.01.23")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation(libs.bundles.kotlin.webflux)
    implementation(libs.bundles.r2dbc.postgres)
    implementation(libs.spring.kafka)
    implementation("com.github.f4b6a3:ulid-creator:5.2.3")
    implementation("com.github.java-json-tools:json-patch:1.13")
    testImplementation(libs.bundles.test.api)
    testImplementation("io.kotest:kotest-framework-datatest:5.9.1")
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.kafka)
}
configurations { all { exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging") } }
dependencyManagement { imports { mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0") } }
tasks {
    jar {
        enabled = false
    }
    test {
        useJUnitPlatform()
    }
}
jib {
    container {
        environment = mapOf(
            "LANG" to "C.UTF-8",
            "TZ" to "Asia/Seoul",
        )
    }
}
kover {
    reports {
        verify {
            rule {
                disabled = false
                bound {
                    minValue = 0
                }
            }
        }
    }
}
