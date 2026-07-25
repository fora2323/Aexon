package com.aexon.theme.hct.utils

import com.aexon.theme.hct.utils.AexonColorUtils.alphaFromArgb
import com.aexon.theme.hct.utils.AexonColorUtils.blueFromArgb
import com.aexon.theme.hct.utils.AexonColorUtils.greenFromArgb
import com.aexon.theme.hct.utils.AexonColorUtils.redFromArgb

object AexonDynamicColorUtils {
    fun blend(colorFrom: Int, colorTo: Int, ratio: Double): Int {
        val inverseRatio = 1.0 - ratio
        val a = (alphaFromArgb(colorFrom) * inverseRatio + alphaFromArgb(colorTo) * ratio).toInt()
        val r = (redFromArgb(colorFrom) * inverseRatio + redFromArgb(colorTo) * ratio).toInt()
        val g = (greenFromArgb(colorFrom) * inverseRatio + greenFromArgb(colorTo) * ratio).toInt()
        val b = (blueFromArgb(colorFrom) * inverseRatio + blueFromArgb(colorTo) * ratio).toInt()
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }
    
    fun darken(color: Int, amount: Double): Int = blend(color, 0xFF000000.toInt(), amount)
    fun lighten(color: Int, amount: Double): Int = blend(color, 0xFFFFFFFF.toInt(), amount)
}