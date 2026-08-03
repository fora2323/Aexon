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
package com.aexon.aexon.animation;

import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class AexonAnimationCompat {
	
	private AexonAnimationCompat() {
		throw new UnsupportedOperationException("No instances");
	}
	
	public static void animateVisibility(@NonNull ViewGroup container) {
		TransitionSet transition = new TransitionSet();
		transition.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
		transition.addTransition(new Fade(Fade.OUT).setDuration(100));
		transition.addTransition(new ChangeBounds().setDuration(260));
		transition.addTransition(new Fade(Fade.IN).setDuration(160));
		transition.setInterpolator(new DecelerateInterpolator(2f));
		TransitionManager.beginDelayedTransition(container, transition);
	}
}