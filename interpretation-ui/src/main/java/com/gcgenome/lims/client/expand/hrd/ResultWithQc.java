package com.gcgenome.lims.client.expand.hrd;

import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import com.gcgenome.lims.dto.InterpretationHrd;
import elemental2.dom.HTMLDivElement;
import jsinterop.base.Js;
import net.sayaya.ui.*;
import org.jboss.elemento.HTMLContainerBuilder;

import static com.gcgenome.lims.dto.InterpretationHrd.Status.*;
import static org.jboss.elemento.Elements.div;

public class ResultWithQc extends HTMLElementBuilder<HTMLDivElement, ResultWithQc> implements InterpretationSubject<HTMLDivElement> {
    public static ResultWithQc build() {
        return new ResultWithQc(div());
    }
    private final DropDownElement iptHrd = DropDownElement.outlined(ListElement.singleLineList()
            .add(ListElement.singleLine().label(POSITIVE.label()))
            .add(ListElement.singleLine().label(NEGATIVE.label()))
            .add(ListElement.singleLine().label(FAIL.label()))
            .add(ListElement.singleLine().label("-")))
            .css("input").text("HRD").style("min-width: 150px;");
    private final DropDownElement iptGi	= DropDownElement.outlined(ListElement.singleLineList()
            .add(ListElement.singleLine().label(POSITIVE.label()))
            .add(ListElement.singleLine().label(NEGATIVE.label()))
            .add(ListElement.singleLine().label(FAIL.label()))
            .add(ListElement.singleLine().label("-")))
            .css("input").text("유전체 불안정성").style("min-width: 150px;");
    private final TextFieldElement.TextFieldOutlined<Double> iptGiScore = TextFieldElement.numberBox().outlined().css("input").text("유전체 불안정성 점수").style("min-width: 150px;").required(true);
    private final DropDownElement iptBrca = DropDownElement.outlined(ListElement.singleLineList()
            .add(ListElement.singleLine().label(POSITIVE.label()))
            .add(ListElement.singleLine().label(NEGATIVE.label()))
            .add(ListElement.singleLine().label(FAIL.label()))
            .add(ListElement.singleLine().label("-")))
            .css("input").text("BRCA").style("min-width: 150px;");
    private final DropDownElement iptSnv = DropDownElement.outlined(ListElement.singleLineList()
            .add(ListElement.singleLine().label(PASS.label()))
            .add(ListElement.singleLine().label(NON_PASS.label()))
            .add(ListElement.singleLine().label(FAIL.label()))
            .add(ListElement.singleLine().label("-")))
            .css("input").text("DNA(SNV) QC").style("min-width: 150px;");
    private final DropDownElement iptCnv = DropDownElement.outlined(ListElement.singleLineList()
            .add(ListElement.singleLine().label(PASS.label()))
            .add(ListElement.singleLine().label(NON_PASS.label()))
            .add(ListElement.singleLine().label(FAIL.label()))
            .add(ListElement.singleLine().label("-")))
            .css("input").text("DNA(CNV) QC").style("min-width: 150px;");
    private final DropDownElement iptTumorFraction = DropDownElement.outlined(ListElement.singleLineList()
                    .add(ListElement.singleLine().label(PASS.label()))
                    .add(ListElement.singleLine().label(FAIL.label()))
                    .add(ListElement.singleLine().label("-")))
            .css("input").text("Tumor Fraction").style("min-width: 150px;");
    private final TextAreaElement<String> iptInterpretation = TextAreaElement.textBox().outlined().style("width: -webkit-fill-available; height: 130px; margin: 16px auto;").text("Interpretation");

    private InterpretationHrd value;

    private ResultWithQc(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.style("margin: 16px;"));
        e.add(div().style("margin-top: 10px;").add(iptHrd).add(iptGi).add(iptGiScore).add(iptBrca).add(iptSnv).add(iptCnv).add(iptTumorFraction))
                .add(iptInterpretation);
        Subjects.interpretation.subscribe(obj->{ try {
            InterpretationHrd value = Js.cast(obj);
            update(value);
        } catch(ClassCastException ignore){ this.value = null;}});
        iptHrd.onValueChange(evt->{
            if (value == null || (value.hrd != null && value.hrd.equals(evt.value()))) return;
            value.hrd = evt.value();
            Subjects.interpretation.next(value);
        });
        iptGi.onValueChange(evt->{
            if (value == null || (value.gi != null && value.gi.equals(evt.value()))) return;
            value.gi = evt.value();
            Subjects.interpretation.next(value);
        });
        iptBrca.onValueChange(evt->{
            if (value == null || (value.brca != null && value.brca.equals(evt.value()))) return;
            value.brca = evt.value();
            Subjects.interpretation.next(value);
        });
        iptSnv.onValueChange(evt->{
            if (value == null || (value.snv != null && value.snv.equals(evt.value()))) return;
            value.snv = evt.value();
            Subjects.interpretation.next(value);
        });
        iptCnv.onValueChange(evt->{
            if (value == null || (value.cnv != null && value.cnv.equals(evt.value()))) return;
            value.cnv = evt.value();
            Subjects.interpretation.next(value);
        });
        iptTumorFraction.onValueChange(evt->{
            if (value == null || (value.tumorFraction != null && value.tumorFraction.equals(evt.value()))) return;
            value.tumorFraction = evt.value();
            Subjects.interpretation.next(value);
        });
        iptGiScore.onValueChange(evt->{
            if(value!=null) value.giScore = evt.value();
            Subjects.interpretation.next(value);
        });
        iptInterpretation.onValueChange(evt->{
            if(this.value!=null) value.interpretation = evt.value();
            Subjects.interpretation.next(value);
        });
    }
    private void update(InterpretationHrd value) {
        this.value = value;

        if(value.hrd!=null) iptHrd.select(value.hrd);
        else value.hrd = iptHrd.value();

        if(value.interpretation!=null) iptInterpretation.value(value.interpretation);
        else value.interpretation = iptInterpretation.value();

        if(value.gi!=null) iptGi.select(value.gi);
        else value.gi = iptGi.value();

        if(value.brca!=null) iptBrca.select(value.brca);
        else value.brca = iptBrca.value();

        if(value.snv!=null) iptSnv.select(value.snv);
        else value.snv = iptSnv.value();

        if(value.cnv!=null) iptCnv.select(value.cnv);
        else value.cnv = iptCnv.value();

        if(value.tumorFraction!=null) iptTumorFraction.select(value.tumorFraction);
        else value.tumorFraction = iptTumorFraction.value();

        iptGiScore.value(value.giScore);
    }
    @Override
    public ResultWithQc that() {
        return this;
    }
}
