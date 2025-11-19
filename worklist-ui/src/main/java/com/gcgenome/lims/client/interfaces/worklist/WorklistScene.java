package com.gcgenome.lims.client.interfaces.worklist;

import elemental2.dom.HTMLDivElement;
import org.jboss.elemento.HTMLContainerBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.jboss.elemento.Elements.div;

@Singleton
public class WorklistScene extends HTMLContainerBuilder<HTMLDivElement> {
    private final WorklistGridElement grid;
    @Inject WorklistScene(WorklistGridElement grid) {
        super(div().element());
        this.grid = grid;
        layout();
    }
    private void layout() {
        this.style("position: relative; margin: 1rem;")/*.add(controller)*/.add(grid)/*.add(page)*/;
    }
}
