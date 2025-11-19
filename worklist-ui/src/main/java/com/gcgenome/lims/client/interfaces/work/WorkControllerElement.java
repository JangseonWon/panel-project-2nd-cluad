package com.gcgenome.lims.client.interfaces.work;

import elemental2.dom.HTMLDivElement;
import org.jboss.elemento.HTMLContainerBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.jboss.elemento.Elements.div;

@Singleton
public class WorkControllerElement extends HTMLContainerBuilder<HTMLDivElement> {
    @Inject WorkControllerElement(GenerateSerialButton btn1, SaveButton btn2) {
        super(div().element());
        this.style("display: flex;flex-direction: row-reverse; margin-bottom: 0.5rem; gap: 0.5rem;").add(btn2).add(btn1);
    }
}

