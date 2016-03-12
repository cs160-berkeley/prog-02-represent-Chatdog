package com.example.chatdog.prog2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Chatdog on 3/1/2016.
 */
public class WearCollectionPagerAdapter extends FragmentStatePagerAdapter {
    public WearCollectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new WearRepFragment();
        Bundle args = new Bundle();
        if(MainActivity.repList.isEmpty()){
            args.putString("RepID", "");
            args.putBoolean("Vote Button", false);
        } else {
            args.putString("RepID", MainActivity.repList.get(i));
            args.putString("RepParty", MainActivity.repPartyList.get(i));
            args.putBoolean("Vote Button", true);
            args.putInt("PicNumber", i);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return Math.max(MainActivity.repList.size(), 1);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }

}

