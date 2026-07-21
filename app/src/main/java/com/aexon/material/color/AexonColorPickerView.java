package com.aexon.material.color;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.SweepGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.aexon.R;

public class AexonColorPickerView extends View {
    
	private Paint colorPaint, thumbPaint, shadowPaint, textPaint, bubblePaint, previewPaint;
	private int centerX, centerY;
	private float viewWidth, viewHeight;
	private float pointerX, pointerY;
	private float pointerSize, cornerRadius, elevation;
	private boolean showIndikator;
	private boolean autoCopy;
	private boolean isPointerPressed = false;
	private boolean isUpdatingFromSlider = false;
	
	private Path viewPath;
	private Region viewRegion;
	private RectF drawingRect;
	private ColorListener colorListener;
	private String currentHex = "#FFFFFF";
	private int selectedColorInt = Color.WHITE;
	private int selectedColorWithoutAlpha = Color.WHITE;
	
	private float indicatorX, indicatorY;
	private boolean showIndicator = false;
	
	private float currentAlpha = 1f;
	private float currentBrightness = 1f;
	private float currentHue = 0f;
	private float currentSaturation = 1f;
	
	private Typeface fontIndicator;
	
	public interface ColorListener {
		void onSelectionColor(int colorInt, String hexColor);
	}
	
	public void setColorListener(ColorListener listener) {
		this.colorListener = listener;
	}
	
	public void setPointerSize(float size) {
		this.pointerSize = size;
		textPaint.setTextSize(pointerSize * 1.5f);
		requestLayout();
		invalidate();
	}
	
	public void setCornerRadius(float radius) {
		this.cornerRadius = radius;
		requestLayout();
		invalidate();
	}
	
	public void setElevation(float elevation) {
		this.elevation = elevation;
		if (elevation > 0) {
			shadowPaint.setMaskFilter(new BlurMaskFilter(elevation, BlurMaskFilter.Blur.OUTER));
		} else {
			shadowPaint.setMaskFilter(null);
		}
		requestLayout();
		invalidate();
	}
	
	public void setShowIndicator(boolean show) {
		this.showIndikator = show;
		invalidate();
	}
	
	public void setAutoCopy(boolean autoCopy) {
		this.autoCopy = autoCopy;
	}
	
	public void setFontIndicator(Typeface typeface) {
		this.fontIndicator = typeface;
		if (textPaint != null && fontIndicator != null) {
			textPaint.setTypeface(fontIndicator);
			invalidate();
		}
	}
	
	public float getCurrentAlpha() {
		return currentAlpha;
	}
	
	public float getCurrentBrightness() {
		return currentBrightness;
	}
	
	public void updateFromSliders(float alpha, float brightness) {
		isUpdatingFromSlider = true;
		this.currentAlpha = alpha;
		this.currentBrightness = brightness;
		
		int color = Color.HSVToColor(new float[]{currentHue, currentSaturation, brightness});
		color = Color.argb((int)(alpha * 255), Color.red(color), Color.green(color), Color.blue(color));
		
		this.selectedColorInt = color;
		this.selectedColorWithoutAlpha = Color.HSVToColor(new float[]{currentHue, currentSaturation, brightness});
		this.currentHex = String.format("#%06X", (0xFFFFFF & selectedColorWithoutAlpha));
		
		if (colorListener != null) {
			colorListener.onSelectionColor(selectedColorInt, currentHex);
		}
		invalidate();
		isUpdatingFromSlider = false;
	}
	
	public void setColor(int color) {
		if (isUpdatingFromSlider) return;
		
		this.selectedColorInt = color;
		this.currentAlpha = Color.alpha(color) / 255f;
		
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		this.currentHue = hsv[0];
		this.currentSaturation = hsv[1];
		this.currentBrightness = hsv[2];
		
		this.selectedColorWithoutAlpha = Color.HSVToColor(new float[]{currentHue, currentSaturation, currentBrightness});
		this.currentHex = String.format("#%06X", (0xFFFFFF & selectedColorWithoutAlpha));
		
		float maxR = viewWidth / 2f;
		float dist = currentSaturation * maxR;
		float rad = (float) Math.toRadians(currentHue);
		pointerX = centerX + (float) (Math.cos(rad) * dist);
		pointerY = centerY + (float) (Math.sin(rad) * dist);
		
		constrainPointerToViewBounds();
		
		if (colorListener != null) {
			colorListener.onSelectionColor(selectedColorInt, currentHex);
		}
		invalidate();
	}
	
