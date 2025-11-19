package com.gcgenome.lims.client.expand.panel.param;

import com.gcgenome.lims.dto.InterpretationPanel;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.CheckBoxElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.TextAreaElement;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.label;

public class InterpretationReservedElement extends HTMLElementBuilder<HTMLDivElement, InterpretationReservedElement> {
    public static InterpretationReservedElement build(InterpretationPanel.Variant... variants) {
        return new InterpretationReservedElement(div(), variants);
    }
    private final List<InterpretationReservedPerVariantElement> children = new LinkedList<>();
    public InterpretationReservedElement(HTMLContainerBuilder<HTMLDivElement> e, InterpretationPanel.Variant... variants) {
        super(e.style("display: flex; flex-direction: column; color: var(--mdc-theme-text-primary-on-background); width: 800px;"));
        Section section = Section.build("Interpretation parameter");
        var children = div().style("padding-bottom: 1em;");
        e.add(section).add(children);
        for(var variant: variants) {
            if(variant.interpretation==null || variant.interpretation.isEmpty()) continue;
            var elem = InterpretationReservedPerVariantElement.build(variant);
            children.add(elem);
            this.children.add(elem);
        }
    }
    public Map<InterpretationPanel.Variant, Boolean> value() {
        return children.stream().collect(Collectors.toMap(child->child.variant, InterpretationReservedPerVariantElement::value));
    }
    @Override
    public InterpretationReservedElement that() {
        return this;
    }
    private final static class InterpretationReservedPerVariantElement extends  HTMLElementBuilder<HTMLDivElement, InterpretationReservedPerVariantElement> {
        public static InterpretationReservedPerVariantElement build(InterpretationPanel.Variant variant) {
            return new InterpretationReservedPerVariantElement(div(), variant);
        }
        private final CheckBoxElement enabled = CheckBoxElement.checkBox(true);
        private final InterpretationPanel.Variant variant;
        private final TextAreaElement<String> value = TextAreaElement.textBox().outlined().text("Interpretation").style("width: 100%; height: 150px; margin-top: 0.3em;");
        public InterpretationReservedPerVariantElement(HTMLContainerBuilder<HTMLDivElement> e, InterpretationPanel.Variant variant) {
            super(e.style("margin-top: 0.5em; margin-bottom: 1.5em;"));
            this.variant = variant;
            e.add(div().add(enabled).add(label(variant.gene + " " + variant.hgvsc))).add(value);
            value.value(variant.interpretation);
            value.onValueChange(evt->this.variant.interpretation = evt.value());
        }
        @Override
        public InterpretationReservedPerVariantElement that() {
            return this;
        }
        public Boolean value() {
            return enabled.value();
        }
    }
}
