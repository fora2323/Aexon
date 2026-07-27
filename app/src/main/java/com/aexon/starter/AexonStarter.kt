package com.aexon.starter

import android.app.DialogFragment
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.aexon.Aexon
import com.aexon.R
import com.aexon.annotation.Nullable
import com.aexon.annotation.RequiresApi
import com.aexon.core.AexonApi
import com.aexon.material.aexonloading.AexonLoading
import com.aexon.theme.AexonTheme

class AexonStarter : DialogFragment() {
	
	companion object {
		const val MODE_SHIZUKU = 0
		const val MODE_ROOT = 1
		
		@JvmStatic
		fun newInstance(mode: Int, @Nullable shizuku: AexonShizukuHelper?): AexonStarter {
			return AexonStarter().also {
				it.mode = mode
				it.shizuku = shizuku
			}
		}
	}
	
	private lateinit var loading: AexonLoading
	private var mode: Int = MODE_SHIZUKU
	@Nullable private var shizuku: AexonShizukuHelper? = null
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		return inflater.inflate(R.layout.aexon_loading, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val theme = AexonTheme.getInstance()
		loading = view.findViewById(R.id.loadingaexon1)
		loading.setTrackColor(theme.getColorSurfaceContainer())
		loading.setThumbColor(theme.getColorPrimary())
		loading.setIndeterminate(true)
		startExecution()
	}
	
	private fun startExecution() {
		val path = Aexon.getPath(activity)
		when (mode) {
			MODE_SHIZUKU -> Thread {
				shizuku?.exec(path)
				waitForBinder()
			}.start()
			MODE_ROOT -> Thread {
				try {
					Runtime.getRuntime().exec(arrayOf("su", "-c", path))
				} catch (e: Exception) {
					e.printStackTrace()
				}
				waitForBinder()
			}.start()
		}
	}
	
	override fun onStart() {
		super.onStart()
		val dialog = dialog ?: return
		val window = dialog.window ?: return
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
		window.setBackgroundDrawableResource(android.R.color.transparent)
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
		window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
		window.setDimAmount(0.6f)
		if (AexonApi.minSdk(RequiresApi.LOLLIPOP)) {
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
			window.statusBarColor = Color.TRANSPARENT
			window.navigationBarColor = Color.TRANSPARENT
		}
		if (AexonApi.minSdk(RequiresApi.PIE)) {
			val lp = window.attributes
			lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
			window.attributes = lp
		}
		if (AexonApi.minSdk(RequiresApi.R)) {
			window.setDecorFitsSystemWindows(false)
		}
		dialog.setCanceledOnTouchOutside(false)
		dialog.setCancelable(false)
	}
	
	override fun onStop() {
		super.onStop()
		if (::loading.isInitialized) loading.setIndeterminate(false)
	}
	
	private fun waitForBinder() {
		while (!Aexon.isBinder()) {
			Thread.sleep(300)
		}
		activity?.runOnUiThread {
			if (!isDetached) dismiss()
		}
	}
}