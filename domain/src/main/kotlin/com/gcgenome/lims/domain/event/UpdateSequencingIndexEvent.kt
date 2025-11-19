package com.gcgenome.lims.domain.event

import com.gcgenome.lims.domain.Index
import java.util.*

data class UpdateSequencingIndexEvent (
    private val id: UUID,
    private val param: Index,
    private val type: PanelEvent.Type = PanelEvent.Type.UPDATE_SEQUENCING_INDEX
): PanelEvent<Index, UpdateSequencingIndexEvent> {
    override fun id(): UUID = id
    override fun type(): PanelEvent.Type = type
    override fun param(): Index = param
}