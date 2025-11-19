package com.gcgenome.lims.client.expand.panel.param;

import com.gcgenome.lims.dto.InterpretationPanel;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import lombok.experimental.Delegate;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.MergeCell;
import net.sayaya.ui.chart.SheetElement;
import net.sayaya.ui.chart.SheetElementSelectableSingle;
import net.sayaya.ui.chart.column.ColumnBuilder;
import net.sayaya.ui.chart.column.ColumnString;
import net.sayaya.ui.chart.column.ColumnText;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.jboss.elemento.Elements.*;

public class HgvsElement extends HTMLElementBuilder<HTMLDivElement, HgvsElement> {
    private final List<GeneElement> children;
    public static HgvsElement build(InterpretationPanel.Variant[] variants) {
        HTMLContainerBuilder<HTMLDivElement> e = div().style("display: flex; flex-direction: column; color: var(--mdc-theme-text-primary-on-background); width: 800px;");
        Section section = Section.build("HGVS parameter");
        var children = div().style("padding-bottom: 1em;");
        e.add(section).add(children);
        List<GeneElement> geneElements = new LinkedList<>();
        for (var variant : variants) {
            var elem = GeneElement.build(variant);
            children.add(elem);
            geneElements.add(elem);
        }
        return new HgvsElement(e, geneElements);
    }
    private HgvsElement(HTMLContainerBuilder<HTMLDivElement> e, List<GeneElement> children) {
        super(e);
        this.children = children;
    }
    public Map<String, Hgvs> value() {
        return children.stream()
                .map(e -> {
                    Data data = e.value();
                    String snv = data.get("snv");
                    String hgvsc = data.get("hgvsc");
                    String hgvsp = data.get("hgvsp");
                    return new AbstractMap.SimpleEntry<>(snv, new Hgvs(hgvsc, hgvsp));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    @Override
    public HgvsElement that() {
        return this;
    }
    private final static class GeneElement extends HTMLElementBuilder<HTMLDivElement, GeneElement> {
        public static GeneElement build(InterpretationPanel.Variant gene) {
            return new GeneElement(div(), gene);
        }
        private static ColumnString column(String name){
            return ColumnBuilder.string(name).name(name).horizontal("center").vertical("middle");
        }
        private static ColumnText column(String id, String name){
            return ColumnBuilder.text(id, 10, 100).name(name).horizontal("center").vertical("middle");
        }
        private final SheetElement.SheetConfiguration config = new SheetElement.SheetConfiguration()
                .autoColSize(true)
                .autoRowSize(true)
                .rowHeaderWidth(30)
                .rowHeaders(false)
                .manualColumnMove(false)
                .manualColumnResize(true)
                .data(new Data[] {})
                .heightAuto().stretchH("all")
                .columns(column("Gene").readOnly(true).horizontal("left").build(), column("hgvsc", "HGVS.c").readOnly(true).horizontal("left").build(), column("hgvsp", "HGVS.p").readOnly(true).horizontal("left").build());
        private final String gene;
        private final SheetElement sheet = config.build();
        @Delegate private final SheetElementSelectableSingle selection = SheetElementSelectableSingle.wrap(sheet);
        public GeneElement(HTMLContainerBuilder<HTMLDivElement> e, InterpretationPanel.Variant variant) {
            super(e.style("margin-top: 0.5em; margin-bottom: 1.5em;"));
            this.gene = variant.gene;
            e.add(label(this.gene)).add(div().style("overflow: hidden;height: 100%; border-top: 1px solid #AAA; border-bottom: 1px solid #AAA;").add(sheet));
            String[] hgvscParts = Optional.ofNullable(variant.originHgvsc)
                    .map(s -> s.split("[|,]"))
                    .orElse(new String[]{"NM_000000.0:c.?"});
            String[] hgvspParts = Optional.ofNullable(variant.originHgvsp)
                    .map(s -> s.split("[|,]"))
                    .orElse(new String[0]);
            String defaultHgvsp = hgvspParts.length == 1 ? hgvspParts[0] : "p.?";
            Data[] data = IntStream.range(0, hgvscParts.length)
                    .mapToObj(i -> map(variant.snv, variant.gene, hgvscParts[i],
                            i < hgvspParts.length ? hgvspParts[i] : defaultHgvsp))
                    .toArray(Data[]::new);
            config.mergeCells(new MergeCell[]{new MergeCell().row(0).col(0).colspan(1).rowspan(data.length)});
            data[0].select(true);
            sheet.values(data);
            sheet.element().style.height = CSSProperties.HeightUnionType.of("auto");
        }
        private Data map(String snv, String gene, String hgvsc, String hgvsp) {
            String dataId = gene + ":" + hgvsc;
            Data data = Data.create(dataId);
            data.put("snv", snv)
                    .put("Gene", gene)
                    .put("hgvsc", hgvsc)
                    .put("hgvsp", hgvsp);
            return data;
        }
        @Override
        public GeneElement that() {
            return this;
        }

        public Data value() {
            Optional<Data> selected = selection.selection();
            if (selected.isEmpty()) {
                DomGlobal.alert("HGVS 파라미터를 선택해주세요.");
                return null;
            }
            return selected.get();
        }
    }
    public record Hgvs(String hgvsc, String hgvsp) {}
}
