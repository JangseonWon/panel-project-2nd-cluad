package com.gcgenome.lims.client.expand.tso;

import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import com.gcgenome.lims.dto.InterpretationTso;
import elemental2.dom.HTMLDivElement;
import jsinterop.base.Js;
import net.sayaya.ui.HTMLElementBuilder;
import org.jboss.elemento.HTMLContainerBuilder;

import static net.sayaya.ui.TextAreaElement.textBox;
import static org.jboss.elemento.Elements.div;

public class Interpretations extends HTMLElementBuilder<HTMLDivElement, Interpretations> implements InterpretationSubject<HTMLDivElement> {
    public static Interpretations build() {
        return new Interpretations(div());
    }
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    public Interpretations(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.style("margin: 16px;"));
        _this = e;
        Subjects.snvs.subscribe(s->update());
        Subjects.cnvs.subscribe(s->update());
        Subjects.fusions.subscribe(s->update());
    }
    private void update() {
        var snvs = Subjects.snvs.getValue();
        var cnvs = Subjects.cnvs.getValue();
        var fusions = Subjects.fusions.getValue();
        var variants = Subjects.variants().toArray(InterpretationTso.Variant[]::new);
        _this.element().innerHTML = "";
        for(var var: variants) {
            if(var.tier()!= InterpretationTso.Tier.Tier1 && var.tier()!= InterpretationTso.Tier.Tier2) continue;
            String title = "";
            if(var.kind() == InterpretationTso.VariantKind.SNV) title = name((InterpretationTso.Snv)Js.cast(var));
            else if(var.kind() == InterpretationTso.VariantKind.CNV) title = name((InterpretationTso.Cnv)Js.cast(var));
            else if(var.kind() == InterpretationTso.VariantKind.FUSION) title = name((InterpretationTso.Fusion)Js.cast(var));
            var input = textBox().outlined().text(title).css("input").style("width: 100%; margin-bottom: 16px;").required(true);
            _this.add(input);
            input.value(var.interpretation);
            input.onValueChange(evt->{
                var.interpretation = evt.value();
                if(var.kind() == InterpretationTso.VariantKind.SNV) Subjects.snvs.next(snvs);
                else if(var.kind() == InterpretationTso.VariantKind.CNV) Subjects.cnvs.next(cnvs);
                else if(var.kind() == InterpretationTso.VariantKind.FUSION) Subjects.fusions.next(fusions);
            });
        }
    }
    private String name(InterpretationTso.Snv snv) {
        return snv.tier() + " /  " + snv.gene + " / " + snv.hgvsc + " / " + snv.hgvsp + " / " + snv.significance;
    }
    private String name(InterpretationTso.Cnv cnv) {
        return cnv.tier() + " /  " + cnv.gene + " / " + cnv.type + " / " + cnv.copyNumber + " / " + cnv.significance;
    }
    private String name(InterpretationTso.Fusion fusion) {
        return fusion.tier() + " /  " + fusion.gene + " / " + fusion.fusion + " / " + fusion.readCount + " / " + fusion.significance;
    }
    @Override
    public Interpretations that() {
        return this;
    }
}
