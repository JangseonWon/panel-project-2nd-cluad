package com.gcgenome.lims.client.expand.panel.param;

import com.gcgenome.lims.dto.InterpretationPanel;
import com.gcgenome.lims.dto.ParameterPanel;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import lombok.experimental.Delegate;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.ButtonElementText;
import net.sayaya.ui.Dialog;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Map.Entry;

import static org.jboss.elemento.Elements.body;

public class InterpretationParamDialog {
    public static InterpretationParamDialog build(ParameterPanel.Sex sex, List<InterpretationPanel.Variant> variants, InterpretationPanel previous) {
        return new InterpretationParamDialog(sex, variants, previous);
    }
    private DiseaseElement elemDisease = null;
    private HgvsElement elemHgvs = null;
    private InterpretationReservedElement elemInterpretation = null;
    private PatientElement elemSex;
    private SuffixElement elemSuffix =  SuffixElement.build();
    private ButtonElementText btnCancel = ButtonElement.flat().text("Cancel");
    private ButtonElementText btnSubmit = ButtonElement.contain().text("Apply");
    @Delegate private Dialog dialog = Dialog.confirmation("Interpretation parameters", btnCancel, btnSubmit);
    private final InterpretationPanel previous;
    public InterpretationParamDialog(ParameterPanel.Sex sex, List<InterpretationPanel.Variant> variants, InterpretationPanel previous) {
        elemSex = PatientElement.build(sex);
        this.previous = previous;
        dialog.add(elemSex);
        var genes = DiseaseFactory.map(variants.stream().collect(Collectors.toMap(v -> v.gene, v -> v, (existing, replacement) -> existing)).values().stream().toArray(InterpretationPanel.Variant[]::new));
        if(genes.length > 0) {
            elemDisease = DiseaseElement.build(genes);
            dialog.add(elemDisease);
        }
        if (variants.size() > 0) {
            elemHgvs = HgvsElement.build(variants.stream().toArray(InterpretationPanel.Variant[]::new));
            dialog.add(elemHgvs);
        }
        var interpretationReserved = variants.stream().filter(k->k.interpretation!=null && !k.interpretation.isEmpty()).toArray(InterpretationPanel.Variant[]::new);
        if(interpretationReserved.length > 0) {
            elemInterpretation = InterpretationReservedElement.build(interpretationReserved);
            dialog.add(elemInterpretation);
        } else elemInterpretation = null;
        dialog.add(elemSuffix);
        btnCancel.onClick(evt->dialog.close());
        HTMLElement surface = (HTMLElement) elemSuffix.element().parentElement.parentElement;
        surface.style.maxWidth = CSSProperties.MaxWidthUnionType.of("none");
        body().add(dialog);
    }
    public Promise<ParameterPanel> onSubmit() {
        return new Promise<>((resolve, reject)-> {
            btnSubmit.onClick(evt -> {
                ParameterPanel param = new ParameterPanel();
                if (elemDisease != null) param.diseases(elemDisease.value());
                if (elemHgvs != null) {
                    elemHgvs.value().forEach((snv, hgvs) -> {
                        if (hgvs == null) return;
                        Stream.concat(
                                Arrays.stream(previous.variants),
                                        Optional.ofNullable(previous.addendum).map(a -> Arrays.stream(a.variants)).orElseGet(Stream::empty)
                                )
                                .filter(variant -> snv.equals(variant.snv))
                                .forEach(variant -> {
                                    variant.hgvsc = hgvs.hgvsc();
                                    variant.hgvsp = hgvs.hgvsp();
                                });
                    });
                }
                param.sex(elemSex.value())
                     .suffix = elemSuffix.value();
                if(elemInterpretation!=null) for(var interpretationPreserved: elemInterpretation.value().entrySet()) {
                    var variant = interpretationPreserved.getKey();
                    for(var p: previous.variants) {
                        if(!p.snv.equals(variant.snv)) continue;
                        if(interpretationPreserved.getValue()) p.interpretation = interpretationPreserved.getKey().interpretation;
                        else p.interpretation = null;
                    }
                }
                param.previous = previous;
                dialog.close();
                dialog.element().remove();
                resolve.onInvoke(param);
            });
            btnCancel.onClick(evt -> {
                dialog.close();
                dialog.element().remove();
                reject.onInvoke(null);
            });
            dialog.open();
        });
    }
}
