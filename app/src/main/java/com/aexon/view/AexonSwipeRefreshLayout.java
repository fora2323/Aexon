package com.aexon.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import com.aexon.R; 
import com.aexon.theme.AexonTheme;
import com.aexon.theme.AexonThemeListener;

public class AexonSwipeRefreshLayout extends FrameLayout implements AexonThemeListener {
	
	public interface OnRefreshListener {
		void onRefresh();
	}
	
	private static final float DRAG_RATE = 0.45f;
	private static final int ANIM_DURATION = 250;
	private static final int DEFAULT_REFRESH_TRIGGER_DP = 72;
	
	public static final int DEFAULT = 1;
	public static final int LARGE = 0;
	
	@Nullable
	private View targetView;
	@NonNull
	private FrameLayout indicatorContainer;
	@NonNull
	private ProgressBar progressBar;
	@NonNull
	private GradientDrawable circleBg;
	
	private int touchSlop;
	private float initialDownY;
	private boolean isBeingDragged;
	private boolean refreshing;
	@Nullable
	private OnRefreshListener listener;
	
	@Px
	private int refreshTrigger;
	@Px
	private int indicatorTotalSize;
	private int size = DEFAULT;
	
	public AexonSwipeRefreshLayout(@NonNull Context context) {
		this(context, null);
	}
	
