package com.gcgenome.lims.client.expand.elem;

import com.google.gwt.i18n.client.NumberFormat;
import elemental2.dom.HTMLDivElement;
import elemental2.promise.Promise;
import jsinterop.base.JsPropertyMap;
import lombok.experimental.Accessors;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.ListElement;
import net.sayaya.ui.chart.Column;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.SheetElement;
import net.sayaya.ui.chart.column.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.jboss.elemento.Elements.div;

public class SnvTableElement extends HTMLElementBuilder<HTMLDivElement, SnvTableElement> /*implements HasSelectionChangeHandlers<SnvTableElement.SnvReport[]>, HasStateChangeHandlers<SheetState>*/ {
    public static SnvTableElement build(int fix, Function<JsPropertyMap<?>, Data> mapper, Column... colums) {
        var builder = SheetElement.builder();
        return new SnvTableElement(builder
                .autoColSize(false)
                .autoRowSize(false)
                .rowHeaders(false)
                .renderAllRows(true)
                .manualColumnMove(true)
                .manualColumnResize(true)
                .fixedColumnsLeft(fix)
                .data(new Data[] {})
                .columns(colums)
                .stretchH("all")
                .build(), mapper);
    }
    private final SheetElement sheet;
    private Map<String, SnvReport> reported = new HashMap<>();
    private final Function<JsPropertyMap<?>, Data> mapper;
    private SnvTableElement(SheetElement e, Function<JsPropertyMap<?>, Data> mapper) {
        super(div().style("overflow: hidden;position: absolute;left: 0;right: 0;top: 115px;bottom: 60px; border: 1px solid #ddd;").add(e));
        this.mapper = mapper;
        sheet = e;
        com.gcgenome.lims.client.Data.snv.subscribe(this::update);
    }
    public SnvTableElement reported(SnvReport[] s) {
        reported.clear();
        for(SnvReport r: s) reported.put(r.variant, r);
        return this;
    }
    private Promise<Void> update(List<JsPropertyMap<?>> objs) {
        var data = objs.stream().map(mapper).toArray(Data[]::new);
        sheet.values(data);
        return null;
    }
    @Override
    public SnvTableElement that() {
        return this;
    }
    public static ColumnText text(String name){
        return text(name, name);
    }
    public static ColumnText text(String id, String name){
        return ColumnBuilder.text(id, 10, 100).name(name).horizontal("center").vertical("middle").readOnly(true);
    }
    public static ColumnNumber number(String id, String name, NumberFormat format){
        return ColumnBuilder.number(id).name(name).horizontal("right").vertical("middle").readOnly(true).format(format);
    }
    public static ColumnLink snv(String id, String name) {
        return ColumnBuilder.link(id, data-> "../info.html#/panel-service/varsnv.html#" + data.get(id)).name(name)
                .font("JetBrains Mono").readOnly(true)
                .horizontal("left").vertical("middle");
    }
    public static ColumnLink link(String id, String name, Function<Data, String> url) {
        return ColumnBuilder.link(id, url).name(name).vertical("middle");
    }
    public static ColumnDropDown dropdown(String name, ListElement.SingleLineItem... items){
        return dropdown(name, name, items);
    }
    public static ColumnDropDown dropdown(String id, String name, ListElement.SingleLineItem... items) {
        return ColumnBuilder.dropdown(id, items).name(name).horizontal("center").vertical("middle");
    }
    @lombok.Data
    @Accessors(fluent = true)
    public static final class SnvReport {
        private String variant;
        private String classification;
    }
}
