package com.aexon;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.SharedPreferences;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.aexon.material.aexonloading.AexonLoading;
import com.aexon.material.edittext.AexonEditText;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.aexon.material.menu.AexonPopupMenu;
import com.aexon.theme.AexonTheme;
import com.aexon.theme.AexonThemeListener;

public class AexonFragmentActivity extends Fragment {
	
	private AexonTheme currentTheme;
	private long currentLoadSession = 0;
	private int currentMode = -1;
	private final AexonThemeListener themeListener = (seedColor, theme) -> {
		_applyTheme(theme);
	};
	private boolean isLoading = false;
	
	private ArrayList<HashMap<String, Object>> list_app = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> list_filtered = new ArrayList<>();
	
	private FrameLayout linear1;
	private LinearLayout linear2;
	private LinearLayout search_view;
	private ListView listview1;
	private LinearLayout container3;
	private AexonLoading loadingaexon1;
	private TextView text_not_found;
	private ImageView imageview1;
	private AexonEditText search_bar;
	private ImageView icon_more;
	
	private SharedPreferences sp;
	
	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		View _view = _inflater.inflate(R.layout.aexon_fragment, _container, false);
		initialize(_savedInstanceState, _view);
		initializeLogic();
		return _view;
	}
	
	private void initialize(Bundle _savedInstanceState, View _view) {
		linear1 = _view.findViewById(R.id.linear1);
		linear2 = _view.findViewById(R.id.linear2);
		search_view = _view.findViewById(R.id.search_view);
		listview1 = _view.findViewById(R.id.listview1);
		container3 = _view.findViewById(R.id.container3);
		loadingaexon1 = _view.findViewById(R.id.loadingaexon1);
		text_not_found = _view.findViewById(R.id.text_not_found);
		imageview1 = _view.findViewById(R.id.imageview1);
		search_bar = _view.findViewById(R.id.search_bar);
		icon_more = _view.findViewById(R.id.icon_more);
		sp = getContext().getSharedPreferences("-sharedAexon", Activity.MODE_PRIVATE);
		
		search_bar.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
				final String _charSeq = _param1.toString();
				list_app.clear();
				if (_charSeq.isEmpty()) {
					list_app.addAll(list_filtered);
				} else {
					for (HashMap<String, Object> _item : list_filtered) {
						Object nameObj = _item.get("app_name");
						if (nameObj != null && nameObj.toString().toLowerCase().contains(_charSeq.toLowerCase())) {
							list_app.add(_item);
						}
					}
				}
				if (listview1.getAdapter() != null) {
					((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
				}
				if (list_app.isEmpty() && !_charSeq.isEmpty()) {
					listview1.setVisibility(View.GONE);
					container3.setVisibility(View.VISIBLE);
					loadingaexon1.setVisibility(View.GONE);
					text_not_found.setVisibility(View.VISIBLE);
				} else {
					listview1.setVisibility(View.VISIBLE);
					container3.setVisibility(View.GONE);
					text_not_found.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
				
			}
			
			@Override
			public void afterTextChanged(Editable _param1) {
				
			}
		});
		
		icon_more.setOnClickListener(_v -> {
			AexonPopupMenu popup = new AexonPopupMenu(getContext(), icon_more);
			popup.getMenuInflater().inflate(R.menu.short_menu, popup.getMenu());
			popup.setWidth(165);
			popup.setOnMenuItemClickListener(item -> {
				int id = item.getItemId();
				if (id == R.id.user) {
					_loadApp(1);
				} else if (id == R.id.game) {
					_loadApp(3);
				} else if (id == R.id.system) {
					_loadApp(2);
				}
				return true;
			});
			
			popup.show();
		});
	}
	
private final Aexon.OnBinderReceivedListener binderReceivedListener = () -> {
	if (getActivity() != null) getActivity().runOnUiThread(() -> {
		int lastMode = sp.getInt("mode", 1);
		currentMode = -1;
		_loadApp(lastMode);
	});
};

