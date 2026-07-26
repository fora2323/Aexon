package com.aexon.theme.hct.utils

import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils

/**
* Blend/darken/lighten antar warna ARGB — inti blend dilimpahkan ke
* androidx.core.graphics.ColorUtils.blendARGB (lerp per-channel, hasil
* sama dengan implementasi manual sebelumnya).
*/
object AexonDynamicColorUtils {
	@ColorInt
	fun blend(@ColorInt colorFrom: Int, @ColorInt colorTo: Int, ratio: Double): Int {
		return ColorUtils.blendARGB(colorFrom, colorTo, ratio.toFloat())
	}
	
	@ColorInt
	fun darken(@ColorInt color: Int, amount: Double): Int = blend(color, 0xFF000000.toInt(), amount)
	@ColorInt
	fun lighten(@ColorInt color: Int, amount: Double): Int = blend(color, 0xFFFFFFFF.toInt(), amount)
}