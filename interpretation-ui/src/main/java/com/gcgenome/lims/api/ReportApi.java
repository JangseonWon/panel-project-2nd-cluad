package com.gcgenome.lims.api;

import elemental2.dom.Blob;
import elemental2.dom.RequestInit;
import elemental2.dom.Response;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import lombok.experimental.UtilityClass;

import static elemental2.core.Global.JSON;

@UtilityClass
public class ReportApi {
	public Promise<Blob> preview(long sample, String service, Object interpretation) {
		RequestInit request = RequestInit.create();
		request.setMethod("POST");
		request.setHeaders(new String[][] {
				new String[] {"Content-Type", "application/json; charset=utf-8"}
		});
		request.setBody(JSON.stringify(interpretation));
		return FetchApi.request("/samples/" + sample + "/services/" + service + "/preview", request)
				.then(response->{
					if(response.status!=200) return Promise.reject(response.statusText);
					return Promise.resolve(response);
				}).then(Response::blob)
				.then(blob-> Promise.resolve(blob.slice(0, blob.size, "application/pdf")));
	}
	public Promise<JsPropertyMap<?>> print(long sample, String service, String description) {
		RequestInit request = RequestInit.create();
		request.setMethod("PUT");
		request.setHeaders(new String[][] {
				new String[] {"Content-Type", "text/plain; charset=utf-8"}
		});
		request.setBody(description);
		return FetchApi.request("/samples/" + sample + "/services/" + service + "/print", request)
				.then(response->{
					if(response.status!=200) return Promise.reject(response.statusText);
					return Promise.resolve(response);
				}).then(Response::json).then(json->Promise.resolve(Js.asPropertyMap(json)));
	}
	public Promise<Response> publish(long sample, String service, long createAt) {
		RequestInit request = RequestInit.create();
		request.setMethod("PUT");
		request.setHeaders(new String[][] {
				new String[] {"Content-Type", "application/json; charset=utf-8"}
		});
		return FetchApi.request("/samples/" + sample + "/services/" + service + "/reports/" + createAt + "/publish", request);
	}
	public Promise<String> state(long sample, String service) {
		return FetchApi.request("/samples/" + sample + "/services/" + service + "/state", null).then(Response::text);
	}
}
