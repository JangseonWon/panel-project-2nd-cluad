package com.gcgenome.lims.client;

import jsinterop.base.JsPropertyMap;

public class Main extends AbstractMain {
	@Override
	protected CollapseElement<?> collapse(String id, long sample, JsPropertyMap<?> service) {
		return CollapseElementFactory.create(id, sample, service);
	}
	@Override
	protected ExpandElement<?> expand(String id, long sample, JsPropertyMap<?> service) {
		return ExpandElementFactory.create(id, sample, service);
	}
}
