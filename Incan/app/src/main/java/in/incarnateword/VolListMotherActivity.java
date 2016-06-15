package in.incarnateword;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
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

import SetterGetter.BirthCentenaryList;
import adapter.BirthCenVolAdapter;
import util.AppController;
import util.Constant;
import util.Typefaces;
import util.Utils;

/**
 * Created by shail on 09/07/15.
 */
public class VolListMotherActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    String  Heading;
    ProgressBar pb;
    ArrayList<BirthCentenaryList> list;
    // ListView birthCenList;
    BirthCenVolAdapter adt;
    String SMsg;
    TextView VolHeader;
//observable List

    private View mImageView;
    private View mListBackgroundView;
    private ObservableListView mListView;
    private int mParallaxImageHeight;
    JsonObjectRequest jsonObjReq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.mothervollistfragment, frameLayout);
        Bundle gt = getIntent().getExtras();
        SMsg = gt.getString("STRING");


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if((SMsg!=null && !SMsg.equals("")) && SMsg.equals(Constant.MOTHER_AGENDA)) {
            setActionBarTitle(VolListMotherActivity.this, getString(R.string.agenda), getSupportActionBar());
        }else{
            setActionBarTitle(VolListMotherActivity.this, getString(R.string.collection_work), getSupportActionBar());
        }

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        VolHeader = (TextView) findViewById(R.id.txtvolheader);

        if (Typefaces.get(VolListMotherActivity.this, "CharlotteSans_nn") != null) {
            VolHeader.setTypeface(Typefaces.get(VolListMotherActivity.this, "CharlotteSans_nn"));
        }

        //obs list
        mImageView = findViewById(R.id.viewtoscrooll);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        mListView = (ObservableListView) findViewById(R.id.list);
        mListView.setScrollViewCallbacks(this);
        // Set padding view for ListView. This is the flexible space.
        View paddingView = new View(VolListMotherActivity.this);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, mParallaxImageHeight);
        paddingView.setLayoutParams(lp);

        // This is required to disable header's list selector effect
        paddingView.setClickable(true);
        mListView.addHeaderView(paddingView);
        // mListBackgroundView makes ListView's background except header view.
        mListBackgroundView = findViewById(R.id.list_background);


        //check internet connection then call Service
        if (Utils.haveNetworkConnection(VolListMotherActivity.this)) {
            if (SMsg != null && !SMsg.equals("")) {
                makeJsonObjectRequest();
            }
        } else {
            Toast.makeText(VolListMotherActivity.this, getResources().getString(R.string.InternetConnection), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        if(SMsg.equals(Constant.MOTHER_COLLECTED_WORK) && position ==6){
            return;
        }
        if(SMsg.equals(Constant.MOTHER_AGENDA) && position == 7){
            return;
        }

        ChangeActivity(VolListMotherActivity.this,position);
    }



    /**
     * call api for json
     * */
    private void makeJsonObjectRequest() {

        try {
            pb.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

         jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                SMsg, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    if(response!=null) {
                        list = jsonParsing(response.toString());
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

    //parse search list responce
    public ArrayList<BirthCentenaryList> jsonParsing(String jsonString) {
        try {
            ArrayList list = new ArrayList<BirthCentenaryList>();
            JSONObject jsonObj = new JSONObject(jsonString);
            JSONArray jsonFeed = null;
            JSONObject jsonObject = jsonObj.getJSONObject("compilation");
            Heading = (jsonObject.getString("cmpn"));
            jsonFeed = jsonObject.getJSONArray("vols");
            for (int i = 0; i < jsonFeed.length(); i++) {
                JSONObject c = jsonFeed.getJSONObject(i);
                BirthCentenaryList fd = new BirthCentenaryList();
                fd.setT(c.getString("t"));
                fd.setUrl(c.getString("url"));
                fd.setVol(c.getString("vol"));
                list.add(fd);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void ShowData(final ArrayList<BirthCentenaryList> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    pb.setVisibility(View.GONE);
                    if (list != null && !list.isEmpty()) {
                        try {
                            VolHeader.setText("");
                            adt = new BirthCenVolAdapter(VolListMotherActivity.this, list, false);
                            mListView.setAdapter(adt);

                            if (Heading != null && !Heading.equals("")) {
                                setActionBarTitle(VolListMotherActivity.this, Heading, getSupportActionBar());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //you may call the cancel() method but if it is not handled in doInBackground() method
        try {
            if(jsonObjReq!=null) {
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
            if(jsonObjReq!=null) {
                AppController.getInstance().getRequestQueue().cancelAll(jsonObjReq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //observable listview methods
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
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        VolListMotherActivity.this.finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(jsonObjReq!=null) {
                AppController.getInstance().getRequestQueue().cancelAll(jsonObjReq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
