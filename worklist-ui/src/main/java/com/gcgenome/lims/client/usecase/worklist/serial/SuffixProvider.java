package com.gcgenome.lims.client.usecase.worklist.serial;

import dev.sayaya.rx.subject.BehaviorSubject;
import lombok.experimental.Delegate;

import javax.inject.Inject;
import javax.inject.Singleton;

import static dev.sayaya.rx.subject.BehaviorSubject.behavior;

@Singleton
public class SuffixProvider {
    @Delegate private final BehaviorSubject<Integer> subject = behavior(null);
    @Inject SuffixProvider() {}
}
