package com.gcgenome.lims.client.expand.panel;

import com.gcgenome.lims.dto.InterpretationPanel;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.TextFieldElement;
import org.jboss.elemento.HTMLContainerBuilder;

import static org.jboss.elemento.Elements.div;

public class DepthCoverageElement extends HTMLElementBuilder<HTMLDivElement, DepthCoverageElement> implements ResultWriter<HTMLDivElement> {
    public static DepthCoverageElement build() {
        return new DepthCoverageElement(div());
    }
    private final TextFieldElement<String, ?> iptDepth = TextFieldElement.textBox().outlined().css("input").text("Mean depth of coverage").style("width: 300px;");
    private final TextFieldElement<String, ?> iptCoverage = TextFieldElement.textBox().outlined().css("input").text("% of Target bases > 10X").style("width: 300px;");
    private DepthCoverageElement(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.style("margin-left: 15px; margin-top: 10px; margin-right: 15px;"));
        e.add(iptDepth).add(iptCoverage);
    }
    @Override
    public void update(InterpretationPanel map) {
        if(map == null) map = new InterpretationPanel();
        iptCoverage.value(map.coverage != null ? map.coverage : "");
        iptDepth.value(map.meanDepth != null ? map.meanDepth : "");
    }
    @Override
    public InterpretationPanel append(InterpretationPanel map) {
        map.coverage = iptCoverage.value();
        map.meanDepth = iptDepth.value();
        return map;
    }
    @Override
    public DepthCoverageElement that() {
        return this;
    }
}
