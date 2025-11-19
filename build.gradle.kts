plugins {
    id("java")
    kotlin("jvm") version "2.0.0" apply false
    kotlin("kapt") version "2.0.0" apply false
    kotlin("plugin.spring") version "2.0.0" apply false
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.github.com/GC-Genome/maven")  {
            credentials {
                username = project.findProperty("github_username") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("github_password") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
        mavenLocal()
    }
    group = "com.gcgenome"
    version = "1.0"
}
