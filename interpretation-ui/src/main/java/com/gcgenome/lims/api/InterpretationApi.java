package com.gcgenome.lims.api;

import com.gcgenome.lims.dto.InterpretationPanel;
import elemental2.core.Global;
import elemental2.dom.RequestInit;
import elemental2.dom.Response;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import lombok.experimental.UtilityClass;

@UtilityClass
public class InterpretationApi {
	public Promise<Object> interpretation(long sampleId, String service) {
		RequestInit request = RequestInit.create();
		request.setHeaders(new String[][] {
				new String[] {"Accept", "application/json"},
				new String[] {"Content-Type", "application/vnd.lims.v1"}
		});
		return FetchApi.request("/samples/" + sampleId + "/services/" + service + "/interpretation", request)
				.then(Response::json);
	}
	public Promise<InterpretationPanel> interpretation(long sampleId, String service, Object param) {
		RequestInit request = RequestInit.create();
		request.setHeaders(new String[][] {
				new String[] {"Accept", "application/json"},
				new String[] {"Content-Type", "application/vnd.lims.v1+json"}
		});
		request.setMethod("PUT");
		request.setBody(Global.JSON.stringify(param));
		return FetchApi.request("/samples/" + sampleId + "/services/" + service + "/auto-interpret", request)
				.then(response->{
					if(response.status!=200) return Promise.reject(response.statusText);
					return Promise.resolve(response);
				}).then(Response::json).then(Js::cast);
	}
	public Promise<Object> save(long sampleId, String service, Object param) {
		RequestInit request = RequestInit.create();
		request.setHeaders(new String[][] {
				new String[] {"Accept", "application/json"},
				new String[] {"Content-Type", "application/vnd.lims.v1+json"}
		});
		request.setMethod("PUT");
		request.setBody(Global.JSON.stringify(param));
		return FetchApi.request("/samples/" + sampleId + "/services/" + service + "/interpretation", request)
				.then(response->{
					if(response.status!=200) return Promise.reject(response.statusText);
					return Promise.resolve(response);
				}).then(Response::json);
	}

	public Promise<Object> negative(long sampleId, String service) {
		RequestInit request = RequestInit.create();
		request.setHeaders(new String[][] {
				new String[] {"Accept", "application/json"},
				new String[] {"Content-Type", "application/vnd.lims.v1"}
		});
		return FetchApi.request("/samples/" + sampleId + "/services/" + service + "/negative-interpret", request)
				.then(response->{
					if(response.status!=200) return Promise.reject(response.statusText);
					return Promise.resolve(response);
				}).then(Response::json).then(Js::cast);
	}
}
