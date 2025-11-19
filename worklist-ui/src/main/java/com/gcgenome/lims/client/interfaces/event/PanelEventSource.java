package com.gcgenome.lims.client.interfaces.event;

import elemental2.dom.DomGlobal;
import elemental2.dom.EventSource;
import lombok.experimental.Delegate;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PanelEventSource {
    @Delegate private final EventSource source = new EventSource("messages");
    @Inject PanelEventSource() {
        source.onerror = err -> {
            DomGlobal.console.log("err");
            DomGlobal.console.log(err);
        };
    }
}
