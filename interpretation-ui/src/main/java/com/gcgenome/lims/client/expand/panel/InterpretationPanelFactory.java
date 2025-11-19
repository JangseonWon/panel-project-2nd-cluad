package com.gcgenome.lims.client.expand.panel;

import com.gcgenome.lims.api.SnvApi;
import com.gcgenome.lims.dto.InterpretationPanel;
import com.gcgenome.lims.dto.VariantRaw;
import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class InterpretationPanelFactory {
    private List<InterpretationPanel.Variant> tidy(InterpretationPanel.Variant[] previous, List<InterpretationPanel.Variant> proven) {
        var list = new LinkedList<InterpretationPanel.Variant>();
        Map<String, InterpretationPanel.Variant> map = proven!=null?proven.stream().collect(Collectors.toMap(p->p.snv, Function.identity())):new HashMap<>();
        Set<String> added = new HashSet<>();
        if(previous!=null) for(var prev: previous) {
            if(prev.reported==null) prev.reported = new LinkedList<>();
            if(map.containsKey(prev.snv)) {
                prev.mim = map.get(prev.snv).mim;
                prev.reported = map.get(prev.snv).reported;
                prev.interpretation = map.get(prev.snv).interpretation;
                prev.genPhenDb = map.get(prev.snv).genPhenDb;
                prev.originHgvsc = map.get(prev.snv).originHgvsc;
                prev.originHgvsp = map.get(prev.snv).originHgvsp;
                list.add(prev);
                added.add(prev.snv);
            } else if(prev.snv==null) list.add(prev);
        }
        if(proven!=null) for(var var: proven) if(!added.contains(var.snv)) list.add(var);
        return list;
    }
    public Promise<InterpretationPanel> panel(long sample, JsPropertyMap<?> service, InterpretationPanel previous) {
        var genes = Js.asArray(service.get("genes"));
        var addenda = service.has("addendum")? Arrays.stream((Js.asArray(service.get("addendum")))).collect(Collectors.toSet()): new HashSet<>();
        Set<String> prevCores = previous.variants!=null?Arrays.stream(previous.variants).map(v->v.snv).collect(Collectors.toSet()) : new HashSet<>();
        Set<String> prevAddendum = (previous.addendum!=null && previous.addendum.variants!=null)?Arrays.stream(previous.addendum.variants).map(v->v.snv).collect(Collectors.toSet()) : new HashSet<>();
        Set<String> core = Arrays.stream(genes).map(Object::toString).filter(i->!addenda.contains(i)).collect(Collectors.toSet());
        String code = (String)service.get("code");
        return SnvApi.selected(sample, code).then(s->{
            var groupByCore = s.stream().map(VariantRaw::toVariant)
                    .collect(Collectors.groupingBy(proven-> {
                        if(prevCores.contains(proven.snv)) return true;
                        if(prevAddendum.contains(proven.snv)) return false;
                        return core.contains(proven.gene);
                    }));
            //                         Core                   Addendum
            var pair = Pair.build(groupByCore.get(true), groupByCore.get(false));
            previous.variants = tidy(previous.variants, pair.first).stream()
                    .peek(var->var.reported = var.reported.stream().filter(info->info.sample!=sample && !info.service.equals(code)).collect(Collectors.toList()))
                    .toArray(InterpretationPanel.Variant[]::new);
            if(pair.second!=null && !pair.second.isEmpty()) {
                if(previous.addendum==null) previous.addendum = new InterpretationPanel();
                previous.addendum.variants = tidy(previous.addendum.variants, pair.second).stream().toArray(InterpretationPanel.Variant[]::new);
            }
            return Promise.resolve(previous);
        });
    }
    private final static class Pair<A, B> {
        private final A first;
        private final B second;
        private Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
        public static <A, B> Pair<A, B> build(A first, B second) {
            return new Pair<>(first, second);
        }
    }
    private final static class Triple<A, B, C> {
        private final A first;
        private final B second;
        private final C third;
        private Triple(A first, B second, C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
        public static <A, B, C> Triple<A, B, C> build(A first, B second, C third) {
            return new Triple<>(first, second, third);
        }
    }
}
