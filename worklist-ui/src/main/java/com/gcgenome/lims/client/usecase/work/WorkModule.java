package com.gcgenome.lims.client.usecase.work;

import dagger.Module;
import dagger.Provides;
import dev.sayaya.rx.Subscription;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class WorkModule {
    @Provides
    @Singleton
    @Named("work-subscription") Subscription worSubscription(WorklistIdProvider worklistId, WorkRepository repository, WorkList works) {
        // worklist id가 업데이트되면 -> 150ms 디바운스 -> WorkApi를 호출하고 -> works 업데이트
        return worklistId.debounceTime(150).mergeMap(repository::findByWorklist).subscribe(works::next);
    }
}
