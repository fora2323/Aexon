package com.aexon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aexon.annotation.NonNull;
import com.aexon.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DebugActivity extends Activity {
	
	private static final Map<String, String> exceptionMap = new HashMap<String, String>() {{
			put("StringIndexOutOfBoundsException", "Invalid string operation\n");
			put("IndexOutOfBoundsException", "Invalid list operation\n");
			put("ArithmeticException", "Invalid arithmetical operation\n");
			put("NumberFormatException", "Invalid toNumber block operation\n");
			put("ActivityNotFoundException", "Invalid intent operation\n");
		}};
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setupStatusBar();
		setupNavigationBar();
		
		SpannableStringBuilder formattedMessage = new SpannableStringBuilder();
		Intent intent = getIntent();
		String errorMessage = "";
		
		if (intent != null) {
			errorMessage = intent.getStringExtra("error");
		}
		
		if (!errorMessage.isEmpty()) {
			String[] split = errorMessage.split("\n");
			String exceptionType = split[0];
			String message = exceptionMap.containsKey(exceptionType) ? exceptionMap.get(exceptionType) : "";
			
			if (!message.isEmpty()) {
				formattedMessage.append(message);
			}
			
			int lineNumber = 1;
			for (int i = 1; i < split.length; i++) {
				if (!split[i].trim().isEmpty()) {
					formattedMessage.append(String.valueOf(lineNumber)).append(": Error > ");
					formattedMessage.append(split[i]);
					formattedMessage.append("\n");
					lineNumber++;
				}
			}
		} else {
			formattedMessage.append("No error message available.");
		}
		
		LinearLayout mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setBackgroundColor(getResources().getColor(R.color.colorSurface));
		
		LinearLayout toolbar = new LinearLayout(this);
		toolbar.setOrientation(LinearLayout.VERTICAL);
		toolbar.setBackgroundColor(getResources().getColor(R.color.colorSurfaceContainer));
		int toolbarPadding = (int) (14 * getResources().getDisplayMetrics().density);
		toolbar.setPadding(toolbarPadding, toolbarPadding, toolbarPadding, toolbarPadding);
		
		TextView titleText = new TextView(this);
		titleText.setText("Aexon Crashed");
		titleText.setTextSize(24);
		titleText.setTextColor(getResources().getColor(R.color.colorPrimary));
		applyCustomFontToolbar(titleText);
		toolbar.addView(titleText);
		
		TextView errorView = new TextView(this);
		errorView.setTextColor(getResources().getColor(R.color.colorOnSurfaceVariant));
		errorView.setTextIsSelectable(true);
		errorView.setLineSpacing(0, 1.2f);
		applyCustomFont(errorView);
		applyNumberColoring(errorView, formattedMessage);
		
		HorizontalScrollView hscroll = new HorizontalScrollView(this);
		ScrollView vscroll = new ScrollView(this);
		int logPadding = (int) (4 * getResources().getDisplayMetrics().density);
		vscroll.setPadding(logPadding, logPadding, logPadding, logPadding);
		
		hscroll.addView(vscroll);
		vscroll.addView(errorView);
		
		mainLayout.addView(toolbar);
		mainLayout.addView(hscroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		
		setContentView(mainLayout);
	}
	
	private void setupStatusBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(R.color.colorSurfaceContainer));
			View decorView = window.getDecorView();
			decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		}
	}
	
	private void setupNavigationBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.setNavigationBarColor(getResources().getColor(R.color.colorSurface));
			View decorView = window.getDecorView();
			decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
		}
	}
	
	private void applyCustomFontToolbar(@NonNull TextView textView) {
		try {
			Typeface customTypeface = getResources().getFont(R.font.f4);
			textView.setTypeface(customTypeface, Typeface.BOLD);
		} catch (Exception e) {
			// Font not found
		}
	}
	
	private void applyCustomFont(@NonNull TextView textView) {
		try {
			Typeface customTypeface = getResources().getFont(R.font.f4);
			textView.setTypeface(customTypeface, Typeface.BOLD);
		} catch (Exception e) {
			// Font not found
		}
	}
	
	private void applyNumberColoring(@NonNull TextView textView, @NonNull SpannableStringBuilder text) {
		String textString = text.toString();
		SpannableStringBuilder spannable = new SpannableStringBuilder(textString);
		
		int primaryColor = getResources().getColor(R.color.colorPrimary);
		
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(textString);
		
		while (matcher.find()) {
			spannable.setSpan(new ForegroundColorSpan(primaryColor), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		textView.setText(spannable);
	}
}