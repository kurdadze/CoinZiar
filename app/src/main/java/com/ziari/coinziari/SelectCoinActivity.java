package com.ziari.coinziari;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ziari.coinziari.Adapters.CoinsAdapter;
import com.ziari.coinziari.Models.Coin;
import com.ziari.coinziari.Services.COIN_LIST;
import com.ziari.coinziari.Tools.GlobalMethods;
import com.ziari.coinziari.Tools.GlobalVariables;
import com.ziari.coinziari.Tools.RetrofitSingleton;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SelectCoinActivity extends AppCompatActivity {

    private static final int COIN_SELECT = 1;

    private Context context = this;
    CoinsAdapter coinsAdapter;
    SearchView searchView;
    ListView listView;
    ImageView imageMore;
    LinearLayout linlaHeaderProgress;

    GlobalMethods GlobalMethods;
    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_coin);

        GlobalMethods = new GlobalMethods();

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        listView = (ListView)findViewById(R.id.coinListView);
        searchView = (SearchView) findViewById(R.id.searchBar);

        searchView.setVisibility(View.GONE);
        searchView.setActivated(true);
        searchView.setQueryHint("Select Coin...");
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                coinsAdapter.getFilter().filter(newText);

                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (GlobalMethods.isConnectedFast(context)) {
            searchView.setVisibility(View.GONE);
            searchView.clearFocus();
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            final RetrofitSingleton fileTasks = new RetrofitSingleton();
            COIN_LIST coinListService = (COIN_LIST) fileTasks.getService(COIN_LIST.class);
            coinListService.GET_COIN_LIST("").enqueue(new Callback<List<Coin>>() {
                @Override
                public void onResponse(Response<List<Coin>> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        try {

                            coinsAdapter = new CoinsAdapter(SelectCoinActivity.this, R.layout.only_coin_item, response.body());
                            coinsAdapter.addAll(response.body());

                            listView.setAdapter(coinsAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                String coin = ((Coin)coinsAdapter.getItem(position)).getPrefix();

                                GlobalVariables.selectedMarket = null;
                                GlobalVariables.selectedPairs = null;
                                GlobalVariables.coinCurrentPrice = null;
                                GlobalVariables.totalCoinPriceValue = null;

                                Intent intent = new Intent(getApplicationContext(), AddEditActivity.class);
                                intent.putExtra("CoinPrefix", String.valueOf(coin));
                                startActivityForResult(intent, COIN_SELECT);
                                }
                            });
                            linlaHeaderProgress.setVisibility(View.GONE);
                            searchView.setVisibility(View.VISIBLE);
                            searchView.clearFocus();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case COIN_SELECT:
                    Toast.makeText(context, "Coin inserted", Toast.LENGTH_LONG).show();
                    finish();
                break;
            }
        }
    }
}
