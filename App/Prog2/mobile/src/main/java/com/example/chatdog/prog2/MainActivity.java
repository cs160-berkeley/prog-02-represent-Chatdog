package com.example.chatdog.prog2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    static int zip;
    static String currLocation;
    static ArrayList<Representative> repList;
    static HashMap<String, Location> locTable;
    static HashMap<Location, ArrayList<Representative>> repMapping;
    static boolean shakeFlag;
    BroadcastReceiver shakeReceiver;

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
            picString += Integer.toString(r.picture) + ";";
        }
        sendIntent.putExtra("REP_INFO", nameString + "/" + partyString);
        sendIntent.putExtra("REP_PIC", picString);
        startService(sendIntent);

    }

    public void onShake(){
        Set<String> locSet = locTable.keySet();
        int choice = new Random().nextInt(locSet.size());
        int count = 0;
        for(String loc : locSet) {
            if (count == choice) {
                currLocation = loc;
                break;
            }
            count++;
        }
        Log.d("T", "Switching to location " + currLocation);
        getRepresentatives(currLocation);
        Intent i = new Intent(MainActivity.this, SecondActivity.class);
        sendInfo();
        startActivity(i);
    }

    public void goButtonOnClick(View v){
        zip = Integer.parseInt(((EditText) findViewById(R.id.zipCode)).getText().toString());
        if (checkZip()) {
            currLocation = "Bradford, Florida";
            getRepresentatives(currLocation);
            Intent i = new Intent(MainActivity.this, SecondActivity.class);
            sendInfo();
            startActivity(i);
        }
    }

    public void detectLocationButtonOnClick(View v){
        currLocation = "Alameda, California";
        getRepresentatives(currLocation);
        Intent i = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(i);
        sendInfo();
    }

    protected boolean checkZip(){
        //check if zip code is valid... 2B Stub
        return true;
    }



    protected void getRepresentatives(String location){
        repList.clear();
        Location l = locTable.get(location);
        ArrayList<Representative> reps = repMapping.get(l);
        for(int i = 0; i < reps.size(); i++){
            repList.add(reps.get(i));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        shakeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onShake();
            }
        };
        shakeFlag = false;
        repList = new ArrayList<Representative>();
        locTable = new HashMap<String, Location>();
        repMapping = new HashMap<Location, ArrayList<Representative>>();

        Location l1 = new Location();
        l1.name = "Alameda, California";
        l1.obamaVote = 78.9;
        l1.romneyVote = 18.2;
        locTable.put("Alameda, California", l1);

        Location l2 = new Location();
        l2.name = "Bradford, Florida";
        l2.obamaVote = 28.5;
        l2.romneyVote = 70.5;
        locTable.put("Bradford, Florida", l2);

        ArrayList<Representative> alamedaReps = new ArrayList<Representative>();
        Representative r1 = new Representative();
        r1.name = "Barbara Boxer";
        r1.position = "Senator";
        r1.party = "Democratic Party";
        r1.email = "Email: Barbara Boxer's Email";
        r1.website = "Website: Barbara Boxer's Website";
        r1.lastTweet = "Last Tweet: Barbara Boxer's Last Tweet";
        r1.termEnd = "Term Ends: Barbara Boxer's Term End";
        r1.committees = "Committees: \n    Committee 1\n    Committee 2\n    Committee 3";
        r1.recentBills = "Recent Bills: \n    List of Recent Bills Here";
        r1.picture = R.drawable.sen1apic;
        alamedaReps.add(r1);

        Representative r2 = new Representative();
        r2.name = "Dianne Feinstein";
        r2.position = "Senator";
        r2.party = "Democratic Party";
        r2.email = "Email: Dianne Feinstein's Email";
        r2.website = "Website: Dianne Feinstein's Website";
        r2.lastTweet = "Last Tweet: Dianne Feinstein's Last Tweet";
        r2.termEnd = "Term Ends: Dianne Feinstein's Term End";
        r2.committees = "Committees: \n    Committee 1\n    Committee 2\n    Committee 3";
        r2.recentBills = "Recent Bills: \n    List of Recent Bills Here";
        r2.picture = R.drawable.sen1bpic;
        alamedaReps.add(r2);

        Representative r3 = new Representative();
        r3.name = "Eric Swalwell";
        r3.position = "Representative";
        r3.party = "Democratic Party";
        r3.email = "Email: Eric Swalwell's Email";
        r3.website = "Website: Eric Swalwell's Website";
        r3.lastTweet = "Last Tweet: Eric Swalwell's Last Tweet";
        r3.termEnd = "Term Ends: Eric Swalwell's Term End";
        r3.committees = "Committees: \n    Committee 1\n    Committee 2\n    Committee 3";
        r3.recentBills = "Recent Bills: \n    List of Recent Bills Here";
        r3.picture = R.drawable.rep1pic;
        alamedaReps.add(r3);

        repMapping.put(l1, alamedaReps);

        ArrayList<Representative> bradfordReps = new ArrayList<Representative>();
        Representative r4 = new Representative();
        r4.name = "Bill Nelson";
        r4.position = "Senator";
        r4.party = "Democratic Party";
        r4.email = "Email: Bill Nelson's Email";
        r4.website = "Website: Bill Nelson's Website";
        r4.lastTweet = "Last Tweet: Bill Nelson's Last Tweet";
        r4.termEnd = "Term Ends: Bill Nelson's Term End";
        r4.committees = "Committees: \n    Committee 1\n    Committee 2\n    Committee 3";
        r4.recentBills = "Recent Bills: \n    List of Recent Bills Here";
        r4.picture = R.drawable.sen2apic;
        bradfordReps.add(r4);

        Representative r5 = new Representative();
        r5.name = "Marco Rubio";
        r5.position = "Senator";
        r5.party = "Republican Party";
        r5.email = "Email: Marco Rubio's Email";
        r5.website = "Website: Marco Rubio's Website";
        r5.lastTweet = "Last Tweet: Marco Rubio's Last Tweet";
        r5.termEnd = "Term Ends: Marco Rubio's Term End";
        r5.committees = "Committees: \n    Committee 1\n    Committee 2\n    Committee 3";
        r5.recentBills = "Recent Bills: \n    List of Recent Bills Here";
        r5.picture = R.drawable.sen2bpic;
        bradfordReps.add(r5);

        Representative r6 = new Representative();
        r6.name = "Ted Yoho";
        r6.position = "Representative";
        r6.party = "Republican Party";
        r6.email = "Email: Ted Yoho's Email";
        r6.website = "Website: Ted Yoho's Website";
        r6.lastTweet = "Last Tweet: Ted Yoho's Last Tweet";
        r6.termEnd = "Term Ends: Ted Yoho's Term End";
        r6.committees = "Committees: \n    Committee 1\n    Committee 2\n    Committee 3";
        r6.recentBills = "Recent Bills: \n    List of Recent Bills Here";
        r6.picture = R.drawable.rep2pic;
        bradfordReps.add(r6);

        repMapping.put(l2, bradfordReps);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
}
