package com.aexon;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.aexon.widget.AexonImageView;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;
import com.aexon.Aexon;
import com.aexon.AexonMain;
import com.aexon.material.toasty.AexonToast;
import android.content.pm.PackageInfo;
import com.aexon.aexon.animation.AexonAnimationCompat;
import android.view.animation.PathInterpolator;
import com.aexon.theme.AexonThemeListener;
import com.aexon.theme.AexonTheme;

public class HomeFragmentActivity extends Fragment {
	
	private AexonTheme currentTheme;
	private View root_view;
	private ViewTreeObserver.OnScrollChangedListener scrollShadowListener;
	private GradientDrawable shadowTop;
	private GradientDrawable shadowBottom;
	private boolean shadowInitialized = false;
	
	//var timer tmp deamon
	private final Handler timerHandler = new Handler(Looper.getMainLooper());
	private long daemonStartTime = 0;
	private final AexonThemeListener themeListener = (seedColor, theme) -> {
		_applyTheme(theme);
	};
	private String bridgePath = "";
	
	private ScrollView vscroll1;
	private LinearLayout container;
	private LinearLayout card_status;
	private LinearLayout sub_container;
	private LinearLayout card_info;
	private LinearLayout card_support;
	private LinearLayout card_problem;
	private LinearLayout card_contributor;
	private LinearLayout linear24;
	private LinearLayout linear25;
	private LinearLayout linear4;
	private TextView textview2;
	private LinearLayout linear26;
	private TextView textview1;
	private TextView textview3;
	private TextView tmp_timer;
	private AexonImageView imageview1;
	private LinearLayout card_plugin;
	private LinearLayout card_boost;
	private ImageView imageview2;
	private LinearLayout linear7;
	private ImageView imageview3;
	private TextView textview4;
	private TextView textview5;
	private ImageView imageview4;
	private LinearLayout linear10;
	private ImageView imageview5;
	private TextView textview6;
	private TextView textview7;
	private LinearLayout card1;
	private LinearLayout card2;
	private LinearLayout card3;
	private ImageView imageview6;
	private TextView textview8;
	private TextView textview9;
	private ImageView imageview7;
	private TextView textview10;
	private TextView textview11;
	private ImageView imageview8;
	private TextView textview12;
	private TextView textview13;
	private LinearLayout linear20;
	private LinearLayout linear21;
	private TextView textview14;
	private TextView textview15;
	private ImageView imageview9;
	private LinearLayout linear22;
	private LinearLayout linear23;
	private TextView textview16;
	private TextView textview17;
	private ImageView imageview10;
	private ImageView imageview11;
	private TextView textview18;
	private LinearLayout fora_container;
	private LinearLayout nezuka_container;
	private LinearLayout linear31;
	private LinearLayout linear33;
	private AexonImageView imageview12;
	private LinearLayout linear27;
	private LinearLayout btn1;
	private TextView title_fora2323;
	private TextView action_fora2323;
	private ImageView icon1;
	private TextView text1;
	private AexonImageView imageview13;
	private LinearLayout linear28;
	private LinearLayout btn2;
	private TextView title_nezuka;
	private TextView action_nezuka;
	private ImageView icon2;
	private TextView text2;
	private AexonImageView imageview16;
	private LinearLayout linear32;
	private LinearLayout btn3;
	private TextView title_chiko;
	private TextView action_chiko;
	private ImageView icon3;
	private TextView text3;
	private AexonImageView imageview17;
	private LinearLayout linear34;
	private LinearLayout btn4;
	private TextView title_nov;
	private TextView action_nov;
	private ImageView icon4;
	private TextView text4;
	
