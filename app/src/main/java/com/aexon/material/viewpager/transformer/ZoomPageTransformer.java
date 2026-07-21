package com.aexon.material.viewpager.transformer;

import android.view.View;
import com.aexon.material.viewpager.PageTransformer;
import com.aexon.annotation.NonNull;

public class ZoomPageTransformer implements PageTransformer {
	
	private static final float MIN_SCALE = 0.85f;
	
	@Override
	public void transformPage(@NonNull View page, float position) {
		if (position <= -1) {
			page.setScaleX(MIN_SCALE);
			page.setScaleY(MIN_SCALE);
			page.setAlpha(0.5f);
		} else if (position <= 1) {
			float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
			page.setScaleX(scaleFactor);
			page.setScaleY(scaleFactor);
			page.setAlpha(0.5f + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * 0.5f);
		} else {
			page.setScaleX(MIN_SCALE);
			page.setScaleY(MIN_SCALE);
			page.setAlpha(0.5f);
		}
	}
}