package com.gcgenome.lims.client.interfaces.work.op;

import dev.sayaya.rx.subject.BehaviorSubject;
import lombok.experimental.Delegate;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.sayaya.rx.subject.BehaviorSubject.behavior;

@Singleton
public class OperationList {
    @Delegate private final BehaviorSubject<Set<Operation>> targets = behavior(null);
    @Inject OperationList(UpdateSequencings patches) {
        patches.subscribe(p-> targets.next(p.stream().collect(Collectors.toSet())));
    }
}
