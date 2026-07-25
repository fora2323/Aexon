package com.aexon.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aexon.R;

public class AexonButton extends Button {
	
	public static final int ICON_GRAVITY_START = 0x1;
	public static final int ICON_GRAVITY_TEXT_START = 0x2;
	public static final int ICON_GRAVITY_END = 0x3;
	public static final int ICON_GRAVITY_TEXT_END = 0x4;
	
	private float cornerRadius = 0f;
	private int iconGravity = ICON_GRAVITY_START;
	private float iconPadding = 0f;
	private int iconTint = Integer.MIN_VALUE;
	private int iconSize = 0;
	private int iconLeft = 0;
	
	@Nullable
	private Drawable icon = null;
	
	private int bgColor = Color.TRANSPARENT;
	private int rippleColor = Color.argb(40, 255, 255, 255);
	private int strokeColor = Color.TRANSPARENT;
	private float strokeWidth = 0f;
	
	public AexonButton(@NonNull Context context) {
		super(context);
		init(context, null);
	}
	
	public AexonButton(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	public AexonButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}
	
	private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
		setAllCaps(false);
		setMinimumWidth(dpToPx(context, 88f));
		setMinimumHeight(dpToPx(context, 36f));
		setIncludeFontPadding(false);
		setGravity(Gravity.CENTER);
		
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AexonButton);
			cornerRadius = a.getDimension(R.styleable.AexonButton_cornerRadius, 0f);
			iconGravity = a.getInt(R.styleable.AexonButton_iconGravity, ICON_GRAVITY_START);
			iconPadding = a.getDimension(R.styleable.AexonButton_iconPadding, dpToPx(context, 8f));
			iconTint = a.getColor(R.styleable.AexonButton_iconTint, Integer.MIN_VALUE);
			iconSize = (int) a.getDimension(R.styleable.AexonButton_iconSize, 0f);
			icon = a.getDrawable(R.styleable.AexonButton_ax_icon);
			bgColor = a.getColor(R.styleable.AexonButton_buttonColor, Color.TRANSPARENT);
			rippleColor = a.getColor(R.styleable.AexonButton_rippleColor, Color.argb(40, 255, 255, 255));
			strokeColor = a.getColor(R.styleable.AexonButton_strokeColor, Color.TRANSPARENT);
			strokeWidth = a.getDimension(R.styleable.AexonButton_strokeWidth, 0f);
			a.recycle();
		}
		
		if (bgColor == Color.TRANSPARENT) {
			Drawable bg = getBackground();
			if (bg instanceof ColorDrawable) {
				bgColor = ((ColorDrawable) bg).getColor();
			}
		}
		
		setCompoundDrawablePadding((int) iconPadding);
		applyBackground();
		updateIcon(true);
	}
	
	private void applyBackground() {
		GradientDrawable shape = new GradientDrawable();
		shape.setColor(bgColor);
		shape.setCornerRadius(cornerRadius);
		
		if (strokeWidth > 0 && strokeColor != Color.TRANSPARENT) {
			shape.setStroke((int) strokeWidth, strokeColor);
		}
		
		GradientDrawable mask = new GradientDrawable();
		mask.setShape(GradientDrawable.RECTANGLE);
		mask.setColor(Color.WHITE);
		mask.setCornerRadius(cornerRadius);
		
		RippleDrawable ripple = new RippleDrawable(ColorStateList.valueOf(rippleColor), shape, mask);
		setBackground(ripple);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			setOutlineProvider(new ViewOutlineProvider() {
				@Override
				public void getOutline(View view, Outline outline) {
					if (view.getWidth() > 0 && view.getHeight() > 0) {
						outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), cornerRadius);
					}
				}
			});
			setClipToOutline(true);
			invalidateOutline();
		}
	}
	
	private void updateIcon(boolean needsIconReset) {
		if (icon != null) {
			icon = icon.mutate();
			
			int width = iconSize != 0 ? iconSize : icon.getIntrinsicWidth();
			int height = iconSize != 0 ? iconSize : icon.getIntrinsicHeight();
			icon.setBounds(iconLeft, 0, iconLeft + width, height);
			
			if (iconTint != Integer.MIN_VALUE) {
				icon.setColorFilter(iconTint, PorterDuff.Mode.SRC_IN);
			} else {
				icon.clearColorFilter();
			}
		}
		
		if (needsIconReset) {
			resetIconDrawable();
		}
	}
	
	private void resetIconDrawable() {
		if (iconGravity == ICON_GRAVITY_START || iconGravity == ICON_GRAVITY_TEXT_START) {
			setCompoundDrawablesRelative(icon, null, null, null);
		} else if (iconGravity == ICON_GRAVITY_END || iconGravity == ICON_GRAVITY_TEXT_END) {
			setCompoundDrawablesRelative(null, null, icon, null);
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		updateIconPosition(w, h);
	}
	
	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		updateIconPosition(getMeasuredWidth(), getMeasuredHeight());
	}
	
	private void updateIconPosition(int buttonWidth, int buttonHeight) {
		if (icon == null || getLayout() == null) return;
		
		if (iconGravity == ICON_GRAVITY_TEXT_START || iconGravity == ICON_GRAVITY_TEXT_END) {
			int localIconSize = iconSize == 0 ? icon.getIntrinsicWidth() : iconSize;
			
			int paddingStart = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ? getPaddingStart() : getPaddingLeft();
			int paddingEnd = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ? getPaddingEnd() : getPaddingRight();
			
			int newIconLeft = (buttonWidth - getTextWidth() - paddingEnd - localIconSize - (int) iconPadding - paddingStart) / 2;
			
			boolean isRTL = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
			
			if (isRTL != (iconGravity == ICON_GRAVITY_TEXT_END)) {
				newIconLeft = -newIconLeft;
			}
			
			newIconLeft = Math.max(0, newIconLeft);
			
			if (iconLeft != newIconLeft) {
				iconLeft = newIconLeft;
				updateIcon(false);
			}
		} else {
			if (iconLeft != 0) {
				iconLeft = 0;
				updateIcon(false);
			}
		}
	}
	
	private int getTextWidth() {
		Paint textPaint = getPaint();
		String text = getText().toString();
		if (getTransformationMethod() != null) {
			text = getTransformationMethod().getTransformation(text, this).toString();
		}
		if (getLayout() != null) {
			return Math.min((int) textPaint.measureText(text), getLayout().getEllipsizedWidth());
		} else {
			return (int) textPaint.measureText(text);
		}
	}
	
	public void setIcon(@Nullable Drawable drawable) {
		if (this.icon != drawable) {
			this.icon = drawable;
			updateIcon(true);
			updateIconPosition(getMeasuredWidth(), getMeasuredHeight());
		}
	}
	
	public void setIcon(int resId) {
		if (resId != 0) {
			Drawable drawable;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				drawable = getContext().getDrawable(resId);
			} else {
				drawable = getContext().getResources().getDrawable(resId);
			}
			setIcon(drawable);
		} else {
			setIcon((Drawable) null);
		}
	}
	
	public void setCornerRadius(float radius) {
		this.cornerRadius = radius;
		applyBackground();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			invalidateOutline();
		}
	}
	
	public void setIconTint(int color) {
		this.iconTint = color;
		updateIcon(false);
	}
	
	@Override
	public void setBackgroundColor(int color) {
		this.bgColor = color;
		applyBackground();
	}
	
	public void setIconSize(int sizePx) {
		this.iconSize = sizePx;
		updateIcon(true);
	}
	
	public void setRippleColor(int color) {
		this.rippleColor = color;
		applyBackground();
	}
	
	public void setStroke(float width, int color) {
		this.strokeWidth = width;
		this.strokeColor = color;
		applyBackground();
	}
	
	public void setStrokeColor(int color) {
		this.strokeColor = color;
		applyBackground();
	}
	
	public void setStrokeWidth(float width) {
		this.strokeWidth = width;
		applyBackground();
	}
	
	public void setIconGravity(int gravity) {
		if (this.iconGravity != gravity) {
			this.iconGravity = gravity;
			updateIconPosition(getMeasuredWidth(), getMeasuredHeight());
		}
	}
	
	private int dpToPx(Context context, float dp) {
		return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
	}
}
