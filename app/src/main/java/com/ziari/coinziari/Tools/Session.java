package com.ziari.coinziari.Tools;

import com.ziari.coinziari.BuildConfig;
import com.ziari.coinziari.Models.Config;
import com.ziari.coinziari.Models.User;

public  class Session {
    public static User CurrentUser;
    public static Config CurrentConfig;

    private static String url_server_local="http://10.80.80.70:1979";
    private static String url_server_global="http://80.241.255.163:1979";
    private static String server_host="/service/";
    private static String server_image="/img/";

    public static int mobileCurrentVersion= BuildConfig.VERSION_CODE;
    public static String API_URL=url_server_global+server_host;
    public static String IMG_URL=url_server_global+server_image;
    public static String APP_ROOT_FOLDER="CoinZiari";

}