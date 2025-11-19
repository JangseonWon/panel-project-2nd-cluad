package com.gcgenome.lims.client.usecase.worklist;

import com.gcgenome.lims.client.domain.Worklist;
import dev.sayaya.rx.subject.BehaviorSubject;
import lombok.experimental.Delegate;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

import static dev.sayaya.rx.subject.BehaviorSubject.behavior;

@Singleton
public class WorklistChanged {
    @Delegate private final BehaviorSubject<Set<Worklist>> changed = behavior(Set.of());
    @Inject WorklistChanged() {}
}
