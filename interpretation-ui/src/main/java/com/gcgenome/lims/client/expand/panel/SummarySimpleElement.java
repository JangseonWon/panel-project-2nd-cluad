package com.gcgenome.lims.client.expand.panel;

import com.gcgenome.lims.dto.InterpretationPanel;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.TextFieldElement;
import org.jboss.elemento.HTMLContainerBuilder;

import static org.jboss.elemento.Elements.div;

public class SummarySimpleElement extends HTMLElementBuilder<HTMLDivElement, SummarySimpleElement> implements ResultWriter<HTMLDivElement> {
    public static SummarySimpleElement build() {
        return new SummarySimpleElement(div());
    }
    private final TextFieldElement<String, ?> iptResultText = TextFieldElement.textBox().outlined().css("input").text("Result(Text)").style("width: 100%;");
    private SummarySimpleElement(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.style("margin-left: 15px; margin-right: 15px; margin-bottom: 10px;"));
        e.add(iptResultText);
    }
    @Override
    public void update(InterpretationPanel map) {
        if(map == null) map = new InterpretationPanel();
        if(map.resultText!=null)        iptResultText.value(map.resultText); else iptResultText.value("");
    }
    @Override
    public InterpretationPanel append(InterpretationPanel map) {
        map.resultText = iptResultText.value();
        return map;
    }
    @Override
    public SummarySimpleElement that() {
        return this;
    }
}

