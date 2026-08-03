/*
* Copyright (c) 2026 Fora
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
* 
* Contact: Fora <fora060823@gmail.com>
* Created: 27-01-2026
*/
package com.aexon.core.core;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public final class AexonMath {

    private AexonMath() {
        throw new UnsupportedOperationException("No instances");
    }

    public static float dpToPx(@NonNull Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public static int dpToPxInt(@NonNull Context context, float dp) {
        return (int) dpToPx(context, dp);
    }

    public static int dpToPxInt(float dp) {
        return (int) dpToPx(dp);
    }

    public static float pxToDp(@NonNull Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxToDp(float px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    public static float spToPx(@NonNull Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static float spToPx(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());
    }

    public static int spToPxInt(@NonNull Context context, float sp) {
        return (int) spToPx(context, sp);
    }

    public static int spToPxInt(float sp) {
        return (int) spToPx(sp);
    }

    public static float pxToSp(@NonNull Context context, float px) {
        return px / context.getResources().getDisplayMetrics().scaledDensity;
    }

    public static float pxToSp(float px) {
        return px / Resources.getSystem().getDisplayMetrics().scaledDensity;
    }

    public static float clamp(float value, float minValue, float maxValue) {
        return Math.max(minValue, Math.min(maxValue, value));
    }

    public static int clamp(int value, int minValue, int maxValue) {
        return Math.max(minValue, Math.min(maxValue, value));
    }

    public static float clamp01(float value) {
        return clamp(value, 0f, 1f);
    }

    public static float lerp(float start, float end, float fraction) {
        return start + fraction * (end - start);
    }

    public static int lerp(int start, int end, float fraction) {
        return Math.round(start + fraction * (end - start));
    }

    public static float inverseLerp(float start, float end, float value) {
        if (start == end) return 0f;
        return clamp01((value - start) / (end - start));
    }

    public static float map(float value, float inMin, float inMax, float outMin, float outMax) {
        float t = inverseLerp(inMin, inMax, value);
        return lerp(outMin, outMax, t);
    }

    public static float roundTo(float value, int decimals) {
        float factor = (float) Math.pow(10.0, decimals);
        return Math.round(value * factor) / factor;
    }

    public static float distance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static boolean isApprox(float a, float b) {
        return isApprox(a, b, 0.0001f);
    }

    public static boolean isApprox(float a, float b, float epsilon) {
        return Math.abs(a - b) < epsilon;
    }

    public static int wrap(int value, int min, int max) {
        int range = max - min;
        if (range <= 0) return min;
        int result = (value - min) % range;
        if (result < 0) result += range;
        return result + min;
    }
}