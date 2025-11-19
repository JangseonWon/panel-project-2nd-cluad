package com.gcgenome.lims.client.expand.panel;

import com.gcgenome.lims.dto.InterpretationPanel;
import elemental2.dom.HTMLElement;
import org.jboss.elemento.IsElement;

public interface ResultWriter<E extends HTMLElement> extends IsElement<E> {
    InterpretationPanel append(InterpretationPanel map);
    void update(InterpretationPanel map);
}
