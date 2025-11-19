package com.gcgenome.lims.client.expand.mrd;

import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import com.gcgenome.lims.dto.InterpretationMrd;
import com.google.gwt.i18n.client.NumberFormat;
import elemental2.core.JsArray;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLabelElement;
import net.sayaya.rx.subject.ListOf;
import net.sayaya.ui.*;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.SheetElement;
import net.sayaya.ui.chart.column.ColumnBuilder;
import net.sayaya.ui.chart.column.ColumnDropDown;
import net.sayaya.ui.chart.column.ColumnNumber;
import net.sayaya.ui.chart.column.ColumnText;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;

import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.label;

public class MrdClones extends HTMLElementBuilder<HTMLDivElement, MrdClones> implements InterpretationSubject<HTMLDivElement> {
    public static MrdClones build(String gene) {
        return new MrdClones(div(), gene);
    }
    private final HTMLContainerBuilder<HTMLLabelElement> title = label().css("title").style("font-size: 1.5rem;");
    private final MrdScreenTableElement table;
    private final TextAreaElement<String> iptInterpretation = TextAreaElement.textBox().outlined().style("width: -webkit-fill-available; height: 250px;").text("Interpretation");
    private final ButtonElement btnAddVariant = ButtonElement.outline().css("button").before(IconElement.icon("plus_one")).text("Append Variant");
    private final ButtonElement btnRemVariant = ButtonElement.outline().css("button").before(IconElement.icon("remove_circle")).text("Remove Variant");
    private MrdClones(HTMLContainerBuilder<HTMLDivElement> e, String gene) {
        super(e);
        table = new MrdScreenTableElement(div(), gene);
        e.style("margin: 16px;")
                .add(title.add(gene.toUpperCase()))
                .add(table)
                .add(div().style("display: flex; justify-content: flex-end;")
                          .add(div().style("height: 42px;").add(btnAddVariant).add(btnRemVariant)))
                .add(iptInterpretation);
        Subjects.histories.subscribe(history->{
            table.update(history);
            update(table.peek(history.first()));
        });
        btnAddVariant.onClick(evt->table.append());
        btnRemVariant.onClick(evt->table.remove());
    }
    private org.gwtproject.event.shared.HandlerRegistration handler = null;
    private void update(InterpretationMrd.MrdGeneResult value) {
        iptInterpretation.value(value.interpretation!=null?value.interpretation:"");
        if(handler!=null) handler.removeHandler();
        handler = iptInterpretation.onValueChange(evt->value.interpretation = evt.value());
    }
    @Override
    public MrdClones that() {
        return this;
    }
    private static class MrdScreenTableElement extends HTMLElementBuilder<HTMLDivElement, MrdScreenTableElement> {
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
        private final SheetElement.SheetConfiguration config = SheetElement.builder()
                .autoColSize(false)
                .autoRowSize(false)
                .viewportColumnRenderingOffset(100.0)
                .rowHeaderWidth(30).preventOverflow("vertical")
                .rowHeaders(false)
                .manualColumnMove(false)
                .manualColumnResize(true)
                .data(new Data[] {})
                .colWidths(new double[]{100.0, 100.0, 75.0, 75.0, 100, 100, 600.0})
                .stretchH("all");
        private final SheetElement sheet = config.build();
        private final HTMLContainerBuilder<HTMLDivElement> container = div().css("variant-summary-table");
        private final HTMLDivElement empty = div().style("font-size: small;display: flex; align-items: center; justify-content: center;user-select: none; height: calc(100% - 27px);").add("No variant").element();
        private final String gene;
        private ListOf<InterpretationMrd.MrdHistory> histories = null;
        private MrdScreenTableElement(HTMLContainerBuilder<HTMLDivElement> e, String gene) {
            super(e);
            this.gene = gene;
            config.columns(
                    column("date", "Date").build(),
                    number("input_dna", "Input DNA", NumberFormat.getFormat("0")).build(),
                    number("total_depth", "Total " + gene + " Read Depth", NumberFormat.getFormat("0")).build(),
                    number("lqic_depth", "LQIC Read Depth", NumberFormat.getFormat("0")).build(),
                    number("clonal_depth", "Clonal " + gene + " Read Depth", NumberFormat.getFormat("0")).build(),
                    dropdown("detected", "MRD 검출 여부", "Detected", "Not Detected", "N/A")
                            .pattern("Detected").than("#FFFFFF", "#AD1742")
                            .pattern("Not Detected").than("#FFFFFF", "#007B5F")
                            .pattern("N/A").than("", "")
                            .build()
            );
            e.add(container.style("height: fit-content; margin-right: 0px; margin-bottom: 5px;")
                            .add(div().style("overflow: hidden; height: auto;").add(sheet))
                            .add(empty));
        }
        private void append() {
            var value = new InterpretationMrd.MrdHistory();
            value.results = new InterpretationMrd.MrdGeneResult[0];
            histories.addFirst(value);
        }
        private void remove() {
            if(histories.size() <= 0) return;
            histories.pollFirst();
        }
        private void update(ListOf<InterpretationMrd.MrdHistory> histories) {
            this.histories = histories;
            if(histories!=null && !histories.isEmpty()) {
                var data = histories.value().stream().map(this::map).toArray(Data[]::new);
                sheet.values(data);
                empty.style.display = "none";
            } else {
                sheet.values(new Data[0]);
                empty.style.display = "flex";
            }
        }
        private InterpretationMrd.MrdGeneResult peek(InterpretationMrd.MrdHistory history) {
            return Arrays.stream(history.results).filter(r->r.gene.equals(gene)).findFirst().orElseGet(()->{
                var empty = new InterpretationMrd.MrdGeneResult();
                empty.gene = gene;
                empty.target = new InterpretationMrd.MrdCloneResult();
                empty.lqic = new InterpretationMrd.MrdCloneResult();
                JsArray.asJsArray(history.results).push(empty);
                return empty;
            });
        }
        private Data map(InterpretationMrd.MrdHistory history) {
            var result = peek(history);
            if(result.target==null) result.target = new InterpretationMrd.MrdCloneResult();
            if(result.lqic == null) result.lqic = new InterpretationMrd.MrdCloneResult();
            Data data = Data.create(history.date);
            data.put("date", history.date)
                .put("input_dna", history.inputDna!=null?String.valueOf(history.inputDna):null)
                .put("total_depth", result.target.readDepth!=null?String.valueOf(result.target.readDepth):null)
                .put("lqic_depth", result.lqic.readDepth!=null?String.valueOf(result.lqic.readDepth):null)
                .put("clonal_depth", result.target.clonalDepth!=null?String.valueOf(result.target.clonalDepth):null);
            {
                String detected = null;
                var tmp = result.result();
                if (InterpretationMrd.Result.DETECTED == tmp) detected = "Detected";
                else if (InterpretationMrd.Result.NOT_DETECTED == tmp) detected = "Not Detected";
                else if (InterpretationMrd.Result.NA == tmp) detected = "N/A";
                else if (InterpretationMrd.Result.CUSTOM == tmp) detected = "Custom";
                data.put("detected", detected);
            }
            data.onValueChange(evt->{
                history.date = data.get("date");
                history.inputDna = data.get("input_dna")!=null?Double.parseDouble(data.get("input_dna")):null;
                result.target.readDepth = data.get("total_depth")!=null?Double.parseDouble(data.get("total_depth")):null;
                result.lqic.readDepth = data.get("lqic_depth")!=null?Double.parseDouble(data.get("lqic_depth")):null;
                result.target.clonalDepth = data.get("clonal_depth")!=null?Double.parseDouble(data.get("clonal_depth")):null;
                InterpretationMrd.Result detected = null;
                if("Detected".equalsIgnoreCase(data.get("detected"))) detected = InterpretationMrd.Result.DETECTED;
                if("Not Detected".equalsIgnoreCase(data.get("detected"))) detected = InterpretationMrd.Result.NOT_DETECTED;
                if("N/A".equalsIgnoreCase(data.get("detected"))) detected = InterpretationMrd.Result.NA;
                if("Custom".equalsIgnoreCase(data.get("detected"))) detected = InterpretationMrd.Result.CUSTOM;
                result.result(detected);
            });
            return data;
        }
        @Override
        public MrdScreenTableElement that() {
            return this;
        }
    }
}
