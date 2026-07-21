package com.aexon.material.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import android.app.Fragment;
import android.widget.EdgeEffect;
import android.graphics.Canvas;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.aexon.annotation.NonNull;
import com.aexon.annotation.Nullable;

public class AexonViewPager extends ViewGroup {
	
	private static final Interpolator sInterpolator = t -> {
		t -= 1.0f;
		return t * t * t * t * t + 1.0f;
	};
	
	public static final int SCROLL_STATE_IDLE = 0;
	public static final int SCROLL_STATE_DRAGGING = 1;
	public static final int SCROLL_STATE_SETTLING = 2;
	
	private static final int DEFAULT_OFFSCREEN_PAGES = 1;
	private static final int MAX_SETTLE_DURATION = 600;
	private static final int MIN_SETTLE_DURATION = 100;
	private static final int MIN_DISTANCE_FOR_FLING_DP = 25;
	private static final int MIN_FLING_VELOCITY_DP = 400;
	private static final int INVALID_POINTER = -1;
	
	private AexonFragmentStatePagerAdapter mAdapter;
	private int mCurItem = 0;
	private int mScrollState = SCROLL_STATE_IDLE;
	private boolean mFirstLayout = true;
	
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	
	private float mLastMotionX;
	private float mInitialMotionX;
	private int mActivePointerId = INVALID_POINTER;
	private boolean mIsBeingDragged = false;
	private boolean mIsUnableToDrag = false;
	
	private int mTouchSlop;
	private int mMinimumVelocity;
	private int mMaximumVelocity;
	private int mFlingDistance;
	private EdgeEffect mLeftEdge;
	private EdgeEffect mRightEdge;
	
	private int mOffscreenPageLimit = DEFAULT_OFFSCREEN_PAGES;
	private int mLastDispatchedItem = -1;
	
	private final Map<Integer, View> mFragmentViews = new HashMap<>();
	
	private PageTransformer mPageTransformer;
	private OnPageScrollListener mScrollListener;
	
	public interface OnPageScrollListener {
		void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
		void onPageSelected(int position);
		void onPageScrollStateChanged(int state);
	}
	
	public AexonViewPager(@NonNull Context context) {
		super(context);
		init(context);
	}
	
	public AexonViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(@NonNull Context context) {
		setWillNotDraw(false);
		setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
		setFocusable(true);
		
		mScroller = new Scroller(context, sInterpolator);
		
		ViewConfiguration config = ViewConfiguration.get(context);
		float density = context.getResources().getDisplayMetrics().density;
		
		mTouchSlop = config.getScaledPagingTouchSlop();
		mMinimumVelocity = (int) (MIN_FLING_VELOCITY_DP * density);
		mMaximumVelocity = config.getScaledMaximumFlingVelocity();
		mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING_DP * density);
		
