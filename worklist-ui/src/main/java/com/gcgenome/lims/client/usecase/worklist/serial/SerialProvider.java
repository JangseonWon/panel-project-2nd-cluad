package com.gcgenome.lims.client.usecase.worklist.serial;

import dev.sayaya.rx.subject.BehaviorSubject;
import lombok.experimental.Delegate;

import javax.inject.Inject;
import javax.inject.Singleton;

import static dev.sayaya.rx.subject.BehaviorSubject.behavior;

@Singleton
public class SerialProvider {
    @Delegate private final BehaviorSubject<String> subject = behavior(null);
    @Inject SerialProvider(BatchProvider batch) {
        batch.subscribe(b->{
            if(b!=null) subject.next(b.serial);
        });
    }
}
