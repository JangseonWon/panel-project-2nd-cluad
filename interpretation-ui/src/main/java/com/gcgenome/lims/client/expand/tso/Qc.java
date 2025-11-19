package com.gcgenome.lims.client.expand.tso;

import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import com.gcgenome.lims.dto.InterpretationTso;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.*;
import org.jboss.elemento.HTMLContainerBuilder;

import static net.sayaya.ui.TextAreaElement.textBox;
import static org.jboss.elemento.Elements.div;

public class Qc extends HTMLElementBuilder<HTMLDivElement, Qc> implements InterpretationSubject<HTMLDivElement> {
    public static Qc build() {
        return new Qc(div());
    }
    private final TextFieldElement.TextFieldOutlined<Double> iptMedianExonCoverage = TextFieldElement.numberBox().outlined()
            .text("Median exon coverage").css("input").style("width: 300px;").required(true);
    private final TextFieldElement.TextFieldOutlined<Double> iptPctExonOver1000 = TextFieldElement.numberBox().outlined()
            .text("Pct exon > 1000X").css("input").style("width: 300px;").required(true);
    private final TextFieldElement.TextFieldOutlined<Double> iptPctExonOver100 = TextFieldElement.numberBox().outlined()
            .text("Pct exon > 100X").css("input").style("width: 300px;").required(true);
    private final DropDownElement iptSnvQc = DropDownElement.outlined(ListElement.singleLineList()
                    .add(ListElement.singleLine().label(""))
                    .add(ListElement.singleLine().label("PASS"))
                    .add(ListElement.singleLine().label("FAIL"))
                    .add(ListElement.singleLine().label("SUBOPTIMAL")))
            .text("SNV & TMB QC").css("input").style("width: 200px;");
    private final TextFieldElement.TextFieldOutlined<Double> iptMad = TextFieldElement.numberBox().outlined()
            .text("MAD").css("input").style("width: 300px;").required(true);
    private final TextFieldElement.TextFieldOutlined<Double> iptMbc = TextFieldElement.numberBox().outlined()
            .text("Median bin count").css("input").style("width: 300px;").required(true);
    private final DropDownElement iptCnvQc = DropDownElement.outlined(ListElement.singleLineList()
                    .add(ListElement.singleLine().label(""))
                    .add(ListElement.singleLine().label("PASS"))
                    .add(ListElement.singleLine().label("FAIL"))
                    .add(ListElement.singleLine().label("SUBOPTIMAL")))
            .text("CNV QC").css("input").style("width: 200px;");
    private final TextFieldElement.TextFieldOutlined<Double> iptUsableMsi = TextFieldElement.numberBox().outlined()
            .text("Usable MSI Sites").css("input").style("width: 300px;").required(true);
    private final DropDownElement iptMsiQc = DropDownElement.outlined(ListElement.singleLineList()
                    .add(ListElement.singleLine().label(""))
                    .add(ListElement.singleLine().label("PASS"))
                    .add(ListElement.singleLine().label("FAIL"))
                    .add(ListElement.singleLine().label("SUBOPTIMAL")))
            .text("MSI QC").css("input").style("width: 200px;");
    private final TextFieldElement.TextFieldOutlined<Double> iptOnTargetReads = TextFieldElement.numberBox().outlined()
            .text("Total on target reads").css("input").style("width: 300px;").required(true);
    private final TextFieldElement.TextFieldOutlined<Double> iptMedianCvOver500x = TextFieldElement.numberBox().outlined()
            .text("Median CV genes with > 500X").css("input").style("width: 300px;").required(true);
    private final DropDownElement iptRnaQc = DropDownElement.outlined(ListElement.singleLineList()
                    .add(ListElement.singleLine().label(""))
                    .add(ListElement.singleLine().label("PASS"))
                    .add(ListElement.singleLine().label("FAIL"))
                    .add(ListElement.singleLine().label("SUBOPTIMAL")))
            .text("RNA QC").css("input").style("width: 200px;");
    private final TextFieldElement.TextFieldOutlined<Double> iptMsaf = TextFieldElement.numberBox().outlined()
            .text("MSAF(%)").css("input").style("width: 300px;").required(true);
    private final TextFieldElement.TextFieldOutlined<Double> iptPurity = TextFieldElement.numberBox().outlined()
            .text("Tumor Purity(%)").css("input").style("width: 300px;").required(true);
    private final TextAreaElement<String> iptInterpretation = textBox().outlined().text("Interpretation").css("input").style("width: 100%;");
    private InterpretationTso.Qc instance;

