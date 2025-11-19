package com.gcgenome.lims.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Schema("panel")
@Table("disease_predefined")
data class DiseasePredefined(
    @Id @Column("gene") val gene: String,
    @Column("disease") val disease: String,
    @Column("abbreviation") val abbreviation: String,
    @Column("inheritance") val inheritance: String
)