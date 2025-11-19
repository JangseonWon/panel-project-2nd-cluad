package com.gcgenome.lims.client.expand;

import com.gcgenome.lims.api.AnalysisApi;
import com.gcgenome.lims.api.InterpretationApi;
import com.gcgenome.lims.api.SnvApi;
import com.gcgenome.lims.client.ExpandElement;
import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.client.WindowState;
import com.gcgenome.lims.client.expand.elem.PreviewElement;
import com.gcgenome.lims.client.expand.elem.*;
import com.gcgenome.lims.client.expand.tso.*;
import com.gcgenome.lims.dto.InterpretationTso;
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

public class TsoExpandElement extends HTMLElementBuilder<HTMLDivElement, TsoExpandElement> implements ExpandElement<HTMLDivElement> {
    public static TsoExpandElement build(String id, JsPropertyMap<?> service) {
        return new TsoExpandElement(div(), id, service);
    }
    protected final HTMLContainerBuilder<HTMLDivElement> controller = div().css("controller");
    private final ButtonElement btnHide = ButtonElement.outline().css("button").before(IconElement.icon("close")).text("Close");
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private final String id;
    private final InterpretationSubject[] sections;
    private final PreviewElement preview = PreviewElement.build();
    private TsoExpandElement(HTMLContainerBuilder<HTMLDivElement> e, String id, JsPropertyMap<?> service) {
        super(e.css("work").style("height: 100vh;"));
        _this = e;
        this.id = id;
        var interpretations = Section.build("Interpretations", Interpretations.build()).enable(true);
        sections = new InterpretationSubject[]{
                CancerType.build(),
                Hypermutability.build(),
                Section.build("SNV/Indel", SnvIndel.build()).enable(true),
                Section.build("CNV", Cnv.build()).enable(true),
                Section.build("Fusion", Fusion.build()).enable(true),
                interpretations,
                Section.build("QC", Qc.build()).enable(true)
        };
        com.gcgenome.lims.client.expand.tso.Subjects.snvs.subscribe(a->interpretations.enable(hasVariants()));
        com.gcgenome.lims.client.expand.tso.Subjects.cnvs.subscribe(a->interpretations.enable(hasVariants()));
        com.gcgenome.lims.client.expand.tso.Subjects.fusions.subscribe(a->interpretations.enable(hasVariants()));
        layout();
        btnHide.onClick(evt->fireStateChangeEvent());
    }
    private boolean hasVariants() {
        return com.gcgenome.lims.client.expand.tso.Subjects.variants()
                .anyMatch(k -> (k.tier() == InterpretationTso.Tier.Tier1) || (k.tier() == InterpretationTso.Tier.Tier2));
    }
    private void layout() {
        var div = div().style("height: calc(100% - 55px); overflow: auto;");
        for(var section: sections) div.add(section);
        _this.add(div)
             .add(controller.add(span().style("margin-left: 10px;")
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
            InterpretationApi.interpretation(Subjects.sample.getValue(), code).catch_(error->Promise.resolve(new InterpretationTso())),
            SnvApi.selected2(Subjects.sample.getValue(), code))
        .then(c->{
            InterpretationTso param1 = Js.cast(c[0]);
            var predefined = param1.variants!=null && param1.variants.length>0;
            List<JsPropertyMap<?>> param2 = Js.cast(c[1]);
            var interpretation = concat(param1, param2);
            if(interpretation.cancerCategory==null || interpretation.hypermutability==null || interpretation.cancerType==null || interpretation.qc==null)
                return AnalysisApi.analysis(Subjects.sample.getValue())
                        .then(array->Promise.resolve(pickLastAnalysis(code, array)))
                        .then(analysis->{
                            JsPropertyMap<?> values = Js.asPropertyMap(analysis.get("values"));
                            if(interpretation.cancerCategory==null && values.has("tissue")) interpretation.cancerCategory = values.get("tissue").toString();
                            if(interpretation.cancerType==null && values.has("cancer_type")) interpretation.cancerType = values.get("cancer_type").toString();
                            if(interpretation.hypermutability==null) {
                                var hypermutability = new InterpretationTso.Hypermutability();
                                interpretation.hypermutability = hypermutability;
                                if(values.has("tmb")) hypermutability.tmb = values.get("tmb").toString();
                                if(values.has("msi_status")) hypermutability.msi = values.get("msi_status").toString();
                                if(values.has("msi_score")) hypermutability.msiScore = Double.parseDouble(values.get("msi_score").toString());
                            }
                            if(interpretation.qc==null) {
                                var qc = new InterpretationTso.Qc();
                                interpretation.qc = qc;
                                if(values.has("qc_snv_tmb")) qc.snvQc = values.get("qc_snv_tmb").toString();
                                if(values.has("qc_cnv")) qc.cnvQc = values.get("qc_cnv").toString();
                                if(values.has("qc_msi")) qc.msiQc = values.get("qc_msi").toString();
                                if(values.has("qc_rna")) qc.rnaQc = values.get("qc_rna").toString();
                                if(values.has("tumor_purity")) qc.purity = Double.parseDouble(values.get("tumor_purity").toString());
                                if(values.has("pct_exon_100x")) qc.pctExonOver100x = Double.parseDouble(values.get("pct_exon_100x").toString());
                                if(values.has("pct_exon_1000x")) qc.pctExonOver1000x = Double.parseDouble(values.get("pct_exon_1000x").toString());
                                if(values.has("mad")) qc.mad = Double.parseDouble(values.get("mad").toString());
                                if(values.has("median_bin_count")) qc.mbc = Double.parseDouble(values.get("median_bin_count").toString());
                                if(values.has("usable_msi_sites")) qc.usableMsi = Double.parseDouble(values.get("usable_msi_sites").toString());
                                if(values.has("total_on_target_reads")) try { qc.onTargetReads = Double.parseDouble(values.get("total_on_target_reads").toString()); } catch(Exception e) {};
                                if(values.has("median_cv_for_genes_with_500x")) try { qc.medianCvOver500x = Double.parseDouble(values.get("median_cv_for_genes_with_500x").toString()); } catch(Exception e) {};
                                if(values.has("median_exon_coverage")) try { qc.medianExonCoverage = Double.parseDouble(values.get("median_exon_coverage").toString()); } catch(Exception e) {};
                                if(values.has("msaf")) try { qc.msaf = Double.parseDouble(values.get("msaf").toString()); } catch(Exception e) {};
                            }
                            if(!predefined && values.has("interpretation")) {
                                var tmp = Js.asPropertyMap(values.get("interpretation"));
                                var variants = new ArrayList<InterpretationTso.Variant>();
                                var prev = interpretation.variants!=null ? Arrays.stream(interpretation.variants).map(v->{
                                    if(v.kind() == InterpretationTso.VariantKind.SNV) return ((InterpretationTso.Snv)v).snv;
                                    else return null;
                                }).collect(Collectors.toCollection(HashSet::new)) : new HashSet<>();

                                if(tmp.has("variants")) for(var item: Js.asArray(tmp.get("variants"))) {
                                    var var = Js.asPropertyMap(item);
                                    if(!var.has("kind")) continue;
                                    var kind = var.get("kind").toString();
                                    InterpretationTso.Tier tier = null;
                                    String gene = null;
                                    String interpret = null;
                                    String significance = "-";
                                    if(var.has("tier")) tier = InterpretationTso.Tier.valueOf(var.get("tier").toString());
                                    if(var.has("gene")) gene = var.get("gene").toString();
                                    if(var.has("interpretation")) interpret = var.get("interpretation").toString();
                                    if(var.has("significance")) significance = var.get("significance").toString();
                                    if("SNV".equalsIgnoreCase(kind)) {
                                        var variant = new InterpretationTso.Snv();
                                        variant.kind(InterpretationTso.VariantKind.SNV).tier(tier);
                                        variant.gene = gene;
                                        variant.interpretation = interpret;
                                        variant.significance = significance;
                                        if(var.has("snv")) variant.snv = var.get("snv").toString();
                                        if(var.has("analysis")) variant.analysis = var.get("analysis").toString();
                                        if(var.has("hgvsc")) variant.hgvsc = var.get("hgvsc").toString();
                                        if(var.has("hgvsp")) variant.hgvsp = var.get("hgvsp").toString();
                                        if(var.has("vaf")) variant.vaf = var.get("vaf").toString();
                                        if(var.has("depth")) variant.depth = Double.parseDouble(var.get("depth").toString());
                                        if(prev.contains(variant.snv)) variants.add(variant);
                                    } else if("CNV".equalsIgnoreCase(kind)) {
                                        var variant = new InterpretationTso.Cnv();
                                        variant.kind(InterpretationTso.VariantKind.CNV).tier(tier);
                                        variant.gene = gene;
                                        variant.interpretation = interpret;
                                        variant.significance = significance;
                                        if(var.has("cnv")) variant.cnv = var.get("cnv").toString();
                                        if(var.has("type")) variant.type = var.get("type").toString();
                                        if(var.has("copy_number")) variant.copyNumber = var.get("copy_number").toString();
                                        variants.add(variant);
                                    } else if("FUSION".equalsIgnoreCase(kind)) {
                                        var variant = new InterpretationTso.Fusion();
                                        variant.kind(InterpretationTso.VariantKind.FUSION).tier(tier);
                                        variant.gene = gene;
                                        variant.interpretation = interpret;
                                        variant.significance = significance;
                                        if(var.has("vaf")) variant.vaf = var.get("vaf").toString();
                                        if(var.has("depth")) variant.depth = Double.parseDouble(var.get("depth").toString());
                                        if(var.has("fusion")) variant.fusion = var.get("fusion").toString();
                                        if(var.has("read_count")) variant.readCount = var.get("read_count").toString();
                                        variants.add(variant);
                                    }
                                }
                                interpretation.variants = variants.toArray(new InterpretationTso.Variant[0]);
                            }
                            return Promise.resolve(interpretation);
                        }).catch_(e->Promise.resolve(interpretation));
            else return Promise.resolve(interpretation);
        }).then(obj->{
            Subjects.interpretation.next(obj);
            return null;
        });
    }
    // interpretation 변이가 snvs에 있는지 확인해서 리스트 만들고 + interpretation에 없는 변이 목록
    private static InterpretationTso concat(InterpretationTso interpretation, List<JsPropertyMap<?>> snvs) {
        if(interpretation.variants==null || interpretation.variants.length<=0) interpretation.variants = new InterpretationTso.Variant[0];
        var sets = snvs.stream().collect(Collectors.toMap(c->c.get("id"), c->c));
        var proven = Arrays.stream(interpretation.variants).filter(s->{
            if(s.kind() == InterpretationTso.VariantKind.SNV) {
                InterpretationTso.Snv cast = Js.cast(s);
                return sets.containsKey(cast.snv);
            }
            else return true;
        }).peek(s->{
            if(s.kind() == InterpretationTso.VariantKind.SNV) {
                InterpretationTso.Snv cast = Js.cast(s);
                var tier = getSafety(sets.get(cast.snv), "report");
                cast.tier(tier!=null? InterpretationTso.Tier.valueOf(tier):null);
            }
        }).collect(Collectors.toList());
        var newbee = snvs.stream().map(TsoExpandElement::map).filter(v->!contains(proven, v)).collect(Collectors.toList());
        interpretation.variants = Stream.concat(proven.stream(), newbee.stream()).toArray(InterpretationTso.Variant[]::new);
        return interpretation;
    }
    private static boolean contains(List<InterpretationTso.Variant> list, InterpretationTso.Variant a) {
        return list.stream().anyMatch(v->equals(v, a));
    }
    private static boolean equals(InterpretationTso.Variant a, InterpretationTso.Variant b) {
        if(a.kind()!=b.kind()) return false;
        if(a.kind() == InterpretationTso.VariantKind.SNV) return equals((InterpretationTso.Snv)a, (InterpretationTso.Snv)b);
        else return false;
    }
    private static boolean equals(InterpretationTso.Snv a, InterpretationTso.Snv b) {
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
    private static InterpretationTso.Snv map(JsPropertyMap<?> map) {
        var variant = new InterpretationTso.Snv();
        var tier = getSafety(map, "report");
        variant.kind(InterpretationTso.VariantKind.SNV);
        variant.snv = getSafety(map, "id");
        variant.tier(tier!=null? InterpretationTso.Tier.valueOf(tier):null);
        variant.analysis = getSafety(map, "analysis");
        variant.gene = getSafety(map, "symbol");
        variant.hgvsc = getSafety(map, "oncokb_hgvsc");
        variant.hgvsp = getSafety(map, "oncokb_hgvsp")!=null ? getSafety(map, "oncokb_hgvsp") : "p.?";
        variant.vaf = getSafety(map, "vaf(%)");
        variant.depth = Double.parseDouble(map.get("depth").toString());
        return variant;
    }
    private static String getSafety(JsPropertyMap<?> map, String key) {
        if(map.has(key)) {
            var value = map.get(key);
            if(value != null ) return value.toString();
            else return null;
        }
        return null;
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
    public TsoExpandElement that() {
        return this;
    }
}
