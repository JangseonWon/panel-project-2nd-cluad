package com.gcgenome.lims.client.interfaces.work;

import com.gcgenome.lims.client.interfaces.api.WorkApi;
import com.gcgenome.lims.client.usecase.work.WorkList;
import com.gcgenome.lims.client.usecase.work.WorklistIdProvider;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import lombok.experimental.Delegate;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.ButtonElementText;
import net.sayaya.ui.HTMLElementBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class GenerateSerialButton extends HTMLElementBuilder<HTMLButtonElement, ButtonElementText> {
    @Delegate
    private final ButtonElementText btn;
    @Inject GenerateSerialButton(WorkApi api, WorklistIdProvider worklist, WorkList works) {
        this(ButtonElement.outline(), api, worklist, works);
    }
    private GenerateSerialButton(ButtonElementText element, WorkApi api,WorklistIdProvider worklist, WorkList works) {
        super(element);
        btn = element;
        add("검체 연번 생성");
        btn.onClick(evt-> generate(api, worklist.getValue()));
        works.subscribe(ws-> enabled(Arrays.stream(ws).noneMatch(w -> w.serial() != null)));
    }
    private void generate(WorkApi api,String worklist) {
        api.create(worklist).then(evt->{
            DomGlobal.alert("생성하였습니다.");
            return null;
        }).catch_(e->{
            DomGlobal.alert("생성 요청이 실패하였습니다:" + e.toString());
            return null;
        });
    }
}
