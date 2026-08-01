package com.aexon.theme.foundation;

import androidx.annotation.ColorInt;

import com.aexon.theme.hct.AexonHctCompat;
import com.aexon.theme.hct.utils.AexonMathUtils;

public class AexonBlend {
    public static int harmonize(@ColorInt int designColor, @ColorInt int sourceColor) {
        AexonHctCompat from = AexonHctCompat.fromInt(designColor);
        AexonHctCompat to = AexonHctCompat.fromInt(sourceColor);

        double diff = AexonMathUtils.differenceDegrees(from.getHue(), to.getHue());
        double rotation = Math.min(diff * 0.5, 15.0);
        double outputHue = AexonMathUtils.sanitizeDegreesDouble(from.getHue() + rotation * AexonMathUtils.rotationDirection(from.getHue(), to.getHue()));

        return new AexonHctCompat(outputHue, from.getChroma(), from.getTone()).toInt();
    }

    public static int blendHct(@ColorInt int fromColor, @ColorInt int toColor, double ratio) {
        AexonHctCompat from = AexonHctCompat.fromInt(fromColor);
        AexonHctCompat to = AexonHctCompat.fromInt(toColor);

        double diff = AexonMathUtils.differenceDegrees(from.getHue(), to.getHue());
        double rotation = diff * ratio;
        double outputHue = AexonMathUtils.sanitizeDegreesDouble(from.getHue() + rotation * AexonMathUtils.rotationDirection(from.getHue(), to.getHue()));

        double outputChroma = from.getChroma() + (to.getChroma() - from.getChroma()) * ratio;
        double outputTone = from.getTone() + (to.getTone() - from.getTone()) * ratio;

        return new AexonHctCompat(outputHue, outputChroma, outputTone).toInt();
    }
}