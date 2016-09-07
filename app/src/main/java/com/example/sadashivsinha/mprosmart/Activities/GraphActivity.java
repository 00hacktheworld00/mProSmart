package com.example.sadashivsinha.mprosmart.Activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sadashivsinha.mprosmart.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {


    float completedPercent , aCompleted, bCompleted, cCompleted;
    float delayPercent , aDelay, bDelay, cDelay, dDelay;
    float rejectPercentage, acceptPercentage , aRejectPercent, bRejectPercent, cRejectPercent, dRejectPercent, eRejectPercent, fRejectPercent,
            aAcceptPercent, bAcceptPercent, cAcceptPercent, dAcceptPercent, eAcceptPercent, fAcceptPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        BarChart chart_rejection = (BarChart) findViewById(R.id.chart_rejection);
        BarChart chart_complete = (BarChart) findViewById(R.id.chart_complete);
        BarChart chart_delay = (BarChart) findViewById(R.id.chart_delay);


        chart_delay.setDrawGridBackground(false);

        chart_delay.setDrawBarShadow(false);
        chart_delay.getAxisLeft().setTextColor(Color.rgb(255,255,255)); // left y-axis
        chart_delay.getXAxis().setTextColor(Color.rgb(255,255,255));
        chart_delay.getLegend().setTextColor(Color.rgb(255,255,255));
        chart_delay.getAxisRight().setTextColor(Color.rgb(255,255,255));

        aDelay = calculateAcceptPercent(50,25);
        bDelay = calculateAcceptPercent(50,30);
        cDelay = calculateAcceptPercent(100,80);


        BarData data = new BarData(getXAxisValues3(), getDataSet3());
        chart_delay.setData(data);
        chart_delay.setDescription("Delay Chart");
        chart_delay.animateXY(2000, 2000);
        chart_delay.invalidate();





        chart_complete.setDrawGridBackground(false);

        chart_complete.setDrawBarShadow(false);
        chart_complete.getAxisLeft().setTextColor(Color.rgb(255,255,255)); // left y-axis
        chart_complete.getXAxis().setTextColor(Color.rgb(255,255,255));
        chart_complete.getLegend().setTextColor(Color.rgb(255,255,255));
        chart_complete.getAxisRight().setTextColor(Color.rgb(255,255,255));
        aCompleted = calculateAcceptPercent(50,25);
        bCompleted = calculateAcceptPercent(50,30);
        cCompleted = calculateAcceptPercent(100,80);


        BarData data3 = new BarData(getXAxisValues2(), getDataSet2());
        chart_complete.setData(data3);
        chart_complete.setDescription("Completion Chart");
        chart_complete.animateXY(2000, 2000);
        chart_complete.invalidate();





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


        BarData data2 = new BarData(getXAxisValues(), getDataSet());
        chart_rejection.setData(data2);
        chart_rejection.setDescription("Rejection Chart");
        chart_rejection.animateXY(2000, 2000);
        chart_rejection.invalidate();
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

    private float calculateAcceptPercent(float quantityTotal, float quantityCompleted)
    {

        //perform calculation

        completedPercent = (quantityCompleted/quantityTotal)*100;

        return completedPercent;
    }



    private ArrayList<BarDataSet> getDataSet2() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(aCompleted, 0); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(bCompleted, 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(cCompleted, 2); // Mar
        valueSet1.add(v1e3);
        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Completed");
        barDataSet1.setColor(Color.rgb(88,143,193));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues2() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Activity-1");
        xAxis.add("Activity-2");
        xAxis.add("Activity-3");
        return xAxis;
    }

    private ArrayList<BarDataSet> getDataSet3() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(aDelay, 0); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(bDelay, 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(cDelay, 2); // Mar
        valueSet1.add(v1e3);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Delay");
        barDataSet1.setColor(Color.rgb(88,143,193));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues3() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Vendor-1");
        xAxis.add("Vendor-2");
        xAxis.add("Vendor-3");
        return xAxis;
    }

    private float calculateAcceptPercent3(float itemReceipts, float itemDelay)
    {

        //perform calculation

        delayPercent = (itemDelay/itemReceipts)*100;

        return delayPercent;
    }
}
