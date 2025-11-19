package com.gcgenome.lims.client.expand.panel;

import com.gcgenome.lims.dto.InterpretationPanel;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.DropDownElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.ListElement;
import net.sayaya.ui.TextFieldElement;
import org.jboss.elemento.HTMLContainerBuilder;

import static org.jboss.elemento.Elements.div;

public class MlpaElement extends HTMLElementBuilder<HTMLDivElement, MlpaElement> implements ResultWriter<HTMLDivElement> {
    public static MlpaElement build(String targetGene) {
        return new MlpaElement(div(), targetGene);
    }

    private final DropDownElement iptResult = DropDownElement.outlined(ListElement.singleLineList()
                    .add(ListElement.singleLine().label("Detected"))
                    .add(ListElement.singleLine().label("Not Detected")))
            .css("input").text("검사결과").style("width: 200px;");
    private final TextFieldElement<String, ?> iptExons = TextFieldElement.textBox().outlined().css("input").text("Exons").style("width: calc(100% - 600px);");
    private final DropDownElement iptZygosity = DropDownElement.outlined(ListElement.singleLineList()
                    .add(ListElement.singleLine().label("Heterozygous"))
                    .add(ListElement.singleLine().label("Hemizygous"))
                    .add(ListElement.singleLine().label("Homozygous")))
            .css("input").text("Zygosity").style("width: 200px;");
    private final DropDownElement iptDelDup = DropDownElement.outlined(ListElement.singleLineList()
                    .add(ListElement.singleLine().label("Deletion"))
                    .add(ListElement.singleLine().label("Duplication")))
            .css("input").text("Del/Dup").style("width: 200px;");
    private final String targetGene;
    public MlpaElement(HTMLContainerBuilder<HTMLDivElement> e, String targetGene) {
        super(e.style("margin-left: 15px; margin-right: 15px; margin-bottom: 10px;"));
        e.add(iptResult).add(iptExons).add(iptZygosity).add(iptDelDup);
        this.targetGene = targetGene;
        iptResult.onValueChange(evt->{
            boolean isDetected = "Detected".equals(evt.value());
            iptExons.enabled(isDetected);
            iptZygosity.enabled(isDetected);
            iptDelDup.enabled(isDetected);
            if (isDetected) {
                if (iptExons.value() == null || !iptExons.value().startsWith("exon")) {
                    iptExons.value("exon ".concat(iptExons.value().trim()));
                }
            } else {
                iptExons.value(null);
                iptZygosity.select(-1);
                iptDelDup.select(-1);
            }
        });
    }

    @Override
    public InterpretationPanel append(InterpretationPanel map) {
        map.mlpaResult = new InterpretationPanel.Mlpa();
        map.mlpaResult.gene = targetGene;
        map.mlpaResult.result = iptResult.value();
        map.mlpaResult.exons = iptExons.value();
        map.mlpaResult.zygosity = iptZygosity.value();
        map.mlpaResult.delDup = iptDelDup.value();
        return map;
    }

    @Override
    public void update(InterpretationPanel map) {
        if(map == null) map = new InterpretationPanel();
        if(map.mlpaResult == null) map.mlpaResult = new InterpretationPanel.Mlpa();
        if(map.mlpaResult.result!=null) iptResult.select(map.mlpaResult.result); else iptResult.select(0);
        if(map.mlpaResult.exons != null) iptExons.value(map.mlpaResult.exons);
        if(map.mlpaResult.zygosity != null) iptZygosity.select(map.mlpaResult.zygosity);
        if(map.mlpaResult.delDup != null) iptDelDup.select(map.mlpaResult.delDup);
    }
}
