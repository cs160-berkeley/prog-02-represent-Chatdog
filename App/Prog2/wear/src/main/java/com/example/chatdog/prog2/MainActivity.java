package com.example.chatdog.prog2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
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