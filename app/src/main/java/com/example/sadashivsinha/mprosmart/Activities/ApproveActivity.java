package com.example.sadashivsinha.mprosmart.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

public class ApproveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve);

        final PreferenceManager pm = new PreferenceManager(this);

        Button btn_approve, btn_reject;
        final TextView text_acc_no;
        final LinearLayout hideToLayout = (LinearLayout) findViewById(R.id.hideToLayout);

        text_acc_no = (TextView) findViewById(R.id.text_acc_no);

        btn_approve = (Button) findViewById(R.id.btn_approve);
        btn_reject = (Button) findViewById(R.id.btn_reject);

        btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_acc_no.setText("APPROVED");
                text_acc_no.setTextColor(getResources().getColor(R.color.accept_green));
                hideToLayout.setVisibility(View.GONE);

                pm.putString("approve", "APPROVED");
                Intent intent = new Intent(ApproveActivity.this, ViewPurchaseOrders.class);
                startActivity(intent);
            }
        });

        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_acc_no.setText("REJECTED");
                text_acc_no.setTextColor(getResources().getColor(R.color.fancy_red));
                hideToLayout.setVisibility(View.GONE);
                Intent intent = new Intent(ApproveActivity.this, ViewPurchaseOrders.class);
                startActivity(intent);

                pm.putString("approve", "REJECTED");
            }
        });
    }
}
