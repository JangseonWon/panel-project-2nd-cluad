package com.gcgenome.lims.client.expand;

import com.gcgenome.lims.client.expand.elem.*;
import jsinterop.base.JsPropertyMap;

public class SingleGeneExpandElement {
    public static GermlineExpandElement build(String id, long sample, JsPropertyMap<?> service) {
        return GermlineExpandElement.build(id, sample, service,
                FilterToggleElement.build("Filtered Variant", "candidate", true),
                FilterToggleElement.build("pindel",  "pindel", false));
    }
}
