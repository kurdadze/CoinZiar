package com.ziari.coinziari.Tools;


import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;

public class RetrofitSingleton<T> {
    private static ConcurrentHashMap<Class, Object> INSTANCE = new ConcurrentHashMap<>();
    private static RetrofitSingleton instance;

    private static OkHttpClient client = new OkHttpClient();
    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    static {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager); //finally set the cookie handler on client
        client.setConnectTimeout(60, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(logging);
    }
    public static RetrofitSingleton getInstance(){
        if(instance==null){
            instance = new RetrofitSingleton();
        }
        return instance;
    }

    public T getService(Class<T> cazz) {
        if (!INSTANCE.containsKey(cazz)) {
            retrofit.Retrofit retrofit = new retrofit.Retrofit.Builder()
                    .baseUrl(Session.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            INSTANCE.put(cazz, retrofit.create(cazz));
        }
        return (T) INSTANCE.get(cazz);
    }
}
