package com.gcgenome.lims.client;

import com.gcgenome.lims.api.InterpretationApi;
import com.gcgenome.lims.api.ReportApi;
import com.gcgenome.lims.api.VersionCheckApi;
import com.gcgenome.lims.client.expand.DescriptionDialog;
import com.gcgenome.lims.dto.InterpretationPanel;
import com.gcgenome.lims.test.Interpretable;
import elemental2.dom.Blob;
import elemental2.dom.DomGlobal;
import elemental2.dom.Response;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.rx.subject.BehaviorSubject;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;

public class Subjects {
    public static final BehaviorSubject<Long> sample = behavior(-1L);
    public static final BehaviorSubject<String> code = behavior("");
    public static final BehaviorSubject<Interpretable.Category> category = behavior(null);
    public static final BehaviorSubject<JsPropertyMap<?>> service = behavior(null);
    public static final BehaviorSubject<Boolean> isFinal = behavior(false);
    public static final BehaviorSubject<Object> interpretation = behavior(new InterpretationPanel());
    public static final BehaviorSubject<Blob> pdf = behavior(new Blob());
    public static final BehaviorSubject<Boolean> onAsync = behavior(false);
    public static final BehaviorSubject<Boolean> doSaveAndPublish = behavior(false);
    static {
        service.subscribe(svc->{
            if(svc==null) return;
            if(svc.has("code")) code.next(svc.get("code").toString());
            if(svc.has("category")) category.next(Interpretable.Category.valueOf(svc.get("interpretation_category").toString()));
        });
        sample.subscribe(s->updateFinal());
        code.subscribe(c->updateFinal());
        doSaveAndPublish.subscribe(c->{if(c) saveAndPublish();});
    }
    private static void updateFinal() {
        if(sample.getValue()< 0 || code.getValue().trim().isEmpty()) return;
        ReportApi.state(sample.getValue(), code.getValue()).then(state->{
            Subjects.isFinal.next("F".equalsIgnoreCase(state));
            return null;
        });
    }
    private static Promise<Response> publish(String desc) {
        if(sample.getValue()< 0 || code.getValue().trim().isEmpty()) return null;

        var sample = Subjects.sample.getValue();
        var code = Subjects.code.getValue();
        return ReportApi.print(sample, code, desc).then(report -> {
            var createAt = Double.valueOf(Js.asDouble(report.get("create_at"))).longValue();
            return ReportApi.publish(sample, code, createAt);
        });
    }
    public static void saveAndPublish() {
        if(sample.getValue()< 0 || code.getValue().trim().isEmpty()) return;

        var sample = Subjects.sample.getValue();
        var code = Subjects.code.getValue();
        var requestChangeLogIfUpdate = VersionCheckApi.isNew(sample, code).then(isNew -> isNew ? Promise.resolve("") : DescriptionDialog.dialog());
        requestChangeLogIfUpdate.then(description->
                InterpretationApi.save(sample, code, Subjects.interpretation.getValue())
                .then(reports->{
                    Subjects.interpretation.next(reports);
                    return null;
                }).then(e->publish(description)))
                .finally_(()->DomGlobal.alert("검사가 완료되었습니다."))
                .finally_(()->doSaveAndPublish.next(false));
    }
}
