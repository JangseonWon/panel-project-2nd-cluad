package com.gcgenome.lims.client.expand.panel;

import com.gcgenome.lims.dto.InterpretationPanel;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.TextAreaElement;
import org.jboss.elemento.HTMLContainerBuilder;

import static org.jboss.elemento.Elements.div;

public class InterpretationElement extends HTMLElementBuilder<HTMLDivElement, InterpretationElement> implements ResultWriter<HTMLDivElement> {
    public static InterpretationElement build() {
        return new InterpretationElement(div());
    }
    private final TextAreaElement<String> iptInterpretation = TextAreaElement.textBox().outlined().style("width: -webkit-fill-available; height: 250px; margin: 16px;").text("Interpretation");

    private InterpretationElement(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.style("transition: all 300ms ease 0s;"));
        e.add(iptInterpretation);
    }
    @Override
    public void update(InterpretationPanel map) {
        if(map == null) map = new InterpretationPanel();
        iptInterpretation.value(map.interpretation != null ? map.interpretation : "");
    }
    @Override
    public InterpretationPanel append(InterpretationPanel map) {
        map.interpretation = iptInterpretation.value();
        return map;
    }
    @Override
    public InterpretationElement that() {
        return this;
    }
}
