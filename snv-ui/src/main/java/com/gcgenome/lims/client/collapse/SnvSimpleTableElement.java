package com.gcgenome.lims.client.collapse;

import com.google.gwt.i18n.client.NumberFormat;
import elemental2.core.Global;
import elemental2.dom.HTMLDivElement;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.chart.Column;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.SheetElement;
import net.sayaya.ui.chart.column.ColumnBuilder;
import net.sayaya.ui.chart.column.ColumnNumber;
import net.sayaya.ui.chart.column.ColumnText;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.jboss.elemento.Elements.div;

public class SnvSimpleTableElement extends HTMLElementBuilder<HTMLDivElement, SnvSimpleTableElement> {
    public static SnvSimpleTableElement build(Column... columns) {
        return new SnvSimpleTableElement(div(), columns);
    }
    static ColumnText text(String name){
        return text(name, name);
    }
    static ColumnText text(String id, String name){
        return ColumnBuilder.text(id, 10, 100).name(name).horizontal("center").vertical("middle").readOnly(true);
    }
    static ColumnNumber number(String id, String name, NumberFormat nf){
        return ColumnBuilder.number(id).name(name).format(nf).horizontal("center").vertical("middle").readOnly(true);
    }
    private final SheetElement.SheetConfiguration config = SheetElement.builder()
            .autoColSize(true)
            .autoRowSize(false)
            .viewportColumnRenderingOffset(100.0)
            .rowHeaderWidth(30)
            .rowHeaders(false)
            .manualColumnMove(false)
            .manualColumnResize(true)
            .data(new Data[] {})
            .colWidths(new Double[]{null, null, 80.0, 100.0, 100.0, null, null, null, 150.0})
            .stretchH("all");
    private final SheetElement sheet = config.build();
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private SnvSimpleTableElement(HTMLContainerBuilder<HTMLDivElement> e, Column... columns) {
        super(e.css("variant-summary-table").style("height: fit-content;"));
        _this = e.add(sheet);
        config.columns(columns);
        sheet.on(EventType.click, evt->{
            evt.stopPropagation();
            evt.preventDefault();
        });
        sheet.on(EventType.mouseover, evt->{
            evt.stopPropagation();
            evt.preventDefault();
        });
    }
    public SnvSimpleTableElement update(Object[] objs) {
        if(objs!=null) update(Arrays.stream(objs).map(this::map).toArray(Data[]::new));
        else update(new Data[0]);
        return that();
    }
    private Data map(Object obj) {
        JsPropertyMap<Object> map = Js.asPropertyMap(obj);
        Data data = Data.create(map.get("snv").toString());
        String analysis = unwrap(map.get("analysis"));
        if(analysis!=null && analysis.contains(":")) analysis = analysis.substring(analysis.indexOf(":")+1);
        map.forEach(key->data.initialize(key, map.get(key).toString()));
        data.put("analysis", analysis);
        return data;
    }
    static String unwrap(Object obj) {
        if (obj == null) return null;
        String p = (String) obj;
        if (p.startsWith("[") && p.endsWith("]")) try {
            Any[] arr = Js.asArray(Global.JSON.parse(p));
            return Arrays.stream(arr).map(s -> String.valueOf(s)).distinct().collect(Collectors.joining(", "));
        } catch (Exception e) {return p;}
        else return p;
    }
    private void update(Data[] values) {
        sheet.values(values);
        // Scheduler.get().scheduleDeferred(()->_this.element().style.height = CSSProperties.HeightUnionType.of((((HTMLElement) sheet.element().querySelector(".ht_master .wtHider")).offsetHeight)+ "px"));
    }
    /*public int height() {
        return calculateTableHeight(config.data());
    }
    private int calculateTableHeight(Data[] values) {
        if(values == null) return 0;
        return 26 + values.length*21;
    }*/
    @Override
    public SnvSimpleTableElement that() {
        return this;
    }
}
