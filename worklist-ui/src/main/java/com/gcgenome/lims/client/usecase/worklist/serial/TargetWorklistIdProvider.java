package com.gcgenome.lims.client.usecase.worklist.serial;

import dev.sayaya.rx.subject.BehaviorSubject;
import lombok.experimental.Delegate;

import javax.inject.Inject;
import javax.inject.Singleton;

import static dev.sayaya.rx.subject.BehaviorSubject.behavior;

@Singleton
public class TargetWorklistIdProvider {
    @Delegate private final BehaviorSubject<String> subject = behavior(null);
    @Inject TargetWorklistIdProvider() {}
}
