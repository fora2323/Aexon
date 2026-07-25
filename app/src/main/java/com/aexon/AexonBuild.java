package com.aexon;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.aexon.annotation.NonNull;
import com.aexon.annotation.Nullable;
import com.aexon.core.AexonApi;

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
	
	public static long getVersionCode(@NonNull Context ctx) {
		try {
			PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			return AexonApi.minSdk(Build.VERSION_CODES.P) ? pi.getLongVersionCode() : pi.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			return -1;
		}
	}
}