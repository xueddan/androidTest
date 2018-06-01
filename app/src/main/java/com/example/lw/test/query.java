package com.example.lw.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class query extends AppCompatActivity {
    private EditText pageNumEdit;
    private EditText pageSizeEdit;
    private Button queryButton;
//    private LinearLayout ll;
    private ScrollView scrollView;

    private TextView responseText;
    List<Book> bookList=new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        scrollView=(ScrollView)findViewById(R.id.data);
        pageNumEdit=(EditText)findViewById(R.id.pageNum);
        pageSizeEdit=(EditText)findViewById(R.id.pageSize);
        queryButton=(Button)findViewById(R.id.query);
        responseText=(TextView)findViewById(R.id.response_text);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestWithOkHttpJson();
            }
        });

    }
    private void sendRequestWithOkHttpJson() {
        Log.d("1111","====================================");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String pageNum = pageNumEdit.getText().toString();
                String pageSize = pageSizeEdit.getText().toString();
                RequestBody requestBody=new FormBody.Builder()
                        .add("pageNum",pageNum)
                        .add("pageSize",pageSize)
                        .build();
                Log.d("requestBody", String.valueOf(requestBody));
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://172.20.10.12:8080/user/queryBook")
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

    private void parseJSONWithJSONObject(final String jsonData) {
        Log.d("2222","====================================");
        Log.d("JsonData",jsonData);
        try {
            JSONObject jsonObject=new JSONObject(jsonData);
            String message = jsonObject.getString("message");
            JSONArray data=jsonObject.getJSONArray("data");
            Log.d("data", String.valueOf(data));
//
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObj = data.getJSONObject(i);
                String bookId=jsonObj.getString("bookId");
                String bookName=jsonObj.getString("bookName");
                String author=jsonObj.getString("author");
                String press=jsonObj.getString("press");
                String publishDate=jsonObj.getString("publishDate");
                String pageNum=jsonObj.getString("pageNum");
                String bookImage=jsonObj.getString("image");
                final Bitmap[] bitmap = new Bitmap[1];
                try {
                    Log.d("run", "try");
                    byte[] url = ImageService.getImage("http://192.168.43.68:8080/bookImage/" + bookId + ".jpg");
                    bitmap[0] = BitmapFactory.decodeByteArray(url, 0, url.length);
//                            Log.d("bitmap[0].size");
                    Log.d("bitmap[0]", String.valueOf(bitmap[0]));
                    Book book = new Book(bookId, bookName, author, pageNum, press, publishDate, bookImage, bitmap[0]);
                    bookList.add(book);
                }catch (Exception e){
                    e.printStackTrace();
            }
//                Book book=new Book(bookId,bookName,author,pageNum,press,publishDate) ;
//                bookList.add(book);
                Log.d("bookList+++++0", String.valueOf(bookList));
                Log.d("bookList", String.valueOf(bookList));
            }
            Intent intent = new Intent(query.this, datashow.class);
            intent.putExtra("bookList",(Serializable)(bookList));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