		mLeftEdge = new EdgeEffect(context);
		mRightEdge = new EdgeEffect(context);
	}
	
	public void setAdapter(@Nullable AexonFragmentStatePagerAdapter adapter) {
		if (mAdapter != null) {
			mAdapter.startUpdate(this);
			for (int i = 0; i < mAdapter.getCount(); i++) {
				Fragment f = mAdapter.getFragmentAt(i);
				if (f != null) mAdapter.destroyItem(this, i, f);
			}
			mAdapter.finishUpdate(this);
			removeAllViews();
			mFragmentViews.clear();
			mLastDispatchedItem = -1;
			mCurItem = 0;
			scrollTo(0, 0);
		}
		
		mAdapter = adapter;
		mFirstLayout = true;
		
		if (mAdapter != null) {
			requestLayout();
		}
	}
	
	public void setCurrentItem(int item) {
		setCurrentItem(item, !mFirstLayout);
	}
	
	public void setCurrentItem(int item, boolean smoothScroll) {
		if (mAdapter == null || mAdapter.getCount() <= 0) return;
		item = clamp(item, 0, mAdapter.getCount() - 1);
		
		boolean dispatchSelected = mCurItem != item;
		mCurItem = item;
		
		if (mFirstLayout) {
			requestLayout();
			if (dispatchSelected) dispatchOnPageSelected(item);
			return;
		}
		
		populate();
		scrollToItem(item, smoothScroll, 0, dispatchSelected);
	}
	
	public int getCurrentItem() {
		return mCurItem;
	}
	
	public void setOffscreenPageLimit(int limit) {
		mOffscreenPageLimit = Math.max(1, limit);
		populate();
	}
	
	public int getOffscreenPageLimit() {
		return mOffscreenPageLimit;
	}
	
	public void setOnPageScrollListener(@Nullable OnPageScrollListener listener) {
		mScrollListener = listener;
	}
	
	public void setPageTransformer(@Nullable PageTransformer transformer) {
		setPageTransformer(true, transformer);
	}
	
	public void setPageTransformer(boolean reverseDrawingOrder, @Nullable PageTransformer transformer) {
		mPageTransformer = transformer;
		setChildrenDrawingOrderEnabled(reverseDrawingOrder);
		updateTransforms();
	}
	
	public int getScrollState() {
		return mScrollState;
	}
	
	private void setScrollState(int newState) {
		if (mScrollState == newState) return;
		mScrollState = newState;
		if (mScrollListener != null) mScrollListener.onPageScrollStateChanged(newState);
	}
	
	private void populate() {
		if (mAdapter == null) return;
		
		int startPos = Math.max(0, mCurItem - mOffscreenPageLimit);
		int endPos = Math.min(mAdapter.getCount() - 1, mCurItem + mOffscreenPageLimit);
		
		mAdapter.startUpdate(this);
		
		for (int i = 0; i < mAdapter.getCount(); i++) {
			if (i < startPos || i > endPos) {
				Fragment f = mAdapter.getFragmentAt(i);
				if (f != null) {
					mAdapter.destroyItem(this, i, f);
					mFragmentViews.remove(i);
				}
			}
		}
		
		for (int i = startPos; i <= endPos; i++) {
			mAdapter.instantiateItem(this, i);
		}
		
		mAdapter.finishUpdate(this);
	}
	
	private void scrollToItem(int item, boolean smoothScroll, int velocity, boolean dispatchSelected) {
		int destX = item * getWidth();
		if (smoothScroll) {
			smoothScrollTo(destX, velocity);
			if (dispatchSelected) dispatchOnPageSelected(item);
		} else {
			if (dispatchSelected) dispatchOnPageSelected(item);
			scrollTo(destX, 0);
			updateTransforms();
			notifyPageScrolled(item, 0f, 0);
		}
	}
	
	private void smoothScrollTo(int destX, int velocity) {
		int sx = getScrollX();
		int dx = destX - sx;
		if (dx == 0) {
			setScrollState(SCROLL_STATE_IDLE);
			populate();
			return;
		}
		
		setScrollState(SCROLL_STATE_SETTLING);
		
		int width = getWidth();
		int halfWidth = width / 2;
		float distRatio = Math.min(1f, 1.0f * Math.abs(dx) / width);
		float distance = halfWidth + halfWidth * distanceInfluenceForSnapDuration(distRatio);
		
		int duration;
		velocity = Math.abs(velocity);
		if (velocity > 0) {
			duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
		} else {
			float pageDelta = (float) Math.abs(dx) / width;
			duration = (int) ((pageDelta + 1) * 100);
		}
		duration = clamp(duration, MIN_SETTLE_DURATION, MAX_SETTLE_DURATION);
		
		mScroller.startScroll(sx, 0, dx, 0, duration);
		invalidate();
	}
	
	private float distanceInfluenceForSnapDuration(float f) {
		f -= 0.5f;
		f *= 0.3f * (float) Math.PI / 2.0f;
		return (float) Math.sin(f);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		if (w == 0 || h == 0) return;
		int contentWidth = w - getPaddingLeft() - getPaddingRight();
		int contentHeight = h - getPaddingTop() - getPaddingBottom();
		int wSpec = MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY);
		int hSpec = MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY);
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child != null && child.getVisibility() != GONE) {
				child.measure(wSpec, hSpec);
			}
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int width = r - l;
		int height = b - t;
		int contentWidth = width - getPaddingLeft() - getPaddingRight();
		int contentHeight = height - getPaddingTop() - getPaddingBottom();
		
		if (mFirstLayout) {
			mFirstLayout = false;
			populate();
			dispatchOnPageSelected(mCurItem);
			scrollTo(mCurItem * width, 0);
		}
		
		if (mAdapter == null) return;
		
		for (int i = 0; i < mAdapter.getCount(); i++) {
			Fragment f = mAdapter.getFragmentAt(i);
			if (f == null) continue;
			View v = f.getView();
			if (v == null || v.getParent() != this) continue;
			mFragmentViews.put(i, v);
			int left = i * width + getPaddingLeft();
			int top = getPaddingTop();
			int right = left + contentWidth;
			int bottom = top + contentHeight;
			v.layout(left, top, right, bottom);
		}
		
		updateTransforms();
	}
	
	@Override
	public void computeScroll() {
		if (!mScroller.computeScrollOffset()) return;
		
		int x = mScroller.getCurrX();
		scrollTo(x, 0);
		updateTransforms();
		notifyPageScrolledFromScrollX(x);
		
		if (mScroller.isFinished()) {
			int pageWidth = getWidth();
			if (pageWidth > 0) scrollTo(mCurItem * pageWidth, 0);
			updateTransforms();
			setScrollState(SCROLL_STATE_IDLE);
			populate();
		} else {
			postInvalidate();
		}
	}
	
	private void updateTransforms() {
		int pageWidth = getWidth();
		if (pageWidth == 0) return;
		float scrollX = getScrollX();
		
		for (Map.Entry<Integer, View> entry : mFragmentViews.entrySet()) {
			int position = entry.getKey();
			View child = entry.getValue();
			if (child == null) continue;
			
			float pageOffset = (position * pageWidth - scrollX) / (float) pageWidth;
			
			if (mPageTransformer != null) {
				mPageTransformer.transformPage(child, pageOffset);
			} else {
				child.setAlpha(1f);
				child.setScaleX(1f);
				child.setScaleY(1f);
				child.setTranslationX(0f);
				child.setTranslationY(0f);
				child.setRotation(0f);
			}
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(@NonNull MotionEvent ev) {
		int action = ev.getAction() & MotionEvent.ACTION_MASK;
		
		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
			mIsBeingDragged = false;
			mIsUnableToDrag = false;
			mActivePointerId = INVALID_POINTER;
			recycleVelocityTracker();
			return false;
		}
		
		if (action != MotionEvent.ACTION_DOWN) {
			if (mIsBeingDragged) return true;
			if (mIsUnableToDrag) return false;
		}
		
		switch (action) {
			case MotionEvent.ACTION_DOWN:
			mLastMotionX = mInitialMotionX = ev.getX();
			mActivePointerId = ev.getPointerId(0);
			mIsUnableToDrag = false;
			mIsBeingDragged = !mScroller.isFinished();
			if (!mScroller.isFinished()) mScroller.abortAnimation();
			break;
			
			case MotionEvent.ACTION_MOVE:
			int idx = ev.findPointerIndex(mActivePointerId);
			if (idx == INVALID_POINTER) break;
			float dx = ev.getX(idx) - mLastMotionX;
			if (Math.abs(dx) > mTouchSlop) {
				mIsBeingDragged = true;
				mLastMotionX = ev.getX(idx);
				setScrollState(SCROLL_STATE_DRAGGING);
			}
			break;
			
			case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;
		}
		
		return mIsBeingDragged;
	}
	
	@Override
	public boolean onTouchEvent(@NonNull MotionEvent ev) {
		if (mAdapter == null || mAdapter.getCount() == 0) return false;
		
		if (mVelocityTracker == null) mVelocityTracker = VelocityTracker.obtain();
		mVelocityTracker.addMovement(ev);
		
		int action = ev.getAction() & MotionEvent.ACTION_MASK;
		
		switch (action) {
			case MotionEvent.ACTION_DOWN:
			mScroller.abortAnimation();
			mLastMotionX = mInitialMotionX = ev.getX();
			mActivePointerId = ev.getPointerId(0);
			setScrollState(SCROLL_STATE_DRAGGING);
			break;
			
			case MotionEvent.ACTION_MOVE:
			if (!mIsBeingDragged) {
				int index = ev.findPointerIndex(mActivePointerId);
				if (index == INVALID_POINTER) break;
				if (Math.abs(ev.getX(index) - mLastMotionX) > mTouchSlop) {
					mIsBeingDragged = true;
					mLastMotionX = ev.getX(index);
					setScrollState(SCROLL_STATE_DRAGGING);
				}
			}
			if (mIsBeingDragged) {
				int index = ev.findPointerIndex(mActivePointerId);
				if (index == INVALID_POINTER) break;
				float x = ev.getX(index);
				float deltaX = mLastMotionX - x;
				mLastMotionX = x;
				int pageWidth = getWidth();
				int maxScroll = (mAdapter.getCount() - 1) * pageWidth;
				int newScrollX = clamp((int) (getScrollX() + deltaX), 0, maxScroll);
				scrollTo(newScrollX, 0);
				updateTransforms();
				notifyPageScrolledFromScrollX(newScrollX);
				
				if (newScrollX == 0 && deltaX < 0) {
					mLeftEdge.onPull(-deltaX / pageWidth);
					if (!mRightEdge.isFinished()) mRightEdge.onRelease();
					invalidate();
				} else if (newScrollX == maxScroll && deltaX > 0) {
					mRightEdge.onPull(deltaX / pageWidth);
					if (!mLeftEdge.isFinished()) mLeftEdge.onRelease();
					invalidate();
				} else {
					if (!mLeftEdge.isFinished()) { mLeftEdge.onRelease(); invalidate(); }
					if (!mRightEdge.isFinished()) { mRightEdge.onRelease(); invalidate(); }
				}
			}
			break;
			
			case MotionEvent.ACTION_UP:
			if (mIsBeingDragged) {
				mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int velocityX = (int) mVelocityTracker.getXVelocity(mActivePointerId);
				int pageWidth = getWidth();
				int nextPage = mCurItem;
				
				if (Math.abs(velocityX) > mMinimumVelocity
				&& Math.abs(getScrollX() - mCurItem * pageWidth) > mFlingDistance) {
					nextPage = velocityX > 0 ? mCurItem - 1 : mCurItem + 1;
				} else {
					float offset = getScrollX() - mCurItem * pageWidth;
					if (offset > pageWidth / 2f) nextPage = mCurItem + 1;
					else if (offset < -pageWidth / 2f) nextPage = mCurItem - 1;
				}
				
				nextPage = clamp(nextPage, 0, mAdapter.getCount() - 1);
				boolean changed = nextPage != mCurItem;
				mCurItem = nextPage;
				
				recycleVelocityTracker();
				mIsBeingDragged = false;
				mActivePointerId = INVALID_POINTER;
				mLeftEdge.onRelease();
				mRightEdge.onRelease();
				populate();
				scrollToItem(nextPage, true, -velocityX, changed);
			}
			break;
			
			case MotionEvent.ACTION_CANCEL:
			if (mIsBeingDragged) {
				scrollToItem(mCurItem, true, 0, false);
				mIsBeingDragged = false;
				mActivePointerId = INVALID_POINTER;
				recycleVelocityTracker();
				mLeftEdge.onRelease();
				mRightEdge.onRelease();
			}
			break;
			
			case MotionEvent.ACTION_POINTER_DOWN:
			int pIdx = ev.getActionIndex();
			mLastMotionX = ev.getX(pIdx);
			mActivePointerId = ev.getPointerId(pIdx);
			break;
			
			case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			mLastMotionX = ev.getX(ev.findPointerIndex(mActivePointerId));
			break;
		}
		
		return true;
	}
	
	private void onSecondaryPointerUp(@NonNull MotionEvent ev) {
		int pointerIndex = ev.getActionIndex();
		int pointerId = ev.getPointerId(pointerIndex);
		if (pointerId == mActivePointerId) {
			int newIndex = pointerIndex == 0 ? 1 : 0;
			mLastMotionX = ev.getX(newIndex);
			mActivePointerId = ev.getPointerId(newIndex);
			if (mVelocityTracker != null) mVelocityTracker.clear();
		}
	}
	
	private void notifyPageScrolledFromScrollX(int scrollX) {
		int pageWidth = getWidth();
		if (pageWidth == 0) return;
		int position = scrollX / pageWidth;
		float offset = (float) (scrollX % pageWidth) / pageWidth;
		int offsetPixels = scrollX % pageWidth;
		notifyPageScrolled(position, offset, offsetPixels);
	}
	
	private void notifyPageScrolled(int position, float offset, int offsetPixels) {
		if (mScrollListener != null) {
			mScrollListener.onPageScrolled(position, offset, offsetPixels);
		}
	}
	
	private void dispatchOnPageSelected(int position) {
		if (mLastDispatchedItem != position) {
			mLastDispatchedItem = position;
			if (mScrollListener != null) mScrollListener.onPageSelected(position);
		}
	}
	
	private void recycleVelocityTracker() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}
	
	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		boolean needsInvalidate = false;
		
		if (!mLeftEdge.isFinished()) {
			int restoreCount = canvas.save();
			canvas.rotate(270);
			canvas.translate(-getHeight(), 0);
			mLeftEdge.setSize(getHeight(), getWidth());
			needsInvalidate |= mLeftEdge.draw(canvas);
			canvas.restoreToCount(restoreCount);
		}
		
		if (!mRightEdge.isFinished()) {
			int restoreCount = canvas.save();
			canvas.rotate(90);
			canvas.translate(0, -getWidth());
			mRightEdge.setSize(getHeight(), getWidth());
			needsInvalidate |= mRightEdge.draw(canvas);
			canvas.restoreToCount(restoreCount);
		}
		
		if (needsInvalidate) invalidate();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w != oldw || h != oldh) {
			mLeftEdge.finish();
			mRightEdge.finish();
		}
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		recycleVelocityTracker();
		if (mScroller != null && !mScroller.isFinished()) {
			mScroller.abortAnimation();
		}
		mFragmentViews.clear();
	}
	
	public void notifyDataSetChanged() {
		if (mAdapter == null) return;
		int newCount = mAdapter.getCount();
		mAdapter.trimFragments(newCount);
		Iterator<Integer> it = mFragmentViews.keySet().iterator();
		while (it.hasNext()) {
			if (it.next() >= newCount) {
				it.remove();
			}
		}
		if (mCurItem >= newCount) {
			mCurItem = Math.max(0, newCount - 1);
		}
		populate();
		scrollTo(mCurItem * getWidth(), 0);
		updateTransforms();
		requestLayout();
	}
}