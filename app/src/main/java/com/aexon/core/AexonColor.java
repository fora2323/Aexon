/*
* Copyright (c) 2026 Fora
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
* 
* Contact: Fora <fora060823@gmail.com>
* Created: 27-01-2026
*/
package com.aexon.core.core;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AexonColor {
	
	private AexonColor() {
		throw new UnsupportedOperationException("No instances");
	}
	
	public static int blend(int from, int to, float ratio) {
		float t = AexonMath.clamp01(ratio);
		int a = (int) AexonMath.lerp(Color.alpha(from), Color.alpha(to), t);
		int r = (int) AexonMath.lerp(Color.red(from), Color.red(to), t);
		int g = (int) AexonMath.lerp(Color.green(from), Color.green(to), t);
		int b = (int) AexonMath.lerp(Color.blue(from), Color.blue(to), t);
		return Color.argb(a, r, g, b);
	}
	
	public static int withAlpha(int color, int alpha) {
		int a = AexonMath.clamp(alpha, 0, 255);
		return Color.argb(a, Color.red(color), Color.green(color), Color.blue(color));
	}
	
	public static int withAlphaFraction(int color, float fraction) {
		return withAlpha(color, Math.round(AexonMath.clamp01(fraction) * 255));
	}
	
	public static int lighten(int color, float amount) {
		return blend(color, Color.WHITE, AexonMath.clamp01(amount));
	}
	
	public static int darken(int color, float amount) {
		return blend(color, Color.BLACK, AexonMath.clamp01(amount));
	}
	
	public static double luminance(int color) {
		return Color.luminance(color);
	}
	
	public static boolean isDark(int color) {
		return luminance(color) < 0.5;
	}
	
	public static int contrastOn(int background) {
		return contrastOn(background, Color.WHITE, Color.BLACK);
	}
	
	public static int contrastOn(int background, int lightColor, int darkColor) {
		return isDark(background) ? lightColor : darkColor;
	}
	
	@NonNull
	public static String toHex(int color) {
		return toHex(color, false);
	}
	
	@NonNull
	public static String toHex(int color, boolean includeAlpha) {
		if (includeAlpha) {
			return String.format("#%08X", color);
		} else {
			return String.format("#%06X", 0xFFFFFF & color);
		}
	}
	
	public static int fromHex(@NonNull String hex) {
		return Color.parseColor(hex);
	}
}