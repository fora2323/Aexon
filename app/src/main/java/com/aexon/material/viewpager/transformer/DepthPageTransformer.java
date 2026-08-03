/*
* Copyright (c) 2026 Fora
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
* 
* Contact: Fora <fora060823@gmail.com>
* Created: 27-01-2026
*/
package com.aexon.material.viewpager.transformer;

import android.view.View;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.aexon.material.viewpager.PageTransformer;

@RequiresApi(api = Build.VERSION_CODES.O)
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