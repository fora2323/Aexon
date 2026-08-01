package com.aexon.theme.foundation;

import androidx.annotation.ColorInt;

import com.aexon.theme.hct.utils.AexonColorUtils;

public class AexonChrome {
	private final double hue;
	private final double chroma;
	
	private AexonChrome(double hue, double chroma) {
		this.hue = hue;
		this.chroma = chroma;
	}
	
	public double getHue() {
		return hue;
	}
	
	public double getChroma() {
		return chroma;
	}
	
	public static AexonChrome fromInt(@ColorInt int argb) {
		double[] lab = AexonColorUtils.labFromArgb(argb);
		double a = lab[1];
		double b = lab[2];
		
		double hue = Math.toDegrees(Math.atan2(b, a));
		if (hue < 0) hue += 360.0;
		
		double chroma = Math.sqrt(a * a + b * b);
		return new AexonChrome(hue, chroma);
	}
}