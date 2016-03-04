package com.example.chatdog.prog2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class PhoneListenerService extends WearableListenerService {
    LocalBroadcastManager broadcaster;
    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if (messageEvent.getPath().equalsIgnoreCase("/Represent")) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            String type = "Details";
            sendResult(type, value);
        } else if (messageEvent.getPath().equalsIgnoreCase("/Shake")){
            String type = "Shake";
            sendResult(type, "Shake");
        }else{
            super.onMessageReceived( messageEvent );
        }
    }


    public void sendResult(String type, String message) {
        Intent intent;
        if(message != null) {
            if(type.equalsIgnoreCase("Details")) {
                intent = new Intent("Details");
                intent.putExtra("RepName", message);
            } else{
                intent = new Intent("Shake");
            }
            broadcaster.sendBroadcast(intent);
        }
    }
}
