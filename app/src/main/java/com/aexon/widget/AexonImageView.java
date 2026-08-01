package com.aexon.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;

import com.aexon.R;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AexonImageView extends ImageView {
	
	private float radiusTopLeft = 0f;
	private float radiusTopRight = 0f;
	private float radiusBottomLeft = 0f;
	private float radiusBottomRight = 0f;
	private float strokeWidth = 0f;
	
	@ColorInt
	private int strokeColor = Color.TRANSPARENT;
	
	@NonNull
	private final Path clipPath = new Path();
	
	@NonNull
	private final Path strokePath = new Path();
	
	@NonNull
	private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	@NonNull
	private final RectF boundsRect = new RectF();
	
	@NonNull
	private final RectF strokeRect = new RectF();
	
	private final float[] radii = new float[8];
	private final float[] strokeRadii = new float[8];
	
	public AexonImageView(@NonNull Context context) {
		this(context, null);
	}
	
	public AexonImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public AexonImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		strokePaint.setStyle(Paint.Style.STROKE);
		init(context, attrs);
	}
	
	private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
		if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AexonImageView);
			try {
				float globalRadius = ta.getDimension(R.styleable.AexonImageView_ax_img_radius, 0f);
				if (globalRadius > 0f) {
					radiusTopLeft = globalRadius;
					radiusTopRight = globalRadius;
					radiusBottomLeft = globalRadius;
					radiusBottomRight = globalRadius;
				} else {
					radiusTopLeft = ta.getDimension(R.styleable.AexonImageView_ax_radiusTopLeft, 0f);
					radiusTopRight = ta.getDimension(R.styleable.AexonImageView_ax_radiusTopRight, 0f);
					radiusBottomLeft = ta.getDimension(R.styleable.AexonImageView_ax_radiusBottomLeft, 0f);
					radiusBottomRight = ta.getDimension(R.styleable.AexonImageView_ax_radiusBottomRight, 0f);
				}
				strokeWidth = ta.getDimension(R.styleable.AexonImageView_ax_strokeWidth, 0f);
				strokeColor = ta.getColor(R.styleable.AexonImageView_ax_strokeColor, Color.TRANSPARENT);
				
				float elevation = ta.getDimension(R.styleable.AexonImageView_ax_img_elevation, 0f);
				if (elevation > 0f) {
					setElevation(elevation);
				}
			} finally {
				ta.recycle();
			}
		}
		setupOutlineProvider();
	}
	
	private void setupOutlineProvider() {
		setOutlineProvider(new ViewOutlineProvider() {
			@Override
			public void getOutline(@NonNull View view, @NonNull Outline outline) {
				boolean isUniform = (radiusTopLeft == radiusTopRight) &&
				(radiusTopLeft == radiusBottomLeft) &&
				(radiusTopLeft == radiusBottomRight);
				
				if (isUniform) {
					outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radiusTopLeft);
				} else {
					Path path = new Path();
					float[] r = new float[]{
						radiusTopLeft, radiusTopLeft,
						radiusTopRight, radiusTopRight,
						radiusBottomRight, radiusBottomRight,
						radiusBottomLeft, radiusBottomLeft
					};
					path.addRoundRect(new RectF(0, 0, view.getWidth(), view.getHeight()), r, Path.Direction.CW);
					outline.setConvexPath(path);
				}
			}
		});
		
		updateClipToOutline();
	}
	
	private void updateClipToOutline() {
		boolean isUniform = (radiusTopLeft == radiusTopRight) &&
		(radiusTopLeft == radiusBottomLeft) &&
		(radiusTopLeft == radiusBottomRight);
		setClipToOutline(isUniform);
	}
	
	@Override
	public void setImageResource(@DrawableRes int resId) {
		super.setImageResource(resId);
		invalidate();
	}
	
	public void setImageResource(@DrawableRes int resId, float radiusDp) {
		setRadius(radiusDp);
		super.setImageResource(resId);
		invalidate();
	}
	
	@Override
	public void setImageDrawable(@Nullable Drawable drawable) {
		super.setImageDrawable(drawable);
		invalidate();
	}
	
	public void setImageDrawable(@Nullable Drawable drawable, float radiusDp) {
		setRadius(radiusDp);
		super.setImageDrawable(drawable);
		invalidate();
	}
	
	@Override
	public void setImageBitmap(@Nullable Bitmap bm) {
		super.setImageBitmap(bm);
		invalidate();
	}
	
	public void setImageBitmap(@Nullable Bitmap bm, float radiusDp) {
		setRadius(radiusDp);
		super.setImageBitmap(bm);
		invalidate();
	}
	
	@Override
	public void setImageURI(@Nullable Uri uri) {
		super.setImageURI(uri);
		invalidate();
	}
	
	public void setImageURI(@Nullable Uri uri, float radiusDp) {
		setRadius(radiusDp);
		super.setImageURI(uri);
		invalidate();
	}
	
	public void setRadiusTopLeft(@Px float radiusTopLeft) {
		this.radiusTopLeft = radiusTopLeft;
		updateClipToOutline();
		invalidateOutline();
		invalidate();
	}
	
	public void setRadiusTopRight(@Px float radiusTopRight) {
		this.radiusTopRight = radiusTopRight;
		updateClipToOutline();
		invalidateOutline();
		invalidate();
	}
	
	public void setRadiusBottomLeft(@Px float radiusBottomLeft) {
		this.radiusBottomLeft = radiusBottomLeft;
		updateClipToOutline();
		invalidateOutline();
		invalidate();
	}
	
	public void setRadiusBottomRight(@Px float radiusBottomRight) {
		this.radiusBottomRight = radiusBottomRight;
		updateClipToOutline();
		invalidateOutline();
		invalidate();
	}
	
	public void setRadius(@Dimension(unit = Dimension.DP) float radiusDp) {
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radiusDp, getResources().getDisplayMetrics());
		this.radiusTopLeft = px;
		this.radiusTopRight = px;
		this.radiusBottomLeft = px;
		this.radiusBottomRight = px;
		updateClipToOutline();
		invalidateOutline();
		invalidate();
	}
	
	public void setStrokeWidth(@Px float strokeWidth) {
		this.strokeWidth = strokeWidth;
		invalidate();
	}
	
	public void setStrokeColor(@ColorInt int strokeColor) {
		this.strokeColor = strokeColor;
		invalidate();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		invalidateOutline();
	}
	
	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		radii[0] = radiusTopLeft;
		radii[1] = radiusTopLeft;
		radii[2] = radiusTopRight;
		radii[3] = radiusTopRight;
		radii[4] = radiusBottomRight;
		radii[5] = radiusBottomRight;
		radii[6] = radiusBottomLeft;
		radii[7] = radiusBottomLeft;
		
		boundsRect.set(0f, 0f, getWidth(), getHeight());
		
		clipPath.reset();
		clipPath.addRoundRect(boundsRect, radii, Path.Direction.CW);
		
		canvas.save();
		canvas.clipPath(clipPath);
		
		super.onDraw(canvas);
		
		canvas.restore();
		
		if (strokeWidth > 0f && strokeColor != Color.TRANSPARENT) {
			strokePaint.setStrokeWidth(strokeWidth);
			strokePaint.setColor(strokeColor);
			float half = strokeWidth / 2f;
			
			for (int i = 0; i < 8; i++) {
				strokeRadii[i] = Math.max(0f, radii[i] - half);
			}
			
			strokePath.reset();
			strokeRect.set(half, half, getWidth() - half, getHeight() - half);
			strokePath.addRoundRect(strokeRect, strokeRadii, Path.Direction.CW);
			canvas.drawPath(strokePath, strokePaint);
		}
	}
}