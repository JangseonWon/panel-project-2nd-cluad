plugins {
    kotlin("jvm")
    id("org.wisepersist.gwt") version "1.1.18"
    id("java")
    id("war")
}

group = "com.gcgenome"
version = "1.0"

dependencies {
    implementation("com.gcgenome:sample-data:2025.08.25-2")
    implementation("com.gcgenome:sample-panel:1.0")
    implementation("com.gcgenome:gateway-api:1.0")
    implementation("com.gcgenome:gateway-service:1.0")
    implementation("com.gcgenome:lims-icon:2.1")

    implementation(libs.bundles.gwt)
    compileOnly(libs.gwt.dev)
    implementation("net.sayaya:ui:4.2")
    implementation("net.sayaya:chart:2.2")
    implementation("net.sayaya:rx:1.6")
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
}
val lombok = project.configurations.annotationProcessor.get().filter { it.name.startsWith("lombok") }.single()
tasks {
    withType<Delete> { doFirst { delete("build/") } }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    gwt {
        gwt.modules = listOf("com.gcgenome.lims.Snv")
        minHeapSize = "1024M"
        maxHeapSize = "2048M"
        sourceLevel = "auto"
        extraJvmArgs = listOf("-javaagent:${lombok}=ECJ")
        gwt.jsInteropExports.setGenerate(true)
        compiler.apply {
            strict = true
            disableClassMetadata = true
            disableCastChecking = true
        }
    }
    gwtDev {
        extraJvmArgs = listOf("-XX:ReservedCodeCacheSize=512M","-javaagent:${lombok}=ECJ")
        port = 9631
        codeServerPort = 9632
        war = file("src/main/webapp")
    }
    register<Copy>("copyWebResources") {
        dependsOn(build)
        from(zipTree("build/libs/panel-service-snv.war")) {
            include("**/*.js")
            include("**/*.css")
            include("**/*.png")
            include("**/*.gif")
            include("**/*.svg")
            include("**/*.ttf")
            include("**/*.woff")
            include("**/*.woff2")
            include("**/*.eot")
            include("*.ico")
            include("*.html")
            includeEmptyDirs = false
        }
        into("build/static")
    }
    withType<War> {
        archiveFileName.set("panel-service-snv.war")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    getByName<Test>("test") {
        useJUnitPlatform()
    }
}
