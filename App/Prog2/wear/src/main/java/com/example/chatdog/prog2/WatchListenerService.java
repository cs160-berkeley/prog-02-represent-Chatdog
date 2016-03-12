package com.example.chatdog.prog2;

import android.content.Context;
import com.google.android.gms.common.ConnectionResult;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import java.util.concurrent.TimeUnit;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;


public class WatchListenerService extends WearableListenerService {
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
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.w("T", "Data Received on watch.");
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals("/Represent")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                int numPics = dataMapItem.getDataMap().getInt("NumImages");
                for(int i = 0; i < numPics; i++) {
                    Asset pictureAsset = dataMapItem.getDataMap().getAsset("Image" + Integer.toString(i));
                    Bitmap bitmap = loadBitmapFromAsset(pictureAsset);
                    bitmap = Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() - bitmap.getWidth()) / 3, bitmap.getWidth(), bitmap.getWidth());
                    String fileName = "im" + Integer.toString(i);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    try {
                        FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                String info = dataMapItem.getDataMap().getString("Info");
                String countyName = dataMapItem.getDataMap().getString("County");
                Double obamaVote = dataMapItem.getDataMap().getDouble("OVote");
                Double romneyVote = dataMapItem.getDataMap().getDouble("RVote");
                String[] infoElements = info.split("/");
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("REPS_NAME", infoElements[0]);
                intent.putExtra("REPS_PARTY", infoElements[1]);
                intent.putExtra("COUNTY", countyName);
                intent.putExtra("OBAMA_VOTE", obamaVote);
                intent.putExtra("ROMNEY_VOTE", romneyVote);
                startActivity(intent);
            }
        }
    }

    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                mApiClient.blockingConnect(3000, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            Log.w("Error", "Result not Successful");
            return null;
        }
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mApiClient, asset).await().getInputStream();
        mApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w("T", "Requested an unknown Asset.");
            return null;
        }
        return BitmapFactory.decodeStream(assetInputStream);
    }
}