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
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation(libs.kotlin.jackson)
    implementation(libs.spring.log4j2)
    implementation("org.springframework.cloud:spring-cloud-starter-kubernetes-fabric8")
    testImplementation(libs.bundles.test.api)
    testImplementation("io.fabric8:kubernetes-server-mock:6.13.4")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
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
jib {
    container {
        environment = mapOf(
            "LANG" to "C.UTF-8",
            "TZ" to "Asia/Seoul",
        )
    }
}