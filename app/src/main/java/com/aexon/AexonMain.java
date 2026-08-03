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

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RequiresApi(api = Build.VERSION_CODES.O)
public final class AexonMain {
	
	private static final byte AX_KEY = 0x5A;
	private static final byte[] PID_FILE_ENC = {0x75, 0x3E, 0x3B, 0x2E, 0x3B, 0x75, 0x36, 0x35, 0x39, 0x3B, 0x36, 0x75, 0x2E, 0x37, 0x2A, 0x75, 0x74, 0x3B, 0x22, 0x05, 0x2A, 0x28, 0x35, 0x39};
	
	private AexonMain() {
		throw new UnsupportedOperationException("No instances");
	}
	
	static {
		System.loadLibrary("native");
	}
	
	public static native String getVersion();
	public static native String getSdk();
	public static native long getStartTime();
	
	private static @NonNull String axDecode(@NonNull byte[] enc) {
		byte[] out = new byte[enc.length];
		for (int i = 0; i < enc.length; i++) {
			out[i] = (byte) (enc[i] ^ AX_KEY);
		}
		return new String(out, StandardCharsets.UTF_8);
	}
	
	@NonNull
	public static String getPid() {
		try (BufferedReader br = new BufferedReader(new FileReader(new File(axDecode(PID_FILE_ENC))))) {
			String pid = br.readLine();
			return pid != null ? pid.trim() : "-1";
		} catch (Throwable e) {
			return "-1";
		}
	}
	
	@NonNull
	public static String getSeLinux() {
		if (!Aexon.isBinder()) return "Unknown";
		try {
			AexonProcess p = Aexon.newProcess(new String[]{"getselinux"}, null, null).execResult();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
				String result = br.readLine();
				return result != null && !result.trim().isEmpty() ? result.trim() : "Unknown";
			}
		} catch (Throwable e) {
			return "Unknown";
		}
	}
}