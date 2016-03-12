package com.example.chatdog.prog2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;


/**
 * Created by David Ni on 3/1/2016.
 */
public class WearRepFragment extends Fragment {
    String repName;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wear_rep_fragment_object, container, false);
        Bundle args = getArguments();
        repName = args.getString("RepID");
        int picNum = args.getInt("PicNumber");
        ((TextView) rootView.findViewById(R.id.repName)).setText(repName);
        ((TextView) rootView.findViewById(R.id.repParty)).setText(args.getString("RepParty"));

        boolean voteButtonFlag = args.getBoolean("Vote Button");
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getActivity().openFileInput("im" + Integer.toString(picNum)));
            ((CircularImageView)(rootView.findViewById(R.id.repPicture))).setImageBitmap(bitmap);
        } catch (Exception e) {
            ((CircularImageView)(rootView.findViewById(R.id.repPicture))).setImageResource(R.drawable.capitol);
            e.printStackTrace();
        }
        rootView.findViewById(R.id.repPicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(getActivity().getBaseContext(), WatchToPhoneService.class);
                sendIntent.putExtra("REP_NAME", repName);
                sendIntent.putExtra("TYPE", "Details");
                getActivity().startService(sendIntent);
            }
        });
        if(voteButtonFlag) {
            rootView.findViewById(R.id.voteButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w("T", "Vote Button Clicked");
                    Intent sendIntent = new Intent(getActivity().getBaseContext(), VoteActivity.class);
                    getActivity().startActivity(sendIntent);
                }
            });
            ((ImageView)(rootView.findViewById(R.id.logoImage))).setPadding(0,0,0,0);
        } else{
            rootView.findViewById(R.id.voteButton).setVisibility(View.GONE);
            ((CircularImageView)(rootView.findViewById(R.id.repPicture))).setImageResource(R.drawable.capitol2);
            ((ImageView)(rootView.findViewById(R.id.logoImage))).setImageResource(R.drawable.represent3);
        }
        return rootView;
    }
}
