package com.aexon.material.bottomsheet;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimation;
import android.view.animation.DecelerateInterpolator;

import com.aexon.annotation.NonNull;
import com.aexon.annotation.RequiresApi;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import static com.aexon.core.AexonApi.minSdk;

public class AexonInsetsAnimationCallback {

    private static final int MIN_IME_HEIGHT_DP = 100;
    private static final Map<View, Object> sListeners = new WeakHashMap<>();

    public static void attach(@NonNull View rootView, @NonNull View sheetView) {
        detach(rootView);
        if (minSdk(RequiresApi.R)) {
            NativeCallback callback = new NativeCallback(sheetView);
            rootView.setWindowInsetsAnimationCallback(callback);
            sListeners.put(rootView, callback);
        } else {
            FallbackListener listener = new FallbackListener(rootView, sheetView);
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(listener);
            sListeners.put(rootView, listener);
        }
    }

    public static void detach(@NonNull View rootView) {
        Object listener = sListeners.remove(rootView);
        if (listener instanceof FallbackListener) {
            ((FallbackListener) listener).unregister(rootView);
        } else if (minSdk(RequiresApi.R)) {
            rootView.setWindowInsetsAnimationCallback(null);
        }
    }

    @RequiresApi(RequiresApi.R)
    private static class NativeCallback extends WindowInsetsAnimation.Callback {
        private final View mSheetView;
        private int mBaseTranslationY = 0;

        NativeCallback(View sheetView) {
            super(WindowInsetsAnimation.Callback.DISPATCH_MODE_STOP);
            mSheetView = sheetView;
        }

        @Override
        public void onPrepare(@NonNull WindowInsetsAnimation animation) {
            mBaseTranslationY = (int) mSheetView.getTranslationY();
        }

        @NonNull
        @Override
        public WindowInsets onProgress(@NonNull WindowInsets insets, @NonNull List<WindowInsetsAnimation> runningAnimations) {
            int imeHeight = insets.getInsets(WindowInsets.Type.ime()).bottom;
            mSheetView.setTranslationY(mBaseTranslationY - imeHeight);
            return insets;
        }
    }

    private static class FallbackListener implements ViewTreeObserver.OnGlobalLayoutListener {
        private final View mRootView;
        private final View mSheetView;
        private ValueAnimator mAnimator;
        private int mLastImeHeight = 0;
        private int mBaseTranslationY = 0;
        private final int mMinImeHeightPx;

        FallbackListener(View rootView, View sheetView) {
            mRootView = rootView;
            mSheetView = sheetView;
            mMinImeHeightPx = (int) (MIN_IME_HEIGHT_DP * rootView.getResources().getDisplayMetrics().density);
        }

        @Override
        public void onGlobalLayout() {
            Rect visibleRect = new Rect();
            mRootView.getWindowVisibleDisplayFrame(visibleRect);
            int rootHeight = mRootView.getHeight();
            int visibleHeight = visibleRect.bottom - visibleRect.top;
            int rawDiff = Math.max(0, rootHeight - visibleHeight);
            int imeHeight = rawDiff >= mMinImeHeightPx ? rawDiff : 0;

            if (imeHeight != mLastImeHeight) {
                mLastImeHeight = imeHeight;
                if (imeHeight > 0 && mBaseTranslationY == 0) {
                    mBaseTranslationY = (int) mSheetView.getTranslationY();
                } else if (imeHeight == 0) {
                    mBaseTranslationY = 0;
                }
                animateTranslationTo(mBaseTranslationY - imeHeight);
            }
        }

        private void animateTranslationTo(int target) {
            if (mAnimator != null) mAnimator.cancel();
            float current = mSheetView.getTranslationY();
            if (Math.abs(current - target) < 0.5f) {
                mSheetView.setTranslationY(target);
                return;
            }
            mAnimator = ValueAnimator.ofFloat(current, target);
            mAnimator.setInterpolator(new DecelerateInterpolator());
            mAnimator.setDuration(250);
            mAnimator.addUpdateListener(animation -> mSheetView.setTranslationY((float) animation.getAnimatedValue()));
            mAnimator.start();
        }

        void unregister(View rootView) {
            if (rootView.getViewTreeObserver().isAlive()) {
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            if (mAnimator != null) {
                mAnimator.cancel();
                mAnimator = null;
            }
        }
    }
}