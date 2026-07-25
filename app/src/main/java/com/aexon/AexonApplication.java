package com.aexon;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;

import com.aexon.theme.AexonTheme;

public class AexonApplication extends Application {
	
	private static Context mApplicationContext;
	public static Context getContext() {
		return mApplicationContext;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		AexonTheme.init(this);
		mApplicationContext = getApplicationContext();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable throwable) {
				handleUncaughtException(thread, throwable);
			}
		});
		
		SketchLogger.startLogging();
	}
	
	private void handleUncaughtException(Thread thread, Throwable throwable) {
		try {
			Intent intent = new Intent(getApplicationContext(), DebugActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.putExtra("error", Log.getStackTraceString(throwable));
			startActivity(intent);
		} catch (Exception ignored) {
		}
		
		try {
			SketchLogger.broadcastLog(Log.getStackTraceString(throwable));
		} catch (Exception ignored) {
			
		}
		Process.killProcess(Process.myPid());
		System.exit(1);
	}
}