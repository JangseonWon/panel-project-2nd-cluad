package com.gcgenome.lims.client.usecase.work;

import dev.sayaya.rx.subject.BehaviorSubject;
import lombok.experimental.Delegate;

import javax.inject.Inject;
import javax.inject.Singleton;

import static dev.sayaya.rx.subject.BehaviorSubject.behavior;

@Singleton
public class WorklistIdProvider {
    @Delegate private final BehaviorSubject<String> subject = behavior(null);
    @Inject WorklistIdProvider() {}
}
