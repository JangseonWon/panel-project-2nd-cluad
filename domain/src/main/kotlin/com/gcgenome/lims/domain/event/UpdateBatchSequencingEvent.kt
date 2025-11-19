package com.gcgenome.lims.domain.event

import com.gcgenome.lims.domain.BatchSequencing
import java.util.*

data class UpdateBatchSequencingEvent (
    private val id: UUID,
    private val param: BatchSequencing,
    private val type: PanelEvent.Type = PanelEvent.Type.UPDATE_BATCH_SEQUENCING
): PanelEvent<BatchSequencing, UpdateBatchSequencingEvent> {
    override fun id(): UUID = id
    override fun type(): PanelEvent.Type = type
    override fun param(): BatchSequencing = param
}