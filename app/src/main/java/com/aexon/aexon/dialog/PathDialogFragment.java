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
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.aexon.aexon.AexonClipboardHelper;
import com.aexon.core.AexonColorUtils;
import com.aexon.SketchwareUtil;
import com.aexon.Aexon;
import com.aexon.material.bottomsheet.AexonBottomSheetFragment;
import com.aexon.theme.AexonTheme;
import com.aexon.AexonDrawable;
import com.aexon.R;

@RequiresApi(api = Build.VERSION_CODES.O)
public class PathDialogFragment extends AexonBottomSheetFragment {
	
	private LinearLayout container;
	private LinearLayout handle;
	private TextView title;
	private TextView dec_tv;
	private LinearLayout linear2;
	private TextView btn_cancel;
	private LinearLayout linear3;
	private TextView btn_share;
	private TextView btn_copy;
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.info_path_layout, container, false);
		initialize(savedInstanceState, view);
		initializeLogic();
		return view;
	}
	
	private void initialize(@Nullable Bundle savedInstanceState, @NonNull View view) {
		container = view.findViewById(R.id.container);
		handle = view.findViewById(R.id.handle);
		title = view.findViewById(R.id.title);
		dec_tv = view.findViewById(R.id.dec_tv);
		linear2 = view.findViewById(R.id.linear2);
		btn_cancel = view.findViewById(R.id.btn_cancel);
		linear3 = view.findViewById(R.id.linear3);
		btn_share = view.findViewById(R.id.btn_share);
		btn_copy = view.findViewById(R.id.btn_copy);
		
		btn_cancel.setOnClickListener(v -> dismiss());
		
		btn_share.setOnClickListener(v -> {
			Context context = getContext();
			if (context != null) {
				String command = "adb shell " + Aexon.getPath(context);
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_TEXT, command);
				startActivity(Intent.createChooser(shareIntent, null));
			}
			dismiss();
		});
		
		btn_copy.setOnClickListener(v -> {
			Context context = getContext();
			if (context != null) {
				String command = "adb shell " + Aexon.getPath(context);
				AexonClipboardHelper.copy(context, command);
			}
			dismiss();
		});
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
		
		btn_copy.setTextColor(theme.getColorPrimary());
		btn_share.setTextColor(theme.getColorPrimary());
		btn_cancel.setTextColor(theme.getColorPrimary());
		title.setTextColor(theme.getColorOnSurface());
		dec_tv.setTextColor(theme.getColorOnSurfaceVariant());
		
		if (context == null) return;
		
		// Get path daemon
		String command = "adb shell " + Aexon.getPath(context);
		String note = "\n\n" + getString(R.string.tag_msg_cmd);
		float radius_medium = SketchwareUtil.getDimension(context, R.dimen.card_radius_medium);
		float radius_max = SketchwareUtil.getDimension(context, R.dimen.card_radius_max);
		
		SpannableStringBuilder message = new SpannableStringBuilder();
		SpannableString commandSpan = new SpannableString(command);
		commandSpan.setSpan(new TypefaceSpan("monospace"), 0, command.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		// Gunakan AexonColorUtils untuk mereset efek alpha
		commandSpan.setSpan(new BackgroundColorSpan(AexonColorUtils.setAlphaComponent(theme.getColorOnSurfaceVariant(), 128)), 0, command.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		message.append(commandSpan);
		message.append(note);
		dec_tv.setText(message);
		
		// Handle
		handle.setBackground(new AexonDrawable.Builder(theme.getColorOnPrimaryDark()).cornerRadius(radius_medium).build().build(context));
		
		// Button action
		btn_cancel.setBackground(new AexonDrawable.Builder(theme.getColorSurfaceContainer()).cornerRadius(radius_max).ripple(theme.getColorOnSurface()).build().build(context));
		btn_share.setBackground(new AexonDrawable.Builder(theme.getColorSurfaceContainer()).cornerRadius(radius_max).ripple(theme.getColorOnSurface()).build().build(context));
		btn_copy.setBackground(new AexonDrawable.Builder(theme.getColorSurfaceContainer()).cornerRadius(radius_max).ripple(theme.getColorOnSurface()).build().build(context));
		
		float containerRadius = SketchwareUtil.dpToPx(context, 18);
		GradientDrawable containerBg = new GradientDrawable();
		containerBg.setShape(GradientDrawable.RECTANGLE);
		containerBg.setCornerRadii(new float[]{containerRadius, containerRadius, containerRadius, containerRadius, 0, 0, 0, 0});
		containerBg.setColor(theme.getColorSurfaceContainer());
		container.setBackground(containerBg);
	}
}