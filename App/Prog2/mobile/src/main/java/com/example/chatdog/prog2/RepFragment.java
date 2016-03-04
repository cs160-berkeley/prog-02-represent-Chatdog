package com.example.chatdog.prog2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;

/**
 * Created by Chatdog on 2/29/2016.
 */
public class RepFragment extends Fragment {

    private SlidingUpPanelLayout panel;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.rep_fragment_object, container, false);
        Bundle args = getArguments();
        int index = args.getInt("repIndex");
        Representative r = MainActivity.repList.get(index);
        ((TextView) rootView.findViewById(R.id.repName)).setText(r.getTitle());
        ((ImageView) rootView.findViewById(R.id.repPic)).setImageResource(r.picture);
        ((TextView) rootView.findViewById(R.id.repParty)).setText(r.party);
        ((TextView) rootView.findViewById(R.id.repEmail)).setText(r.email);
        ((TextView) rootView.findViewById(R.id.repWebsite)).setText(r.website);
        ((TextView) rootView.findViewById(R.id.repTweet)).setText(r.lastTweet);
        ((TextView) rootView.findViewById(R.id.repTerm)).setText(r.termEnd);
        ((TextView) rootView.findViewById(R.id.repCommittee)).setText(r.committees);
        ((TextView) rootView.findViewById(R.id.repBills)).setText(r.recentBills);
        panel = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        panel.setCoveredFadeColor(android.R.color.transparent);
        return rootView;
    }

    public void expandPanel() {
        panel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

}
