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
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation(libs.bundles.kotlin.webflux)
    implementation(libs.bundles.r2dbc.postgres)
    implementation(libs.spring.kafka)
    testImplementation(libs.bundles.test.api)
    testImplementation(libs.bundles.test.containers)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.kafka)
    testImplementation("com.fasterxml.jackson.core:jackson-annotations:2.18.1")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.1")
    testImplementation(testFixtures(project(":testcontainer")))
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
                    minValue = 80
                }
            }
        }
    }
}
