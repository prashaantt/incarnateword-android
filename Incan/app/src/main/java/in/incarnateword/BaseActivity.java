package in.incarnateword;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import SetterGetter.SearchContent;
import util.AppController;
import util.Constant;
import util.Typefaces;
import util.Utils;

/**
 * Created by shaileshgaikwad on 8/26/15.
 */

public class BaseActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener, AbsListView.OnScrollListener {
    //actionbar init
    public static Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    float mActionBarHeight;
    RelativeLayout profileBox;
    private static boolean isLaunch = true;
    protected FrameLayout frameLayout;
    protected static int position = 0;
    EditText SearchEdt;
    ListView listView;
    JsonObjectRequest jsonObjReq;
    Button btnClearList;
    ArrayList<SearchContent> list;
    String records, size;
    int start = 0;
    SearchResultAdapter adt;
    public ArrayList<SearchContent> arr;
    String query;
    RecyclerView DrawerRecycler;
    TextView TxtPageCount;
    //  AnimatedDotsView red;
    boolean WorkingState = false;
    // DrawerLayout drawerLayout;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baseactivity);
        TxtPageCount = (TextView) findViewById(R.id.txtpagecount);
        TxtPageCount.setTypeface(Typefaces.get(BaseActivity.this, "CharlotteSans_nn"));
        DrawerRecycler = (RecyclerView) findViewById(R.id.drawerList);
        btnClearList = (Button) findViewById(R.id.clear_list);
        btnClearList.setVisibility(View.INVISIBLE);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        profileBox = (RelativeLayout) findViewById(R.id.profileBox);
        SearchEdt = (EditText) findViewById(R.id.searchedittext);
        SearchEdt.setTypeface(Typefaces.get(BaseActivity.this, "CharlotteSans_nn"));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pb = (ProgressBar) findViewById(R.id.progressBar2);
//        pb.getIndeterminateDrawable().setColorFilter(
//                getResources().getColor(R.color.ProgressBarColor),
//                android.graphics.PorterDuff.Mode.SRC_IN);
        // drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setActionBarTitle(BaseActivity.this, getString(R.string.the_mother), getSupportActionBar());
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

// set width near about 80 % of screen width
        ViewGroup.LayoutParams params = drawerFragment.getView().getLayoutParams();
        params.width = AppController.DrawerWidth;
        drawerFragment.getView().setLayoutParams(params);


        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        mActionBarHeight = styledAttributes.getDimension(0, 0);

//        red = (AnimatedDotsView) findViewById(R.id.adv_2);
//        red.setVisibility(View.GONE);
        // red.startAnimation();
        // VisibleGoneDrawerList(true);

        listView = (ListView) findViewById(R.id.SearchlistView);
        listView.setOnScrollListener(this);

        profileBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    drawerFragment.CloseDra();
                    ChangeActivity(BaseActivity.this, 11);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //navigation drawer in %=done
        // scroll problem=resolved
        //remove quotes=done
        //if text not entered remove close icon if list not exits=done
        //in onresume show existing list if avilable=done
        //list below edittext=done
        //home in drawer=done
        //check internet connection=done

        //close time=done
        //preocessing dialog
        //image chapta=done
        //first close drawer then go to next activity=not done
        //change font of hint and edit text=done
        //change progress bar color
//search

        SearchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //close virtual keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(SearchEdt.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    //close drawer
                    //CloseDrawer();
                    if (SearchEdt.getText().toString() != null && !SearchEdt.getText().toString().equals("")) {
                        //call asynk tsk
                        if (Utils.haveNetworkConnection(BaseActivity.this)) {
                            SearchService(SearchEdt.getText().toString());
                        } else {
                            Toast.makeText(BaseActivity.this, getResources().getString(R.string.InternetConnection), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(BaseActivity.this, "Please enter text.", Toast.LENGTH_LONG).show();
                        // SearchEdt.setError("Please enter text.");
                    }
                    //call activity
                    //ChangeActivity(BaseActivity.this, 0);

                    handled = true;
                }
                return handled;
            }
        });

        SearchEdt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                try {
                    if (s.length() <= 0) {
                                /* hide keyboard */
                        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                        if (arr != null && arr.size() > 0) {

                            if (btnClearList.getVisibility() == View.INVISIBLE || btnClearList.getVisibility() == View.GONE) {
                                btnClearList.setVisibility(View.VISIBLE);
                            }

                        } else {
                            btnClearList.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        btnClearList.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        SearchEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OncliclOfEdittext();
            }
        });
        btnClearList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

