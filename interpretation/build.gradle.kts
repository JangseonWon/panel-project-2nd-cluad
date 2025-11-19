plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
}
dependencies {
    implementation("com.gcgenome:sample-data:2025.08.25-2")
    implementation("org.springframework.cloud:spring-cloud-starter-zookeeper-discovery")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("com.jcraft:jsch:0.1.55")
    implementation(libs.bundles.kotlin.webflux)
    implementation(libs.bundles.r2dbc.postgres)
    implementation(libs.bundles.r2dbc.querydsl)
    kapt(libs.bundles.r2dbc.querydsl)
    testImplementation(libs.bundles.test.api)
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
}
kapt {
    keepJavacAnnotationProcessors = true
}
configurations { all { exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging") } }
dependencyManagement { imports { mavenBom(libs.spring.cloud.bom.get().toString()) } }
tasks.getByName<Jar>("jar") {
    enabled = false
}
tasks.processResources {
    if(project.gradle.startParameter.taskNames.contains("build")) exclude("application.yml")
}
tasks.bootJar {
    archiveFileName.set("panel-service-interpretation2.jar")
}
tasks.test {
    useJUnitPlatform()
}
