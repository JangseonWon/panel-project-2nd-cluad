package com.gcgenome.lims.client.expand.hrd;

import com.gcgenome.lims.client.Subjects;
import com.gcgenome.lims.client.expand.elem.InterpretationSubject;
import com.gcgenome.lims.dto.InterpretationHrd;
import elemental2.dom.HTMLDivElement;
import jsinterop.base.Js;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.TextFieldElement;
import org.jboss.elemento.HTMLContainerBuilder;

import static org.jboss.elemento.Elements.div;

public class CancerType extends HTMLElementBuilder<HTMLDivElement, CancerType> implements InterpretationSubject<HTMLDivElement> {
    public static CancerType build() {
        return new CancerType(div());
    }

    private final TextFieldElement.TextFieldOutlined<String> iptCancerType = TextFieldElement.textBox().outlined().text("Cancer Type").style("width: 100%;");
    private InterpretationHrd value;
    private CancerType(HTMLContainerBuilder<HTMLDivElement> e) {
        super(e.style("margin: 16px;"));
        e.add(iptCancerType);
        Subjects.interpretation.subscribe(obj->{ try {
            InterpretationHrd value = Js.cast(obj);
            update(value);
        } catch(ClassCastException ignore){ this.value = null; }});
        iptCancerType.onValueChange(evt->{
            if(this.value!=null) value.cancerType = evt.value();
            Subjects.interpretation.next(value);
        });
    }
    private void update(InterpretationHrd value) {
        this.value = value;
        if(value==null) iptCancerType.value("");
        else iptCancerType.value(value.cancerType);
    }
    @Override
    public CancerType that() {
        return this;
    }
}
