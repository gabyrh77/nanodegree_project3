package barqsoft.footballscores.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.LruCache;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Gaby on 11/19/2015.
 */
public class BitmapUtil {
    private static final String SVG_EXTENSION = ".svg";
    private LruCache<String, Bitmap> mMemoryCache;
    private Context mContext;
    private BitmapPool bitmapPool;
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";
    private static final int DISK_CACHE_INDEX = 0;

    public BitmapUtil(Context context){
        mContext = context;
        bitmapPool = Glide.get(context).getBitmapPool();
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        // Initialize disk cache on background thread
        File cacheDir = getDiskCacheDir(DISK_CACHE_SUBDIR);
        new InitDiskCacheTask().execute(cacheDir);
    }

    class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                try {
                    mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(), 1, DISK_CACHE_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mDiskCacheStarting = false; // Finished initialization
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }
    }

    public Bitmap getBitmapFromUrl(String url){
        Bitmap bitmap = getBitmapFromMemCache(url);
        if (bitmap != null) {
            return bitmap;
        } else {
            try {
                if (url.toLowerCase().endsWith(SVG_EXTENSION)) {
                    bitmap = Glide.with(mContext).as(Bitmap.class).load(url).into(100, 100).get();
                } else {
                    bitmap = Glide.with(mContext).asBitmap().load(url).into(100, 100).get();
                }
            }catch (Exception e){
                return null;
            }

            addBitmapToMemoryCache(url, bitmap);
            return bitmap;
        }
    }

    public Bitmap getBitmapFromPictureDrawable(PictureDrawable picture, String name)
    {
        Bitmap bitmap = getBitmapFromMemCache(name);
        if (bitmap != null) {
            return bitmap;
        } else {
            // create the bitmap
            bitmap = bitmapPool.get(picture.getIntrinsicWidth(), picture.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            // Create canvas manipulator
            Canvas canvas = new Canvas(bitmap);

            // Draws the image
            canvas.drawPicture(picture.getPicture());

            // cache and return
            addBitmapToMemoryCache(name, bitmap);
            return bitmap;
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public Bitmap getBitmapFromDiskCache(String key) {
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        synchronized (mDiskCacheLock) {
            // Wait while disk cache is started from background thread
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {}
            }
            if (mDiskLruCache != null) {
                DiskLruCache.Snapshot snapshot;
                try {
                    snapshot = mDiskLruCache.get(key);
                    if (snapshot != null) {
                        fileInputStream =
                                (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
                        fileDescriptor = fileInputStream.getFD();
                    }
                } catch (IOException e) {
                   // Log.e(TAG, "processBitmap - " + e);
                }
            }
        }
        Bitmap bitmap = null;
     /*   if (fileDescriptor != null) {
            bitmap = decodeSampledBitmapFromDescriptor(fileDescriptor, mImageWidth,
                    mImageHeight, getImageCache());
        }
        if (fileInputStream != null) {
            try {
                fileInputStream.close();
            } catch (IOException e) {}
        }*/
        return bitmap;
    }

    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
// but if not mounted, falls back on internal storage.
    public File getDiskCacheDir(String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Environment.isExternalStorageRemovable() ? mContext.getExternalCacheDir().getPath() :
                        mContext.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    public int getAppVersion() {
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

}
