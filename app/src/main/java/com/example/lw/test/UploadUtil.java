package com.example.lw.test;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * Created by Administrator on 2018/5/18.
 */

public class UploadUtil {
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000;//超时时间
    private static final String CHARSET = "utf-8";//设置编码

    /**
     * android上传文件到服务器
     *
     * @param file       需要上传的文件
     * @param RequestURL  请求的url
     * @return 返回响应的内容
     */
    public static void uploadImage(final File file, final String RequestURL) {

        final String BOUNDARY = UUID.randomUUID().toString();//边界标识 随机生成
        final String PREFIX = "--", LINE_END = "\r\n";
        final String CONTENT_TYPE = "multipart/form-data";//内容类型
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = "error";
                    URL url = new URL(RequestURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(TIME_OUT);
                    conn.setConnectTimeout(TIME_OUT);
                    conn.setDoInput(true);//允许输入流
                    conn.setDoOutput(true);//允许输出流
                    conn.setUseCaches(false);//不允许使用缓存
                    conn.setRequestMethod("POST");//请求方式
                    conn.setRequestProperty("Charset", CHARSET);//设置编码
                    conn.setRequestProperty("connection", "keep-alive");
                    conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
                    conn.connect();
                    Log.d("oo","OK");
                    if (file != null) {
                        //当文件不为空，把文件包装并且上传
                        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                        dos.writeBytes(PREFIX + BOUNDARY + LINE_END);
                        dos.writeBytes("Content-Disposition: form-data; " + "name=\"inputName\";filename=\"" + file.getName() + "\"" + LINE_END);
                        dos.writeBytes(LINE_END);
//                dos.writeBytes("userId="+userId);
                        Log.d("oo","OK1");
                        FileInputStream is = new FileInputStream(file);
                        Log.d("oo","OK2");
                        byte[] bytes = new byte[1024];
                        int len = -1;
                        while ((len = is.read(bytes)) != -1) {
                            dos.write(bytes, 0, len);
                        }
                        is.close();
                        dos.write(LINE_END.getBytes());

                        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                        dos.write(end_data);
                        dos.flush();
                        Log.d("oo","OK5");
                /*
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */
                        int res = conn.getResponseCode();
                        Log.d("oo","OK4");
                        if (res == 200) {
                            Log.d("oo","OK3"+res);
                            InputStream input = conn.getInputStream();
                            StringBuilder sbs = new StringBuilder();
                            int ss;
                            while ((ss = input.read()) != -1) {
                                sbs.append((char) ss);
                            }
                            result = sbs.toString();
                            Log.i(TAG, "result------------------>>" + result);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static String getMIMEType(File file) {
        String fileName = file.getName();
        if (fileName.endsWith("png") || fileName.endsWith("PNG")) {
            return "image/png";
        } else {
            return "image/jpg";
        }
    }
}
