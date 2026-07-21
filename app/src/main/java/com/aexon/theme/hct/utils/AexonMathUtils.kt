package com.aexon.theme.hct.utils

import kotlin.math.abs

object AexonMathUtils {
    fun clampDouble(min: Double, max: Double, input: Double): Double {
        return when {
            input < min -> min
            input > max -> max
            else -> input
        }
    }
    
    fun clampInt(min: Int, max: Int, input: Int): Int {
        return when {
            input < min -> min
            input > max -> max
            else -> input
        }
    }
    
    fun sanitizeDegreesDouble(degrees: Double): Double {
        var result = degrees % 360.0
        if (result < 0) result += 360.0
        return result
    }
    
    fun sanitizeDegreesInt(degrees: Int): Int {
        var result = degrees % 360
        if (result < 0) result += 360
        return result
    }
    
    fun differenceDegrees(a: Double, b: Double): Double {
        return 180.0 - abs(abs(a - b) - 180.0)
    }
    
    fun rotationDirection(from: Double, to: Double): Double {
        val increasingDifference = sanitizeDegreesDouble(to - from)
        return if (increasingDifference <= 180.0) 1.0 else -1.0
    }
    
    fun lerp(start: Double, stop: Double, amount: Double): Double {
        return (1.0 - amount) * start + amount * stop
    }
    
    fun signum(value: Double): Double {
        return when {
            value < 0 -> -1.0
            value > 0 -> 1.0
            else -> 0.0
        }
    }
}