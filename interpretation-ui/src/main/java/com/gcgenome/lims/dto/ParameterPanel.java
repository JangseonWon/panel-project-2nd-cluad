package com.gcgenome.lims.dto;

import jsinterop.annotations.*;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

import java.util.Map;

@JsType(isNative = true, namespace= JsPackage.GLOBAL, name="Object")
public final class ParameterPanel {
    private String sex;
    public InterpretationPanel previous;
    public JsPropertyMap<Object> disease;
    public String suffix;
    @JsOverlay
    @JsIgnore
    public void diseases(Map<String, Disease[]> map) {
        disease = Js.asPropertyMap(new Object());
        for(var key: map.keySet()) disease.set(key, map.get(key));
    }
    @JsOverlay
    @JsIgnore
    public ParameterPanel sex(Sex sex) {
        if(sex == null) this.sex = null;
        else this.sex = sex.name();
        return this;
    }
    @JsOverlay
    @JsIgnore
    public Sex sex() {
        return Sex.valueOf(this.sex);
    }
    @JsType(isNative = true, namespace= JsPackage.GLOBAL, name="Object")
    public final static class Disease {
        @JsProperty(name="full_name")
        public String fullName;
        public String abbreviation;
        public String[] inheritance;
    }
    public enum Sex {
        F, M
    }
}