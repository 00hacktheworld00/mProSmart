package com.example.sadashivsinha.mprosmart.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.R;
import com.weiwangcn.betterspinner.library.BetterSpinner;

public class QualityStandardItemCreate extends AppCompatActivity {

    EditText text_criteria, text_uom, text_result , text_comments;
    BetterSpinner spinner_status;
    Button createBtn;
    String criteria, uom, result, comments, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_standard_item_create);

        text_criteria = (EditText) findViewById(R.id.text_criteria);
        text_uom = (EditText) findViewById(R.id.text_uom);
        text_result = (EditText) findViewById(R.id.text_result);
        text_comments = (EditText) findViewById(R.id.text_comments);

        spinner_status = (BetterSpinner) findViewById(R.id.spinner_status);

        createBtn = (Button) findViewById(R.id.createBtn);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"ACCEPT", "REJECT"});

        spinner_status.setAdapter(adapter);


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QualityStandardItemCreate.this, QualityStandardActivity.class);

                if (text_criteria.getText().toString().isEmpty())
                {
                    text_criteria.setError("Field cannot be empty");
                }
                else if (text_uom.getText().toString().isEmpty())
                {
                    text_uom.setError("Field cannot be empty");
                }
                else if (text_result.getText().toString().isEmpty())
                {
                    text_result.setError("Field cannot be empty");
                }
                else if (text_comments.getText().toString().isEmpty())
                {
                    text_comments.setError("Field cannot be empty");
                }
                else if (spinner_status.getText().toString().equals("Activity"))
                {
                    Toast.makeText(QualityStandardItemCreate.this, "Select Status first", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    criteria = text_criteria.getText().toString();
                    uom = text_uom.getText().toString();
                    result = text_result.getText().toString();
                    comments = text_comments.getText().toString();
                    status = spinner_status.getText().toString();

                    intent.putExtra("criteria", criteria);
                    intent.putExtra("uom", uom);
                    intent.putExtra("result", result);
                    intent.putExtra("comments", comments);
                    intent.putExtra("status", status);

                    Toast.makeText(QualityStandardItemCreate.this, "Item Created", Toast.LENGTH_SHORT).show();

                    startActivity(intent);

                }
            }
        });
    }
}
