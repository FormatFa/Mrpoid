package com.edroid.common.utils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.widget.ImageView;

/**
 * 远程图片加载器
 * 
 * @author Yichou
 * 
 */
public class UrlBitmapLoader {
	public static interface IBitmapHolder {
		public void setBitmap(Bitmap bitmap);
	}
	
	private static int size = 0;
	
	/**
	 * 设置图标大小，跟屏幕密度无关的大小，0 则不进行缩放
	 * 
	 * @param width
	 */
	public static void setIconSize(int width) {
		size = width;
	}
	
	public static Bitmap fitDpi(Resources res, Bitmap src){
		if(size == 0)
			return src;
		
		DisplayMetrics display = res.getDisplayMetrics();
		int srcW = src.getWidth();
		int dstW = (int)(display.densityDpi*size/240.0);
		
		if(srcW != dstW)
		{ //缩放位图
			Bitmap tmpBitmap = Bitmap.createScaledBitmap(src, dstW, dstW, true);
			src.recycle();
			return tmpBitmap;
		}
		
		return src;
	}
	
	static class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private IBitmapHolder holder;
		private Resources res;

		
		public BitmapWorkerTask(ImageView imageView, IBitmapHolder holder) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			imageViewReference = new WeakReference<ImageView>(imageView);
			this.holder = holder;
			this.res = imageView.getResources();
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(String... params) {
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(new URL(params[0]).openStream());
				return fitDpi(res, bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			holder.setBitmap(bitmap); // 保存 bitmap 对象

			if (isCancelled()) {
				bitmap = null;
			}

			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
				if (this == bitmapWorkerTask && imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

	static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}

		return null;
	}

	/**
	 * 取消异步加载
	 * 
	 * @param url
	 * @param imageView
	 * @return
	 */
	public static boolean cancelPotentialWork(ImageView imageView, IBitmapHolder holder) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			if (holder != bitmapWorkerTask.holder) {
				// Cancel previous task
				bitmapWorkerTask.cancel(true);
			} else {
				// The same work is already in progress
				return false;
			}
		}

		// No task associated with the ImageView, or an existing task was
		// cancelled
		return true;
	}

	/**
	 * 异步加载 Bitmap
	 * 
	 * @param res
	 * @param imageView
	 * @param url
	 * @param placeHolderBitmap
	 */
	public static void loadBitmap(Context context, ImageView imageView, String url, IBitmapHolder holder) {
		if (cancelPotentialWork(imageView, holder)) {
			final BitmapWorkerTask task = new BitmapWorkerTask(imageView, holder);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), null, task);
			imageView.setImageDrawable(asyncDrawable);
			task.execute(url);
		}
	}
}
