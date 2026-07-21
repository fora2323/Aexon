package com.aexon.material.viewpager.transformer;

import android.view.View;
import com.aexon.material.viewpager.PageTransformer;
import com.aexon.annotation.NonNull;

public class RotatePageTransformer implements PageTransformer {
	
	private static final float MAX_ROTATION = 45.0f;
	
	@Override
	public void transformPage(@NonNull View page, float position) {
		if (position < -1) {
			page.setRotation(MAX_ROTATION * -1);
			page.setAlpha(0);
		} else if (position <= 1) {
			float rotation = MAX_ROTATION * position;
			page.setRotation(rotation);
			page.setAlpha(Math.max(0.5f, 1f - Math.abs(position)));
		} else {
			page.setRotation(MAX_ROTATION);
			page.setAlpha(0);
		}
	}
}