package com.example.chatdog.prog2;

import android.graphics.Bitmap;

/**
 * Created by Chatdog on 3/3/2016.
 */
public class Representative {
    public String name;
    public String party;
    public String position;
    public String email;
    public String website;
    public String lastTweet;
    public String termEnd;
    public String committees;
    public String recentBills;
    public Bitmap picture;

    public Representative(){

    }

    public String getTitle(){
        return position + " " + name;
    }
}
