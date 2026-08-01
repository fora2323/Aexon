package com.aexon.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aexon.R;

public class AexonCompatButton extends LinearLayout {
	
	private ImageView icon;
	private TextView text;
	
	private float radius;
	@ColorInt
	private int backgroundColor = Color.TRANSPARENT;
	@ColorInt
	private int rippleColor = 0x33000000;
	private int strokeWidth = 0;
	@ColorInt
	private int strokeColor = Color.TRANSPARENT;
	
	@Nullable
	private OnClickListener btnClickListener;
	
	public AexonCompatButton(@NonNull Context context) {
		super(context);
		init(context, null);
	}
	
	public AexonCompatButton(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);
		
		LayoutInflater.from(context).inflate(R.layout.aexon_button, this, true);
		
		icon = findViewById(R.id.imageview1);
		text = findViewById(R.id.textview1);
		
		radius = dp(20);
		
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AexonCompatButton);
			
			Drawable iconDrawable = a.getDrawable(R.styleable.AexonCompatButton_ax_btn_icon);
			String btnText = a.getString(R.styleable.AexonCompatButton_ax_btn_text);
			float textSize = a.getDimension(R.styleable.AexonCompatButton_ax_btn_textSize, text.getTextSize());
			int textColor = a.getColor(R.styleable.AexonCompatButton_ax_btn_textColor, text.getCurrentTextColor());
			int iconTint = a.getColor(R.styleable.AexonCompatButton_ax_btn_iconTint, 0);
			int iconSize = (int) a.getDimension(R.styleable.AexonCompatButton_ax_btn_iconSize, dp(24));
			radius = a.getDimension(R.styleable.AexonCompatButton_ax_btn_radius, radius);
			backgroundColor = a.getColor(R.styleable.AexonCompatButton_ax_btn_backgroundColor, backgroundColor);
			rippleColor = a.getColor(R.styleable.AexonCompatButton_ax_btn_rippleColor, rippleColor);
			strokeWidth = (int) a.getDimension(R.styleable.AexonCompatButton_ax_btn_strokeWidth, 0);
			strokeColor = a.getColor(R.styleable.AexonCompatButton_ax_btn_strokeColor, Color.TRANSPARENT);
			
			if (iconDrawable != null) setIcon(iconDrawable);
			if (btnText != null) setText(btnText);
			
			setTextSize(textSize);
			setTextColor(textColor);
			if (iconTint != 0) setIconTint(iconTint);
			setIconSize(iconSize);
			
			a.recycle();
		}
		
		applyOutline();
		applyBackground();
		
		setClickable(true);
		setFocusable(true);
		
		super.setOnClickListener(v -> {
			if (btnClickListener != null) {
				btnClickListener.onClick(v);
			}
		});
	}
	
	private float dp(int value) {
		return value * getContext().getResources().getDisplayMetrics().density;
	}
	
	private void applyOutline() {
		setOutlineProvider(new ViewOutlineProvider() {
			@Override
			public void getOutline(View view, Outline outline) {
				outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
			}
		});
		setClipToOutline(true);
	}
	
	private void applyBackground() {
		GradientDrawable bg = new GradientDrawable();
		bg.setShape(GradientDrawable.RECTANGLE);
		bg.setCornerRadius(radius);
		bg.setColor(backgroundColor);
		
		if (strokeWidth > 0) {
			bg.setStroke(strokeWidth, strokeColor);
		}
		
		GradientDrawable mask = new GradientDrawable();
		mask.setShape(GradientDrawable.RECTANGLE);
		mask.setCornerRadius(radius);
		mask.setColor(Color.WHITE);
		
		int targetAlpha = 0x1F;
		int finalRippleColor = Color.argb(targetAlpha, Color.red(rippleColor), Color.green(rippleColor), Color.blue(rippleColor));
		
		RippleDrawable ripple = new RippleDrawable(ColorStateList.valueOf(finalRippleColor), bg, mask);
		setBackground(ripple);
	}
	
	@Override
	public void setOnClickListener(@Nullable OnClickListener listener) {
		this.btnClickListener = listener;
	}
	
	public void setIcon(@DrawableRes int resId) {
		icon.setImageDrawable(getContext().getDrawable(resId));
	}
	
	public void setIcon(@NonNull Drawable drawable) {
		icon.setImageDrawable(drawable);
	}
	
	public void setIconTint(@ColorInt int color) {
		icon.setImageTintList(ColorStateList.valueOf(color));
	}
	
	public void setIconSize(int sizePx) {
		LayoutParams params = (LayoutParams) icon.getLayoutParams();
		params.width = sizePx;
		params.height = sizePx;
		icon.setLayoutParams(params);
	}
	
	public void setText(@NonNull String value) {
		text.setText(value);
	}
	
	public void setTextSize(float sizePx) {
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizePx);
	}
	
	public void setTextColor(@ColorInt int color) {
		text.setTextColor(color);
	}
	
	public void setButtonBackgroundColor(@ColorInt int color) {
		this.backgroundColor = color;
		applyBackground();
	}
	
	public void setButtonRippleColor(@ColorInt int color) {
		this.rippleColor = color;
		applyBackground();
	}
	
	public void setButtonRadius(float radiusPx) {
		this.radius = radiusPx;
		applyOutline();
		applyBackground();
	}
	
	public void setButtonStroke(int strokeWidthPx, @ColorInt int color) {
		this.strokeWidth = strokeWidthPx;
		this.strokeColor = color;
		applyBackground();
	}
	
	public void setButtonStrokeWidth(int strokeWidthPx) {
		this.strokeWidth = strokeWidthPx;
		applyBackground();
	}
	
	public void setButtonStrokeColor(@ColorInt int color) {
		this.strokeColor = color;
		applyBackground();
	}
}