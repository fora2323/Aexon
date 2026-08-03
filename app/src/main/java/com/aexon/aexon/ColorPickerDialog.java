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

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.aexon.R;
import com.aexon.SketchwareUtil;
import com.aexon.material.color.AexonColorPickerView;
import com.aexon.material.color.AexonColorSliderBrightness;
import com.aexon.material.dialog.AexonAlertDialog;
import com.aexon.widget.AexonEditText;
import com.aexon.theme.AexonTheme;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ColorPickerDialog {
	
	public interface OnColorConfirmed {
		void onConfirmed(int color);
	}
	
	private static final int DEFAULT_COLOR = 0xFF80D4D6;
	private static boolean isShowing = false;
	
	private ColorPickerDialog() {
		throw new UnsupportedOperationException("No instances");
	}
	
	public static void show(@NonNull Context context, @Nullable OnColorConfirmed callback) {
		if (isShowing) return;
		isShowing = true;
		AexonTheme theme = AexonTheme.getInstance();
		int currentColor = theme.getSeedColor();
		
		View dialogView = LayoutInflater.from(context).inflate(R.layout.picker_color, null);
		
		AexonColorPickerView colorPickerView = dialogView.findViewById(R.id.view1);
		AexonColorSliderBrightness sliderBrightness = dialogView.findViewById(R.id.view3);
		LinearLayout hexContainer = dialogView.findViewById(R.id.hex_container);
		AexonEditText inputHex = dialogView.findViewById(R.id.input_hex);
		
		sliderBrightness.setColorPickerView(colorPickerView);
		sliderBrightness.setStrokeColor(theme.getColorOutline());
		
		applyContainerStroke(hexContainer, theme.getColorSurfaceVariant(), 1, 8, context);
		
		inputHex.setOnFocusChangeListener((v, hasFocus) -> {
			if (hasFocus) {
				applyContainerStroke(hexContainer, theme.getColorPrimary(), 2, 8, context);
			} else {
				applyContainerStroke(hexContainer, theme.getColorSurfaceVariant(), 1, 8, context);
			}
		});
		
		colorPickerView.post(() -> {
			colorPickerView.setColor(currentColor);
			sliderBrightness.setUpdateFromColorPicker(currentColor);
			inputHex.setText(colorToHex(currentColor));
			inputHex.setSelection(inputHex.getText().length());
		});
		
		colorPickerView.setColorListener((colorInt, hexColor) -> {
			sliderBrightness.setUpdateFromColorPicker(colorInt);
			if (!inputHex.isFocused()) {
				inputHex.setText(colorToHex(colorInt));
			}
		});
		
		inputHex.addTextChangedListener(new TextWatcher() {
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void afterTextChanged(Editable s) {
				if (!inputHex.isFocused()) return;
				String text = s.toString();
				if (!text.startsWith("#")) {
					inputHex.removeTextChangedListener(this);
					inputHex.setText("#" + text.replace("#", ""));
					inputHex.setSelection(inputHex.getText().length());
					inputHex.addTextChangedListener(this);
					return;
				}
				String hex = text.trim();
				if (hex.length() == 7) {
					try {
						int parsed = Color.parseColor(hex);
						colorPickerView.setColor(parsed);
						sliderBrightness.setUpdateFromColorPicker(parsed);
					} catch (IllegalArgumentException ignored) {}
				}
			}
		});
		
		inputHex.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(7) });
		
		AexonAlertDialog builder = new AexonAlertDialog(context);
		builder.setTitle(R.string.tag_title_dialog_picker);
		builder.setView(dialogView);
		builder.setPositiveButton(R.string.tag_btn_ok, (d, which) -> {
			int picked = colorPickerView.getColor();
			theme.setSeedColor(picked);
			if (callback != null) callback.onConfirmed(picked);
		});
		builder.setNeutralButton(R.string.tag_btn_reset, (d, which) -> {
			theme.setSeedColor(DEFAULT_COLOR);
		});
		builder.setNegativeButton(R.string.tag_btn_cancel, null);
		AlertDialog alertDialog = builder.show();
		alertDialog.setOnDismissListener(d -> isShowing = false);
		
		if (alertDialog.getWindow() != null) {
			alertDialog.getWindow().setLayout(
			(int) (context.getResources().getDisplayMetrics().widthPixels * 0.85f),
			WindowManager.LayoutParams.WRAP_CONTENT
			);
		}
	}
	
	private static void applyContainerStroke(@NonNull LinearLayout container, int strokeColor, int strokeDp, float radiusDp, @NonNull Context context) {
		int strokePx = SketchwareUtil.dpToPx(context, strokeDp);
		float radiusPx = SketchwareUtil.getDip(context, (int) radiusDp);
		GradientDrawable bg = new GradientDrawable();
		bg.setShape(GradientDrawable.RECTANGLE);
		bg.setCornerRadius(radiusPx);
		bg.setColor(Color.TRANSPARENT);
		bg.setStroke(strokePx, strokeColor);
		container.setBackground(bg);
	}
	
	private static String colorToHex(int color) {
		return String.format("#%06X", 0xFFFFFF & color);
	}
}