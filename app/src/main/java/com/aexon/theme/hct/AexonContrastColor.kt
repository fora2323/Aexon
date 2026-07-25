package com.aexon.theme.hct

import com.aexon.theme.hct.utils.AexonColorUtils

object AexonContrastColor {
    fun calculateContrast(foreground: Int, background: Int): Double {
        val l1 = AexonColorUtils.yFromLstar(AexonColorUtils.lstarFromArgb(foreground)) / 100.0
        val l2 = AexonColorUtils.yFromLstar(AexonColorUtils.lstarFromArgb(background)) / 100.0
        val lighter = maxOf(l1, l2)
        val darker = minOf(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }
    
    fun isContrastValid(foreground: Int, background: Int, minContrastRatio: Double): Boolean {
        return calculateContrast(foreground, background) >= minContrastRatio
    }
}