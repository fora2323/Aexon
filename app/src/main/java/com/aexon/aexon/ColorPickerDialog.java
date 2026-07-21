package com.aexon.aexon;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.aexon.theme.AexonTheme;
import com.aexon.material.color.AexonColorPickerView;
import com.aexon.material.color.AexonColorSliderBrightness;
import com.aexon.material.edittext.AexonEditText;
import com.aexon.material.dialog.AexonAlertDialog;
import com.aexon.R;

public class ColorPickerDialog {
	
	public interface OnColorConfirmed {
		void onConfirmed(int color);
	}
	
	private static final int DEFAULT_COLOR = 0xFFFFFFFF;
	private static boolean isShowing = false;
	
	public static void show(Context context, OnColorConfirmed callback) {
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
	
	private static void applyContainerStroke(LinearLayout container, int strokeColor, int strokeDp, float radiusDp, Context context) {
		float density = context.getResources().getDisplayMetrics().density;
		int strokePx = (int) (strokeDp * density + 0.5f);
		float radiusPx = radiusDp * density;
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