	public int getColor() {
		return selectedColorInt;
	}
	
	public String getHexColor() {
		return currentHex;
	}
	
	public AexonColorPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		float density = getResources().getDisplayMetrics().density;
		
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AexonColorPickerView, 0, 0);
		
		try {
			this.cornerRadius = a.getDimension(R.styleable.AexonColorPickerView_radius_view, 0);
			this.pointerSize = a.getDimension(R.styleable.AexonColorPickerView_size_pointer, 10 * density);
			this.elevation = a.getDimension(R.styleable.AexonColorPickerView_elevation_view, 2 * density);
			this.showIndikator = a.getBoolean(R.styleable.AexonColorPickerView_indikator_pointer, false);
			this.autoCopy = a.getBoolean(R.styleable.AexonColorPickerView_auto_copy, false);
			
			int fontResId = a.getResourceId(R.styleable.AexonColorPickerView_font_indikator, -1);
			if (fontResId != -1) {
				this.fontIndicator = getResources().getFont(fontResId);
			} else {
				TypedArray ta = context.obtainStyledAttributes(attrs, new int[]{ android.R.attr.fontFamily });
				try {
					String fontFamily = ta.getString(0);
					if (fontFamily != null) {
						this.fontIndicator = Typeface.create(fontFamily, Typeface.NORMAL);
					}
				} finally {
					ta.recycle();
				}
			}
		} finally {
			a.recycle();
		}
		init();
	}
	
	private void init() {
		colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		shadowPaint.setColor(0x40000000);
		if (elevation > 0) shadowPaint.setMaskFilter(new BlurMaskFilter(elevation, BlurMaskFilter.Blur.OUTER));
		
		thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		thumbPaint.setColor(Color.WHITE);
		thumbPaint.setStyle(Paint.Style.FILL);
		thumbPaint.setShadowLayer(8, 0, 2, 0x60000000);
		
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(pointerSize * 1.5f);
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setFakeBoldText(true);
		
		if (fontIndicator != null) {
			textPaint.setTypeface(fontIndicator);
		}
		
		bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bubblePaint.setColor(0xDD000000);
		bubblePaint.setShadowLayer(10, 0, 4, 0x80000000);
		
		previewPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		previewPaint.setStyle(Paint.Style.FILL);
		previewPaint.setShadowLayer(4, 0, 2, 0x40000000);
		
		viewPath = new Path();
		viewRegion = new Region();
		drawingRect = new RectF();
		setLayerType(LAYER_TYPE_SOFTWARE, null);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		float left = getPaddingLeft();
		float top = getPaddingTop();
		float right = w - getPaddingRight();
		float bottom = h - getPaddingBottom();
		
		drawingRect.set(left, top, right, bottom);
		
		float size = Math.min(drawingRect.width(), drawingRect.height());
		float centerLeft = left + (drawingRect.width() - size) / 2f;
		float centerTop = top + (drawingRect.height() - size) / 2f;
		drawingRect.set(centerLeft, centerTop, centerLeft + size, centerTop + size);
		
		viewWidth = drawingRect.width();
		viewHeight = drawingRect.height();
		centerX = (int) (drawingRect.left + viewWidth / 2);
		centerY = (int) (drawingRect.top + viewHeight / 2);
		
		viewPath.reset();
		viewPath.addRoundRect(drawingRect, cornerRadius, cornerRadius, Path.Direction.CW);
		
		Region clip = new Region(0, 0, w, h);
		viewRegion.setPath(viewPath, clip);
		
		int[] colors = {0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF, 0xFFFF0000};
		Shader sweep = new SweepGradient(centerX, centerY, colors, null);
		Shader radial = new RadialGradient(centerX, centerY, viewWidth / 2f, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
		colorPaint.setShader(new ComposeShader(sweep, radial, PorterDuff.Mode.SRC_OVER));
		
		if (pointerX == 0 && pointerY == 0) {
			pointerX = centerX;
			pointerY = centerY;
		} else {
			constrainPointerToViewBounds();
		}
		
		updateColorSelection();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		
		if (elevation > 0) canvas.drawPath(viewPath, shadowPaint);
		canvas.drawPath(viewPath, colorPaint);
		canvas.drawCircle(pointerX, pointerY, pointerSize, thumbPaint);
		
		canvas.restore();
		
		if (showIndikator && showIndicator) {
			drawFloatingIndicator(canvas);
		}
	}
	
	private void drawFloatingIndicator(Canvas canvas) {
		String text = String.format("#%06X", (0xFFFFFF & selectedColorWithoutAlpha));
		float textWidth = textPaint.measureText(text);
		float previewSize = pointerSize * 2f;
		float spacing = 12f;
		float bPad = 20f;
		
		float contentWidth = previewSize + spacing + textWidth;
		float bW = contentWidth + (bPad * 2);
		float bH = Math.max(previewSize, textPaint.getTextSize() * 1.5f) + (bPad * 1.5f);
		
		float margin = 20f;
		float indicatorCenterX = pointerX;
		float indicatorCenterY = pointerY;
		
		int direction = 0;
		
		if (pointerY - bH - margin < drawingRect.top) {
			direction = 2;
		} else if (pointerY + bH + margin > drawingRect.bottom) {
			direction = 3;
		} else if (pointerX - bW - margin < drawingRect.left) {
			direction = 4;
		} else if (pointerX + bW + margin > drawingRect.right) {
			direction = 5;
		} else {
			direction = 1;
		}
		
		switch (direction) {
			case 1:
			indicatorCenterY = pointerY - pointerSize - bH/2 - margin;
			break;
			case 2:
			indicatorCenterY = pointerY + pointerSize + bH/2 + margin;
			break;
			case 3:
			indicatorCenterY = pointerY - pointerSize - bH/2 - margin;
			break;
			case 4:
			indicatorCenterX = pointerX + pointerSize + bW/2 + margin;
			break;
			case 5:
			indicatorCenterX = pointerX - pointerSize - bW/2 - margin;
			break;
		}
		
		indicatorCenterX = Math.max(drawingRect.left + bW/2, Math.min(indicatorCenterX, drawingRect.right - bW/2));
		indicatorCenterY = Math.max(drawingRect.top + bH/2, Math.min(indicatorCenterY, drawingRect.bottom - bH/2));
		
		indicatorX = indicatorCenterX;
		indicatorY = indicatorCenterY;
		
		RectF bRect = new RectF(indicatorX - bW/2, indicatorY - bH/2, 
		indicatorX + bW/2, indicatorY + bH/2);
		
		float cornerRad = Math.min(bH / 3, 20f);
		canvas.drawRoundRect(bRect, cornerRad, cornerRad, bubblePaint);
		
		float contentStartX = indicatorX - contentWidth / 2;
		float contentCenterY = indicatorY;
		
		RectF previewRect = new RectF(contentStartX, contentCenterY - previewSize/2, 
		contentStartX + previewSize, contentCenterY + previewSize/2);
		
		previewPaint.setColor(selectedColorWithoutAlpha);
		canvas.drawRoundRect(previewRect, 8f, 8f, previewPaint);
		
		Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		strokePaint.setStyle(Paint.Style.STROKE);
		strokePaint.setStrokeWidth(2f);
		strokePaint.setColor(Color.WHITE);
		canvas.drawRoundRect(previewRect, 8f, 8f, strokePaint);
		
		float textStartX = contentStartX + previewSize + spacing;
		canvas.drawText(text, textStartX + textWidth/2, contentCenterY + textPaint.getTextSize() / 3, textPaint);
		
		Path tri = new Path();
		float triSize = 12f;
		
		switch (direction) {
			case 1:
			tri.moveTo(pointerX - triSize, indicatorY + bH/2);
			tri.lineTo(pointerX + triSize, indicatorY + bH/2);
			tri.lineTo(pointerX, indicatorY + bH/2 + triSize);
			break;
			case 2:
			tri.moveTo(pointerX - triSize, indicatorY - bH/2);
			tri.lineTo(pointerX + triSize, indicatorY - bH/2);
			tri.lineTo(pointerX, indicatorY - bH/2 - triSize);
			break;
			case 3:
			tri.moveTo(pointerX - triSize, indicatorY + bH/2);
			tri.lineTo(pointerX + triSize, indicatorY + bH/2);
			tri.lineTo(pointerX, indicatorY + bH/2 + triSize);
			break;
			case 4:
			tri.moveTo(indicatorX - bW/2, pointerY - triSize);
			tri.lineTo(indicatorX - bW/2, pointerY + triSize);
			tri.lineTo(indicatorX - bW/2 - triSize, pointerY);
			break;
			case 5:
			tri.moveTo(indicatorX + bW/2, pointerY - triSize);
			tri.lineTo(indicatorX + bW/2, pointerY + triSize);
			tri.lineTo(indicatorX + bW/2 + triSize, pointerY);
			break;
		}
		tri.close();
		canvas.drawPath(tri, bubblePaint);
	}
	
	private void copyToClipboard(String text) {
		ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("Color", text);
		clipboard.setPrimaryClip(clip);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float tx = event.getX();
		float ty = event.getY();
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			if (isPointInViewBounds(tx, ty)) {
				isPointerPressed = true;
				showIndicator = true;
				updatePointerPosition(tx, ty);
				invalidate();
				return true;
			}
			break;
			
			case MotionEvent.ACTION_MOVE:
			if (isPointerPressed) {
				updatePointerPosition(tx, ty);
				invalidate();
				return true;
			}
			break;
			
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			isPointerPressed = false;
			showIndicator = false;
			if (autoCopy) {
				copyToClipboard(String.format("#%06X", (0xFFFFFF & selectedColorWithoutAlpha)));
			}
			invalidate();
			return true;
		}
		
		return super.onTouchEvent(event);
	}
	
	private boolean isPointInViewBounds(float x, float y) {
		return viewRegion.contains((int)x, (int)y);
	}
	
	private void updatePointerPosition(float x, float y) {
		if (isPointInViewBounds(x, y)) {
			pointerX = x;
			pointerY = y;
		} else {
			findNearestPointInPath(x, y);
		}
		
		updateColorSelection();
	}
	
	private void findNearestPointInPath(float x, float y) {
		float bestX = pointerX;
		float bestY = pointerY;
		float bestDist = Float.MAX_VALUE;
		
		for (float angle = 0; angle < 360; angle += 3) {
			float rad = (float) Math.toRadians(angle);
			float testX = centerX + (float) (Math.cos(rad) * Math.max(viewWidth, viewHeight));
			float testY = centerY + (float) (Math.sin(rad) * Math.max(viewWidth, viewHeight));
			
			float low = 0;
			float high = (float) Math.hypot(testX - centerX, testY - centerY);
			
			for (int i = 0; i < 20; i++) {
				float mid = (low + high) / 2;
				float pointX = centerX + (float) (Math.cos(rad) * mid);
				float pointY = centerY + (float) (Math.sin(rad) * mid);
				
				if (isPointInViewBounds(pointX, pointY)) {
					low = mid;
				} else {
					high = mid;
				}
			}
			
			float edgeX = centerX + (float) (Math.cos(rad) * low);
			float edgeY = centerY + (float) (Math.sin(rad) * low);
			
			if (isPointInViewBounds(edgeX, edgeY)) {
				float dist = (float) Math.hypot(edgeX - x, edgeY - y);
				if (dist < bestDist) {
					bestDist = dist;
					bestX = edgeX;
					bestY = edgeY;
				}
			}
		}
		
		pointerX = bestX;
		pointerY = bestY;
	}
	
	private void constrainPointerToViewBounds() {
		if (!isPointInViewBounds(pointerX, pointerY)) {
			findNearestPointInPath(pointerX, pointerY);
		}
	}
	
	private void updateColorSelection() {
		float dx = pointerX - centerX;
		float dy = pointerY - centerY;
		float dist = (float) Math.sqrt(dx * dx + dy * dy);
		float maxR = viewWidth / 2f;
		
		float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
		if (angle < 0) angle += 360;
		
		float sat = Math.max(0, Math.min(dist / maxR, 1f));
		
		currentHue = angle;
		currentSaturation = sat;
		
		int color = Color.HSVToColor(new float[]{currentHue, currentSaturation, currentBrightness});
		color = Color.argb((int)(currentAlpha * 255), Color.red(color), Color.green(color), Color.blue(color));
		
		selectedColorInt = color;
		selectedColorWithoutAlpha = Color.HSVToColor(new float[]{currentHue, currentSaturation, currentBrightness});
		currentHex = String.format("#%06X", (0xFFFFFF & selectedColorWithoutAlpha));
		
		if (colorListener != null) {
			colorListener.onSelectionColor(selectedColorInt, currentHex);
		}
	}
}