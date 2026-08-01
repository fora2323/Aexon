package com.aexon.theme.hct.utils;

import androidx.annotation.ColorInt;

public class AexonDynamicColorUtils {
    @ColorInt
    public static int blend(@ColorInt int colorFrom, @ColorInt int colorTo, double ratio) {
        double inverseRatio = 1 - ratio;
        int a = (int) (AexonColorUtils.alphaFromArgb(colorFrom) * inverseRatio + AexonColorUtils.alphaFromArgb(colorTo) * ratio);
        int r = (int) (AexonColorUtils.redFromArgb(colorFrom) * inverseRatio + AexonColorUtils.redFromArgb(colorTo) * ratio);
        int g = (int) (AexonColorUtils.greenFromArgb(colorFrom) * inverseRatio + AexonColorUtils.greenFromArgb(colorTo) * ratio);
        int b = (int) (AexonColorUtils.blueFromArgb(colorFrom) * inverseRatio + AexonColorUtils.blueFromArgb(colorTo) * ratio);
        return ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
    }

    @ColorInt
    public static int darken(@ColorInt int color, double amount) {
        return blend(color, 0xFF000000, amount);
    }

    @ColorInt
    public static int lighten(@ColorInt int color, double amount) {
        return blend(color, 0xFFFFFFFF, amount);
    }
}