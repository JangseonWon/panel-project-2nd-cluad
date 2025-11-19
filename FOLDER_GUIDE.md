# 프로젝트 폴더별 역할 가이드

## 📦 21개 모듈 한눈에 보기

---

## 🎯 핵심 비즈니스 로직 (6개)

### 1. `domain/`
**"공통 데이터 구조 보관함"**

```
무엇을 하는가?
  ✓ 모든 모듈이 공통으로 사용하는 데이터 구조 정의
  ✓ Request, Sample, Patient, Worklist, Serial 등의 엔티티
  ✓ 도메인 이벤트 (UpdateSerialEvent 등)

누가 사용하는가?
  → 모든 백엔드 모듈

예시:
  - Request: 검사 의뢰 정보
  - Sample: 검체 정보
  - Worklist: 워크리스트 정보
  - Serial: 시리얼 번호
```

---

### 2. `worklist/`
**"워크리스트와 시리얼 번호 관리자"**

```
무엇을 하는가?
  ✓ 워크리스트 생성, 조회, 검색
  ✓ 시리얼 번호 자동 생성 (2025-001, 2025-002...)
  ✓ 작업 관리

API:
  GET  /worklists                              워크리스트 목록
  PUT  /worklists/{id}/generate-serials        시리얼 생성

핵심 기능:
  - WorklistYearyAndGroupableSerialGenerator → 연도별 시리얼 생성
  - RetestSerialGenerator → 재검 시리얼 생성

테이블:
  - panel.worklist
  - panel.serial
```

---

### 3. `interpretation/`
**"변이 해석 엔진 (가장 중요!)"**

```
무엇을 하는가?
  ✓ 29종 이상의 패널 타입 지원
  ✓ 변이 자동 해석
  ✓ 음성 결과 해석
  ✓ 한국어 해석 문구 자동 생성
  ✓ ClinVar, in-silico 예측 통합

API:
  GET  /samples/{sample}/services/{service}/interpretation      해석 조회
  PUT  /samples/{sample}/services/{service}/interpretation      해석 저장
  PUT  /samples/{sample}/services/{service}/auto-interpret      자동 해석
  GET  /samples/{sample}/services/{service}/negative-interpret  음성 해석

지원 패널:
  - 희귀질환 (RareDiseasePanel)
  - 단일 유전자 (SingleGenePanel)
  - 혈액암 (BloodCancerPanel)
  - 고형암 (SolidTumorPanel)
  - HRD, TSO, MRD 등

핵심 파일:
  - interpretable/impl/RareDisease.kt      희귀질환 해석 로직
  - interpretable/impl/BloodCancer.kt      혈액암 해석 로직
  - interpretable/kokr/Korean.kt           한국어 문구 생성

파일 수: 107개 (가장 복잡한 모듈)
포트: 62413
```

---

### 4. `snv/`
**"변이(SNV) 검색 및 분류 관리자"**

```
무엇을 하는가?
  ✓ SNV 검색 (Elasticsearch 연동)
  ✓ SNV 분류 (Pathogenic, Likely Pathogenic, VUS 등)
  ✓ 보고된 SNV 조회

API:
  GET    /samples/{sample}/services/{service}/snvs                       SNV 목록
  POST   /samples/{sample}/services/{service}/batches/{batch}/{row}/snvs SNV 검색
  PUT    /snvs/{variant}/class/{class}                                   분류 생성
  DELETE /snvs/{variant}/class                                           분류 삭제
  GET    /samples/{sample}/analysis                                      분석 결과

테이블:
  - panel.snv

포트: 62415
```

---

### 5. `bi-analysis-subscriber/`
**"BI 분석 파일 자동 처리 로봇"**

```
무엇을 하는가?
  ✓ BI팀이 만든 분석 파일 자동 감지 (Warden 패턴)
  ✓ QC 파일 읽기 → DB 저장
  ✓ SNV 파일 읽기 → Elasticsearch 저장
  ✓ 패널별 다른 처리 로직 (All, AML, Hema, Lym...)
  ✓ 성공/실패 파일 자동 분류
  ✓ Jandi 알림

동작 방식:
  1. /data/analysis/all/ 폴더 감시 (10초마다)
  2. ALL_QC_20250119.txt 파일 감지
  3. 파일 읽기 & 파싱
  4. DB/Elasticsearch 저장
  5. /data/processed/ 폴더로 이동
  6. Jandi "분석 완료!" 알림

지원 패널:
  - All v1, v2
  - AML v2
  - Hema v1
  - Lym v1, v2
  - MDS v2
  - HRD, TSO

핵심 파일:
  - analysis/actor/all/v1/WardenAll.kt    All v1 파일 감시
  - analysis/actor/hema/v1/WardenHema.kt  Hema v1 파일 감시
```

