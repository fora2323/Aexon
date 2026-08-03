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

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AexonProcess extends Process {
	
	private final InputStream inputStream;
	private final InputStream errorStream;
	
	AexonProcess(@Nullable String result) {
		String data = result != null ? result : "";
		this.inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
		this.errorStream = new ByteArrayInputStream(new byte[0]);
	}
	
	@Override
	@NonNull
	public InputStream getInputStream() {
		return inputStream;
	}
	
	@Override
	@NonNull
	public InputStream getErrorStream() {
		return errorStream;
	}
	
	@Override
	@NonNull
	public OutputStream getOutputStream() {
		return new OutputStream() {
			@Override
			public void write(int b) {
				// No-op (mengabaikan semua input stream ke output)
			}
		};
	}
	
	@Override
	public int waitFor() {
		return 0;
	}
	
	@Override
	public int exitValue() {
		return 0;
	}
	
	@Override
	public void destroy() {}
}