package com.aexon.material.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.RippleDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aexon.theme.AexonTheme;

public class AexonAlertDialog extends AlertDialog.Builder {
	
	private Context context;
	private int primaryColor;
	private int onSurfaceColor;
	private int onSurfaceVariantColor;
	private int surfaceContainer;
	
	public AexonAlertDialog(Context context) {
		super(context);
		this.context = context;
		initThemeColors();
	}
	
	public AexonAlertDialog(Context context, int themeResId) {
		super(context, themeResId);
		this.context = context;
		initThemeColors();
	}
	
	private void initThemeColors() {
		AexonTheme theme = AexonTheme.getInstance();
		surfaceContainer = theme.getColorSurfaceContainer();
		primaryColor = theme.getColorPrimary();
		onSurfaceColor = theme.getColorOnSurface();
		onSurfaceVariantColor = theme.getColorOnSurfaceVariant();
	}
	
	@Override
	public AlertDialog create() {
		AlertDialog dialog = super.create();
		Window window = dialog.getWindow();
		if (window != null) {
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		}
		dialog.setOnShowListener(dialogInterface -> applyMaterialStyling(dialog));
		return dialog;
	}
	
	@Override
	public AlertDialog show() {
		AlertDialog dialog = create();
		dialog.show();
		applyMaterialStyling(dialog);
		return dialog;
	}
	
	private void applyMaterialStyling(AlertDialog dialog) {
		Window window = dialog.getWindow();
		if (window != null) {
			GradientDrawable shape = new GradientDrawable();
			shape.setShape(GradientDrawable.RECTANGLE);
			shape.setCornerRadius(dpToPxInt(28));
			shape.setColor(surfaceContainer);
			
			int inset = dpToPxInt(24);
			InsetDrawable insetDrawable = new InsetDrawable(shape, inset, inset, inset, inset);
			
			window.setBackgroundDrawable(insetDrawable);
			window.setDimAmount(0.4f);
			
			int titleId = context.getResources().getIdentifier("alertTitle", "id", "android");
			if (titleId != 0) {
				TextView titleView = dialog.findViewById(titleId);
				if (titleView != null) {
					titleView.setTextColor(onSurfaceColor);
					titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
				}
			}
			
			int messageId = context.getResources().getIdentifier("message", "id", "android");
			if (messageId != 0) {
				TextView messageView = dialog.findViewById(messageId);
				if (messageView != null) {
					messageView.setTextColor(onSurfaceVariantColor);
					messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					messageView.setLineSpacing(0, 1.25f);
				}
			}
			
			ListView listView = dialog.getListView();
			if (listView != null) {
				listView.setDivider(null);
				listView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
					@Override
					public void onChildViewAdded(View parent, View child) {
						if (child instanceof CheckedTextView) {
							CheckedTextView ctv = (CheckedTextView) child;
							ctv.setTextColor(onSurfaceColor);
							ctv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
							ColorStateList csl = new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked}}, new int[]{primaryColor, onSurfaceVariantColor});
							ctv.setCheckMarkTintList(csl);
						} else if (child instanceof TextView) {
							((TextView) child).setTextColor(onSurfaceColor);
							((TextView) child).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
						}
					}
					
