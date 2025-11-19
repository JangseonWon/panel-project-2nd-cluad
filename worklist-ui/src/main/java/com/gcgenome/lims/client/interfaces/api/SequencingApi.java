package com.gcgenome.lims.client.interfaces.api;

import com.gcgenome.lims.client.interfaces.work.op.PatchOperation;
import com.gcgenome.lims.client.interfaces.work.op.SequencingRepository;
import elemental2.dom.RequestInit;
import elemental2.promise.Promise;

import javax.inject.Inject;
import javax.inject.Singleton;

import static elemental2.core.Global.JSON;

@Singleton
public class SequencingApi implements SequencingRepository {
    private final FetchApi fetchApi;
    @Inject SequencingApi(FetchApi fetchApi) {
        this.fetchApi = fetchApi;
    }

    @Override
    public Promise<Void> update(String worklist, Integer index, PatchOperation[] patches) {
        RequestInit request = RequestInit.create();
        request.setMethod("PATCH");
        request.setHeaders(new String[][] {
                new String[] {"Content-Type", "application/json-patch+json"}
        });
        request.setBody(JSON.stringify(patches));
        return fetchApi.request("worklists/" + worklist + "/" + index + "/sequencing-index", request).then(response->{
            if(response.status==200) return Promise.resolve((Void) null);
            else return response.text().then(Promise::reject);
        });
    }
}
