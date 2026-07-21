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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aexon.material.aexonloading.AexonLoading;
import com.aexon.material.viewpager.AexonViewPager;
import com.aexon.widget.AexonNavigationBar;
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
import com.aexon.core.AexonPermission;
import android.content.pm.PackageManager;

public class MainActivity extends Activity {
	
	private final AexonThemeListener themeListener = (seedColor, theme) -> {
		_applyTheme(theme);
	};
	
	private int previousPosition = 0;
	
	//request permission notification
	private static final int NOTIFICATION_REQUEST_CODE = 3070;
	private LinearLayout root_view;
	
	private LinearLayout container1;
	private LinearLayout container2;
	private LinearLayout container3;
	private LinearLayout toolbar;
	private FrameLayout sub_container;
	private LinearLayout toolbar_container1;
	private ImageView icon_power_ax;
	private ImageView icon_info_dev;
	private TextView textview1;
	private TextView textview2;
	private AexonViewPager viewpager1;
	private LinearLayout fab_container;
	private AexonNavigationBar aexonnavigationbbar1;
	private ImageView imageview1;
	private AexonLoading loadingaexon1;
	
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
		container1 = findViewById(R.id.container1);
		container2 = findViewById(R.id.container2);
		container3 = findViewById(R.id.container3);
		toolbar = findViewById(R.id.toolbar);
		sub_container = findViewById(R.id.sub_container);
		toolbar_container1 = findViewById(R.id.toolbar_container1);
		icon_power_ax = findViewById(R.id.icon_power_ax);
		icon_info_dev = findViewById(R.id.icon_info_dev);
		textview1 = findViewById(R.id.textview1);
		textview2 = findViewById(R.id.textview2);
		viewpager1 = findViewById(R.id.viewpager1);
		fab_container = findViewById(R.id.fab_container);
		aexonnavigationbbar1 = findViewById(R.id.aexonnavigationbbar1);
		imageview1 = findViewById(R.id.imageview1);
		loadingaexon1 = findViewById(R.id.loadingaexon1);
		
		icon_power_ax.setOnClickListener(_v -> {
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
		});
		
		icon_info_dev.setOnClickListener(_v -> {
			InfoDialogFragmentActivity bottomSheet = new InfoDialogFragmentActivity();
			bottomSheet.show(getFragmentManager(), "InfoDialog");
		});
		
