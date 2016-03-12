package com.example.chatdog.prog2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.json.JSONObject;

import au.com.bytecode.opencsv.CSVReader;
import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "sYZQ7OMWRF8cfSAG5aXjW8qdG";
    private static final String TWITTER_SECRET = "ZKR0XZL8m08EHNxwY7za0lHRKa9z1KkSs0ZVHX6q2QT2BEg3JJ";

    static int zip;
    static Location currLocation;
    static ArrayList<Representative> repList;
    static boolean shakeFlag;
    static JSONObject voteData;
    ArrayList<String[]> coordinateList;
    BroadcastReceiver shakeReceiver;
    GoogleApiClient mGoogleApiClient;

    final static String sunlightAPIKey = "&apikey=12c5fc848624469092d79a7fd011cb76";
    final static String googleGeocodeKey = "&key=AIzaSyCt74Kj6AaCHGODW-3LpAOF_jBaVt67ppk";

    private void sendInfo(){
        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        String nameString = "";
        String partyString = "";
        String picString = "";
        Representative r;
        for(int j = 0; j < repList.size(); j++){
            r = repList.get(j);
            nameString += r.getTitle() + ";";
            partyString += r.party + ";";
        }
        sendIntent.putExtra("REP_INFO", nameString + "/" + partyString);
        sendIntent.putExtra("NUM_PICS", repList.size());
        startService(sendIntent);

    }

    public void onShake(){
        int choice = new Random().nextInt(coordinateList.size());
        String[] coordinate = coordinateList.get(choice);
        String latitude = coordinate[0];
        String longitude = coordinate[1];
        getDataFromCoordinates(latitude, longitude);
    }

    protected void onNetworkingComplete(){
        sendInfo();
        Intent i = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(i);
    }

    public void goButtonOnClick(View v){
        zip = Integer.parseInt(((EditText) findViewById(R.id.zipCode)).getText().toString());
        if (checkZip()) {
            //currLocation = "Bradford, Florida";
            //getRepresentatives(currLocation);

            repList.clear();
            String domain = "https://congress.api.sunlightfoundation.com";
            String request = "/legislators/locate?zip=" + Integer.toString(zip);
            String geocodeRequest = "https://maps.googleapis.com/maps/api/geocode/json?components=postal_code:" + Integer.toString(zip);
            URL url = null;
            URL gcURL = null;
            try{
                url = new URL(domain + request + sunlightAPIKey);
                gcURL = new URL(geocodeRequest + googleGeocodeKey);

            } catch(Exception e) {
                e.printStackTrace();
            }
            new SunLightAPICall(this).execute(url, gcURL);
        }
    }

    public void detectLocationButtonOnClick(View v){
        try {
            android.location.Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation == null) {
                Log.d("Error", "Last location is null");
                return;
            }
            String latitude = String.valueOf(mLastLocation.getLatitude());
            String longitude = String.valueOf(mLastLocation.getLongitude());
            Log.d("Location", "Latitude is " + latitude + " Longitude is " + longitude);
            getDataFromCoordinates(latitude, longitude);

        } catch(SecurityException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    private void getDataFromCoordinates(String latitude, String longitude){
        repList.clear();
        try {
            String domain = "https://congress.api.sunlightfoundation.com";
            String request = "/legislators/locate?latitude=" + latitude + "&longitude=" + longitude;
            String geocodeRequest = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude;
            URL url = new URL(domain + request + sunlightAPIKey);
            URL gcURL = new URL(geocodeRequest + googleGeocodeKey);
            new SunLightAPICall(this).execute(url, gcURL);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean checkZip(){
        //check if zip code is valid... 2B Stub
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        coordinateList = new ArrayList<String[]>();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //code adapted from http://stackoverflow.com/questions/13814503/reading-a-json-file-in-android
        String json = null;
        try {
            InputStream is = getAssets().open("newelectioncounty2012.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            voteData = new JSONObject(json);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream csvStream =  getAssets().open("coordinates.csv");
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
            CSVReader csvReader = new CSVReader(csvStreamReader);
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                coordinateList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        shakeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onShake();
            }
        };
        shakeFlag = false;
        repList = new ArrayList<Representative>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        LocalBroadcastManager.getInstance(this).registerReceiver((shakeReceiver),
                new IntentFilter("Shake")
        );
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(shakeFlag){
            shakeFlag = false;
            onShake();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(shakeReceiver);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // We are now connected!
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // We are not connected anymore!
    }

}
