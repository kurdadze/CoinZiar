package com.ziari.coinziari.Services;


import com.ziari.coinziari.Models.User;

import java.util.Map;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface USER_SERVICE {
    String REGISTRATION_URI = "user/registration";
    @FormUrlEncoded
    @POST(REGISTRATION_URI)
    Call<User> REGISTER_USER(
            @Field("user") String user,
            @Field("pass") String pass,
            @Field("firstName") String firstName,
            @Field("lastName") String lastName
    );

    String LOGIN_URI="user/login";
    @FormUrlEncoded
    @POST(LOGIN_URI)
    Call<User> LOGIN_VALUES(
            @Field("user") String user,
            @Field("pass") String pass
    );

    String RESTORE_URI="user/restore";
    @FormUrlEncoded
    @POST(RESTORE_URI)
    Call<Map<String,Map<String,Object>>> RESTORE_VALUES(
            @Field("token") String token,
            @Field("currPass") String currPass,
            @Field("newPass") String newPass,
            @Field("reNewPass") String reNewPass
    );
}
