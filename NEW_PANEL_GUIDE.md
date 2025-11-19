# 신규 패널 추가 완벽 가이드

## 📋 목차
1. [개요](#개요)
2. [예시 시나리오](#예시-시나리오)
3. [단계별 작업 가이드](#단계별-작업-가이드)
4. [체크리스트](#체크리스트)
5. [예상 일정](#예상-일정)
6. [트러블슈팅](#트러블슈팅)

---

## 개요

신규 패널을 시스템에 추가하는 전체 과정을 단계별로 안내합니다.

### 작업 범위
- 백엔드 해석 로직 구현
- BI 파일 처리 로직 구현
- UI 화면 구현
- 테스트 작성
- 배포

### 필요 기술
- Kotlin (백엔드)
- Java + GWT (프론트엔드)
- R2DBC (데이터베이스)
- Elasticsearch (검색)

---

## 예시 시나리오

### 신규 패널: 폐암 패널 (Lung Cancer Panel)

```
패널명: 폐암 패널
패널 코드: L001
검사 유형: 체세포 변이 (Somatic)
분석 범위:
  - SNV/Indel
  - CNV (Copy Number Variation)
  - Fusion
  - TMB (Tumor Mutational Burden)
  - MSI (Microsatellite Instability)
타겟 유전자: EGFR, ALK, ROS1, KRAS, BRAF, MET 등 50개
```

---

## 단계별 작업 가이드

---

## 📋 STEP 1: 요구사항 정의 및 분석

### 1.1 패널 기본 정보 수집

```
작업 내용:
  - 의료진과 미팅
  - 검사 범위 확정
  - 보고서 양식 확인
  - 해석 규칙 논의

필요 정보:
  ✓ 패널 코드 (예: L001)
  ✓ 패널 이름 (한글/영문)
  ✓ 검사 유형 (Germline/Somatic)
  ✓ 타겟 유전자 리스트
  ✓ 변이 분류 기준
  ✓ 보고서 포함 항목
```

**예시:**
```
패널 코드: L001
패널 이름: 폐암 패널 / Lung Cancer Panel
검사 유형: Somatic (체세포)
타겟 유전자: 50개 (EGFR, ALK, ROS1, KRAS, BRAF, MET, ...)

변이 분류:
  - Tier 1: Actionable mutation (치료 가능, FDA 승인 약물 있음)
  - Tier 2: Prognostic marker (예후 예측)
  - Tier 3: Biological significance (생물학적 의미)
  - Tier 4: Unknown significance (의미 불명)

보고서 항목:
  1. 주요 변이 리스트 (Tier 1, 2)
  2. 약물-변이 매칭 정보
  3. TMB 점수
  4. MSI 상태
  5. 참고 문헌
```

### 1.2 BI 분석 파일 형식 확인

```
작업 내용:
  - BI팀과 미팅
  - 파일 형식 합의
  - 컬럼 정의 확정
  - 샘플 파일 수령

필요 정보:
  ✓ 파일 종류 (QC, SNV, CNV, Fusion 등)
  ✓ 파일 형식 (TSV, CSV, Excel 등)
  ✓ 파일 명명 규칙
  ✓ 컬럼 정의
  ✓ 필수/선택 컬럼
```

**예시:**

**QC 파일: L001_QC_20250119_12345.txt**
```tsv
Sample    Coverage    Uniformity    OnTarget    QC_Status
12345     500         95.2          98.5        PASS
```

**SNV 파일: L001_SNV_20250119_12345.txt**
```tsv
Gene    Transcript        HGVS.c          HGVS.p        VAF     Coverage    Tier    Drug
EGFR    NM_005228.5       c.2573T>G       p.L858R       35.2    1200        1       Gefitinib
ALK     NM_004304.4       c.3522C>T       p.R1174W      42.1    980         1       Crizotinib
KRAS    NM_033360.3       c.35G>A         p.G12D        28.5    1500        2       -
```

**CNV 파일: L001_CNV_20250119_12345.txt**
```tsv
Gene    Copy_Number    Status
MET     5.2            Amplification
ERBB2   4.8            Amplification
```

**Fusion 파일: L001_Fusion_20250119_12345.txt**
```tsv
Gene1    Gene2    Junction    Read_Count
EML4     ALK      E13:A20     45
ROS1     CD74     R32:C6      38
```

### 1.3 해석 규칙 정의

```
작업 내용:
  - 변이 분류 기준 정의
  - 약물 매칭 규칙 정의
  - 음성 결과 기준 정의
  - 한국어 문구 작성

문서화:
  ✓ 변이 분류 기준 문서
  ✓ 약물-변이 매칭 데이터베이스
  ✓ 음성 결과 문구 템플릿
```

**예시 - 변이 분류 기준:**
```
Tier 1 (치료 가능):
  - FDA 승인 약물이 있는 변이
  - NCCN 가이드라인 Level 1A, 1B
  - 예: EGFR L858R → Gefitinib, Erlotinib, Afatinib

Tier 2 (예후 마커):
  - 임상시험 약물이 있는 변이
  - NCCN 가이드라인 Level 2A, 2B
  - 예: KRAS G12C → Sotorasib (임상시험)

Tier 3 (생물학적 의미):
  - 종양 발생과 관련된 변이
  - 치료제 없음
  - 예: TP53 R273H

Tier 4 (의미 불명):
  - VUS (Variant of Uncertain Significance)
  - 추가 연구 필요
```

**예시 - 약물 매칭:**
```
EGFR L858R:
  - Gefitinib (1차 치료)
  - Erlotinib (1차 치료)
  - Afatinib (1차 치료)
  - Osimertinib (T790M 동반 시)

ALK Fusion:
  - Crizotinib (1차 치료)
  - Ceritinib (2차 치료)
  - Alectinib (2차 치료)
```

**예시 - 음성 결과 문구:**
```
"본 검사에서 폐암과 관련된 임상적으로 유의미한 체세포 변이는
검출되지 않았습니다. 다만, 본 검사는 50개 유전자의
coding region을 대상으로 하며, 전체 유전체를 분석하지는
않았음을 알려드립니다."
```

---

## 🏗️ STEP 2: 백엔드 작업 (interpretation 모듈)

### 2.1 패널 타입 정의

**작업 위치:** `interpretation/src/main/kotlin/com/gcgenome/lims/test/`

**파일 생성:** `LungCancerPanel.kt`

**작업 내용:**

```kotlin
package com.gcgenome.lims.test

enum class LungCancerPanel(
    override val code: String,
    val description: String
) : HasCode {
    L001("L001", "폐암 패널 v1"),
    L002("L002", "폐암 패널 v2 (확장)");

    override fun toString() = description
}
```

**체크포인트:**
- [ ] enum class 생성 완료
- [ ] HasCode 인터페이스 구현
- [ ] 패널 코드와 설명 매핑

---

### 2.2 해석 로직 구현

**작업 위치:** `interpretation/src/main/kotlin/com/gcgenome/lims/interpretable/impl/`

**파일 생성:** `LungCancer.kt`

**작업 내용:**

```kotlin
package com.gcgenome.lims.interpretable.impl

@Service
class LungCancer(
    private val snvDao: SnvDao,
    private val requestDao: RequestDao
) : SomaticCancer() {  // 또는 AbstractPanel 상속

    override fun interpret(sample: Long, service: String): Mono<InterpretationSomatic> {
        return snvDao.findBySample(sample, service)
            .collectList()
            .flatMap { snvList ->
                // 1. Tier 분류
                val tiered = classifyByTier(snvList)

                // 2. 약물 매칭
                val drugMatches = matchDrugs(tiered)

                // 3. TMB 계산
                val tmb = calculateTMB(snvList)

                // 4. MSI 상태 판정
                val msi = determineMSI(sample, service)

                // 5. 한국어 해석 문구 생성
                val interpretation = generateKoreanInterpretation(tiered, drugMatches)

                Mono.just(InterpretationSomatic(
                    variants = tiered,
                    tmb = tmb,
                    msi = msi,
                    drugMatches = drugMatches,
                    interpretation = interpretation
                ))
            }
    }

    override fun negative(sample: Long, service: String): Mono<String> {
        return Mono.just("""
            본 검사에서 폐암과 관련된 임상적으로 유의미한 체세포 변이는
            검출되지 않았습니다. 다만, 본 검사는 50개 유전자의
            coding region을 대상으로 하며, 전체 유전체를 분석하지는
            않았음을 알려드립니다.
        """.trimIndent())
    }

    private fun classifyByTier(snvList: List<Snv>): Map<Int, List<Snv>> {
        return snvList.groupBy { snv ->
            when {
                isActionable(snv) -> 1      // Tier 1
                isPrognostic(snv) -> 2      // Tier 2
                isBiological(snv) -> 3      // Tier 3
                else -> 4                    // Tier 4
            }
        }
    }

    private fun isActionable(snv: Snv): Boolean {
        // EGFR L858R, ALK fusion 등 FDA 승인 약물 있는 변이
        val actionableVariants = setOf(
            "EGFR:p.L858R",
            "EGFR:p.T790M",
            "EGFR:p.L861Q",
            "ALK:fusion",
            "ROS1:fusion",
            "BRAF:p.V600E"
        )
        return actionableVariants.contains("${snv.gene}:${snv.hgvsp}")
    }

    private fun matchDrugs(tiered: Map<Int, List<Snv>>): List<DrugMatch> {
        val drugDatabase = mapOf(
            "EGFR:p.L858R" to listOf("Gefitinib", "Erlotinib", "Afatinib"),
            "ALK:fusion" to listOf("Crizotinib", "Ceritinib", "Alectinib"),
            "BRAF:p.V600E" to listOf("Dabrafenib", "Vemurafenib")
        )

        return tiered[1]?.flatMap { snv ->
            val key = "${snv.gene}:${snv.hgvsp}"
            drugDatabase[key]?.map { drug ->
                DrugMatch(
                    variant = key,
                    drug = drug,
                    evidence = "FDA approved"
                )
            } ?: emptyList()
        } ?: emptyList()
    }

    private fun calculateTMB(snvList: List<Snv>): Double {
        // TMB = (총 변이 수 / 검사 영역 크기 Mb)
        val totalVariants = snvList.size
        val panelSizeMb = 1.5  // 폐암 패널: 1.5 Mb
        return totalVariants / panelSizeMb
    }

    private fun determineMSI(sample: Long, service: String): Mono<String> {
        // MSI 상태 판정 로직
        // 실제로는 MSI marker 분석 결과 필요
        return Mono.just("MSS")  // MSS, MSI-L, MSI-H
    }
}
```

**체크포인트:**
- [ ] interpret() 메소드 구현
- [ ] negative() 메소드 구현
- [ ] Tier 분류 로직 구현
- [ ] 약물 매칭 로직 구현
- [ ] TMB 계산 로직 구현
- [ ] MSI 판정 로직 구현

---

### 2.3 DTO 정의

**작업 위치:** `interpretation/src/main/kotlin/com/gcgenome/lims/dto/`

**파일 생성:** `InterpretationLungCancer.kt`

**작업 내용:**

```kotlin
package com.gcgenome.lims.dto

data class InterpretationSomatic(
    val variants: Map<Int, List<LungCancerVariant>>,  // Tier별 변이
    val tmb: Double,                                   // TMB 점수
    val msi: String,                                   // MSI 상태
    val drugMatches: List<DrugMatch>,                  // 약물 매칭
    val qc: QcMetrics?,                                // QC 지표
    val interpretation: String                         // 해석 문구
)

data class LungCancerVariant(
    val gene: String,
    val transcript: String,
    val hgvsc: String,
    val hgvsp: String,
    val vaf: Double,
    val coverage: Int,
    val tier: Int,
    val clinvar: String?,
    val cosmic: String?
)

data class DrugMatch(
    val variant: String,
    val drug: String,
    val evidence: String,
    val references: List<String>? = null
)

data class QcMetrics(
    val coverage: Int,
    val uniformity: Double,
    val onTarget: Double,
    val status: String  // PASS, WARNING, FAIL
)
```

---

### 2.4 라우터에 패널 등록

**작업 위치:** `interpretation/src/main/kotlin/com/gcgenome/lims/service/Router.kt`

**수정 내용:**

```kotlin
// 기존 (28-34라인)
private val services: Map<String, Any> = (
    RareDiseasePanel.values() +
    SingleGenePanel.values() +
    BloodCancerPanel.values() +
    SolidTumorPanel.values() +
    // ... 기타 패널
).stream().collect(Collectors.toMap(HasCode::code, identity()))

// 수정 후
private val services: Map<String, Any> = (
    RareDiseasePanel.values() +
    SingleGenePanel.values() +
    BloodCancerPanel.values() +
    SolidTumorPanel.values() +
    LungCancerPanel.values() +  // ← 추가
    // ... 기타 패널
).stream().collect(Collectors.toMap(HasCode::code, identity()))
```

**체크포인트:**
- [ ] Router.kt 수정 완료
- [ ] 빌드 오류 없음

---

### 2.5 한국어 해석 문구 구현 (선택)

**작업 위치:** `interpretation/src/main/kotlin/com/gcgenome/lims/interpretable/kokr/`

**파일 생성:** `LungCancerPhraseKoKr.kt`

**작업 내용:**

```kotlin
package com.gcgenome.lims.interpretable.kokr

object LungCancerPhraseKoKr {

    fun drugMatchPhrase(variant: String, drug: String): String {
        return when (variant) {
            "EGFR:p.L858R" ->
                "$drug는 EGFR L858R 변이를 가진 폐암 환자에서 1차 치료제로 FDA 승인되었습니다."
            "ALK:fusion" ->
                "$drug는 ALK 융합 유전자를 가진 폐암 환자에서 효과적인 표적 치료제입니다."
            else ->
                "$drug는 $variant 변이에 대한 표적 치료제로 고려될 수 있습니다."
        }
    }

    fun tmbPhrase(tmb: Double): String {
        return when {
            tmb >= 20 -> "TMB-High (${tmb} mutations/Mb): 면역항암제 반응 가능성이 높습니다."
            tmb >= 10 -> "TMB-Intermediate (${tmb} mutations/Mb): 면역항암제 반응 가능성이 있습니다."
            else -> "TMB-Low (${tmb} mutations/Mb): 면역항암제 반응 가능성이 낮습니다."
        }
    }

    fun msiPhrase(msi: String): String {
        return when (msi) {
            "MSI-H" -> "MSI-High: 면역항암제(pembrolizumab 등) 치료 대상입니다."
            "MSI-L" -> "MSI-Low: 미세부수체 불안정성이 낮게 관찰됩니다."
            "MSS" -> "MSS (Microsatellite Stable): 미세부수체 안정성이 유지됩니다."
            else -> msi
        }
    }
}
```

---

## 🔬 STEP 3: BI 분석 파일 처리 (bi-analysis-subscriber 모듈)

### 3.1 디렉토리 구조 생성

**작업 위치:** `bi-analysis-subscriber/src/main/kotlin/com/gcgenome/lims/analysis/actor/`

**디렉토리 생성:**

```
actor/
└── lung/
    ├── WardenLung.kt           # 파일 감시자
    ├── Lung.kt                 # 공통 인터페이스
    ├── LungQc.kt              # QC 파일 읽기
    ├── LungSnv.kt             # SNV 파일 읽기
    ├── LungCnv.kt             # CNV 파일 읽기
    └── LungFusion.kt          # Fusion 파일 읽기
```

---

### 3.2 Warden 구현

**파일:** `WardenLung.kt`

```kotlin
package com.gcgenome.lims.analysis.actor.lung

import com.gcgenome.lims.analysis.actor.Warden
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.moveTo

@Service("wardenLung")
class WardenLung(
    @Value("\${subscriber.lung.path}") val path: Path,
    @Value("\${subscriber.lung.interval}") interval: Long = 10000,
    @Value("\${subscriber.processed}") val processed: Path,
    @Value("\${subscriber.error}") val error: Path,
    val readers: List<Lung>
) : Warden(path, interval) {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun create(path: Path) {
        readers.filter { it.chkFormat(path) }.forEach { reader ->
            val batch = reader.batch(path)
            try {
                val dest = if(batch != null) {
                    Path.of(path.absolutePathString()
                        .replace(this.path.absolutePathString(),
                                this.processed.resolve(batch).absolutePathString()))
                } else null

                // 파일 처리
                reader.exec(path)

                // 성공 시 processed 폴더로 이동
                if (dest != null) {
                    if (dest.parent.toFile().exists().not()) {
                        dest.parent.toFile().mkdirs()
                    }
                    logger.info("✓ 처리 완료, 이동: ${dest.absolutePathString()}")
                    path.moveTo(dest)
                }
            } catch(e: Exception) {
                logger.error("✗ 처리 실패: ${path.fileName}", e)

                // 실패 시 error 폴더로 이동
                val dest = if(batch != null) {
                    Path.of(path.absolutePathString()
                        .replace(this.path.absolutePathString(),
                                this.error.resolve(batch).absolutePathString()))
                } else null

                if (dest != null) {
                    if (dest.parent.toFile().exists().not()) {
                        dest.parent.toFile().mkdirs()
                    }
                    logger.info("→ 에러 폴더로 이동: ${dest.absolutePathString()}")
                    path.moveTo(dest)
                }
            }
        }
    }
}
```

---

### 3.3 파일 리더 구현

**파일:** `Lung.kt` (인터페이스)

```kotlin
package com.gcgenome.lims.analysis.actor.lung

import java.nio.file.Path

interface Lung {
    fun chkFormat(path: Path): Boolean  // 파일 형식 체크
    fun batch(path: Path): String?      // 배치 정보 추출
    suspend fun exec(path: Path)        // 파일 처리
}
```

**파일:** `LungQc.kt`

```kotlin
package com.gcgenome.lims.analysis.actor.lung

import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name

@Component
class LungQc(
    private val analysisRepository: AnalysisRepository
) : AbstractQcFileReader(), Lung {

    override fun chkFormat(path: Path): Boolean {
        // L001_QC_20250119_12345.txt 형식 체크
        return path.name.matches(Regex("L\\d{3}_QC_\\d{8}_\\d+\\.txt"))
    }

    override fun batch(path: Path): String? {
        // 파일명에서 날짜 추출: 20250119
        return Regex("L\\d{3}_QC_(\\d{8})_\\d+\\.txt")
            .find(path.name)
            ?.groupValues?.get(1)
    }

    override suspend fun exec(path: Path) {
        val lines = Files.readAllLines(path)

        // 헤더 파싱
        val header = lines[0].split("\t")

        // 데이터 파싱
        lines.drop(1).forEach { line ->
            val cols = line.split("\t")
            val qc = QcData(
                sample = cols[header.indexOf("Sample")].toLong(),
                coverage = cols[header.indexOf("Coverage")].toInt(),
                uniformity = cols[header.indexOf("Uniformity")].toDouble(),
                onTarget = cols[header.indexOf("OnTarget")].toDouble(),
                status = cols[header.indexOf("QC_Status")]
            )

            // DB 저장
            analysisRepository.saveQc(qc).subscribe()
        }

        logger.info("QC 파일 처리 완료: ${path.name}")
    }
}
```

**파일:** `LungSnv.kt`

```kotlin
package com.gcgenome.lims.analysis.actor.lung

import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path

@Component
class LungSnv(
    private val elasticsearchRepository: ElasticsearchRepository
) : AbstractSnvFileReader(), Lung {

    override fun chkFormat(path: Path): Boolean {
        return path.name.matches(Regex("L\\d{3}_SNV_\\d{8}_\\d+\\.txt"))
    }

    override fun batch(path: Path): String? {
        return Regex("L\\d{3}_SNV_(\\d{8})_\\d+\\.txt")
            .find(path.name)
            ?.groupValues?.get(1)
    }

    override suspend fun exec(path: Path) {
        val lines = Files.readAllLines(path)
        val header = lines[0].split("\t")

        val snvList = lines.drop(1).map { line ->
            val cols = line.split("\t")
            SnvDocument(
                sample = extractSampleId(path),
                service = "L001",
                gene = cols[header.indexOf("Gene")],
                transcript = cols[header.indexOf("Transcript")],
                hgvsc = cols[header.indexOf("HGVS.c")],
                hgvsp = cols[header.indexOf("HGVS.p")],
                vaf = cols[header.indexOf("VAF")].toDouble(),
                coverage = cols[header.indexOf("Coverage")].toInt(),
                tier = cols[header.indexOf("Tier")].toInt(),
                drug = cols.getOrNull(header.indexOf("Drug"))
            )
        }

        // Elasticsearch 저장
        snvList.forEach { snv ->
            elasticsearchRepository.save(snv).subscribe()
        }

        logger.info("SNV 파일 처리 완료: ${path.name}, ${snvList.size}개 변이")
    }

    private fun extractSampleId(path: Path): Long {
        return Regex("L\\d{3}_SNV_\\d{8}_(\\d+)\\.txt")
            .find(path.name)
            ?.groupValues?.get(1)
            ?.toLong()
            ?: throw IllegalArgumentException("샘플 ID 추출 실패: ${path.name}")
    }
}
```

**파일:** `LungCnv.kt`, `LungFusion.kt` (비슷한 구조)

---

### 3.4 설정 파일 수정

**작업 위치:** `bi-analysis-subscriber/src/main/resources/application.yml`

**추가 내용:**

```yaml
subscriber:
  lung:
    path: /data/analysis/lung      # 폐암 패널 파일 감시 디렉토리
    interval: 10000                # 10초마다 체크
  processed: /data/processed       # 처리 완료 파일 이동 경로
  error: /data/error              # 에러 파일 이동 경로
```

**체크포인트:**
- [ ] Warden 구현 완료
- [ ] QC 파일 리더 구현
- [ ] SNV 파일 리더 구현
- [ ] CNV, Fusion 리더 구현 (필요시)
- [ ] application.yml 설정 완료

---

## 🎨 STEP 4: UI 작업 (interpretation-ui 모듈)

### 4.1 UI 컴포넌트 생성

**작업 위치:** `interpretation-ui/src/main/java/com/gcgenome/lims/client/expand/`

**파일 생성:** `LungCancerExpandElement.java`

```java
package com.gcgenome.lims.client.expand;

import com.gcgenome.lims.client.expand.lung.*;
import org.jboss.elemento.IsElement;
import elemental2.dom.HTMLElement;
import static org.jboss.elemento.Elements.*;

public class LungCancerExpandElement extends AbstractPanelExpandElement {

    private TierTable tierTable;
    private DrugMatchPanel drugMatchPanel;
    private TmbScore tmbScore;
    private MsiStatus msiStatus;

    public LungCancerExpandElement() {
        super();
        init();
    }

    private void init() {
        // Tier별 변이 테이블
        tierTable = new TierTable();

        // 약물 매칭 패널
        drugMatchPanel = new DrugMatchPanel();

        // TMB 점수
        tmbScore = new TmbScore();

        // MSI 상태
        msiStatus = new MsiStatus();

        // 레이아웃 구성
        element = div()
            .add(h3().textContent("폐암 패널 해석 결과"))
            .add(div().css("qc-section")
                .add(h4().textContent("QC 지표"))
                // QC 지표 추가
            )
            .add(div().css("variant-section")
                .add(h4().textContent("변이 목록"))
                .add(tierTable)
            )
            .add(div().css("drug-section")
                .add(h4().textContent("약물 매칭 정보"))
                .add(drugMatchPanel)
            )
            .add(div().css("biomarker-section")
                .add(h4().textContent("바이오마커"))
                .add(tmbScore)
                .add(msiStatus)
            )
            .element();
    }

    public void setData(InterpretationSomatic data) {
        tierTable.setVariants(data.getVariants());
        drugMatchPanel.setDrugMatches(data.getDrugMatches());
        tmbScore.setScore(data.getTmb());
        msiStatus.setStatus(data.getMsi());
    }
}
```

---

### 4.2 폐암 특화 컴포넌트

**작업 위치:** `interpretation-ui/src/main/java/com/gcgenome/lims/client/expand/lung/`

**파일 생성:**

#### `TierTable.java`

```java
package com.gcgenome.lims.client.expand.lung;

import net.sayaya.ui.elements.TableElement;
import java.util.Map;
import java.util.List;

public class TierTable implements IsElement<HTMLElement> {

    private TableElement table;

    public TierTable() {
        table = TableElement.create()
            .addColumn("Tier", variant -> getTierBadge(variant.getTier()))
            .addColumn("Gene", LungCancerVariant::getGene)
            .addColumn("Variant", LungCancerVariant::getHgvsp)
            .addColumn("VAF", v -> String.format("%.1f%%", v.getVaf()))
            .addColumn("Coverage", v -> v.getCoverage() + "x")
            .addColumn("약물", v -> v.getDrug() != null ? v.getDrug() : "-");
    }

    public void setVariants(Map<Integer, List<LungCancerVariant>> tieredVariants) {
        // Tier 1부터 순서대로 표시
        for (int tier = 1; tier <= 4; tier++) {
            if (tieredVariants.containsKey(tier)) {
                tieredVariants.get(tier).forEach(table::add);
            }
        }
    }

    private HTMLElement getTierBadge(int tier) {
        String color = switch(tier) {
            case 1 -> "red";      // Actionable
            case 2 -> "orange";   // Prognostic
            case 3 -> "blue";     // Biological
            default -> "gray";    // Unknown
        };

        return span()
            .css("tier-badge", "tier-" + tier)
            .style("background-color: " + color)
            .textContent("Tier " + tier)
            .element();
    }

    @Override
    public HTMLElement element() {
        return table.element();
    }
}
```

#### `DrugMatchPanel.java`

```java
package com.gcgenome.lims.client.expand.lung;

public class DrugMatchPanel implements IsElement<HTMLElement> {

    private HTMLElement element;

    public DrugMatchPanel() {
        element = div().css("drug-match-panel").element();
    }

    public void setDrugMatches(List<DrugMatch> matches) {
        element.innerHTML = "";  // 초기화

        matches.forEach(match -> {
            HTMLElement card = div().css("drug-card")
                .add(div().css("variant-info")
                    .add(strong().textContent(match.getVariant())))
                .add(div().css("drug-name")
                    .add(span().textContent("치료제: " + match.getDrug())))
                .add(div().css("evidence")
                    .add(span().textContent("근거: " + match.getEvidence())))
                .element();

            element.appendChild(card);
        });
    }

    @Override
    public HTMLElement element() {
        return element;
    }
}
```

#### `TmbScore.java`

```java
package com.gcgenome.lims.client.expand.lung;

public class TmbScore implements IsElement<HTMLElement> {

    private HTMLElement element;
    private HTMLElement scoreElement;
    private HTMLElement interpretationElement;

    public TmbScore() {
        scoreElement = span().css("tmb-score").element();
        interpretationElement = div().css("tmb-interpretation").element();

        element = div().css("tmb-container")
            .add(div().css("tmb-header")
                .add(strong().textContent("TMB (Tumor Mutational Burden): "))
                .add(scoreElement))
            .add(interpretationElement)
            .element();
    }

    public void setScore(double tmb) {
        scoreElement.textContent = String.format("%.2f mutations/Mb", tmb);

        String interpretation;
        String cssClass;

        if (tmb >= 20) {
            interpretation = "TMB-High: 면역항암제 반응 가능성이 높습니다.";
            cssClass = "tmb-high";
        } else if (tmb >= 10) {
            interpretation = "TMB-Intermediate: 면역항암제 반응 가능성이 있습니다.";
            cssClass = "tmb-intermediate";
        } else {
            interpretation = "TMB-Low: 면역항암제 반응 가능성이 낮습니다.";
            cssClass = "tmb-low";
        }

        interpretationElement.textContent = interpretation;
        interpretationElement.className = "tmb-interpretation " + cssClass;
    }

    @Override
    public HTMLElement element() {
        return element;
    }
}
```

#### `MsiStatus.java`

```java
package com.gcgenome.lims.client.expand.lung;

public class MsiStatus implements IsElement<HTMLElement> {

    private HTMLElement element;
    private HTMLElement statusElement;

    public MsiStatus() {
        statusElement = span().css("msi-status").element();

        element = div().css("msi-container")
            .add(strong().textContent("MSI 상태: "))
            .add(statusElement)
            .element();
    }

    public void setStatus(String msi) {
        String cssClass = switch(msi) {
            case "MSI-H" -> "msi-high";
            case "MSI-L" -> "msi-low";
            default -> "msi-stable";
        };

        statusElement.textContent = msi;
        statusElement.className = "msi-status " + cssClass;
    }

    @Override
    public HTMLElement element() {
        return element;
    }
}
```

**체크포인트:**
- [ ] LungCancerExpandElement 생성
- [ ] TierTable 구현
- [ ] DrugMatchPanel 구현
- [ ] TmbScore 구현
- [ ] MsiStatus 구현
- [ ] CSS 스타일링
- [ ] API 연동 테스트

---

## 🧪 STEP 5: 테스트 작업

### 5.1 백엔드 단위 테스트

**작업 위치:** `interpretation/src/test/kotlin/com/gcgenome/lims/interpretable/impl/`

**파일 생성:** `LungCancerTest.kt`

```kotlin
package com.gcgenome.lims.interpretable.impl

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

class LungCancerTest : BehaviorSpec({

    val snvDao = mockk<SnvDao>()
    val requestDao = mockk<RequestDao>()
    val lungCancer = LungCancer(snvDao, requestDao)

    given("EGFR L858R 변이가 있는 검체") {
        val sampleId = 12345L
        val service = "L001"

        val snvList = listOf(
            createSnv("EGFR", "p.L858R", 35.2, 1200),
            createSnv("KRAS", "p.G12D", 28.5, 1500)
        )

        every { snvDao.findBySample(sampleId, service) } returns Flux.fromIterable(snvList)

        `when`("자동 해석을 실행하면") {
            val result = lungCancer.interpret(sampleId, service)

            then("Tier 1에 EGFR 변이가 분류되어야 함") {
                StepVerifier.create(result)
                    .assertNext { interpretation ->
                        interpretation.variants[1] shouldNotBe null
                        interpretation.variants[1]?.any { it.gene == "EGFR" } shouldBe true
                    }
                    .verifyComplete()
            }

            then("Gefitinib 약물이 매칭되어야 함") {
                StepVerifier.create(result)
                    .assertNext { interpretation ->
                        interpretation.drugMatches.any {
                            it.drug == "Gefitinib"
                        } shouldBe true
                    }
                    .verifyComplete()
            }
        }
    }

    given("변이가 없는 검체") {
        val sampleId = 99999L
        val service = "L001"

        every { snvDao.findBySample(sampleId, service) } returns Flux.empty()

        `when`("음성 결과 해석을 요청하면") {
            val result = lungCancer.negative(sampleId, service)

            then("음성 결과 문구가 반환되어야 함") {
                StepVerifier.create(result)
                    .assertNext { text ->
                        text shouldNotBe null
                        text.contains("검출되지 않았습니다") shouldBe true
                    }
                    .verifyComplete()
            }
        }
    }

    given("다양한 변이가 있는 검체") {
        val snvList = listOf(
            createSnv("EGFR", "p.L858R", 35.2, 1200),   // Tier 1
            createSnv("ALK", "fusion", 42.1, 980),       // Tier 1
            createSnv("KRAS", "p.G12D", 28.5, 1500),     // Tier 2
            createSnv("TP53", "p.R273H", 45.0, 1100),    // Tier 3
            createSnv("UNKNOWN", "p.X123Y", 5.2, 500)    // Tier 4
        )

        `when`("TMB를 계산하면") {
            val tmb = lungCancer.calculateTMB(snvList)

            then("변이 수 / 패널 크기로 계산되어야 함") {
                val expected = snvList.size / 1.5  // 1.5 Mb
                tmb shouldBe expected
            }
        }
    }
})

private fun createSnv(gene: String, hgvsp: String, vaf: Double, coverage: Int): Snv {
    return Snv(
        gene = gene,
        transcript = "NM_123456",
        hgvsc = "c.123A>G",
        hgvsp = hgvsp,
        vaf = vaf,
        coverage = coverage
    )
}
```

**테스트 커버리지 목표:** 80% 이상

**체크포인트:**
- [ ] interpret() 테스트
- [ ] negative() 테스트
- [ ] Tier 분류 테스트
- [ ] 약물 매칭 테스트
- [ ] TMB 계산 테스트
- [ ] 테스트 커버리지 80% 이상

---

### 5.2 통합 테스트

**작업 위치:** `interpretation/src/test/kotlin/`

**파일 생성:** `LungCancerIntegrationTest.kt`

```kotlin
package com.gcgenome.lims

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.extensions.testcontainers.perSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.GenericContainer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LungCancerIntegrationTest(
    private val webTestClient: WebTestClient
) : BehaviorSpec({

    extension(SpringExtension)

    val postgres = PostgreSQLContainer<Nothing>("postgres:15").apply {
        withDatabaseName("lims_test")
        withUsername("test")
        withPassword("test")
    }

    val elasticsearch = GenericContainer<Nothing>("elasticsearch:8.11.0").apply {
        withExposedPorts(9200)
        withEnv("discovery.type", "single-node")
    }

    listener(postgres.perSpec())
    listener(elasticsearch.perSpec())

    given("폐암 패널 서비스 API") {

        `when`("GET /services/L001 요청") {
            then("패널 정보가 반환되어야 함") {
                webTestClient.get()
                    .uri("/services/L001")
                    .header("Content-Type", "application/vnd.lims.v1+json")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("L001")
                    .jsonPath("$.description").isEqualTo("폐암 패널 v1")
            }
        }

        `when`("PUT /samples/12345/services/L001/auto-interpret 요청") {
            then("자동 해석이 실행되어야 함") {
                webTestClient.put()
                    .uri("/samples/12345/services/L001/auto-interpret")
                    .header("Content-Type", "application/vnd.lims.v1+json")
                    .bodyValue(mapOf("variants" to emptyList<Any>()))
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .jsonPath("$.tmb").exists()
                    .jsonPath("$.msi").exists()
            }
        }
    }
})
```

---

### 5.3 BI 파일 처리 테스트

**작업 위치:** `bi-analysis-subscriber/src/test/kotlin/`

**파일 생성:** `LungFileProcessingTest.kt`

```kotlin
package com.gcgenome.lims.analysis.actor.lung

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeText

class LungFileProcessingTest : BehaviorSpec({

    val tempDir = Files.createTempDirectory("lung-test")

    given("L001_QC_20250119_12345.txt 파일") {
        val qcFile = tempDir.resolve("L001_QC_20250119_12345.txt")
        qcFile.writeText("""
            Sample	Coverage	Uniformity	OnTarget	QC_Status
            12345	500	95.2	98.5	PASS
        """.trimIndent())

        val lungQc = LungQc(mockk())

        `when`("파일 형식을 체크하면") {
            val result = lungQc.chkFormat(qcFile)

            then("true를 반환해야 함") {
                result shouldBe true
            }
        }

        `when`("배치 정보를 추출하면") {
            val batch = lungQc.batch(qcFile)

            then("20250119를 반환해야 함") {
                batch shouldBe "20250119"
            }
        }
    }

    given("L001_SNV_20250119_12345.txt 파일") {
        val snvFile = tempDir.resolve("L001_SNV_20250119_12345.txt")
        snvFile.writeText("""
            Gene	Transcript	HGVS.c	HGVS.p	VAF	Coverage	Tier	Drug
            EGFR	NM_005228.5	c.2573T>G	p.L858R	35.2	1200	1	Gefitinib
            ALK	NM_004304.4	c.3522C>T	p.R1174W	42.1	980	1	Crizotinib
        """.trimIndent())

        val lungSnv = LungSnv(mockk())

        `when`("파일을 처리하면") {
            // 실제 처리 로직 테스트
            // Elasticsearch mock 필요
            then("2개의 변이가 저장되어야 함") {
                // 검증 로직
            }
        }
    }

    afterSpec {
        // 임시 파일 정리
        tempDir.toFile().deleteRecursively()
    }
})
```

---

## 🚀 STEP 6: 배포 작업

### 6.1 빌드 확인

```bash
# 백엔드 빌드
./gradlew :interpretation:build
./gradlew :bi-analysis-subscriber:build

# UI 빌드
./gradlew :interpretation-ui:build

# 전체 테스트
./gradlew test

# 커버리지 확인
./gradlew koverHtmlReport
```

**체크포인트:**
- [ ] 빌드 성공
- [ ] 테스트 통과
- [ ] 커버리지 80% 이상
- [ ] 의존성 충돌 없음

---

### 6.2 Test 환경 배포

**GitHub Actions 사용:**

```yaml
# .github/workflows/interpretation-deploy.yaml
name: Deploy Interpretation (Test)

on:
  push:
    branches:
      - feature/lung-cancer-panel
    paths:
      - 'interpretation/**'

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Setup JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'

      - name: Build with Gradle
        run: ./gradlew :interpretation:build

      - name: Build Docker Image (Jib)
        run: ./gradlew :interpretation:jib

      - name: Deploy to OpenShift
        run: |
          # OpenShift 배포 스크립트
          oc rollout restart deployment/interpretation2-service
```

**수동 배포:**

```bash
# 1. JAR 빌드
./gradlew :interpretation:build

# 2. Docker 이미지 빌드 (Jib)
./gradlew :interpretation:jib

# 3. OpenShift 배포
oc rollout restart deployment/interpretation2-service

# 4. 배포 확인
oc get pods | grep interpretation2-service
oc logs -f <pod-name>
```

---

### 6.3 Prod 환경 배포

**Jenkins 파이프라인:**

```groovy
// Jenkinsfile
pipeline {
    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['test', 'prod'])
    }

    stages {
        stage('Build Interpretation') {
            steps {
                sh './gradlew :interpretation:build'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew :interpretation:test'
                sh './gradlew :interpretation:koverVerify'
            }
        }

        stage('Deploy to Prod') {
            when {
                expression { params.DEPLOY_ENV == 'prod' }
            }
            steps {
                // SSH를 통해 Aries/Taurus 서버에 배포
                sh '''
                    scp interpretation/build/libs/*.jar user@aries:/opt/lims/
                    ssh user@aries "systemctl restart panel-interpretation"
                '''
            }
        }
    }
}
```

**체크포인트:**
- [ ] Test 환경 배포 성공
- [ ] Test 환경 동작 확인
- [ ] Prod 환경 배포 승인
- [ ] Prod 환경 배포 성공
- [ ] Prod 환경 동작 확인

---

## 🔄 STEP 7: 전체 플로우 검증

### 실제 시나리오 테스트

```
1. 검사 접수
   - 검체 ID: 12345
   - 패널: L001 (폐암 패널)
   ✓ 워크리스트 생성 완료

2. 시리얼 번호 생성
   - PUT /worklists/{id}/generate-serials
   ✓ 시리얼: L001-2025-001

3. BI 분석 수행 (외부 시스템)
   ✓ 분석 완료

4. 파일 생성
   - /data/analysis/lung/L001_QC_20250119_12345.txt
   - /data/analysis/lung/L001_SNV_20250119_12345.txt
   - /data/analysis/lung/L001_CNV_20250119_12345.txt
   - /data/analysis/lung/L001_Fusion_20250119_12345.txt
   ✓ 파일 생성 완료

5. WardenLung 파일 감지
   - 10초 이내 감지
   ✓ 파일 감지 완료

6. QC 파일 처리
   - LungQc.exec() 실행
   - Coverage: 500x, Uniformity: 95.2%, Status: PASS
   ✓ panel.analysis 테이블 저장 완료

7. SNV 파일 처리
   - LungSnv.exec() 실행
   - 발견된 변이: EGFR L858R, ALK fusion, KRAS G12D
   ✓ Elasticsearch 저장 완료
   ✓ Tier 자동 분류 완료

8. CNV, Fusion 파일 처리
   ✓ 처리 완료

9. 파일 이동
   ✓ /data/processed/20250119/ 폴더로 이동 완료

10. Jandi 알림
    ✓ "검체 12345 폐암 패널 분석 완료" 알림 발송

11. SNV 조회
    - POST /samples/12345/services/L001/batches/20250119/1/snvs
    ✓ 3개 변이 조회 완료

12. 자동 해석 실행
    - PUT /samples/12345/services/L001/auto-interpret
    - LungCancer.interpret() 실행
    ✓ Tier 1: EGFR L858R, ALK fusion
    ✓ Tier 2: KRAS G12D
    ✓ 약물 매칭: Gefitinib, Crizotinib
    ✓ TMB 계산: 2.0 mutations/Mb
    ✓ MSI: MSS
    ✓ 한국어 해석 문구 생성 완료

13. 해석 결과 저장
    ✓ panel.interpretation 테이블 저장 완료

14. UI에서 확인
    - GET /samples/12345/services/L001/interpretation
    ✓ LungCancerExpandElement에 표시
    ✓ Tier 테이블 표시 완료
    ✓ 약물 정보 표시 완료
    ✓ TMB 점수 표시 완료
    ✓ MSI 상태 표시 완료

15. 전문의 검토
    ✓ 해석 결과 확인
    ✓ 필요시 수정
    ✓ 저장 완료

16. 보고서 발행
    ✓ 완료
```

---

## ✅ 체크리스트

### 백엔드 체크리스트

- [ ] 패널 타입 정의 (LungCancerPanel.kt)
- [ ] 해석 로직 구현 (LungCancer.kt)
  - [ ] interpret() 메소드
  - [ ] negative() 메소드
  - [ ] Tier 분류
  - [ ] 약물 매칭
  - [ ] TMB 계산
  - [ ] MSI 판정
- [ ] DTO 정의 (InterpretationLungCancer.kt)
- [ ] Router 등록 (Router.kt)
- [ ] 한국어 문구 (LungCancerPhraseKoKr.kt) - 선택
- [ ] 단위 테스트 (LungCancerTest.kt)
- [ ] 통합 테스트 (LungCancerIntegrationTest.kt)
- [ ] 테스트 커버리지 80% 이상

### BI 파일 처리 체크리스트

- [ ] Warden 구현 (WardenLung.kt)
- [ ] 공통 인터페이스 (Lung.kt)
- [ ] QC 파일 리더 (LungQc.kt)
- [ ] SNV 파일 리더 (LungSnv.kt)
- [ ] CNV 파일 리더 (LungCnv.kt) - 선택
- [ ] Fusion 파일 리더 (LungFusion.kt) - 선택
- [ ] application.yml 설정
- [ ] 파일 처리 테스트 (LungFileProcessingTest.kt)

### UI 체크리스트

- [ ] 메인 컴포넌트 (LungCancerExpandElement.java)
- [ ] Tier 테이블 (TierTable.java)
- [ ] 약물 매칭 패널 (DrugMatchPanel.java)
- [ ] TMB 점수 (TmbScore.java)
- [ ] MSI 상태 (MsiStatus.java)
- [ ] CSS 스타일링
- [ ] API 연동
- [ ] 화면 테스트

### 배포 체크리스트

- [ ] 빌드 성공
- [ ] 테스트 통과
- [ ] Test 환경 배포
- [ ] Test 환경 검증
- [ ] Prod 환경 배포 승인
- [ ] Prod 환경 배포
- [ ] Prod 환경 검증
- [ ] 롤백 계획 수립

### 문서화 체크리스트

- [ ] 패널 명세서 작성
- [ ] API 문서 작성
- [ ] BI팀 가이드 작성
- [ ] 운영 가이드 작성
- [ ] 사용자 매뉴얼 작성

### BI팀 협업 체크리스트

- [ ] 파일 명명 규칙 합의
- [ ] 파일 형식 합의
- [ ] 컬럼 정의 합의
- [ ] 파일 전송 경로 설정
- [ ] 권한 설정
- [ ] 테스트 파일 수령
- [ ] 실제 파일로 검증

---

## 📅 예상 일정

### 1주차: 요구사항 및 백엔드 기본 구현

**월요일:**
- 요구사항 정의
- 의료진 미팅
- BI팀 미팅

**화요일-목요일:**
- 패널 타입 정의
- 해석 로직 구현
- DTO 정의
- 단위 테스트 작성

**금요일:**
- 백엔드 통합 테스트
- 코드 리뷰

### 2주차: BI 파일 처리 및 UI 기본 구현

**월요일-수요일:**
- Warden 구현
- 파일 리더 구현 (QC, SNV, CNV, Fusion)
- 파일 처리 테스트

**목요일-금요일:**
- UI 메인 컴포넌트 구현
- Tier 테이블 구현
- 약물 매칭 패널 구현

### 3주차: UI 완성 및 테스트

**월요일-수요일:**
- TMB, MSI 컴포넌트 구현
- CSS 스타일링
- API 연동

**목요일-금요일:**
- 통합 테스트
- 문서 작성
- 코드 리뷰

### 4주차: Test 환경 배포 및 검증

**월요일-화요일:**
- Test 환경 배포
- BI팀 협업 테스트

**수요일-목요일:**
- 버그 수정
- 실제 데이터 테스트

**금요일:**
- Prod 배포 승인 회의

### 5주차: Prod 배포 및 안정화

**월요일:**
- Prod 환경 배포
- 배포 후 검증

**화요일-금요일:**
- 모니터링
- 긴급 버그 수정
- 사용자 피드백 반영

---

## 🔧 트러블슈팅

### 자주 발생하는 문제

#### 1. 빌드 오류

**문제:** `LungCancerPanel` not found

**원인:** Router.kt에 추가했지만 import 누락

**해결:**
```kotlin
import com.gcgenome.lims.test.LungCancerPanel
```

---

#### 2. 파일 감지 안됨

**문제:** WardenLung이 파일을 감지하지 못함

**원인:**
- 파일 명명 규칙 불일치
- 경로 권한 문제
- Warden 미실행

**해결:**
```bash
# 파일명 확인
ls -la /data/analysis/lung/

# 권한 확인
chmod 755 /data/analysis/lung/

# Warden 로그 확인
tail -f logs/bi-analysis-subscriber.log | grep Warden
```

---

#### 3. Elasticsearch 저장 실패

**문제:** SNV가 Elasticsearch에 저장되지 않음

**원인:**
- Elasticsearch 연결 실패
- 인덱스 매핑 오류
- 데이터 형식 오류

**해결:**
```bash
# Elasticsearch 연결 확인
curl http://localhost:9200/_cluster/health

# 인덱스 확인
curl http://localhost:9200/_cat/indices?v

# 매핑 확인
curl http://localhost:9200/snv/_mapping
```

---

#### 4. UI에 데이터 표시 안됨

**문제:** 화면에 해석 결과가 표시되지 않음

**원인:**
- API 응답 형식 불일치
- JSON 파싱 오류
- 컴포넌트 초기화 오류

**해결:**
```javascript
// 브라우저 개발자 도구 Console 확인
console.log(interpretationData);

// Network 탭에서 API 응답 확인
```

---

#### 5. 테스트 커버리지 부족

**문제:** 커버리지 80% 미만

**원인:**
- edge case 테스트 누락
- negative test 누락
- exception handling 미테스트

**해결:**
- 모든 분기 테스트
- null, empty 케이스 테스트
- exception 케이스 테스트

---

## 📚 참고 자료

### 내부 문서
- PROJECT_ANALYSIS.md
- FOLDER_GUIDE.md
- 기존 패널 구현 코드 (BloodCancer.kt, SolidTumor.kt)

### 외부 문서
- Spring WebFlux 공식 문서
- Kotlin Coroutines 가이드
- GWT 공식 문서
- Elasticsearch 공식 문서

---

## 📝 버전 정보

- **문서 버전:** 1.0
- **작성일:** 2025-01-19
- **작성자:** Claude Code AI
- **최종 수정일:** 2025-01-19

---

## 💡 추가 팁

### 코드 재사용

기존 패널 코드를 최대한 활용하세요:

```kotlin
// BloodCancer.kt의 로직을 참고
class LungCancer : SomaticCancer() {
    // SomaticCancer의 공통 로직 상속
    // 폐암 특화 로직만 override
}
```

### 점진적 개발

한 번에 모든 기능을 구현하지 말고 단계적으로:

1. 최소 기능 (SNV만)
2. CNV 추가
3. Fusion 추가
4. TMB/MSI 추가

### 문서화 습관

코드 작성과 동시에 문서화:

```kotlin
/**
 * 폐암 패널 자동 해석
 *
 * @param sample 검체 ID
 * @param service 서비스 코드 (L001)
 * @return 해석 결과 (Tier, 약물 매칭, TMB, MSI)
 */
override fun interpret(sample: Long, service: String): Mono<InterpretationSomatic>
```

---

**신규 패널 추가 성공을 기원합니다! 🎉**
