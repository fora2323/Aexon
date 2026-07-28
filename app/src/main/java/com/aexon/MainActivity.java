package com.aexon;

import android.animation.*;
import android.app.*;
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
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnAdapterChangeListener;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import com.aexon.databinding.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aexon.corex.AexonFragmentAdapter;
import com.aexon.viewx.AexonNavigationBar;

public class MainActivity extends AppCompatActivity {
	
	private AexonFragmentAdapter adapter;
	private MainBinding binding;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		EdgeToEdge.enable(this);
		binding = MainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
	getWindow().setNavigationBarContrastEnforced(false);
}

ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
	Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
	
	binding.toolbar.setPadding(0, systemBars.top, 0, 0);
	
	v.setPadding(0, 0, 0, 0);
	
	ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) binding.axNav1.getLayoutParams();
	int defaultMargin = (int) (24 * getResources().getDisplayMetrics().density); 
	lp.bottomMargin = defaultMargin + systemBars.bottom; 
	binding.axNav1.setLayoutParams(lp);
	
	return insets;
});
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		adapter = new AexonFragmentAdapter(getSupportFragmentManager());
	}
	
	private void initializeLogic() {
		adapter.setFragments(new HomeFragmentActivity(), new AppFragmentActivity(), new PluginFragmentActivity(), new SettingsFragmentActivity());
		binding.viewpager1.setAdapter(adapter);
		binding.viewpager1.setOffscreenPageLimit(1);
		binding.axNav1.setOnItemSelectedListener((index, itemId) -> {
			binding.viewpager1.setCurrentItem(index, true);
		});
		
		binding.viewpager1.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int _position, float _positionOffset, int _positionOffsetPixels) {
				
			}
			
			@Override
			public void onPageSelected(int _position) {
				if (_position == 0) {
					binding.axNav1.setChecked(0);
				}
				if (_position == 1) {
					binding.axNav1.setChecked(1);
				}
				if (_position == 2) {
					binding.axNav1.setChecked(2);
				}
				if (_position == 3) {
					binding.axNav1.setChecked(3);
				}
			}
			
			@Override
			public void onPageScrollStateChanged(int _scrollState) {
				
			}
		});
	}
	
}