package com.aexon.material.button

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.Button
import com.aexon.R
import com.aexon.annotation.NonNull
import com.aexon.annotation.Nullable
import com.aexon.core.AexonMath
import kotlin.math.max
import kotlin.math.min

class AexonButton : Button {
	
	companion object {
		const val ICON_GRAVITY_START = 0x1
		const val ICON_GRAVITY_TEXT_START = 0x2
		const val ICON_GRAVITY_END = 0x3
		const val ICON_GRAVITY_TEXT_END = 0x4
	}
	
	private var cornerRadius = 0f
	private var iconGravity = ICON_GRAVITY_START
	private var iconPadding = 0f
	private var iconTint = Integer.MIN_VALUE
	private var iconSize = 0
	private var iconLeft = 0
	private var icon: Drawable? = null
	private var bgColor = Color.TRANSPARENT
	private var rippleColor = Color.argb(40, 255, 255, 255)
	private var strokeColor = Color.TRANSPARENT
	private var strokeWidth = 0f
	
	constructor(@NonNull context: Context) : super(context) {
		init(context, null)
	}
	
	constructor(@NonNull context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {
		init(context, attrs)
	}
	
	constructor(@NonNull context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		init(context, attrs)
	}
	
	private fun init(@NonNull context: Context, @Nullable attrs: AttributeSet?) {
		isAllCaps = false
		minimumWidth = AexonMath.dpToPxInt(context, 88f)
		minimumHeight = AexonMath.dpToPxInt(context, 36f)
		includeFontPadding = false
		gravity = Gravity.CENTER
		
		if (attrs != null) {
			val a = context.obtainStyledAttributes(attrs, R.styleable.AexonButton)
			cornerRadius = a.getDimension(R.styleable.AexonButton_cornerRadius, 0f)
			iconGravity = a.getInt(R.styleable.AexonButton_iconGravity, ICON_GRAVITY_START)
			iconPadding = a.getDimension(R.styleable.AexonButton_iconPadding, AexonMath.dpToPxInt(context, 8f).toFloat())
			iconTint = a.getColor(R.styleable.AexonButton_iconTint, Integer.MIN_VALUE)
			iconSize = a.getDimension(R.styleable.AexonButton_iconSize, 0f).toInt()
			icon = a.getDrawable(R.styleable.AexonButton_icon)
			bgColor = a.getColor(R.styleable.AexonButton_buttonColor, Color.TRANSPARENT)
			rippleColor = a.getColor(R.styleable.AexonButton_rippleColor, Color.argb(40, 255, 255, 255))
			strokeColor = a.getColor(R.styleable.AexonButton_strokeColor, Color.TRANSPARENT)
			strokeWidth = a.getDimension(R.styleable.AexonButton_strokeWidth, 0f)
			a.recycle()
		}
		
		if (bgColor == Color.TRANSPARENT) {
			val bg = background
			if (bg is android.graphics.drawable.ColorDrawable) {
				bgColor = bg.color
			}
		}
		
		compoundDrawablePadding = iconPadding.toInt()
		applyBackground()
		updateIcon(true)
	}
	
	private fun applyBackground() {
		val shape = GradientDrawable().apply {
			setColor(bgColor)
			setCornerRadius(cornerRadius)
			
			if (strokeWidth > 0 && strokeColor != Color.TRANSPARENT) {
				setStroke(strokeWidth.toInt(), strokeColor)
			}
		}
		
		val ripple = RippleDrawable(ColorStateList.valueOf(rippleColor), shape, null)
		background = ripple
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			outlineProvider = object : android.view.ViewOutlineProvider() {
				override fun getOutline(view: View, outline: android.graphics.Outline) {
					if (view.width > 0 && view.height > 0) {
						outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
					}
				}
			}
			clipToOutline = true
			invalidateOutline()
		}
	}
	
	private fun updateIcon(needsIconReset: Boolean) {
		if (icon != null) {
			icon = icon!!.mutate()
			
			val width = if (iconSize != 0) iconSize else icon!!.intrinsicWidth
			val height = if (iconSize != 0) iconSize else icon!!.intrinsicHeight
			icon!!.setBounds(iconLeft, 0, iconLeft + width, height)
			
			if (iconTint != Integer.MIN_VALUE) {
				icon!!.setColorFilter(iconTint, PorterDuff.Mode.SRC_IN)
			} else {
				icon!!.clearColorFilter()
			}
		}
		
		if (needsIconReset) {
			resetIconDrawable()
		}
	}
	
