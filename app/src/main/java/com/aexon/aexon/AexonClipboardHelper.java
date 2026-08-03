/*
* Copyright (c) 2026 Fora
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
* 
* Contact: Fora <fora060823@gmail.com>
* Created: 27-01-2026
*/
package com.aexon.aexon;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.annotation.NonNull;

public class AexonClipboardHelper {
	
	private AexonClipboardHelper() {
		throw new UnsupportedOperationException("No instances");
	}
	
	public static void copy(@NonNull Context context, @NonNull String text) {
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		if (clipboard != null) {
			ClipData clip = ClipData.newPlainText("text", text);
			clipboard.setPrimaryClip(clip);
		}
	}
}