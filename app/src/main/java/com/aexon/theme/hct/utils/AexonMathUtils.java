package com.aexon.theme.hct.utils;

public class AexonMathUtils {
    public static double clampDouble(double min, double max, double input) {
        if (input < min) return min;
        if (input > max) return max;
        return input;
    }

    public static int clampInt(int min, int max, int input) {
        if (input < min) return min;
        if (input > max) return max;
        return input;
    }

    public static double sanitizeDegreesDouble(double degrees) {
        double result = degrees % 360.0;
        if (result < 0) result += 360.0;
        return result;
    }

    public static int sanitizeDegreesInt(int degrees) {
        int result = degrees % 360;
        if (result < 0) result += 360;
        return result;
    }

    public static double differenceDegrees(double a, double b) {
        return 180.0 - Math.abs(Math.abs(a - b) - 180.0);
    }

    public static double rotationDirection(double from, double to) {
        double increasingDifference = sanitizeDegreesDouble(to - from);
        return increasingDifference <= 180.0 ? 1.0 : -1.0;
    }

    public static double lerp(double start, double stop, double amount) {
        return (1.0 - amount) * start + amount * stop;
    }

    public static double signum(double value) {
        if (value < 0) return -1.0;
        if (value > 0) return 1.0;
        return 0.0;
    }
}