package com.gcgenome.lims.client.expand.panel.param;

import com.gcgenome.lims.dto.ParameterPanel;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.DropDownElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.ListElement;
import org.jboss.elemento.HTMLContainerBuilder;

import static org.jboss.elemento.Elements.div;

public class PatientElement extends HTMLElementBuilder<HTMLDivElement, PatientElement> {
    public static PatientElement build(ParameterPanel.Sex sex) {
        return new PatientElement(div(), sex);
    }
    private final DropDownElement iptSex = DropDownElement.outlined(ListElement.singleLineList()
            .add(ListElement.singleLine().label("Male"))
            .add(ListElement.singleLine().label("Female"))).text("Sex");
    public PatientElement(HTMLContainerBuilder<HTMLDivElement> e, ParameterPanel.Sex sex) {
        super(e.style("display: flex; flex-direction: column; color: var(--mdc-theme-text-primary-on-background); width: 800px; padding-bottom: 1em;"));
        Section section = Section.build("Patient parameter");
        e.add(section).add(iptSex);
        if(sex == ParameterPanel.Sex.M) iptSex.select("Male");
        else if(sex == ParameterPanel.Sex.F) iptSex.select("Female");
    }
    public ParameterPanel.Sex value() {
        String value = iptSex.value();
        if("Male".equalsIgnoreCase(value)) return ParameterPanel.Sex.M;
        else if("Female".equalsIgnoreCase(value)) return ParameterPanel.Sex.F;
        else return null;
    }
    @Override
    public PatientElement that() {
        return this;
    }
}
