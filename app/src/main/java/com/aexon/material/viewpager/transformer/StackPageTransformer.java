package com.aexon.material.viewpager.transformer;

import android.view.View;
import com.aexon.material.viewpager.PageTransformer;
import com.aexon.annotation.NonNull;

public class StackPageTransformer implements PageTransformer {
	
	@Override
	public void transformPage(@NonNull View page, float position) {
		if (position < 0) {
			page.setAlpha(1 + position);
			page.setTranslationX(page.getWidth() * -position);
		} else if (position <= 1) {
			page.setAlpha(1.0f);
			page.setTranslationX(0);
		} else {
			page.setAlpha(0);
		}
	}
}