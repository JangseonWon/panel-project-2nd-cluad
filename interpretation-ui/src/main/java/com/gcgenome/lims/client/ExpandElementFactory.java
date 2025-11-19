package com.gcgenome.lims.client;

import com.gcgenome.lims.client.expand.*;
import com.gcgenome.lims.test.Interpretable;
import jsinterop.base.JsPropertyMap;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExpandElementFactory {
	public ExpandElement<?> create(String id, long sample, JsPropertyMap<?> service) {
		Subjects.sample.next(sample);
		Subjects.service.next(service);
		var category = Interpretable.Category.valueOf((String)service.get("interpretation_category"));
		if(category == Interpretable.Category.RareDisease || category == Interpretable.Category.WES || category == Interpretable.Category.WesWithSingle) return PanelExpandElement.build(id, sample, service);
		else if(category == Interpretable.Category.Single || category == Interpretable.Category.SingleWithMLPA ||
				category == Interpretable.Category.SinglePlus || category == Interpretable.Category.Cancer) return SingleGenePanelExpandElement.build(id, sample, service);
		else if(category == Interpretable.Category.BloodCancer) return SomaticExpandElement.build(id);
		else if(category == Interpretable.Category.SolidTumor) return TsoExpandElement.build(id, service);
		else if(category == Interpretable.Category.HRD) return HrdExpandElement.build(id);
		else if(category == Interpretable.Category.MRD) {
			var isScreen = (Boolean) service.get("is_screen");
			if(isScreen) return MrdScreenExpandElement.build(id, service);
			else return MrdExpandElement.build(id, sample, service);
		}
		return DefaultExpandElement.build(sample, service);
	}
}
