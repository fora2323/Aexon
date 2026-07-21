package com.aexon;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;

import com.aexon.annotation.NonNull;
import com.aexon.material.viewpager.AexonFragmentStatePagerAdapter;

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