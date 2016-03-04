package com.example.chatdog.prog2;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class VoteActivity extends Activity {
    private RelativeLayout layout;
    private PieChart chart;
    private ArrayList<String> x;
    private ArrayList<Integer> y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x = new ArrayList<String>();
        y = new ArrayList<Integer>();
        setContentView(R.layout.activity_vote);
        layout = (RelativeLayout) findViewById(R.id.mainLayout);
        chart = new PieChart(this);
        layout.addView(chart);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        lp.addRule(RelativeLayout.CENTER_IN_PARENT);

        chart.setLayoutParams(lp);
        Legend l = chart.getLegend();
        l.setEnabled(false);
        chart.setCenterText("2012 Presidential Vote\n" + MainActivity.countyName);
        chart.setCenterTextSize(11);
        chart.setUsePercentValues(true);
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(47);
        chart.setTransparentCircleRadius(47);
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);

        x.add("Barack Obama");
        x.add("Mitt Romney");
        x.add("Other");

        y.add((MainActivity.obamaVote).intValue());
        y.add(MainActivity.romneyVote.intValue());
        y.add(100 - MainActivity.obamaVote.intValue() - MainActivity.romneyVote.intValue());

        ArrayList<String> xEntries = new ArrayList<String>();
        ArrayList<Entry> yEntries = new ArrayList<Entry>();
        for (int i = 0; i < y.size(); i++) {
            yEntries.add(new Entry(y.get(i), i));
        }
        for (int i = 0; i < x.size(); i++) {
            xEntries.add(x.get(i));
        }

        PieDataSet dataSet = new PieDataSet(yEntries, "Vote Percentage");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.PASTEL_COLORS) {
            colors.add(c);
        }

        dataSet.setColors(colors);

        PieData data = new PieData(xEntries, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(8f);
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);
        chart.highlightValues(null);
        chart.invalidate();
        }

}
