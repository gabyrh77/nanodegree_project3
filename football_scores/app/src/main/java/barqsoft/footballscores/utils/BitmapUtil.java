package barqsoft.footballscores.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.bumptech.glide.Glide;

/**
 * Created by Gaby on 11/19/2015.
 */
public class BitmapUtil {
    public static final String SVG_EXTENSION = ".svg";
    private LruCache<String, Bitmap> mMemoryCache;
    private Context mContext;

    public BitmapUtil(Context context){
        mContext = context;

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

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
