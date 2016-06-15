package in.incarnateword;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import SetterGetter.BirthCenVolChapter;
import SetterGetter.BirthCentenaryList;
import adapter.BirthCentChaptAdapter;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter;
import util.AppController;
import util.Typefaces;
import util.Utils;

/**
 * Created by shail on 09/07/15.
 */
public class ChapterListActivity extends ActionBarActivity implements ObservableScrollViewCallbacks, Animation.AnimationListener {

    ProgressBar pb;
    ArrayList<BirthCenVolChapter> list;
    // ListView birthCenList;
    private ObservableListView listView;
    BirthCentChaptAdapter adt;
    JsonObjectRequest jsonObjReq;
    TextView btnPrevVol, btnNextVol, txtVol, txtVolName, txtVolDetail;
    static String Smsg;
    String Svol, SvolDetal, Sparent;
    String SvolName = "";
    ArrayList<String> mHeaderNames;
    ArrayList<Integer> mHeaderPositions;
    ArrayList<Integer> mSubHeaderPosition;
    ArrayList<Integer> mSubSubHeaderPosition;
    private ArrayList<SimpleSectionedListAdapter.Section> sections;
    //new imp
    private View mImageView;
    private View mListBackgroundView;
    private int mParallaxImageHeight;
    FrameLayout NextPrevButton;

