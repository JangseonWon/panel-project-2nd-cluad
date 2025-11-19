package com.gcgenome.lims.client.expand;

import com.gcgenome.lims.api.AnalysisApi;
import com.gcgenome.lims.api.InterpretationApi;
import com.gcgenome.lims.api.SnvApi;
import com.gcgenome.lims.client.ExpandElement;
import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.client.WindowState;
import com.gcgenome.lims.client.expand.elem.PreviewElement;
import com.gcgenome.lims.client.expand.elem.*;
import com.gcgenome.lims.client.expand.hrd.CancerType;
import com.gcgenome.lims.client.expand.hrd.ResultWithQc;
import com.gcgenome.lims.client.expand.hrd.TierWithInterpretation;
import com.gcgenome.lims.dto.InterpretationHrd;
import com.gcgenome.lims.dto.Message;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.event.HasStateChangeHandlers;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static elemental2.core.Global.JSON;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.span;

public class HrdExpandElement extends HTMLElementBuilder<HTMLDivElement, HrdExpandElement> implements ExpandElement<HTMLDivElement> {
    public static HrdExpandElement build(String id) {
        return new HrdExpandElement(div(), id);
    }
    protected final HTMLContainerBuilder<HTMLDivElement> controller = div().css("controller");
    private final ButtonElement btnHide = ButtonElement.outline().css("button").before(IconElement.icon("close")).text("Close");
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private final String id;
    private final String code;
    private final long sample;
    private final InterpretationSubject[] sections;
    private final PreviewElement preview = PreviewElement.build();
    private HrdExpandElement(HTMLContainerBuilder<HTMLDivElement> e, String id) {
        super(e.css("work").style("height: 100vh;"));
        _this = e;
        this.id = id;
        this.code = Subjects.code.getValue();
        this.sample = Subjects.sample.getValue();
        sections = new InterpretationSubject[] {
                CancerType.build(),
                ResultWithQc.build(),
                Section.build("BRCA", TierWithInterpretation.build(InterpretationHrd.Tier.BRCA)).enable(true),
                Section.build("Tier1", TierWithInterpretation.build(InterpretationHrd.Tier.TIER1)).enable(true),
                Section.build("Tier2", TierWithInterpretation.build(InterpretationHrd.Tier.TIER2)).enable(true)
        };
        layout();
        btnHide.onClick(evt->fireStateChangeEvent());
    }
    private void layout() {
        var div = div().style("height: calc(100% - 55px); overflow: auto;");
        for(var section: sections) div.add(section);
        _this.add(div)
                .add(controller.add(span()
                                .add(ButtonAutoElement.build()))
                        .add(span().style("margin-left: 10px;")
                                .add(ButtonSaveElement.build())
                                .add(ButtonPreviewElement.build())
                                .add(btnHide)))
                .add(preview);
    }
    @Override
    public void update() {
        DomGlobal.window.parent.postMessage(JSON.stringify(Message.builder().id(id).type(Message.MessageType.STRETCH).build()), "*");
        Promise.all(
                InterpretationApi.interpretation(sample, code).catch_(error -> Promise.resolve(InterpretationHrd.createWithDefaults())),
                getLatestBatch().then(map -> {
                    if (map.equals(JsPropertyMap.of())) {
                        return Promise.resolve(Collections.emptyList());
                    }
                    return SnvApi.getSelectedByBatch(sample, code, (String) map.get("batch"));
                })).then(result-> {
                    InterpretationHrd interpretation = filterVariantBySnvTableAndRegroup(Js.cast(result[0]), Js.cast(result[1]));
                    if(interpretation.hrd == null || interpretation.gi == null || interpretation.giScore == null || interpretation.cancerType == null
                            || interpretation.cnv == null || interpretation.snv == null || interpretation.tumorFraction == null){
                        return getLatestBatch().then(analysis ->{
                            JsPropertyMap<?> values = Js.asPropertyMap(analysis.get("values"));
                            if(interpretation.cancerType == null && values.has("cancer_type")) interpretation.cancerType = values.get("cancer_type").toString();
                            if(interpretation.hrd == null && values.has("hrd")) interpretation.hrd = values.get("hrd").toString();
                            if((interpretation.giScore == null || interpretation.giScore == 0.0 ) && values.has("gi_score")) interpretation.giScore = Double.parseDouble(values.get("gi_score").toString());
                            if(interpretation.gi == null && values.has("gi")) interpretation.gi = values.get("gi").toString();
                            if(interpretation.brca == null && values.has("brca")) interpretation.brca = values.get("brca").toString();
                            if(interpretation.snv == null && values.has("snv")) interpretation.snv = values.get("snv").toString();
                            if(interpretation.cnv == null && values.has("cnv")) interpretation.cnv = values.get("cnv").toString();
                            if(interpretation.tumorFraction == null && values.has("tumor_fraction")) interpretation.tumorFraction = values.get("tumor_fraction").toString();
                            return Promise.resolve(interpretation);
                        }).catch_(e -> Promise.resolve(interpretation));
                    } else return Promise.resolve(interpretation);
                }).then(obj -> {
                    Subjects.interpretation.next(obj);
                    return null;
                });
    }

