package com.aexon;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.aexon.material.aexonswitch.AexonSwitch;
import com.aexon.material.cardview.AexonCardView;
import com.aexon.material.edittext.AexonEditText;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;
import com.aexon.theme.AexonTheme;
import com.aexon.theme.AexonThemeListener;
import com.aexon.aexon.animation.AexonAnimationCompat;

public class SettingsFragmentActivity extends Fragment {
	
	private AexonTheme currentTheme;
	private LinearLayout root_view;
	private ViewTreeObserver.OnScrollChangedListener scrollShadowListener;
	private GradientDrawable shadowTop;
	private GradientDrawable shadowBottom;
	private boolean shadowInitialized = false;
	private final AexonThemeListener themeListener = (seedColor, theme) -> {
		_applyTheme(theme);
	};
	
	private ScrollView vscroll1;
	private LinearLayout container;
	private AexonCardView cardview1;
	private AexonCardView cardview2;
	private AexonCardView cardview3;
	private AexonCardView cardview4;
	private AexonCardView cardview5;
	private LinearLayout linear1;
	private ImageView imageview1;
	private LinearLayout linear2;
	private AexonSwitch switch1;
	private TextView textview1;
	private TextView textview2;
	private LinearLayout linear3;
	private LinearLayout linear4;
	private LinearLayout linear5;
	private ImageView imageview3;
	private LinearLayout linear6;
	private AexonSwitch switch3;
	private TextView textview5;
	private TextView textview6;
	private AexonCardView cardview6;
	private AexonCardView cardview7;
	private LinearLayout linear7;
	private AexonEditText edittext1;
	private ImageView imageview4;
	private LinearLayout linear8;
	private AexonEditText edittext2;
	private ImageView imageview5;
	private LinearLayout linear9;
	private ImageView imageview2;
	private LinearLayout linear11;
	private AexonSwitch switch2;
	private TextView textview3;
	private TextView textview4;
	private LinearLayout linear12;
	private ImageView imageview6;
	private LinearLayout linear13;
	private TextView textview7;
	private TextView textview8;
	private LinearLayout linear14;
	private LinearLayout linear15;
	private LinearLayout linear16;
	private LinearLayout linear17;
	private ImageView imageview7;
	private TextView textview9;
	private ImageView imageview8;
	private TextView textview10;
	private ImageView imageview9;
	private TextView textview11;
	
