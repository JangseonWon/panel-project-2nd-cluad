package com.gcgenome.lims.client.expand.elem;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLabelElement;
import net.sayaya.ui.HTMLElementBuilder;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.LinkedList;
import java.util.List;

public class Section extends HTMLElementBuilder<HTMLDivElement, Section> implements InterpretationSubject<HTMLDivElement> {
    public static Section build(String title, InterpretationSubject<?>... elems) {
        var section = new Section(title);
        for(var elem: elems) section.children.add(elem);
        return section;
    }
    private final HTMLContainerBuilder<HTMLLabelElement> title;
    private boolean enabled = true;
    private final List<InterpretationSubject<?>> children = new LinkedList<>();
    private Section(String title) {
        this(Elements.div().css("section"), title);
    }
    private Section(HTMLContainerBuilder<HTMLDivElement> e, String title) {
        super(e);
        this.title = Elements.label().css("title").style("margin-left: 0px;");
        this.title.textContent(title);
        e.add(this.title);
    }

    public Section enable(boolean enabled) {
        this.enabled = enabled;
        if(enabled) {
            this.style("transition: all 300ms ease 0s; overflow: auto; max-height: 100%; margin-left: 15px; margin-right: 15px; margin-bottom: 16px;");
            for(var elem: children) super.element().append(elem.element());
        } else {
            this.style("transition: all 300ms ease 0s; overflow: auto; max-height: 0%;");
            for(var elem: children) elem.element().remove();
        }
        return this;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public Section that() {
        return this;
    }
}
