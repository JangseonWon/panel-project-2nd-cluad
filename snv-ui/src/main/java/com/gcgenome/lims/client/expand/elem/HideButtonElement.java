package com.gcgenome.lims.client.expand.elem;

import com.gcgenome.lims.client.WindowState;
import com.gcgenome.lims.ui.IconElement;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.ButtonElementText;
import net.sayaya.ui.event.HasStateChangeHandlers;

public class HideButtonElement {
    public static ButtonElementText build(HasStateChangeHandlers<WindowState> parent) {
        var btn = ButtonElement.outline().css("button").before(IconElement.icon("close")).text("Close");
        btn.onClick(evt->parent.fireStateChangeEvent());
        return btn;
    }
}