	private fun resetIconDrawable() {
		when (iconGravity) {
			ICON_GRAVITY_START, ICON_GRAVITY_TEXT_START -> setCompoundDrawablesRelative(icon, null, null, null)
			ICON_GRAVITY_END, ICON_GRAVITY_TEXT_END -> setCompoundDrawablesRelative(null, null, icon, null)
		}
	}
	
	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		updateIconPosition(w, h)
	}
	
	override fun onTextChanged(text: CharSequence?, start: Int, before: Int, after: Int) {
		super.onTextChanged(text, start, before, after)
		updateIconPosition(measuredWidth, measuredHeight)
	}
	
	private fun updateIconPosition(buttonWidth: Int, buttonHeight: Int) {
		if (icon == null || layout == null) return
		
		if (iconGravity == ICON_GRAVITY_TEXT_START || iconGravity == ICON_GRAVITY_TEXT_END) {
			val localIconSize = if (iconSize == 0) icon!!.intrinsicWidth else iconSize
			
			val paddingStart = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) paddingStart else paddingLeft
			val paddingEnd = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) paddingEnd else paddingRight
			
			var newIconLeft = (buttonWidth - textWidth - paddingEnd - localIconSize - iconPadding.toInt() - paddingStart) / 2
			
			val isRTL = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && layoutDirection == View.LAYOUT_DIRECTION_RTL
			
			if (isRTL != (iconGravity == ICON_GRAVITY_TEXT_END)) {
				newIconLeft = -newIconLeft
			}
			
			newIconLeft = max(0, newIconLeft)
			
			if (iconLeft != newIconLeft) {
				iconLeft = newIconLeft
				updateIcon(false)
			}
		} else {
			if (iconLeft != 0) {
				iconLeft = 0
				updateIcon(false)
			}
		}
	}
	
	private val textWidth: Int
	get() {
		val textPaint = paint
		var text = getText().toString()
		if (transformationMethod != null) {
			text = transformationMethod!!.getTransformation(text, this).toString()
		}
		return if (layout != null) {
			min(textPaint.measureText(text).toInt(), layout!!.ellipsizedWidth)
		} else {
			textPaint.measureText(text).toInt()
		}
	}
	
	fun setIcon(@Nullable drawable: Drawable?) {
		if (this.icon != drawable) {
			this.icon = drawable
			updateIcon(true)
			updateIconPosition(measuredWidth, measuredHeight)
		}
	}
	
	fun setIcon(resId: Int) {
		if (resId != 0) {
			val drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				context.getDrawable(resId)
			} else {
				context.resources.getDrawable(resId)
			}
			setIcon(drawable)
		} else {
			setIcon(null as Drawable?)
		}
	}
	
	fun setCornerRadius(radius: Float) {
		this.cornerRadius = radius
		applyBackground()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			invalidateOutline() 
		}
	}
	
	fun setIconTint(color: Int) {
		this.iconTint = color
		updateIcon(false)
	}
	
	override fun setBackgroundColor(color: Int) {
		this.bgColor = color
		applyBackground()
	}
	
	fun setIconSize(sizePx: Int) {
		this.iconSize = sizePx
		updateIcon(true)
	}
	
	fun setRippleColor(color: Int) {
		this.rippleColor = color
		applyBackground()
	}
	
	fun setStroke(width: Float, color: Int) {
		this.strokeWidth = width
		this.strokeColor = color
		applyBackground()
	}
	
	fun setStrokeColor(color: Int) {
		this.strokeColor = color
		applyBackground()
	}
	
	fun setStrokeWidth(width: Float) {
		this.strokeWidth = width
		applyBackground()
	}
	
	fun setIconGravity(gravity: Int) {
		if (this.iconGravity != gravity) {
			this.iconGravity = gravity
			updateIconPosition(measuredWidth, measuredHeight)
		}
	}
}