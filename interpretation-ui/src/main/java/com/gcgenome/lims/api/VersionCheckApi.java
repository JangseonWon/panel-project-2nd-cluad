package com.gcgenome.lims.api;

import elemental2.dom.Response;
import elemental2.promise.Promise;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VersionCheckApi {
    public Promise<Boolean> isNew(long sample, String service) {
        return FetchApi.request("/samples/"+sample+"/services/"+service+"/reports/log", null)
                .then(Response::text)
                .then(log -> Promise.resolve(log.equals("[]")));
    }
}
