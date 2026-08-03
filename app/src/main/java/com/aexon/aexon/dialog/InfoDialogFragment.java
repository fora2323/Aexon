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
package com.aexon.aexon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aexon.material.bottomsheet.AexonBottomSheetFragment;
import com.aexon.theme.AexonTheme;
import com.aexon.widget.AexonCompatButton;
import com.aexon.widget.AexonImageView;
import com.aexon.SketchwareUtil;
import com.aexon.R;

public class InfoDialogFragment extends AexonBottomSheetFragment {
	
	private LinearLayout container;
	private LinearLayout handle;
	private AexonImageView imageview1;
	private TextView textview1;
	private TextView textview2;
	private TextView textview3;
	private LinearLayout linear1;
	private AexonCompatButton btn_donation;
	private AexonCompatButton btn_tl;
	private AexonCompatButton btn_wa;
	
	private final Intent i_aexon = new Intent();
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.info_layout, container, false);
		initialize(savedInstanceState, view);
		initializeLogic();
		return view;
	}
	
	private void initialize(@Nullable Bundle savedInstanceState, @NonNull View view) {
		container = view.findViewById(R.id.container);
		handle = view.findViewById(R.id.handle);
		imageview1 = view.findViewById(R.id.imageview1);
		textview1 = view.findViewById(R.id.textview1);
		textview2 = view.findViewById(R.id.textview2);
		textview3 = view.findViewById(R.id.textview3);
		linear1 = view.findViewById(R.id.linear1);
		btn_donation = view.findViewById(R.id.btn_donation);
		btn_tl = view.findViewById(R.id.btn_tl);
		btn_wa = view.findViewById(R.id.btn_wa);
	}
	
	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setOnShowListener(d -> {
			if (dialog.getWindow() != null) {
				dialog.getWindow().setDecorFitsSystemWindows(false);
			}
		});
		return dialog;
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (container != null) {
			final int originalPaddingBottom = container.getPaddingBottom();
			
			container.setOnApplyWindowInsetsListener((v, insets) -> {
				int navBarHeight = insets.getStableInsetBottom();
				v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), originalPaddingBottom + navBarHeight);
				return insets;
			});
			container.post(() -> container.requestApplyInsets());
		}
	}
	
	private void initializeLogic() {
		AexonTheme theme = AexonTheme.getInstance();
		Context context = getContext();
		
		// Donation button
		btn_donation.setButtonBackgroundColor(theme.getColorPrimary());
		btn_donation.setIconTint(theme.getColorOnPrimary());
		btn_donation.setTextColor(theme.getColorOnPrimary());
		btn_donation.setButtonRippleColor(theme.getColorOnPrimaryDark());
		btn_donation.setOnClickListener(v -> {
			i_aexon.setAction(Intent.ACTION_VIEW);
			i_aexon.setData(Uri.parse(SketchwareUtil.getString(getContext(), R.string.tag_url_donation)));
			startActivity(i_aexon);
		});
		
		// WhatsApp button
		btn_wa.setButtonBackgroundColor(theme.getColorPrimary());
		btn_wa.setIconTint(theme.getColorOnPrimary());
		btn_wa.setTextColor(theme.getColorOnPrimary());
		btn_wa.setButtonRippleColor(theme.getColorOnPrimaryDark());
		btn_wa.setOnClickListener(v -> {
			i_aexon.setAction(Intent.ACTION_VIEW);
			i_aexon.setData(Uri.parse(SketchwareUtil.getString(getContext(), R.string.url_wa)));
			startActivity(i_aexon);
		});
		
		// Telegram button
		btn_tl.setButtonBackgroundColor(theme.getColorPrimary());
		btn_tl.setIconTint(theme.getColorOnPrimary());
		btn_tl.setTextColor(theme.getColorOnPrimary());
		btn_tl.setButtonRippleColor(theme.getColorOnPrimaryDark());
		btn_tl.setOnClickListener(v -> {
			i_aexon.setAction(Intent.ACTION_VIEW);
			i_aexon.setData(Uri.parse(SketchwareUtil.getString(getContext(), R.string.url_tele)));
			startActivity(i_aexon);
		});
		
		if (context != null) {
			float containerRadius = SketchwareUtil.dpToPx(context, 18);
			GradientDrawable containerBg = new GradientDrawable();
			containerBg.setShape(GradientDrawable.RECTANGLE);
			containerBg.setCornerRadii(new float[]{containerRadius, containerRadius, containerRadius, containerRadius, 0, 0, 0, 0});
			containerBg.setColor(theme.getColorSurfaceContainer());
			container.setBackground(containerBg);
			
			float handleRadius = SketchwareUtil.dpToPx(context, 12);
			GradientDrawable handleBg = new GradientDrawable();
			handleBg.setShape(GradientDrawable.RECTANGLE);
			handleBg.setCornerRadius(handleRadius);
			handleBg.setColor(theme.getColorOnPrimaryDark());
			handle.setBackground(handleBg);
		}
		
		textview1.setTextColor(theme.getColorOnSurface());
		textview2.setTextColor(theme.getColorPrimary());
		textview3.setTextColor(theme.getColorOnSurfaceVariant());
	}
}