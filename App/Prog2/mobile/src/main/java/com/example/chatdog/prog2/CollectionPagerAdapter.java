package com.example.chatdog.prog2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.HashMap;

/**
 * Created by Chatdog on 2/29/2016.
 */
public class CollectionPagerAdapter extends FragmentStatePagerAdapter {
    HashMap<Integer, RepFragment> FragmentReferenceMap;
    public CollectionPagerAdapter(FragmentManager fm) {
        super(fm);
        FragmentReferenceMap = new HashMap<Integer, RepFragment>();
    }

    public RepFragment getFragment(int key) {
        return FragmentReferenceMap.get(key);
    }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        FragmentReferenceMap.remove(position);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new RepFragment();
        Bundle args = new Bundle();
        args.putInt("repIndex", i);
        fragment.setArguments(args);
        FragmentReferenceMap.put(i, (RepFragment) fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return MainActivity.repList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }

}