    //actionbar init
    public static Toolbar mToolbar;
    public static Activity activity;
    float mActionBarHeight;
    boolean Am;
    // Animation
    Animation animSlideUp, animSideDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birthcenvolchapterfragment_layout);

        Bundle gt = getIntent().getExtras();
        Smsg = gt.getString("STRING");
        Am = gt.getBoolean("AM");
        mImageView = findViewById(R.id.viewtoscrooll);
        if (Am) {
            mImageView.setBackgroundResource(R.drawable.example1);
        } else {
            mImageView.setBackgroundResource(R.drawable.example);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (gt.getString("CHAPTNAME") != null && !gt.getString("CHAPTNAME").equals("")) {
            setActionBarTitle(ChapterListActivity.this, gt.getString("CHAPTNAME"), getSupportActionBar());
        }

        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        mActionBarHeight = styledAttributes.getDimension(0, 0);


        //other things
        listView = (ObservableListView) findViewById(R.id.list);
        listView.setScrollViewCallbacks(this);
        mImageView = findViewById(R.id.viewtoscrooll);
        pb = (ProgressBar) findViewById(R.id.progressBar1);
        btnPrevVol = (TextView) findViewById(R.id.btnprev);
        btnNextVol = (TextView) findViewById(R.id.btnnext);
        txtVol = (TextView) findViewById(R.id.txtvol);
        txtVolName = (TextView) findViewById(R.id.txtvolname);
        txtVolDetail = (TextView) findViewById(R.id.txtvoldetial);
        NextPrevButton = (FrameLayout) findViewById(R.id.NxtPrv);

        try {
            if (Typefaces.get(ChapterListActivity.this, "CharlotteSans_nn") != null) {
                txtVolName.setTypeface(Typefaces.get(ChapterListActivity.this, "CharlotteSans_nn"));
                txtVol.setTypeface(Typefaces.get(ChapterListActivity.this, "CharlotteSans_nn"));
                btnPrevVol.setTypeface(Typefaces.get(ChapterListActivity.this, "CharlotteSans_nn"));
                btnNextVol.setTypeface(Typefaces.get(ChapterListActivity.this, "CharlotteSans_nn"));
            }

            if (Typefaces.get(ChapterListActivity.this, "Charlotte_Sans_Italic") != null) {
                txtVolDetail.setTypeface(Typefaces.get(ChapterListActivity.this, "Charlotte_Sans_Italic"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // load the animation
        animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        animSideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        // set animation listener
        animSlideUp.setAnimationListener(this);
        animSideDown.setAnimationListener(this);

        mListBackgroundView = findViewById(R.id.list_background);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);


        btnNextVol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (jsonObjReq != null) {
                        AppController.getInstance().getRequestQueue().cancelAll(jsonObjReq);
                    }
                    if (Utils.haveNetworkConnection(ChapterListActivity.this)) {
                        if (btnNextVol.getTag() != null && !btnNextVol.getTag().toString().equals("")) {
                            Smsg = btnNextVol.getTag().toString();
                            if (Smsg != null && !Smsg.equals("")) {
                                makeJsonObjectRequest(Smsg);
                            } else {

                            }
                        }
                    } else {
                        // Toast.makeText(ChapterListActivity.this, getResources().getString(R.string.InternetConnection), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        btnPrevVol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (jsonObjReq != null) {
                        AppController.getInstance().getRequestQueue().cancelAll(jsonObjReq);
                    }
                    if (Utils.haveNetworkConnection(ChapterListActivity.this)) {
                        if (btnPrevVol.getTag() != null && !btnPrevVol.getTag().toString().equals("")) {
                            Smsg = btnPrevVol.getTag().toString();
                            if (Smsg != null && !Smsg.equals("")) {
                                makeJsonObjectRequest(Smsg);
                            } else {

                            }
                        }
                    } else {
                        //Toast.makeText(ChapterListActivity.this, getResources().getString(R.string.InternetConnection), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //obs init
        // Set padding view for ListView. This is the flexible space.
        View paddingView = new View(ChapterListActivity.this);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, mParallaxImageHeight);
        paddingView.setLayoutParams(lp);
        // This is required to disable header's list selector effect
        paddingView.setClickable(true);

        listView.addHeaderView(paddingView);
        // mListBackgroundView makes ListView's background except header view.

        //call asynk tsk
        if (Utils.haveNetworkConnection(ChapterListActivity.this)) {
            makeJsonObjectRequest(Smsg);
        } else {
            Toast.makeText(ChapterListActivity.this, getResources().getString(R.string.InternetConnection), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // check for zoom in animation
        try {
            if (animation == animSlideUp) {
                NextPrevButton.clearAnimation();
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

    /**
     * call api for json
     */
    private void makeJsonObjectRequest(String UrlStr) {

        try {
            pb.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        String url = "http://incarnateword.in/" + UrlStr + ".json";
        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response != null) {
                        list = ParseJsonResponce(response.toString());
                        if(list!=null && !list.isEmpty()){
                            ShowData(list);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.Volleyerror), Toast.LENGTH_SHORT).show();

                }

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

    public void ShowData(final ArrayList<BirthCenVolChapter> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pb.setVisibility(View.GONE);
                try {
                    if (btnPrevVol.getTag() != null && !btnPrevVol.getTag().toString().equals("")) {
                        btnPrevVol.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        btnPrevVol.setTextColor(getResources().getColor(R.color.disabletext));
                    }
                    if (btnNextVol.getTag() != null && !btnNextVol.getTag().toString().equals("")) {
                        btnNextVol.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        btnNextVol.setTextColor(getResources().getColor(R.color.disabletext));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (list != null && !list.isEmpty()) {
                    try {

                        if (Sparent != null && !Sparent.equals("")) {
                            txtVol.setText("" + Sparent);
                        } else {
                            txtVol.setVisibility(View.INVISIBLE);
                        }
                        if (Svol != null && !Svol.equals("")) {

                            txtVolName.setVisibility(View.VISIBLE);
                            txtVolName.setText("Volume " + Svol);

//                        txtVol.setVisibility(View.VISIBLE);
//                        txtVol.setText("Volume " + Svol);
                        } else {
                            txtVolName.setVisibility(View.INVISIBLE);
                            //txtVol.setVisibility(View.INVISIBLE);
                        }
                        if (SvolName != null && !SvolName.equals("")) {
                            //txtVolName.setVisibility(View.VISIBLE);
                            //txtVolName.setText("" + SvolName);
                            setActionBarTitle(ChapterListActivity.this, SvolName, getSupportActionBar());
                            // ((MainActivity) getActivity()).setActionBarTitle(SvolName);
                        } else {
                            // txtVolName.setVisibility(View.INVISIBLE);
                        }
                        if (SvolDetal != null && !SvolDetal.equals("")) {
                            txtVolDetail.setVisibility(View.VISIBLE);
                            txtVolDetail.setText("" + SvolDetal);
                        } else {
                            txtVolDetail.setVisibility(View.INVISIBLE);
                        }

                        adt = new BirthCentChaptAdapter(ChapterListActivity.this, list, Smsg, SvolName, mSubHeaderPosition, mSubSubHeaderPosition);
                        for (int i = 0; i < mHeaderPositions.size(); i++) {
                            sections.add(new SimpleSectionedListAdapter.Section(mHeaderPositions.get(i), mHeaderNames.get(i)));
                        }
                        SimpleSectionedListAdapter simpleSectionedGridAdapter = new SimpleSectionedListAdapter(ChapterListActivity.this, adt, R.layout.list_item_header, R.id.header);
                        simpleSectionedGridAdapter.setSections(sections.toArray(new SimpleSectionedListAdapter.Section[0]));
                        listView.setAdapter(simpleSectionedGridAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        //System.out.println("HERE ALSO CALLABLE*********************");
        super.onResume();
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
        //you may call the cancel() method but if it is not handled in doInBackground() method
        try {
            if (jsonObjReq != null) {
                AppController.getInstance().getRequestQueue().cancelAll(jsonObjReq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<BirthCenVolChapter> ParseJsonResponce(String txtJson) {

        try {
            sections = new ArrayList<SimpleSectionedListAdapter.Section>();
            mHeaderNames = new ArrayList<String>();
            mHeaderPositions = new ArrayList<Integer>();
            mSubHeaderPosition = new ArrayList<Integer>();
            mSubSubHeaderPosition = new ArrayList<Integer>();
            //json contents
            ArrayList list = new ArrayList<BirthCentenaryList>();
            JSONObject jsonObj = new JSONObject(txtJson);
            JSONArray chapter = null;
            JSONObject jsonObject = jsonObj.getJSONObject("volume");
            //titles under volume
            if (jsonObject.has("t")) {
                SvolName = jsonObject.getString("t");
            } else {
                SvolName = "";
            }
            if (jsonObject.has("subt")) {
                SvolDetal = jsonObject.getString("subt");
            } else {
                SvolDetal = "";
            }
            if (jsonObject.has("cmpn")) {
                Sparent = jsonObject.getString("cmpn");
            } else {
                Sparent = "";
            }

            if (jsonObject.has("nxtu")) {
                btnNextVol.setTag("" + jsonObject.getString("nxtu"));

            } else {
                btnNextVol.setTag("");
            }
            if (jsonObject.has("prvu")) {
                btnPrevVol.setTag("" + jsonObject.getString("prvu"));

            } else {
                btnPrevVol.setTag("");
            }
            if (jsonObject.has("vol")) {
                Svol = jsonObject.getString("vol");
            } else {
                Svol = "";
            }
            JSONObject jsontoc = jsonObject.getJSONObject("toc");

//            SetterGetter.BirthCenVolChapter chapt = new SetterGetter.BirthCenVolChapter();
//            chapt.setChapt("Perseus the Deliverer");
//            list.add(chapt);
            //if has book array first
            if (jsontoc.has("books")) {
                // System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% BOOKS %%%%%%%%%%%%%%%%%%%%%");
                JSONArray BookArray = jsontoc.getJSONArray("books");
                for (int i = 0; i < BookArray.length(); i++) {
                    JSONObject BookObject = BookArray.getJSONObject(i);
                    //if book has parts
                    if (BookObject.has("parts")) {
                        JSONArray BookPartArray = BookObject.getJSONArray("parts");
                        for (int j = 0; j < BookPartArray.length(); j++) {
                            JSONObject BookPartObject = BookPartArray.getJSONObject(j);
                            //add header array here
                            if (BookPartObject.has("partt")) {
                                if (!BookPartObject.getString("partt").equals("")) {
                                    mHeaderPositions.add(list.size());
                                    mHeaderNames.add(BookPartObject.getString("partt"));
                                } else {
                                    if (BookPartObject.has("part")) {
                                        if (!BookPartObject.getString("part").equals("")) {
                                            mHeaderPositions.add(list.size());
                                            mHeaderNames.add(BookPartObject.getString("part"));
                                        }
                                    }
                                }
                            }
                            if (BookPartObject.has("sections")) {
                                JSONArray BookPartSectionArray = BookPartObject.getJSONArray("sections");
                                for (int k = 0; k < BookPartSectionArray.length(); k++) {
                                    JSONObject SectionObject = BookPartSectionArray.getJSONObject(k);
                                    //add sub header
                                    if (SectionObject.has("sect")) {
                                        SetterGetter.BirthCenVolChapter chapt = new SetterGetter.BirthCenVolChapter();
                                        chapt.setChapt(SectionObject.get("sect").toString());
                                        mSubHeaderPosition.add(list.size());
                                        list.add(chapt);

                                    }

                                    if (SectionObject.has("subsections")) {
                                        JSONArray Subsection = SectionObject.getJSONArray("subsections");
                                        for (int l = 0; l < Subsection.length(); l++) {
                                            JSONObject SubSectionObject = Subsection.getJSONObject(l);

                                            //add sub header
                                            if (SubSectionObject.has("subst")) {
                                                SetterGetter.BirthCenVolChapter chapt = new SetterGetter.BirthCenVolChapter();
                                                chapt.setChapt(SubSectionObject.get("subst").toString());
                                                mSubSubHeaderPosition.add(list.size());
                                                list.add(chapt);

                                            }
                                            if (SubSectionObject.has("chapters")) {
                                                JSONArray ChapterArray = SubSectionObject.getJSONArray("chapters");
                                                for (int m = 0; m < ChapterArray.length(); m++) {
                                                    JSONObject ch = ChapterArray.getJSONObject(m);
                                                    SetterGetter.BirthCenVolChapter chapt = new SetterGetter.BirthCenVolChapter();
                                                    chapt.setChapt(ch.getString("chapt"));
                                                    chapt.setU(ch.getString("u"));
                                                    //   System.out.println(ch.getString("chapt"));
                                                    list.add(chapt);
                                                }
                                            }
                                        }

                                    } else {
                                        //chapter
                                        if (SectionObject.has("chapters")) {
                                            JSONArray ChapterArray = SectionObject.getJSONArray("chapters");
                                            for (int m = 0; m < ChapterArray.length(); m++) {
                                                JSONObject ch = ChapterArray.getJSONObject(m);
                                                SetterGetter.BirthCenVolChapter chapt = new SetterGetter.BirthCenVolChapter();
                                                chapt.setChapt(ch.getString("chapt"));
                                                chapt.setU(ch.getString("u"));
                                                //System.out.println(ch.getString("chapt"));
                                                list.add(chapt);
                                            }
                                        }

                                    }
                                }
                            } else {
                                //chapter
                                if (BookPartObject.has("chapters")) {
                                    JSONArray ChapterArray = BookPartObject.getJSONArray("chapters");
                                    for (int m = 0; m < ChapterArray.length(); m++) {
                                        JSONObject ch = ChapterArray.getJSONObject(m);
                                        SetterGetter.BirthCenVolChapter chapt = new SetterGetter.BirthCenVolChapter();
                                        chapt.setChapt(ch.getString("chapt"));
                                        chapt.setU(ch.getString("u"));
                                        //System.out.println(ch.getString("chapt"));
                                        list.add(chapt);
                                    }
                                }
                            }

                        }
                    } else {
                        //chapter
                        if (BookObject.has("chapters")) {
                            JSONArray ChapterArray = BookObject.getJSONArray("chapters");
                            for (int m = 0; m < ChapterArray.length(); m++) {
                                JSONObject ch = ChapterArray.getJSONObject(m);
                                SetterGetter.BirthCenVolChapter chapt = new SetterGetter.BirthCenVolChapter();
                                chapt.setChapt(ch.getString("chapt"));
                                chapt.setU(ch.getString("u"));
                                //System.out.println(ch.getString("chapt"));
                                list.add(chapt);
                            }
                        }
                    }
                }
            }


            //if volumn has parts array direct
            if (jsontoc.has("parts")) {
                // System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PARTS %%%%%%%%%%%%%%%%%%%%%");
                JSONArray BookPartArray = jsontoc.getJSONArray("parts");
                for (int j = 0; j < BookPartArray.length(); j++) {
                    JSONObject BookPartObject = BookPartArray.getJSONObject(j);
                    //add header array here
                    if (BookPartObject.has("partt")) {
                        if (!BookPartObject.getString("partt").equals("")) {
                            mHeaderPositions.add(list.size());
                            mHeaderNames.add(BookPartObject.getString("partt"));
                        } else {
                            if (BookPartObject.has("part")) {
                                if (!BookPartObject.getString("part").equals("")) {
                                    mHeaderPositions.add(list.size());
                                    mHeaderNames.add(BookPartObject.getString("part"));
                                }
                            }
                        }
                    }
                    if (BookPartObject.has("sections")) {
                        JSONArray BookPartSectionArray = BookPartObject.getJSONArray("sections");
                        for (int k = 0; k < BookPartSectionArray.length(); k++) {
                            JSONObject SectionObject = BookPartSectionArray.getJSONObject(k);
                            //sub header
                            if (SectionObject.has("sect")) {
                                SetterGetter.BirthCenVolChapter chapt = new SetterGetter.BirthCenVolChapter();
                                chapt.setChapt(SectionObject.get("sect").toString());
                                mSubHeaderPosition.add(list.size());
                                list.add(chapt);

                            }
                            if (SectionObject.has("subsections")) {
                                JSONArray Subsection = SectionObject.getJSONArray("subsections");
                                for (int l = 0; l < Subsection.length(); l++) {
                                    JSONObject SubSectionObject = Subsection.getJSONObject(l);
                                    //add sub header
                                    if (SubSectionObject.has("subst")) {
                                        SetterGetter.BirthCenVolChapter chapt = new SetterGetter.BirthCenVolChapter();
                                        chapt.setChapt(SubSectionObject.get("subst").toString());
                                        mSubSubHeaderPosition.add(list.size());
                                        list.add(chapt);

                                    }
                                    if (SubSectionObject.has("chapters")) {
                                        JSONArray ChapterArray = SubSectionObject.getJSONArray("chapters");
                                        for (int m = 0; m < ChapterArray.length(); m++) {
                                            JSONObject ch = ChapterArray.getJSONObject(m);
                                            SetterGetter.BirthCenVolChapter chapt = new SetterGetter.BirthCenVolChapter();
                                            chapt.setChapt(ch.getString("chapt"));
                                            chapt.setU(ch.getString("u"));
                                            // System.out.println(ch.getString("chapt"));
                                            list.add(chapt);
                                        }
                                    }
                                }

                            } else {
                                //chapter
                                if (SectionObject.has("chapters")) {
                                    JSONArray ChapterArray = SectionObject.getJSONArray("chapters");
                                    for (int m = 0; m < ChapterArray.length(); m++) {
                                        JSONObject ch = ChapterArray.getJSONObject(m);
                                        SetterGetter.BirthCenVolChapter chapt = new SetterGetter.BirthCenVolChapter();
                                        chapt.setChapt(ch.getString("chapt"));
                                        chapt.setU(ch.getString("u"));
                                        //System.out.println(ch.getString("chapt"));
                                        list.add(chapt);
                                    }
                                }

                            }
                        }
                    } else {
                        //chapter
                        if (BookPartObject.has("chapters")) {
                            JSONArray ChapterArray = BookPartObject.getJSONArray("chapters");
                            for (int m = 0; m < ChapterArray.length(); m++) {
                                JSONObject ch = ChapterArray.getJSONObject(m);
                                SetterGetter.BirthCenVolChapter chapt = new SetterGetter.BirthCenVolChapter();
                                chapt.setChapt(ch.getString("chapt"));
                                chapt.setU(ch.getString("u"));
                                // System.out.println(ch.getString("chapt"));
                                list.add(chapt);
                            }
                        }
                    }

                }
            }
            //if direct chatpter array
            if (jsontoc.has("chapters")) {
                if (jsontoc.has("chapters")) {
                    JSONArray ChapterArray = jsontoc.getJSONArray("chapters");
                    for (int m = 0; m < ChapterArray.length(); m++) {
                        JSONObject ch = ChapterArray.getJSONObject(m);
                        SetterGetter.BirthCenVolChapter chapt = new SetterGetter.BirthCenVolChapter();
                        chapt.setChapt(ch.getString("chapt"));
                        chapt.setU(ch.getString("u"));
                        list.add(chapt);
                    }
                }
            }

            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //obs list view methods
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.primary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mImageView, -scrollY / 2);

        // Translate list background
        ViewHelper.setTranslationY(mListBackgroundView, Math.max(0, -scrollY + mParallaxImageHeight));
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

        if (scrollState == ScrollState.UP) {
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
            // NextPrevButton.setVisibility(View.INVISIBLE);

        } else if (scrollState == ScrollState.DOWN) {
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
            // NextPrevButton.setVisibility(View.VISIBLE);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ChapterListActivity.this.finish();
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
}
