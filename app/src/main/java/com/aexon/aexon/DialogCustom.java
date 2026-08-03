/*
* Copyright (c) 2026 Fora
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
* 
* Contact: Fora <fora060823@gmail.com>
* Created: 27-01-2026
*/
package com.aexon.aexon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.aexon.R;
import com.aexon.SketchwareUtil;
import com.aexon.theme.AexonTheme;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DialogCustom extends Dialog {
	
	public interface OnSwipeListener {
		void onSwipeUp();
		void onSwipeDown();
	}
	
	private OnSwipeListener swipeListener;
	private float touchStartY = 0f;
	private static final float SWIPE_THRESHOLD = 0.85f;
	private int originalNavColor = Color.BLACK;
	private int originalStatusColor = Color.TRANSPARENT;
	
	public DialogCustom(@NonNull Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
	}
	
	public void setOnSwipeListener(@Nullable OnSwipeListener listener) {
		this.swipeListener = listener;
	}
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_custom);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			if (getOwnerActivity() != null) {
				Window activityWindow = getOwnerActivity().getWindow();
				originalNavColor = activityWindow.getNavigationBarColor();
				originalStatusColor = activityWindow.getStatusBarColor();
			}
		}
		
		if (getWindow() != null) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			getWindow().setDimAmount(0.4f);
			
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
				getWindow().setStatusBarColor(Color.TRANSPARENT);
				getWindow().setNavigationBarColor(Color.TRANSPARENT);
			}
			
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
				getWindow().setAttributes(lp);
			}
			
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
				getWindow().setDecorFitsSystemWindows(false);
			}
		}
		
		setCanceledOnTouchOutside(true);
		setCancelable(true);
		
		setupViews();
	}
	
	@Override
	public void dismiss() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			if (getOwnerActivity() != null) {
				Window activityWindow = getOwnerActivity().getWindow();
				activityWindow.setNavigationBarColor(originalNavColor);
				activityWindow.setStatusBarColor(originalStatusColor);
			}
		}
		super.dismiss();
	}
	
	private void setupViews() {
		AexonTheme theme = AexonTheme.getInstance();
		ImageView imageviewTop = findViewById(R.id.imageview1);
		LinearLayout track = findViewById(R.id.track);
		View rootView = findViewById(android.R.id.content);
		
		rootView.setOnClickListener(v -> dismiss());
		track.setOnClickListener(v -> {});
		imageviewTop.setOnClickListener(v -> {});
		
		int colorOnSurface = theme.getColorOnSurface();
		int colorSurfaceContainerHigh = theme.getColorSurfaceContainerHigh();
		int colorSurfaceContainer = theme.getColorSurfaceContainer();
		int colorErrorContainer = theme.getColorErrorContainer();
		
		GradientDrawable circleFireBg = new GradientDrawable();
		circleFireBg.setShape(GradientDrawable.OVAL);
		circleFireBg.setColor(colorErrorContainer);
		imageviewTop.setBackground(circleFireBg);
		imageviewTop.setColorFilter(colorOnSurface);
		
		GradientDrawable trackBg = new GradientDrawable();
		trackBg.setShape(GradientDrawable.RECTANGLE);
		trackBg.setCornerRadius(SketchwareUtil.dpToPx(getContext(), 30));
		trackBg.setColor(colorSurfaceContainerHigh);
		track.setBackground(trackBg);
		
		int iconSize = SketchwareUtil.dpToPx(getContext(), 24);
		int trackPadding = SketchwareUtil.dpToPx(getContext(), 4);
		int trackWidth = SketchwareUtil.dpToPx(getContext(), 60);
		int thumbSize = trackWidth - (trackPadding * 2) - SketchwareUtil.dpToPx(getContext(), 4);
		
		track.setOrientation(LinearLayout.VERTICAL);
		track.setGravity(Gravity.CENTER_HORIZONTAL);
		
		FrameLayout container = new FrameLayout(getContext());
		LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		container.setLayoutParams(containerParams);
		
		ImageView icPower = new ImageView(getContext());
		FrameLayout.LayoutParams paramsPower = new FrameLayout.LayoutParams(iconSize, iconSize);
		paramsPower.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		paramsPower.topMargin = SketchwareUtil.dpToPx(getContext(), 12);
		icPower.setLayoutParams(paramsPower);
		icPower.setImageResource(R.drawable.ic_power);
		icPower.setColorFilter(colorOnSurface);
		icPower.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		container.addView(icPower);
		
		ImageView icRestart = new ImageView(getContext());
		FrameLayout.LayoutParams paramsRestart = new FrameLayout.LayoutParams(iconSize, iconSize);
		paramsRestart.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		paramsRestart.bottomMargin = SketchwareUtil.dpToPx(getContext(), 12);
		icRestart.setLayoutParams(paramsRestart);
		icRestart.setImageResource(R.drawable.ic_restart);
		icRestart.setColorFilter(colorOnSurface);
		icRestart.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		container.addView(icRestart);
		
		ImageView thumb = new ImageView(getContext());
		FrameLayout.LayoutParams paramsThumb = new FrameLayout.LayoutParams(thumbSize, thumbSize);
		paramsThumb.gravity = Gravity.CENTER;
		thumb.setLayoutParams(paramsThumb);
		thumb.setImageResource(R.drawable.ic_code);
		thumb.setColorFilter(colorSurfaceContainer);
		thumb.setRotation(90f);
		
		int pad10 = SketchwareUtil.dpToPx(getContext(), 10);
		thumb.setPadding(pad10, pad10, pad10, pad10);
		
		GradientDrawable thumbBg = new GradientDrawable();
		thumbBg.setShape(GradientDrawable.OVAL);
		thumbBg.setColor(colorOnSurface);
		thumb.setBackground(thumbBg);
		container.addView(thumb);
		
		track.addView(container);
		
		container.post(() -> {
			int thumbTop = thumb.getTop();
			int thumbH = thumb.getHeight();
			int thumbCenterY = thumbTop + thumbH / 2;
			
			int icPowerCenterY = icPower.getTop() + icPower.getHeight() / 2;
			int icRestartCenterY = icRestart.getTop() + icRestart.getHeight() / 2;
			
			float maxUp = icPowerCenterY - thumbCenterY;
			float maxDown = icRestartCenterY - thumbCenterY;
			
			thumb.setOnTouchListener((v, event) -> {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
					touchStartY = event.getRawY();
					return true;
					
					case MotionEvent.ACTION_MOVE:
					float moveY = event.getRawY() - touchStartY;
					float clamped = Math.max(maxUp, Math.min(maxDown, moveY));
					thumb.setTranslationY(clamped);
					return true;
					
					case MotionEvent.ACTION_UP:
					float deltaY = event.getRawY() - touchStartY;
					
					if (deltaY < 0 && thumb.getTranslationY() <= maxUp * SWIPE_THRESHOLD) {
						animateThumb(thumb, maxUp, () -> {
							if (swipeListener != null) swipeListener.onSwipeUp();
							dismiss();
						});
					} else if (deltaY > 0 && thumb.getTranslationY() >= maxDown * SWIPE_THRESHOLD) {
						animateThumb(thumb, maxDown, () -> {
							if (swipeListener != null) swipeListener.onSwipeDown();
							dismiss();
						});
					} else {
						animateThumb(thumb, 0f, null);
					}
					return true;
				}
				return false;
			});
		});
	}
	
	private void animateThumb(@NonNull View thumb, float toY, @Nullable Runnable onEnd) {
		ObjectAnimator anim = ObjectAnimator.ofFloat(thumb, "translationY", thumb.getTranslationY(), toY);
		anim.setDuration(200);
		if (onEnd != null) {
			anim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					onEnd.run();
				}
			});
		}
		anim.start();
	}
}