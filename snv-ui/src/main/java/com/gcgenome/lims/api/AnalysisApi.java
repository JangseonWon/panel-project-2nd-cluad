package com.gcgenome.lims.api;

import elemental2.dom.Response;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import lombok.experimental.UtilityClass;

import java.util.Arrays;

@UtilityClass
public class AnalysisApi {
    public Promise<JsPropertyMap<?>[]> analysis(long sampleId) {
        return FetchApi.request("/samples/" + sampleId + "/analysis", null)
                .then(response->{
                    if(response.status!=200) return Promise.reject(response.statusText);
                    return Promise.resolve(response);
                }).then(Response::json)
                .then(jsons-> Promise.resolve(Arrays.stream(Js.asArray(jsons)).map(Js::asPropertyMap).toArray(JsPropertyMap[]::new)));
    }
}
