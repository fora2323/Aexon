package com.aexon.material.edittext;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import java.lang.reflect.Field;

import com.aexon.annotation.NonNull;
import com.aexon.annotation.Nullable;
import com.aexon.annotation.RequiresApi;
import com.aexon.theme.AexonTheme;
import com.aexon.theme.AexonThemeListener;
import com.aexon.R;

public class AexonEditText extends EditText {
	
	public static final int MODE_DEFAULT = 0;
	public static final int MODE_FILLED = 1;
	
	private int mode = MODE_DEFAULT;
	private boolean isErrorState = false;
	
	private Paint labelPaint;
	private float labelY;
	private float labelStartY;
	private float labelEndY;
	private float labelStartSize;
	private float labelEndSize;
	private float labelAnimFraction = 0f;
	private boolean isFloating = false;
	private ValueAnimator labelAnimator;
	private String hintText = "";
	private int activeColor;
	private int inactiveColor;
	
	private int paddingHori;
	private int normalPadTop, normalPadBottom;
	private int filledPadTop, filledPadBottom;
	
	private final AexonThemeListener themeListener = (seedColor, theme) -> applyTheme(theme);
	
	public AexonEditText(@NonNull Context context) {
		super(context);
		init(context, null);
	}
	
	public AexonEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	public AexonEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AexonEditText);
			mode = a.getInt(R.styleable.AexonEditText_aexon_mode, MODE_DEFAULT);
			a.recycle();
		}
		
		if (mode == MODE_FILLED) {
			hintText = getHint() != null ? getHint().toString() : "";
			setHint("");
			
			labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			labelStartSize = getTextSize();
			labelEndSize = labelStartSize * (10f / 13f);
			labelPaint.setTextSize(labelStartSize);
			labelPaint.setTypeface(getTypeface());
			
			paddingHori = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
			normalPadTop = getPaddingTop();
			normalPadBottom = getPaddingBottom();
			filledPadTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
			filledPadBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
			
			setPadding(paddingHori, normalPadTop, paddingHori, normalPadBottom);
		}
		
		applyTheme(AexonTheme.getInstance());
		
		if (mode == MODE_FILLED) {
			labelEndY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics());
			labelStartY = 0;
			labelY = labelStartY;
			
			if (getText() != null && getText().length() > 0) {
				labelAnimFraction = 1f;
				labelY = labelEndY;
				labelPaint.setTextSize(labelEndSize);
				isFloating = true;
				setPadding(paddingHori, filledPadTop, paddingHori, filledPadBottom);
			}
		}
		
		setOnFocusChangeListener((v, hasFocus) -> {
			if (mode == MODE_FILLED) {
				if (hasFocus && !isFloating) {
					animateLabel(true);
				} else if (!hasFocus && (getText() == null || getText().length() == 0)) {
					animateLabel(false);
				}
			}
			applyTheme(AexonTheme.getInstance());
		});
	}
	
	public void setMode(int mode) {
		this.mode = mode;
		init(getContext(), null);
		invalidate();
	}
	
	public int getMode() {
		return mode;
	}
	
	@Override
	public void setTypeface(Typeface tf) {
		super.setTypeface(tf);
		if (labelPaint != null) {
			labelPaint.setTypeface(tf);
			invalidate();
		}
	}
	
	@Override
	public void setTypeface(Typeface tf, int style) {
		super.setTypeface(tf, style);
		if (labelPaint != null) {
			labelPaint.setTypeface(getTypeface());
			invalidate();
		}
	}
	
	private void animateLabel(boolean toFloat) {
		if (labelAnimator != null) labelAnimator.cancel();
		
		if (!toFloat || labelAnimFraction == 0f) {
			labelStartY = getHeight() / 2f + labelStartSize / 3f;
		}
		
		float from = labelAnimFraction;
		float to = toFloat ? 1f : 0f;
		labelAnimator = ValueAnimator.ofFloat(from, to);
		labelAnimator.setDuration(220);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			labelAnimator.setInterpolator(new PathInterpolator(0.2f, 0f, 0f, 1f));
		} else {
			labelAnimator.setInterpolator(new DecelerateInterpolator());
		}
		labelAnimator.addUpdateListener(anim -> {
			labelAnimFraction = (float) anim.getAnimatedValue();
			labelY = labelStartY + (labelEndY - labelStartY) * labelAnimFraction;
			float size = labelStartSize + (labelEndSize - labelStartSize) * labelAnimFraction;
			labelPaint.setTextSize(size);
			
			int padTop = (int) (normalPadTop + (filledPadTop - normalPadTop) * labelAnimFraction);
			int padBottom = (int) (normalPadBottom + (filledPadBottom - normalPadBottom) * labelAnimFraction);
			setPadding(paddingHori, padTop, paddingHori, padBottom);
			
			invalidate();
		});
		labelAnimator.start();
		isFloating = toFloat;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (mode == MODE_FILLED) {
			if (labelAnimFraction == 0f && !isFloating) {
				labelY = getHeight() / 2f + labelPaint.getTextSize() / 3f;
			}
			
			Drawable bg = getBackground();
			boolean bgTransparent = (bg == null) || (bg instanceof ColorDrawable && ((ColorDrawable) bg).getColor() == Color.TRANSPARENT);
			
			if (!bgTransparent) {
				int w = getWidth();
				int h = getHeight();
				float strokeH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, isFocused() ? 2 : 1, getResources().getDisplayMetrics());
				
				Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				linePaint.setColor(isErrorState ? activeColor : (isFocused() ? activeColor : inactiveColor));
				linePaint.setStrokeWidth(strokeH);
				canvas.drawLine(0, h - strokeH, w, h - strokeH, linePaint);
			}
			
			int labelColor = isErrorState ? activeColor : (isFocused() || isFloating ? activeColor : inactiveColor);
			labelPaint.setColor(labelColor);
			labelPaint.setTypeface(getTypeface());
			int paddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
			canvas.drawText(hintText, paddingLeft, labelY, labelPaint);
		}
		
		super.onDraw(canvas);
	}
	
	@Override
	public void setError(CharSequence error) {
		super.setError(error);
		checkErrorState();
	}
	
	@Override
	public void setError(CharSequence error, Drawable icon) {
		super.setError(error, icon);
		checkErrorState();
	}
	
	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		if (isErrorState && text != null && text.length() > 0) {
			setError(null);
		} else {
			checkErrorState();
		}
		if (mode == MODE_FILLED) {
			if (text != null && text.length() > 0 && !isFloating) {
				animateLabel(true);
			} else if ((text == null || text.length() == 0) && isFloating && !isFocused()) {
				animateLabel(false);
			}
		}
	}
	
	private void checkErrorState() {
		boolean current = getError() != null;
		if (isErrorState != current) {
			isErrorState = current;
			applyTheme(AexonTheme.getInstance());
		}
	}
	
	private void applyTheme(AexonTheme theme) {
		if (theme == null) return;
		activeColor = isErrorState ? theme.getColorError() : theme.getColorPrimary();
		inactiveColor = theme.getColorOnSurfaceVariant();
		
		setTextColor(theme.getColorOnSurface());
		if (mode == MODE_DEFAULT) setHintTextColor(theme.getColorOnSurfaceVariant());
		setHighlightColor(activeColor & 0x33FFFFFF);
		
		applyCursorColor(activeColor);
		applyHandleColor(activeColor);
		
		if (mode == MODE_DEFAULT) applyUnderlineColor(activeColor);
	}
	
	@RequiresApi(RequiresApi.Q)
	private void applyCursorColorApi29(int color) {
		Drawable cursor = getTextCursorDrawable();
		if (cursor != null) {
			cursor = cursor.mutate();
			cursor.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_IN));
			setTextCursorDrawable(cursor);
		} else {
			GradientDrawable gd = new GradientDrawable();
			gd.setShape(GradientDrawable.RECTANGLE);
			gd.setColor(color);
			int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
			float r = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, getResources().getDisplayMetrics());
			gd.setSize(width, 0);
			gd.setCornerRadius(r);
			setTextCursorDrawable(gd);
		}
	}
	
	private void applyCursorColor(int color) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			applyCursorColorApi29(color);
		} else {
			try {
				Field fRes = TextView.class.getDeclaredField("mCursorDrawableRes");
				fRes.setAccessible(true);
				int resId = fRes.getInt(this);
				Field fEditor = TextView.class.getDeclaredField("mEditor");
				fEditor.setAccessible(true);
				Object editor = fEditor.get(this);
				if (editor == null) return;
				Field fCursor = editor.getClass().getDeclaredField("mCursorDrawable");
				fCursor.setAccessible(true);
				Drawable d0 = getContext().getDrawable(resId);
				Drawable d1 = getContext().getDrawable(resId);
				if (d0 != null) d0.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
				if (d1 != null) d1.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
				fCursor.set(editor, new Drawable[]{d0, d1});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@RequiresApi(RequiresApi.Q)
	private void applyHandleColorApi29(int color) {
		Drawable left = getTextSelectHandleLeft();
		if (left != null) {
			left = left.mutate();
			left.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_IN));
			setTextSelectHandleLeft(left); 
		}
		Drawable right = getTextSelectHandleRight();
		if (right != null) {
			right = right.mutate();
			right.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_IN)); 
			setTextSelectHandleRight(right); 
		}
		Drawable center = getTextSelectHandle();
		if (center != null) { 
			center = center.mutate();
			center.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_IN)); 
			setTextSelectHandle(center); 
		}
	}
	
	private void applyHandleColor(int color) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			applyHandleColorApi29(color);
		} else {
			try {
				Field fEditor = TextView.class.getDeclaredField("mEditor");
				fEditor.setAccessible(true);
				Object editor = fEditor.get(this);
				if (editor == null) return;
				Class<?> clazz = editor.getClass();
				String[] fields = {"mSelectHandleLeft", "mSelectHandleRight", "mSelectHandleCenter"};
				String[] resFields = {"mTextSelectHandleLeftRes", "mTextSelectHandleRightRes", "mTextSelectHandleRes"};
				for (int i = 0; i < 3; i++) {
					Field f = clazz.getDeclaredField(fields[i]);
					f.setAccessible(true);
					Drawable d = (Drawable) f.get(editor);
					if (d == null) {
						Field fRes = TextView.class.getDeclaredField(resFields[i]);
						fRes.setAccessible(true);
						d = getContext().getDrawable(fRes.getInt(this));
						if (d != null) f.set(editor, d);
					}
					if (d != null) d.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void applyUnderlineColor(int color) {
		Drawable bg = getBackground();
		if (bg != null) {
			bg = bg.mutate();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				bg.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_IN));
			} else {
				bg.setColorFilter(color, PorterDuff.Mode.SRC_IN);
			}
		}
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		AexonTheme.getInstance().addListener(themeListener);
		applyTheme(AexonTheme.getInstance());
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		AexonTheme.getInstance().removeListener(themeListener);
	}
}