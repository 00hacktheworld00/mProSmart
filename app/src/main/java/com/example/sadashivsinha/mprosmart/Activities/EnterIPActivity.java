package com.example.sadashivsinha.mprosmart.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.font.RobotoTextView;

public class EnterIPActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_ip);

        final EditText text_ip = (EditText) findViewById(R.id.text_ip);
        final EditText text_port = (EditText) findViewById(R.id.text_port);

        RobotoTextView btn_continue = (RobotoTextView) findViewById(R.id.btn_continue);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EnterIPActivity.this, LoginScreen.class);

                if(text_ip.getText().toString().isEmpty() && text_port.getText().toString().isEmpty()){
                    intent.putExtra("NEWADDRESS", "NO");
                    intent.putExtra("IPADDRESS", getResources().getString(R.string.server_upload_url));
                    intent.putExtra("PORTNO", getResources().getString(R.string.server_port));

                    startActivity(intent);
                }
                else if(!text_ip.getText().toString().isEmpty() && text_port.getText().toString().isEmpty())
                {
                    text_port.setError("Enter Port No.");
                }
                else if(text_ip.getText().toString().isEmpty() && !text_port.getText().toString().isEmpty())
                {
                    text_ip.setError("Enter IP Address");
                }
                else
                {
                    intent.putExtra("NEWADDRESS", "YES");
                    intent.putExtra("IPADDRESS", text_ip.getText().toString());
                    intent.putExtra("PORTNO", text_port.getText().toString());

                    Log.d("IP: ", text_ip.getText().toString() + ":" + text_port.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }
}
