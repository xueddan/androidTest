package com.example.lw.test;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by LW on 2018/5/3.
 */

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Administrator on 2018/5/2.
 */

public class Book implements Parcelable{
    //    String bookImage;
    String bookId;
    String bookName;
    String author;
    String pageNum;
    String press;
    String publishDate;
    String image;
    Bitmap bitmap;

    protected Book(Parcel in) {
        bookId = in.readString();
        bookName = in.readString();
        author = in.readString();
        pageNum = in.readString();
        press = in.readString();
        publishDate = in.readString();
        image = in.readString();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookId);
        dest.writeString(bookName);
        dest.writeString(author);
        dest.writeString(pageNum);
        dest.writeString(press);
        dest.writeString(publishDate);
        dest.writeString(image);
        dest.writeParcelable(bitmap, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Book(String bookId, String bookName, String author, String pageNum, String press, String publishDate, String image, Bitmap bitmap) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.author = author;
        this.pageNum = pageNum;
        this.press = press;
        this.publishDate = publishDate;
        this.image = image;
        this.bitmap=bitmap;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPageNum() {
        return pageNum;
    }

    public void setPageNum(String pageNum) {
        this.pageNum = pageNum;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }



}
