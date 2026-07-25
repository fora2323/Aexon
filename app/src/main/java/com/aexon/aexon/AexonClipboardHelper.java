package com.aexon.aexon;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class AexonClipboardHelper {

    public static void copy(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", text);
        clipboard.setPrimaryClip(clip);
    }
}