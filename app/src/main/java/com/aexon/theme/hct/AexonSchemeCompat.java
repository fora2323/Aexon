package com.aexon.theme.hct;

import androidx.annotation.ColorInt;

public class AexonSchemeCompat {
    private static final double GRAYSCALE_CHROMA_THRESHOLD = 5.0;
    private static final double NEAR_WHITE_TONE_FLOOR = 90.0;
    private static final double PASTEL_CHROMA_CEILING = 22.0;
    private static final double PASTEL_TONE_FLOOR = 78.0;
    private static final double DEFAULT_PRIMARY_CHROMA = 48.0;

    private final AexonSchemeTonal primary;
    private final AexonSchemeTonal secondary;
    private final AexonSchemeTonal tertiary;
    private final AexonSchemeTonal error;
    private final AexonSchemeTonal neutral;
    private final AexonSchemeTonal neutralVariant;
    private final boolean isGrayscaleSeed;
    private final double seedTone;
    private final boolean isDark;

    public AexonSchemeCompat(@ColorInt int seedColor, boolean isDark) {
        this.isDark = isDark;
        AexonHctCompat hct = AexonHctCompat.fromInt(seedColor);
        double rawHue = hct.getHue();
        double rawChroma = hct.getChroma();
        double rawTone = hct.getTone();

        this.isGrayscaleSeed = rawChroma < GRAYSCALE_CHROMA_THRESHOLD;
        this.seedTone = rawTone;

        double[] chosen;
        if (isGrayscaleSeed) {
            chosen = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        } else if (rawTone > NEAR_WHITE_TONE_FLOOR && rawChroma < PASTEL_CHROMA_CEILING) {
            double cappedChroma = Math.min(rawChroma, 30.0);
            chosen = new double[]{
                    rawHue,
                    Math.max(cappedChroma * 0.9, 6.0),
                    Math.max(cappedChroma * 0.4, 3.0),
                    60.0,
                    Math.max(cappedChroma * 0.5, 4.0),
                    Math.min(cappedChroma * 0.1, 3.0),
                    Math.min(cappedChroma * 0.15, 5.0)
            };
        } else if (rawChroma < PASTEL_CHROMA_CEILING && rawTone > PASTEL_TONE_FLOOR) {
            double chromaScale = rawChroma / PASTEL_CHROMA_CEILING;
            double toneScale = (rawTone - PASTEL_TONE_FLOOR) / (NEAR_WHITE_TONE_FLOOR - PASTEL_TONE_FLOOR);
            double blendFactor = chromaScale * (1.0 - toneScale * 0.6);
            double primaryChroma = Math.max(rawChroma + blendFactor * (28.0 - rawChroma), 8.0);
            chosen = new double[]{
                    rawHue,
                    primaryChroma,
                    Math.max(rawChroma * 0.55, 4.0),
                    60.0,
                    Math.max(rawChroma * 0.7, 5.0),
                    Math.min(rawChroma * 0.25, 4.0),
                    Math.min(rawChroma * 0.4, 7.0)
            };
        } else {
            chosen = new double[]{
                    rawHue,
                    Math.max(rawChroma, DEFAULT_PRIMARY_CHROMA),
                    Math.max(rawChroma * 0.5, 26.0),
                    60.0,
                    Math.max(rawChroma * 0.7, 32.0),
                    4.0,
                    8.0
            };
        }

        double primaryHue = chosen[0];
        double primaryChroma = chosen[1];
        double secondaryChroma = chosen[2];
        double tertiaryHueOffset = chosen[3];
        double tertiaryChroma = chosen[4];
        double neutralChroma = chosen[5];
        double neutralVariantChroma = chosen[6];

        double tertiaryHue = sanitizeHue(primaryHue + tertiaryHueOffset);

        this.primary = new AexonSchemeTonal(primaryHue, primaryChroma);
        this.secondary = new AexonSchemeTonal(primaryHue, secondaryChroma);
        this.tertiary = new AexonSchemeTonal(tertiaryHue, tertiaryChroma);
        this.error = new AexonSchemeTonal(25.0, 84.0);
        this.neutral = new AexonSchemeTonal(primaryHue, neutralChroma);
        this.neutralVariant = new AexonSchemeTonal(primaryHue, neutralVariantChroma);
    }

    public boolean isGrayscaleSeed() {
        return isGrayscaleSeed;
    }

    @ColorInt
    public int colorPrimary() {
        return primary.tone((int) seedTone);
    }

    @ColorInt
    public int colorPrimaryContainer() {
        if (!isDark && seedTone >= 60.0) {
            return primary.tone(Math.max(40, (int) seedTone - 20));
        } else {
            return primary.tone(isDark ? 30 : 90);
        }
    }

