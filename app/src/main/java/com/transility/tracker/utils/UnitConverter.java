package com.transility.tracker.utils;

public class UnitConverter {

	public static final float LB_KG = 0.453592F;
	public static final float FT_M = 0.3048F;
	public static final float INCH_M = 0.0254F;
	public static final float CM_M = 0.01F;
	public static float getWeightInKG(float amount, String unit){
		float amountInKg = amount;
		if(unit.equals("LB")){
			amountInKg = amount*LB_KG;
		}else{
			amountInKg = amount*1;
		}
		return amountInKg;
	}
	
	public static float getHeightInMeters(float amount, String unit){
		float amountInMeter = amount;
		if(unit.equals("Feet")){
			amountInMeter = amount*FT_M;
		}else if(unit.equals("Inches")){
			amountInMeter = amount*INCH_M;
		}else if(unit.equals("CM")){
			amountInMeter = amount*CM_M;
		}else{
			amountInMeter = amount*1;
		}
		return amountInMeter;
	}
}
