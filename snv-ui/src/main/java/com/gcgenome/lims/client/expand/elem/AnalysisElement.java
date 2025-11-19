package com.gcgenome.lims.client.expand.elem;

import com.gcgenome.lims.client.Data;
import elemental2.dom.HTMLDivElement;
import jsinterop.base.Any;
import jsinterop.base.Js;
import net.sayaya.ui.DropDownElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.ListElement;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.jboss.elemento.Elements.div;

public class AnalysisElement extends HTMLElementBuilder<HTMLDivElement, AnalysisElement> {
    public static AnalysisElement build(String code) {
        return new AnalysisElement(div(), code);
    }
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private final String code;
    private AnalysisElement(HTMLContainerBuilder<HTMLDivElement> e, String code) {
        super(e.style("display: flex;align-items: center; margin-top: 5px;margin-left: 10px; margin-right: 10px;"));
        _this = e;
        this.code = code;
        Data.analysisList.subscribe(this::update);
    }
    private void update(List<Any> list) {
        var analysisList = ListElement.singleLineList();
        var tmp = list.stream().map(Js::asPropertyMap).filter(c-> c.get("service").equals(code)).collect(Collectors.toList());
        tmp.stream().sorted(Comparator.comparing(c->c.get("batch").toString())).forEach(a -> analysisList.add(ListElement.singleLine().label(a.get("serial").toString())));
        var iptAnalysis = DropDownElement.outlined(analysisList).css("input").text("Analysis").style("z-index:9999999; min-width: 400px;");
        iptAnalysis.onValueChange(evt->{
            tmp.stream().filter(e->e.get("serial").equals(evt.value())).findFirst().ifPresent(Data.analysis::next);
        });
        _this.element().innerHTML= "";
        _this.add(iptAnalysis);
        iptAnalysis.select(tmp.size() - 1);
    }
    @Override
    public AnalysisElement that() {
        return this;
    }
}
