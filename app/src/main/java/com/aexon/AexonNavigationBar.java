package com.aexon.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Checkable;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.ArrayList;
import java.util.List;

import com.aexon.R;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AexonNavigationBar extends LinearLayoutCompat {
	
	private final List<NavItemButton> items = new ArrayList<>();
	private OnItemSelectedListener listener;
	private ColorStateList itemIconTint;
	
	private float thumbRadius;
	private int thumbColor = 0x33FFFFFF;
	private int rippleColor = 0x1AFFFFFF;
	
	public interface OnItemSelectedListener {
		void onItemSelected(int index, int itemId);
	}
	
	public AexonNavigationBar(Context context) {
		super(context);
		init(context, null);
	}
	
	public AexonNavigationBar(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);
		
		float radius = dp(100);
		float elevation = 0;
		int menuRes = 0;
		
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AexonNavigationBar);
			radius = a.getDimension(R.styleable.AexonNavigationBar_ax_radius, radius);
			elevation = a.getDimension(R.styleable.AexonNavigationBar_ax_elevation, elevation);
			
			int tintResId = a.getResourceId(R.styleable.AexonNavigationBar_ax_itemIconTint, 0);
			if (tintResId != 0) {
				itemIconTint = AppCompatResources.getColorStateList(context, tintResId);
			} else {
				itemIconTint = a.getColorStateList(R.styleable.AexonNavigationBar_ax_itemIconTint);
			}
			
			menuRes = a.getResourceId(R.styleable.AexonNavigationBar_ax_menu, 0);
			thumbColor = a.getColor(R.styleable.AexonNavigationBar_ax_itemThumbColor, thumbColor);
			rippleColor = a.getColor(R.styleable.AexonNavigationBar_ax_itemRippleColor, rippleColor);
			a.recycle();
		}
		
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
	
	private NavItemButton addItem(Drawable iconDrawable, int itemId) {
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
		button.setBackground(buildItemBackground(thumbRadius));
		
		if (itemIconTint != null) {
			button.setImageTintList(itemIconTint);
		}
		
		final int index = items.size();
		button.setOnClickListener(v -> selectItem(index));
		
		items.add(button);
		addView(button);
		
		button.setChecked(false);
		if (index == 0) {
			selectItem(0);
		}
		
		return button;
	}
	
	private RippleDrawable buildItemBackground(float radius) {
		ShapeAppearanceModel shapeModel = ShapeAppearanceModel.builder().setAllCornerSizes(radius).build();
		
		MaterialShapeDrawable selectedShape = new MaterialShapeDrawable(shapeModel);
		selectedShape.setFillColor(ColorStateList.valueOf(thumbColor));
		
		MaterialShapeDrawable normalShape = new MaterialShapeDrawable(shapeModel);
		normalShape.setFillColor(ColorStateList.valueOf(Color.TRANSPARENT));
		
		StateListDrawable selector = new StateListDrawable();
		selector.addState(new int[]{android.R.attr.state_checked}, selectedShape);
		selector.addState(new int[]{}, normalShape);
		
		MaterialShapeDrawable mask = new MaterialShapeDrawable(shapeModel);
		mask.setFillColor(ColorStateList.valueOf(Color.WHITE));
		
		return new RippleDrawable(ColorStateList.valueOf(rippleColor), selector, mask);
	}
	
	public void selectItem(int index) {
		for (int i = 0; i < items.size(); i++) {
			boolean selected = (i == index);
			NavItemButton button = items.get(i);
			button.setChecked(selected);
		}
		if (listener != null) {
			listener.onItemSelected(index, items.get(index).getId());
		}
	}
	
	private int dp(int value) {
		float density = getContext().getResources().getDisplayMetrics().density;
		return Math.round(value * density);
	}
	
	private class NavItemButton extends AppCompatImageButton implements Checkable {
		private boolean mChecked = false;
		private final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};
		
		public NavItemButton(Context context) {
			super(context);
			setBackground(null);
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