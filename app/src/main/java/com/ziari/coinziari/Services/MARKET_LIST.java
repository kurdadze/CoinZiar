package com.ziari.coinziari.Services;


import com.ziari.coinziari.Models.Market;

import java.util.List;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface MARKET_LIST {
    String GET_MARKETBYCOIN_LIST_URI = "getmarketsbycoin/";
    @FormUrlEncoded
    @POST(GET_MARKETBYCOIN_LIST_URI)
    Call<List<Market>> GET_MARKET_LIST_BY_COIN(
            @Field("coin") String coin
    );
}
