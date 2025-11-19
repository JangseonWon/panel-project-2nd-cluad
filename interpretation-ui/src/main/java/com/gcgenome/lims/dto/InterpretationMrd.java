package com.gcgenome.lims.dto;

import jsinterop.annotations.*;

import java.util.Arrays;

@JsType(isNative = true, namespace= JsPackage.GLOBAL, name="Object")
public final class InterpretationMrd {
    // For MRD
    @JsProperty(name="cancer_type")
    public String cancerType;
    public MrdHistory[] histories;

    // For MRD-Screen
    private String date;
    @JsProperty(name="input_dna")
    public Double inputDna;
    public InterpretationMrdScreen.MrdScreenGeneResult[] results;

    // Common
    @JsProperty(name="mutation_rate")
    private String mutationRate;
    @JsOverlay
    @JsIgnore
    public MrdHistory last() {
        return histories[0];
    }

    @JsType(isNative = true, namespace= JsPackage.GLOBAL, name="Object")
    public static final class MrdHistory {
        public String date;
        @JsProperty(name="input_dna")
        public Double inputDna;
        public MrdGeneResult[] results;
        @JsOverlay @JsIgnore
        public long nucleatedCells() {
            return Math.round(inputDna/6.5 * 1000);
        }
        @JsOverlay @JsIgnore
        public double pctClonalNucelatedCells(String gene) {
            return Arrays.stream(results).filter(r->gene.equalsIgnoreCase(r.gene)).findAny().get().equivalent() / (double) nucleatedCells();
        }
    }
    @JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
    public static final class MrdGeneResult {
        public String gene;
        public MrdCloneResult target;
        public MrdCloneResult lqic;
        public String result;
        public String interpretation;
        @JsOverlay @JsIgnore
        public long bCells() {
            return Math.round(target.readDepth * 100.0 / lqic.readDepth - 100);
        }
        @JsOverlay @JsIgnore
        public long equivalent() {
            return Math.round(target.clonalDepth * 100.0 / lqic.readDepth);
        }
        @JsOverlay @JsIgnore
        public float pctClonalBCells() {
            return equivalent() / (float) bCells();
        }
        @JsOverlay @JsIgnore
        public void result(Result result) {
            if(result==null) this.result = null;
            else this.result = result.name();
        }
        @JsOverlay @JsIgnore
        public Result result() {
            if(result==null) return null;
            return Result.valueOf(result);
        }
    }
    @JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
    public static final class MrdCloneResult {
        @JsProperty(name="read_depth")
        public Double readDepth;
        @JsProperty(name="clonal_depth")
        public Double clonalDepth;
    }
    public enum Result {
        DETECTED, NOT_DETECTED, NA, CUSTOM
    }
}
