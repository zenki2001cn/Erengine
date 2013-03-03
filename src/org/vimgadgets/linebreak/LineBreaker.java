package org.vimgadgets.linebreak;

import java.io.File;

import com.easyview.ebook.reader.engine.core.ERManager;
import com.easyview.ebook.reader.engine.core.IEngineService;
import com.easyview.ebook.reader.engine.util.EngineConfig;
import com.easyview.ebook.reader.engine.util.Logger;

public final class LineBreaker {
	static private final String TAG = "LineBreaker-v2";
	// modify by zenki-zha-xxx
	static {
		IEngineService ies = (IEngineService) ERManager
				.getService(ERManager.ERENGINE_SERVICE);

		String libPath = ies.getConfig().getLibraryPath();
		String soPath = libPath + File.separator + "libLineBreak-v2.so";

		try {
			if (libPath.equals(EngineConfig.DEFAULT_LIBRARY_LOCAL)) {
				System.loadLibrary("LineBreak-v2");
			} else {
				System.load(soPath);
			}
		} catch (Exception e) {
			Logger.eLog("EREngine", "load libLineBreak-v2.so = " + e.getMessage());
		}

		init();
	}

	public static final char MUSTBREAK = 0;
	public static final char ALLOWBREAK = 1;
	public static final char NOBREAK = 2;
	public static final char INSIDEACHAR = 3;

	private static native void init();
	private static native void setLineBreaksForCharArray(char[] data, int offset, int length, String lang, byte[] breaks);
	private static native void setLineBreaksForString(String data, String lang, byte[] breaks);

	private final String myLanguage;

	public LineBreaker(String lang) {
		myLanguage = lang;
	}

	public void setLineBreaks(char[] data, int offset, int length, byte[] breaks) {
		setLineBreaksForCharArray(data, offset, length, myLanguage, breaks);
	}

	public void setLineBreaks(String data, byte[] breaks) {
		setLineBreaksForString(data, myLanguage, breaks);
	}
}
