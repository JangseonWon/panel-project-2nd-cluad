package com.gcgenome.lims.client.expand.tso;

import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.TextFieldElement;
import org.jboss.elemento.HTMLContainerBuilder;

import static org.jboss.elemento.Elements.div;

public class CancerType extends HTMLElementBuilder<HTMLDivElement, CancerType> implements InterpretationSubject<HTMLDivElement> {
    public static CancerType build() {
        return new CancerType(div());
    }
    private CancerType(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.style("margin: 16px;"));
        TextFieldElement.TextFieldOutlined<String> iptCancerCategory = TextFieldElement.textBox().outlined().text("Cancer Category").css("input").style("width: 50%;");
        TextFieldElement.TextFieldOutlined<String> iptCancerType = TextFieldElement.textBox().outlined().text("Cancer Type").css("input").style("width: 50%;");
        e.add(iptCancerCategory).add(iptCancerType);
        Subjects.cancerCategory.subscribe(iptCancerCategory::value);
        Subjects.cancerType.subscribe(iptCancerType::value);
        iptCancerCategory.onValueChange(evt->Subjects.cancerCategory.next(evt.value()));
        iptCancerType.onValueChange(evt->Subjects.cancerType.next(evt.value()));
    }
    @Override
    public CancerType that() {
        return this;
    }
}
