package adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.incarnateword.ChapterListActivity;
import SetterGetter.BirthCentenaryList;
import in.incarnateword.R;
import util.Typefaces;


public class BirthCenVolAdapter extends BaseAdapter {

    private ArrayList<BirthCentenaryList> arr;
    Context context;
    boolean Am;

    public BirthCenVolAdapter(Context con, ArrayList<BirthCentenaryList> arr, boolean AM) {
        this.arr = arr;
        this.context = con;
        this.Am = AM;

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
        Holder holderobj = null;
        final LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final BirthCentenaryList record = arr.get(position);
        if (convertView == null) {
            convertView = (RelativeLayout) vi.inflate(R.layout.aurobindobirthce_list, parent, false);
            holderobj = new Holder();
            holderobj.volumeNames = (TextView) convertView.findViewById(R.id.volumename);
            holderobj.volumeNames.setTag(record.getUrl());
            try {
                if (Typefaces.get(context, "CharlotteSans_nn") != null) {
                    holderobj.volumeNames.setTypeface(Typefaces.get(context, "CharlotteSans_nn"));
                }else{
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            convertView.setTag(holderobj);
        } else {
            holderobj = (Holder) convertView.getTag();
        }
        try {
            Spanned Htmltxt = Html.fromHtml("<font color=\"#337AB7\">" + record.getVol() + " / " + "</font>" + "<font color=\"#555555\">" + record.getT() + "</font>");
            holderobj.volumeNames.setText(Htmltxt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holderobj.volumeNames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//                    BirthCentenaryList record = arr.get(position);
//                    MainActivity.ChangeFragment(record.getUrl().toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                try {
                    BirthCentenaryList record = arr.get(position);
                    Intent i = new Intent(view.getContext(), ChapterListActivity.class);
                    i.putExtra("STRING", record.getUrl().toString());
                    i.putExtra("CHAPTNAME", record.getT().toString());
                    i.putExtra("AM",Am);
                    view.getContext().startActivity(i);
                    //VolChapterFragment.ChangeFragment(record.getU().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return convertView;
    }


}

