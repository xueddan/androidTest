/*
package com.example.lw.test;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

*/
/**
 * Created by LW on 2018/5/4.
 *//*


public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>{
    private int resourceId;
    public FruitAdapter(Context context, int textViewResourceId, List<Fruit> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        Fruit fruit=getItem(position);//获取当前的fruit实例
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.fruitImage=(ImageView) view.findViewById(R.id.fruit_image);
            viewHolder.fruitName=(TextView) view.findViewById(R.id.fruit_name);
            view.setTag(viewHolder);//将ViewHolder存储在View中
        }else {
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();//重新获取ViewHolder
        }
//        ImageView fruitImage=(ImageView) view.findViewById(R.id.fruit_image);
//        TextView fruitName=(TextView) view.findViewById(R.id.fruit_name);
//        fruitImage.setImageResource(fruit.getImageId());
//        fruitName.setText(fruit.getName());
        viewHolder.fruitImage.setImageResource(fruit.getImageId());
        viewHolder.fruitName.setText(fruit.getName());
        return view;
    }
    class ViewHolder{
        ImageView fruitImage;
        TextView fruitName;
    }
}
*/
