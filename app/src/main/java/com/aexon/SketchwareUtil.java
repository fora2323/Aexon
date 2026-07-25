package com.aexon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.io.File;

public class SketchwareUtil {
	
	public static int TOP = 1;
	public static int CENTER = 2;
	public static int BOTTOM = 3;
	
	public static void sortListMap(final ArrayList<HashMap<String, Object>> listMap, final String key, final boolean isNumber, final boolean ascending) {
		Collections.sort(listMap, new Comparator<HashMap<String, Object>>() {
			public int compare(HashMap<String, Object> _compareMap1, HashMap<String, Object> _compareMap2) {
				if (isNumber) {
					int _count1 = Integer.valueOf(_compareMap1.get(key).toString());
					int _count2 = Integer.valueOf(_compareMap2.get(key).toString());
					if (ascending) {
						return _count1 < _count2 ? -1 : _count1 < _count2 ? 1 : 0;
					} else {
						return _count1 > _count2 ? -1 : _count1 > _count2 ? 1 : 0;
					}
				} else {
					if (ascending) {
						return _compareMap1.get(key).toString().compareTo(_compareMap2.get(key).toString());
					} else {
						return _compareMap2.get(key).toString().compareTo(_compareMap1.get(key).toString());
					}
				}
			}
		});
	}
	
	public static void CropImage(Activity _activity, String _path, int _requestCode) {
		try {
			Intent _intent = new Intent("com.android.camera.action.CROP");
			File _file = new File(_path);
			Uri _contentUri = Uri.fromFile(_file);
			_intent.setDataAndType(_contentUri, "image/*");
			_intent.putExtra("crop", "true");
			_intent.putExtra("aspectX", 1);
			_intent.putExtra("aspectY", 1);
			_intent.putExtra("outputX", 280);
			_intent.putExtra("outputY", 280);
			_intent.putExtra("return-data", false);
			_activity.startActivityForResult(_intent, _requestCode);
		} catch (Exception _e) {
			Toast.makeText(_activity, "Your device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
		}
	}
	
	public static boolean isConnected(Context _context) {
		ConnectivityManager _connectivityManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo _activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
		return _activeNetworkInfo != null && _activeNetworkInfo.isConnected();
	}
	
	public static String copyFromInputStream(InputStream _inputStream) {
		ByteArrayOutputStream _outputStream = new ByteArrayOutputStream();
		byte[] _buf = new byte[1024];
		int _i;
		try {
			while ((_i = _inputStream.read(_buf)) != -1){
				_outputStream.write(_buf, 0, _i);
			}
			_outputStream.close();
			_inputStream.close();
		} catch (IOException _e) {
		}
		
		return _outputStream.toString();
	}
	
	public static void hideKeyboard(Context _context) {
		InputMethodManager _inputMethodManager = (InputMethodManager) _context.getSystemService(Context.INPUT_METHOD_SERVICE);
		_inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}
	
	public static void showKeyboard(Context _context) {
		InputMethodManager _inputMethodManager = (InputMethodManager) _context.getSystemService(Context.INPUT_METHOD_SERVICE);
		_inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}
	
	public static void showMessage(Context _context, String _s) {
		Toast.makeText(_context, _s, Toast.LENGTH_SHORT).show();
	}
	public static int getLocationX(View _view) {
		int _location[] = new int[2];
		_view.getLocationInWindow(_location);
		return _location[0];
	}
	
	public static int getLocationY(View _view) {
		int _location[] = new int[2];
		_view.getLocationInWindow(_location);
		return _location[1];
	}
	
	public static int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	public static ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double) _arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	public static float getDip(Context _context, int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, _context.getResources().getDisplayMetrics());
	}
	
	public static int getDisplayWidthPixels(Context _context) {
		return _context.getResources().getDisplayMetrics().widthPixels;
	}
	
	public static int getDisplayHeightPixels(Context _context) {
		return _context.getResources().getDisplayMetrics().heightPixels;
	}
	
	public static void getAllKeysFromMap(Map<String, Object> _map, ArrayList<String> _output) {
		if (_output == null) return;
		_output.clear();
		if (_map == null || _map.size() < 1) return;
		for (Map.Entry<String, Object> _entry : _map.entrySet()) {
			_output.add(_entry.getKey());
		}
	}
	
	public static String getString(Context _context, int _resId) {
		return _context.getResources().getString(_resId);
	}
	
	public static float getDimension(Context _context, int _resId) {
		return _context.getResources().getDimension(_resId);
	}
}