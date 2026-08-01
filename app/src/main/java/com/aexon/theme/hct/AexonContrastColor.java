package com.aexon.theme.hct;

import androidx.annotation.ColorInt;

import com.aexon.theme.hct.utils.AexonColorUtils;

public class AexonContrastColor {
    public static double calculateContrast(@ColorInt int foreground, @ColorInt int background) {
        double l1 = AexonColorUtils.yFromLstar(AexonColorUtils.lstarFromArgb(foreground)) / 100.0;
        double l2 = AexonColorUtils.yFromLstar(AexonColorUtils.lstarFromArgb(background)) / 100.0;
        double lighter = Math.max(l1, l2);
        double darker = Math.min(l1, l2);
        return (lighter + 0.05) / (darker + 0.05);
    }

    public static boolean isContrastValid(@ColorInt int foreground, @ColorInt int background, double minContrastRatio) {
        return calculateContrast(foreground, background) >= minContrastRatio;
    }
}