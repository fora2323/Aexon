package com.aexon;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.aexon.theme.AexonTheme;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AexonApplication extends Application {

    @Nullable
    private static Context mApplicationContext;

    @NonNull
    public static Context getContext() {
        if (mApplicationContext == null) {
            throw new IllegalStateException("ApplicationContext belum diinisialisasi!");
        }
        return mApplicationContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = getApplicationContext();

        AexonTheme.init(this);

        // Penanganan error saat app crash
        Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);
    }

    private void handleUncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        String stackTrace = Log.getStackTraceString(throwable);

        try {
            Intent intent = new Intent(getApplicationContext(), DebugActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("error", stackTrace);
            startActivity(intent);
        } catch (Exception ignored) {
        }
        Process.killProcess(Process.myPid());
        System.exit(1);
    }
}