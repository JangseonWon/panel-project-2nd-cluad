package com.gcgenome.lims.client.expand.panel.param;

import com.gcgenome.lims.api.DiseaseApi;
import com.gcgenome.lims.dto.ParameterPanel;
import com.google.gwt.core.client.Scheduler;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import jsinterop.base.Js;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.MergeCell;
import net.sayaya.ui.chart.SheetElement;
import net.sayaya.ui.chart.SheetElementSelectableMulti;
import net.sayaya.ui.chart.column.ColumnBuilder;
import net.sayaya.ui.chart.column.ColumnString;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.label;

public class DiseaseElement extends HTMLElementBuilder<HTMLDivElement, DiseaseElement> {
    public static DiseaseElement build(DiseaseFactory.GeneDisease... genes) {
        return new DiseaseElement(div(), genes);
    }
    private final List<GeneElement> children = new LinkedList<>();
    public DiseaseElement(HTMLContainerBuilder<HTMLDivElement> e, DiseaseFactory.GeneDisease... genes) {
        super(e.style("display: flex; flex-direction: column; color: var(--mdc-theme-text-primary-on-background); width: 800px;"));
        Section section = Section.build("Disease parameter");
        var children = div().style("padding-bottom: 1em;");
        e.add(section).add(children);
        for(var gene: genes) {
            var elem = GeneElement.build(gene);
            children.add(elem);
            this.children.add(elem);
        }
    }
    public Map<String, ParameterPanel.Disease[]> value() {
        return children.stream().map(e->{
            DiseaseFactory.GeneDisease gd = new DiseaseFactory.GeneDisease();
            gd.gene = e.gene;
            gd.diseases = e.value();
            return gd;
        }).collect(Collectors.toMap(gd->gd.gene, gd->gd.diseases));
    }
    @Override
    public DiseaseElement that() {
        return this;
    }
    private final static class GeneElement extends HTMLElementBuilder<HTMLDivElement, GeneElement> {
        public static GeneElement build(DiseaseFactory.GeneDisease gene) {
            return new GeneElement(div(), gene);
        }
        private static ColumnString column(String name){
            return ColumnBuilder.string(name).name(name).horizontal("center").vertical("middle");
        }
        private final SheetElement.SheetConfiguration config = new SheetElement.SheetConfiguration()
                .autoColSize(true)
                .autoRowSize(true)
                .rowHeaderWidth(30)
                .rowHeaders(false)
                .manualColumnMove(false)
                .manualColumnResize(true)
                .columns(column("Gene").horizontal("left").build(), column("Disease").horizontal("left").build(), column("Abbreviation").build(), column("Inheritance").build())
                .data(new Data[0])
                .heightAuto().stretchH("all");
        private final String gene;
        private final SheetElement sheet = config.build();
        private final SheetElementSelectableMulti selection = SheetElementSelectableMulti.wrap(sheet);
        public GeneElement(HTMLContainerBuilder<HTMLDivElement> e, DiseaseFactory.GeneDisease gene) {
            super(e.style("margin-top: 0.5em; margin-bottom: 1.5em;"));
            this.gene = gene.gene;
            e.add(label(this.gene)).add(div().style("overflow: hidden;height: 100%; border-top: 1px solid #AAA; border-bottom: 1px solid #AAA;").add(sheet));
            DiseaseApi.disease(this.gene).then(disease->{
                Set<String> previous = Arrays.stream(disease).map(d->d.fullName.trim()).collect(Collectors.toSet());
                Data[] data = Stream.concat(
                        Arrays.stream(disease),
                        Arrays.stream(gene.diseases).filter(d->!previous.contains(d.fullName.trim()))
                ).map(this::map).toArray(Data[]::new);
                if(data.length > 1) config.mergeCells(new MergeCell[] { new MergeCell().row(0).col(0).colspan(1).rowspan(data.length) });
                else if(data.length <= 0) data = new Data[] { Data.create("").put("Gene", this.gene) };
                data[0].select(true);
                sheet.element().style.height = CSSProperties.HeightUnionType.of("auto");
                sheet.values(data);
                return null;
            });
        }
        private Data map(ParameterPanel.Disease disease) {
            Data data = Data.create(disease.fullName);
            data.put("Gene", this.gene).put("Disease", disease.fullName).put("Abbreviation", disease.abbreviation);
            if(disease.inheritance!=null) data.put("Inheritance", Arrays.stream(disease.inheritance).collect(Collectors.joining(", ")));
            return data;
        }
        private ParameterPanel.Disease map(Data data) {
            ParameterPanel.Disease disease = new ParameterPanel.Disease();
            disease.fullName = data.get("Disease");
            disease.abbreviation = data.get("Abbreviation");
            String inheritance = data.get("Inheritance");
            if(inheritance!=null) disease.inheritance = Arrays.stream(inheritance.split(",")).map(String::trim).toArray(String[]::new);
            return disease;
        }
        @Override
        public GeneElement that() {
            return this;
        }
        public ParameterPanel.Disease[] value() {
            return Arrays.stream(selection.selection()).map(this::map).toArray(ParameterPanel.Disease[]::new);
        }
    }
}
