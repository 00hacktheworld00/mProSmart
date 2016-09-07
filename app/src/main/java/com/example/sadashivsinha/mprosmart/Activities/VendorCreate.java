package com.example.sadashivsinha.mprosmart.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.R;

public class VendorCreate extends AppCompatActivity {

    EditText first_name, last_name, phone, email, building_no, street_name, locality, state, country, zipcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_create);

        Button createBtn = (Button) findViewById(R.id.createBtn);

        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        phone = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);
        building_no = (EditText) findViewById(R.id.building_no);
        street_name = (EditText) findViewById(R.id.street_name);
        locality = (EditText) findViewById(R.id.locality);
        state = (EditText) findViewById(R.id.state);
        country = (EditText) findViewById(R.id.country);
        zipcode = (EditText) findViewById(R.id.zipcode);


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(first_name.getText().toString().isEmpty())
                {
                    first_name.setError("Field cannot be empty.");
                }
                else if(last_name.getText().toString().isEmpty())
                {
                    last_name.setError("Field cannot be empty.");
                }
                else if(phone.getText().toString().isEmpty())
                {
                    phone.setError("Field cannot be empty.");
                }
                else if(email.getText().toString().isEmpty())
                {
                    email.setError("Field cannot be empty.");
                }
                else if(building_no.getText().toString().isEmpty())
                {
                    building_no.setError("Field cannot be empty.");
                }
                else if(street_name.getText().toString().isEmpty())
                {
                    street_name.setError("Field cannot be empty.");
                }
                else if(locality.getText().toString().isEmpty())
                {
                    locality.setError("Field cannot be empty.");
                }
                else if(state.getText().toString().isEmpty())
                {
                    state.setError("Field cannot be empty.");
                }
                else if(country.getText().toString().isEmpty())
                {
                    country.setError("Field cannot be empty.");
                }
                else if(zipcode.getText().toString().isEmpty())
                {
                    zipcode.setError("Field cannot be empty.");
                }
                else
                {
                    Intent intent = new Intent(VendorCreate.this, VendorActivity.class);
                    Toast.makeText(VendorCreate.this, "Vendor Line Item craeted", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }
        });
    }
}
