package com.example.chatdog.prog2;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.PutDataMapRequest;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * Created by joleary on 2/19/16.
 */
public class PhoneToWatchService extends Service {

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        final String info = extras.getString("REP_INFO");
        final int numPics = extras.getInt("NUM_PICS");
        final Location l = MainActivity.currLocation;
        if(l == null){
            return START_NOT_STICKY;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                mApiClient.connect();
                sendData(info, numPics, l);
            }
        }).start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void sendData(final String info, final int numPics, final Location locInfo){
        new Thread( new Runnable() {
            @Override
            public void run() {
                PutDataMapRequest dataMap = PutDataMapRequest.create("/Represent");
                dataMap.getDataMap().putString("Info", info);
                dataMap.getDataMap().putString("County", locInfo.name);
                dataMap.getDataMap().putDouble("OVote", locInfo.obamaVote);
                dataMap.getDataMap().putDouble("RVote", locInfo.romneyVote);
                dataMap.getDataMap().putLong("Time", new Date().getTime());

                for(int j = 0; j < MainActivity.repList.size(); j++) {
                    try {
                        Bitmap bitmap = MainActivity.repList.get(j).picture;
                        Asset asset = createAssetFromBitmap(bitmap);
                        dataMap.getDataMap().putAsset("Image" + Integer.toString(j), asset);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                dataMap.getDataMap().putInt("NumImages", numPics);

                PutDataRequest request = dataMap.asPutDataRequest();
                Log.w("T", "Sending Request");
                Wearable.DataApi.putDataItem(mApiClient, request);
            }
        }).start();
    }

    /* Creates Asset object from bitmap. Code from http://developer.android.com/training/wearables/data-layer/assets.html */
    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }
}
