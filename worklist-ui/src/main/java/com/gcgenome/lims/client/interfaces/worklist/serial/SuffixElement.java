package com.gcgenome.lims.client.interfaces.worklist.serial;

import com.gcgenome.lims.client.usecase.worklist.serial.SuffixProvider;
import elemental2.dom.HTMLLabelElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.TextFieldElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import static net.sayaya.ui.TextFieldElement.numberBox;

@Singleton
public class SuffixElement extends HTMLElementBuilder<HTMLLabelElement, SuffixElement> {
    @Inject SuffixElement(SuffixProvider suffix) { this(numberBox().outlined().required(true).text("Suffix"), suffix); }
    private SuffixElement(TextFieldElement.TextFieldOutlined<Double> e, SuffixProvider suffix) {
        super(e);
        // 엘리먼트에서 값이 변경되면 서브젝트를 업데이트한다
        e.onValueChange(evt->suffix.next(evt.value()!=null?evt.value().intValue():null));
        // 서브젝트에서 값이 변경되면 엘리먼트 값을 업데이트한다
        suffix.subscribe(s->e.value(s!=null? s.doubleValue() : 0));
    }
}
