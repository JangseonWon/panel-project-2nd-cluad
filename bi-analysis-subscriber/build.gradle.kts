plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
}
dependencies {
    implementation(libs.stdlib.jdk8)
    implementation(libs.kotlin.reactor)
    implementation(libs.kotlin.coroutines.reactor)
    implementation(libs.kotlin.jackson)
    implementation(libs.spring.kafka)
    implementation(libs.bundles.r2dbc.postgres)
    implementation(libs.spring.log4j2)
    testImplementation(libs.bundles.test.api)
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.gcgenome:jandi-webhook:1.0.2")
    implementation("com.gcgenome:workflow-publisher:2025.3.19")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

    testRuntimeOnly("org.postgresql:postgresql")
    testImplementation("org.testcontainers:r2dbc:1.18.1")
    testImplementation("org.testcontainers:postgresql:1.18.1")
    testImplementation("org.testcontainers:elasticsearch:1.18.1")
}
configurations { all { exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging") } }
dependencyManagement { imports { mavenBom(libs.spring.cloud.bom.get().toString()) } }
kapt {
    keepJavacAnnotationProcessors = true
}
tasks.processResources {
    if(project.gradle.startParameter.taskNames.contains("build")) exclude("application.yml")
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}
tasks.test {
    useJUnitPlatform()
}
tasks.bootJar {
    archiveFileName.set("panel-project-bi-analysis-agent.jar")
}
tasks.getByName<Jar>("jar") {
    enabled = false
}