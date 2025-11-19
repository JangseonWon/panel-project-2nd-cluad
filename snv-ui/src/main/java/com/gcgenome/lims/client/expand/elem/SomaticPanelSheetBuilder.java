package com.gcgenome.lims.client.expand.elem;

import jsinterop.base.JsPropertyMap;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.chart.Column;
import net.sayaya.ui.chart.Data;

import java.util.List;

public class SomaticPanelSheetBuilder {
    public static SnvTableElement build(Column... colums) {
        return SnvTableElement.build(2, SomaticPanelSheetBuilder::toData, colums);
    }
    private final static BehaviorSubject<List<Data>> snvChanged = com.gcgenome.lims.client.Data.snvChanged;
    private static Data toData(JsPropertyMap<?> map) {
        String id = map.get("id").toString();
        Data data = Data.create(id);
        String reported = String.valueOf(map.get("reported"));
        if(reported!=null && reported.trim().length() > 5) {
            if(reported.contains("=Tier1")) data.put("report", "*");
            else if(reported.contains("=Tier2")) data.put("report", "+");
            else if(reported.contains("=Tier3")) data.put("report", "?");
        }
        else data.put("report", "-");
        data.initialize("analysis", (String)map.get("analysis"));
        data.initialize("variant", map.get("snv")!=null?(String)map.get("snv"):"");
        map.forEach(key->data.initialize(key, map.get(key).toString()));
        data.onValueChange(evt->{
            var list = snvChanged.getValue();
            if(evt.value().isChanged()) list.add(data);
            else list.remove(data);
            snvChanged.next(list);
        });
        return data;
    }
}
