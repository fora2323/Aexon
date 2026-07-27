package com.aexon.core

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

class AexonMath {
	companion object {
		
		@JvmStatic
		fun dpToPx(context: Context, dp: Float): Float {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
		}
		
		@JvmStatic
		fun dpToPx(dp: Float): Float {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().displayMetrics)
		}
		
		@JvmStatic
		fun dpToPxInt(context: Context, dp: Float): Int {
			return dpToPx(context, dp).toInt()
		}
		
		@JvmStatic
		fun dpToPxInt(dp: Float): Int {
			return dpToPx(dp).toInt()
		}
		
		@JvmStatic
		fun pxToDp(context: Context, px: Float): Float {
			return px / context.resources.displayMetrics.density
		}
		
		@JvmStatic
		fun pxToDp(px: Float): Float {
			return px / Resources.getSystem().displayMetrics.density
		}
		
		@JvmStatic
		fun spToPx(context: Context, sp: Float): Float {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)
		}
		
		@JvmStatic
		fun spToPx(sp: Float): Float {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().displayMetrics)
		}
		
		@JvmStatic
		fun spToPxInt(context: Context, sp: Float): Int {
			return spToPx(context, sp).toInt()
		}
		
		@JvmStatic
		fun spToPxInt(sp: Float): Int {
			return spToPx(sp).toInt()
		}
		
		@JvmStatic
		fun pxToSp(context: Context, px: Float): Float {
			return px / context.resources.displayMetrics.scaledDensity
		}
		
		@JvmStatic
		fun pxToSp(px: Float): Float {
			return px / Resources.getSystem().displayMetrics.scaledDensity
		}
		
		@JvmStatic
		fun clamp(value: Float, minValue: Float, maxValue: Float): Float {
			return max(minValue, min(maxValue, value))
		}
		
		@JvmStatic
		fun clamp(value: Int, minValue: Int, maxValue: Int): Int {
			return max(minValue, min(maxValue, value))
		}
		
		@JvmStatic
		fun clamp01(value: Float): Float {
			return clamp(value, 0f, 1f)
		}
		
		@JvmStatic
		fun lerp(start: Float, end: Float, fraction: Float): Float {
			return start + fraction * (end - start)
		}
		
		@JvmStatic
		fun lerp(start: Int, end: Int, fraction: Float): Int {
			return (start + fraction * (end - start)).roundToInt()
		}
		
		@JvmStatic
		fun inverseLerp(start: Float, end: Float, value: Float): Float {
			if (start == end) return 0f
			return clamp01((value - start) / (end - start))
		}
		
		@JvmStatic
		fun map(value: Float, inMin: Float, inMax: Float, outMin: Float, outMax: Float): Float {
			val t = inverseLerp(inMin, inMax, value)
			return lerp(outMin, outMax, t)
		}
		
		@JvmStatic
		fun roundTo(value: Float, decimals: Int): Float {
			val factor = Math.pow(10.0, decimals.toDouble()).toFloat()
			return (value * factor).roundToInt() / factor
		}
		
		@JvmStatic
		fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
			val dx = x2 - x1
			val dy = y2 - y1
			return sqrt(dx * dx + dy * dy)
		}
		
		@JvmStatic
		fun isApprox(a: Float, b: Float, epsilon: Float = 0.0001f): Boolean {
			return abs(a - b) < epsilon
		}
		
		@JvmStatic
		fun wrap(value: Int, min: Int, max: Int): Int {
			val range = max - min
			if (range <= 0) return min
			var result = (value - min) % range
			if (result < 0) result += range
			return result + min
		}
	}
}