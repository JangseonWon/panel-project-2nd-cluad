package com.gcgenome.lims.client;

import com.gcgenome.lims.client.collapse.*;
import com.gcgenome.lims.test.Interpretable;
import jsinterop.base.JsPropertyMap;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CollapseElementFactory {
	public CollapseElement<?> create(String id, long sample, JsPropertyMap<?> service) {
		Subjects.service.next(service);
		var category = Interpretable.Category.valueOf((String)service.get("interpretation_category"));
		if(category == Interpretable.Category.DES || category == Interpretable.Category.WES || category == Interpretable.Category.DGS ||
			category == Interpretable.Category.Single || category == Interpretable.Category.SingleWithMLPA || category == Interpretable.Category.SinglePlus ||
			category == Interpretable.Category.Cancer || category == Interpretable.Category.RareDisease) return PanelCollapseElement.build(id, sample, service);
		else if(category == Interpretable.Category.BloodCancer) return SomaticCollapseElement.build(id, sample, service);
		else if(category == Interpretable.Category.SolidTumor) return TsoCollapseElement.build(id, sample, service);
		else if(category == Interpretable.Category.HRD) return HrdCollapseElement.build(id, sample, service);
		else if(category == Interpretable.Category.MRD) return MrdCollapseElement.build(id, sample, service);
		else return DefaultCollapseElement.build(sample, service);
		/*return switch(category) {
			case DES, WES, DGS, Single, SinglePlus, Cancer, RareDisease -> PanelCollapseElement.build(id, sample, service);
			default -> DefaultCollapseElement.build(sample, service);
		};*/
	}
}
