package com.aexon.theme;

import androidx.annotation.NonNull;

public interface AexonThemeListener {
    void onThemeChanged(int seedColor, @NonNull AexonTheme theme);
}