package com.gcgenome.lims.client.expand;

import com.gcgenome.lims.client.expand.panel.*;
import com.gcgenome.lims.dto.InterpretationPanel;
import com.gcgenome.lims.test.Interpretable;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.HTMLDivElement;
import elemental2.promise.Promise;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.ButtonElementToggle;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.jboss.elemento.Elements.div;

/*
 * Panel과 같으나 Depth, Coverage가 없다.
 * MLPA 가능
 */
public class SingleGenePanelExpandElement extends AbstractPanelExpandElement<SingleGenePanelExpandElement> {
    public static SingleGenePanelExpandElement build(String id, long sample, JsPropertyMap<?> service) {
        VariantElement variantCore = VariantElement.build();
        VariantElement variantAddendum = VariantElement.build();
        ButtonElementToggle btnAddendum = ButtonElement.toggle().css("button").before(IconElement.icon("post_add")).text("Addendum");
        variantCore.link(IconElement.icon(IconElement.Type.Solid, "fa-angle-double-down"), "Addendum", variantAddendum);
        variantAddendum.link(IconElement.icon(IconElement.Type.Solid, "fa-angle-double-up"), "Shift to Core", variantCore);
        variantAddendum.onValueChange(evt->btnAddendum.value(evt.value()!=null && evt.value().length > 0));
        Section elemReport = Section.build(IconElement.icon(IconElement.Type.Light, "fa-file-prescription"), "CORE",
                SummaryElement.build(),
                variantCore,
                AbbreviationElement.build(),
                InterpretationElement.build());
        Section elemAddendum = Section.proxy(IconElement.icon(IconElement.Type.Light, "fa-file-prescription"), "ADDENDUM",
                i->{
                    if(i.addendum==null) i.addendum = new InterpretationPanel();
                    return i.addendum;
                }, i->{
                    if(i.addendum!=null && i.addendum.variants!=null && i.addendum.variants.length>0) return i.addendum;
                    return null;
                }, SummarySimpleElement.build(),
                variantAddendum,
                AbbreviationElement.build(),
                InterpretationElement.build());
        var category = Interpretable.Category.valueOf((String)service.get("interpretation_category"));
        if(category == Interpretable.Category.SingleWithMLPA) {
            Section elemMlpa = Section.build(IconElement.icon(IconElement.Type.Light, "fa-dna"), "MLPA", MlpaElement.build((String) service.get("gene")));
            return new SingleGenePanelExpandElement(div(), id, sample, service, elemReport, elemAddendum, btnAddendum, elemMlpa);
        } else return new SingleGenePanelExpandElement(div(), id, sample, service, elemReport, elemAddendum, btnAddendum, null);
    }
    private SingleGenePanelExpandElement(HTMLContainerBuilder<HTMLDivElement> e, String id, long sample, JsPropertyMap<?> service, Section elemReport, Section elemAddendum, ButtonElementToggle btnAddendum, Section elemMlpa) {
        super(e, id, sample, service, elemMlpa!=null? new ResultWriter<?>[] { elemReport, elemAddendum, elemMlpa } : new ResultWriter<?>[] { elemReport, elemAddendum });
        AtomicBoolean lock = new AtomicBoolean(false);
        btnAddendum.onValueChange(evt->{
            if(!lock.get()) {
                lock.set(true);
                elemAddendum.enable(evt.value());
                lock.set(false);
            }
        });
        elemAddendum.onStateChange(evt->{
            if(!lock.get()) {
                lock.set(true);
                btnAddendum.value(evt.state()== Section.State.OPEN, evt.event());
                lock.set(false);
            }
        });
        controller.element().firstElementChild.append(btnAddendum.element());
    }
    @Override
    protected Promise<InterpretationPanel> initialize() {
        return Promise.resolve(new InterpretationPanel());
    }
    @Override
    public SingleGenePanelExpandElement that() {
        return this;
    }
}
