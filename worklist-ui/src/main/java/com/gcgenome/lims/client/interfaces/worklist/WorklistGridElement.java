package com.gcgenome.lims.client.interfaces.worklist;

import com.gcgenome.lims.client.Router;
import com.gcgenome.lims.client.usecase.worklist.ChartData;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.SheetElement;
import net.sayaya.ui.chart.column.ColumnBuilder;
import net.sayaya.ui.chart.column.ColumnString;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.HTMLElementBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.jboss.elemento.Elements.div;

@Singleton
public class WorklistGridElement extends HTMLElementBuilder<HTMLDivElement> {
    private static ColumnString column(String name){
        return ColumnBuilder.string(name).name(name).readOnly(true).horizontal("center");
    }
    @Inject WorklistGridElement(ChartData subject) {
        this(div(), subject);
    }
    private WorklistGridElement(HTMLContainerBuilder<HTMLDivElement> e, ChartData subject) {
        super(e.element());
        var sheet = SheetElement.builder()
                .autoColSize(false)
                .autoRowSize(false)
                .rowHeaders(false)
                .renderAllRows(true)
                .viewportColumnRenderingOffset(99999.0)
                .manualColumnMove(true)
                .manualColumnResize(true)
                .data(new Data[]{})
                .columns(
                        column("생성일").build(),
                        column("Title").horizontal("left").build(),
                        column("Created by").build(),
                        ColumnBuilder.link("검체", data->"#"+data.idx()).name("검체").readOnly(true).horizontal("center").font("Nanum Gothic Coding")
                                .onClick(data-> Router.location(data.idx(), true)).build(),
                        column("Status").build(),
                        column("비고").build()
                ).stretchH("all")
                .build();
        e.add(sheet.style("height: fit-content;border-top: 1px solid #AAA; border-bottom: 1px solid #AAA;"));
        subject.debounceTime(100).subscribe(sheet::values);
    }
}
