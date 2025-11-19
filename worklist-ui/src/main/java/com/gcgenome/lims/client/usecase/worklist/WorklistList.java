package com.gcgenome.lims.client.usecase.worklist;

import com.gcgenome.lims.client.domain.Batch;
import com.gcgenome.lims.client.domain.Worklist;
import dev.sayaya.rx.subject.BehaviorSubject;
import lombok.experimental.Delegate;

import javax.inject.Inject;
import javax.inject.Singleton;

import static dev.sayaya.rx.subject.BehaviorSubject.behavior;

@Singleton
public class WorklistList {
    @Delegate private final BehaviorSubject<Worklist[]> worklists = behavior(new Worklist[0]);
    @Inject WorklistList(UpdateWorklistEvent events) {
        events.subscribe(evt->{
            Batch param = evt.param();
            if (param == null || worklists.getValue() == null) return;
            boolean updated = false;
            for(var worklist: worklists.getValue()) {
                if(!worklist.id().equals(param.id)) continue;
                worklist.serial(param.serial).prefix(param.infix).idx(param.idx).note(param.note);
                updated = true;
            }
            if(updated) worklists.next(worklists.getValue());
        });
    }
}
