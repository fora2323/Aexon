package com.aexon.material.viewpager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

import com.aexon.annotation.NonNull;
import com.aexon.annotation.Nullable;

public abstract class AexonFragmentStatePagerAdapter {
	
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