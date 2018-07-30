package com.ziari.coinziari.Services;


import com.ziari.coinziari.Models.Coin;
import com.ziari.coinziari.Models.PinnedCoin;

import java.util.List;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface COIN_LIST {
    String GET_COIN_LIST_URI = "getcoins/";
    @FormUrlEncoded
    @POST(GET_COIN_LIST_URI)
    Call<List<Coin>> GET_COIN_LIST(
            @Field("coin") String coin
    );

    String GET_PINNED_COINS1 = "getpinnedcoins/";
    @FormUrlEncoded
    @POST(GET_PINNED_COINS1)
    Call<List<PinnedCoin>> GET_PINNED_COINS(
            @Field("userid") int userid,
            @Field("token") String token
    );

    String DEL_PINNED_COIN = "deletepinnedcoins/";
    @FormUrlEncoded
    @POST(DEL_PINNED_COIN)
    Call<List<PinnedCoin>> DELETE_PINNED_COIN(
            @Field("userid") int userid,
            @Field("token") String token,
            @Field("recordid") int recordid
    );
}
