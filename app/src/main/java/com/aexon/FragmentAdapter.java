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

package com.aexon;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.aexon.material.viewpager.AexonFragmentStatePagerAdapter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FragmentAdapter extends AexonFragmentStatePagerAdapter {
	
	private final Context context;
	private int tabCount;
	
	public FragmentAdapter(@NonNull Context context, @NonNull FragmentManager manager) {
		super(manager);
		this.context = context;
	}
	
	public void setTabCount(int tabCount) {
		this.tabCount = tabCount;
	}
	
	@Override
	public int getCount() {
		return tabCount;
	}
	
	@NonNull
	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case 0:
			return new HomeFragmentActivity();
			case 1:
			return new AexonFragmentActivity();
			case 2:
			return new SettingsFragmentActivity();
			default:
			throw new IllegalArgumentException("Invalid position: " + position);
		}
	}
}