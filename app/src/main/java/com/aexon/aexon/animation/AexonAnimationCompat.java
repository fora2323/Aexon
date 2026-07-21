package com.aexon.aexon.animation;

import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

public class AexonAnimationCompat {

    public static void animateVisibility(ViewGroup container) {
        TransitionSet transition = new TransitionSet();
        transition.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
        transition.addTransition(new Fade(Fade.OUT).setDuration(100));
        transition.addTransition(new ChangeBounds().setDuration(260));
        transition.addTransition(new Fade(Fade.IN).setDuration(160));
        transition.setInterpolator(new DecelerateInterpolator(2f));
        android.transition.TransitionManager.beginDelayedTransition(container, transition);
    }
}