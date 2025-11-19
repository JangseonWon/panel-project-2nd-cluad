package com.gcgenome.lims.client.expand.somatic;

import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import com.gcgenome.lims.dto.InterpretationSomatic;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.HTMLDivElement;
import jsinterop.base.Js;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.ButtonElementText;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.TextAreaElement;
import net.sayaya.ui.chart.Data;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.jboss.elemento.Elements.div;

public class TierWithInterpretation extends HTMLElementBuilder<HTMLDivElement, TierWithInterpretation> implements InterpretationSubject<HTMLDivElement> {
    public static TierWithInterpretation build(InterpretationSomatic.Tier tier) {
        return new TierWithInterpretation(div(), tier);
    }
    private final InterpretationSomatic.Tier tier;
    private final Tier table;
    private final HTMLContainerBuilder<HTMLDivElement> controller = div();
    private final ButtonElementText btnSortByVaf = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-sort-numeric-down-alt")).text("Sort by VAF").enabled(true);
    private final ButtonElement btnToUpward = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-angle-up")).text("Up").enabled(false);
    private final ButtonElement btnToDownward = ButtonElement.outline().css("button").before(IconElement.icon(IconElement.Type.Solid, "fa-angle-down")).text("Down").enabled(false);
    private final ButtonElement btnAddVariant = ButtonElement.outline().css("button").before(net.sayaya.ui.IconElement.icon("plus_one")).text("Append Variant");
    private final ButtonElement btnAdd5Variant = ButtonElement.outline().css("button").text("Append 5 Variant");
    private final ButtonElement btnRemVariant = ButtonElement.outline().css("button").before(net.sayaya.ui.IconElement.icon("remove_circle")).text("Remove Variant");
    private final TextAreaElement<String> iptInterpretation = TextAreaElement.textBox().outlined().style("width: -webkit-fill-available; height: 250px;").text("Interpretation");
    private InterpretationSomatic value;
    private TierWithInterpretation(HTMLContainerBuilder<HTMLDivElement> e, InterpretationSomatic.Tier tier) {
        super(e);
        this.tier = tier;
        this.table = Tier.build(tier);
        e.add(this.table)
         .add(controller.style("margin-top: 5px;text-align: right; margin-bottom: 15px;").add(btnAddVariant).add(btnAdd5Variant).add(btnRemVariant).add(btnSortByVaf).add(btnToUpward).add(btnToDownward))
         .add(iptInterpretation);

        table.onSelectionChange(evt->{
            boolean enabled = evt.selection()!=null && evt.selection().length > 0;
            btnSortByVaf.enabled(!enabled);
            btnToUpward.enabled(enabled);
            btnToDownward.enabled(enabled);
            btnAddVariant.enabled(!enabled);
            btnAdd5Variant.enabled(!enabled);
            btnRemVariant.enabled(!enabled);
        });
        btnSortByVaf.onClick(evt->table.sortBy(
                Comparator.comparing(s->(s.vaf!=null)?-(s.vaf):null, Comparator.nullsLast(Comparator.naturalOrder())),
                Comparator.comparing(s->(s.get("vaf")!=null)?-Double.parseDouble(s.get("vaf")):null, Comparator.nullsLast(Comparator.naturalOrder()))
        ));
        btnToUpward.onClick(evt->table.upward());
        btnToDownward.onClick(evt->table.downward());
        btnAddVariant.onClick(evt->table.append());
        btnAdd5Variant.onClick(evt-> { for(int i = 0; i < 5; ++i) table.append(); });
        btnRemVariant.onClick(evt->table.trim());

        Subjects.interpretation.subscribe(obj->{ try {
            InterpretationSomatic value = Js.cast(obj);
            update(value);
        } catch(ClassCastException ignore){ this.value = null; }});
        iptInterpretation.onValueChange(evt->{
            if(this.value!=null) update(evt.value());
            Subjects.interpretation.next(value);
        });
    }


    private void update(InterpretationSomatic value) {
        this.value = value;
        if(value==null || value.results==null) return;
        var hasResult = Arrays.stream(value.results).filter(result->result.tier()== tier).findAny();
        if(hasResult.isPresent()) {
            iptInterpretation.value(hasResult.get().interpretation);
            controller.element().style.display = null;
        } else {
            iptInterpretation.value("");
            controller.element().style.display = "none";
        }
    }
    private void update(String text) {
        var hasResult = Arrays.stream(value.results).filter(result->result.tier()== tier).findAny();
        if(hasResult.isEmpty()) {
            var result = new InterpretationSomatic.Result().tier(tier);
            var tmp = Arrays.stream(value.results).collect(Collectors.toList());
            tmp.add(result);
            value.results = tmp.stream().sorted(Comparator.comparing(e->e.tier().ordinal())).toArray(InterpretationSomatic.Result[]::new);
            hasResult = Optional.of(result);
        }
        hasResult.get().interpretation = text;
    }
    @Override
    public TierWithInterpretation that() {
        return this;
    }
}
