plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
}
dependencies {
    implementation(libs.kotlin.jackson)
    implementation(libs.webflux)
    implementation(libs.r2dbc)
}
tasks.test {
    useJUnitPlatform()
}