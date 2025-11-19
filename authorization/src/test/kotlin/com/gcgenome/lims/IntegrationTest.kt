package com.gcgenome.lims

import com.gcgenome.lims.testcontainer.Database
import com.gcgenome.lims.`interface`.api.UserAuthentication
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@SpringBootTest(properties = [
    "management.endpoint.health.probes.enabled=true",
    "logging.level.io.r2dbc.postgresql.QUERY=DEBUG",
    "logging.level.io.r2dbc.postgresql.PARAM=DEBUG",
]) @SpringBootConfiguration
@EnableAutoConfiguration
@AutoConfigureWebTestClient
@ComponentScan
@Import(IntegrationTest.Companion.MockController::class)
@Testcontainers
internal class IntegrationTest(private val client: WebTestClient, private val db: DatabaseClient): BehaviorSpec({
    Given("인증이 안 된 상태에서") {
        When("액추에이터 readiness 엔드포인트를 요청하면") {
            val req = client.get().uri("/actuator/health/readiness")
            Then("OK 코드와 응답을 리턴한다") {
                req.exchange().expectStatus().isOk
                   .expectBody(String::class.java)
                   .returnResult().responseBody shouldBe "{\"status\":\"UP\"}"
            }
        }
        When("액추에이터 liveness 엔드포인트를 요청하면") {
            val req = client.get().uri("/actuator/health/liveness")
            Then("OK 코드와 응답을 리턴한다") {
                req.exchange().expectStatus().isOk
                   .expectBody(String::class.java)
                   .returnResult().responseBody shouldBe "{\"status\":\"UP\"}"
            }
        }
        When("서비스에 접근하면") {
            val req = client.get().uri("/get")
            Then("Unauthorized 코드를 리턴한다") {
                req.exchange().expectStatus().isUnauthorized
                   .expectBody().isEmpty()
            }
        }
    }
    Given("존재하지 않는 사용자가") {
        val withUserAccount = client.get()
        withUserAccount.header("X-USER-ID", "invalid")
        When("서비스에 접근하면") {
            val req = withUserAccount.uri("/get")
            Then("Unauthorized 코드를 리턴한다") {
                req.exchange().expectStatus().isUnauthorized
                    .expectBody().isEmpty()
            }
        }
    }
    Given("비활성화된 사용자가") {
        val withUserAccount = client.get()
        withUserAccount.header("X-USER-ID", "inactive")
        When("서비스에 접근하면") {
            val req = withUserAccount.uri("/get")
            Then("Unauthorized 코드를 리턴한다") {
                req.exchange().expectStatus().isUnauthorized
                    .expectBody().isEmpty()
            }
        }
    }
    Given("Admin으로 인증된 상태에서") {
        val withUserAccount = client.get()
        withUserAccount.header("X-USER-ID", "admin")
        When("사용자 서비스에 접근하면") {
            val req = withUserAccount.uri("/get")
            Then("OK 코드와 결과를 반환한다") {
                req.exchange().expectStatus().isOk
                   .expectBody(String::class.java)
                   .returnResult().responseBody shouldBe "ROLE_MANAGER,ROLE_USER"
            }
        }
        When("Manager 서비스에 접근하면") {
            val req = withUserAccount.uri("/manager")
            Then("OK 코드와 결과를 반환한다") {
                req.exchange().expectStatus().isOk
                   .expectBody(Boolean::class.java)
                   .returnResult().responseBody shouldBe true
            }
        }
    }
    Given("Manager로 인증된 상태에서") {
        val withUserAccount = client.get()
        withUserAccount.header("X-USER-ID", "manager")
        When("사용자 서비스에 접근하면") {
            val req = withUserAccount.uri("/get")
            Then("OK 코드와 결과를 반환한다") {
                req.exchange().expectStatus().isOk
                   .expectBody(String::class.java)
                   .returnResult().responseBody shouldBe "ROLE_MANAGER,ROLE_USER"
            }
        }
        When("Manager 서비스에 접근하면") {
            val req = withUserAccount.uri("/manager")
            Then("OK 코드와 결과를 반환한다") {
                req.exchange().expectStatus().isOk
                   .expectBody(Boolean::class.java)
                   .returnResult().responseBody shouldBe true
            }
        }
    }
    Given("일반 사용자로 인증된 상태에서") {
        val withUserAccount = client.get()
        withUserAccount.header("X-USER-ID", "user")
        When("사용자 서비스에 접근하면") {
            val req = withUserAccount.uri("/get")
            Then("OK 코드와 결과를 반환한다") {
                req.exchange().expectStatus().isOk
                   .expectBody(String::class.java)
                   .returnResult().responseBody shouldBe "ROLE_USER"
            }
        }
        When("Manager 서비스에 접근하면") {
            val req = withUserAccount.uri("/manager")
            Then("Forbidden 코드를 리턴한다") {
                req.exchange().expectStatus().isForbidden
            }
        }
    }
}) {
    companion object {
        @RestController
        class MockController {
            @GetMapping("/get")
            fun getByUser(authentication: Authentication): Mono<String> = (authentication as UserAuthentication).roles.sorted().joinToString(",").toMono()
            @GetMapping("/manager")
            @PreAuthorize("hasRole('MANAGER')")
            fun getByManager(): Mono<Boolean> = Mono.just(true)
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            Database().registerDynamicProperties(registry)
        }
    }
}