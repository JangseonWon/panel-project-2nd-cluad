plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
}
dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-zookeeper-discovery")
    implementation(libs.spring.log4j2)
}
configurations { all { exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging") } }
dependencyManagement { imports { mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0") } }
tasks.getByName<Jar>("jar") {
    enabled = false
}
tasks.processResources {
    if(project.gradle.startParameter.taskNames.contains("build")) exclude("application.yml")
}
tasks.bootJar {
    archiveFileName.set("panel-proxy-gaia.jar")
}