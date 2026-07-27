package com.aexon.theme.hct

import androidx.annotation.ColorInt
import com.aexon.theme.hct.utils.AexonColorUtils
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
* HCT (Hue-Chroma-Tone) versi Aexon — inti algoritma Material You
* buat generate skema warna dari 1 seed color.
*/
class AexonHctCompat(val hue: Double, val chroma: Double, val tone: Double) {
	
	@ColorInt
	fun toInt(): Int = toInt(tone)
	
	@ColorInt
	fun toInt(targetTone: Double): Int {
		if (targetTone <= 0.0) return 0xFF000000.toInt()
		if (targetTone >= 100.0) return 0xFFFFFFFF.toInt()
		
		// Chroma diclamp ke batas gamut sRGB paling aman di tone target,
		// biar hasil convert nggak "clipping" jadi warna yang salah
		val maxSafeChroma = findMaxSafeChromaAtTone(hue, targetTone)
		val actualChroma = minOf(chroma, maxSafeChroma)
		
		val a = actualChroma * cos(Math.toRadians(hue))
		val b = actualChroma * sin(Math.toRadians(hue))
		return AexonColorUtils.argbFromLab(targetTone, a, b)
	}
	
	companion object {
		fun fromInt(@ColorInt argb: Int): AexonHctCompat {
			val lab = AexonColorUtils.labFromArgb(argb)
			val l = lab[0]
			val a = lab[1]
			val b = lab[2]
			
			var hue: Double
			var chroma: Double
			
			// Warna sangat gelap (L* < 8) di-"boost" dulu biar hue/chroma-nya
			// nggak noise akibat presisi Lab yang jelek di area gelap
			if (l < 8.0) {
				val r = (argb shr 16) and 0xFF
				val g = (argb shr 8) and 0xFF
				val bl = argb and 0xFF
				val max = maxOf(r, maxOf(g, bl))
				if (max > 0) {
					val nr = r / max.toDouble()
					val ng = g / max.toDouble()
					val nb = bl / max.toDouble()
					val boosted = AexonColorUtils.argbFromRgb((nr * 180).roundToInt(), (ng * 180).roundToInt(), (nb * 180).roundToInt())
					val boostedLab = AexonColorUtils.labFromArgb(boosted)
					hue = atan2(boostedLab[2], boostedLab[1]).let { 
						(Math.toDegrees(it) + 360.0) % 360.0 
					}
					chroma = sqrt(boostedLab[1] * boostedLab[1] + boostedLab[2] * boostedLab[2])
				} else {
					hue = 0.0
					chroma = 0.0
				}
			} else {
				hue = atan2(b, a).let { 
					(Math.toDegrees(it) + 360.0) % 360.0 
				}
				chroma = sqrt(a * a + b * b)
			}
			
			return AexonHctCompat(hue, chroma, l)
		}
		
		// Binary search: cari chroma maksimum di hue+tone tertentu yang masih
		// bisa direpresentasikan sebagai warna sRGB valid (nggak keluar gamut)
		fun findMaxSafeChromaAtTone(hue: Double, tone: Double): Double {
			if (tone <= 0.0 || tone >= 100.0) return 0.0
			val hueRad = Math.toRadians(hue)
			val cosH = cos(hueRad)
			val sinH = sin(hueRad)
			var low = 0.0
			var high = 160.0
			
			repeat(24) {
				val mid = (low + high) / 2.0
				val a = mid * cosH
				val b = mid * sinH
				if (isLabInGamut(tone, a, b)) {
					low = mid
				} else {
					high = mid
				}
			}
			return low
		}
		
		private fun isLabInGamut(l: Double, a: Double, b: Double): Boolean {
			val fy = (l + 16.0) / 116.0
			val fx = a / 500.0 + fy
			val fz = fy - b / 200.0
			
			val x = labInvf(fx) * 95.047
			val y = labInvf(fy) * 100.0
			val z = labInvf(fz) * 108.883
			
			val linearR = 3.2413774792388685 * x - 1.5376652402851851 * y - 0.49885366846268053 * z
			val linearG = -0.9691452513005321 * x + 1.8758853451067872 * y + 0.04156585616912061 * z
			val linearB = 0.05562093689691305 * x - 0.20395524564742123 * y + 1.0571799111220335 * z
			
			val epsilon = 0.001
			return linearR >= -epsilon && linearR <= 100.0 + epsilon &&
			linearG >= -epsilon && linearG <= 100.0 + epsilon &&
			linearB >= -epsilon && linearB <= 100.0 + epsilon
		}
		
		private fun labInvf(ft: Double): Double {
			val e = 216.0 / 24389.0
			val kappa = 24389.0 / 27.0
			val ft3 = ft * ft * ft
			return if (ft3 > e) ft3 else (116.0 * ft - 16.0) / kappa
		}
	}
}