	private Intent ax_intent = new Intent();
	
	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		View _view = _inflater.inflate(R.layout.home_fragment, _container, false);
		initialize(_savedInstanceState, _view);
		initializeLogic();
		return _view;
	}
	
	private void initialize(Bundle _savedInstanceState, View _view) {
		root_view = _view.findViewById(R.id.root_view);
		vscroll1 = _view.findViewById(R.id.vscroll1);
		container = _view.findViewById(R.id.container);
		card_status = _view.findViewById(R.id.card_status);
		sub_container = _view.findViewById(R.id.sub_container);
		card_info = _view.findViewById(R.id.card_info);
		card_support = _view.findViewById(R.id.card_support);
		card_problem = _view.findViewById(R.id.card_problem);
		card_contributor = _view.findViewById(R.id.card_contributor);
		linear24 = _view.findViewById(R.id.linear24);
		linear25 = _view.findViewById(R.id.linear25);
		linear4 = _view.findViewById(R.id.linear4);
		textview2 = _view.findViewById(R.id.textview2);
		linear26 = _view.findViewById(R.id.linear26);
		textview1 = _view.findViewById(R.id.textview1);
		textview3 = _view.findViewById(R.id.textview3);
		tmp_timer = _view.findViewById(R.id.tmp_timer);
		imageview1 = _view.findViewById(R.id.imageview1);
		card_plugin = _view.findViewById(R.id.card_plugin);
		card_boost = _view.findViewById(R.id.card_boost);
		imageview2 = _view.findViewById(R.id.imageview2);
		linear7 = _view.findViewById(R.id.linear7);
		imageview3 = _view.findViewById(R.id.imageview3);
		textview4 = _view.findViewById(R.id.textview4);
		textview5 = _view.findViewById(R.id.textview5);
		imageview4 = _view.findViewById(R.id.imageview4);
		linear10 = _view.findViewById(R.id.linear10);
		imageview5 = _view.findViewById(R.id.imageview5);
		textview6 = _view.findViewById(R.id.textview6);
		textview7 = _view.findViewById(R.id.textview7);
		card1 = _view.findViewById(R.id.card1);
		card2 = _view.findViewById(R.id.card2);
		card3 = _view.findViewById(R.id.card3);
		imageview6 = _view.findViewById(R.id.imageview6);
		textview8 = _view.findViewById(R.id.textview8);
		textview9 = _view.findViewById(R.id.textview9);
		imageview7 = _view.findViewById(R.id.imageview7);
		textview10 = _view.findViewById(R.id.textview10);
		textview11 = _view.findViewById(R.id.textview11);
		imageview8 = _view.findViewById(R.id.imageview8);
		textview12 = _view.findViewById(R.id.textview12);
		textview13 = _view.findViewById(R.id.textview13);
		linear20 = _view.findViewById(R.id.linear20);
		linear21 = _view.findViewById(R.id.linear21);
		textview14 = _view.findViewById(R.id.textview14);
		textview15 = _view.findViewById(R.id.textview15);
		imageview9 = _view.findViewById(R.id.imageview9);
		linear22 = _view.findViewById(R.id.linear22);
		linear23 = _view.findViewById(R.id.linear23);
		textview16 = _view.findViewById(R.id.textview16);
		textview17 = _view.findViewById(R.id.textview17);
		imageview10 = _view.findViewById(R.id.imageview10);
		imageview11 = _view.findViewById(R.id.imageview11);
		textview18 = _view.findViewById(R.id.textview18);
		fora_container = _view.findViewById(R.id.fora_container);
		nezuka_container = _view.findViewById(R.id.nezuka_container);
		linear31 = _view.findViewById(R.id.linear31);
		linear33 = _view.findViewById(R.id.linear33);
		imageview12 = _view.findViewById(R.id.imageview12);
		linear27 = _view.findViewById(R.id.linear27);
		btn1 = _view.findViewById(R.id.btn1);
		title_fora2323 = _view.findViewById(R.id.title_fora2323);
		action_fora2323 = _view.findViewById(R.id.action_fora2323);
		icon1 = _view.findViewById(R.id.icon1);
		text1 = _view.findViewById(R.id.text1);
		imageview13 = _view.findViewById(R.id.imageview13);
		linear28 = _view.findViewById(R.id.linear28);
		btn2 = _view.findViewById(R.id.btn2);
		title_nezuka = _view.findViewById(R.id.title_nezuka);
		action_nezuka = _view.findViewById(R.id.action_nezuka);
		icon2 = _view.findViewById(R.id.icon2);
		text2 = _view.findViewById(R.id.text2);
		imageview16 = _view.findViewById(R.id.imageview16);
		linear32 = _view.findViewById(R.id.linear32);
		btn3 = _view.findViewById(R.id.btn3);
		title_chiko = _view.findViewById(R.id.title_chiko);
		action_chiko = _view.findViewById(R.id.action_chiko);
		icon3 = _view.findViewById(R.id.icon3);
		text3 = _view.findViewById(R.id.text3);
		imageview17 = _view.findViewById(R.id.imageview17);
		linear34 = _view.findViewById(R.id.linear34);
		btn4 = _view.findViewById(R.id.btn4);
		title_nov = _view.findViewById(R.id.title_nov);
		action_nov = _view.findViewById(R.id.action_nov);
		icon4 = _view.findViewById(R.id.icon4);
		text4 = _view.findViewById(R.id.text4);
		
		card_status.setOnClickListener(_v -> {
			if (getContext() != null) {
				ax_intent.setClass(getContext(), ActivasiActivity.class);
				startActivity(ax_intent);
				getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});
		
		card_support.setOnClickListener(_v -> {
			ax_intent.setAction(Intent.ACTION_VIEW);
			ax_intent.setData(Uri.parse(getString(R.string.tag_url_github_repo)));
			startActivity(ax_intent);
		});
		
		imageview10.setOnClickListener(_v -> {
			ax_intent.setAction(Intent.ACTION_VIEW);
			ax_intent.setData(Uri.parse(getString(R.string.url_wa)));
			startActivity(ax_intent);
		});
		
		imageview11.setOnClickListener(_v -> {
			ax_intent.setAction(Intent.ACTION_VIEW);
			ax_intent.setData(Uri.parse(getString(R.string.url_tele)));
			startActivity(ax_intent);
		});
		
		btn1.setOnClickListener(_v -> {
			ax_intent.setAction(Intent.ACTION_VIEW);
			ax_intent.setData(Uri.parse(getString(R.string.link_github_fora2323)));
			startActivity(ax_intent);
		});
		
		btn2.setOnClickListener(_v -> {
			ax_intent.setAction(Intent.ACTION_VIEW);
			ax_intent.setData(Uri.parse(getString(R.string.link_github_nezuka)));
			startActivity(ax_intent);
		});
		
		btn3.setOnClickListener(_v -> {
			ax_intent.setAction(Intent.ACTION_VIEW);
			ax_intent.setData(Uri.parse(getString(R.string.link_github_chikoo)));
			startActivity(ax_intent);
		});
		
		btn4.setOnClickListener(_v -> {
			ax_intent.setAction(Intent.ACTION_VIEW);
			ax_intent.setData(Uri.parse(getString(R.string.link_github_nov)));
			startActivity(ax_intent);
		});
	}
	
//metod timer tmp deamon
private final Runnable timerRunnable = new Runnable() {
    @Override
    public void run() {
        long now = System.currentTimeMillis() / 1000;
        long elapsed = now - daemonStartTime;
        if (elapsed < 0) elapsed = 0;
        tmp_timer.setText(String.format(Locale.getDefault(), "T+%02d:%02d:%02d", elapsed/3600, (elapsed%3600)/60, elapsed%60));
        timerHandler.postDelayed(this, 1000);
    }
};

private final Aexon.OnBinderReceivedListener binderReceivedListener = () -> {
	_applyTheme(AexonTheme.getInstance());
	tmp_timer.setVisibility(View.VISIBLE);
	new Thread(() -> {
		long start = AexonMain.getStartTime();
		Activity act = getActivity();
		if (act == null) return;
		act.runOnUiThread(() -> {
			daemonStartTime = start;
			timerHandler.removeCallbacks(timerRunnable);
			timerHandler.post(timerRunnable);
		});
	}).start();
	new Thread(() -> {
		String selinux = AexonMain.getSeLinux();
		Activity act = getActivity();
		if (act == null) return;
		act.runOnUiThread(() -> {
			if (textview13 != null) textview13.setText(selinux);
		});
	}).start();
};

private final Aexon.OnBinderDeadListener binderDeadListener = () -> {
	_applyTheme(AexonTheme.getInstance());
	timerHandler.removeCallbacks(timerRunnable);
	tmp_timer.setVisibility(View.GONE);
	new Thread(() -> {
		String selinux = AexonMain.getSeLinux();
		Activity act = getActivity();
		if (act == null) return;
		act.runOnUiThread(() -> {
			if (textview13 != null) textview13.setText(selinux);
		});
	}).start();
};

	private void initializeLogic() {
		textview9.setText(AexonMain.getVersion() + "(SDK " + AexonMain.getSdk() + ")");
		new Thread(() -> {
			String selinux = AexonMain.getSeLinux();
			getActivity().runOnUiThread(() -> {
				if (textview13 != null) textview13.setText(selinux);
			});
		}).start();
		_githubContributor();
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
		AexonTheme.getInstance().addListener(themeListener);
		Aexon.addBinderReceivedListener(binderReceivedListener);
		Aexon.addBinderDeadListener(binderDeadListener);
		
		if (Aexon.isBinder()) {
			tmp_timer.setVisibility(View.VISIBLE);
			new Thread(() -> {
				long start = AexonMain.getStartTime();
				Activity act = getActivity();
				if (act == null) return;
				act.runOnUiThread(() -> {
					daemonStartTime = start;
					timerHandler.removeCallbacks(timerRunnable);
					timerHandler.post(timerRunnable);
				});
			}).start();
		} else {
			tmp_timer.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		AexonTheme.getInstance().removeListener(themeListener);
		Aexon.removeBinderReceivedListener(binderReceivedListener);
		Aexon.removeBinderDeadListener(binderDeadListener);
		timerHandler.removeCallbacks(timerRunnable);
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
	public void _setStatus() {
		if (Aexon.isBinder()) {
			long versionCode = AexonBuild.getVersionCode(getContext());
			textview2.setText("Version: " + versionCode + " | Pid: " + AexonMain.getPid());
		} else {
			textview2.setText(getString(R.string.tag_aexon_not_run));
		}
	}
	
	
	public void _applyTheme(final AexonTheme _theme) {
		if (getContext() == null || !isAdded()) return;
		currentTheme = _theme;
		_initScrollShadow(_theme.getColorSurface());
		float radius = SketchwareUtil.getDimension(getContext(), R.dimen.card_radius_medium);
		float radius_small = SketchwareUtil.getDimension(getContext(), R.dimen.card_radius_small);
		float radius_medium = SketchwareUtil.getDimension(getContext(), R.dimen.card_radius_medium);
		float stroke = SketchwareUtil.getDimension(getContext(), R.dimen.card_stroke_small);
		
		// Card status & support
		card_status.setBackground(new AexonDrawable.Builder(_theme.getColorPrimary()).cornerRadius(radius).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		card_status.setClickable(true);
		card_support.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(radius).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		card_support.setClickable(true);
		
		// Card info & problem & contributor
		card_info.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(radius).build().build(getContext()));
		card_problem.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(radius).build().build(getContext()));
		card_contributor.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(radius).build().build(getContext()));
		
		// Card plugin & boost
		card_plugin.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(radius).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		card_plugin.setClickable(true);
		card_boost.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(radius).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		card_boost.setClickable(true);
		
		//card container
		card1.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainerHighest()).cornerRadius(radius_small).build().build(getContext()));
		card2.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainerHighest()).cornerRadius(radius_small).build().build(getContext()));
		card3.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainerHighest()).cornerRadius(radius_small).build().build(getContext()));
		
		//button follow btn1 btn2 btn3
		btn1.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(radius_medium).stroke(stroke, _theme.getColorOutlineVariant()).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		btn1.setClickable(true);
		btn2.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(radius_medium).stroke(stroke, _theme.getColorOutlineVariant()).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		btn2.setClickable(true);
		btn3.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(radius_medium).stroke(stroke, _theme.getColorOutlineVariant()).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		btn3.setClickable(true);
		btn4.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).cornerRadius(radius_medium).stroke(stroke, _theme.getColorOutlineVariant()).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		btn4.setClickable(true);
		
		textview3.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)SketchwareUtil.getDip(getContext().getApplicationContext(), (int)(4)), _theme.getColorPrimaryDark()));
		
		if (Aexon.isBinder()) {
			sub_container.setVisibility(View.VISIBLE);
			_setStatus();
			textview1.setText(getString(R.string.tag_running));
			textview3.setVisibility(View.VISIBLE);
			imageview1.setImageResource(R.drawable.ic_aexon);
		} else {
			_setStatus();
			sub_container.setVisibility(View.GONE);
			textview1.setText(getString(R.string.tag_not_running));
			textview3.setVisibility(View.GONE);
			imageview1.setImageResource(R.drawable.ic_cancel);
		}
		//design status container
		imageview1.setColorFilter(_theme.getColorOnPrimaryDark(), PorterDuff.Mode.SRC_ATOP);
		imageview1.setBackgroundColor(_theme.getColorPrimaryDark());
		
		imageview3.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		
		imageview2.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		imageview4.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		imageview5.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		imageview6.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		imageview7.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		imageview8.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		imageview9.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		imageview10.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		imageview10.setBackground(AexonDrawable.oval(getContext(), _theme.getColorSurfaceContainer()));
		imageview10.setClickable(true);
		imageview10.setFocusable(true);
		imageview11.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		imageview11.setBackground(AexonDrawable.oval(getContext(), _theme.getColorSurfaceContainer()));
		imageview11.setClickable(true);
		imageview11.setFocusable(true);
		
		textview1.setTextColor(_theme.getColorOnPrimary());
		textview2.setTextColor(_theme.getColorOnPrimary());
		
		textview3.setTextColor(_theme.getColorOnPrimaryDark());
		
		textview4.setTextColor(_theme.getColorOnSurface());
		textview5.setTextColor(_theme.getColorOnSurfaceVariant());
		textview6.setTextColor(_theme.getColorOnSurface());
		textview7.setTextColor(_theme.getColorOnSurfaceVariant());
		textview8.setTextColor(_theme.getColorOnSurface());
		textview9.setTextColor(_theme.getColorOnSurfaceVariant());
		textview10.setTextColor(_theme.getColorOnSurface());
		textview11.setTextColor(_theme.getColorOnSurfaceVariant());
		textview12.setTextColor(_theme.getColorOnSurface());
		textview13.setTextColor(_theme.getColorOnSurfaceVariant());
		textview14.setTextColor(_theme.getColorOnSurface());
		textview15.setTextColor(_theme.getColorOnSurfaceVariant());
		textview16.setTextColor(_theme.getColorOnSurface());
		textview17.setTextColor(_theme.getColorOnSurfaceVariant());
		textview18.setTextColor(_theme.getColorOnSurface());
		tmp_timer.setTextColor(_theme.getColorOnPrimary());
		
		title_fora2323.setTextColor(_theme.getColorOnSurface());
		action_fora2323.setTextColor(_theme.getColorOnSurfaceVariant());
		
		title_nezuka.setTextColor(_theme.getColorOnSurface());
		action_nezuka.setTextColor(_theme.getColorOnSurfaceVariant());
		
		title_chiko.setTextColor(_theme.getColorOnSurface());
		action_chiko.setTextColor(_theme.getColorOnSurfaceVariant());
		
		title_nov.setTextColor(_theme.getColorOnSurface());
		action_nov.setTextColor(_theme.getColorOnSurfaceVariant());
		
		text1.setTextColor(_theme.getColorOnSurface());
		text2.setTextColor(_theme.getColorOnSurface());
		text3.setTextColor(_theme.getColorOnSurface());
		text4.setTextColor(_theme.getColorOnSurface());
		
		icon1.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		icon2.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		icon3.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		icon4.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
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
	
	
	public void _githubContributor() {
		//fora2323
		title_fora2323.setText(getString(R.string.tag_username_fora2323) + "(" + getString(R.string.tag_name_fora2323) + ")");
		action_fora2323.setText(getString(R.string.tag_action_fora2323));
		//nezuka
		title_nezuka.setText(getString(R.string.tag_username_nezuka) + "(" + getString(R.string.tag_name_nezuka) + ")");
		action_nezuka.setText(getString(R.string.tag_action_nezuka));
		//chikoo
		title_chiko.setText(getString(R.string.tag_username_chikoo) + "(" + getString(R.string.tag_name_chikoo) + ")");
		action_chiko.setText(getString(R.string.tag_action_chikoo));
		//nov
		title_nov.setText(getString(R.string.tag_name_nov));
		action_nov.setText(getString(R.string.tag_action_nov));
	}
	
}