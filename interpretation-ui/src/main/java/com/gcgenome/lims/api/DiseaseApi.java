package com.gcgenome.lims.api;

import com.gcgenome.lims.dto.ParameterPanel;
import elemental2.dom.RequestInit;
import elemental2.dom.Response;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DiseaseApi {
    public Promise<ParameterPanel.Disease[]> disease(String gene) {
        RequestInit request = RequestInit.create();
        request.setHeaders(new String[][] {
                new String[] {"Accept", "application/json"},
                new String[] {"Content-Type", "application/vnd.lims.v1"}
        });
        return FetchApi.request("/genes/" + gene + "/disease", request)
                .then(response->{
                    if(response.status!=200) return Promise.reject(response.statusText);
                    return Promise.resolve(response);
                }).then(Response::json).then(Js::cast);
    }
}
