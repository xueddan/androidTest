package com.example.lw.test;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2018/5/2.
 */

public class User {

    //用户ID
    private String userId;
    //用户名
    private String userName;
    //密码
    private String passWord;


    private String birthDay;

    private Integer age;

    private Integer sex;

    private String image;

    Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public User(String userId, String userName, String passWord, String birthDay, Integer sex, Integer age, Bitmap bitmap) {
        this.userId = userId;
        this.userName = userName;
        this.passWord = passWord;
        this.birthDay = birthDay;
        this.age = age;
        this.sex = sex;

        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", passWord='" + passWord + '\'' +
                ", birthDay='" + birthDay + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", image='" + image + '\'' +
                ", bitmap=" + bitmap +
                '}';
    }
}

