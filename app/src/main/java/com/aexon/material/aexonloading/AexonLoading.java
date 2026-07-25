package com.aexon.material.aexonloading;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.aexon.R;
import com.aexon.annotation.NonNull;
import com.aexon.annotation.Nullable;

public class AexonLoading extends View {
	
	private Drawable iconDrawable;
	private Drawable trackDrawable;
	private Drawable thumbDrawable;
	
	private int trackColor;
	private int thumbColor;
	
	private float progress = 0f;
	private ValueAnimator animator;
	
	private OnAexonLoadingListener listener;
	
	private long defaultDuration = 1000;
	
	private final Rect iconBounds = new Rect();
	private final Rect clipBounds = new Rect();
	
	private boolean isAnimating = false;
	private boolean indeterminate = false;
	
	public interface OnAexonLoadingListener {
		void complete();
	}
	
	public AexonLoading(@NonNull Context context) {
		super(context);
		init(context, null);
	}
	
	public AexonLoading(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	public AexonLoading(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}
	
	private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
		trackColor = Color.parseColor("#E0E0E0");
		thumbColor = Color.parseColor("#2196F3");
		
		int iconResId = R.drawable.ic_bolt_round;
		
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AexonLoading);
			trackColor = a.getColor(R.styleable.AexonLoading_track_color, trackColor);
			thumbColor = a.getColor(R.styleable.AexonLoading_thumb_color, thumbColor);
			iconResId = a.getResourceId(R.styleable.AexonLoading_icon, R.drawable.ic_bolt_round);
			indeterminate = a.getBoolean(R.styleable.AexonLoading_indeterminate, false);
			defaultDuration = a.getInt(R.styleable.AexonLoading_duration, 1000);
			a.recycle();
		}
		
		setIconInternal(context.getResources().getDrawable(iconResId));
		setBackgroundColor(Color.TRANSPARENT);
		
		if (indeterminate) startIndeterminate();
	}
	
	private void setIconInternal(Drawable drawable) {
		if (drawable != null) {
			this.iconDrawable = drawable;
			this.trackDrawable = iconDrawable.getConstantState().newDrawable().mutate();
			this.thumbDrawable = iconDrawable.getConstantState().newDrawable().mutate();
		}
	}
	
	public void setTrackColor(int color) {
		this.trackColor = color;
		invalidate();
	}
	
	public void setThumbColor(int color) {
		this.thumbColor = color;
		invalidate();
	}
	
	public void setIcon(int resId) {
		setIconInternal(getContext().getResources().getDrawable(resId));
		invalidate();
	}
	
	public void setIcon(Drawable drawable) {
		setIconInternal(drawable);
		invalidate();
	}
	
	public void setIndeterminate(boolean indeterminate) {
		this.indeterminate = indeterminate;
		if (indeterminate) {
			startIndeterminate();
		} else {
			stopAnimation();
			progress = 0f;
			invalidate();
		}
	}
	
	public boolean isIndeterminate() {
		return indeterminate;
	}
	
	public void setDuration(long durationMs) {
		this.defaultDuration = durationMs;
	}
	
	private void startIndeterminate() {
		stopAnimation();
		progress = 0f;
		isAnimating = true;
		
		animator = ValueAnimator.ofFloat(0f, 1f);
		animator.setDuration(defaultDuration);
		animator.setInterpolator(new LinearInterpolator());
		animator.setRepeatCount(ValueAnimator.INFINITE);
		animator.setRepeatMode(ValueAnimator.RESTART);
		animator.addUpdateListener(animation -> {
			progress = (float) animation.getAnimatedValue();
			invalidate();
		});
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationCancel(Animator animation) {
				isAnimating = false;
			}
		});
		animator.start();
	}
	
	@NonNull
	public AexonLoading setStart(long durationMs) {
		this.defaultDuration = durationMs;
		startDeterminate();
		return this;
	}
	
	public void start() {
		startDeterminate();
	}
	
	private void startDeterminate() {
		stopAnimation();
		progress = 0f;
		isAnimating = true;
		
		animator = ValueAnimator.ofFloat(0f, 1f);
		animator.setDuration(defaultDuration);
		animator.setInterpolator(new LinearInterpolator());
		animator.addUpdateListener(animation -> {
			progress = (float) animation.getAnimatedValue();
			invalidate();
		});
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				isAnimating = false;
				if (listener != null && progress >= 1f) {
					listener.complete();
				}
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				isAnimating = false;
			}
		});
		animator.start();
	}
	
	public void setProgress(float value) {
		if (indeterminate) return;
		if (animator != null && animator.isRunning()) animator.cancel();
		if (value < 0f) value = 0f;
		if (value > 1f) value = 1f;
		this.progress = value;
		invalidate();
		if (this.progress >= 1f && listener != null) listener.complete();
	}
	
	public void setProgress(int currentItem, int totalItems) {
		if (totalItems <= 0) return;
		setProgress((float) currentItem / totalItems);
	}
	
	private void stopAnimation() {
		if (animator != null) {
			animator.cancel();
			animator.removeAllUpdateListeners();
			animator.removeAllListeners();
			animator = null;
		}
		isAnimating = false;
	}
	
	public void setOnAexonLoadingListener(@Nullable OnAexonLoadingListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		super.onDraw(canvas);
		
		int width = getWidth();
		int height = getHeight();
		
		if (width == 0 || height == 0 || trackDrawable == null || thumbDrawable == null) return;
		
		int iconSize = Math.min(width, height);
		int iconLeft = (width - iconSize) / 2;
		int iconTop = (height - iconSize) / 2;
		int iconRight = iconLeft + iconSize;
		int iconBottom = iconTop + iconSize;
		
		iconBounds.set(iconLeft, iconTop, iconRight, iconBottom);
		
		trackDrawable.setBounds(iconBounds);
		trackDrawable.setColorFilter(new PorterDuffColorFilter(trackColor, PorterDuff.Mode.SRC_IN));
		trackDrawable.draw(canvas);
		
		if (progress > 0f) {
			float fillHeight = iconSize * progress;
			float fillTop = iconBottom - fillHeight;
			
			clipBounds.set(iconLeft, (int) fillTop, iconRight, iconBottom);
			
			canvas.save();
			canvas.clipRect(clipBounds);
			thumbDrawable.setBounds(iconBounds);
			thumbDrawable.setColorFilter(new PorterDuffColorFilter(thumbColor, PorterDuff.Mode.SRC_IN));
			thumbDrawable.draw(canvas);
			canvas.restore();
		}
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (indeterminate) startIndeterminate();
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		stopAnimation();
	}
}