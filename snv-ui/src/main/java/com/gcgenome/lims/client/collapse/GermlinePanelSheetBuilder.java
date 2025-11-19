package com.gcgenome.lims.client.collapse;

import com.google.gwt.i18n.client.NumberFormat;

import static com.gcgenome.lims.client.collapse.SnvSimpleTableElement.number;
import static com.gcgenome.lims.client.collapse.SnvSimpleTableElement.text;

public class GermlinePanelSheetBuilder {
    public static SnvSimpleTableElement build() {
        return SnvSimpleTableElement.build(
                text("report", "D.Class").build(),
                text("gene.refgene", "Gene").build(),
                text("hgvsc", "HGVS.c").build(),
                text("hgvsp", "HGVS.p").build(),
                number("genotype", "Zygosity",NumberFormat.getFormat("0")).build(),
                number("analysis", "Analysis", NumberFormat.getFormat("0.##")).build()
        );
    }
}
