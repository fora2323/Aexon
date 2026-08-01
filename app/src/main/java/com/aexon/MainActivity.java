package com.aexon;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.Intent;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import com.aexon.material.viewpager.AexonViewPager;
import com.aexon.view.AexonFloatingButton;
import com.aexon.view.AexonNavigationBar;
import com.aexon.viewx.AexonToolbarView;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;
import com.aexon.material.toasty.AexonToast;
import com.aexon.theme.AexonTheme;
import com.aexon.theme.AexonThemeListener;
import com.aexon.aexon.DialogCustom;
import com.aexon.aexon.AexonWindowHelper;
import com.aexon.aexon.animation.AexonAnimationCompat;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.content.pm.ShortcutInfo;



public class MainActivity extends Activity {
	
	private final AexonThemeListener themeListener = (seedColor, theme) -> {
		_applyTheme(theme);
	};
	private LinearLayout root_view;
	
	private AexonToolbarView toolbar;
	private FrameLayout sub_container;
	private AexonViewPager viewpager1;
	private AexonFloatingButton fab;
	private AexonNavigationBar aexonnavigationbbar1;
	
	private Intent ax_intent = new Intent();
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			getWindow().setDecorFitsSystemWindows(true);
		}
		root_view = findViewById(R.id.root_view);
		toolbar = findViewById(R.id.toolbar);
		sub_container = findViewById(R.id.sub_container);
		viewpager1 = findViewById(R.id.viewpager1);
		fab = findViewById(R.id.fab);
		aexonnavigationbbar1 = findViewById(R.id.aexonnavigationbbar1);
		
		viewpager1.setOnPageScrollListener(new AexonViewPager.OnPageScrollListener() {
			@Override
			public void onPageScrolled(int _position, float _positionOffset, int _positionOffsetPixels) {
				
			}
			
			@Override
			public void onPageSelected(int _position) {
				AexonTheme theme = AexonTheme.getInstance();
				if (_position == 0) {
					aexonnavigationbbar1.setChecked(0);
					toolbar.setTitle(SketchwareUtil.getString(MainActivity.this, R.string.tag_title_home_toolbar));
					toolbar.setSubtitle(SketchwareUtil.getString(MainActivity.this, R.string.tag_sub_title_home_toolbar));
					toolbar.setItemVisible(R.id.action_power, true);
					toolbar.setItemVisible(R.id.action_info, false);
					_setStatus();
				}
				if (_position == 1) {
					aexonnavigationbbar1.setChecked(1);
					toolbar.setTitle(SketchwareUtil.getString(MainActivity.this, R.string.tag_title_app_toolbar));
					toolbar.setSubtitle(SketchwareUtil.getString(MainActivity.this, R.string.tag_sub_title_app_toolbar));
					toolbar.setItemVisible(R.id.action_power, false);
					toolbar.setItemVisible(R.id.action_info, false);
					fab.setVisibility(View.GONE);
				}
				if (_position == 2) {
					aexonnavigationbbar1.setChecked(2);
					toolbar.setTitle(SketchwareUtil.getString(MainActivity.this, R.string.tag_title_settings_toolbar));
					toolbar.setSubtitle(SketchwareUtil.getString(MainActivity.this, R.string.tag_sub_title_settings_toolbar));
					toolbar.setItemVisible(R.id.action_power, false);
					toolbar.setItemVisible(R.id.action_info, true);
					fab.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void onPageScrollStateChanged(int _scrollState) {
				
			}
		});
		
		fab.setOnClickListener(_v -> {
			ax_intent.setClass(MainActivity.this, TerminalActivity.class);
			startActivity(ax_intent);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		});
	}
	
private final Aexon.OnBinderReceivedListener mainBinderReceived = () -> {
	runOnUiThread(() -> {
		if (viewpager1.getCurrentItem() == 0) _setStatus();
	});
};

private final Aexon.OnBinderDeadListener mainBinderDead = () -> {
	runOnUiThread(() -> {
		if (viewpager1.getCurrentItem() == 0) _setStatus();
	});
};
	private void initializeLogic() {
		FragmentAdapter fragment = new FragmentAdapter(getApplicationContext(), getFragmentManager());
		fragment.setTabCount(3);
		viewpager1.setAdapter(fragment);
		viewpager1.setCurrentItem(0);
		viewpager1.setOffscreenPageLimit(3);
		
		aexonnavigationbbar1.setOnItemSelectedListener(new AexonNavigationBar.OnItemSelectedListener() {
			@Override
			public void onItemSelected(int index, int itemId) {
				viewpager1.setCurrentItem(index, true);
			}
		});
		
		toolbar.setItemVisible(R.id.action_power, true);
		toolbar.setItemVisible(R.id.action_info, false);
		toolbar.setOnMenuItemClickListener((itemId, view) -> {
			if (itemId == R.id.action_power) {
				DialogCustom dialog = new DialogCustom(MainActivity.this);
				dialog.setOwnerActivity(MainActivity.this);
				dialog.setOnSwipeListener(new DialogCustom.OnSwipeListener() {
					@Override
					public void onSwipeUp() {
						Aexon.stopDaemonPermanently();
					}
					@Override
					public void onSwipeDown() {
						Aexon.stopDaemon();
					}
				});
				dialog.show();
			} else if (itemId == R.id.action_info) {
				InfoDialogFragmentActivity bottomSheet = new InfoDialogFragmentActivity();
				bottomSheet.show(getFragmentManager(), "InfoDialog");
			}
		});
		_applyTheme(AexonTheme.getInstance());
		fab.setElevation((float)SketchwareUtil.getDip(getApplicationContext(), (int)(4)));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
			ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
			if (shortcutManager != null) {
				Intent shortcutIntent = new Intent(this, TerminalActivity.class);
				shortcutIntent.setAction(Intent.ACTION_VIEW);
				ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "shortcut_terminal").setShortLabel("Open Terminal").setIcon(Icon.createWithResource(this, R.drawable.ic_terminal_2)).setIntent(shortcutIntent).build();
				shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
			}
		}
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		_applyTheme(AexonTheme.getInstance());
	}
	
	@Override
	public void onStart() {
		super.onStart();
		AexonTheme.getInstance().addListener(themeListener);
		Aexon.addBinderReceivedListener(mainBinderReceived);
		Aexon.addBinderDeadListener(mainBinderDead);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		AexonTheme.getInstance().removeListener(themeListener);
		Aexon.removeBinderReceivedListener(mainBinderReceived);
		Aexon.removeBinderDeadListener(mainBinderDead);
	}
	public void _applyTheme(final AexonTheme _theme) {
		//nav
		aexonnavigationbbar1.setBackgroundColor(_theme.getColorSurfaceContainerHigh());
		ColorStateList iconTint = new ColorStateList(new int[][]{new int[]{ android.R.attr.state_checked }, new int[]{}}, new int[]{_theme.getColorPrimary(), _theme.getColorOnSurfaceVariant()});
		aexonnavigationbbar1.setItemIconTint(iconTint);
		aexonnavigationbbar1.setThumbColor(_theme.getColorPrimary());
		aexonnavigationbbar1.setRippleColor(_theme.getColorOnPrimaryDark());
		
		//fab
		fab.setBackgroundColor(_theme.getColorPrimary());
		fab.setRippleColor(_theme.getColorOnPrimaryDark());
		fab.setIconTint(_theme.getColorOnPrimary());
		
		//toolbar
		toolbar.setTitleColor(_theme.getColorPrimary());
		toolbar.setSubtitleColor(_theme.getColorOnSurfaceVariant());
		toolbar.setActionRippleColor(_theme.getColorOnSurface());
		toolbar.setActionIconTint(_theme.getColorOnSurface());
		
		//
		root_view.setBackgroundColor(_theme.getColorSurface());
		AexonWindowHelper.setWindowStyle(getWindow(), _theme.getColorSurface());
	}
	
	
	public void _setStatus() {
		if (Aexon.isBinder()) {
			toolbar.setItemVisible(R.id.action_power, true);
			fab.setVisibility(View.VISIBLE);
		} else {
			toolbar.setItemVisible(R.id.action_power, false);
			fab.setVisibility(View.GONE);
		}
	}
	
}
