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
package com.aexon.starter.server;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AexonServer {
	
	private static final int PORT = 7788;
	private static final byte AX_KEY = 0x5A;
	
	private static final byte[] AUTH_TOKEN_ENC = new byte[]{
		0x1B, 0x02, 0x63, 0x31, 0x68, 0x37, 0x00, 0x2B, 0x16, 0x62, 0x34, 0x0A, 0x2D, 0x08, 0x2E, 0x03
	};
	
	private static final byte[] PID_FILE_ENC = new byte[]{
		0x75, 0x3E, 0x3B, 0x2E, 0x3B, 0x75, 0x36, 0x35, 0x39, 0x3B, 0x36, 0x75, 0x2E, 0x37, 0x2A, 0x75, 0x74, 0x3B, 0x22, 0x05, 0x2A, 0x28, 0x35, 0x39
	};
	
	private static String axDecode(byte[] enc) {
		byte[] out = new byte[enc.length];
		for (int i = 0; i < enc.length; i++) {
			out[i] = (byte) (enc[i] ^ AX_KEY);
		}
		return new String(out, StandardCharsets.UTF_8);
	}
	
	private static long getProcessPid(Process process) {
		try {
			Field field = process.getClass().getDeclaredField("pid");
			field.setAccessible(true);
			return field.getLong(process);
		} catch (Exception e) {
			return -1L;
		}
	}
	
	public static void main(@NonNull String[] args) {
		try {
			ServerSocket server = new ServerSocket(PORT, 10, InetAddress.getByName("127.0.0.1"));
			savePid();
			while (true) {
				Socket client = server.accept();
				new Thread(() -> handleClient(client)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void savePid() {
		try (FileWriter fw = new FileWriter(axDecode(PID_FILE_ENC))) {
			fw.write(String.valueOf(android.os.Process.myPid()));
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void handleClient(@NonNull Socket socket) {
		try (Socket sock = socket) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_8));
			OutputStream out = sock.getOutputStream();
			sock.setSoTimeout(30000);
			
			String token = reader.readLine();
			if (token == null || !token.equals(axDecode(AUTH_TOKEN_ENC))) {
				return;
			}
			
			String command = reader.readLine();
			if (command == null || command.isEmpty()) {
				return;
			}
			
			if ("getselinux".equals(command)) {
				try {
					Process p2 = Runtime.getRuntime().exec(new String[]{"/system/bin/sh", "-c", "cat /proc/self/attr/current"});
					InputStream is2 = p2.getInputStream();
					byte[] buf2 = new byte[256];
					int len2 = is2.read(buf2);
					if (len2 > 0) {
						out.write(buf2, 0, len2);
					}
					out.flush();
					p2.waitFor();
				} catch (Exception ignored) {
				}
				return;
			}
			
			if (command.startsWith("@@EXEC:")) {
				sock.setSoTimeout(0);
				String actualCmd = command.substring(7);
				try {
					Process p = Runtime.getRuntime().exec(new String[]{"/system/bin/sh", "-c", actualCmd});
					long pid = getProcessPid(p);
					synchronized (out) {
						out.write(("@@PID:" + pid + "\n").getBytes(StandardCharsets.UTF_8));
						out.flush();
					}
					
					InputStream isInput = p.getInputStream();
					InputStream isError = p.getErrorStream();
					
					Thread errThread = new Thread(() -> {
						try {
							byte[] errBuf = new byte[8192];
							int errLen;
							while ((errLen = isError.read(errBuf)) != -1) {
								synchronized (out) {
									out.write(errBuf, 0, errLen);
									out.flush();
								}
							}
						} catch (Exception ignored) {
						}
					});
					errThread.start();
					
					byte[] outBuf = new byte[8192];
					int outLen;
					while ((outLen = isInput.read(outBuf)) != -1) {
						synchronized (out) {
							out.write(outBuf, 0, outLen);
							out.flush();
						}
					}
					errThread.join();
					
					int exitCode = p.waitFor();
					synchronized (out) {
						out.write(("\n@@EXIT:" + exitCode + "\n").getBytes(StandardCharsets.UTF_8));
						out.flush();
					}
				} catch (Exception e) {
					synchronized (out) {
						out.write("@@PID:-1\n".getBytes(StandardCharsets.UTF_8));
						out.write("@@EXIT:-1\n".getBytes(StandardCharsets.UTF_8));
						out.flush();
					}
				}
				return;
			}
			
			Process p = Runtime.getRuntime().exec(new String[]{"/system/bin/sh", "-c", command});
			InputStream isInput = p.getInputStream();
			InputStream isError = p.getErrorStream();
			
			byte[] buf = new byte[8192];
			int len;
			
			while ((len = isInput.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			
			while ((len = isError.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			
			out.flush();
			p.waitFor();
			
		} catch (Exception e) {
			try {
				socket.close();
			} catch (Exception ignored) {
			}
		}
	}
}