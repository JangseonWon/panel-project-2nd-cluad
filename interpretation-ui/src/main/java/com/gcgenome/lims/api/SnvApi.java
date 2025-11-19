package com.gcgenome.lims.api;

import com.gcgenome.lims.dto.VariantRaw;
import elemental2.core.JsArray;
import elemental2.dom.RequestInit;
import elemental2.dom.Response;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class SnvApi {
    public Promise<List<VariantRaw>> selected(long sampleId, String service) {

       return FetchApi.request("/samples/" + sampleId + "/services/" + service + "/snvs", null)
                .then(Response::json).<JsArray<Object>>then(Js::cast)
               .then(array->Promise.resolve(array.asList().stream().<VariantRaw>map(Js::cast).collect(Collectors.toList())));
    }

    public Promise<List<JsPropertyMap<?>>> selected2(long sampleId, String service) {
        RequestInit request = RequestInit.create();
        request.setHeaders(new String[][] {
                new String[] {"Content-Type", "application/vnd.lims.v1"},
                new String[] {"Accept", "application/json"}
        });
        return FetchApi.request("/samples/" + sampleId + "/services/" + service + "/snvs", request)
                .then(Response::json).<JsArray<Object>>then(Js::cast)
                .then(array->Promise.resolve(array.asList().stream().map(Js::asPropertyMap).collect(Collectors.toList())));
    }

    public Promise<List<JsPropertyMap<?>>> getSelectedByBatch(long sampleId, String service, String batch) {
        RequestInit request = RequestInit.create();
        request.setHeaders(new String[][] {
                new String[] {"Content-Type", "application/vnd.lims.v1"},
                new String[] {"Accept", "application/json"}
        });
        return FetchApi.request("/samples/" + sampleId + "/services/" + service + "/batches/" + batch + "/snvs", request)
                .then(Response::json).<JsArray<Object>>then(Js::cast)
                .then(array->Promise.resolve(array.asList().stream().map(Js::asPropertyMap).collect(Collectors.toList())));
    }

    public Promise<Response> test(long sampleId, String service) {
        RequestInit request = RequestInit.create();
        request.setHeaders(new String[][] {
                new String[] {"Content-Type", "application/vnd.lims.v1"},
                new String[] {"Accept", "application/json"}
        });
        return FetchApi.request("/samples/" + sampleId + "/services/" + service + "/snvs", request);
    }

    public Promise<Response> delete(long sampleId, String service, String variant) {
        RequestInit request = RequestInit.create();
        request.setMethod("DELETE");
        request.setHeaders(new String[][] {
                new String[] {"Content-Type", "application/vnd.lims.v1"}
        });
        return FetchApi.request("/samples/" + sampleId + "/services/" + service + "/snvs/" + variant + "/class", request);
    }
}
