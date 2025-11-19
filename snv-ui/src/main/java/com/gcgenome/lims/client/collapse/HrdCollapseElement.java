package com.gcgenome.lims.client.collapse;

import com.gcgenome.lims.api.AnalysisApi;
import com.gcgenome.lims.api.SnvApi;
import com.gcgenome.lims.client.CollapseElement;
import com.gcgenome.lims.client.WindowState;
import com.gcgenome.lims.dto.Message;
import com.gcgenome.lims.ui.IconElement;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.i18n.client.NumberFormat;
import elemental2.core.Global;
import elemental2.dom.*;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.event.HasStateChangeHandlers;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.*;

import static com.gcgenome.lims.client.collapse.SnvSimpleTableElement.number;
import static com.gcgenome.lims.client.collapse.SnvSimpleTableElement.text;
import static org.jboss.elemento.Elements.*;

public class HrdCollapseElement extends HTMLElementBuilder<HTMLDivElement, HrdCollapseElement> implements CollapseElement<HTMLDivElement> {
    public static HrdCollapseElement build(String id, long sample, JsPropertyMap<?> service) {
        return new HrdCollapseElement(div(), id, sample, service);
    }
    private final IconElement icon = IconElement.icon(IconElement.Type.Light, "fa-search");
    private final HTMLContainerBuilder<HTMLElement> title = span().css("mdc-list-item__primary-text").add("SNV/InDel");
    private final HTMLContainerBuilder<HTMLElement> info = span().css("mdc-list-item__secondary-text").add("발견된 SNV/InDel 정보를 열람하고 변이의 병원성을 판별하여 입력합니다.");
    private final HTMLContainerBuilder<HTMLElement> meta = span().css("mdc-list-item__meta");
    private final SnvSimpleTableElement summary = SnvSimpleTableElement.build(
            text("report", "D.Class").build(),
            text("symbol", "Gene").build(),
            text("hgvsc", "DNA").build(),
            text("hgvsp", "Protein").build(),
            number("vaf(%)", "VAF(%)", NumberFormat.getFormat("0")).build(),
            number("depth", "Depth(X)", NumberFormat.getFormat("0.##")).build(),
            text("cosmic_v90_legacy_id", "COSMIC").build()
    );
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private final String id;
    private final long sample;
    private final JsPropertyMap<?> service;
    private final String code;
    private HrdCollapseElement(HTMLContainerBuilder<HTMLDivElement> e, String id, long sample, JsPropertyMap<?> service) {
        super(e.css("work-summary")
                .style("margin-left: 16px;margin-right: 16px;display: flex; flex-direction: row; " +
                        "flex-wrap: wrap; align-content: space-between; justify-content: flex-start; " +
                        "align-items: center; cursor: pointer; overflow: hidden; width: 1168px;"));
        this.id = id;
        this.sample = sample;
        this.service = service;
        this.code = (String)service.get("code");
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
                        .add(info))
                .add(meta);
    }

    public void update() {
        meta.element().innerHTML = "";
        meta.add(label("Loading...").style("margin-right: 40px;"));
        getLatestBatch().then(latestBatch -> SnvApi.getReportedByBatch(sample, code, latestBatch)
                .then(Response::json)
                .then(json -> {
                    update(Js.asArray(json));
                    return null;
                })
        );
    }

    private void update(Object[] values) {
        meta.element().innerHTML = "";
        if(values==null || values.length == 0) {
            meta.add(label("No variant").style("margin-right: 40px;")).style("");
            Message msg = Message.builder().id(id).type(Message.MessageType.COLLAPSE).param("64px").build();
            DomGlobal.window.parent.postMessage(Global.JSON.stringify(msg), "*");
        } else {
            summary.element().style.width = CSSProperties.WidthUnionType.of("min-content;");
            meta.add(summary.update(values)).style("flex-basis: 100%;");
            Scheduler.get().scheduleDeferred(()->{
                Message msg = Message.builder().id(id).type(Message.MessageType.COLLAPSE).param((summary.element().offsetHeight+80) + "px").build();
                DomGlobal.window.parent.postMessage(Global.JSON.stringify(msg), "*");
                Scheduler.get().scheduleFixedDelay(()->{
                    meta.add(summary.update(values)).style("flex-basis: 100%;");
                    return false;
                }, 150);
            });
        }
    }

    private Promise<String> getLatestBatch() {
        return AnalysisApi.analysis(sample)
                .then(array -> {
                    if (array == null) array = new JsPropertyMap<?>[0];
                    return Promise.resolve(Arrays.stream(array)
                            .filter(map -> code.equals(map.get("service")))
                            .map(map -> (String) map.get("batch"))
                            .max(String::compareTo)
                            .orElseThrow(() -> new RuntimeException("No analysis found")));
                });
    }

    @Override
    public HrdCollapseElement that() {
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

