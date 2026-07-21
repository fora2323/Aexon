package com.aexon.core

import android.graphics.Color
import kotlin.math.roundToInt

class AexonColor {
	companion object {
		
		@JvmStatic
		fun blend(from: Int, to: Int, ratio: Float): Int {
			val t = AexonMath.clamp01(ratio)
			val a = AexonMath.lerp(Color.alpha(from), Color.alpha(to), t)
			val r = AexonMath.lerp(Color.red(from), Color.red(to), t)
			val g = AexonMath.lerp(Color.green(from), Color.green(to), t)
			val b = AexonMath.lerp(Color.blue(from), Color.blue(to), t)
			return Color.argb(a, r, g, b)
		}
		
		@JvmStatic
		fun withAlpha(color: Int, alpha: Int): Int {
			val a = AexonMath.clamp(alpha, 0, 255)
			return Color.argb(a, Color.red(color), Color.green(color), Color.blue(color))
		}
		
		@JvmStatic
		fun withAlphaFraction(color: Int, fraction: Float): Int {
			return withAlpha(color, (AexonMath.clamp01(fraction) * 255).roundToInt())
		}
		
		@JvmStatic
		fun lighten(color: Int, amount: Float): Int {
			return blend(color, Color.WHITE, AexonMath.clamp01(amount))
		}
		
		@JvmStatic
		fun darken(color: Int, amount: Float): Int {
			return blend(color, Color.BLACK, AexonMath.clamp01(amount))
		}
		
		@JvmStatic
		fun luminance(color: Int): Double {
			return Color.luminance(color).toDouble()
		}
		
		@JvmStatic
		fun isDark(color: Int): Boolean {
			return luminance(color) < 0.5
		}
		
		@JvmStatic
		fun contrastOn(background: Int, lightColor: Int = Color.WHITE, darkColor: Int = Color.BLACK): Int {
			return if (isDark(background)) lightColor else darkColor
		}
		
		@JvmStatic
		fun toHex(color: Int, includeAlpha: Boolean = false): String {
			return if (includeAlpha) {
				String.format("#%08X", color)
			} else {
				String.format("#%06X", 0xFFFFFF and color)
			}
		}
		
		@JvmStatic
		fun fromHex(hex: String): Int {
			return Color.parseColor(hex)
		}
	}
}