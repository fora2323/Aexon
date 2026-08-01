package com.aexon.material.slider;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;

import com.aexon.R;

public class Slider extends SeekBar {
	
	private static final float TRACK_HEIGHT_DP = 14f;
	private static final float THUMB_HEIGHT_DP = 38f;
	private static final float THUMB_WIDTH_DP = 3f;
	private static final float GAP_DP = 5f;
	
	private int trackActiveColor;
	private int trackInactiveColor;
	private int thumbColor;
	
	private float density;
	private float trackHeight;
	private float thumbHeight;
	private float thumbWidth;
	private float thumbWidthAnim;
	private float gapX;
	private float outerRadius;
	private float innerRadius;
	private float dotRadius;
	
	private Paint trackPaint;
	private Paint thumbPaint;
	private Paint dotPaint;
	
	private ValueAnimator widthAnimator;
	
	private SliderListener sliderListener;
	
	public Slider(Context context) {
		super(context);
		init();
	}
	
	public Slider(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		
		density = getResources().getDisplayMetrics().density;
		
		int primary          = getResources().getColor(R.color.colorPrimary);
		trackActiveColor     = primary;
		trackInactiveColor   = (primary & 0x00FFFFFF) | 0x4D000000;
		thumbColor           = primary;
		
		trackHeight = dp(TRACK_HEIGHT_DP);
		thumbHeight = dp(THUMB_HEIGHT_DP);
		thumbWidth  = dp(THUMB_WIDTH_DP);
		gapX        = dp(GAP_DP);
		
		outerRadius = dp(7.5f);
		innerRadius = dp(2f);
		dotRadius   = dp(2.5f);
		
		thumbWidthAnim = thumbWidth;
		
		trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		trackPaint.setStyle(Paint.Style.FILL);
		
		thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		thumbPaint.setStyle(Paint.Style.FILL);
		thumbPaint.setColor(thumbColor);
		
		dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		dotPaint.setStyle(Paint.Style.FILL);
		dotPaint.setColor(trackActiveColor);
		
		setBackground(null);
		setThumb(null);
		setSplitTrack(false);
		
		int px = (int) dp(8);
		int py = (int) dp(6);
		setPadding(px, py, px, py);
	}
	
	private float dp(float v) {
		return v * density;
	}
	
	/* ========================= */
	/* ===== LISTENER API ====== */
	/* ========================= */
	
	public interface SliderListener {
		void onValueChanged(Slider slider, int value, boolean fromUser);
		void onStartTracking(Slider slider);
		void onStopTracking(Slider slider);
	}
	
	public void setOnSliderChangeListener(SliderListener listener) {
		this.sliderListener = listener;
	}
	
	/* ========================= */
	/* ===== TOUCH SYSTEM ====== */
	/* ========================= */
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (!isEnabled()) return false;
		
		float x = event.getX();
		
		switch (event.getAction()) {
			
			case MotionEvent.ACTION_DOWN:
			getParent().requestDisallowInterceptTouchEvent(true);
			animateThumb(thumbWidthAnim, dp(2));
			
			if (sliderListener != null)
			sliderListener.onStartTracking(this);
			
			updateProgress(x, true);
			return true;
			
			case MotionEvent.ACTION_MOVE:
			updateProgress(x, true);
			return true;
			
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			animateThumb(thumbWidthAnim, thumbWidth);
			
			if (sliderListener != null)
			sliderListener.onStopTracking(this);
			
			getParent().requestDisallowInterceptTouchEvent(false);
			return true;
		}
		
		return false;
	}
	
	private void updateProgress(float touchX, boolean fromUser) {
		
		float left  = getPaddingLeft();
		float right = getWidth() - getPaddingRight();
		
		float clamped = Math.max(left, Math.min(touchX, right));
		float percent = (clamped - left) / (right - left);
		
		int value = Math.round(percent * getMax());
		
		setProgress(value);
		
		if (sliderListener != null) {
			sliderListener.onValueChanged(this, value, fromUser);
		}
		
		invalidate();
	}
	
	/* ========================= */
	/* ===== THUMB ANIMATION === */
	/* ========================= */
	
	private void animateThumb(float from, float to) {
		
		if (widthAnimator != null) widthAnimator.cancel();
		
		widthAnimator = ValueAnimator.ofFloat(from, to);
		widthAnimator.setDuration(180);
		widthAnimator.setInterpolator(new DecelerateInterpolator());
		
		widthAnimator.addUpdateListener(a -> {
			thumbWidthAnim = (float) a.getAnimatedValue();
			invalidate();
		});
		
		widthAnimator.start();
	}
	
	/* ========================= */
	/* ===== MEASURE =========== */
	/* ========================= */
	
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int mode = MeasureSpec.getMode(heightMeasureSpec);
		int size = MeasureSpec.getSize(heightMeasureSpec);
		
		thumbHeight = dp(THUMB_HEIGHT_DP);
		thumbWidth  = dp(THUMB_WIDTH_DP);
		gapX        = dp(GAP_DP);
		
		int finalHeight;
		int minimumRequiredHeight = (int) (thumbHeight + getPaddingTop() + getPaddingBottom());
		
		if (mode == MeasureSpec.EXACTLY) {
			trackHeight = Math.max(0, size - getPaddingTop() - getPaddingBottom());
			finalHeight = Math.max(minimumRequiredHeight, size);
		} else {
			trackHeight = dp(TRACK_HEIGHT_DP);
			finalHeight = minimumRequiredHeight;
			
			if (mode == MeasureSpec.AT_MOST) {
				finalHeight = Math.min(finalHeight, size);
			}
		}
		
		outerRadius = trackHeight / 2f;
		innerRadius = thumbWidth / 2f;
		
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), finalHeight);
	}
	
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		
		float centerY = getHeight() / 2f;
		
		float top    = centerY - trackHeight / 2f;
		float bottom = centerY + trackHeight / 2f;
		
		float left  = getPaddingLeft();
		float right = getWidth() - getPaddingRight();
		
		float max   = getMax() == 0 ? 1 : getMax();
		float ratio = (float) getProgress() / max;
		float thumbX = left + ratio * (right - left);
		
		trackPaint.setColor(trackActiveColor);
		float activeEnd = thumbX - gapX;
		
		if (activeEnd > left) {
			drawRect(canvas, left, top, activeEnd, bottom,
			outerRadius, innerRadius, trackPaint);
		}
		
		trackPaint.setColor(trackInactiveColor);
		float inactiveStart = thumbX + gapX;
		
		if (inactiveStart < right) {
			drawRect(canvas, inactiveStart, top, right, bottom,
			innerRadius, outerRadius, trackPaint);
			
			if (getProgress() < getMax()) {
				float dotCenterX = right - outerRadius;
				
				if (inactiveStart < dotCenterX) {
					canvas.drawCircle(dotCenterX, centerY, dotRadius, dotPaint);
				}
			}
		}
		
		canvas.drawRoundRect(new RectF(thumbX - thumbWidthAnim / 2f, centerY - thumbHeight / 2f, thumbX + thumbWidthAnim / 2f, centerY + thumbHeight / 2f), thumbWidthAnim / 2f, thumbWidthAnim / 2f, thumbPaint);
	}
	
	private void drawRect(Canvas c, float l, float t, float r, float b, float rl, float rr, Paint p) {
		Path path = new Path();
		path.addRoundRect(new RectF(l, t, r, b), new float[]{ rl, rl, rr, rr, rr, rr, rl, rl }, Path.Direction.CW);
		c.drawPath(path, p);
	}
}