package com.gcgenome.lims.client.expand.panel;

import com.gcgenome.lims.dto.InterpretationPanel;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.TextAreaElement;
import net.sayaya.ui.TextFieldElement;
import net.sayaya.ui.TextFieldElement.TextFieldOutlined;
import org.jboss.elemento.HTMLContainerBuilder;

import static org.jboss.elemento.Elements.div;

public class AbbreviationElement extends HTMLElementBuilder<HTMLDivElement, AbbreviationElement> implements ResultWriter<HTMLDivElement> {
    public static AbbreviationElement build() {
        return new AbbreviationElement(div());
    }
    private final TextFieldOutlined<String> iptReferenceSeq = TextFieldElement.textBox().outlined().css("input").text("Reference Sequence").style("width: 300px");
    private final TextFieldOutlined<String> iptDisease = TextFieldElement.textBox().outlined().css("input").text("Disease Abbr").style("width: calc(100% - 300px);");
    private final TextAreaElement<String> iptAbbr = TextAreaElement.textBox().outlined().css("input").text("Abbreviation").style("width: 100%; min-height: 50px;");
    private AbbreviationElement(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e);
        e.add(div().style("margin-left: 15px; margin-right: 15px;").add(iptReferenceSeq).add(iptDisease))
         .add(div().style("margin-left: 15px; margin-top: 10px; margin-right: 15px;").add(iptAbbr));
    }
    @Override
    public void update(InterpretationPanel map) {
        if(map == null) map = new InterpretationPanel();
        if(map.variants==null || map.variants.length <= 0) element().style.display = "none";
        else element().style.display = null;
        iptReferenceSeq.value(map.abbreviationReference != null ? map.abbreviationReference : "");
        iptDisease.value(map.abbreviationDisease != null ? map.abbreviationDisease : "");
        iptAbbr.value(map.abbreviation != null ? map.abbreviation : "");
    }
    @Override
    public InterpretationPanel append(InterpretationPanel map) {
        map.abbreviationReference = iptReferenceSeq.value();
        map.abbreviationDisease = iptDisease.value();
        map.abbreviation = iptAbbr.value();
        return map;
    }
    @Override
    public AbbreviationElement that() {
        return this;
    }
}
