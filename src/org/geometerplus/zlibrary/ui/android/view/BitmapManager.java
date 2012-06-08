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

package org.geometerplus.zlibrary.ui.android.view;

import android.graphics.Bitmap;
// add by zenki-zha-xxx
import android.graphics.Canvas;
import android.util.Log;

import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.view.ZLView;

public class BitmapManager {
	static private final String TAG = "BitmapManager";
	private final int SIZE = 2;
	private final Bitmap[] myBitmaps = new Bitmap[SIZE];
	private final ZLView.PageIndex[] myIndexes = new ZLView.PageIndex[SIZE];

	private int myWidth;
	private int myHeight;
	private Bitmap.Config mBitmapConfig = Bitmap.Config.RGB_565;

	// del by zenki-zha-xxx
	// private final ZLAndroidWidget myWidget;

	// modify by zenki-zha-xxx
	public BitmapManager() {
		// myWidget = widget;
	}

	// modify by zenki-zha-xxx
	public void setSize(int w, int h) {
		if (myWidth != w || myHeight != h) {
			myWidth = w;
			myHeight = h;
			for (int i = 0; i < SIZE; ++i) {
				myBitmaps[i] = null;
				myIndexes[i] = null;
			}
			System.gc();
			System.gc();
			System.gc();
		}
	}
	
	public void setBitmapConfig(Bitmap.Config config) {
		mBitmapConfig = config;
	}
	
	public Bitmap.Config getBitmapConfig() {
		return mBitmapConfig;
	}

	// modify by zenki-zha-xxx
	public Bitmap getBitmap(ZLView.PageIndex index, int width, int height) {
		// Log.e(TAG, "debug 1 getBitmap index == " + index);
		for (int i = 0; i < SIZE; ++i) {
			if (index == myIndexes[i]) {
				// Log.e(TAG, "debug 1 getBitmap == " + myBitmaps[i]);
				return myBitmaps[i];
			}
		}
		final int iIndex = getInternalIndex(index);

		myIndexes[iIndex] = index;
		if (myBitmaps[iIndex] == null) {
			try {
				myBitmaps[iIndex] = Bitmap.createBitmap(myWidth, myHeight, mBitmapConfig);
			} catch (OutOfMemoryError e) {
				System.gc();
				System.gc();
				myBitmaps[iIndex] = Bitmap.createBitmap(myWidth, myHeight, mBitmapConfig);
			}
		}

		// Log.e(TAG, "debug 3 getBitmap index == " + index);

		drawOnBitmap(myBitmaps[iIndex], index, width, height);

		// load next or previous page content
		// del by zenki-zha-xxx
		// final ZLView view = ZLApplication.Instance().getCurrentView();
		// view.onScrollingFinished(index);
		
		return myBitmaps[iIndex];
	}

	// add by zenki-zha-xxx
	// move from ZLAndroidWidget
	private void drawOnBitmap(Bitmap bitmap, ZLView.PageIndex index, int width,
			int height) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		// Log.e(TAG, "debug drawOnBitmap view == " + view);
		if (view == null) {
			return;
		}

		final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
				new Canvas(bitmap), width, height, 0);

		// Log.e(TAG, "debug drawOnBitmap ZLAndroidPaintContext == " + context);

		view.paint(context, index);
	}

	// modify by zenki-zha-xxx
	public int getInternalIndex(ZLView.PageIndex index) {
		for (int i = 0; i < SIZE; ++i) {
			if (myIndexes[i] == null) {
				return i;
			}
		}
		for (int i = 0; i < SIZE; ++i) {
			if (myIndexes[i] != ZLView.PageIndex.current) {
				return i;
			}
		}
		throw new RuntimeException("That's impossible");
	}

	// modify by zenki-zha-xxx
	public void reset() {
		for (int i = 0; i < SIZE; ++i) {
			myIndexes[i] = null;
		}
	}

	// modify by zenki-zha-xxx
	public void shift(boolean forward) {
		for (int i = 0; i < SIZE; ++i) {
			if (myIndexes[i] == null) {
				continue;
			}
			myIndexes[i] = forward ? myIndexes[i].getPrevious() : myIndexes[i]
					.getNext();
		}
	}
}
