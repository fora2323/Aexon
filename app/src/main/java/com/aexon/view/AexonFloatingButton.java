package com.aexon.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aexon.R;

public class AexonFloatingButton extends LinearLayout {
	
	private ImageView icon;
	
	private float radius = 0f;
	private int rippleColor = 0x33FFFFFF;
	private int iconSize = 0;
	
	public AexonFloatingButton(@NonNull Context context) {
		super(context);
		init(context, null);
	}
	
	public AexonFloatingButton(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
		setOrientation(VERTICAL);
		setGravity(Gravity.CENTER);
		
		icon = new ImageView(context);
		icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
		
		float elevation = 0f;
		Drawable iconDrawable = null;
		int iconTint = 0;
		
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AexonFloatingButton);
			
			iconDrawable = a.getDrawable(R.styleable.AexonFloatingButton_ax_fab_icon);
			iconTint = a.getColor(R.styleable.AexonFloatingButton_ax_fab_iconTint, 0);
			iconSize = (int) a.getDimension(R.styleable.AexonFloatingButton_ax_fab_iconSize, dpToPx(24));
			elevation = a.getDimension(R.styleable.AexonFloatingButton_ax_fab_elevation, 0f);
			radius = a.getDimension(R.styleable.AexonFloatingButton_ax_fab_radius, dpToPxFloat(28));
			rippleColor = a.getColor(R.styleable.AexonFloatingButton_ax_fab_rippleColor, rippleColor);
			
			a.recycle();
		} else {
			radius = dpToPxFloat(28);
			iconSize = dpToPx(24);
		}
		
		LayoutParams iconParams = new LayoutParams(iconSize, iconSize);
		icon.setLayoutParams(iconParams);
		addView(icon);
		
		if (iconDrawable != null) {
			setIcon(iconDrawable);
		}
		if (iconTint != 0) {
			setIconTint(iconTint);
		}
		
		setElevation(elevation);
		applyOutline();
		applyRipple();
		
		setClickable(true);
		setFocusable(true);
	}
	
	private int dpToPx(int dp) {
		return (int) (dp * getContext().getResources().getDisplayMetrics().density);
	}
	
	private float dpToPxFloat(int dp) {
		return dp * getContext().getResources().getDisplayMetrics().density;
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
	
	@NonNull
	private RippleDrawable buildItemRipple(@Nullable Drawable content, float radius) {
		GradientDrawable mask = new GradientDrawable();
		mask.setShape(GradientDrawable.RECTANGLE);
		mask.setCornerRadius(radius);
		mask.setColor(Color.WHITE);
		
		int targetAlpha = 0x1F;
		int finalRippleColor = Color.argb(targetAlpha, Color.red(rippleColor), Color.green(rippleColor), Color.blue(rippleColor));
		
		return new RippleDrawable(ColorStateList.valueOf(finalRippleColor), content, mask);
	}
	
	private void applyRipple() {
		Drawable currentBg = getBackground();
		if (currentBg instanceof RippleDrawable) {
			RippleDrawable currentRipple = (RippleDrawable) currentBg;
			if (currentRipple.getNumberOfLayers() > 0) {
				currentBg = currentRipple.getDrawable(0);
			}
		}
		super.setBackground(buildItemRipple(currentBg, radius));
	}
	
	@Override
	public void setBackgroundColor(@ColorInt int color) {
		GradientDrawable bg = new GradientDrawable();
		bg.setShape(GradientDrawable.RECTANGLE);
		bg.setCornerRadius(radius);
		bg.setColor(color);
		
		super.setBackground(buildItemRipple(bg, radius));
	}
	
	public void setRippleColor(@ColorInt int color) {
		this.rippleColor = color;
		applyRipple();
	}
	
	public void setRadius(float radiusPx) {
		this.radius = radiusPx;
		applyOutline();
		applyRipple();
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
	
	public void setFabElevation(float elevationPx) {
		setElevation(elevationPx);
	}
	
	public void setIconSize(int sizePx) {
		this.iconSize = sizePx;
		LayoutParams params = (LayoutParams) icon.getLayoutParams();
		params.width = sizePx;
		params.height = sizePx;
		icon.setLayoutParams(params);
	}
}