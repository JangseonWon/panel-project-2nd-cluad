package com.gcgenome.lims.client.interfaces.api;

import com.gcgenome.lims.client.api.Page;
import com.gcgenome.lims.client.api.SearchApi;
import com.gcgenome.lims.client.domain.Search;
import com.gcgenome.lims.client.domain.Worklist;
import com.gcgenome.lims.client.usecase.worklist.WorklistRepository;
import dev.sayaya.rx.Observable;
import dev.sayaya.rx.subject.AsyncSubject;
import elemental2.dom.RequestInit;
import elemental2.dom.Response;
import elemental2.promise.Promise;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorklistApi implements SearchApi<Worklist>, WorklistRepository {
    private final FetchApi fetchApi;
    @Inject WorklistApi(FetchApi fetchApi) {
        this.fetchApi = fetchApi;
    }
    @Override
    public Promise<Response> searchRequest(String url) {
        var request = RequestInit.create();
        request.setHeaders(new String[][] {
                new String[] {"Accept", "application/vnd.gcgenome.lims.v1+json"}
        });
        return fetchApi.request(url, request);
    }

    @Override
    public Observable<Page<Worklist>> search(Search search) {
        // if(JsWindow.progress!=null) JsWindow.progress.enabled(true).intermediate(true);
        var promise = search("worklists", search);/*.finally_(()-> {
             if(JsWindow.progress!=null) JsWindow.progress.enabled(false);
        })*/;
        return AsyncSubject.await(promise);
    }
}
