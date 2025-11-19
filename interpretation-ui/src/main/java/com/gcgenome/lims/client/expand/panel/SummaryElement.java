package com.gcgenome.lims.client.expand.panel;

import com.gcgenome.lims.dto.InterpretationPanel;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.DropDownElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.ListElement;
import net.sayaya.ui.TextFieldElement;
import org.jboss.elemento.HTMLContainerBuilder;

import static org.jboss.elemento.Elements.div;

public class SummaryElement extends HTMLElementBuilder<HTMLDivElement, SummaryElement> implements ResultWriter<HTMLDivElement> {
    public static SummaryElement build() {
        return new SummaryElement(div());
    }
    private final TextFieldElement<String, ?> iptReferral = TextFieldElement.textBox().outlined().css("input").text("Reason for Referral").style("width: 300px;");
    private final DropDownElement iptResult = DropDownElement.outlined(ListElement.singleLineList()
                    .add(ListElement.singleLine().label("POSITIVE"))
                    .add(ListElement.singleLine().label("INCONCLUSIVE"))
                    .add(ListElement.singleLine().label("NEGATIVE")))
            .css("input").text("Result").style("width: 200px;");
    private final TextFieldElement<String, ?> iptResultText = TextFieldElement.textBox().outlined().css("input").text("Result(Text)").style("width: calc(100% - 500px);");
    private SummaryElement(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.style("margin-left: 15px; margin-right: 15px; margin-bottom: 10px;"));
        e.add(iptResult).add(iptResultText).add(iptReferral);
    }
    @Override
    public void update(InterpretationPanel map) {
        if(map == null) map = new InterpretationPanel();
        if(map.result!=null)            iptResult.select(map.result); else iptResult.select(0);
        if(map.resultText!=null)        iptResultText.value(map.resultText); else iptResultText.value("");
        if(map.reasonForReferral!=null) iptReferral.value(map.reasonForReferral); else iptReferral.value("");
    }
    @Override
    public InterpretationPanel append(InterpretationPanel map) {
        map.result = iptResult.value();
        map.resultText = iptResultText.value();
        map.reasonForReferral = iptReferral.value();
        return map;
    }
    @Override
    public SummaryElement that() {
        return this;
    }
}

