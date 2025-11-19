package com.gcgenome.lims.client.domain;

import jsinterop.annotations.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Getter(onMethod_= {@JsOverlay, @JsIgnore})
@Accessors(fluent = true)
public final class Worklist {
    private String id;
    @JsProperty(name="create_at")
    private String createAt;
    @JsProperty(name="create_by")
    private String createBy;
    @JsProperty(name="last_modify_at")
    private String lastModifyAt;
    private String title;
    private String status;
    private String remark;
    private String domain;
    @JsProperty(name="sample_count")
    private Double sampleCount;
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String serial;
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String prefix;
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private Double idx;
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String note;

    @JsOverlay
    @JsIgnore
    public Integer sampleCount() {
        if(sampleCount==null) return null;
        double v = sampleCount;
        return (int)v;
    }
}
