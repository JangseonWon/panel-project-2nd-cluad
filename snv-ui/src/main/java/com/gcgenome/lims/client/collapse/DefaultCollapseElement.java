package com.gcgenome.lims.client.collapse;

import com.gcgenome.lims.client.CollapseElement;
import com.gcgenome.lims.client.WindowState;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.HTMLElementBuilder;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.span;

public class DefaultCollapseElement extends HTMLElementBuilder<HTMLDivElement, DefaultCollapseElement> implements CollapseElement<HTMLDivElement> {
	public static DefaultCollapseElement build(long sample, JsPropertyMap<?> service) {
		return new DefaultCollapseElement(div(), sample, service);
	}
	private final IconElement icon = IconElement.icon(IconElement.Type.Light, "fa-search");
	private final HTMLContainerBuilder<HTMLElement> title = span().css("mdc-list-item__primary-text").add("SNV/InDel");
	private final HTMLContainerBuilder<HTMLElement> info = span().css("mdc-list-item__secondary-text").add("아직 지원하고 있지 않는 검사입니다.");
	private final HTMLContainerBuilder<HTMLElement> meta = span().css("mdc-list-item__meta");
	private final HTMLContainerBuilder<HTMLDivElement> _this;
	private DefaultCollapseElement(HTMLContainerBuilder<HTMLDivElement> e, long sample, JsPropertyMap<?> service) {
		super(e.css("work-summary")
			   .style("margin-left: 16px;margin-right: 16px;display: flex; flex-direction: row; " +
					  "flex-wrap: nowrap; align-content: space-between; justify-content: space-between; " +
					  "align-items: center; cursor: pointer;"));
		_this = e;
		layout();
	}

	private void layout() {
		_this.add(span().css("mdc-list-item__graphic").style("height: auto; align-self: unset;").add(icon))
			 .add(div().css("mdc-list-item__text").style("margin-bottom: 10px;").add(title).add(info))
			 .add(meta);
	}

	@Override
	public DefaultCollapseElement that() {
		return this;
	}

	private final Set<StateChangeEventListener<WindowState>> listeners = new HashSet<>();
	@Override
	public Collection<StateChangeEventListener<WindowState>> listeners() {
		return listeners;
	}

	@Override
	public WindowState state() {
		return WindowState.FULLSCREEN;
	}
}
