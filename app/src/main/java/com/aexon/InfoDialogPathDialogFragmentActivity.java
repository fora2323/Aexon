package com.aexon;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import com.aexon.material.bottomsheet.AexonBottomSheetFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
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
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;
import com.aexon.theme.AexonTheme;
import com.aexon.theme.AexonThemeListener;
import com.aexon.aexon.AexonClipboardHelper;
import com.aexon.core.AexonColorUtils;


public class InfoDialogPathDialogFragmentActivity extends AexonBottomSheetFragment {
	
	private LinearLayout container;
	private LinearLayout handle;
	private TextView title;
	private TextView dec_tv;
	private LinearLayout linear2;
	private TextView btn_cancel;
	private LinearLayout linear3;
	private TextView btn_share;
	private TextView btn_copy;
	
	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		View _view = _inflater.inflate(R.layout.info_dialog_path_dialog_fragment, _container, false);
		initialize(_savedInstanceState, _view);
		initializeLogic();
		return _view;
	}
	
	private void initialize(Bundle _savedInstanceState, View _view) {
		container = _view.findViewById(R.id.container);
		handle = _view.findViewById(R.id.handle);
		title = _view.findViewById(R.id.title);
		dec_tv = _view.findViewById(R.id.dec_tv);
		linear2 = _view.findViewById(R.id.linear2);
		btn_cancel = _view.findViewById(R.id.btn_cancel);
		linear3 = _view.findViewById(R.id.linear3);
		btn_share = _view.findViewById(R.id.btn_share);
		btn_copy = _view.findViewById(R.id.btn_copy);
		
		btn_cancel.setOnClickListener(_v -> {
			//dismiss dialog
			dismiss();
		});
		
		btn_share.setOnClickListener(_v -> {
			String command = "adb shell " + Aexon.getPath(getContext());
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_TEXT, command);
			startActivity(Intent.createChooser(shareIntent, null));
			dismiss();
		});
		
		btn_copy.setOnClickListener(_v -> {
			String command = "adb shell " + Aexon.getPath(getContext());
			AexonClipboardHelper.copy(getContext(), command);
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
		btn_copy.setTextColor(theme.getColorPrimary());
		btn_share.setTextColor(theme.getColorPrimary());
		btn_cancel.setTextColor(theme.getColorPrimary());
		title.setTextColor(theme.getColorOnSurface());
		dec_tv.setTextColor(theme.getColorOnSurfaceVariant());
		//get path deamon
		String command = "adb shell " + Aexon.getPath(getContext());
		String note = "\n\n" + getString(R.string.tag_msg_cmd);
		float radius_medium = SketchwareUtil.getDimension(getContext(), R.dimen.card_radius_medium);
		float radius_max= SketchwareUtil.getDimension(getContext(), R.dimen.card_radius_max);
		
		SpannableStringBuilder message = new SpannableStringBuilder();
		SpannableString commandSpan = new SpannableString(command);
		commandSpan.setSpan(new TypefaceSpan("monospace"), 0, command.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		//gunakan aexon color utils untuk mecipefek alpha
		commandSpan.setSpan(new BackgroundColorSpan(AexonColorUtils.setAlphaComponent(theme.getColorOnSurfaceVariant(), 128)), 0, command.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		message.append(commandSpan);
		message.append(note);
		dec_tv.setText(message);
		
		//handle
		handle.setBackground(new AexonDrawable.Builder(theme.getColorOnPrimaryDark()).cornerRadius(radius_medium).build().build(getContext()));
		
		//button action
		btn_cancel.setBackground(new AexonDrawable.Builder(theme.getColorSurfaceContainer()).cornerRadius(radius_max).ripple(theme.getColorOnSurface()).build().build(getContext()));
		btn_share.setBackground(new AexonDrawable.Builder(theme.getColorSurfaceContainer()).cornerRadius(radius_max).ripple(theme.getColorOnSurface()).build().build(getContext()));
		btn_copy.setBackground(new AexonDrawable.Builder(theme.getColorSurfaceContainer()).cornerRadius(radius_max).ripple(theme.getColorOnSurface()).build().build(getContext()));
		container.setBackground(new GradientDrawable(){{setCornerRadii(new float[]{SketchwareUtil.getDip(getContext().getApplicationContext(), (int)(18)), SketchwareUtil.getDip(getContext().getApplicationContext(), (int)(18)), SketchwareUtil.getDip(getContext().getApplicationContext(), (int)(18)), SketchwareUtil.getDip(getContext().getApplicationContext(), (int)(18)), 0, 0, 0, 0});setColor(theme.getColorSurfaceContainer());}});
	}
	
}
