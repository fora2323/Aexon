package com.aexon;

import android.content.res.ColorStateList;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;


import com.aexon.annotation.NonNull;
import com.aexon.annotation.Nullable;
import com.aexon.annotation.RequiresApi;

public class AexonDrawable {
	
	private final float cornerRadius;
	private final float cornerRadiusTopLeft;
	private final float cornerRadiusTopRight;
	private final float cornerRadiusBottomRight;
	private final float cornerRadiusBottomLeft;
	private final int startColor;
	private final int endColor;
	private final boolean useGradient;
	private final boolean useRipple;
	private final int rippleColor;
	private final boolean autoRippleColor;
	private final float strokeWidth;
	private final int strokeColor;
	private final float strokeDashWidth;
	private final float strokeDashGap;
	
	private static final int DEFAULT_RIPPLE_ALPHA_LIGHT = 0x1F;
	private static final int DEFAULT_RIPPLE_ALPHA_DARK = 0x1F;
	private static final double LUMINANCE_THRESHOLD = 0.5;
	private static final double SRGB_THRESHOLD = 0.03928;
	private static final double SRGB_DIVISOR = 12.92;
	private static final double SRGB_OFFSET = 0.055;
	private static final double SRGB_GAMMA_DIVISOR = 1.055;
	private static final double GAMMA_EXPONENT = 2.4;
	private static final double LUMINANCE_RED_WEIGHT = 0.2126;
	private static final double LUMINANCE_GREEN_WEIGHT = 0.7152;
	private static final double LUMINANCE_BLUE_WEIGHT = 0.0722;
	
	private AexonDrawable(Builder builder) {
		this.cornerRadius = builder.cornerRadius;
		this.cornerRadiusTopLeft = builder.cornerRadiusTopLeft;
		this.cornerRadiusTopRight = builder.cornerRadiusTopRight;
		this.cornerRadiusBottomRight = builder.cornerRadiusBottomRight;
		this.cornerRadiusBottomLeft = builder.cornerRadiusBottomLeft;
		this.startColor = builder.startColor;
		this.endColor = builder.endColor;
		this.useGradient = builder.useGradient;
		this.useRipple = builder.useRipple;
		this.rippleColor = builder.rippleColor;
		this.autoRippleColor = builder.autoRippleColor;
		this.strokeWidth = builder.strokeWidth;
		this.strokeColor = builder.strokeColor;
		this.strokeDashWidth = builder.strokeDashWidth;
		this.strokeDashGap = builder.strokeDashGap;
	}
	
	public static class Builder {
		private float cornerRadius;
		private float cornerRadiusTopLeft;
		private float cornerRadiusTopRight;
		private float cornerRadiusBottomRight;
		private float cornerRadiusBottomLeft;
		private int startColor;
		private int endColor;
		private boolean useGradient;
		private boolean useRipple;
		private int rippleColor;
		private boolean autoRippleColor = true;
		private float strokeWidth;
		private int strokeColor;
		private float strokeDashWidth;
		private float strokeDashGap;
		
		public Builder(int color) {
			this.startColor = color;
		}
		
		public Builder(int startColor, int endColor) {
			this.startColor = startColor;
			this.endColor = endColor;
			this.useGradient = true;
		}
		
		public Builder cornerRadius(float dp) {
			this.cornerRadius = dp;
			return this;
		}
		
		public Builder cornerRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
			this.cornerRadiusTopLeft = topLeft;
			this.cornerRadiusTopRight = topRight;
			this.cornerRadiusBottomRight = bottomRight;
			this.cornerRadiusBottomLeft = bottomLeft;
			return this;
		}
		
		public Builder ripple() {
			this.useRipple = true;
			return this;
		}
		
		public Builder ripple(int rippleColor) {
			this.useRipple = true;
			this.rippleColor = rippleColor;
			this.autoRippleColor = false;
			return this;
		}
		
		public Builder stroke(float widthDp, int color) {
			this.strokeWidth = widthDp;
			this.strokeColor = color;
			return this;
		}
		
		public Builder stroke(float widthDp, int color, float dashWidthDp, float dashGapDp) {
			this.strokeWidth = widthDp;
			this.strokeColor = color;
			this.strokeDashWidth = dashWidthDp;
			this.strokeDashGap = dashGapDp;
			return this;
		}
		
