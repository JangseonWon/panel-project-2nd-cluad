package com.gcgenome.lims.client.expand.mrd.screen;

import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import com.gcgenome.lims.dto.InterpretationMrdScreen;
import com.google.gwt.i18n.client.NumberFormat;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLabelElement;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.rx.subject.ListOf;
import net.sayaya.ui.*;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.SheetElement;
import net.sayaya.ui.chart.column.ColumnBuilder;
import net.sayaya.ui.chart.column.ColumnNumber;
import net.sayaya.ui.chart.column.ColumnText;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.label;

public class MrdScreenClones extends HTMLElementBuilder<HTMLDivElement, MrdScreenClones> implements InterpretationSubject<HTMLDivElement> {
    public static MrdScreenClones build(String cell, String gene) {
        return new MrdScreenClones(div(), cell, gene);
    }
    private final HTMLContainerBuilder<HTMLLabelElement> title = label().css("title").style("font-size: 1.5rem;");
    private final MrdScreenTableElement table;
    private final TextAreaElement<String> iptInterpretation = TextAreaElement.textBox().outlined().style("width: -webkit-fill-available; height: 250px;").text("Interpretation");
    private final TextFieldElement.TextFieldOutlined<Double> iptDepthTotal = TextFieldElement.numberBox().outlined().css("input").css("button").text("Total Depth").required(true);
    private final TextFieldElement.TextFieldOutlined<Double> iptCellCount = TextFieldElement.numberBox().outlined().css("input").css("button").readOnly(true);
    private final TextFieldElement.TextFieldOutlined<Double> iptDepthLqic = TextFieldElement.numberBox().outlined().css("input", "button").text("LQIC Depth").required(true);
    private final TextFieldElement.TextFieldOutlined<Double> iptLengthLqic = TextFieldElement.numberBox().outlined().css("input", "button").text("LQIC Length").required(true);
    private final ButtonElement btnAddVariant = ButtonElement.outline().css("button").before(IconElement.icon("plus_one")).text("Append Variant");
    private final ButtonElement btnRemVariant = ButtonElement.outline().css("button").before(IconElement.icon("remove_circle")).text("Remove Variant");
    private final BehaviorSubject<InterpretationMrdScreen.MrdScreenGeneResult> result = behavior(new InterpretationMrdScreen.MrdScreenGeneResult());
    private final BehaviorSubject<ListOf<InterpretationMrdScreen.MrdScreenCloneResult>> clones = (BehaviorSubject) behavior(ListOf.class);
    private MrdScreenClones(HTMLContainerBuilder<HTMLDivElement> e, String cell, String gene) {
        super(e);
        table = new MrdScreenTableElement(div(), cell);
        e.style("margin: 16px;")
                .add(title.add(gene.toUpperCase()))
                .add(table)
                .add(div().style("display: flex; justify-content: space-between;")
                          .add(div().add(iptDepthTotal).add(iptCellCount.text("Total " + cell + " Count")).add(iptDepthLqic).add(iptLengthLqic))
                          .add(div().add(btnAddVariant).add(btnRemVariant)))
                .add(iptInterpretation);
        Subjects.geneResults.subscribe(list->{
            var result = list.value().stream().filter(r->r.gene.equals(gene)).findFirst().orElseGet(()->{
                var empty = new InterpretationMrdScreen.MrdScreenGeneResult();
                empty.gene = gene;
                empty.clones = new InterpretationMrdScreen.MrdScreenCloneResult[0];
                list.add(empty);
                return empty;
            });
            this.result.next(result);
        });
        result.subscribe(this::update);
        result.subscribe(result->{
            var listOf = new ListOf<InterpretationMrdScreen.MrdScreenCloneResult>();
            var results = result.clones;
            if(results!=null && results.length > 0) listOf.replaceTo(Arrays.stream(result.clones).collect(Collectors.toList()));
            listOf.onValueChange(evt -> clones.next(listOf));
            clones.next(listOf);
        });
        clones.subscribe(list->{
            table.update(this.result.getValue(), list.value());
            InterpretationMrdScreen.MrdScreenGeneResult value = this.result.getValue();
            value.clones = list.value().stream().toArray(InterpretationMrdScreen.MrdScreenCloneResult[]::new);
        });
        iptDepthTotal.onValueChange(evt-> {
            result.getValue().depthTotal = evt.value();
            result.next(result.getValue());
        });
        iptDepthLqic.onValueChange(evt-> {
            result.getValue().depthLqic = evt.value();
            result.next(result.getValue());
        });
        iptLengthLqic.onValueChange(evt-> {
            result.getValue().lengthLqic = evt.value();
            result.next(result.getValue());
        });
        iptInterpretation.onValueChange(evt->result.getValue().interpretation = evt.value());
        btnAddVariant.onClick(evt->clones.getValue().add(new InterpretationMrdScreen.MrdScreenCloneResult()));
        btnRemVariant.onClick(evt->clones.getValue().pollLast());
    }
    private void update(InterpretationMrdScreen.MrdScreenGeneResult value) {
        iptDepthTotal.value(value.depthTotal);
        iptDepthLqic.value(value.depthLqic);
        iptLengthLqic.value(value.lengthLqic);
        iptInterpretation.value(value.interpretation);
        try { iptCellCount.value(value.bCells()+0.0); }
        catch(Exception e) { iptCellCount.value(null); }
    }
    @Override
    public MrdScreenClones that() {
        return this;
    }
    private final static class MrdScreenTableElement extends HTMLElementBuilder<HTMLDivElement, MrdScreenTableElement> {
        private static ColumnText column(String id, String name){
            return ColumnBuilder.text(id, 10, 100).name(name).horizontal("center").vertical("middle");
        }
        private static ColumnNumber number(String id, String name, NumberFormat format){
            return ColumnBuilder.number(id).name(name).horizontal("center").vertical("middle").format(format);
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
        private MrdScreenTableElement(HTMLContainerBuilder<HTMLDivElement> e, String cell) {
            super(e);
            config.columns(
                    column("region_v", "V Region").build(),
                    column("region_j", "J Region").build(),
                    column("depth", "Depth").build(),
                    column("length", "Length(bp)").build(),
                    column("equivalent", "Clonal Cell\nEquivalent").readOnly(true).build(),
                    number("pctClonal", "Clonal/Total\n" + cell + "(%)", NumberFormat.getFormat("0.00")).readOnly(true).build(),
                    column("sequence", "Sequence").horizontal("left").build()
            );
            e.add(container.style("height: fit-content; margin-right: 0px; margin-bottom: 5px;")
                            .add(div().style("overflow: hidden; height: auto;").add(sheet))
                            .add(empty));
        }
        private void update(InterpretationMrdScreen.MrdScreenGeneResult parent, List<InterpretationMrdScreen.MrdScreenCloneResult> objs) {
            if(objs!=null && objs.size() > 0) {
                var data = objs.stream().map(obj->map(parent, obj)).toArray(Data[]::new);
                sheet.values(data);
                empty.style.display = "none";
            } else {
                sheet.values(new Data[0]);
                empty.style.display = "flex";
            }
        }
        private Data map(InterpretationMrdScreen.MrdScreenGeneResult parent, InterpretationMrdScreen.MrdScreenCloneResult obj) {
            Data data = Data.create(obj.no);
            data.put("region_v", obj.regionV)
                .put("region_j", obj.regionJ)
                .put("length", obj.length!=null?String.valueOf(obj.length):null)
                .put("depth", obj.depth!=null?String.valueOf(obj.depth):null)
                .put("sequence", obj.sequence);
            sync(data, parent, obj);
            data.onValueChange(evt->{
                obj.regionV = data.get("region_v");
                obj.regionJ = data.get("region_j");
                obj.length = data.get("length")!=null?Double.parseDouble(data.get("length")):null;
                obj.depth = data.get("depth")!=null?Double.parseDouble(data.get("depth")):null;
                obj.sequence = data.get("sequence");
                sync(data, parent, obj);
            });
            return data;
        }
        private void sync(Data data, InterpretationMrdScreen.MrdScreenGeneResult parent, InterpretationMrdScreen.MrdScreenCloneResult obj) {
            try { data.put("equivalent", String.valueOf(obj.equivalent(parent.depthLqic.longValue()))); }
            catch(Exception e) {data.put("equivalent", null);}
            try {
                if(parent.bCells()> 0) data.put("pctClonal", String.valueOf(obj.coverage(parent.depthLqic.longValue(), parent.bCells())));
                else data.put("pctClonal", null); }
            catch(Exception e) {data.put("pctClonal", null);}
        }
        @Override
        public MrdScreenTableElement that() {
            return this;
        }
    }
}
