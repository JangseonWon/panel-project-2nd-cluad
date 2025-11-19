package com.gcgenome.lims.client.expand.elem;

import com.gcgenome.lims.client.Data;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.HTMLElementBuilder;
import org.jboss.elemento.HTMLContainerBuilder;

import static org.jboss.elemento.Elements.div;

public class NotAnalyzedElement extends HTMLElementBuilder<HTMLDivElement, NotAnalyzedElement> {
    public static NotAnalyzedElement build() {
        return new NotAnalyzedElement(div());
    }
    private NotAnalyzedElement(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e);
        e.add("Not Analyzed Yet")
                .style("display:none;position: absolute;z-index: 999999;top: 50%;left: 0;right: 0; margin-left: auto; margin-right: auto;width: fit-content;text-align: center;" +
                        "font-size: 3em;font-weight: 800;user-select: none; color: var(--mdc-theme-text-disabled-on-background);");
        Data.analysisList.subscribe(list->visibility(list == null || list.isEmpty()));
    }
    public void visibility(boolean visible) {
        if(visible) element().style.display = null;
        else element().style.display = "none";
    }
    @Override
    public NotAnalyzedElement that() {
        return this;
    }
}
