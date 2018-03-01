package com.temolin.WifiHotManager;


/**
 * @ClassName:  WiFiAPListener
 * @Description:  Listenning the state of wifi hot
 * @author: jajuan.wang
 * @date:   2015-06-09 00:52
 * version:1.0.0
 */
public interface WiFiAPListener {

    public static final int WIFI_AP_CLOSEING 		= 10;  //wifi hot is closeing
    public static final int WIFI_AP_CLOSE_SUCCESS 	= 11;  //wifi hot close success
    public static final int WIFI_AP_OPENING 		= 12;  //WiFi hot is opening
    public static final int WIFI_AP_OPEN_SUCCESS 	= 13;  //WiFi hot open success

    /**
     * the state of wifi hot changed
     * @param state
     */
    public void stateChanged(int state) ;
}