//                        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
//                                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                    btnClearList.setVisibility(View.INVISIBLE);
                    SearchEdt.setText("");
                    TxtPageCount.setText("");
                    ClearData();
                    ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                            .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                    // listView.setBackgroundColor(Color.parseColor("#00000000"));
                    //VisibleGoneDrawerList(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //  drawerLayout.setDrawerListener(new RightMenuListener());
    }

    @Override
    public void CloseDrawerList(boolean Cl) {
        if (!Cl) {
            OpenChapter();
//            try {
//                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
//                        .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }

    public void SearchService(String Word) {
        try {
            query = URLEncoder.encode(Word, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (list != null && !list.isEmpty()) {
                list.clear();

            }
            if (arr != null && !arr.isEmpty()) {
                arr.clear();
            }
            records = "";
            size = "";
            start = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Toast.makeText(SearchList.this, "Called from searchlis", Toast.LENGTH_LONG).show();
        if (Utils.haveNetworkConnection(BaseActivity.this)) {
            makeJsonObjectRequest("http://incarnateword.in/search.json?q=" + query + "", false);
        } else {
            Toast.makeText(BaseActivity.this, getResources().getString(R.string.InternetConnection), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

    }

    public void CloseDrawer() {
        drawerFragment.CloseDra();
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


    public void OpenSearchChapter(final int position) {
        Thread background = new Thread() {
            public void run() {

                try {

                    sleep(1 * 250);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            SearchContent record = arr.get(position);
                            Intent openlink = new Intent(BaseActivity.this, LinkViewActivity.class);
                            openlink.putExtra("STRING", "file:///" + record.getUrl());
                            startActivity(openlink);
                            OpenChapter();

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        background.start();

    }


    public void ChangeActivity(final Context context, final int ActivityNo) {
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 1 seconds
                    sleep(1 * 300);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

//                            if (ActivityNo == 0) {
//                                if (SearchEdt.getText().toString() != null && !SearchEdt.getText().toString().equals("")) {
//                                    Intent i = new Intent(BaseActivity.this, SearchList.class);
//                                    i.putExtra("SEARCHTXT", SearchEdt.getText().toString());
//                                    startActivity(i);
//                                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    } else {
//                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    }
//                                    ((Activity) BaseActivity.this).finish();
//                                }
//                            }

                            if (ActivityNo == 11) {
                                Intent i = new Intent(context, IntroScreenActivity.class);
                                startActivity(i);
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                } else {
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                }
                                ((Activity) context).finish();
                                // context.this.finish();
                            }
                            if (ActivityNo == 1) {
                                Intent i = new Intent(context, AboutSriAurobindoActivity.class);
                                startActivity(i);
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                } else {
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                }
                                ((Activity) context).finish();
                                // BaseActivity.this.finish();
                            }
                            if (ActivityNo == 2) {
                                Intent i = new Intent(context, VolListSriAuorActivity.class);
                                i.putExtra("STRING", Constant.BIRTH_CEN_LIB);
                                startActivity(i);
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                } else {
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                }
                                ((Activity) context).finish();
                                // BaseActivity.this.finish();
                            }
                            if (ActivityNo == 3) {
                                Intent i = new Intent(context, VolListSriAuorActivity.class);
                                i.putExtra("STRING", Constant.COMP_WORK);
                                startActivity(i);
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                } else {
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                }
                                ((Activity) context).finish();
                                //BaseActivity.this.finish();
                            }
                            if (ActivityNo == 5) {
                                Intent i = new Intent(context, AboutMotherActivity.class);
                                startActivity(i);
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                } else {
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                }
                                ((Activity) context).finish();
                                //BaseActivity.this.finish();
                            }
                            if (ActivityNo == 6) {
                                Intent i = new Intent(context, VolListMotherActivity.class);
                                i.putExtra("STRING", Constant.MOTHER_COLLECTED_WORK);
                                startActivity(i);
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                } else {
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                }
                                ((Activity) context).finish();
                                // BaseActivity.this.finish();
                            }
                            if (ActivityNo == 7) {
                                Intent i = new Intent(context, VolListMotherActivity.class);
                                i.putExtra("STRING", Constant.MOTHER_AGENDA);
                                startActivity(i);
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                } else {
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                }
                                ((Activity) context).finish();
                                // BaseActivity.this.finish();
                            }
                            if (ActivityNo == 9) {
                                Intent i = new Intent(context, DictionaryActivity.class);
                                startActivity(i);
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                } else {
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                }
                                ((Activity) context).finish();
                                //BaseActivity.this.finish();
                            }


                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        background.start();

    }


    //adepter class here for listview

    public class SearchResultAdapter extends BaseAdapter {


        Context context;
        boolean Am;

        public SearchResultAdapter(Context con, ArrayList<SearchContent> arrS) {
            arr = arrS;
            this.context = con;

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return arr.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return arr.get(position);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        public class Holder {
            TextView txtSearH;
            TextView txtSearD;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holderobj = null;
            final LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final SearchContent record = arr.get(position);
            if (convertView == null) {
                convertView = (RelativeLayout) vi.inflate(R.layout.search_list_row, parent, false);
                holderobj = new Holder();
                holderobj.txtSearH = (TextView) convertView.findViewById(R.id.txtSearchHeading);
                holderobj.txtSearD = (TextView) convertView.findViewById(R.id.txtSearchDesc);
                holderobj.txtSearD.setTag(record.getUrl());
                try {
                    if (Typefaces.get(context, "CharlotteSans_nn") != null) {
                        holderobj.txtSearH.setTypeface(Typefaces.get(context, "CharlotteSans_nn"));
                        holderobj.txtSearD.setTypeface(Typefaces.get(context, "MonotypeSabon_nn"));
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                convertView.setTag(holderobj);
            } else {
                holderobj = (Holder) convertView.getTag();
            }
            try {
                holderobj.txtSearH.setText(record.getHeading());

                Spanned Htmltxt = Html.fromHtml("<font color=\"#555555\">" + record.getDesc() + " / " + "</font>");
                holderobj.txtSearD.setText(Html.fromHtml(record.getDesc()).toString().replaceAll("\\\\r|\\\\n", " "));


//                String html = record.getDesc();
//                html = html.replaceAll("<(.*?)\\>", " ");//Removes all items in brackets
//                html = html.replaceAll("<(.*?)\\\n", " ");//Must be undeneath
//                html = html.replaceFirst("(.*?)\\>", " ");//Removes any connected item to the last bracket
//                html = html.replaceAll("&nbsp;", " ");
//                html = html.replaceAll("&amp;", " ");
//                holderobj.txtSearD.setText(html);
            } catch (Exception e) {
                e.printStackTrace();
            }
//on click of list item
            holderobj.txtSearD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (Utils.haveNetworkConnection(BaseActivity.this)) {
                            drawerFragment.CloseDra();
                            OpenSearchChapter(position);
                        } else {
                            Toast.makeText(BaseActivity.this, getResources().getString(R.string.InternetConnection), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return convertView;
        }


    }

    /**
     * call api for json
     */
    private void makeJsonObjectRequest(String url, final boolean MoreData) {
        String Url = "";
        WorkingState = true;
        try {
//            red.setVisibility(View.VISIBLE);
//            red.startAnimation();
//            if(red.getVisibility()==View.INVISIBLE){
//                red.setVisibility(View.VISIBLE);
//            }
            //TxtPageCount.setText("Loading");
            if (btnClearList.getVisibility() == View.VISIBLE) {
                btnClearList.setVisibility(View.INVISIBLE);
            }
            pb.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        if (MoreData) {
            Url = url + (start + 10);

        } else {
            Url = url;
        }
        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {


                    if (response != null) {
                        list = jsonParsingSearch(response.toString());
                        if (list != null && !list.isEmpty()) {
                            //TxtNoResult.setVisibility(View.INVISIBLE);
                            if (MoreData) {
                                ShowDataForMore(list);

                            } else {
                                ShowDataSearch(list);

                            }
                            WorkingState = false;
                        } else {

                            if (!MoreData) {
                                Toast.makeText(BaseActivity.this, "No results found.", Toast.LENGTH_SHORT).show();
                                ClearData();
                            } else {
                                start = start - 10;
//                                if (red.getVisibility() == View.VISIBLE) {
//                                    red.stopAnimation();
//                                    red.setVisibility(View.GONE);
//
//                                }
                                if (pb.getVisibility() == View.VISIBLE) {
                                    pb.setVisibility(View.GONE);
                                }
                                if (btnClearList.getVisibility() == View.INVISIBLE || btnClearList.getVisibility() == View.GONE) {
                                    btnClearList.setVisibility(View.VISIBLE);
                                }
                                Toast.makeText(BaseActivity.this, "End of Results...", Toast.LENGTH_SHORT).show();
                            }
                            WorkingState = false;
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
                    WorkingState = false;
                    TxtPageCount.setText("");
                    if (pb.getVisibility() == View.VISIBLE) {
                        pb.setVisibility(View.GONE);
                    }
                    if (btnClearList.getVisibility() == View.INVISIBLE || btnClearList.getVisibility() == View.GONE) {
                        btnClearList.setVisibility(View.VISIBLE);
                    }
//                    if (red.getVisibility() == View.VISIBLE) {
//                        red.setVisibility(View.GONE);
//                        red.stopAnimation();
//                    }
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
    public ArrayList<SearchContent> jsonParsingSearch(String jsonString) {
        try {
            String highlights;
            ArrayList list = new ArrayList<SearchContent>();
            JSONObject jsonObj = new JSONObject(jsonString);
            JSONArray jsonarray = null;
            JSONObject jsonObject = jsonObj.getJSONObject("query");
            if (jsonObject.has("records")) {
                records = jsonObject.getString("records");
            }
            if (jsonObject.has("size")) {
                size = jsonObject.getString("size");
            }
            if (jsonObject.has("start")) {
                if (jsonObject.getString("start") != null && !jsonObject.getString("start").equals("") && !jsonObject.getString("start").equals("null")) {
                    start = Integer.parseInt(jsonObject.getString("start"));
                }
            }

            //Heading = (jsonObject.getString("records"));
            jsonarray = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonarray.length(); i++) {
                SearchContent searchContent = new SearchContent();
                JSONObject c = jsonarray.getJSONObject(i);

                //** take heading and url from source object**
                JSONObject source = c.getJSONObject("_source");
                if (source.has("t")) {
                    searchContent.setHeading(source.getString("t"));
                }
                if (source.has("url")) {
                    searchContent.setUrl(source.getString("url"));
                }
                if (!c.has("highlight")) {
                    if (source.has("txt") && !source.getString("txt").equals("null") && !source.getString("txt").equals("")) {
                        searchContent.setDesc(source.getString("txt"));
                    } else {
                        if (source.has("items")) {
                            JSONArray itemArray = source.getJSONArray("items");
                            for (int j = 0; j < itemArray.length(); j++) {
                                JSONObject TxtFromItem = itemArray.getJSONObject(j);
                                if (TxtFromItem.has("txt")) {
                                    searchContent.setDesc(TxtFromItem.getString("txt"));
                                    break;
                                }
                            }

                        }
                    }
                } else {
                    //** take description from highlight object**
                    JSONObject highlight = c.getJSONObject("highlight");
                    if (highlight.has("items.txt")) {

//                        JSONArray highlightTxtArray = highlight.getJSONArray("items.txt");
//                        for(int k=0;k<highlightTxtArray.length();k++){
//                            JSONObject itmsss=highlightTxtArray.getJSONObject(k);
//                            System.out.println("OP===="+itmsss.toString());
//                        }
//                        String descr = highlightTxtArray.toString();
//                        searchContent.setDesc(descr.replace("[", "").replace("]", ""));


                        String Desc = null;
                        JSONArray highlightTxtArray = highlight.getJSONArray("items.txt");
                        for (int k = 0; k < highlightTxtArray.length(); k++) {
                            highlightTxtArray.get(k);
                            if (k == 0) {
                                Desc = highlightTxtArray.get(k).toString();
                            } else {

                                Desc += "..." + highlightTxtArray.get(k).toString();
                            }
                        }
                        searchContent.setDesc(Desc);
                    } else {
                        if (highlight.has("txt")) {
                            String Desc = null;
                            JSONArray highlightTxtArray = highlight.getJSONArray("txt");
                            for (int k = 0; k < highlightTxtArray.length(); k++) {
                                highlightTxtArray.get(k);
                                if (k == 0) {
                                    Desc = highlightTxtArray.get(k).toString();
                                } else {

                                    Desc += "..." + highlightTxtArray.get(k).toString();
                                }
                            }
                            searchContent.setDesc(Desc);
//                            String descr = highlightTxtArray.toString();
//                            searchContent.setDesc(descr.replace("[", "").replace("]", ""));
                        } else {
                            if (highlight.has("segments.items.txt")) {
                                String Desc = null;
                                JSONArray highlightTxtArray = highlight.getJSONArray("segments.items.txt");
                                for (int k = 0; k < highlightTxtArray.length(); k++) {
                                    highlightTxtArray.get(k);
                                    if (k == 0) {
                                        Desc = highlightTxtArray.get(k).toString();
                                    } else {

                                        Desc += "..." + highlightTxtArray.get(k).toString();
                                    }
                                }
                                searchContent.setDesc(Desc);
                            }
                        }
                    }
                }
                // System.out.println("HEIGHLIGH====" + searchContent.getDesc());
                list.add(searchContent);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //new word search...
    public void ClearData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //  btnClearList.setVisibility(View.INVISIBLE);
                    if (list != null && !list.isEmpty()) {
                        list.clear();
                    }
                    if (arr != null && arr.isEmpty()) {
                        arr.clear();
                    }
                    TxtPageCount.setText("");
                    listView.setAdapter(null);
                    VisibleGoneDrawerList(true);
                    if (pb.getVisibility() == View.VISIBLE) {
                        pb.setVisibility(View.GONE);
                    }
                    // TxtPageCount.setText("No results found.");
//                    if (red.getVisibility() == View.VISIBLE) {
//                        red.stopAnimation();
//                        red.setVisibility(View.GONE);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void ShowDataSearch(final ArrayList<SearchContent> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //  btnClearList.setVisibility(View.VISIBLE);
                    //listView.setBackgroundColor(Color.parseColor("#ffffff"));
                    if (list != null && !list.isEmpty()) {
                        VisibleGoneDrawerList(false);
                        if (listView.getVisibility() == View.GONE) {

                            listView.setVisibility(View.VISIBLE);

                        }
                        adt = new SearchResultAdapter(BaseActivity.this, list);
                        listView.setAdapter(adt);
                        if (TxtPageCount.getVisibility() == View.INVISIBLE) {
                            TxtPageCount.setVisibility(View.VISIBLE);
                        }
                        if (pb.getVisibility() == View.VISIBLE) {
                            pb.setVisibility(View.GONE);

                            if (btnClearList.getVisibility() == View.INVISIBLE || btnClearList.getVisibility() == View.GONE) {
                                btnClearList.setVisibility(View.VISIBLE);
                            }
                        }
//                        if (red.getVisibility() == View.VISIBLE) {
//                            red.stopAnimation();
//                            red.setVisibility(View.GONE);
//
//                        }
                        TxtPageCount.setText(arr.size() + " of " + records + " results");
                    } else {
                        TxtPageCount.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void VisibleGoneDrawerList(final boolean VG) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (VG) {
                        listView.setAdapter(null);
                        if (list != null) {
                            list.clear();
                        }
                        if (arr != null) {
                            arr.clear();
                        }
                        if (listView.getVisibility() == View.VISIBLE) {
                            listView.setVisibility(View.GONE);

                        }
                        if (DrawerRecycler.getVisibility() == View.GONE || DrawerRecycler.getVisibility() == View.INVISIBLE) {
                            DrawerRecycler.invalidate();
                            DrawerRecycler.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (DrawerRecycler.getVisibility() == View.VISIBLE) {
                            DrawerRecycler.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void OpenChapter() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (listView.getVisibility() == View.VISIBLE) {
                        listView.setVisibility(View.GONE);
                        TxtPageCount.setVisibility(View.INVISIBLE);
                    }
                    if (DrawerRecycler.getVisibility() == View.GONE) {
                        DrawerRecycler.invalidate();
                        DrawerRecycler.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }
            }
        });

    }

    public void OncliclOfEdittext() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (arr != null && !arr.isEmpty() && arr.size() >= 1) {
                        if (listView.getVisibility() == View.INVISIBLE || listView.getVisibility() == View.GONE) {
                            listView.setVisibility(View.VISIBLE);
                            TxtPageCount.setVisibility(View.VISIBLE);
                        }
                        if (DrawerRecycler.getVisibility() == View.VISIBLE) {
                            //DrawerRecycler.invalidate();
                            DrawerRecycler.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }
            }
        });
    }

    public void ShowDataForMore(final ArrayList<SearchContent> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    arr.addAll(list);
                    listView.deferNotifyDataSetChanged();
                    //listView.invalidateViews();
                    listView.requestLayout();
                    int percentage = (start / 10);
                    if (TxtPageCount.getVisibility() == View.INVISIBLE) {
                        TxtPageCount.setVisibility(View.VISIBLE);


                    }
                    if (pb.getVisibility() == View.VISIBLE) {
                        pb.setVisibility(View.GONE);
                        if (btnClearList.getVisibility() == View.INVISIBLE || btnClearList.getVisibility() == View.GONE) {
                            btnClearList.setVisibility(View.VISIBLE);
                        }
                    }

//                    if (red.getVisibility() == View.VISIBLE) {
//                        red.stopAnimation();
//                        red.setVisibility(View.GONE);
//                    }
                    TxtPageCount.setText(arr.size() + " of " + records + " results");
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {


        try {
            if (arr != null && !arr.isEmpty() && arr.size() >= 10 && arr.size() < Integer.parseInt(records)) {
                if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1 && listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()) {
                    try {
                        if (!WorkingState) {
                            if (Utils.haveNetworkConnection(BaseActivity.this)) {
                                makeJsonObjectRequest("http://incarnateword.in/search.json?q=" + query + "&start=", true);
                                //System.out.println("END OF THE LIST now ");
                            } else {
                                Toast.makeText(BaseActivity.this, getResources().getString(R.string.InternetConnection), Toast.LENGTH_SHORT).show();
                            }

                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    //It is scrolled all the way down here
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }


    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
//        try {
//            listView.setAdapter(null);
//            list.clear();
//            list = null;
//            arr.clear();
//            arr=null;
//        } catch (Exception e) {
//
//        }
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
            listView.setAdapter(null);
            list.clear();
            list = null;
            arr.clear();
            arr = null;
        } catch (Exception e) {

        }
        try {
            if (jsonObjReq != null) {
                AppController.getInstance().getRequestQueue().cancelAll(jsonObjReq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //System.out.println("On resume of base activity.....");
    }
}
