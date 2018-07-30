package com.ziari.coinziari.Tools;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GlobalMethods {

    public static final int NETWORK_TYPE_EHRPD=14; // Level 11
    public static final int NETWORK_TYPE_EVDO_B=12; // Level 9
    public static final int NETWORK_TYPE_HSPAP=15; // Level 13
    public static final int NETWORK_TYPE_IDEN=11; // Level 8
    public static final int NETWORK_TYPE_LTE=13; // Level 11

    Gson gson;

    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static boolean isConnectedFast(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected() && GlobalMethods.isConnectionFast(info.getType(),info.getSubtype()));
    }

    public static boolean isConnectionFast(int type, int subType){
        if(type==ConnectivityManager.TYPE_WIFI){
            System.out.println("CONNECTED VIA WIFI");
            return true;
        }else if(type==ConnectivityManager.TYPE_MOBILE){
            switch(subType){
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                // NOT AVAILABLE YET IN API LEVEL 7
                case GlobalMethods.NETWORK_TYPE_EHRPD:
                    return true; // ~ 1-2 Mbps
                case GlobalMethods.NETWORK_TYPE_EVDO_B:
                    return true; // ~ 5 Mbps
                case GlobalMethods.NETWORK_TYPE_HSPAP:
                    return true; // ~ 10-20 Mbps
                case GlobalMethods.NETWORK_TYPE_IDEN:
                    return false; // ~25 kbps
                case GlobalMethods.NETWORK_TYPE_LTE:
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return false;
                default:
                    return false;
            }
        }else{
            return false;
        }
    }

    public String GenerationStringWithDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyy_HHmmss");
        String timestamp = sdf.format(new Date());
        return timestamp;
    }

    public static void CreateJsonFile(String folderName, String fName, String jsonContent, Boolean encryption) {
        String filePath = Environment.getExternalStoragePublicDirectory(folderName).toString();
        File root = new File(filePath);
        if (!root.exists()) {root.mkdirs();}
        File cfgfile = new File(root + "/"+fName);

        FileWriter writer = null;
        try {
            writer = new FileWriter(cfgfile);
            writer.append(jsonContent);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Boolean SignOut(){
        Boolean logOut = false;
        Session.CurrentConfig = null;
        String configFilePath = Environment.getExternalStoragePublicDirectory(Session.APP_ROOT_FOLDER + "/config.cfg").toString();
        File configFile = new File(configFilePath);
        if (configFile.exists()) {
            configFile.delete();
            logOut = true;
        }
        return logOut;
    }

    public static <T> T FillFromFile(String filePath, Class<T> klass) {
        Gson gson = new Gson();
        T myClass = null;
        try {
            String configFilePath = Environment.getExternalStoragePublicDirectory(filePath).toString();
            File configFile = new File(configFilePath);
            if (configFile.exists()) {
                FileInputStream in = new FileInputStream(configFile);
                byte[] data = new byte[(int) configFile.length()];
                in.read(data);
                in.close();
                String cfgContent = new String(data, "UTF-8");
                myClass = (T) gson.fromJson(cfgContent.toString(), klass);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myClass;
    }

    public String HideStringPart(String str, int from, int to, String mask){
        String  txt = "";
        String  part        = str.substring(from, to);
        int     partLength  = part.length();

        String leftPart     = str.substring(0, from);
        String rightPart    = str.substring(to, str.length());

        for (int i = 0; i < partLength; i++) {
            txt = txt + mask;
        }
        txt = leftPart + txt + rightPart;

        return txt;
    }

    private File getTempFile(Context context, String url) {
        File file = null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
        }
        return file;
    }

    public File CreateImageFileByUser(int userId) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = String.valueOf(userId) + "_" + timeStamp + ".jpg";
        File photo = new File(Environment.getExternalStorageDirectory(),  imageFileName);
        return photo;
    }

    public String GetFileNameFromPath(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
