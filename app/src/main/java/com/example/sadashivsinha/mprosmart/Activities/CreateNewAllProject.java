package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateNewAllProject extends AppCompatActivity {

    ProgressDialog pDialog;
    EditText text_project_name, text_project_desc;
    Button createBtn;
    String currentUserId, currentCompanyId, currentDate;
    EditText address_one, address_two, city, state, country, pincode;
    LinearLayout hiddenLayoutProjectDetails, hiddenLayoutAddress;
    CheckBox checkbox_project_details, checkbox_address;
    CardView card_project_details, card_address;
    BetterSpinner spinner_currency;
    ImageButton btn_next_project, btn_next_address;
    ConnectionDetector cd;
    public static final String TAG = CreateNewAllProject.class.getSimpleName();
    Boolean isInternetPresent = false;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_all_project);

        pm = new PreferenceManager(CreateNewAllProject.this);

        text_project_name = (EditText) findViewById(R.id.text_project_name);
        text_project_desc = (EditText) findViewById(R.id.text_project_desc);

        //Load animation
        final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        address_one = (EditText) findViewById(R.id.address_one);
        address_two = (EditText) findViewById(R.id.address_two);
        city = (EditText) findViewById(R.id.city);
        state = (EditText) findViewById(R.id.state);
        country = (EditText) findViewById(R.id.country);
        pincode = (EditText) findViewById(R.id.pincode);

        currentUserId = pm.getString("userId");

        currentCompanyId = pm.getString("companyId");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        checkbox_project_details = (CheckBox) findViewById(R.id.checkbox_project_details);
        checkbox_address = (CheckBox) findViewById(R.id.checkbox_address);

        card_project_details = (CardView) findViewById(R.id.card_project_details);
        card_address = (CardView) findViewById(R.id.card_address);

        btn_next_project = (ImageButton) findViewById(R.id.btn_next_project);
        btn_next_address = (ImageButton) findViewById(R.id.btn_next_address);

        hiddenLayoutProjectDetails = (LinearLayout) findViewById(R.id.hiddenLayoutProjectDetails);
        hiddenLayoutAddress = (LinearLayout) findViewById(R.id.hiddenLayoutAddress);

        spinner_currency = (BetterSpinner) findViewById(R.id.spinner_currency);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateNewAllProject.this,
                android.R.layout.simple_dropdown_item_1line, new String[] {"INR", "$", "EURO"});
        spinner_currency.setAdapter(adapter);

        createBtn = (Button) findViewById(R.id.createBtn);





        card_project_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayoutProjectDetails.getVisibility()==View.GONE)
                {
                    hiddenLayoutProjectDetails.setVisibility(View.VISIBLE);
                    hiddenLayoutProjectDetails.startAnimation(slide_up);
                }

                else
                {
                    hiddenLayoutProjectDetails.startAnimation(slide_down);
                    hiddenLayoutProjectDetails.setVisibility(View.GONE);
                }
            }
        });

        btn_next_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(text_project_name.getText().toString().isEmpty())
                {
                    text_project_name.setError("Field cannot be empty");
                }
                else if(text_project_desc.getText().toString().isEmpty())
                {
                    text_project_desc.setError("Field cannot be empty");
                }
                else
                {
                    hiddenLayoutProjectDetails.startAnimation(slide_down);
                    hiddenLayoutProjectDetails.setVisibility(View.GONE);
                    hiddenLayoutAddress.setVisibility(View.VISIBLE);
                    hiddenLayoutAddress.startAnimation(slide_up);
                    checkbox_project_details.setChecked(true);
                }
            }
        });


        card_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayoutAddress.getVisibility()==View.GONE)
                {
                    hiddenLayoutAddress.setVisibility(View.VISIBLE);
                    hiddenLayoutAddress.startAnimation(slide_up);
                }

                else
                {
                    hiddenLayoutAddress.startAnimation(slide_down);
                    hiddenLayoutAddress.setVisibility(View.GONE);
                }
            }
        });

        btn_next_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(address_one.getText().toString().isEmpty())
                {
                    address_one.setError("Field cannot be empty");
                }
                else if(address_two.getText().toString().isEmpty())
                {
                    address_two.setError("Field cannot be empty");
                }
                else if(city.getText().toString().isEmpty())
                {
                    city.setError("Field cannot be empty");
                }
                else if(state.getText().toString().isEmpty())
                {
                    state.setError("Field cannot be empty");
                }
                else if(country.getText().toString().isEmpty())
                {
                    country.setError("Field cannot be empty");
                }
                else if(pincode.getText().toString().isEmpty())
                {
                    pincode.setError("Field cannot be empty");
                }
                else
                {
                    hiddenLayoutAddress.setVisibility(View.GONE);
                    hiddenLayoutAddress.startAnimation(slide_down);
                    checkbox_address.setChecked(true);
                }
            }
        });






        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(text_project_name.getText().toString().isEmpty())
                {
                    text_project_name.setError("Field cannot be empty");
                    hiddenLayoutProjectDetails.setVisibility(View.VISIBLE);
                }
                else if(text_project_desc.getText().toString().isEmpty())
                {
                    text_project_desc.setError("Field cannot be empty");
                    hiddenLayoutProjectDetails.setVisibility(View.VISIBLE);
                }
                else if(address_one.getText().toString().isEmpty())
                {
                    address_one.setError("Field cannot be empty");
                    hiddenLayoutAddress.setVisibility(View.VISIBLE);
                }
                else if(address_two.getText().toString().isEmpty())
                {
                    address_two.setError("Field cannot be empty");
                    hiddenLayoutAddress.setVisibility(View.VISIBLE);
                }
                else if(city.getText().toString().isEmpty())
                {
                    city.setError("Field cannot be empty");
                    hiddenLayoutAddress.setVisibility(View.VISIBLE);
                }
                else if(state.getText().toString().isEmpty())
                {
                    state.setError("Field cannot be empty");
                    hiddenLayoutAddress.setVisibility(View.VISIBLE);
                }
                else if(country.getText().toString().isEmpty())
                {
                    country.setError("Field cannot be empty");
                    hiddenLayoutAddress.setVisibility(View.VISIBLE);
                }
                else if(pincode.getText().toString().isEmpty())
                {
                    pincode.setError("Field cannot be empty");
                    hiddenLayoutAddress.setVisibility(View.VISIBLE);
                }
                else if(spinner_currency.getText().toString().isEmpty())
                {
                    Toast.makeText(CreateNewAllProject.this, "Select Currency", Toast.LENGTH_SHORT).show();
                    hiddenLayoutProjectDetails.setVisibility(View.VISIBLE);
                }
                else
                {
                    pm.putString("currency", spinner_currency.getText().toString());

                    pDialog = new ProgressDialog(CreateNewAllProject.this);
                    pDialog.setMessage("Sending Data ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();
                    prepareItems();

//                    class MyTask extends AsyncTask<Void, Void, Void> {
//
//                        @Override
//                        protected Void doInBackground(Void... params) {
//                            prepareItems();
//                            return null;
//                        }
//                    }
//
//                    new MyTask().execute();

                }
                  }
        });
    }

    public void prepareItems() {
        JSONObject object = new JSONObject();

        try {
            object.put("projectName", text_project_name.getText().toString());
            object.put("projectDescription", text_project_desc.getText().toString());
            object.put("createdDate", currentDate);
            object.put("createdBy", currentUserId);
            object.put("companyId", currentCompanyId);
            object.put("photoUrl", "http://www.hw.ac.uk/schools/energy-geoscience-infrastructure-society/img/Construction-Management-Innovation.jpg");
            object.put("addressLine1", address_one.getText().toString());
            object.put("addressLine2", address_two.getText().toString());
            object.put("city", city.getText().toString());
            object.put("state", state.getText().toString());
            object.put("country", country.getText().toString());
            object.put("pin", pincode.getText().toString());
            object.put("totalBudget", "0");
            object.put("currencyCode", spinner_currency.getText().toString());

            Log.d("object project :", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(CreateNewAllProject.this);

        String url = CreateNewAllProject.this.getResources().getString(R.string.server_url) + "/postProject";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response project :", response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(CreateNewAllProject.this, "Project Created. ID - "+ response.getString("data"), Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(CreateNewAllProject.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
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
        if(pDialog!=null)
            pDialog.dismiss();

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createProjectPending = pm.getBoolean("createProjectPending");

            if(createProjectPending)
            {
                Toast.makeText(CreateNewAllProject.this, "Already a project creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(CreateNewAllProject.this, "Internet not currently available. Project will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectProject", object.toString());
                pm.putString("urlProject", url);
                pm.putString("toastMessageProject", "Project Created");
                pm.putBoolean("createProjectPending", true);
            }
        }
        else
        {
            requestQueue.add(jor);
        }

//        requestQueue.add(jor);


        Intent intent = new Intent(CreateNewAllProject.this, NewAllProjects.class);
        startActivity(intent);
    }
}
