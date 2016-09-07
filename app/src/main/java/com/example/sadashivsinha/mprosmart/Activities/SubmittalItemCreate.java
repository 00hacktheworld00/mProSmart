package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

public class SubmittalItemCreate extends AppCompatActivity {

    EditText text_doc_type, text_short_desc, text_variation_contract, text_variation_doc;
    BetterSpinner spinner_status, spinner_type;
    ProgressDialog pDialog;
    String currentSubmittalId;
    ConnectionDetector cd;
    public static final String TAG = SubmittalItemCreate.class.getSimpleName();
    Boolean isInternetPresent = false;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submittal_item_create);

        Button createBtn, attachBtn;

        pm = new PreferenceManager(this);
        currentSubmittalId = pm.getString("submittalNo");

        createBtn = (Button) findViewById(R.id.createBtn);
        attachBtn = (Button) findViewById(R.id.attachBtn);

        attachBtn = (Button) findViewById(R.id.attachBtn);

        text_doc_type = (EditText) findViewById(R.id.text_doc_type);
        text_short_desc = (EditText) findViewById(R.id.text_short_desc);
        text_variation_contract = (EditText) findViewById(R.id.text_variation_contract);
        text_variation_doc = (EditText) findViewById(R.id.text_variation_doc);

        spinner_status = (BetterSpinner) findViewById(R.id.spinner_status);
        spinner_type = (BetterSpinner) findViewById(R.id.spinner_type);

        ArrayAdapter<String> adapterStatus = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[] {"Active", "Inactive"});

        spinner_status.setAdapter(adapterStatus);


        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[] {"Incoming", "Outgoing"});

        spinner_type.setAdapter(adapterType);

        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubmittalItemCreate.this, AttachmentActivity.class);
                startActivity(intent);
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveSubmittalLine();
            }
        });
    }

    public void saveSubmittalLine()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("docType", text_doc_type.getText().toString());
            object.put("description", text_short_desc.getText().toString());
            object.put("variationFromContract", text_variation_contract.getText().toString());
            object.put("variationFromContractDocDsc", text_variation_doc.getText().toString());
            object.put("status", spinner_status.getText().toString());
            object.put("submittalsId", currentSubmittalId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(SubmittalItemCreate.this);

        String url = SubmittalItemCreate.this.getResources().getString(R.string.server_url) + "/postSubmittalLineItems";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("RESPONSE SERVER : ", response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(SubmittalItemCreate.this, "Submittal Line created ID - " + response.getString("data"), Toast.LENGTH_SHORT).show();
                            }

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");
                        Toast.makeText(SubmittalItemCreate.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createSubmittalLinePending = pm.getBoolean("createSubmittalLinePending");

            if(createSubmittalLinePending)
            {
                Toast.makeText(SubmittalItemCreate.this, "Already a Submittal Line creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(SubmittalItemCreate.this, "Internet not currently available. Submittal Line will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectSubmittalLine", object.toString());
                pm.putString("urlSubmittalLine", url);
                pm.putString("toastMessageSubmittalLine", "Submittal Line Created");
                pm.putBoolean("createSubmittalLinePending", true);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
        if(pDialog!=null)
            pDialog.dismiss();

        Intent intent = new Intent(SubmittalItemCreate.this, SubmittalActivity.class);
        startActivity(intent);
    }
}