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

package com.aexon.material.bottomsheet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AexonBottomSheetBehavior {

    public static final int STATE_DRAGGING = 1;
    public static final int STATE_SETTLING = 2;
    public static final int STATE_EXPANDED = 3;
    public static final int STATE_COLLAPSED = 4;
    public static final int STATE_HIDDEN = 5;
    public static final int STATE_HALF_EXPANDED = 6;

    private static final float HIDE_THRESHOLD = 0.5f;

    private View mView;
    private int mState = STATE_COLLAPSED;
    private int mLastStableState = STATE_COLLAPSED;
    private int mPendingState = -1;

    private boolean mHideable = true;
    private boolean mSkipCollapsed = false;
    private boolean mDraggable = true;
    private int mPeekHeight = -1;
    private float mHalfExpandedRatio = 0.5f;
    private int mParentHeight = -1;
    private int mChildHeight = -1;
    private int mExpandedOffset = 0;

    private VelocityTracker mVelocityTracker;
    private int mActivePointerId = -1;
    private float mInitialY;
    private float mLastY;
    private boolean mIsDragging;
    private int mTouchSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;

    private ValueAnimator mSettlingAnimator;
    private BottomSheetCallback mCallback;

    public AexonBottomSheetBehavior() {
    }

    public void attachToView(@NonNull View view) {
        mView = view;
        Context ctx = view.getContext();
        ViewConfiguration config = ViewConfiguration.get(ctx);
        mTouchSlop = config.getScaledTouchSlop();
        mMinFlingVelocity = config.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = config.getScaledMaximumFlingVelocity();
    }

    @NonNull
    public static AexonBottomSheetBehavior from(@NonNull View view) {
        AexonBottomSheetBehavior behavior = new AexonBottomSheetBehavior();
        behavior.attachToView(view);
        return behavior;
    }

    public void setState(int state) {
        if (state == mState) return;
        if (mView == null || mView.getParent() == null) {
            mState = state;
            mLastStableState = state;
            return;
        }
        if (mParentHeight <= 0) {
            mPendingState = state;
            mState = state;
            mLastStableState = state;
            return;
        }
        startSettlingAnimation(state);
    }

    public int getState() {
        return mState;
    }

    public void setHideable(boolean hideable) {
        if (mHideable != hideable) {
            mHideable = hideable;
            if (!hideable && mState == STATE_HIDDEN) {
                setState(STATE_COLLAPSED);
            }
        }
    }

    public boolean isHideable() {
        return mHideable;
    }

    public void setSkipCollapsed(boolean skipCollapsed) {
        mSkipCollapsed = skipCollapsed;
    }

    public boolean getSkipCollapsed() {
        return mSkipCollapsed;
    }

    public void setDraggable(boolean draggable) {
        mDraggable = draggable;
    }

    public boolean isDraggable() {
        return mDraggable;
    }

    public void setPeekHeight(int peekHeight) {
        boolean changed = mPeekHeight != peekHeight;
        mPeekHeight = Math.max(0, peekHeight);
        if (changed && mState == STATE_COLLAPSED && mParentHeight > 0) {
            setState(STATE_COLLAPSED);
        }
    }

    public int getPeekHeight() {
        return mPeekHeight;
    }

    public void setHalfExpandedRatio(float ratio) {
        if (ratio <= 0f || ratio >= 1f) {
            throw new IllegalArgumentException("ratio must be between 0 and 1 exclusive");
        }
        mHalfExpandedRatio = ratio;
    }

    public float getHalfExpandedRatio() {
        return mHalfExpandedRatio;
    }

    public void setBottomSheetCallback(@Nullable BottomSheetCallback callback) {
        mCallback = callback;
    }

    @Nullable
    public BottomSheetCallback getBottomSheetCallback() {
        return mCallback;
    }

    private int calculatePeekHeight() {
        if (mPeekHeight >= 0) {
            return mPeekHeight;
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64f, mView.getResources().getDisplayMetrics());
    }

    private float getOffsetForState(int state) {
        if (mParentHeight <= 0) return 0f;
        switch (state) {
            case STATE_EXPANDED:
                return mExpandedOffset;
            case STATE_HALF_EXPANDED:
                return Math.max(mExpandedOffset, mParentHeight * (1f - mHalfExpandedRatio));
            case STATE_COLLAPSED:
                return Math.max(mExpandedOffset, mParentHeight - calculatePeekHeight());
            case STATE_HIDDEN:
                return mParentHeight;
            default:
                return mView != null ? mView.getTranslationY() : 0f;
        }
    }

    private float calculateSlideOffset() {
        if (mView == null || mParentHeight <= 0) return 0f;
        float currentOffset = mView.getTranslationY();
        float expandedOffset = getOffsetForState(STATE_EXPANDED);
        float collapsedOffset = getOffsetForState(STATE_COLLAPSED);
        if (currentOffset <= collapsedOffset) {
            float range = collapsedOffset - expandedOffset;
            if (range == 0f) return 1f;
            return 1f - (currentOffset - expandedOffset) / range;
        } else {
            float range = mParentHeight - collapsedOffset;
            if (range == 0f) return 0f;
            return -(currentOffset - collapsedOffset) / range;
        }
    }

    private void setStateInternal(int state) {
        if (mState == state) return;
        mState = state;
        if (state == STATE_EXPANDED || state == STATE_COLLAPSED || state == STATE_HALF_EXPANDED || state == STATE_HIDDEN) {
            mLastStableState = state;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && mView != null) {
            if (state == STATE_HIDDEN) {
                mView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
            } else {
                mView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
            }
        }
        if (mCallback != null && mView != null) {
            mCallback.onStateChanged(mView, state);
        }
    }

    private void startSettlingAnimation(int targetState) {
        if (mView == null) return;
        float targetOffset = getOffsetForState(targetState);
        float currentOffset = mView.getTranslationY();
        if (Math.abs(currentOffset - targetOffset) < 1f) {
            setStateInternal(targetState);
            return;
        }
        if (mSettlingAnimator != null) {
            mSettlingAnimator.cancel();
            mSettlingAnimator = null;
        }
        setStateInternal(STATE_SETTLING);
        ValueAnimator animator = ValueAnimator.ofFloat(currentOffset, targetOffset);
        animator.setInterpolator(new DecelerateInterpolator(1.5f));
        long duration = computeSettleDuration(currentOffset, targetOffset);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mView != null) {
                    mView.setTranslationY((Float) animation.getAnimatedValue());
                    if (mCallback != null) {
                        mCallback.onSlide(mView, calculateSlideOffset());
                    }
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSettlingAnimator = null;
                setStateInternal(targetState);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mSettlingAnimator = null;
            }
        });
        mSettlingAnimator = animator;
        animator.start();
    }

    private long computeSettleDuration(float currentOffset, float targetOffset) {
        float distance = Math.abs(currentOffset - targetOffset);
        if (mParentHeight <= 0) return 250L;
        float ratio = distance / mParentHeight;
        long duration = (long) (150f + ratio * 200f);
        return Math.max(100L, Math.min(350L, duration));
    }

    private int findTargetState(float currentOffset, float yVelocity) {
        float expandedOffset = getOffsetForState(STATE_EXPANDED);
        float halfExpandedOffset = getOffsetForState(STATE_HALF_EXPANDED);
        float collapsedOffset = getOffsetForState(STATE_COLLAPSED);

        if (Math.abs(yVelocity) > mMinFlingVelocity) {
            if (yVelocity > 0) {
                if (mHideable && shouldHide(currentOffset, yVelocity)) {
                    return STATE_HIDDEN;
                }
                if (mSkipCollapsed) {
                    return mHideable ? STATE_HIDDEN : STATE_HALF_EXPANDED;
                }
                return STATE_COLLAPSED;
            } else {
                return STATE_EXPANDED;
            }
        }

        if (currentOffset < halfExpandedOffset) {
            float mid = (expandedOffset + halfExpandedOffset) / 2f;
            return currentOffset < mid ? STATE_EXPANDED : STATE_HALF_EXPANDED;
        } else if (currentOffset < collapsedOffset) {
            float mid = (halfExpandedOffset + collapsedOffset) / 2f;
            return currentOffset < mid ? STATE_HALF_EXPANDED : STATE_COLLAPSED;
        } else {
            if (mHideable) {
                float mid = (collapsedOffset + mParentHeight) / 2f;
                return currentOffset < mid ? STATE_COLLAPSED : STATE_HIDDEN;
            }
            return STATE_COLLAPSED;
        }
    }

    private boolean shouldHide(float currentOffset, float yVelocity) {
        if (currentOffset > getOffsetForState(STATE_COLLAPSED)) {
            return true;
        }
        float hideOffset = getOffsetForState(STATE_COLLAPSED)
                + (mParentHeight - getOffsetForState(STATE_COLLAPSED)) * HIDE_THRESHOLD;
        return currentOffset > hideOffset || Math.abs(yVelocity) > mMaxFlingVelocity * 0.5f;
    }

    private void resetTouch() {
        mIsDragging = false;
        mActivePointerId = -1;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public boolean onInterceptTouchEvent(@NonNull ViewGroup parent, @NonNull View child, @NonNull MotionEvent ev) {
        if (!mDraggable || mView == null) return false;

        int action = ev.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            mIsDragging = false;
            mActivePointerId = ev.getPointerId(0);
            mInitialY = ev.getY();
            mLastY = mInitialY;
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            } else {
                mVelocityTracker.clear();
            }
            mVelocityTracker.addMovement(ev);
            return false;
        }

        if (mActivePointerId == -1) return false;

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                int index = ev.findPointerIndex(mActivePointerId);
                if (index == -1) return false;
                float y = ev.getY(index);
                float dy = Math.abs(y - mInitialY);
                if (mVelocityTracker != null) {
                    mVelocityTracker.addMovement(ev);
                }
                if (dy > mTouchSlop) {
                    mIsDragging = true;
                    mLastY = y;
                    setStateInternal(STATE_DRAGGING);
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                int actionIndex = ev.getActionIndex();
                int pointerId = ev.getPointerId(actionIndex);
                if (pointerId == mActivePointerId) {
                    int newIndex = actionIndex == 0 ? 1 : 0;
                    mActivePointerId = ev.getPointerId(newIndex);
                    mLastY = ev.getY(newIndex);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                resetTouch();
                break;
        }
        return false;
    }

    public boolean onTouchEvent(@NonNull ViewGroup parent, @NonNull View child, @NonNull MotionEvent ev) {
        if (!mDraggable || mView == null) return false;

        int action = ev.getActionMasked();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mActivePointerId = ev.getPointerId(0);
                mLastY = ev.getY();
                mIsDragging = true;
                setStateInternal(STATE_DRAGGING);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int index = ev.findPointerIndex(mActivePointerId);
                if (index == -1) return false;
                float y = ev.getY(index);
                if (!mIsDragging) {
                    mLastY = y;
                    mIsDragging = true;
                    setStateInternal(STATE_DRAGGING);
                }
                float dy = y - mLastY;
                mLastY = y;
                dragBy(dy);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                int actionIndex = ev.getActionIndex();
                int pointerId = ev.getPointerId(actionIndex);
                if (pointerId == mActivePointerId) {
                    int newIndex = actionIndex == 0 ? 1 : 0;
                    mActivePointerId = ev.getPointerId(newIndex);
                    mLastY = ev.getY(newIndex);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mVelocityTracker != null && mActivePointerId != -1) {
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                    float yvel = mVelocityTracker.getYVelocity(mActivePointerId);
                    float currentOffset = mView.getTranslationY();
                    int targetState = findTargetState(currentOffset, yvel);
                    if (mSkipCollapsed && targetState == STATE_COLLAPSED) {
                        targetState = mHideable ? STATE_HIDDEN : STATE_EXPANDED;
                    }
                    startSettlingAnimation(targetState);
                }
                resetTouch();
                break;
            }
        }
        return true;
    }

    private void dragBy(float dy) {
        if (mView == null || mParentHeight <= 0) return;
        float currentOffset = mView.getTranslationY();
        float newOffset = currentOffset + dy;
        float minOffset = mExpandedOffset;
        float maxOffset = mHideable ? mParentHeight : getOffsetForState(STATE_COLLAPSED);
        newOffset = Math.max(minOffset, Math.min(maxOffset, newOffset));
        mView.setTranslationY(newOffset);
        if (mCallback != null) {
            mCallback.onSlide(mView, calculateSlideOffset());
        }
    }

    public void onLayoutChild(@NonNull ViewGroup parent, @NonNull View child) {
        mParentHeight = parent.getHeight();
        mChildHeight = child.getHeight();
        mExpandedOffset = Math.max(0, mParentHeight - mChildHeight);
        
        if (mPendingState != -1) {
            mView.setTranslationY(mParentHeight);
            int state = mPendingState;
            mPendingState = -1;
            startSettlingAnimation(state);
        } else {
            float targetOffset = getOffsetForState(mState);
            if (Math.abs(mView.getTranslationY() - targetOffset) > 1f) {
                mView.setTranslationY(targetOffset);
            }
        }
    }

    public void reset() {
        if (mSettlingAnimator != null) {
            mSettlingAnimator.cancel();
            mSettlingAnimator = null;
        }
        resetTouch();
        mIsDragging = false;
    }

    public abstract static class BottomSheetCallback {
        public abstract void onStateChanged(@NonNull View bottomSheet, int newState);
        public abstract void onSlide(@NonNull View bottomSheet, float slideOffset);
    }
}