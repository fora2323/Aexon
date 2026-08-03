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
package com.aexon.starter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import rikka.shizuku.Shizuku;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AexonShizukuHelper {
	
	private final Context context;
	private boolean binderReceived = false;
	
	private final Shizuku.OnBinderReceivedListener binderReceivedListener = () -> binderReceived = true;
	private final Shizuku.OnBinderDeadListener binderDeadListener = () -> binderReceived = false;
	
	public AexonShizukuHelper(@NonNull Context context) {
		this.context = context;
		Shizuku.addBinderReceivedListenerSticky(binderReceivedListener);
		Shizuku.addBinderDeadListener(binderDeadListener);
	}
	
	public boolean isShizukuInstalled() {
		try {
			context.getPackageManager().getPackageInfo("moe.shizuku.privileged.api", 0);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean isShizukuRunning() {
		try {
			return binderReceived || Shizuku.pingBinder();
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean hasPermission() {
		if (!isShizukuRunning()) return false;
		try {
			return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void requestPermission(int requestCode) {
		if (!isShizukuRunning()) return;
		try {
			Shizuku.requestPermission(requestCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@NonNull
	public RishResult exec(@NonNull String command) {
		if (!isShizukuRunning() || !hasPermission()) {
			return new RishResult(false, "", "Shizuku not running or permission not granted");
		}
		try {
			Process process = Shizuku.newProcess(new String[]{"sh", "-c", command}, null, null);
			String output = readStream(process.getInputStream());
			String error = readStream(process.getErrorStream());
			int exitCode = process.waitFor();
			return new RishResult(exitCode == 0, output.trim(), error.trim());
		} catch (Exception e) {
			String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";
			return new RishResult(false, "", errorMessage);
		}
	}
	
	public void destroy() {
		Shizuku.removeBinderReceivedListener(binderReceivedListener);
		Shizuku.removeBinderDeadListener(binderDeadListener);
	}
	
	private String readStream(InputStream inputStream) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			StringBuilder builder = new StringBuilder();
			String line;
			boolean first = true;
			while ((line = reader.readLine()) != null) {
				if (!first) {
					builder.append("\n");
				}
				builder.append(line);
				first = false;
			}
			return builder.toString();
		} catch (Exception e) {
			return "";
		}
	}
	
	public interface RootCheckCallback {
		void onResult(boolean rooted);
	}
	
	public static void isRooted(@NonNull RootCheckCallback callback) {
		new Thread(() -> {
			Process process = null;
			boolean result = false;
			try {
				process = Runtime.getRuntime().exec(new String[]{"su", "-c", "echo ok"});
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String out = reader.readLine();
				process.waitFor();
				reader.close();
				result = "ok".equals(out);
			} catch (Exception e) {
				result = false;
			} finally {
				if (process != null) {
					process.destroy();
				}
			}
			
			final boolean finalResult = result;
			new Handler(Looper.getMainLooper()).post(() -> callback.onResult(finalResult));
		}).start();
	}
	
	public static class RishResult {
		private final boolean success;
		@NonNull private final String output;
		@NonNull private final String error;
		
		public RishResult(boolean success, @NonNull String output, @NonNull String error) {
			this.success = success;
			this.output = output;
			this.error = error;
		}
		
		public boolean isSuccess() {
			return success;
		}
		
		@NonNull
		public String getOutput() {
			return output;
		}
		
		@NonNull
		public String getError() {
			return error;
		}
	}
}