		public AexonDrawable build() {
			return new AexonDrawable(this);
		}
	}
	
	@NonNull
	public Drawable build(@NonNull android.content.Context context) {
		GradientDrawable gd = createBaseDrawable(context);
		applyCornerRadii(context, gd);
		applyStroke(context, gd);
		
		if (useRipple) {
			int finalRippleColor = resolveRippleColor();
			return createRippleDrawable(finalRippleColor, gd);
		}
		return gd;
	}
	
	@NonNull
	public static Drawable oval(@NonNull android.content.Context context, int surfaceColor) {
		GradientDrawable mask = new GradientDrawable();
		mask.setShape(GradientDrawable.OVAL);
		mask.setColor(Color.WHITE);
		
		double luminance = calculateLuminance(surfaceColor);
		int targetAlpha = (luminance > LUMINANCE_THRESHOLD) ? DEFAULT_RIPPLE_ALPHA_DARK : DEFAULT_RIPPLE_ALPHA_LIGHT;
		int baseColor = (luminance > LUMINANCE_THRESHOLD) ? 0 : 255;
		int rippleColor = Color.argb(targetAlpha, baseColor, baseColor, baseColor);
		
		return new RippleDrawable(ColorStateList.valueOf(rippleColor), null, mask);
	}
	
	@NonNull
	public static Drawable fadeEdge(@NonNull Context context, int color) {
		int startColor = color & 0x00FFFFFF;
		int endColor = (100 << 24) | (color & 0x00FFFFFF);
		GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{startColor, endColor});
		
		GradientDrawable mask = new GradientDrawable();
		mask.setColor(Color.WHITE);
		
		double luminance = calculateLuminance(color);
		int targetAlpha = (luminance > LUMINANCE_THRESHOLD) ? DEFAULT_RIPPLE_ALPHA_DARK : DEFAULT_RIPPLE_ALPHA_LIGHT;
		int baseColor = (luminance > LUMINANCE_THRESHOLD) ? 0 : 255;
		int rippleColor = Color.argb(targetAlpha, baseColor, baseColor, baseColor);
		
		return new RippleDrawable(ColorStateList.valueOf(rippleColor), gd, mask);
	}
	
	@NonNull
	public static Drawable fadeEdge(@NonNull Context context, int gradientColor, int rippleBaseColor) {
		int startColor = gradientColor & 0x00FFFFFF;
		int endColor = (100 << 24) | (gradientColor & 0x00FFFFFF);
		GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{startColor, endColor});
		
		GradientDrawable mask = new GradientDrawable();
		mask.setColor(Color.WHITE);
		
		double luminance = calculateLuminance(rippleBaseColor);
		int targetAlpha = (luminance > LUMINANCE_THRESHOLD) ? DEFAULT_RIPPLE_ALPHA_DARK : DEFAULT_RIPPLE_ALPHA_LIGHT;
		int baseColor = (luminance > LUMINANCE_THRESHOLD) ? 0 : 255;
		int rippleColor = Color.argb(targetAlpha, baseColor, baseColor, baseColor);
		
		return new RippleDrawable(ColorStateList.valueOf(rippleColor), gd, mask);
	}
	
	@NonNull
	private GradientDrawable createBaseDrawable(@NonNull Context context) {
		GradientDrawable gd;
		if (useGradient) {
			gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{startColor, endColor});
		} else {
			gd = new GradientDrawable();
			gd.setColor(startColor);
		}
		return gd;
	}
	
	private void applyCornerRadii(@NonNull android.content.Context context, @NonNull GradientDrawable gd) {
		if (cornerRadiusTopLeft > 0 || cornerRadiusTopRight > 0 || cornerRadiusBottomRight > 0 || cornerRadiusBottomLeft > 0) {
			float tl = dipToPx(context, cornerRadiusTopLeft);
			float tr = dipToPx(context, cornerRadiusTopRight);
			float br = dipToPx(context, cornerRadiusBottomRight);
			float bl = dipToPx(context, cornerRadiusBottomLeft);
			gd.setCornerRadii(new float[]{tl, tl, tr, tr, br, br, bl, bl});
		} else if (cornerRadius > 0) {
			float radius = dipToPx(context, cornerRadius);
			gd.setCornerRadius(radius);
		}
	}
	
	private void applyStroke(@NonNull android.content.Context context, @NonNull GradientDrawable gd) {
		if (strokeWidth > 0) {
			float widthPx = dipToPx(context, strokeWidth);
			if (strokeDashWidth > 0 && strokeDashGap > 0) {
				float dashPx = dipToPx(context, strokeDashWidth);
				float gapPx = dipToPx(context, strokeDashGap);
				gd.setStroke((int) widthPx, strokeColor, dashPx, gapPx);
			} else {
				gd.setStroke((int) widthPx, strokeColor);
			}
		}
	}
	
	private int resolveRippleColor() {
		double luminance = calculateLuminance(startColor);
		int targetAlpha = (luminance > LUMINANCE_THRESHOLD) ? DEFAULT_RIPPLE_ALPHA_DARK : DEFAULT_RIPPLE_ALPHA_LIGHT;
		
		if (autoRippleColor) {
			int baseColor = (luminance > LUMINANCE_THRESHOLD) ? 0 : 255;
			return Color.argb(targetAlpha, baseColor, baseColor, baseColor);
		}
		
		return Color.argb(targetAlpha, Color.red(rippleColor), Color.green(rippleColor), Color.blue(rippleColor));
	}
	
	@NonNull
	private Drawable createRippleDrawable(int rippleColor, @NonNull GradientDrawable contentDrawable) {
		return new RippleDrawable(ColorStateList.valueOf(rippleColor), contentDrawable, contentDrawable);
	}
	
	private static double calculateLuminance(int color) {
		double r = Color.red(color) / 255.0;
		double g = Color.green(color) / 255.0;
		double b = Color.blue(color) / 255.0;
		
		r = linearizeSRGB(r);
		g = linearizeSRGB(g);
		b = linearizeSRGB(b);
		
		return LUMINANCE_RED_WEIGHT * r + LUMINANCE_GREEN_WEIGHT * g + LUMINANCE_BLUE_WEIGHT * b;
	}
	
	private static double linearizeSRGB(double component) {
		if (component <= SRGB_THRESHOLD) {
			return component / SRGB_DIVISOR;
		}
		return Math.pow((component + SRGB_OFFSET) / SRGB_GAMMA_DIVISOR, GAMMA_EXPONENT);
	}
	
	private float dipToPx(@NonNull android.content.Context context, float dp) {
		return android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}
}