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

package com.aexon.material.bottomsheet;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AexonBottomSheetFragment extends DialogFragment {
	
	private static final String KEY_STATE = "aexon_bottom_sheet_state";
	private int mSavedState = -1;
	
	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Context ctx = getActivity();
		if (ctx == null) {
			ctx = getContext();
		}
		AexonBottomSheetDialog dialog = new AexonBottomSheetDialog(ctx);
		if (savedInstanceState != null) {
			mSavedState = savedInstanceState.getInt(KEY_STATE, -1);
		}
		return dialog;
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (mSavedState != -1) {
			AexonBottomSheetBehavior behavior = getBehavior();
			behavior.setState(mSavedState);
			mSavedState = -1;
		}
	}
	
	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		AexonBottomSheetBehavior behavior = getBehavior();
		int state = behavior.getState();
		if (state == AexonBottomSheetBehavior.STATE_DRAGGING || state == AexonBottomSheetBehavior.STATE_SETTLING) {
			state = AexonBottomSheetBehavior.STATE_COLLAPSED;
		}
		outState.putInt(KEY_STATE, state);
	}
	
	@NonNull
	public AexonBottomSheetBehavior getBehavior() {
		Dialog dialog = getDialog();
		if (dialog instanceof AexonBottomSheetDialog) {
			return ((AexonBottomSheetDialog) dialog).getBehavior();
		}
		return new AexonBottomSheetBehavior();
	}
	
	public void setState(int state) {
		Dialog dialog = getDialog();
		if (dialog != null && dialog.isShowing()) {
			getBehavior().setState(state);
		} else {
			mSavedState = state;
		}
	}
	
	public int getState() {
		return getBehavior().getState();
	}
	
	@Override
	public int show(FragmentTransaction transaction, @Nullable String tag) {
		return super.show(transaction, tag);
	}
	
	public void show(@NonNull FragmentManager manager, @Nullable String tag) {
		super.show(manager, tag);
	}
}