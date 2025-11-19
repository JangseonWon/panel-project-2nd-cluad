package com.gcgenome.lims.client.usecase.worklist;

import com.gcgenome.lims.client.domain.Search;
import dev.sayaya.rx.subject.BehaviorSubject;
import lombok.experimental.Delegate;

import javax.inject.Inject;
import javax.inject.Singleton;

import static dev.sayaya.rx.subject.BehaviorSubject.behavior;

@Singleton
public class SearchProvider {
    @Delegate private final BehaviorSubject<Search> subject = behavior(Search.builder().build());
    @Inject SearchProvider(WorklistRepository repository, WorklistList sink) {
        // Search가 업데이트되면 -> 150ms 디바운스 -> WorklistApi를 호출하고 -> worklists, total pages, total elements 업데이트
        subject.distinct().debounceTime(150).mergeMap(repository::search).subscribe(page-> {
            sink.next(page.getContent());
        });
    }
}
