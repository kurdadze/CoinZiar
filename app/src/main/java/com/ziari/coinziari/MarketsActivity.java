package com.ziari.coinziari;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.ziari.coinziari.Adapters.MarketsAdapter;
import com.ziari.coinziari.Models.Market;
import com.ziari.coinziari.Services.MARKET_LIST;
import com.ziari.coinziari.Tools.GlobalMethods;
import com.ziari.coinziari.Tools.RetrofitSingleton;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MarketsActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context = this;
    MarketsAdapter marketAdapter;
    ListView listView;
    ImageView backImage;

    String coinPrefix;

    GlobalMethods GlobalMethods;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markets);

        coinPrefix = getIntent().getStringExtra("CoinPrefix");

        backImage = (ImageView)findViewById(R.id.back);
        listView = (ListView)findViewById(R.id.marketsListView);

        backImage.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (GlobalMethods.isConnectedFast(context)) {
            final RetrofitSingleton fileTasks = new RetrofitSingleton();
            MARKET_LIST marketListService = (MARKET_LIST) fileTasks.getService(MARKET_LIST.class);
            marketListService.GET_MARKET_LIST_BY_COIN(coinPrefix).enqueue(new Callback<List<Market>>() {
                @Override
                public void onResponse(Response<List<Market>> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        try {

                            marketAdapter = new MarketsAdapter(MarketsActivity.this, R.layout.market_item);
                            marketAdapter.addAll(response.body());

                            listView.setAdapter(marketAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                                    String market = ((Market)marketAdapter.getItem(position)).getName();
                                    final Intent intent = new Intent();
                                    intent.putExtra("market", market);
                                    setResult(RESULT_OK, intent);
                                    finish();

                                }
                            });
                        } catch (Exception ex) {
                            Log.d("hj", "ghjgj");
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("hj", "ghjgj");
                }
            });
        } else {
            Log.d("Network", "Error");
        }
    }

    @Override
    public void onClick(View view) {
        if(view == backImage){
            finish();
        }
    }
}
