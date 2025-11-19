rootProject.name = "panel-project-2nd"
include("domain")
include("service")
include("gateway")
include("gateway-gaia")
include("proxy-gaia")
include("authorization")
include("search")
include("elasticsearch")
include("worklist")
include("worklist-ui")
include("interpretation")
include("interpretation-ui")
include("bi-variant-service")
include("snv-marker")
include("bi-analysis-subscriber")
include("snv")
include("snv-ui")
include("variant-snv")
include("variant-snv-ui")
include("search-querydsl")
include("testcontainer")
include("event-broadcaster")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("reflect", "org.jetbrains.kotlin", "kotlin-reflect").withoutVersion()
            library("stdlib-jdk8", "org.jetbrains.kotlin", "kotlin-stdlib-jdk8").withoutVersion()
            bundle("kotlin", listOf("reflect", "stdlib-jdk8"))

            library("kotlin-reactor", "io.projectreactor.kotlin", "reactor-kotlin-extensions").withoutVersion()
            library("kotlin-coroutines-reactor", "org.jetbrains.kotlinx", "kotlinx-coroutines-reactor").withoutVersion()
            library("kotlin-jackson", "com.fasterxml.jackson.module", "jackson-module-kotlin").withoutVersion()
            library("webflux", "org.springframework.boot", "spring-boot-starter-webflux").withoutVersion()
            bundle("kotlin-webflux", listOf("reflect", "stdlib-jdk8", "kotlin-reactor", "kotlin-coroutines-reactor", "kotlin-jackson", "webflux"))

            library("r2dbc", "org.springframework.boot", "spring-boot-starter-data-r2dbc").withoutVersion()
            library("r2dbc-postgres", "org.postgresql", "r2dbc-postgresql").version { require("1.0.1.RELEASE") }
            bundle("r2dbc-postgres", listOf("r2dbc", "r2dbc-postgres"))

            library("querydsl-core", "com.querydsl", "querydsl-core").withoutVersion()
            library("querydsl-apt", "com.querydsl", "querydsl-apt").withoutVersion()
            library("querydsl-r2dbc", "com.infobip", "infobip-spring-data-r2dbc-querydsl-boot-starter").version { require("8.1.1") }
            bundle("r2dbc-querydsl", listOf("querydsl-core", "querydsl-apt", "querydsl-r2dbc"))

            library("jooq", "org.jooq", "jooq").withoutVersion()
            library("jooq-codegen", "org.jooq", "jooq-codegen").withoutVersion()
            library("jooq-meta", "org.jooq", "jooq-meta").withoutVersion()
            bundle("r2dbc-jooq", listOf("jooq", "jooq-codegen", "jooq-meta"))

            library("spring-log4j2", "org.springframework.boot", "spring-boot-starter-log4j2").withoutVersion()

            library("spring-boot-test", "org.springframework.boot", "spring-boot-starter-test").withoutVersion()
            library("reactor-test", "io.projectreactor", "reactor-test").withoutVersion()
            library("mockk", "io.mockk", "mockk").version{ require("1.13.16") }

            library("spring-kafka", "org.springframework.cloud", "spring-cloud-starter-stream-kafka").withoutVersion()
            library("spring-cloud-bom", "org.springframework.cloud", "spring-cloud-dependencies").version { require("2022.0.2") }

            library("elemento-core", "org.jboss.elemento", "elemento-core").version { require("1.6.11") }
            library("elemental2-svg", "com.google.elemental2", "elemental2-svg").version { require("1.2.3") }
            library("gwt-user", "org.gwtproject", "gwt-user").version { require("2.12.2") }
            library("gwt-dev", "org.gwtproject", "gwt-dev").version { require("2.12.2") }
            bundle("gwt", listOf("elemento-core", "elemental2-svg", "gwt-user"))
            library("lombok", "org.projectlombok", "lombok").version { require("1.18.36") }
            library("jackson-annotations", "com.fasterxml.jackson.core", "jackson-annotations").version { require("2.14.2") }

            library("kotest-runner", "io.kotest", "kotest-runner-junit5").version { require("5.9.1") }
            library("kotest-extensions-spring", "io.kotest.extensions", "kotest-extensions-spring").version { require("1.3.0") }
            bundle("test-api", listOf("reactor-test", "kotest-runner", "mockk", "kotest-extensions-spring", "spring-boot-test"))

            library("testcontainers-junit", "org.testcontainers", "junit-jupiter").withoutVersion()
            library("testcontainers-postgresql", "org.testcontainers", "postgresql").withoutVersion()
            library("testcontainers-kafka", "org.testcontainers", "kafka").version { require("1.20.4") }
            library("kotest-extensions-testcontainers", "io.kotest.extensions", "kotest-extensions-testcontainers").version { require("2.0.2") }
            bundle("test-containers", listOf("testcontainers-junit", "kotest-extensions-testcontainers"))

            library("dagger-gwt", "com.google.dagger", "dagger-gwt").version { require("2.51.1") }
            library("dagger-compiler", "com.google.dagger", "dagger-compiler").version { require("2.51.1") }

            library("sayaya-ui", "net.sayaya", "ui").version { require("4.2") }
            library("sayaya-rx", "dev.sayaya", "rx").version { require("2.0") }
            bundle("sayaya-web", listOf("elemento-core", "elemental2-svg", "gwt-user", "dagger-gwt", "dagger-compiler", "sayaya-ui", "sayaya-rx", "lombok"))
        }
    }
}
