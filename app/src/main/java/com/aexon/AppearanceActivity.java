package com.aexon;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.SharedPreferences;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aexon.material.aexonswitch.AexonSwitch;
import com.aexon.material.button.AexonRadioButton;
import com.aexon.material.cardview.AexonCardView;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;
import com.aexon.theme.AexonTheme;
import com.aexon.theme.AexonThemeListener;
import com.aexon.aexon.ColorPickerDialog;
import com.aexon.aexon.AexonWindowHelper;
import com.aexon.aexon.animation.AexonAnimationCompat;
import com.aexon.material.dialog.AexonAlertDialog;

public class AppearanceActivity extends Activity {
	
	private final AexonThemeListener themeListener = (seedColor, theme) -> {
		_applyTheme(theme);
	};
	
	private LinearLayout toolbar;
	private LinearLayout linear1;
	private ImageView imageview1;
	private TextView textview1;
	private AexonCardView cardview2;
	private AexonCardView cardview3;
	private AexonCardView cardview4;
	private LinearLayout theme_container;
	private LinearLayout linear15;
	private LinearLayout divider;
	private LinearLayout linear17;
	private ImageView imageview5;
	private LinearLayout linear18;
	private AexonSwitch switch2;
	private TextView textview9;
	private TextView textview10;
	private AexonRadioButton follow_systm;
	private AexonRadioButton dark_mode;
	private AexonRadioButton light_mode;
	private LinearLayout linear11;
	private ImageView imageview4;
	private LinearLayout linear12;
	private AexonSwitch switch1;
	private TextView textview7;
	private TextView textview8;
	private LinearLayout linear10;
	private LinearLayout linear5;
	private LinearLayout linear9;
	private ImageView imageview3;
	private LinearLayout linear6;
	private TextView textview4;
	private TextView textview5;
	private TextView textview6;
	private LinearLayout palet_color;
	
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.appearance);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		AexonTheme.getInstance().addListener(themeListener);
		toolbar = findViewById(R.id.toolbar);
		linear1 = findViewById(R.id.linear1);
		imageview1 = findViewById(R.id.imageview1);
		textview1 = findViewById(R.id.textview1);
		cardview2 = findViewById(R.id.cardview2);
		cardview3 = findViewById(R.id.cardview3);
		cardview4 = findViewById(R.id.cardview4);
		theme_container = findViewById(R.id.theme_container);
		linear15 = findViewById(R.id.linear15);
		divider = findViewById(R.id.divider);
		linear17 = findViewById(R.id.linear17);
		imageview5 = findViewById(R.id.imageview5);
		linear18 = findViewById(R.id.linear18);
		switch2 = findViewById(R.id.switch2);
		textview9 = findViewById(R.id.textview9);
		textview10 = findViewById(R.id.textview10);
		follow_systm = findViewById(R.id.follow_systm);
		dark_mode = findViewById(R.id.dark_mode);
		light_mode = findViewById(R.id.light_mode);
		linear11 = findViewById(R.id.linear11);
		imageview4 = findViewById(R.id.imageview4);
		linear12 = findViewById(R.id.linear12);
		switch1 = findViewById(R.id.switch1);
		textview7 = findViewById(R.id.textview7);
		textview8 = findViewById(R.id.textview8);
		linear10 = findViewById(R.id.linear10);
		linear5 = findViewById(R.id.linear5);
		linear9 = findViewById(R.id.linear9);
		imageview3 = findViewById(R.id.imageview3);
		linear6 = findViewById(R.id.linear6);
		textview4 = findViewById(R.id.textview4);
		textview5 = findViewById(R.id.textview5);
		textview6 = findViewById(R.id.textview6);
		palet_color = findViewById(R.id.palet_color);
		sp = getSharedPreferences("-sharedAexon", Activity.MODE_PRIVATE);
		
		imageview1.setOnClickListener(_v -> onBackPressed());
		
		switch2.setOnCheckedChangeListener((_buttonView, _isChecked) -> {
			sp.edit().putBoolean("ax_theme", _isChecked).commit();
			AexonAnimationCompat.animateVisibility(linear1);
			if (_isChecked) {
				divider.setVisibility(View.GONE);
				linear17.setVisibility(View.GONE);
				AexonTheme.getInstance().setThemeMode(AexonTheme.MODE_FOLLOW_SYSTEM);
				_updateRadioButtons(AexonTheme.MODE_FOLLOW_SYSTEM);
			} else {
				divider.setVisibility(View.VISIBLE);
				linear17.setVisibility(View.VISIBLE);
				boolean systemGelap = (getResources().getConfiguration().uiMode
				& Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
				double detectedMode = systemGelap ? AexonTheme.MODE_DARK : AexonTheme.MODE_LIGHT;
				AexonTheme.getInstance().setThemeMode((int) detectedMode);
				sp.edit().putInt("theme_mode", (int) detectedMode).apply();
				_updateRadioButtons(detectedMode);
			}
		});
		
		linear10.setOnClickListener(_v -> ColorPickerDialog.show(AppearanceActivity.this, null));
	}
	
	private void initializeLogic() {
		imageview1.setClickable(true);
		imageview1.setFocusable(true);
		
		boolean dynamicSupported = AexonTheme.isDynamicColorSupported();
		if (!dynamicSupported) {
			cardview2.setVisibility(View.GONE);
		}
		
		boolean dynamicEnabled = sp.getBoolean("dynamic_color_enabled", false);
		if (dynamicSupported) {
			AexonTheme.getInstance().setDynamicColor(dynamicEnabled);
		}
		
		switch1.setChecked(AexonTheme.getInstance().isDynamicColorEnabled());
		switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
			sp.edit().putBoolean("dynamic_color_enabled", isChecked).apply();
			AexonTheme.getInstance().setDynamicColor(isChecked);
			_applyDynamicState(AexonTheme.getInstance());
			if (isChecked) {
				textview5.setText(getString(R.string.tag_dec_dynamic_on));
			} else {
				textview5.setText(getString(R.string.tag_dec_dynamic_off));
			}
		});
		_applyTheme(AexonTheme.getInstance());
		switch2.setChecked(sp.getBoolean("ax_theme", true));
		boolean newAndroid = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
		
		//check theme android 10+
		if (!newAndroid) {
			switch2.setChecked(false);
			switch2.setEnabled(false);
			switch2.setAlpha(0.4f);
			follow_systm.setEnabled(false);
			follow_systm.setAlpha(0.4f);
			divider.setVisibility(View.VISIBLE);
			linear17.setVisibility(View.VISIBLE);
			
			double savedMode = sp.getInt("theme_mode", AexonTheme.MODE_FOLLOW_SYSTEM);
			AexonTheme.getInstance().setThemeMode((int) savedMode);
			_updateRadioButtons(savedMode);
		} else {
			boolean followSystem = sp.getBoolean("ax_theme", false);
			switch2.setChecked(followSystem);
			
			
			if (followSystem) {
				AexonTheme.getInstance().setThemeMode(AexonTheme.MODE_FOLLOW_SYSTEM);
				divider.setVisibility(View.GONE);
				linear17.setVisibility(View.GONE);
				_updateRadioButtons(AexonTheme.MODE_FOLLOW_SYSTEM);
			} else {
				boolean systemGelap = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
				double detectedMode = systemGelap ? AexonTheme.MODE_DARK : AexonTheme.MODE_LIGHT;
				double savedMode = sp.getInt("theme_mode", (int) detectedMode);
				AexonTheme.getInstance().setThemeMode((int) savedMode);
				
				_updateRadioButtons(savedMode);
				divider.setVisibility(View.VISIBLE);
				linear17.setVisibility(View.VISIBLE);
			}
		}
		follow_systm.setOnClickListener(_v -> {
			AexonTheme.getInstance().setThemeMode(AexonTheme.MODE_FOLLOW_SYSTEM);
			sp.edit().putInt("theme_mode", AexonTheme.MODE_FOLLOW_SYSTEM).apply();
			_updateRadioButtons(AexonTheme.MODE_FOLLOW_SYSTEM);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				switch2.setChecked(true);
				sp.edit().putBoolean("ax_theme", true).commit();
				divider.setVisibility(View.GONE);
				linear17.setVisibility(View.GONE);
				AexonAnimationCompat.animateVisibility(linear1);
			}
		});
		
		dark_mode.setOnClickListener(_v -> {
			AexonTheme.getInstance().setThemeMode(AexonTheme.MODE_DARK);
			sp.edit().putInt("theme_mode", AexonTheme.MODE_DARK).apply();
			_updateRadioButtons(AexonTheme.MODE_DARK);
		});
		
		light_mode.setOnClickListener(_v -> {
			AexonTheme.getInstance().setThemeMode(AexonTheme.MODE_LIGHT);
			sp.edit().putInt("theme_mode", AexonTheme.MODE_LIGHT).apply();
			_updateRadioButtons(AexonTheme.MODE_LIGHT);
		});
	}
	
	
	@Override
	public void onBackPressed() {
		super.finish();
		overridePendingTransition(R.anim.fade_in_back, R.anim.fade_out_back);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		AexonTheme.getInstance().removeListener(themeListener);
	}
	public void _applyTheme(final AexonTheme _theme) {
		AexonWindowHelper.setWindowStyle(getWindow(), _theme.getColorSurface());
		_applyDynamicState(_theme);
		palet_color.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b, int c, int d) { this.setCornerRadius(a); this.setStroke(b, c); this.setColor(d); return this; } }.getIns((int)SketchwareUtil.getDip(getApplicationContext(), (int)(50)), (int)SketchwareUtil.getDip(getApplicationContext(), (int)(1)), _theme.getColorOnSurface(), _theme.getSeedColor()));
		toolbar.setBackgroundColor(_theme.getColorSurface());
		linear1.setBackgroundColor(_theme.getColorSurface());
		divider.setBackgroundColor(_theme.getColorOutlineVariant());
		imageview1.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		imageview1.setBackground(AexonDrawable.oval(this, _theme.getColorSurfaceContainer()));
		imageview1.setClickable(true);
		imageview1.setFocusable(true);
		imageview3.setColorFilter(_theme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP);
		imageview4.setColorFilter(_theme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP);
		imageview5.setColorFilter(_theme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP);
		textview1.setTextColor(_theme.getColorOnSurface());
		textview4.setTextColor(_theme.getColorOnSurface());
		textview5.setTextColor(_theme.getColorOnSurfaceVariant());
		textview6.setTextColor(_theme.getColorOnSurfaceVariant());
		textview7.setTextColor(_theme.getColorOnSurface());
		textview8.setTextColor(_theme.getColorOnSurfaceVariant());
		textview9.setTextColor(_theme.getColorOnSurface());
		textview10.setTextColor(_theme.getColorOnSurfaceVariant());
		follow_systm.setTextColor(_theme.getColorOnSurfaceVariant());
		dark_mode.setTextColor(_theme.getColorOnSurfaceVariant());
		light_mode.setTextColor(_theme.getColorOnSurfaceVariant());
		linear11.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).ripple(_theme.getColorOnSurface()).build().build(AppearanceActivity.this));
		linear11.setClickable(true);
		linear11.setFocusable(true);
		
		theme_container.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).ripple(_theme.getColorOnSurface()).build().build(AppearanceActivity.this));
		theme_container.setClickable(true);
		theme_container.setFocusable(true);
	}
	
	
	public void _applyDynamicState(final AexonTheme _theme) {
		boolean dynamicActive = _theme.isDynamicColorEnabled();
		if (dynamicActive) {
			linear10.setBackground(new AexonDrawable.Builder(Color.TRANSPARENT).build().build(AppearanceActivity.this));
			linear10.setClickable(false);
			linear10.setFocusable(false);
			imageview3.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		} else {
			linear10.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).ripple(_theme.getColorOnSurface()).build().build(AppearanceActivity.this));
			linear10.setClickable(true);
			linear10.setFocusable(true);
			imageview3.setColorFilter(_theme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP);
		}
	}
	
	
	public void _updateRadioButtons(final double _mode) {
		follow_systm.setChecked(_mode == AexonTheme.MODE_FOLLOW_SYSTEM);
		dark_mode.setChecked(_mode == AexonTheme.MODE_DARK);
		light_mode.setChecked(_mode == AexonTheme.MODE_LIGHT);
	}
	
}