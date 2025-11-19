package com.gcgenome.lims.client.expand.tso;

import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import com.gcgenome.lims.dto.InterpretationTso;
import com.gcgenome.lims.ui.IconElement;
import com.google.gwt.i18n.client.NumberFormat;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLabelElement;
import lombok.experimental.Delegate;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.ButtonElementText;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.ListElement;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.SheetElement;
import net.sayaya.ui.chart.SheetElementSelectableMulti;
import net.sayaya.ui.chart.column.ColumnBuilder;
import net.sayaya.ui.chart.column.ColumnDropDown;
import net.sayaya.ui.chart.column.ColumnNumber;
import net.sayaya.ui.chart.column.ColumnText;
import net.sayaya.ui.event.HasSelectionChangeHandlers;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.label;

public class SnvIndel extends HTMLElementBuilder<HTMLDivElement, SnvIndel> implements InterpretationSubject<HTMLDivElement>, HasSelectionChangeHandlers<Data[]> {
    public static SnvIndel build() {
        return new SnvIndel(div());
    }
    private static ColumnText column(String name){
        return column(name, name);
    }
    private static ColumnText column(String id, String name){
        return ColumnBuilder.text(id, 10, 100).name(name).horizontal("center").vertical("middle");
    }
    private static ColumnDropDown select(String id, String name, String... items){
        ListElement.SingleLineItem[] classes = new ListElement.SingleLineItem[items.length];
        for(int i = 0; i < items.length; ++i) classes[i] = ListElement.singleLine().label(items[i]);
        return ColumnBuilder.dropdown(id, classes).name(name).horizontal("center").vertical("middle");
    }
    private static ColumnNumber number(String id, String name, NumberFormat format){
        return ColumnBuilder.number(id).name(name).horizontal("center").vertical("middle").format(format);
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
            .colWidths(new double[]{40, 80, 100, 80, 30, 40, 100, 40})
            .stretchH("all");
    private final SheetElement sheet = config.build();
    private final HTMLDivElement empty = div().style("font-size: small;display: flex; align-items: center; justify-content: center;user-select: none; height: calc(100% - 27px);").add("No variant").element();
    @Delegate private final SheetElementSelectableMulti selection = SheetElementSelectableMulti.wrap(sheet);
    private final ButtonElement btnSortByTier = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-sort-alpha-down")).text("Sort by Tier").enabled(true);

    private final ButtonElementText btnSortByVaf = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-sort-numeric-down-alt")).text("Sort by VAF").enabled(true);
    private final ButtonElement btnToUpward = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-angle-up")).text("Up").enabled(false);
    private final ButtonElement btnToDownward = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-angle-down")).text("Down").enabled(false);
    private final HTMLContainerBuilder<HTMLLabelElement> lblCnt = label().style("font-size: 1.2em;");
    private final HTMLContainerBuilder<HTMLDivElement> controller = div().style("margin-top: 5px;text-align: right;").add(btnSortByTier).add(btnSortByVaf).add(btnToUpward).add(btnToDownward);
    private BehaviorSubject<InterpretationTso.Snv[]> subject;
    private SnvIndel(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.css("variant-summary-table").style("height: fit-content; margin-right: 0px;border-bottom: 0px hidden;"));
        config.columns(
                column("tier", "Tier").readOnly(true).build(),
                column("gene", "Gene").build(),
                column("hgvsc", "DNA").readOnly(false).build(),
                column("hgvsp", "Protein").build(),
                number("vaf", "VAF(%)", NumberFormat.getFormat("0.##")).build(),
                number("depth", "Depth(X)", NumberFormat.getFormat("0")).build(),
                select("significance", "Significance",
                        "-",
                        "Therapeutic", "Prognostic", "Diagnostic",
                        "Therapeutic / Prognostic", "Therapeutic / Diagnostic", "Prognostic / Diagnostic",
                        "Therapeutic / Prognostic / Diagnostic",
                        "Therapeutic(R)",
                        "Therapeutic(R) / Prognostic", "Therapeutic(R) / Diagnostic",
                        "Therapeutic(R) / Prognostic / Diagnostic"
                ).build(),
                ColumnBuilder.link("Reported", data-> "../info.html#/panel-service/varsnv.html#" + data.get("snv")).horizontal("center").vertical("middle").build());
        e.add(div().style("overflow: hidden; height: auto;border-bottom: 1px solid #AAA;").add(sheet))
                .add(div().style("display: flex; justify-content: space-between;")
                        .add(lblCnt).add(controller)).add(empty);
        selection.onSelectionChange(evt->{
            boolean enabled = evt.selection() != null && evt.selection().length > 0;
            btnSortByTier.enabled(!enabled);
            btnSortByVaf.enabled(!enabled);
            btnToUpward.enabled(enabled);
            btnToDownward.enabled(enabled);
        });
        btnSortByVaf.onClick(evt->sortBy(Comparator.comparing(s->(s.vaf!=null&&!s.vaf.trim().isEmpty())?-Double.parseDouble(s.vaf):null, Comparator.nullsLast(Comparator.naturalOrder()))));
        btnSortByTier.onClick(evt->sortBy(Comparator.comparing(InterpretationTso.Variant::tier, Comparator.nullsLast(Comparator.naturalOrder()))));
        btnToUpward.onClick(evt->upward());
        btnToDownward.onClick(evt->downward());
        this.subject = Subjects.snvs;
        Subjects.snvs.subscribe(this::update);
    }
    private void sortBy(Comparator<InterpretationTso.Snv> comparator) {
        var all = variants();
        Arrays.sort(all, comparator);
        subject.next(all);
    }
    private void upward() {
        Set<String> selections = Arrays.stream(selection.selection()).map(Data::idx).collect(Collectors.toSet());
        var all = variants();
        for(int i = 1; i < all.length; ++i) {
            var v = all[i];
            String idx = v.snv;
            if(selections.contains(idx)) {
                all[i] = all[i-1];
                all[i-1] = v;
            }
        }
        subject.next(all);
    }
    private void downward() {
        Set<String> selections = Arrays.stream(selection.selection()).map(Data::idx).collect(Collectors.toSet());
        var all = variants();
        for(int i = all.length-2; i >= 0; --i) {
            var v = all[i];
            String idx = v.snv;
            if(selections.contains(idx)) {
                all[i] = all[i+1];
                all[i+1] = v;
            }
        }
        subject.next(all);
    }
    private InterpretationTso.Snv[] variants() {
        return subject.getValue();
    }
    private void update(InterpretationTso.Snv[] values) {
        var preselected = Arrays.stream(selection()).map(Data::idx).collect(Collectors.toSet());
        if(values!=null && values.length > 0) {
            var data = Arrays.stream(values).map(this::map).peek(d->d.select(preselected.contains(d.idx()))).toArray(Data[]::new);
            sheet.values(data);
            empty.style.display = "none";
        } else {
            sheet.values(new Data[0]);
            empty.style.display = "flex";
        }
        lblCnt.textContent("Total SNVs: " + (values!=null?values.length:0));
    }
    private Data map(InterpretationTso.Snv variant) {
        var id = variant.snv;
        var data = Data.create(id)
                .initialize("tier", variant.tier()!=null?variant.tier().name():"")
                .initialize("gene", variant.gene)
                .initialize("hgvsc", variant.hgvsc)
                .initialize("hgvsp", variant.hgvsp)
                .initialize("vaf", variant.vaf)
                .initialize("depth", variant.depth!=null?NumberFormat.getFormat("0.##").format(variant.depth):"")
                .initialize("significance", variant.significance);
        if(variant.depth!=null) data.initialize("depth", NumberFormat.getFormat("0.##").format(variant.depth));
        data.onValueChange(evt->{
            var d = evt.value();
            if(d.isChanged()) {
                variant.tier((d.get("tier")!=null&&!d.get("tier").isEmpty()) ? InterpretationTso.Tier.valueOf(d.get("tier").toString()) : null);
                variant.gene = d.get("gene");
                variant.hgvsc = d.get("hgvsc");
                variant.hgvsp = d.get("hgvsp");
                variant.vaf = d.get("vaf");
                variant.depth = (d.get("depth")!=null && !d.get("depth").isEmpty())? NumberFormat.getFormat("0.##").parse(d.get("depth")):null;
                variant.significance = d.get("significance");
            }
        });
        return data;
    }
    @Override
    public SnvIndel that() {
        return this;
    }
}
