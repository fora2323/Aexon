package com.aexon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aexon.aexon.AexonWindowHelper;
import com.aexon.material.dialog.AexonAlertDialog;
import com.aexon.material.toasty.AexonToast;
import com.aexon.starter.AexonShizukuHelper;
import com.aexon.starter.AexonStarter;
import com.aexon.theme.AexonTheme;
import com.aexon.widget.AexonButton;

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
	private LinearLayout card2;
	private LinearLayout card3;
	private LinearLayout linear11;
	private TextView textview3;
	private AexonButton connect_shizuku;
	private AexonButton start_shizuku;
	private ImageView imageview6;
	private TextView textview2;
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
	protected void onCreate(@Nullable Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.activasi);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(@Nullable Bundle _savedInstanceState) {
		shizuku = new AexonShizukuHelper(this);
		linear12 = findViewById(R.id.linear12);
		toolbar = findViewById(R.id.toolbar);
		vscroll1 = findViewById(R.id.vscroll1);
		imageview1 = findViewById(R.id.imageview1);
		textview1 = findViewById(R.id.textview1);
		linear1 = findViewById(R.id.linear1);
		card1 = findViewById(R.id.card1);
		card2 = findViewById(R.id.card2);
		card3 = findViewById(R.id.card3);
		linear11 = findViewById(R.id.linear11);
		textview3 = findViewById(R.id.textview3);
		connect_shizuku = findViewById(R.id.connect_shizuku);
		start_shizuku = findViewById(R.id.start_shizuku);
		imageview6 = findViewById(R.id.imageview6);
		textview2 = findViewById(R.id.textview2);
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
		toolbar.setBackgroundColor(theme.getColorSurface());
		vscroll1.setBackgroundColor(theme.getColorSurface());
		
		// card card1 & card2 & card3
		card1.setBackground(new AexonDrawable.Builder(theme.getColorSurfaceContainer()).cornerRadius(SketchwareUtil.getDimension(this, R.dimen.card_radius_medium)).build().build(ActivasiActivity.this));
		card2.setBackground(new AexonDrawable.Builder(theme.getColorSurfaceContainer()).cornerRadius(SketchwareUtil.getDimension(this, R.dimen.card_radius_medium)).build().build(ActivasiActivity.this));
		card3.setBackground(new AexonDrawable.Builder(theme.getColorSurfaceContainer()).cornerRadius(SketchwareUtil.getDimension(this, R.dimen.card_radius_medium)).build().build(ActivasiActivity.this));
		
		// warna icon button 
		connect_shizuku.setIconTint(theme.getColorOnPrimary());
		start_shizuku.setIconTint(theme.getColorOnPrimary());
		view_cmd.setIconTint(theme.getColorOnPrimary());
		start_root.setIconTint(theme.getColorOnPrimary());
		
		connect_shizuku.setBackgroundColor(theme.getColorPrimary());
		start_shizuku.setBackgroundColor(theme.getColorPrimary());
		view_cmd.setBackgroundColor(theme.getColorPrimary());
		start_root.setBackgroundColor(theme.getColorPrimary());
		AexonWindowHelper.setWindowStyle(getWindow(), theme.getColorSurface());
		start_root.setTextColor(theme.getColorOnPrimary());
		view_cmd.setTextColor(theme.getColorOnPrimary());
		connect_shizuku.setTextColor(theme.getColorOnPrimary());
		start_shizuku.setTextColor(theme.getColorOnPrimary());
		
		textview1.setTextColor(theme.getColorOnSurface());
		textview2.setTextColor(theme.getColorOnSurface());
		textview3.setTextColor(theme.getColorOnSurfaceVariant());
		textview4.setTextColor(theme.getColorOnSurface());
		textview5.setTextColor(theme.getColorOnSurfaceVariant());
		textview6.setTextColor(theme.getColorOnSurface());
		textview7.setTextColor(theme.getColorOnSurfaceVariant());
	}
	
	@Override
	public void onBackPressed() {
		super.finish();
		overridePendingTransition(R.anim.fade_in_back, R.anim.fade_out_back);
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