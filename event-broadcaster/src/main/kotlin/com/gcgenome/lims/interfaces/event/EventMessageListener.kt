package com.gcgenome.lims.interfaces.event

import com.gcgenome.lims.usecase.Broadcaster
import org.springframework.stereotype.Component
import java.util.function.Consumer

@Component("panel")
class EventMessageListener(private val emitter: Broadcaster): Consumer<String> {
    override fun accept(event: String) = emitter.broadcast(event)
}