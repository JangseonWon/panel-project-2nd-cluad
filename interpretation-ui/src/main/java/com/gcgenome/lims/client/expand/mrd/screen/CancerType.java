package com.gcgenome.lims.client.expand.mrd.screen;

import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import elemental2.dom.HTMLLabelElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.TextFieldElement;

public class CancerType extends HTMLElementBuilder<HTMLLabelElement, CancerType> implements InterpretationSubject<HTMLLabelElement> {
    public static CancerType build() {
        return new CancerType(TextFieldElement.textBox().outlined());
    }
    private final TextFieldElement.TextFieldOutlined<String> ipt;
    private CancerType(TextFieldElement.TextFieldOutlined<String> ipt) {
        super(ipt.text("Cancer Type").style("width: calc(100% - 200px);").css("input"));
        this.ipt = ipt;
        Subjects.cancerType.subscribe(this::update);
        ipt.onValueChange(evt->Subjects.cancerType.next(evt.value()));
    }
    private void update(String value) {
        if(value==null) ipt.value("");
        else ipt.value(value);
    }
    @Override
    public CancerType that() {
        return this;
    }
}
