/*
 * Copyright (C) 2010-2011 Geometer Plus <contact@geometerplus.com>
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

package org.geometerplus.android.fbreader;

import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.net.Uri;

import org.geometerplus.zlibrary.text.view.*;

import org.geometerplus.fbreader.fbreader.FBAction;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.bookmodel.FBHyperlinkType;

// modify by zenki-zha-xxx
public class ProcessHyperlinkAction extends FBAction {
	private static final String ACTION_LINK_PREFIX = "fbreader-action://";

	public ProcessHyperlinkAction(FBReaderApp fbreader) {
		super(fbreader);
	}

	public boolean isEnabled() {
		return Reader.getTextView().getSelectedRegion() != null;
	}

	public void run() {
		final ZLTextRegion region = Reader.getTextView().getSelectedRegion();
		if (region == null) {
			return;
		}

		final ZLTextRegion.Soul soul = region.getSoul();
		if (soul instanceof ZLTextHyperlinkRegionSoul) {
			Reader.getTextView().hideSelectedRegionBorder();
//			Reader.getViewWidget().repaint();
			final ZLTextHyperlink hyperlink = ((ZLTextHyperlinkRegionSoul)soul).Hyperlink;
			switch (hyperlink.Type) {
				case FBHyperlinkType.EXTERNAL:
					if (hyperlink.Id.startsWith(ACTION_LINK_PREFIX)) {
						Reader.doAction(hyperlink.Id.substring(ACTION_LINK_PREFIX.length()));
					} else {
						// 打开外部链接，暂禁用
//						openInBrowser(hyperlink.Id);
					}
					break;
				case FBHyperlinkType.INTERNAL:
					Reader.Model.Book.markHyperlinkAsVisited(hyperlink.Id);
					Reader.tryOpenFootnote(hyperlink.Id);
					break;
			}
		} 
		// 处理图片
//		else if (soul instanceof ZLTextImageRegionSoul) {
//			Reader.getTextView().hideSelectedRegionBorder();
//			Reader.getViewWidget().repaint();
//			final String uriString = ((ZLTextImageRegionSoul)soul).ImageElement.URI;
//			if (uriString != null) {
//				try {
//					final Intent intent = new Intent();
//					intent.setClass(BaseActivity, ImageViewActivity.class);
//					intent.setData(Uri.parse(uriString));
//					intent.putExtra(
//						ImageViewActivity.BACKGROUND_COLOR_KEY,
//						Reader.ImageViewBackgroundOption.getValue().getIntValue()
//					);
//					BaseActivity.startActivity(intent);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		} 
		// 处理单词
//		else if (soul instanceof ZLTextWordRegionSoul) {
//			DictionaryUtil.openWordInDictionary(
//				BaseActivity, ((ZLTextWordRegionSoul)soul).Word, region
//			);
//		}
	}

	// 打开外部链接
//	private void openInBrowser(String urlString) {
//		final Intent intent = new Intent(Intent.ACTION_VIEW);
//		boolean externalUrl = true;
//		if (BookDownloader.acceptsUri(Uri.parse(urlString))) {
//			intent.setClass(BaseActivity, BookDownloader.class);
//			intent.putExtra(BookDownloaderService.SHOW_NOTIFICATIONS_KEY, BookDownloaderService.Notifications.ALL);
//			externalUrl = false;
//		}
//		final NetworkLibrary nLibrary = NetworkLibrary.Instance();
//		try {
//			nLibrary.initialize();
//		} catch (ZLNetworkException e) {
//		}
//		intent.setData(Uri.parse(NetworkLibrary.Instance().rewriteUrl(urlString, externalUrl)));
//		try {
//			BaseActivity.startActivity(intent);
//		} catch (ActivityNotFoundException e) {
//			// TODO: show an error message
//		}
//	}
}
