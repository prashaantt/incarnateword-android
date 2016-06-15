package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.incarnateword.R;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import util.Typefaces;

public class StickyDictiAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer, Filterable {

    private final Context mContext;
    private ArrayList<String> mData;
    private int[] mSectionIndices;
    private Character[] mSectionLetters;
    private LayoutInflater mInflater;


    List<String> mOriginalValues; // Original Values

    public StickyDictiAdapter(Context context, ArrayList<String> arrayList) {
        mContext = context;
        this.mData = arrayList;
        mInflater = LayoutInflater.from(context);
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        char lastFirstChar = mData.get(0).charAt(0);
        sectionIndices.add(0);
        for (int i = 1; i < mData.size(); i++) {
            if (mData.get(i).charAt(0) != lastFirstChar) {
                lastFirstChar = mData.get(i).charAt(0);
                sectionIndices.add(i);
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        return sections;
    }

    private Character[] getSectionLetters() {
        Character[] letters = new Character[mSectionIndices.length];
        for (int i = 0; i < mSectionIndices.length; i++) {
            letters[i] = mData.get(mSectionIndices[i]).charAt(0);

        }
        return letters;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.txt_title);
            try {
                if (Typefaces.get(mContext, "CharlotteSans_nn") != null) {
                    holder.text.setTypeface(Typefaces.get(mContext, "CharlotteSans_nn"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            holder.text.setText(mData.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public View getHeaderView(final int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text1);
            try {
                if (Typefaces.get(mContext, "Monotype_Sabon_Italic") != null) {
                    holder.text.setTypeface(Typefaces.get(mContext, "Monotype_Sabon_Italic"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as first char in name
        try {
            String headerChar = String.valueOf(mData.get(position).subSequence(0, 1));
            holder.text.setText(headerChar.toLowerCase());
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    @Override
    public long getHeaderId(int position) {
        // return the first character of the country as ID because this is what
        // headers are based upon
        Character c = mData.get(position).subSequence(0, 1).charAt(0);
//        System.out.println("shailesh==="+c.toLowerCase(c));
        return c.toLowerCase(c);
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return mSectionLetters;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                mData = (ArrayList<String>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<String> FilteredArrList = new ArrayList<String>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<String>(mData); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i);
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(data);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

//    public void clear() {
//        mData = new String[0];
//        mSectionIndices = new int[0];
//        mSectionLetters = new Character[0];
//        notifyDataSetChanged();
//    }
//
//    public void restore() {
//        mData = mContext.getResources().getStringArray(R.array.countries);
//        mSectionIndices = getSectionIndices();
//        mSectionLetters = getSectionLetters();
//        notifyDataSetChanged();
//    }

    class HeaderViewHolder {
        TextView text;
    }

    class ViewHolder {
        TextView text;
    }


}
