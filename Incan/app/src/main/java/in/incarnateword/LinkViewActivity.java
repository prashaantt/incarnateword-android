
package in.incarnateword;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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
import java.util.Date;

import SetterGetter.Chapter;
import observable.BaseActivity;
import util.AppController;
import util.FunctionLoder;
import util.Typefaces;
import util.Utils;

public class LinkViewActivity extends BaseActivity implements ObservableScrollViewCallbacks {
    FrameLayout NextPrevButton;
    String UrlString;
    //WebView WebChapter;
    ProgressBar pb;
    TextView ChapterDesc;
    public static FrameLayout ChaptDesc;
    FunctionLoder fx;
    public boolean ShowHideForShare = false;
    boolean ShowInfo = false;

    ObservableWebView WebChapter;
    // Animation
    ActionBar ab;
    JsonObjectRequest jsonObjReq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chapter_description);
        Bundle gt = getIntent().getExtras();
        UrlString = gt.getString("STRING");
        NextPrevButton = (FrameLayout) findViewById(R.id.NxtPrv);
        NextPrevButton.setVisibility(View.INVISIBLE);

        ChaptDesc = (FrameLayout) findViewById(R.id.chapterDesFram);
        //  WebChapter = (WebView) findViewById(R.id.webViewChapter);
        ChapterDesc = (TextView) findViewById(R.id.txtChaptDesc);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        ab = getSupportActionBar();
        try {
            if (Typefaces.get(LinkViewActivity.this, "CharlotteSans_nn") != null) {
                ChapterDesc.setTypeface(Typefaces.get(LinkViewActivity.this, "CharlotteSans_nn"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        System.out.println("Link==UrlString=="+UrlString);
        setActionBarTitle(LinkViewActivity.this, "", getSupportActionBar());
        WebChapter = (ObservableWebView) findViewById(R.id.webViewChapter);
        WebChapter.setScrollViewCallbacks(this);

        this.WebChapter.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                try {
                    String[] newUrl = url.split("///");
                    String[] FinalUrl = newUrl[1].split("#");
                    makeJsonObjectRequest(FinalUrl[0]);
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

        if (Utils.haveNetworkConnection(LinkViewActivity.this) && UrlString != null && !UrlString.equals("")) {
            WebChapter.getSettings().setJavaScriptEnabled(true);
            WebChapter.loadUrl("file:///android_asset/marked-feature-footnotes/lib/MarkdownScript.html");
            try {
                String[] newUrl = UrlString.split("///");
                String[] FinalUrl = newUrl[1].split("#");
                makeJsonObjectRequest(FinalUrl[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(LinkViewActivity.this, getResources().getString(R.string.InternetConnection), Toast.LENGTH_SHORT).show();
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
                ab.hide();
            }


        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();

            }
        }
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
        shareItem.setVisible(false);

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

    public void ShowData(final Chapter chapter){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String heading = "";
                WebChapter.scrollTo(0, 0);

                if (chapter.getText() != null && !chapter.getText().equals("")) {
                    try {
                        // scrollView.fullScroll(ScrollView.FOCUS_UP);
                        if (chapter.getT() != null) {
                            setActionBarTitle(LinkViewActivity.this, chapter.getT(), getSupportActionBar());
                            heading = "#" + chapter.getT().toString() + "\n\n";
                        }
                        String FinalString = heading + chapter.getText();
                        fx = new FunctionLoder(LinkViewActivity.this);
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

    public Chapter jsonParsing(String JsonString) {
        Chapter chapter = new Chapter();
        try {
            JSONObject jsonObj = new JSONObject(JsonString);
            JSONObject Chapter = jsonObj.getJSONObject("chapter");

            if (Chapter.has("desc")) {
                chapter.setDesc(Chapter.getString("desc"));
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
        LinkViewActivity.this.finish();
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

}
