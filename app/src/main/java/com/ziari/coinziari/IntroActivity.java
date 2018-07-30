package com.ziari.coinziari;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ziari.coinziari.Tools.GlobalMethods;
import com.ziari.coinziari.User.SigninActivity;

public class IntroActivity extends AppCompatActivity {

    private Context context = this;

    GlobalMethods GlobalMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        GlobalMethods = new GlobalMethods();

        if(GlobalMethods.isConnectedFast(context)){
//            finish();
            startActivity(new Intent(this, SigninActivity.class));
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }
    }
}
