package com.gcgenome.lims.client.expand.tso;

import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import com.gcgenome.lims.dto.InterpretationTso;
import com.gcgenome.lims.ui.IconElement;
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
import net.sayaya.ui.chart.column.ColumnText;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.label;

public class Cnv extends HTMLElementBuilder<HTMLDivElement, Cnv> implements InterpretationSubject<HTMLDivElement> {
    public static Cnv build() {
        return new Cnv(div());
    }
    private static ColumnText column(String id, String name){
        return ColumnBuilder.text(id, 10, 100).name(name).horizontal("center").vertical("middle");
    }
    private static ColumnDropDown select(String id, String name, String... items){
        ListElement.SingleLineItem[] classes = new ListElement.SingleLineItem[items.length];
        for(int i = 0; i < items.length; ++i) classes[i] = ListElement.singleLine().label(items[i]);
        return ColumnBuilder.dropdown(id, classes).name(name).horizontal("center").vertical("middle");
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
            .colWidths(new double[]{50, 80, 100, 40, 80, 40, 40})
            .stretchH("all");
    private final SheetElement sheet = config.build();
    @Delegate
    private final SheetElementSelectableMulti selection = SheetElementSelectableMulti.wrap(sheet);
    private final HTMLDivElement empty = div().style("font-size: small;display: flex; align-items: center; justify-content: center;user-select: none; height: calc(100% - 27px);").add("No variant").element();
    private final ButtonElement btnSortByTier = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-sort-alpha-down")).text("Sort by Tier").enabled(true);

    private final ButtonElementText btnSortByFc = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-sort-numeric-down-alt")).text("Sort by F/C").enabled(true);
    private final ButtonElement btnToUpward = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-angle-up")).text("Up").enabled(false);
    private final ButtonElement btnToDownward = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-angle-down")).text("Down").enabled(false);private final ButtonElement btnAppend = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-plus")).text("1 Variant");
    private final ButtonElement btnRemove = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-minus")).text("1 Variant");
    private final HTMLContainerBuilder<HTMLLabelElement> lblCnt = label().style("font-size: 1.2em;");
    private final HTMLContainerBuilder<HTMLDivElement> controller = div().style("margin-top: 5px;text-align: right;").add(btnSortByTier).add(btnSortByFc).add(btnToUpward).add(btnToDownward).add(btnAppend).add(btnRemove);
    private final BehaviorSubject<com.gcgenome.lims.dto.InterpretationTso.Cnv[]> subject;
    private Cnv(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.css("variant-summary-table").style("height: fit-content; margin-right: 0px;border-bottom: 0px hidden;"));
        config.columns(
                select("tier", "Tier", "Tier1", "Tier2", "Tier3").build(),
                column("gene", "Gene").build(),
                column("type", "Type").build(),
                column("copy_number", "Copy number").build(),
                select("significance", "Significance",
                        "-",
                        "Therapeutic", "Prognostic", "Diagnostic",
                        "Therapeutic / Prognostic", "Therapeutic / Diagnostic", "Prognostic / Diagnostic",
                        "Therapeutic / Prognostic / Diagnostic",
                        "Therapeutic(R)",
                        "Therapeutic(R) / Prognostic", "Therapeutic(R) / Diagnostic",
                        "Therapeutic(R) / Prognostic / Diagnostic"
                ).build()
        );
        e.add(div().style("overflow: hidden; height: auto;border-bottom: 1px solid #AAA;").add(sheet))
                .add(div().style("display: flex; justify-content: space-between;")
                        .add(lblCnt).add(controller))
                .add(empty);
        selection.onSelectionChange(evt->{
            boolean enabled = evt.selection() != null && evt.selection().length > 0;
            btnSortByTier.enabled(!enabled);
            btnSortByFc.enabled(!enabled);
            btnToUpward.enabled(enabled);
            btnToDownward.enabled(enabled);
        });
        btnSortByFc.onClick(evt->sortBy(Comparator.comparing(s->(s.copyNumber!=null&&!s.copyNumber.trim().isEmpty())?
                -Double.parseDouble(s.copyNumber.toLowerCase().replace("fold change", "").replace("copy number", "").trim().split(" ")[0]):null,
                Comparator.nullsLast(Comparator.naturalOrder()))));
        btnSortByTier.onClick(evt->sortBy(Comparator.comparing(InterpretationTso.Variant::tier, Comparator.nullsLast(Comparator.naturalOrder()))));
        btnToUpward.onClick(evt->upward());
        btnToDownward.onClick(evt->downward());
        btnAppend.onClick(evt->append());
        btnRemove.onClick(evt->remove());
        this.subject = Subjects.cnvs;
        Subjects.cnvs.subscribe(this::update);
    }
    private void sortBy(Comparator<InterpretationTso.Cnv> comparator) {
        var all = variants();
        Arrays.sort(all, comparator);
        subject.next(all);
    }
    private void upward() {
        Set<String> selections = Arrays.stream(selection.selection()).map(Data::idx).collect(Collectors.toSet());
        var all = Arrays.stream(subject.getValue()).toArray(InterpretationTso.Cnv[]::new);
        for(int i = 1; i < all.length; ++i) {
            var v = all[i];
            String idx = id(v);
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
            String idx = id(v);
            if(selections.contains(idx)) {
                all[i] = all[i+1];
                all[i+1] = v;
            }
        }
        subject.next(all);
    }
    private void append() {
        var last = new InterpretationTso.Cnv();
        last.kind(InterpretationTso.VariantKind.CNV);
        subject.next(Stream.concat(Arrays.stream(subject.getValue()), Stream.of(last)).toArray(InterpretationTso.Cnv[]::new));
    }
    private void remove() {
        var all = variants();
        var trim = Arrays.stream(all).limit(all.length-1).toArray(InterpretationTso.Cnv[]::new);
        subject.next(trim);
    }
    private InterpretationTso.Cnv[] variants() {
        return subject.getValue();
    }
    private InterpretationTso.Cnv[] value = null;
    private void update(InterpretationTso.Cnv[] value) {
        this.value = value;
        var preselected = Arrays.stream(selection()).map(Data::idx).collect(Collectors.toSet());
        if(value!=null && value.length > 0) {
            var data = Arrays.stream(value).map(this::map).peek(d->d.select(preselected.contains(d.idx()))).toArray(Data[]::new);
            sheet.values(data);
            empty.style.display = "none";
        } else {
            sheet.values(new Data[0]);
            empty.style.display = "flex";
        }
        lblCnt.textContent("Total CNVs: " + (value!=null?value.length:0));
    }
    private String id(InterpretationTso.Cnv variant) {
        return variant.gene + "$" + variant.tier() + "%" + variant.copyNumber + "&" + variant.significance;
    }
    private Data map(InterpretationTso.Cnv variant) {
        var data = Data.create(id(variant))
                .initialize("tier", variant.tier()!=null?variant.tier().name():"")
                .initialize("gene", variant.gene)
                .initialize("type", variant.type)
                .initialize("copy_number", variant.copyNumber)
                .initialize("significance", variant.significance);
        data.onValueChange(evt->{
            var d = evt.value();
            if(d.isChanged()) {
                variant.tier((d.get("tier")!=null&&!d.get("tier").isEmpty()) ? InterpretationTso.Tier.valueOf(d.get("tier").toString()) : null);
                variant.gene = d.get("gene");
                variant.type = d.get("type");
                variant.copyNumber = d.get("copy_number");
                variant.significance = d.get("significance");
                Subjects.cnvs.next(value);
            }
        });
        return data;
    }
    @Override
    public Cnv that() {
        return this;
    }
}
