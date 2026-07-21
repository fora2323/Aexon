package com.aexon.material.bottomnavigation

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

import com.aexon.R
import com.aexon.annotation.RequiresApi
import com.aexon.core.AexonAnimator
import com.aexon.core.AexonAttrs
import com.aexon.core.AexonColor
import com.aexon.core.AexonHaptic
import com.aexon.core.AexonIcon
import com.aexon.core.AexonMath
import com.aexon.core.AexonPath
import com.aexon.core.AexonText

@RequiresApi(RequiresApi.OREO)
class AexonNavigation @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
	
	companion object {
		private const val ICON_SIZE_RATIO = 0.46f
		private const val TEXT_SIZE_RATIO = 0.32f
		private const val GAP_RATIO = 0.26f
		private const val ITEM_PAD_RATIO = 0.34f
		private const val MIN_FIT_SCALE = 0.45f
		
		const val LABEL_AUTO = 0
		const val LABEL_LABELED = 1
		const val LABEL_UNLABELED = 2
	}
	
	private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
	private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
	private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
	
	private var radius: Int = 0
	private var cornerStyle: Int = 0
	private var iconTintList: ColorStateList? = null
	private var textColorList: ColorStateList? = null
	private var thumbColor: Int = 0
	private var bgColor: Int = 0
	private var labelVisibilityMode: Int = LABEL_AUTO
	
	private val items = ArrayList<NavItem>()
	private var selectedIndex = 0
	private var listener: OnItemSelectedListener? = null
	private var animatedIndex = 0f
	private var typeface: Typeface? = null
	private var flexibleWidth = true
	private var lastIconSize = 0f
	private var lastGap = 0f
	
	private val bgRect = RectF()
	private val thumbRect = RectF()
	private val iconBounds = Rect()
	
	private var progressArray = FloatArray(0)
	private var contentWArray = FloatArray(0)
	private var textWArray = FloatArray(0)
	private var leftPadArray = FloatArray(0)
	private var rightPadArray = FloatArray(0)
	private var slotLeftArray = FloatArray(0)
	private var slotRightArray = FloatArray(0)
	
	val menu: MenuChain = MenuChain()
	
	private val navAnimator = AexonAnimator(durationMs = 300) { value ->
		animatedIndex = value
		if (flexibleWidth) {
			requestLayout()
		}
		invalidate()
	}
	
	interface OnItemSelectedListener {
		fun onSelected(index: Int)
	}
	
	inner class MenuChain {
		private var intPos: Int = 0
		private var floatPos: Float = 0f
		private var useFloat: Boolean = false
		
		fun setCount(position: Int): MenuChain {
			this.intPos = position
			this.useFloat = false
			return this
		}
		
		fun setPageCount(position: Float): MenuChain {
			this.floatPos = position
			this.useFloat = true
			return this
		}
		
		fun setChecked(checked: Boolean) {
			if (checked) {
				if (useFloat) {
					setAnimatedIndexDirect(floatPos)
				} else {
					setSelectedIndexSilent(intPos)
				}
			}
		}
	}
	
	inner class ItemBuilder(private val index: Int) {
		private var title: String? = null
		
		fun title(title: String): ItemBuilder {
			this.title = title
			return this
		}
		
		fun icon(resId: Int) {
			while (items.size <= index) {
				items.add(NavItem("", 0))
			}
			items[index] = NavItem(title ?: "", resId)
			requestLayout()
			invalidate()
		}
	}
	
	private data class NavItem(val title: String, val icon: Int)
	
	init {
		textPaint.textAlign = Paint.Align.LEFT
		
		AexonAttrs.obtain(context, attrs, R.styleable.AexonNavigation) { a ->
			radius = a.getDimensionPixelSize(R.styleable.AexonNavigation_navigastionRadius, 50)
			cornerStyle = a.getInt(R.styleable.AexonNavigation_navigastionCornerStyle, 0)
			labelVisibilityMode = a.getInt(R.styleable.AexonNavigation_navigastionLabelVisibilityMode, LABEL_AUTO)
			
			val onTintValue = a.getColor(R.styleable.AexonNavigation_navigastionOnTint, Color.WHITE)
			val offTintValue = a.getColor(R.styleable.AexonNavigation_navigastionOffTint, Color.GRAY)
			val onTextColorValue = a.getColor(R.styleable.AexonNavigation_navigastionOnTextColor, Color.WHITE)
			thumbColor = a.getColor(R.styleable.AexonNavigation_navigastionThumbColor, Color.DKGRAY)
			
			val iconTintRes = a.getColorStateList(R.styleable.AexonNavigation_navigastionIconTintList)
			val textColorRes = a.getColorStateList(R.styleable.AexonNavigation_navigastionTextColorList)
			
			iconTintList = iconTintRes ?: ColorStateList(
			arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
			intArrayOf(onTintValue, offTintValue)
			)
			textColorList = textColorRes ?: ColorStateList(
			arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
			intArrayOf(onTextColorValue, onTextColorValue)
			)
		}
		
		val currentBackground = background
		if (currentBackground is ColorDrawable) {
			bgColor = currentBackground.color
		} else {
			bgColor = AexonColor.fromHex("#1A1A1A")
		}
		background = null
		
		bgPaint.color = bgColor
		thumbPaint.color = thumbColor
		
		val textStyle = Typeface.NORMAL
		
		AexonAttrs.obtain(context, attrs, intArrayOf(android.R.attr.fontFamily)) { taFont ->
			val fontResId = taFont.getResourceId(0, 0)
			if (fontResId != 0) {
				try {
					typeface = context.resources.getFont(fontResId)
				} catch (ignored: Exception) {
					typeface = Typeface.defaultFromStyle(textStyle)
				}
			}
		}
		
		if (typeface == null) {
			typeface = Typeface.defaultFromStyle(textStyle)
		}
		textPaint.typeface = typeface
	}
	
	fun setItemBuilder(index: Int): ItemBuilder {
		return ItemBuilder(index)
	}
	
	private fun labelProgress(i: Int): Float {
		return when (labelVisibilityMode) {
			LABEL_UNLABELED -> 0f
			LABEL_LABELED -> 1f
			else -> progressArray[i]
		}
	}
	
	private fun getArray(array: FloatArray?, size: Int): FloatArray {
		return if (array == null || array.size != size) {
			FloatArray(size)
		} else {
			array
		}
	}
	
	private fun layoutItems(
	startX: Float, availH: Float, itemCount: Int,
	constraintW: Float, effBgR: Float
	): Float {
		val iconSize = availH * ICON_SIZE_RATIO
		val textSize = availH * TEXT_SIZE_RATIO
		val gapNatural = availH * GAP_RATIO
		val padNatural = availH * ITEM_PAD_RATIO
		textPaint.textSize = textSize
		
		progressArray = getArray(progressArray, itemCount)
		contentWArray = getArray(contentWArray, itemCount)
		textWArray = getArray(textWArray, itemCount)
		leftPadArray = getArray(leftPadArray, itemCount)
		rightPadArray = getArray(rightPadArray, itemCount)
		slotLeftArray = getArray(slotLeftArray, itemCount)
		slotRightArray = getArray(slotRightArray, itemCount)
		
		var naturalTotal = 0f
		for (i in 0 until itemCount) {
			progressArray[i] = kotlin.math.max(0f, 1f - kotlin.math.abs(animatedIndex - i))
			textWArray[i] = AexonText.measureWidth(textPaint, items[i].title)
			val lp = if (i == 0) kotlin.math.max(padNatural, effBgR) else padNatural
			val rp = if (i == itemCount - 1) kotlin.math.max(padNatural, effBgR) else padNatural
			leftPadArray[i] = lp
			rightPadArray[i] = rp
			val lProg = labelProgress(i)
			val content = iconSize + (textWArray[i] + gapNatural) * lProg
			contentWArray[i] = content
			naturalTotal += lp + content + rp
		}
		
		var scale = 1f
		if (constraintW >= 0f && naturalTotal > constraintW) {
			scale = kotlin.math.max(MIN_FIT_SCALE, constraintW / naturalTotal)
		}
		
		val gapFinal = gapNatural * scale
		var fittedTotal = 0f
		for (i in 0 until itemCount) {
			leftPadArray[i] *= scale
			rightPadArray[i] *= scale
			val lProg = labelProgress(i)
			val content = iconSize + (textWArray[i] + gapFinal) * lProg
			contentWArray[i] = content
			fittedTotal += leftPadArray[i] + content + rightPadArray[i]
		}
		
		if (constraintW >= 0f) {
			val extra = constraintW - fittedTotal
			if (extra > 0f) {
				val half = (extra / itemCount) / 2f
				for (i in 0 until itemCount) {
					leftPadArray[i] += half
					rightPadArray[i] += half
				}
				fittedTotal = constraintW
			}
		}
		
		var cursor = startX
		for (i in 0 until itemCount) {
			slotLeftArray[i] = cursor
			cursor += leftPadArray[i] + contentWArray[i] + rightPadArray[i]
			slotRightArray[i] = cursor
		}
		
		lastIconSize = iconSize
		lastGap = gapFinal
		return fittedTotal
	}
	
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		val widthMode = MeasureSpec.getMode(widthMeasureSpec)
		val widthSize = MeasureSpec.getSize(widthMeasureSpec)
		val heightMode = MeasureSpec.getMode(heightMeasureSpec)
		val heightSize = MeasureSpec.getSize(heightMeasureSpec)
		
		flexibleWidth = widthMode != MeasureSpec.EXACTLY
		
		var desiredHeight = heightSize
		if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
			desiredHeight = AexonMath.dpToPxInt(context, 55f)
		}
		
		val pL = paddingLeft.toFloat()
		val pT = paddingTop.toFloat()
		val pR = paddingRight.toFloat()
		val pB = paddingBottom.toFloat()
		val availH = desiredHeight - pT - pB
		
		var desiredWidth = widthSize
		if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
			if (items.isEmpty()) {
				desiredWidth = (pL + pR).toInt()
			} else {
				val itemCount = items.size
				val effBgR = kotlin.math.min(radius.toFloat(), desiredHeight / 2f)
				val naturalContentW = layoutItems(0f, availH, itemCount, -1f, effBgR)
				desiredWidth = (pL + pR + naturalContentW).toInt()
				if (widthMode == MeasureSpec.AT_MOST) {
					desiredWidth = kotlin.math.min(desiredWidth, widthSize)
				}
			}
		}
		
		setMeasuredDimension(
		if (widthMode == MeasureSpec.EXACTLY) widthSize else desiredWidth,
		if (heightMode == MeasureSpec.EXACTLY) heightSize else desiredHeight
		)
	}
	
	fun setSelectedItemIndex(index: Int) {
		if (index < 0 || index >= items.size) {
			return
		}
		selectedIndex = index
		navAnimator.animateTo(index.toFloat(), animatedIndex)
		listener?.onSelected(index)
	}
	
	private fun setAnimatedIndexDirect(index: Float) {
		navAnimator.setImmediate(index)
	}
	
	private fun setSelectedIndexSilent(index: Int) {
		this.selectedIndex = index
	}
	
	private fun stopAnimation() {
		navAnimator.cancel()
	}
	
	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		if (items.isEmpty()) return
		
		val w = width.toFloat()
		val h = height.toFloat()
		val pL = paddingLeft.toFloat()
		val pT = paddingTop.toFloat()
		val pR = paddingRight.toFloat()
		val pB = paddingBottom.toFloat()
		
		val availW = w - pL - pR
		val availH = h - pT - pB
		
		bgRect.set(0f, 0f, w, h)
		val maxBgR = kotlin.math.min(w / 2f, h / 2f)
		val effBgR = kotlin.math.min(radius.toFloat(), maxBgR)
		
		if (cornerStyle == 1) {
			val bgPath = AexonPath.cutCornerRect(bgRect, effBgR, effBgR, effBgR, effBgR)
			canvas.drawPath(bgPath, bgPaint)
		} else {
			val bgPath = AexonPath.roundedRect(bgRect, effBgR)
			canvas.drawPath(bgPath, bgPaint)
		}
		
		val itemCount = items.size
		layoutItems(pL, availH, itemCount, availW, effBgR)
		val iconSize = lastIconSize
		val gap = lastGap
		
		var thumbLeft = 0f
		var thumbRight = 0f
		for (i in 0 until itemCount) {
			thumbLeft += slotLeftArray[i] * progressArray[i]
			thumbRight += slotRightArray[i] * progressArray[i]
		}
		
		thumbRect.set(thumbLeft, pT, thumbRight, h - pB)
		
		if (cornerStyle == 1) {
			val cutX = kotlin.math.min(kotlin.math.max(0f, effBgR - pL), availW / 2f)
			val cutY = kotlin.math.min(kotlin.math.max(0f, effBgR - pT), availH / 2f)
			
			var leftCutFactor = 0f
			var rightCutFactor = 0f
			if (animatedIndex < 1f) {
				leftCutFactor = kotlin.math.max(0f, 1f - animatedIndex)
			}
			if (animatedIndex > itemCount - 2) {
				rightCutFactor = kotlin.math.max(0f, 1f - ((itemCount - 1) - animatedIndex))
			}
			
			val cL_X = cutX * leftCutFactor
			val cL_Y = cutY * leftCutFactor
			val cR_X = cutX * rightCutFactor
			val cR_Y = cutY * rightCutFactor
			
			val thumbPath = Path()
			thumbPath.moveTo(thumbRect.left + cL_X, thumbRect.top)
			thumbPath.lineTo(thumbRect.right - cR_X, thumbRect.top)
			thumbPath.lineTo(thumbRect.right, thumbRect.top + cR_Y)
			thumbPath.lineTo(thumbRect.right, thumbRect.bottom - cR_Y)
			thumbPath.lineTo(thumbRect.right - cR_X, thumbRect.bottom)
			thumbPath.lineTo(thumbRect.left + cL_X, thumbRect.bottom)
			thumbPath.lineTo(thumbRect.left, thumbRect.bottom - cL_Y)
			thumbPath.lineTo(thumbRect.left, thumbRect.top + cL_Y)
			thumbPath.close()
			canvas.drawPath(thumbPath, thumbPaint)
		} else {
			val thumbRx = kotlin.math.min(kotlin.math.max(0f, effBgR - pL), availW / 2f)
			val thumbRy = kotlin.math.min(kotlin.math.max(0f, effBgR - pT), availH / 2f)
			val thumbPath = AexonPath.roundedRect(thumbRect, kotlin.math.min(thumbRx, thumbRy))
			canvas.drawPath(thumbPath, thumbPaint)
		}
		
		val textOffsetY = AexonText.verticalCenterOffset(textPaint)
		val centerY = pT + (availH / 2f)
		
		val checkedIconColor = iconTintList?.getColorForState(intArrayOf(android.R.attr.state_checked), Color.WHITE) ?: Color.WHITE
		val uncheckedIconColor = iconTintList?.defaultColor ?: Color.GRAY
		val textCheckedColor = textColorList?.getColorForState(intArrayOf(android.R.attr.state_checked), Color.WHITE) ?: Color.WHITE
		val textUncheckedColor = textColorList?.defaultColor ?: Color.GRAY
		
		for (i in 0 until itemCount) {
			val p = progressArray[i]
			val lProg = labelProgress(i)
			val slotWidth = slotRightArray[i] - slotLeftArray[i]
			val iconX = if (lProg <= 0f) {
				slotLeftArray[i] + (slotWidth - iconSize) / 2f
			} else {
				slotLeftArray[i] + leftPadArray[i]
			}
			val iconColor = AexonColor.blend(uncheckedIconColor, checkedIconColor, p)
			
			val hs = (iconSize / 2).toInt()
			iconBounds.set(iconX.toInt(), (centerY - hs).toInt(), (iconX + iconSize).toInt(), (centerY + hs).toInt())
			AexonIcon.drawTinted(context, canvas, items[i].icon, iconBounds, iconColor, p > 0.5f)
			
			if (lProg > 0.1f) {
				textPaint.typeface = Typeface.create(typeface, if (p > 0.9f) Typeface.BOLD else Typeface.NORMAL)
				val textColor = AexonColor.blend(textUncheckedColor, textCheckedColor, p)
				textPaint.color = AexonColor.withAlphaFraction(textColor, lProg)
				canvas.drawText(items[i].title, iconX + iconSize + (gap * lProg), centerY + textOffsetY, textPaint)
			}
		}
	}
	
	private fun findIndexAt(x: Float, itemCount: Int): Int {
		if (slotLeftArray.size != itemCount || slotRightArray.size != itemCount) {
			return -1
		}
		for (i in 0 until itemCount) {
			if (x >= slotLeftArray[i] && x < slotRightArray[i]) {
				return i
			}
		}
		if (itemCount > 0 && x < slotLeftArray[0]) return 0
		if (itemCount > 0 && x >= slotRightArray[itemCount - 1]) return itemCount - 1
		return -1
	}
	
	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent): Boolean {
		if (items.isEmpty()) return super.onTouchEvent(event)
		
		val action = event.actionMasked
		val x = event.x
		val itemCount = items.size
		
		when (action) {
			MotionEvent.ACTION_DOWN -> return true
			
			MotionEvent.ACTION_UP -> {
				val index = findIndexAt(x, itemCount)
				if (index >= 0 && index != selectedIndex) {
					AexonHaptic.tick(this)
					setSelectedItemIndex(index)
				}
				performClick()
			}
		}
		return true
	}
	
	override fun performClick(): Boolean {
		return super.performClick()
	}
	
	fun setOnItemSelectedListener(l: OnItemSelectedListener) {
		this.listener = l
	}
	
	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		stopAnimation()
	}
	
	override fun setBackgroundColor(color: Int) {
		this.bgColor = color
		bgPaint.color = color
		super.setBackground(null)
		invalidate()
	}
	
	fun setItemActiveIndicatorColor(color: Int) {
		this.thumbColor = color
		thumbPaint.color = color
		invalidate()
	}
	
	fun setRadius(radiusPx: Int) {
		this.radius = radiusPx
		invalidate()
	}
	
	fun setItemIconTintList(tint: ColorStateList?) {
		this.iconTintList = tint
		invalidate()
	}
	
	fun setItemTextColor(colors: ColorStateList?) {
		this.textColorList = colors
		invalidate()
	}
	
	fun setLabelVisibilityMode(mode: Int) {
		this.labelVisibilityMode = mode
		requestLayout()
		invalidate()
	}
}