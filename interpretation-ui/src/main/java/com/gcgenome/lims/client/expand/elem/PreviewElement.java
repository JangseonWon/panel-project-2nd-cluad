package com.gcgenome.lims.client.expand.elem;

import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLIFrameElement;
import elemental2.dom.URL;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.HTMLElementBuilder;
import org.jboss.elemento.HTMLContainerBuilder;

import static org.jboss.elemento.Elements.*;

public class PreviewElement extends HTMLElementBuilder<HTMLDivElement, PreviewElement> {
    public static PreviewElement build() {
        return new PreviewElement(div()).that();
    }
    private final HTMLContainerBuilder<HTMLIFrameElement> pdf = iframe().style("width: 100%; height: calc(100% - 60px); border: 1px solid #ddd;");
    private final HTMLContainerBuilder<HTMLDivElement> controller = div().css("controller");
    private final ButtonElement btnClose = ButtonElement.outline().css("button").before(IconElement.icon("undo")).text("Cancel");
    private final ButtonElement btnSave = ButtonElement.outline().css("button").before(IconElement.icon("description")).text("Confirm");
    private final HTMLContainerBuilder<HTMLDivElement> container = div();
    private PreviewElement(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.css("preview"));
        e.add(container.add(pdf).add(controller.add(span().add(btnClose).add(btnSave))));
        Subjects.pdf.subscribe(blob->{
            if(blob !=null && blob.size > 0) {
                element().style.display = null;
                pdf.element().setAttribute("src", URL.createObjectURL(blob));
            }
        });
        btnClose.onClick(evt->{
            element().style.display = "none";
        });
        btnSave.onClick(evt->{
            element().style.display = "none";
            Subjects.doSaveAndPublish.next(true);
        });
        element().style.display = "none";
    }
    @Override
    public PreviewElement that() {
        return this;
    }
}
