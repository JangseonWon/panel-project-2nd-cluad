package com.gcgenome.lims

import com.gcgenome.lims.analysis.UrlManager
import com.gcgenome.lims.analysis.entity.Analysis
import com.gcgenome.lims.workflow.*
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import java.time.LocalDateTime
import java.util.*

@Configuration
@EnableAspectJAutoProxy
class EventConfig(private val urlManagers: List<UrlManager>) {
    private val source = "bi-analysis-subscriber"
    private val process = EventProcess.ANALYSIS
    @Publish
    fun publish(request: com.gcgenome.lims.analysis.entity.Request, analysis: Analysis): List<Event> {
        val createAt = LocalDateTime.now()
        val url = urlManagers.firstNotNullOfOrNull { it.toUrl(request, analysis) }
        val param = if(url!=null) mutableMapOf<String, Any>("lims_url" to url) else mutableMapOf()
        return listOf(Event(
            id = UUID.randomUUID(),
            timestamp = createAt,
            request = map(request),
            source = source,
            process = process,
            type = EventType.COMPLETE,
            param = param.asParam("analysis"),
            user = "system"
        ))
    }

    private fun map(request: com.gcgenome.lims.analysis.entity.Request): Request = Request(
        id = "${request.sample}:${request.service}",
        service = Service(request.service, request.service_name),
        samples = listOf(Sample(
            id = request.sample,
            type = request.sample_type?:"",
            patient = Patient(
                organization = Organization(request.organization?:"", request.organization_name?:""),
                name = request.patient_name,
                birth = null,
                sex = request.sex?.let(Patient.Companion.Sex::valueOf),
                mrn = request.mrn
            ),
            dateSampling = request.date_sampling?.toLocalDate(),
            age = request.age,
            remark = request.remark
        )), sample = Sample (
            id = request.sample,
            type = request.sample_type?:"",
            patient = Patient(
                organization = Organization(request.organization?:"", request.organization_name?:""),
                name = request.patient_name,
                birth = null,
                sex = request.sex?.let(Patient.Companion.Sex::valueOf),
                mrn = request.mrn
            ),
            dateSampling = request.date_sampling?.toLocalDate(),
            age = request.age,
            remark = request.remark
        ), requester = Organization("", "")
    )
}