		viewpager1.setOnPageScrollListener(new AexonViewPager.OnPageScrollListener() {
			@Override
			public void onPageScrolled(int _position, float _positionOffset, int _positionOffsetPixels) {
				
			}
			
			@Override
			public void onPageSelected(int _position) {
				AexonTheme theme = AexonTheme.getInstance();
				if (_position == 0) {
					aexonnavigationbbar1.setChecked(0);
					previousPosition = 0;
					textview1.setText(getString(R.string.home_tag));
					textview2.setText(getString(R.string.home_name));
					icon_info_dev.setVisibility(View.GONE);
					_setStatus();
				}
				if (_position == 1) {
					aexonnavigationbbar1.setChecked(1);
					if (!Aexon.isBinder()) {
						new Handler(Looper.getMainLooper()).postDelayed(() -> {
							viewpager1.setCurrentItem(previousPosition, false);
							AexonToast.make(getApplicationContext()).title(getString(R.string.tag_error)).message(getString(R.string.tag_aexon_not_running)).duration(AexonToast.LENGTH_SHORT).show();
						}, 200);
						return;
					}
					previousPosition = 1;
					textview1.setText(getString(R.string.app_tag));
					textview2.setText(getString(R.string.aexon_name));
					icon_info_dev.setVisibility(View.GONE);
					icon_power_ax.setVisibility(View.GONE);
					fab_container.setVisibility(View.GONE);
				}
				if (_position == 2) {
					aexonnavigationbbar1.setChecked(2);
					previousPosition = 2;
					textview1.setText(getString(R.string.settings_tag));
					textview2.setText(getString(R.string.settings_name));
					icon_info_dev.setVisibility(View.VISIBLE);
					icon_power_ax.setVisibility(View.GONE);
					fab_container.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void onPageScrollStateChanged(int _scrollState) {
				
			}
		});
		
		fab_container.setOnClickListener(_v -> {
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
		
		//navBar
		aexonnavigationbbar1.setOnItemSelectedListener((_index, itemId) -> {
			viewpager1.setCurrentItem(_index);
		});
		container3.setVisibility(View.VISIBLE);
		container2.setVisibility(View.GONE);
		loadingaexon1.setStart(2500).setOnAexonLoadingListener(new AexonLoading.OnAexonLoadingListener() {
			@Override
			public void complete() {
				container2.animate().alpha(1f).setDuration(300).withStartAction(() -> {
					container2.setVisibility(View.VISIBLE);
					container2.setAlpha(0f);
				}).start();
				container3.animate().alpha(0f).setDuration(300).withEndAction(() -> {
					container3.setVisibility(View.GONE);
				}).start();
				
				//request notification permission
				if (!AexonPermission.hasNotificationPermission(MainActivity.this)) {
					if (AexonPermission.shouldShowNotificationRationale(MainActivity.this)) {
						
					} else {
						AexonPermission.requestNotificationPermission(MainActivity.this, NOTIFICATION_REQUEST_CODE);
					}
				}
			}
		});
		_applyTheme(AexonTheme.getInstance());
		fab_container.setElevation((float)SketchwareUtil.getDip(getApplicationContext(), (int)(4)));
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
		previousPosition = viewpager1.getCurrentItem();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (viewpager1 != null) {
			viewpager1.setCurrentItem(previousPosition, false);
		}
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
	
	@Override
	public void onRequestPermissionsResult(int _requestCode, String[] _permissions, int[] _grantResults) {
		super.onRequestPermissionsResult(_requestCode, _permissions, _grantResults);
		if (_requestCode == NOTIFICATION_REQUEST_CODE) {
			if (_grantResults.length > 0 && _grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				SketchwareUtil.showMessage(getApplicationContext(), "Allowed");
			} else {
				SketchwareUtil.showMessage(getApplicationContext(), "Not allowed");
			}
		}
	}
	public void _applyTheme(final AexonTheme _theme) {
		//nav
		ColorStateList iconTint = new ColorStateList(new int[][]{new int[]{ android.R.attr.state_checked }, new int[]{}}, new int[]{_theme.getColorOnAccentContainer(), _theme.getColorOnSurfaceVariant()});
		aexonnavigationbbar1.setItemIconTint(iconTint);
		aexonnavigationbbar1.setThumbColor(_theme.getColorAccentContainer());
		aexonnavigationbbar1.setRippleColor((_theme.getColorOnAccentContainer() & 0x00FFFFFF) | 0x1A000000);
		aexonnavigationbbar1.setBackgroundColor(_theme.getColorSurfaceContainerHigh());
		root_view.setBackgroundColor(_theme.getColorSurface());
		textview1.setTextColor(_theme.getColorPrimary());
		textview2.setTextColor(_theme.getColorOnSurfaceVariant());
		icon_power_ax.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		icon_info_dev.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		icon_info_dev.setBackground(AexonDrawable.oval(this, _theme.getColorSurfaceContainer()));
		icon_info_dev.setClickable(true);
		icon_info_dev.setFocusable(true);
		
		icon_power_ax.setBackground(AexonDrawable.oval(this, _theme.getColorSurfaceContainer()));
		icon_power_ax.setClickable(true);
		icon_power_ax.setFocusable(true);
		AexonWindowHelper.setWindowStyle(getWindow(), _theme.getColorSurface());
		loadingaexon1.setTrackColor(_theme.getColorSurfaceContainer());
		loadingaexon1.setThumbColor(_theme.getColorPrimary());
		fab_container.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)SketchwareUtil.getDip(getApplicationContext(), (int)(12)), _theme.getColorPrimary()));
		fab_container.setBackground(new RippleDrawable(ColorStateList.valueOf(_theme.getColorOnSurface()), fab_container.getBackground(), fab_container.getBackground()));
		fab_container.setClickable(true);
		fab_container.setFocusable(true);
		imageview1.setColorFilter(_theme.getColorOnPrimary(), PorterDuff.Mode.SRC_ATOP);
	}
	
	
	public void _setStatus() {
		if (Aexon.isBinder()) {
			icon_power_ax.setVisibility(View.VISIBLE);
			fab_container.setVisibility(View.VISIBLE);
		} else {
			icon_power_ax.setVisibility(View.GONE);
			fab_container.setVisibility(View.GONE);
		}
	}
	
}