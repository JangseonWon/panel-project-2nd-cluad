package com.gcgenome.lims.client.interfaces.worklist.serial;

import com.gcgenome.lims.client.usecase.worklist.serial.SerialProvider;
import dev.sayaya.rx.subject.BehaviorSubject;
import elemental2.dom.HTMLLabelElement;
import net.sayaya.ui.HTMLElementBuilder;
import org.jboss.elemento.HTMLContainerBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.jboss.elemento.Elements.label;

@Singleton
public class SerialElement extends HTMLElementBuilder<HTMLLabelElement, SerialElement> {
    @Inject SerialElement(SerialProvider serial) { this(label(), serial); }
    private SerialElement(HTMLContainerBuilder<HTMLLabelElement> e, SerialProvider serial) {
        super(e);
        serial.subscribe(s->e.element().innerHTML = s);
    }
}

