package com.gcgenome.lims.client.expand.elem;

import com.gcgenome.lims.api.ProgressApi;
import com.gcgenome.lims.api.SnvApi;
import com.gcgenome.lims.client.Data;
import com.gcgenome.lims.ui.IconElement;
import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.ButtonElementText;

public class SaveButtonElement {
    public static ButtonElementText build(long sample, String code) {
        var btn = ButtonElement.outline().css("button").before(IconElement.icon("save")).text("Save");
        Data.snvChanged.subscribe(data->btn.enabled(data!=null && data.size()>0));
        btn.onClick(evt->save(sample, code));
        return btn;
    }
    private static void save(long sample, String code) {
        var changes = Data.snvChanged.getValue();
        if(changes.size() <= 0) return;
        if(!DomGlobal.confirm("저장합니다.")) return;
        ProgressApi.open(true);
        var promises = changes.stream().map(d->{
            var classification = d.get("report");
            if("-".equalsIgnoreCase(classification)) classification = null;
            return SnvApi.save(sample, code, d.idx(), classification);
        }).toArray(Promise[]::new);
        Promise.all(promises).finally_(()->{
            ProgressApi.close();
            DomGlobal.alert("저장되었습니다.");
            Data.query.next(Data.query.getValue());
        });
    }
}
