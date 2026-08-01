package com.aexon.viewx;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.aexon.R;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AexonToolbarView extends LinearLayout {
	
	private TextView tvTitle;
	private TextView tvSubtitle;
	private LinearLayout actionContainer;
	
	@NonNull
	private final List<ImageButton> actionButtons = new ArrayList<>();
	
	private int iconTint = 0;
	private int iconSizePx = 0;
	private int rippleColor = 0x33000000;
	
	@Nullable
	private OnMenuItemClickListener menuItemClickListener;
	
	public interface OnMenuItemClickListener {
		void onMenuItemClick(@IdRes int itemId, @NonNull View view);
	}
	
	public AexonToolbarView(@NonNull Context context) {
		super(context);
		init(context, null);
	}
	
	public AexonToolbarView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
		setOrientation(HORIZONTAL);
		
		LayoutInflater.from(context).inflate(R.layout.aexon_toolbar, this, true);
		
		tvTitle = findViewById(R.id.tvTitle);
		tvSubtitle = findViewById(R.id.tvSubtitle);
		actionContainer = findViewById(R.id.actionContainer);
		
		int menuRes = 0;
		
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AexonToolbarView);
			
			String title = a.getString(R.styleable.AexonToolbarView_ax_title);
			String subtitle = a.getString(R.styleable.AexonToolbarView_ax_subtitle);
			
			float titleSize = a.getDimension(R.styleable.AexonToolbarView_ax_titleTextSize, tvTitle.getTextSize());
			float subtitleSize = a.getDimension(R.styleable.AexonToolbarView_ax_subtitleTextSize, tvSubtitle.getTextSize());
			
			int titleColor = a.getColor(R.styleable.AexonToolbarView_ax_titleColor, tvTitle.getCurrentTextColor());
			int subtitleColor = a.getColor(R.styleable.AexonToolbarView_ax_subtitleColor, tvSubtitle.getCurrentTextColor());
			
			menuRes = a.getResourceId(R.styleable.AexonToolbarView_ax_tool_menu, 0);
			iconTint = a.getColor(R.styleable.AexonToolbarView_ax_actionIconTint, 0);
			iconSizePx = (int) a.getDimension(R.styleable.AexonToolbarView_ax_actionIconSize, dp(24));
			
			if (title != null) setTitle(title);
			if (subtitle != null) setSubtitle(subtitle);
			
			setTitleTextSize(titleSize);
			setSubtitleTextSize(subtitleSize);
			setTitleColor(titleColor);
			setSubtitleColor(subtitleColor);
			
			a.recycle();
		}
		
		if (menuRes != 0) {
			inflateMenu(menuRes);
		}
	}
	
	private int dp(int value) {
		float density = getContext().getResources().getDisplayMetrics().density;
		return Math.round(value * density);
	}
	
	public void inflateMenu(@MenuRes int menuRes) {
		actionContainer.removeAllViews();
		actionButtons.clear();
		
		PopupMenu p = new PopupMenu(getContext(), null);
		p.inflate(menuRes);
		Menu menu = p.getMenu();
		
		for (int i = 0; i < menu.size(); i++) {
			MenuItem item = menu.getItem(i);
			addActionItem(item.getItemId(), item.getIcon());
		}
	}
	
	public void addActionItem(@IdRes int itemId, @Nullable Drawable icon) {
		ImageButton button = new ImageButton(getContext());
		button.setId(itemId);
		button.setImageDrawable(icon);
		button.setScaleType(ImageButton.ScaleType.FIT_CENTER);
		button.setBackground(buildItemRipple(null, dp(20)));
		
		int padding = dp(6);
		button.setPadding(padding, padding, padding, padding);
		
		LayoutParams params = new LayoutParams(iconSizePx + padding * 2, iconSizePx + padding * 2);
		if (!actionButtons.isEmpty()) {
			params.setMarginStart(dp(4));
		}
		button.setLayoutParams(params);
		
		if (iconTint != 0) {
			button.setImageTintList(ColorStateList.valueOf(iconTint));
		}
		
		button.setOnClickListener(v -> {
			if (menuItemClickListener != null) {
				menuItemClickListener.onMenuItemClick(itemId, v);
			}
		});
		
		actionButtons.add(button);
		actionContainer.addView(button);
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
	
	public void setOnMenuItemClickListener(@Nullable OnMenuItemClickListener listener) {
		this.menuItemClickListener = listener;
	}
	
	public void setTitle(@NonNull String title) {
		tvTitle.setText(title);
	}
	
	public void setSubtitle(@NonNull String subtitle) {
		tvSubtitle.setText(subtitle);
	}
	
	public void setTitleTextSize(float sizePx) {
		tvTitle.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, sizePx);
	}
	
	public void setSubtitleTextSize(float sizePx) {
		tvSubtitle.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, sizePx);
	}
	
	public void setTitleColor(@ColorInt int color) {
		tvTitle.setTextColor(color);
	}
	
	public void setSubtitleColor(@ColorInt int color) {
		tvSubtitle.setTextColor(color);
	}
	
	public void setActionIconTint(@ColorInt int color) {
		this.iconTint = color;
		ColorStateList tint = ColorStateList.valueOf(color);
		for (ImageButton button : actionButtons) {
			button.setImageTintList(tint);
		}
	}
	
	public void setActionRippleColor(@ColorInt int color) {
		this.rippleColor = color;
		for (ImageButton button : actionButtons) {
			button.setBackground(buildItemRipple(null, dp(20)));
		}
	}
	
	public void setItemVisible(@IdRes int itemId, boolean visible) {
		View v = findActionButton(itemId);
		if (v != null) {
			v.setVisibility(visible ? VISIBLE : GONE);
		}
	}
	
	public void setItemIcon(@IdRes int itemId, @DrawableRes int iconRes) {
		ImageButton button = findActionButton(itemId);
		if (button != null) {
			button.setImageDrawable(getContext().getDrawable(iconRes));
		}
	}
	
	@Nullable
	private ImageButton findActionButton(@IdRes int itemId) {
		for (ImageButton button : actionButtons) {
			if (button.getId() == itemId) return button;
		}
		return null;
	}
}