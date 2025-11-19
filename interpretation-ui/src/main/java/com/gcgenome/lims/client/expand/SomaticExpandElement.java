package com.gcgenome.lims.client.expand;

import com.gcgenome.lims.api.AnalysisApi;
import com.gcgenome.lims.api.InterpretationApi;
import com.gcgenome.lims.api.SnvApi;
import com.gcgenome.lims.client.ExpandElement;
import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.client.WindowState;
import com.gcgenome.lims.client.expand.elem.PreviewElement;
import com.gcgenome.lims.client.expand.elem.*;
import com.gcgenome.lims.client.expand.somatic.CancerType;
import com.gcgenome.lims.client.expand.somatic.DrugPhenotype;
import com.gcgenome.lims.client.expand.somatic.Qc;
import com.gcgenome.lims.client.expand.somatic.TierWithInterpretation;
import com.gcgenome.lims.dto.InterpretationSomatic;
import com.gcgenome.lims.dto.Message;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.HTMLElementBuilder;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static elemental2.core.Global.JSON;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.span;

public class SomaticExpandElement extends HTMLElementBuilder<HTMLDivElement, SomaticExpandElement> implements ExpandElement<HTMLDivElement> {
    public static SomaticExpandElement build(String id) {
        return new SomaticExpandElement(div(), id);
    }
    protected final HTMLContainerBuilder<HTMLDivElement> controller = div().css("controller");
    private final ButtonElement btnHide = ButtonElement.outline().css("button").before(IconElement.icon("close")).text("Close");
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private final String id;
    private final InterpretationSubject[] sections;
    private final PreviewElement preview = PreviewElement.build();
    private SomaticExpandElement(HTMLContainerBuilder<HTMLDivElement> e, String id) {
        super(e.css("work").style("height: 100vh;"));
        _this = e;
        this.id = id;
        String code = Subjects.code.getValue();
        if("N082".equalsIgnoreCase(code) || "N094".equalsIgnoreCase(code)) sections = new InterpretationSubject[] {
                CancerType.build(),
                Section.build("Tier 1", TierWithInterpretation.build(InterpretationSomatic.Tier.Tier1)).enable(true),
                Section.build("Tier 2", TierWithInterpretation.build(InterpretationSomatic.Tier.Tier2)).enable(true),
                Section.build("Tier 3", TierWithInterpretation.build(InterpretationSomatic.Tier.Tier3)).enable(true),
                DrugPhenotype.build("NUDT15"),
                DrugPhenotype.build("TPMT"),
                Qc.build()
        };
        else sections = new InterpretationSubject[] {
                CancerType.build(),
                Section.build("Tier 1", TierWithInterpretation.build(InterpretationSomatic.Tier.Tier1)).enable(true),
                Section.build("Tier 2", TierWithInterpretation.build(InterpretationSomatic.Tier.Tier2)).enable(true),
                Section.build("Tier 3", TierWithInterpretation.build(InterpretationSomatic.Tier.Tier3)).enable(true),
                Qc.build()
        };
        layout();
        btnHide.onClick(evt->fireStateChangeEvent());
    }
    private void layout() {
        var div = div().style("height: calc(100% - 55px); overflow: auto;");
        for(var section: sections) div.add(section);
        _this.add(div)
             .add(controller.add(span()
                            .add(ButtonAutoElement.build())
                            .add(ButtonNegativeElement.build()))
             .add(span().style("margin-left: 10px;")
                            .add(ButtonSaveElement.build())
                            .add(ButtonPreviewElement.build())
                            .add(btnHide)))
             .add(preview);
    }
    @Override
    public void update() {
        DomGlobal.window.parent.postMessage(JSON.stringify(Message.builder().id(id).type(Message.MessageType.STRETCH).build()), "*");
        var code = Subjects.code.getValue();
        Promise.all(
            InterpretationApi.interpretation(Subjects.sample.getValue(), code).catch_(error->Promise.resolve(new InterpretationSomatic())),
            SnvApi.selected2(Subjects.sample.getValue(), code))
        .then(c->{
            var interpretation = concat(Js.cast(c[0]), Js.cast(c[1]));
            if(interpretation.coverage==null || interpretation.meanDepth==null)
                return AnalysisApi.analysis(Subjects.sample.getValue())
                        .then(array->Promise.resolve(pickLastAnalysis(code, array)))
                        .then(analysis->{
                            JsPropertyMap<?> values = Js.asPropertyMap(analysis.get("values"));
                            if(interpretation.coverage==null && values.has("PCT.250x")) interpretation.coverage = values.get("PCT.250x").toString();
                            if(interpretation.meanDepth==null && values.has("mean.depth")) {
                                var str = values.get("mean.depth").toString();
                                var integer = Math.round(Double.parseDouble(str));
                                interpretation.meanDepth = String.valueOf(integer);
                            }
                            return Promise.resolve(interpretation);
                        }).catch_(e->Promise.resolve(interpretation));
            else return Promise.resolve(interpretation);
        }).then(obj->{
            Subjects.interpretation.next(obj);
            return null;
        });
    }
    private static String[] tiers = new String[] { "Tier1", "Tier2", "Tier3"};
    // interpretation 변이가 snvs에 있는지 확인해서 리스트 만들고 + interpretation에 없는 변이 목록
    private static InterpretationSomatic concat(InterpretationSomatic interpretation,  List<JsPropertyMap<?>> snvs) {
        var groupByTier = snvs.stream().collect(Collectors.groupingBy(snv->snv.get("report").toString(), Collectors.mapping(SomaticExpandElement::map, Collectors.toList())));
        for(var tier: tiers) {
            InterpretationSomatic.Result result = null;
            if(interpretation.results==null) {
                result = createEmpty(tier);
                interpretation.results = new InterpretationSomatic.Result[] { result };
            } else result = Arrays.stream(interpretation.results).filter(r->r.tier().name().equals(tier)).findAny().orElseGet(()->{
                var tmp = createEmpty(tier);
                interpretation.results = Stream.concat(Arrays.stream(interpretation.results), Stream.of(tmp)).toArray(InterpretationSomatic.Result[]::new);
                return tmp;
            });
            if(groupByTier.containsKey(tier)) {
                var proven = result.variants != null ? Arrays.stream(result.variants)
                        .filter(v -> (contains(groupByTier.get(tier), v)) || (v.snv == null))
                        .collect(Collectors.toList()) : new LinkedList<InterpretationSomatic.Variant>();
                var newbee = groupByTier.get(tier).stream().filter(v -> !contains(proven, v)).collect(Collectors.toList());
                result.variants = Stream.concat(proven.stream(), newbee.stream()).toArray(InterpretationSomatic.Variant[]::new);
            } else result.variants = new InterpretationSomatic.Variant[0];
        }
        return interpretation;
    }
    private static InterpretationSomatic.Result createEmpty(String tier) {
        var tmp = new InterpretationSomatic.Result();
        tmp.tier(InterpretationSomatic.Tier.valueOf(tier));
        tmp.variants = new InterpretationSomatic.Variant[0];
        return tmp;
    }
    private static boolean contains(List<InterpretationSomatic.Variant> list, InterpretationSomatic.Variant a) {
        return list.stream().anyMatch(v->equals(v, a));
    }
    private static boolean equals(InterpretationSomatic.Variant a, InterpretationSomatic.Variant b) {
        if(a.snv==null && b.snv==null) {
            return a.gene.equals(b.gene) && a.hgvsc.equals(b.hgvsc);
        } else if(a.snv==null) return false;
        else return a.snv.equals(b.snv);
    }
    private JsPropertyMap<?> pickLastAnalysis(String code, JsPropertyMap<?>[] analysis) {
        Comparator<JsPropertyMap<?>> comparator = Comparator.comparing(map->(String)map.get("batch"), Comparator.nullsLast(Comparator.reverseOrder()));
        if(analysis==null) analysis = new JsPropertyMap[0];
        return Arrays.stream(analysis)
                .filter(map -> code.equals(map.get("service"))).min(comparator)
                .orElseThrow(()->new RuntimeException("No analysis found"));
    }
    private static InterpretationSomatic.Variant map(JsPropertyMap<?> map) {
        var variant = new InterpretationSomatic.Variant();
        variant.snv = map.get("id").toString();
        variant.analysis = map.get("analysis").toString();
        variant.gene = map.get("symbol").toString();
        variant.hgvsc = map.get("hgvsc").toString();
        variant.hgvsp = map.has("hgvsp") ? map.get("hgvsp").toString() : "p.?";
        variant.vaf = Double.parseDouble(map.get("vaf(%)").toString());
        variant.depth(Integer.parseInt(map.get("depth").toString()));
        variant.cosmic = map.has("cosmic_v90_legacy_id") ? map.get("cosmic_v90_legacy_id").toString() :"-";
        return variant;
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
    @Override
    public SomaticExpandElement that() {
        return this;
    }
}
