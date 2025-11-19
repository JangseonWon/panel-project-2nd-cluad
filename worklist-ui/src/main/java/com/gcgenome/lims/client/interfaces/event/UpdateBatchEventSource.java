package com.gcgenome.lims.client.interfaces.event;

import com.gcgenome.lims.client.domain.Batch;
import com.gcgenome.lims.client.domain.PanelEvent;
import com.gcgenome.lims.client.usecase.worklist.UpdateWorklistEvent;
import dev.sayaya.rx.subject.Subject;
import elemental2.dom.MessageEvent;
import jsinterop.base.Js;
import lombok.experimental.Delegate;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.function.Consumer;

import static dev.sayaya.rx.subject.Subject.subject;
import static elemental2.core.Global.JSON;

@Singleton
public class UpdateBatchEventSource implements UpdateWorklistEvent {
    @Delegate private final Subject<PanelEvent<Batch>> events;
    @Inject UpdateBatchEventSource(PanelEventSource source) {
        var events = subject(PanelEvent.class);
        source.addEventListener("UPDATE_BATCH", evt->{
            MessageEvent<String> cast = Js.cast(evt);
            Batch param = (Batch) JSON.parse(cast.data);
            var event = PanelEvent.builder().type("UPDATE_BATCH").param(param).build();
            events.next(event);
        });
        this.events = (Subject) events;
    }
    @Override
    public void subscribe(Consumer<PanelEvent<Batch>> consumer) {
        events.subscribe(consumer);
    }
}
