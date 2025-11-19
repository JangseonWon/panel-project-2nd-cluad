package com.gcgenome.lims.client.expand.elem;

import com.gcgenome.lims.client.Data;
import com.gcgenome.lims.dto.Query;
import net.sayaya.ui.ChipElement;
import net.sayaya.ui.ChipElementCheckable;

import java.util.function.Supplier;

public class FilterToggleElement {
    public static ChipElementCheckable build(String name, String tag, boolean init) {
        var filter = new Query.Filter().key("tags").value(tag);
        var toggle = ChipElement.check(name).value(init);
        toggle.onValueChange(evt->{
            var list = Data.filters.getValue();
            if(evt.value()) list.add(filter);
            else list.remove(filter);
            Data.filters.next(list);
        });
        return toggle;
    }
    public static ChipElementCheckable build(String name, Supplier<String> tag, boolean init) {
        var toggle = ChipElement.check(name).value(init);
        var filter = new Query.Filter().key("tags");
        toggle.onValueChange(evt->{
            filter.value(tag.get());
            var list = Data.filters.getValue();
            if(evt.value()) list.add(filter);
            else list.remove(filter);
            Data.filters.next(list);
        });
        return toggle;
    }
}
