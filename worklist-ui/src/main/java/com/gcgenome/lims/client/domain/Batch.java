package com.gcgenome.lims.client.domain;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import lombok.experimental.Accessors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Accessors(fluent = true)
public final class Batch {
    public String id;
    public String serial;
    public String infix;
    public double idx;
    public String note;
}
