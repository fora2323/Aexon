package com.aexon.material.menu;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aexon.R;
import com.aexon.theme.AexonTheme;

public class AexonPopupMenu {
	
	public interface OnMenuItemClickListener {
		boolean onMenuItemClick(MenuItem item);
	}
	
	private static final float CORNER_DP = 8f;
	private static final float ITEM_H_DP = 44f;
	private static final float PAD_V_DP = 8f;
	private static final float PAD_H_DP = 12f;
	private static final float DEFAULT_W_DP = 196f;
	private static final float MIN_W_DP = 112f;
	private static final float MAX_W_DP = 280f;
	private static final float ICON_DP = 24f;
	private static final float ICON_GAP_DP = 12f;
	private static final float TEXT_SP = 15f;
	private static final float DIVIDER_DP = 1f;
	
	private final Context context;
	private final View anchor;
	private final android.widget.PopupMenu internal;
	
	private PopupWindow popupWindow;
	private View containerView;
	private OnMenuItemClickListener clickListener;
	private float customWidthDp = DEFAULT_W_DP;
	private boolean isAnimatingOut = false;
	
	public AexonPopupMenu(Context context, View anchor) {
		this.context = context;
		this.anchor = anchor;
		this.internal = new android.widget.PopupMenu(context, anchor);
	}
	
	public MenuInflater getMenuInflater() {
		return internal.getMenuInflater();
	}
	
	public Menu getMenu() {
		return internal.getMenu();
	}
	
