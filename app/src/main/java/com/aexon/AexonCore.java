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
package com.aexon;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import dalvik.system.DexClassLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AexonCore {
	
	@NonNull
	public static String getAppList(@NonNull Context context, int type) {
		try {
			File dexFile = new File(context.getFilesDir(), "aexon_core.dex");
			
			if (!dexFile.exists()) {
				try (InputStream is = context.getAssets().open("aexon_core.dex");
				FileOutputStream fos = new FileOutputStream(dexFile)) {
					byte[] buffer = new byte[1024];
					int len;
					while ((len = is.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
				}
				dexFile.setWritable(false);
			}
			
			DexClassLoader loader = new DexClassLoader(dexFile.getAbsolutePath(), null, null, context.getClassLoader());
			
			Class<?> coreClass = loader.loadClass("com.aexon.core.MainCore");
			Object coreInstance = coreClass.getDeclaredConstructor().newInstance();
			Method getPackages = coreClass.getMethod("getPackages", Context.class, int.class);
			
			String result = (String) getPackages.invoke(coreInstance, context, type);
			
			if (result != null && !result.isEmpty()) {
				return result.replace(",", "\n");
			} else {
				return "";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return "Error: " + e.toString();
		}
	}
}