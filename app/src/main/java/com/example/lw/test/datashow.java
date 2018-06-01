package com.example.lw.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class datashow extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datashow);

        List<Book> bookList= getIntent().getParcelableArrayListExtra("bookList");
        Log.d("书籍信息", String.valueOf(bookList));
        adapter adapter=new adapter(datashow.this,R.layout.bookinfo,bookList);
        ListView listView=(ListView)findViewById(R.id.bookItem);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
