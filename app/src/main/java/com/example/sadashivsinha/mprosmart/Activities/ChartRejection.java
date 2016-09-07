package com.example.sadashivsinha.mprosmart.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.sadashivsinha.mprosmart.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class ChartRejection extends AppCompatActivity {

    float rejectPercentage, acceptPercentage , aRejectPercent, bRejectPercent, cRejectPercent, dRejectPercent, eRejectPercent, fRejectPercent,
            aAcceptPercent, bAcceptPercent, cAcceptPercent, dAcceptPercent, eAcceptPercent, fAcceptPercent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_rejection);
        BarChart chart = (BarChart) findViewById(R.id.chart);

        aRejectPercent = calculateRejectPercent(50,50,25);
        bRejectPercent = calculateRejectPercent(100,50,20);
        cRejectPercent = calculateRejectPercent(150,100,20);
        dRejectPercent = calculateRejectPercent(75,25,10);
        eRejectPercent = calculateRejectPercent(10,8,2);
        fRejectPercent = calculateRejectPercent(100,20,10);

        aAcceptPercent = calculateAcceptPercent(50,50,25);
        bAcceptPercent = calculateAcceptPercent(100,50,30);
        cAcceptPercent = calculateAcceptPercent(150,100,80);
        dAcceptPercent = calculateAcceptPercent(75,25,15);
        eAcceptPercent = calculateAcceptPercent(10,8,6);
        fAcceptPercent = calculateAcceptPercent(100,20,10);


        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.setDescription("Rejection Chart");
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(aAcceptPercent, 0); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(bAcceptPercent, 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(cAcceptPercent, 2); // Mar
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(dAcceptPercent, 3); // Apr
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(eAcceptPercent, 4); // May
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(fAcceptPercent, 5); // Jun
        valueSet1.add(v1e6);

        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        BarEntry v2e1 = new BarEntry(aRejectPercent, 0); // Jan
        valueSet2.add(v2e1);
        BarEntry v2e2 = new BarEntry(bRejectPercent, 1); // Feb
        valueSet2.add(v2e2);
        BarEntry v2e3 = new BarEntry(cRejectPercent, 2); // Mar
        valueSet2.add(v2e3);
        BarEntry v2e4 = new BarEntry(dRejectPercent, 3); // Apr
        valueSet2.add(v2e4);
        BarEntry v2e5 = new BarEntry(eRejectPercent, 4); // May
        valueSet2.add(v2e5);
        BarEntry v2e6 = new BarEntry(fRejectPercent, 5); // Jun
        valueSet2.add(v2e6);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Accepted");
        barDataSet1.setColor(Color.rgb(113,186,81));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Rejected");
        barDataSet2.setColor(Color.rgb(238,84,58));


        //to set multi-color graphs
//        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("01");
        xAxis.add("02");
        xAxis.add("03");
        xAxis.add("04");
        xAxis.add("05");
        xAxis.add("06");
        return xAxis;
    }

    private float calculateRejectPercent(float quantityOrdered, float quantityReceived, float quantityRejected)
    {

        //perform calculation

        rejectPercentage = (quantityRejected/quantityReceived)*100;

        return rejectPercentage;
    }

    private float calculateAcceptPercent(float quantityOrdered, float quantityReceived, float quantityAccepted)
    {

        //perform calculation

        acceptPercentage = (quantityAccepted/quantityReceived)*100;

        return acceptPercentage;
    }
}