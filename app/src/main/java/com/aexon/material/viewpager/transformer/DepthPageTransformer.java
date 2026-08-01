package com.aexon.material.viewpager.transformer;

import android.view.View;
import com.aexon.material.viewpager.PageTransformer;
import com.aexon.annotation.NonNull;

public class DepthPageTransformer implements PageTransformer {
	
	private static final float MIN_ALPHA = 0.5f;
	private static final float MIN_SCALE = 0.75f;
	
	@Override
	public void transformPage(@NonNull View page, float position) {
		int pageWidth = page.getWidth();
		
		if (position < -1) {
			page.setAlpha(0);
		} else if (position <= 0) {
			page.setAlpha(1);
			page.setScaleX(1);
			page.setScaleY(1);
			page.setTranslationX(0);
		} else if (position <= 1) {
			page.setAlpha(MIN_ALPHA + (1 - Math.abs(position)) * (1 - MIN_ALPHA));
			
			page.setTranslationX(pageWidth * -position);
			
			float scaleFactor = MIN_SCALE + (1 - Math.abs(position)) * (1 - MIN_SCALE);
			page.setScaleX(scaleFactor);
			page.setScaleY(scaleFactor);
		} else {
			page.setAlpha(0);
		}
	}
}