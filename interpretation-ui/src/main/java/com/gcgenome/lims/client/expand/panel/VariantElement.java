package com.gcgenome.lims.client.expand.panel;

import com.gcgenome.lims.dto.InterpretationPanel;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.event.HasValueChangeHandlers;
import org.gwtproject.event.shared.HandlerRegistration;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jboss.elemento.Elements.div;

public class VariantElement extends HTMLElementBuilder<HTMLDivElement, VariantElement> implements ResultWriter<HTMLDivElement>, HasValueChangeHandlers<Data[]> {
    public static VariantElement build() {
        return new VariantElement(div());
    }
    private final SnvTableElement variants = SnvTableElement.build().style("margin-right: 0px; margin-bottom: 5px; height: fit-content;");
    private final ButtonElement btnToUpward = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-angle-up")).text("Up").enabled(false);
    private final ButtonElement btnToDownward = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-angle-down")).text("Down").enabled(false);
    private final HTMLContainerBuilder<HTMLDivElement> controller = div().style("margin-bottom: 16px; text-align: right;").add(btnToUpward).add(btnToDownward);
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private VariantElement(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.style("transition: all 300ms ease 0s;"));
        _this = e;
        layout();
        variants.onSelectionChange(evt->{
            boolean enabled = evt.selection() != null && evt.selection().length > 0;
            btnToUpward.enabled(enabled);
            btnToDownward.enabled(enabled);
        });
        btnToUpward.onClick(evt->upward());
        btnToDownward.onClick(evt->downward());
    }
    private void layout() {
        _this.add(div().style("margin-left: 15px; padding-right: 15px;").add(variants).add(controller));
    }
    void append(Data... values) {
        variants.append(values);
    }
    public void link(IconElement icon, String label, VariantElement other) {
        ButtonElement btn = ButtonElement.outline().css("button").before(icon).text(label).enabled(false);
        controller.add(btn);
        variants.onSelectionChange(evt->btn.enabled(evt.selection() != null && evt.selection().length > 0));
        btn.onClick(evt->jump(other));
    }
    private void upward() {
        Set<String> selections = Arrays.stream(variants.selection()).map(Data::idx).collect(Collectors.toSet());
        Data[] all = variants.value();
        for(int i = 1; i < all.length; ++i) {
            Data v = all[i];
            String idx = v.idx();
            if(selections.contains(idx)) {
                all[i] = all[i-1];
                all[i-1] = v;
            }
        }
        variants.update(all);
    }
    private void downward() {
        Set<String> selections = Arrays.stream(variants.selection()).map(Data::idx).collect(Collectors.toSet());
        Data[] all = variants.value();
        for(int i = all.length-2; i >= 0; --i) {
            Data v = all[i];
            String idx = v.idx();
            if(selections.contains(idx)) {
                all[i] = all[i+1];
                all[i+1] = v;
            }
        }
        variants.update(all);
    }
    private void jump(VariantElement other) {
        Data[] selection = variants.selection();
        other.append(selection);
        variants.removes(selection);
    }
    @Override
    public void update(InterpretationPanel map) {
        variants.update(map);
    }
    @Override
    public InterpretationPanel append(InterpretationPanel map) {
        variants.append(map);
        return map;
    }
    @Override
    public Data[] value() {
        return variants.value();
    }
    @Override
    public HandlerRegistration onValueChange(ValueChangeEventListener<Data[]> valueChangeEventListener) {
        return variants.onValueChange(valueChangeEventListener);
    }
    @Override
    public VariantElement that() {
        return this;
    }
}
