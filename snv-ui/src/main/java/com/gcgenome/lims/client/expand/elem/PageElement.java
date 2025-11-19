package com.gcgenome.lims.client.expand.elem;

import com.gcgenome.lims.client.Data;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;

public class PageElement {
    public static net.sayaya.ui.PageElement build() {
        var page = net.sayaya.ui.PageElement.instance().show(1);
        page.element().style.position = "absolute";
        page.element().style.zIndex = CSSProperties.ZIndexUnionType.of("999");
        page.element().style.left = "20px";
        page.element().style.right = "240px";
        page.element().style.bottom = "5px";
        page.show(predictRowCount()).idx(0);
        Data.query.next(Data.query.getValue().limit(page.show()).page((int)page.page()));
        // DomGlobal.window.addEventListener("resize", evt -> page.show(predictRowCount()).idx(0));
        Data.total.subscribe(page::total);
        page.onValueChange(evt-> Data.query.next(Data.query.getValue().limit(page.show()).page((int)page.page())));
        return page;
    }
    public static int predictRowCount() {
        int windowHeight = DomGlobal.window.outerHeight;
        int tableMargin = 110;
        int headerHeightExpect = 28;
        int rowHeightExpect = 23;
        return Math.max(1, (windowHeight - tableMargin - headerHeightExpect) / rowHeightExpect - 1);
    }
}
