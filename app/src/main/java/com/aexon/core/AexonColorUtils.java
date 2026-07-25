package com.aexon.core;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

public class AexonColorUtils {
	
	@ColorInt
	public static int setAlphaComponent(@ColorInt int color, @IntRange(from = 0, to = 255) int alpha) {
		if (alpha < 0 || alpha > 255) {
			throw new IllegalArgumentException("alpha must be between 0 and 255.");
		}
		return (color & 0x00FFFFFF) | (alpha << 24);
	}
	
	@ColorInt
	public static int setAlphaComponent(@ColorInt int color, @FloatRange(from = 0.0, to = 1.0) float alphaPercentage) {
		return setAlphaComponent(color, Math.round(255 * alphaPercentage));
	}
	
	@IntRange(from = 0, to = 255)
	public static int alpha(@ColorInt int color) {
		return color >>> 24;
	}
	
	@IntRange(from = 0, to = 255)
	public static int red(@ColorInt int color) {
		return (color >> 16) & 0xFF;
	}
	
	@IntRange(from = 0, to = 255)
	public static int green(@ColorInt int color) {
		return (color >> 8) & 0xFF;
	}
	
	@IntRange(from = 0, to = 255)
	public static int blue(@ColorInt int color) {
		return color & 0xFF;
	}
	
	@ColorInt
	public static int blendARGB(@ColorInt int color1, @ColorInt int color2, @FloatRange(from = 0.0, to = 1.0) float ratio) {
		float inverseRatio = 1 - ratio;
		float a = alpha(color1) * inverseRatio + alpha(color2) * ratio;
		float r = red(color1) * inverseRatio + red(color2) * ratio;
		float g = green(color1) * inverseRatio + green(color2) * ratio;
		float b = blue(color1) * inverseRatio + blue(color2) * ratio;
		return ((Math.round(a) & 0xFF) << 24) | ((Math.round(r) & 0xFF) << 16) | ((Math.round(g) & 0xFF) << 8) | (Math.round(b) & 0xFF);
	}
}