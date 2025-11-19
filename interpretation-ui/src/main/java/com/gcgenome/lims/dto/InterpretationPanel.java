package com.gcgenome.lims.dto;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

import java.util.List;

@JsType(isNative = true, namespace= JsPackage.GLOBAL, name="Object")
public class InterpretationPanel {
    public String result;
    @JsProperty(name="result_text")
    public String resultText;
    @JsProperty(name="reason_for_referral")
    public String reasonForReferral;
    @JsProperty(name="clinical_information")
    public String clinicalInformation;
    @JsProperty(name="abbreviation_reference")
    public String abbreviationReference;
    @JsProperty(name="abbreviation_disease")
    public String abbreviationDisease;
    public String abbreviation;
    public String interpretation;
    @JsProperty(name="mean_depth")
    public String meanDepth;
    public String coverage;
    public Variant[] variants;
    public String recommendation;
    public InterpretationPanel addendum;
    @JsProperty(name="incidental_findings")
    public InterpretationPanel incidentalFindings;
    public List<Author> authors;
    public Boolean revision;
    @JsProperty(name="mlpa_result")
    public Mlpa mlpaResult;
    @JsType(isNative = true, namespace= JsPackage.GLOBAL, name="Object")
    public static class Variant {
        public String snv;
        public String analysis;
        public String gene;
        public String hgvsc;
        public String hgvsp;
        @JsProperty(name="origin_hgvsc")
        public String originHgvsc;
        @JsProperty(name="origin_hgvsp")
        public String originHgvsp;
        public String zygosity;
        public String disease;
        @JsProperty(name="disease_full_name")
        public String diseaseFullName;
        public String inheritance;
        @JsProperty(name="class")
        public String clazz;
        public String interpretation;
        @JsProperty(name="gen_phen_db")
        public String genPhenDb;

        // for client only
        public String mim;
        public List<VariantRaw.ReportInfo> reported;
    }
    @JsType(isNative = true, namespace= JsPackage.GLOBAL, name="Object")
    public static class Author {
        public String name;
        public String comment;
    }
    @JsType(isNative = true, namespace= JsPackage.GLOBAL, name="Object")
    public static class Mlpa {
        public String gene;
        public String result;
        public String exons;
        public String zygosity;
        @JsProperty(name="del_dup")
        public String delDup;
    }
}