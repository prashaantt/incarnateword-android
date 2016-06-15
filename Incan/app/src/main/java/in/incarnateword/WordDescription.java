package in.incarnateword;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.commonsware.cwac.anddown.AndDown;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import util.AppController;
import util.Constant;
import util.ContentItem;
import util.FunctionLoder;
import util.Typefaces;
import util.Utils;

/**
 * Created by shail on 7/2/15.
 */
public class WordDescription extends ActionBarActivity {
    WebView txtDescription;
    // Keep reference to the ShareActionProvider from the menu
    private ShareActionProvider mShareActionProvider;
    ProgressBar pb;
    String Word,WordForString;
    private final ArrayList<ContentItem> mItems = ChapterActivity.getSampleContent();
    public static String ShareLink = "http://incarnateword.in/";
    //actionbar init
    public static Toolbar mToolbar;
    float mActionBarHeight;
    JsonObjectRequest jsonObjReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worddescription);

        Bundle gt = getIntent().getExtras();
        try {
            Word = gt.getString("STRING");
            String chkSpace = gt.getString("STRINGFORURL");
            WordForString = chkSpace.trim().replaceAll(" ", "-");
        }catch (Exception e){
            e.printStackTrace();
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarTitle(WordDescription.this, Word, getSupportActionBar());


        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        mActionBarHeight = styledAttributes.getDimension(0, 0);

        txtDescription = (WebView) findViewById(R.id.txtdescript);
        pb = (ProgressBar) findViewById(R.id.progressBar1);

        this.txtDescription.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.clearCache(true);
            }
        });

        //txtHeading.setText("" + Word);
        if (Utils.haveNetworkConnection(WordDescription.this)) {
            if (WordForString != null && !WordForString.equals("")) {
                txtDescription.getSettings().setJavaScriptEnabled(true);
                txtDescription.loadUrl("file:///android_asset/marked-feature-footnotes/lib/MarkdownScript.html");
                makeJsonObjectRequest();
            }
        } else {
            Toast.makeText(WordDescription.this, getResources().getString(R.string.InternetConnection), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return false;
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu resource
        getMenuInflater().inflate(R.menu.main_menu_m, menu);

        //info
        MenuItem info = menu.findItem(R.id.action_info);
        info.setVisible(false);

        //search
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);

        // Retrieve the share menu item
        MenuItem shareItem = menu.findItem(R.id.menu_share);
        shareItem.setVisible(true);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        setShareIntent(1);

        return super.onCreateOptionsMenu(menu);

    }


    /**
     * call api for json
     */
    private void makeJsonObjectRequest() {

        try {
            pb.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        String url = "http://dictionary.incarnateword.in/" + WordForString + ".json";
        ShareLink = "http://dictionary.incarnateword.in/" + WordForString;

        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response != null) {
                        //  publishProgress(SUCCESS);
                        ShowData(jsonParsing(response.toString()));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.Volleyerror), Toast.LENGTH_SHORT).show();
                }
                //show data here

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.Volleyerror), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                try {
                    pb.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void ShowData(final String Description){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    pb.setVisibility(View.GONE);
                    if (Description != null && !Description.equals("")) {
                        FunctionLoder fx = new FunctionLoder(WordDescription.this);
                        fx.ShowTxt(Description, txtDescription, 0, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //parse search list responce
    public String jsonParsing(String jsonString) {
        try {
            String Description = null;
            JSONObject jsonObj = new JSONObject(jsonString);
            JSONObject jsonObject = jsonObj.getJSONObject("entry");
            Description = jsonObject.getString("definition");
            return Description;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (jsonObjReq != null) {
                AppController.getInstance().getRequestQueue().cancelAll(jsonObjReq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (jsonObjReq != null) {
                AppController.getInstance().getRequestQueue().cancelAll(jsonObjReq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setActionBarTitle(Context context, String title, ActionBar actionBar) {
        try {
            if (Typefaces.get(context, "CharlotteSans_nn") != null) {
                SpannableString SpanString = new SpannableString(title);
                SpanString.setSpan(new util.TypefaceSpan(context, Typeface.createFromAsset(getAssets(), "fonts/CharlotteSans_nn.ttf").toString()), 0, SpanString.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                actionBar.setTitle(SpanString);
            } else {
                actionBar.setTitle(title);
            }
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (jsonObjReq != null) {
                AppController.getInstance().getRequestQueue().cancelAll(jsonObjReq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        WordDescription.this.finish();
    }


    private void setShareIntent(int position) {
        // BEGIN_INCLUDE(update_sap)
        if (mShareActionProvider != null) {
            // Get the currently selected item, and retrieve it's share intent
            ContentItem item = mItems.get(position);
            Intent shareIntent = item.getShareIntent(WordDescription.this, WordDescription.ShareLink);

            // Now update the ShareActionProvider with the new share intent
            mShareActionProvider.setShareIntent(shareIntent);
        }
        // END_INCLUDE(update_sap)
    }
}
