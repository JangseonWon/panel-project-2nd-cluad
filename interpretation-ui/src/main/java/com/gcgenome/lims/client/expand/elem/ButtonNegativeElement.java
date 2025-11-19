package com.gcgenome.lims.client.expand.elem;

import com.gcgenome.lims.api.InterpretationApi;
import com.gcgenome.lims.api.ProgressApi;
import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.ButtonElementText;
import net.sayaya.ui.HTMLElementBuilder;

import static com.gcgenome.lims.client.Subjects.sample;

public class ButtonNegativeElement extends HTMLElementBuilder<HTMLButtonElement, ButtonElementText> {
    public static ButtonElement build() {
        return new ButtonNegativeElement(ButtonElement.outline().css("button").before(IconElement.icon("search_off")).text("Negative")).that();
    }
    private final ButtonElementText _this;
    private ButtonNegativeElement(ButtonElementText e) {
        super(e);
        _this = e;
        e.onClick(evt->negative());

        Subjects.onAsync.subscribe(d->{ if (!Subjects.isFinal.getValue()) _this.enabled(!d); });
    }

    private void negative() {
        if(Subjects.sample.getValue()< 0 || Subjects.code.getValue().trim().isEmpty()) return;

        ProgressApi.open(false);
        if(!DomGlobal.confirm("음성 결과를 입력합니다.")) return;
        InterpretationApi.negative(sample.getValue(), Subjects.code.getValue())
                .then(reports->{
                    Subjects.interpretation.next(reports);
                    return null;
                })
                .finally_(()->Subjects.onAsync.next(false))
                .finally_(()-> DomGlobal.alert("입력되었습니다. 저장하세요."))
                .finally_(ProgressApi::close);
    }
    @Override
    public ButtonElementText that() {
        return _this;
    }
}
