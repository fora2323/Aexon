package com.aexon.theme.hct

import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils

/**
* Rasio kontras WCAG antara 2 warna — dilimpahkan penuh ke
* androidx.core.graphics.ColorUtils.calculateContrast (rumusnya identik
* dengan implementasi manual sebelumnya, cuma dipindah ke fungsi teruji).
*/
object AexonContrastColor {
	fun calculateContrast(@ColorInt foreground: Int, @ColorInt background: Int): Double {
		return ColorUtils.calculateContrast(foreground, background)
	}
	
	fun isContrastValid(@ColorInt foreground: Int, @ColorInt background: Int, minContrastRatio: Double): Boolean {
		return calculateContrast(foreground, background) >= minContrastRatio
	}
}