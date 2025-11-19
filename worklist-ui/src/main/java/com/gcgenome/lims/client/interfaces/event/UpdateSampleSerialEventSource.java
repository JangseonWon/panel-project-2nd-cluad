package com.gcgenome.lims.client.interfaces.event;

import com.gcgenome.lims.client.domain.PanelEvent;
import com.gcgenome.lims.client.domain.Serial;
import com.gcgenome.lims.client.usecase.work.UpdateSerialEvent;
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
public class UpdateSampleSerialEventSource implements UpdateSerialEvent {
    @Delegate private final Subject<PanelEvent<Serial>> events;
    @Inject UpdateSampleSerialEventSource(PanelEventSource source) {
        var events = subject(PanelEvent.class);
        source.addEventListener("UPDATE_SERIAL", evt->{
            MessageEvent<String> cast = Js.cast(evt);
            Serial param = (Serial) JSON.parse(cast.data);
            var event = PanelEvent.builder().type("UPDATE_SERIAL").param(param).build();
            events.next(event);
        });
        this.events = (Subject) events;
    }
    @Override
    public void subscribe(Consumer<PanelEvent<Serial>> consumer) {
        events.subscribe(consumer);
    }
}
