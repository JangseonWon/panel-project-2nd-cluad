package com.gcgenome.lims.client;

import elemental2.dom.DomGlobal;
import net.sayaya.ui.event.HasValueChangeHandlers;
import org.gwtproject.event.shared.HandlerRegistration;

import java.util.LinkedList;
import java.util.Objects;

public class Router {
	private static LinkedList<HasValueChangeHandlers.ValueChangeEventListener<String>> listeners = new LinkedList<>();
	private static String location = null;
	public static void initialize() {
		DomGlobal.window.onhashchange = evt->{
			refresh(true);
			return false;
		};
		refresh(true);
	}
	public static void location(String target, boolean shouldUpdate) {
		location = hash(target, shouldUpdate);
	}
	private static void refresh(boolean shouldUpdate) {
		String hash = DomGlobal.window.location.hash;
		if(hash == null || hash.trim().isEmpty()) location = null;
		else location = hash.substring(1);
		if(shouldUpdate) fire();
	}
	private static void fire() {
		HasValueChangeHandlers.ValueChangeEvent<String> evt = HasValueChangeHandlers.ValueChangeEvent.event(null, location());
		for(HasValueChangeHandlers.ValueChangeEventListener<String> listener: listeners) listener.handle(evt);
	}
	private static String hash(String hash, boolean shouldUpdate) {
		if(hash==null || hash.trim().isEmpty()) {
			String url = DomGlobal.window.location.pathname + DomGlobal.window.location.search;
			DomGlobal.window.history.replaceState("", DomGlobal.document.title, url);
			refresh(shouldUpdate);
			return null;
		} else {
			hash = hash.trim();
			String prevHash = DomGlobal.window.location.hash;
			if(Objects.equals("#" + hash, prevHash)) return hash;
			if(!shouldUpdate) DomGlobal.window.history.replaceState(null, null, "#" + hash);
			else DomGlobal.window.location.hash = hash;
			return hash;
		}
	}
	public static String location() {
		return location;
	}
	public static HandlerRegistration addValueChangeHandler(HasValueChangeHandlers.ValueChangeEventListener<String> valueChangeEventListener) {
		listeners.add(valueChangeEventListener);
		return ()->listeners.remove(valueChangeEventListener);
	}
}
