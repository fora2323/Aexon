package com.aexon.theme.hct

import kotlin.math.max
import kotlin.math.min

class AexonSchemeCompat(seedColor: Int, private val isDark: Boolean) {
    private val primary: AexonSchemeTonal
    private val secondary: AexonSchemeTonal
    private val tertiary: AexonSchemeTonal
    private val error: AexonSchemeTonal
    private val neutral: AexonSchemeTonal
    private val neutralVariant: AexonSchemeTonal
    private val isGrayscaleSeed: Boolean
    private val seedTone: Double
    
    init {
        val hct = AexonHctCompat.fromInt(seedColor)
        val rawHue = hct.hue
        val rawChroma = hct.chroma
        val rawTone = hct.tone
        
        this.isGrayscaleSeed = rawChroma < GRAYSCALE_CHROMA_THRESHOLD
        this.seedTone = rawTone
        
        val chosen: List<Double> = when {
            isGrayscaleSeed -> listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
            rawTone > NEAR_WHITE_TONE_FLOOR && rawChroma < PASTEL_CHROMA_CEILING -> {
                val cappedChroma = min(rawChroma, 30.0)
                listOf(rawHue, max(cappedChroma * 0.9, 6.0), max(cappedChroma * 0.4, 3.0), 60.0, max(cappedChroma * 0.5, 4.0), min(cappedChroma * 0.1, 3.0), min(cappedChroma * 0.15, 5.0))
            }
            rawChroma < PASTEL_CHROMA_CEILING && rawTone > PASTEL_TONE_FLOOR -> {
                val chromaScale = rawChroma / PASTEL_CHROMA_CEILING
                val toneScale = (rawTone - PASTEL_TONE_FLOOR) / (NEAR_WHITE_TONE_FLOOR - PASTEL_TONE_FLOOR)
                val blendFactor = chromaScale * (1.0 - toneScale * 0.6)
                val primaryChroma = max(rawChroma + blendFactor * (28.0 - rawChroma), 8.0)
                listOf(rawHue, primaryChroma, max(rawChroma * 0.55, 4.0), 60.0, max(rawChroma * 0.7, 5.0), min(rawChroma * 0.25, 4.0), min(rawChroma * 0.4, 7.0))
            }
            else -> {
                listOf(
                    rawHue, 
                    max(rawChroma, DEFAULT_PRIMARY_CHROMA), 
                    max(rawChroma * 0.5, 26.0), 
                    60.0, 
                    max(rawChroma * 0.7, 32.0), 
                    4.0, 
                    8.0
                )
            }
        }
        
        val primaryHue = chosen[0]
        val primaryChroma = chosen[1]
        val secondaryChroma = chosen[2]
        val tertiaryHueOffset = chosen[3]
        val tertiaryChroma = chosen[4]
        val neutralChroma = chosen[5]
        val neutralVariantChroma = chosen[6]
        
        val tertiaryHue = sanitizeHue(primaryHue + tertiaryHueOffset)
        
        this.primary = AexonSchemeTonal(primaryHue, primaryChroma)
        this.secondary = AexonSchemeTonal(primaryHue, secondaryChroma)
        this.tertiary = AexonSchemeTonal(tertiaryHue, tertiaryChroma)
        this.error = AexonSchemeTonal(25.0, 84.0)
        this.neutral = AexonSchemeTonal(primaryHue, neutralChroma)
        this.neutralVariant = AexonSchemeTonal(primaryHue, neutralVariantChroma)
    }
    
    fun isGrayscaleSeed() = isGrayscaleSeed
    
    fun colorPrimary(): Int {
        return if (!isDark) {
            primary.tone(seedTone.toInt())
        } else {
            primary.vividColor(80)
        }
    }
    
    fun colorPrimaryContainer(): Int {
        return if (!isDark && seedTone >= 60.0) {
            primary.tone(max(40, seedTone.toInt() - 20))
        } else {
            primary.tone(if (isDark) 30 else 90)
        }
    }
    
