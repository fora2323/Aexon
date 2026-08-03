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
package com.aexon.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.aexon.theme.AexonTheme;
import com.aexon.theme.AexonThemeListener;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AexonRadioButton extends RadioButton {
	
	private final AexonThemeListener themeListener = (view, theme) -> applyTheme(theme);
	
	public AexonRadioButton(@NonNull Context context) {
		super(context);
		init();
	}
	
	public AexonRadioButton(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public AexonRadioButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	private void init() {
		applyTheme(AexonTheme.getInstance());
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		AexonTheme.getInstance().addListener(themeListener);
		applyTheme(AexonTheme.getInstance());
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		AexonTheme.getInstance().removeListener(themeListener);
	}
	
	private void applyTheme(@NonNull AexonTheme theme) {
		int colorPrimary = theme.getColorPrimary();
		int colorOnSurface = theme.getColorOnSurface();
		int colorOutline = theme.getColorOutline();
		
		setButtonTintList(new ColorStateList(new int[][] {new int[] { -android.R.attr.state_enabled, android.R.attr.state_checked }, new int[] { -android.R.attr.state_enabled, -android.R.attr.state_checked }, new int[] { android.R.attr.state_checked }, new int[] {}}, new int[] {withAlpha(colorPrimary, 0.38f), withAlpha(colorOutline, 0.38f),colorPrimary, colorOutline}));
		applyRippleBackground(colorPrimary, colorOnSurface);
		setTextColor(new ColorStateList(new int[][] {new int[] { -android.R.attr.state_enabled }, new int[] {}}, new int[] {withAlpha(colorOnSurface, 0.38f), colorOnSurface}));
	}
	
	private void applyRippleBackground(int colorPrimary, int colorOnSurface) {
		ColorStateList rippleColor = new ColorStateList(new int[][] {new int[] { android.R.attr.state_checked }, new int[] {}}, new int[] {withAlpha(colorPrimary, 0.12f), withAlpha(colorOnSurface, 0.08f)});
		
		Drawable defaultBg = getBackground();
		if (defaultBg instanceof RippleDrawable) {
			RippleDrawable defaultRipple = (RippleDrawable) defaultBg.mutate();
			defaultRipple.setColor(rippleColor);
			setBackground(defaultRipple);
		} else {
			setBackground(new RippleDrawable(rippleColor, null, null));
		}
	}
	
	private int withAlpha(int color, float alpha) {
		return (color & 0x00FFFFFF) | (Math.round(alpha * 255) << 24);
	}
}