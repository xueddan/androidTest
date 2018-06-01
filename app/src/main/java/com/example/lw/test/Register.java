package com.example.lw.test;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
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
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.attr.bitmap;

public class Register extends AppCompatActivity {
    private EditText userId;
    private EditText userName;
    private EditText age;
    private EditText passWord;
    private EditText birthday;
    private Button register;
//    private RadioGroup sex;
    private Button upload;
    private EditText sex1;
    protected static final int CHOOSE_PICTURE=0;//相册返回码
    protected static final int TAKE_PICTURE=1;//拍照返回码
    private static final int CROP_SMALL_PICTURE=2;
    protected static Uri tempUri;
    private ImageView imageView;
    private String img_src;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register=(Button)findViewById(R.id.Register);
//        sex=(RadioGroup)findViewById(R.id.sex);
        sex1=(EditText) findViewById(R.id.sex1);
        userId =(EditText)findViewById(R.id.userId);
        userName=(EditText)findViewById(R.id.userName);
        age=(EditText)findViewById(R.id.age);
        passWord=(EditText)findViewById(R.id.passWord);
        birthday=(EditText)findViewById(R.id.birthday);
        upload=(Button)findViewById(R.id.upload);
        imageView=(ImageView)findViewById(R.id.photo);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("点击注册","==============================");
                sendRequestWithOkHttp();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosePicDialog();
            }
        });


    }
    protected void showChoosePicDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("设置头像");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, CHOOSE_PICTURE);

//                        Intent openAlbumIntent = new Intent(
//                                Intent.ACTION_GET_CONTENT);
//                        openAlbumIntent.setType("image/*");
//                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        takePicture();
//                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        tempUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
//                        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
//                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
//                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;

                case CHOOSE_PICTURE:
//                    startPhotoZoom(data.getData());// 开始对图片进行裁剪处理
                    Uri uri = data.getData();
                    img_src = uri.getPath();//这是本机的图片路径

                    ContentResolver cr = this.getContentResolver();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        /* 将Bitmap设定到ImageView */
                        imageView.setImageBitmap(bitmap);

                        String[] proj = {MediaStore.Images.Media.DATA};
                        CursorLoader loader = new CursorLoader(this, uri, proj, null, null, null);
                        Cursor cursor = loader.loadInBackground();
                        if (cursor != null) {
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();

                            img_src = cursor.getString(column_index);//图片实际路径

                        }
                        Log.e("img_src", img_src);
                        cursor.close();
//                        img_src=Rename(img_src,idEdit.getText().toString());
                        Log.d("newPath=",img_src);
                        uploadPic(img_src);
                    } catch (FileNotFoundException e) {
                        Log.e("Exception", e.getMessage(), e);
                    }

                    break;

                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                        break;
                    }
            }
        }
    }


    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            photo =ImageUtils.toRoundBitmap(photo); // 这个时候的图片已经被处理成圆形的了
            imageView.setImageBitmap(photo);
            uploadPic(img_src);//测试
        }
    }

    private void uploadPic(final String path) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String uploadurl="";
                if(userId.length()>0)
                    uploadurl= "http://172.20.10.12:8080/user/uploadImg?userId="+userId.getText().toString();
                else
                    uploadurl= "http://172.20.10.12:8080/user/uploadImg";
                try {
                    File file = new File(path);
                    UploadUtil.uploadImage(file, uploadurl);
//                    Log.d("result=",result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }


    private void takePicture() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= 23) {
            // 需要申请动态权限
            int check = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (check != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        Intent openCameraIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment
                .getExternalStorageDirectory(), "image.jpg");
        if (Build.VERSION.SDK_INT >= 24) {
            openCameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            tempUri = FileProvider.getUriForFile(Register.this, "com.example.lw.test.fileProvider", file);
        } else {
            tempUri = Uri.fromFile(new File(Environment
                    .getExternalStorageDirectory(), "image.jpg"));
            Log.d("tempUri----------",tempUri.toString());
        }
        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    private void sendRequestWithOkHttp(){
        Log.d("111111","===============================");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path="";
                String type="";
                String userid=userId.getText().toString();
                String username = userName.getText().toString();
                String Age =age.getText().toString();
                String pwd=passWord.getText().toString();

                String  sexIndex=sex1.getText().toString();
                if(img_src.length()>0) {
                    type = img_src.substring(img_src.lastIndexOf(".") + 1, img_src.length());
                    path=userid + "." + type;
                }
                String birthDay=birthday.getText().toString();
                JSONObject jsonObject=new JSONObject();
//                JSONObject jsonObject1=new JSONObject();
                try {
                    jsonObject.put("userId",userid);
                    jsonObject.put("userName",username);
                    jsonObject.put("passWord",pwd);
                    jsonObject.put("birthDay",birthDay);
                    jsonObject.put("age",Age);
                    jsonObject.put("sex",sexIndex);
                    jsonObject.put("image",path);
//                    jsonObject1.put("data",jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("jsonObject========",jsonObject.toString());
                RequestBody requestBody=new FormBody.Builder()
                        .add("data",jsonObject.toString())
                        .build();
                try{
                    OkHttpClient client=new OkHttpClient();
                    Request request=new Request.Builder()
                            .url("http://172.20.10.12:8080/session/register")
                            .post(requestBody)
                            .build();
                    Response response=client.newCall(request).execute();
                    String responseData=response.body().string();
                    Log.d("responseData===========",responseData);
                    parseJSONWithJSONObject(responseData);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void parseJSONWithJSONObject(String jsonData) {
        Log.d("222222","====================================");
        Log.d("JsonData",jsonData);
        try {
            JSONObject jsonObject=new JSONObject(jsonData);
            String message = jsonObject.getString("message");
            Log.d("message",message);
            int code = jsonObject.getInt("code");
            Log.d("code", String.valueOf(code));
            if(code==0){
                show(message);
            }else{
                show(message);
            }
            Log.d("Register", "message is " + message);
            Log.d("Register", "code is " + code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void show(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Register.this,message,Toast.LENGTH_SHORT).show();
            }
        });
    }
}















































//                int sexIndex=0;
//                RadioButton rb = (RadioButton)Register.this.findViewById(sex.getCheckedRadioButtonId());
//                if(rb.getText().equals("男")){
//                    sexIndex=1;
//                }