package com.example.sadashivsinha.mprosmart.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InventoryItemCreate extends AppCompatActivity {

    EditText text_date, text_received, text_issued, text_closing_bal;
    Button createBtn;
    String date, received, issued, closingBalance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_item_create);

        text_date = (EditText) findViewById(R.id.text_date);
        text_received = (EditText) findViewById(R.id.text_received);
        text_issued = (EditText) findViewById(R.id.text_issued);
        text_closing_bal = (EditText) findViewById(R.id.text_closing_bal);


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = sdf.format(c.getTime());

        text_date.setText(strDate);


        createBtn = (Button) findViewById(R.id.createBtn);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryItemCreate.this, InventoryManagementActivity.class);

                if(text_date.getText().toString().isEmpty())
                {
                    text_date.setError("Field cannot be empty");
                }
                else if(text_received.getText().toString().isEmpty())
                {
                    text_received.setError("Field cannot be empty");
                }
                else if(text_issued.getText().toString().isEmpty())
                {
                    text_issued.setError("Field cannot be empty");
                }
                else if(text_closing_bal.getText().toString().isEmpty())
                {
                    text_closing_bal.setError("Field cannot be empty");
                }

                else
                {
                    date = text_date.getText().toString();
                    received = text_received.getText().toString();
                    issued = text_issued.getText().toString();
                    closingBalance = text_closing_bal.getText().toString();

                    intent.putExtra("date",date);
                    intent.putExtra("received",received);
                    intent.putExtra("issued",issued);
                    intent.putExtra("closingBalance",closingBalance);

                    Toast.makeText(InventoryItemCreate.this, "Item Created", Toast.LENGTH_SHORT).show();

                    startActivity(intent);

                }

            }
        });
    }
}
