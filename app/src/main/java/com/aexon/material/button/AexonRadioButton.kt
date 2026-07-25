package com.aexon.material.button

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.widget.RadioButton

import com.aexon.annotation.NonNull
import com.aexon.annotation.Nullable
import com.aexon.annotation.RequiresApi
import com.aexon.theme.AexonTheme
import com.aexon.theme.AexonThemeListener
import com.aexon.core.AexonApi.minSdk

class AexonRadioButton : RadioButton {
	
	private val themeListener = AexonThemeListener { _, theme -> applyTheme(theme) }
	
	constructor(@NonNull context: Context) : super(context) {
		init()
	}
	
	constructor(@NonNull context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {
		init()
	}
	
	constructor(@NonNull context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		init()
	}
	
	private fun init() {
		applyTheme(AexonTheme.getInstance())
	}
	
	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		AexonTheme.getInstance().addListener(themeListener)
		applyTheme(AexonTheme.getInstance())
	}
	
	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		AexonTheme.getInstance().removeListener(themeListener)
	}
	
	private fun applyTheme(@NonNull theme: AexonTheme) {
		val colorPrimary = theme.colorPrimary
		val colorOnSurface = theme.colorOnSurface
		val colorOutline = theme.colorOutline
		setButtonTintList(ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_enabled, android.R.attr.state_checked), intArrayOf(-android.R.attr.state_enabled, -android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked), intArrayOf()), intArrayOf(withAlpha(colorPrimary, 0.38f), withAlpha(colorOutline, 0.38f), colorPrimary, colorOutline)))
		if (minSdk(RequiresApi.LOLLIPOP)) {
			applyRippleBackground(colorPrimary, colorOnSurface)
		}
		setTextColor(ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf()), intArrayOf(withAlpha(colorOnSurface, 0.38f), colorOnSurface)))
	}
	
	@RequiresApi(RequiresApi.LOLLIPOP)
	private fun applyRippleBackground(colorPrimary: Int, colorOnSurface: Int) {
		val rippleColor = ColorStateList(
		arrayOf(
		intArrayOf(android.R.attr.state_checked),
		intArrayOf()
		),
		intArrayOf(
		withAlpha(colorPrimary, 0.12f),
		withAlpha(colorOnSurface, 0.08f)
		)
		)
		
		val defaultBg = background
		if (defaultBg is RippleDrawable) {
			val defaultRipple = defaultBg.mutate() as RippleDrawable
			defaultRipple.setColor(rippleColor)
			setBackground(defaultRipple)
		} else {
			setBackground(RippleDrawable(rippleColor, null, null))
		}
	}
	
	private fun withAlpha(color: Int, alpha: Float): Int {
		return (color and 0x00FFFFFF) or (Math.round(alpha * 255) shl 24)
	}
}