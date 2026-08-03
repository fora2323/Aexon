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