package com.gcgenome.lims.client.expand.mrd.screen;

import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import com.gcgenome.lims.dto.InterpretationMrdScreen;
import com.google.gwt.i18n.client.NumberFormat;
import elemental2.dom.HTMLDivElement;
import net.sayaya.rx.subject.ListOf;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.IconElement;
import net.sayaya.ui.ListElement;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.SheetElement;
import net.sayaya.ui.chart.column.ColumnBuilder;
import net.sayaya.ui.chart.column.ColumnDropDown;
import net.sayaya.ui.chart.column.ColumnNumber;
import net.sayaya.ui.chart.column.ColumnText;
import org.jboss.elemento.HTMLContainerBuilder;

import static org.jboss.elemento.Elements.div;

public class HyperMutation extends HTMLElementBuilder<HTMLDivElement, HyperMutation> implements InterpretationSubject<HTMLDivElement> {
    public static HyperMutation build() {
        return new HyperMutation(div());
    }
    private final SheetElement.SheetConfiguration config = SheetElement.builder()
            .autoColSize(false)
            .autoRowSize(false)
            .viewportColumnRenderingOffset(100.0)
            .rowHeaderWidth(30).preventOverflow("vertical")
            .rowHeaders(false)
            .manualColumnMove(false)
            .manualColumnResize(true)
            .data(new Data[] {})
            .colWidths(new double[]{80.0, 300.0, 80.0})
            .stretchH("all");
    private final SheetElement sheet = config.build();
    private final HTMLDivElement empty = div().style("font-size: small;display: flex; align-items: center; justify-content: center;user-select: none; height: calc(100% - 27px);").add("No clone").element();
    private final ButtonElement btnAddVariant = ButtonElement.outline().css("button").before(IconElement.icon("plus_one")).text("Append Clone");
    private final ButtonElement btnRemVariant = ButtonElement.outline().css("button").before(IconElement.icon("remove_circle")).text("Remove Clone");
    private ListOf<InterpretationMrdScreen.SomaticMutation> list = null;
    private HyperMutation(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e);
        config.columns(column("clone", "Clone").build(),
                dropdown("hyper_mutation", "Somatic hypermutation status",
                        "Inconclusive (no clonal sequence)",
                        "Inconclusive (In-frame or No stop codon: N)",
                        "Presence of somatic hypermutation (Mutation rate: TEXT%)",
                        "No presence of somatic hypermutation (Mutation rate: TEXT%)",
                        "Not applicable",
                        "TEXT"
                ).readOnly(false).build(),
                number("mutation_rate", "Mutation rate(%)", NumberFormat.getFormat("0.00")).build());
        e.style("margin: 16px;")
         .add(div().css("variant-summary-table").style("height: fit-content; margin-right: 0px; margin-bottom: 5px;")
                   .add(div().style("overflow: hidden; height: auto;").add(sheet))
                   .add(empty))
         .add(div().style("margin-bottom: 16px; text-align: right;")
                   .add(btnAddVariant).add(btnRemVariant));
        Subjects.somaticMutations.subscribe(this::update);
        btnAddVariant.onClick(evt->{
            int n = list.size() + 1;
            var value = new InterpretationMrdScreen.SomaticMutation();
            value.clone = "Clone " + n;
            list.add(value);
        });
        btnRemVariant.onClick(evt->list.pollLast());
    }
    private void update(ListOf<InterpretationMrdScreen.SomaticMutation> objs) {
        this.list = objs;
        if(objs!=null && objs.size() > 0) {
            var data = objs.value().stream().map(this::map).toArray(Data[]::new);
            sheet.values(data);
            empty.style.display = "none";
        } else {
            sheet.values(new Data[0]);
            empty.style.display = "flex";
        }
    }
    private Data map(InterpretationMrdScreen.SomaticMutation obj) {
        Data data = Data.create(obj.clone);
        data.put("clone", obj.clone)
            .put("hyper_mutation", obj.hyperMutation)
            .put("mutation_rate", obj.mutationRate!=null && !obj.mutationRate.trim().isEmpty()? obj.mutationRate:null );
        data.onValueChange(evt->{
            var d = evt.value();
            if(d.isChanged()) {
                obj.clone = d.get("clone");
                obj.hyperMutation = d.get("hyper_mutation");
                obj.mutationRate = d.get("mutation_rate");
            }
        });
        return data;
    }
    @Override
    public HyperMutation that() {
        return this;
    }
    private static ColumnText column(String id, String name){
        return ColumnBuilder.text(id, 10, 100).name(name).horizontal("center").vertical("middle");
    }
    private static ColumnNumber number(String id, String name, NumberFormat format){
        return ColumnBuilder.number(id).name(name).horizontal("center").vertical("middle").format(format);
    }
    private static ColumnDropDown dropdown(String id, String name, String... items) {
        ListElement.SingleLineItem[] classes = new ListElement.SingleLineItem[items.length];
        for(int i = 0; i < items.length; ++i) classes[i] = ListElement.singleLine().label(items[i]);
        return ColumnBuilder.dropdown(id, classes).name(name).horizontal("center").vertical("middle");
    }
}
