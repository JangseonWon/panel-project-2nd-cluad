package com.gcgenome.lims.domain.event

import com.gcgenome.lims.domain.Serial
import java.util.*

data class UpdateSerialEvent (
    private val id: UUID,
    private val param: Serial,
    private val type: PanelEvent.Type = PanelEvent.Type.UPDATE_SERIAL
): PanelEvent<Serial, UpdateSerialEvent> {
    override fun id(): UUID = id
    override fun type(): PanelEvent.Type = type
    override fun param(): Serial = param
}