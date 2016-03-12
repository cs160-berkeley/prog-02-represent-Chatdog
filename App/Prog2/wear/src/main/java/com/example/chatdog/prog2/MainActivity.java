package com.example.chatdog.prog2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "5lZ9TNyphJlQylYiZ40ZWWbGf";
    private static final String TWITTER_SECRET = "VMj6Xf0yFAyKVXrJ1XH1rTT1nbFpVWqkUPkhqu0CrbaA3kisay";

    static ArrayList<String> repPartyList;
    static ArrayList<String> repList;
    WearCollectionPagerAdapter myCollectionPagerAdapter;
    ViewPager mViewPager;
    static String countyName;
    static Double obamaVote;
    static Double romneyVote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        repList = new ArrayList<String>();
        repPartyList = new ArrayList<String>();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String repsName = extras.getString("REPS_NAME");
            String repsParty = extras.getString("REPS_PARTY");
            countyName = extras.getString("COUNTY");
            obamaVote = extras.getDouble("OBAMA_VOTE");
            romneyVote = extras.getDouble("ROMNEY_VOTE");
            String[] repsNameElements = repsName.split(";");
            String[] repsPartyElements = repsParty.split(";");

            for (int i = 0; i < repsNameElements.length; i++) {
                repList.add(repsNameElements[i]);
                repPartyList.add(repsPartyElements[i]);
            }


        }
        myCollectionPagerAdapter = new WearCollectionPagerAdapter(getSupportFragmentManager());

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mViewPager = (ViewPager) stub.findViewById(R.id.pager);
                mViewPager.setAdapter(myCollectionPagerAdapter);

            }
        });
    }
}