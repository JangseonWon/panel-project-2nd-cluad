package com.gcgenome.lims.client.usecase.work;

import com.gcgenome.lims.client.domain.Serial;
import com.gcgenome.lims.client.domain.Work;
import dev.sayaya.rx.subject.BehaviorSubject;
import lombok.experimental.Delegate;

import javax.inject.Inject;
import javax.inject.Singleton;

import static dev.sayaya.rx.subject.BehaviorSubject.behavior;

@Singleton
public class WorkList {
    @Delegate private final BehaviorSubject<Work[]> works = behavior(new Work[0]);
    @Inject WorkList(UpdateSerialEvent events) {
        events.subscribe(evt->{
            Serial param = evt.param();
            boolean updated = false;
            for(var work: getValue()) {
                if(!work.index().equals(param.index) || !work.worklist().equals(param.worklist)) continue;
                work.id(param.id).serial(param.serial);
                updated = true;
                break;
            }
            if(updated) next(getValue());
        });
    }
}
