package com.aexon.starter.server

import java.io.BufferedReader
import java.io.FileWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

import com.aexon.annotation.NonNull

class AexonServer {
	
	companion object {
		private const val PORT = 7788
		private val AX_KEY: Byte = 0x5A
		
		private val AUTH_TOKEN_ENC = byteArrayOf(0x1B, 0x02, 0x63, 0x31, 0x68, 0x37, 0x00, 0x2B, 0x16, 0x62, 0x34, 0x0A, 0x2D, 0x08, 0x2E, 0x03)
		private val PID_FILE_ENC = byteArrayOf(0x75, 0x3E, 0x3B, 0x2E, 0x3B, 0x75, 0x36, 0x35, 0x39, 0x3B, 0x36, 0x75, 0x2E, 0x37, 0x2A, 0x75, 0x74, 0x3B, 0x22, 0x05, 0x2A, 0x28, 0x35, 0x39)
		
		private fun axDecode(enc: ByteArray): String {
			val out = ByteArray(enc.size)
			for (i in enc.indices) out[i] = (enc[i].toInt() xor AX_KEY.toInt()).toByte()
			return String(out, Charsets.UTF_8)
		}
		
		private fun getProcessPid(process: Process): Long {
			return try {
				val field = process.javaClass.getDeclaredField("pid")
				field.isAccessible = true
				field.getLong(process)
			} catch (e: Exception) {
				-1L
			}
		}
		
		@JvmStatic
		fun main(@NonNull args: Array<String>) {
			try {
				val server = ServerSocket(PORT, 10, InetAddress.getByName("127.0.0.1"))
				savePid()
				while (true) {
					val client = server.accept()
					Thread { 
						handleClient(client) 
					}.start()
				}
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
		
		private fun savePid() {
			try {
				FileWriter(axDecode(PID_FILE_ENC)).use { fw ->
					fw.write(android.os.Process.myPid().toString())
					fw.flush()
				}
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
		
		private fun handleClient(@NonNull socket: Socket) {
			try {
				socket.use { sock ->
					val reader = BufferedReader(InputStreamReader(sock.getInputStream(), "UTF-8"))
					val out = sock.getOutputStream()
					sock.soTimeout = 30000
					
					val token = reader.readLine()
					if (token == null || token != axDecode(AUTH_TOKEN_ENC)) {
						return@use
					}
					
					val command = reader.readLine()
					if (command == null || command.isEmpty()) {
						return@use
					}
					
					if (command == "getselinux") {
						try {
							val p2 = Runtime.getRuntime().exec(arrayOf("/system/bin/sh", "-c", "cat /proc/self/attr/current"))
							val is2 = p2.inputStream
							val buf2 = ByteArray(256)
							val len2 = is2.read(buf2)
							if (len2 > 0) out.write(buf2, 0, len2)
							out.flush()
							p2.waitFor()
						} catch (_: Exception) {
                            
                        }
						return@use
					}
					
					if (command.startsWith("@@EXEC:")) {
						sock.soTimeout = 0
						val actualCmd = command.substring(7)
						try {
							val p = Runtime.getRuntime().exec(arrayOf("/system/bin/sh", "-c", actualCmd))
							val pid = getProcessPid(p)
							synchronized(out) {
								out.write("@@PID:$pid\n".toByteArray())
								out.flush()
							}
							
							val isInput = p.inputStream
							val isError = p.errorStream
							
							val errThread = Thread {
								try {
									val errBuf = ByteArray(8192)
									var errLen: Int
									while (isError.read(errBuf).also { errLen = it } != -1) {
										synchronized(out) {
											out.write(errBuf, 0, errLen)
											out.flush()
										}
									}
								} catch (_: Exception) {
									
								}
							}
							errThread.start()
							
							val outBuf = ByteArray(8192)
							var outLen: Int
							while (isInput.read(outBuf).also { outLen = it } != -1) {
								synchronized(out) {
									out.write(outBuf, 0, outLen)
									out.flush()
								}
							}
							errThread.join()
							
							val exitCode = p.waitFor()
							synchronized(out) {
								out.write("\n@@EXIT:$exitCode\n".toByteArray())
								out.flush()
							}
						} catch (e: Exception) {
							synchronized(out) {
								out.write("@@PID:-1\n".toByteArray())
								out.write("@@EXIT:-1\n".toByteArray())
								out.flush()
							}
						}
						return@use
					}
					
					val p = Runtime.getRuntime().exec(arrayOf("/system/bin/sh", "-c", command))
					val isInput = p.inputStream
					val isError = p.errorStream
					
					val buf = ByteArray(8192)
					var len: Int
					
					while (isInput.read(buf).also { len = it } != -1) {
						out.write(buf, 0, len)
					}
					
					while (isError.read(buf).also { len = it } != -1) {
						out.write(buf, 0, len)
					}
					
					out.flush()
					p.waitFor()
				}
			} catch (e: Exception) {
				try {
					socket.close()
				} catch (_: Exception) {
                    
                }
			}
		}
	}
}