---

### 6. `bi-variant-service/`
**"BI 분석자용 변이 조회 API"**

```
무엇을 하는가?
  ✓ 보고된 변이 전체 조회 (BI팀 통계용)
  ✓ 검체별 변이 조회

API:
  GET /variants/reported            전체 변이
  GET /variants/reported/{sample}   검체별 변이

누가 사용하는가?
  → BI 분석팀

포트: 62419
```

---

## 🎨 UI (화면) 모듈 (4개)

### 7. `worklist-ui/`
**"워크리스트 화면"**

```
기술: GWT (Java → JavaScript 컴파일)

화면:
  ✓ 워크리스트 목록
  ✓ 워크리스트 상세
  ✓ 시리얼 번호 생성 버튼

언어: Java (GWT)
빌드: WAR 파일
```

---

### 8. `interpretation-ui/`
**"변이 해석 화면"**

```
기술: GWT + sayaya-chart

화면:
  ✓ 해석 에디터
  ✓ 패널별 커스텀 UI
    - 고형암: Tier 테이블, 약물-변이 매칭
    - MRD: Clone 정보
    - TSO: TMB 점수
  ✓ 변이 목록
  ✓ QC 지표

핵심 파일:
  - expand/SomaticExpandElement.java   고형암 UI
  - expand/MrdExpandElement.java       MRD UI
  - expand/TsoExpandElement.java       TSO UI
```

---

### 9. `snv-ui/`
**"SNV 목록 화면"**

```
기술: GWT

화면:
  ✓ SNV 테이블
  ✓ 변이 검색
  ✓ 분류 선택 (Pathogenic, VUS...)
```

---

### 10. `variant-snv-ui/`
**"SNV 화면 v2"**

```
설명: snv-ui의 개선 버전 (병행 운영 중)
```

---

## 🌐 게이트웨이 & 인프라 (5개)

### 11. `gateway/`
**"모든 요청의 관문 (API Gateway)"**

```
무엇을 하는가?
  ✓ 모든 API 요청 받기
  ✓ 각 서비스로 라우팅
  ✓ CORS 설정
  ✓ Load Balancing

기술: Spring Cloud Gateway + Zookeeper

포트: 19643

라우팅 예시:
  /panel-service/worklists         → worklist-service
  /panel-service/samples/*/snvs    → snv2-service
  /panel-service/interpretation    → interpretation2-service

설정 파일: application.yml
```

---

### 12. `gateway-gaia/`
**"Kubernetes용 API Gateway"**

```
차이점:
  - gateway: Zookeeper 기반 (온프레미스)
  - gateway-gaia: Kubernetes 기반 (클라우드)

기술: Kubernetes Fabric8
```

---

### 13. `proxy-gaia/`
**"온프레미스 ↔ 클라우드 브릿지"**

```
역할:
  ✓ 베어메탈 서버의 요청을 Kubernetes로 전달
  ✓ 하이브리드 환경 연결

사용 시나리오:
  온프레미스 서버 → proxy-gaia → gateway-gaia → Kubernetes 서비스
```

---

### 14. `service/`
**"메인 서비스 게이트웨이"**

```
설명: gateway와 유사한 역할 (레거시 또는 다른 용도)
기술: Spring Cloud Gateway
```

---

### 15. `authorization/`
**"인증/인가 담당관"**

```
무엇을 하는가?
  ✓ 사용자 인증
  ✓ 역할 기반 접근 제어
  ✓ X-USER-ID 헤더 검증

인증 방식:
  - X-USER-ID 헤더 기반
  - Spring Security Reactive

역할:
  - A: Admin (관리자)
  - M: Manager (매니저)
  - U: User (일반 사용자)

사용처: 모든 백엔드 모듈의 공통 의존성
테이블: public.user
```

---

## 🔍 검색 모듈 (3개)

### 16. `search/`
**"검색 인터페이스 정의"**

```
역할: 검색 추상화 계층
내용: SearchParam, 페이지네이션 인터페이스
```

---

### 17. `search-querydsl/`
**"QueryDSL 검색 구현체"**

```
기술: R2DBC + QueryDSL
기능: 타입 안전 쿼리, 동적 쿼리 생성
사용처: worklist, interpretation
```

---

### 18. `elasticsearch/`
**"Elasticsearch 검색 구현체"**

```
기술: Spring Data Elasticsearch
기능: SNV 전문 검색
사용처: snv, bi-analysis-subscriber
```

