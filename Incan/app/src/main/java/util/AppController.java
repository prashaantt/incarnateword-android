package util;

import android.app.Application;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {
 
    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static AppController mInstance;
 public static int DrawerWidth=460;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        try {
            DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            DrawerWidth = width - 80;
        }catch (Exception e){
            DrawerWidth=460;
            e.printStackTrace();
        }
    }
 
    public static synchronized AppController getInstance() {
        return mInstance;
    }
 
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
 
        return mRequestQueue;
    }
 
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
 
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
 
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}