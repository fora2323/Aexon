package com.aexon.core

import android.Manifest
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.DecelerateInterpolator

class AexonAnimator(private val durationMs: Long = 300, private val interpolator: TimeInterpolator = DecelerateInterpolator(), private val onUpdate: (Float) -> Unit) {
	private var animator: ValueAnimator? = null
	var value: Float = 0f
	private set
	
	fun animateTo(target: Float, from: Float = value) {
		cancel()
		value = from
		animator = ValueAnimator.ofFloat(from, target).apply {
			duration = durationMs
			interpolator = this@AexonAnimator.interpolator
			addUpdateListener {
				value = it.animatedValue as Float
				onUpdate(value)
			}
			start()
		}
	}
	
	fun setImmediate(target: Float) {
		cancel()
		value = target
		onUpdate(value)
	}
	
	fun cancel() {
		animator?.let {
			it.cancel()
			it.removeAllUpdateListeners()
		}
		animator = null
	}
}

object AexonAttrs {
	
	@JvmStatic
	inline fun <T> obtain(context: Context, attrs: AttributeSet?, styleable: IntArray, block: (TypedArray) -> T): T {
		val ta = context.obtainStyledAttributes(attrs, styleable)
		try {
			return block(ta)
		} finally {
			ta.recycle()
		}
	}
}

object AexonIcon {
	
	@JvmStatic
	fun drawTinted(context: Context, canvas: Canvas, resId: Int, bounds: Rect, color: Int, checked: Boolean = false) {
		val d = context.getDrawable(resId) ?: return
		d.mutate()
		d.state = if (checked) intArrayOf(android.R.attr.state_checked) else intArrayOf(-android.R.attr.state_checked)
		if (AexonApi.minSdk(Build.VERSION_CODES.Q)) {
			d.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_IN)
		} else {
			d.setColorFilter(color, PorterDuff.Mode.SRC_IN)
		}
		d.bounds = bounds
		d.draw(canvas)
	}
}

object AexonPath {
	
	@JvmStatic
	fun roundedRect(rect: RectF, radius: Float): Path {
		val path = Path()
		path.addRoundRect(rect, radius, radius, Path.Direction.CW)
		return path
	}
	
	@JvmStatic
	fun cutCornerRect(rect: RectF, cutTL: Float, cutTR: Float, cutBR: Float, cutBL: Float): Path {
		val path = Path()
		path.moveTo(rect.left + cutTL, rect.top)
		path.lineTo(rect.right - cutTR, rect.top)
		if (cutTR > 0f) path.lineTo(rect.right, rect.top + cutTR)
		path.lineTo(rect.right, rect.bottom - cutBR)
		if (cutBR > 0f) path.lineTo(rect.right - cutBR, rect.bottom)
		path.lineTo(rect.left + cutBL, rect.bottom)
		if (cutBL > 0f) path.lineTo(rect.left, rect.bottom - cutBL)
		path.lineTo(rect.left, rect.top + cutTL)
		if (cutTL > 0f) path.close()
		return path
	}
}

object AexonText {
	
	@JvmStatic
	fun verticalCenterOffset(paint: Paint): Float {
		val fm = paint.fontMetrics
		return (fm.descent - fm.ascent) / 2f - fm.descent
	}
	
	@JvmStatic
	fun measureWidth(paint: Paint, text: String): Float {
		return paint.measureText(text)
	}
	
	@JvmStatic
	fun drawCentered(canvas: Canvas, paint: Paint, text: String, centerX: Float, centerY: Float) {
		val width = measureWidth(paint, text)
		val offsetY = verticalCenterOffset(paint)
		canvas.drawText(text, centerX - width / 2f, centerY + offsetY, paint)
	}
}

object AexonHaptic {
	
	@JvmStatic
	fun tick(view: View) {
		if (AexonApi.minSdk(Build.VERSION_CODES.R)) {
			view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
		} else {
			view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
		}
	}
	
	@JvmStatic
	fun confirm(view: View) {
		if (AexonApi.minSdk(Build.VERSION_CODES.R)) {
			view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
		} else {
			view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
		}
	}
}

object AexonPermission {
	
	@JvmStatic
	fun hasNotificationPermission(context: Context): Boolean {
		if (AexonApi.minSdk(Build.VERSION_CODES.TIRAMISU)) {
			return context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
		}
		return true
	}
	
	@JvmStatic
	fun shouldShowNotificationRationale(activity: Activity): Boolean {
		if (AexonApi.minSdk(Build.VERSION_CODES.TIRAMISU)) {
			return activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
		}
		return false
	}
	
	@JvmStatic
	fun requestNotificationPermission(activity: Activity, requestCode: Int) {
		if (AexonApi.minSdk(Build.VERSION_CODES.TIRAMISU)) {
			activity.requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), requestCode)
		}
	}
}