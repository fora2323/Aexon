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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AexonBuild {
	
	@NonNull
	public static String getPackageName(@NonNull Context ctx) {
		return ctx.getPackageName();
	}
	
	@NonNull
	public static String getAppName(@NonNull Context ctx) {
		return ctx.getApplicationInfo().loadLabel(ctx.getPackageManager()).toString();
	}
	
	@Nullable
	public static String getVersionName(@NonNull Context ctx) {
		try {
			return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			return "unknown";
		}
	}
	
	@SuppressWarnings("deprecation")
	public static long getVersionCode(@NonNull Context ctx) {
		try {
			PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
				return pi.getLongVersionCode();
			} else {
				return pi.versionCode;
			}
		} catch (PackageManager.NameNotFoundException e) {
			return -1;
		}
	}
}