package com.aexon.material.aexonswitch;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.CompoundButton;

import com.aexon.theme.AexonTheme;
import com.aexon.theme.AexonThemeListener;

public class AexonSwitch extends CompoundButton {

    private Paint paintTrack;
    private Paint paintTrackOutline;
    private Paint paintThumb;
    private Paint paintRipple;

    private float trackWidth;
    private float trackHeight;

    private float thumbSizeOff;
    private float thumbSizeOn;
    private float thumbSizePressed;
    private float rippleRadius;

    private float thumbCy;
    private float currentThumbCx;
    private float currentThumbSize;
    private float currentRippleAlpha = 0f;

    private float thumbOnCx;
    private float thumbOffCx;

    private ValueAnimator translateAnimator;
    private ValueAnimator sizeAnimator;
    private ValueAnimator rippleInAnimator;
    private ValueAnimator rippleOutAnimator;

    private int colorTrackOn;
    private int colorTrackOff;
    private int colorThumbOn;
    private int colorThumbOff;
    private int colorOutline;
    private int colorRippleOn;
    private int colorRippleOff;

    private int animDuration = 300;
    private int pressDuration = 100;
    private int rippleInDuration = 100;
    private int rippleOutDuration = 200;

    private static final int RIPPLE_MAX_ALPHA = (int) (255 * 0.12f);

    private float strokeWidth;

    private final RectF rectTrack = new RectF();
    private final RectF rectThumb = new RectF();

    private float trackLeft, trackRight, trackTop, trackBottom, trackCy;

    private boolean positionsReady = false;

    private final AexonThemeListener themeListener = (seedColor, theme) -> {
        loadThemeColors(theme);
        invalidate();
    };

    public AexonSwitch(Context context) {
        super(context);
        init();
    }

    public AexonSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AexonSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paintTrack = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTrackOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTrackOutline.setStyle(Paint.Style.STROKE);
        paintThumb = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRipple = new Paint(Paint.ANTI_ALIAS_FLAG);

        loadThemeColors(AexonTheme.getInstance());

        setClickable(true);
        setFocusable(true);
        setButtonDrawable(null);

        trackWidth = dpToPx(52);
        trackHeight = dpToPx(32);
        thumbSizeOff = dpToPx(16);
        thumbSizeOn = dpToPx(24);
        thumbSizePressed = dpToPx(28);
        rippleRadius = dpToPx(20);
        strokeWidth = dpToPx(2);

