package com.gcgenome.lims.client.expand;

import com.gcgenome.lims.api.SnvApi;
import com.gcgenome.lims.client.Data;
import com.gcgenome.lims.client.ExpandElement;
import com.gcgenome.lims.client.WindowState;
import com.gcgenome.lims.client.expand.elem.*;
import com.gcgenome.lims.dto.Message;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.i18n.client.NumberFormat;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.rx.subject.AsyncSubject;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.ListElement;
import net.sayaya.ui.chart.Column;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static elemental2.core.Global.JSON;
import static org.jboss.elemento.Elements.div;

public class BloodCancerExpandElement extends HTMLElementBuilder<HTMLDivElement, BloodCancerExpandElement> implements ExpandElement<HTMLDivElement> {
    public static BloodCancerExpandElement build(String id, long sample, JsPropertyMap<?> service) {
        return new BloodCancerExpandElement(div(), id, sample, service);
    }
    private final static Column[] colums = new Column[] {
            SnvTableElement.dropdown("report", "D.class",
                            ListElement.singleLine().label("-"),
                            ListElement.singleLine().label("Tier1"),
                            ListElement.singleLine().label("Tier2"),
                            ListElement.singleLine().label("Tier3"),
                            ListElement.singleLine().label("*"),
                            ListElement.singleLine().label("+"),
                            ListElement.singleLine().label("?")
                    ).pattern("-").than("", "")
                    .pattern("Tier1").than("#FFFFFF", "#AD1742")
                    .pattern("Tier2").than("#FFFFFF", "#D26263")
                    .pattern("Tier3").than( "#FFFFFF", "#FF8200")
                    .pattern("\\*").than("#FFFFFF", "#AD1742")
                    .pattern("\\+").than("#FFFFFF", "#D26263")
                    .pattern("\\?").than("#FFFFFF", "#FF8200")
                    .build(),
            SnvTableElement.snv("variant", "Id").build(),
            SnvTableElement.text("chrom", "Chrom").build(),
            SnvTableElement.text("pos", "Pos").horizontal("right").build(),
            SnvTableElement.text("ref", "Ref").horizontal("left").build(),
            SnvTableElement.text("alt", "Alt").horizontal("left").build(),
            SnvTableElement.text("consequence", "Consequence").horizontal("left").build(),
            SnvTableElement.text("symbol", "Gene").build(),
            SnvTableElement.text("hgvsc", "HGVS.c").horizontal("left").build(),
            SnvTableElement.text("hgvsp", "HGVS.p").horizontal("left").build(),
            SnvTableElement.text("depth", "Depth").horizontal("right").build(),
            SnvTableElement.text("alt_depth", "Depth(Alt)").horizontal("right").build(),
            SnvTableElement.number("vaf(%)", "VAF(%)", NumberFormat.getFormat("0.###")).build(),
            SnvTableElement.number("base_quality", "Base Quality", NumberFormat.getFormat("0.#")).build(),
            SnvTableElement.text("quality", "Quality").horizontal("left").build(),
            SnvTableElement.text("mutect2_filter", "Mutect2").horizontal("left").build(),
            SnvTableElement.text("caller", "caller").build(),
            SnvTableElement.text("max_popfreq(1k_&_gnomad_&_krg)", "Max PropFreq").horizontal("right").build(),
            SnvTableElement.text("cosmic_v90_legacy_id", "COSMIC v90 Legacy").build(),
            SnvTableElement.text("cosmic_v90_cnt", "COSMIC v90 cnt").build(),
            SnvTableElement.text("clinvar_clnsig", "ClinVar clnsig").build(),
            SnvTableElement.text("oncokb(oncogenicity)", "oncokb oncogenicity").build(),
    };
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private final long sample;
    private final String code;
    private final String id;
    private SnvTableElement sheet;
    private BloodCancerExpandElement(HTMLContainerBuilder<HTMLDivElement> e, String id, long sample, JsPropertyMap<?> service) {
        super(e);
        _this = e;
        this.id = id;
        this.sample = sample;
        this.code = (String)service.get("code");
        layout();
    }
    private void layout() {
        var iptFilters = FilterElementSet.build();
        sheet = SomaticPanelSheetBuilder.build(colums);
        _this.css("work").style("height: 100vh;")
                .add(div().style("display: flex;align-items: center; margin-top: 5px;margin-left: 10px; margin-right: 10px;")
                        .add(AnalysisElement.build(code))
                        .add(iptFilters.iptKeyword))
                .add(div().style("display: flex; align-items: center; margin-left: 10px;")
                        .add(iptFilters.tagSet.add(FilterToggleElement.build("Filtered Variant", "candidate", true)))
                        .add(iptFilters.tagSet.add(FilterToggleElement.build("Single Variant", "single", false)))
                        .add(iptFilters.tagSet.add(FilterToggleElement.build("pindel",  "pindel", false))))
                .add(sheet).add(NotAnalyzedElement.build())
                .add(PageElement.build())
                .add(div().css("controller")
                        .add(SaveButtonElement.build(sample, code).enabled(false))
                        .add(HideButtonElement.build(this)));
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
        sheet.reported(reported);
    }
    @Override
    public BloodCancerExpandElement that() {
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
