# 신규 패널 추가 가이드 (운영 + 소스 분석 기반)

> 15년차 개발자를 위한 실전 운영 가이드
> 실제 소스 코드 분석 기반으로 작성

---

## 📋 목차

1. [프로젝트 소스 구조 분석](#1-프로젝트-소스-구조-분석)
2. [신규 패널 추가 절차](#2-신규-패널-추가-절차)
3. [운영 관점 체크리스트](#3-운영-관점-체크리스트)
4. [배포 및 롤백 런북](#4-배포-및-롤백-런북)
5. [모니터링 및 알람 설정](#5-모니터링-및-알람-설정)
6. [장애 대응 플레이북](#6-장애-대응-플레이북)

---

## 1. 프로젝트 소스 구조 분석

### 1.1 실제 소스 코드 구조

```
interpretation/
├── src/main/kotlin/com/gcgenome/lims/
│   ├── service/
│   │   ├── Router.kt                          # 패널 라우팅 등록 (28-34라인)
│   │   └── Handler.kt                         # 해석 핸들러
│   ├── interpretable/impl/
│   │   ├── RareDisease.kt                     # 희귀질환 패널 구현체
│   │   ├── SomaticCancer.kt                   # 암 패널 구현체
│   │   ├── Single.kt                          # 단일 유전자 패널
│   │   └── AbstractPanel.kt                   # 공통 추상 클래스
│   └── test/
│       └── (패널별 enum 정의)

bi-analysis-subscriber/
├── src/main/kotlin/com/gcgenome/lims/analysis/actor/
│   ├── all/v1/
│   │   ├── WardenAll.kt                       # 파일 감시자
│   │   └── All.kt                             # 파일 처리 인터페이스
│   ├── hema/v1/
│   │   ├── WardenHema.kt
│   │   └── Hema.kt
│   └── Warden.kt                              # 추상 Warden

gateway/
└── src/main/resources/application.yml         # 라우팅 설정 (port: 19643)
```

### 1.2 실제 코드 패턴 분석

#### Router.kt (interpretation/src/main/kotlin/com/gcgenome/lims/service/Router.kt)

```kotlin
// 28-34라인: 패널 등록 핵심 코드
private val services: Map<String, Any> = (
    RareDiseasePanel.values() +
    SingleGenePanel.values() +
    SingleGenePanelWithMlpa.values() +
    GenePlusPanel.values() +
    BloodCancerPanel.values() +
    SolidTumorPanel.values() +
    NonTSO.values() +
    GenomeScreen.values() +
    Wes.values() +
    WesWithSingleGene.values() +
    Sanger.values() +
    Des.values() +
    Mrd.values() +
    Hrd.values() +
    AlloSeq.values() +
    Cancerch.values() +
    Guardant.values() +
    `FLT3-ITD`.values() +
    Ballondor.values()
    // ↑ 여기에 신규 패널 추가
).stream().collect(Collectors.toMap(HasCode::code, identity()))

// API 엔드포인트
GET /services/{service}                                    # 패널 정보 조회
GET /samples/{sample}/services/{service}/interpretation    # 해석 결과 조회
PUT /samples/{sample}/services/{service}/interpretation    # 해석 결과 저장
PUT /samples/{sample}/services/{service}/auto-interpret    # 자동 해석 실행
GET /samples/{sample}/services/{service}/negative-interpret # 음성 결과
DELETE /samples/{sample}/services/{service}/interpretation  # 해석 삭제
```

#### RareDisease.kt 패턴 분석

```kotlin
// interpretation/src/main/kotlin/com/gcgenome/lims/interpretable/impl/RareDisease.kt
@Component
class RareDisease(
    om: ObjectMapper,
    requestDao: RequestDao,
    snvRepo: SnvDao,
    inserts: List<Insert>
) : AbstractPanel<RareDiseasePanel>(om, requestDao, snvRepo, inserts) {

    override fun chk(sample: Long, service: String): Boolean {
        // 특정 패널 제외
        if ("ON001".equals(service, ignoreCase = true)) return false
        if ("ON040".equals(service, ignoreCase = true)) return false
        return RareDiseasePanel.values().map{t->t.code()}.any(service::equals)
    }

    override fun test(service: String): RareDiseasePanel =
        RareDiseasePanel.values().first { service == it.code() }

    override fun referralDefault(service: String): HasReferralDefault = test(service)
}
```

#### SomaticCancer.kt 패턴 분석

```kotlin
// interpretation/src/main/kotlin/com/gcgenome/lims/interpretable/impl/SomaticCancer.kt
@Component
class SomaticCancer(private val om: ObjectMapper): Interpretable {

    override fun chk(sample: Long, service: String): Boolean {
        for (test in BloodCancerPanel.values())
            if (test.code().equals(service, ignoreCase = true)) return true
        return false
    }

    private fun test(service: String): SomaticCancerPanel {
        for (test in BloodCancerPanel.values())
            if (test.code().equals(service, ignoreCase = true)) return test
        throw RuntimeException()
    }

    override fun interpret(sample: Long, service: String, param: Map<*, *>): Mono<*> {
        if(param.isEmpty()) return negative(sample, service)
        val param = om.convertValue(param, InterpretationSomatic::class.java)
        return interpret(sample, service, param)
    }

    fun interpret(sample: Long, service: String, interpretation: InterpretationSomatic): Mono<InterpretationSomatic> {
        val test = test(service)
        interpretation.cancerType = "R/O ${test.referralDefault()}"
        return Mono.just(interpretation)
    }
}
```

---

## 2. 신규 패널 추가 절차

### 예제: 폐암 패널 (L001) 추가

### STEP 1: 패널 Enum 정의

**위치:** `interpretation/src/main/kotlin/com/gcgenome/lims/test/`

```kotlin
package com.gcgenome.lims.test

enum class LungCancerPanel(
    private val code: String,
    private val description: String
) : HasCode, SomaticCancerPanel {

    L001("L001", "Lung Cancer Panel v1"),
    L002("L002", "Lung Cancer Panel v2");

    override fun code() = code
    override fun toString() = description
    override fun referralDefault() = "Lung Cancer"
}
```

### STEP 2: 해석 로직 구현

**위치:** `interpretation/src/main/kotlin/com/gcgenome/lims/interpretable/impl/`

```kotlin
package com.gcgenome.lims.interpretable.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.dto.InterpretationSomatic
import com.gcgenome.lims.test.LungCancerPanel
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class LungCancer(
    private val om: ObjectMapper,
    private val snvDao: SnvDao
) : Interpretable {

    override fun chk(sample: Long, service: String): Boolean {
        return LungCancerPanel.values()
            .any { it.code().equals(service, ignoreCase = true) }
    }

    private fun test(service: String): LungCancerPanel {
        return LungCancerPanel.values()
            .first { it.code().equals(service, ignoreCase = true) }
    }

    override fun interpret(sample: Long, service: String, param: Map<*, *>): Mono<*> {
        if(param.isEmpty()) return negative(sample, service)
        val interpretation = om.convertValue(param, InterpretationSomatic::class.java)
        return interpret(sample, service, interpretation)
    }

    fun interpret(sample: Long, service: String, interpretation: InterpretationSomatic): Mono<InterpretationSomatic> {
        val test = test(service)
        interpretation.cancerType = "R/O ${test.referralDefault()}"

        // 폐암 특화 로직 추가
        // - Tier 분류
        // - TMB 계산
        // - 약물 매칭

        return Mono.just(interpretation)
    }

    override fun negative(sample: Long, service: String): Mono<String> {
        return Mono.just("본 검사에서 폐암 관련 유의미한 변이는 검출되지 않았습니다.")
    }
}
```

### STEP 3: Router에 패널 등록

**파일:** `interpretation/src/main/kotlin/com/gcgenome/lims/service/Router.kt`

```kotlin
// 28-34라인 수정
private val services: Map<String, Any> = (
    RareDiseasePanel.values() +
    SingleGenePanel.values() +
    // ... 기존 패널들 ...
    LungCancerPanel.values() +  // ← 추가
    Ballondor.values()
).stream().collect(Collectors.toMap(HasCode::code, identity()))
```

### STEP 4: BI 파일 처리 구현

**위치:** `bi-analysis-subscriber/src/main/kotlin/com/gcgenome/lims/analysis/actor/lung/`

#### WardenLung.kt

```kotlin
package com.gcgenome.lims.analysis.actor.lung.v1

import com.gcgenome.lims.analysis.actor.Warden
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.moveTo

@Service("wardenLung")
class WardenLung(
    @Value("\${subscriber.lung.path}") val path: Path,
    @Value("\${subscriber.lung.interval:10000}") interval: Long,
    @Value("\${subscriber.processed}") val processed: Path,
    @Value("\${subscriber.error}") val error: Path,
    val readers: List<Lung>
) : Warden(path, interval) {

    override suspend fun create(path: Path) {
        readers.filter { it.chkFormat(path) }.forEach { reader ->
            val batch = reader.batch(path)
            try {
                reader.exec(path)

                // 성공: processed 폴더로 이동
                val dest = processed.resolve(batch).resolve(path.fileName.toString())
                dest.parent.toFile().mkdirs()
                path.moveTo(dest)

                logger.info("✓ 처리 완료: ${path.fileName}")
            } catch(e: Exception) {
                logger.error("✗ 처리 실패: ${path.fileName}", e)

                // 실패: error 폴더로 이동
                val dest = error.resolve(batch).resolve(path.fileName.toString())
                dest.parent.toFile().mkdirs()
                path.moveTo(dest)
            }
        }
    }
}
```

#### Lung.kt (인터페이스)

```kotlin
package com.gcgenome.lims.analysis.actor.lung.v1

import java.nio.file.Path

interface Lung {
    fun chkFormat(path: Path): Boolean  // 파일 형식 체크
    fun batch(path: Path): String?      // 배치 정보 추출
    suspend fun exec(path: Path)        // 파일 처리
}
```

#### LungSnv.kt

```kotlin
package com.gcgenome.lims.analysis.actor.lung.v1

import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path

@Component
class LungSnv(
    private val elasticsearchRepository: ElasticsearchRepository
) : Lung {

    override fun chkFormat(path: Path): Boolean {
        // L001_SNV_20250119_12345.txt
        return path.fileName.toString().matches(Regex("L\\d{3}_SNV_\\d{8}_\\d+\\.txt"))
    }

    override fun batch(path: Path): String? {
        return Regex("L\\d{3}_SNV_(\\d{8})_\\d+\\.txt")
            .find(path.fileName.toString())
            ?.groupValues?.get(1)
    }

    override suspend fun exec(path: Path) {
        val lines = Files.readAllLines(path)
        val header = lines[0].split("\t")

        lines.drop(1).forEach { line ->
            val cols = line.split("\t")
            val snv = SnvDocument(
                sample = extractSampleId(path),
                service = "L001",
                gene = cols[header.indexOf("Gene")],
                hgvsc = cols[header.indexOf("HGVS.c")],
                hgvsp = cols[header.indexOf("HGVS.p")],
                vaf = cols[header.indexOf("VAF")].toDouble(),
                coverage = cols[header.indexOf("Coverage")].toInt()
            )
            elasticsearchRepository.save(snv).subscribe()
        }

        logger.info("SNV 처리 완료: ${lines.size - 1}개 변이")
    }

    private fun extractSampleId(path: Path): Long {
        return Regex("\\d+(?=\\.txt)").find(path.fileName.toString())
            ?.value?.toLong() ?: throw IllegalArgumentException("샘플 ID 추출 실패")
    }
}
```

### STEP 5: 설정 파일 업데이트

**파일:** `bi-analysis-subscriber/src/main/resources/application.yml`

```yaml
subscriber:
  lung:
    path: /data/analysis/lung
    interval: 10000  # 10초마다 체크
  processed: /data/processed
  error: /data/error
```

### STEP 6: Gateway 라우팅 확인

**파일:** `gateway/src/main/resources/application.yml`

```yaml
# 이미 설정되어 있음 (port: 19643)
spring:
  cloud:
    gateway:
      routes:
        - id: interpretation2
          uri: lb://interpretation2-service
          predicates:
            - Header=Content-Type, ^application/vnd\.lims\.v1[+json]*
            - Path=/panel-service/services/**, /panel-service/samples/*/services/*/interpretation
```

---

## 3. 운영 관점 체크리스트

### 3.1 개발 단계

```
✓ 패널 Enum 정의 완료
✓ 해석 로직 구현 완료
✓ Router 등록 완료
✓ Warden 구현 완료
✓ 파일 리더 구현 완료
✓ 단위 테스트 작성 (Kotest 5.9.1)
✓ 통합 테스트 작성
✓ 테스트 커버리지 80% 이상 확인
✓ 코드 리뷰 완료
✓ SonarQube 정적 분석 통과
```

### 3.2 배포 전 체크

```
✓ application.yml 설정 확인 (test/prod 환경별)
✓ Elasticsearch 인덱스 매핑 생성
✓ PostgreSQL 테이블 스키마 확인
✓ Kafka 토픽 생성 (필요시)
✓ 파일 경로 권한 설정 (755)
✓ Warden 디렉토리 생성
  - /data/analysis/lung
  - /data/processed
  - /data/error
✓ BI팀과 파일 명명 규칙 합의
✓ BI팀 테스트 파일 수령
✓ 실제 데이터로 end-to-end 테스트
```

### 3.3 배포 후 확인

```
✓ Pod 정상 기동 확인
✓ Health check 응답 확인
✓ Warden 파일 감시 동작 확인
✓ API 엔드포인트 응답 확인
✓ Elasticsearch 연결 확인
✓ PostgreSQL 연결 확인
✓ 로그 정상 출력 확인
✓ Jandi 알림 전송 확인
```

---

## 4. 배포 및 롤백 런북

### 4.1 배포 절차 (Test 환경)

```bash
# 1. 소스 빌드
./gradlew :interpretation:build
./gradlew :bi-analysis-subscriber:build

# 2. 테스트 실행
./gradlew :interpretation:test
./gradlew :interpretation:koverVerify  # 커버리지 확인

# 3. Docker 이미지 빌드 (Jib)
./gradlew :interpretation:jib
./gradlew :bi-analysis-subscriber:jib

# 4. OpenShift 배포
oc project panel-test
oc rollout restart deployment/interpretation2-service
oc rollout restart deployment/bi-analysis-subscriber-service

# 5. 배포 상태 확인
oc rollout status deployment/interpretation2-service
oc get pods | grep interpretation2

# 6. 로그 모니터링
oc logs -f deployment/interpretation2-service
```

### 4.2 배포 절차 (Prod 환경)

```bash
# 1. 배포 승인 확인
# - PM 승인
# - 의료진 확인
# - BI팀 준비 완료

# 2. 배포 시간: 업무 시간 외 (야간 또는 주말)

# 3. 사전 백업
pg_dump -h prod-db -U lims lims_panel > backup_$(date +%Y%m%d_%H%M%S).sql

# 4. Prod 배포
./gradlew :interpretation:jib -Penv=prod
./gradlew :bi-analysis-subscriber:jib -Penv=prod

oc project panel-prod
oc rollout restart deployment/interpretation2-service
oc rollout restart deployment/bi-analysis-subscriber-service

# 5. Smoke Test
curl -H "Content-Type: application/vnd.lims.v1+json" \
  https://prod-gateway:19643/panel-service/services/L001

# 6. 모니터링 (30분)
# - Grafana 대시보드 확인
# - 에러율 확인
# - 응답 시간 확인
```

### 4.3 롤백 절차

```bash
# 긴급 롤백 (5분 이내)

# 1. 이전 버전으로 롤백
oc rollout undo deployment/interpretation2-service
oc rollout undo deployment/bi-analysis-subscriber-service

# 2. 롤백 확인
oc rollout status deployment/interpretation2-service

# 3. 헬스체크
curl https://prod-gateway:19643/actuator/health

# 4. 로그 확인
oc logs -f deployment/interpretation2-service | grep ERROR

# 5. 인시던트 리포트 작성
# - 롤백 사유
# - 발생 시간
# - 영향 범위
# - 재배포 계획
```

### 4.4 배포 체크포인트

| 단계 | 체크 항목 | 정상 기준 | 롤백 조건 |
|------|----------|----------|----------|
| 빌드 | Gradle build | SUCCESS | FAILURE |
| 테스트 | 전체 테스트 통과 | 100% | 1개 이상 실패 |
| 커버리지 | koverVerify | ≥80% | <80% |
| 배포 | Pod Running | 2/2 Running | CrashLoopBackOff |
| Health | /actuator/health | UP | DOWN |
| API | /services/L001 | 200 OK | 404/500 |
| 응답시간 | P95 latency | <500ms | >2s |
| 에러율 | Error rate | <0.1% | >1% |

---

## 5. 모니터링 및 알람 설정

### 5.1 Grafana 대시보드

```
패널별 메트릭:
- interpretation_request_total{service="L001"}        # 총 요청 수
- interpretation_request_duration_seconds{service="L001"}  # 응답 시간
- interpretation_error_total{service="L001"}          # 에러 수
- warden_file_processed_total{panel="lung"}          # 처리된 파일 수
- warden_file_error_total{panel="lung"}              # 파일 처리 에러
- elasticsearch_index_size{index="snv"}              # ES 인덱스 크기
```

### 5.2 Alert Rules (Prometheus)

```yaml
groups:
  - name: lung_panel_alerts
    interval: 1m
    rules:
      # 에러율 증가
      - alert: LungPanelHighErrorRate
        expr: rate(interpretation_error_total{service="L001"}[5m]) > 0.01
        for: 5m
        labels:
          severity: critical
          team: backend
        annotations:
          summary: "폐암 패널 에러율 증가"
          description: "L001 패널 에러율이 1%를 초과했습니다. 현재: {{ $value }}"

      # 응답 시간 증가
      - alert: LungPanelSlowResponse
        expr: histogram_quantile(0.95, rate(interpretation_request_duration_seconds_bucket{service="L001"}[5m])) > 2
        for: 10m
        labels:
          severity: warning
          team: backend
        annotations:
          summary: "폐암 패널 응답 지연"
          description: "L001 패널 P95 응답 시간이 2초를 초과했습니다."

      # Warden 파일 처리 실패
      - alert: LungWardenFileProcessingFailed
        expr: rate(warden_file_error_total{panel="lung"}[5m]) > 0
        for: 5m
        labels:
          severity: critical
          team: backend
        annotations:
          summary: "폐암 패널 파일 처리 실패"
          description: "BI 파일 처리 중 에러가 발생했습니다. /data/error 확인 필요"

      # Elasticsearch 저장 실패
      - alert: ElasticsearchIndexingFailed
        expr: rate(elasticsearch_index_errors_total{index="snv"}[5m]) > 0.1
        for: 5m
        labels:
          severity: critical
          team: backend
        annotations:
          summary: "Elasticsearch 인덱싱 실패"
          description: "SNV Elasticsearch 저장 실패율 증가"
```

### 5.3 Jandi Webhook 알림

```kotlin
// 파일 처리 완료 알림
fun sendJandiNotification(sample: Long, service: String, snvCount: Int) {
    val message = """
        ✓ 검체 처리 완료
        - 샘플: $sample
        - 패널: $service
        - 변이 수: $snvCount
        - 시간: ${LocalDateTime.now()}
    """.trimIndent()

    jandiWebhookClient.send(message)
}

// 에러 알림
fun sendErrorNotification(path: Path, error: Exception) {
    val message = """
        ✗ 파일 처리 실패
        - 파일: ${path.fileName}
        - 에러: ${error.message}
        - 위치: /data/error/${LocalDate.now()}
        - 시간: ${LocalDateTime.now()}
    """.trimIndent()

    jandiWebhookClient.sendError(message)
}
```

### 5.4 로그 모니터링 (ELK Stack)

```
Kibana 쿼리:

# 에러 로그 검색
level: ERROR AND service: interpretation2 AND message: *L001*

# 느린 쿼리 검색
duration: >2000 AND service: interpretation2

# Warden 파일 처리 로그
logger: WardenLung AND level: INFO

# Elasticsearch 인덱싱 에러
message: "ElasticsearchException" AND service: bi-analysis-subscriber
```

---

## 6. 장애 대응 플레이북

### 6.1 파일 처리 실패

**증상:**
```
- /data/error 폴더에 파일 적재
- Jandi 에러 알림 수신
- Warden 로그에 Exception 출력
```

**원인 분석:**
```bash
# 1. 에러 파일 확인
ls -la /data/error/$(date +%Y%m%d)/

# 2. 로그 확인
oc logs -f deployment/bi-analysis-subscriber-service | grep ERROR

# 3. 파일 형식 확인
head -20 /data/error/20250119/L001_SNV_20250119_12345.txt
```

**해결 방법:**

```bash
# Case 1: 파일 형식 오류 (BI팀 확인 필요)
# - BI팀에 재전송 요청
# - 파일 명명 규칙 재확인

# Case 2: 데이터 파싱 오류
# - 특정 컬럼 누락/추가 확인
# - 코드 수정 필요 시 핫픽스 배포

# Case 3: Elasticsearch 연결 실패
# - ES 클러스터 상태 확인
curl http://elasticsearch:9200/_cluster/health

# - ES 재시작 (필요시)
oc rollout restart deployment/elasticsearch

# Case 4: 파일 재처리
# - error 폴더에서 원본 폴더로 이동
mv /data/error/20250119/L001_SNV_*.txt /data/analysis/lung/

# - Warden이 10초 내 자동 재처리
```

### 6.2 해석 결과 조회 실패

**증상:**
```
- UI에서 해석 결과 미표시
- GET /samples/{id}/services/L001/interpretation → 404
```

**원인 분석:**
```bash
# 1. DB 데이터 확인
psql -h prod-db -U lims -d lims_panel
SELECT * FROM panel.interpretation WHERE sample = 12345 AND service = 'L001';

# 2. API 직접 호출
curl -H "Content-Type: application/vnd.lims.v1+json" \
  https://prod-gateway:19643/panel-service/samples/12345/services/L001/interpretation

# 3. interpretation2-service 로그 확인
oc logs -f deployment/interpretation2-service | grep "sample=12345"
```

**해결 방법:**

```bash
# Case 1: DB에 데이터 없음
# - 자동 해석 재실행
curl -X PUT \
  -H "Content-Type: application/vnd.lims.v1+json" \
  -d '{}' \
  https://prod-gateway:19643/panel-service/samples/12345/services/L001/auto-interpret

# Case 2: Gateway 라우팅 오류
# - Gateway 로그 확인
oc logs -f deployment/gateway-service | grep L001

# - Gateway 재시작
oc rollout restart deployment/gateway-service

# Case 3: Service Discovery 실패 (Zookeeper)
# - Zookeeper 상태 확인
echo stat | nc localhost 2181

# - interpretation2-service 재시작
oc rollout restart deployment/interpretation2-service
```

### 6.3 응답 시간 증가 (Performance Degradation)

**증상:**
```
- Grafana: P95 latency > 2s
- Prometheus Alert: LungPanelSlowResponse
```

**원인 분석:**
```bash
# 1. 애플리케이션 프로파일링
# - VisualVM 연결
# - Thread dump 수집
oc exec -it deployment/interpretation2-service -- jstack 1 > thread_dump.txt

# 2. 데이터베이스 슬로우 쿼리 확인
SELECT query, mean_exec_time, calls
FROM pg_stat_statements
WHERE query LIKE '%interpretation%'
ORDER BY mean_exec_time DESC LIMIT 10;

# 3. Elasticsearch 쿼리 성능 확인
GET snv/_search
{
  "profile": true,
  "query": {
    "bool": {
      "must": [
        {"term": {"sample": 12345}},
        {"term": {"service": "L001"}}
      ]
    }
  }
}
```

**해결 방법:**

```bash
# Case 1: DB 커넥션 풀 고갈
# - 커넥션 풀 사이즈 증가 (application.yml)
spring.r2dbc.pool.max-size: 20  # 기본 10 → 20

# - 서비스 재시작
oc rollout restart deployment/interpretation2-service

# Case 2: Elasticsearch 인덱스 조각화
# - 인덱스 재최적화
POST snv/_forcemerge?max_num_segments=1

# Case 3: 메모리 부족
# - Pod 리소스 증가
oc set resources deployment/interpretation2-service \
  --requests=memory=1Gi,cpu=500m \
  --limits=memory=2Gi,cpu=1000m

# Case 4: GC 오버헤드
# - JVM 옵션 튜닝
JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xms1g -Xmx2g"
```

### 6.4 On-Call 대응 프로토콜

```
[Level 1 - Critical] 즉시 대응 (15분 이내)
- 서비스 전체 다운
- 에러율 >5%
- 데이터 유실 가능성

조치:
1. 장애 상황 Jandi 공유
2. 롤백 실행 (이전 버전)
3. 원인 분석 시작
4. 인시던트 리포트 작성

[Level 2 - High] 1시간 이내 대응
- 응답 시간 >5s
- 에러율 1-5%
- 특정 기능 장애

조치:
1. 원인 분석
2. 임시 조치 (서비스 재시작 등)
3. 핫픽스 준비
4. 다음 배포 시 반영

[Level 3 - Medium] 익일 대응
- 응답 시간 2-5s
- 에러율 0.1-1%
- 로그 경고 발생

조치:
1. 백로그에 이슈 등록
2. 주간 회의에서 논의
3. 다음 스프린트 반영
```

---

## 7. 실전 체크리스트

### 배포 전날

```
□ 배포 계획서 작성 (시간, 순서, 롤백 조건)
□ 관련팀 공지 (PM, 의료진, BI팀, QA)
□ 배포 승인 확보
□ DB 백업 스크립트 준비
□ 롤백 스크립트 준비
□ On-call 담당자 지정
□ Grafana 대시보드 준비
□ Jandi 알림 채널 확인
```

### 배포 당일 (D-Day)

```
□ DB 백업 실행
□ 배포 시작 공지
□ 빌드 실행
□ 테스트 실행
□ Docker 이미지 빌드
□ Test 환경 배포
□ Test 환경 검증 (30분)
□ Prod 환경 배포
□ Smoke Test 실행
□ Health Check 확인
□ Grafana 모니터링 (1시간)
□ 배포 완료 공지
```

### 배포 익일 (D+1)

```
□ 전날 메트릭 확인
  - 에러율: _____%
  - P95 응답시간: _____ms
  - 처리된 파일 수: _____개
□ 에러 로그 검토
□ BI팀 피드백 수집
□ 의료진 피드백 수집
□ 개선사항 백로그 등록
□ 회고 미팅 일정 잡기
```

---

## 8. 참고 문서

### 내부 문서
- `PROJECT_ANALYSIS.md` - 전체 프로젝트 구조 분석
- `FOLDER_GUIDE.md` - 폴더별 상세 설명
- `interpretation/src/main/kotlin/com/gcgenome/lims/service/Router.kt:28-34` - 패널 등록 코드
- `bi-analysis-subscriber/src/main/kotlin/com/gcgenome/lims/analysis/actor/` - Warden 패턴 구현

### 실제 파일 경로
- Gateway: `gateway/src/main/resources/application.yml` (port: 19643)
- Interpretation: `interpretation/src/main/kotlin/com/gcgenome/lims/`
- BI Subscriber: `bi-analysis-subscriber/src/main/kotlin/com/gcgenome/lims/analysis/`
- Domain: `domain/src/main/kotlin/com/gcgenome/lims/domain/`

### 주요 API 엔드포인트
```
GET    /services/{service}                                    # 패널 정보
GET    /samples/{sample}/services/{service}/interpretation    # 해석 조회
PUT    /samples/{sample}/services/{service}/interpretation    # 해석 저장
PUT    /samples/{sample}/services/{service}/auto-interpret    # 자동 해석
DELETE /samples/{sample}/services/{service}/interpretation    # 해석 삭제
```

---

## 마무리

이 가이드는 **실제 소스 코드 분석**을 기반으로 작성되었으며, **운영 경험**을 반영했습니다.

### 핵심 포인트

1. **Router.kt 28-34라인**에 패널 enum 추가
2. **Interpretable 인터페이스** 구현 (chk, interpret, negative)
3. **Warden 패턴**으로 BI 파일 자동 처리
4. **배포 전 철저한 테스트** (80% 커버리지)
5. **롤백 계획 필수** (5분 내 복구)
6. **모니터링 알람 설정** (에러율, 응답시간)
7. **On-call 대응 프로토콜** 숙지

### 배포 성공을 위한 조언

- 급하게 배포하지 마세요. Test 환경에서 충분히 검증하세요.
- BI팀과 긴밀히 협업하세요. 파일 형식 불일치가 가장 흔한 에러입니다.
- 롤백은 항상 준비되어 있어야 합니다. 5분 내 복구 가능해야 합니다.
- 배포 후 최소 1시간은 모니터링하세요.
- 장애 발생 시 혼자 해결하려 하지 마세요. 팀에 즉시 공유하세요.

**행운을 빕니다! 🚀**

---

**문서 버전:** 2.0 (운영+소스 기반)
**작성일:** 2025-01-21
**대상:** 15년차 개발자
