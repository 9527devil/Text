package com.example.mrwang.examination_06_06.utils;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {
    //全局变量
     private MyHandler handler = new MyHandler();
     private static final int SUCCESS = 0;
     private static final int MISS = 1;
     private HttpUtilsListener httpUtilsListener;
    //单利模式
    private static HttpUtils httpUtils = new HttpUtils();

    public static HttpUtils getinstance(){
        if(httpUtils==null){
            HttpUtils httpUtils = new HttpUtils();
        }
        return httpUtils;
    }
    //构造私有化
    private HttpUtils(){};

    public void get(final String urls){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL(urls);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(3000);
                    InputStream inputStream = connection.getInputStream();
                    String json = arrange(inputStream);
                    Message message = handler.obtainMessage();
                    message.what = SUCCESS;
                    message.obj = json;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = handler.obtainMessage();
                    message.what = MISS;
                    message.obj = e.getMessage();
                    handler.sendMessage(message);
                }
            }
        }.start();
    }
    public String arrange(InputStream inputStream) throws IOException {
        int len = 0;
        byte[] b = new byte[1024];
        StringBuffer buffer = new StringBuffer();
        while((len = inputStream.read(b))!=-1){
            String s = new String(b,0,len);
            buffer.append(s);
        }
        return buffer.toString();
    }

    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SUCCESS:
                    String json = (String) msg.obj;
                    httpUtilsListener.getSuccessData(json);
                    break;
                case MISS:
                    String miss = (String) msg.obj;
                    httpUtilsListener.getMissData(miss);
                    break;
            }
        }
    }
    public interface HttpUtilsListener{
        void getSuccessData(String json);
        void getMissData(String miss);
    }
    public void setHttpUtilsListener(HttpUtilsListener httpUtilsListener){
        this.httpUtilsListener = httpUtilsListener;
    }

}
