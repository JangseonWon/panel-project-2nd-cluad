package com.gcgenome.lims.client.interfaces.work;

import com.gcgenome.lims.client.domain.Work;
import com.gcgenome.lims.client.interfaces.work.op.Operation;
import com.gcgenome.lims.client.interfaces.work.op.OperationList;
import com.gcgenome.lims.client.usecase.work.WorkList;
import dev.sayaya.rx.subject.BehaviorSubject;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.promise.Promise;
import lombok.experimental.Delegate;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.ButtonElementText;
import net.sayaya.ui.HTMLElementBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Set;

@Singleton
public class SaveButton extends HTMLElementBuilder<HTMLButtonElement, ButtonElementText> {
    @Delegate private final ButtonElementText btn;
    @Inject SaveButton(OperationList patches, WorkList works) {
        this(ButtonElement.outline(), patches, works);
    }
    private SaveButton(ButtonElementText element, OperationList patches, WorkList works) {
        super(element);
        btn = element;
        add("저장");
        btn.onClick(evt-> save(patches.getValue()));
        patches.subscribe(s->btn.enabled(s!=null && !s.isEmpty()));
        works.subscribe(ws-> enabled(Arrays.stream(ws).allMatch(w -> w.serial() != null)));
    }
    private void save(Set<Operation> patches) {
        Promise.all(patches.stream().map(Operation::update).toArray(Promise[]::new)).then(evt->{
            DomGlobal.alert("저장하였습니다.");
            return null;
        }).catch_(e->{
            DomGlobal.alert("변경 요청이 실패하였습니다:" + e.toString());
            return null;
        });
    }
}
