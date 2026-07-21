package com.aexon.material.bottomsheet;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.aexon.R;
import com.aexon.annotation.NonNull;
import com.aexon.annotation.Nullable;
import com.aexon.annotation.RequiresApi;
import com.aexon.core.AexonApi;

public class AexonBottomSheetDialog extends Dialog {
	
	private BottomSheetContainer mContainer;
	private View mSheetView;
	private View mNavBarSpacer;
	private AexonBottomSheetBehavior mBehavior;
	private boolean mCanceledOnTouchOutside = true;
	private boolean mCancelable = true;
	private int mScrimColor = Color.BLACK;
	private float mScrimMaxAlpha = 0.6f;
	
	public AexonBottomSheetDialog(@NonNull Context context) {
		this(context, R.style.AexonBottomSheetTheme);
	}
	
	public AexonBottomSheetDialog(@NonNull Context context, int theme) {
		super(context, theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ensureContainer();
	}
	
	private void ensureContainer() {
		if (mContainer != null) return;
		
		Window window = getWindow();
		if (window != null) {
			window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			
			if (AexonApi.minSdk(RequiresApi.LOLLIPOP)) {
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
				window.setStatusBarColor(Color.TRANSPARENT);
				window.setNavigationBarColor(Color.TRANSPARENT);
			}
			
			if (AexonApi.minSdk(RequiresApi.R)) {
				window.setDecorFitsSystemWindows(false);
			}
		}
		
		mContainer = new BottomSheetContainer(getContext());
		mContainer.setBackgroundColor(Color.TRANSPARENT);
		mContainer.setFitsSystemWindows(false);
		super.setContentView(mContainer, new ViewGroup.LayoutParams(
		ViewGroup.LayoutParams.MATCH_PARENT,
		ViewGroup.LayoutParams.MATCH_PARENT));
	}
	
	private void applyEdgeToEdgePadding() {
		if (AexonApi.minSdk(RequiresApi.LOLLIPOP)) {
			mContainer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
				@Override
				public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
					int navBottom;
					if (AexonApi.minSdk(RequiresApi.R)) {
						navBottom = insets.getInsets(WindowInsets.Type.navigationBars()).bottom;
					} else {
						navBottom = insets.getSystemWindowInsetBottom();
					}
					
					if (mNavBarSpacer != null) {
						ViewGroup.LayoutParams lp = mNavBarSpacer.getLayoutParams();
						if (lp.height != navBottom) {
							lp.height = navBottom;
							mNavBarSpacer.setLayoutParams(lp);
						}
					}
					return insets;
				}
			});
			
			mContainer.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
				@Override
				public void onViewAttachedToWindow(View v) {
					v.requestApplyInsets();
				}
				
