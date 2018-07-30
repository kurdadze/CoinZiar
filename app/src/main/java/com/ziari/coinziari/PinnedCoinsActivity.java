package com.ziari.coinziari;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.ziari.coinziari.Adapters.PinnedCoinsAdapter;
import com.ziari.coinziari.Models.PinnedCoin;
import com.ziari.coinziari.Services.COIN_LIST;
import com.ziari.coinziari.Tools.GlobalMethods;
import com.ziari.coinziari.Tools.GlobalVariables;
import com.ziari.coinziari.Tools.RetrofitSingleton;
import com.ziari.coinziari.Tools.Session;
import com.ziari.coinziari.Tools.Utils;
import com.ziari.coinziari.User.ProfileActivity;
import com.ziari.coinziari.User.SigninActivity;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class PinnedCoinsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton pinNewCoin, refreshPinnedCoins, prifile;
    private Context context = this;
    ListView listView;
    PinnedCoinsAdapter PinnedCoinsAdapter;
    LinearLayout pinnedCoinsSlistProgress;
    PullRefreshLayout layout;
    TextView sumaryValue;
//    private DrawerLayout mDrawerLayout;

    GlobalVariables GlobalVars = new GlobalVariables();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinned_coins);

//        mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_main);
//        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
//        mDrawerLayout.addDrawerListener(mToggle);
//        mToggle.syncState();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        pinnedCoinsSlistProgress = findViewById(R.id.pinnedCoinsSlistProgress);
        listView = findViewById(R.id.coin_list);
        pinNewCoin = findViewById(R.id.pinNewCoin);
        refreshPinnedCoins = findViewById(R.id.refreshPinnedCoins);
        prifile = findViewById(R.id.profile);
        sumaryValue = findViewById(R.id.sumary_value);

        pinNewCoin.setOnClickListener(this);
        prifile.setOnClickListener(this);
        refreshPinnedCoins.setOnClickListener(this);

        ImageView icon = new ImageView(this);
        icon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_menu));
        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();
//
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
//
        ImageView itemIcon;
        itemIcon = new ImageView(this);
        itemIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_logout));
        SubActionButton logout = itemBuilder.setContentView(itemIcon).build();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GlobalMethods.SignOut()){
                    Intent i = new Intent(getApplicationContext(), SigninActivity.class);
                    startActivity(i);
                }
            }
        });

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(logout)
                .attachTo(actionButton)
                .build();

        PinnedCoinsAdapter = new PinnedCoinsAdapter(PinnedCoinsActivity.this, R.layout.pinned_coin_item);

        layout = findViewById(R.id.swipeRefreshLayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPinnedCoinList();
            }
        });
        getPinnedCoinList();
    }

    @Override
    public void onClick(View view) {
        if(view == pinNewCoin){
            Intent i = new Intent(this, SelectCoinActivity.class);
            startActivity(i);
        }
        if(view == refreshPinnedCoins){
            layout.setRefreshing(true);
            getPinnedCoinList();
        }
        if(view == prifile){
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GlobalMethods.isConnectedFast(context)) {
            getPinnedCoinList();
        } else {
            Log.d("Network", "Error");
        }
    }

    protected void onFinish() {
        super.onResume();
        if (GlobalMethods.isConnectedFast(context)) {
            getPinnedCoinList();
        } else {
            Log.d("Network", "Error");
        }
    }

    public void getPinnedCoinList(){
//        pinnedCoinsSlistProgress.setVisibility(View.VISIBLE);
        final RetrofitSingleton fileTasks = new RetrofitSingleton();
        COIN_LIST PinnedCoinList = (COIN_LIST) fileTasks.getService(COIN_LIST.class);
        PinnedCoinList.GET_PINNED_COINS(Session.CurrentConfig.UserID,Session.CurrentConfig.Token).enqueue(new Callback<List<PinnedCoin>>() {
            @Override
            public void onResponse(Response<List<PinnedCoin>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    try {
//                        GlobalVariables.summaryCoinPriceValue = 0.0;
                        PinnedCoinsAdapter = new PinnedCoinsAdapter(PinnedCoinsActivity.this, R.layout.pinned_coin_item);
                        PinnedCoinsAdapter.addAll(response.body());
                        listView.setAdapter(PinnedCoinsAdapter);
                        List<PinnedCoin> dataFromServer = response.body();
                        sumaryValue.setText(String.valueOf(Utils.round(Double.valueOf(dataFromServer.get(0).getSumPrice()), 2)) + " USD");
                        layout.setRefreshing(false);
//                        pinnedCoinsSlistProgress.setVisibility(View.GONE);
                    } catch (Exception ex) {
                        layout.setRefreshing(false);
//                        pinnedCoinsSlistProgress.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                layout.setRefreshing(false);
//                pinnedCoinsSlistProgress.setVisibility(View.GONE);
//                listView.setEnabled(true);
            }
        });
    }

}