    fun colorOnPrimary(): Int {
        return if (!isDark && seedTone >= 60.0) {
            primary.tone(10)
        } else {
            primary.tone(if (isDark) 20 else 100)
        }
    }
    
    fun colorOnPrimaryContainer(): Int {
        return if (!isDark && seedTone >= 60.0) {
            primary.tone(10)
        } else {
            primary.tone(if (isDark) 90 else 10)
        }
    }
    
    fun colorSecondary(): Int = if (isDark) secondary.vividColor(80) else secondary.tone(max(40, seedTone.toInt()))
    fun colorOnSecondary(): Int = secondary.tone(if (isDark) 20 else 100)
    fun colorSecondaryContainer(): Int = secondary.tone(if (isDark) 30 else 90)
    fun colorOnSecondaryContainer(): Int = secondary.tone(if (isDark) 90 else 10)
    
    fun colorTertiary(): Int = if (isDark) tertiary.vividColor(80) else tertiary.tone(max(40, seedTone.toInt()))
    fun colorOnTertiary(): Int = tertiary.tone(if (isDark) 20 else 100)
    fun colorTertiaryContainer(): Int = tertiary.tone(if (isDark) 30 else 90)
    fun colorOnTertiaryContainer(): Int = tertiary.tone(if (isDark) 90 else 10)
    
    fun colorError(): Int = if (isDark) error.vividColor(80) else error.tone(40)
    fun colorOnError(): Int = error.tone(if (isDark) 20 else 100)
    fun colorErrorContainer(): Int = error.tone(if (isDark) 30 else 90)
    fun colorOnErrorContainer(): Int = error.tone(if (isDark) 90 else 10)
    
    fun colorSurface(): Int = neutral.tone(if (isDark) 6 else 98)
    fun colorOnSurface(): Int = neutral.tone(if (isDark) 90 else 10)
    fun colorSurfaceVariant(): Int = neutralVariant.tone(if (isDark) 30 else 90)
    fun colorOnSurfaceVariant(): Int = neutralVariant.tone(if (isDark) 80 else 30)
    
    fun colorSurfaceDim(): Int = neutral.tone(if (isDark) 6 else 87)
    fun colorSurfaceBright(): Int = neutral.tone(if (isDark) 24 else 98)
    fun colorSurfaceContainerLowest(): Int = neutral.tone(if (isDark) 4 else 100)
    fun colorSurfaceContainerLow(): Int = neutral.tone(if (isDark) 10 else 96)
    fun colorSurfaceContainer(): Int = neutral.tone(if (isDark) 12 else 94)
    fun colorSurfaceContainerHigh(): Int = neutral.tone(if (isDark) 17 else 92)
    fun colorSurfaceContainerHighest(): Int = neutral.tone(if (isDark) 22 else 90)
    
    fun colorSurfaceInverse(): Int = neutral.tone(if (isDark) 90 else 20)
    fun colorOnSurfaceInverse(): Int = neutral.tone(if (isDark) 20 else 95)
    fun colorPrimaryInverse(): Int = primary.tone(if (isDark) 40 else 80)
    
    fun colorOutline(): Int = neutralVariant.tone(if (isDark) 60 else 50)
    fun colorOutlineVariant(): Int = neutralVariant.tone(if (isDark) 30 else 80)
    
    fun colorScrim(): Int = neutral.tone(0)
    fun colorScrimFix(): Int = neutral.tone(if (isDark) 10 else 80)
    
    fun colorControlHighlight(): Int = primary.tone(if (isDark) 30 else 90)
    fun colorControlNormal(): Int = neutralVariant.tone(if (isDark) 60 else 50)
    
    companion object {
        private const val GRAYSCALE_CHROMA_THRESHOLD = 5.0
        private const val NEAR_WHITE_TONE_FLOOR = 90.0
        private const val PASTEL_CHROMA_CEILING = 22.0
        private const val PASTEL_TONE_FLOOR = 78.0
        private const val DEFAULT_PRIMARY_CHROMA = 48.0
        
        private fun sanitizeHue(hue: Double): Double {
            var result = hue % 360.0
            if (result < 0.0) result += 360.0
            return result
        }
    }
}