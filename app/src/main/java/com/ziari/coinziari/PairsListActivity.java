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
import android.widget.ListView;

import com.ziari.coinziari.Adapters.PairsAdapter;
import com.ziari.coinziari.Models.Pairs;
import com.ziari.coinziari.Services.PAIRS_LIST;
import com.ziari.coinziari.Tools.GlobalMethods;
import com.ziari.coinziari.Tools.RetrofitSingleton;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class PairsListActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context = this;
    GlobalMethods GlobalMethods;

    PairsAdapter pairsAdapter;

    ImageView backImage;
    ListView listView;

    String market;
    String coin;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairs_list);

        market = getIntent().getStringExtra("market");
        coin = getIntent().getStringExtra("coin");

        listView = (ListView)findViewById(R.id.pairsListView);
        backImage = (ImageView)findViewById(R.id.back);
        searchView = (SearchView) findViewById(R.id.searchBar);


        searchView.setActivated(true);
        searchView.setQueryHint("Select Pairs...");
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

                pairsAdapter.getFilter().filter(newText);

                return false;
            }
        });
        backImage.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();


        if(GlobalMethods.isConnectedFast(context)){
            final RetrofitSingleton fileTasks = new RetrofitSingleton();
            PAIRS_LIST pairsListService = (PAIRS_LIST) fileTasks.getService(PAIRS_LIST.class);
            pairsListService.GET_PAIRS_LIST_BY_MARKET(market, coin).enqueue(new Callback<List<Pairs>>() {
                @Override
                public void onResponse(Response<List<Pairs>> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        try {

                            pairsAdapter = new PairsAdapter(PairsListActivity.this, R.layout.pair_item, response.body());
                            pairsAdapter.addAll(response.body());

                            listView.setAdapter(pairsAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                                    String firstpair = ((Pairs)pairsAdapter.getItem(position)).getFirstpair();
                                    String secondpair = ((Pairs)pairsAdapter.getItem(position)).getSecondpair();
                                    final Intent intent = new Intent();
                                    intent.putExtra("firstpair", firstpair);
                                    intent.putExtra("secondpair", secondpair);
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
        }
    }

    @Override
    public void onClick(View view) {
        if(view == backImage){
            finish();
        }
    }
}
