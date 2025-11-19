plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.adarshr.test-logger") version "4.0.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
}
dependencies {
    implementation(libs.bundles.kotlin.webflux)
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation(libs.bundles.r2dbc.postgres)
    testImplementation(libs.bundles.test.api)
    testImplementation(libs.bundles.test.containers)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation(testFixtures(project(":testcontainer")))
}
tasks.test {
    useJUnitPlatform()
}
tasks.bootJar {
    enabled = false
}
kover.reports.verify.rule {
    disabled = false
    bound {
        minValue = 80
    }
}