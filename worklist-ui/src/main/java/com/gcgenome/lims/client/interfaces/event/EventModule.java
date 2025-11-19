package com.gcgenome.lims.client.interfaces.event;

import com.gcgenome.lims.client.usecase.work.UpdateSerialEvent;
import com.gcgenome.lims.client.usecase.worklist.UpdateWorklistEvent;
import dagger.Binds;
import dagger.Module;

@Module
public interface EventModule {
    @Binds UpdateWorklistEvent updateWorklistEventProvider(UpdateBatchEventSource impl);
    @Binds UpdateSerialEvent updateSerialEventProvider(UpdateSampleSerialEventSource impl);
}
