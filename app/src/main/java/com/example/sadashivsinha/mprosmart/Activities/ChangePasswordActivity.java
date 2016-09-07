package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText current_password, new_password, confirm_password;
    Button saveBtn;
    ProgressDialog pDialog;
    PreferenceManager pm;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        current_password = (EditText) findViewById(R.id.current_password);
        new_password = (EditText) findViewById(R.id.new_password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);

        saveBtn = (Button) findViewById(R.id.saveBtn);

        pm = new PreferenceManager(this);
        userId = pm.getString("userId");

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_password.getText().toString().isEmpty()) {
                    current_password.setError("This field cannot be empty");
                }
                else if (new_password.getText().toString().isEmpty()) {
                    new_password.setError("This field cannot be empty");
                }
                else if (confirm_password.getText().toString().isEmpty()) {
                    confirm_password.setError("This field cannot be empty");
                }
                else if (!new_password.getText().toString().equals(confirm_password.getText().toString())) {
                    confirm_password.setError("Passwords does not match");
                    current_password.setError("Passwords does not match");
                }
                else {
                    pDialog = new ProgressDialog(ChangePasswordActivity.this);
                    pDialog.setMessage("Change Password ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    class MyTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... params) {
                            changePassword();
                            return null;
                        }
                    }

                    new MyTask().execute();
                }
            }
        });
    }
    public void changePassword() {
        JSONObject object = new JSONObject();

        try {
            object.put("password", new_password.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(ChangePasswordActivity.this);

        String url = ChangePasswordActivity.this.getResources().getString(R.string.server_url) + "/changePassword?userId=\""+userId+"\"&password=\""+current_password.getText().toString()+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(ChangePasswordActivity.this, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        pDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jor);
        Intent intent = new Intent(ChangePasswordActivity.this, WelcomeActivity.class);
        startActivity(intent);

    }
}
