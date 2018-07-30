package com.ziari.coinziari.Services;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface COINS {
    String SET_COINS_ADD_EDIT = "coinsaddedit/";
    @FormUrlEncoded
    @POST(SET_COINS_ADD_EDIT)
    Call<String> SET_DATA(
            @Field("userid") String userid,
            @Field("markets") String market,
            @Field("pairs") String pairs,
            @Field("quantity") String quantity,
            @Field("myprice") String myprice,
            @Field("curentprice") String curentprice,
            @Field("totalvalue") String totalvalue,
            @Field("minvalue") String minvalue,
            @Field("minvaluepercent") Boolean minvaluepercent,
            @Field("maxvalue") String maxvalue,
            @Field("maxvaluepercent") Boolean maxvaluepercent,
            @Field("action") String action,
            @Field("token") String currentToken
    );
}
