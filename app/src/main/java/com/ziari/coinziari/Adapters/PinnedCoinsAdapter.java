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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.ziari.coinziari.Models.PinnedCoin;
import com.ziari.coinziari.R;
import com.ziari.coinziari.Services.COIN_LIST;
import com.ziari.coinziari.Tools.GlobalVariables;
import com.ziari.coinziari.Tools.RetrofitSingleton;
import com.ziari.coinziari.Tools.Session;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class PinnedCoinsAdapter extends ArrayAdapter {

    private LayoutInflater inflater;

    List list=new ArrayList();

    Context contect;

    public PinnedCoinsAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.contect = context;
        inflater = LayoutInflater.from(context);
    }

    static class DataHandler{
        ImageView coinimagepath, deleteImage, moreImage;
        TextView coinName, marketName, coinsQuantity, totalPrice, percentValue;
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

        final ConnectivityManager cm = (ConnectivityManager) contect.getSystemService(Context.CONNECTIVITY_SERVICE);

        PinnedCoin pinnedCoin = (PinnedCoin)this.getItem(position);

        View row;
        row=convertView;
        DataHandler handler;
        if(convertView==null){

            LayoutInflater inflater=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row= inflater.inflate(R.layout.pinned_coin_item, parent, false);
            handler=new DataHandler();
            handler.coinimagepath=row.findViewById(R.id.coinImage);
            handler.deleteImage=row.findViewById(R.id.deleteImage);
            handler.moreImage=row.findViewById(R.id.moreImage);

            handler.coinName=row.findViewById(R.id.coinName);
            handler.marketName=row.findViewById(R.id.marketName);
            handler.coinsQuantity=row.findViewById(R.id.coinsQuantity);
            handler.totalPrice=row.findViewById(R.id.totalPrice);
            handler.percentValue=row.findViewById(R.id.percentValue);

            row.setTag(handler);
        }else {
            handler=(DataHandler)row.getTag();
        }

        handler.coinName.setText(pinnedCoin.getCoinname() + " (" + pinnedCoin.getCoin1() + ")");
        handler.marketName.setText(pinnedCoin.getMarkets());
        handler.coinsQuantity.setText(pinnedCoin.getQuantity());
        handler.totalPrice.setText(pinnedCoin.getTotalvalue());

        GlobalVariables.summaryCoinPriceValue = Double.valueOf(pinnedCoin.getSumPrice());

        Float percent = Float.parseFloat(pinnedCoin.getPercent());
        int percentColor;
        if(percent < 0.0)
            percentColor = 0xE15441;
        else
            percentColor = 0x32B60A;

        handler.percentValue.setText(pinnedCoin.getPercent() + "%");
        handler.percentValue.setTextColor(percentColor);

        handler.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int imageID = ((PinnedCoin)list.get(position)).getId();
                String coin = ((PinnedCoin)list.get(position)).getCoinname();
                String market = ((PinnedCoin)list.get(position)).getMarkets();
                new MaterialDialog.Builder(contect)
                    .title("Delete pinned coin")
                    .content("Are you sure wont to delete? (" + coin + " - "+ market + ")" )
                    .positiveText("Yes")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            if(cm.getActiveNetworkInfo() != null){
                                final RetrofitSingleton abstractSingl = new RetrofitSingleton();
                                COIN_LIST service = (COIN_LIST) abstractSingl.getService(COIN_LIST.class);
                                service.DELETE_PINNED_COIN(Session.CurrentConfig.getUserID(), Session.CurrentConfig.getToken(), imageID).enqueue(new Callback<List<PinnedCoin>>() {
                                    @Override
                                    public void onResponse(Response<List<PinnedCoin>> response, Retrofit retrofit) {
                                        if (response.isSuccess() == true) {
                                            list.remove(position);
                                            notifyDataSetChanged();
                                            Toast.makeText(contect, "Coin deleted", Toast.LENGTH_LONG).show();
                                        } else {
                                            Log.d("LOG", response.toString());
                                        }
                                    }
                                    @Override
                                    public void onFailure(Throwable t) {
                                        Log.d("LOG", "sdfsdf");
                                    }
                                });
                            }
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {

                        }
                    })
                .show();
            }
        });

        if(cm.getActiveNetworkInfo() != null){
            Glide
                .with(contect)
                .load(Session.IMG_URL+((PinnedCoin)(list.get(position))).getImage())
                .into((ImageView)row.findViewById(R.id.coinImage));
        } else {
            String filePathImg = ((PinnedCoin) (list.get(position))).getImage();
            File file = new File(filePathImg);
            Uri imageUri = Uri.fromFile(file);
            Glide
                .with(contect)
                .load(imageUri)
                .into((ImageView) row.findViewById(R.id.coinImage));
        }
        return row;
    }
}
