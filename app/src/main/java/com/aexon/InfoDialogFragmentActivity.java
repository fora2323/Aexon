package com.aexon;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import com.aexon.material.bottomsheet.AexonBottomSheetFragment;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aexon.material.button.AexonButton;
import com.aexon.material.imageview.AexonImageView;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;
import com.aexon.theme.AexonTheme;
import com.aexon.theme.AexonThemeListener;

public class InfoDialogFragmentActivity extends AexonBottomSheetFragment {
	
	private LinearLayout container;
	private LinearLayout handle;
	private AexonImageView imageview1;
	private TextView textview1;
	private TextView textview2;
	private TextView textview3;
	private LinearLayout linear1;
	private AexonButton button3;
	private AexonButton button1;
	private AexonButton button2;
	
	private Intent i_aexon = new Intent();
	
	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		View _view = _inflater.inflate(R.layout.info_dialog_fragment, _container, false);
		initialize(_savedInstanceState, _view);
		initializeLogic();
		return _view;
	}
	
	private void initialize(Bundle _savedInstanceState, View _view) {
		container = _view.findViewById(R.id.container);
		handle = _view.findViewById(R.id.handle);
		imageview1 = _view.findViewById(R.id.imageview1);
		textview1 = _view.findViewById(R.id.textview1);
		textview2 = _view.findViewById(R.id.textview2);
		textview3 = _view.findViewById(R.id.textview3);
		linear1 = _view.findViewById(R.id.linear1);
		button3 = _view.findViewById(R.id.button3);
		button1 = _view.findViewById(R.id.button1);
		button2 = _view.findViewById(R.id.button2);
		
		button3.setOnClickListener(_v -> {
			i_aexon.setAction(Intent.ACTION_VIEW);
			i_aexon.setData(Uri.parse(getString(R.string.tag_url_donation)));
			startActivity(i_aexon);
			dismiss();
		});
		
		button1.setOnClickListener(_v -> {
			i_aexon.setAction(Intent.ACTION_VIEW);
			i_aexon.setData(Uri.parse(getString(R.string.url_tele)));
			startActivity(i_aexon);
			dismiss();
		});
		
		button2.setOnClickListener(_v -> {
			i_aexon.setAction(Intent.ACTION_VIEW);
			i_aexon.setData(Uri.parse(getString(R.string.url_wa)));
			startActivity(i_aexon);
			dismiss();
		});
	}
	
 @Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
	Dialog dialog = super.onCreateDialog(savedInstanceState);
	dialog.setOnShowListener(d -> {
		if (dialog.getWindow() != null) {
			dialog.getWindow().setDecorFitsSystemWindows(false);
		}
	});
	return dialog;
}

@Override
public void onViewCreated(View view, Bundle savedInstanceState) {
	super.onViewCreated(view, savedInstanceState);
	if (container != null) {
		final int originalPaddingBottom = container.getPaddingBottom();
		
		container.setOnApplyWindowInsetsListener((v, insets) -> {
			int navBarHeight = insets.getStableInsetBottom();
			v.setPadding(
			v.getPaddingLeft(),
			v.getPaddingTop(),
			v.getPaddingRight(), originalPaddingBottom + navBarHeight);
			return insets;
		});
		container.post(() -> container.requestApplyInsets());
	}
}
	private void initializeLogic() {
		AexonTheme theme = AexonTheme.getInstance();
		container.setBackground(new GradientDrawable(){{setCornerRadii(new float[]{SketchwareUtil.getDip(getContext().getApplicationContext(), (int)(18)), SketchwareUtil.getDip(getContext().getApplicationContext(), (int)(18)), SketchwareUtil.getDip(getContext().getApplicationContext(), (int)(18)), SketchwareUtil.getDip(getContext().getApplicationContext(), (int)(18)), 0, 0, 0, 0});setColor(theme.getColorSurfaceContainer());}});
		handle.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)SketchwareUtil.getDip(getContext().getApplicationContext(), (int)(12)), theme.getColorOnPrimaryDark()));
		imageview1.setElevation((float)SketchwareUtil.getDip(getContext().getApplicationContext(), (int)(4)));
		button1.setBackgroundColor(theme.getColorPrimary());
		button2.setBackgroundColor(theme.getColorPrimary());
		button3.setBackgroundColor(theme.getColorPrimary());
		button1.setIconTint(theme.getColorOnPrimary());
		button2.setIconTint(theme.getColorOnPrimary());
		button3.setIconTint(theme.getColorOnPrimary());
		button1.setTextColor(theme.getColorOnPrimary());
		button2.setTextColor(theme.getColorOnPrimary());
		button3.setTextColor(theme.getColorOnPrimary());
		textview1.setTextColor(theme.getColorOnSurface());
		textview2.setTextColor(theme.getColorPrimary());
		textview3.setTextColor(theme.getColorOnSurfaceVariant());
	}
	
}