    @ColorInt
    public int colorOnPrimary() {
        if (!isDark && seedTone >= 60.0) {
            return primary.tone(10);
        } else {
            return primary.tone(isDark ? 20 : 100);
        }
    }

    @ColorInt
    public int colorOnPrimaryContainer() {
        if (!isDark && seedTone >= 60.0) {
            return primary.tone(10);
        } else {
            return primary.tone(isDark ? 90 : 10);
        }
    }

    @ColorInt
    public int colorSecondary() {
        return secondary.tone(Math.max(40, (int) seedTone));
    }

    @ColorInt
    public int colorOnSecondary() {
        return secondary.tone(isDark ? 20 : 100);
    }

    @ColorInt
    public int colorSecondaryContainer() {
        return secondary.tone(isDark ? 30 : 90);
    }

    @ColorInt
    public int colorOnSecondaryContainer() {
        return secondary.tone(isDark ? 90 : 10);
    }

    @ColorInt
    public int colorTertiary() {
        return tertiary.tone(Math.max(40, (int) seedTone));
    }

    @ColorInt
    public int colorOnTertiary() {
        return tertiary.tone(isDark ? 20 : 100);
    }

    @ColorInt
    public int colorTertiaryContainer() {
        return tertiary.tone(isDark ? 30 : 90);
    }

    @ColorInt
    public int colorOnTertiaryContainer() {
        return tertiary.tone(isDark ? 90 : 10);
    }

    @ColorInt
    public int colorError() {
        return isDark ? error.vividColor(80) : error.tone(40);
    }

    @ColorInt
    public int colorOnError() {
        return error.tone(isDark ? 20 : 100);
    }

    @ColorInt
    public int colorErrorContainer() {
        return error.tone(isDark ? 30 : 90);
    }

    @ColorInt
    public int colorOnErrorContainer() {
        return error.tone(isDark ? 90 : 10);
    }

    @ColorInt
    public int colorSurface() {
        return neutral.tone(isDark ? 6 : 98);
    }

    @ColorInt
    public int colorOnSurface() {
        return neutral.tone(isDark ? 90 : 10);
    }

    @ColorInt
    public int colorSurfaceVariant() {
        return neutralVariant.tone(isDark ? 30 : 90);
    }

    @ColorInt
    public int colorOnSurfaceVariant() {
        return neutralVariant.tone(isDark ? 80 : 30);
    }

    @ColorInt
    public int colorSurfaceDim() {
        return neutral.tone(isDark ? 6 : 87);
    }

    @ColorInt
    public int colorSurfaceBright() {
        return neutral.tone(isDark ? 24 : 98);
    }

    @ColorInt
    public int colorSurfaceContainerLowest() {
        return neutral.tone(isDark ? 4 : 100);
    }

    @ColorInt
    public int colorSurfaceContainerLow() {
        return neutral.tone(isDark ? 10 : 96);
    }

    @ColorInt
    public int colorSurfaceContainer() {
        return neutral.tone(isDark ? 12 : 94);
    }

    @ColorInt
    public int colorSurfaceContainerHigh() {
        return neutral.tone(isDark ? 17 : 92);
    }

    @ColorInt
    public int colorSurfaceContainerHighest() {
        return neutral.tone(isDark ? 22 : 90);
    }

    @ColorInt
    public int colorSurfaceInverse() {
        return neutral.tone(isDark ? 90 : 20);
    }

    @ColorInt
    public int colorOnSurfaceInverse() {
        return neutral.tone(isDark ? 20 : 95);
    }

    @ColorInt
    public int colorPrimaryInverse() {
        return primary.tone(isDark ? 40 : 80);
    }

    @ColorInt
    public int colorOutline() {
        return neutralVariant.tone(isDark ? 60 : 50);
    }

    @ColorInt
    public int colorOutlineVariant() {
        return neutralVariant.tone(isDark ? 30 : 80);
    }

    @ColorInt
    public int colorScrim() {
        return neutral.tone(0);
    }

    @ColorInt
    public int colorScrimFix() {
        return neutral.tone(isDark ? 10 : 80);
    }

    @ColorInt
    public int colorControlHighlight() {
        return primary.tone(isDark ? 30 : 90);
    }

    @ColorInt
    public int colorControlNormal() {
        return neutralVariant.tone(isDark ? 60 : 50);
    }

    private static double sanitizeHue(double hue) {
        double result = hue % 360.0;
        if (result < 0.0) result += 360.0;
        return result;
    }
}