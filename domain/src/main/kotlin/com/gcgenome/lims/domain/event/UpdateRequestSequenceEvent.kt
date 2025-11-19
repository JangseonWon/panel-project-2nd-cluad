package com.gcgenome.lims.domain.event

import com.gcgenome.lims.domain.RequestSequence
import java.util.*

data class UpdateRequestSequenceEvent (
    private val id: UUID,
    private val param: RequestSequence,
    private val type: PanelEvent.Type = PanelEvent.Type.UPDATE_REQUEST_SEQUENCE
): PanelEvent<RequestSequence, UpdateRequestSequenceEvent> {
    override fun id(): UUID = id
    override fun type(): PanelEvent.Type = type
    override fun param(): RequestSequence = param
}