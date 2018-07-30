package com.ziari.coinziari.User;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ziari.coinziari.R;
import com.ziari.coinziari.Services.USER_SERVICE;
import com.ziari.coinziari.Tools.FileUtil;
import com.ziari.coinziari.Tools.GlobalMethods;
import com.ziari.coinziari.Tools.RetrofitSingleton;
import com.ziari.coinziari.Tools.Session;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context = this;
    ImageView backImage, accept;
    EditText email, currPass, newPass, reNewPass;
    TextView changeProfilePhoto;
    CircleImageView myPhoto;
    private File actualImage;
    GlobalMethods GlobalMethods;

    File defFolder;

    String tmpFolder, realFolder;

    public static final int THIS_CAMERA_REQUEST = 4;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        GlobalMethods = new GlobalMethods();


        changeProfilePhoto = findViewById(R.id.changeProfilePhoto);
        myPhoto = findViewById(R.id.profile_photo);
        backImage = findViewById(R.id.back);
        accept = findViewById(R.id.accept);
        email = findViewById(R.id.email);
        currPass = findViewById(R.id.currPass);
        newPass = findViewById(R.id.newPass);
        reNewPass = findViewById(R.id.reNewPass);


        backImage.setOnClickListener(this);
        accept.setOnClickListener(this);

        String userEmail = GlobalMethods.HideStringPart(Session.CurrentConfig.UserName, 3, 7, "*");
        tmpFolder = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))+"/Camera";
        realFolder = Environment.getExternalStoragePublicDirectory(Session.APP_ROOT_FOLDER).toString();

        defFolder = new File(realFolder);

        email.setText(userEmail);

        changeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, THIS_CAMERA_REQUEST);
            }
        });

        File[] avatarFile = defFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.contains(".jpg");
            }
        });

        if (avatarFile.length != 0) {
            myPhoto.setImageURI(Uri.fromFile(new File(avatarFile[0].getAbsolutePath())));
        }
    }

    @Override
    public void onClick(View view) {
        if (view == backImage) {
            finish();
        }
        if (view == accept) {
            SaveData();
        }
    }

    private void SaveData() {
        String tokenV = Session.CurrentConfig.Token;
        String currPassV = currPass.getText().toString();
        String newPassV = newPass.getText().toString();
        String reNewPassV = reNewPass.getText().toString();

        if((newPassV.trim().length() == 0) || (reNewPassV.trim().length() == 0)){
            Toast.makeText(context, R.string.newPasswordFieldIsNull, Toast.LENGTH_SHORT).show();
        } else {
            if (GlobalMethods.isConnectedFast(context)) {
                final RetrofitSingleton fileTasks = new RetrofitSingleton();
                USER_SERVICE userService = (USER_SERVICE) fileTasks.getService(USER_SERVICE.class);
                userService.RESTORE_VALUES(tokenV, currPassV, newPassV, reNewPassV).enqueue(new Callback<Map<String, Map<String, Object>>>() {
                    @Override
                    public void onResponse(Response<Map<String, Map<String, Object>>> response, Retrofit retrofit) {
                        if (response.isSuccess()) try {
                            Map<String, Map<String, Object>> value = response.body();
                            if (!value.isEmpty()) {
                                String key = value.keySet().iterator().next();
                                Map<String, Object> values = value.get(key);
                                if (key.equals("response")) {
                                    Toast.makeText(context, R.string.DATA_IS_CHANGED, Toast.LENGTH_SHORT).show();
                                } else {
                                    String messageValue = values.get("messageText").toString();
                                    try {
                                        Field resourceField = R.string.class.getDeclaredField(messageValue);
                                        Toast.makeText(context, context.getString(resourceField.getInt(resourceField)), Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            Log.d("hj", "ghjgj");
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("hj", "ghjgj");
                    }
                });
            } else {
                Log.d("Network", "Error");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case THIS_CAMERA_REQUEST:
                    try {
                        actualImage = FileUtil.from(this, data.getData());
                        String photoName = GlobalMethods.GetFileNameFromPath(this, data.getData());
                        String tmpFolder = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))+"/Camera";
                        String realFolder = Environment.getExternalStoragePublicDirectory(Session.APP_ROOT_FOLDER).toString();

                        File defFolder = new File(realFolder);
                        if (defFolder.isDirectory()) {
                            String[] children = defFolder.list();
                            for (String child : children) {
                                if (child.endsWith(".jpeg") || child.endsWith(".jpg"))
                                    new File(defFolder, child).delete();
                            }
                        }

                        File sourceLocation = new File (tmpFolder + "/" + photoName);
                        File targetLocation = new File (realFolder + "/" + photoName);
                        sourceLocation.renameTo(targetLocation);

                        File compressedImage = new Compressor(this)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                .setDestinationDirectoryPath(realFolder)
                                .compressToFile(actualImage);
                        myPhoto.setImageURI(Uri.fromFile(compressedImage));
                        FileUtil.deleteTempFiles(getCacheDir());
                        Toast.makeText(context, R.string.profilePictureChanged, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("Network", "Error");
                break;
            }
        }
    }
}
