package com.gcgenome.lims.dto;

import jsinterop.annotations.*;
import lombok.experimental.Accessors;

import java.util.Arrays;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
public class InterpretationSomatic {
    @JsProperty(name="cancer_type")
    public String cancerType;
    public Result[] results;
    @JsProperty(name="qc_dna")
    public String qcDna;
    @JsProperty(name="qc_library")
    public String qcLibrary;
    @JsProperty(name="qc_sequencing")
    public String qcSequencing;
    @JsProperty(name="mean_depth")
    public String meanDepth;
    public String coverage;
    @JsProperty(name="drug_phenotypes")
    public DrugPhenotype[] drugPhenotypes;
    public Boolean revision;

    @JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
    @Accessors(fluent=true)
    public static final class Result {
        private String tier;
        public Variant[] variants;
        public String interpretation;
        @JsOverlay
        @JsIgnore
        public Tier tier() {
            if(tier == null) return null;
            return Arrays.stream(Tier.values()).filter(c->c.name().equals(tier)).findFirst().orElse(null);
        }
        @JsOverlay
        @JsIgnore
        public Result tier(Tier tier) {
            this.tier = tier.name();
            return this;
        }
    }
    public enum Tier {
        Tier1, Tier2, Tier3
    }
    @JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
    public static final class Variant {
        public String snv;
        public String analysis;
        public String gene;
        public String hgvsc;
        public String hgvsp;
        public Double vaf;
        private Double depth;
        public String cosmic;
        public String interpretation;
        @JsOverlay
        @JsIgnore
        public Variant depth(int depth) {
            this.depth = depth + 0.0;
            return this;
        }
        @JsOverlay
        @JsIgnore
        public Integer depth() {
            if(depth == null) return null;
            else return depth.intValue();
        }
    }
    @JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
    public static final class DrugPhenotype {
        public String gene;
        public String diplotype;
        @JsProperty(name="allele_status")
        public String alleleStatus;
        public String phenotype;
        public String result;
        public String interpretation;
    }
}
