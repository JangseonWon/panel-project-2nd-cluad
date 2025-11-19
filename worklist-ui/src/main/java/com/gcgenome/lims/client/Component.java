package com.gcgenome.lims.client;

import com.gcgenome.lims.client.interfaces.api.ApiModule;
import com.gcgenome.lims.client.interfaces.event.EventModule;
import com.gcgenome.lims.client.interfaces.work.WorkScene;
import com.gcgenome.lims.client.interfaces.worklist.WorklistScene;
import com.gcgenome.lims.client.usecase.work.WorkModule;
import com.gcgenome.lims.client.usecase.work.WorklistIdProvider;
import com.gcgenome.lims.client.usecase.worklist.WorklistModule;
import dev.sayaya.rx.Subscription;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { ApiModule.class, EventModule.class, WorklistModule.class, WorkModule.class })
public interface Component {
    WorklistScene worklistScene();
    WorkScene workScene();
    WorklistIdProvider worklistId();
    @Named("worklist-subscription") Subscription worklistSubscription();
    @Named("work-subscription") Subscription workSubscription();
}
