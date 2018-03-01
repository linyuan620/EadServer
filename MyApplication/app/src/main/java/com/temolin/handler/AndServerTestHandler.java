/*
 * Copyright © Yan Zhenjie. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.temolin.handler;

import android.util.Log;

import com.temolin.service.SerialPortService;
import com.yanzhenjie.andserver.AndServerRequestHandler;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.protocol.HttpContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * <p>其它测试接口。</p>
 * Created on 2016/6/13.
 *
 * @author Yan Zhenjie.
 */
public class AndServerTestHandler implements AndServerRequestHandler {
    private String TAG = "AndServerTestHandler";
    private SerialPortService mserialPortService = new SerialPortService();

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        // 拿到客户端参数key-value。
        Map<String, String> params = HttpRequestParser.parse(request);

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            stringBuilder.append(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
        System.out.println("客户端提交的参数：" + stringBuilder.toString());
//-----------------------------------------------------------------------------
        System.out.println(""+mserialPortService.getCount());
        String WindSpeed = mserialPortService.WindSpeed;
        String WindDir = mserialPortService.WindDir;
        String Temperature = mserialPortService.Temperature;
        String Humidity = mserialPortService.Humidity;
        String Pressure = mserialPortService.Pressure;
        String Tsp = mserialPortService.Tsp;
        String Pm10 = mserialPortService.Pm10;
        String Pm25 = mserialPortService.Pm25;
        String Noise = mserialPortService.Noise;
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月dd日hh-mm-ss");
        String date = sDateFormat.format(new java.util.Date());
        String htmlContent =
        "<html>"+
        "<head>"+
        "<meta http-equiv=\"refresh\" content=\"3\">"+
        "<title>扬尘与噪声在线监测平台</title>"+
        "</head>"+

        "<style type=\"text/css\">"+
        "table {"+
        "font-size:20px;}"+
        "</style>"+

        "<body>"+
        "<h2>扬尘与噪声实时值</h2>"+
        "<table cellspacing=\"10\">"+

        "<tr>"+
        "<td>时间</td>"+
        "<td>"+date+"</td>"+
        "</tr>"+

        "<tr>"+
        "<td>风速</td>"+
        "<td>"+WindSpeed+" m/s</td>"+
        "</tr>"+

        "<tr>"+
        "<td>风向</td>"+
        "<td>"+WindDir+" °"+"</td>"+
        "</tr>"+

        "<tr>"+
        "<td>温度</td>"+
        "<td>"+Temperature+" ℃</td>"+
        "</tr>"+

        "<tr>"+
        "<td>湿度</td>"+
        "<td>"+Humidity+"% RH</td>"+
        "</tr>"+

        "<tr>"+
        "<td>压力</td>"+
        "<td>"+Pressure+" kPa</td>"+
        "</tr>"+

        "<tr>"+
        "<td>Tsp</td>"+
        "<td>"+Tsp+" ug/m3</td>"+
        "</tr>"+

        "<tr>"+
        "<td>Pm10</td>"+
        "<td>"+Pm10+" ug/m3</td>"+
        "</tr>"+

        "<tr>"+
        "<td>Pm2.5</td>"+
        "<td>"+Pm25+" ug/m3</td>"+
        "</tr>"+

        "<tr>"+
        "<td>噪声</td>"+
        "<td>"+Noise+" dB</td>"+
        "</tr>"+

        "</body>"+

        "</html>";

        byte body[] = htmlContent.getBytes();
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(body);
        response.setEntity(byteArrayEntity);
        // 如果要更新UI，这里用Handler或者广播发送过去。
    }
}
