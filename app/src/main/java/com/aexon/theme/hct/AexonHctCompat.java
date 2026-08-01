package com.aexon.theme.hct;

import androidx.annotation.ColorInt;

import com.aexon.theme.hct.utils.AexonColorUtils;

public class AexonHctCompat {
	private final double hue;
	private final double chroma;
	private final double tone;
	
	public AexonHctCompat(double hue, double chroma, double tone) {
		this.hue = hue;
		this.chroma = chroma;
		this.tone = tone;
	}
	
	public double getHue() {
		return hue;
	}
	
	public double getChroma() {
		return chroma;
	}
	
	public double getTone() {
		return tone;
	}
	
	@ColorInt
	public int toInt() {
		return toInt(tone);
	}
	
	@ColorInt
	public int toInt(double targetTone) {
		if (targetTone <= 0.0) return 0xFF000000;
		if (targetTone >= 100.0) return 0xFFFFFFFF;
		
		double maxSafeChroma = findMaxSafeChromaAtTone(hue, targetTone);
		double actualChroma = Math.min(chroma, maxSafeChroma);
		
		double a = actualChroma * Math.cos(Math.toRadians(hue));
		double b = actualChroma * Math.sin(Math.toRadians(hue));
		return AexonColorUtils.argbFromLab(targetTone, a, b);
	}
	
	public static AexonHctCompat fromInt(@ColorInt int argb) {
		double[] lab = AexonColorUtils.labFromArgb(argb);
		double l = lab[0];
		double a = lab[1];
		double b = lab[2];
		
		double hue;
		double chroma;
		
		if (l < 8.0) {
			int r = (argb >> 16) & 0xFF;
			int g = (argb >> 8) & 0xFF;
			int bl = argb & 0xFF;
			int max = Math.max(r, Math.max(g, bl));
			if (max > 0) {
				double nr = r / (double) max;
				double ng = g / (double) max;
				double nb = bl / (double) max;
				int boosted = AexonColorUtils.argbFromRgb((int) Math.round(nr * 180), (int) Math.round(ng * 180), (int) Math.round(nb * 180));
				double[] boostedLab = AexonColorUtils.labFromArgb(boosted);
				hue = Math.toDegrees(Math.atan2(boostedLab[2], boostedLab[1]));
				if (hue < 0) hue += 360.0;
				chroma = Math.sqrt(boostedLab[1] * boostedLab[1] + boostedLab[2] * boostedLab[2]);
			} else {
				hue = 0.0;
				chroma = 0.0;
			}
		} else {
			hue = Math.toDegrees(Math.atan2(b, a));
			if (hue < 0) hue += 360.0;
			chroma = Math.sqrt(a * a + b * b);
		}
		
		return new AexonHctCompat(hue, chroma, l);
	}
	
	public static double findMaxSafeChromaAtTone(double hue, double tone) {
		if (tone <= 0.0 || tone >= 100.0) return 0.0;
		double hueRad = Math.toRadians(hue);
		double cosH = Math.cos(hueRad);
		double sinH = Math.sin(hueRad);
		double low = 0.0;
		double high = 160.0;
		
		for (int i = 0; i < 24; i++) {
			double mid = (low + high) / 2.0;
			double a = mid * cosH;
			double b = mid * sinH;
			if (isLabInGamut(tone, a, b)) {
				low = mid;
			} else {
				high = mid;
			}
		}
		return low;
	}
	
	private static boolean isLabInGamut(double l, double a, double b) {
		double fy = (l + 16.0) / 116.0;
		double fx = a / 500.0 + fy;
		double fz = fy - b / 200.0;
		
		double x = labInvf(fx) * 95.047;
		double y = labInvf(fy) * 100.0;
		double z = labInvf(fz) * 108.883;
		
		double linearR = 3.2413774792388685 * x - 1.5376652402851851 * y - 0.49885366846268053 * z;
		double linearG = -0.9691452513005321 * x + 1.8758853451067872 * y + 0.04156585616912061 * z;
		double linearB = 0.05562093689691305 * x - 0.20395524564742123 * y + 1.0571799111220335 * z;
		
		double epsilon = 0.001;
		return linearR >= -epsilon && linearR <= 100.0 + epsilon && linearG >= -epsilon && linearG <= 100.0 + epsilon && linearB >= -epsilon && linearB <= 100.0 + epsilon;
	}
	
	private static double labInvf(double ft) {
		double e = 216.0 / 24389.0;
		double kappa = 24389.0 / 27.0;
		double ft3 = ft * ft * ft;
		return ft3 > e ? ft3 : (116.0 * ft - 16.0) / kappa;
	}
}