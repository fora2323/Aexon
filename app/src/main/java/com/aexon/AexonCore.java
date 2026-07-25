package com.aexon;

import android.content.Context;
import dalvik.system.DexClassLoader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

public class AexonCore {
	
	public static String getAppList(Context context, int type) {
		try {
			File dexFile = new File(context.getFilesDir(), "aexon_core.dex");
			
			if (!dexFile.exists()) {
				InputStream is = context.getAssets().open("aexon_core.dex");
				FileOutputStream fos = new FileOutputStream(dexFile);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = is.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				is.close();
				
				dexFile.setWritable(false);
			}
			
			DexClassLoader loader = new DexClassLoader(dexFile.getAbsolutePath(), null, null, context.getClassLoader());
			
			Class<?> coreClass = loader.loadClass("com.aexon.core.MainCore");
			Object coreInstance = coreClass.newInstance();
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