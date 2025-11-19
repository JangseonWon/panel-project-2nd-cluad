package com.gcgenome.lims.client.expand;

import com.gcgenome.lims.client.Data;
import com.gcgenome.lims.client.expand.elem.FilterToggleElement;
import com.gcgenome.lims.dto.Query;
import com.gcgenome.lims.test.HasCategory;
import elemental2.dom.DomGlobal;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.ChipElement;
import net.sayaya.ui.event.HasValueChangeHandlers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GermlineHasGenesExpandElement {
    public static final String[] GENE_INCIDENTAL_FINDINGS = new String[]{
            "ABCD1","ACTA2","ACTC1","ACVRL1","APC","APOB","ATP7B","BAG3","BMPR1A","BRCA1","BRCA2","BTD","CACNA1S","CALM1",
            "CALM2", "CALM3","CASQ2","COL3A1","CYP27A1","DES","DSC2","DSG2","DSP", "ENG","FBN1","FLNC","GAA","GLA","HFE",
            "HNF1A","KCNH2", "KCNQ1","LDLR","LMNA","MAX","MEN1","MLH1","MSH2","MSH6","MUTYH","MYBPC3","MYH11",
            "MYH7","MYL2","MYL3","NF2", "OTC","PALB2","PCSK9","PKP2","PLN","PMS2","PRKAG2","PTEN","RB1","RBM20","RET",
            "RPE65","RYR1","RYR2","SCN5A", "SDHAF2", "SDHB","SDHC","SDHD","SMAD3","SMAD4","STK11","TGFBR1","TGFBR2",
            "TMEM127","TMEM43","TNNC1","TNNI3","TNNT2", "TP53","TPM1","TRDN","TSC1","TSC2","TTN","TTR","VHL","WT1"
    };
    private static Query.Filter filterByGenes = new Query.Filter().key("genes.refgene");
    public static GermlineExpandElement build(String id, long sample, JsPropertyMap<?> service) {
        Set<String> addenda = service.has("addendum") ? Arrays.stream(Js.asArray(service.get("addendum"))).map(Any::asString).collect(Collectors.toSet()) : Set.of();
        Set<String> cores = service.has("genes") ? Arrays.stream(Js.asArray(service.get("genes"))).map(Any::asString).filter(g->!addenda.contains(g)).collect(Collectors.toSet()) : Set.of();

        var ifs = Arrays.stream(GENE_INCIDENTAL_FINDINGS).map(String::trim).filter(g->!cores.contains(g) && !addenda.contains(g)).collect(Collectors.toSet());
        var chipCore = ChipElement.check("Tier1").value(false);
        var chipAddendum = ChipElement.check("Tier2").value(false);
        var chipIncidental = ChipElement.check("IF").value(false);
        if(cores.isEmpty()) chipCore.style("display: none;");
        else {
            chipCore.value(true);
            if(cores.size() == 1) chipCore.text(cores.stream().findFirst().get());
            else chipCore.text("Tier 1");
        }
        if(addenda.isEmpty()) chipAddendum.style("display: none;");
        else chipAddendum.value(true);
        var category = HasCategory.Category.valueOf((String) service.get("category"));
        if(category != HasCategory.Category.DES && category != HasCategory.Category.WES && category != HasCategory.Category.DGS) chipIncidental.style("display: none;");
        HasValueChangeHandlers.ValueChangeEventListener<Boolean> update = evt->{
            String genes = merge(
                    chipCore.value()?cores:Set.of(),
                    chipAddendum.value()?addenda:Set.of(),
                    chipIncidental.value()?ifs:Set.of()
            ).stream().collect(Collectors.joining(","));
            var list = Data.filters.getValue();
            filterByGenes.value(genes);
            for(var f: list) if(filterByGenes.key.equalsIgnoreCase(f.key)) list.remove(f);
            if(genes.length() > 2) list.add(filterByGenes);
            Data.filters.next(list);
        };
        chipCore.onValueChange(update);
        chipAddendum.onValueChange(update);
        chipIncidental.onValueChange(update);
        com.gcgenome.lims.client.Data.snv.subscribe(list->hook(cores, addenda, ifs, list));
        return GermlineExpandElement.build(id, sample, service,
                FilterToggleElement.build("Filtered Variant", "candidate", true),
                chipCore,
                chipAddendum,
                chipIncidental,
                FilterToggleElement.build("MT", "mt", false));
    }

    private static Set<String> merge(Set<String> cores, Set<String> addenda, Set<String> ifs) {
        var genes = new HashSet<String>();
        genes.addAll(cores);
        genes.addAll(addenda);
        genes.addAll(ifs);
        return genes;

    }
    private static void hook(Set<String> cores, Set<String> addenda, Set<String> ifs, List<JsPropertyMap<?>> jsPropertyMaps) {
        boolean h = false;
        for(JsPropertyMap<?> map: jsPropertyMaps) {
            if(map.has("tier")) continue;
            h = true;
            var gene = (String) map.get("gene.refgene");
            if(gene == null) continue;
            var cast = Js.asPropertyMap(map);
            if(cores.contains(gene)) cast.set("tier", "Tier1");
            else if(addenda.contains(gene)) cast.set("tier", "Tier2");
            else if(ifs.contains(gene)) cast.set("tier", "IF");
            else cast.set("tier", "Tier3");
        }
        if(h) com.gcgenome.lims.client.Data.snv.next(jsPropertyMaps);
    }
}
