
package in.incarnateword;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.commonsware.cwac.anddown.AndDown;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import SetterGetter.Chapter;
import adapter.BirthCenVolAdapter;
import observable.BaseActivity;
import util.AppController;
import util.ContentItem;
import util.FunctionLoder;
import util.Typefaces;
import util.Utils;

public class ChapterActivity extends BaseActivity implements ObservableScrollViewCallbacks, Animation.AnimationListener {
    FrameLayout NextPrevButton;
    String UrlString;
    //WebView WebChapter;
    ProgressBar pb;
    TextView ChapterDesc, BtnNext, BtnPrev;
    public static FrameLayout ChaptDesc;
    public static String ShareLink = "http://incarnateword.in/";
    FunctionLoder fx;
    public boolean ShowHideForShare = false;
    boolean ShowInfo = false;
    // Keep reference to the ShareActionProvider from the menu
    private ShareActionProvider mShareActionProvider;
    private final ArrayList<ContentItem> mItems = ChapterActivity.getSampleContent();
    ObservableWebView WebChapter;
    String ActionBarHeading, title;
    // Animation
    Animation animSlideUp, animSideDown;
    ActionBar ab;
    JsonObjectRequest jsonObjReq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chapter_description);
        Bundle gt = getIntent().getExtras();
        UrlString = gt.getString("STRING");
        ActionBarHeading = gt.getString("VolName");
        NextPrevButton = (FrameLayout) findViewById(R.id.NxtPrv);


        ChaptDesc = (FrameLayout) findViewById(R.id.chapterDesFram);
        //  WebChapter = (WebView) findViewById(R.id.webViewChapter);
        ChapterDesc = (TextView) findViewById(R.id.txtChaptDesc);

        BtnNext = (TextView) findViewById(R.id.btnnext);
        BtnPrev = (TextView) findViewById(R.id.btnprev);
        pb = (ProgressBar) findViewById(R.id.progressBar1);
        ab = getSupportActionBar();
        try {
            if (Typefaces.get(ChapterActivity.this, "CharlotteSans_nn") != null) {
                ChapterDesc.setTypeface(Typefaces.get(ChapterActivity.this, "CharlotteSans_nn"));
                BtnNext.setTypeface(Typefaces.get(ChapterActivity.this, "CharlotteSans_nn"));
                BtnPrev.setTypeface(Typefaces.get(ChapterActivity.this, "CharlotteSans_nn"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (ActionBarHeading != null && !ActionBarHeading.equals("")) {
            title = ActionBarHeading;
        } else {
            title = getString(R.string.app_name);
        }

        setActionBarTitle(ChapterActivity.this, title, getSupportActionBar());
        WebChapter = (ObservableWebView) findViewById(R.id.webViewChapter);
        WebChapter.setScrollViewCallbacks(this);

        // load the animation
        animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        animSideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        // set animation listener
        animSlideUp.setAnimationListener(this);
        animSideDown.setAnimationListener(this);

        //next button click listener
        BtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (Utils.haveNetworkConnection(ChapterActivity.this)) {
                        if (BtnNext.getTag() != null && !BtnNext.getTag().toString().equals("")) {
                            try {
                                if (jsonObjReq != null) {
                                    AppController.getInstance().getRequestQueue().cancelAll(jsonObjReq);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            UrlString = BtnNext.getTag().toString();
                            if (UrlString != null && !UrlString.equals("")) {
                               makeJsonObjectRequest(UrlString);
                            } else {

                            }

                        } else {
                            // Toast.makeText(ChapterActivity.this, getResources().getString(R.string.InternetConnection), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //previous button click listenerlistener
        BtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (Utils.haveNetworkConnection(ChapterActivity.this)) {
                        if (BtnPrev.getTag() != null && !BtnPrev.getTag().toString().equals("")) {
                            try {
                                if (jsonObjReq != null) {
                                    AppController.getInstance().getRequestQueue().cancelAll(jsonObjReq);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            UrlString = BtnPrev.getTag().toString();
                            if (UrlString != null && !UrlString.equals("")) {
                             makeJsonObjectRequest(UrlString);
                            } else {
                                //Toast.makeText(ChapterActivity.this, "Sorry Previous Chapter Not available...", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        //Toast.makeText(ChapterActivity.this, getResources().getString(R.string.InternetConnection), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.WebChapter.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    Intent openlink = new Intent(ChapterActivity.this, LinkViewActivity.class);
                    openlink.putExtra("STRING", url);
                    startActivity(openlink);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.clearCache(true);
            }
        });
        WebChapter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (ChaptDesc.getVisibility() == View.VISIBLE) {
                    ChaptDesc.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        if (Utils.haveNetworkConnection(ChapterActivity.this)) {
            WebChapter.getSettings().setJavaScriptEnabled(true);
            WebChapter.loadUrl("file:///android_asset/marked-feature-footnotes/lib/MarkdownScript.html");
         makeJsonObjectRequest(UrlString);
        } else {
            Toast.makeText(ChapterActivity.this, getResources().getString(R.string.InternetConnection), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

        if (ab == null) {
            return;
        }
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                try {
                    if (NextPrevButton.getVisibility() == View.VISIBLE) {
                        NextPrevButton.setVisibility(View.INVISIBLE);
                        NextPrevButton.startAnimation(animSlideUp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    NextPrevButton.clearAnimation();
                    NextPrevButton.setVisibility(View.INVISIBLE);
                }
            }


        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
                ShowView();


            }
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        try {
            if (animation == animSlideUp) {
                NextPrevButton.clearAnimation();
                if (ab.isShowing()) {
                    ab.hide();
                }
            }
            if (animation == animSideDown) {
                NextPrevButton.clearAnimation();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    //set status of connection using UI Thread
    public void ShowView() {
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 1 seconds
                    sleep(1 * 500);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                if (NextPrevButton.getVisibility() == View.INVISIBLE) {
                                    NextPrevButton.setVisibility(View.VISIBLE);
                                    NextPrevButton.startAnimation(animSideDown);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                NextPrevButton.clearAnimation();
                                NextPrevButton.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        background.start();
        // start thread

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu resource
        getMenuInflater().inflate(R.menu.main_menu_m, menu);

        //info
        MenuItem info = menu.findItem(R.id.action_info);
        info.setVisible(ShowInfo);

        //search
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);

        // Retrieve the share menu item
        MenuItem shareItem = menu.findItem(R.id.menu_share);
        shareItem.setVisible(ShowHideForShare);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        setShareIntent(1);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_search:
                //mSearchView.setIconified(false);
                return true;
            case R.id.action_info:
                ShowChaptDesc();
                return true;

        }

        return false;
    }

    private void setShareIntent(int position) {
        // BEGIN_INCLUDE(update_sap)
        if (mShareActionProvider != null) {
            // Get the currently selected item, and retrieve it's share intent
            ContentItem item = mItems.get(position);
            Intent shareIntent = item.getShareIntent(ChapterActivity.this, ChapterActivity.ShareLink);

            // Now update the ShareActionProvider with the new share intent
            mShareActionProvider.setShareIntent(shareIntent);
        }
        // END_INCLUDE(update_sap)
    }

    public static void ShowChaptDesc() {
        try {
            if (ChaptDesc.getVisibility() == View.VISIBLE) {
                ChaptDesc.setVisibility(View.INVISIBLE);
            } else {
                ChaptDesc.setVisibility(View.VISIBLE);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RefreshMenu(boolean Share, boolean info, boolean search) {
        ShowHideForShare = Share;
        ShowInfo = info;

        invalidateOptionsMenu();
    }




    /**
     * call api for json
     */
    private void makeJsonObjectRequest(String StrUrl) {

        try {
            pb.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        String url = "http://incarnateword.in/" + StrUrl + ".json";
        if (!StrUrl.substring(0, 1).equals("/")) {
            ShareLink = "http://incarnateword.in/" + StrUrl;
        } else {
            ShareLink = "http://incarnateword.in" + StrUrl;
        }
        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response != null) {
                        Chapter chapter;
                        chapter = jsonParsing(response.toString());
                        if(chapter!=null){
                            ShowData(chapter);
                        }
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

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return outputDate;

    }

    public void ShowData(final Chapter chapter){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String heading = "";
                WebChapter.scrollTo(0, 0);
                try {
                    if (BtnPrev.getTag() != null && !BtnPrev.getTag().toString().equals("")) {
                        BtnPrev.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        BtnPrev.setTextColor(getResources().getColor(R.color.disabletext));
                    }
                    if (BtnNext.getTag() != null && !BtnNext.getTag().toString().equals("")) {
                        BtnNext.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        BtnNext.setTextColor(getResources().getColor(R.color.disabletext));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (chapter.getText() != null && !chapter.getText().equals("")) {
                    try {
                        // scrollView.fullScroll(ScrollView.FOCUS_UP);
                        if (chapter.getT() != null) {
                            heading = "#" + chapter.getT().toString() + "\n\n";
                        }
                        String FinalString = heading + chapter.getText();
                        fx = new FunctionLoder(ChapterActivity.this);
                        fx.ShowTxt(FinalString, WebChapter, 0, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {

                        if (chapter.getDesc() != null && !chapter.getDesc().equals("")) {
                            String markdown = null;
                            AndDown converter = new AndDown();
                            RefreshMenu(true, true, false);
                            if (chapter.getDate() != null && !chapter.getDate().equals("")) {
                                markdown = converter.markdownToHtml(chapter.getDesc());
                                ChapterDesc.setText(Html.fromHtml(markdown) + "\n" + formateDateFromstring("yyyy-MM-dd", "d MMMM yyyy", chapter.getDate().toString()));
                            } else {
                                markdown = converter.markdownToHtml(chapter.getDesc());
                                ChapterDesc.setText("" + Html.fromHtml(markdown));
                            }
                        } else {
                            if (chapter.getDate() != null && !chapter.getDate().equals("")) {
                                RefreshMenu(true, true, false);
                                ChapterDesc.setText("" + formateDateFromstring("yyyy-MM-dd", "d MMMM yyyy", chapter.getDate().toString()));
                            } else {
                                RefreshMenu(true, false, false);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                pb.setVisibility(View.GONE);
            }
        });

    }
    public Chapter jsonParsing(String JsonString) {
        Chapter chapter = new Chapter();
        try {
            JSONObject jsonObj = new JSONObject(JsonString);
            JSONObject Chapter = jsonObj.getJSONObject("chapter");

            if (Chapter.has("desc")) {
                chapter.setDesc(Chapter.getString("desc"));
            }
            if (Chapter.has("nxtu")) {
                chapter.setNxtu(Chapter.getString("nxtu"));
                BtnNext.setTag("" + Chapter.getString("nxtu"));
            } else {
                BtnNext.setTag("");
            }
            if (Chapter.has("prvu")) {
                chapter.setPrevu(Chapter.getString("prvu"));
                BtnPrev.setTag("" + Chapter.get("prvu"));
            } else {
                BtnPrev.setTag("");
            }
            if (Chapter.has("dt")) {
                chapter.setDate(Chapter.getString("dt"));
            }
            if (Chapter.has("t")) {
                chapter.setT(Chapter.getString("t"));
            }
            if (Chapter.has("url")) {
                chapter.setUrl(Chapter.getString("url"));
            }
            if (Chapter.has("yre")) {
                chapter.setYre(Chapter.getString("yre"));
            }
            if (Chapter.has("yrs")) {
                chapter.setYrs(Chapter.getString("yrs"));
            }
            if (Chapter.has("text")) {
                chapter.setText(Chapter.getString("text"));
            }
            JSONArray path = Chapter.getJSONArray("path");
            String appendPath = "";
            for (int i = 0; i < path.length(); i++) {
                JSONObject pat = path.getJSONObject(i);
                if (pat.has("t")) {
                    if (!appendPath.equals("")) {
                        appendPath = appendPath + "/" + pat.getString("t");
                    } else {
                        appendPath = appendPath + pat.getString("t");
                    }
                }
            }
            chapter.setPath(appendPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return chapter;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ChapterActivity.this.finish();
    }

    @Override
    protected void onPause() {
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
    protected void onStop() {
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
    protected void onDestroy() {
        super.onDestroy();
        //you may call the cancel() method but if it is not handled in doInBackground() method
        try {
            if (jsonObjReq != null) {
                AppController.getInstance().getRequestQueue().cancelAll(jsonObjReq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    static ArrayList<ContentItem> getSampleContent() {
        ArrayList<ContentItem> items = new ArrayList<ContentItem>();

        items.add(new ContentItem(ContentItem.CONTENT_TYPE_TEXT, "http://incarnateword.in/"));
        items.add(new ContentItem(ContentItem.CONTENT_TYPE_TEXT, "http://incarnateword.in/"));
        items.add(new ContentItem(ContentItem.CONTENT_TYPE_TEXT, "http://incarnateword.in/"));

        return items;
    }
}
