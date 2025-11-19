package com.gcgenome.lims.client.expand.elem;

import com.gcgenome.lims.client.Data;
import com.gcgenome.lims.dto.Query;
import net.sayaya.ui.ChipElement;
import net.sayaya.ui.ChipSetElement;
import net.sayaya.ui.TextFieldElement;

public class FilterElementSet {
    public static FilterElementSet build() {
        return new FilterElementSet();
    }
    public final TextFieldElement.TextFieldOutlined<String> iptKeyword = TextFieldElement.textBox().outlined().text("Search Keyword").style("margin-left: auto; margin-top: 5px; width: 400px;");
    public final ChipSetElement tagSet;
    FilterElementSet() {
        this.tagSet = ChipSetElement.filters().style("margin-left: auto; align-items: center;");
        iptKeyword.onValueChange(evt->createTag(evt.value()));
    }
    private void createTag(String value) {
        if(value == null || value.isEmpty()) return;
        iptKeyword.value("");
        var filter = toFilter(value);
        if(filter==null || filter.value.isEmpty()) return;
        ChipElement tag = ChipElement.chip(value).removable();
        tagSet.add(tag);
        var list = Data.filters.getValue();
        list.add(filter);
        Data.filters.next(list);
        tag.onRemove(evt2->{
            var list2 = Data.filters.getValue();
            list2.remove(filter);
            Data.filters.next(list2);
        });
    }
    private static Query.Filter toFilter(String text) {
        if(text.contains(":")) {
            String[] split = text.split(":");
            if(split.length == 0) return null;
            return new Query.Filter().key(split[0].trim()).value(split[1].trim());
        } else return new Query.Filter().key("").value(text);
    }
}
