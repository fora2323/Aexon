package com.aexon.theme.hct.utils;

import androidx.annotation.ColorInt;

public class AexonColorUtils {
	private static final double[][] SRGB_TO_XYZ = {
		{0.41233895, 0.35762064, 0.18051042},
		{0.2126, 0.7152, 0.0722},
		{0.01932141, 0.11916382, 0.95034478}
	};
	private static final double[][] XYZ_TO_SRGB = {
		{3.2413774792388685, -1.5376652402851851, -0.49885366846268053},
		{-0.9691452513005321, 1.8758853451067872, 0.04156585616912061},
		{0.05562093689691305, -0.20395524564742123, 1.0571799111220335}
	};
	private static final double[] WHITE_POINT_D65 = {95.047, 100.0, 108.883};
	
	public static double[] linrgbFromArgb(@ColorInt int argb) {
		int r = (argb >> 16) & 0xff;
		int g = (argb >> 8) & 0xff;
		int b = argb & 0xff;
		return new double[]{linearized(r), linearized(g), linearized(b)};
	}
	
	@ColorInt
	public static int argbFromLinrgb(double[] linrgb) {
		int r = delinearized(linrgb[0]);
		int g = delinearized(linrgb[1]);
		int b = delinearized(linrgb[2]);
		return argbFromRgb(r, g, b);
	}
	
	@ColorInt
	public static int argbFromRgb(int red, int green, int blue) {
		return (255 << 24) | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff);
	}
	
	public static int alphaFromArgb(@ColorInt int argb) {
		return (argb >> 24) & 0xff;
	}
	
	public static int redFromArgb(@ColorInt int argb) {
		return (argb >> 16) & 0xff;
	}
	
	public static int greenFromArgb(@ColorInt int argb) {
		return (argb >> 8) & 0xff;
	}
	
	public static int blueFromArgb(@ColorInt int argb) {
		return argb & 0xff;
	}
	
	public static boolean isOpaque(@ColorInt int argb) {
		return alphaFromArgb(argb) >= 255;
	}
	
	@ColorInt
	public static int argbFromLstar(double lstar) {
		double y = yFromLstar(lstar);
		int component = delinearized(y);
		return argbFromRgb(component, component, component);
	}
	
	public static double lstarFromArgb(@ColorInt int argb) {
		double y = xyzFromArgb(argb)[1];
		return lstarFromY(y);
	}
	
	public static double yFromLstar(double lstar) {
		return 100.0 * labInvf((lstar + 16.0) / 116.0);
	}
	
	public static double lstarFromY(double y) {
		return labF(y / 100.0) * 116.0 - 16.0;
	}
	
	public static double linearized(int rgbComponent) {
		double normalized = rgbComponent / 255.0;
		if (normalized <= 0.04045) {
			return normalized / 12.92 * 100.0;
		} else {
			return Math.pow((normalized + 0.055) / 1.055, 2.4) * 100.0;
		}
	}
	
	public static int delinearized(double rgbComponent) {
		double normalized = rgbComponent / 100.0;
		double delinearized;
		if (normalized <= 0.0031308) {
			delinearized = normalized * 12.92;
		} else {
			delinearized = 1.055 * Math.pow(normalized, 1.0 / 2.4) - 0.055;
		}
		return AexonMathUtils.clampInt(0, 255, (int) Math.round(delinearized * 255.0));
	}
	
	public static double[] xyzFromArgb(@ColorInt int argb) {
		double[] linrgb = linrgbFromArgb(argb);
		return matrixMultiply(linrgb, SRGB_TO_XYZ);
	}
	
	@ColorInt
	public static int argbFromXyz(double x, double y, double z) {
		double[] linrgb = matrixMultiply(new double[]{x, y, z}, XYZ_TO_SRGB);
		return argbFromLinrgb(linrgb);
	}
	
	public static double[] labFromArgb(@ColorInt int argb) {
		double[] xyz = xyzFromArgb(argb);
		double xNorm = xyz[0] / WHITE_POINT_D65[0];
		double yNorm = xyz[1] / WHITE_POINT_D65[1];
		double zNorm = xyz[2] / WHITE_POINT_D65[2];
		double fx = labF(xNorm);
		double fy = labF(yNorm);
		double fz = labF(zNorm);
		double l = 116.0 * fy - 16;
		double a = 500.0 * (fx - fy);
		double b = 200.0 * (fy - fz);
		return new double[]{l, a, b};
	}
	
	@ColorInt
	public static int argbFromLab(double l, double a, double b) {
		double fy = (l + 16.0) / 116.0;
		double fx = a / 500.0 + fy;
		double fz = fy - b / 200.0;
		double x = labInvf(fx) * WHITE_POINT_D65[0];
		double y = labInvf(fy) * WHITE_POINT_D65[1];
		double z = labInvf(fz) * WHITE_POINT_D65[2];
		return argbFromXyz(x, y, z);
	}
	
	private static double labF(double t) {
		double e = 216.0 / 24389.0;
		double kappa = 24389.0 / 27.0;
		if (t > e) {
			return Math.pow(t, 1.0 / 3.0);
		} else {
			return (kappa * t + 16) / 116;
		}
	}
	
	private static double labInvf(double ft) {
		double e = 216.0 / 24389.0;
		double kappa = 24389.0 / 27.0;
		double ft3 = ft * ft * ft;
		if (ft3 > e) {
			return ft3;
		} else {
			return (116 * ft - 16) / kappa;
		}
	}
	
	private static double[] matrixMultiply(double[] input, double[][] matrix) {
		double x = input[0] * matrix[0][0] + input[1] * matrix[0][1] + input[2] * matrix[0][2];
		double y = input[0] * matrix[1][0] + input[1] * matrix[1][1] + input[2] * matrix[1][2];
		double z = input[0] * matrix[2][0] + input[1] * matrix[2][1] + input[2] * matrix[2][2];
		return new double[]{x, y, z};
	}
}