plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("java-test-fixtures")
}
dependencies {
    testFixturesApi(libs.bundles.test.api)
    testFixturesApi(libs.bundles.test.containers)

    testFixturesImplementation(libs.testcontainers.postgresql)
}