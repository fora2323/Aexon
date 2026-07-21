package com.aexon.core

import android.os.Build

object AexonApi {
	
	@JvmStatic
	fun minSdk(apiLevel: Int): Boolean {
		return Build.VERSION.SDK_INT >= apiLevel
	}
	
	@JvmStatic
	fun maxSdk(apiLevel: Int): Boolean {
		return Build.VERSION.SDK_INT <= apiLevel
	}
}