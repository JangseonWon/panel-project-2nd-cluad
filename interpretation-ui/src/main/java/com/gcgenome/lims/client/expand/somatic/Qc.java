package com.gcgenome.lims.client.expand.somatic;

import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import com.gcgenome.lims.dto.InterpretationSomatic;
import elemental2.dom.HTMLDivElement;
import jsinterop.base.Js;
import net.sayaya.ui.DropDownElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.ListElement;
import net.sayaya.ui.TextFieldElement;
import org.jboss.elemento.HTMLContainerBuilder;

import static org.jboss.elemento.Elements.div;

public class Qc extends HTMLElementBuilder<HTMLDivElement, Qc> implements InterpretationSubject<HTMLDivElement> {
    public static Qc build() {
        return new Qc(div());
    }
    private final DropDownElement iptQcDna				= DropDownElement.outlined(ListElement.singleLineList().add(ListElement.singleLine().label("Pass")).add(ListElement.singleLine().label("Non-Pass"))).css("input").style("width: 140px;").text("Sample(DNA) QC　").select(0);
    private final DropDownElement iptQcLibrary			= DropDownElement.outlined(ListElement.singleLineList().add(ListElement.singleLine().label("Pass")).add(ListElement.singleLine().label("Non-Pass"))).css("input").style("width: 140px;").text("Library QC　").select(0);
    private final DropDownElement iptQcSequencing		= DropDownElement.outlined(ListElement.singleLineList().add(ListElement.singleLine().label("Pass")).add(ListElement.singleLine().label("Non-Pass"))).css("input").style("width: 140px;").text("Sequencing QC　").select(0);
    private final TextFieldElement.TextFieldOutlined<Double> iptDepth = TextFieldElement.numberBox().outlined().css("input").text("Mean depth(X))");
    private final TextFieldElement.TextFieldOutlined<Double> iptCoverage = TextFieldElement.numberBox().outlined().css("input").text("≥ 250X");
    private InterpretationSomatic value;
    private Qc(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.style("margin: 16px;"));
        e.add(iptQcDna).add(iptQcLibrary).add(iptQcSequencing).add(iptDepth).add(iptCoverage);
        iptCoverage.input().element().setAttribute("step", "0.01");
        Subjects.interpretation.subscribe(obj->{ try {
            InterpretationSomatic value = Js.cast(obj);
            update(value);
        } catch(ClassCastException ignore){ this.value = null; }});
        iptQcDna.onValueChange(evt->{
            if(value == null || (value.qcDna != null && value.qcDna.equals(evt.value()))) return;
            value.qcDna = evt.value();
            Subjects.interpretation.next(value);
        });
        iptQcLibrary.onValueChange(evt->{
            if(value == null || (value.qcLibrary != null && value.qcLibrary.equals(evt.value()))) return;
            value.qcLibrary = evt.value();
            Subjects.interpretation.next(value);
        });
        iptQcSequencing.onValueChange(evt->{
            if(value == null || (value.qcSequencing != null && value.qcSequencing.equals(evt.value()))) return;
            value.qcSequencing = evt.value();
            Subjects.interpretation.next(value);
        });
        iptDepth.onValueChange(evt->{
            if(value!=null) value.meanDepth = String.valueOf(evt.value());
            Subjects.interpretation.next(value);
        });
        iptCoverage.onValueChange(evt->{
            if(value!=null) value.coverage = String.valueOf(evt.value());
            Subjects.interpretation.next(value);
        });
    }
    private void update(InterpretationSomatic value) {
        this.value = value;
        if(value.qcDna!=null) iptQcDna.select(value.qcDna);
        else value.qcDna = iptQcDna.value();
        if(value.qcLibrary!=null) iptQcLibrary.select(value.qcLibrary);
        else value.qcLibrary = iptQcLibrary.value();
        if(value.qcSequencing!=null) iptQcSequencing.select(value.qcSequencing);
        else value.qcSequencing = iptQcSequencing.value();
        if(value.meanDepth!=null) iptDepth.value(Double.valueOf(value.meanDepth));
        else iptDepth.value(null);
        if(value.coverage!=null) iptCoverage.value(Double.valueOf(value.coverage));
        else iptCoverage.value(null);
    }
    @Override
    public Qc that() {
        return this;
    }
}