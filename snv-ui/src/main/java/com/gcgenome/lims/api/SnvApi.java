package com.gcgenome.lims.api;

import com.gcgenome.lims.dto.Query;
import elemental2.dom.RequestInit;
import elemental2.dom.Response;
import elemental2.promise.Promise;
import lombok.experimental.UtilityClass;

import static elemental2.core.Global.JSON;

@UtilityClass
public class SnvApi {
    public Promise<Response> analysis(long sampleId) {
        return FetchApi.request("/samples/" + sampleId + "/analysis", null);
    }
    public Promise<Response> reported(long sampleId, String service) {
        RequestInit request = RequestInit.create();
        request.setHeaders(new String[][] {
                new String[] {"Content-Type", "application/vnd.lims.v1"},
                new String[] {"Accept", "application/json"}
        });
        return FetchApi.request("/samples/" + sampleId + "/services/" + service + "/snvs", request);
    }
    public Promise<Response> getReportedByBatch(long sampleId, String service, String batch) {
        RequestInit request = RequestInit.create();
        request.setHeaders(new String[][] {
                new String[] {"Content-Type", "application/vnd.lims.v1"},
                new String[] {"Accept", "application/json"}
        });
        return FetchApi.request("/samples/" + sampleId + "/services/" + service + "/batches/" + batch + "/snvs", request);
    }
    public Promise<Response> search(long sampleId, String service, String batch, int row, Query query) {
        RequestInit request = RequestInit.create();
        request.setMethod("POST");
        request.setHeaders(new String[][] {
                new String[] {"Content-Type", "application/vnd.lims.v1"},
                new String[] {"Accept", "application/json"}
        });
        request.setBody(JSON.stringify(query));
        return FetchApi.request("/samples/" + sampleId + "/services/" + service + "/batches/" + batch + "/" + row + "/snvs", request);
    }
    public Promise<Response> save(long sampleId, String service, String variant, String classification) {
        RequestInit request = RequestInit.create();
        request.setHeaders(new String[][] {
                new String[] {"Content-Type", "application/vnd.lims.v1"}
        });
        if(classification!=null && !"" .equalsIgnoreCase(classification)) {
            request.setMethod("PUT");
            return FetchApi.request("/samples/" + sampleId + "/services/" + service + "/snvs/" + variant + "/class/" + classification, request);
        } else {
            request.setMethod("DELETE");
            return FetchApi.request("/samples/" + sampleId + "/services/" + service + "/snvs/" + variant + "/class", request);
        }
    }
}
