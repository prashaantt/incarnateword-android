package util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Created by shaileshgaikwad on 8/19/15.
 */
public class Typefaces{

    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public static Typeface get(Context c, String name){
        synchronized(cache){
            if(!cache.containsKey(name)){
                try {
                    Typeface t = Typeface.createFromAsset(
                            c.getAssets(),
                            String.format("fonts/%s.ttf", name)
                    );
                    cache.put(name, t);
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
            return cache.get(name);
        }
    }

}
