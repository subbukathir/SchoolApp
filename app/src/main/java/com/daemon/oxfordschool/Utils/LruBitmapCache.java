/* @(#)LruBitmapCache.java}
 *
 * Copyright (c) 2015 The Android Open Source Project
 *
 * This software is the confidential and proprietary information of
 * Daemon Software and Services Pvt, Ltd. You shall not disclose such
 * Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into
 * with Daemon Software and Services.
 */
package com.daemon.oxfordschool.Utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * @author  Samuel Stephen
 * <b>Date: </b>
 * 		  24-Apr-2015
 * <b>Module: </b>
 * 		  EREMIT
 */
public class LruBitmapCache extends LruCache<String, Bitmap> implements
        ImageCache {
	public static int getDefaultLruCacheSize() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;

		return cacheSize;
	}

	public LruBitmapCache() {
		this(getDefaultLruCacheSize());
	}

	public LruBitmapCache(int sizeInKiloBytes) {
		super(sizeInKiloBytes);
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight() / 1024;
	}

	@Override
	public Bitmap getBitmap(String url) {
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}
}