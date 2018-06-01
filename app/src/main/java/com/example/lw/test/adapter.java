package com.example.lw.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

/**
 * Created by LW on 2018/5/16.
 */

public class adapter extends ArrayAdapter<Book> {
    private int resourceId;
//    private Handler handler=null;
//    private Bitmap bitmap;

    public adapter(Context context, int textViewResourceId, List<Book> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Book book=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.image=(ImageView) view.findViewById(R.id.book_photo);
            viewHolder.bookName=(TextView) view.findViewById(R.id.book_name);
            viewHolder.author=(TextView) view.findViewById(R.id.author);
            viewHolder.pageNum=(TextView) view.findViewById(R.id.pageNum);
            viewHolder.press=(TextView) view.findViewById(R.id.press);
            viewHolder.publishDate=(TextView) view.findViewById(R.id.publish_date);
            view.setTag(viewHolder);//将ViewHolder存储在View中
        }else {
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();//重新获取ViewHolder
        }

        viewHolder.bookName.setText(book.getBookName());
        viewHolder.author.setText(book.getAuthor());
//        viewHolder.pageNum.setText(book.getPageNum());
        viewHolder.press.setText(book.getPress());
        viewHolder.publishDate.setText(book.getPublishDate());
        viewHolder.image.setImageBitmap(book.getBitmap());   //显示图片
        return view;
    }

    class ViewHolder{
        ImageView image;
        TextView bookName;
        TextView author;
        TextView pageNum;
        TextView press;
        TextView publishDate;
    }

    public void test(final ViewHolder viewHolder, final Book book){
        try {
            byte[] data = ImageService.getImage("http://172.20.10.12:8080/bookImage/"+book.getBookId()+".jpg");
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            viewHolder.image.setImageBitmap(bitmap);   //显示图片
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
