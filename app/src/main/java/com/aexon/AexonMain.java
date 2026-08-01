package com.aexon;

import com.aexon.annotation.NonNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.InputStreamReader;

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
		try {
			return new String(out, "UTF-8");
		} catch (Exception e) {
			return "";
		}
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
			try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
				String result = br.readLine();
				return result != null && !result.trim().isEmpty() ? result.trim() : "Unknown";
			}
		} catch (Throwable e) {
			return "Unknown";
		}
	}
}