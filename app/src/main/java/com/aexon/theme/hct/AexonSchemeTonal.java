package com.aexon.theme.hct;

import androidx.annotation.ColorInt;

public class AexonSchemeTonal {
    private final double hue;
    private final double chroma;

    public AexonSchemeTonal(double hue, double chroma) {
        this.hue = hue;
        this.chroma = chroma;
    }

    @ColorInt
    public int tone(int tone) {
        double safeChroma = AexonHctCompat.findMaxSafeChromaAtTone(hue, tone);
        double clampedChroma = Math.min(chroma, safeChroma);
        AexonHctCompat hct = new AexonHctCompat(hue, clampedChroma, tone);
        return hct.toInt();
    }

    @ColorInt
    public int vividColor(int preferredTone) {
        return vividColor(preferredTone, 50, 0.75);
    }

    @ColorInt
    public int vividColor(int preferredTone, int minTone, double minChromaRatio) {
        if (chroma <= 0.0) return tone(preferredTone);

        int t = preferredTone;
        while (t > minTone) {
            double safeChroma = AexonHctCompat.findMaxSafeChromaAtTone(hue, t);
            if (safeChroma / chroma >= minChromaRatio) break;
            t -= 2;
        }
        return tone(Math.max(t, minTone));
    }

    public static AexonSchemeTonal fromInt(@ColorInt int argb) {
        AexonHctCompat hct = AexonHctCompat.fromInt(argb);
        return new AexonSchemeTonal(hct.getHue(), hct.getChroma());
    }
}