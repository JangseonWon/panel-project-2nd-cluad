package com.gcgenome.lims.client.expand.hrd;

import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import com.gcgenome.lims.dto.InterpretationHrd;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.HTMLDivElement;
import jsinterop.base.Js;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.TextAreaElement;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.jboss.elemento.Elements.div;

public class TierWithInterpretation extends HTMLElementBuilder<HTMLDivElement, TierWithInterpretation> implements InterpretationSubject<HTMLDivElement> {
    public static TierWithInterpretation build(InterpretationHrd.Tier tier) {
        return new TierWithInterpretation(div(), tier);
    }
    private final InterpretationHrd.Tier tier;
    private final Tier table;
    private final HTMLContainerBuilder<HTMLDivElement> controller = div();
    private final TextAreaElement<String> iptInterpretation = TextAreaElement.textBox().outlined().style("width: -webkit-fill-available; height: 250px;").text("Interpretation");
    private final ButtonElement btnToUpward = ButtonElement.outline().css("button").before(com.gcgenome.lims.ui.IconElement.icon(com.gcgenome.lims.ui.IconElement.Type.Solid, "fa-angle-up")).text("Up").enabled(false);
    private final ButtonElement btnToDownward = ButtonElement.outline().css("button").before(com.gcgenome.lims.ui.IconElement.icon(IconElement.Type.Solid, "fa-angle-down")).text("Down").enabled(false);
    private final ButtonElement btnRemVariant = ButtonElement.outline().css("button").before(net.sayaya.ui.IconElement.icon("remove_circle")).text("Remove Variant").enabled(false);

    private InterpretationHrd value;
    private TierWithInterpretation(HTMLContainerBuilder<HTMLDivElement> e, InterpretationHrd.Tier tier) {
        super(e);
        this.tier = tier;
        this.table = Tier.build(tier);
        e.add(this.table).add(controller.style("margin-bottom: 16px;text-align: right;").add(btnToUpward).add(btnToDownward).add(btnRemVariant))
         .add(iptInterpretation);

        table.onSelectionChange(evt->{
            boolean enabled = evt.selection()!=null && evt.selection().length > 0;
            btnToUpward.enabled(enabled);
            btnToDownward.enabled(enabled);
            btnRemVariant.enabled(enabled);
        });
        btnToUpward.onClick(evt->table.upward());
        btnToDownward.onClick(evt->table.downward());
        btnRemVariant.onClick(evt->table.remove());

        Subjects.interpretation.subscribe(obj->{ try {
            InterpretationHrd value = Js.cast(obj);
            update(value);
        } catch(ClassCastException ignore){ this.value = null; }});
        iptInterpretation.onValueChange(evt->{
            if(this.value!=null) update(evt.value());
            Subjects.interpretation.next(value);
        });
    }

    private void update(InterpretationHrd value) {
        this.value = value;
        if(value==null || value.results==null) {
            var result = new InterpretationHrd.InterpretationTier();
            result.tier = tier.capitalizeFirstLetterOfNumericTiers();
            result.interpretation = "";
            var tmp = Arrays.stream(value.results).collect(Collectors.toList());
            tmp.add(result);
            value.results = tmp.stream().toArray(InterpretationHrd.InterpretationTier[]::new);
            iptInterpretation.value("");
        }
        var hasResult = getResult(value);
        if(hasResult.isPresent()) {
            if (hasResult.get().interpretation != null) {
                iptInterpretation.value(hasResult.get().interpretation);
            } else {
                hasResult.get().interpretation = "";
                iptInterpretation.value("");
            }
        }
        controller.element().style.display = null;
    }

    private void update(String text) {
        var hasResult = getResult(value);
        if(hasResult.isEmpty()) {
            var result = new InterpretationHrd.InterpretationTier();
            result.tier = tier.capitalizeFirstLetterOfNumericTiers();
            var tmp = Arrays.stream(value.results).collect(Collectors.toList());
            tmp.add(result);
            value.results = tmp.stream().toArray(InterpretationHrd.InterpretationTier[]::new);
            hasResult = Optional.of(result);
        }
        hasResult.get().interpretation = text;
    }

    private Optional<InterpretationHrd.InterpretationTier> getResult(InterpretationHrd value) {
        return Arrays.stream(value.results).filter(r -> InterpretationHrd.Tier.equalsIgnoreCaseRemoveWhitespace(tier, r.tier)).findAny();
    }
    
    @Override
    public TierWithInterpretation that() {
        return this;
    }
}
