plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
}
dependencies {
    implementation("com.gcgenome:query-dsl-persist:1.3.1")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation(libs.bundles.kotlin.webflux)
    implementation(libs.bundles.r2dbc.postgres)
    implementation(libs.bundles.r2dbc.querydsl)
    implementation("org.springframework.security:spring-security-core:5.7.3")
    kapt(libs.bundles.r2dbc.querydsl)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}
kapt {
    keepJavacAnnotationProcessors = true
}
tasks.getByName<Jar>("jar") {
    enabled = false
}
tasks.processResources {
    if(project.gradle.startParameter.taskNames.contains("build")) exclude("application.yml")
}
tasks.bootJar {
    archiveFileName.set("panel-service-snv-marker.jar")
}