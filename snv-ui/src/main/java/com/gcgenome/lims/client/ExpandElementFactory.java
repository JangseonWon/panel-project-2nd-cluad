package com.gcgenome.lims.client;

import com.gcgenome.lims.client.expand.*;
import com.gcgenome.lims.test.HasCategory;
import jsinterop.base.JsPropertyMap;
import lombok.experimental.UtilityClass;

import java.util.Arrays;

@UtilityClass
public class ExpandElementFactory {
	public ExpandElement<?> create(String id, long sample, JsPropertyMap<?> service) {
		var category = HasCategory.Category.valueOf((String)service.get("category"));
		if(category == HasCategory.Category.BloodCancer) return BloodCancerExpandElement.build(id, sample, service);
		else if(category == HasCategory.Category.SolidTumor) return TsoExpandElement.build(id, sample, service);
		else if(category == HasCategory.Category.HRD) return HrdExpandElement.build(id, sample, service);
		else if(category == HasCategory.Category.Single || category == HasCategory.Category.Cancer ||
				category == HasCategory.Category.DES || category == HasCategory.Category.WES ||
				category == HasCategory.Category.DGS || category == HasCategory.Category.RareDisease ||
				category == HasCategory.Category.SinglePlus || category == HasCategory.Category.GenomeScreen ||
				category == HasCategory.Category.SingleWithMLPA || category == HasCategory.Category.WesWithSingle) return GermlineHasGenesExpandElement.build(id, sample, service);
		return DefaultExpandElement.build(sample, service);
	}
}