---

## 🔧 유틸리티 (3개)

### 19. `event-broadcaster/`
**"이벤트 방송국"**

```
무엇을 하는가?
  ✓ 도메인 이벤트를 Kafka로 발행
  ✓ 이벤트 중개자 역할

이벤트 종류:
  - UpdateSerialEvent (시리얼 업데이트)
  - UpdateSequencingItemEvent (시퀀싱 아이템 업데이트)

기술: Spring Kafka
```

---

### 20. `snv-marker/`
**"SNV 마커 관리"**

```
역할: SNV Consensual Class 조회 및 관리
기술: QueryDSL
```

---

### 21. `testcontainer/`
**"테스트용 데이터 & 컨테이너"**

```
역할: 통합 테스트용 공통 설정
내용:
  - PostgreSQL Testcontainer
  - Kafka Testcontainer
  - Elasticsearch Testcontainer
  - 테스트 픽스처

사용처: 모든 모듈의 테스트 코드
```

---

## 📊 모듈 분류표

| 분류 | 개수 | 모듈 |
|------|------|------|
| **핵심 비즈니스** | 6개 | domain, worklist, interpretation, snv, bi-analysis-subscriber, bi-variant-service |
| **UI** | 4개 | worklist-ui, interpretation-ui, snv-ui, variant-snv-ui |
| **게이트웨이** | 5개 | gateway, gateway-gaia, proxy-gaia, service, authorization |
| **검색** | 3개 | search, search-querydsl, elasticsearch |
| **유틸리티** | 3개 | event-broadcaster, snv-marker, testcontainer |

---

## 🎯 실무 가이드

### 버그 수정 시 어느 폴더를 봐야 하나?

```
워크리스트가 안 보여요
  → worklist, worklist-ui

해석이 안돼요
  → interpretation, interpretation-ui

SNV 검색이 안돼요
  → snv, elasticsearch

파일 처리가 안돼요
  → bi-analysis-subscriber

API가 안 불러와져요
  → gateway

로그인이 안돼요
  → authorization
```

---

### 신규 기능 개발 시 수정할 폴더

```
신규 패널 추가 (예: 폐암 패널)
  1. domain → 이벤트 추가 (필요시)
  2. interpretation → 해석 로직 추가
  3. interpretation-ui → 화면 추가
  4. bi-analysis-subscriber → 파일 처리 추가
  5. gateway → 라우팅 확인 (보통 수정 불필요)

신규 검색 기능
  1. search → 인터페이스 정의
  2. search-querydsl 또는 elasticsearch → 구현
  3. 해당 백엔드 모듈 → 사용

신규 UI 화면
  1. 해당 -ui 모듈 → 화면 추가
  2. 해당 백엔드 모듈 → API 추가 (필요시)
```

---

### 자주 수정하는 폴더 순위

```
1위: interpretation       (가장 복잡, 패널 추가 빈번)
2위: interpretation-ui    (화면 수정 빈번)
3위: bi-analysis-subscriber (파일 형식 변경)
4위: snv                  (검색 로직 개선)
5위: worklist             (시리얼 생성 규칙 변경)
```

---

## 🔄 전체 흐름도

```
[사용자 브라우저]
    ↓
[gateway:19643] ← 모든 요청의 시작점
    ↓
    ├─> [worklist] ────> DB (panel.worklist, serial)
    │       ↓
    │   [event-broadcaster] ─> Kafka
    │
    ├─> [interpretation:62413] ─> DB (panel.interpretation)
    │       ↓
    │   [search-querydsl] ─> PostgreSQL
    │
    ├─> [snv:62415] ─────> DB (panel.snv)
    │       ↓
    │   [elasticsearch] ─> Elasticsearch
    │
    └─> [bi-variant-service:62419] ─> DB

[BI 분석 시스템]
    ↓
[파일 생성: /data/analysis/all/]
    ↓
[bi-analysis-subscriber]
    ├─> Warden이 파일 감지 (10초마다)
    ├─> 파일 읽기 & 파싱
    ├─> Elasticsearch + PostgreSQL 저장
    ├─> /data/processed/ 이동
    └─> Jandi 알림
```

---

## 📝 정리

- **총 21개 모듈**
- **각 폴더는 단일 책임 원칙**
- **헥사고날 아키텍처** (domain, usecase, interfaces)
- **명확한 의존성 방향** (하위 → 상위 참조 금지)
- **마이크로서비스 아키텍처**

---

**문서 버전:** 1.0
**작성일:** 2025-01-19
**작성자:** Claude Code AI