	private Intent ax_intent = new Intent();
	private SharedPreferences sp;
	
	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		View _view = _inflater.inflate(R.layout.settings_fragment, _container, false);
		initialize(_savedInstanceState, _view);
		initializeLogic();
		return _view;
	}
	
	private void initialize(Bundle _savedInstanceState, View _view) {
		root_view = _view.findViewById(R.id.root_view);
		vscroll1 = _view.findViewById(R.id.vscroll1);
		container = _view.findViewById(R.id.container);
		cardview1 = _view.findViewById(R.id.cardview1);
		cardview2 = _view.findViewById(R.id.cardview2);
		cardview3 = _view.findViewById(R.id.cardview3);
		cardview4 = _view.findViewById(R.id.cardview4);
		cardview5 = _view.findViewById(R.id.cardview5);
		linear1 = _view.findViewById(R.id.linear1);
		imageview1 = _view.findViewById(R.id.imageview1);
		linear2 = _view.findViewById(R.id.linear2);
		switch1 = _view.findViewById(R.id.switch1);
		textview1 = _view.findViewById(R.id.textview1);
		textview2 = _view.findViewById(R.id.textview2);
		linear3 = _view.findViewById(R.id.linear3);
		linear4 = _view.findViewById(R.id.linear4);
		linear5 = _view.findViewById(R.id.linear5);
		imageview3 = _view.findViewById(R.id.imageview3);
		linear6 = _view.findViewById(R.id.linear6);
		switch3 = _view.findViewById(R.id.switch3);
		textview5 = _view.findViewById(R.id.textview5);
		textview6 = _view.findViewById(R.id.textview6);
		cardview6 = _view.findViewById(R.id.cardview6);
		cardview7 = _view.findViewById(R.id.cardview7);
		linear7 = _view.findViewById(R.id.linear7);
		edittext1 = _view.findViewById(R.id.edittext1);
		imageview4 = _view.findViewById(R.id.imageview4);
		linear8 = _view.findViewById(R.id.linear8);
		edittext2 = _view.findViewById(R.id.edittext2);
		imageview5 = _view.findViewById(R.id.imageview5);
		linear9 = _view.findViewById(R.id.linear9);
		imageview2 = _view.findViewById(R.id.imageview2);
		linear11 = _view.findViewById(R.id.linear11);
		switch2 = _view.findViewById(R.id.switch2);
		textview3 = _view.findViewById(R.id.textview3);
		textview4 = _view.findViewById(R.id.textview4);
		linear12 = _view.findViewById(R.id.linear12);
		imageview6 = _view.findViewById(R.id.imageview6);
		linear13 = _view.findViewById(R.id.linear13);
		textview7 = _view.findViewById(R.id.textview7);
		textview8 = _view.findViewById(R.id.textview8);
		linear14 = _view.findViewById(R.id.linear14);
		linear15 = _view.findViewById(R.id.linear15);
		linear16 = _view.findViewById(R.id.linear16);
		linear17 = _view.findViewById(R.id.linear17);
		imageview7 = _view.findViewById(R.id.imageview7);
		textview9 = _view.findViewById(R.id.textview9);
		imageview8 = _view.findViewById(R.id.imageview8);
		textview10 = _view.findViewById(R.id.textview10);
		imageview9 = _view.findViewById(R.id.imageview9);
		textview11 = _view.findViewById(R.id.textview11);
		sp = getContext().getSharedPreferences("-sharedAexon", Activity.MODE_PRIVATE);
		
		switch1.setOnCheckedChangeListener((_buttonView, _isChecked) -> sp.edit().putBoolean("_sv_service_monitoring", _isChecked).apply());
		
		switch3.setOnCheckedChangeListener((_buttonView, _isChecked) -> {
			sp.edit().putBoolean("focus_app", _isChecked).commit();
			AexonAnimationCompat.animateVisibility(container);
			if (_isChecked) {
				linear5.setVisibility(View.VISIBLE);
			} else {
				linear5.setVisibility(View.GONE);
			}
		});
		
		linear16.setOnClickListener(_v -> {
			if (getContext() != null) {
				ax_intent.setClass(getContext(), AppearanceActivity.class);
				startActivity(ax_intent);
				getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});
	}
	
	private void initializeLogic() {
		switch1.setChecked(sp.getBoolean("_sv_service_monitoring", true));
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
		AexonTheme.getInstance().addListener(themeListener);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		AexonTheme.getInstance().removeListener(themeListener);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		_applyTheme(AexonTheme.getInstance());
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (scrollShadowListener != null && vscroll1 != null) {
			vscroll1.getViewTreeObserver().removeOnScrollChangedListener(scrollShadowListener);
			scrollShadowListener = null;
		}
		shadowInitialized = false;
		shadowTop = null;
		shadowBottom = null;
		root_view = null;
		vscroll1 = null;
	}
	public void _applyTheme(final AexonTheme _theme) {
		_initScrollShadow(_theme.getColorSurface());
		imageview1.setColorFilter(_theme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP);
		imageview2.setColorFilter(_theme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP);
		imageview3.setColorFilter(_theme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP);
		imageview4.setColorFilter(_theme.getColorOnSurfaceVariant(), PorterDuff.Mode.SRC_ATOP);
		imageview5.setColorFilter(_theme.getColorOnSurfaceVariant(), PorterDuff.Mode.SRC_ATOP);
		imageview6.setColorFilter(_theme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP);
		imageview7.setColorFilter(_theme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP);
		imageview8.setColorFilter(_theme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP);
		imageview9.setColorFilter(_theme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP);
		textview1.setTextColor(_theme.getColorOnSurface());
		textview5.setTextColor(_theme.getColorOnSurface());
		textview3.setTextColor(_theme.getColorOnSurface());
		textview7.setTextColor(_theme.getColorOnSurface());
		textview9.setTextColor(_theme.getColorOnSurface());
		textview10.setTextColor(_theme.getColorOnSurface());
		textview11.setTextColor(_theme.getColorOnSurface());
		textview2.setTextColor(_theme.getColorOnSurfaceVariant());
		textview6.setTextColor(_theme.getColorOnSurfaceVariant());
		textview4.setTextColor(_theme.getColorOnSurfaceVariant());
		textview8.setTextColor(_theme.getColorOnSurfaceVariant());
		edittext1.setTextColor(_theme.getColorOnSurface());
		edittext2.setTextColor(_theme.getColorOnSurface());
		edittext1.setHintTextColor(_theme.getColorOnSurfaceVariant());
		edittext2.setHintTextColor(_theme.getColorOnSurfaceVariant());
		linear1.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		linear1.setClickable(true);
		linear1.setFocusable(true);
		
		linear3.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		linear3.setClickable(true);
		linear3.setFocusable(true);
		
		linear9.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		linear9.setClickable(true);
		linear9.setFocusable(true);
		
		linear12.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		linear12.setClickable(true);
		linear12.setFocusable(true);
		
		linear15.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		linear15.setClickable(true);
		linear15.setFocusable(true);
		
		linear16.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		linear16.setClickable(true);
		linear16.setFocusable(true);
		
		linear17.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		linear17.setClickable(true);
		linear17.setFocusable(true);
		linear7.setBackgroundColor(_theme.getColorSurfaceContainerLow());
		linear8.setBackgroundColor(_theme.getColorSurfaceContainerLow());
	}
	
	
	public void _initScrollShadow(final int _color) {
		int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
		int transparentColor = _color & 0x00FFFFFF;
		
		// Kalau sudah pernah init, cukup update warna saja
		if (shadowInitialized && shadowTop != null && shadowBottom != null) {
			shadowTop.setColors(new int[]{_color, transparentColor});
			shadowBottom.setColors(new int[]{_color, transparentColor});
			return;
		}
		
		shadowTop = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{_color, transparentColor});
		shadowTop.setShape(GradientDrawable.RECTANGLE);
		shadowTop.setSize(0, height);
		
		shadowBottom = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{_color, transparentColor});
		shadowBottom.setShape(GradientDrawable.RECTANGLE);
		shadowBottom.setSize(0, height);
		
		final LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{shadowTop, shadowBottom});
		layerDrawable.setLayerGravity(0, Gravity.TOP);
		layerDrawable.setLayerGravity(1, Gravity.BOTTOM);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			root_view.setForeground(layerDrawable);
		}
		
		shadowTop.setAlpha(0);
		shadowBottom.setAlpha(255);
		
		scrollShadowListener = () -> {
			if (vscroll1 == null || shadowTop == null || shadowBottom == null) return;
			int scrollY = vscroll1.getScrollY();
			
			int topAlpha = Math.min(255, scrollY * 255 / height);
			shadowTop.setAlpha(topAlpha);
			
			View child = vscroll1.getChildAt(0);
			if (child == null) return;
			int maxScroll = child.getHeight() - vscroll1.getHeight();
			int distanceFromBottom = maxScroll - scrollY;
			int bottomAlpha = Math.max(0, Math.min(255, distanceFromBottom * 255 / height));
			shadowBottom.setAlpha(bottomAlpha);
		};
		
		vscroll1.getViewTreeObserver().addOnScrollChangedListener(scrollShadowListener);
		shadowInitialized = true;
	}
	
}