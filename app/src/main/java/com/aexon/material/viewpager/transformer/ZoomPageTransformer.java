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