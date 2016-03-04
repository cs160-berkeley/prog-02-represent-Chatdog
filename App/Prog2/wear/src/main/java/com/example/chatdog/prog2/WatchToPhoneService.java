package com.example.chatdog.prog2;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class WatchToPhoneService extends Service {
    private GoogleApiClient mApiClient;
    @Override
    public void onCreate() {
        super.onCreate();
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null){
            return 0;
        }
        Bundle extras = intent.getExtras();
        String type = extras.getString("TYPE");
        final String key;
        final String msg;
        if(type.equalsIgnoreCase("Details")) {
            msg = extras.getString("REP_NAME");
            key = "/Represent";
        } else{
            msg = "Shake";
            key = "/Shake";
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                mApiClient.connect();
                sendMessage(key, msg);
            }
        }).start();
        return START_STICKY;
    }

    private void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                Log.w("T", "Sending Message to Phone");
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                }
            }
        }).start();
    }
}
