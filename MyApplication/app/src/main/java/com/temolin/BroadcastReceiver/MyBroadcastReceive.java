package com.temolin.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.temolin.activity.MainActivity;


/**
 * Created by linyuan on 2017/2/18.
 */

public class MyBroadcastReceive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent m_intent=new Intent();
            m_intent.setClass(context, MainActivity.class);
            m_intent.addFlags(m_intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(m_intent);
        }
    }
}