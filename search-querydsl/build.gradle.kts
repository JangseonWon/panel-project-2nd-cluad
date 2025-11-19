plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
}
dependencies {
    implementation(project(":search"))
    implementation(libs.kotlin.jackson)
    implementation(libs.webflux)
    implementation(libs.r2dbc)
    implementation(libs.querydsl.core)
    implementation(libs.querydsl.r2dbc)
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}
tasks.test {
    useJUnitPlatform()
}