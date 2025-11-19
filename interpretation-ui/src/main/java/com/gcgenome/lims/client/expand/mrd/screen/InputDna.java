package com.gcgenome.lims.client.expand.mrd.screen;

import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import elemental2.dom.HTMLLabelElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.TextFieldElement;

public class InputDna extends HTMLElementBuilder<HTMLLabelElement, InputDna> implements InterpretationSubject<HTMLLabelElement> {
    public static InputDna build() {
        return new InputDna(TextFieldElement.numberBox().outlined().required(true));
    }
    private InputDna(TextFieldElement.TextFieldOutlined<Double> ipt) {
        super(ipt.text("Input DNA").style("width: 200px;").css("input"));
        Subjects.inputDna.subscribe(ipt::value);
        ipt.onValueChange(evt->Subjects.inputDna.next(evt.value()));
    }
    @Override
    public InputDna that() {
        return this;
    }
}
