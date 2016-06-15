package util;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.util.LruCache;

/**
 * Created by shaileshgaikwad on 8/13/15.
 */
public class TypefaceSpan extends MetricAffectingSpan {
    /**
     * An <code>LruCache</code> for previously loaded typefaces.
     */


    private LruCache<String, Typeface> sTypefaceCache =
            new LruCache<String, Typeface>(12);

    private Typeface mTypeface;
    public Context context;

    /**
     * Load the {@link Typeface} and apply to a {@link android.text.Spannable}.
     */
    public TypefaceSpan(Context context, String typefaceName) {
        mTypeface = sTypefaceCache.get(typefaceName);
        this.context = context;
        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(this.context.getApplicationContext().getAssets(), "fonts/CharlotteSans_nn.ttf");

            // Cache the loaded Typeface
            sTypefaceCache.put(typefaceName, mTypeface);
        }
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTypeface(mTypeface);

        // Note: This flag is required for proper typeface rendering
        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTypeface(mTypeface);

        // Note: This flag is required for proper typeface rendering
        tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }
}
