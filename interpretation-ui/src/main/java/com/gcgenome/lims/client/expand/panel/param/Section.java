package com.gcgenome.lims.client.expand.panel.param;

import com.gcgenome.lims.client.expand.panel.ResultWriter;
import com.gcgenome.lims.dto.InterpretationPanel;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.HTMLElementBuilder;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.LinkedList;
import java.util.List;

import static org.jboss.elemento.Elements.div;

public class Section extends HTMLElementBuilder<HTMLDivElement, Section> implements ResultWriter<HTMLDivElement> {
    public static Section build(String title, ResultWriter<?>... elems) {
        var section = new Section(div(), title);
        for(ResultWriter<?> elem: elems) section.append(elem);
        return section;
    }
    private boolean enabled = true;
    private final List<ResultWriter<?>> children = new LinkedList<>();
    private Section(HTMLContainerBuilder<HTMLDivElement> e, String title) {
        super(e);
        e.add(Elements.label().css("title").textContent(title));
    }
    public boolean isEnabled() {
        return enabled;
    }
    private void append(ResultWriter<?> elem) {
        super.element().append(elem.element());
        children.add(elem);
    }
    @Override
    public InterpretationPanel append(InterpretationPanel map) {
        for(ResultWriter<?> child: children) map = child.append(map);
        return map;
    }
    @Override
    public void update(InterpretationPanel map) {
        for(ResultWriter<?> child: children) child.update(map);
    }
    @Override
    public Section that() {
        return this;
    }
}
