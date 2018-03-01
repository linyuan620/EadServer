package com.temolin.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.temolin.application.Application;

import com.temolin.SerialPort;
import com.temolin.myapplication.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Arrays;

/**
 * Created by linyuan on 2017/2/25.
 */

public class SerialPortService extends Service {

    private Messenger mActivityMessenger;
    private Handler handler;
    public static int count;
    public static  String WindSpeed = new String("0");
    public static String WindDir = new String("0");
    public static String Temperature = new String("0");
    public static String Humidity = new String("0");
    public static String Pressure = new String("0");
    public static String Tsp = new String("0");
    public static String Pm10 = new String("0");
    public static String Pm25 = new String("0");
    public static String Noise = new String("0");

    private Messenger mServiceMessenger;

    protected Application mApplication;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    protected InputStream mInputStream;
    private ReadThread mReadThread;

    private void SerialPortService(){

    }
    public int getCount(){
        return count;
    }
//    public SensorValue getSensorValue(){
//        return sensorValue;
//    }
    //public SensorValue sensorValue = new SensorValue();
    public String data_frame = new String("");

    //帧总长117字节
    //Start<WindSpeed:1250;WindDir:2250;
    // Temperature:213;Humidity:495;
    // Pressure:1020;Tsp:200;Pm10:200;
    // Pm2.5:200;Noise:575;>End

//    public class SensorValue{
//        //解析的传感器值
//        public String WindSpeed = new String("0");
//        public String WindDir = new String("0");
//        public String Temperature = new String("0");
//        public String Humidity = new String("0");
//        public String Pressure = new String("0");
//        public String Tsp = new String("0");
//        public String Pm10 = new String("0");
//        public String Pm25 = new String("0");
//        public String Noise = new String("0");
//    }
    private class ReadThread extends Thread{
        @Override
        public void run() {
            super.run();
            while(!isInterrupted()){
                int size;
                try{
                    byte[] buffer =  new byte[64];
                    if(mInputStream == null)return;
                    size = mInputStream.read(buffer);
                    if(size>0){
                        onDataReceived(buffer,size);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private void DisplayError(int resourceId){
        final Intent intent = new Intent(this,SerialPortService.class);
        AlertDialog.Builder b =  new AlertDialog.Builder(this);
        b.setTitle("Error");
        b.setMessage(resourceId);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SerialPortService.this.stopService(intent);
            }
        });
        b.show();
    }

    public void onDataReceived(final byte[] buffer,final int size)
    {
        String original_data_frame = new String(buffer,0,size);

        if(size <100)
        {
            data_frame = data_frame.concat(original_data_frame);
            //System.out.println(data_frame);
        }else
        {
            data_frame = "";
        }
        if(data_frame.length() >100)
        {
            int m = data_frame.indexOf('<');
            int n = data_frame.indexOf('>');
            if(n-m > 90){
                String b = data_frame.substring(m+1,n);
                System.out.println(b);
                String[] aa = b.split("\\;");
                for (int i = 0 ; i <aa.length ; i++ ) {
                    //System.out.println(aa[i]);
                    String[] cc = aa[i].split("\\:");
                    for(int j = 0 ; j <cc.length ; j++){
                        //System.out.println(cc[j]);
                    }
                    //System.out.println(""+cc.length);
                    if(cc[0].equals("WindSpeed")){
                        StringBuilder stringBuilder  = new StringBuilder(cc[1]);
                        if(cc[1].length()>1) {
                            WindSpeed = stringBuilder.insert(cc[1].length()-1, ".").toString();
                        }
                        System.out.println("WindSpeed = "+WindSpeed+"m/s");
                    }
                    if(cc[0].equals("WindDir")){
                        StringBuilder stringBuilder  = new StringBuilder(cc[1]);
                        if(cc[1].length()>1) {
                            WindDir = stringBuilder.insert(cc[1].length()-1, ".").toString();
                        }
                        System.out.println("WindDir = "+WindDir+"°");
                    }
                    if(cc[0].equals("Temperature")){
                        StringBuilder stringBuilder  = new StringBuilder(cc[1]);
                        if(cc[1].length()>1) {
                            Temperature = stringBuilder.insert(cc[1].length()-1, ".").toString();
                        }
                        System.out.println("Temperature = "+Temperature+"°C");
                    }
                    if(cc[0].equals("Humidity")){
                        StringBuilder stringBuilder  = new StringBuilder(cc[1]);
                        if(cc[1].length()>1) {
                            Humidity = stringBuilder.insert(cc[1].length()-1, ".").toString();
                        }
                        System.out.println("Humidity = "+Humidity+"%RH");
                    }
                    if(cc[0].equals("Pressure")){
                        StringBuilder stringBuilder  = new StringBuilder(cc[1]);
                        if(cc[1].length()>1) {
                            Pressure = stringBuilder.insert(cc[1].length()-1, ".").toString();
                        }
                        System.out.println("Pressure = "+Pressure+"kPa");
                    }
                    if(cc[0].equals("Tsp")){
                        StringBuilder stringBuilder  = new StringBuilder(cc[1]);
                        if(cc[1].length()>1) {
                            Tsp = stringBuilder.insert(cc[1].length()-1, ".").toString();
                        }
                        System.out.println("Tsp = "+Tsp+"ug/M3");
                    }
                    if(cc[0].equals("Pm10")){
                        StringBuilder stringBuilder  = new StringBuilder(cc[1]);
                        if(cc[1].length()>1) {
                            Pm10 = stringBuilder.insert(cc[1].length()-1, ".").toString();
                        }
                        System.out.println("Pm10 = "+Pm10+"ug/M3");
                    }
                    if(cc[0].equals("Pm25")){
                        StringBuilder stringBuilder  = new StringBuilder(cc[1]);
                        if(cc[1].length()>1) {
                            Pm25 = stringBuilder.insert(cc[1].length()-1, ".").toString();
                        }
                        System.out.println("Pm25 = "+Pm25+"ug/M3");
                    }
                    if(cc[0].equals("Noise")){
                        StringBuilder stringBuilder  = new StringBuilder(cc[1]);
                        if(cc[1].length()>1) {
                            Noise = stringBuilder.insert(cc[1].length()-1, ".").toString();
                        }
                        System.out.println("Noise = "+Noise+"dB");
                    }
                }
            }
            data_frame = "";
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("SerialPortService OnBind!");
        return mServiceMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //HandlerThread 是android系统专门为Handler封装的一个线程类
        //通过HandlerThread创建的Handler便可以进行耗时操作了
        //HandlerThread是一个子线程,在调用handlerThread.getLooper()
        //之前必须先执行HandlerThread的start方法
        HandlerThread handlerThread = new HandlerThread("serviceCal");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x11){
                    if(mActivityMessenger == null){
                        mActivityMessenger = msg.replyTo;
                    }
                    //模拟耗时任务
                    try{
                        Thread.sleep(1000);
                        count++;
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    //System.out.println("Service Received Message!");
                    //发送结果回Activity
                    Message message = this.obtainMessage();
                    message.what = 0x12;
                    message.arg1 = count;
                    Bundle bundle = new Bundle();

                    bundle.putString("WINDSPEED",WindSpeed);
                    bundle.putString("WINDDIR",WindDir);
                    bundle.putString("TEMPERATURE",Temperature);
                    bundle.putString("HUMIDITY",Humidity);
                    bundle.putString("PRESSURE",Pressure);
                    bundle.putString("TSP",Tsp);
                    bundle.putString("PM10",Pm10);
                    bundle.putString("PM25",Pm25);
                    bundle.putString("NOISE",Noise);

                    message.setData(bundle);
                    try{
                        mActivityMessenger.send(message);
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        mServiceMessenger = new Messenger(handler);

        System.out.println("Serivce is Created");

        mApplication = (Application)getApplication();
        try{
            mSerialPort = mApplication.getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            //create a read thread
            mReadThread = new ReadThread();
            mReadThread.start();
        }catch (IOException e){
            e.printStackTrace();
        }catch(SecurityException e){
            DisplayError(R.string.error_security);
        }catch (InvalidParameterException e){
            DisplayError(R.string.error_configuration);
        }

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        if(mReadThread != null)
            mReadThread.interrupt();
        mApplication.closeSerialPort();
        mSerialPort = null;
        super.onDestroy();
    }
}

