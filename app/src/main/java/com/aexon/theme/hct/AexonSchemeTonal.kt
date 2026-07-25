package com.aexon.theme.hct

import kotlin.math.max
import kotlin.math.min

class AexonSchemeTonal(private val hue: Double, private val chroma: Double) {
    fun tone(tone: Int): Int {
        val safeChroma = AexonHctCompat.findMaxSafeChromaAtTone(hue, tone.toDouble())
        val clampedChroma = min(chroma, safeChroma)
        val hct = AexonHctCompat(hue, clampedChroma, tone.toDouble())
        return hct.toInt()
    }
    
    fun vividColor(preferredTone: Int, minTone: Int = 50, minChromaRatio: Double = 0.75): Int {
        if (chroma <= 0.0) return tone(preferredTone)
        
        var t = preferredTone
        while (t > minTone) {
            val safeChroma = AexonHctCompat.findMaxSafeChromaAtTone(hue, t.toDouble())
            if (safeChroma / chroma >= minChromaRatio) break
            t -= 2
        }
        return tone(max(t, minTone))
    }
    
    companion object {
        fun fromInt(argb: Int): AexonSchemeTonal {
            val hct = AexonHctCompat.fromInt(argb)
            return AexonSchemeTonal(hct.hue, hct.chroma)
        }
    }
}