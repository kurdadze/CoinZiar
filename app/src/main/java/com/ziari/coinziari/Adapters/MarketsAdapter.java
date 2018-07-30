package com.ziari.coinziari.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.ziari.coinziari.Models.Market;
import com.ziari.coinziari.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MarketsAdapter extends ArrayAdapter implements Filterable {

    private LayoutInflater inflater;

    List list=new ArrayList();

    Context contect;

    public MarketsAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.contect = context;
        inflater = LayoutInflater.from(context);
    }

    static class DataHandler{
        TextView MarketName;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        Market market=(Market)this.getItem(position);

        View row;
        row=convertView;
        DataHandler handler;
        if(convertView==null){

            LayoutInflater inflater=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.market_item, parent, false);
            handler = new DataHandler();
            handler.MarketName=(TextView)row.findViewById(R.id.marketName);

            row.setTag(handler);
        }else {
            handler=(DataHandler)row.getTag();
        }

        handler.MarketName.setText(market.getName());

        return row;
    }

}
