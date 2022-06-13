package com.example.wayout_ver_01.Activity.Chat.Chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class msgCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String str = intent.getStringExtra("msg");
        Log.e("//===========//","================================================");
        Log.e("","\n"+"[ 받은 메세지 SmsReceiver : "+str+" ]");
        Log.e("//===========//","================================================");


    }
}