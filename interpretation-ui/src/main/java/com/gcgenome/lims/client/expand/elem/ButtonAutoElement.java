package com.gcgenome.lims.client.expand.elem;

import com.gcgenome.lims.api.InterpretationApi;
import com.gcgenome.lims.api.ProgressApi;
import com.gcgenome.lims.client.InterpretationI18nUtil;
import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.ButtonElementText;
import net.sayaya.ui.HTMLElementBuilder;

public class ButtonAutoElement extends HTMLElementBuilder<HTMLButtonElement, ButtonElementText> {
    public static ButtonElement build() {
        return new ButtonAutoElement(ButtonElement.outline().css("button").before(IconElement.icon("smart_button")).text("Interpretation")).that();
    }
    private final ButtonElementText _this;
    private ButtonAutoElement(ButtonElementText e) {
        super(e);
        _this = e;
        e.onClick(evt->auto());
        Subjects.onAsync.subscribe(d->{ if (!Subjects.isFinal.getValue()) _this.enabled(!d); });
    }
    private static void auto() {
        if(Subjects.sample.getValue()< 0 || Subjects.code.getValue().trim().isEmpty()) return;
        if(!InterpretationI18nUtil.confirmAutoInterpretation()) return;
        var sample = Subjects.sample.getValue();
        var code = Subjects.code.getValue();
        ProgressApi.open(false);
        InterpretationApi.interpretation(sample, code, Subjects.interpretation.getValue())
                .then(reports->{
                    Subjects.interpretation.next(reports);
                    return null;
                }).finally_(()->Subjects.onAsync.next(false))
                .finally_(()-> DomGlobal.alert(" 생성되었습니다. 저장하세요."))
                .finally_(ProgressApi::close);
    }
    @Override
    public ButtonElementText that() {
        return _this;
    }

}
