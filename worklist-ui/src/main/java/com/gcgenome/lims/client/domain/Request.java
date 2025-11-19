package com.gcgenome.lims.client.domain;

import com.google.gwt.i18n.client.DateTimeFormat;
import jsinterop.annotations.*;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Date;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Getter(onMethod_= {@JsOverlay, @JsIgnore})
@Accessors(fluent=true)
public final class Request {
    private Sample sample;
    private Service service;
    private Organization requester;
    @JsProperty(name="date_request")
    private Double dateRequest;
    @JsProperty(name="date_reception")
    private Double dateReception;
    @JsProperty(name="date_due")
    private Double dateDue;
    @JsProperty(name="date_due_publish")
    private Double dateDuePublish;
    private String remark;
    @JsOverlay @JsIgnore
    public String id() {
        try {
            return sample.id() + "$" + service.id();
        } catch(Exception e) {
            return null;
        }
    }
    @JsOverlay @JsIgnore
    private final static DateTimeFormat DEFAULT_DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
    @JsOverlay @JsIgnore
    public String dateRequest() {
        if(dateRequest==null) return null;
        return DEFAULT_DATE_FORMAT.format(new Date(dateRequest.longValue()));
    }
    @JsOverlay @JsIgnore
    public String dateReception() {
        if(dateReception==null) return null;
        return DEFAULT_DATE_FORMAT.format(new Date(dateReception.longValue()));
    }
    @JsOverlay @JsIgnore
    public String dateDue() {
        if(dateDue==null) return null;
        return DEFAULT_DATE_FORMAT.format(new Date(dateDue.longValue()));
    }
    @JsOverlay @JsIgnore
    public String dateDuePublish() {
        if(dateDuePublish==null) return null;
        return DEFAULT_DATE_FORMAT.format(new Date(dateDuePublish.longValue()));
    }
}
