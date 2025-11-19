package com.gcgenome.lims.api;

import com.gcgenome.lims.dto.Sample;
import elemental2.dom.RequestInit;
import elemental2.dom.Response;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import lombok.experimental.UtilityClass;

import static elemental2.core.Global.JSON;

@UtilityClass
public class SampleApi {
	public Promise<String> sex(long sampleId) {
		RequestInit request = RequestInit.create();
		request.setHeaders(new String[][] {
				new String[] {"Accept", "application/json"},
				new String[] {"Content-Type", "application/vnd.lims.v1"}
		});
		return FetchApi.request("/../samples/" + sampleId, request)
				.then(Response::json).then(json->{
					var map = Js.asPropertyMap(json);
					if(map == null) return null;
					var patient = map.get("patient");
					if(patient!=null) return Promise.resolve((String) Js.asPropertyMap(patient).get("sex"));
					return null;
				});
	}
	public Promise<Sample[]> siblings(long sampleId) {
		RequestInit request = RequestInit.create();
		request.setHeaders(new String[][] {new String[] {"Accept", "application/json"}});
		ProgressApi.open(true);
		return FetchApi.request("/../samples/" + sampleId + "/siblings", request)
				.then(Response::text)
				.then(r->{
					ProgressApi.close();
					if(r!=null && !r.trim().isEmpty()) return Promise.resolve((Sample[])JSON.parse(r));
					else return Promise.resolve((Sample[])null);
				});
	}
}
