package com.gcgenome.lims.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Schema("panel")
@Table("request000")
data class Request(
    @Column("sample") val sample: Long,
    @Column("service") val service: String,
    val organization: String,
    @Column("organization_name") val organizationName: String,
    @Column("sample_type") val sampleType: String,
    val age: Int? = null,
    @Column("patient_name") val patientName: String,
    @Column("patient_code") val patientCode: String? = null,
    val mrn: String? = null,
    val sex: String,
    val birth: LocalDate? = null,
    @Column("remark")
    val info: String? = null,
    val register: Boolean = true,
    val cancel: Boolean = false,
    val delete: Boolean = false
) {
    @Id @Transient val _id: Interpretation.Companion.InterpretationPK = Interpretation.Companion.InterpretationPK(sample, service)
}