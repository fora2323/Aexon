package com.aexon.theme.hct

import com.aexon.theme.hct.utils.AexonColorUtils
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

class AexonHctCompat(val hue: Double, val chroma: Double, val tone: Double) {
	
	fun toInt(): Int = toInt(tone)
	
	// FIXED: Mengonversi ke ARGB dengan perlindungan Gamut penuh agar Tone & Hue 100% akurat
	fun toInt(targetTone: Double): Int {
		if (targetTone <= 0.0) return 0xFF000000.toInt()
		if (targetTone >= 100.0) return 0xFFFFFFFF.toInt()
		
		// Kunci chroma agar tidak melebihi batas gamut sRGB terluar pada Tone & Hue ini
		val maxSafeChroma = findMaxSafeChromaAtTone(hue, targetTone)
		val actualChroma = minOf(chroma, maxSafeChroma)
		
		val a = actualChroma * cos(Math.toRadians(hue))
		val b = actualChroma * sin(Math.toRadians(hue))
		return AexonColorUtils.argbFromLab(targetTone, a, b)
	}
	
	companion object {
		// FIXED: Booster warna gelap bawaan lu dipertahankan utuh untuk menjaga vibransi palet
		fun fromInt(argb: Int): AexonHctCompat {
			val lab = AexonColorUtils.labFromArgb(argb)
			val l = lab[0]
			val a = lab[1]
			val b = lab[2]
			
			var hue: Double
			var chroma: Double
			
			if (l < 8.0) {
				val r = (argb shr 16) and 0xFF
				val g = (argb shr 8) and 0xFF
				val bl = argb and 0xFF
				val max = maxOf(r, maxOf(g, bl))
				if (max > 0) {
					val nr = r / max.toDouble()
					val ng = g / max.toDouble()
					val nb = bl / max.toDouble()
					val boosted = AexonColorUtils.argbFromRgb(
						(nr * 180).roundToInt(),
						(ng * 180).roundToInt(),
						(nb * 180).roundToInt()
					)
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
		
		// FIXED: Pencarian batas Chroma terluar menggunakan Matematika Gamut Linear murni (Sangat Akurat)
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
		
		// DETEKTOR BARU: Memvalidasi apakah koordinat LAB berada di dalam sRGB sebelum terkena clipping/clamp
		private fun isLabInGamut(l: Double, a: Double, b: Double): Boolean {
			val fy = (l + 16.0) / 116.0
			val fx = a / 500.0 + fy
			val fz = fy - b / 200.0
			
			// Konversi LAB ke XYZ (Mengikuti White Point D65 asli dari AexonColorUtils)
			val x = labInvf(fx) * 95.047
			val y = labInvf(fy) * 100.0
			val z = labInvf(fz) * 108.883
			
			// Transformasi XYZ ke Linear sRGB (Sesuai konstanta XYZ_TO_SRGB di AexonColorUtils)
			val linearR = 3.2413774792388685 * x - 1.5376652402851851 * y - 0.49885366846268053 * z
			val linearG = -0.9691452513005321 * x + 1.8758853451067872 * y + 0.04156585616912061 * z
			val linearB = 0.05562093689691305 * x - 0.20395524564742123 * y + 1.0571799111220335 * z
			
			// Toleransi pembulatan float aman
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