package com.gcgenome.lims.client.collapse;

import com.gcgenome.lims.api.InterpretationApi;
import com.gcgenome.lims.client.CollapseElement;
import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.client.WindowState;
import com.gcgenome.lims.dto.InterpretationSomatic;
import com.gcgenome.lims.dto.Message;
import com.gcgenome.lims.ui.IconElement;
import elemental2.core.Global;
import elemental2.dom.*;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.event.HasStateChangeHandlers;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jboss.elemento.Elements.*;

public class SomaticCollapseElement extends HTMLElementBuilder<HTMLDivElement, SomaticCollapseElement> implements CollapseElement<HTMLDivElement> {
    public static SomaticCollapseElement build(String id, long sample, JsPropertyMap<?> service) {
        return new SomaticCollapseElement(div(), id, sample, service);
    }
    private final IconElement icon = IconElement.icon(IconElement.Type.Light, "fa-comment-medical");
    private final HTMLContainerBuilder<HTMLElement> title = span().css("mdc-list-item__primary-text").add("Interpretation");
    private final HTMLContainerBuilder<HTMLElement> info = span().css("mdc-list-item__secondary-text");
    private final HTMLContainerBuilder<HTMLElement> meta = span().css("mdc-list-item__meta").style("margin-right: 40px;padding-top: 10px;padding-bottom: 10px;");
    private final HTMLContainerBuilder<HTMLLabelElement> summaryTier1 = label();
    private final HTMLContainerBuilder<HTMLLabelElement> summaryTier2 = label();
    private final HTMLContainerBuilder<HTMLLabelElement> summaryTier3 = label();
    private final HTMLContainerBuilder<HTMLTableElement> summary = table().style("text-align: right; font-size: var(--mdc-typography-caption-font-size, 0.75rem);")
            .add(tr().add(td().style("color: var(--mdc-theme-text-secondary-on-background, rgba(0, 0, 0, 0.54));")
                    .add("Tier1: ")).add(td().add(summaryTier1)))
            .add(tr().add(td().style("color: var(--mdc-theme-text-secondary-on-background, rgba(0, 0, 0, 0.54));")
                    .add("Tier2: ")).add(td().add(summaryTier2)))
            .add(tr().add(td().style("color: var(--mdc-theme-text-secondary-on-background, rgba(0, 0, 0, 0.54));")
                    .add("Tier3: ")).add(td().add(summaryTier3)));
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private final String id;
    private final long sample;
    private final JsPropertyMap<?> service;
    private SomaticCollapseElement(HTMLContainerBuilder<HTMLDivElement> e, String id, long sample, JsPropertyMap<?> service) {
        super(e.css("work-summary")
                .style("margin-left: 16px;margin-right: 16px;display: flex; flex-direction: row; " +
                        "flex-wrap: wrap; align-content: space-between; justify-content: flex-start; " +
                        "align-items: center; cursor: pointer;"));
        _this = e;
        this.id = id;
        this.sample = sample;
        this.service = service;
        layout();
        this.on(EventType.click, evt->fireStateChangeEvent());
        Subjects.interpretation.subscribe(interpretation->{});
    }
    private void layout() {
        _this.add(span().css("mdc-list-item__graphic").style("height: auto; align-self: unset;").add(icon))
                .add(div().css("mdc-list-item__text").style("margin-bottom: 10px;").add(title)
                        .add(info.add("발견된 변이 정보를 토대로 " + service.get("name") + "의 임상소견을 작성합니다.")))
                .add(meta);
    }
    @Override
    public void update() {
        meta.element().innerHTML = "";
        meta.add(label("Loading..."));
        Promise<InterpretationSomatic> interpretation = InterpretationApi.interpretation(sample, (String)service.get("code")).then(Js::cast);
        interpretation.then(this::update).catch_(error->update(null));
    }
    private Promise<Void> update(InterpretationSomatic map) {
        meta.element().innerHTML = "";
        summaryTier1.element().innerHTML = "";
        summaryTier2.element().innerHTML = "";
        summaryTier3.element().innerHTML = "";
        if(map==null || map.results==null) {
            meta.add(label("No interpretation"));
            Message msg = Message.builder().id(id).type(Message.MessageType.COLLAPSE).param("64px").build();
            DomGlobal.window.parent.postMessage(Global.JSON.stringify(msg), "*");
        } else {
            InterpretationSomatic.Result t1 = null;
            InterpretationSomatic.Result t2 = null;
            InterpretationSomatic.Result t3 = null;
            for(InterpretationSomatic.Result result: map.results) {
                if(result.tier()==null) continue;
                if(InterpretationSomatic.Tier.Tier1.equals(result.tier())) t1 = result;
                else if(InterpretationSomatic.Tier.Tier2.equals(result.tier())) t2 = result;
                else if(InterpretationSomatic.Tier.Tier3.equals(result.tier())) t3 = result;
            }
            update(t1, summaryTier1);
            update(t2, summaryTier2);
            update(t3, summaryTier3);
            meta.add(summary);
            Message msg = Message.builder().id(id).type(Message.MessageType.COLLAPSE).param("96px").build();
            DomGlobal.window.parent.postMessage(Global.JSON.stringify(msg), "*");
        }
        return Promise.resolve((Void)null);
    }
    private static void update(InterpretationSomatic.Result result, HTMLContainerBuilder<HTMLLabelElement> label) {
        if(result == null || result.variants == null || result.variants.length <=0) label.add("Negative").style("color: 0xEFEFEF;font-weight: bold;");
        else label.add(Arrays.stream(result.variants).map(v->v.gene).distinct().collect(Collectors.joining(", "))).style("color: rgb(135,51,61);font-weight: bold;");
    }
    @Override
    public SomaticCollapseElement that() {
        return this;
    }
    private final Set<StateChangeEventListener<WindowState>> listeners = new HashSet<>();
    @Override
    public Collection<HasStateChangeHandlers.StateChangeEventListener<WindowState>> listeners() {
        return listeners;
    }
    @Override
    public WindowState state() {
        return WindowState.FULLSCREEN;
    }
}
