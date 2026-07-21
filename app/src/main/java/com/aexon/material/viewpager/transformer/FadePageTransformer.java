package com.aexon.material.viewpager.transformer;

import android.view.View;
import com.aexon.material.viewpager.PageTransformer;
import com.aexon.annotation.NonNull;

public class FadePageTransformer implements PageTransformer {
	
	@Override
	public void transformPage(@NonNull View page, float position) {
		page.setTranslationX(-position * page.getWidth());
		page.setAlpha(Math.max(0.5f, 1f - Math.abs(position)));
	}
}