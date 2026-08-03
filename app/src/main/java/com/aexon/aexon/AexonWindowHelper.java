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
package com.aexon.aexon;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AexonWindowHelper {
	
	private AexonWindowHelper() {
		throw new UnsupportedOperationException("No instances");
	}
	
	public static void setWindowStyle(@NonNull Window window, int color) {
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.setStatusBarColor(color);
		window.setNavigationBarColor(color);
		boolean darkIcon = needsDarkIcon(color);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			applyInsetsController(window, darkIcon);
		} else {
			applyLegacyFlags(window, darkIcon);
		}
	}
	
	public static void setWindowStyle(@NonNull Window window, int statusBarColor, int navigationBarColor) {
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.setStatusBarColor(statusBarColor);
		window.setNavigationBarColor(navigationBarColor);
		boolean darkIcon = needsDarkIcon(statusBarColor);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			applyInsetsController(window, darkIcon);
		} else {
			applyLegacyFlags(window, darkIcon);
		}
	}
	
	@RequiresApi(api = Build.VERSION_CODES.R)
	private static void applyInsetsController(@NonNull Window window, boolean darkIcon) {
		WindowInsetsController controller = window.getInsetsController();
		if (controller != null) {
			int appearance = 0;
			if (darkIcon) {
				appearance |= WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS;
				appearance |= WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
			}
			controller.setSystemBarsAppearance(appearance, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
		}
	}
	
	private static void applyLegacyFlags(@NonNull Window window, boolean darkIcon) {
		View decorView = window.getDecorView();
		int flags = decorView.getSystemUiVisibility();
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (darkIcon) {
				flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
			} else {
				flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
			}
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			if (darkIcon) {
				flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
			} else {
				flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
			}
		}
		decorView.setSystemUiVisibility(flags);
	}
	
	private static boolean needsDarkIcon(int color) {
		double r = Color.red(color) / 255.0;
		double g = Color.green(color) / 255.0;
		double b = Color.blue(color) / 255.0;
		double luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b;
		return luminance > 0.5;
	}
}