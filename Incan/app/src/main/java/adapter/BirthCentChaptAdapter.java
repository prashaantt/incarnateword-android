package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import SetterGetter.BirthCenVolChapter;
import in.incarnateword.ChapterActivity;
import in.incarnateword.R;
import util.Typefaces;


public class BirthCentChaptAdapter extends BaseAdapter {
    private ArrayList<BirthCenVolChapter> arr;
    private ArrayList<Integer> SubTitlePosition;
    private ArrayList<Integer> SubSubTitlePosition;
    Context context;
    Holder holderobj;
    String VolSmsg;
    String VolName;

    public BirthCentChaptAdapter(Context con, ArrayList<BirthCenVolChapter> arr, String Smsg, String VolName, ArrayList<Integer> SubTitlePosition ,ArrayList<Integer> SubSubTitlePosition) {
        this.VolName = VolName;
        this.VolSmsg = Smsg;
        this.arr = arr;
        this.context = con;
        this.SubTitlePosition = SubTitlePosition;
        this.SubSubTitlePosition=SubSubTitlePosition;

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
        TextView volumeNames;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        holderobj = new Holder();

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final BirthCenVolChapter record = arr.get(position);
        if (convertView == null) {
            convertView = (RelativeLayout) vi.inflate(R.layout.aurobindobirthce_list, parent, false);
            holderobj.volumeNames = (TextView) convertView.findViewById(R.id.volumename);
            holderobj.volumeNames.setTag("" + record.getU());
            try {
                if (Typefaces.get(context, "CharlotteSans_nn") != null) {
                    holderobj.volumeNames.setTypeface(Typefaces.get(context, "CharlotteSans_nn"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            convertView.setTag(holderobj);
        } else {
            holderobj = (Holder) convertView.getTag();
        }

//        Spanned Htmltxt = Html.fromHtml("<font color=\"#337AB7\">" + record.getChapt() + "</font>");
//        holderobj.volumeNames.setText("" + Htmltxt);
        try {
            if (SubTitlePosition.contains(position)) {
                holderobj.volumeNames.setBackgroundColor(Color.parseColor("#C4CFD4"));
                holderobj.volumeNames.setTextColor(Color.parseColor("#000000"));

            }
            if (SubSubTitlePosition.contains(position)) {
                holderobj.volumeNames.setBackgroundColor(Color.parseColor("#eceff1"));
                holderobj.volumeNames.setTextColor(Color.parseColor("#000000"));

            }
            if(!SubTitlePosition.contains(position) && !SubSubTitlePosition.contains(position)) {
                holderobj.volumeNames.setBackgroundColor(Color.WHITE);
                holderobj.volumeNames.setTextColor(Color.parseColor("#555555"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            holderobj.volumeNames.setText("" + record.getChapt());
        } catch (Exception e) {
            e.printStackTrace();
        }
        holderobj.volumeNames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BirthCenVolChapter record = arr.get(position);
                    if (record.getU() != null && !record.getU().equals("")) {
                        Intent i = new Intent(view.getContext(), ChapterActivity.class);
                        i.putExtra("STRING", VolSmsg + "/" + record.getU().toString());
                        i.putExtra("VolName", VolName);
                        view.getContext().startActivity(i);
                    }
                    //VolChapterFragment.ChangeFragment(record.getU().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return convertView;
    }


}

