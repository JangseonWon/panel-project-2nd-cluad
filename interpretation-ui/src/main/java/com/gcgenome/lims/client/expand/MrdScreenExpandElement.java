package com.gcgenome.lims.client.expand;

import com.gcgenome.lims.api.InterpretationApi;
import com.gcgenome.lims.client.ExpandElement;
import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.client.WindowState;
import com.gcgenome.lims.client.expand.elem.PreviewElement;
import com.gcgenome.lims.client.expand.elem.*;
import com.gcgenome.lims.client.expand.mrd.screen.CancerType;
import com.gcgenome.lims.client.expand.mrd.screen.HyperMutation;
import com.gcgenome.lims.client.expand.mrd.screen.InputDna;
import com.gcgenome.lims.client.expand.mrd.screen.MrdScreenClones;
import com.gcgenome.lims.dto.InterpretationMrdScreen;
import com.gcgenome.lims.dto.Message;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.HTMLElementBuilder;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static elemental2.core.Global.JSON;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.span;

public class MrdScreenExpandElement extends HTMLElementBuilder<HTMLDivElement, MrdScreenExpandElement> implements ExpandElement<HTMLDivElement> {
    public static MrdScreenExpandElement build(String id, JsPropertyMap<?> service) {
        return new MrdScreenExpandElement(div(), id, service);
    }
    protected final HTMLContainerBuilder<HTMLDivElement> controller = div().css("controller");
    private final ButtonElement btnHide = ButtonElement.outline().css("button").before(IconElement.icon("close")).text("Close");
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private final String id;
    private final CancerType iptCancerType = CancerType.build();
    private final InputDna iptInputDna = InputDna.build();
    private final HyperMutation iptHyperMutation = HyperMutation.build();

    private final PreviewElement preview = PreviewElement.build();
    private MrdScreenExpandElement(HTMLContainerBuilder<HTMLDivElement> e, String id, JsPropertyMap<?> service) {
        super(e.css("work").style("height: 100vh;"));
        _this = e;
        this.id = id;
        layout(service);
        btnHide.onClick(evt->fireStateChangeEvent());
    }
    private void layout(JsPropertyMap<?> service) {
        var div = div().style("height: calc(100% - 55px); overflow: auto;")
                .add(div().style("margin-left: 15px; margin-top: 10px; margin-right: 15px;").add(iptCancerType).add(iptInputDna))
                .add(iptHyperMutation);
        var genes = Js.asArray(service.get("genes"));
        var cell = Js.asPropertyMap(service.get("method")).get("cell");
        for(var gene: genes) div.add(MrdScreenClones.build(cell.toString(), gene.asString()));
        _this.add(div.add(controller.add(span().add(ButtonAutoElement.build()).add(ButtonNegativeElement.build()))
                .add(span().style("margin-left: 10px;").add(ButtonSaveElement.build()).add(ButtonPreviewElement.build()).add(btnHide))))
                .add(preview);

    }
    @Override
    public void update() {
        DomGlobal.window.parent.postMessage(JSON.stringify(Message.builder().id(id).type(Message.MessageType.STRETCH).build()), "*");
        var code = Subjects.code.getValue();
        InterpretationApi.interpretation(Subjects.sample.getValue(), code).catch_(error-> Promise.resolve(new InterpretationMrdScreen()))
                .then(obj->{
                    Subjects.interpretation.next(obj);
                    return null;
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
    @Override
    public MrdScreenExpandElement that() {
        return this;
    }
}
