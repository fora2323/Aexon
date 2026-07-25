package com.aexon.material.viewpager;

import android.view.View;

import com.aexon.annotation.NonNull;

public interface PageTransformer {
    void transformPage(@NonNull View page, float position);
}