package com.aexon.material.cardview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.aexon.R;

public class AexonCardView extends FrameLayout {

	private final Path clipPath = new Path();
	private final RectF rectF = new RectF();
	private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private float rLT, rRT, rLB, rRB;
	private float cLT, cRT, cLB, cRB;
	private boolean isCutFixed;
	private float cutFixedTop;
	private float cutFixedBottom;
	private float cutMultiplier = 1.0f;

	private float strokeWidth;
	private int strokeColor, strokeStartColor, strokeEndColor;
	private boolean hasStrokeGradient;

	private int shadowColor, shadowStartColor, shadowEndColor;
	private float shadowRadius, shadowDx, shadowDy;
	private boolean isGlowActive;
	private boolean hasShadowGradient;
	private boolean isShadowEnabled;

	private float elevation;
	private float elevationMax;
	private float currentElevation;
	private ValueAnimator elevationAnimator;
    private static final float RIPPLE_HOLD_ALPHA = 0.6f;

	private boolean isRippleEnabled;
	private int rippleColor;
	private float rippleX, rippleY;
	private float rippleRadius;
	private float rippleMaxRadius;
	private float rippleAlpha;
	private ValueAnimator rippleAnimator;
	private ValueAnimator rippleFadeAnimator;
	private boolean isFingerDown;

	public AexonCardView(Context context) {
		this(context, null);
	}

