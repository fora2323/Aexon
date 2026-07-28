package com.aexon.viewx;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.aexon.R;

public class AexonToolbarView extends LinearLayout {
	
	private TextView tvTitle;
	private TextView tvSubtitle;
	private ImageButton btnAction;
	
	@Nullable
	private OnActionClickListener actionClickListener;
	
	public interface OnActionClickListener {
		void onActionClick(@NonNull View view);
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
		
		LayoutInflater.from(context).inflate(R.layout.view_aexon_toolbar, this, true);
		
		tvTitle = findViewById(R.id.tvTitle);
		tvSubtitle = findViewById(R.id.tvSubtitle);
		btnAction = findViewById(R.id.btnAction);
		
		btnAction.setOnClickListener(v -> {
			if (actionClickListener != null) {
				actionClickListener.onActionClick(v);
			}
		});
		
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AexonToolbarView);
			
			String title = a.getString(R.styleable.AexonToolbarView_ax_title);
			String subtitle = a.getString(R.styleable.AexonToolbarView_ax_subtitle);
			
			float titleSize = a.getDimension(R.styleable.AexonToolbarView_ax_titleTextSize, tvTitle.getTextSize());
			float subtitleSize = a.getDimension(R.styleable.AexonToolbarView_ax_subtitleTextSize, tvSubtitle.getTextSize());
			
			int titleColor = a.getColor(R.styleable.AexonToolbarView_ax_titleColor, tvTitle.getCurrentTextColor());
			int subtitleColor = a.getColor(R.styleable.AexonToolbarView_ax_subtitleColor, tvSubtitle.getCurrentTextColor());
			
			Drawable actionIcon = a.getDrawable(R.styleable.AexonToolbarView_ax_actionIcon);
			int iconTint = a.getColor(R.styleable.AexonToolbarView_ax_actionIconTint, 0);
			float iconSize = a.getDimension(R.styleable.AexonToolbarView_ax_actionIconSize, 0);
			
			if (title != null) setTitle(title);
			if (subtitle != null) setSubtitle(subtitle);
			
			setTitleTextSize(titleSize);
			setSubtitleTextSize(subtitleSize);
			setTitleColor(titleColor);
			setSubtitleColor(subtitleColor);
			
			if (actionIcon != null) setActionIcon(actionIcon);
			if (iconTint != 0) setActionIconTint(iconTint);
			if (iconSize > 0) setActionIconSize((int) iconSize);
			
			a.recycle();
		}
	}
	
	public void setOnActionClickListener(@Nullable OnActionClickListener listener) {
		this.actionClickListener = listener;
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
	
	public void setActionIcon(@DrawableRes int resId) {
		btnAction.setImageDrawable(ContextCompat.getDrawable(getContext(), resId));
	}
	
	public void setActionIcon(@NonNull Drawable drawable) {
		btnAction.setImageDrawable(drawable);
	}
	
	public void setActionIconTint(@ColorInt int color) {
		btnAction.setImageTintList(ColorStateList.valueOf(color));
	}
	
	public void setActionIconSize(int sizePx) {
		LayoutParams params = (LayoutParams) btnAction.getLayoutParams();
		params.width = sizePx;
		params.height = sizePx;
		btnAction.setLayoutParams(params);
	}
	
	public void setActionVisible(boolean visible) {
		btnAction.setVisibility(visible ? VISIBLE : GONE);
	}
}