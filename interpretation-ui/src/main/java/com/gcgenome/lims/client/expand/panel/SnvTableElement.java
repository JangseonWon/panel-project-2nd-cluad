package com.gcgenome.lims.client.expand.panel;

import com.gcgenome.lims.dto.InterpretationPanel;
import elemental2.core.JsArray;
import elemental2.dom.CustomEvent;
import elemental2.dom.HTMLDivElement;
import lombok.experimental.Delegate;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.ListElement;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.SheetElement;
import net.sayaya.ui.chart.SheetElementSelectableMulti;
import net.sayaya.ui.chart.column.ColumnBuilder;
import net.sayaya.ui.chart.column.ColumnDropDown;
import net.sayaya.ui.chart.column.ColumnText;
import net.sayaya.ui.event.HasSelectionChangeHandlers;
import net.sayaya.ui.event.HasValueChangeHandlers;
import org.gwtproject.event.shared.HandlerRegistration;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jboss.elemento.Elements.div;

public class SnvTableElement extends HTMLElementBuilder<HTMLDivElement, SnvTableElement> implements ResultWriter<HTMLDivElement>, HasSelectionChangeHandlers<Data[]>, HasValueChangeHandlers<Data[]> {
    public static SnvTableElement build(String... classAllows) {
        if(classAllows==null || classAllows.length<=0) classAllows = new String[] {"PV", "LPV", "VUS"};
        return new SnvTableElement(div(), classAllows);
    }
    private static ColumnText column(String name){
        return column(name, name);
    }
    private static ColumnText column(String id, String name){
        return ColumnBuilder.text(id, 10, 100).name(name).horizontal("center").vertical("middle");
    }
    private static ColumnDropDown classColumn(String... classAllows) {
        ListElement.SingleLineItem[] classes = new ListElement.SingleLineItem[classAllows.length];
        for(int i = 0; i < classAllows.length; ++i) classes[i] = ListElement.singleLine().label(classAllows[i]);
        return ColumnBuilder.dropdown("Class", classes)
                .name("Class").horizontal("center")
                .pattern("^PV$").than("#FFFFFF", "#AD1742")
                .pattern("^LPV$").than("#121212", "#D26263")
                .pattern("^VUS$").than("#121212", "#FF8200")
                .pattern("^LBV$").than("#FFFFFF", "#00AB84")
                .pattern("^BV$").than("#FFFFFF", "#007B5F");
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
            .colWidths(new double[]{40, 50, 80, 100, 40, 80, 40, 40})
            .stretchH("all");
    private final SheetElement sheet = config.build();
    @Delegate private final SheetElementSelectableMulti selection = SheetElementSelectableMulti.wrap(sheet);
    private final Set<String> classAllows;
    private SnvTableElement(HTMLContainerBuilder<HTMLDivElement> e, String... classAllows) {
        super(e.css("variant-summary-table"));
        this.classAllows = Arrays.stream(classAllows).collect(Collectors.toSet());
        config.columns(classColumn(classAllows).build(), column("Gene").build(),
                column("hgvsc", "HGVS.c").readOnly(false).build(), column("hgvsp", "HGVS.p").build(), column("Zygosity").build(),
                column("Disease").build(), column("Inheritance").build(),
                ColumnBuilder.link("Reported", data-> "../info.html#/panel-service/varsnv.html#" + data.get("snv")).horizontal("center").vertical("middle").build());
        e.add(sheet);
    }
    @Override
    public InterpretationPanel append(InterpretationPanel map) {
        map.variants = Arrays.stream(sheet.values()).map(this::map).toArray(InterpretationPanel.Variant[]::new);
        return map;
    }
    public void append(Data... vars) {
        Data[] data = sheet.values();
        JsArray<Data> array = JsArray.asJsArray(data);
        for(Data var: vars) var.select(false);
        array.push(vars);
        update(data);
    }
    public void removes(Data... values) {
        Set<Data> tmp = Arrays.stream(values).collect(Collectors.toSet());
        Data[] data = Arrays.stream(sheet.values()).filter(v->!tmp.contains(v)).toArray(Data[]::new);
        update(data);
    }
    @Override
    public void update(InterpretationPanel map) {
        if(map!=null && map.variants!=null) update(Arrays.stream(map.variants).filter(v->classAllows.contains(v.clazz)).map(this::map).toArray(Data[]::new));
        else update(new Data[0]);
    }
    public void update(Data[] data) {
        sheet.values(data);
        element().dispatchEvent(new CustomEvent<>("change"));
    }
    private Data map(InterpretationPanel.Variant variant) {
        Data data = Data.create(variant.gene + ":" + variant.hgvsc);
        String reported = variant.reported.isEmpty()?null:"Reported";
        data.put("analysis", variant.analysis)
                .put("Gene", variant.gene)
                .put("hgvsc", variant.hgvsc)
                .put("hgvsp", variant.hgvsp)
                .put("origin_hgvsc", variant.originHgvsc)
                .put("origin_hgvsp", variant.originHgvsp)
                .put("Zygosity", variant.zygosity)
                .put("Disease", variant.disease)
                .put("Inheritance", variant.inheritance)
                .put("Class", variant.clazz)
                .put("disease_full_name", variant.diseaseFullName)
                .put("mim", variant.mim)
                .put("snv", variant.snv)
                .put("Reported", reported)
                .put("interpretation", variant.interpretation)
                .put("gen_phen_db", variant.genPhenDb);
        return data;
    }
    private InterpretationPanel.Variant map(Data data) {
        var variant = new InterpretationPanel.Variant();
        variant.snv = data.get("snv");
        variant.analysis = data.get("analysis");
        variant.gene = data.get("Gene");
        variant.hgvsc = data.get("hgvsc");
        variant.hgvsp = data.get("hgvsp");
        variant.originHgvsc = data.get("origin_hgvsc");
        variant.originHgvsp = data.get("origin_hgvsp");
        variant.zygosity = data.get("Zygosity");
        variant.disease = data.get("Disease");
        variant.inheritance = data.get("Inheritance");
        variant.clazz = data.get("Class");
        variant.diseaseFullName = data.get("disease_full_name");
        variant.mim = data.get("mim");
        variant.interpretation = data.get("interpretation");
        variant.genPhenDb = data.get("gen_phen_db");
        return variant;
    }
    @Override
    public SnvTableElement that() {
        return this;
    }

    @Override
    public HandlerRegistration onValueChange(ValueChangeEventListener<Data[]> valueChangeEventListener) {
        return onValueChange(element(), valueChangeEventListener);
    }
}
