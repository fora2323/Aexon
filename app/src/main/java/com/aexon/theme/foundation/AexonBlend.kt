package com.aexon.theme.foundation

import com.aexon.theme.hct.AexonHctCompat
import com.aexon.theme.hct.utils.AexonMathUtils
import kotlin.math.min

object AexonBlend {
    fun harmonize(designColor: Int, sourceColor: Int): Int {
        val from = AexonHctCompat.fromInt(designColor)
        val to = AexonHctCompat.fromInt(sourceColor)
        
        val diff = AexonMathUtils.differenceDegrees(from.hue, to.hue)
        val rotation = min(diff * 0.5, 15.0)
        val outputHue = AexonMathUtils.sanitizeDegreesDouble(
            from.hue + rotation * AexonMathUtils.rotationDirection(from.hue, to.hue)
        )
        
        return AexonHctCompat(outputHue, from.chroma, from.tone).toInt()
    }

    fun blendHct(fromColor: Int, toColor: Int, ratio: Double): Int {
        val from = AexonHctCompat.fromInt(fromColor)
        val to = AexonHctCompat.fromInt(toColor)
        
        val diff = AexonMathUtils.differenceDegrees(from.hue, to.hue)
        val rotation = diff * ratio
        val outputHue = AexonMathUtils.sanitizeDegreesDouble(
            from.hue + rotation * AexonMathUtils.rotationDirection(from.hue, to.hue)
        )
        
        val outputChroma = from.chroma + (to.chroma - from.chroma) * ratio
        val outputTone = from.tone + (to.tone - from.tone) * ratio
        
        return AexonHctCompat(outputHue, outputChroma, outputTone).toInt()
    }
}