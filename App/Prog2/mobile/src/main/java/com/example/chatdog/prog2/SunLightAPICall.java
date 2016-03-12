package com.example.chatdog.prog2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.UserTimeline;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Chatdog on 3/9/2016.
 */
public class SunLightAPICall extends AsyncTask<URL, Integer, Long> {
    protected String result;
    JSONArray jsArray;
    protected MainActivity activity;
    HashMap<String, String> committeeMap;
    HashMap<String, String> billMap;
    HashMap<String, String> tweetMap;
    HashMap<String, Bitmap> imageMap;

    public SunLightAPICall(MainActivity activity)
    {
        this.activity = activity;
    }


    protected Long doInBackground(URL... url) {
        committeeMap = new HashMap<String, String>();
        billMap = new HashMap<String, String>();
        tweetMap = new HashMap<String, String>();
        imageMap = new HashMap<String, Bitmap>();

        long totalSize = 0;
        StringBuilder resultBuilder = new StringBuilder();
        try {
            InputStreamReader reader = new InputStreamReader(url[0].openStream());
            BufferedReader in = new BufferedReader(reader);
            String resultPiece;
            while ((resultPiece = in.readLine()) != null) {
                resultBuilder.append(resultPiece);
            }
            in.close();
            result = resultBuilder.toString();
            JSONObject jsonObject = new JSONObject(result);
            String results = jsonObject.getString("results");
            jsArray = new JSONArray(results);

            for(int i = 0; i < jsArray.length(); i++){
                jsonObject = (JSONObject) jsArray.get(i);
                final String id = jsonObject.getString("bioguide_id");
                final String twitterID = jsonObject.getString("twitter_id");
                String committeeRequest = "https://congress.api.sunlightfoundation.com/committees?member_ids=" + id + "&apikey=" + MainActivity.sunlightAPIKey;
                URL committeeURL = new URL(committeeRequest);
                reader = new InputStreamReader(committeeURL.openStream());
                in = new BufferedReader(reader);

                StringBuilder committeeBuilder = new StringBuilder();
                String committeePiece;
                while ((committeePiece = in.readLine()) != null) {
                    committeeBuilder.append(committeePiece);
                }
                in.close();
                committeeMap.put(id, committeeBuilder.toString());

                String billRequest = "https://congress.api.sunlightfoundation.com/bills?sponsor_id=" + id + "&apikey=" + MainActivity.sunlightAPIKey;
                URL billURL = new URL(billRequest);
                reader = new InputStreamReader(billURL.openStream());
                in = new BufferedReader(reader);
                StringBuilder billBuilder = new StringBuilder();
                String billPiece;
                while ((billPiece = in.readLine()) != null) {
                    billBuilder.append(billPiece);
                }
                in.close();
                billMap.put(id, billBuilder.toString());

                // Tweets, code adapted from https://twittercommunity.com/t/test-run-with-fabric-android/60673
                TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
                    @Override
                    public void success(Result<AppSession> appSessionResult) {
                        AppSession session = appSessionResult.data;
                        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                        twitterApiClient.getStatusesService().userTimeline(null, twitterID, 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
                            @Override
                            public void success(Result<List<Tweet>> listResult) {
                                for(Tweet tweet: listResult.data) {
                                    Log.d("Tweet", "Found Tweet");
                                    tweetMap.put(id, tweet.text);
                                }
                            }
                            @Override
                            public void failure(TwitterException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });

                //Images
                URL imageURL = new URL("https://theunitedstates.io/images/congress/450x550/" + id + ".jpg");
                Bitmap bmp = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                imageMap.put(id, bmp);



            }
            //County and Vote Data
            StringBuilder geoCodeBuilder = new StringBuilder();
            InputStreamReader geoReader = new InputStreamReader(url[1].openStream());
            in = new BufferedReader(geoReader);
            String geoPiece;
            while ((geoPiece = in.readLine()) != null) {
                geoCodeBuilder.append(geoPiece);
            }
            in.close();
            result = geoCodeBuilder.toString();
            jsonObject = new JSONObject(result);
            JSONArray gcArray = jsonObject.getJSONArray("results");
            jsonObject = (JSONObject) gcArray.get(0);
            gcArray = jsonObject.getJSONArray("address_components");

            String county = "Alameda";
            String state = "CA";
            for(int k = 0; k < gcArray.length(); k++){
                jsonObject = (JSONObject) gcArray.get(k);
                if(jsonObject.getString("types").contains("administrative_area_level_2")){
                    county = jsonObject.getString("long_name");

                }
                if(jsonObject.getString("types").contains("administrative_area_level_1")){
                    state = jsonObject.getString("short_name");
                }
            }
            Location loc = new Location();
            loc.name = county + ", " + state;
            JSONObject votes = activity.voteData.getJSONObject(loc.name);
            loc.romneyVote = Double.parseDouble(votes.getString("romney"));
            loc.obamaVote = Double.parseDouble(votes.getString("obama"));
            activity.currLocation = loc;
            Log.d("Loc", "Loc Name is " + loc.name);
            Log.d("Loc", "Loc Obama Vote is " + Double.toString(loc.obamaVote));
            Log.d("Loc", "Loc Romney Vote is " + Double.toString(loc.romneyVote));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalSize;
    }


    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(Long result) {

        try {
            for(int i = 0; i < jsArray.length(); i++){
                Representative rep = new Representative();
                JSONObject jsonObject = (JSONObject) jsArray.get(i);
                if(jsonObject.getString("title").equals("Rep")){
                    rep.position = "Representative";
                } else{
                    rep.position = "Senator";
                }
                String name = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name");
                rep.name = name;
                if(jsonObject.getString("party").equals("D")){
                    rep.party = "Democratic Party";
                } else if (jsonObject.getString("party").equals("R")){
                    rep.party = "Republican Party";
                } else{
                    rep.party = "Independent";
                }
                rep.email = jsonObject.getString("oc_email");
                rep.website = jsonObject.getString("website");
                rep.termEnd = jsonObject.getString("term_end");

                String id = jsonObject.getString("bioguide_id");
                JSONObject committeeJSON = new JSONObject(committeeMap.get(id));
                String results = committeeJSON.getString("results");
                JSONArray committeeArray = new JSONArray(results);
                rep.committees = "";
                for(int j = 0; j < Math.min(committeeArray.length(), 3); j++){
                    committeeJSON = (JSONObject) committeeArray.get(j);
                    if(!committeeJSON.getString("subcommittee").equals("true")){
                        rep.committees = rep.committees + "•  " + committeeJSON.getString("name") + "\n";
                    }
                }
                JSONObject billJSON = new JSONObject(billMap.get(id));
                results = billJSON.getString("results");
                JSONArray billArray = new JSONArray(results);
                rep.recentBills = "";
                for(int j = 0; j < Math.min(billArray.length(), 3); j++){
                    billJSON = (JSONObject) billArray.get(j);
                    if(!billJSON.getString("short_title").equals("null")) {
                        rep.recentBills = rep.recentBills + "•  " + billJSON.getString("short_title") + "\n";
                    }
                }

                rep.lastTweet = tweetMap.get(id);
                rep.picture = imageMap.get(id);

                MainActivity.repList.add(rep);

            }
            activity.onNetworkingComplete();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}