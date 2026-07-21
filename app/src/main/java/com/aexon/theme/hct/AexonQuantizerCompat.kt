package com.aexon.theme.hct

import com.aexon.annotation.NonNull

object AexonQuantizerCompat {
    fun extractDominantColor(@NonNull pixels: IntArray, fallbackColor: Int): Int {
        val colorCounts = mutableMapOf<Int, Int>()
        val colorSums = mutableMapOf<Int, LongArray>()
        
        for (pixel in pixels) {
            val alpha = (pixel shr 24) and 0xff
            if (alpha < 240) continue
            
            val r = (pixel shr 16) and 0xff
            val g = (pixel shr 8) and 0xff
            val b = pixel and 0xff
            
            val groupedColor = (0xff shl 24) or ((r and 0xF0) shl 16) or ((g and 0xF0) shl 8) or (b and 0xF0)
            colorCounts[groupedColor] = colorCounts.getOrDefault(groupedColor, 0) + 1
            
            val sums = colorSums.getOrPut(groupedColor) { longArrayOf(0, 0, 0) }
            sums[0] += r
            sums[1] += g
            sums[2] += b
        }
        
        if (colorCounts.isEmpty()) return fallbackColor
        
        var bestColor = fallbackColor
        var maxScore = -1.0
        
        for ((group, count) in colorCounts) {
            val sums = colorSums[group] ?: continue
            val avgR = (sums[0] / count).toInt()
            val avgG = (sums[1] / count).toInt()
            val avgB = (sums[2] / count).toInt()
            val avgColor = (0xff shl 24) or (avgR shl 16) or (avgG shl 8) or avgB
            
            val hct = AexonHctCompat.fromInt(avgColor)
            val chroma = hct.chroma
            if (chroma < 15.0 && colorCounts.size > 1) continue
            
            val populationFraction = count.toDouble() / pixels.size
            val score = (chroma * 2.0) + (populationFraction * 100.0)
            if (score > maxScore) {
                maxScore = score
                bestColor = avgColor
            }
        }
        
        if (maxScore == -1.0) {
            var maxCount = 0
            for ((group, count) in colorCounts) {
                if (count > maxCount) {
                    maxCount = count
                    val sums = colorSums[group] ?: continue
                    bestColor = (0xff shl 24) or ((sums[0] / maxCount).toInt() shl 16) or ((sums[1] / maxCount).toInt() shl 8) or (sums[2] / maxCount).toInt()
                }
            }
        }
        return bestColor
    }
}