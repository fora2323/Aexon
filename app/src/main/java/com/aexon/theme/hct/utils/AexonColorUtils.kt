package com.aexon.theme.hct.utils

import kotlin.math.pow
import kotlin.math.roundToInt

object AexonColorUtils {
    private val WHITE_POINT_D65 = doubleArrayOf(95.047, 100.0, 108.883)
    private val SRGB_TO_XYZ = doubleArrayOf(0.41233895, 0.35762064, 0.18051042, 0.2126, 0.7152, 0.0722, 0.01932141, 0.11916382, 0.95034478)
    private val XYZ_TO_SRGB = doubleArrayOf(3.2413774792388685, -1.5376652402851851, -0.49885366846268053, -0.9691452513005321, 1.8758853451067872, 0.04156585616912061, 0.05562093689691305, -0.20395524564742123, 1.0571799111220335)
    
    fun linrgbFromArgb(argb: Int): DoubleArray {
        val r = (argb shr 16) and 0xff
        val g = (argb shr 8) and 0xff
        val b = argb and 0xff
        return doubleArrayOf(
            linearized(r),
            linearized(g),
            linearized(b)
        )
    }
    
    fun argbFromLinrgb(linrgb: DoubleArray): Int {
        val r = delinearized(linrgb[0])
        val g = delinearized(linrgb[1])
        val b = delinearized(linrgb[2])
        return argbFromRgb(r, g, b)
    }
    
    fun argbFromRgb(red: Int, green: Int, blue: Int): Int {
        return (255 shl 24) or ((red and 0xff) shl 16) or ((green and 0xff) shl 8) or (blue and 0xff)
    }
    
    fun alphaFromArgb(argb: Int): Int = (argb shr 24) and 0xff
    fun redFromArgb(argb: Int): Int = (argb shr 16) and 0xff
    fun greenFromArgb(argb: Int): Int = (argb shr 8) and 0xff
    fun blueFromArgb(argb: Int): Int = argb and 0xff
    fun isOpaque(argb: Int): Boolean = alphaFromArgb(argb) >= 255
    
    fun argbFromLstar(lstar: Double): Int {
        val y = yFromLstar(lstar)
        val component = delinearized(y)
        return argbFromRgb(component, component, component)
    }
    
    fun lstarFromArgb(argb: Int): Double {
        val y = xyzFromArgb(argb)[1]
        return lstarFromY(y)
    }
    
    fun yFromLstar(lstar: Double): Double = 100.0 * labInvf((lstar + 16.0) / 116.0)
    fun lstarFromY(y: Double): Double = labF(y / 100.0) * 116.0 - 16.0
    
    fun linearized(rgbComponent: Int): Double {
        val normalized = rgbComponent / 255.0
        return if (normalized <= 0.04045) {
            normalized / 12.92 * 100.0
        } else {
            ((normalized + 0.055) / 1.055).pow(2.4) * 100.0
        }
    }
    
    fun delinearized(rgbComponent: Double): Int {
        val normalized = rgbComponent / 100.0
        val delinearized = if (normalized <= 0.0031308) {
            normalized * 12.92
        } else {
            1.055 * normalized.pow(1.0 / 2.4) - 0.055
        }
        return AexonMathUtils.clampInt(0, 255, (delinearized * 255.0).roundToInt())
    }
    
    fun xyzFromArgb(argb: Int): DoubleArray {
        val r = linearized(redFromArgb(argb))
        val g = linearized(greenFromArgb(argb))
        val b = linearized(blueFromArgb(argb))
        return matrixMultiply(doubleArrayOf(r, g, b), SRGB_TO_XYZ)
    }
    
    fun argbFromXyz(x: Double, y: Double, z: Double): Int {
        val matrix = XYZ_TO_SRGB
        val linearR = matrix[0] * x + matrix[1] * y + matrix[2] * z
        val linearG = matrix[3] * x + matrix[4] * y + matrix[5] * z
        val linearB = matrix[6] * x + matrix[7] * y + matrix[8] * z
        val r = delinearized(linearR)
        val g = delinearized(linearG)
        val b = delinearized(linearB)
        return argbFromRgb(r, g, b)
    }
    
    fun labFromArgb(argb: Int): DoubleArray {
        val xyz = xyzFromArgb(argb)
        val whitePoint = WHITE_POINT_D65
        val fx = labF(xyz[0] / whitePoint[0])
        val fy = labF(xyz[1] / whitePoint[1])
        val fz = labF(xyz[2] / whitePoint[2])
        val L = 116.0 * fy - 16
        val a = 500.0 * (fx - fy)
        val b = 200.0 * (fy - fz)
        return doubleArrayOf(L, a, b)
    }
    
    fun argbFromLab(l: Double, a: Double, b: Double): Int {
        val whitePoint = WHITE_POINT_D65
        val fy = (l + 16.0) / 116.0
        val fx = a / 500.0 + fy
        val fz = fy - b / 200.0
        val x = labInvf(fx) * whitePoint[0]
        val y = labInvf(fy) * whitePoint[1]
        val z = labInvf(fz) * whitePoint[2]
        return argbFromXyz(x, y, z)
    }
    
    private fun labF(t: Double): Double {
        val e = 216.0 / 24389.0
        val kappa = 24389.0 / 27.0
        return if (t > e) {
            t.pow(1.0 / 3.0)
        } else {
            (kappa * t + 16) / 116
        }
    }
    
    private fun labInvf(ft: Double): Double {
        val e = 216.0 / 24389.0
        val kappa = 24389.0 / 27.0
        val ft3 = ft * ft * ft
        return if (ft3 > e) {
            ft3
        } else {
            (116 * ft - 16) / kappa
        }
    }
    
    private fun matrixMultiply(row: DoubleArray, matrix: DoubleArray): DoubleArray {
        val a = row[0] * matrix[0] + row[1] * matrix[1] + row[2] * matrix[2]
        val b = row[0] * matrix[3] + row[1] * matrix[4] + row[2] * matrix[5]
        val c = row[0] * matrix[6] + row[1] * matrix[7] + row[2] * matrix[8]
        return doubleArrayOf(a, b, c)
    }
}