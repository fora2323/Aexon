package com.aexon.theme;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aexon.theme.hct.AexonSchemeCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AexonTheme {
	public static final int MODE_FOLLOW_SYSTEM = 0;
	public static final int MODE_DARK = 1;
	public static final int MODE_LIGHT = 2;
	
	private static final String PREF_NAME = "aexon_theme";
	private static final String KEY_SEED = "seed_color";
	private static final String KEY_DYNAMIC = "dynamic_color";
	private static final String KEY_THEME_MODE = "theme_mode";
	private static final String KEY_AMOLED = "amoled_mode";
	
	@ColorInt
	private static final int DEFAULT_SEED = 0xFF80D4D6;
	
	@Nullable
	private static AexonTheme instance;
	
	public static void init(@NonNull Context context) {
		instance = new AexonTheme(context.getApplicationContext());
	}
	
	@NonNull
	public static AexonTheme getInstance() {
		if (instance == null) throw new IllegalStateException("AexonTheme belum di-init. Panggil AexonTheme.init(context) di Application.");
		return instance;
	}
	
	private final Context appContext;
	private final SharedPreferences prefs;
	@ColorInt
	private int seedColor;
	private boolean dynamicColorEnabled;
	private boolean amoledModeEnabled;
	private int themeMode;
	private final Map<String, Integer> cache = new HashMap<>();
	private final List<AexonThemeListener> listeners = new ArrayList<>();
	
	private AexonTheme(@NonNull Context context) {
		this.appContext = context;
		prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		int savedSeed = prefs.getInt(KEY_SEED, DEFAULT_SEED);
		dynamicColorEnabled = prefs.getBoolean(KEY_DYNAMIC, false);
		amoledModeEnabled = prefs.getBoolean(KEY_AMOLED, false);
		themeMode = prefs.getInt(KEY_THEME_MODE, MODE_FOLLOW_SYSTEM);
		this.seedColor = savedSeed;
		
		if (dynamicColorEnabled && isDynamicColorSupported()) {
			applySeedInternal(getSystemSeedColor());
		} else {
			applySeedInternal(savedSeed);
		}
		
		appContext.registerComponentCallbacks(new ComponentCallbacks2() {
			@Override
			public void onConfigurationChanged(@NonNull Configuration newConfig) {
				if (themeMode == MODE_FOLLOW_SYSTEM || dynamicColorEnabled) {
					refresh();
				}
			}
			
			@Override
			public void onLowMemory() {}
			
			@Override
			public void onTrimMemory(int level) {}
		});
	}
	
	public void setSeedColor(@ColorInt int color) {
		this.seedColor = color;
		prefs.edit().putInt(KEY_SEED, color).apply();
		
		if (!dynamicColorEnabled) {
			applySeedInternal(color);
			notifyListeners();
		}
	}
	
	@ColorInt
	public int getSeedColor() {
		return seedColor;
	}
	
	public void setThemeMode(int mode) {
		if (mode != MODE_FOLLOW_SYSTEM && mode != MODE_DARK && mode != MODE_LIGHT) return;
		if (this.themeMode == mode) return;
		this.themeMode = mode;
		prefs.edit().putInt(KEY_THEME_MODE, mode).apply();
		refresh();
	}
	
	public int getThemeMode() {
		return themeMode;
	}
	
	public boolean isDarkMode() {
		switch (themeMode) {
			case MODE_DARK:
			return true;
			case MODE_LIGHT:
			return false;
			default:
			int uiMode = appContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
			return uiMode == Configuration.UI_MODE_NIGHT_YES;
		}
	}
	
	public void setDynamicColor(boolean enabled) {
		if (this.dynamicColorEnabled == enabled) return;
		this.dynamicColorEnabled = enabled;
		prefs.edit().putBoolean(KEY_DYNAMIC, enabled).apply();
		
		if (enabled && isDynamicColorSupported()) {
			applySeedInternal(getSystemSeedColor());
		} else {
			applySeedInternal(seedColor);
		}
		notifyListeners();
	}
	
	public boolean isDynamicColorEnabled() {
		return dynamicColorEnabled;
	}
	
	public void setAmoledMode(boolean enabled) {
		if (this.amoledModeEnabled == enabled) return;
		this.amoledModeEnabled = enabled;
		prefs.edit().putBoolean(KEY_AMOLED, enabled).apply();
		refresh();
	}
	
	public boolean isAmoledModeEnabled() {
		return amoledModeEnabled;
	}
	
	public static boolean isDynamicColorSupported() {
		return Build.VERSION.SDK_INT >= 31;
	}
	
	@ColorInt
	private int getSystemSeedColor() {
		try {
			int resId = android.R.color.system_accent1_600;
			return appContext.getResources().getColor(resId, appContext.getTheme());
		} catch (Exception e) {
			return DEFAULT_SEED;
		}
	}
	
	public void refresh() {
		if (dynamicColorEnabled && isDynamicColorSupported()) {
			applySeedInternal(getSystemSeedColor());
		} else {
			applySeedInternal(seedColor);
		}
		notifyListeners();
	}
	
	private void applySeedInternal(@ColorInt int color) {
		boolean dark = isDarkMode();
		AexonSchemeCompat scheme = new AexonSchemeCompat(color, dark);
		cache.clear();
		
		cache.put("colorPrimary", scheme.colorPrimary());
		cache.put("colorPrimaryDark", scheme.colorPrimaryContainer());
		cache.put("colorOnPrimary", scheme.colorOnPrimary());
		cache.put("colorOnPrimaryDark", scheme.colorOnPrimaryContainer());
		
		cache.put("colorAccent", scheme.colorSecondary());
		cache.put("colorOnAccent", scheme.colorOnSecondary());
		cache.put("colorAccentContainer", scheme.colorSecondaryContainer());
		cache.put("colorOnAccentContainer", scheme.colorOnSecondaryContainer());
		
		cache.put("colorControlHighlight", scheme.colorControlHighlight());
		cache.put("colorControlNormal", scheme.colorControlNormal());
		
		cache.put("colorTertiary", scheme.colorTertiary());
		cache.put("colorOnTertiary", scheme.colorOnTertiary());
		cache.put("colorTertiaryContainer", scheme.colorTertiaryContainer());
		cache.put("colorOnTertiaryContainer", scheme.colorOnTertiaryContainer());
		
		cache.put("colorError", scheme.colorError());
		cache.put("colorOnError", scheme.colorOnError());
		cache.put("colorErrorContainer", scheme.colorErrorContainer());
		cache.put("colorOnErrorContainer", scheme.colorOnErrorContainer());
		
		cache.put("colorSurface", scheme.colorSurface());
		cache.put("colorOnSurface", scheme.colorOnSurface());
		cache.put("colorSurfaceVariant", scheme.colorSurfaceVariant());
		cache.put("colorOnSurfaceVariant", scheme.colorOnSurfaceVariant());
		cache.put("colorSurfaceDim", scheme.colorSurfaceDim());
		cache.put("colorSurfaceBright", scheme.colorSurfaceBright());
		cache.put("colorSurfaceContainerLowest", scheme.colorSurfaceContainerLowest());
		cache.put("colorSurfaceContainerLow", scheme.colorSurfaceContainerLow());
		cache.put("colorSurfaceContainer", scheme.colorSurfaceContainer());
		cache.put("colorSurfaceContainerHigh", scheme.colorSurfaceContainerHigh());
		cache.put("colorSurfaceContainerHighest", scheme.colorSurfaceContainerHighest());
		cache.put("colorSurfaceInverse", scheme.colorSurfaceInverse());
		cache.put("colorOnSurfaceInverse", scheme.colorOnSurfaceInverse());
		cache.put("colorPrimaryInverse", scheme.colorPrimaryInverse());
		
		cache.put("colorOutline", scheme.colorOutline());
		cache.put("colorOutlineVariant", scheme.colorOutlineVariant());
		cache.put("colorScrim", scheme.colorScrim());
		cache.put("colorScrimFix", scheme.colorScrimFix());
		
		cache.put("colorSurfaceFix", 0xFFFFFFFF);
		cache.put("colorSurfaceVariantFix", 0xFFF5F5F5);
		
		if (dark && amoledModeEnabled) {
			cache.put("colorSurface", 0xFF000000);
			cache.put("colorSurfaceDim", 0xFF000000);
			cache.put("colorSurfaceContainerLowest", 0xFF000000);
			cache.put("colorSurfaceContainerLow", blendWithBlack(scheme.colorSurfaceContainerLow(), 0.25f));
			cache.put("colorSurfaceContainer", blendWithBlack(scheme.colorSurfaceContainer(), 0.45f));
			cache.put("colorSurfaceContainerHigh", blendWithBlack(scheme.colorSurfaceContainerHigh(), 0.60f));
			cache.put("colorSurfaceContainerHighest", blendWithBlack(scheme.colorSurfaceContainerHighest(), 0.75f));
			cache.put("colorOutline", adjustAlpha(scheme.colorPrimary(), 0.30f));
		}
	}
	
	@ColorInt
	private int blendWithBlack(@ColorInt int color, float ratio) {
		int r = Math.round(Color.red(color) * ratio);
		int g = Math.round(Color.green(color) * ratio);
		int b = Math.round(Color.blue(color) * ratio);
		return Color.rgb(r, g, b);
	}
	
	@ColorInt
	private int adjustAlpha(@ColorInt int color, float factor) {
		int alpha = Math.round(Color.alpha(color) * factor);
		return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
	}
	
	@ColorInt
	private static int parseHex(@Nullable String hex) {
		if (hex == null) return 0xFF000000;
		return Color.parseColor(hex) | 0xFF000000;
	}
	
	@ColorInt
	private int get(@NonNull String key) {
		Integer val = cache.get(key);
		return val != null ? val : 0xFF000000;
	}
	
	public void addListener(@NonNull AexonThemeListener listener) {
		if (!listeners.contains(listener)) listeners.add(listener);
	}
	
	public void removeListener(@NonNull AexonThemeListener listener) {
		listeners.remove(listener);
	}
	
	public void clearListeners() {
		listeners.clear();
	}
	
	private void notifyListeners() {
		for (AexonThemeListener l : new ArrayList<>(listeners)) {
			l.onThemeChanged(seedColor, this);
		}
	}
	
	@ColorInt
	public int getColorPrimary() {
		return get("colorPrimary");
	}
	
	@ColorInt
	public int getColorPrimaryDark() {
		return get("colorPrimaryDark");
	}
	
	@ColorInt
	public int getColorOnPrimary() {
		return get("colorOnPrimary");
	}
	
	@ColorInt
	public int getColorOnPrimaryDark() {
		return get("colorOnPrimaryDark");
	}
	
	@ColorInt
	public int getColorAccent() {
		return get("colorAccent");
	}
	
	@ColorInt
	public int getColorOnAccent() {
		return get("colorOnAccent");
	}
	
	@ColorInt
	public int getColorAccentContainer() {
		return get("colorAccentContainer");
	}
	
	@ColorInt
	public int getColorOnAccentContainer() {
		return get("colorOnAccentContainer");
	}
	
	@ColorInt
	public int getColorControlHighlight() {
		return get("colorControlHighlight");
	}
	
	@ColorInt
	public int getColorControlNormal() {
		return get("colorControlNormal");
	}
	
	@ColorInt
	public int getColorTertiary() {
		return get("colorTertiary");
	}
	
	@ColorInt
	public int getColorOnTertiary() {
		return get("colorOnTertiary");
	}
	
	@ColorInt
	public int getColorTertiaryContainer() {
		return get("colorTertiaryContainer");
	}
	
	@ColorInt
	public int getColorOnTertiaryContainer() {
		return get("colorOnTertiaryContainer");
	}
	
	@ColorInt
	public int getColorError() {
		return get("colorError");
	}
	
	@ColorInt
	public int getColorOnError() {
		return get("colorOnError");
	}
	
	@ColorInt
	public int getColorErrorContainer() {
		return get("colorErrorContainer");
	}
	
	@ColorInt
	public int getColorOnErrorContainer() {
		return get("colorOnErrorContainer");
	}
	
	@ColorInt
	public int getColorSurface() {
		return get("colorSurface");
	}
	
	@ColorInt
	public int getColorOnSurface() {
		return get("colorOnSurface");
	}
	
	@ColorInt
	public int getColorSurfaceVariant() {
		return get("colorSurfaceVariant");
	}
	
	@ColorInt
	public int getColorOnSurfaceVariant() {
		return get("colorOnSurfaceVariant");
	}
	
	@ColorInt
	public int getColorSurfaceDim() {
		return get("colorSurfaceDim");
	}
	
	@ColorInt
	public int getColorSurfaceBright() {
		return get("colorSurfaceBright");
	}
	
	@ColorInt
	public int getColorSurfaceContainerLowest() {
		return get("colorSurfaceContainerLowest");
	}
	
	@ColorInt
	public int getColorSurfaceContainerLow() {
		return get("colorSurfaceContainerLow");
	}
	
	@ColorInt
	public int getColorSurfaceContainer() {
		return get("colorSurfaceContainer");
	}
	
	@ColorInt
	public int getColorSurfaceContainerHigh() {
		return get("colorSurfaceContainerHigh");
	}
	
	@ColorInt
	public int getColorSurfaceContainerHighest() {
		return get("colorSurfaceContainerHighest");
	}
	
	@ColorInt
	public int getColorSurfaceInverse() {
		return get("colorSurfaceInverse");
	}
	
	@ColorInt
	public int getColorOnSurfaceInverse() {
		return get("colorOnSurfaceInverse");
	}
	
	@ColorInt
	public int getColorPrimaryInverse() {
		return get("colorPrimaryInverse");
	}
	
	@ColorInt
	public int getColorOutline() {
		return get("colorOutline");
	}
	
	@ColorInt
	public int getColorOutlineVariant() {
		return get("colorOutlineVariant");
	}
	
	@ColorInt
	public int getColorScrim() {
		return get("colorScrim");
	}
	
	@ColorInt
	public int getColorScrimFix() {
		return get("colorScrimFix");
	}
	
	@ColorInt
	public int getColorSurfaceFix() {
		return get("colorSurfaceFix");
	}
	
	@ColorInt
	public int getColorSurfaceVariantFix() {
		return get("colorSurfaceVariantFix");
	}
}