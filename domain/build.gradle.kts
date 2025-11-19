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
    implementation("org.springframework.data:spring-data-commons")  // Page 클래스
    testImplementation(libs.bundles.test.api)
}
tasks.test {
    useJUnitPlatform()
}
kover.reports.verify.rule {
    disabled = false
    bound {
        minValue = 80
    }
}