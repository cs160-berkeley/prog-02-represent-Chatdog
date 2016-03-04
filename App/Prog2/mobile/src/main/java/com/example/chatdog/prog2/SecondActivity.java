package com.example.chatdog.prog2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Random;
import java.util.Set;

public class SecondActivity extends FragmentActivity {


    CollectionPagerAdapter CollectionPagerAdapter;
    ViewPager mViewPager;
    BroadcastReceiver receiver;
    BroadcastReceiver shakeReceiver;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        CollectionPagerAdapter =
                new CollectionPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(CollectionPagerAdapter);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra("RepName");
                for(int i = 0; i < MainActivity.repList.size(); i++) {
                    if (MainActivity.repList.get(i).getTitle().equals(s)) {
                        mViewPager.setCurrentItem(i);
                        CollectionPagerAdapter adapter = ((CollectionPagerAdapter) mViewPager.getAdapter());
                        RepFragment fragment = adapter.getFragment(i);
                        fragment.expandPanel();
                    }
                }
            }
        };
        shakeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MainActivity.shakeFlag = true;
                finish();
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter("Details")
        );
        LocalBroadcastManager.getInstance(this).registerReceiver((shakeReceiver),
                new IntentFilter("Shake")
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(shakeReceiver);
        super.onStop();
    }

}
