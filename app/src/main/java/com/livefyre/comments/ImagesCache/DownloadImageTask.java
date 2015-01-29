package com.livefyre.comments.ImagesCache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	private int inSampleSize = 0;

	private String imageUrl;

	private BaseAdapter adapter;

	private ImagesCache cache;

	private Bitmap image = null;

	private ImageView ivImageView;

	public DownloadImageTask(BaseAdapter adapter) {
		this.adapter = adapter;

		this.cache = ImagesCache.getInstance();

	}

	public DownloadImageTask(ImagesCache cache, ImageView ivImageView) {
		this.cache = cache;

		this.ivImageView = ivImageView;

	}

	@Override
	protected Bitmap doInBackground(String... params) {
		imageUrl = params[0];

		return getImage(imageUrl);
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);

		if (result != null) {
			cache.addImageToWarehouse(imageUrl, result);

			if (ivImageView != null) {
				ivImageView.setImageBitmap(result);
			} else {

			}

			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
	}

	private Bitmap getImage(String imageUrl) {
		if (cache.getImageFromWarehouse(imageUrl) == null) {
			BitmapFactory.Options options = new BitmapFactory.Options();

			options.inJustDecodeBounds = true;

			options.inSampleSize = inSampleSize;

			try {
				URL url = new URL(imageUrl);

				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();

				InputStream stream = connection.getInputStream();

				image = BitmapFactory.decodeStream(stream, null, options);

				int imageWidth = options.outWidth;

				int imageHeight = options.outHeight;

				options.inJustDecodeBounds = false;

				connection = (HttpURLConnection) url.openConnection();

				stream = connection.getInputStream();

				image = BitmapFactory.decodeStream(stream, null, options);
				Log.d("image", image.toString());
				return image;
			}

			catch (Exception e) {
				Log.e("getImage", e.toString());
			}
		}

		return image;
	}

//	Transformation transformation = new RoundedTransformationBuilder()
//    .borderColor(Color.BLACK)
//    .borderWidthDp(3)
//    .cornerRadiusDp(30)
//    .oval(false)
//    .build();

}