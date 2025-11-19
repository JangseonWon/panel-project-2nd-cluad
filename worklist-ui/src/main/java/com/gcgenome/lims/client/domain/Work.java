package com.gcgenome.lims.client.domain;

import jsinterop.annotations.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Getter(onMethod_= {@JsOverlay, @JsIgnore})
@Accessors(fluent = true)
public final class Work {
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String id;
    private Request[] requests;
    private String worklist;
    private Double index;
    private String type;
    private String gid;
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String serial;
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String prefix;
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private Double idx;
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String position;
    @JsProperty(name="gestational_week")
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String gestationalWeek;        // 임신주수
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private Double weight;                 // 체중 (소수점 포함 가능)
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private Double height;                 // 키
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private Double bmi;                    // BMI
    @JsProperty(name="number_of_fetuses")
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private Double numberOfFetuses;        // 태아수
    @JsProperty(name="ivf_procedure")
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String ivfProcedure;           // 시험관시술여부
    @JsProperty(name="triple_test_result")
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String tripleTestResult;       // 트리플검사소견
    @JsProperty(name="other_findings")
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String otherFindings;          // 기타특이소견
    @JsProperty(name="ultrasound_findings")
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String ultrasoundFindings;     // 초음파특이소견
    @JsProperty(name="additional_tests")
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private Boolean additionalTests;       // 추가검사
    @JsProperty(name="nuchal_translucency")
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String nuchalTranslucency;     // 목덜미투명대 (단위는 mm)
    @JsProperty(name="fetal_sex")
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private Boolean fetalSex;              // 태아성별(선택/미선택)
    @JsProperty(name="sequencing_file_name")
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String sequencingFileName;
    @JsProperty(name="i7_index_name")
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String i7indexName;
    @JsProperty(name="i5_index_name")
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String i5indexName;
    @JsProperty(name="i7_index_sequence")
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String i7IndexSequence;
    @JsProperty(name="i5_index_sequence")
    @Setter(onMethod_= {@JsOverlay, @JsIgnore})
    private String i5IndexSequence;
}
