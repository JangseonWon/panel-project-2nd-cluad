package com.gcgenome.lims.client.interfaces.work;

import elemental2.dom.HTMLDivElement;
import org.jboss.elemento.HTMLContainerBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.jboss.elemento.Elements.div;

@Singleton
public class WorkScene extends HTMLContainerBuilder<HTMLDivElement> {
    private final WorkControllerElement controller;
    private final WorkGridElement grid;
    @Inject WorkScene(WorkControllerElement controller, WorkGridElement grid) {
        super(div().element());
        this.controller = controller;
        this.grid = grid;
        layout();
    }
    private void layout() {
        this.style("position: relative; margin: 1rem;").add(controller).add(grid);
    }
}
