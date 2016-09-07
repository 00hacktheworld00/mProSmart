package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddResourceActivity extends AppCompatActivity {

    ImageButton btn_next_resource, btn_next_address;
    LinearLayout hiddenLayoutResourceDetails, hiddenLayoutAddress;
    Button createBtn;
    CardView card_resource, card_address;
    CheckBox checkbox_resource, checkbox_address;

    EditText text_first_name, text_last_name, text_designation, text_email, text_phone, text_rate_per_hour;
    BetterSpinner spinner_currency, spinner_res_type;
    EditText text_house_no, text_street_name, text_city, text_state, text_country, text_zipcode;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    String currentResource, resId;
    String firstName, lastName, resourceTypeId, designationId, ratePerHour, currencyId, emailId, phone, houseNo ,streetName,
            locality, state, country;
    HelveticaRegular title;
    String[] currencyArray, resTypeArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resource);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        //Load animation
        final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        text_first_name = (EditText) findViewById(R.id.text_first_name);
        text_last_name = (EditText) findViewById(R.id.text_last_name);
        text_designation = (EditText) findViewById(R.id.text_designation);
        text_email = (EditText) findViewById(R.id.text_email);
        text_phone = (EditText) findViewById(R.id.text_phone);
        text_rate_per_hour = (EditText) findViewById(R.id.text_rate_per_hour);

        text_house_no = (EditText) findViewById(R.id.text_house_no);
        text_street_name = (EditText) findViewById(R.id.text_street_name);
        text_city = (EditText) findViewById(R.id.text_city);
        text_state = (EditText) findViewById(R.id.text_state);
        text_country = (EditText) findViewById(R.id.text_country);
        text_zipcode = (EditText) findViewById(R.id.text_zipcode);

        checkbox_resource = (CheckBox) findViewById(R.id.checkbox_resource);
        checkbox_address = (CheckBox) findViewById(R.id.checkbox_address);

        card_resource = (CardView) findViewById(R.id.card_resource);
        card_address = (CardView) findViewById(R.id.card_address);

        btn_next_resource = (ImageButton) findViewById(R.id.btn_next_resource);
        btn_next_address = (ImageButton) findViewById(R.id.btn_next_address);

        spinner_currency = (BetterSpinner) findViewById(R.id.spinner_currency);
        spinner_res_type = (BetterSpinner) findViewById(R.id.spinner_res_type);

        currencyArray = new String[3];
        currencyArray[0] = "INR";
        currencyArray[1] = "$";
        currencyArray[2] = "EURO";

        resTypeArray = new String[2];
        resTypeArray[0] = "EMPLOYEE";
        resTypeArray[1] = "CONTRACT";

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddResourceActivity.this,
                android.R.layout.simple_dropdown_item_1line, currencyArray);
        spinner_currency.setAdapter(adapter);

        ArrayAdapter<String> adapterResType = new ArrayAdapter<String>(AddResourceActivity.this,
                android.R.layout.simple_dropdown_item_1line, resTypeArray);
        spinner_res_type.setAdapter(adapterResType);

        hiddenLayoutResourceDetails = (LinearLayout) findViewById(R.id.hiddenLayoutResourceDetails);
        hiddenLayoutAddress = (LinearLayout) findViewById(R.id.hiddenLayoutAddress);

        createBtn = (Button) findViewById(R.id.createBtn);

        title = (HelveticaRegular) findViewById(R.id.title);

        //insert previous data if editing
        if(getIntent().hasExtra("edit"))
        {
            if(getIntent().getStringExtra("edit").equals("yes"))
            {
                title.setText("Edit Details");
                createBtn.setText("SAVE");

                currentResource = getIntent().getStringExtra("resId");

                pDialog = new ProgressDialog(AddResourceActivity.this);
                pDialog.setMessage("Getting Details ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                class MyTask extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... params) {
                        setResourceDetails();
                        return null;
                    }

                }
                new MyTask().execute();
            }
        }

        card_resource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayoutResourceDetails.getVisibility()==View.GONE)
                {
                    hiddenLayoutResourceDetails.setVisibility(View.VISIBLE);
                    hiddenLayoutResourceDetails.startAnimation(slide_up);
                }

                else
                {
                    hiddenLayoutResourceDetails.startAnimation(slide_down);
                    hiddenLayoutResourceDetails.setVisibility(View.GONE);
                }
            }
        });

        btn_next_resource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(text_first_name.getText().toString().isEmpty())
                {
                    text_first_name.setError("Cannot be left empty");
                }
                else if(text_last_name.getText().toString().isEmpty())
                {
                    text_last_name.setError("Cannot be left empty");
                }
                else if(spinner_res_type.getText().toString().isEmpty())
                {
                    Toast.makeText(AddResourceActivity.this, "Select Resource Type", Toast.LENGTH_SHORT).show();
                }
                else if(text_designation.getText().toString().isEmpty())
                {
                    text_designation.setError("Cannot be left empty");
                }
                else if(text_email.getText().toString().isEmpty())
                {
                    text_email.setError("Cannot be left empty");
                }
                else if(text_phone.getText().toString().isEmpty())
                {
                    text_phone.setError("Cannot be left empty");
                }
                else if(text_rate_per_hour.getText().toString().isEmpty())
                {
                    text_rate_per_hour.setError("Cannot be left empty");
                }
                else
                {
                    hiddenLayoutResourceDetails.startAnimation(slide_down);
                    hiddenLayoutResourceDetails.setVisibility(View.GONE);
                    hiddenLayoutAddress.setVisibility(View.VISIBLE);
                    hiddenLayoutAddress.startAnimation(slide_up);
                    checkbox_resource.setChecked(true);
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

                if(text_house_no.getText().toString().isEmpty())
                {
                    text_house_no.setError("Cannot be left empty");
                }
                else if(text_street_name.getText().toString().isEmpty())
                {
                    text_street_name.setError("Cannot be left empty");
                }
                else if(text_city.getText().toString().isEmpty())
                {
                    text_city.setError("Cannot be left empty");
                }
                else if(text_state.getText().toString().isEmpty())
                {
                    text_state.setError("Cannot be left empty");
                }
                else if(text_country.getText().toString().isEmpty())
                {
                    text_country.setError("Cannot be left empty");
                }
                else if(text_zipcode.getText().toString().isEmpty())
                {
                    text_zipcode.setError("Cannot be left empty");
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

                //resource
                if(text_first_name.getText().toString().isEmpty())
                {
                    text_first_name.setError("Cannot be left empty");
                }
                else if(text_last_name.getText().toString().isEmpty())
                {
                    text_last_name.setError("Cannot be left empty");
                }
                else if(spinner_res_type.getText().toString().isEmpty())
                {
                    Toast.makeText(AddResourceActivity.this, "Select Resource Type", Toast.LENGTH_SHORT).show();
                }
                else if(text_designation.getText().toString().isEmpty())
                {
                    text_designation.setError("Cannot be left empty");
                }
                else if(text_email.getText().toString().isEmpty())
                {
                    text_email.setError("Cannot be left empty");
                }
                else if(text_phone.getText().toString().isEmpty())
                {
                    text_phone.setError("Cannot be left empty");
                }
                else if(text_rate_per_hour.getText().toString().isEmpty())
                {
                    text_rate_per_hour.setError("Cannot be left empty");
                }
                //address

                else if(text_house_no.getText().toString().isEmpty())
                {
                    text_house_no.setError("Cannot be left empty");
                    hiddenLayoutAddress.setVisibility(View.VISIBLE);
                }
                else if(text_street_name.getText().toString().isEmpty())
                {
                    text_street_name.setError("Cannot be left empty");
                    hiddenLayoutAddress.setVisibility(View.VISIBLE);
                }
                else if(text_city.getText().toString().isEmpty())
                {
                    text_city.setError("Cannot be left empty");
                    hiddenLayoutAddress.setVisibility(View.VISIBLE);
                }
                else if(text_state.getText().toString().isEmpty())
                {
                    text_state.setError("Cannot be left empty");
                    hiddenLayoutAddress.setVisibility(View.VISIBLE);
                }
                else if(text_country.getText().toString().isEmpty())
                {
                    text_country.setError("Cannot be left empty");
                    hiddenLayoutAddress.setVisibility(View.VISIBLE);
                }
                else if(text_zipcode.getText().toString().isEmpty())
                {
                    text_zipcode.setError("Cannot be left empty");
                    hiddenLayoutAddress.setVisibility(View.VISIBLE);
                }

                else
                {
                    //create API if new, or UPDATE API call if Editing
                    if(getIntent().hasExtra("edit"))
                    {
                        if (getIntent().getStringExtra("edit").equals("yes"))
                        {
                            Intent intent = new Intent(AddResourceActivity.this, AllAddResources.class);
                            Toast.makeText(AddResourceActivity.this, "Details Updated", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    }
                    else
                    {
                        pDialog = new ProgressDialog(AddResourceActivity.this);
                        pDialog.setMessage("Saving Details ...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(true);
                        pDialog.show();

                        class MyTask extends AsyncTask<Void, Void, Void> {

                            @Override
                            protected Void doInBackground(Void... params) {
                                createResource();
                                return null;
                            }

                        }
                        new MyTask().execute();
                    }

                }
            }
        });

    }

    public void setResourceDetails()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getResource";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AddResourceActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }

                            if(type.equals("INFO"))
                            {
                                Log.d("response resources : ", response.toString());

                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    resId = dataObject.getString("id");
                                    if(resId.equals(currentResource))
                                    {
                                        dataObject = dataArray.getJSONObject(i);
                                        firstName = dataObject.getString("firstName");
                                        lastName = dataObject.getString("lastName");
                                        designationId = dataObject.getString("designationId");
                                        ratePerHour = dataObject.getString("ratePerHour");

                                        currencyId = dataObject.getString("currencyId");
                                        resourceTypeId = dataObject.getString("resourceTypeId");

                                        emailId = dataObject.getString("emailId");
                                        phone = dataObject.getString("phone");
                                        houseNo = dataObject.getString("houseNo");
                                        streetName = dataObject.getString("streetName");
                                        locality = dataObject.getString("locality");
                                        state = dataObject.getString("state");
                                        country = dataObject.getString("country");

                                        text_first_name.setText(firstName);
                                        text_last_name.setText(lastName);


                                        text_designation.setText(designationId);


                                        text_email.setText(emailId);
                                        text_phone.setText(phone);
                                        text_rate_per_hour.setText(ratePerHour);

                                        text_house_no.setText(houseNo);
                                        text_street_name.setText(streetName);
                                        text_city.setText(locality);
                                        text_state.setText(state);
                                        text_country.setText(country);
//                                        text_zipcode.setText(issuedBy);

                                        for(int j=0; j<currencyArray.length ;j++)
                                        {
                                            //matching server currency and spinner currency and setting into spinner
                                            if(currencyArray[j].equals(currencyId))
                                                spinner_currency.setText(currencyArray[j]);
                                        }
                                        if(resourceTypeId!=null)
                                        {
                                            spinner_res_type.setText(resTypeArray[Integer.parseInt(resourceTypeId)-1]);
                                        }

                                    }
                                }
                                pDialog.dismiss();
                            }
                            pDialog.dismiss();
                        }catch(JSONException e){e.printStackTrace();
                            pDialog.dismiss();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Log.e("Volley","Error");

                    }
                }
        );
        requestQueue.add(jor);

        if(pDialog!=null)
            pDialog.dismiss();
    }

    public void createResource()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("firstName", text_first_name.getText().toString());
            object.put("lastName",text_last_name.getText().toString());
            object.put("designationId",text_designation.getText().toString());
            object.put("ratePerHour",text_rate_per_hour.getText().toString());
            object.put("currencyId", spinner_currency.getText().toString());
            object.put("emailId",text_email.getText().toString());
            object.put("phone",text_phone.getText().toString());
            object.put("houseNo", text_house_no.getText().toString());
            object.put("streetName",text_street_name.getText().toString());
            object.put("locality",text_city.getText().toString());
            object.put("state",text_state.getText().toString());
            object.put("country",text_country.getText().toString());

            object.put("currencyId", spinner_currency.getText().toString());

            if(spinner_res_type.getText().toString().equals("EMPLOYEE"))
            {
                object.put("resourceTypeId","1");
            }
            else
            {
                object.put("resourceTypeId","2");
            }

            Log.d("resource object :", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AddResourceActivity.this);

        String url = AddResourceActivity.this.getResources().getString(R.string.server_url) + "/postResource";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("resource response :", response.toString());
                            if(response.getString("msg").equals("success"))
                            {
                                String successMsg = "Resource Created. ID - "+ response.getString("data");
                                Toast.makeText(AddResourceActivity.this, successMsg, Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                Intent intent = new Intent(AddResourceActivity.this, AllAddResources.class);
                                startActivity(intent);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");
                        Toast.makeText(AddResourceActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jor);

        if(pDialog!=null)
            pDialog.dismiss();
    }
}