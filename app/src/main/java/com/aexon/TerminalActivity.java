package com.aexon;

import android.content.Context;
import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.aexon.core.AexonColor;
import com.aexon.core.AexonMath;
import com.aexon.material.cardview.AexonCardView;
import com.aexon.material.edittext.AexonEditText;
import com.aexon.material.toasty.AexonToast;
import com.aexon.theme.AexonTheme;
import com.aexon.aexon.AexonWindowHelper;
import com.aexon.material.menu.AexonPopupMenu;

public class TerminalActivity extends Activity {
	
	private boolean isKeyboardShowing = false;
	private boolean isRunning = false;
	private boolean isBusyboxMode = false;
	private long currentPid = -1;
	private AexonTheme currentTheme;
	
	private LinearLayout container;
	private LinearLayout toolbar;
	private FrameLayout sub_container;
	private TextView textview1;
	private ImageView icon_stop;
	private ImageView icon_clear;
	private ImageView icon_more;
	private TextView txt_output;
	private ScrollView vscroll_output;
	private AexonCardView cardview1;
	private LinearLayout linear2;
	private AexonEditText input_exe;
	private ImageView icon_exe;
	
	private Intent aexon_intent = new Intent();
	
	private final StringBuilder logBuffer = new StringBuilder();
	private Handler mainHandler = new Handler(Looper.getMainLooper());
	private Runnable flushRunnable;
	private static final long FLUSH_INTERVAL = 200;
	private boolean flushScheduled = false;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.terminal);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		container = findViewById(R.id.container);
		toolbar = findViewById(R.id.toolbar);
		sub_container = findViewById(R.id.sub_container);
		textview1 = findViewById(R.id.textview1);
		icon_stop = findViewById(R.id.icon_stop);
		icon_clear = findViewById(R.id.icon_clear);
		icon_more = findViewById(R.id.icon_more);
		txt_output = findViewById(R.id.txt_output);
		vscroll_output = findViewById(R.id.vscroll_output);
		cardview1 = findViewById(R.id.cardview1);
		linear2 = findViewById(R.id.linear2);
		input_exe = findViewById(R.id.input_exe);
		icon_exe = findViewById(R.id.icon_exe);
		
		txt_output.setTextIsSelectable(true);
		txt_output.setTypeface(Typeface.MONOSPACE);
		txt_output.setIncludeFontPadding(false);
		txt_output.setHorizontallyScrolling(true);
		txt_output.setHorizontalScrollBarEnabled(false);
		txt_output.setOverScrollMode(View.OVER_SCROLL_NEVER);
		
		if (vscroll_output != null) {
			vscroll_output.setVerticalScrollBarEnabled(false);
			vscroll_output.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
		
		input_exe.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		input_exe.setTypeface(Typeface.MONOSPACE);
		
		flushRunnable = new Runnable() {
			@Override
			public void run() {
				flushLogBuffer();
			}
		};
		
		icon_exe.setOnClickListener(v -> {
			String cmd = input_exe.getText().toString().trim();
			if (cmd.isEmpty() || isRunning) return;
			runCommand(cmd);
		});
		
		icon_stop.setOnClickListener(v -> {
			if (isRunning && currentPid > 0) {
				Aexon.killProcess(currentPid);
			}
		});
		
		icon_clear.setOnClickListener(v -> {
			synchronized (logBuffer) {
				if (txt_output.length() == 0 && logBuffer.length() == 0) return;
				txt_output.setText("");
				logBuffer.setLength(0);
			}
			updateIconStates();
		});
		
		icon_more.setOnClickListener(v -> {
			AexonPopupMenu popup = new AexonPopupMenu(TerminalActivity.this, icon_more);
			popup.getMenuInflater().inflate(R.menu.terminal_menu, popup.getMenu());
			popup.getMenu().findItem(R.id.ax_mode).setChecked(isBusyboxMode);
			popup.setWidth(200);
			popup.setOnMenuItemClickListener(item -> {
				int id = item.getItemId();
				if (id == R.id.ax_mode) {
					isBusyboxMode = item.isChecked();
					getSharedPreferences("terminal_prefs", Context.MODE_PRIVATE).edit().putBoolean("busybox_mode", isBusyboxMode).apply();
				} else if (id == R.id.save_log) {
					saveLogViaShell();
				} else if (id == R.id.delete_log) {
					String deleteCmd = "rm -rf /storage/emulated/0/.Aexon/*";
					Aexon.execStream(deleteCmd, new Aexon.OnProcessOutputListener() {
						@Override public void onStart(long pid) {}
						@Override public void onOutput(String chunk) {}
						@Override
						public void onExit(int exitCode) {
							mainHandler.post(() -> {
								if (exitCode == 0) {
									AexonToast.make(TerminalActivity.this).title("Delete Log").message("Isi folder .Aexon berhasil dihapus").show();
								} else {
									AexonToast.make(TerminalActivity.this).title("Delete Log").message("Gagal menghapus log (exit " + exitCode + ")").show();
								}
							});
						}
					});
				}
				return true;
			});
			popup.show();
		});
	}
	
	private void saveLogViaShell() {
		String logContent = txt_output.getText().toString();
		if (logContent.isEmpty()) {
			AexonToast.make(this).title("Save Log").message("Log is empty").show();
			return;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.getDefault());
		String fileName = "log_" + sdf.format(new Date()) + ".txt";
		String filePath = "/storage/emulated/0/.Aexon/" + fileName;
		
		String encoded;
		try {
			encoded = android.util.Base64.encodeToString(logContent.getBytes("UTF-8"), android.util.Base64.NO_WRAP);
		} catch (Exception e) {
			AexonToast.make(this).title("Save Log").message("Failed: " + e.getMessage()).show();
			return;
		}
		
		String saveCmd = "mkdir -p /storage/emulated/0/.Aexon/ && echo '" + encoded + "' | base64 -d > '" + filePath + "'";
		
		Aexon.execStream(saveCmd, new Aexon.OnProcessOutputListener() {
			@Override public void onStart(long pid) {
                
            }
			@Override public void onOutput(String chunk) {
                
            }
			@Override
			public void onExit(int exitCode) {
				mainHandler.post(() -> {
					if (exitCode == 0) {
						AexonToast.make(TerminalActivity.this).title("Save Log").message("Saved: " + fileName).show();
					} else {
						AexonToast.make(TerminalActivity.this).title("Save Log").message("Failed to save log (exit " + exitCode + ")").show();
					}
				});
			}
		});
	}
	
	private void initializeLogic() {
		currentTheme = AexonTheme.getInstance();
		
		SharedPreferences prefs = getSharedPreferences("terminal_prefs", Context.MODE_PRIVATE);
		isBusyboxMode = prefs.getBoolean("busybox_mode", false);
		
		icon_more.setColorFilter(currentTheme.getColorOnSurface(), PorterDuff.Mode.SRC_ATOP);
		icon_exe.setColorFilter(currentTheme.getColorPrimary(), PorterDuff.Mode.SRC_ATOP);
		
		icon_more.setBackground(AexonDrawable.oval(this, currentTheme.getColorSurfaceContainer()));
		icon_more.setClickable(true);
		icon_more.setFocusable(true);
		
		icon_exe.setBackground(AexonDrawable.oval(this, currentTheme.getColorSurfaceContainer()));
		icon_exe.setClickable(true);
		icon_exe.setFocusable(true);
		
		toolbar.setBackgroundColor(currentTheme.getColorSurface());
		sub_container.setBackgroundColor(currentTheme.getColorSurface());
		cardview1.setBackgroundColor(currentTheme.getColorSurfaceContainer());
		textview1.setTextColor(currentTheme.getColorOnSurface());
		AexonWindowHelper.setWindowStyle(getWindow(), currentTheme.getColorSurface());
		
		updateIconStates();
		
		final View rootView = findViewById(android.R.id.content);
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Rect r = new Rect();
				rootView.getWindowVisibleDisplayFrame(r);
				int screenHeight = rootView.getRootView().getHeight();
				int keypadHeight = screenHeight - r.bottom;
				boolean isShowing = keypadHeight > screenHeight * 0.15;
				if (isKeyboardShowing && !isShowing) {
					View focused = getCurrentFocus();
					if (focused instanceof AexonEditText && focused.isFocused()) {
						focused.postDelayed(() -> focused.clearFocus(), 100);
					}
				}
				isKeyboardShowing = isShowing;
			}
		});
	}
	
	private void flushLogBuffer() {
		flushScheduled = false;
		String outputText;
		
		synchronized (logBuffer) {
			if (logBuffer.length() == 0) return;
			outputText = logBuffer.toString();
			logBuffer.setLength(0);
		}
		
		boolean wasAtBottom = isScrollAtBottom();
		txt_output.append(outputText);
		updateIconStates();
		
		if (wasAtBottom && vscroll_output != null) {
			vscroll_output.post(() -> vscroll_output.fullScroll(View.FOCUS_DOWN));
		}
	}
	
	private void runCommand(String cmd) {
		isRunning = true;
		setInputEnabled(false);
		
		synchronized (logBuffer) {
			logBuffer.setLength(0);
		}
		updateIconStates();
		
		String finalCmd = cmd;
		String busyboxPath = getApplicationInfo().nativeLibraryDir + "/libbusybox.so";
		String[] parts = cmd.trim().split("\\s+");
		
		if (isBusyboxMode && parts.length > 0 && !parts[0].isEmpty()) {
			if (!parts[0].equals("busybox") && !parts[0].equals(busyboxPath)) {
				finalCmd = busyboxPath + " " + cmd;
			}
		}
		
		Aexon.execStream(finalCmd, new Aexon.OnProcessOutputListener() {
			@Override
			public void onStart(long pid) {
				currentPid = pid;
				synchronized (logBuffer) {
					logBuffer.append("[shproc] Process[pid=").append(currentPid).append(", status=start]\n");
				}
				mainHandler.post(() -> {
					if (!flushScheduled) {
						flushScheduled = true;
						mainHandler.postDelayed(flushRunnable, FLUSH_INTERVAL);
					}
				});
			}
			
			@Override
			public void onOutput(String chunk) {
				if (chunk.contains("\0")) {
					chunk = chunk.replace('\0', ' ');
				}
				if (!chunk.endsWith("\n")) {
					chunk = chunk + "\n";
				}
				int currentLength;
				synchronized (logBuffer) {
					logBuffer.append(chunk);
					currentLength = logBuffer.length();
				}
				mainHandler.post(() -> {
					if (currentLength > 4096) {
						mainHandler.removeCallbacks(flushRunnable);
						flushLogBuffer();
					} else if (!flushScheduled) {
						flushScheduled = true;
						mainHandler.postDelayed(flushRunnable, FLUSH_INTERVAL);
					}
				});
			}
			
			@Override
			public void onExit(int exitCode) {
				mainHandler.removeCallbacks(flushRunnable);
				mainHandler.post(() -> {
					flushLogBuffer();
					synchronized (logBuffer) {
						logBuffer.append("[shproc] Process[pid=").append(currentPid).append(", status=exit, exitcode=").append(exitCode).append("]\n");
					}
					flushLogBuffer();
					isRunning = false;
					setInputEnabled(true);
					currentPid = -1;
					updateIconStates();
				});
			}
		});
	}
	
	private boolean isScrollAtBottom() {
		if (vscroll_output == null) return true;
		int diff = (txt_output.getBottom() - (vscroll_output.getHeight() + vscroll_output.getScrollY()));
		return diff <= AexonMath.dpToPxInt(this, 24f);
	}
	
	private void updateIconStates() {
		boolean hasLog;
		synchronized (logBuffer) {
			hasLog = txt_output.length() > 0 || logBuffer.length() > 0;
		}
		setIconState(icon_clear, hasLog, currentTheme.getColorOnSurface());
		setIconState(icon_stop, isRunning, currentTheme.getColorPrimary());
	}
	
	private void setIconState(ImageView icon, boolean active, int activeColor) {
		int inactiveColor = currentTheme.getColorSurfaceVariant();
		icon.setColorFilter(active ? activeColor : inactiveColor, PorterDuff.Mode.SRC_ATOP);
		icon.setBackground(AexonDrawable.oval(this, currentTheme.getColorSurfaceContainer()));
		icon.setClickable(active);
		icon.setFocusable(active);
	}
	
	private void setInputEnabled(boolean enabled) {
		input_exe.setEnabled(enabled);
		icon_exe.setEnabled(enabled);
		icon_exe.setAlpha(enabled ? 1.0f : 0.5f);
		input_exe.setAlpha(enabled ? 1.0f : 0.5f);
	}
	
	@Override
	public void onBackPressed() {
		super.finish();
		overridePendingTransition(R.anim.fade_in_back, R.anim.fade_out_back);
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mainHandler.removeCallbacks(flushRunnable);
	}
}