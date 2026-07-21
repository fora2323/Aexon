package com.aexon.material.imageview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView

import com.aexon.R
import com.aexon.core.AexonAttrs
import com.aexon.core.AexonMath

class AexonImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ImageView(context, attrs, defStyleAttr) {
	
	var radiusTopLeft: Float = 0f
	set(value) { 
		field = value; invalidate()
	}
	
	var radiusTopRight: Float = 0f
	set(value) {
		field = value; invalidate()
	}
	
	var radiusBottomLeft: Float = 0f
	set(value) { 
		field = value; invalidate()
	}
	
	var radiusBottomRight: Float = 0f
	set(value) {
		field = value; invalidate() 
	}
	
	var radius: Float = 0f
	set(value) {
		field = value
		val px = AexonMath.dpToPx(context, value)
		radiusTopLeft = px
		radiusTopRight = px
		radiusBottomLeft = px
		radiusBottomRight = px
	}
	
	var strokeWidth: Float = 0f
	set(value) { 
		field = value; invalidate() 
	}
	
	var strokeColor: Int = Color.TRANSPARENT
	set(value) { 
		field = value; invalidate() 
	}
	
	private val clipPath = Path()
	private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		style = Paint.Style.STROKE
	}
	
	init {
		AexonAttrs.obtain(context, attrs, R.styleable.AexonImageView) { ta ->
			val globalRadius = ta.getDimension(R.styleable.AexonImageView_ax_radius, 0f)
			if (globalRadius > 0f) {
				radiusTopLeft = globalRadius
				radiusTopRight = globalRadius
				radiusBottomLeft = globalRadius
				radiusBottomRight = globalRadius
			} else {
				radiusTopLeft = ta.getDimension(R.styleable.AexonImageView_ax_radiusTopLeft, 0f)
				radiusTopRight = ta.getDimension(R.styleable.AexonImageView_ax_radiusTopRight, 0f)
				radiusBottomLeft = ta.getDimension(R.styleable.AexonImageView_ax_radiusBottomLeft, 0f)
				radiusBottomRight = ta.getDimension(R.styleable.AexonImageView_ax_radiusBottomRight, 0f)
			}
			strokeWidth = ta.getDimension(R.styleable.AexonImageView_ax_strokeWidth, 0f)
			strokeColor = ta.getColor(R.styleable.AexonImageView_ax_strokeColor, Color.TRANSPARENT)
		}
	}
	
	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		outlineProvider = object : ViewOutlineProvider() {
			override fun getOutline(view: View, outline: Outline) {
				outline.setRoundRect(0, 0, w, h, maxOf(radiusTopLeft, radiusTopRight, radiusBottomLeft, radiusBottomRight))
			}
		}
		clipToOutline = true
	}
	
	override fun onDraw(canvas: Canvas) {
		clipPath.reset()
		val radii = floatArrayOf(radiusTopLeft, radiusTopLeft, radiusTopRight, radiusTopRight, radiusBottomRight, radiusBottomRight, radiusBottomLeft, radiusBottomLeft)
		clipPath.addRoundRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), radii, Path.Direction.CW)
		canvas.clipPath(clipPath)
		super.onDraw(canvas)
		
		if (strokeWidth > 0f && strokeColor != Color.TRANSPARENT) {
			strokePaint.strokeWidth = strokeWidth
			strokePaint.color = strokeColor
			val half = strokeWidth / 2f
			val strokeRadii = radii.map { maxOf(0f, it - half) }.toFloatArray()
			val strokePath = Path()
			strokePath.addRoundRect(RectF(half, half, width - half, height - half), strokeRadii, Path.Direction.CW)
			canvas.drawPath(strokePath, strokePaint)
		}
	}
}