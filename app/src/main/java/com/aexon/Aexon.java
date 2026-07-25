package com.aexon;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.aexon.annotation.NonNull;
import com.aexon.annotation.Nullable;

public class Aexon {
	
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 7788;
	private static final byte AX_KEY = 0x5A;
	private static final byte[] AUTH_TOKEN_ENC = {0x1B, 0x02, 0x63, 0x31, 0x68, 0x37, 0x00, 0x2B, 0x16, 0x62, 0x34, 0x0A, 0x2D, 0x08, 0x2E, 0x03};
	private static final byte[] PID_FILE_ENC = {0x75, 0x3E, 0x3B, 0x2E, 0x3B, 0x75, 0x36, 0x35, 0x39, 0x3B, 0x36, 0x75, 0x2E, 0x37, 0x2A, 0x75, 0x74, 0x3B, 0x22, 0x05, 0x2A, 0x28, 0x35, 0x39};
	private static final byte[] STARTER_PID_FILE_ENC = {0x75, 0x3E, 0x3B, 0x2E, 0x3B, 0x75, 0x36, 0x35, 0x39, 0x3B, 0x36, 0x75, 0x2E, 0x37, 0x2A, 0x75, 0x74, 0x3B, 0x22, 0x05, 0x29, 0x2E, 0x3B, 0x28, 0x2E, 0x3F, 0x28};
	private static final Handler mainHandler = new Handler(Looper.getMainLooper());
	
	private @NonNull String[] command;
	private @Nullable String[] env;
	private @Nullable String dir;
	
	private static final List<OnBinderReceivedListener> binderReceivedListeners = new CopyOnWriteArrayList<>();
	private static final List<OnBinderDeadListener> binderDeadListeners = new CopyOnWriteArrayList<>();
	private static boolean isMonitoring = false;
	private static boolean isReceiverMonitoring = false;
	
	public interface OnBinderReceivedListener {
		void onBinderReceived();
	}
	
	public interface OnBinderDeadListener {
		void onBinderDead();
	}
	
	public interface OnProcessOutputListener {
		void onStart(long pid);
		void onOutput(String line);
		void onExit(int exitCode);
	}
	
	private Aexon(@NonNull String[] command, @Nullable String[] env, @Nullable String dir) {
		this.command = command;
		this.env = env;
		this.dir = dir;
	}
	
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
	
