package com.gcgenome.lims.client.expand.tso;

import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import com.gcgenome.lims.dto.InterpretationTso;
import com.gcgenome.lims.ui.IconElement;
import com.google.gwt.i18n.client.NumberFormat;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLabelElement;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.ListElement;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.SheetElement;
import net.sayaya.ui.chart.column.ColumnBuilder;
import net.sayaya.ui.chart.column.ColumnDropDown;
import net.sayaya.ui.chart.column.ColumnText;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.label;

public class Fusion extends HTMLElementBuilder<HTMLDivElement, Fusion> implements InterpretationSubject<HTMLDivElement> {
    public static Fusion build() {
        return new Fusion(div());
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
    private final HTMLDivElement empty = div().style("font-size: small;display: flex; align-items: center; justify-content: center;user-select: none; height: calc(100% - 27px);").add("No variant").element();
    private final ButtonElement btnAppend = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-plus")).text("1 Variant");
    private final ButtonElement btnRemove = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-minus")).text("1 Variant");
    private final HTMLContainerBuilder<HTMLLabelElement> lblCnt = label().style("font-size: 1.2em;");
    private final HTMLContainerBuilder<HTMLDivElement> controller = div().style("margin-top: 5px;text-align: right;").add(btnAppend).add(btnRemove);
    private final BehaviorSubject<InterpretationTso.Fusion[]> subject;
    private Fusion(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.css("variant-summary-table").style("height: fit-content; margin-right: 0px;border-bottom: 0px hidden;"));
        config.columns(
                select("tier", "Tier", "Tier1", "Tier2", "Tier3").build(),
                column("gene", "Gene").build(),
                column("fusion", "Fusion").build(),
                column("vaf", "vaf(%)").build(),
                column("depth", "depth(X)").build(),
                column("read_count", "Read count").build(),
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
        btnAppend.onClick(evt->append());
        btnRemove.onClick(evt->remove());
        this.subject = Subjects.fusions;
        Subjects.fusions.subscribe(this::update);
    }
    private void append() {
        var last = new InterpretationTso.Cnv();
        last.kind(InterpretationTso.VariantKind.FUSION);
        subject.next(Stream.concat(Arrays.stream(subject.getValue()), Stream.of(last)).toArray(InterpretationTso.Fusion[]::new));
    }
    private void remove() {
        var all = subject.getValue();
        var trim = Arrays.stream(all).limit(all.length-1).toArray(InterpretationTso.Fusion[]::new);
        subject.next(trim);
    }
    private InterpretationTso.Fusion[] value = null;
    private void update(InterpretationTso.Fusion[] value) {
        this.value = value;
        if(value!=null && value.length > 0) {
            var data = Arrays.stream(value).map(this::map).toArray(Data[]::new);
            sheet.values(data);
            empty.style.display = "none";
        } else {
            sheet.values(new Data[0]);
            empty.style.display = "flex";
        }
        lblCnt.textContent("Total Fusions: " + (value!=null?value.length:0));
    }
    private Data map(InterpretationTso.Fusion variant) {
        var id = variant.gene;
        var data = Data.create(id)
                .initialize("tier", variant.tier()!=null?variant.tier().name():"")
                .initialize("gene", variant.gene)
                .initialize("fusion", variant.fusion)
                .initialize("vaf", variant.vaf)
                .initialize("depth", variant.depth!=null? NumberFormat.getFormat("0.##").format(variant.depth):"")
                .initialize("read_count", variant.readCount)
                .initialize("significance", variant.significance);
        data.onValueChange(evt->{
            var d = evt.value();
            if(d.isChanged()) {
                variant.tier((d.get("tier")!=null&&!d.get("tier").isEmpty()) ? InterpretationTso.Tier.valueOf(d.get("tier").toString()) : null);
                variant.gene = d.get("gene");
                variant.fusion = d.get("fusion");
                variant.vaf = d.get("vaf");
                variant.depth = (d.get("depth")!=null && !d.get("depth").isEmpty())? NumberFormat.getFormat("0.##").parse(d.get("depth")):null;
                variant.readCount = d.get("read_count");
                variant.significance = d.get("significance");
                Subjects.fusions.next(value);
            }
        });
        return data;
    }
    @Override
    public Fusion that() {
        return this;
    }
}
