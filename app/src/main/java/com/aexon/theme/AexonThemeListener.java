package com.aexon.theme;

import com.aexon.annotation.NonNull;

public interface AexonThemeListener {
    void onThemeChanged(int seedColor, @NonNull AexonTheme theme);
}