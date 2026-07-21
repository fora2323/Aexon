package com.aexon.material.color;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.aexon.R;

public class AexonColorSliderAlpha extends View {
    
	private Paint trackPaint, thumbPaint, strokePaint, shadowPaint;
	private float thumbX, thumbSize, cornerRadius, strokeWidth, trackElevation;
	private int baseColor = Color.RED;
	private float alpha = 1f;
	private final RectF trackRect = new RectF();
	private AexonColorPickerView colorPickerView;
	private Bitmap checkerBitmap;
	private boolean isDragging = false;
	
	public AexonColorSliderAlpha(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorSlider);
		cornerRadius = a.getDimension(R.styleable.ColorSlider_radius_slider, 8);
		strokeWidth = a.getDimension(R.styleable.ColorSlider_slider_stroke, 1);
		int sColor = a.getColor(R.styleable.ColorSlider_slider_stroke_color, Color.parseColor("#424242"));
		trackElevation = a.getDimension(R.styleable.ColorSlider_slider_elevation_track, 4);
		a.recycle();
		
		initPaints(sColor);
		createCheckerPattern();
		setLayerType(LAYER_TYPE_SOFTWARE, null);
	}
	
	public void setCornerRadius(float radius) {
		this.cornerRadius = radius;
		requestLayout();
		invalidate();
	}
	
	public void setStrokeWidth(float width) {
		this.strokeWidth = width;
		strokePaint.setStrokeWidth(width);
		invalidate();
	}
	
	public void setStrokeColor(int color) {
		strokePaint.setColor(color);
		invalidate();
	}
	
	public void setTrackElevation(float elevation) {
		this.trackElevation = elevation;
		if (elevation > 0) {
			shadowPaint.setMaskFilter(new BlurMaskFilter(elevation, BlurMaskFilter.Blur.NORMAL));
		} else {
			shadowPaint.setMaskFilter(null);
		}
		requestLayout();
		invalidate();
	}
	
	private void initPaints(int sColor) {
		trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		shadowPaint.setColor(0x40000000);
		if (trackElevation > 0) shadowPaint.setMaskFilter(new BlurMaskFilter(trackElevation, BlurMaskFilter.Blur.NORMAL));
		thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		thumbPaint.setColor(Color.WHITE);
		thumbPaint.setShadowLayer(4, 0, 1, 0x40000000);
		strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		strokePaint.setStyle(Paint.Style.STROKE);
		strokePaint.setStrokeWidth(strokeWidth);
		strokePaint.setColor(sColor);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		thumbSize = h * 0.5f; 
		float trackHeight = h * 0.8f; 
		float extraOffset = Math.max(trackElevation, strokeWidth);
		
		float pL = getPaddingLeft() + extraOffset;
		float pR = getPaddingRight() + extraOffset;
		float pY = (h - trackHeight) / 2f;
		
		trackRect.set(pL, pY, w - pR, h - pY);
		updateGradient();
		updateThumbPosition();
	}
	
	private void updateThumbPosition() {
		if (getWidth() > 0) {
			float gap = thumbSize * 0.8f; 
			float startX = trackRect.left + gap;
			float endX = trackRect.right - gap;
			thumbX = startX + (alpha * (endX - startX));
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (trackElevation > 0) canvas.drawRoundRect(trackRect, cornerRadius, cornerRadius, shadowPaint);
		if (checkerBitmap != null) {
			Paint cp = new Paint(Paint.ANTI_ALIAS_FLAG);
			cp.setShader(new BitmapShader(checkerBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
			canvas.drawRoundRect(trackRect, cornerRadius, cornerRadius, cp);
		}
		canvas.drawRoundRect(trackRect, cornerRadius, cornerRadius, trackPaint);
		if (strokeWidth > 0) canvas.drawRoundRect(trackRect, cornerRadius, cornerRadius, strokePaint);
		canvas.drawCircle(thumbX, trackRect.centerY(), thumbSize / 2f, thumbPaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float gap = thumbSize * 0.8f;
		float startX = trackRect.left + gap;
		float endX = trackRect.right - gap;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
			isDragging = true;
			thumbX = Math.max(startX, Math.min(event.getX(), endX));
			alpha = (endX - startX) > 0 ? (thumbX - startX) / (endX - startX) : 0;
			if (colorPickerView != null) colorPickerView.updateFromSliders(alpha, colorPickerView.getCurrentBrightness());
			invalidate();
			return true;
			case MotionEvent.ACTION_UP:
			isDragging = false;
			return true;
		}
		return super.onTouchEvent(event);
	}
	
	private void createCheckerPattern() {
		int size = 16;
		Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint p = new Paint();
		p.setColor(Color.LTGRAY);
		canvas.drawRect(0, 0, size/2f, size/2f, p);
		canvas.drawRect(size/2f, size/2f, size, size, p);
		p.setColor(Color.WHITE);
		canvas.drawRect(size/2f, 0, size, size/2f, p);
		canvas.drawRect(0, size/2f, size/2f, size, p);
		checkerBitmap = bitmap;
	}
	
	private void updateGradient() {
		if (trackRect.width() <= 0) return;
		int c0 = Color.argb(0, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor));
		int c1 = Color.argb(255, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor));
		trackPaint.setShader(new LinearGradient(trackRect.left, 0, trackRect.right, 0, c0, c1, Shader.TileMode.CLAMP));
	}
	
	public void setColorPickerView(AexonColorPickerView cpv) {
		this.colorPickerView = cpv; 
	}
	
	public void setUpdateFromColorPicker(int color) {
		if (!isDragging) {
			this.alpha = Color.alpha(color) / 255f;
			this.baseColor = Color.rgb(Color.red(color), Color.green(color), Color.blue(color));
			updateGradient();
			updateThumbPosition();
			invalidate();
		}
	}
}