package com.gcgenome.lims.client.collapse;

import com.gcgenome.lims.api.InterpretationApi;
import com.gcgenome.lims.client.CollapseElement;
import com.gcgenome.lims.client.WindowState;
import com.gcgenome.lims.dto.InterpretationPanel;
import com.gcgenome.lims.dto.Message;
import com.gcgenome.lims.ui.IconElement;
import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.TextAreaElement;
import net.sayaya.ui.event.HasStateChangeHandlers;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.jboss.elemento.Elements.*;

public class PanelCollapseElement extends HTMLElementBuilder<HTMLDivElement, PanelCollapseElement> implements CollapseElement<HTMLDivElement> {
    public static PanelCollapseElement build(String id, long sample, JsPropertyMap<?> service) {
        return new PanelCollapseElement(div(), id, sample, service);
    }
    private final IconElement icon = IconElement.icon(IconElement.Type.Light, "fa-comment-medical");
    private final HTMLContainerBuilder<HTMLElement> title = span().css("mdc-list-item__primary-text").add("Interpretation");
    private final HTMLContainerBuilder<HTMLElement> info = span().css("mdc-list-item__secondary-text");
    private final HTMLContainerBuilder<HTMLElement> meta = span().css("mdc-list-item__meta");
    private final TextAreaElement<String> summary = TextAreaElement.textBox().outlined().readOnly(true).text("Interpretation").style("width: 100%; height: 120px; margin-bottom: 16px;");
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private final String id;
    private final long sample;
    private final JsPropertyMap<?> service;
    private PanelCollapseElement(HTMLContainerBuilder<HTMLDivElement> e, String id, long sample, JsPropertyMap<?> service) {
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
        this.summary.on(EventType.click, evt->{
            evt.stopPropagation();
            evt.preventDefault();
            evt.stopImmediatePropagation();
        });
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
        meta.add(label("Loading...").style("margin-right: 40px;"));
        Promise<InterpretationPanel> interpretation = InterpretationApi.interpretation(sample, (String)service.get("code")).then(Js::cast);
        interpretation.then(this::update).catch_(error->update(null));
    }
    private Promise<Void> update(InterpretationPanel map) {
        meta.element().innerHTML = "";
        if(map==null) {
            meta.add(label("No interpretation").style("margin-right: 40px;"));
            Message msg = Message.builder().id(id).type(Message.MessageType.COLLAPSE).param("64px").build();
            DomGlobal.window.parent.postMessage(Global.JSON.stringify(msg), "*");
        } else {
            meta.add(summary.value(map.interpretation)).style("flex-basis: 100%;");
            Message msg = Message.builder().id(id).type(Message.MessageType.COLLAPSE).param("200px").build();
            DomGlobal.window.parent.postMessage(Global.JSON.stringify(msg), "*");
        }
        return Promise.resolve((Void)null);
    }
    @Override
    public PanelCollapseElement that() {
        return this;
    }
    private final Set<HasStateChangeHandlers.StateChangeEventListener<WindowState>> listeners = new HashSet<>();
    @Override
    public Collection<HasStateChangeHandlers.StateChangeEventListener<WindowState>> listeners() {
        return listeners;
    }
    @Override
    public WindowState state() {
        return WindowState.FULLSCREEN;
    }
}
