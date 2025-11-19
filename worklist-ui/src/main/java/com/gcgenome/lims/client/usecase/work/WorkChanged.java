package com.gcgenome.lims.client.usecase.work;

import dev.sayaya.rx.subject.BehaviorSubject;
import lombok.experimental.Delegate;
import net.sayaya.ui.chart.Data;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

import static dev.sayaya.rx.subject.BehaviorSubject.behavior;

@Singleton
public class WorkChanged {
    @Delegate private final BehaviorSubject<Set<Data>> changed = behavior(Set.of());
    @Inject WorkChanged() {}
}
