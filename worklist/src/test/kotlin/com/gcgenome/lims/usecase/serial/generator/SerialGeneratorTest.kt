package com.gcgenome.lims.usecase.serial.generator

import com.gcgenome.lims.domain.Request
import com.gcgenome.lims.domain.Work
import com.gcgenome.lims.usecase.serial.SerialRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import reactor.kotlin.core.publisher.toFlux
import reactor.test.StepVerifier
import java.util.*

/*
 2025-04 현재 시점에, LIMS1에 적용된 결과에서 검사코드 별 가장 마지막 시리얼을 가져옴.
 구현된 버전의 결과와 비교하여 차이가 있는지 테스트한다.
 */
class SerialGeneratorTest : FunSpec({
    val worklistId = UUID.randomUUID()
    context("시리얼 생성 테스트") {
        data class TestCase (
            val latestIndex: Int,
            val codes: Array<out String>,
            val expected: String
        ) {
            constructor(vararg codes: String, latestIndex: Int, expected: String): this(latestIndex, codes, expected)
        }
        withData<TestCase>({
            "검사코드:${it.codes.joinToString(", ")}, 마지막 인덱스:${it.latestIndex} -> ${it.expected}" },
            listOf(
                TestCase("G0022402", latestIndex = 927, expected = "STTSO928"),
                TestCase("G068", latestIndex = 413, expected = "BRCA414"),
                TestCase("G069", latestIndex = 413, expected = "BRCA414"),
                TestCase("G2200101", latestIndex = 440, expected = "CAN441"),
                TestCase("G2200102", latestIndex = 0, expected = "TMB_R001"),
                TestCase("G2200201", latestIndex = 432, expected = "CAN433"),
                TestCase("N003", latestIndex = 418, expected = "DES419"),
                TestCase("N027", latestIndex = 459, expected = "WES460"),
                TestCase("N037", latestIndex = 1573, expected = "WES1574"),
                TestCase("N038", latestIndex = 1561, expected = "WES1562"),
                TestCase("N039", latestIndex = 844, expected = "WES845"),
                TestCase("N040", latestIndex = 561, expected = "CAN562"),
                TestCase("N041", latestIndex = 1531, expected = "WES1532"),
                TestCase("N042", latestIndex = 1547, expected = "WES1548"),
                TestCase("N043", latestIndex = 1516, expected = "WES1517"),
                TestCase("N044", latestIndex = 1545, expected = "WES1546"),
                TestCase("N045", latestIndex = 1364, expected = "WES1365"),
                TestCase("N046", latestIndex = 1564, expected = "WES1565"),
                TestCase("N047", latestIndex = 1334, expected = "WES1335"),
                TestCase("N048", latestIndex = 1544, expected = "WES1545"),
                TestCase("N049", latestIndex = 4142, expected = "WES4143"),
                TestCase("N050", latestIndex = 1519, expected = "WES1520"),
                TestCase("N051", latestIndex = 1324, expected = "WES1325"),
                TestCase("N052", latestIndex = 886, expected = "WES887"),
                TestCase("N053", latestIndex = 1560, expected = "WES1561"),
                TestCase("N054", latestIndex = 1540, expected = "WES1541"),
                TestCase("N055", latestIndex = 1300, expected = "WES1301"),
                TestCase("N057", latestIndex = 748, expected = "WES749"),
                TestCase("N058", latestIndex = 748, expected = "WES749"),
                TestCase("N059", latestIndex = 748, expected = "WES749"),
                TestCase("N062", latestIndex = 1321, expected = "WES1322"),
                TestCase("N064", latestIndex = 177, expected = "AML178"),
                TestCase("N065", latestIndex = 197, expected = "MDS198"),
                TestCase("N067", latestIndex = 1530, expected = "WES1531"),
                TestCase("N071", latestIndex = 1332, expected = "WES1333"),
                TestCase("N072", latestIndex = 1081, expected = "WES1082"),
                TestCase("N073", latestIndex = 1555, expected = "WES1556"),
                TestCase("N074", latestIndex = 2, expected = "SMC-CAN003"),
                TestCase("N075", latestIndex = 0, expected = "ST001"),          // 가장 마지막 기록이 22SMC-Cardio001
                TestCase("N076", latestIndex = 403, expected = "CAN404"),
                TestCase("N078", latestIndex = 1567, expected = "WES1568"),
                TestCase("N079", latestIndex = 3071, expected = "WES3072"),
                TestCase("N080", latestIndex = 1380, expected = "WES1381"),
                TestCase("N081", latestIndex = 1331, expected = "WES1332"),
                TestCase("N082", latestIndex = 94, expected = "ALL095"),
                TestCase("N083", latestIndex = 72, expected = "LYM073"),
                TestCase("N084", latestIndex = 1570, expected = "WES1571"),
                TestCase("N085", latestIndex = 236, expected = "DES237"),
                TestCase("N087", latestIndex = 117, expected = "ST118"),
                TestCase("N088", latestIndex = 116, expected = "ST117"),
                TestCase("N089", latestIndex = 122, expected = "ST123"),
                TestCase("N090", latestIndex = 575, expected = "CAN576"),
                TestCase("N093", latestIndex = 120, expected = "F_LYM121"),
                TestCase("N094", latestIndex = 1, expected = "F_ALL002"),
                TestCase("N095", latestIndex = 1526, expected = "WES1527"),
                TestCase("N096", latestIndex = 1562, expected = "WES1563"),
                TestCase("N097", latestIndex = 1, expected = "WES002"),         // 가장 마지막 기록이 24KD002
                TestCase("N098", latestIndex = 1569, expected = "WES1570"),
                TestCase("N099", latestIndex = 1521, expected = "WES1522"),
                TestCase("N100", latestIndex = 1576, expected = "WES1577"),
                TestCase("N101", latestIndex = 7, expected = "BRCA008"),        // 가장 마지막 기록이 23CGS008
                TestCase("N103", latestIndex = 18, expected = "ST2(161)019"),
                TestCase("N104", latestIndex = 187, expected = "MM188"),
                TestCase("N105", latestIndex = 1, expected = "F_MM002"),
                TestCase("N106", latestIndex = 1368, expected = "WES1369"),
                TestCase("N107", latestIndex = 1533, expected = "WES1534"),
                TestCase("N108", latestIndex = 21, expected = "WES022"),        // 가장 마지막 기록이 24CD022
                TestCase("N109", latestIndex = 2, expected = "GSC003"),
                TestCase("N110", latestIndex = 1563, expected = "WES1564"),
                TestCase("N111", latestIndex = 1, expected = "BRCA002"),        // 가장 마지막 기록이 23CGS002
                TestCase("N112", latestIndex = 57, expected = "ST058"),         // 가장 마지막 기록이 21GSH058
                TestCase("N117", latestIndex = 242, expected = "PAN243"),
                TestCase("N119", latestIndex = 1559, expected = "WES1560"),
                TestCase("N121", latestIndex = 1568, expected = "WES1569"),
                TestCase("N122", latestIndex = 1389, expected = "WES1390"),
                TestCase("N123", latestIndex = 1571, expected = "WES1572"),
                TestCase("N124", latestIndex = 3878, expected = "TMB_R3879"),
                TestCase("N125", latestIndex = 53, expected = "TMB_D054"),      // 가장 마지막 기록이 23TMB_D054
                TestCase("N127", latestIndex = 3, expected = "DGS004"),
                TestCase("N130", latestIndex = 1541, expected = "WES1542"),
                TestCase("N131", latestIndex = 457, expected = "DES458"),
                TestCase("N132", latestIndex = 456, expected = "DES457"),
                TestCase("N133", latestIndex = 459, expected = "DES460"),
                TestCase("N134", latestIndex = 155, expected = "WES156"),
                TestCase("N135", latestIndex = 157, expected = "WES158"),
                TestCase("N136", latestIndex = 156, expected = "WES157"),
                TestCase("N137", latestIndex = 23, expected = "DGS024"),
                TestCase("N138", latestIndex = 21, expected = "DGS022"),
                TestCase("N139", latestIndex = 24, expected = "DGS025"),
                TestCase("N140", latestIndex = 373, expected = "HRD374"),
                TestCase("N147", latestIndex = 246, expected = "PN-F247"),
                TestCase("N148", latestIndex = 1525, expected = "WES1526"),
                TestCase("N149", latestIndex = 1367, expected = "WES1368"),
                TestCase("N157", latestIndex = 245, expected = "LYM246"),
                TestCase("N158", latestIndex = 20, expected = "F_LYM021"),
                TestCase("N159", latestIndex = 0, expected = "CHIPstudy001"),
                TestCase("N176", latestIndex = 1406, expected = "WES1407"),
                TestCase("N180", latestIndex = 1357, expected = "WES1358"),
                TestCase("N181", latestIndex = 825, expected = "WES826"),
                TestCase("N182", latestIndex = 874, expected = "WES875"),
                TestCase("N183", latestIndex = 1513, expected = "WES1514"),
                TestCase("N184", latestIndex = 539, expected = "WES540"),
                TestCase("N185", latestIndex = 646, expected = "ST647"),
                TestCase("N187", latestIndex = 1036, expected = "WES1037"),
                TestCase("N188", latestIndex = 3692, expected = "WES3693"),
                TestCase("N189", latestIndex = 1548, expected = "WES1549"),
                TestCase("N190", latestIndex = 945, expected = "WES946"),
                TestCase("N191", latestIndex = 90, expected = "WES091"),
                TestCase("N192", latestIndex = 1549, expected = "WES1550"),
                TestCase("N193", latestIndex = 4025, expected = "WES4026"),
                TestCase("N194", latestIndex = 935, expected = "WES936"),
                TestCase("N198", latestIndex = 105, expected = "CFTSO106"),
                TestCase("N199", latestIndex = 1501, expected = "STTSO1502"),
                TestCase("N200", latestIndex = 976, expected = "STTSO977"),
                TestCase("N208", latestIndex = 1403, expected = "WES1404"),
                TestCase("N209", latestIndex = 43, expected = "WES044"),
                TestCase("N210", latestIndex = 43, expected = "WES044"),
                TestCase("N211", latestIndex = 43, expected = "WES044"),
                TestCase("N214", latestIndex = 2975, expected = "WES2976"),
                TestCase("N215", latestIndex = 1240, expected = "WES1241"),
                TestCase("N216", latestIndex = 313, expected = "WES314"),
                TestCase("N217", latestIndex = 0, expected = "WES001"),         // 의뢰내역 없음
                TestCase("N218", latestIndex = 1152, expected = "WES1153"),
                TestCase("N219", latestIndex = 0, expected = "WES001"),         // 의뢰내역 없음
                TestCase("N220", latestIndex = 870, expected = "WES871"),
                TestCase("N221", latestIndex = 3847, expected = "WES3848"),
                TestCase("N222", latestIndex = 3134, expected = "WES3135"),
                TestCase("N223", latestIndex = 0, expected = "WES001"),         // 의뢰내역 없음
                TestCase("N224", latestIndex = 461, expected = "WES462"),
                TestCase("N225", latestIndex = 0, expected = "WES001"),         // 의뢰내역 없음
                TestCase("N226", latestIndex = 4365, expected = "WES4366"),
                TestCase("N227", latestIndex = 802, expected = "WES803"),
                TestCase("N228", latestIndex = 4313, expected = "WES4314"),
                TestCase("N229", latestIndex = 636, expected = "WES637"),
                TestCase("N230", latestIndex = 0, expected = "WES001"),         // 의뢰내역 없음
                TestCase("N231", latestIndex = 2956, expected = "WES2957"),
                TestCase("N232", latestIndex = 0, expected = "WES001"),         // 의뢰내역 없음
                TestCase("N233", latestIndex = 0, expected = "WES001"),         // 의뢰내역 없음
                TestCase("N234", latestIndex = 2983, expected = "WES2984"),
                TestCase("N235", latestIndex = 0, expected = "WES001"),         // 의뢰내역 없음
                TestCase("N236", latestIndex = 0, expected = "WES001"),         // 의뢰내역 없음
                TestCase("N237", latestIndex = 1493, expected = "WES1494"),
                TestCase("N238", latestIndex = 0, expected = "WES001"),         // 의뢰내역 없음
                TestCase("N239", latestIndex = 1081, expected = "WES1082"),
                TestCase("N240", latestIndex = 0, expected = "WES001"),         // 의뢰내역 없음
                TestCase("N241", latestIndex = 0, expected = "WES001"),         // 의뢰내역 없음
                TestCase("N242", latestIndex = 3055, expected = "WES3056"),
                TestCase("N243", latestIndex = 583, expected = "WES584"),
                TestCase("N244", latestIndex = 3614, expected = "WES3615"),
                TestCase("N245", latestIndex = 1353, expected = "WES1354"),
                TestCase("N246", latestIndex = 410, expected = "WES411"),
                TestCase("N247", latestIndex = 1235, expected = "WES1236"),
                TestCase("N248", latestIndex = 0, expected = "WES001"),         // 의뢰내역 없음
                TestCase("N249", latestIndex = 0, expected = "WES001"),         // 의뢰내역 없음
                TestCase("N250", latestIndex = 0, expected = "WES001"),         // 의뢰내역 없음
                TestCase("N257", latestIndex = 568, expected = "CAN569"),
                TestCase("O004", latestIndex = 232, expected = "PAN233"),
                TestCase("ON001", latestIndex = 1, expected = "W-BRCA002"),
                TestCase("ON003", latestIndex = 436, expected = "DES437"),
                TestCase("ON040", latestIndex = 263, expected = "CAN264"),
                TestCase("ON064", latestIndex = 177, expected = "AML178"),
                TestCase("ON065", latestIndex = 129, expected = "MDS130"),
                TestCase("ON082", latestIndex = 163, expected = "ALL164"),      // 24ZZZ725003
                TestCase("ON083", latestIndex = 259, expected = "LYM260"),
                TestCase("ON087", latestIndex = 1484, expected = "ST1485"),
                TestCase("ON088", latestIndex = 281, expected = "ST282"),       // 가장 마지막 기록이 21SL282
                TestCase("ON089", latestIndex = 1337, expected = "ST1338"),
                TestCase("ON090", latestIndex = 287, expected = "CAN288"),
                TestCase("ON104", latestIndex = 169, expected = "MM170"),       // 24ZZZ725005
                TestCase("ON122", latestIndex = 0, expected = "WES001"),        // 가장 마지막 기록이 24mtDNA001
                TestCase("ON124", latestIndex = 3332, expected = "TMB_R3333"),
                TestCase("ON125", latestIndex = 15, expected = "TMB_D016"),
                TestCase("ON127", latestIndex = 4, expected = "DGS005"),
                TestCase("ON131", latestIndex = 565, expected = "DES566"),
                TestCase("ON132", latestIndex = 563, expected = "DES564"),
                TestCase("ON133", latestIndex = 564, expected = "DES565"),
                TestCase("ON134", latestIndex = 94, expected = "WES095"),
                TestCase("ON135", latestIndex = 95, expected = "WES096"),
                TestCase("ON136", latestIndex = 96, expected = "WES097"),
                TestCase("ON137", latestIndex = 0, expected = "DGS001"),
                TestCase("ON138", latestIndex = 1, expected = "DGS002"),
                TestCase("ON139", latestIndex = 2, expected = "DGS003"),
                TestCase("ON140", latestIndex = 133, expected = "HRD134"),
                TestCase("ON147", latestIndex = 248, expected = "PN-F249"),
                TestCase("ON198", latestIndex = 25, expected = "CFTSO026"),
                TestCase("ON199", latestIndex = 1407, expected = "STTSO1408"),
                TestCase("ON200", latestIndex = 1391, expected = "STTSO1392"),
                TestCase("ON201", latestIndex = 1405, expected = "WES1406"),
                TestCase("ON209", latestIndex = 997, expected = "WES998"),
                TestCase("ON210", latestIndex = 999, expected = "WES1000"),
                TestCase("ON211", latestIndex = 998, expected = "WES999"),
                TestCase("OS004", latestIndex = 352, expected = "DES-F353"),
                TestCase("OS022", latestIndex = 247, expected = "PN-F248"),
                TestCase("OT036", latestIndex = 40, expected = "WES-F041"),
                TestCase("R2200302", latestIndex = 33, expected = "IE034"),
                TestCase("R2300101", latestIndex = 24, expected = "WES025"),
                TestCase("S004", latestIndex = 10, expected = "DES-F011"),
                TestCase("S022", latestIndex = 245, expected = "PN-F246"),
                TestCase("S030", latestIndex = 1577, expected = "WES1578"),
                TestCase("S032", latestIndex = 1128, expected = "WES1129"),
                TestCase("S034", latestIndex = 1322, expected = "WES1323"),
                TestCase("S051", latestIndex = 52, expected = "AML053"),      // AML053-I
                TestCase("S053", latestIndex = 989, expected = "WES990"),
                TestCase("S054", latestIndex = 302, expected = "WES303"),
                TestCase("S055", latestIndex = 209, expected = "WES210"),       // 가장 마지막 기록이 23AN210
                TestCase("S061", latestIndex = 556, expected = "CAN557"),
                TestCase("S062", latestIndex = 435, expected = "CAN436"),
                TestCase("S063", latestIndex = 1403, expected = "CAN1404"),
                TestCase("S064", latestIndex = 1546, expected = "WES1547"),
                TestCase("S065", latestIndex = 1574, expected = "WES1575"),
                TestCase("S067", latestIndex = 608, expected = "WES609"),
                TestCase("S077", latestIndex = 1251, expected = "WES1252"),
                TestCase("S078", latestIndex = 1542, expected = "WES1543"),
                TestCase("S080", latestIndex = 347, expected = "WES348"),
                TestCase("S081", latestIndex = 1538, expected = "WES1539"),
                TestCase("S082", latestIndex = 1151, expected = "WES1152"),
                TestCase("S083", latestIndex = 1020, expected = "WES1021"),
                TestCase("S084", latestIndex = 2703, expected = "WES2704"),
                TestCase("S085", latestIndex = 346, expected = "WES347"),
                TestCase("S086", latestIndex = 4208, expected = "WES4209"),
                TestCase("S087", latestIndex = 213, expected = "WES214"),
                TestCase("S088", latestIndex = 3868, expected = "WES3869"),
                TestCase("S089", latestIndex = 3281, expected = "WES3282"),
                TestCase("S090", latestIndex = 4206, expected = "WES4207"),
                TestCase("S091", latestIndex = 21, expected = "WES022"),         // 가장 마지막 기록이 22SS022
                TestCase("S092", latestIndex = 3257, expected = "WES3258"),
                TestCase("S093", latestIndex = 1182, expected = "WES1183"),
                TestCase("S096", latestIndex = 522, expected = "CAN523"),
                TestCase("S097", latestIndex = 377, expected = "CAN378"),
                TestCase("S102", latestIndex = 1572, expected = "WES1573"),
                TestCase("S103", latestIndex = 2203, expected = "WES2204"),
                TestCase("S104", latestIndex = 748, expected = "WES749"),
                TestCase("S109", latestIndex = 990, expected = "WES991"),
                TestCase("S110", latestIndex = 1264, expected = "WES1265"),
                TestCase("S113", latestIndex = 0, expected = "RP001"),          // 의뢰내역 없음
                TestCase("S119", latestIndex = 0, expected = "MD001"),
                TestCase("S120", latestIndex = 1537, expected = "WES1538"),
                TestCase("S121", latestIndex = 39, expected = "WES-F040"),
                TestCase("S123", latestIndex = 741, expected = "WES742"),
                TestCase("S125", latestIndex = 1284, expected = "WES1285"),
                TestCase("S126", latestIndex = 765, expected = "WES766"),
                TestCase("S127", latestIndex = 865, expected = "WES866"),
                TestCase("S128", latestIndex = 390, expected = "CAN391"),
                TestCase("S129", latestIndex = 402, expected = "CAN403"),
                TestCase("T001", latestIndex = 101, expected = "WES102"),
                TestCase("T016", latestIndex = 0, expected = "WES001"),         // 22ZZZ183009
                TestCase("T023", latestIndex = 162, expected = "WES163"),
                TestCase("T036", latestIndex = 45, expected = "WES-F046"),
                TestCase("X009", latestIndex = 408, expected = "BRCA409"),
                TestCase("Z137", latestIndex = 427, expected = "BRCA428"),
                TestCase("Z138", latestIndex = 425, expected = "BRCA426"),
                TestCase("Z141", latestIndex = 233, expected = "CAN234"),
                TestCase("Z962", latestIndex = 1458, expected = "CAN1459"),
                TestCase("Z964", latestIndex = 519, expected = "CAN520")
            )) { (latestIndex, codes, expected) ->
            // Given
            val repo = mockk<SerialRepository>().apply {
                val latestIndexes = codes
                    .map { code -> WorklistYearyAndGroupableSerialGenerator.Companion.groupByService[code]!! }
                    .associateWith { _ -> latestIndex.toShort() }.toMutableMap()
                every { findLatest(any()) } returns latestIndexes.entries.map { it.key to it.value }.toFlux()
            }
            val work = mockk<Work>().apply {
                every { worklist } returns worklistId
                every { index } returns 1
                every { requests } returns codes.map { code -> mockk<Request>().apply {
                    every { service.id } returns code
                }}.toList()
            }
            val generator = WorklistYearyAndGroupableSerialGenerator(repo)

            // WHEN & THEN
            generator.generate(listOf(work)).let(StepVerifier::create).expectNextMatches { pair ->
                pair.first shouldBe work
                pair.second.serial.endsWith(expected)
            }.verifyComplete()
        }
    }
})