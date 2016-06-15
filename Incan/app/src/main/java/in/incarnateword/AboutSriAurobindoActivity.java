package in.incarnateword;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.commonsware.cwac.anddown.AndDown;

import org.json.JSONObject;

import java.util.HashMap;

import util.AppController;
import util.Constant;
import util.FunctionLoder;
import util.Typefaces;
import util.Utils;

/**
 * Created by shaileshgaikwad on 6/24/15.
 */
public class AboutSriAurobindoActivity extends BaseActivity  {
    TextView txtHeading;
    WebView txtDescription;
    ProgressBar pb;
    String StrHeading, StrDescription;
    RelativeLayout AllView;
    AndDown converter;
    JsonObjectRequest jsonObjReq;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.about_sriaurobindo, frameLayout);
        setActionBarTitle(AboutSriAurobindoActivity.this, getString(R.string.Sri_Aurobindo), getSupportActionBar());
        pb= (ProgressBar) findViewById(R.id.progressBar1);
        txtHeading = (TextView) findViewById(R.id.txtheading);
        AllView= (RelativeLayout) findViewById(R.id.allview);
        AllView.setVisibility(View.INVISIBLE);
        txtDescription = (WebView) findViewById(R.id.txtdescript);
        converter = new AndDown();


        try {
            if (Typefaces.get(AboutSriAurobindoActivity.this, "Charlotte_Sans") != null) {
                txtHeading.setTypeface(Typefaces.get(AboutSriAurobindoActivity.this, "Charlotte_Sans"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }



        if (Utils.haveNetworkConnection(AboutSriAurobindoActivity.this)) {
            txtDescription.getSettings().setJavaScriptEnabled(true);
            txtDescription.loadUrl("file:///android_asset/marked-feature-footnotes/lib/MarkdownScript.html");
            makeJsonObjectRequest(Constant.ABOUT_SRI);

        } else {
            Toast.makeText(AboutSriAurobindoActivity.this, getResources().getString(R.string.InternetConnection), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        if(position != 1) {
            ChangeActivity(AboutSriAurobindoActivity.this, position);
        }
    }

    /**
     * call api for json
     */
    private void makeJsonObjectRequest(String url) {

        try {
            pb.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response != null) {
                        ParseJson(response.toString());
                        ShowData();
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
    public void ShowData(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    pb.setVisibility(View.GONE);
                    if ((StrHeading != null && !StrHeading.equals("")) && (StrDescription != null && !StrDescription.equals(""))) {
                        txtHeading.setText("" + StrHeading);
                        FunctionLoder functionLoder=new FunctionLoder(AboutSriAurobindoActivity.this);
                        functionLoder.ShowTxt(StrDescription,txtDescription,0,false);
                        AllView.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void ParseJson(String JsonResp) {
        try {
            JSONObject jsonObj = new JSONObject(JsonResp);
            JSONObject jsonObject = jsonObj.getJSONObject("author");
            if (jsonObject.has("desc")) {
                StrDescription = jsonObject.getString("desc");
            }
            if (jsonObject.has("dest")) {
                StrHeading = jsonObject.getString("dest");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void onBackPressed() {
        super.onBackPressed();
        AboutSriAurobindoActivity.this.finish();
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
