package com.ziari.coinziari.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.ziari.coinziari.Models.Pairs;
import com.ziari.coinziari.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PairsAdapter extends ArrayAdapter implements Filterable {

    private LayoutInflater inflater;

    List list=new ArrayList();

    List<Pairs> mStringFilterList;

    ValueFilter valueFilter;

    Context contect;

    public PairsAdapter(@NonNull Context context, int resource, List<Pairs> cancel_type) {
        super(context, resource);
        this.contect = context;
        inflater = LayoutInflater.from(context);
        mStringFilterList = cancel_type;
    }

    static class DataHandler{
        TextView PairName;
    }

    @Override
    public void addAll(@NonNull Collection collection) {
        super.addAll(collection);
        list.addAll(collection);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new PairsAdapter.ValueFilter();
        }
        return valueFilter;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Pairs pairs = (Pairs)this.getItem(position);

        View row;
        row=convertView;
        DataHandler handler;
        if(convertView==null){

            LayoutInflater inflater=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.pair_item, parent, false);
            handler = new DataHandler();
            handler.PairName=(TextView)row.findViewById(R.id.pairsName);

            row.setTag(handler);
        }else {
            handler=(DataHandler)row.getTag();
        }

        handler.PairName.setText(pairs.getFirstpair() + " - " + pairs.getSecondpair());

        return row;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
//
            if (constraint != null && constraint.length() > 0) {
                List<Pairs> filterList = new ArrayList<>();
                for (int i = 0; i < mStringFilterList.size(); i++) {

                    Log.d("x","a");
                    if ((mStringFilterList.get(i).getPairs().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(mStringFilterList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list = (List<String>) results.values;
            notifyDataSetChanged();
        }

    }
}
