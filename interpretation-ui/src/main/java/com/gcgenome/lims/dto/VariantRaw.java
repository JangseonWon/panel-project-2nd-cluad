package com.gcgenome.lims.dto;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.*;
import jsinterop.base.Any;
import jsinterop.base.Js;
import lombok.Builder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static elemental2.core.Global.JSON;

@JsType(isNative = true, namespace= JsPackage.GLOBAL, name="Object")
public final class VariantRaw {
    public String snv;
    public String analysis;
    @JsProperty(name="genotype")
    public String zygosity;
    @JsProperty(name="class")
    private String clazz;
    private String reported;
    private String interpretation;
    private String genPhenDb;
    @JsOverlay
    public String gene() {
        return unwrap(Js.asPropertyMap(this).get("gene.refgene"));
    }
    @JsOverlay
    public String originHgvsc() {
        String hgvsc = unwrap(Js.asPropertyMap(this).get("hgvsc_in_mane"));
        if (hgvsc == null || hgvsc.isEmpty()) {
            hgvsc = unwrap(Js.asPropertyMap(this).get("hgvsc"));
       }
        return hgvsc;
    }
    @JsOverlay
    public String originHgvsp() {
        String hgvsp = unwrap(Js.asPropertyMap(this).get("hgvsp_in_mane"));
        if (hgvsp == null || hgvsp.isEmpty()) {
            hgvsp = unwrap(Js.asPropertyMap(this).get("hgvsp"));
        }
        return hgvsp;
    }
    @JsOverlay
    public String disease() {
        String disease = (String) Js.asPropertyMap(this).get("mim.disease");
        String inheritance = (String) Js.asPropertyMap(this).get("mim.inheritance");
        if(disease != null && inheritance != null) return disease + "%" + inheritance;
        return disease;
    }
    @JsOverlay
    @JsIgnore
    public String clazz() {
        if("P".equals(clazz)) clazz = "PV";
        else if("LP".equals(clazz)) clazz = "LPV";
        else if("LB".equals(clazz)) clazz = "LBV";
        else if("B".equals(clazz)) clazz = "BV";
        return clazz;
    }
    @JsOverlay
    @JsIgnore
    public InterpretationPanel.Variant toVariant() {
        InterpretationPanel.Variant var = new InterpretationPanel.Variant();
        var.snv = snv;
        var.analysis = analysis;
        var.gene = gene();
        var.originHgvsc = originHgvsc();
        var.originHgvsp = originHgvsp();
        var.zygosity = zygosity;
        var.clazz = clazz();
        var.mim = disease();
        var.reported = reported();
        var.interpretation = interpretation;
        return var;
    }
    @JsOverlay
    @JsIgnore
    private String unwrap(Object obj) {
        if(obj == null) return null;
        String p = (String)obj;
        if(p.startsWith("[") && p.endsWith("]")) {
            try {
                Any[] arr = Js.asArray(JSON.parse(p));
                return Arrays.stream(arr).map(String::valueOf).distinct().collect(Collectors.joining(", "));
            }catch (Exception e){
                return p.substring(1, p.length()-2);
            }
        } else return p;
    }
    @JsOverlay
    @JsIgnore
    public List<ReportInfo> reported() {
        if(this.reported == null) return new LinkedList<>();
        JavaScriptObject obj = (JavaScriptObject) JSON.parse(reported);
        Any[] split = Js.asArray(obj);
        return Arrays.stream(split).filter(Objects::nonNull).map(Any::asString).map(s->{
            int p1 = s.indexOf(":");
            int p2 = s.indexOf("=");
            long sample = Long.parseLong(s.substring(0, p1));
            String service = s.substring(p1+1, p2);
            String clazz = s.substring(p2+1);
            return ReportInfo.builder().sample(sample).service(service).clazz(clazz).build();
        }).collect(Collectors.toList());
    }
    @Builder
    public static class ReportInfo {
        public long sample;
        public String service;
        public String clazz;
    }
}
