package com.aexon.theme.hct.utils

import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils

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