package com.gcgenome.lims.client.expand.elem;

import com.gcgenome.lims.api.InterpretationApi;
import com.gcgenome.lims.api.ProgressApi;
import com.gcgenome.lims.client.InterpretationI18nUtil;
import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import jsinterop.base.Js;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.ButtonElementText;
import net.sayaya.ui.HTMLElementBuilder;

public class ButtonSaveElement extends HTMLElementBuilder<HTMLButtonElement, ButtonElementText> {
    public static ButtonElement build() {
        return new ButtonSaveElement(ButtonElement.outline().css("button").before(IconElement.icon("save")).text("Save")).that();
    }
    private final ButtonElementText _this;
    private ButtonSaveElement(ButtonElementText e) {
        super(e);
        _this = e;
        e.onClick(evt->save());
        Subjects.isFinal.subscribe(isFinal->_this.enabled(!isFinal).text(isFinal?"검사 완료":"SAVE"));
        Subjects.onAsync.subscribe(d->{ if (!Subjects.isFinal.getValue()) _this.enabled(!d); });
    }
    private void save() {
        if(!InterpretationI18nUtil.validateEnglishReport(Js.cast(Subjects.interpretation.getValue()))) return;
        if(!DomGlobal.confirm("저장합니다.")) return;
        ProgressApi.open(false);
        InterpretationApi.save(Subjects.sample.getValue(), Subjects.code.getValue(), Subjects.interpretation.getValue())
                .then(reports->{
                    Subjects.interpretation.next(reports);
                    return null;
                }).finally_(()-> DomGlobal.alert("저장되었습니다."))
                .finally_(ProgressApi::close);
    }
    @Override
    public ButtonElementText that() {
        return _this;
    }
}
