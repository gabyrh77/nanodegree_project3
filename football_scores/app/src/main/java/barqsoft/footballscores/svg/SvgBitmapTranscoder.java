package barqsoft.footballscores.svg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.SimpleResource;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.request.target.Target;
import com.caverock.androidsvg.SVG;

/**
 * Created by Gaby on 11/15/2015.
 */
public class SvgBitmapTranscoder implements ResourceTranscoder<SVG, Bitmap> {
    private BitmapPool bitmapPool;

    public SvgBitmapTranscoder(Context context) {
        this(Glide.get(context).getBitmapPool());
    }

    public SvgBitmapTranscoder(BitmapPool bitmapPool) {
        this.bitmapPool = bitmapPool;
    }

    @Override
    public Resource<Bitmap> transcode(Resource<SVG> toTranscode) {
        try {
            SVG svg = toTranscode.get();
            Picture picture = svg.renderToPicture();
            PictureDrawable drawable = new PictureDrawable(picture);
            Bitmap bitmap = bitmapPool.get(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawPicture(drawable.getPicture());
            return BitmapResource.obtain(bitmap, bitmapPool);
        }catch (Exception e){
            Log.d(SvgBitmapTranscoder.class.getName(), "Error on transcoder. " + e.getMessage());
            return null;
        }
    }

}
