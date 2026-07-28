package com.aexon.corex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AexonFragmentAdapter extends FragmentStatePagerAdapter {
	
	private final List<Fragment> fragmentList = new ArrayList<>();
	private final List<String> fragmentTitleList = new ArrayList<>();
	
	public AexonFragmentAdapter(@NonNull FragmentManager fm) {
		super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
	}
	
	public void setFragments(@NonNull Fragment f1, @NonNull Fragment f2, @NonNull Fragment f3, @NonNull Fragment f4) {
		fragmentList.clear();
		fragmentTitleList.clear();
		fragmentList.add(f1);
		fragmentList.add(f2);
		fragmentList.add(f3);
		fragmentList.add(f4);
		notifyDataSetChanged();
	}
	
	public void addFragment(@NonNull Fragment fragment) {
		fragmentList.add(fragment);
		fragmentTitleList.add("");
	}
	
	public void addFragment(@NonNull Fragment fragment, @NonNull String title) {
		fragmentList.add(fragment);
		fragmentTitleList.add(title);
	}
	
	@NonNull
	@Override
	public Fragment getItem(int position) {
		return fragmentList.get(position);
	}
	
	@Override
	public int getCount() {
		return fragmentList.size();
	}
	
	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		if (position < fragmentTitleList.size()) {
			return fragmentTitleList.get(position);
		}
		return super.getPageTitle(position);
	}
}