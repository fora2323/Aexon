package com.aexon.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aexon.annotation.NonNull;
import com.aexon.annotation.Nullable;
import com.aexon.theme.hct.AexonSchemeCompat;

public class AexonTheme {
    public static final int MODE_FOLLOW_SYSTEM = 0;
    public static final int MODE_DARK = 1;
    public static final int MODE_LIGHT = 2;
    
    private static final String PREF_NAME = "aexon_theme";
    private static final String KEY_SEED = "seed_color";
    private static final String KEY_DYNAMIC = "dynamic_color";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final int DEFAULT_SEED = 0xFFFFFFFF;
    
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
    private int seedColor;
    private boolean dynamicColorEnabled;
    private int themeMode;
    private final Map<String, Integer> cache = new HashMap<>();
    private final List<AexonThemeListener> listeners = new ArrayList<>();
    
    private AexonTheme(Context context) {
        this.appContext = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int savedSeed = prefs.getInt(KEY_SEED, DEFAULT_SEED);
        dynamicColorEnabled = prefs.getBoolean(KEY_DYNAMIC, false);
        themeMode = prefs.getInt(KEY_THEME_MODE, MODE_FOLLOW_SYSTEM);
        this.seedColor = savedSeed;
        
        if (dynamicColorEnabled && isDynamicColorSupported()) {
            applySeedInternal(getSystemSeedColor());
        } else {
            applySeedInternal(savedSeed);
        }
    }
    
    public void setSeedColor(int color) {
        this.seedColor = color;
        prefs.edit().putInt(KEY_SEED, color).apply();
        
        if (!dynamicColorEnabled) {
            applySeedInternal(color);
            notifyListeners();
        }
    }
    
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
    
    public static boolean isDynamicColorSupported() {
        return Build.VERSION.SDK_INT >= 31;
    }
    
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
    
    private void applySeedInternal(int color) {
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
    }
    
    private static int parseHex(String hex) {
        if (hex == null) return 0xFF000000;
        return Color.parseColor(hex) | 0xFF000000;
    }
    
    private int get(String key) {
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
    
    public int getColorPrimary() { 
        return get("colorPrimary");
    }
    
    public int getColorPrimaryDark() {
        return get("colorPrimaryDark"); 
    }
    
    public int getColorOnPrimary() { 
        return get("colorOnPrimary");
    }
    
    public int getColorOnPrimaryDark() {
        return get("colorOnPrimaryDark"); 
    }
    
    public int getColorAccent() { 
        return get("colorAccent");
    }
    
    public int getColorOnAccent() { 
        return get("colorOnAccent");
    }
    
    public int getColorAccentContainer() { 
        return get("colorAccentContainer"); 
    }
    
    public int getColorOnAccentContainer() {
        return get("colorOnAccentContainer");
    }
    
    public int getColorControlHighlight() {
        return get("colorControlHighlight"); 
    }
    
    public int getColorControlNormal() { 
        return get("colorControlNormal");
    }
    
    public int getColorTertiary() {
        return get("colorTertiary");
    }
    
    public int getColorOnTertiary() { 
        return get("colorOnTertiary"); 
    }
    
    public int getColorTertiaryContainer() {
        return get("colorTertiaryContainer"); 
    }
    
    public int getColorOnTertiaryContainer() {
        return get("colorOnTertiaryContainer"); 
    }
    
    public int getColorError() { 
        return get("colorError");
    }
    
    public int getColorOnError() { 
        return get("colorOnError");
    }
    
    public int getColorErrorContainer() { 
        return get("colorErrorContainer"); 
    }
    
    public int getColorOnErrorContainer() { 
        return get("colorOnErrorContainer");
    }
    
    public int getColorSurface() { 
        return get("colorSurface");
    }
    public int getColorOnSurface() {
        return get("colorOnSurface");
    }
    
    public int getColorSurfaceVariant() {
        return get("colorSurfaceVariant");
    }
    
    public int getColorOnSurfaceVariant() { 
        return get("colorOnSurfaceVariant"); 
    }
    
    public int getColorSurfaceDim() {
        return get("colorSurfaceDim"); 
    }
    
    public int getColorSurfaceBright() { 
        return get("colorSurfaceBright");
    }
    
    public int getColorSurfaceContainerLowest() {
        return get("colorSurfaceContainerLowest"); 
    }
    
    public int getColorSurfaceContainerLow() {
        return get("colorSurfaceContainerLow");
    }
    
    public int getColorSurfaceContainer() {
        return get("colorSurfaceContainer");
    }
    
    public int getColorSurfaceContainerHigh() { 
        return get("colorSurfaceContainerHigh"); 
    }
    
    public int getColorSurfaceContainerHighest() {
        return get("colorSurfaceContainerHighest"); 
    }
    
    public int getColorSurfaceInverse() {
        return get("colorSurfaceInverse");
    }
    
    public int getColorOnSurfaceInverse() { 
        return get("colorOnSurfaceInverse");
    }
    
    public int getColorPrimaryInverse() { 
        return get("colorPrimaryInverse");
    }
    
    public int getColorOutline() { 
        return get("colorOutline");
    }
    
    public int getColorOutlineVariant() { 
        return get("colorOutlineVariant");
    }
    
    public int getColorScrim() {
        return get("colorScrim"); 
    }
    public int getColorScrimFix() {
        return get("colorScrimFix"); 
    }
}