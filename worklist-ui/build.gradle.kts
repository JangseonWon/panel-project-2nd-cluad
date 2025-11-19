import org.docstr.gwt.AbstractBaseTask

plugins {
    kotlin("jvm")
    id("war")
    id("org.docstr.gwt") version "2.1.6"
    id("com.adarshr.test-logger") version "4.0.0"
}
dependencies {
    implementation(libs.bundles.sayaya.web)
    implementation("com.gcgenome:gateway-api:1.0")
    implementation("com.gcgenome:lims-icon:2.1")
    implementation("com.gcgenome:search-gwt:2025.01.23")
    implementation("net.sayaya:chart:2.2")
    implementation("dev.sayaya:rx:2.0")
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.dagger.compiler)

    // testImplementation(libs.bundles.test.web)
    testAnnotationProcessor(libs.lombok)
    testAnnotationProcessor(libs.dagger.compiler)
}
val lombok = project.configurations.annotationProcessor.get().filter { it.name.startsWith("lombok") }.single()!!
sourceSets {
    named("main") {
        java.srcDirs("build/generated/sources/annotationProcessor/java/main")
    }
    named("test") {
        java.srcDirs("build/generated/sources/annotationProcessor/java/test")
    }
}
gwt {
    gwtVersion = "2.12.2"
    modules = listOf("com.gcgenome.lims.Worklist")
    sourceLevel = "auto"
    war = file("src/main/webapp")
}
tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    withType<AbstractBaseTask> {
        setJvmArgs(listOf("-javaagent:${lombok}=ECJ"))
    }
    test {
        useJUnitPlatform()
        jvmArgs = listOf("-javaagent:${lombok}=ECJ")
        systemProperty("kotest.framework.classpath.scanning.autoscan.disable", "true")
    }
    jar {
        enabled = false
    }
    withType<War> {
        dependsOn(gwtCompile)
        archiveFileName.set("panel-worklist-ui.war")
        duplicatesStrategy = DuplicatesStrategy.WARN
    }
}