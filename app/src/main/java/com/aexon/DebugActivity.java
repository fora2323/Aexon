package com.aexon;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aexon.aexon.AexonWindowHelper;
import com.aexon.theme.AexonTheme;
import com.aexon.widget.AexonCompatButton;

public class DebugActivity extends Activity {
	
	private LinearLayout container;
	private LinearLayout toolbar;
	private TextView textview3;
	private LinearLayout linear2;
	private LinearLayout btn_container;
	private ImageView imageview1;
	private LinearLayout linear1;
	private TextView textview1;
	private TextView textview2;
	private LinearLayout log_container;
	private ScrollView vscroll1;
	private TextView textview4;
	private AexonCompatButton button1;
	private AexonCompatButton button2;
	
	@NonNull
	private String crashLog = "";
	
	@Override
	protected void onCreate(@Nullable Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.debug);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	@MainThread
	private void initialize(@Nullable Bundle _savedInstanceState) {
		container = findViewById(R.id.container);
		toolbar = findViewById(R.id.toolbar);
		textview3 = findViewById(R.id.textview3);
		linear2 = findViewById(R.id.linear2);
		btn_container = findViewById(R.id.btn_container);
		imageview1 = findViewById(R.id.imageview1);
		linear1 = findViewById(R.id.linear1);
		textview1 = findViewById(R.id.textview1);
		textview2 = findViewById(R.id.textview2);
		log_container = findViewById(R.id.log_container);
		vscroll1 = findViewById(R.id.vscroll1);
		textview4 = findViewById(R.id.textview4);
		button1 = findViewById(R.id.button1);
		button2 = findViewById(R.id.button2);
		
		vscroll1.setVerticalScrollBarEnabled(false);
		vscroll1.setHorizontalScrollBarEnabled(false);
		
		String error = getIntent().getStringExtra("error");
		crashLog = error != null ? error : "Tidak ada log error yang tersedia.";
		
		textview4.setText(crashLog);
		textview4.setTypeface(Typeface.MONOSPACE);
	}
	
	@MainThread
	private void initializeLogic() {
		AexonTheme theme = AexonTheme.getInstance();
		
		button1.setButtonBackgroundColor(theme.getColorSurfaceContainerHigh());
		button1.setIconTint(theme.getColorOnSurface());
		button1.setTextColor(theme.getColorOnSurface());
		button1.setButtonRippleColor(theme.getColorOnSurface());
		button1.setButtonStroke((int)SketchwareUtil.getDimension(DebugActivity.this, R.dimen.stroke_size_small), theme.getColorOutlineVariant());
		button1.setOnClickListener(v -> copyLogToClipboard());
		
		button2.setButtonBackgroundColor(theme.getColorPrimary());
		button2.setIconTint(theme.getColorOnPrimary());
		button2.setTextColor(theme.getColorOnPrimary());
		button2.setButtonRippleColor(theme.getColorOnPrimaryDark());
		button2.setOnClickListener(v -> restartApp());
		
		textview1.setTextColor(theme.getColorError());
		textview2.setTextColor(theme.getColorOnSurfaceVariant());
		textview3.setTextColor(theme.getColorOnSurface());
		textview4.setTextColor(theme.getColorOnSurfaceVariant());
		
		imageview1.setColorFilter(theme.getColorError(), PorterDuff.Mode.SRC_ATOP);
		
		AexonWindowHelper.setWindowStyle(getWindow(), theme.getColorSurface(), theme.getColorSurfaceContainerHigh());
		container.setBackgroundColor(theme.getColorSurface());
		btn_container.setBackgroundColor(theme.getColorSurfaceContainerHigh());
		log_container.setBackground(new AexonDrawable.Builder(theme.getColorSurfaceContainerHigh()).cornerRadius(SketchwareUtil.getDimension(DebugActivity.this, R.dimen.card_radius_medium)).build().build(DebugActivity.this));
	}
	
	@MainThread
	private void copyLogToClipboard() {
		StringBuilder sb = new StringBuilder();
		sb.append("Device: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append("\n");
		sb.append("Android Version: ").append(Build.VERSION.RELEASE).append("\n");
		sb.append("SDK: ").append(Build.VERSION.SDK_INT).append("\n");
		sb.append("--------------------\n");
		sb.append(crashLog);
		
		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("Crash Log", sb.toString());
		clipboard.setPrimaryClip(clip);
		
		Toast.makeText(this, "Log Copied Successfully", Toast.LENGTH_SHORT).show();
	}
	
	@MainThread
	private void restartApp() {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		Runtime.getRuntime().exit(0);
	}
}