package com.aexon.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.OvershootInterpolator;
import android.widget.Checkable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;

import com.aexon.R;

public class AexonNavigationBar extends LinearLayout {
	
	private static final int MODE_SLIDE = 0;
	private static final int MODE_SNAP_BOUNCE = 1;
	
	private final List<NavItemButton> items = new ArrayList<>();
	private OnItemSelectedListener listener;
	private ColorStateList itemIconTint;
	
	private float thumbRadius;
	private int thumbColor = 0x70000000;
	private int rippleColor = 0x1A000000;
	
	private Paint thumbPaint;
	private RectF thumbRect = new RectF();
	private float currentThumbLeft = 0f;
	private float currentThumbRight = 0f;
	private float thumbScale = 1f;
	private int selectedIndex = -1;
	private ValueAnimator animator;
	
	public interface OnItemSelectedListener {
		void onItemSelected(int index, int itemId);
	}
	
	public AexonNavigationBar(Context context) {
		super(context);
		init(context, null);
	}
	
	public AexonNavigationBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);
		setWillNotDraw(false);
		
		thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		float radius = dp(100);
		float elevation = 0;
		int menuRes = 0;
		
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AexonNavigationBar);
			radius = a.getDimension(R.styleable.AexonNavigationBar_ax_radius, radius);
			elevation = a.getDimension(R.styleable.AexonNavigationBar_ax_elevation, elevation);
			
			int tintResId = a.getResourceId(R.styleable.AexonNavigationBar_ax_itemIconTint, 0);
			if (tintResId != 0) {
				if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
					itemIconTint = context.getColorStateList(tintResId);
				} else {
					itemIconTint = getResources().getColorStateList(tintResId);
				}
			} else {
				itemIconTint = a.getColorStateList(R.styleable.AexonNavigationBar_ax_itemIconTint);
			}
			
			menuRes = a.getResourceId(R.styleable.AexonNavigationBar_ax_menu, 0);
			thumbColor = a.getColor(R.styleable.AexonNavigationBar_ax_itemThumbColor, thumbColor);
			rippleColor = a.getColor(R.styleable.AexonNavigationBar_ax_itemRippleColor, rippleColor);
			
			a.recycle();
		}
		
		thumbColor = (thumbColor & 0x00FFFFFF) | 0x70000000;
		thumbPaint.setColor(thumbColor);
		
		setElevation(elevation);
		
		final float finalRadius = radius;
		setOutlineProvider(new ViewOutlineProvider() {
			@Override
			public void getOutline(View view, Outline outline) {
				outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), finalRadius);
			}
		});
		setClipToOutline(true);
		
		int pLeft = getPaddingLeft();
		int pTop = getPaddingTop();
		int pRight = getPaddingRight();
		int pBottom = getPaddingBottom();
		int maxPadding = Math.max(Math.max(pLeft, pTop), Math.max(pRight, pBottom));
		
		thumbRadius = Math.max(0, radius - maxPadding);
		
		if (menuRes != 0) {
			inflateMenu(menuRes);
		}
	}
	
	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		this.listener = listener;
	}
	
	private void inflateMenu(int menuRes) {
		PopupMenu p = new PopupMenu(getContext(), null);
		p.inflate(menuRes);
		Menu menu = p.getMenu();
		for (int i = 0; i < menu.size(); i++) {
			MenuItem item = menu.getItem(i);
			addItem(item.getIcon(), item.getItemId());
		}
	}
	
	public void addItem(Drawable iconDrawable, int itemId) {
		NavItemButton button = new NavItemButton(getContext());
		button.setId(itemId);
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		if (items.size() > 0) {
			params.setMarginStart(dp(4));
		}
		button.setLayoutParams(params);
		
		if (iconDrawable != null) {
			button.setImageDrawable(iconDrawable.mutate());
		}
		
		button.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
		button.setBackground(buildItemRipple(thumbRadius));
		
		if (itemIconTint != null) {
			button.setImageTintList(itemIconTint);
		}
		
		final int index = items.size();
		button.setOnClickListener(v -> selectItem(index, true, MODE_SNAP_BOUNCE));
		
		items.add(button);
		addView(button);
		
		button.setChecked(false);
		if (items.size() == 1) {
			selectItem(0, false, MODE_SLIDE);
		}
	}
	
	public void setChecked(int index) {
		selectItem(index, true, MODE_SLIDE);
	}
	
	public void setItemIcon(int index, Drawable icon) {
		if (index >= 0 && index < items.size() && icon != null) {
			items.get(index).setImageDrawable(icon.mutate());
		}
	}
	
	public void setItemIconTint(ColorStateList tint) {
		this.itemIconTint = tint;
		for (NavItemButton item : items) {
			item.setImageTintList(tint);
		}
	}
	
	public void setThumbColor(int color) {
		this.thumbColor = (color & 0x00FFFFFF) | 0x70000000;
		thumbPaint.setColor(this.thumbColor);
		invalidate();
	}
	
	public void setRippleColor(int color) {
		this.rippleColor = color;
		for (NavItemButton item : items) {
			item.setBackground(buildItemRipple(thumbRadius));
		}
	}
	
	private RippleDrawable buildItemRipple(float radius) {
		GradientDrawable mask = new GradientDrawable();
		mask.setShape(GradientDrawable.RECTANGLE);
		mask.setCornerRadius(radius);
		mask.setColor(Color.WHITE);
		return new RippleDrawable(ColorStateList.valueOf(rippleColor), null, mask);
	}
	
	private void selectItem(int index, boolean animate, int mode) {
		if (index < 0 || index >= items.size()) return;
		if (selectedIndex == index) return;
		
		selectedIndex = index;
		
		for (int i = 0; i < items.size(); i++) {
			items.get(i).setChecked(i == index);
		}
		
		if (listener != null) {
			listener.onItemSelected(index, items.get(index).getId());
		}
		
		View targetView = items.get(index);
		if (targetView.getWidth() == 0) {
			return;
		}
		
		float targetLeft = targetView.getLeft();
		float targetRight = targetView.getRight();
		
		if (!animate || (currentThumbLeft == 0 && currentThumbRight == 0)) {
			currentThumbLeft = targetLeft;
			currentThumbRight = targetRight;
			thumbScale = 1f;
			invalidate();
			return;
		}
		
		if (animator != null && animator.isRunning()) {
			animator.cancel();
		}
		
		if (mode == MODE_SNAP_BOUNCE) {
			currentThumbLeft = targetLeft;
			currentThumbRight = targetRight;
			
			animator = ValueAnimator.ofFloat(1.15f, 1f);
			animator.setDuration(280);
			animator.setInterpolator(new OvershootInterpolator(2.2f));
			animator.addUpdateListener(a -> {
				thumbScale = (float) a.getAnimatedValue();
				invalidate();
			});
			animator.start();
		} else {
			thumbScale = 1f;
			final float startLeft = currentThumbLeft;
			final float startRight = currentThumbRight;
			
			animator = ValueAnimator.ofFloat(0f, 1f);
			animator.setDuration(220);
			animator.setInterpolator(new OvershootInterpolator(1.5f));
			animator.addUpdateListener(animation -> {
				float val = (float) animation.getAnimatedValue();
				currentThumbLeft = startLeft + ((targetLeft - startLeft) * val);
				currentThumbRight = startRight + ((targetRight - startRight) * val);
				invalidate();
			});
			animator.start();
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (items.size() > 0 && selectedIndex >= 0 && selectedIndex < items.size()) {
			if (animator == null || !animator.isRunning()) {
				View targetView = items.get(selectedIndex);
				currentThumbLeft = targetView.getLeft();
				currentThumbRight = targetView.getRight();
				invalidate();
			}
		}
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (currentThumbLeft != 0 || currentThumbRight != 0) {
			float top = getPaddingTop();
			float bottom = getHeight() - getPaddingBottom();
			float centerX = (currentThumbLeft + currentThumbRight) / 2f;
			float centerY = (top + bottom) / 2f;
			float halfWidth = ((currentThumbRight - currentThumbLeft) / 2f) * thumbScale;
			float halfHeight = ((bottom - top) / 2f) * thumbScale;
			
			thumbRect.set(centerX - halfWidth, centerY - halfHeight, centerX + halfWidth, centerY + halfHeight);
			canvas.drawRoundRect(thumbRect, thumbRadius, thumbRadius, thumbPaint);
		}
		super.dispatchDraw(canvas);
	}
	
	private int dp(int value) {
		float density = getContext().getResources().getDisplayMetrics().density;
		return Math.round(value * density);
	}
	
	private class NavItemButton extends ImageButton implements Checkable {
		private boolean mChecked = false;
		private final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};
		
		public NavItemButton(Context context) {
			super(context);
		}
		
		@Override
		public void setChecked(boolean checked) {
			if (mChecked != checked) {
				mChecked = checked;
				refreshDrawableState();
			}
		}
		
		@Override
		public boolean isChecked() {
			return mChecked;
		}
		
		@Override
		public void toggle() {
			setChecked(!mChecked);
		}
		
		@Override
		public int[] onCreateDrawableState(int extraSpace) {
			final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
			if (isChecked()) {
				mergeDrawableStates(drawableState, CHECKED_STATE_SET);
			}
			return drawableState;
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(heightMeasureSpec, heightMeasureSpec);
			int size = MeasureSpec.getSize(heightMeasureSpec);
			int pad = (int) (size * 0.25f);
			setPadding(pad, pad, pad, pad);
		}
	}
}