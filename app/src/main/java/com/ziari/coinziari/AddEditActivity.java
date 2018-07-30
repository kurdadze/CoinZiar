package com.ziari.coinziari;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ziari.coinziari.Services.COINS;
import com.ziari.coinziari.Services.PAIRS_LIST;
import com.ziari.coinziari.Tools.GlobalMethods;
import com.ziari.coinziari.Tools.GlobalVariables;
import com.ziari.coinziari.Tools.RetrofitSingleton;
import com.ziari.coinziari.Tools.Session;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

;
//selectedMarket

public class AddEditActivity extends AppCompatActivity implements View.OnClickListener {

    EditText coinQty, minValue, maxValue, myPrice;

    CheckBox checkBox;

    TextView currentPrice, totalCoinPriceValue, markets, pairs, saveCoin;

    ImageView selectMarket, selectPairs;

    String coinPrefix;
    String selectedMarket;

    String firstPair;
    String secondPair;

    public static final int GET_FROM_MARKET = 1;
    public static final int GET_FROM_PAIRS = 2;

    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        coinPrefix = getIntent().getStringExtra("CoinPrefix");

        markets = (TextView)findViewById(R.id.exchange);
        pairs = (TextView)findViewById(R.id.pairs);
        selectMarket = (ImageView)findViewById(R.id.selectMarket);
        selectPairs = (ImageView)findViewById(R.id.selectPairs);

        coinQty = (EditText)findViewById(R.id.editQuantity);
        myPrice = (EditText)findViewById(R.id.myPrice);
        currentPrice = (TextView)findViewById(R.id.currentPrice);
        totalCoinPriceValue = (TextView)findViewById(R.id.totalCoinPriceValue);

        checkBox = (CheckBox)findViewById(R.id.checkBox);
        minValue = (EditText)findViewById(R.id.minValue);
        maxValue = (EditText)findViewById(R.id.maxValue);

        saveCoin = (TextView)findViewById(R.id.saveCoin);

        selectMarket.setOnClickListener(this);
        selectPairs.setOnClickListener(this);
        coinQty.setOnClickListener(this);
        saveCoin.setOnClickListener(this);

        coinQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0) {
                    try {
                        NumberFormat formatter = new DecimalFormat("#0.00000000");
                        String totalPrice = formatter.format(Double.valueOf(GlobalVariables.coinCurrentPrice) * Float.valueOf(charSequence.toString().trim()));
                        totalCoinPriceValue.setText(String.valueOf(totalPrice));
                    } catch (Exception e){
                        totalCoinPriceValue.setText("0");
                    }
                } else {
                    totalCoinPriceValue.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    minValue.setEnabled(true);
                    maxValue.setEnabled(true);
                    minValue.setText("");
                    maxValue.setText("");
                }else{
                    minValue.setEnabled(false);
                    maxValue.setEnabled(false);
                    minValue.setText("");
                    maxValue.setText("");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        if(view == selectMarket) {
            intent = new Intent(this, MarketsActivity.class);
            intent.putExtra("CoinPrefix", coinPrefix);
            startActivityForResult(intent, GET_FROM_MARKET);
        }
        if(view == selectPairs) {
            intent = new Intent(this, PairsListActivity.class);
            intent.putExtra("market", GlobalVariables.selectedMarket);
            intent.putExtra("coin", coinPrefix);
            startActivityForResult(intent, GET_FROM_PAIRS);
        }
        if( view == saveCoin ){
            dialog = new MaterialDialog.Builder(this)
                    .title("Data saving")
                    .content("Please wait...")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            if (GlobalMethods.isConnectedFast(getApplicationContext())) {
                AddEditCoins();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case GET_FROM_MARKET:
                    selectedMarket = data.getStringExtra("market");
                    GlobalVariables.selectedMarket = selectedMarket;
                    markets.setText(selectedMarket);
                break;
                case GET_FROM_PAIRS:
                    firstPair = data.getStringExtra("firstpair");
                    secondPair = data.getStringExtra("secondpair");
                    GlobalVariables.selectedPairs = firstPair + " - " + secondPair;
                    markets.setText(GlobalVariables.selectedPairs);
                    if (GlobalMethods.isConnectedFast(getApplicationContext())) {
                        int sepPos = GlobalVariables.selectedPairs.indexOf(" - ");
                        String symbol1, symbol2;

                        symbol1 = GlobalVariables.selectedPairs.substring(0, sepPos).trim();
                        symbol2 = GlobalVariables.selectedPairs.substring(sepPos + 2, GlobalVariables.selectedPairs.length()).trim();

                        final RetrofitSingleton fileTasks = new RetrofitSingleton();
                        PAIRS_LIST pairsListService = (PAIRS_LIST) fileTasks.getService(PAIRS_LIST.class);
                        pairsListService.GET_PAIRS_PRICE(GlobalVariables.selectedMarket, symbol1, symbol2).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Response<String> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    try {
                                        GlobalVariables.coinCurrentPrice = response.body();
                                        currentPrice.setText(GlobalVariables.coinCurrentPrice);
                                        if(coinQty.getText().toString().trim().length() > 1){
                                            NumberFormat formatter = new DecimalFormat("#0.00000000");
                                            String totalPrice = formatter.format(Double.valueOf(GlobalVariables.coinCurrentPrice) * Float.valueOf(coinQty.getText().toString().trim()));
                                            totalCoinPriceValue.setText(String.valueOf(totalPrice));
                                        } else {
                                            totalCoinPriceValue.setText("0");
                                        }
                                    } catch (Exception ex) {
                                        Log.d("hj", "ghjgj");
                                        GlobalVariables.coinCurrentPrice = "0";
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.d("hj", "ghjgj");
                                GlobalVariables.coinCurrentPrice = "0";
                            }
                        });
                    }
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("eee","rrr");
        markets.setText(GlobalVariables.selectedMarket);
        pairs.setText(GlobalVariables.selectedPairs);
        currentPrice.setText(GlobalVariables.coinCurrentPrice);
        totalCoinPriceValue.setText(GlobalVariables.totalCoinPriceValue);
    }

    private void AddEditCoins() {
        final Intent intent = new Intent();

        String userid = String.valueOf(Session.CurrentConfig.UserID);
        String selectedMarket = GlobalVariables.selectedMarket;
        String selectedPairs = GlobalVariables.selectedPairs;
        String quantity = coinQty.getText().toString();
        String myprice = myPrice.getText().toString();
        String currPrice = currentPrice.getText().toString();
        String totalPriceValue = totalCoinPriceValue.getText().toString();
        String minimalValue = minValue.getText().toString();
        Boolean checkBoxPrcMin = true;
        String maximalValue = maxValue.getText().toString();
        Boolean checkBoxPrcMax = true;
        String action = "save";
        String currentToken = Session.CurrentConfig.getToken();


        final RetrofitSingleton fileTasks = new RetrofitSingleton();
        COINS pairsListService = (COINS) fileTasks.getService(COINS.class);
        pairsListService.SET_DATA(
                userid,
                selectedMarket,
                selectedPairs,
                quantity,
                myprice,
                currPrice,
                totalPriceValue,
                minimalValue,
                checkBoxPrcMin,
                maximalValue,
                checkBoxPrcMax,
                action,
                currentToken).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    try {
                        dialog.dismiss();
                        intent.putExtra("saved", 1);
                        setResult(RESULT_OK, intent);
                        finish();
                    } catch (Exception ex) {
                        dialog.dismiss();
                    }
                } else {
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("hj", "ghjgj");
                dialog.dismiss();
            }
        });
    }
}
