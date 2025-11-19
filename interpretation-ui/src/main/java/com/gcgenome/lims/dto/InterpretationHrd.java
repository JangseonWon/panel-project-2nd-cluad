package com.gcgenome.lims.dto;

import jsinterop.annotations.*;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
public final class InterpretationHrd {
	public String hrd;
	@JsProperty(name="cancer_type")
	public String cancerType;
	public String gi;
	@JsProperty(name="gi_score")
	public Double giScore;
	public String brca;
	public String snv;
	public String cnv;
	@JsProperty(name="tumor_fraction")
	public String tumorFraction;
	public String interpretation;
	public InterpretationTier[] results;

	@JsOverlay
	@JsIgnore
	public static InterpretationHrd createWithDefaults() {
		InterpretationHrd interpretationHrd = new InterpretationHrd();
		interpretationHrd.giScore = 0.0;
		return interpretationHrd;
	}

	@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
	@Accessors(fluent=true)
	@NoArgsConstructor
	public static final class InterpretationTier {
		public String tier;
		public Variant[] variants;
		public String interpretation;
	}
	@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
	@Accessors(fluent=true)
	public static final class Variant {
		public String snv;
		public String analysis;
		public String gene;
		public String hgvsc;
		public String hgvsp;
		public Double vaf;
		public Double depth;
		@JsProperty(name="cosmic_id")
		public String cosmicId;
		public String dna;
		public String protein;
		@JsOverlay
		@JsIgnore
		public InterpretationHrd.Variant depth(Integer depth) {
			if(depth!=null) this.depth = depth + 0.0;
			else this.depth = null;
			return this;
		}
		@JsOverlay
		@JsIgnore
		public Integer depth() {
			if(depth == null) return null;
			else return depth.intValue();
		}
	}
	public enum Tier {
		BRCA,
		TIER1,
		TIER2;

		public static boolean equalsIgnoreCaseRemoveWhitespace(Tier tier, String str) {
			return str.replaceAll("\\s", "").equalsIgnoreCase(tier.toString());
		}
		public String capitalizeFirstLetterOfNumericTiers(){ // BI와 협의된 tier format
			return this.toString().matches(".*\\d+.*") ?
					this.toString().toLowerCase().replaceFirst(".", Character.toString(this.toString().charAt(0)).toUpperCase())
					: this.toString();
		}
		public String getAdjustedTierForReport() { // (SNV/InDel 탭에 반영되는 annotation 파일 기준) BRCA 변이는 Tier 1 으로 보고되거나, 보고되지 않는다.
			return (this.equals(BRCA)) ? TIER1.capitalizeFirstLetterOfNumericTiers() : this.capitalizeFirstLetterOfNumericTiers();
		}
	}
	public enum Status {
		POSITIVE("Positive"), NEGATIVE("Negative"),
		PASS("Pass"), NON_PASS("Non-Pass"),
		FAIL("Fail");

		private final String label;
		Status(String label) {this.label = label;}
		public String label() {return label;}
	}
}
