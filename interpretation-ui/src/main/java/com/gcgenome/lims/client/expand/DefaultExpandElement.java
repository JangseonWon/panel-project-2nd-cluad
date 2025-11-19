package com.gcgenome.lims.client.expand;

import com.gcgenome.lims.client.ExpandElement;
import com.gcgenome.lims.client.WindowState;
import elemental2.dom.HTMLDivElement;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.event.HasStateChangeHandlers;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.jboss.elemento.Elements.div;

public class DefaultExpandElement extends HTMLElementBuilder<HTMLDivElement, DefaultExpandElement> implements ExpandElement<HTMLDivElement> {
	public static DefaultExpandElement build(long sample, JsPropertyMap<?> service) {
		return new DefaultExpandElement(div(), sample, service);
	}
	private DefaultExpandElement(HTMLContainerBuilder<HTMLDivElement> e, long sample, JsPropertyMap<?> service) {
		super(e.add("Not supported yet:" + service.get("code") + "(" + service.get("name") + ")"));
		this.on(EventType.click, evt->fireStateChangeEvent());
	}

	@Override
	public DefaultExpandElement that() {
		return this;
	}

	private final Set<HasStateChangeHandlers.StateChangeEventListener<WindowState>> listeners = new HashSet<>();
	@Override
	public Collection<HasStateChangeHandlers.StateChangeEventListener<WindowState>> listeners() {
		return listeners;
	}

	@Override
	public WindowState state() {
		return WindowState.COLLAPSE;
	}
}
