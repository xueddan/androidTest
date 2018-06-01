package com.example.lw.test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class userinfo extends AppCompatActivity {
    private TextView userid;
    private TextView username;
    private TextView sex;
    private TextView age;
    private TextView birthday;
    private String userId;
    private TextView phonemac;
    private TextView lapmac;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        userid = (TextView) findViewById(R.id.userId);
        username = (TextView) findViewById(R.id.userName);
        sex = (TextView) findViewById(R.id.sex);
        age = (TextView) findViewById(R.id.age);
        birthday = (TextView) findViewById(R.id.birthday);
        phonemac=(TextView)findViewById(R.id.phonemac);
        lapmac=(TextView)findViewById(R.id.lapmac);
        userId = getIntent().getStringExtra("userId");
        Log.d("userInfo-userId", userId);
        imageView=(ImageView)findViewById(R.id.userPhoto);
//        imageView.setImageResource((int)R.drawable.photo);
//        imageView.setImageDrawable(R.drawable.photo);
        sendRequestWithOkHttpJson();
    }
//    private int getImageId(int imageId){
//        return imageId;
//    }

    private void sendRequestWithOkHttpJson() {
        Log.d("1111", "====================================");
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("userId", userId)
                        .build();
                Log.d("requestBody", String.valueOf(requestBody));
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://172.20.10.12:8080/user/queryInfo")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("responseData", responseData);
                    parseJSONWithJSONObject(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(final String jsonData) {
        Log.d("2222", "====================================");
        Log.d("JsonData", jsonData);

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject userData = jsonObject.getJSONObject("data");
            String message=jsonObject.getString("message").substring(8);
            String userId = userData.getString("userId");
            String userName = userData.getString("userName");
            String passWord = userData.getString("passWord");
            String birthDay = userData.getString("birthDay");
            Integer sex = userData.getInt("sex");
            Integer age = userData.getInt("age");
//            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//            Date date=sdf.parse(birthDay);
            User user = new User(userId, userName, passWord, birthDay, sex, age,getBitmap());
            setText(user,message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Bitmap getBitmap(){
        final Bitmap[] bitmap = {null};
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        try {
            byte[] url= ImageService.getImage("http://172.20.10.12:8080/upload/" +userId + ".png");
            bitmap[0] = BitmapFactory.decodeByteArray(url, 0, url.length);
            Log.d("getUrl []=","http://10.0.2.2:8080/upload/" +userId + ".png");
        } catch (IOException e) {
            e.printStackTrace();
        }
//            }
//        }).start();
        Log.d("bitmap  getBitmap", String.valueOf(bitmap[0]));
//        Log.d("url[0]", String.valueOf(url[0].length));
        return bitmap[0];
    }

    private void show(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(userinfo.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setText(final User user,final String mac) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("Info---=", user.toString());
                userid.setText(user.getUserId());
                username.setText(user.getUserName());
                if (user.getSex()==0)
                    sex.setText("女");
                else
                    sex.setText("男");
                age.setText(String.valueOf(user.getAge()));
                birthday.setText(user.getBirthDay());
                phonemac.setText(getMacAddress());
                lapmac.setText("08-62-66-03-7A-01");

                imageView.setImageBitmap(user.getBitmap());
            }
        });
    }


    //手机Mac地址
    public static String getMacAddress(){
        String macAddress = null;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "02:00:00:00:00:02";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "02:00:00:00:00:02";
        }
        return macAddress;
    }

}



















































































































































