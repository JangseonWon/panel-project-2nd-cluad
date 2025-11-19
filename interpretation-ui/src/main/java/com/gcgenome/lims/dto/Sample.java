package com.gcgenome.lims.dto;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace= JsPackage.GLOBAL, name="Object")
public final class Sample {
    public Double id;
    @JsProperty(name="sample_type")
    public String sampleType;
    @JsProperty(name="date_sampling")
    public Double dateSampling;

}
