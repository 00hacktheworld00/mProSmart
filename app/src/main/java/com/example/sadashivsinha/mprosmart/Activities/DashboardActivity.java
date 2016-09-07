package com.example.sadashivsinha.mprosmart.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sadashivsinha.mprosmart.R;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button delayBtn, rejectionBtn, completedBtn;

        delayBtn = (Button) findViewById(R.id.delayBtn);
        rejectionBtn = (Button) findViewById(R.id.rejectionBtn);
        completedBtn = (Button) findViewById(R.id.completedBtn);

        delayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ChartDelay.class);
                startActivity(intent);
            }
        });

        rejectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ChartRejection.class);
                startActivity(intent);
            }
        });

        completedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ChartComplete.class);
                startActivity(intent);
            }
        });
    }
}
