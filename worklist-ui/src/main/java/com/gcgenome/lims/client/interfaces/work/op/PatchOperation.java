package com.gcgenome.lims.client.interfaces.work.op;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import lombok.experimental.Accessors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Accessors(fluent = true)
public class PatchOperation {
    public String op;
    public String path;
    public Object value;
}
