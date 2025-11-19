package com.gcgenome.lims.client.usecase.worklist.serial;

import com.gcgenome.lims.client.domain.Batch;
import dev.sayaya.rx.Operator;
import dev.sayaya.rx.subject.BehaviorSubject;
import elemental2.core.JsString;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import lombok.experimental.Delegate;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

import static dev.sayaya.rx.subject.BehaviorSubject.behavior;

@Singleton
public class BatchProvider {
    @Delegate private final BehaviorSubject<Batch> subject = behavior(null);
    @Inject BatchProvider(TargetWorklistIdProvider worklist, InfixProvider infix, SuffixProvider suffix, DigitProvider digit) {
        Operator.combineLatest(
                worklist.asObservable(),
                infix.asObservable(),
                suffix.asObservable(),
                digit.asObservable()
        ).map(JsArrayLike::asList).map(values ->{
            if(values.stream().anyMatch(Objects::isNull)) return null;
            String w = Js.cast(values.get(0));
            JsString p = Js.cast(values.get(1));
            Integer s = Js.cast(values.get(2));
            Integer d = Js.cast(values.get(3));
            Batch batch = subject.getValue();
            batch.id = w;
            batch.infix = p.toString();
            batch.idx = s;
            batch.serial = p + new JsString(s.toString()).padStart(d - p.length, "0");
            return batch;
        }).filter(Objects::nonNull).subscribe(subject::next);
    }
}