private final Aexon.OnBinderDeadListener binderDeadListener = () -> {
	if (getActivity() != null) getActivity().runOnUiThread(() -> {
		isLoading = false;
		listview1.setVisibility(View.GONE);
		container3.setVisibility(View.VISIBLE);
		loadingaexon1.setVisibility(View.GONE);
		text_not_found.setVisibility(View.VISIBLE);
	});
};

	private void initializeLogic() {
		double lastMode = (double) sp.getInt("mode", 1);
		_loadApp(lastMode);
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
		AexonTheme.getInstance().addListener(themeListener);
		Aexon.addBinderReceivedListener(binderReceivedListener);
		Aexon.addBinderDeadListener(binderDeadListener);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		AexonTheme.getInstance().removeListener(themeListener);
		Aexon.removeBinderReceivedListener(binderReceivedListener);
		Aexon.removeBinderDeadListener(binderDeadListener);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		_applyTheme(AexonTheme.getInstance());
	}
	public void _loadApp(final double _app) {
		int mode = (int) _app;
		
		if (mode == currentMode && !isLoading) return;
		currentMode = mode;
		
		if (search_bar != null) search_bar.setText("");
		sp.edit().putInt("mode", mode).apply();
		
		if (Aexon.isBinder()) {
			text_not_found.setVisibility(View.GONE);
			listview1.setVisibility(View.GONE);
			container3.setVisibility(View.VISIBLE);
			loadingaexon1.setVisibility(View.VISIBLE);
			loadingaexon1.setProgress(0f);
			
			isLoading = true;
			
			final Context safeContext = getContext();
			final Activity safeActivity = getActivity();
			
			if (safeContext == null || safeActivity == null) {
				isLoading = false;
				return;
			}
			
			currentLoadSession = System.currentTimeMillis();
			final long mySessionId = currentLoadSession;
			
			new Thread(() -> {
				try {
					final String gamesRaw = AexonCore.getAppList(safeContext, mode);
					
					if (mySessionId != currentLoadSession) return;
					
					if (gamesRaw == null || gamesRaw.isEmpty()) {
						safeActivity.runOnUiThread(() -> {
							if (mySessionId == currentLoadSession) {
								container3.setVisibility(View.GONE);
								loadingaexon1.setVisibility(View.GONE);
								isLoading = false;
							}
						});
						return;
					}
					
					final String[] packageArray = gamesRaw.split("\n");
					final PackageManager pm = safeContext.getPackageManager();
					final ArrayList<HashMap<String, Object>> tempList = new ArrayList<>();
					int total = packageArray.length;
					
					for (int i = 0; i < total; i++) {
						if (mySessionId != currentLoadSession) return;
						
						String pkg = packageArray[i].trim();
						if (!pkg.isEmpty()) {
							HashMap<String, Object> item = new HashMap<>();
							item.put("package", pkg);
							try {
								ApplicationInfo info = pm.getApplicationInfo(pkg, 0);
								item.put("app_name", pm.getApplicationLabel(info).toString());
								item.put("app_icon", pm.getApplicationIcon(info));
							} catch (Exception e) {
								item.put("app_name", pkg);
								item.put("app_icon", pm.getDefaultActivityIcon());
							}
							tempList.add(item);
						}
						
						final int progress = i + 1;
						if (progress % 5 == 0 || progress == total) {
							safeActivity.runOnUiThread(() -> {
								if (mySessionId == currentLoadSession) {
									loadingaexon1.setProgress(progress, total);
								}
							});
						}
					}
					
					if (mySessionId != currentLoadSession) return;
					
					safeActivity.runOnUiThread(() -> {
						if (mySessionId != currentLoadSession) return;
						
						list_app.clear();
						list_app.addAll(tempList);
						list_filtered.clear();
						list_filtered.addAll(tempList);
						if (listview1.getAdapter() == null) {
							listview1.setAdapter(new Listview1Adapter(list_app));
						} else {
							((BaseAdapter) listview1.getAdapter()).notifyDataSetChanged();
						}
						container3.setVisibility(View.GONE);
						listview1.setVisibility(View.VISIBLE);
						loadingaexon1.setVisibility(View.GONE);
						isLoading = false;
					});
				} catch (Exception e) {
					safeActivity.runOnUiThread(() -> {
						if (mySessionId == currentLoadSession) isLoading = false;
					});
				}
			}).start();
			
		} else {
			listview1.setVisibility(View.GONE);
			container3.setVisibility(View.VISIBLE);
			loadingaexon1.setVisibility(View.GONE);
			text_not_found.setVisibility(View.VISIBLE);
		}
	}
	
	
	public boolean _isGame(final String _pkg) {
		try {
			ApplicationInfo info = getContext().getPackageManager().getApplicationInfo(_pkg, 0);
			int category = ApplicationInfo.CATEGORY_GAME;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				return info.category == ApplicationInfo.CATEGORY_GAME;
			} else {
				return (info.flags & ApplicationInfo.FLAG_IS_GAME) != 0;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	
	public void _setShadowEdge(final int _color) {
		int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
		
		int transparentColor = _color & 0x00FFFFFF;
		
		GradientDrawable topGradient = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{_color, transparentColor});
		topGradient.setShape(GradientDrawable.RECTANGLE);
		topGradient.setSize(0, height);
		
		GradientDrawable bottomGradient = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{_color, transparentColor});
		bottomGradient.setShape(GradientDrawable.RECTANGLE);
		bottomGradient.setSize(0, height);
		
		LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{topGradient, bottomGradient});
		
		layerDrawable.setLayerGravity(0, Gravity.TOP);
		layerDrawable.setLayerGravity(1, Gravity.BOTTOM);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			linear2.setForeground(layerDrawable);
		}
	}
	
	
	public void _applyTheme(final AexonTheme _theme) {
		currentTheme = _theme;
		_setShadowEdge(_theme.getColorSurface());
		search_view.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b, int c, int d) { this.setCornerRadius(a); this.setStroke(b, c); this.setColor(d); return this; } }.getIns((int)SketchwareUtil.getDip(getContext().getApplicationContext(), (int)(20)), (int)SketchwareUtil.getDip(getContext().getApplicationContext(), (int)(1)), _theme.getColorOutlineVariant(), _theme.getColorSurfaceContainer()));
		loadingaexon1.setTrackColor(_theme.getColorSurfaceContainer());
		loadingaexon1.setThumbColor(_theme.getColorPrimary());
		search_bar.setTextColor(_theme.getColorOnSurface());
		search_bar.setHintTextColor(_theme.getColorOnSurfaceVariant());
		imageview1.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		icon_more.setColorFilter(_theme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		text_not_found.setTextColor(_theme.getColorOnSurfaceVariant());
		if (listview1 != null && listview1.getAdapter() != null) {
			((BaseAdapter) listview1.getAdapter()).notifyDataSetChanged();
		}
	}
	
	public class Listview1Adapter extends BaseAdapter {
		
		ArrayList<HashMap<String, Object>> _data;
		
		public Listview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public int getCount() {
			return _data.size();
		}
		
		@Override
		public HashMap<String, Object> getItem(int _index) {
			return _data.get(_index);
		}
		
		@Override
		public long getItemId(int _index) {
			return _index;
		}
		
		@Override
		public View getView(final int _position, View _v, ViewGroup _container) {
			LayoutInflater _inflater = getActivity().getLayoutInflater();
			View _view = _v;
			if (_view == null) {
				_view = _inflater.inflate(R.layout.list_app, null);
			}
			
			final LinearLayout container = _view.findViewById(R.id.container);
			final LinearLayout container_view = _view.findViewById(R.id.container_view);
			final LinearLayout monito_enrgy = _view.findViewById(R.id.monito_enrgy);
			final com.aexon.material.cardview.AexonCardView cardview1 = _view.findViewById(R.id.cardview1);
			final TextView name_app = _view.findViewById(R.id.name_app);
			final ImageView boost_btn = _view.findViewById(R.id.boost_btn);
			final ImageView icon_app = _view.findViewById(R.id.icon_app);
			
			container.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)SketchwareUtil.getDip(getContext().getApplicationContext(), (int)(14)), currentTheme.getColorSurfaceContainer()));
			name_app.setTextColor(currentTheme.getColorOnSurface());
			container_view.setVisibility(View.VISIBLE);
			monito_enrgy.setVisibility(View.GONE);
			HashMap<String, Object> item = _data.get(_position);
			
			//ambil name
			name_app.setText(String.valueOf(item.get("app_name")));
			
			//ambil package
			final String _pkg = String.valueOf(item.get("package"));
			
			//ambil icon
			if (item.containsKey("app_icon")) {
				icon_app.setImageDrawable((Drawable) item.get("app_icon"));
			}
			
			//simpan nama PKG
			boolean isPkg = sp.getBoolean(_pkg, false);
			if (_isGame(_pkg)) {
				boost_btn.setVisibility(View.VISIBLE);
				if (isPkg) boost_btn.setColorFilter(currentTheme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP); else boost_btn.setColorFilter(currentTheme.getColorSurfaceVariant(), PorterDuff.Mode.SRC_ATOP);
				boost_btn.setTag(_pkg);
				boost_btn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View _view) {
						//ambil nama tag pkg
						String pkg = (String) _view.getTag();
						boolean newState = !sp.getBoolean(pkg, false);
						
						//set nama PKG ke sharedprefrence
						sp.edit().putBoolean(pkg, newState).apply();
						ImageView btn = (ImageView) _view;
						if (newState) boost_btn.setColorFilter(currentTheme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP); else boost_btn.setColorFilter(currentTheme.getColorSurfaceVariant(), PorterDuff.Mode.SRC_ATOP);
					}
				});
			} else {
				boost_btn.setVisibility(View.GONE);
			}
			
			return _view;
		}
	}
}