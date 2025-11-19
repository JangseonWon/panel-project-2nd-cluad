package com.gcgenome.lims.client.collapse;

import com.gcgenome.lims.api.InterpretationApi;
import com.gcgenome.lims.client.CollapseElement;
import com.gcgenome.lims.client.WindowState;
import com.gcgenome.lims.dto.InterpretationHrd;
import com.gcgenome.lims.dto.Message;
import com.gcgenome.lims.ui.IconElement;
import elemental2.core.Global;
import elemental2.dom.*;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.HTMLElementBuilder;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gcgenome.lims.dto.InterpretationHrd.Status.NEGATIVE;
import static com.gcgenome.lims.dto.InterpretationHrd.Tier.BRCA;
import static org.jboss.elemento.Elements.*;

public class HrdCollapseElement extends HTMLElementBuilder<HTMLDivElement, HrdCollapseElement> implements CollapseElement<HTMLDivElement> {
    public static HrdCollapseElement build(String id, long sample, JsPropertyMap<?> service) {
        return new HrdCollapseElement(div(), id, sample, service);
    }
    private final IconElement icon = IconElement.icon(IconElement.Type.Light, "fa-comment-medical");
    private final HTMLContainerBuilder<HTMLElement> title = span().css("mdc-list-item__primary-text").add("Interpretation");
    private final HTMLContainerBuilder<HTMLElement> info = span().css("mdc-list-item__secondary-text").add("발견된 변이 정보를 토대로 임상소견을 작성합니다.");
    private final HTMLContainerBuilder<HTMLElement> meta = span().css("mdc-list-item__meta");
    private final HTMLContainerBuilder<HTMLLabelElement> summaryGi = label();
    private final HTMLContainerBuilder<HTMLLabelElement> summaryBrca = label();
    private final HTMLContainerBuilder<HTMLTableElement> summary = table().style("text-align: right; font-size: var(--mdc-typography-caption-font-size, 0.75rem);")
            .add(tr().add(td().style("color: var(--mdc-theme-text-secondary-on-background, rgba(0, 0, 0, 0.54));")
                    .add("GI: ")).add(td().add(summaryGi)))
            .add(tr().add(td().style("color: var(--mdc-theme-text-secondary-on-background, rgba(0, 0, 0, 0.54));")
                    .add("BRCA: ")).add(td().add(summaryBrca)));
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private final String id;
    private final long sample;
    private final JsPropertyMap<?> service;
    private HrdCollapseElement(HTMLContainerBuilder<HTMLDivElement> e, String id, long sample, JsPropertyMap<?> service) {
        super(e.css("work-summary")
                .style("margin-left: 16px;margin-right: 16px;display: flex; flex-direction: row; " +
                        "flex-wrap: wrap; align-content: space-between; justify-content: flex-start; " +
                        "align-items: center; cursor: pointer;"));
        this.id = id;
        this.sample = sample;
        this.service = service;
        _this = e;
        layout();
        this.on(EventType.click, evt->fireStateChangeEvent());
    }
    private void layout() {
        _this.add(span().css("mdc-list-item__graphic").style("height: auto; align-self: unset;").add(icon))
                .add(div().css("mdc-list-item__text").style("margin-bottom: 10px;").add(title).add(info))
                .add(meta);
    }
    @Override
    public void update() {
        meta.element().innerHTML = "";
        meta.add(label("Loading...").style("margin-right: 40px;"));
        Promise<InterpretationHrd> interpretation = InterpretationApi.interpretation(sample, (String)service.get("code")).then(Js::cast);
        interpretation.then(this::update).catch_(error->update(null));
    }
    private Promise<Void> update(InterpretationHrd map) {
        clearContents(meta.element(), summaryGi.element(), summaryBrca.element());

        if(map != null && map.interpretation != null) {
            summaryGi.add(map.gi);
            summaryBrca.add(map.brca);
            updateStyle(summaryGi);
            updateStyle(summaryBrca);
            meta.add(summary);
            Message msg = Message.builder().id(id).type(Message.MessageType.COLLAPSE).param("64px").build();
            DomGlobal.window.parent.postMessage(Global.JSON.stringify(msg), "*");
        } else {
            meta.add(label("No interpretation").style("margin-right: 40px;"));
            Message msg = Message.builder().id(id).type(Message.MessageType.COLLAPSE).param("64px").build();
            DomGlobal.window.parent.postMessage(Global.JSON.stringify(msg), "*");
        }
        return Promise.resolve((Void)null);
    }
    private void clearContents(Element... elements) {
        Arrays.stream(elements).forEach(element -> element.innerHTML = "");
    }
    private void updateStyle(HTMLContainerBuilder<HTMLLabelElement> summaryElement) {
        String color = NEGATIVE.label().equalsIgnoreCase(summaryElement.element().textContent) ? "0xEFEFEF" : "rgb(135,51,61)";
        summaryElement.style("color: " + color + "; font-weight: bold;");
    }
    @Override
    public HrdCollapseElement that() {
        return this;
    }
    private final Set<StateChangeEventListener<WindowState>> listeners = new HashSet<>();
    @Override
    public Collection<StateChangeEventListener<WindowState>> listeners() {
        return listeners;
    }
    @Override
    public WindowState state() {
        return WindowState.FULLSCREEN;
    }
}
