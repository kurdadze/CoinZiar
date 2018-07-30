package com.ziari.coinziari.User;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.ziari.coinziari.BuildConfig;
import com.ziari.coinziari.Models.Config;
import com.ziari.coinziari.Models.PinnedCoin;
import com.ziari.coinziari.Models.User;
import com.ziari.coinziari.PinnedCoinsActivity;
import com.ziari.coinziari.R;
import com.ziari.coinziari.Services.COIN_LIST;
import com.ziari.coinziari.Services.USER_SERVICE;
import com.ziari.coinziari.Tools.GlobalMethods;
import com.ziari.coinziari.Tools.GlobalVariables;
import com.ziari.coinziari.Tools.RetrofitSingleton;
import com.ziari.coinziari.Tools.Session;
import com.ziari.coinziari.Tools.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SigninActivity";
    public static final int GET_PER_STOR_ANSWER = 1;

    private Button buttonSignin;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;

    private Context context = this;
    private MaterialDialog dialog;

    Utils Utils;
    GlobalMethods GlobalMethods;
    Config myConfig;
    Gson gson;
    List<String> PERM_LIST = new ArrayList<String>();



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GET_PER_STOR_ANSWER: {
                if (grantResults[0] != -1) {
                    Config config = GlobalMethods.FillFromFile(Session.APP_ROOT_FOLDER + "/config.cfg", Config.class);
                    Session.CurrentConfig = config;
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myConfig = new Config();
        gson = new Gson();

        Utils = new Utils();
        GlobalMethods = new GlobalMethods();

        editTextEmail    = (EditText)findViewById(R.id.edtEmail);
        editTextPassword = (EditText)findViewById(R.id.edtPassword);

        buttonSignin   = (Button)findViewById(R.id.btSignin);
        textViewSignup   = (TextView)findViewById(R.id.tvSignup);

        buttonSignin.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);

        Config config = GlobalMethods.FillFromFile(Session.APP_ROOT_FOLDER + "/config.cfg", Config.class);
        Session.CurrentConfig = config;

        if (Build.VERSION.SDK_INT > 22) {
            int perChk_READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
            int perChk_WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

            int perChk_ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
            int perChk_ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);

            if (perChk_ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                PERM_LIST.add(ACCESS_COARSE_LOCATION);
            }
            if (perChk_ACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                PERM_LIST.add(ACCESS_FINE_LOCATION);
            }
            if (perChk_READ_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                PERM_LIST.add(READ_EXTERNAL_STORAGE);
            }
            if (perChk_WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                PERM_LIST.add(WRITE_EXTERNAL_STORAGE);
            }
            if (PERM_LIST.size() > 0) {
                requestPermissions(PERM_LIST.toArray(new String[PERM_LIST.size()]), GET_PER_STOR_ANSWER);
            }
        }

        if (Session.CurrentConfig != null) {
            if (Session.CurrentConfig.getAutorised() == true) {
                finish();
//                getPinnedCoinList();
                startActivity(new Intent(context, PinnedCoinsActivity.class));
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view == buttonSignin){
            loginUser();
        }

        if(view == textViewSignup){
            finish();
            startActivity(new Intent(this, SignupActivity.class));
        }
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        // ლოდინის ფანჯრის ჩართვა
        if (GlobalMethods.isConnectedFast(context)) {

            dialog = new MaterialDialog.Builder(this)
                    .title("Signin")
                    .content("Please wait...")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            final RetrofitSingleton scheduleTasks = new RetrofitSingleton();
            USER_SERVICE RetrofitSingleton = (USER_SERVICE) scheduleTasks.getService(USER_SERVICE.class);
            RetrofitSingleton.LOGIN_VALUES(email, password).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Response<User> response, Retrofit retrofit) {
                    Log.d(TAG, String.valueOf(response.body()));
                    // ლოდინის ფანჯრის გამორთვა
                    dialog.dismiss();
                    // ვამოწმებთ, არის თუ არა მომხმარებელი უკვე დარეგისტრირებული
                    if(response.body().ID==0){
                        Toast.makeText(context, "User not found, please signup", Toast.LENGTH_SHORT).show();
                    } else {
                        Session.CurrentUser = response.body();
                        int versionCode = BuildConfig.VERSION_CODE;
                        myConfig.setUserName(Session.CurrentUser.Email);
                        myConfig.setAutorised(true);
                        myConfig.setToken(Session.CurrentUser.Token);
                        myConfig.setUserID(Session.CurrentUser.ID);
                        myConfig.setFirstLastName(Session.CurrentUser.FirstName + " " + Session.CurrentUser.LastName);
                        myConfig.setVersion(versionCode);
                        String jsonContent = gson.toJson(myConfig);
                        GlobalMethods.CreateJsonFile(Session.APP_ROOT_FOLDER,"config.cfg", jsonContent, false);
                        Config config = GlobalMethods.FillFromFile(Session.APP_ROOT_FOLDER + "/config.cfg", Config.class);
                        Session.CurrentConfig = config;
                        finish();
                        startActivity(new Intent(context, PinnedCoinsActivity.class));
                    }
                }
                @Override
                public void onFailure(Throwable t) {
                    Log.d(TAG, t.toString());
                    dialog.dismiss();
                    Toast.makeText(context, "Could not signup. Please try again", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }
    }

    public void getPinnedCoinList(){
        final RetrofitSingleton fileTasks = new RetrofitSingleton();
        COIN_LIST PinnedCoinList = (COIN_LIST) fileTasks.getService(COIN_LIST.class);
        PinnedCoinList.GET_PINNED_COINS(Session.CurrentConfig.UserID,Session.CurrentConfig.Token).enqueue(new Callback<List<PinnedCoin>>() {
            @Override
            public void onResponse(Response<List<PinnedCoin>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    try {
                        List<PinnedCoin> dataFromServer = response.body();
                        GlobalVariables.summaryCoinPriceValue = Double.valueOf(dataFromServer.get(0).getSumPrice());
                        String al = String.valueOf(dataFromServer.get(0).getSumPrice());
                    } catch (Exception ex) {
                        GlobalVariables.summaryCoinPriceValue = 0.0;
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {}
        });
    }


}
