package com.gcgenome.lims.client.interfaces.work;

import com.gcgenome.lims.client.usecase.work.ChartData;
import com.google.gwt.i18n.client.NumberFormat;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.SheetElement;
import net.sayaya.ui.chart.column.ColumnBuilder;
import net.sayaya.ui.chart.column.ColumnNumber;
import net.sayaya.ui.chart.column.ColumnString;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.HTMLElementBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.jboss.elemento.Elements.div;

@Singleton
public class WorkGridElement extends HTMLElementBuilder<HTMLDivElement> {
    private static ColumnString string(String name){
        return ColumnBuilder.string(name).name(name).readOnly(true).horizontal("center");
    }
    private static ColumnNumber number(String name){
        return ColumnBuilder.number(name).name(name).readOnly(true).horizontal("center");
    }
    private final static String INPUT_BG_COLOR = "#F2F0A1";
    @Inject WorkGridElement(ChartData subject) {
        this(div(), subject);
    }
    private WorkGridElement(HTMLContainerBuilder<HTMLDivElement> e, ChartData subject) {
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
                        number("#").width(50).build(),
                        string("Type").width(70).build(),
                        ColumnBuilder.link("의뢰번호", data->"#"+data.idx()).name("의뢰번호").readOnly(true).horizontal("center")
                                .onClick(data-> DomGlobal.window.open("../sample.html#"+data.get("의뢰번호")))
                                .font("Nanum Gothic Coding").width(120).build(),
                        ColumnBuilder.link("Serial", data->"#"+data.idx()).name("Serial").readOnly(true).horizontal("center")
                                /*.onClick(this::openWorkSerialGenerateDialog)*/.font("Nanum Gothic Coding").width(90).build(),
                        number("나이").format(NumberFormat.getFormat("0")).width(50).build(),
                        string("성별").width(50).build(),
                        string("수진자명").horizontal("left").width(120).build(),
                        string("STB").font("Nanum Gothic Coding").width(50).build(),
                        string("검사명").horizontal("left").width(190).build(),
                        string("의뢰기관").horizontal("left").width(200).build(),
                        string("지놈예정일").width(80).build(),
                        string("MRN").horizontal("left").width(80).build(),
                        string("검체종류").width(75).build(),
                        string("i7 시퀀싱 인덱스").font("Nanum Gothic Coding").readOnly(false).colorBackground(INPUT_BG_COLOR).width(80).build(),
                        string("i5 시퀀싱 인덱스").font("Nanum Gothic Coding").readOnly(false).colorBackground(INPUT_BG_COLOR).width(80).build(),
                        string("i7 인덱스 시퀀스").font("Nanum Gothic Coding").width(80).build(),
                        string("i5 인덱스 시퀀스").font("Nanum Gothic Coding").width(80).build(),
                        string("시퀀싱 파일명").font("Nanum Gothic Coding").width(190).build()
                ).stretchH("all")
                .build();
        e.add(sheet.style("height: fit-content;border-top: 1px solid #AAA; border-bottom: 1px solid #AAA;"));
        subject.debounceTime(100).subscribe(sheet::values);
    }
}
