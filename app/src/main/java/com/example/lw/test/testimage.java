package com.example.lw.test;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class testimage extends AppCompatActivity {
    private Button upload;
    private ImageView imageView;
    protected static final int CHOOSE_PICTURE=0;//相册返回码
    protected static final int TAKE_PICTURE=1;//拍照返回码
    private static final int CROP_SMALL_PICTURE=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimage);
        upload=(Button)findViewById(R.id.upload);
        imageView=(ImageView)findViewById(R.id.image);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosePicDialog();
            }
        });

    }
    private void showChoosePicDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(testimage.this);
        builder.setTitle("添加图片");
        String[] items={"选择本地照片","拍照"};
        builder.setNegativeButton("取消",null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    //选择本地照片
                    case CHOOSE_PICTURE:
                        if (ContextCompat.checkSelfPermission(testimage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(testimage.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                        } else {
                            Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
                            // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                            pickIntent.setType("image/*");
                            startActivityForResult(pickIntent, CHOOSE_PICTURE);
                        }

                        break;
                    //拍照
                    case TAKE_PICTURE:
                        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //下面这句指定调用相机拍照后的照片存储的路径
                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"image.jpg")));
                        startActivityForResult(takeIntent,TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.create().show();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case CHOOSE_PICTURE:// 直接从相册获取
                try {
                    String imagePath = null;
                    Uri uri = data.getData();
                    Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
                    if (DocumentsContract.isDocumentUri(this, uri)) {
                        // 如果是document类型的Uri，则通过document id处理
                        String docId = DocumentsContract.getDocumentId(uri);
                        if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                            String id = docId.split(":")[1]; // 解析出数字格式的id
                            String selection = MediaStore.Images.Media._ID + "=" + id;
                            imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                        } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                            Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                            imagePath = getImagePath(contentUri, null);
                        }
                    } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                        // 如果是content类型的Uri，则使用普通方式处理
                        imagePath = getImagePath(uri, null);
                    } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                        // 如果是file类型的Uri，直接获取图片路径即可
                        imagePath = uri.getPath();
                    }
                    displayImage(imagePath);
//                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case TAKE_PICTURE:// 调用相机拍照
                File temp = new File(Environment.getExternalStorageDirectory(),"/image.jpg");
                startPhotoZoom(Uri.fromFile(temp));
                break;
            case CROP_SMALL_PICTURE:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }
    /**
     * 保存裁剪之后的图片数据
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            // 取得SDCard图片路径做显示
            Bitmap photo = extras.getParcelable("data");
//            Drawable drawable = new BitmapDrawable(null, photo);
//            urlpath = ImageUtils.savePhoto(mContext, "temphead.jpg", photo);
//            imageView.setImageDrawable(drawable);
            photo = ImageUtils.toRoundBitmap(photo); // 这个时候的图片已经被处理成圆形的了
            imageView.setImageBitmap(photo);
            // 新线程后台上传服务端
//            pd = ProgressDialog.show(mContext, null, "正在上传图片，请稍候...");
//            new Thread(uploadImageRunnable).start();
        }
    }

}
