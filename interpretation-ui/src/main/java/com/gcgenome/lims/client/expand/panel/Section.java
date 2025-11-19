package com.gcgenome.lims.client.expand.panel;

import com.gcgenome.lims.dto.InterpretationPanel;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.IconElement;
import net.sayaya.ui.event.HasStateChangeHandlers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class Section extends com.gcgenome.lims.client.Section implements ResultWriter<HTMLDivElement>, HasStateChangeHandlers<Section.State> {
    public static Section build(IconElement icon, String title, ResultWriter<?>... elems) {
        return proxy(icon, title, i->i, i->i, elems);
    }
    public static Section proxy(IconElement icon, String title, Function<InterpretationPanel, InterpretationPanel> to, Function<InterpretationPanel, InterpretationPanel> from, ResultWriter<?>... elems) {
        var section = new Section(icon, title, to, from);
        for(ResultWriter<?> elem: elems) section.append(elem);
        return section;
    }
    private boolean enabled = true;
    private final List<ResultWriter<?>> children = new LinkedList<>();
    private final Function<InterpretationPanel, InterpretationPanel> to;
    private final Function<InterpretationPanel, InterpretationPanel> from;
    private final List<StateChangeEventListener<State>> listeners = new LinkedList<>();
    private Section(IconElement icon, String title, Function<InterpretationPanel, InterpretationPanel> to, Function<InterpretationPanel, InterpretationPanel> from) {
        super(icon.css("title-icon"), title);
        this.to = to;
        this.from = from;
    }
    public void enable(boolean enabled) {
        this.enabled = enabled;
        if(enabled) this.style("transition: all 300ms ease 0s; overflow: hidden; max-height: 1000%;");
        else this.style("transition: all 300ms ease 0s; overflow: hidden; max-height: 0%;");
        fireStateChangeEvent();
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
        if(!isEnabled()) return map;
        var target = to.apply(map);
        for(ResultWriter<?> child: children) target = child.append(target);
        return map;
    }
    @Override
    public void update(InterpretationPanel map) {
        var target = from.apply(map);
        enable(target!=null);
        for(ResultWriter<?> child: children) child.update(target);
    }
    @Override
    public Collection<StateChangeEventListener<State>> listeners() {
        return listeners;
    }
    @Override
    public State state() {
        if(enabled) return State.OPEN;
        else return State.CLOSE;
    }

    public enum State {
        OPEN, CLOSE
    }
}