    private Promise<? extends JsPropertyMap<?>> getLatestBatch() {
        return AnalysisApi.analysis(sample)
                .then(array -> {
                    if (array == null || array.length == 0) return Promise.resolve(JsPropertyMap.of());
                    return Promise.resolve(Arrays.stream(array)
                            .filter(map -> code.equals(map.get("service")))
                            .max(Comparator.comparing(map -> (String) map.get("batch")))
                            .orElse(JsPropertyMap.of()));
                });
    }
    /**
     * HRD 결과지 그룹(BRCA, Tier1, Tier2)으로 선택 변이 재분류
     */
    private static InterpretationHrd filterVariantBySnvTableAndRegroup(InterpretationHrd interpretation, List<JsPropertyMap<?>> snvs) {
        var tiers = Arrays.asList(InterpretationHrd.Tier.values());
        for (var tier : tiers) {
            InterpretationHrd.InterpretationTier result = getOrCreateTier(interpretation, tier);
            Map<String, List<InterpretationHrd.Variant>> groupByTier =  snvs.isEmpty() ? new HashMap<>() : snvs.stream()
                    .collect(Collectors.groupingBy(
                            snv -> snv.get("report").toString(),
                            Collectors.mapping(HrdExpandElement::map, Collectors.toList())));
            List<InterpretationHrd.Variant> snvInTier = groupByTier.getOrDefault(tier.getAdjustedTierForReport(), new LinkedList<>());

            if(!snvInTier.isEmpty()) {
                List<InterpretationHrd.Variant> variantsFromInterpretation = result.variants != null ? Arrays.stream(result.variants)
                        .filter(v -> !snvInTier.isEmpty() && contains(snvInTier, v))
                        .collect(Collectors.toList()) : new ArrayList<>();
                List<InterpretationHrd.Variant> variantsFromSnv = snvInTier.stream().filter(v -> !contains(variantsFromInterpretation, v)).collect(Collectors.toList());
                result.variants = Stream.concat(variantsFromInterpretation.stream(), variantsFromSnv.stream()).toArray(InterpretationHrd.Variant[]::new);
            } else result.variants = new InterpretationHrd.Variant[0];
        }
        return interpretation;
    }
    private static InterpretationHrd.InterpretationTier getOrCreateTier(InterpretationHrd interpretation, InterpretationHrd.Tier tier) {
        return Arrays.stream(interpretation.results != null ? interpretation.results : new InterpretationHrd.InterpretationTier[0])
                .filter(r -> InterpretationHrd.Tier.equalsIgnoreCaseRemoveWhitespace(tier, r.tier))
                .findAny()
                .orElseGet(() -> {
                    InterpretationHrd.InterpretationTier newTier = createEmpty(tier.capitalizeFirstLetterOfNumericTiers());
                    interpretation.results = Stream.concat(
                            Arrays.stream(interpretation.results != null ? interpretation.results : new InterpretationHrd.InterpretationTier[0]),
                            Stream.of(newTier)
                    ).toArray(InterpretationHrd.InterpretationTier[]::new);
                    return newTier;
                });
    }
    private static InterpretationHrd.InterpretationTier createEmpty(String tier) {
        var tierInstance = new InterpretationHrd.InterpretationTier();
        tierInstance.tier = tier;
        tierInstance.variants = new InterpretationHrd.Variant[0];
        return tierInstance;
    }
    public static boolean contains(List<InterpretationHrd.Variant> list, InterpretationHrd.Variant variant) {
        return list.stream().filter(v -> v.snv != null && variant.snv != null).anyMatch(v -> v.snv.equals(variant.snv));
    }
    public static InterpretationHrd.Variant map(JsPropertyMap<?> map) {
        var variant = new InterpretationHrd.Variant();
        variant.snv = map.get("id").toString();
        variant.analysis = map.get("analysis").toString();
        variant.gene = map.get("symbol").toString();
        variant.hgvsc = getOrDefault(map, "oncokb_hgvsc", "c.?");
        variant.hgvsp = getOrDefault(map, "oncokb_hgvsp","p.?");
        variant.vaf = Double.parseDouble(map.get("vaf(%)").toString());
        variant.depth = Double.parseDouble(map.get("depth").toString());
        variant.cosmicId = getOrDefault(map, "cosmic_v90_legacy_id","-");
        return variant;
    }
    private static String getOrDefault(JsPropertyMap<?> map, String key, String defaultValue) {
        var value = map.has(key) && map.get(key) != null ? map.get(key).toString() : null;
        if(value == null || value.toString().trim().isEmpty()) value = defaultValue;
        return value;
    }
    private final Set<HasStateChangeHandlers.StateChangeEventListener<WindowState>> listeners = new HashSet<>();

    @Override
    public Collection<StateChangeEventListener<WindowState>> listeners() {
        return listeners;
    }
    @Override
    public WindowState state() {
        return WindowState.COLLAPSE;
    }
    @Override
    public HrdExpandElement that() {
        return this;
    }
}
