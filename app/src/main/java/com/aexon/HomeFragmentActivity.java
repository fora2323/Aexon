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
import com.aexon.material.cardview.AexonCardView;
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
	private AexonCardView cardview1;
	private LinearLayout sub_container;
	private AexonCardView cardview4;
	private AexonCardView cardview8;
	private AexonCardView cardview9;
	private LinearLayout linear3;
	private LinearLayout linear24;
	private LinearLayout linear25;
	private LinearLayout linear4;
	private TextView textview2;
	private LinearLayout linear26;
	private TextView textview1;
	private TextView textview3;
	private TextView tmp_timer;
	private ImageView imageview1;
	private AexonCardView cardview2;
	private AexonCardView cardview3;
	private LinearLayout linear6;
	private ImageView imageview2;
	private LinearLayout linear7;
	private ImageView imageview3;
	private TextView textview4;
	private TextView textview5;
	private LinearLayout linear9;
	private ImageView imageview4;
	private LinearLayout linear10;
	private ImageView imageview5;
	private TextView textview6;
	private TextView textview7;
	private LinearLayout linear12;
	private AexonCardView cardview5;
	private AexonCardView cardview6;
	private AexonCardView cardview7;
	private LinearLayout linear15;
	private ImageView imageview6;
	private TextView textview8;
	private TextView textview9;
	private LinearLayout linear16;
	private ImageView imageview7;
	private TextView textview10;
	private TextView textview11;
	private LinearLayout linear17;
	private ImageView imageview8;
	private TextView textview12;
	private TextView textview13;
	private LinearLayout linear18;
	private LinearLayout linear20;
	private LinearLayout linear21;
	private TextView textview14;
	private TextView textview15;
	private ImageView imageview9;
	private LinearLayout linear19;
	private LinearLayout linear22;
	private LinearLayout linear23;
	private TextView textview16;
	private TextView textview17;
	private ImageView imageview10;
	private ImageView imageview11;
	
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
		cardview1 = _view.findViewById(R.id.cardview1);
		sub_container = _view.findViewById(R.id.sub_container);
		cardview4 = _view.findViewById(R.id.cardview4);
		cardview8 = _view.findViewById(R.id.cardview8);
		cardview9 = _view.findViewById(R.id.cardview9);
		linear3 = _view.findViewById(R.id.linear3);
		linear24 = _view.findViewById(R.id.linear24);
		linear25 = _view.findViewById(R.id.linear25);
		linear4 = _view.findViewById(R.id.linear4);
		textview2 = _view.findViewById(R.id.textview2);
		linear26 = _view.findViewById(R.id.linear26);
		textview1 = _view.findViewById(R.id.textview1);
		textview3 = _view.findViewById(R.id.textview3);
		tmp_timer = _view.findViewById(R.id.tmp_timer);
		imageview1 = _view.findViewById(R.id.imageview1);
		cardview2 = _view.findViewById(R.id.cardview2);
		cardview3 = _view.findViewById(R.id.cardview3);
		linear6 = _view.findViewById(R.id.linear6);
		imageview2 = _view.findViewById(R.id.imageview2);
		linear7 = _view.findViewById(R.id.linear7);
		imageview3 = _view.findViewById(R.id.imageview3);
		textview4 = _view.findViewById(R.id.textview4);
		textview5 = _view.findViewById(R.id.textview5);
		linear9 = _view.findViewById(R.id.linear9);
		imageview4 = _view.findViewById(R.id.imageview4);
		linear10 = _view.findViewById(R.id.linear10);
		imageview5 = _view.findViewById(R.id.imageview5);
		textview6 = _view.findViewById(R.id.textview6);
		textview7 = _view.findViewById(R.id.textview7);
		linear12 = _view.findViewById(R.id.linear12);
		cardview5 = _view.findViewById(R.id.cardview5);
		cardview6 = _view.findViewById(R.id.cardview6);
		cardview7 = _view.findViewById(R.id.cardview7);
		linear15 = _view.findViewById(R.id.linear15);
		imageview6 = _view.findViewById(R.id.imageview6);
		textview8 = _view.findViewById(R.id.textview8);
		textview9 = _view.findViewById(R.id.textview9);
		linear16 = _view.findViewById(R.id.linear16);
		imageview7 = _view.findViewById(R.id.imageview7);
		textview10 = _view.findViewById(R.id.textview10);
		textview11 = _view.findViewById(R.id.textview11);
		linear17 = _view.findViewById(R.id.linear17);
		imageview8 = _view.findViewById(R.id.imageview8);
		textview12 = _view.findViewById(R.id.textview12);
		textview13 = _view.findViewById(R.id.textview13);
		linear18 = _view.findViewById(R.id.linear18);
		linear20 = _view.findViewById(R.id.linear20);
		linear21 = _view.findViewById(R.id.linear21);
		textview14 = _view.findViewById(R.id.textview14);
		textview15 = _view.findViewById(R.id.textview15);
		imageview9 = _view.findViewById(R.id.imageview9);
		linear19 = _view.findViewById(R.id.linear19);
		linear22 = _view.findViewById(R.id.linear22);
		linear23 = _view.findViewById(R.id.linear23);
		textview16 = _view.findViewById(R.id.textview16);
		textview17 = _view.findViewById(R.id.textview17);
		imageview10 = _view.findViewById(R.id.imageview10);
		imageview11 = _view.findViewById(R.id.imageview11);
		
		cardview1.setOnClickListener(_v -> {
			if (!Aexon.isBinder()) {
				if (getContext() != null) {
					ax_intent.setClass(getContext(), ActivasiActivity.class);
					startActivity(ax_intent);
					getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			}
		});
		
		linear18.setOnClickListener(_v -> {
			ax_intent.setAction(Intent.ACTION_VIEW);
			ax_intent.setData(Uri.parse(getString(R.string.tag_url_github)));
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
		imageview1.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		imageview1.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)SketchwareUtil.getDip(getContext().getApplicationContext(), (int)(50)), _theme.getColorPrimaryDark()));
		imageview2.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		imageview3.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
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
		
		
		linear6.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		linear6.setClickable(true);
		linear6.setFocusable(true);
		
		linear9.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		linear9.setClickable(true);
		linear9.setFocusable(true);
		
		linear18.setBackground(new AexonDrawable.Builder(_theme.getColorSurfaceContainer()).ripple(_theme.getColorOnSurface()).build().build(getContext()));
		linear18.setClickable(true);
		linear18.setFocusable(true);
		cardview1.setBackgroundColor(_theme.getColorPrimary());
		cardview2.setBackgroundColor(_theme.getColorSurfaceContainer());
		cardview3.setBackgroundColor(_theme.getColorSurfaceContainer());
		cardview4.setBackgroundColor(_theme.getColorSurfaceContainer());
		linear19.setBackgroundColor(_theme.getColorSurfaceContainer());
		textview1.setTextColor(_theme.getColorOnPrimary());
		textview2.setTextColor(_theme.getColorOnPrimary());
		textview3.setTextColor(_theme.getColorOnSurfaceVariant());
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
		cardview5.setBackgroundColor(_theme.getColorSurfaceContainerHighest());
		cardview6.setBackgroundColor(_theme.getColorSurfaceContainerHighest());
		cardview7.setBackgroundColor(_theme.getColorSurfaceContainerHighest());
		tmp_timer.setTextColor(_theme.getColorOnPrimary());
		
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