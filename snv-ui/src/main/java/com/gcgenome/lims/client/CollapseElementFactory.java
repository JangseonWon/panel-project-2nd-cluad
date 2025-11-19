package com.gcgenome.lims.client;

import com.gcgenome.lims.client.collapse.*;
import com.gcgenome.lims.test.HasCategory;
import com.gcgenome.lims.test.HasCategory.Category;
import jsinterop.base.JsPropertyMap;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CollapseElementFactory {
	public CollapseElement<?> create(String id, long sample, JsPropertyMap<?> service) {
		var category = Category.valueOf((String) service.get("category"));
		if(category == Category.BloodCancer) return BloodCancerCollapseElement.build(id, sample, service);
		else if(category == Category.SolidTumor) return TsoCollapseElement.build(id, sample, service);
		else if(category == HasCategory.Category.HRD) return HrdCollapseElement.build(id, sample, service);
		else if(category == Category.DES || category == Category.WES || category == Category.DGS || category == Category.Single ||
		   		category == Category.SinglePlus || category == Category.Cancer || category == Category.RareDisease ||
				category == HasCategory.Category.GenomeScreen || category == HasCategory.Category.SingleWithMLPA || category == HasCategory.Category.WesWithSingle) return GermlineCollapseElement.build(id, sample, service);
		else return DefaultCollapseElement.build(sample, service);

	}
}
