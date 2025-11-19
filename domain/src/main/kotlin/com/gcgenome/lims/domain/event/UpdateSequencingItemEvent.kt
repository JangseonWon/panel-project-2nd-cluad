package com.gcgenome.lims.domain.event

import com.gcgenome.lims.domain.SequencingItem
import java.util.*

data class UpdateSequencingItemEvent (
    private val id: UUID,
    private val param: SequencingItem,
    private val type: PanelEvent.Type = PanelEvent.Type.UPDATE_SEQUENCING_ITEM
): PanelEvent<SequencingItem, UpdateSequencingItemEvent> {
    override fun id(): UUID = id
    override fun type(): PanelEvent.Type = type
    override fun param(): SequencingItem = param
}