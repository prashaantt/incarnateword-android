package in.incarnateword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
import util.Typefaces;

/**
 * Created by shailbgaikwad on 20/09/15.
 */
public class SearchList extends BaseActivity implements SearchView.OnQueryTextListener, AbsListView.OnScrollListener {

    private SearchView mSearchView;
    JsonObjectRequest jsonObjReq;
    ProgressBar pb;
    ArrayList<SearchContent> list;
    String records, size;
    int start = 0;
    SearchResultAdapter adt;
    public ArrayList<SearchContent> arr;
    String query;
    ListView listView;
    TextView TxtPageCount, TxtNoResult;
    String SearchTxtFromDrawer;
    EditText DrawerEdtTxt;
    private View footer;
    //http://www.survivingwithandroid.com/2013/10/android-listview-endless-adapter.html
    //http://www.mysamplecode.com/2012/07/android-listview-load-more-data.html
    //http://www.informit.com/articles/article.aspx?p=2066699&seqNum=4
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.search_list, frameLayout);
        DrawerEdtTxt = (EditText) findViewById(R.id.searchedittext);
        // DrawerEdtTxt.setVisibility(View.GONE);
        pb = (ProgressBar) findViewById(R.id.progressBar1);
        listView = (ListView) findViewById(R.id.SearchlistView);
        TxtNoResult = (TextView) findViewById(R.id.txtnoresult);
        TxtPageCount = (TextView) findViewById(R.id.txtpagecount);
        TxtPageCount.setVisibility(View.INVISIBLE);
        TxtPageCount.setTypeface(Typefaces.get(SearchList.this, "CharlotteSans_nn"));
        setActionBarTitle(SearchList.this, getString(R.string.search), getSupportActionBar());
        listView.setOnScrollListener(this);

        Bundle gt = getIntent().getExtras();
        try {
            SearchTxtFromDrawer = gt.getString("SEARCHTXT").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (SearchTxtFromDrawer.toString() != null && !SearchTxtFromDrawer.toString().equals("")) {
            SearchService(SearchTxtFromDrawer);
        }

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
                    CloseDrawer();
                    if (SearchEdt.getText().toString() != null && !SearchEdt.getText().toString().equals("")) {
                        SearchService(SearchEdt.getText().toString());
                    }
                    //call activity
                    //ChangeActivity(BaseActivity.this, 0);

                    handled = true;
                }
                return handled;
            }
        });
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        if (position == 0) {
            return;
        }

        ChangeActivity(SearchList.this, position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu resource
        getMenuInflater().inflate(R.menu.main_menu_m, menu);

        //info
        MenuItem info = menu.findItem(R.id.action_info);
        info.setVisible(false);

        //search
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(true);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(this);

        // Retrieve the share menu item
        MenuItem shareItem = menu.findItem(R.id.menu_share);
        shareItem.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void SearchService(String Word){
        try {
            query = URLEncoder.encode(Word, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (list != null && !list.isEmpty()) {
                list.clear();
            }
            records = "";
            size = "";
            start = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Toast.makeText(SearchList.this, "Called from searchlis", Toast.LENGTH_LONG).show();
        makeJsonObjectRequest("http://incarnateword.in/search.json?q=" + query + "", false);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_search:
                mSearchView.setIconified(false);
                return true;
            case R.id.action_info:
                return true;

        }

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String queryTxt) {
        SearchService(queryTxt);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /**
     * call api for json
     */
    private void makeJsonObjectRequest(String url, final boolean MoreData) {
        String Url = "";
        try {

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
        System.out.println("Generated url=== " + Url);
        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {


                    if (response != null) {
                        list = jsonParsing(response.toString());
                        if (list != null && !list.isEmpty()) {
                            TxtNoResult.setVisibility(View.INVISIBLE);
                            if (MoreData) {
                                ShowDataForMore(list);
                            } else {
                                ShowData(list);
                            }
                        } else {

                            if (!MoreData) {
                                ClearData();
                            } else {
                                start = start - 10;
                                pb.setVisibility(View.GONE);
                                Toast.makeText(SearchList.this, "End of Results...", Toast.LENGTH_SHORT).show();
                            }

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

    public void ClearData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    pb.setVisibility(View.GONE);
                    listView.setAdapter(null);
                    if (TxtPageCount.getVisibility() == View.VISIBLE) {
                        TxtPageCount.setVisibility(View.INVISIBLE);
                    }
                    TxtPageCount.setText("");
                    TxtNoResult.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void ShowData(final ArrayList<SearchContent> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    pb.setVisibility(View.GONE);
                    if (list != null && !list.isEmpty()) {
                        adt = new SearchResultAdapter(SearchList.this, list);
                        listView.setAdapter(adt);
                        int percentage = (start / 10);
                        if (TxtPageCount.getVisibility() == View.INVISIBLE) {
                            TxtPageCount.setVisibility(View.VISIBLE);
                        }
                        TxtPageCount.setText("Page " + percentage + " of " + records + " results");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void ShowDataForMore(final ArrayList<SearchContent> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    pb.setVisibility(View.GONE);
                    arr.addAll(list);
                    listView.deferNotifyDataSetChanged();
                    //listView.invalidateViews();
                    listView.requestLayout();
                    int percentage = (start / 10);
                    if (TxtPageCount.getVisibility() == View.INVISIBLE) {
                        TxtPageCount.setVisibility(View.VISIBLE);
                    }
                    TxtPageCount.setText("Page " + percentage + " of " + records + " results");
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }
            }
        });
    }


    //parse search list responce
    public ArrayList<SearchContent> jsonParsing(String jsonString) {
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
                    searchContent.setDesc(source.getString("txt"));
                } else {
                    //** take description from highlight object**
                    JSONObject highlight = c.getJSONObject("highlight");
                    if (highlight.has("items.txt")) {
                        JSONArray highlightTxtArray = highlight.getJSONArray("items.txt");
                        String descr = highlightTxtArray.toString();
                        searchContent.setDesc(descr.replace("[", "").replace("]", ""));

//                    for (int k = 0; k < highlightTxtArray.length(); k++) {
//                        JSONObject sssss=highlightTxtArray.getJSONObject(k);
////                        String sss= String.valueOf(highlightTxtArray.getJSONObject(k).getString(""));
////                        System.out.println("HEIGHLIGH====" + sss);
//
//                    }
                    } else {
                        if (highlight.has("txt")) {
                            JSONArray highlightTxtArray = highlight.getJSONArray("txt");
                            String descr = highlightTxtArray.toString();
                            searchContent.setDesc(descr.replace("[", "").replace("]", ""));
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

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {


        try {

            if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1 && listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()) {

                if (!list.isEmpty() && list != null && list.size() >= 10) {
                    try {

                        makeJsonObjectRequest("http://incarnateword.in/search.json?q=" + query + "&start=", true);
                        System.out.println("END OF THE LIST now ");

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
                //It is scrolled all the way down here

            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

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
                        holderobj.txtSearD.setTypeface(Typefaces.get(context, "CharlotteSans_nn"));
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
                holderobj.txtSearD.setText(Html.fromHtml(record.getDesc()).toString().replaceAll("\n\n|\n", "...."));


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
                    SearchContent record = arr.get(position);
                    Intent openlink = new Intent(SearchList.this, LinkViewActivity.class);
                    openlink.putExtra("STRING", "file:///" + record.getUrl());
                    startActivity(openlink);
                }
            });
            return convertView;
        }


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
//remove quotes
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
        SearchList.this.finish();
    }
}
