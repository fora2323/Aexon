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
import com.aexon.widget.AexonButton;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;
import com.aexon.theme.AexonTheme;
import com.aexon.aexon.AexonWindowHelper;
import com.aexon.starter.AexonShizukuHelper;
import com.aexon.material.toasty.AexonToast;
import com.aexon.starter.AexonStarter;
import android.text.method.LinkMovementMethod;
import com.aexon.material.dialog.AexonAlertDialog;

public class ActivasiActivity extends Activity {
	
	private final Aexon.OnBinderReceivedListener activasiBinderReceived = () -> {
		if (!isFinishing() && !isDestroyed()) {
			onBackPressed();
		}
	};
	private AexonShizukuHelper shizuku;
	
	private LinearLayout linear12;
	private LinearLayout toolbar;
	private ScrollView vscroll1;
	private ImageView imageview1;
	private TextView textview1;
	private LinearLayout linear1;
	private LinearLayout card1;
	private LinearLayout card4;
	private LinearLayout card2;
	private LinearLayout card3;
	private LinearLayout linear11;
	private TextView textview3;
	private AexonButton connect_shizuku;
	private AexonButton start_shizuku;
	private ImageView imageview6;
	private TextView textview2;
	private LinearLayout linear14;
	private TextView textview8;
	private TextView textview10;
	private AexonButton stepbystep;
	private AexonButton connect_pair;
	private AexonButton start_pair;
	private ImageView imageview7;
	private TextView textview9;
	private LinearLayout linear3;
	private TextView textview5;
	private AexonButton view_cmd;
	private ImageView imageview2;
	private TextView textview4;
	private LinearLayout linear8;
	private TextView textview7;
	private AexonButton start_root;
	private ImageView imageview4;
	private TextView textview6;
	
