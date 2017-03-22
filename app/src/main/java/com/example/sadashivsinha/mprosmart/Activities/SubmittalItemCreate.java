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

import java.util.Arrays;
import java.util.List;

public class SubmittalItemCreate extends AppCompatActivity {

    EditText text_doc_type, text_short_desc, text_variation_contract, text_variation_doc;
    BetterSpinner spinner_status;
    ProgressDialog pDialog;
    String currentSubmittalId;
    ConnectionDetector cd;
    public static final String TAG = SubmittalItemCreate.class.getSimpleName();
    Boolean isInternetPresent = false;
    PreferenceManager pm;
    String SERVER_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submittal_item_create);

        Button createBtn, attachBtn;

        pm = new PreferenceManager(this);
        pm.putString("totalImageUrls", "");

        currentSubmittalId = pm.getString("submittalNo");

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        SERVER_URL = pm.getString("SERVER_UPLOAD_URL") + "/upload/file-upload";;

        createBtn = (Button) findViewById(R.id.createBtn);
        attachBtn = (Button) findViewById(R.id.attachBtn);

        text_doc_type = (EditText) findViewById(R.id.text_doc_type);
        text_short_desc = (EditText) findViewById(R.id.text_short_desc);
        text_variation_contract = (EditText) findViewById(R.id.text_variation_contract);
        text_variation_doc = (EditText) findViewById(R.id.text_variation_doc);

        spinner_status = (BetterSpinner) findViewById(R.id.spinner_status);

        ArrayAdapter<String> adapterStatus = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[] {"Active", "Inactive"});

        spinner_status.setAdapter(adapterStatus);

        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubmittalItemCreate.this, AttachmentActivity.class);
                intent.putExtra("class", "SubmittalLine");
                startActivity(intent);
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(text_doc_type.getText().toString().isEmpty())
                {
                    text_doc_type.setError("Field cannot be empty");
                }
                else if(text_short_desc.getText().toString().isEmpty())
                {
                    text_short_desc.setError("Field cannot be empty");
                }
                else if(text_variation_contract.getText().toString().isEmpty())
                {
                    text_variation_contract.setError("Field cannot be empty");
                }
                else if(text_variation_doc.getText().toString().isEmpty())
                {
                    text_variation_doc.setError("Field cannot be empty");
                }
                else if(spinner_status.getText().toString().isEmpty())
                {
                    Toast.makeText(SubmittalItemCreate.this, "Select Item Status", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    saveSubmittalLine();
                }
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
            object.put("submittalRegisterType", "");

            if(pm.getString("className").equals("SubmittalLine"))
            {
                object.put("noOfAttachments", pm.getString("totalImageUrlSize"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(SubmittalItemCreate.this);

        String url = SubmittalItemCreate.this.pm.getString("SERVER_URL") + "/postSubmittalLineItems";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("RESPONSE SERVER : ", response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                String totalImageUrls = pm.getString("totalImageUrls");
                                if(pm.getString("className").equals("SubmittalLine") && isInternetPresent
                                        && !totalImageUrls.isEmpty())
                                {
                                    uploadImage(response.getString("data"), totalImageUrls);
                                }
                                else
                                {
                                    if(pDialog!=null)
                                        pDialog.dismiss();

                                    Toast.makeText(SubmittalItemCreate.this, "Submittal Line Item created. ID - "+response.getString("data"), Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(SubmittalItemCreate.this, SubmittalActivity.class);
                                    startActivity(intent);
                                }
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

                if(pDialog!=null)
                    pDialog.dismiss();

                Intent intent = new Intent(SubmittalItemCreate.this, SubmittalActivity.class);
                startActivity(intent);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
    }

    public void uploadImage(final String id, String totalUrls) {

        final List<String> imageList = Arrays.asList(totalUrls.split(","));
        String seperateImageUrl;


        for (int i = 0; i < imageList.size(); i++) {

            final int count = i;
            JSONObject object = new JSONObject();

            try {

                seperateImageUrl = imageList.get(i);

                object.put("lineNo", id);
                object.put("url", seperateImageUrl);

                Log.d("JSON OBJ SENT", object.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestQueue requestQueue = Volley.newRequestQueue(SubmittalItemCreate.this);

            String url = SubmittalItemCreate.this.pm.getString("SERVER_URL") + "/postSubmittalLineFiles";

            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                Log.d("RESPONSE SERVER : ", response.toString());

                                if (response.getString("msg").equals("success")) {
                                    if (count == imageList.size() - 1) {
                                        Toast.makeText(SubmittalItemCreate.this, "Submittal Line created ID - " + id, Toast.LENGTH_SHORT).show();

                                        if (pDialog != null)
                                            pDialog.dismiss();
                                        Intent intent = new Intent(SubmittalItemCreate.this, SubmittalActivity.class);
                                        startActivity(intent);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //response success message display
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", "Error");
                            Toast.makeText(SubmittalItemCreate.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            requestQueue.add(jor);
        }
    }

}