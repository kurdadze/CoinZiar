package com.ziari.coinziari.Adapters;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ziari.coinziari.Models.Coin;
import com.ziari.coinziari.R;
import com.ziari.coinziari.Tools.Session;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CoinsAdapter extends ArrayAdapter implements Filterable {

    private LayoutInflater inflater;

    List list=new ArrayList();

    List<Coin> mStringFilterList;

    ValueFilter valueFilter;

    Context contect;

    public CoinsAdapter(@NonNull Context context, int resource, List<Coin> cancel_type) {
        super(context, resource);
        this.contect = context;
        inflater = LayoutInflater.from(context);
        mStringFilterList = cancel_type;
    }

    static class DataHandler{
        TextView CoinName;
        ImageView Image;
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
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ConnectivityManager cm = (ConnectivityManager) contect.getSystemService(Context.CONNECTIVITY_SERVICE);

        Coin coin=(Coin)this.getItem(position);

        View row;
        row=convertView;
        DataHandler handler;
        if(convertView==null){

            LayoutInflater inflater=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row= inflater.inflate(R.layout.only_coin_item, parent, false);
            handler=new DataHandler();
            handler.CoinName=(TextView)row.findViewById(R.id.coinName);
            handler.Image=(ImageView)row.findViewById(R.id.coinImage);

            row.setTag(handler);
        }else {
            handler=(DataHandler)row.getTag();
        }

        handler.CoinName.setText(coin.getCoin());

        if(cm.getActiveNetworkInfo() != null){
            Glide
                .with(contect)
                .load(Session.IMG_URL+((Coin)(list.get(position))).getImage())
                .into((ImageView)row.findViewById(R.id.coinImage));
        } else {
            String filePathImg = ((Coin) (list.get(position))).getImage();
            File file = new File(filePathImg);
            Uri imageUri = Uri.fromFile(file);
            Glide
                    .with(contect)
                    .load(imageUri)
                    .into((ImageView) row.findViewById(R.id.coinImage));
        }
        return row;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
//
            if (constraint != null && constraint.length() > 0) {
                List<Coin> filterList = new ArrayList<>();
                for (int i = 0; i < mStringFilterList.size(); i++) {

                    Log.d("x","a");
                    if ((mStringFilterList.get(i).getCoin().toUpperCase()).contains(constraint.toString().toUpperCase())) {
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
