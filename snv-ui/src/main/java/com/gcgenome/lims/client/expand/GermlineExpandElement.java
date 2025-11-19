package com.gcgenome.lims.client.expand;

import com.gcgenome.lims.api.SnvApi;
import com.gcgenome.lims.client.Data;
import com.gcgenome.lims.client.ExpandElement;
import com.gcgenome.lims.client.WindowState;
import com.gcgenome.lims.client.expand.elem.*;
import com.gcgenome.lims.dto.Message;
import com.google.gwt.core.client.Scheduler;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.rx.subject.AsyncSubject;
import net.sayaya.ui.ChipElementCheckable;
import net.sayaya.ui.HTMLElementBuilder;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static elemental2.core.Global.JSON;
import static org.jboss.elemento.Elements.div;

public class GermlineExpandElement extends HTMLElementBuilder<HTMLDivElement, GermlineExpandElement> implements ExpandElement<HTMLDivElement> {
    public static GermlineExpandElement build(String id, long sample, JsPropertyMap<?> service, ChipElementCheckable... chips) {
        return new GermlineExpandElement(div(), id, sample, service, chips);
    }
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private final long sample;
    private final String code;
    private final String id;
    private final SnvTableElement table = GermlinePanelSheetBuilder.build();
    private final ChipElementCheckable[] chips;
    private GermlineExpandElement(HTMLContainerBuilder<HTMLDivElement> e, String id, long sample, JsPropertyMap<?> service, ChipElementCheckable... chips) {
        super(e);
        _this = e;
        this.id = id;
        this.sample = sample;
        this.code = (String)service.get("code");
        this.chips = chips;
        layout();
    }
    private void layout() {
        var iptFilters = FilterElementSet.build();
        _this.css("work").style("height: 100vh;")
                .add(div().style("display: flex;align-items: center; margin-top: 5px;margin-left: 10px; margin-right: 10px;")
                        .add(AnalysisElement.build(code))
                        .add(iptFilters.iptKeyword))
                .add(div().style("display: flex; align-items: center; margin-left: 10px;")
                        .add(iptFilters.tagSet))
                .add(table).add(NotAnalyzedElement.build())
                .add(PageElement.build())
                .add(div().css("controller")
                        .add(SaveButtonElement.build(sample, code).enabled(false))
                        .add(HideButtonElement.build(this)));
        for(ChipElementCheckable chip: chips) iptFilters.tagSet.add(chip);
    }
    @Override
    public void update() {
        DomGlobal.window.parent.postMessage(JSON.stringify(Message.builder().id(id).type(Message.MessageType.FULLSCREEN).build()), "*");
        var list = Data.analysisList.getValue();
        list.clear();
        Data.analysisList.next(list);
        Scheduler.get().scheduleFixedDelay(()->{
            AsyncSubject.json(SnvApi.reported(sample, code)).subscribe(json -> update(Data.analysisList.getValue().toArray()));
            AsyncSubject.json(SnvApi.analysis(sample)).subscribe(json -> {
                list.addAll(Arrays.stream(Js.asArray(json)).collect(Collectors.toList()));
                Data.analysisList.next(list);
            });
            return false;
        }, 150);
    }

    private void update(Object[] values) {
        SnvTableElement.SnvReport[] reported = Arrays.stream(values).map(dto->{
            JsPropertyMap<Object> map = Js.asPropertyMap(dto);
            String analysis = (String) map.get("analysis");
            String variant = (String) map.get("snv");
            String classification = (String) map.get("class");
            return new SnvTableElement.SnvReport().variant(analysis + ":" + variant).classification(classification);
        }).toArray(SnvTableElement.SnvReport[]::new);
        table.reported(reported);
    }
    @Override
    public GermlineExpandElement that() {
        return this;
    }
    private final Set<StateChangeEventListener<WindowState>> listeners = new HashSet<>();
    @Override
    public Collection<StateChangeEventListener<WindowState>> listeners() {
        return listeners;
    }
    @Override
    public WindowState state() {
        return WindowState.COLLAPSE;
    }
}