	public AexonSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	private void init(@NonNull Context ctx, @Nullable AttributeSet attrs) {
		touchSlop = ViewConfiguration.get(ctx).getScaledTouchSlop();
		float density = getResources().getDisplayMetrics().density;
		
		refreshTrigger = (int) (DEFAULT_REFRESH_TRIGGER_DP * density + 0.5f);
		int circleSize = (int) (40 * density);
		int progressSize = (int) (24 * density);
		indicatorTotalSize = circleSize;
		
		indicatorContainer = new FrameLayout(ctx);
		circleBg = new GradientDrawable();
		circleBg.setShape(GradientDrawable.OVAL);
		
		indicatorContainer.setBackground(circleBg);
		indicatorContainer.setElevation(6 * density);
		
		progressBar = new ProgressBar(ctx, null, android.R.attr.progressBarStyleSmall);
		progressBar.setIndeterminate(true);
		
		FrameLayout.LayoutParams progressLp = new FrameLayout.LayoutParams(progressSize, progressSize);
		progressLp.gravity = Gravity.CENTER;
		indicatorContainer.addView(progressBar, progressLp);
		
		LayoutParams containerLp = new LayoutParams(circleSize, circleSize);
		containerLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		containerLp.topMargin = -circleSize;
		addView(indicatorContainer, containerLp);
		
		indicatorContainer.setVisibility(View.GONE);
		indicatorContainer.setScaleX(0f);
		indicatorContainer.setScaleY(0f);
		
		setClipToPadding(false);
		setWillNotDraw(false);
		
		applyTheme(AexonTheme.getInstance());
		
		if (attrs != null) {
			TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.AexonSwipeRefreshLayout);
			
			if (a.hasValue(R.styleable.AexonSwipeRefreshLayout_ax_swipe_size)) {
				setSize(a.getInt(R.styleable.AexonSwipeRefreshLayout_ax_swipe_size, DEFAULT));
			}
			
			if (a.hasValue(R.styleable.AexonSwipeRefreshLayout_ax_indicator_background_color)) {
				setIndicatorBackgroundColor(a.getColor(R.styleable.AexonSwipeRefreshLayout_ax_indicator_background_color, 0xFFFFFFFF));
			}
			
			if (a.hasValue(R.styleable.AexonSwipeRefreshLayout_ax_progress_color)) {
				setProgressColor(a.getColor(R.styleable.AexonSwipeRefreshLayout_ax_progress_color, 0xFF000000));
			}
			
			if (a.hasValue(R.styleable.AexonSwipeRefreshLayout_ax_circle_size) || a.hasValue(R.styleable.AexonSwipeRefreshLayout_ax_progress_size) || a.hasValue(R.styleable.AexonSwipeRefreshLayout_ax_refresh_trigger)) {
				int cSize = a.getDimensionPixelSize(R.styleable.AexonSwipeRefreshLayout_ax_circle_size, indicatorTotalSize);
				int pSize = a.getDimensionPixelSize(R.styleable.AexonSwipeRefreshLayout_ax_progress_size, progressSize);
				int trigger = a.getDimensionPixelSize(R.styleable.AexonSwipeRefreshLayout_ax_refresh_trigger, refreshTrigger);
				setCustomSizePx(cSize, pSize, trigger);
			}
			
			a.recycle();
		}
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		AexonTheme.getInstance().addListener(this);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		AexonTheme.getInstance().removeListener(this);
	}
	
	@Override
	public void onThemeChanged(int seedColor, @NonNull AexonTheme theme) {
		applyTheme(theme);
	}
	
	private void applyTheme(@NonNull AexonTheme theme) {
		setIndicatorBackgroundColor(theme.getColorSurfaceContainerHighest());
		setProgressColor(theme.getColorPrimary());
	}
	
	public void setIndicatorBackgroundColor(@ColorInt int color) {
		if (circleBg != null) {
			circleBg.setColor(color);
		}
	}
	
	public void setProgressColor(@ColorInt int color) {
		if (progressBar != null && progressBar.getIndeterminateDrawable() != null) {
			progressBar.getIndeterminateDrawable().setTintList(ColorStateList.valueOf(color));
		}
	}
	
	public void setSize(int size) {
		if (size != DEFAULT && size != LARGE) {
			return;
		}
		this.size = size;
		updatePresetSizes();
	}
	
	public void setCustomSizeDp(int circleSizeDp, int progressSizeDp, int triggerDp) {
		float density = getResources().getDisplayMetrics().density;
		int circlePx = (int) (circleSizeDp * density + 0.5f);
		int progressPx = (int) (progressSizeDp * density + 0.5f);
		int triggerPx = (int) (triggerDp * density + 0.5f);
		
		applyCustomSizes(circlePx, progressPx, triggerPx);
	}
	
	public void setCustomSizePx(@Px int circleSizePx, @Px int progressSizePx, @Px int triggerPx) {
		applyCustomSizes(circleSizePx, progressSizePx, triggerPx);
	}
	
	private void updatePresetSizes() {
		float density = getResources().getDisplayMetrics().density;
		int circleSize;
		int progressSize;
		
		if (size == LARGE) {
			circleSize = (int) (56 * density + 0.5f);
			progressSize = (int) (32 * density + 0.5f);
			refreshTrigger = (int) (120 * density + 0.5f);
		} else {
			circleSize = (int) (40 * density + 0.5f);
			progressSize = (int) (24 * density + 0.5f);
			refreshTrigger = (int) (DEFAULT_REFRESH_TRIGGER_DP * density + 0.5f);
		}
		
		applyCustomSizes(circleSize, progressSize, refreshTrigger);
	}
	
	private void applyCustomSizes(@Px int circlePx, @Px int progressPx, @Px int triggerPx) {
		this.refreshTrigger = triggerPx;
		this.indicatorTotalSize = circlePx;
		
		ViewGroup.LayoutParams containerLp = indicatorContainer.getLayoutParams();
		if (containerLp != null) {
			containerLp.width = circlePx;
			containerLp.height = circlePx;
			if (containerLp instanceof LayoutParams) {
				((LayoutParams) containerLp).topMargin = -circlePx;
			}
			indicatorContainer.setLayoutParams(containerLp);
		}
		
		ViewGroup.LayoutParams progressLp = progressBar.getLayoutParams();
		if (progressLp != null) {
			progressLp.width = progressPx;
			progressLp.height = progressPx;
			progressBar.setLayoutParams(progressLp);
		}
		
		if (!refreshing) {
			indicatorContainer.setTranslationY(0);
		} else {
			animateIndicatorTo(refreshTrigger);
		}
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (getChildCount() > 1) {
			for (int i = 0; i < getChildCount(); i++) {
				View ch = getChildAt(i);
				if (ch != indicatorContainer) {
					targetView = ch;
					break;
				}
			}
		} else if (getChildCount() == 1 && getChildAt(0) != indicatorContainer) {
			targetView = getChildAt(0);
		}
	}
	
	public void setTargetView(@Nullable View v) {
		this.targetView = v;
	}
	
	public void setOnRefreshListener(@Nullable OnRefreshListener l) {
		this.listener = l;
	}
	
	public boolean isRefreshing() {
		return refreshing;
	}
	
	public void setRefreshing(boolean refreshing) {
		if (this.refreshing == refreshing) return;
		this.refreshing = refreshing;
		
		if (refreshing) {
			indicatorContainer.setVisibility(View.VISIBLE);
			indicatorContainer.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
			animateIndicatorTo(refreshTrigger);
		} else {
			indicatorContainer.animate().scaleX(0f).scaleY(0f).setDuration(ANIM_DURATION).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					indicatorContainer.setVisibility(View.GONE);
					animateIndicatorTo(0);
					indicatorContainer.animate().setListener(null);
				}
			}).start();
		}
	}
	
	public void setIndicatorOffsetDp(int offsetDp) {
		float density = getResources().getDisplayMetrics().density;
		int offsetPx = (int) (offsetDp * density + 0.5f);
		
		ViewGroup.LayoutParams lp = indicatorContainer.getLayoutParams();
		if (lp instanceof LayoutParams) {
			LayoutParams containerLp = (LayoutParams) lp;
			containerLp.topMargin = offsetPx - indicatorTotalSize;
			indicatorContainer.setLayoutParams(containerLp);
		}
	}
	
	
	private void animateIndicatorTo(int toOffset) {
		ObjectAnimator anim = ObjectAnimator.ofFloat(indicatorContainer, "translationY", indicatorContainer.getTranslationY(), toOffset);
		anim.setDuration(ANIM_DURATION);
		anim.setInterpolator(new DecelerateInterpolator());
		anim.start();
	}
	
	@Override
	public boolean onInterceptTouchEvent(@NonNull MotionEvent ev) {
		if (!isEnabled() || canChildScrollUp() || refreshing) {
			return false;
		}
		
		switch (ev.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
			initialDownY = ev.getY();
			isBeingDragged = false;
			break;
			case MotionEvent.ACTION_MOVE:
			float y = ev.getY();
			float yDiff = y - initialDownY;
			if (yDiff > touchSlop && !isBeingDragged) {
				isBeingDragged = true;
				indicatorContainer.setVisibility(View.VISIBLE);
			}
			break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			isBeingDragged = false;
			break;
		}
		return isBeingDragged;
	}
	
	@Override
	public boolean onTouchEvent(@NonNull MotionEvent ev) {
		if (!isEnabled() || canChildScrollUp() || refreshing) {
			return false;
		}
		
		switch (ev.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
			initialDownY = ev.getY();
			return true;
			case MotionEvent.ACTION_MOVE: {
				float y = ev.getY();
				float dy = (y - initialDownY) * DRAG_RATE;
				if (dy < 0) return false;
				
				float maxDrag = refreshTrigger * 1.5f;
				if (dy > maxDrag) dy = maxDrag;
				
				moveIndicator((int) dy);
				return true;
			}
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL: {
				float y = ev.getY();
				float dy = (y - initialDownY) * DRAG_RATE;
				
				if (dy > refreshTrigger) {
					setRefreshing(true);
					if (listener != null) {
						new Handler(Looper.getMainLooper()).postDelayed(() -> listener.onRefresh(), 100);
					}
				} else {
					indicatorContainer.animate().scaleX(0f).scaleY(0f).setDuration(150).setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							indicatorContainer.setVisibility(View.GONE);
							moveIndicator(0);
							indicatorContainer.animate().setListener(null);
						}
					}).start();
				}
				isBeingDragged = false;
				return true;
			}
		}
		return super.onTouchEvent(ev);
	}
	
	private void moveIndicator(int offset) {
		indicatorContainer.setTranslationY(offset);
		
		if (offset > 0) {
			float progress = (float) offset / refreshTrigger;
			float scale = Math.min(1.0f, progress);
			indicatorContainer.setScaleX(scale);
			indicatorContainer.setScaleY(scale);
			
			progressBar.setRotation(offset * 2.0f);
		}
	}
	
	public boolean canChildScrollUp() {
		if (targetView == null) return true;
		if (targetView instanceof ListView) {
			ListView lv = (ListView) targetView;
			if (lv.getChildCount() == 0) return false;
			if (lv.getFirstVisiblePosition() > 0) return true;
			View firstChild = lv.getChildAt(0);
			if (firstChild == null) return false;
			return firstChild.getTop() < lv.getPaddingTop();
		} else {
			return targetView.canScrollVertically(-1);
		}
	}
}