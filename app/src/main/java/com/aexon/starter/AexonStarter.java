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
package com.aexon.starter;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.app.DialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.aexon.Aexon;
import com.aexon.R;
import com.aexon.view.AexonLoading;
import com.aexon.theme.AexonTheme;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AexonStarter extends DialogFragment {
	
	public static final int MODE_SHIZUKU = 0;
	public static final int MODE_ROOT = 1;
	
	private AexonLoading loading;
	private int mode = MODE_SHIZUKU;
	@Nullable private AexonShizukuHelper shizuku;
	
	@NonNull
	public static AexonStarter newInstance(int mode, @Nullable AexonShizukuHelper shizuku) {
		AexonStarter fragment = new AexonStarter();
		fragment.mode = mode;
		fragment.shizuku = shizuku;
		return fragment;
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.aexon_loading, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		AexonTheme theme = AexonTheme.getInstance();
		loading = view.findViewById(R.id.loadingaexon1);
		loading.setTrackColor(theme.getColorSurfaceContainer());
		loading.setThumbColor(theme.getColorPrimary());
		loading.setIndeterminate(true);
		startExecution();
	}
	
	private void startExecution() {
		String path = Aexon.getPath(getActivity());
		if (mode == MODE_SHIZUKU) {
			new Thread(() -> {
				if (shizuku != null) {
					shizuku.exec(path);
				}
				waitForBinder();
			}).start();
		} else if (mode == MODE_ROOT) {
			new Thread(() -> {
				try {
					Runtime.getRuntime().exec(new String[]{"su", "-c", path});
				} catch (Exception e) {
					e.printStackTrace();
				}
				waitForBinder();
			}).start();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Dialog dialog = getDialog();
		if (dialog == null) return;
		Window window = dialog.getWindow();
		if (window == null) return;
		
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		window.setBackgroundDrawableResource(android.R.color.transparent);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		window.setDimAmount(0.6f);
		
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		window.setStatusBarColor(Color.TRANSPARENT);
		window.setNavigationBarColor(Color.TRANSPARENT);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
			window.setAttributes(lp);
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			window.setDecorFitsSystemWindows(false);
		}
		
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if (loading != null) {
			loading.setIndeterminate(false);
		}
	}
	
	private void waitForBinder() {
		while (!Aexon.isBinder()) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (getActivity() != null) {
			getActivity().runOnUiThread(() -> {
				if (!isDetached()) {
					dismiss();
				}
			});
		}
	}
}