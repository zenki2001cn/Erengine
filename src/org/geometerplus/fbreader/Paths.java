/*
 * Copyright (C) 2007-2011 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader;

import java.io.File;

import org.geometerplus.zlibrary.core.options.ZLStringOption;

import com.easyview.ebook.reader.engine.core.EREngine;
import com.easyview.ebook.reader.engine.core.ERManager;
import com.easyview.ebook.reader.engine.core.IEngineService;

import android.content.Context;
import android.os.Environment;

//modify by zenki-zha-xxx begin
public abstract class Paths {
	static File mFile;

	static {
		IEngineService ies = (IEngineService) ERManager
				.getService(ERManager.ERENGINE_SERVICE);
		mFile = ies.getContext().getDir("cache", Context.MODE_WORLD_WRITEABLE);
	}

	public static String cardDirectory() {
		return Environment.getExternalStorageDirectory().getPath();
	}
	
	public static ZLStringOption BooksDirectoryOption() {
		return new ZLStringOption("Files", "BooksDirectory",
				mFile.getAbsolutePath() + "/Books");
	}

	public static ZLStringOption FontsDirectoryOption() {
		return new ZLStringOption("Files", "FontsDirectory",
				mFile.getAbsolutePath() + "/Fonts");
	}

	public static ZLStringOption WallpapersDirectoryOption() {
		return new ZLStringOption("Files", "WallpapersDirectory",
				mFile.getAbsolutePath() + "/Wallpapers");
	}

	public static String cacheDirectory() {
		return BooksDirectoryOption().getValue() + "/.Easyview";
	}

	public static String networkCacheDirectory() {
		return cacheDirectory() + "/cache";
	}
}
