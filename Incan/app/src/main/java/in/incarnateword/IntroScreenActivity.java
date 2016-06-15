package in.incarnateword;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import util.Typefaces;

/**
 * Created by adityamathur on 19/06/15.
 */
public class IntroScreenActivity extends BaseActivity {
    TextView txtHeader, txtHeaderSub, txtFirstSta, txtSecondSta, txtThirdStan, txtFirsWrit, txtSecondWrit, txtThirdWrit;
    TextView txt4, txt8, txt11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.intoduction, frameLayout);
        setActionBarTitle(IntroScreenActivity.this, "The Incarnate Word", getSupportActionBar());
        txtHeader = (TextView) findViewById(R.id.textView);
        txtHeaderSub = (TextView) findViewById(R.id.textView2h);
        txtFirstSta = (TextView) findViewById(R.id.textView2);
        txtSecondSta = (TextView) findViewById(R.id.textView7);
        txtThirdStan = (TextView) findViewById(R.id.textView10);
        txt4 = (TextView) findViewById(R.id.textView4);
        txt8 = (TextView) findViewById(R.id.textView8);
        txt11 = (TextView) findViewById(R.id.textView11);

        txtFirsWrit = (TextView) findViewById(R.id.textView4);
        txtSecondWrit = (TextView) findViewById(R.id.textView8);
        txtThirdWrit = (TextView) findViewById(R.id.textView11);


        try {
            if (Typefaces.get(IntroScreenActivity.this, "Charlotte_Sans") != null) {
                txtHeader.setTypeface(Typefaces.get(IntroScreenActivity.this, "Charlotte_Sans"));
                txt4.setTypeface(Typefaces.get(IntroScreenActivity.this, "Charlotte_Sans"));
                txt8.setTypeface(Typefaces.get(IntroScreenActivity.this, "Charlotte_Sans"));
                txt11.setTypeface(Typefaces.get(IntroScreenActivity.this, "Charlotte_Sans"));
            }
            if (Typefaces.get(IntroScreenActivity.this, "Monotype_Sabon_Italic") != null) {
                txtHeaderSub.setTypeface(Typefaces.get(IntroScreenActivity.this, "Monotype_Sabon_Italic"));
            }
            if (Typefaces.get(IntroScreenActivity.this, "MonotypeSabon_nn") != null) {
                txtFirstSta.setTypeface(Typefaces.get(IntroScreenActivity.this, "MonotypeSabon_nn"));
                txtThirdStan.setTypeface(Typefaces.get(IntroScreenActivity.this, "MonotypeSabon_nn"));
                txtSecondSta.setTypeface(Typefaces.get(IntroScreenActivity.this, "MonotypeSabon_nn"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        profileBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CloseDrawer();
            }
        });
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        if(position == 0){
            return;
        }
        ChangeActivity(IntroScreenActivity.this, position);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        IntroScreenActivity.this.finish();
    }

}
