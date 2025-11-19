package com.gcgenome.lims.client.expand;

import elemental2.dom.*;
import elemental2.promise.Promise;
import net.sayaya.ui.ButtonElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.IconElement;
import org.jboss.elemento.HTMLContainerBuilder;

import static org.jboss.elemento.Elements.*;

public class PreviewElement extends HTMLElementBuilder<HTMLDivElement, PreviewElement> {
	public static PreviewElement build(Blob pdf) {
		return new PreviewElement(div(), pdf);
	}
	private final HTMLContainerBuilder<HTMLIFrameElement> pdf = iframe().style("width: 100%; height: calc(100% - 60px); border: 1px solid #ddd;");
	private final HTMLContainerBuilder<HTMLDivElement> controller = div().css("controller");
	private final ButtonElement btnClose = ButtonElement.outline().css("button").before(IconElement.icon("undo")).text("Cancel");
	private final ButtonElement btnSave = ButtonElement.outline().css("button").before(IconElement.icon("description")).text("Confirm");
	private final HTMLContainerBuilder<HTMLDivElement> container = div();
	private PreviewElement(HTMLContainerBuilder<HTMLDivElement> e, Blob blob) {
		super(e.css("preview"));
		e.add(container.add(pdf)
				.add(controller.add(span().add(btnClose).add(btnSave))));
		pdf.element().setAttribute("src", URL.createObjectURL(blob));
	}
	public Promise<Void> onSave() {
		return new Promise<>((resolve, reject)-> {
			btnSave.onClick(evt -> {
				if(!DomGlobal.confirm("이대로 결과지를 생성하고 완료 합니다.")) reject.onInvoke(null);
				else resolve.onInvoke((Void)null);
				PreviewElement.this.element().remove();
			});
			btnClose.onClick(evt -> {
				PreviewElement.this.element().remove();
				reject.onInvoke(null);
			});
		});
	}
	@Override
	public PreviewElement that() {
		return this;
	}
}
