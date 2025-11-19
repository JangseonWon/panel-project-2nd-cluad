package com.gcgenome.lims.client.expand.hrd;

import com.gcgenome.lims.api.SnvApi;
import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import com.gcgenome.lims.dto.InterpretationHrd;
import com.google.gwt.i18n.client.NumberFormat;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import lombok.experimental.Delegate;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.SheetElement;
import net.sayaya.ui.chart.SheetElementSelectableMulti;
import net.sayaya.ui.chart.column.ColumnBuilder;
import net.sayaya.ui.chart.column.ColumnNumber;
import net.sayaya.ui.chart.column.ColumnText;
import net.sayaya.ui.event.HasSelectionChangeHandlers;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jboss.elemento.Elements.div;

public class Tier extends HTMLElementBuilder<HTMLDivElement, Tier> implements InterpretationSubject<HTMLDivElement>, HasSelectionChangeHandlers<Data[]> {
    public static Tier build(InterpretationHrd.Tier tier) {
        return new Tier(div(), tier);
    }
    private static ColumnText column(String id, String name){
        return ColumnBuilder.text(id, 10, 100).name(name).horizontal("center").vertical("middle");
    }
    private static ColumnNumber number(String id, String name, NumberFormat format){
        return ColumnBuilder.number(id).name(name).horizontal("center").vertical("middle").format(format);
    }
    private final SheetElement.SheetConfiguration config = SheetElement.builder()
            .autoColSize(true)
            .autoRowSize(false)
            .rowHeaders(false)
            .manualColumnMove(false)
            .manualColumnResize(true)
            .data(new Data[] {})
            .colWidths(new Double[]{null, 80.0, 100.0, null, 80.0, null, 80.0})
            .stretchH("all");
    private final SheetElement sheet = config.build();
    private final InterpretationHrd.Tier tier;
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private final String code;
    private final long sample;
    private final HTMLDivElement empty = div().style("font-size: small;display: flex; align-items: center; justify-content: center;user-select: none; height: calc(100% - 27px);").add("No variant").element();
    @Delegate private final SheetElementSelectableMulti selection = SheetElementSelectableMulti.wrap(sheet);
    private Tier(HTMLContainerBuilder<HTMLDivElement> e, InterpretationHrd.Tier tier) {
        super(e.css("variant-summary-table").style("height: fit-content; margin-right: 0px;"));
        this.tier = tier;
        this.code = Subjects.code.getValue();
        this.sample = Subjects.sample.getValue();
        _this = e.add(div().style("overflow: hidden; height: auto;").add(sheet)).add(empty);
        config.columns(column("gene", "Gene").build(),
                column("hgvsc", "DNA").readOnly(false).build(),
                column("hgvsp", "Protein").build(),
                number("vaf", "VAF(%)", NumberFormat.getFormat("0.##")).build(),
                number("depth", "Depth(X)", NumberFormat.getFormat("0")).build(),
                column("cosmic_id", "COSMIC ID").build());
//                ColumnBuilder.link("Reported", data-> "../info.html#/panel-service/varsnv.html#" + data.get("snv")).horizontal("center").vertical("middle").build());
        e.add(div().style("overflow: hidden; height: auto;").add(sheet)).add(empty);
        Subjects.interpretation.subscribe(obj->{ try {
            InterpretationHrd value = Js.cast(obj);
            update(value);
        } catch(ClassCastException ignore){ }});
    }
    private InterpretationHrd.Variant[] variants() {
        InterpretationHrd value = Js.cast(Subjects.interpretation.getValue());
        return Arrays.stream(value.results).filter(r -> InterpretationHrd.Tier.equalsIgnoreCaseRemoveWhitespace(tier, r.tier)).findAny().get().variants;
    }

    void upward() {
        Set<String> selections = Arrays.stream(selection.selection()).map(Data::idx).collect(Collectors.toSet());
        Data[] all = config.data();
        var all2 = variants();
        for (int i = 1; i < all.length; ++i) {
            Data v = all[i];
            var v2 = all2[i];
            String idx = v.idx();
            if (selections.contains(idx)) {
                all[i] = all[i - 1];
                all[i - 1] = v;
                all2[i] = all2[i - 1];
                all2[i - 1] = v2;
            }
        }
        sheet.values(all);
    }
    void downward() {
        Set<String> selections = Arrays.stream(selection.selection()).map(Data::idx).collect(Collectors.toSet());
        Data[] all = config.data();
        var all2 = variants();
        for(int i = all.length-2; i >= 0; --i) {
            Data v = all[i];
            var v2 = all2[i];
            String idx = v.idx();
            if(selections.contains(idx)) {
                all[i] = all[i+1];
                all[i+1] = v;
                all2[i] = all2[i+1];
                all2[i+1] = v2;
            }
        }
        sheet.values(all);
    }

    void remove() {
        Set<String> selectedIds = Arrays.stream(selection.selection())
                .map(Data::idx)
                .collect(Collectors.toSet());
        Data[] data = Arrays.stream(config.data())
                .filter(d -> !selectedIds.contains(d.idx()))
                .toArray(Data[]::new);
        sheet.values(data);
        var promises =  Arrays.stream(variants()).filter(v -> selectedIds.contains(v.snv)).map(v ->  SnvApi.delete(sample, code, v.snv)).toArray(Promise[]::new);
        Promise.all(promises).finally_(()->{
            DomGlobal.alert("선택하신 변이가 제거되었습니다. 새로고침하여 확인해주세요.");
            JsArray.asJsArray(variants()).filter((v, index) -> !selectedIds.contains(v.snv));
        });
    }

    private void update(InterpretationHrd value) {
        if(value==null || value.results==null) return;
        var find = Arrays.stream(value.results)
                .filter(r -> InterpretationHrd.Tier.equalsIgnoreCaseRemoveWhitespace(tier, r.tier))
                .findAny();
        if(find.isPresent() && find.get().variants!=null) {
            var data = Arrays.stream(find.get().variants).map(this::map).toArray(Data[]::new);
            sheet.values(data);
            empty.style.display = "none";
        } else {
            sheet.values(new Data[0]);
            empty.style.display = "flex";
        }
    }
    private Data map(InterpretationHrd.Variant variant) {
        var id = variant.snv;
        if(id == null) id = variant.toString();
        var data = Data.create(id)
                .initialize("gene", variant.gene)
                .initialize("hgvsc", variant.hgvsc != null ? variant.hgvsc : variant.dna)
                .initialize("hgvsp", variant.hgvsp != null ? variant.hgvsp : variant.protein)
                .initialize("cosmic_id", variant.cosmicId);
        if(variant.vaf!=null) data.initialize("vaf", NumberFormat.getFormat("0.##").format(variant.vaf));
        if(variant.depth()!=null) data.initialize("depth", NumberFormat.getFormat("0.##").format(variant.depth()));
        data.onValueChange(evt->{
            var d = evt.value();
            if(d.isChanged("gene")) variant.gene = d.get("gene");
            if(d.isChanged("hgvsc")) variant.hgvsc = d.get("hgvsc");
            if(d.isChanged("hgvsp")) variant.hgvsp = d.get("hgvsp");
            if(d.isChanged("vaf")) variant.vaf = Double.parseDouble(d.get("vaf"));
            if(d.isChanged("depth")) variant.depth(Integer.parseInt(d.get("depth")));
            if(d.isChanged("cosmic_id")) variant.cosmicId = d.get("cosmic_id");
        });
        return data;
    }
    @Override
    public Tier that() {
        return this;
    }
}