	private static void allowNetwork() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
	}
	
	public static @NonNull Aexon newProcess(@NonNull String[] command, @Nullable String[] env, @Nullable String dir) {
		return new Aexon(command, env, dir);
	}
	
	public void exec() {
		new Thread(() -> {
			StringBuilder cmd = new StringBuilder();
			if (env != null) for (String e : env) cmd.append(e).append(" ");
			if (dir != null) cmd.append("cd ").append(dir).append(" && ");
			for (int i = 0; i < command.length; i++) {
				cmd.append(command[i]);
				if (i < command.length - 1) cmd.append(" ");
			}
			sendCommand(cmd.toString());
		}).start();
	}
	
	public @NonNull AexonProcess execResult() {
		StringBuilder cmd = new StringBuilder();
		if (env != null) for (String e : env) cmd.append(e).append(" ");
		if (dir != null) cmd.append("cd ").append(dir).append(" && ");
		for (int i = 0; i < command.length; i++) {
			cmd.append(command[i]);
			if (i < command.length - 1) cmd.append(" ");
		}
		return new AexonProcess(sendCommand(cmd.toString()));
	}
	
	private static @NonNull String sendCommand(@NonNull String command) {
		allowNetwork();
		try {
			Socket socket = new Socket(HOST, PORT);
			socket.setSoTimeout(5000);
			OutputStream out = socket.getOutputStream();
			out.write((axDecode(AUTH_TOKEN_ENC) + "\n").getBytes("UTF-8"));
			out.flush();
			out.write((command + "\n").getBytes("UTF-8"));
			out.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line).append("\n");
			}
			socket.close();
			return result.toString().trim();
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}
	
	public static boolean isBinder() {
		allowNetwork();
		try {
			Socket socket = new Socket(HOST, PORT);
			socket.setSoTimeout(500);
			OutputStream out = socket.getOutputStream();
			out.write((axDecode(AUTH_TOKEN_ENC) + "\n").getBytes("UTF-8"));
			out.flush();
			out.write(("echo ok\n").getBytes("UTF-8"));
			out.flush();
			socket.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void stopDaemon() {
		new Thread(() -> sendCommand("kill $(cat " + axDecode(PID_FILE_ENC) + ")")).start();
	}
	
	public static void stopDaemonPermanently() {
		new Thread(() -> sendCommand("kill $(cat " + axDecode(STARTER_PID_FILE_ENC) + "); kill $(cat " + axDecode(PID_FILE_ENC) + ")")).start();
	}
	
	public static void execStream(@NonNull String command, @NonNull OnProcessOutputListener callback) {
		new Thread(() -> {
			allowNetwork();
			try {
				Socket socket = new Socket(HOST, PORT);
				socket.setSoTimeout(0);
				OutputStream out = socket.getOutputStream();
				out.write((axDecode(AUTH_TOKEN_ENC) + "\n").getBytes("UTF-8"));
				out.flush();
				out.write(("@@EXEC:" + command + "\n").getBytes("UTF-8"));
				out.flush();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				
				String pidLine = reader.readLine();
				long pid = -1;
				if (pidLine != null && pidLine.startsWith("@@PID:")) {
					try {
						pid = Long.parseLong(pidLine.substring(6).trim());
					} catch (Exception ignored) {
						
					}
				}
				final long finalPid = pid;
				mainHandler.post(() -> callback.onStart(finalPid));
				
				String line;
				int exitCode = -1;
				while ((line = reader.readLine()) != null) {
					if (line.startsWith("@@EXIT:")) {
						try {
							exitCode = Integer.parseInt(line.substring(7).trim());
						} catch (Exception ignored) {
							
						}
						break;
					}
					final String outLine = line;
					mainHandler.post(() -> callback.onOutput(outLine));
				}
				socket.close();
				final int finalExit = exitCode;
				mainHandler.post(() -> callback.onExit(finalExit));
			} catch (Exception e) {
				mainHandler.post(() -> callback.onExit(-1));
			}
		}).start();
	}
	
	public static void killProcess(long pid) {
		if (pid <= 0) return;
		new Thread(() -> sendCommand("kill -9 " + pid)).start();
	}
	
	public static @NonNull String getPath(@NonNull Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			return context.getApplicationInfo().nativeLibraryDir + "/libaexon.so";
		} else {
			return "sh /sdcard/Android/data/" + context.getPackageName() + "/start.sh";
		}
	}
	
	public static void addBinderReceivedListener(@NonNull OnBinderReceivedListener listener) {
		binderReceivedListeners.add(listener);
		new Thread(() -> {
			if (isBinder()) {
				mainHandler.post(listener::onBinderReceived);
				return;
			}
			if (isReceiverMonitoring) return;
			isReceiverMonitoring = true;
			boolean wasRunning = false;
			while (true) {
				boolean isRunning = isBinder();
				if (!wasRunning && isRunning) {
					for (OnBinderReceivedListener l : binderReceivedListeners) {
						mainHandler.post(l::onBinderReceived);
					}
				}
				wasRunning = isRunning;
				try {
					Thread.sleep(500);
				} catch (Exception ignored) {
					
				}
			}
		}).start();
	}
	
	public static void removeBinderReceivedListener(@NonNull OnBinderReceivedListener listener) {
		binderReceivedListeners.remove(listener);
	}
	
	public static void addBinderDeadListener(@NonNull OnBinderDeadListener listener) {
		binderDeadListeners.add(listener);
		startMonitoring();
	}
	
	public static void removeBinderDeadListener(@NonNull OnBinderDeadListener listener) {
		binderDeadListeners.remove(listener);
	}
	
	private static void startMonitoring() {
		if (isMonitoring) return;
		isMonitoring = true;
		new Thread(() -> {
			boolean wasRunning = isBinder();
			while (true) {
				try { 
					Thread.sleep(500);
				} catch (Exception ignored) {
					
				}
				boolean isRunning = isBinder();
				if (wasRunning && !isRunning) {
					for (OnBinderDeadListener l : binderDeadListeners) {
						mainHandler.post(l::onBinderDead);
					}
				}
				wasRunning = isRunning;
			}
		}).start();
	}
}