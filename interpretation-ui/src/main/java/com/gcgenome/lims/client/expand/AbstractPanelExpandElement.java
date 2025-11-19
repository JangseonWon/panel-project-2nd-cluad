package com.gcgenome.lims.client.expand;

import com.gcgenome.lims.api.*;
import com.gcgenome.lims.client.ExpandElement;
import com.gcgenome.lims.client.InterpretationI18nUtil;
import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.client.WindowState;
import com.gcgenome.lims.client.expand.panel.InterpretationPanelFactory;
import com.gcgenome.lims.client.expand.panel.ResultWriter;
import com.gcgenome.lims.client.expand.panel.param.InterpretationParamDialog;
import com.gcgenome.lims.dto.InterpretationPanel;
import com.gcgenome.lims.dto.Message;
import com.gcgenome.lims.dto.ParameterPanel;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import lombok.NonNull;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.HTMLElementBuilder;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.*;

import static elemental2.core.Global.JSON;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.span;

public abstract class AbstractPanelExpandElement<SELF extends HTMLElementBuilder<HTMLDivElement, SELF>> extends HTMLElementBuilder<HTMLDivElement, SELF> implements ExpandElement<HTMLDivElement> {
    protected final HTMLContainerBuilder<HTMLDivElement> controller = div().css("controller");
    private final ButtonElement btnAuto = ButtonElement.outline().css("button").before(IconElement.icon("smart_button")).text("Interpretation");
    private final ButtonElement btnNegative = ButtonElement.outline().css("button").before(IconElement.icon("search_off")).text("Negative");
    private final ButtonElement btnSave = ButtonElement.outline().css("button").before(IconElement.icon("save")).text("Save");
    private final ButtonElement btnPreview = ButtonElement.outline().css("button").before(IconElement.icon("preview")).text("Preview");
    private final ButtonElement btnHide = ButtonElement.outline().css("button").before(IconElement.icon("close")).text("Close");
    private final String id;
    protected final long sample;
    protected final String code;
    private final JsPropertyMap<?> service;
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private final ResultWriter<?>[] sections;
    protected AbstractPanelExpandElement(HTMLContainerBuilder<HTMLDivElement> e, String id, long sample, JsPropertyMap<?> service, ResultWriter<?>... sections) {
        super(e);
        this.id = id;
        this.sample = sample;
        this.service = service;
        this.code = (String)service.get("code");
        this.sections = sections;
        _this = e.css("work").style("height: 100vh;");
        layout();
        btnHide.onClick(evt->fireStateChangeEvent());
        btnSave.onClick(evt->save());
        btnAuto.onClick(evt->auto());
        btnPreview.onClick(evt->preview());
        btnNegative.onClick(evt->negative());
    }
    private void layout() {
        var div = div().style("height: calc(100% - 55px); overflow: auto;");
        if(sections!=null) for(ResultWriter<?> section: sections) div.add(section);
        _this.add(div)
             .add(controller.add(span().add(btnAuto).add(btnNegative))
                            .add(span().style("margin-left: 10px;").add(btnSave).add(btnPreview).add(btnHide)));
    }
    @Override
    public final void update() {
        DomGlobal.window.parent.postMessage(JSON.stringify(Message.builder().id(id).type(Message.MessageType.STRETCH).build()), "*");
        InterpretationApi.interpretation(sample, code)
                .catch_(error->initialize())
                .then(reports->InterpretationPanelFactory.panel(sample, service, reports))
                .then(this::update);
    }
    protected abstract Promise<InterpretationPanel> initialize();
    private Promise<Boolean> update(@NonNull InterpretationPanel map) {
        ReportApi.state(sample, code).then(state->{
            boolean isFinal = "F".equalsIgnoreCase(state);
            btnSave.enabled(!isFinal);
            btnPreview.enabled(!isFinal);
            btnSave.text(isFinal?"검사 완료":"SAVE");
            return null;
        });

        if(sections!=null) for(var section: sections) section.update(map);
        return Promise.resolve(true);
    }
    private InterpretationPanel value() {
        var value = new InterpretationPanel();
        if(sections!=null) for(var section: sections) section.append(value);
        return value;
    }
    private void auto() {
        if(!InterpretationI18nUtil.confirmAutoInterpretation()) return;
        SampleApi.sex(sample)
                .then(sex->{
                    ParameterPanel.Sex s = null;
                    try { s = ParameterPanel.Sex.valueOf(sex); } catch(Exception ignore){}
                    var value = value();
                    List<InterpretationPanel.Variant> variants = new LinkedList<>();
                    if(value.variants!=null) Arrays.stream(value.variants).forEach(variants::add);
                    if(value.addendum!=null && value.addendum.variants!=null) Arrays.stream(value.addendum.variants).forEach(variants::add);
                    var dialog = InterpretationParamDialog.build(s, variants, value);
                    return dialog.onSubmit();
                }).then(param->{
                    ProgressApi.open(false);
                    btnAuto.enabled(false);
                    return InterpretationApi.interpretation(sample, code, param);
                }).then(reports->InterpretationPanelFactory.panel(sample, service, reports))
                .then(this::update)
                .finally_(()->btnAuto.enabled(true))
                .finally_(ProgressApi::close);
    }
    private void negative() {
        if(!DomGlobal.confirm("음성 소견을 입력합니다. 현재 편집하신 내용이 있다면 소실됩니다.")) return;
        var param = new ParameterPanel();
        var prev = value();
        param.previous = new InterpretationPanel();
        param.previous.meanDepth = prev.meanDepth;
        param.previous.coverage = prev.coverage;
        param.previous.authors = prev.authors;
        param.previous.revision = prev.revision;
        ProgressApi.open(false);
        btnNegative.enabled(false);
        SampleApi.sex(sample)
                .then(sex->{
                    param.sex(ParameterPanel.Sex.valueOf(sex));
                    return Promise.resolve(param);
                }).then(p->InterpretationApi.interpretation(sample, code, p))
                .then(reports->InterpretationPanelFactory.panel(sample, service, reports))
                .then(this::update)
                .finally_(()->btnNegative.enabled(true))
                .finally_(ProgressApi::close);
    }
    private void save() {
        if(!InterpretationI18nUtil.validateEnglishReport(Js.cast(value()))) return;
        if(!DomGlobal.confirm("저장합니다.")) return;
        ProgressApi.open(false);
        btnSave.enabled(false);
        InterpretationApi.save(sample, code, value())
                .then(reports->InterpretationPanelFactory.panel(sample, service, (InterpretationPanel) reports))
                .then(this::update)
                .finally_(()-> DomGlobal.alert("저장되었습니다."))
                .finally_(()->btnSave.enabled(true))
                .finally_(ProgressApi::close);
    }
    private void preview() {
        var value = value();
        btnPreview.enabled(false);
        ReportApi.preview(sample, code, value)
        .then(blob->{
            var preview = PreviewElement.build(blob);
            element().append(preview.element());
            return preview.onSave();
        }).finally_(()-> ProgressApi.open(true))
        .then(ok->VersionCheckApi.isNew(sample, code).then(isNew->
                isNew?Promise.resolve(""):DescriptionDialog.dialog())).finally_(()->ProgressApi.progress(0.3))
        .then(description->InterpretationApi.save(sample, code, value)
            .then(reports->InterpretationPanelFactory.panel(sample, service, (InterpretationPanel) reports).finally_(()->ProgressApi.progress(0.6))
                .then(saved->ReportApi.print(sample, code, description)).finally_(()->ProgressApi.progress(0.9))
                .then(report->{
                    var createAt = Double.valueOf(Js.asDouble(report.get("create_at"))).longValue();
                    return ReportApi.publish(sample, code, createAt);
                }).then(e->Promise.resolve(reports))))
        .then(e->this.update((InterpretationPanel) e))
        .then(d->{
            DomGlobal.alert("검사가 완료되었습니다.");
            return null;
        }).finally_(()->btnPreview.enabled(true))
        .finally_(ProgressApi::close).catch_(e->{
            if(e!=null) DomGlobal.alert(e);
            return Promise.reject(e);
        });
    }
    private final Set<StateChangeEventListener<WindowState>> listeners = new HashSet<>();
    @Override
    public Collection<StateChangeEventListener<WindowState>> listeners() {
        return listeners;
    }
    @Override
    public WindowState state() {
        return WindowState.COLLAPSE;
    }
}
