package com.gcgenome.lims.client.expand.tso;

import com.gcgenome.lims.dto.InterpretationTso;
import com.google.gwt.core.client.Scheduler;
import elemental2.core.JsArray;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.rx.subject.Subject;

import java.util.Arrays;
import java.util.stream.Stream;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;
import static net.sayaya.rx.subject.Subject.subject;

public class Subjects {
    public final static Subject<String> cancerCategory = subject(String.class);
    public final static Subject<String> cancerType = subject(String.class);
    public final static Subject<InterpretationTso.Hypermutability> hypermutability = subject(InterpretationTso.Hypermutability.class);
    public final static BehaviorSubject<InterpretationTso.Snv[]> snvs = behavior(new InterpretationTso.Snv[0]);
    public final static BehaviorSubject<InterpretationTso.Cnv[]> cnvs = behavior(new InterpretationTso.Cnv[0]);
    public final static BehaviorSubject<InterpretationTso.Fusion[]> fusions = behavior(new InterpretationTso.Fusion[0]);
    public final static Subject<InterpretationTso.Qc> qc = subject(InterpretationTso.Qc.class);
    public final static BehaviorSubject<InterpretationTso.Method> method = behavior(null);
    static {
        com.gcgenome.lims.client.Subjects.interpretation.subscribe(interpretation->{ try {
            InterpretationTso cast = Js.cast(interpretation);
            cancerCategory.next(cast.cancerCategory);
            cancerType.next(cast.cancerType);
            hypermutability.next(cast.hypermutability);
            qc.next(cast.qc);
            snvs.next(cast.snvs().stream().toArray(InterpretationTso.Snv[]::new));
            cnvs.next(cast.cnvs().stream().toArray(InterpretationTso.Cnv[]::new));
            fusions.next(cast.fusion().stream().toArray(InterpretationTso.Fusion[]::new));
            if(cast.method!=null) method.next(cast.method);
            else cast.method = method.getValue();
        } catch(ClassCastException e) {e.printStackTrace();}});
        cancerCategory.subscribe(evt->{
            var _interpretation = com.gcgenome.lims.client.Subjects.interpretation.getValue();
            if(_interpretation == null) com.gcgenome.lims.client.Subjects.interpretation.next(new InterpretationTso());
            InterpretationTso cast = Js.cast(_interpretation);
            cast.cancerCategory = evt;
        });
        cancerType.subscribe(evt->{
            var _interpretation = com.gcgenome.lims.client.Subjects.interpretation.getValue();
            if(_interpretation == null) com.gcgenome.lims.client.Subjects.interpretation.next(new InterpretationTso());
            InterpretationTso cast = Js.cast(_interpretation);
            cast.cancerType = evt;
        });
        hypermutability.subscribe(evt->{
            var _interpretation = com.gcgenome.lims.client.Subjects.interpretation.getValue();
            if(_interpretation == null) com.gcgenome.lims.client.Subjects.interpretation.next(new InterpretationTso());
            InterpretationTso cast = Js.cast(_interpretation);
            cast.hypermutability = evt;
        });
        qc.subscribe(evt->{
            var _interpretation = com.gcgenome.lims.client.Subjects.interpretation.getValue();
            if(_interpretation == null) com.gcgenome.lims.client.Subjects.interpretation.next(new InterpretationTso());
            InterpretationTso cast = Js.cast(_interpretation);
            cast.qc = evt;
        });
        snvs.subscribe(evt->{
            var _interpretation = com.gcgenome.lims.client.Subjects.interpretation.getValue();
            if(_interpretation == null) com.gcgenome.lims.client.Subjects.interpretation.next(new InterpretationTso());
            InterpretationTso cast = Js.cast(_interpretation);
            Scheduler.get().scheduleDeferred(()-> cast.variants = Stream.of(
                    snvs.getValue(),
                    cnvs.getValue(),
                    fusions.getValue()
            ).flatMap(Arrays::stream).toArray(InterpretationTso.Variant[]::new));
        });
        cnvs.subscribe(evt->{
            var _interpretation = com.gcgenome.lims.client.Subjects.interpretation.getValue();
            if(_interpretation == null) com.gcgenome.lims.client.Subjects.interpretation.next(new InterpretationTso());
            InterpretationTso cast = Js.cast(_interpretation);
            Scheduler.get().scheduleDeferred(()-> cast.variants = Stream.of(
                    snvs.getValue(),
                    cnvs.getValue(),
                    fusions.getValue()
            ).flatMap(Arrays::stream).toArray(InterpretationTso.Variant[]::new));
        });
        fusions.subscribe(evt->{
            var _interpretation = com.gcgenome.lims.client.Subjects.interpretation.getValue();
            if(_interpretation == null) com.gcgenome.lims.client.Subjects.interpretation.next(new InterpretationTso());
            InterpretationTso cast = Js.cast(_interpretation);
            Scheduler.get().scheduleDeferred(()-> cast.variants = Stream.of(
                    snvs.getValue(),
                    cnvs.getValue(),
                    fusions.getValue()
            ).flatMap(Arrays::stream).toArray(InterpretationTso.Variant[]::new));
        });
        com.gcgenome.lims.client.Subjects.service.subscribe(svc->{
            if(svc==null) return;
            InterpretationTso.Method method = new InterpretationTso.Method();
            var m = findSafe(svc, "method", JsPropertyMap.class);
            method.region = findSafe(m, "region", String.class);
            method.panel = findSafe(m, "panel", String.class);
            method.method = findSafe(m, "probe", String.class);
            method.sequencing = findSafe(m, "sequencing", String.class);
            method.pipeline = findSafe(m, "pipeline", String.class);
            method.reference = findSafe(m, "reference", String.class);
            method.immunotherapyInfo = findSafe(svc, "immunotherapy_info", String.class);
            method.qcInfo = findSafe(svc, "qc_info", String.class);
            method.fusionInfo = findSafe(svc, "fusion_info", String.class);
            var snv = toGeneSet("small_variants", findSafe(svc, "small_variants", JsArray.class));
            var cnv = toGeneSet("copy_number_variants", findSafe(svc, "copy_number_variants", JsArray.class));
            var fusion = toGeneSet("fusion_variants", findSafe(svc, "fusion_variants", JsArray.class));
            method.geneSets = new InterpretationTso.GeneSet[] { snv, cnv, fusion };
            var limitations = findSafe(svc, "limitations", JsArray.class);
            if(limitations!=null) method.limitations = (String[]) limitations.asList().stream().toArray(String[]::new);
            Subjects.method.next(method);
        });
        Subjects.method.subscribe(method->{
            var _interpretation = com.gcgenome.lims.client.Subjects.interpretation.getValue();
            if(_interpretation == null) com.gcgenome.lims.client.Subjects.interpretation.next(new InterpretationTso());
            InterpretationTso cast = Js.cast(_interpretation);
            cast.method = method;
        });
    }
    private static <T> T findSafe(JsPropertyMap<?> map, String key, Class<T> clazz) {
        if(map.has(key)) {
            var value = map.get(key);
            if(value != null ) return (T) value;
            else return null;
        }
        return null;
    }
    private static InterpretationTso.GeneSet toGeneSet(String label, JsArray genes) {
        var geneset = new InterpretationTso.GeneSet();
        geneset.label = label;
        if(genes!=null) geneset.genes = (String[]) genes.asList().stream().toArray(String[]::new);
        return geneset;
    }
    public static Stream<InterpretationTso.Variant> variants() {
        return Stream.of(
                com.gcgenome.lims.client.expand.tso.Subjects.snvs.getValue(),
                com.gcgenome.lims.client.expand.tso.Subjects.cnvs.getValue(),
                com.gcgenome.lims.client.expand.tso.Subjects.fusions.getValue()
        ).flatMap(Arrays::stream);
    }
}