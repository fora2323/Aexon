package com.aexon.theme.hct.utils

import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils

import kotlin.math.pow
import kotlin.math.roundToInt

/**
* Konversi warna low-level (linear RGB <-> XYZ <-> Lab).
* Konversi XYZ & Lab dilimpahkan ke androidx.core.graphics.ColorUtils
* (sudah teruji, white point D65-nya sama persis dengan yang dipakai di sini).
* Bagian linear RGB & L* tetap custom karena androidx tidak expose fungsi itu langsung.
*/
object AexonColorUtils {
	
	fun linrgbFromArgb(@ColorInt argb: Int): DoubleArray {
		val r = (argb shr 16) and 0xff
		val g = (argb shr 8) and 0xff
		val b = argb and 0xff
		return doubleArrayOf(
		linearized(r),
		linearized(g),
		linearized(b)
		)
	}
	
	@ColorInt
	fun argbFromLinrgb(linrgb: DoubleArray): Int {
		val r = delinearized(linrgb[0])
		val g = delinearized(linrgb[1])
		val b = delinearized(linrgb[2])
		return argbFromRgb(r, g, b)
	}
	
	@ColorInt
	fun argbFromRgb(red: Int, green: Int, blue: Int): Int {
		return (255 shl 24) or ((red and 0xff) shl 16) or ((green and 0xff) shl 8) or (blue and 0xff)
	}
	
	fun alphaFromArgb(@ColorInt argb: Int): Int = (argb shr 24) and 0xff
	fun redFromArgb(@ColorInt argb: Int): Int = (argb shr 16) and 0xff
	fun greenFromArgb(@ColorInt argb: Int): Int = (argb shr 8) and 0xff
	fun blueFromArgb(@ColorInt argb: Int): Int = argb and 0xff
	fun isOpaque(@ColorInt argb: Int): Boolean = alphaFromArgb(argb) >= 255
	
	@ColorInt
	fun argbFromLstar(lstar: Double): Int {
		val y = yFromLstar(lstar)
		val component = delinearized(y)
		return argbFromRgb(component, component, component)
	}
	
	// L* = komponen pertama dari ColorUtils.colorToLAB (androidx) — sama definisinya (CIE L*)
	fun lstarFromArgb(@ColorInt argb: Int): Double {
		val lab = DoubleArray(3)
		ColorUtils.colorToLAB(argb, lab)
		return lab[0]
	}
	
	fun yFromLstar(lstar: Double): Double = 100.0 * labInvf((lstar + 16.0) / 116.0)
	fun lstarFromY(y: Double): Double = labF(y / 100.0) * 116.0 - 16.0
	
	fun linearized(rgbComponent: Int): Double {
		val normalized = rgbComponent / 255.0
		return if (normalized <= 0.04045) {
			normalized / 12.92 * 100.0
		} else {
			((normalized + 0.055) / 1.055).pow(2.4) * 100.0
		}
	}
	
	fun delinearized(rgbComponent: Double): Int {
		val normalized = rgbComponent / 100.0
		val delinearized = if (normalized <= 0.0031308) {
			normalized * 12.92
		} else {
			1.055 * normalized.pow(1.0 / 2.4) - 0.055
		}
		return AexonMathUtils.clampInt(0, 255, (delinearized * 255.0).roundToInt())
	}
	
	// Delegasi ke androidx.core.graphics.ColorUtils.colorToXYZ
	fun xyzFromArgb(@ColorInt argb: Int): DoubleArray {
		val xyz = DoubleArray(3)
		ColorUtils.colorToXYZ(argb, xyz)
		return xyz
	}
	
	@ColorInt
	fun argbFromXyz(x: Double, y: Double, z: Double): Int {
		return ColorUtils.XYZToColor(x, y, z)
	}
	
	// Delegasi ke androidx.core.graphics.ColorUtils.colorToLAB
	fun labFromArgb(@ColorInt argb: Int): DoubleArray {
		val lab = DoubleArray(3)
		ColorUtils.colorToLAB(argb, lab)
		return lab
	}
	
	@ColorInt
	fun argbFromLab(l: Double, a: Double, b: Double): Int {
		return ColorUtils.LABToColor(l, a, b)
	}
	
	private fun labF(t: Double): Double {
		val e = 216.0 / 24389.0
		val kappa = 24389.0 / 27.0
		return if (t > e) {
			t.pow(1.0 / 3.0)
		} else {
			(kappa * t + 16) / 116
		}
	}
	
	private fun labInvf(ft: Double): Double {
		val e = 216.0 / 24389.0
		val kappa = 24389.0 / 27.0
		val ft3 = ft * ft * ft
		return if (ft3 > e) {
			ft3
		} else {
			(116 * ft - 16) / kappa
		}
	}
}