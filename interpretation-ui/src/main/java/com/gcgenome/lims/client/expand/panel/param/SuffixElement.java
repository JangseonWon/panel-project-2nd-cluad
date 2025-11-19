package com.gcgenome.lims.client.expand.panel.param;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import net.sayaya.ui.DropDownElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.ListElement;
import net.sayaya.ui.TextAreaElement;
import org.jboss.elemento.HTMLContainerBuilder;

import static org.jboss.elemento.Elements.div;

public class SuffixElement extends HTMLElementBuilder<HTMLDivElement, SuffixElement> {
    public static SuffixElement build() {
        return new SuffixElement(div());
    }
    private static final String[] SUFFIXES = new String[] {
            "--없음--",
            "Clinical correlation 및 가족검사가 권장됩니다.",
            "Clinical correlation 및 필요하다고 판단될 경우 가족검사가 권장됩니다.",
            "상염색체 열성 질환에서 한 개의 Heterozygous VUS 변이만이 발견된 경우, 1) 이 변이가 질환과 관련이 없는 변이일 가능성, 2) 실제 질환과 관련이 있지만 보인자일 가능성, 3) 실제 이 유전자와 관련된 질환을 가지고 있지만 나머지 하나의 대립 유전자가 본 검사로 검출되지 않는 종류의 변이(large deletion or insertion, deep intronic variant, etc.)를 동반할 가능성 등이 있습니다. 상염색체 열성 유전 질환에서 한 개의 VUS가 발견된 경우, 가족검사를 시행하더라도 변이의 Pathogenicity를 판단하는데 도움이 되지 않으므로 가족검사가 권장되지 않습니다. Clinical correlation이 권장되며, 임상적으로 필요하다고 판단될 경우 deep intronic variant, large deletion/insertion 등을 검출하기 위한 추가 검사(Diagnostic Genome Sequencing 등)를 고려하시기 바랍니다.",
            "상염색체 열성 질환에서 한 개의 Heterozygous PV 만 발견된 경우, 1) 환자가 보인자일 가능성, 2) 환자가 이 유전자와 관련된 질환을 가지고 있지만 나머지 하나의 대립 유전자가 본 검사로 검출되지 않는 종류의 변이(large deletion or insertion, deep intronic variant, etc.)를 동반할 가능성 등이 있습니다. 상염색체 열성 유전 질환에서 한 개의 PV가 발견된 경우, 가족검사를 시행하더라도 변이의 Pathogenicity를 판단하는데 도움이 되지 않으므로 가족검사가 권장되지 않습니다. Clinical correlation이 권장되며, 임상적으로 필요하다고 판단될 경우 deep intronic variant, large deletion/insertion 등을 검출하기 위한 추가 검사(Diagnostic Genome Sequencing 등)를 고려하시기 바랍니다.",
            "Clinical correlation 및 필요하다고 판단될 경우 가족검사가 권장됩니다. 만약 동일한 임상 양상을 가진 가족 구성원에서 변이가 발견될 경우(familial segregation) 질환 관련성이 높아지므로 Likely Pathogenic Variant (LPV)로 재분류될 수 있습니다. 반면, 임상 양상이 합당하지 않거나, 질환의 임상 양상이 없는 가족 구성원에서 변이가 발견될 경우 질환 관련성이 낮아지므로 Likely Benign Variant (LBV)로 재분류될 수 있습니다.",
            "Clinical correlation 및 필요하다고 판단될 경우 가족검사가 권장됩니다. 만약 부모검사에서 변이가 발견되지 않거나(de novo), 동일한 임상 양상을 가진 가족 구성원에서 변이가 발견될 경우(familial segregation) 질환 관련성이 높아지므로 Likely Pathogenic Variant (LPV)로 재분류될 수 있습니다. 반면, 임상 양상이 합당하지 않거나, 부모 또는 가족검사에서 질환의 임상 양상이 없는 가족 구성원에서 변이가 발견될 경우 질환 관련성이 낮아지므로 Likely Benign Variant (LBV)로 재분류될 수 있습니다."
    };
    private final DropDownElement iptSuffixes;
    private final TextAreaElement<String> iptResult = TextAreaElement.textBox().outlined().text("Suffix").style("margin-top: 10px; min-height: 200px;");
    public SuffixElement(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.style("display: flex; flex-direction: column; color: var(--mdc-theme-text-primary-on-background); width: 800px;"));
        ListElement<ListElement.SingleLineItem> listSuffixex = ListElement.singleLineList();
        for(String suffix: SUFFIXES) {
            ListElement.SingleLineItem li = ListElement.singleLine().label(suffix);
            listSuffixex.add(li);
            li.element().style.setProperty("height", "auto");
            li.element().style.setProperty("padding-top", "5px");
            li.element().style.setProperty("padding-bottom", "5px");
            li.element().style.setProperty("max-width", "800px");

            HTMLElement t = (HTMLElement) li.element().getElementsByClassName("mdc-list-item__text").item(0);
            t.style.whiteSpace = "break-spaces";
        }
        iptSuffixes = DropDownElement.outlined(listSuffixex).css("input").text("Select Suffix").style("margin-top: 10px;");
        iptSuffixes.onValueChange(evt->{
            if(evt.value().equals(SUFFIXES[0])) return;
            String prev = iptResult.value();
            if(!prev.isEmpty()) iptResult.value(prev + "\n" + evt.value());
            else iptResult.value(evt.value());
            iptSuffixes.select(0);
        });
        Section section = Section.build("Suffix parameter");
        e.add(section).add(iptSuffixes).add(iptResult);
    }
    public String value() {
        return iptResult.value();
    }
    @Override
    public SuffixElement that() {
        return this;
    }
}
