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

public class ChartComplete extends AppCompatActivity {

    float completedPercent , aCompleted, bCompleted, cCompleted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_complete);
        BarChart chart = (BarChart) findViewById(R.id.chart);

        chart.setDrawGridBackground(false);

        chart.setDrawBarShadow(false);
        chart.getAxisLeft().setTextColor(Color.rgb(255,255,255)); // left y-axis
        chart.getXAxis().setTextColor(Color.rgb(255,255,255));
        chart.getLegend().setTextColor(Color.rgb(255,255,255));
        chart.getAxisRight().setTextColor(Color.rgb(255,255,255));
        aCompleted = calculateAcceptPercent(50,25);
        bCompleted = calculateAcceptPercent(50,30);
        cCompleted = calculateAcceptPercent(100,80);


        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.setDescription("Completion Chart");
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }

    private ArrayList<BarDataSet> getDataSet() {
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

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Activity-1");
        xAxis.add("Activity-2");
        xAxis.add("Activity-3");
        return xAxis;
    }

    private float calculateAcceptPercent(float quantityTotal, float quantityCompleted)
    {

        //perform calculation

        completedPercent = (quantityCompleted/quantityTotal)*100;

        return completedPercent;
    }
}