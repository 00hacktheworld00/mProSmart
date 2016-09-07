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

public class QualityChecklistItemCreate extends AppCompatActivity {

    EditText text_subject , text_comments;
    BetterSpinner spinner_status;
    Button createBtn;
    String subject, comments, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_checklist_item_create);

        text_subject = (EditText) findViewById(R.id.text_subject);
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
                Intent intent = new Intent(QualityChecklistItemCreate.this, QualityCheckListActivity.class);

                if (text_subject.getText().toString().isEmpty())
                {
                    text_subject.setError("Field cannot be empty");
                }
                else if (text_comments.getText().toString().isEmpty())
                {
                    text_comments.setError("Field cannot be empty");
                }
                else if (spinner_status.getText().toString().equals("Activity"))
                {
                    Toast.makeText(QualityChecklistItemCreate.this, "Select Status first", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    subject = text_subject.getText().toString();
                    comments = text_comments.getText().toString();
                    status = spinner_status.getText().toString();

                    intent.putExtra("subject", subject);
                    intent.putExtra("comments", comments);
                    intent.putExtra("status", status);

                    Toast.makeText(QualityChecklistItemCreate.this, "Item Created", Toast.LENGTH_SHORT).show();

                    startActivity(intent);

                }
            }
        });
    }
}
