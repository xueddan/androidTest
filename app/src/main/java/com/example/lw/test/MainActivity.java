package com.example.lw.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText accountEdit;
    private EditText passwordEdit;
    private Button login;
    private Button register;
    private Button queryBook;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.Register);
        queryBook=(Button)findViewById(R.id.queryBook);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("点击登陆","====================");

                sendRequestWithOkHttpJson();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("点击注册","====================================");
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });
        queryBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, query.class);
                startActivity(intent);
            }
        });
    }
    private void sendRequestWithOkHttpJson() {
        Log.d("1111","================");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                RequestBody requestBody=new FormBody.Builder()
                        .add("userId",account)
                        .add("passWord",password)
                        .build();
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://172.20.10.12:8080/session/login")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("responseData",responseData);
                    parseJSONWithJSONObject(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void parseJSONWithJSONObject(String jsonData) {
        Log.d("2222","====================================");
        Log.d("JsonData",jsonData);
        try {
            JSONObject jsonObject=new JSONObject(jsonData);
            String message = jsonObject.getString("message");
//
            Log.d("message",message);
//            String code = jsonObject.getString("code");
            int code=jsonObject.getInt("code");
            Log.d("code", String.valueOf(code));
            if(code==0){
                Intent intent=new Intent(MainActivity.this,userinfo.class);
                Log.d("accpuntEdit+++++++",accountEdit.getText().toString());
                intent.putExtra("userId",accountEdit.getText().toString());
                startActivity(intent);
                show(message);
            }else{
                show(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void show(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
