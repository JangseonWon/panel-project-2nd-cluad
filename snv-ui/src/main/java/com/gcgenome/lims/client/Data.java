package com.gcgenome.lims.client;

import com.gcgenome.lims.api.SnvApi;
import com.gcgenome.lims.dto.Query;
import elemental2.dom.Response;
import elemental2.promise.Promise;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.rx.subject.BehaviorSubject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;

public class Data {
    public static BehaviorSubject<List<Any>> analysisList = behavior(new LinkedList<>());
    public static BehaviorSubject<JsPropertyMap<?>> analysis = behavior(null);
    public static BehaviorSubject<Query> query = behavior(new Query().limit(1).page(0).sortBy("snv").asc(true).filters(new Query.Filter[0]));
    public static BehaviorSubject<List<Query.Filter>> filters =  behavior(new LinkedList<>());
    public static BehaviorSubject<List<JsPropertyMap<?>>> snv = behavior(new LinkedList<>());
    public static BehaviorSubject<Long> total = behavior(-1L);
    public static BehaviorSubject<List<net.sayaya.ui.chart.Data>> snvChanged =  behavior(new LinkedList<>());
    static {
        filters.subscribe(filters->query.next(query.getValue().page(0).filters(filters.stream().toArray(Query.Filter[]::new))));
        analysis.subscribe(analysis->refresh());
        query.subscribe(query->refresh());
    }
    static void refresh() {
        var analysis = Data.analysis.getValue();
        if(analysis==null) return;
        var query = Data.query.getValue();
        if(query == null) return;
        var sample = Long.parseLong(analysis.get("sample").toString());
        var service = analysis.get("service").toString();
        var batch = analysis.get("batch").toString();
        var row = Js.asInt(analysis.get("row"));
        var snvs = snv.getValue();
        snvs.clear();
        snv.next(snvs);
        snvChanged.getValue().clear();
        SnvApi.search(sample, service, batch, row, query)
                .then(response->{
                    var cnt = Long.parseLong(response.headers.get("X-Total-Count"));
                    total.next(cnt);
                    return Promise.resolve(response);
                }).then(Response::json)
                .then(json->{
                    JsPropertyMap<?>[] array = (JsPropertyMap<?>[]) Js.asArray(json);
                    var list = Arrays.stream(array).collect(Collectors.toList());
                    return Promise.resolve(list);
                }).then(s->{
                    snvs.addAll(s);
                    snv.next(snvs);
                    snvChanged.getValue().clear();
                    return null;
                });
    }
}
