package com.gcgenome.lims.client.expand;

import com.gcgenome.lims.client.ExpandElement;
import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.client.WindowState;
import com.gcgenome.lims.client.expand.elem.ButtonPreviewElement;
import com.gcgenome.lims.client.expand.elem.ButtonSaveElement;
import com.gcgenome.lims.client.expand.elem.PreviewElement;
import com.gcgenome.lims.client.expand.mrd.CancerType;
import com.gcgenome.lims.client.expand.mrd.MrdClones;
import com.gcgenome.lims.dto.Message;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.ButtonElementText;
import net.sayaya.ui.HTMLElementBuilder;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static elemental2.core.Global.JSON;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.span;

public class MrdExpandElement extends HTMLElementBuilder<HTMLDivElement, MrdExpandElement> implements ExpandElement<HTMLDivElement> {
    public static MrdExpandElement build(String id, long sample, JsPropertyMap<?> service) {
        return new MrdExpandElement(div(), id, sample, service);
    }
    protected final HTMLContainerBuilder<HTMLDivElement> controller = div().css("controller");
    private final ButtonElement btnHide = ButtonElement.outline().css("button").before(IconElement.icon("close")).text("Close");
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private final String id;
    private final long sample;
    private final JsPropertyMap<?> service;
    private final com.gcgenome.lims.client.expand.mrd.CancerType iptCancerType = CancerType.build();

    private final PreviewElement preview = PreviewElement.build();
    private MrdExpandElement(HTMLContainerBuilder<HTMLDivElement> e, String id, long sample, JsPropertyMap<?> service) {
        super(e.css("work").style("height: 100vh;"));
        _this = e;
        this.id = id;
        this.sample = sample;
        this.service = service;
        layout(service);
        btnHide.onClick(evt->fireStateChangeEvent());
    }
    private void layout(JsPropertyMap<?> service) {
        var div = div().style("height: calc(100% - 55px); overflow: auto;")
                .add(div().style("margin-left: 15px; margin-top: 10px; margin-right: 15px;").add(iptCancerType));
        var genes = Js.asArray(service.get("genes"));
        for(var gene: genes) div.add(MrdClones.build(gene.asString()));
        _this.add(div.add(controller.add(span().add(ButtonReadPreviousElement.build()))
                                    .add(span().style("margin-left: 10px;")
                                               .add(ButtonSaveElement.build())
                                               .add(ButtonPreviewElement.build()).add(btnHide))))
                .add(preview);

    }
    @Override
    public void update() {
        DomGlobal.window.parent.postMessage(JSON.stringify(Message.builder().id(id).type(Message.MessageType.STRETCH).build()), "*");
        com.gcgenome.lims.client.expand.mrd.Subjects.findInterpretation(sample, service);
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
    @Override
    public MrdExpandElement that() {
        return this;
    }

    public static class ButtonReadPreviousElement extends HTMLElementBuilder<HTMLButtonElement, ButtonElementText> {
        public static ButtonElement build() {
            return new ButtonReadPreviousElement(ButtonElement.outline().css("button").before(IconElement.icon("search_off")).text("Read previous")).that();
        }
        private final ButtonElementText _this;
        private ButtonReadPreviousElement(ButtonElementText e) {
            super(e);
            _this = e;
            Subjects.onAsync.subscribe(d->updateState());
            Subjects.interpretation.subscribe(d->updateState());
        }
        private void updateState() {
            if (!Subjects.isFinal.getValue() && Subjects.interpretation.getValue()==null) _this.enabled(!Subjects.onAsync.getValue());
            else _this.enabled(false);
        }
        @Override
        public ButtonElementText that() {
            return _this;
        }
    }
}