	private Intent aexon_intent = new Intent();
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.activasi);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		shizuku = new AexonShizukuHelper(this);
		linear12 = findViewById(R.id.linear12);
		toolbar = findViewById(R.id.toolbar);
		vscroll1 = findViewById(R.id.vscroll1);
		imageview1 = findViewById(R.id.imageview1);
		textview1 = findViewById(R.id.textview1);
		linear1 = findViewById(R.id.linear1);
		card1 = findViewById(R.id.card1);
		card4 = findViewById(R.id.card4);
		card2 = findViewById(R.id.card2);
		card3 = findViewById(R.id.card3);
		linear11 = findViewById(R.id.linear11);
		textview3 = findViewById(R.id.textview3);
		connect_shizuku = findViewById(R.id.connect_shizuku);
		start_shizuku = findViewById(R.id.start_shizuku);
		imageview6 = findViewById(R.id.imageview6);
		textview2 = findViewById(R.id.textview2);
		linear14 = findViewById(R.id.linear14);
		textview8 = findViewById(R.id.textview8);
		textview10 = findViewById(R.id.textview10);
		stepbystep = findViewById(R.id.stepbystep);
		connect_pair = findViewById(R.id.connect_pair);
		start_pair = findViewById(R.id.start_pair);
		imageview7 = findViewById(R.id.imageview7);
		textview9 = findViewById(R.id.textview9);
		linear3 = findViewById(R.id.linear3);
		textview5 = findViewById(R.id.textview5);
		view_cmd = findViewById(R.id.view_cmd);
		imageview2 = findViewById(R.id.imageview2);
		textview4 = findViewById(R.id.textview4);
		linear8 = findViewById(R.id.linear8);
		textview7 = findViewById(R.id.textview7);
		start_root = findViewById(R.id.start_root);
		imageview4 = findViewById(R.id.imageview4);
		textview6 = findViewById(R.id.textview6);
		
		imageview1.setOnClickListener(_v -> onBackPressed());
		
		connect_shizuku.setOnClickListener(_v -> {
			if (!shizuku.isShizukuInstalled()) {
				AexonAlertDialog dialog = new AexonAlertDialog(ActivasiActivity.this);
				dialog.setTitle(R.string.tag_shizuku_title);
				dialog.setMessage(R.string.tag_shizuku_dec);
				dialog.setPositiveButton(R.string.tag_btn_install, (d, w) -> {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.tag_url_shizuku)));
					startActivity(intent);
				});
				dialog.setNegativeButton(R.string.tag_btn_cancel, null);
				dialog.show();
				return;
			}
			
			if (!shizuku.isShizukuRunning()) {
				AexonToast.make(ActivasiActivity.this).title(getString(R.string.tag_not_running)).message(getString(R.string.tag_shizuku_dec_toasty)).show();
				return;
			}
			
			if (!shizuku.hasPermission()) {
				shizuku.requestPermission(100);
				return;
			}
			
			AexonToast.make(ActivasiActivity.this).title(getString(R.string.tag_shizuku_running)).message(getString(R.string.tag_shizuku_running_dec)).show();
		});
		
		start_shizuku.setOnClickListener(_v -> {
			if (!shizuku.isShizukuInstalled()) {
				AexonAlertDialog dialog = new AexonAlertDialog(ActivasiActivity.this);
				dialog.setTitle(R.string.tag_shizuku_title);
				dialog.setMessage(R.string.tag_shizuku_dec);
				dialog.setPositiveButton(R.string.tag_btn_install, (d, w) -> {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.tag_url_shizuku)));
					startActivity(intent);
				});
				dialog.setNegativeButton(R.string.tag_btn_cancel, null);
				dialog.show();
				return;
			}
			
			if (!shizuku.hasPermission()) {
				shizuku.requestPermission(100);
				return;
			}
			
			if (!shizuku.isShizukuRunning()) {
				AexonAlertDialog dialog = new AexonAlertDialog(ActivasiActivity.this);
				dialog.setTitle(R.string.tag_rish_not_running);
				dialog.setMessage(R.string.tag_rish_not_running_dec);
				dialog.setNegativeButton(R.string.tag_btn_cancel, null);
				dialog.show();
				return;
			}
			AexonStarter.newInstance(AexonStarter.MODE_SHIZUKU, shizuku).show(getFragmentManager(), "loading");
		});
		
		view_cmd.setOnClickListener(_v -> {
			InfoDialogPathDialogFragmentActivity bottomSheet = new InfoDialogPathDialogFragmentActivity();
			bottomSheet.show(getFragmentManager(), "InfoDialog");
		});
		
		start_root.setOnClickListener(_v -> {
			AexonShizukuHelper.isRooted(rooted -> {
				if (!rooted) {
					AexonToast.make(ActivasiActivity.this).title(getString(R.string.tag_root_not_support)).message(getString(R.string.tag_root_not_support_dec)).show();
					return;
				}
				AexonStarter.newInstance(AexonStarter.MODE_ROOT, null).show(getFragmentManager(), "loading");
			});
		});
	}
	
	private void initializeLogic() {
		textview7.setText(Html.fromHtml(getString(R.string.tag_dec_rooted), Html.FROM_HTML_MODE_LEGACY));
		textview7.setLinkTextColor(0xFF00BCD4);
		textview7.setMovementMethod(LinkMovementMethod.getInstance());
		AexonTheme theme = AexonTheme.getInstance();
		if (Aexon.isBinder()) {
			onBackPressed();
			return;
		}
		imageview1.setColorFilter(theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		imageview1.setBackground(AexonDrawable.oval(this, theme.getColorSurfaceContainer()));
		imageview1.setClickable(true);
		imageview1.setFocusable(true);
		imageview2.setColorFilter(theme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP);
		imageview4.setColorFilter(theme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP);
		imageview6.setColorFilter(theme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP);
		imageview7.setColorFilter(theme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP);
		toolbar.setBackgroundColor(theme.getColorSurface());
		vscroll1.setBackgroundColor(theme.getColorSurface());
		//card card1 & card2 & card3
		card1.setBackground(new AexonDrawable.Builder(theme.getColorSurfaceContainer()).cornerRadius(SketchwareUtil.getDimension(this, R.dimen.card_radius_medium)).build().build(ActivasiActivity.this));
		card2.setBackground(new AexonDrawable.Builder(theme.getColorSurfaceContainer()).cornerRadius(SketchwareUtil.getDimension(this, R.dimen.card_radius_medium)).build().build(ActivasiActivity.this));
		card3.setBackground(new AexonDrawable.Builder(theme.getColorSurfaceContainer()).cornerRadius(SketchwareUtil.getDimension(this, R.dimen.card_radius_medium)).build().build(ActivasiActivity.this));
		card4.setBackground(new AexonDrawable.Builder(theme.getColorSurfaceContainer()).cornerRadius(SketchwareUtil.getDimension(this, R.dimen.card_radius_medium)).build().build(ActivasiActivity.this));
		
		
		//warna icon button
		connect_shizuku.setIconTint(theme.getColorOnPrimary());
		start_shizuku.setIconTint(theme.getColorOnPrimary());
		view_cmd.setIconTint(theme.getColorOnPrimary());
		start_root.setIconTint(theme.getColorOnPrimary());
		stepbystep.setIconTint(theme.getColorOnPrimary());
		connect_pair.setIconTint(theme.getColorOnPrimary());
		start_pair.setIconTint(theme.getColorOnPrimary());
		connect_shizuku.setBackgroundColor(theme.getColorPrimary());
		start_shizuku.setBackgroundColor(theme.getColorPrimary());
		view_cmd.setBackgroundColor(theme.getColorPrimary());
		start_root.setBackgroundColor(theme.getColorPrimary());
		stepbystep.setBackgroundColor(theme.getColorPrimary());
		connect_pair.setBackgroundColor(theme.getColorPrimary());
		start_pair.setBackgroundColor(theme.getColorPrimary());
		AexonWindowHelper.setWindowStyle(getWindow(), theme.getColorSurface());
		start_root.setTextColor(theme.getColorOnPrimary());
		view_cmd.setTextColor(theme.getColorOnPrimary());
		connect_shizuku.setTextColor(theme.getColorOnPrimary());
		start_shizuku.setTextColor(theme.getColorOnPrimary());
		stepbystep.setTextColor(theme.getColorOnPrimary());
		connect_pair.setTextColor(theme.getColorOnPrimary());
		start_pair.setTextColor(theme.getColorOnPrimary());
		textview1.setTextColor(theme.getColorOnSurface());
		textview2.setTextColor(theme.getColorOnSurface());
		textview3.setTextColor(theme.getColorOnSurfaceVariant());
		textview4.setTextColor(theme.getColorOnSurface());
		textview5.setTextColor(theme.getColorOnSurfaceVariant());
		textview6.setTextColor(theme.getColorOnSurface());
		textview7.setTextColor(theme.getColorOnSurfaceVariant());
		textview8.setTextColor(theme.getColorOnSurfaceVariant());
		textview9.setTextColor(theme.getColorOnSurface());
		textview10.setTextColor(theme.getColorOnSurfaceVariant());
	}
	
	
	@Override
	public void onBackPressed() {
		super.finish();
		overridePendingTransition(R.anim.fade_in_back, R.anim.fade_out_back);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Aexon.removeBinderReceivedListener(activasiBinderReceived);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Aexon.addBinderReceivedListener(activasiBinderReceived);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		shizuku.destroy();
	}
}