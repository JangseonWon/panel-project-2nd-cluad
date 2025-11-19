package com.gcgenome.lims.client.interfaces.api;

import com.gcgenome.lims.client.domain.Work;
import com.gcgenome.lims.client.usecase.work.WorkRepository;
import dev.sayaya.rx.subject.AsyncSubject;
import elemental2.dom.RequestInit;
import elemental2.promise.Promise;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkApi implements WorkRepository {
    private final FetchApi fetchApi;
    @Inject WorkApi(FetchApi fetchApi) {
        this.fetchApi = fetchApi;
    }

    @Override
    public AsyncSubject<Work[]> findByWorklist(String worklistId) {
        RequestInit request = RequestInit.create();
        request.setHeaders(new String[][] {
                new String[] {"Accept", "application/vnd.gcgenome.lims.v1+json"}
        });
        // if(JsWindow.progress!=null) JsWindow.progress.enabled(true).intermediate(true);
        var promise = fetchApi.request("worklists/" + worklistId, request).then(response->{
            if(response.status==200) {
                return response.json().then(values-> Promise.resolve((Work[])values));
            } else if(response.status==204)   return Promise.reject("Empty result");
            return Promise.reject(response.statusText);
        });/*.finally_(()-> {
             if(JsWindow.progress!=null) JsWindow.progress.enabled(false);
        })*/;
        return AsyncSubject.await(promise);
    }
    public Promise<Void> create(String worklist) {
        RequestInit request = RequestInit.create();
        request.setMethod("PUT");
        request.setHeaders(new String[][] {
                new String[] {"Content-Type", "application/json"}
        });
        return fetchApi.request("worklists/" + worklist + "/generate-serials", request).then(response->{
            if(response.status==200) return Promise.resolve((Void) null);
            else return response.text().then(Promise::reject);
        });
    }
}
