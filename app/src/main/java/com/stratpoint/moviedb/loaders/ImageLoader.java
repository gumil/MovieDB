package com.stratpoint.moviedb.loaders;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoader {

    private static final String TAG = "ImageLoader";
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    private final Resources mResources;
    private Activity mActivity;
    private ImageCache mImageCache;

    public ImageLoader(Activity activity) {
        mActivity = activity;
        mResources = mActivity.getResources();
        mImageCache = ImageCache.getInstance(mActivity.getFragmentManager());
    }

    public void loadBitmap(String url, ImageView imageView) {
        final String imageKey = String.valueOf(url);

        final BitmapDrawable bitmap = mImageCache.getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageDrawable(bitmap);
        } else if (cancelPotentialWork(url, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mActivity.getResources(), null, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(url);
        }
    }

    private boolean cancelPotentialWork(String data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.data;
            if (bitmapData.trim().isEmpty() || bitmapData.equals(data)) {
                bitmapWorkerTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap decodeSampledBitmapFromStream(InputStream stream, InputStream streamCopy,
                                                 int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        addInBitmapOptions(options);

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeStream(streamCopy, null, options);
    }

    private void addInBitmapOptions(BitmapFactory.Options options) {
        options.inMutable = true;

        if (mImageCache != null) {
            Bitmap inBitmap = mImageCache.getBitmapFromReusableSet(options);

            if (inBitmap != null) {
                options.inBitmap = inBitmap;
            }
        }
    }

    private class BitmapWorkerTask extends AsyncTask<String, Void, BitmapDrawable> {
        private final WeakReference<ImageView> imageViewReference;
        private String data = "";

        public BitmapWorkerTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected BitmapDrawable doInBackground(String... params) {
            data = params[0];
            final ImageView imageView = imageViewReference.get();
            Bitmap bitmap = downloadUrlToStream(data, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
            BitmapDrawable bitmapDrawable = new BitmapDrawable(mResources, bitmap);
            mImageCache.addBitmapToMemoryCache(data, bitmapDrawable);
            return bitmapDrawable;
        }

        @Override
        protected void onPostExecute(BitmapDrawable bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    setImageDrawable(imageView, bitmap);
                }
            }
        }

        private Bitmap downloadUrlToStream(String urlString, int reqWidth, int reqHeight) {
            HttpURLConnection urlConnection = null;
            BufferedInputStream in = null;
            InputStream in2 = null;

            try {
                final URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.connect();
                in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                copy(in, out);
                in2 = new ByteArrayInputStream(out.toByteArray());
                return decodeSampledBitmapFromStream(in, in2, reqWidth, reqHeight);
            } catch (final IOException e) {
                Log.e(TAG, "Error in downloadBitmap - " + e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (in2 != null) {
                        in2.close();
                    }
                } catch (final IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
            return null;
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[IO_BUFFER_SIZE];
        int count = 0;
        int n;

        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    private void setImageDrawable(ImageView imageView, Drawable drawable) {
        final TransitionDrawable td =
                new TransitionDrawable(new Drawable[]{
                        new ColorDrawable(android.R.color.transparent),
                        drawable
                });

        imageView.setImageDrawable(td);
        td.startTransition(200);
    }
}