    private Qc(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.style("margin: 16px;"));
        var snvs = div().style("margin-bottom: 0.5em;");
        var cnvs = div().style("margin-bottom: 0.5em;").add(iptMad).add(iptMbc).add(iptCnvQc);
        var msis = div().style("margin-bottom: 0.5em;").add(iptUsableMsi).add(iptMsiQc);
        var rnas = div().style("margin-bottom: 0.5em;").add(iptOnTargetReads).add(iptMedianCvOver500x).add(iptRnaQc);
        com.gcgenome.lims.client.Subjects.code.subscribe(code->{
            if("N198".equalsIgnoreCase(code) || "ON198".equalsIgnoreCase(code)) {
                e.add(snvs.add(iptMedianExonCoverage).add(iptPctExonOver1000).add(iptSnvQc))
                        .add(cnvs)
                        .add(div().style("margin-bottom: 0.5em;").add(iptMsaf))
                        .add(div().add(iptInterpretation));
            } else if("N199".equalsIgnoreCase(code) || "ON199".equalsIgnoreCase(code) || "G0022402".equalsIgnoreCase(code)) {
                e.add(snvs.add(iptPctExonOver100).add(iptSnvQc))
                        .add(cnvs)
                        .add(msis)
                        .add(rnas)
                        .add(div().style("margin-bottom: 0.5em;").add(iptPurity))
                        .add(div().add(iptInterpretation));
            } else if("N200".equalsIgnoreCase(code) || "ON200".equalsIgnoreCase(code)) {
                e.add(snvs.add(iptPctExonOver100).add(iptSnvQc))
                        .add(cnvs)
                        .add(msis)
                        .add(div().style("margin-bottom: 0.5em;").add(iptPurity))
                        .add(div().add(iptInterpretation));
            }
        });
        Subjects.qc.subscribe(this::update);
        iptMedianExonCoverage.onValueChange(evt -> {
            if (instance != null) instance.medianExonCoverage = evt.value();
            Subjects.qc.next(instance);
        });
        iptPctExonOver1000.onValueChange(evt -> {
            if (instance != null) instance.pctExonOver1000x = evt.value();
            Subjects.qc.next(instance);
        });
        iptPctExonOver100.onValueChange(evt->{
            if (instance != null) instance.pctExonOver100x = evt.value();
            Subjects.qc.next(instance);
        });
        iptSnvQc.onValueChange(evt->{
            if (instance == null || (instance.snvQc != null && instance.snvQc.equals(evt.value()))) return;
            instance.snvQc = evt.value();
            Subjects.qc.next(instance);
        });
        iptMad.onValueChange(evt->{
            if (instance != null) instance.mad = evt.value();
            Subjects.qc.next(instance);
        });
        iptMbc.onValueChange(evt->{
            if (instance != null) instance.mbc = evt.value();
            Subjects.qc.next(instance);
        });
        iptCnvQc.onValueChange(evt->{
            if (instance == null || (instance.cnvQc != null && instance.cnvQc.equals(evt.value()))) return;
            instance.cnvQc = evt.value();
            Subjects.qc.next(instance);
        });
        iptUsableMsi.onValueChange(evt->{
            if (instance != null) instance.usableMsi = evt.value();
            Subjects.qc.next(instance);
        });
        iptMsiQc.onValueChange(evt->{
            if (instance == null || (instance.msiQc != null && instance.msiQc.equals(evt.value()))) return;
            instance.msiQc = evt.value();
            Subjects.qc.next(instance);
        });
        iptOnTargetReads.onValueChange(evt->{
            if (instance != null) instance.onTargetReads = evt.value();
            Subjects.qc.next(instance);
        });
        iptMedianCvOver500x.onValueChange(evt->{
            if (instance != null) instance.medianCvOver500x = evt.value();
            Subjects.qc.next(instance);
        });
        iptRnaQc.onValueChange(evt->{
            if (instance == null || (instance.rnaQc != null && instance.rnaQc.equals(evt.value()))) return;
            instance.rnaQc = evt.value();
            Subjects.qc.next(instance);
        });
        iptMsaf.onValueChange(evt->{
            if (instance != null) instance.msaf = evt.value();
            Subjects.qc.next(instance);
        });
        iptPurity.onValueChange(evt->{
            if (instance != null) instance.purity = evt.value();
            Subjects.qc.next(instance);
        });
        iptInterpretation.onValueChange(evt->{
            if (instance != null) instance.interpretation = evt.value();
            Subjects.qc.next(instance);
        });
        iptPctExonOver1000.input().element().setAttribute("step", "0.01");
        iptPctExonOver100.input().element().setAttribute("step", "0.01");
        iptMad.input().element().setAttribute("step", "0.001");
        iptMbc.input().element().setAttribute("step", "0.1");
        iptMedianCvOver500x.input().element().setAttribute("step", "0.01");
        iptMsaf.input().element().setAttribute("step", "0.01");
        iptPurity.input().element().setAttribute("step", "0.1");
    }
    private void update(InterpretationTso.Qc dto) {
        this.instance = dto;
        if(dto==null) {
            iptMedianExonCoverage.value(null);
            iptPctExonOver1000.value(null);
            iptPctExonOver100.value(null);
            iptSnvQc.select(0);
            iptMad.value(null);
            iptMbc.value(null);
            iptCnvQc.select(0);
            iptUsableMsi.value(null);
            iptMsiQc.select(0);
            iptOnTargetReads.value(null);
            iptMedianCvOver500x.value(null);
            iptRnaQc.select(0);
            iptMsaf.value(null);
            iptPurity.value(null);
            iptInterpretation.value("");
        } else {
            iptMedianExonCoverage.value(dto.medianExonCoverage);
            iptPctExonOver1000.value(dto.pctExonOver1000x);
            iptPctExonOver100.value(dto.pctExonOver100x);
            iptSnvQc.select(dto.snvQc);
            iptMad.value(dto.mad);
            iptMbc.value(dto.mbc);
            iptCnvQc.select(dto.cnvQc);
            iptUsableMsi.value(dto.usableMsi);
            iptMsiQc.select(dto.msiQc);
            iptOnTargetReads.value(dto.onTargetReads);
            iptMedianCvOver500x.value(dto.medianCvOver500x);
            iptRnaQc.select(dto.rnaQc);
            iptMsaf.value(dto.msaf);
            iptPurity.value(dto.purity);
            iptInterpretation.value(dto.interpretation);
        }
    }
    @Override
    public Qc that() {
        return this;
    }
}
