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

package com.aexon.material.viewpager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public abstract class AexonFragmentStatePagerAdapter {
	//add Fragment
	private final FragmentManager mFragmentManager;
	private FragmentTransaction mCurTransaction = null;
	private List<Fragment> mFragments = new ArrayList<>();
	private List<Fragment.SavedState> mSavedState = new ArrayList<>();
	
	public AexonFragmentStatePagerAdapter(FragmentManager fm) {
		mFragmentManager = fm;
	}
	
	@NonNull
	public abstract Fragment getItem(int position);
	public abstract int getCount();
	public void startUpdate(@NonNull ViewGroup container) {
	}
	
	@NonNull
	public Object instantiateItem(@NonNull ViewGroup container, int position) {
		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}
		
		while (mFragments.size() <= position) {
			mFragments.add(null);
			mSavedState.add(null);
		}
		
		Fragment fragment = mFragments.get(position);
		
		if (fragment == null) {
			fragment = getItem(position);
			
			if (position < mSavedState.size()
			&& mSavedState.get(position) != null) {
				fragment.setInitialSavedState(mSavedState.get(position));
			}
			
			mFragments.set(position, fragment);
			
			if (!fragment.isAdded()) {
				mCurTransaction.add(container.getId(), fragment);
			}
			
		} else {
			if (fragment.isDetached()) {
				mCurTransaction.attach(fragment);
			}
		}
		
		return fragment;
	}
	
	public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}
		Fragment fragment = (Fragment) object;
		Fragment.SavedState savedState = mFragmentManager.saveFragmentInstanceState(fragment);
		while (mSavedState.size() <= position) {
			mSavedState.add(null);
		}
		mSavedState.set(position, savedState);
		mFragments.set(position, null);
		mCurTransaction.remove(fragment);
	}
	
	public void finishUpdate(@NonNull ViewGroup container) {
		if (mCurTransaction != null) {
			mCurTransaction.commitAllowingStateLoss();
			mCurTransaction = null;
			mFragmentManager.executePendingTransactions();
		}
	}
	
	public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
		Fragment fragment = (Fragment) object;
		View v = fragment.getView();
		return v == view;
	}
	
	@Nullable
	public Fragment getFragmentAt(int position) {
		if (position >= 0 && position < mFragments.size()) {
			return mFragments.get(position);
		}
		return null;
	}
	
	void trimFragments(int newCount) {
		if (newCount >= mFragments.size()) return;
		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}
		for (int i = mFragments.size() - 1; i >= newCount; i--) {
			Fragment f = mFragments.get(i);
			if (f != null) {
				mCurTransaction.remove(f);
			}
			if (i < mSavedState.size()) {
				mSavedState.set(i, null);
			}
		}
		mFragments = new ArrayList<>(mFragments.subList(0, newCount));
		mSavedState = new ArrayList<>(mSavedState.subList(0, newCount));
	}
}