	public AexonCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		strokePaint.setStyle(Paint.Style.STROKE);
		shadowPaint.setStyle(Paint.Style.FILL);
		ripplePaint.setStyle(Paint.Style.FILL);

		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AexonCardView);
			loadAttributes(a);
			a.recycle();
		}

		setupShadowFilter();
		setWillNotDraw(false);
		setClickable(true);
	}

	private void loadAttributes(TypedArray a) {
		float boxRadius = a.getDimension(R.styleable.AexonCardView_boxRadius, -1);
		float boxRadiusCut = a.getDimension(R.styleable.AexonCardView_boxRadiusCut, -1);

		if (boxRadius >= 0) {
			rLT = rRT = rLB = rRB = boxRadius;
		} else {
			rLT = a.getDimension(R.styleable.AexonCardView_boxRadiusLeftTop, 0);
			rRT = a.getDimension(R.styleable.AexonCardView_boxRadiusRightTop, 0);
			rLB = a.getDimension(R.styleable.AexonCardView_boxRadiusLeftBottom, 0);
			rRB = a.getDimension(R.styleable.AexonCardView_boxRadiusRightBottom, 0);
		}

		if (boxRadiusCut >= 0) {
			cLT = cRT = cLB = cRB = boxRadiusCut;
		} else {
			cLT = a.getDimension(R.styleable.AexonCardView_boxRadiusCutLeftTop, 0);
			cRT = a.getDimension(R.styleable.AexonCardView_boxRadiusCutRightTop, 0);
			cLB = a.getDimension(R.styleable.AexonCardView_boxRadiusCutLeftBottom, 0);
			cRB = a.getDimension(R.styleable.AexonCardView_boxRadiusCutRightBottom, 0);
		}

		isCutFixed = a.getBoolean(R.styleable.AexonCardView_boxRadiusCutFixed, false);
		cutFixedTop = a.getDimension(R.styleable.AexonCardView_boxRadiusCutFixedTop, 0);
		cutFixedBottom = a.getDimension(R.styleable.AexonCardView_boxRadiusCutFixedBottom, 0);

		int angleEnum = a.getInt(R.styleable.AexonCardView_boxRadiusCutAngle, 4);
		setBoxRadiusCutAngleInternal(angleEnum);

		strokeWidth = a.getDimension(R.styleable.AexonCardView_boxViewStroke, 0);
		strokeColor = a.getColor(R.styleable.AexonCardView_boxViewStrokeColor, Color.WHITE);
		strokeStartColor = a.getColor(R.styleable.AexonCardView_boxViewStrokeGradientStart, 0);
		strokeEndColor = a.getColor(R.styleable.AexonCardView_boxViewStrokeGradientEnd, 0);
		hasStrokeGradient = (strokeStartColor != 0 && strokeEndColor != 0);

		shadowColor = a.getColor(R.styleable.AexonCardView_boxShadowColor, 0x40000000);
		shadowRadius = a.getDimension(R.styleable.AexonCardView_boxShadowRadius, 0);
		shadowDx = a.getDimension(R.styleable.AexonCardView_boxShadowDx, 0);
		shadowDy = a.getDimension(R.styleable.AexonCardView_boxShadowDy, 0);
		isGlowActive = a.getBoolean(R.styleable.AexonCardView_boxGlow, false);
		shadowStartColor = a.getColor(R.styleable.AexonCardView_boxShadowGradientStart, 0);
		shadowEndColor = a.getColor(R.styleable.AexonCardView_boxShadowGradientEnd, 0);
		hasShadowGradient = (shadowStartColor != 0 && shadowEndColor != 0);
		isShadowEnabled = a.getBoolean(R.styleable.AexonCardView_boxShadow, shadowRadius > 0);

		elevation = a.getDimension(R.styleable.AexonCardView_boxElevation, 0);
		elevationMax = a.getDimension(R.styleable.AexonCardView_boxElevationMax, elevation * 2);
		currentElevation = elevation;

		isRippleEnabled = a.getBoolean(R.styleable.AexonCardView_boxRipple, false);
		rippleColor = a.getColor(R.styleable.AexonCardView_boxRippleColor, 0x35000000);

		strokePaint.setStrokeWidth(strokeWidth);
		if (!hasStrokeGradient) strokePaint.setColor(strokeColor);
		if (!hasShadowGradient) shadowPaint.setColor(shadowColor);

		ripplePaint.setColor(rippleColor);

		if (elevation > 0 && shadowRadius == 0) {
			applyElevationToShadow(elevation);
		}
	}

	private void applyElevationToShadow(float elev) {
		float density = getResources().getDisplayMetrics().density;
		shadowRadius = elev * 0.8f + (2 * density);
		shadowDy = elev * 0.4f;
		shadowDx = 0;
		if (!isShadowEnabled) isShadowEnabled = true;
		if (shadowColor == 0) shadowColor = 0x40000000;
		shadowPaint.setColor(shadowColor);
		setupShadowFilter();
	}

	private void setBoxRadiusCutAngleInternal(int angleEnum) {
		switch (angleEnum) {
			case 0: cutMultiplier = (float) Math.tan(Math.toRadians(5)); break;
			case 1: cutMultiplier = (float) Math.tan(Math.toRadians(15)); break;
			case 2: cutMultiplier = (float) Math.tan(Math.toRadians(30)); break;
			case 3: cutMultiplier = (float) Math.tan(Math.toRadians(35)); break;
			case 4:
			default: cutMultiplier = 1.0f; break;
		}
	}

	private void setupShadowFilter() {
		if ((shadowRadius > 0) && isShadowEnabled) {
			BlurMaskFilter.Blur blurType = isGlowActive ? BlurMaskFilter.Blur.OUTER : BlurMaskFilter.Blur.NORMAL;
			shadowPaint.setMaskFilter(new BlurMaskFilter(shadowRadius, blurType));
		} else {
			shadowPaint.setMaskFilter(null);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		updatePath(w, h);
	}

	private void updatePath(int w, int h) {
		float activeShadowRadius = (isShadowEnabled || elevation > 0) ? shadowRadius : 0;
		float padding = activeShadowRadius + Math.max(Math.abs(shadowDx), Math.abs(shadowDy)) + strokeWidth;
		float contentW = w - (padding * 2);
		float contentH = h - (padding * 2);
		final float start = padding;

		clipPath.reset();
		drawCorners(start, contentW, contentH);
		clipPath.close();

		applyGradients(start, contentW, contentH);
		setupOutline(start, contentW, contentH);
	}

	private void drawCorners(float s, float w, float h) {
		float fTop = cutFixedTop;
		float fBot = cutFixedBottom;

		if (isCutFixed && fTop == 0 && fBot == 0) {
			float density = getResources().getDisplayMetrics().density;
			fTop = 5f * density;
			fBot = 5f * density;
		}

		final float M = cutMultiplier;

		if (cLT > 0) {
			float cx = cLT * M;
			float cy = cLT;
			float rT = Math.min(fTop, cLT / 2.1f);
			float rB = Math.min(fBot, cLT / 2.1f);
			if (rB > 0 || rT > 0) {
				if (rB > 0) {
					clipPath.moveTo(s, s + cy + rB);
					clipPath.quadTo(s, s + cy, s + (rB * M), s + cy - rB);
				} else {
					clipPath.moveTo(s, s + cy);
				}
				if (rT > 0) {
					clipPath.lineTo(s + cx - (rT * M), s + rT);
					clipPath.quadTo(s + cx, s, s + cx + rT, s);
				} else {
					clipPath.lineTo(s + cx, s);
				}
			} else {
				clipPath.moveTo(s, s + cy);
				clipPath.lineTo(s + cx, s);
			}
		} else if (rLT > 0) {
			rectF.set(s, s, s + rLT * 2, s + rLT * 2);
			clipPath.arcTo(rectF, 180, 90);
		} else {
			clipPath.moveTo(s, s);
		}

		if (cRT > 0) {
			float cx = cRT * M;
			float cy = cRT;
			float rT = Math.min(fTop, cRT / 2.1f);
			float rB = Math.min(fBot, cRT / 2.1f);
			if (rT > 0 || rB > 0) {
				if (rT > 0) {
					clipPath.lineTo(s + w - cx - rT, s);
					clipPath.quadTo(s + w - cx, s, s + w - cx + (rT * M), s + rT);
				} else {
					clipPath.lineTo(s + w - cx, s);
				}
				if (rB > 0) {
					clipPath.lineTo(s + w - (rB * M), s + cy - rB);
					clipPath.quadTo(s + w, s + cy, s + w, s + cy + rB);
				} else {
					clipPath.lineTo(s + w, s + cy);
				}
			} else {
				clipPath.lineTo(s + w - cx, s);
				clipPath.lineTo(s + w, s + cy);
			}
		} else if (rRT > 0) {
			rectF.set(s + w - rRT * 2, s, s + w, s + rRT * 2);
			clipPath.arcTo(rectF, 270, 90);
		} else {
			clipPath.lineTo(s + w, s);
		}

		if (cRB > 0) {
			float cx = cRB * M;
			float cy = cRB;
			float rT = Math.min(fTop, cRB / 2.1f);
			float rB = Math.min(fBot, cRB / 2.1f);
			if (rT > 0 || rB > 0) {
				if (rT > 0) {
					clipPath.lineTo(s + w, s + h - cy - rT);
					clipPath.quadTo(s + w, s + h - cy, s + w - (rT * M), s + h - cy + rT);
				} else {
					clipPath.lineTo(s + w, s + h - cy);
				}
				if (rB > 0) {
					clipPath.lineTo(s + w - cx + (rB * M), s + h - rB);
					clipPath.quadTo(s + w - cx, s + h, s + w - cx - rB, s + h);
				} else {
					clipPath.lineTo(s + w - cx, s + h);
				}
			} else {
				clipPath.lineTo(s + w, s + h - cy);
				clipPath.lineTo(s + w - cx, s + h);
			}
		} else if (rRB > 0) {
			rectF.set(s + w - rRB * 2, s + h - rRB * 2, s + w, s + h);
			clipPath.arcTo(rectF, 0, 90);
		} else {
			clipPath.lineTo(s + w, s + h);
		}

		if (cLB > 0) {
			float cx = cLB * M;
			float cy = cLB;
			float rB = Math.min(fBot, cLB / 2.1f);
			float rT = Math.min(fTop, cLB / 2.1f);
			if (rB > 0 || rT > 0) {
				if (rB > 0) {
					clipPath.lineTo(s + cx + rB, s + h);
					clipPath.quadTo(s + cx, s + h, s + cx - (rB * M), s + h - rB);
				} else {
					clipPath.lineTo(s + cx, s + h);
				}
				if (rT > 0) {
					clipPath.lineTo(s + (rT * M), s + h - cy + rT);
					clipPath.quadTo(s, s + h - cy, s, s + h - cy - rT);
				} else {
					clipPath.lineTo(s, s + h - cy);
				}
			} else {
				clipPath.lineTo(s + cx, s + h);
				clipPath.lineTo(s, s + h - cy);
			}
		} else if (rLB > 0) {
			rectF.set(s, s + h - rLB * 2, s + rLB * 2, s + h);
			clipPath.arcTo(rectF, 90, 90);
		} else {
			clipPath.lineTo(s, s + h);
		}
	}

	private void applyGradients(float start, float w, float h) {
		if (hasStrokeGradient) {
			strokePaint.setShader(new LinearGradient(start, start, start + w, start + h, strokeStartColor, strokeEndColor, Shader.TileMode.CLAMP));
		}
		if (hasShadowGradient) {
			shadowPaint.setShader(new LinearGradient(start, start, start + w, start + h, shadowStartColor, shadowEndColor, Shader.TileMode.CLAMP));
		} else if (hasStrokeGradient) {
			shadowPaint.setShader(strokePaint.getShader());
		}
	}

	private void setupOutline(final float start, final float w, final float h) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			setOutlineProvider(new ViewOutlineProvider() {
				@Override
				public void getOutline(View view, Outline outline) {
					if (clipPath.isConvex()) {
						outline.setConvexPath(clipPath);
					} else {
						outline.setRect((int) start, (int) start, (int) (start + w), (int) (start + h));
					}
				}
			});
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isRippleEnabled) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rippleX = event.getX();
					rippleY = event.getY();
					startRipple();
					if (elevation > 0) animateElevation(elevation, elevationMax);
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					cancelRipple();
					if (elevation > 0) animateElevation(currentElevation, elevation);
					break;
			}
		} else if (elevation > 0) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					animateElevation(elevation, elevationMax);
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					animateElevation(currentElevation, elevation);
					break;
			}
		}
		return super.onTouchEvent(event);
	}

	private void startRipple() {
		if (rippleAnimator != null) rippleAnimator.cancel();
		if (rippleFadeAnimator != null) rippleFadeAnimator.cancel();

		isFingerDown = true;
		rippleRadius = 0;
		rippleAlpha = 1f;

		float w = getWidth();
		float h = getHeight();
		float dx = Math.max(rippleX, w - rippleX);
		float dy = Math.max(rippleY, h - rippleY);
		rippleMaxRadius = (float) Math.sqrt(dx * dx + dy * dy);

		rippleAnimator = ValueAnimator.ofFloat(0f, rippleMaxRadius);
		rippleAnimator.setDuration(400);
		rippleAnimator.setInterpolator(new DecelerateInterpolator());
		rippleAnimator.addUpdateListener(anim -> {
			rippleRadius = (float) anim.getAnimatedValue();
			invalidate();
		});
		rippleAnimator.start();

		rippleFadeAnimator = ValueAnimator.ofFloat(1f, RIPPLE_HOLD_ALPHA);
		rippleFadeAnimator.setDuration(400);
		rippleFadeAnimator.setInterpolator(new DecelerateInterpolator());
		rippleFadeAnimator.addUpdateListener(anim -> {
			rippleAlpha = (float) anim.getAnimatedValue();
			ripplePaint.setAlpha((int) (Color.alpha(rippleColor) * rippleAlpha));
			invalidate();
		});
		rippleFadeAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(android.animation.Animator animation) {
				if (!isFingerDown) {
					fadeOutRipple();
				}
			}
		});
		rippleFadeAnimator.start();
	}

	private void cancelRipple() {
		isFingerDown = false;
		boolean holdDone = rippleFadeAnimator == null || !rippleFadeAnimator.isRunning();
		if (holdDone) {
			fadeOutRipple();
		}
	}

	private void fadeOutRipple() {
		if (rippleFadeAnimator != null) rippleFadeAnimator.cancel();

		rippleFadeAnimator = ValueAnimator.ofFloat(rippleAlpha, 0f);
		rippleFadeAnimator.setDuration(200);
		rippleFadeAnimator.setInterpolator(new DecelerateInterpolator());
		rippleFadeAnimator.addUpdateListener(anim -> {
			rippleAlpha = (float) anim.getAnimatedValue();
			ripplePaint.setAlpha((int) (Color.alpha(rippleColor) * rippleAlpha));
			invalidate();
		});
		rippleFadeAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(android.animation.Animator animation) {
				rippleRadius = 0;
				rippleAlpha = 0;
				invalidate();
			}
		});
		rippleFadeAnimator.start();
	}

	private void animateElevation(float from, float to) {
		if (elevationAnimator != null) elevationAnimator.cancel();
		elevationAnimator = ValueAnimator.ofFloat(from, to);
		elevationAnimator.setDuration(150);
		elevationAnimator.setInterpolator(new DecelerateInterpolator());
		elevationAnimator.addUpdateListener(anim -> {
			currentElevation = (float) anim.getAnimatedValue();
			applyElevationToShadow(currentElevation);
			updatePathAndInvalidate();
		});
		elevationAnimator.start();
	}

	@Override
	public void draw(Canvas canvas) {
		if (isShadowEnabled && shadowRadius > 0) {
			canvas.save();
			canvas.translate(shadowDx, shadowDy);
			ensureSoftwareLayer();
			canvas.drawPath(clipPath, shadowPaint);
			canvas.restore();
		}

		canvas.save();
		canvas.clipPath(clipPath);
		if (getLayerType() == LAYER_TYPE_SOFTWARE) {
			setLayerType(LAYER_TYPE_HARDWARE, null);
		}
		super.draw(canvas);

		if (isRippleEnabled && rippleRadius > 0) {
			canvas.drawCircle(rippleX, rippleY, rippleRadius, ripplePaint);
		}

		canvas.restore();

		if (strokeWidth > 0) {
			canvas.drawPath(clipPath, strokePaint);
		}
	}

	private void ensureSoftwareLayer() {
		if (getLayerType() != LAYER_TYPE_SOFTWARE) {
			setLayerType(LAYER_TYPE_SOFTWARE, null);
		}
	}

	private void updatePathAndInvalidate() {
		if (getWidth() > 0 && getHeight() > 0) {
			updatePath(getWidth(), getHeight());
		}
		invalidate();
	}

	public void setBoxRadius(float radius) {
		rLT = rRT = rLB = rRB = radius;
		updatePathAndInvalidate();
	}

	public void setBoxRadiusCut(float radius) {
		cLT = cRT = cLB = cRB = radius;
		updatePathAndInvalidate();
	}

	public void setBoxRadiusCutAngle(int angleEnum) {
		setBoxRadiusCutAngleInternal(angleEnum);
		updatePathAndInvalidate();
	}

	public void setBoxRadiusLeftTop(float radius) {
		this.rLT = radius;
		updatePathAndInvalidate();
	}

	public void setBoxRadiusRightTop(float radius) {
		this.rRT = radius;
		updatePathAndInvalidate();
	}

	public void setBoxRadiusLeftBottom(float radius) {
		this.rLB = radius;
		updatePathAndInvalidate();
	}

	public void setBoxRadiusRightBottom(float radius) {
		this.rRB = radius;
		updatePathAndInvalidate();
	}

	public void setBoxRadiusCutLeftTop(float radius) {
		this.cLT = radius;
		updatePathAndInvalidate();
	}

	public void setBoxRadiusCutRightTop(float radius) {
		this.cRT = radius;
		updatePathAndInvalidate();
	}

	public void setBoxRadiusCutLeftBottom(float radius) {
		this.cLB = radius;
		updatePathAndInvalidate();
	}

	public void setBoxRadiusCutRightBottom(float radius) {
		this.cRB = radius;
		updatePathAndInvalidate();
	}

	public void setBoxRadiusCutFixed(boolean isFixed) {
		this.isCutFixed = isFixed;
		updatePathAndInvalidate();
	}

	public void setBoxRadiusCutFixedTop(float radius) {
		this.cutFixedTop = radius;
		updatePathAndInvalidate();
	}

	public void setBoxRadiusCutFixedBottom(float radius) {
		this.cutFixedBottom = radius;
		updatePathAndInvalidate();
	}

	public void setAexonCardViewStroke(float width) {
		this.strokeWidth = width;
		strokePaint.setStrokeWidth(width);
		updatePathAndInvalidate();
	}

	public void setAexonCardViewStrokeColor(int color) {
		this.strokeColor = color;
		if (!hasStrokeGradient) strokePaint.setColor(color);
		invalidate();
	}

	public void setAexonCardViewStrokeGradient(int startColor, int endColor) {
		this.strokeStartColor = startColor;
		this.strokeEndColor = endColor;
		this.hasStrokeGradient = (startColor != 0 && endColor != 0);
		if (!hasStrokeGradient) strokePaint.setColor(strokeColor);
		updatePathAndInvalidate();
	}

	public void setBoxShadow(boolean enabled) {
		this.isShadowEnabled = enabled;
		setupShadowFilter();
		updatePathAndInvalidate();
	}

	public void setBoxShadowColor(int color) {
		this.shadowColor = color;
		if (!hasShadowGradient) shadowPaint.setColor(color);
		invalidate();
	}

	public void setBoxShadowRadius(float radius) {
		this.shadowRadius = radius;
		setupShadowFilter();
		updatePathAndInvalidate();
	}

	public void setBoxShadowDx(float dx) {
		this.shadowDx = dx;
		updatePathAndInvalidate();
	}

	public void setBoxShadowDy(float dy) {
		this.shadowDy = dy;
		updatePathAndInvalidate();
	}

	public void setBoxGlow(boolean isActive) {
		this.isGlowActive = isActive;
		setupShadowFilter();
		invalidate();
	}

	public boolean isBoxGlow() {
		return isGlowActive;
	}

	public void setBoxShadowGradient(int startColor, int endColor) {
		this.shadowStartColor = startColor;
		this.shadowEndColor = endColor;
		this.hasShadowGradient = (startColor != 0 && endColor != 0);
		if (!hasShadowGradient) shadowPaint.setColor(shadowColor);
		updatePathAndInvalidate();
	}

	public void setBoxElevation(float elev) {
		this.elevation = elev;
		this.currentElevation = elev;
		if (elevationMax < elev) elevationMax = elev * 2;
		applyElevationToShadow(elev);
		updatePathAndInvalidate();
	}

	public void setBoxElevationMax(float elevMax) {
		this.elevationMax = elevMax;
	}

	public void setBoxRipple(boolean enabled) {
		this.isRippleEnabled = enabled;
		invalidate();
	}

	public void setBoxRippleColor(int color) {
		this.rippleColor = color;
		ripplePaint.setColor(color);
		invalidate();
	}
}