        paintTrackOutline.setStrokeWidth(strokeWidth);
        currentThumbSize = isChecked() ? thumbSizeOn : thumbSizeOff;
    }

    private void loadThemeColors(AexonTheme theme) {
        colorTrackOff = theme.getColorSurfaceContainerHighest();
        colorTrackOn = theme.getColorPrimary();
        colorThumbOff = theme.getColorOutline();
        colorThumbOn = theme.getColorOnPrimary();
        colorOutline = theme.getColorOutline();
        colorRippleOn = theme.getColorPrimary();
        colorRippleOff = theme.getColorOnSurface();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AexonTheme.getInstance().addListener(themeListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AexonTheme.getInstance().removeListener(themeListener);
        stopToggleAnimators();
        if (rippleInAnimator  != null) rippleInAnimator.cancel();
        if (rippleOutAnimator != null) rippleOutAnimator.cancel();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingH = getPaddingLeft() + getPaddingRight();
        int paddingV = getPaddingTop() + getPaddingBottom();

        float rippleOverflow = rippleRadius - (trackHeight - thumbSizeOff) / 2f - thumbSizeOff / 2f;
        rippleOverflow = Math.max(0, rippleOverflow);
        int minW = (int) (trackWidth + rippleOverflow * 2);
        int minH = (int) dpToPx(48);

        int width  = resolveSize(minW + paddingH, widthMeasureSpec);
        int height = resolveSize(Math.max(minH, (int) trackHeight + paddingV), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w <= 0 || h <= 0) { positionsReady = false; return; }

        float cx = (w - getPaddingLeft() - getPaddingRight()) / 2f + getPaddingLeft();
        trackCy = h / 2f;
        thumbCy = trackCy;

        trackLeft   = cx - trackWidth  / 2f;
        trackRight  = cx + trackWidth  / 2f;
        trackTop    = trackCy - trackHeight / 2f;
        trackBottom = trackCy + trackHeight / 2f;

        float marginOn  = (trackHeight - thumbSizeOn)  / 2f;
        float marginOff = (trackHeight - thumbSizeOff) / 2f;

        thumbOnCx  = trackRight - thumbSizeOn  / 2f - marginOn;
        thumbOffCx = trackLeft  + thumbSizeOff / 2f + marginOff;

        currentThumbCx = isChecked() ? thumbOnCx : thumbOffCx;
        positionsReady = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!positionsReady) return;

        boolean checked = isChecked();
        float   radius  = trackHeight / 2f;

        paintTrack.setColor(checked ? colorTrackOn : colorTrackOff);
        rectTrack.set(trackLeft, trackTop, trackRight, trackBottom);
        canvas.drawRoundRect(rectTrack, radius, radius, paintTrack);

        if (!checked) {
            paintTrackOutline.setColor(colorOutline);
            float inset = strokeWidth / 2f;
            rectTrack.inset(inset, inset);
            canvas.drawRoundRect(rectTrack, radius - inset, radius - inset, paintTrackOutline);
        }

        if (currentRippleAlpha > 0f) {
            int base  = checked ? colorRippleOn : colorRippleOff;
            int alpha = Math.round(currentRippleAlpha * RIPPLE_MAX_ALPHA);
            paintRipple.setARGB(alpha, (base >> 16) & 0xFF, (base >> 8) & 0xFF, base & 0xFF);
            canvas.drawCircle(currentThumbCx, thumbCy, rippleRadius, paintRipple);
        }

        float half     = currentThumbSize / 2f;
        float innerPad = (trackHeight - thumbSizeOn) / 2f;
        float minCx    = trackLeft  + half + innerPad * 0.5f;
        float maxCx    = trackRight - half - innerPad * 0.5f;
        float clampedCx = Math.max(minCx, Math.min(maxCx, currentThumbCx));

        paintThumb.setColor(checked ? colorThumbOn : colorThumbOff);
        rectThumb.set(clampedCx - half, thumbCy - half, clampedCx + half, thumbCy + half);
        canvas.drawOval(rectThumb, paintThumb);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                animateSize(thumbSizePressed, pressDuration, false);
                animateRipple(true);
                return true;
            case MotionEvent.ACTION_UP:
                if (isClickable()) {
                    toggle();
                } else {
                    animateSize(isChecked() ? thumbSizeOn : thumbSizeOff, pressDuration, false);
                }
                animateRipple(false);
                return true;
            case MotionEvent.ACTION_CANCEL:
                animateSize(isChecked() ? thumbSizeOn : thumbSizeOff, pressDuration, false);
                animateRipple(false);
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void setChecked(boolean checked) {
        boolean was = isChecked();
        super.setChecked(checked);
        if (checked != was) animateToggle(checked);
    }

    private void animateToggle(boolean checked) {
        if (!positionsReady) {
            currentThumbCx   = checked ? thumbOnCx  : thumbOffCx;
            currentThumbSize = checked ? thumbSizeOn : thumbSizeOff;
            invalidate();
            return;
        }

        final float startCx  = currentThumbCx;
        final float endCx    = checked ? thumbOnCx  : thumbOffCx;
        final float targetSz = checked ? thumbSizeOn : thumbSizeOff;

        stopToggleAnimators();

        translateAnimator = ValueAnimator.ofFloat(0f, 1f);
        translateAnimator.setDuration(animDuration);
        translateAnimator.setInterpolator(new DecelerateInterpolator());
        translateAnimator.addUpdateListener(anim -> {
            float t = (float) anim.getAnimatedValue();
            currentThumbCx = startCx + (endCx - startCx) * t;
            invalidate();
        });

        animateSize(targetSz, animDuration, true);
        translateAnimator.start();
    }

    private void animateSize(float target, int duration, boolean overshoot) {
        if (sizeAnimator != null) {
            sizeAnimator.cancel();
            sizeAnimator.removeAllUpdateListeners();
        }
        sizeAnimator = ValueAnimator.ofFloat(currentThumbSize, target);
        sizeAnimator.setDuration(duration);
        sizeAnimator.setInterpolator(overshoot ? new OvershootInterpolator(1.8f) : new DecelerateInterpolator());
        sizeAnimator.addUpdateListener(anim -> {
            currentThumbSize = (float) anim.getAnimatedValue();
            invalidate();
        });
        sizeAnimator.start();
    }

    private void animateRipple(boolean fadeIn) {
        if (rippleInAnimator  != null) { rippleInAnimator.cancel();  rippleInAnimator  = null; }
        if (rippleOutAnimator != null) { rippleOutAnimator.cancel(); rippleOutAnimator = null; }

        ValueAnimator anim = ValueAnimator.ofFloat(currentRippleAlpha, fadeIn ? 1f : 0f);
        anim.setDuration(fadeIn ? rippleInDuration : rippleOutDuration);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(a -> {
            currentRippleAlpha = (float) a.getAnimatedValue();
            invalidate();
        });
        anim.start();

        if (fadeIn) rippleInAnimator  = anim;
        else        rippleOutAnimator = anim;
    }

    private void stopToggleAnimators() {
        if (translateAnimator != null) {
            translateAnimator.cancel();
            translateAnimator.removeAllUpdateListeners();
            translateAnimator = null;
        }
        if (sizeAnimator != null) {
            sizeAnimator.cancel();
            sizeAnimator.removeAllUpdateListeners();
            sizeAnimator = null;
        }
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}