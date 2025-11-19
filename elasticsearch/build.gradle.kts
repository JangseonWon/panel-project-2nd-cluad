plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
}
dependencies {
    implementation(libs.kotlin.reactor)
    api(project(":search"))
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
}
tasks.test {
    useJUnitPlatform()
}