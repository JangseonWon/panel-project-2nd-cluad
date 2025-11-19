package com.gcgenome.lims.client.expand;

import elemental2.promise.Promise;
import net.sayaya.ui.*;

import static org.jboss.elemento.Elements.body;

public class DescriptionDialog {
    public static Promise<String> dialog() {
        ButtonElementText confirm = ButtonElement.outline().text("CONFIRM").style("margin-left:0;").css("button").before(IconElement.icon("description"));
        ButtonElementText cancel = ButtonElement.outline().text("CANCEL").css("button").before(IconElement.icon("close"));
        TextAreaElement<String> iptDescription = TextAreaElement.textBox().outlined().text("Description").autocomplete("").style("width: 100%; height: 15em; margin-top: 10px;");
        iptDescription.element().firstElementChild.setAttribute("style", "width: 20em;");
        Dialog dialog = Dialog.alert("변경 내용", cancel, confirm);
        dialog.add(iptDescription);
        body().add(dialog);
        dialog.open();

        return new Promise<>((resolve, reject) -> {
            confirm.onClick(evt ->{
                dialog.close();
                dialog.element().remove();
                resolve.onInvoke(iptDescription.value());
            });

            cancel.onClick(evt -> {
                dialog.close();
                dialog.element().remove();
                reject.onInvoke(null);
            });
        });
    }
}
