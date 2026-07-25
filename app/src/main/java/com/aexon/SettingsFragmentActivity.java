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
	private LinearLayout card1;
	private LinearLayout card2;
	private LinearLayout card3;
	private LinearLayout card4;
	private LinearLayout card5;
	private ImageView imageview1;
	private LinearLayout linear2;
	private AexonSwitch switch1;
	private TextView textview1;
	private TextView textview2;
	private LinearLayout linear4;
	private LinearLayout linear5;
	private ImageView imageview3;
	private LinearLayout linear6;
	private AexonSwitch switch3;
	private TextView textview5;
	private TextView textview6;
	private LinearLayout card_container1;
	private LinearLayout card_container2;
	private AexonEditText edittext1;
	private ImageView imageview4;
	private AexonEditText edittext2;
	private ImageView imageview5;
	private ImageView imageview2;
	private LinearLayout linear11;
	private AexonSwitch switch2;
	private TextView textview3;
	private TextView textview4;
	private ImageView imageview6;
	private LinearLayout linear13;
	private TextView textview7;
	private TextView textview8;
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
		card1 = _view.findViewById(R.id.card1);
		card2 = _view.findViewById(R.id.card2);
		card3 = _view.findViewById(R.id.card3);
		card4 = _view.findViewById(R.id.card4);
		card5 = _view.findViewById(R.id.card5);
		imageview1 = _view.findViewById(R.id.imageview1);
		linear2 = _view.findViewById(R.id.linear2);
		switch1 = _view.findViewById(R.id.switch1);
		textview1 = _view.findViewById(R.id.textview1);
		textview2 = _view.findViewById(R.id.textview2);
		linear4 = _view.findViewById(R.id.linear4);
		linear5 = _view.findViewById(R.id.linear5);
		imageview3 = _view.findViewById(R.id.imageview3);
		linear6 = _view.findViewById(R.id.linear6);
		switch3 = _view.findViewById(R.id.switch3);
		textview5 = _view.findViewById(R.id.textview5);
		textview6 = _view.findViewById(R.id.textview6);
		card_container1 = _view.findViewById(R.id.card_container1);
		card_container2 = _view.findViewById(R.id.card_container2);
		edittext1 = _view.findViewById(R.id.edittext1);
		imageview4 = _view.findViewById(R.id.imageview4);
		edittext2 = _view.findViewById(R.id.edittext2);
		imageview5 = _view.findViewById(R.id.imageview5);
		imageview2 = _view.findViewById(R.id.imageview2);
		linear11 = _view.findViewById(R.id.linear11);
		switch2 = _view.findViewById(R.id.switch2);
		textview3 = _view.findViewById(R.id.textview3);
		textview4 = _view.findViewById(R.id.textview4);
		imageview6 = _view.findViewById(R.id.imageview6);
		linear13 = _view.findViewById(R.id.linear13);
		textview7 = _view.findViewById(R.id.textview7);
		textview8 = _view.findViewById(R.id.textview8);
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
		switch3.setChecked(sp.getBoolean("focus_app", false));
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
		float radius = SketchwareUtil.getDimension(getContext(), R.dimen.card_radius_medium);
		
		// Card 1
		card1.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(radius).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		card1.setClickable(true);
		
		// Card 2
		card2.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(radius).build().build(getContext()));
		
		// Card 3
		card3.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(radius).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		card3.setClickable(true);
		
		//card 4
		card4.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(radius).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		card4.setClickable(true);
		
		// Card 5 (Kontainer utama, tanpa ripple)
		card5.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(radius).build().build(getContext()));
		
		// Linear 15 (Item Atas) -> Radius Top-Left & Top-Right
		linear15.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(radius, radius, 0, 0).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		linear15.setClickable(true);
		
		// Linear 16 (Item Tengah) -> Tanpa radius
		linear16.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		linear16.setClickable(true);
		
		// Linear 17 (Item Bawah) -> Radius Bottom-Right & Bottom-Left
		linear17.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(0, 0, radius, radius).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		linear17.setClickable(true);
		
		//card container
		card_container1.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainerLow()).cornerRadius(radius).build().build(getContext()));
		card_container2.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainerLow()).cornerRadius(radius).build().build(getContext()));
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