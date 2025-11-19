package com.gcgenome.lims.client.usecase.worklist;

import dagger.Module;
import dagger.Provides;
import dev.sayaya.rx.Subscription;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class WorklistModule {
    @Provides @Singleton @Named("worklist-subscription") Subscription worlistSubscription(SearchProvider search, WorklistRepository repository, WorklistList sink) {
        // Search가 업데이트되면 -> 150ms 디바운스 -> WorklistApi를 호출하고 -> worklists, total pages, total elements 업데이트
        return search.distinct().debounceTime(150).mergeMap(repository::search).subscribe(page -> {
            sink.next(page.getContent());
        });
    }
}
