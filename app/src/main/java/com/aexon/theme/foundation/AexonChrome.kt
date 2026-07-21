package com.aexon.theme.foundation

import com.aexon.theme.hct.utils.AexonColorUtils
import kotlin.math.atan2
import kotlin.math.sqrt

class AexonChrome private constructor(val hue: Double, val chroma: Double) {
    companion object {
        fun fromInt(argb: Int): AexonChrome {
            val lab = AexonColorUtils.labFromArgb(argb)
            val a = lab[1]
            val b = lab[2]

            var hue = Math.toDegrees(atan2(b, a))
            if (hue < 0) hue += 360.0

            val chroma = sqrt(a * a + b * b)
            return AexonChrome(hue, chroma)
        }
    }
}