				@Override
				public void onViewDetachedFromWindow(View v) {
				}
			});
			
			if (mContainer.isAttachedToWindow()) {
				mContainer.requestApplyInsets();
			}
		}
	}
	
	private int getFallbackColor() {
		TypedValue tv = new TypedValue();
		Context ctx = getContext();
		int colorSurfaceAttr = ctx.getResources().getIdentifier("colorSurface", "attr", ctx.getPackageName());
		if (colorSurfaceAttr != 0 && ctx.getTheme().resolveAttribute(colorSurfaceAttr, tv, true)) {
			return tv.data;
		}
		if (ctx.getTheme().resolveAttribute(android.R.attr.windowBackground, tv, true)) {
			if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
				return tv.data;
			}
		}
		return Color.BLACK;
	}
	
	public void setBottomSpacerColor(int color) {
		if (mNavBarSpacer != null) {
			mNavBarSpacer.setBackgroundColor(color);
		}
	}
	
	@Override
	public void setContentView(View view) {
		ensureContainer();
		if (mSheetView != null) {
			mContainer.removeView(mSheetView);
		}
		if (mNavBarSpacer != null) {
			mContainer.removeView(mNavBarSpacer);
		}
		
		mSheetView = view;
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
		FrameLayout.LayoutParams.MATCH_PARENT,
		FrameLayout.LayoutParams.WRAP_CONTENT);
		mContainer.addView(mSheetView, lp);
		
		mNavBarSpacer = new View(getContext());
		if (mSheetView.getBackground() instanceof ColorDrawable) {
			mNavBarSpacer.setBackgroundColor(((ColorDrawable) mSheetView.getBackground()).getColor());
		} else {
			mNavBarSpacer.setBackgroundColor(getFallbackColor());
		}
		
		FrameLayout.LayoutParams spacerLp = new FrameLayout.LayoutParams(
		FrameLayout.LayoutParams.MATCH_PARENT, 0);
		mContainer.addView(mNavBarSpacer, spacerLp);
		
		applyEdgeToEdgePadding();
		setupBehavior();
	}
	
	@Override
	public void setContentView(int layoutResID) {
		ensureContainer();
		setContentView(LayoutInflater.from(getContext()).inflate(layoutResID, mContainer, false));
	}
	
	@Override
	public void setContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
		ensureContainer();
		if (mSheetView != null) {
			mContainer.removeView(mSheetView);
		}
		if (mNavBarSpacer != null) {
			mContainer.removeView(mNavBarSpacer);
		}
		
		mSheetView = view;
		if (params != null) {
			mContainer.addView(mSheetView, params);
		} else {
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.WRAP_CONTENT);
			mContainer.addView(mSheetView, lp);
		}
		
		mNavBarSpacer = new View(getContext());
		if (mSheetView.getBackground() instanceof ColorDrawable) {
			mNavBarSpacer.setBackgroundColor(((ColorDrawable) mSheetView.getBackground()).getColor());
		} else {
			mNavBarSpacer.setBackgroundColor(getFallbackColor());
		}
		
		FrameLayout.LayoutParams spacerLp = new FrameLayout.LayoutParams(
		FrameLayout.LayoutParams.MATCH_PARENT, 0);
		mContainer.addView(mNavBarSpacer, spacerLp);
		
		applyEdgeToEdgePadding();
		setupBehavior();
	}
	
	private void setupBehavior() {
		if (mSheetView == null) return;
		mBehavior = AexonBottomSheetBehavior.from(mSheetView);
		mBehavior.setHideable(true);
		mBehavior.setSkipCollapsed(true);
		mBehavior.setState(AexonBottomSheetBehavior.STATE_EXPANDED);
		mBehavior.setBottomSheetCallback(new AexonBottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {
				if (newState == AexonBottomSheetBehavior.STATE_HIDDEN) {
					dismiss();
				}
			}
			
			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {
				updateScrim(slideOffset);
				if (mNavBarSpacer != null) {
					mNavBarSpacer.setTranslationY(bottomSheet.getTranslationY());
				}
			}
		});
	}
	
	private void updateScrim(float slideOffset) {
		if (mContainer == null) return;
		float alpha = Math.max(0f, Math.min(1f, slideOffset));
		int a = (int) (alpha * mScrimMaxAlpha * 255f);
		mContainer.setBackgroundColor(Color.argb(a,
		Color.red(mScrimColor),
		Color.green(mScrimColor),
		Color.blue(mScrimColor)));
	}
	
	@Nullable
	public View getSheetView() {
		return mSheetView;
	}
	
	@NonNull
	public AexonBottomSheetBehavior getBehavior() {
		if (mBehavior == null) {
			mBehavior = new AexonBottomSheetBehavior();
		}
		return mBehavior;
	}
	
	public void setScrimColor(int color) {
		mScrimColor = color;
	}
	
	public void setScrimMaxAlpha(float alpha) {
		mScrimMaxAlpha = Math.max(0f, Math.min(1f, alpha));
	}
	
	@Override
	public void setCanceledOnTouchOutside(boolean cancel) {
		super.setCanceledOnTouchOutside(cancel);
		mCanceledOnTouchOutside = cancel;
	}
	
	@Override
	public void setCancelable(boolean flag) {
		super.setCancelable(flag);
		mCancelable = flag;
	}
	
	@Override
	public void onBackPressed() {
		if (mBehavior != null && mBehavior.getState() != AexonBottomSheetBehavior.STATE_HIDDEN) {
			mBehavior.setState(AexonBottomSheetBehavior.STATE_HIDDEN);
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	public void show() {
		super.show();
		Window window = getWindow();
		if (window != null) {
			window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		}
	}
	
	@Override
	public void dismiss() {
		if (mBehavior != null && mBehavior.getState() != AexonBottomSheetBehavior.STATE_HIDDEN) {
			mBehavior.setState(AexonBottomSheetBehavior.STATE_HIDDEN);
		} else {
			super.dismiss();
		}
	}
	
	private class BottomSheetContainer extends FrameLayout {
		
		public BottomSheetContainer(Context context) {
			super(context);
			setWillNotDraw(false);
		}
		
		@Override
		protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
			super.onLayout(changed, left, top, right, bottom);
			if (mSheetView != null && mBehavior != null) {
				mBehavior.onLayoutChild(this, mSheetView);
			}
			if (mNavBarSpacer != null && mSheetView != null) {
				int spacerTop = mSheetView.getBottom();
				mNavBarSpacer.layout(left, spacerTop, right, spacerTop + mNavBarSpacer.getMeasuredHeight());
				mNavBarSpacer.setTranslationY(mSheetView.getTranslationY());
			}
		}
		
		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			if (mSheetView == null || mBehavior == null) {
				return handleScrimTouch(ev);
			}
			
			float sheetTop = mSheetView.getTranslationY();
			float y = ev.getY();
			
			if (y < sheetTop) {
				return false;
			}
			
			if (!mBehavior.isDraggable()) {
				return false;
			}
			
			return mBehavior.onInterceptTouchEvent(this, mSheetView, ev);
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			if (mSheetView == null || mBehavior == null) {
				return handleScrimTouch(ev);
			}
			
			float sheetTop = mSheetView.getTranslationY();
			float y = ev.getY();
			
			if (y < sheetTop) {
				if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
					if (mCanceledOnTouchOutside && mCancelable) {
						cancel();
					}
				}
				return true;
			}
			
			if (!mBehavior.isDraggable()) {
				return false;
			}
			
			return mBehavior.onTouchEvent(this, mSheetView, ev);
		}
		
		private boolean handleScrimTouch(MotionEvent ev) {
			if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
				if (mCanceledOnTouchOutside && mCancelable) {
					cancel();
				}
			}
			return true;
		}
	}
}