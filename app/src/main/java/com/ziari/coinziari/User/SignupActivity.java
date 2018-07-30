package com.ziari.coinziari.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ziari.coinziari.MainActivity;
import com.ziari.coinziari.Models.User;
import com.ziari.coinziari.R;
import com.ziari.coinziari.Services.USER_SERVICE;
import com.ziari.coinziari.Tools.GlobalMethods;
import com.ziari.coinziari.Tools.RetrofitSingleton;
import com.ziari.coinziari.Tools.Session;
import com.ziari.coinziari.Tools.Utils;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignupActivity";
    private Button   buttonSignup;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private TextView textViewSignin;

    private Context context = this;
    private MaterialDialog dialog;

    Utils Utils;
    GlobalMethods GlobalMethods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Utils = new Utils();
        GlobalMethods = new GlobalMethods();

        editTextEmail    = (EditText)findViewById(R.id.edtEmail);
        editTextPassword = (EditText)findViewById(R.id.edtPassword);
        editTextFirstName = (EditText)findViewById(R.id.edtFirstName);
        editTextLastName = (EditText)findViewById(R.id.edtLastName);

        buttonSignup   = (Button)findViewById(R.id.btSignup);
        textViewSignin   = (TextView)findViewById(R.id.tvSignin);

        buttonSignup.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == buttonSignup){
            registerUser();
        }

        if(view == textViewSignin){
            finish();
            startActivity(new Intent(this, SigninActivity.class));
        }
    }

    private void registerUser(){

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();

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
                    .title("Signup")
                    .content("Please wait...")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            final RetrofitSingleton scheduleTasks = new RetrofitSingleton();
            USER_SERVICE RetrofitSingleton = (USER_SERVICE) scheduleTasks.getService(USER_SERVICE.class);
            RetrofitSingleton.REGISTER_USER(email, password, firstName, lastName).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Response<User> response, Retrofit retrofit) {
                    Log.d(TAG, String.valueOf(response.body()));

                    // ლოდინის ფანჯრის გამორთვა
                    dialog.dismiss();

                    // ვამოწმებთ, არის თუ არა მომხმარებელი უკვე დარეგისტრირებული
                    if(response.body().ID==0){
                        Toast.makeText(context, "User already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        Session.CurrentUser = response.body();
                        finish();
                        startActivity(new Intent(context, MainActivity.class));
                    }
                }
                @Override
                public void onFailure(Throwable t) {
                    Log.d(TAG, t.toString());
                    dialog.dismiss();
                    Toast.makeText(context, "Could not register. Please try again", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }
    }
}