	public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
		this.clickListener = listener;
	}
	
	public void setWidth(int dp) {
		this.customWidthDp = Math.max(MIN_W_DP, Math.min(MAX_W_DP, dp));
	}
	
	public void show() {
		buildAndShow();
	}
	
	public void dismiss() {
		if (popupWindow == null || !popupWindow.isShowing() || isAnimatingOut) return;
		isAnimatingOut = true;
		animateOut(() -> {
			if (popupWindow != null) {
				popupWindow.dismiss();
				popupWindow = null;
			}
			containerView = null;
			isAnimatingOut = false;
		});
	}
	
	private void buildAndShow() {
		Menu menu = internal.getMenu();
		int n = menu.size();
		if (n == 0) return;
		
		AexonTheme theme = AexonTheme.getInstance();
		
		float density = context.getResources().getDisplayMetrics().density;
		int itemH = dp(density, ITEM_H_DP);
		int padV = dp(density, PAD_V_DP);
		int padH = dp(density, PAD_H_DP);
		int iconSz = dp(density, ICON_DP);
		int iconGap = dp(density, ICON_GAP_DP);
		int popW = dp(density, customWidthDp);
		int corner = dp(density, CORNER_DP);
		int dividerH = dp(density, DIVIDER_DP);
		
		int cBg = theme.getColorSurfaceContainerHigh();
		int cText = theme.getColorOnSurface();
		int cIcon = theme.getColorOnSurfaceVariant();
		int cDivider = theme.getColorOutline();
		int cRipple = theme.getColorOnSurface();
		int cPrimary = theme.getColorPrimary();
		
		boolean hasIcons = false;
		for (int i = 0; i < n; i++) {
			MenuItem mi = menu.getItem(i);
			if (mi.isVisible() && mi.getIcon() != null) {
				hasIcons = true;
				break;
			}
		}
		
		LinearLayout list = new LinearLayout(context);
		list.setOrientation(LinearLayout.VERTICAL);
		list.setPadding(0, padV, 0, padV);
		
		int prevGroupId = -1;
		for (int i = 0; i < n; i++) {
			MenuItem mi = menu.getItem(i);
			if (!mi.isVisible()) continue;
			if (i > 0 && mi.getGroupId() != prevGroupId && prevGroupId != -1) {
				list.addView(buildDivider(cDivider, dividerH, density));
			}
			prevGroupId = mi.getGroupId();
			list.addView(buildRow(mi, hasIcons, itemH, padH, iconSz, iconGap, cText, cIcon, cRipple, cPrimary, density),
			new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		}
		
		GradientDrawable bg = new GradientDrawable();
		bg.setColor(cBg);
		bg.setCornerRadius(corner);
		
		list.measure(View.MeasureSpec.makeMeasureSpec(popW, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		int listH = list.getMeasuredHeight();
		int maxH = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.4f);
		
		View content;
		int contentH;
		if (listH > maxH) {
			ScrollView sv = new ScrollView(context);
			sv.addView(list, new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT));
			sv.setBackground(bg);
			sv.setVerticalScrollBarEnabled(false);
			content = sv;
			contentH = maxH;
		} else {
			list.setBackground(bg);
			content = list;
			contentH = listH;
		}
		
		FrameLayout wrapper = new FrameLayout(context);
		wrapper.addView(content, new FrameLayout.LayoutParams(
		FrameLayout.LayoutParams.MATCH_PARENT,
		FrameLayout.LayoutParams.MATCH_PARENT));
		containerView = wrapper;
		
		if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
		
		popupWindow = new PopupWindow(containerView, popW, contentH, true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT) {
			public boolean isTransparent() {
				return false;
			}
		});
		popupWindow.setOutsideTouchable(true);
		popupWindow.setOnDismissListener(() -> {
			popupWindow = null;
			containerView = null;
			isAnimatingOut = false;
		});
		
		int offsetX = anchor.getWidth() - popW;
		
		containerView.setAlpha(0f);
		containerView.setScaleX(0.92f);
		containerView.setScaleY(0.92f);
		containerView.setPivotX(popW);
		containerView.setPivotY(0f);
		
		popupWindow.showAsDropDown(anchor, offsetX, 0);
		containerView.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(150).setInterpolator(new DecelerateInterpolator()).start();
	}
	
	private View buildRow(final MenuItem item, boolean hasIcons, int itemH, int padH, int iconSz,
	int iconGap, int cText, int cIcon, int cRipple, int cPrimary, float density) {
		LinearLayout row = new LinearLayout(context);
		row.setOrientation(LinearLayout.HORIZONTAL);
		row.setGravity(Gravity.CENTER_VERTICAL);
		
		int minHeight = dp(density, ITEM_H_DP);
		row.setMinimumHeight(minHeight);
		row.setPadding(padH, dp(density, 4f), padH, dp(density, 4f));
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			int rippleAlpha = (int) (255 * 0.12f);
			ColorStateList ripple = ColorStateList.valueOf(Color.argb(rippleAlpha,
			Color.red(cText), Color.green(cText), Color.blue(cText)));
			row.setBackground(new RippleDrawable(ripple, null, new ColorDrawable(cRipple)));
		} else {
			row.setBackgroundDrawable(new ColorDrawable(cRipple));
		}
		
		if (hasIcons) {
			ImageView iv = new ImageView(context);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(iconSz, iconSz);
			lp.setMarginEnd(iconGap);
			iv.setLayoutParams(lp);
			iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
			if (item.getIcon() != null) {
				iv.setImageDrawable(item.getIcon().getConstantState().newDrawable().mutate());
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					int iconColor = item.isEnabled() ? cIcon : alphaOf(cIcon, 0.38f);
					iv.setImageTintList(ColorStateList.valueOf(iconColor));
				}
			} else {
				iv.setVisibility(View.INVISIBLE);
			}
			row.addView(iv);
		}
		
		TextView tv = new TextView(context);
		tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
		tv.setText(item.getTitle());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SP);
		tv.setTextColor(item.isEnabled() ? cText : alphaOf(cText, 0.38f));
		Typeface typeface = context.getResources().getFont(R.font.f4);
		tv.setTypeface(typeface, Typeface.BOLD);
		tv.setSingleLine(true);
		tv.setEllipsize(TextUtils.TruncateAt.END);
		row.addView(tv);
		
		if (item.isCheckable()) {
			CheckBox cb = new CheckBox(context);
			cb.setChecked(item.isChecked());
			cb.setClickable(false);
			cb.setFocusable(false);
			
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				ColorStateList colorStateList = new ColorStateList(
				new int[][] {
					new int[] { android.R.attr.state_checked },
					new int[] { -android.R.attr.state_checked }
				},
				new int[] { cPrimary, cIcon }
				);
				cb.setButtonTintList(colorStateList);
			}
			
			LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT
			);
			cbLp.setMarginStart(iconGap);
			cb.setLayoutParams(cbLp);
			row.addView(cb);
			
			row.setEnabled(item.isEnabled());
			if (item.isEnabled()) {
				row.setOnClickListener(v -> {
					boolean newChecked = !item.isChecked();
					item.setChecked(newChecked);
					cb.setChecked(newChecked);
					if (clickListener != null) clickListener.onMenuItemClick(item);
				});
			}
		} else {
			row.setEnabled(item.isEnabled());
			if (item.isEnabled()) {
				row.setOnClickListener(v -> {
					if (clickListener != null) clickListener.onMenuItemClick(item);
					dismiss();
				});
			}
		}
		
		return row;
	}
	
	private View buildDivider(int color, int heightPx, float density) {
		View divider = new View(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.MATCH_PARENT, heightPx);
		int margin = dp(density, 8f);
		lp.setMargins(0, margin, 0, margin);
		divider.setLayoutParams(lp);
		divider.setBackgroundColor(color);
		return divider;
	}
	
	private void animateOut(Runnable onEnd) {
		if (containerView == null) {
			if (onEnd != null) onEnd.run();
			return;
		}
		containerView.animate().alpha(0f).scaleX(0.92f).scaleY(0.92f).setDuration(80).setInterpolator(new AccelerateInterpolator()).withEndAction(onEnd).start();
	}
	
	private int alphaOf(int color, float alpha) {
		return Color.argb(Math.round(Color.alpha(color) * alpha), Color.red(color), Color.green(color), Color.blue(color));
	}
	
	private int dp(float density, float dp) {
		return Math.round(dp * density);
	}
}