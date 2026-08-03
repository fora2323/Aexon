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

package com.aexon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.annotation.ArrayRes;
import androidx.annotation.BoolRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SketchwareUtil {
	
	public static void showMessage(@NonNull Context context, @NonNull String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	
	public static void showMessage(@NonNull Context context, @StringRes int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}
	
	@NonNull
	public static String getString(@NonNull Context context, @StringRes int resId) {
		return context.getString(resId);
	}
	
	@NonNull
	public static String getString(@NonNull Context context, @StringRes int resId, Object... formatArgs) {
		return context.getString(resId, formatArgs);
	}
	
	@ColorInt
	public static int getColor(@NonNull Context context, @ColorRes int resId) {
		return context.getColor(resId);
	}
	
	@Nullable
	public static Drawable getDrawable(@NonNull Context context, @DrawableRes int resId) {
		return context.getDrawable(resId);
	}
	
	public static float getDimension(@NonNull Context context, @DimenRes int resId) {
		return context.getResources().getDimension(resId);
	}
	
	public static int getDimensionPixelSize(@NonNull Context context, @DimenRes int resId) {
		return context.getResources().getDimensionPixelSize(resId);
	}
	
	@NonNull
	public static String[] getStringArray(@NonNull Context context, @ArrayRes int resId) {
		return context.getResources().getStringArray(resId);
	}
	
	@NonNull
	public static int[] getIntArray(@NonNull Context context, @ArrayRes int resId) {
		return context.getResources().getIntArray(resId);
	}
	
	public static boolean getBoolean(@NonNull Context context, @BoolRes int resId) {
		return context.getResources().getBoolean(resId);
	}
	
	public static int getInteger(@NonNull Context context, @IntegerRes int resId) {
		return context.getResources().getInteger(resId);
	}
	
	public static float getDip(@NonNull Context context, int input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, input, context.getResources().getDisplayMetrics());
	}
	
	public static int dpToPx(@NonNull Context context, float dp) {
		return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
	}
}