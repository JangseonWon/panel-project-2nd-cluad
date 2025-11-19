package com.gcgenome.lims.client.expand;

import com.gcgenome.lims.api.AnalysisApi;
import com.gcgenome.lims.client.expand.panel.*;
import com.gcgenome.lims.dto.InterpretationPanel;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.ButtonElementToggle;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.jboss.elemento.Elements.div;

public class PanelExpandElement extends AbstractPanelExpandElement<PanelExpandElement> {
    public static PanelExpandElement build(String id, long sample, JsPropertyMap<?> service) {
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
        return new PanelExpandElement(div(), id, sample, service, elemReport, elemAddendum, btnAddendum, DepthCoverageElement.build());
    }
    private PanelExpandElement(HTMLContainerBuilder<HTMLDivElement> e, String id, long sample, JsPropertyMap<?> service, Section elemReport, Section elemAddendum, ButtonElementToggle btnAddendum, ResultWriter<?> elemDepth) {
        super(e, id, sample, service, elemReport, elemAddendum, elemDepth);
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
        return AnalysisApi.analysis(sample)
                .then(array->Promise.resolve(pickLastAnalysis(array)))
                .then(analysis->{
                    var instance = new InterpretationPanel();
                    var values = Js.asPropertyMap(analysis.get("values"));
                    instance.coverage = (String) (values.has("10x(%)")?values.get("10x(%)"):values.get("10x>=(%)"));
                    instance.meanDepth = (String) values.get("depth(x)");
                    return Promise.resolve(instance);
                }).catch_(e->{
                    if(e!=null) DomGlobal.alert(e);
                    return null;
                });
    }
    private JsPropertyMap<?> pickLastAnalysis(JsPropertyMap<?>[] analysis) {
        Comparator<JsPropertyMap<?>> comparator = Comparator.comparing(map->(String)map.get("batch"), Comparator.nullsLast(Comparator.reverseOrder()));
        if(analysis==null) analysis = new JsPropertyMap[0];
        return Arrays.stream(analysis)
                .filter(map -> code.equals(map.get("service"))).min(comparator)
                .orElseThrow(()->new RuntimeException("No analysis found"));
    }
    @Override
    public PanelExpandElement that() {
        return this;
    }
}
