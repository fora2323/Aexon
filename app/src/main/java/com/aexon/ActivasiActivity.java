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
import com.aexon.material.button.AexonButton;
import com.aexon.material.cardview.AexonCardView;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;
import android.text.method.LinkMovementMethod;
import android.content.pm.PackageManager;
import android.Manifest;
import android.provider.Settings;
import com.aexon.material.dialog.AexonAlertDialog;
import com.aexon.aexon.AexonClipboardHelper;
import com.aexon.theme.AexonTheme;
import com.aexon.aexon.AexonWindowHelper;
import com.aexon.starter.AexonShizukuHelper;
import com.aexon.material.toasty.AexonToast;
import com.aexon.starter.AexonStarter;

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
	private AexonCardView cardview1;
	private AexonCardView cardview2;
	private AexonCardView cardview3;
	private LinearLayout linear10;
	private LinearLayout linear11;
	private TextView textview3;
	private AexonButton pair;
	private AexonButton start;
	private ImageView imageview6;
	private TextView textview2;
	private LinearLayout linear5;
	private LinearLayout linear3;
	private TextView textview5;
	private AexonButton view_cmd;
	private ImageView imageview2;
	private TextView textview4;
	private LinearLayout linear7;
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
		cardview1 = findViewById(R.id.cardview1);
		cardview2 = findViewById(R.id.cardview2);
		cardview3 = findViewById(R.id.cardview3);
		linear10 = findViewById(R.id.linear10);
		linear11 = findViewById(R.id.linear11);
		textview3 = findViewById(R.id.textview3);
		pair = findViewById(R.id.pair);
		start = findViewById(R.id.start);
		imageview6 = findViewById(R.id.imageview6);
		textview2 = findViewById(R.id.textview2);
		linear5 = findViewById(R.id.linear5);
		linear3 = findViewById(R.id.linear3);
		textview5 = findViewById(R.id.textview5);
		view_cmd = findViewById(R.id.view_cmd);
		imageview2 = findViewById(R.id.imageview2);
		textview4 = findViewById(R.id.textview4);
		linear7 = findViewById(R.id.linear7);
		linear8 = findViewById(R.id.linear8);
		textview7 = findViewById(R.id.textview7);
		start_root = findViewById(R.id.start_root);
		imageview4 = findViewById(R.id.imageview4);
		textview6 = findViewById(R.id.textview6);
		
		imageview1.setOnClickListener(_v -> onBackPressed());
		
		pair.setOnClickListener(_v -> {
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
		
		start.setOnClickListener(_v -> {
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
			String command = "adb shell " + Aexon.getPath(ActivasiActivity.this);
			String note = "\n\n" + getString(R.string.tag_msg_cmd);
			
			SpannableStringBuilder message = new SpannableStringBuilder();
			SpannableString commandSpan = new SpannableString(command);
			commandSpan.setSpan(new TypefaceSpan("monospace"), 0, command.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			commandSpan.setSpan(new BackgroundColorSpan(0x662D2D2D), 0, command.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			message.append(commandSpan);
			message.append(note);
			
			AexonAlertDialog dialog = new AexonAlertDialog(ActivasiActivity.this);
			dialog.setTitle(getString(R.string.tag_title_view_cmd));
			dialog.setMessage(message);
			dialog.setPositiveButton(getString(R.string.tag_btn_copy), (d, which) -> {
				AexonClipboardHelper.copy(ActivasiActivity.this, command);
				d.dismiss();
			});
			dialog.setNeutralButton(getString(R.string.tag_btn_send), (d, which) -> {
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_TEXT, command);
				startActivity(Intent.createChooser(shareIntent, null));
				d.dismiss();
			});
			dialog.setNegativeButton(getString(R.string.tag_btn_cancel), (d, which) -> {
				d.dismiss();
			});
			dialog.show();
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
		AexonTheme theme = AexonTheme.getInstance();
		textview7.setText(Html.fromHtml(getString(R.string.tag_dec_rooted), Html.FROM_HTML_MODE_LEGACY));
		//
		textview7.setLinkTextColor(theme.getColorSurfaceVariant());
		textview7.setMovementMethod(LinkMovementMethod.getInstance());
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
		linear10.setBackgroundColor(theme.getColorSurfaceContainer());
		linear5.setBackgroundColor(theme.getColorSurfaceContainer());
		linear7.setBackgroundColor(theme.getColorSurfaceContainer());
		pair.setBackgroundColor(theme.getColorPrimary());
		start.setBackgroundColor(theme.getColorPrimary());
		view_cmd.setBackgroundColor(theme.getColorPrimary());
		start_root.setBackgroundColor(theme.getColorPrimary());
		AexonWindowHelper.setWindowStyle(getWindow(), theme.getColorSurface());
		pair.setIconTint(theme.getColorOnPrimary());
		start.setIconTint(theme.getColorOnPrimary());
		view_cmd.setIconTint(theme.getColorOnPrimary());
		start_root.setIconTint(theme.getColorOnPrimary());
		start_root.setTextColor(theme.getColorOnPrimary());
		view_cmd.setTextColor(theme.getColorOnPrimary());
		pair.setTextColor(theme.getColorOnPrimary());
		start.setTextColor(theme.getColorOnPrimary());
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