					@Override
					public void onChildViewRemoved(View parent, View child) {}
				});
			}
			
			styleButtonPanel(dialog);
		}
	}
	
	private void styleButtonPanel(AlertDialog dialog) {
		Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
		Button negativeBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
		Button neutralBtn  = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
		View anyButton = positiveBtn != null ? positiveBtn : (negativeBtn != null ? negativeBtn : neutralBtn);
		
		if (anyButton != null && anyButton.getParent() instanceof LinearLayout) {
			LinearLayout buttonPanel = (LinearLayout) anyButton.getParent();
			buttonPanel.setPadding(dpToPxInt(16), dpToPxInt(8), dpToPxInt(16), dpToPxInt(16));
		}
		
		applyButtonStyle(positiveBtn);
		applyButtonStyle(negativeBtn);
		applyButtonStyle(neutralBtn);
	}
	
	private void applyButtonStyle(Button button) {
		if (button == null) return;
		
		button.setTextColor(primaryColor);
		button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		button.setAllCaps(false);
		
		Typeface semiBold = Typeface.create("sans-serif-medium", Typeface.NORMAL);
		button.setTypeface(semiBold);
		
		GradientDrawable rippleMask = new GradientDrawable();
		rippleMask.setShape(GradientDrawable.RECTANGLE);
		rippleMask.setCornerRadius(dpToPxInt(100));
		rippleMask.setColor(Color.WHITE);
		
		int rippleColor = (primaryColor & 0x00FFFFFF) | 0x1F000000;
		ColorStateList colorStateList = ColorStateList.valueOf(rippleColor);
		
		RippleDrawable ripple = new RippleDrawable(colorStateList, null, rippleMask);
		button.setBackground(ripple);
		
		button.setMinHeight(dpToPxInt(40));
		button.setMinimumHeight(dpToPxInt(40));
		button.setPadding(dpToPxInt(16), dpToPxInt(10), dpToPxInt(16), dpToPxInt(10));
		
		ViewGroup.LayoutParams params = button.getLayoutParams();
		if (params instanceof LinearLayout.LayoutParams) {
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) params;
			lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
			lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
			lp.setMargins(dpToPxInt(4), 0, dpToPxInt(4), 0);
			button.setLayoutParams(lp);
		}
	}
	
	private int dpToPxInt(float dp) {
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
		return (int) (px + 0.5f);
	}
	
	@Override
	public AexonAlertDialog setTitle(CharSequence title) {
		super.setTitle(title);
		return this;
	}
	
	@Override
	public AexonAlertDialog setTitle(int titleId) {
		super.setTitle(titleId);
		return this;
	}
	
	@Override
	public AexonAlertDialog setCustomTitle(View customTitleView) {
		super.setCustomTitle(customTitleView);
		return this;
	}
	
	@Override
	public AexonAlertDialog setMessage(CharSequence message) {
		super.setMessage(message);
		return this;
	}
	
	@Override
	public AexonAlertDialog setMessage(int messageId) {
		super.setMessage(messageId);
		return this;
	}
	
	@Override
	public AexonAlertDialog setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
		super.setPositiveButton(text, listener);
		return this;
	}
	
	@Override
	public AexonAlertDialog setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
		super.setPositiveButton(textId, listener);
		return this;
	}
	
	@Override
	public AexonAlertDialog setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
		super.setNegativeButton(text, listener);
		return this;
	}
	
	@Override
	public AexonAlertDialog setNegativeButton(int textId, DialogInterface.OnClickListener listener) {
		super.setNegativeButton(textId, listener);
		return this;
	}
	
	@Override
	public AexonAlertDialog setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
		super.setNeutralButton(text, listener);
		return this;
	}
	
	@Override
	public AexonAlertDialog setNeutralButton(int textId, DialogInterface.OnClickListener listener) {
		super.setNeutralButton(textId, listener);
		return this;
	}
	
	@Override
	public AexonAlertDialog setIcon(int iconId) {
		super.setIcon(iconId);
		return this;
	}
	
	@Override
	public AexonAlertDialog setIcon(Drawable icon) {
		super.setIcon(icon);
		return this;
	}
	
	@Override
	public AexonAlertDialog setCancelable(boolean cancelable) {
		super.setCancelable(cancelable);
		return this;
	}
	
	@Override
	public AexonAlertDialog setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
		super.setOnCancelListener(onCancelListener);
		return this;
	}
	
	@Override
	public AexonAlertDialog setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
		super.setOnDismissListener(onDismissListener);
		return this;
	}
	
	@Override
	public AexonAlertDialog setItems(CharSequence[] items, DialogInterface.OnClickListener listener) {
		super.setItems(items, listener);
		return this;
	}
	
	@Override
	public AexonAlertDialog setSingleChoiceItems(CharSequence[] items, int checkedItem, DialogInterface.OnClickListener listener) {
		super.setSingleChoiceItems(items, checkedItem, listener);
		return this;
	}
	
	@Override
	public AexonAlertDialog setSingleChoiceItems(ListAdapter adapter, int checkedItem, DialogInterface.OnClickListener listener) {
		super.setSingleChoiceItems(adapter, checkedItem, listener);
		return this;
	}
	
	@Override
	public AexonAlertDialog setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems, DialogInterface.OnMultiChoiceClickListener listener) {
		super.setMultiChoiceItems(items, checkedItems, listener);
		return this;
	}
	
	@Override
	public AexonAlertDialog setView(View view) {
		super.setView(view);
		return this;
	}
}