package com.gcgenome.lims.client.expand.elem;

import com.gcgenome.lims.api.ReportApi;
import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.HTMLButtonElement;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.ButtonElementText;
import net.sayaya.ui.HTMLElementBuilder;

public class ButtonPreviewElement extends HTMLElementBuilder<HTMLButtonElement, ButtonElementText> {
    public static ButtonElement build() {
        return new ButtonPreviewElement(ButtonElement.outline().css("button").before(IconElement.icon("preview")).text("Preview")).that();
    }
    private final ButtonElementText _this;
    private ButtonPreviewElement(ButtonElementText e) {
        super(e);
        _this = e;
        _this.onClick(evt->preview());
        Subjects.isFinal.subscribe(isFinal->_this.enabled(!isFinal));
        Subjects.onAsync.subscribe(dialogue->{
            if (!Subjects.isFinal.getValue()) _this.enabled(!dialogue);
        });
    }
    private void preview() {
        ReportApi.preview(Subjects.sample.getValue(), Subjects.code.getValue(), Subjects.interpretation.getValue()).then(blob->{
            Subjects.pdf.next(blob);
            return null;
        });

    }
    @Override
    public ButtonElementText that() {
        return _this;
    }
}
