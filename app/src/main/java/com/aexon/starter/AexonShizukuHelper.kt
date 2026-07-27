package com.aexon.starter

import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import java.io.BufferedReader
import java.io.InputStreamReader

import com.aexon.annotation.NonNull
import com.aexon.annotation.Nullable
import rikka.shizuku.Shizuku

class AexonShizukuHelper(@NonNull private val context: Context) {
	
	private var binderReceived = false
	
	private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
		binderReceived = true
	}
	
	private val binderDeadListener = Shizuku.OnBinderDeadListener {
		binderReceived = false
	}
	
	init {
		Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
		Shizuku.addBinderDeadListener(binderDeadListener)
	}
	
	fun isShizukuInstalled(): Boolean {
		return try {
			context.packageManager.getPackageInfo("moe.shizuku.privileged.api", 0)
			true
		} catch (e: Exception) {
			false
		}
	}
	
	fun isShizukuRunning(): Boolean {
		return try {
			binderReceived || Shizuku.pingBinder()
		} catch (e: Exception) {
			false
		}
	}
	
	fun hasPermission(): Boolean {
		if (!isShizukuRunning()) return false
		return try {
			Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
		} catch (e: Exception) {
			false
		}
	}
	
	fun requestPermission(requestCode: Int) {
		if (!isShizukuRunning()) return
		try {
			Shizuku.requestPermission(requestCode)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
	
	fun exec(@NonNull command: String): RishResult {
		if (!isShizukuRunning() || !hasPermission()) {
			return RishResult(false, "", "Shizuku not running or permission not granted")
		}
		return try {
			val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
			val output = BufferedReader(InputStreamReader(process.inputStream)).readText()
			val error = BufferedReader(InputStreamReader(process.errorStream)).readText()
			val exitCode = process.waitFor()
			RishResult(exitCode == 0, output.trim(), error.trim())
		} catch (e: Exception) {
			RishResult(false, "", e.message ?: "Unknown error")
		}
	}
	
	fun destroy() {
		Shizuku.removeBinderReceivedListener(binderReceivedListener)
		Shizuku.removeBinderDeadListener(binderDeadListener)
	}
	
	data class RishResult(val success: Boolean, val output: String, val error: String)
	
	interface RootCheckCallback {
		fun onResult(rooted: Boolean)
	}
	
	companion object {
		@JvmStatic
		fun isRooted(callback: RootCheckCallback) {
			Thread {
				var process: Process? = null
				val result = try {
					process = Runtime.getRuntime().exec(arrayOf("su", "-c", "echo ok"))
					val reader = BufferedReader(InputStreamReader(process.inputStream))
					val out = reader.readLine()
					process.waitFor()
					reader.close()
					"ok" == out
				} catch (e: Exception) {
					false
				} finally {
					process?.destroy()
				}
				
				Handler(Looper.getMainLooper()).post {
					callback.onResult(result)
				}
			}.start()
		}
	}
}