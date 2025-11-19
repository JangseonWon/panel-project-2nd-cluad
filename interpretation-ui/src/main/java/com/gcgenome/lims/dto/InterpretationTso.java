package com.gcgenome.lims.dto;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
public class InterpretationTso {
    @JsProperty(name="cancer_category")
    public String cancerCategory;
    @JsProperty(name="cancer_type")
    public String cancerType;
    @JsProperty(name="variants")
    public Variant[] variants;
    public Hypermutability hypermutability;
    public InterpretationTso.Qc qc;
    public Method method;

    @JsOverlay
    public final List<Snv> snvs() {
        if(variants==null) return new LinkedList<>();
        return Arrays.stream(variants).filter(c->c.kind() == InterpretationTso.VariantKind.SNV).map(c->(InterpretationTso.Snv) Js.cast(c)).collect(Collectors.toList());
    }
    @JsOverlay
    public final List<Cnv> cnvs() {
        if(variants==null) return new LinkedList<>();
        return Arrays.stream(variants).filter(c->c.kind() == InterpretationTso.VariantKind.CNV).map(c->(InterpretationTso.Cnv) Js.cast(c)).collect(Collectors.toList());
    }
    @JsOverlay
    public final List<Fusion> fusion() {
        if(variants==null) return new LinkedList<>();
        return Arrays.stream(variants).filter(c->c.kind() == VariantKind.FUSION).map(c->(InterpretationTso.Fusion) Js.cast(c)).collect(Collectors.toList());
    }
    @JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
    public static class Method {
        public String region;
        public String panel;
        public String method;
        public String sequencing;
        public String pipeline;
        public String reference;
        @JsProperty(name="gene_sets")
        public GeneSet[] geneSets;
        @JsProperty(name="immunotherapy_info")
        public String immunotherapyInfo;
        @JsProperty(name="qc_info")
        public String qcInfo;
        @JsProperty(name="fusion_info")
        public String fusionInfo;
        public String[] limitations;
    }
    @JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
    public static class Variant {
        private String kind;
        private String tier;
        public String gene;
        public String significance;
        public String interpretation;
        @JsOverlay
        public final VariantKind kind() {
            if(kind==null) return null;
            return VariantKind.valueOf(kind);
        }
        @JsOverlay
        public final Tier tier() {
            if(tier==null) return null;
            return Tier.valueOf(tier);
        }
        @JsOverlay
        public final Variant kind(VariantKind kind) {
            if(kind==null) this.kind = null;
            else this.kind = kind.name();
            return this;
        }
        @JsOverlay
        public final Variant tier(Tier tier) {
            if(tier==null) this.tier = null;
            else this.tier = tier.name();
            return this;
        }
    }
    public enum VariantKind {
        SNV, CNV, FUSION
    }
    @JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
    public static class Snv extends Variant {
        public String snv;
        public String analysis;
        public String hgvsc;
        public String hgvsp;
        public String vaf;
        public Double depth;
    }
    @JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
    public static class Cnv extends Variant {
        public String cnv;
        public String type;
        @JsProperty(name="copy_number")
        public String copyNumber;
    }
    @JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
    public static class Fusion extends Variant {
        public String fusion;
        @JsProperty(name="read_count")
        public String readCount;
        public String vaf;
        public Double depth;
    }
    @JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
    public static class GeneSet {
        public String label;
        public String[] genes;
    }
    @JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
    public static class Hypermutability {
        public String tmb;
        public String msi;
        @JsProperty(name="msi_score")
        public Double msiScore;
    }
    @JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
    public static class Qc {
        @JsProperty(name="snv_qc")
        public String snvQc;
        @JsProperty(name="cnv_qc")
        public String cnvQc;
        @JsProperty(name="msi_qc")
        public String msiQc;
        @JsProperty(name="rna_qc")
        public String rnaQc;
        public String interpretation;
        public Double purity;
        @JsProperty(name="pct_exon_over_100x")
        public Double pctExonOver100x;
        @JsProperty(name="pct_exon_over_1000x")
        public Double pctExonOver1000x;
        @JsProperty(name="median_exon_coverage")
        public Double medianExonCoverage;
        public Double mad;
        public Double mbc;
        @JsProperty(name="usable_msi")
        public Double usableMsi;
        @JsProperty(name="on_target_reads")
        public Double onTargetReads;
        @JsProperty(name="median_cv_over_500x")
        public Double medianCvOver500x;
        public Double msaf;
    }
    public enum Tier {
        Tier1, Tier2, Tier3
    }
}
