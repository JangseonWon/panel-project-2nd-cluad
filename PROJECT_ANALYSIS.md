# Panel Project 2nd - 프로젝트 상세 분석 문서

## 📋 목차
1. [프로젝트 개요](#프로젝트-개요)
2. [전체 폴더 구조 및 역할](#전체-폴더-구조-및-역할)
3. [모듈별 상세 분석](#모듈별-상세-분석)
4. [데이터베이스 스키마](#데이터베이스-스키마)
5. [API 엔드포인트 전체 목록](#api-엔드포인트-전체-목록)
6. [데이터 플로우](#데이터-플로우)
7. [신규 패널 추가 가이드](#신규-패널-추가-가이드)
8. [기술 스택](#기술-스택)

---

## 프로젝트 개요

**프로젝트명:** panel-project-2nd
**시스템 유형:** LIMS (Laboratory Information Management System)
**도메인:** 유전자 패널 검사 관리
**아키텍처:** 반응형 마이크로서비스

### 핵심 특징
- 21개 마이크로서비스 모듈로 구성
- 반응형 아키텍처 (Spring WebFlux + R2DBC)
- 29종 이상의 패널 타입 지원
- 한국어 자동 해석 문구 생성
- 이벤트 기반 아키텍처 (Kafka)
- Warden 패턴 기반 파일 자동 처리
- 헥사고날 아키텍처
- Kubernetes 기반 배포

---

## 전체 폴더 구조 및 역할

### 🎯 핵심 비즈니스 모듈 (6개)

#### 1. domain
```
역할: 공통 도메인 모델 저장소
내용:
  - Request, Sample, Patient, Worklist, Serial 등 도메인 엔티티
  - UpdateSerialEvent 등 도메인 이벤트
  - 모든 모듈에서 공통으로 사용하는 기본 데이터 구조
사용처: 모든 백엔드 모듈에서 의존성으로 사용
```

#### 2. worklist
```
역할: 워크리스트 및 시리얼 번호 관리 백엔드
주요 기능:
  - 워크리스트 생성/조회/검색
  - 시리얼 번호 자동 생성
  - 작업(Work) 관리
API:
  - GET /worklists (워크리스트 목록)
  - PUT /worklists/{id}/generate-serials (시리얼 생성)
```

#### 3. interpretation
```
역할: 변이 해석 백엔드 (가장 핵심 모듈)
주요 기능:
  - 29종+ 패널 타입 지원
  - 자동 변이 해석
  - 음성 결과 해석
  - 한국어 해석 문구 자동 생성
API:
  - GET /samples/{sample}/services/{service}/interpretation
  - PUT /samples/{sample}/services/{service}/auto-interpret
  - GET /samples/{sample}/services/{service}/negative-interpret
파일 수: 107개 Kotlin 파일 (가장 복잡)
포트: 62413 (interpretation2-service)
```

#### 4. snv
```
역할: SNV(Single Nucleotide Variant) 변이 관리 백엔드
주요 기능:
  - SNV 검색 (Elasticsearch 연동)
  - SNV 분류 생성/삭제 (Pathogenic, VUS 등)
  - 보고된 SNV 조회
  - 분석 결과 조회
API:
  - GET /samples/{sample}/services/{service}/snvs
  - POST /samples/{sample}/services/{service}/batches/{batch}/{row}/snvs
  - PUT /snvs/{variant}/class/{class}
포트: 62415 (snv2-service)
```

#### 5. bi-analysis-subscriber
```
역할: BI 분석 완료 파일 자동 처리 (이벤트 구독자)
주요 기능:
  - BI팀이 생성한 분석 파일 자동 감지 (Warden 패턴)
  - QC 파일 읽기 → DB 저장
  - SNV 파일 읽기 → Elasticsearch 저장
  - 패널별 처리 로직 (All, AML, Hema, Lym, HRD, TSO 등)
  - 처리 완료/에러 파일 자동 분류
  - Jandi Webhook 알림
동작 방식: 백그라운드 파일 감시 서비스
```

#### 6. bi-variant-service
```
역할: BI 분석자용 변이 정보 조회 API
주요 기능:
  - 전체 보고된 변이 조회
  - 검체별 보고된 변이 조회
API:
  - GET /variants/reported
  - GET /variants/reported/{sample}
포트: 62419
```

### 🎨 UI 모듈 (4개)

#### 7. worklist-ui
```
역할: 워크리스트 관리 화면 (GWT)
화면: 워크리스트 목록, 상세, 시리얼 생성 UI
기술: GWT 2.12.2 + Dagger DI + sayaya-ui
```

#### 8. interpretation-ui
```
역할: 변이 해석 화면 (GWT)
화면: 해석 에디터, 패널별 커스텀 UI, Tier 테이블, 약물-변이 매칭
기술: GWT + sayaya-chart
```

#### 9. snv-ui
```
역할: SNV 변이 목록 화면 (GWT)
화면: SNV 테이블, 변이 검색, 분류 선택
```

#### 10. variant-snv-ui
```
역할: SNV 변이 화면 v2 (GWT)
```

### 🌐 인프라/게이트웨이 모듈 (5개)

#### 11. gateway
```
역할: API Gateway (Zookeeper 기반)
포트: 19643
기능: 모든 백엔드 서비스 라우팅, CORS, Load Balancing
```

#### 12. gateway-gaia
```
역할: API Gateway (Kubernetes 기반)
기능: Kubernetes Fabric8 서비스 디스커버리
```

#### 13. proxy-gaia
```
역할: 가이아 프록시 (리버스 프록시)
기능: 베어메탈 ↔ 클라우드 브릿지
```

#### 14. service
```
역할: 메인 서비스 게이트웨이
기능: Spring Cloud Gateway + Zookeeper
```

#### 15. authorization
```
역할: 인증/인가 공통 모듈
기능: X-USER-ID 헤더 기반 인증, 역할 기반 접근 제어
```

### 🔍 검색 관련 모듈 (3개)

#### 16. search
```
역할: 검색 공통 인터페이스
```

#### 17. search-querydsl
```
역할: QueryDSL 기반 검색 구현
```

#### 18. elasticsearch
```
역할: Elasticsearch 연동 모듈
```

### 🔧 유틸리티 모듈 (3개)

#### 19. event-broadcaster
```
역할: 도메인 이벤트 브로드캐스터
기능: 도메인 이벤트를 Kafka로 발행
```

#### 20. snv-marker
```
역할: SNV 마커 관리
기능: SNV Consensual Class 조회
```

#### 21. testcontainer
```
역할: 테스트용 공통 픽스처
기능: Testcontainers 설정 공유
```

---

## 모듈별 상세 분석

### Domain 모듈

#### 도메인 엔티티 (13개)

| 파일명 | 클래스명 | 주요 필드 |
|--------|---------|-----------|
| Request.kt | Request | sample, service, requester, dateRequest, dateReception, dateDue, barcode |
| Sample.kt | Sample | patient, id, type, age, barcode, remark, dateCollection |
| Patient.kt | Patient | - |
| Worklist.kt | Worklist | id(UUID), title, status, createAt, createBy, domain, sampleCount |
| Serial.kt | Serial | id(UUID), worklist(UUID), index, serial, infix, idx |
| Work.kt | Work | - |
| Service.kt | Service | - |
| Organization.kt | Organization | - |
| BatchSequencing.kt | BatchSequencing | - |
| SequencingItem.kt | SequencingItem | - |
| Index.kt | Index | - |
| User.kt | User | - |
| RequestSequence.kt | RequestSequence | - |

#### 도메인 이벤트 (6개)

- UpdateRequestSequenceEvent
- UpdateSequencingItemEvent
- UpdateSequencingIndexEvent
- UpdateSerialEvent
- UpdateBatchSequencingEvent
- PanelEvent

### Worklist 모듈

#### 디렉토리 구조
```
worklist/src/main/kotlin/com/gcgenome/lims/
├── usecase/
│   ├── worklist/
│   │   ├── WorklistRepository.kt
│   │   └── WorklistSearchService.kt
│   ├── serial/
│   │   ├── SerialService.kt
│   │   ├── SerialRepository.kt
│   │   ├── SerialGenerator.kt
│   │   └── generator/
│   │       ├── WorklistYearyAndGroupableSerialGenerator.kt
│   │       └── RetestSerialGenerator.kt
│   └── work/
│       ├── WorkService.kt
│       └── WorkRepository.kt
├── interfaces/
│   ├── api/
│   │   ├── WorklistController.kt
│   │   ├── SerialController.kt
│   │   └── WorkController.kt
│   ├── database/
│   │   ├── worklist/
│   │   │   ├── WorklistEntity.kt
│   │   │   └── R2dbcWorklistRepository.kt
│   │   └── serial/
│   │       ├── SerialEntity.kt
│   │       └── R2DbcSerialRepository.kt
│   └── event/
│       └── EventMessageSender.kt
└── Application.kt
```

#### API 엔드포인트

| Method | Path | 파일 위치 | 기능 |
|--------|------|-----------|------|
| GET | /worklists | WorklistController.kt:13 | 워크리스트 검색 |
| PUT | /worklists/{worklist}/generate-serials | SerialController.kt:14 | 시리얼 생성 |

#### 시리얼 생성 전략

1. **WorklistYearyAndGroupableSerialGenerator**
   - 연도별 그룹 시리얼 생성
   - 형식: {연도}-{그룹}-{순번}

2. **RetestSerialGenerator**
   - 재검 시리얼 생성
   - 기존 시리얼에 suffix 추가

### Interpretation 모듈

#### 파일 구조
```
interpretation/src/main/kotlin/com/gcgenome/lims/
├── service/
│   ├── Router.kt                    # Functional Endpoints
│   ├── Handler.kt
│   └── Repository.kt
├── entity/
│   └── Interpretation.kt            # panel.interpretation 테이블
├── interpretable/
│   ├── impl/                        # 패널별 해석 구현
│   │   ├── RareDisease.kt
│   │   ├── Single.kt
│   │   ├── BloodCancer.kt
│   │   ├── Cancer.kt
│   │   ├── Hrd.kt
│   │   └── ...
│   └── kokr/                        # 한국어 해석 문구
│       ├── Korean.kt
│       ├── VariantInterpreterSimpleKoKr.kt
│       ├── ClinvarPhraseKoKr.kt
│       └── ...
├── inserts/                         # 음성 결과 문구
│   ├── RareDiseaseNegativeDefault.kt
│   └── ...
└── dto/
```

#### API 엔드포인트

| Method | Path | 라인 | 기능 |
|--------|------|------|------|
| GET | /services/{service} | 20 | 서비스(패널) 정보 조회 |
| GET | /samples/{sample}/services/{service}/interpretation | 22 | 해석 결과 조회 |
| PUT | /samples/{sample}/services/{service}/interpretation | 23 | 해석 결과 저장 |
| PUT | /samples/{sample}/services/{service}/auto-interpret | 24 | 자동 해석 |
| GET | /samples/{sample}/services/{service}/negative-interpret | 25 | 음성 결과 해석 |
| DELETE | /samples/{sample}/services/{service}/interpretation | 26 | 해석 삭제 |

#### 지원 패널 타입 (29종+)

- RareDiseasePanel (희귀질환)
- SingleGenePanel (단일 유전자)
- SingleGenePanelWithMlpa (MLPA 포함)
- GenePlusPanel (유전자 플러스)
- BloodCancerPanel (혈액암)
- SolidTumorPanel (고형암)
- NonTSO, GenomeScreen, Wes, WesWithSingleGene
- Sanger, Des, Mrd, Hrd
- AlloSeq, Cancerch, Guardant, FLT3-ITD, Ballondor

### SNV 모듈

#### API 엔드포인트

| Method | Path | 라인 | 기능 |
|--------|------|------|------|
| GET | /samples/{sample}/services/{service}/snvs | 19 | 검체별 SNV 조회 |
| GET | /samples/{sample}/services/{service}/batches/{batch}/snvs | 20 | 배치별 SNV 조회 |
| POST | /samples/{sample}/services/{service}/batches/{batch}/{row}/snvs | 21 | SNV 검색 (Elasticsearch) |
| PUT | /samples/{sample}/services/{service}/snvs/{variant}/class/{class} | 22 | SNV 분류 생성 |
| DELETE | /samples/{sample}/services/{service}/snvs/{variant}/class | 23 | SNV 분류 삭제 |
| GET | /samples/{sample}/analysis | 25 | 분석 결과 조회 |

### BI-Analysis-Subscriber 모듈

#### Warden 패턴 구조

```
actor/
├── Warden.kt                  # 추상 Warden
├── all/
│   ├── v1/WardenAll.kt
│   └── v2/WardenAll.kt
├── aml/v2/WardenAml.kt
├── hema/v1/WardenHema.kt
├── lym/
│   ├── v1/WardenLym.kt
│   └── v2/WardenLym.kt
├── mds/v2/WardenMds.kt
├── hrd/WardenHrd.kt
└── tso/WardenTso.kt
```

#### 동작 방식

1. 파일 생성 감지 (10초 간격)
2. 파일 형식 체크 (chkFormat)
3. 배치 정보 추출 (batch)
4. QC 파일 읽기 → DB 저장
5. SNV 파일 읽기 → Elasticsearch 저장
6. 성공 시 processed 폴더로 이동
7. 실패 시 error 폴더로 이동
8. Workflow 이벤트 발행

### Gateway 모듈

#### 주요 라우팅 규칙

| 라우트 ID | 대상 서비스 | Path 패턴 |
|-----------|-------------|-----------|
| interpretation2 | lb://interpretation2-service | /panel-service/services/**<br>/panel-service/samples/*/services/*/interpretation |
| snv-api | lb://snv2-service | /panel-service/samples/*/services/*/snvs** |
| worklist | lb://worklist-service | /panel-service/worklists** |
| sample | lb://sample-service | /panel-service/samples/** |

#### 설정

```yaml
server:
  port: 19643

spring:
  cloud:
    zookeeper:
      connect-string: localhost:2181
    gateway:
      globalcors:
        allowedOrigins: "*"
```

---

## 데이터베이스 스키마

### 주요 테이블

| 스키마 | 테이블명 | 용도 | 주요 컬럼 | 모듈 |
|--------|---------|------|-----------|------|
| panel | worklist | 워크리스트 | id(UUID), title, status, createAt, domain, sampleCount | worklist |
| panel | serial | 시리얼 번호 | id(UUID), worklist, index, serial, infix, idx | worklist |
| panel | interpretation | 해석 결과 | sample(PK), service(PK), value(JSON), createUser | interpretation |
| panel | snv | 보고된 SNV | sample(PK), service(PK), snv(PK), class | snv |
| public | user | 사용자 | id(PK), name, department, role, state | authorization |

### User 테이블 상세

```sql
CREATE TABLE public."user" (
    id VARCHAR(64) PRIMARY KEY,
    department VARCHAR(64),
    name VARCHAR(64),
    password VARCHAR(64),
    email VARCHAR(128),
    key UUID,
    serial VARCHAR(8),
    state VARCHAR(10),
    config JSONB,
    role CHAR DEFAULT 'U' NOT NULL,
    pw_fail_cnt INTEGER DEFAULT 0,
    department_code VARCHAR(16),
    department_detail VARCHAR(64)
);
```

**역할:**
- A: Admin (관리자)
- M: Manager (매니저)
- U: User (일반 사용자)

**상태:**
- ACTIVATE: 활성
- INACTIVATE: 비활성

---

## API 엔드포인트 전체 목록

```
[Gateway: 19643]
├─ /panel-service/worklists
│  ├─ GET                                # 워크리스트 목록
│  └─ /{id}/generate-serials
│     └─ PUT                             # 시리얼 생성
├─ /panel-service/services/{service}
│  └─ GET                                # 서비스(패널) 정보
├─ /panel-service/samples/{sample}/services/{service}
│  ├─ /interpretation
│  │  ├─ GET                             # 해석 조회
│  │  ├─ PUT                             # 해석 저장
│  │  └─ DELETE                          # 해석 삭제
│  ├─ /auto-interpret
│  │  └─ PUT                             # 자동 해석
│  ├─ /negative-interpret
│  │  └─ GET                             # 음성 해석
│  ├─ /snvs
│  │  ├─ GET                             # SNV 목록
│  │  └─ /{variant}/class/{class}
│  │     ├─ PUT                          # SNV 분류 생성
│  │     └─ DELETE                       # SNV 분류 삭제
│  └─ /batches/{batch}/{row}/snvs
│     └─ POST                            # SNV 검색
├─ /panel-service/samples/{sample}/analysis
│  └─ GET                                # 분석 결과 조회
└─ /panel-service/variants/reported
   ├─ GET                                # 보고된 변이 전체
   └─ /{sample}
      └─ GET                             # 검체별 변이
```

---

## 데이터 플로우

### 전체 워크플로우: 검사 의뢰 → 분석 → 해석

```
1. 검사 의뢰 접수
   └─> Request, Sample 생성

2. 워크리스트 생성
   - WorklistController → POST /worklists
   └─> panel.worklist 테이블 저장

3. 시리얼 번호 생성
   - SerialController → PUT /worklists/{id}/generate-serials
   - WorklistYearyAndGroupableSerialGenerator 실행
   └─> panel.serial 테이블 저장
   └─> UpdateSerialEvent 발행 (Kafka)

4. BI 분석 완료 (외부 시스템)
   └─> 분석 파일 생성 (QC, SNV)

5. 분석 파일 처리
   - WardenAll/WardenHema 등이 파일 감지
   - QC 파일 읽기 → panel.analysis 저장
   - SNV 파일 읽기 → Elasticsearch 저장
   └─> Workflow 이벤트 발행
   └─> Jandi Webhook 알림

6. SNV 조회 및 분류
   - POST /samples/{sample}/services/{service}/batches/{batch}/{row}/snvs
   - Elasticsearch 검색
   - PUT /snvs/{variant}/class/{class}
   └─> panel.snv 테이블 저장

7. 변이 해석
   - PUT /samples/{sample}/services/{service}/auto-interpret
   - RareDisease.kt, BloodCancer.kt 등 패널별 로직
   - Korean.kt로 한국어 문구 생성
   └─> panel.interpretation 테이블 저장 (JSON)

8. 해석 결과 조회
   - InterpretationApi.java (GWT)
   - GET /samples/{sample}/services/{service}/interpretation
   └─> interpretation-ui에서 표시
```

---

## 신규 패널 추가 가이드

### 예시: 폐암 패널 (Lung Cancer Panel, L001)

#### STEP 1: 요구사항 정의

```
패널명: 폐암 패널
패널 코드: L001
검사 유형: 체세포 변이 (Somatic)
분석 범위: SNV/Indel, CNV, Fusion, TMB
타겟 유전자: EGFR, ALK, ROS1, KRAS, BRAF 등 50개
```

#### STEP 2: Interpretation 모듈 작업

**2.1 패널 타입 정의**
- 위치: `interpretation/src/main/kotlin/com/gcgenome/lims/test/`
- 파일: `LungCancerPanel.kt` 생성
- 내용: enum class로 패널 종류 정의

**2.2 해석 로직 구현**
- 위치: `interpretation/src/main/kotlin/com/gcgenome/lims/interpretable/impl/`
- 파일: `LungCancer.kt` 생성
- 구현: AbstractPanel 또는 SomaticCancer 상속
- 메소드: interpret(), negative(), validate()

**2.3 DTO 정의**
- 위치: `interpretation/src/main/kotlin/com/gcgenome/lims/dto/`
- 파일: `InterpretationLungCancer.kt`

**2.4 라우터 등록**
- 위치: `interpretation/src/main/kotlin/com/gcgenome/lims/service/Router.kt`
- 수정: 28-34라인에 `LungCancerPanel.values()` 추가

**2.5 한국어 문구 (선택)**
- 위치: `interpretation/src/main/kotlin/com/gcgenome/lims/interpretable/kokr/`

#### STEP 3: BI-Analysis-Subscriber 모듈 작업

**3.1 Warden 구현**
- 위치: `bi-analysis-subscriber/src/main/kotlin/com/gcgenome/lims/analysis/actor/lung/`
- 파일:
  - `WardenLung.kt` (파일 감시자)
  - `LungQc.kt` (QC 파일 읽기)
  - `LungSnv.kt` (SNV 파일 읽기)
  - `LungCnv.kt` (CNV 파일 읽기)

**3.2 설정**
- 위치: `bi-analysis-subscriber/src/main/resources/application.yml`
- 추가:
  ```yaml
  subscriber:
    lung:
      path: /data/analysis/lung
      interval: 10000
  ```

#### STEP 4: UI 작업

**4.1 UI 컴포넌트**
- 위치: `interpretation-ui/src/main/java/com/gcgenome/lims/client/expand/`
- 파일: `LungCancerExpandElement.java`
- 구현: AbstractPanelExpandElement 상속

**4.2 폐암 특화 컴포넌트**
- 위치: `interpretation-ui/src/main/java/com/gcgenome/lims/client/expand/lung/`
- 파일:
  - `TierTable.java`
  - `DrugMatchPanel.java`
  - `TmbScore.java`

#### STEP 5: 테스트

**5.1 백엔드 테스트**
- 위치: `interpretation/src/test/kotlin/.../interpretable/impl/`
- 파일: `LungCancerTest.kt`
- 커버리지: 80% 이상

**5.2 통합 테스트**
- API 호출 테스트
- Elasticsearch 연동 테스트
- Testcontainers 활용

#### STEP 6: 배포

**6.1 Test 환경**
- GitHub Actions 워크플로우 트리거
- OpenShift 배포

**6.2 Prod 환경**
- Jenkins 파이프라인 실행
- Aries/Taurus 서버 배포

#### STEP 7: 운영 준비

- 모니터링 설정
- 문서화
- BI팀 협업 (파일 형식, 전송 경로)

---

## 기술 스택

### 백엔드

| 카테고리 | 기술 | 버전 |
|---------|------|------|
| 언어 | Kotlin | 2.0.0 |
| 프레임워크 | Spring Boot | 3.x |
| 웹 | Spring WebFlux | - |
| 데이터베이스 | PostgreSQL | - |
| DB 드라이버 | R2DBC PostgreSQL | 1.0.1 |
| 쿼리 | QueryDSL (Infobip) | 8.1.1 |
| 검색 | Elasticsearch | - |
| 메시징 | Apache Kafka | - |
| 서비스 디스커버리 | Zookeeper / Kubernetes | - |
| 빌드 | Gradle | 8.9 |

### 프론트엔드

| 카테고리 | 기술 | 버전 |
|---------|------|------|
| 프레임워크 | GWT | 2.12.2 |
| DI | Dagger | 2.51.1 |
| HTML 빌더 | Elemento | 1.6.11 |
| UI 컴포넌트 | sayaya-ui | 4.2 |
| Reactive | sayaya-rx | 2.0 |

### 테스트

| 카테고리 | 기술 | 버전 |
|---------|------|------|
| 테스트 프레임워크 | Kotest | 5.9.1 |
| Mocking | MockK | 1.13.16 |
| 컨테이너 | Testcontainers | - |
| 커버리지 | Kover | 0.9.1 |

### 배포

| 카테고리 | 기술 |
|---------|------|
| 컨테이너 | Docker (Jib 3.4.5) |
| 오케스트레이션 | Kubernetes + Helm |
| CI/CD | GitHub Actions + Jenkins |
| 이미지 레지스트리 | OpenShift Container Registry |

---

## 디자인 패턴

### 1. 헥사고날 아키텍처 (모든 백엔드 모듈)

```
모듈/
├── domain/          # 도메인 모델
├── usecase/         # 유스케이스 (비즈니스 로직)
└── interfaces/      # 어댑터 구현
    ├── api/         # REST API
    ├── database/    # R2DBC
    └── event/       # Kafka
```

### 2. Strategy Pattern (시리얼 생성)

- SerialGenerator 인터페이스
- WorklistYearyAndGroupableSerialGenerator
- RetestSerialGenerator

### 3. Warden Pattern (파일 감시)

- Warden 추상 클래스
- 패널별 Warden 구현 (WardenAll, WardenHema, ...)
- 파일 감지 → 처리 → 이동 (processed/error)

### 4. Repository Pattern

- 데이터 접근 추상화
- R2DBC + QueryDSL

### 5. Functional Router (WebFlux)

- DSL 방식 라우팅
- Handler 분리

---

## 모듈 분류 요약

| 분류 | 모듈 수 | 모듈 목록 |
|------|---------|-----------|
| 핵심 비즈니스 | 6개 | domain, worklist, interpretation, snv, bi-analysis-subscriber, bi-variant-service |
| UI | 4개 | worklist-ui, interpretation-ui, snv-ui, variant-snv-ui |
| 게이트웨이/인프라 | 5개 | gateway, gateway-gaia, proxy-gaia, service, authorization |
| 검색 | 3개 | search, search-querydsl, elasticsearch |
| 유틸리티 | 3개 | event-broadcaster, snv-marker, testcontainer |
| **합계** | **21개** | |

---

## 주요 서비스 포트

| 서비스 | 포트 | 설명 |
|--------|------|------|
| gateway | 19643 | API Gateway |
| interpretation2-service | 62413 | 해석 서비스 |
| snv2-service | 62415 | SNV 서비스 |
| bi-service | 62419 | BI 변이 서비스 |

---

## 문서 버전

- 작성일: 2025-01-19
- 버전: 1.0
- 작성자: Claude Code AI Analysis

---

## 참고사항

- 이 문서는 실제 코드 분석을 기반으로 작성되었습니다
- 프로젝트 구조 변경 시 문서도 업데이트 필요
- 신규 패널 추가 시 본 가이드 참고
