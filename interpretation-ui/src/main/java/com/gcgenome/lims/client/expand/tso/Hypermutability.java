package com.gcgenome.lims.client.expand.tso;

import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import com.gcgenome.lims.dto.InterpretationTso;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.TextFieldElement;
import org.jboss.elemento.HTMLContainerBuilder;

import static org.jboss.elemento.Elements.div;

public class Hypermutability extends HTMLElementBuilder<HTMLDivElement, Hypermutability> implements InterpretationSubject<HTMLDivElement> {
    public static Hypermutability build() {
        return new Hypermutability(div());
    }
    private final TextFieldElement.TextFieldOutlined<String> iptTmb = TextFieldElement.textBox().outlined().text("Tumor Mutation Burden (TMB)").css("input").style("width: 34%;");
    private final TextFieldElement.TextFieldOutlined<String> iptMsi = TextFieldElement.textBox().outlined().text("Microsatellite Instability Status").css("input").style("width: 33%;");
    private final TextFieldElement.TextFieldOutlined<Double> iptMsiScore = TextFieldElement.numberBox().outlined().text("MSI Score").css("input").style("width: 33%;");

    private Hypermutability(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.style("margin: 16px;"));
        e.add(iptTmb).add(iptMsi).add(iptMsiScore);
        Subjects.hypermutability.subscribe(this::update);
        iptTmb.onValueChange(evt->expose());
        iptMsi.onValueChange(evt->expose());
        iptMsiScore.onValueChange(evt->expose());
        iptMsiScore.input().element().setAttribute("step", "0.01");
    }
    private void update(InterpretationTso.Hypermutability dto) {
        if(dto==null) {
            iptTmb.value("");
            iptMsi.value("");
            iptMsiScore.value(0.0);
        } else {
            iptTmb.value(dto.tmb);
            iptMsi.value(dto.msi);
            iptMsiScore.value(dto.msiScore);
        }
    }
    private void expose() {
        var instance = new InterpretationTso.Hypermutability();
        instance.tmb = iptTmb.value();
        instance.msi = iptMsi.value();
        instance.msiScore = iptMsiScore.value();
        Subjects.hypermutability.next(instance);
    }
    @Override
    public Hypermutability that() {
        return this;
    }
}
