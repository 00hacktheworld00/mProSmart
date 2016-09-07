package com.example.sadashivsinha.mprosmart.Activities;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewSwitcher;

import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

public class MyProfileActivity extends AppCompatActivity {

    ViewSwitcher switcher;
    Button editBtn;
    EditText name, age, status, mobile, city, state, country;
    HelveticaRegular text_name, text_age, text_status, text_mobile, text_city, text_state, text_country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        editBtn = (Button) findViewById(R.id.editBtn);
        switcher = (ViewSwitcher) findViewById(R.id.my_switcher);

        name = (EditText) findViewById(R.id.name);
        age = (EditText) findViewById(R.id.age);
        status = (EditText) findViewById(R.id.status);
        mobile = (EditText) findViewById(R.id.mobile);
        city = (EditText) findViewById(R.id.city);
        state = (EditText) findViewById(R.id.state);
        country = (EditText) findViewById(R.id.country);

        text_name = (HelveticaRegular) findViewById(R.id.text_name);
        text_age = (HelveticaRegular) findViewById(R.id.text_age);
        text_status = (HelveticaRegular) findViewById(R.id.text_status);
        text_mobile = (HelveticaRegular) findViewById(R.id.text_mobile);
        text_city = (HelveticaRegular) findViewById(R.id.text_city);
        text_state = (HelveticaRegular) findViewById(R.id.text_state);
        text_country = (HelveticaRegular) findViewById(R.id.text_country);

        editBtn.setOnClickListener(new View.OnClickListener() {
            int count=0;
            @Override
            public void onClick(View v) {
                if (count == 0)
                {

                    if(name.getText().toString().isEmpty())
                    {
                        name.setError("Field Cannot be empty");
                    }
                    else if(age.getText().toString().isEmpty())
                    {
                        age.setError("Field Cannot be empty");
                    }
                    else if(status.getText().toString().isEmpty())
                    {
                        status.setError("Field Cannot be empty");
                    }
                    else if(mobile.getText().toString().isEmpty())
                    {
                        mobile.setError("Field Cannot be empty");
                    }
                    else if(state.getText().toString().isEmpty())
                    {
                        state.setError("Field Cannot be empty");
                    }
                    else if(city.getText().toString().isEmpty())
                    {
                        city.setError("Field Cannot be empty");
                    }
                    else if(country.getText().toString().isEmpty())
                    {
                        country.setError("Field Cannot be empty");
                    }


                    else
                    {

                        switcher.showNext(); //or switcher.showPrevious();
                        editBtn.setText("EDIT");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            editBtn.setBackgroundTintList(getResources().getColorStateList(R.color.fancy_red));
                        }
                        count++;

                        text_name.setText(name.getText().toString());
                        text_age.setText(age.getText().toString());
                        text_status.setText(status.getText().toString());
                        text_mobile.setText(mobile.getText().toString());
                        text_city.setText(city.getText().toString());
                        text_state.setText(state.getText().toString());
                        text_country.setText(country.getText().toString());
                    }
                }
                else
                {
                        switcher.showNext(); //or switcher.showPrevious();
                        name.requestFocus();
                        editBtn.setText("SAVE");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            editBtn.setBackgroundTintList(getResources().getColorStateList(R.color.success_green));
                        }
                        count--;

                }
            }
        });
    }
}
