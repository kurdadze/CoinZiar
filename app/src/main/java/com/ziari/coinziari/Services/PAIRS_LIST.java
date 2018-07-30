package com.ziari.coinziari.Services;


import com.ziari.coinziari.Models.Pairs;

import java.util.List;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface PAIRS_LIST {
    String GET_PAIRSBUMARKET_LIST_URI = "getpairsbymarket/";
    @FormUrlEncoded
    @POST(GET_PAIRSBUMARKET_LIST_URI)
    Call<List<Pairs>> GET_PAIRS_LIST_BY_MARKET(
            @Field("market") String market,
            @Field("coin") String coin
    );

    String GET_PAIRSPRICE_URI = "getpairsprice/";
    @FormUrlEncoded
    @POST(GET_PAIRSPRICE_URI)
    Call<String> GET_PAIRS_PRICE(
            @Field("market") String market,
            @Field("symbol1") String symbol1,
            @Field("symbol2") String symbol2
    );
}
