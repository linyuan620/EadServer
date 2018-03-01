package com.temolin.activity;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.temolin.WifiHotManager.WiFiAPService;
import com.temolin.myapplication.R;
import com.temolin.service.SerialPortService;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.AndServerBuild;
import com.temolin.handler.AndServerPingHandler;
import com.temolin.handler.AndServerTestHandler;

import com.temolin.WifiHotManager.WiFiAPListener;
import com.temolin.WifiHotManager.WifiHotUtil;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    /*** AndServer*/
    private AndServer mAndServer;
    private static String TAG = "MainActivity";

    private final String defaultHotName = "Skyray-Hot";
    private final String defaultHotPwd = "skyray1992";

    private boolean isWifiOpen = false;
    private WifiHotUtil wifiHotUtil;
    private Button btnWifiHot;

    //Service 端的messenger对象
    public Messenger mServiceMessenger;
    //Activity 端的messenger对象
    public Messenger mActivityMessenger;
    //activity 端的handler处理Service中的消息

    public Handler handler =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x12){

                final  TextView textView_WindSpeed = (TextView)findViewById(R.id.WindSpeed);
                final  TextView textView_WindDir = (TextView)findViewById(R.id.WindDir);
                final  TextView textView_Tmp = (TextView)findViewById(R.id.Tmp);
                final  TextView textView_Hum = (TextView)findViewById(R.id.Hum);
                final  TextView textView_Pre = (TextView)findViewById(R.id.Pre);
                final  TextView textView_Tsp = (TextView)findViewById(R.id.Tsp);
                final  TextView textView_Pm10 = (TextView)findViewById(R.id.Pm10);
                final  TextView textView_Pm2_5 = (TextView)findViewById(R.id.Pm2_5);
                final  TextView textView_Noise = (TextView)findViewById(R.id.Noise);

                textView_WindSpeed.setText("风速:  "+msg.getData().getString("WINDSPEED")+" m/s");
                textView_WindDir.setText("风向:  "+msg.getData().getString("WINDDIR")+" °");
                textView_Tmp.setText("温度:  "+msg.getData().getString("TEMPERATURE")+" °C");
                textView_Hum.setText("湿度:  "+msg.getData().getString("HUMIDITY")+" %RH");
                textView_Pre.setText("压力:  "+msg.getData().getString("PRESSURE")+" kPa");
                textView_Tsp.setText("Tsp:  "+msg.getData().getString("TSP")+" ug/m3");
                textView_Pm10.setText("Pm10:  "+msg.getData().getString("PM10")+" ug/m3");
                textView_Pm2_5.setText("Pm2.5:  "+msg.getData().getString("PM25")+" ug/m3");
                textView_Noise.setText("噪声:  "+msg.getData().getString("NOISE")+" dB");

                Toast.makeText(MainActivity.this,""+ msg.arg1,Toast.LENGTH_SHORT).show();
            }
        }
    };

    private ServiceConnection conn =  new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("Service Connected");
            mServiceMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("Service DisConnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //Activity端的Messenger
                if(mActivityMessenger == null){
                    mActivityMessenger = new Messenger(handler);
                }
                //创建消息
                Message message = Message.obtain();
                message.what = 0x11;
                //设定消息要回应的Messenger
                message.replyTo = mActivityMessenger;

                try{
                    Thread.sleep(3000);
                    //通过ServiceMessenger将消息发送到Service中的Handler
                    mServiceMessenger.send(message);
                    //System.out.println("Timer sendMessage!");
                }catch(RemoteException e) {
                    e.printStackTrace();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        },0,1000);

        //绑定Service
        Intent intent = new Intent(MainActivity.this,SerialPortService.class);
        bindService(intent,conn, Service.BIND_AUTO_CREATE);

        if (mAndServer == null || !mAndServer.isRunning()) {// 服务器没启动。
            startAndServer();// 启动服务器。
        } else {
            Toast.makeText(MainActivity.this, "AndServer已经启动，请不要重复启动。", Toast.LENGTH_LONG).show();
        }

        //start WiFiAPService
        WiFiAPService.startService(this);
        //init WifiHotUtil
        wifiHotUtil = new WifiHotUtil(this);
        //开机开启WifiHot
        openWifiHot();

        btnWifiHot = (Button)findViewById(R.id.btnWifiHot);
        btnWifiHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWifiOpen) {
                    openWifiHot();
                }else {
                    closeWifiHot();
                }
            }
        });

        WiFiAPService.addWiFiAPListener(new WiFiAPListener() {
            @Override
            public void stateChanged(int state) {
                Log.i(TAG, "state= "+state);
                switch (state) {
                    case WiFiAPListener.WIFI_AP_OPENING:
                        btnWifiHot.setText(getResources().getString(R.string.opening_wifi_hot));
                        break;
                    case WiFiAPListener.WIFI_AP_OPEN_SUCCESS:
                        isWifiOpen = true;
                        Toast.makeText(MainActivity.this, "WiFi打开成功"+defaultHotName,
                                Toast.LENGTH_LONG).show();
                        btnWifiHot.setText(getResources().getString(R.string.close_wifi_hot));
                        break;
                    case WiFiAPListener.WIFI_AP_CLOSEING:
                        btnWifiHot.setText(getResources().getString(R.string.closeing_wifi_hot));
                        break;
                    case WiFiAPListener.WIFI_AP_CLOSE_SUCCESS:
                        isWifiOpen = false;
                        Toast.makeText(MainActivity.this, "WiFi关闭成功"+defaultHotName,
                                Toast.LENGTH_LONG).show();
                        btnWifiHot.setText(getResources().getString(R.string.open_wifi_hot));
                        openWifiHot();//靠此行代码不停的开启wifi热点
                        break;
                    default:
                        break;
                }
            }
        });

        findViewById(R.id.btn_start).setOnClickListener(onClickListener);
        findViewById(R.id.btn_stop).setOnClickListener(onClickListener);

        final Button buttonQuit = (Button)findViewById(R.id.ButtonQuit);
        buttonQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });
    }

    /*** 按钮监听。*/
    private View.OnClickListener onClickListener = new View.OnClickListener() {

        private String TAG = "AndServerTestHandler";
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_start) {
                if (mAndServer == null || !mAndServer.isRunning()) {// 服务器没启动。
                    startAndServer();// 启动服务器。
                } else {
                    Toast.makeText(MainActivity.this, "AndServer已经启动，请不要重复启动。", Toast.LENGTH_LONG).show();
                }
            } else if (v.getId() == R.id.btn_stop) {
                if (mAndServer == null || !mAndServer.isRunning()) {
                    Toast.makeText(MainActivity.this, "AndServer还没有启动。", Toast.LENGTH_LONG).show();
                } else {// 关闭服务器。
                    mAndServer.close();
                    Toast.makeText(MainActivity.this, "AndServer已经停止。", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    // 这里为了简单就写在Activity中了，强烈建议写在服务中。
    /*** 启动服务器。*/
    private void startAndServer() {
        if (mAndServer == null || !mAndServer.isRunning()) {

            AndServerBuild andServerBuild = AndServerBuild.create();
            andServerBuild.setPort(8888);// 指定端口号。

            // 添加普通接口。
            andServerBuild.add("ping", new AndServerPingHandler());// 到时候在浏览器访问是：http://localhost:4477/ping
            andServerBuild.add("tsp", new AndServerTestHandler());// 到时候在浏览器访问是：http://localhost:4477/test

            // 添加接受客户端上传文件的接口。
            //andServerBuild.add("upload", new AndServerUploadHandler());// 到时候在浏览器访问是：http://localhost:4477/upload
            mAndServer = andServerBuild.build();

            // 启动服务器。
            mAndServer.launch();
            Toast.makeText(this, "AndServer已经成功启动", Toast.LENGTH_LONG).show();
        }
    }
    /*** open the wifi hot with the setted hotName and password*/
    private void openWifiHot() {
        wifiHotUtil.startWifiAp(defaultHotName, defaultHotPwd);
    }

    /*** close wifi hot*/
    private void closeWifiHot() {
        wifiHotUtil.closeWifiAp();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAndServer != null && mAndServer.isRunning()) {
            mAndServer.close();
        }
    }
}
