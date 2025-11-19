package com.gcgenome.lims.dto;

import jsinterop.annotations.*;

import java.util.Arrays;

@JsType(isNative = true, namespace= JsPackage.GLOBAL, name="Object")
public final class InterpretationMrdScreen {
    public String date;
    @JsProperty(name="cancer_type")
    public String cancerType;
    @JsProperty(name="input_dna")
    public Double inputDna;
    public MrdScreenGeneResult[] results;
    @JsProperty(name="somatic_mutations")
    public SomaticMutation[] somaticMutations;
    @JsIgnore @JsOverlay
    public long nucleatedCells() {
        return Math.round(inputDna /6.5 * 1000);
    }

    @JsType(isNative = true, namespace= JsPackage.GLOBAL, name="Object")
    public static final class MrdScreenGeneResult {
        public String gene;
        public MrdScreenCloneResult[] clones;
        @JsProperty(name="depth_total")
        public Double depthTotal;
        @JsProperty(name="depth_lqic")
        public Double depthLqic;
        @JsProperty(name="length_lqic")
        public Double lengthLqic;
        public String interpretation;
        @JsIgnore @JsOverlay
        public Long depthClonal() {
            if(clones==null) return null;
            else {
                long sum = 0;
                for(MrdScreenCloneResult clone: clones) if(clone.depth!=null) sum += clone.depth;
                return sum;
            }
        }
        @JsIgnore @JsOverlay
        public double totalClonalCells() {
            if(clones==null || clones.length <=0) return 0;
            else return Arrays.stream(clones).mapToDouble(c->c.coverage(depthLqic!=null?depthLqic.longValue():0, bCells())).sum();
        }
        @JsIgnore @JsOverlay
        public long bCells() {
            return Math.round(depthTotal * 100.0 / depthLqic - 100);
        }
    }
    @JsType(isNative = true, namespace= JsPackage.GLOBAL, name="Object")
    public static final class MrdScreenCloneResult {
        public String no;
        @JsProperty(name="region_v")
        public String regionV;
        @JsProperty(name="region_j")
        public String regionJ;
        public Double length;
        public Double depth;
        public String sequence;
        @JsIgnore @JsOverlay
        public double coverage(long depthLqic, long bCells) {
            return equivalent(depthLqic) * 100.0 / bCells;
        }
        @JsIgnore @JsOverlay
        public long equivalent(long depthLqic) {
            return Math.round(depth * 100.0 / depthLqic);
        }
    }
    public enum Result {
        DETECTED, NOT_DETECTED
    }
    @JsType(isNative = true, namespace= JsPackage.GLOBAL, name="Object")
    public static final class SomaticMutation {
        @JsProperty(name="clone")
        public String clone;
        @JsProperty(name="hyper_mutation")
        public String hyperMutation;
        @JsProperty(name="mutation_rate")
        public String mutationRate;
    }
}
