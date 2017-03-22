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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class AddResourceActivity extends AppCompatActivity {

    ImageButton btn_next_resource, btn_next_address;
    LinearLayout hiddenLayoutResourceDetails, hiddenLayoutAddress;
    Button createBtn;
    CardView card_resource, card_address;
    CheckBox checkbox_resource, checkbox_address;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    public static final String TAG = AddResourceActivity.class.getSimpleName();

    LinearLayout layout_subcontractor;
    Spinner spinner_subcontractor;

    EditText text_first_name, text_last_name, text_email, text_phone, text_rate_per_hour;
    BetterSpinner spinner_currency, spinner_res_type, spinner_designation;
    EditText text_house_no, text_street_name, text_city, text_state, text_country, text_zipcode;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    String currentResource, resId;
    String firstName, lastName, resourceTypeId, designationId, ratePerHour, currencyId, emailId, phone, houseNo ,streetName,
            locality, state, country, zipCode;
    HelveticaRegular title;
    String[] currencyArray, resTypeArray, designationArray, vendorNameArray, vendorIdArray;
    String vendorTypeId, vendorName, vendorId;
    String currentSubcontractor, subContractor;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resource);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        pm = new PreferenceManager(getApplicationContext());

        //Load animation
        final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        text_first_name = (EditText) findViewById(R.id.text_first_name);
        text_last_name = (EditText) findViewById(R.id.text_last_name);
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
        spinner_designation = (BetterSpinner) findViewById(R.id.spinner_designation);

        currencyArray = new String[3];
        currencyArray[0] = "INR";
        currencyArray[1] = "$";
        currencyArray[2] = "EURO";

        resTypeArray = new String[2];
        resTypeArray[0] = "EMPLOYEE";
        resTypeArray[1] = "CONTRACT";

        designationArray = new String[3];
        designationArray[0] = "Design Consultant";
        designationArray[1] = "Engineer";
        designationArray[2] = "Architect";

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddResourceActivity.this,
                android.R.layout.simple_dropdown_item_1line, currencyArray);
        spinner_currency.setAdapter(adapter);

        ArrayAdapter<String> adapterResType = new ArrayAdapter<String>(AddResourceActivity.this,
                android.R.layout.simple_dropdown_item_1line, resTypeArray);
        spinner_res_type.setAdapter(adapterResType);

        ArrayAdapter<String> adapterDesignation = new ArrayAdapter<String>(AddResourceActivity.this,
                android.R.layout.simple_dropdown_item_1line, designationArray);
        spinner_designation.setAdapter(adapterDesignation);

        hiddenLayoutResourceDetails = (LinearLayout) findViewById(R.id.hiddenLayoutResourceDetails);
        hiddenLayoutAddress = (LinearLayout) findViewById(R.id.hiddenLayoutAddress);

        createBtn = (Button) findViewById(R.id.createBtn);

        title = (HelveticaRegular) findViewById(R.id.title);

        spinner_subcontractor = (Spinner) findViewById(R.id.spinner_subcontractor);
        layout_subcontractor = (LinearLayout) findViewById(R.id.layout_subcontractor);

        layout_subcontractor.setVisibility(View.GONE);

        spinner_subcontractor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSubcontractor = vendorNameArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_res_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(spinner_res_type.getText().toString().equals("CONTRACT"))
                {
                    layout_subcontractor.setVisibility(View.VISIBLE);
                }
                else
                {
                    layout_subcontractor.setVisibility(View.GONE);
                }
            }
        });

        //insert previous data if editing
        if(getIntent().hasExtra("edit"))
        {
            if(getIntent().getStringExtra("edit").equals("yes"))
            {
                title.setText("Edit Details");
                createBtn.setText("SAVE");

                currentResource = getIntent().getStringExtra("resId");


                getAllSubcontractorsFromVendors("edit");
            }
        }
        else
        {
            getAllSubcontractorsFromVendors("noEdit");
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

                else  if(spinner_res_type.getText().toString().equals("CONTRACT"))

                        {
                            if(spinner_subcontractor.getSelectedItem().toString().isEmpty())
                            {
                                Toast.makeText(AddResourceActivity.this, "There are no subcontractors", Toast.LENGTH_SHORT).show();
                            }
                            else if(spinner_subcontractor.getSelectedItem().toString().equals("Select Subcontractor"))
                            {
                                Toast.makeText(AddResourceActivity.this, "Select Subcontractor", Toast.LENGTH_SHORT).show();
                            }
                            else if(spinner_subcontractor.getSelectedItem().toString().equals("No Subcontractor"))
                            {
                                Toast.makeText(AddResourceActivity.this, "There are no subcontractors", Toast.LENGTH_SHORT).show();
                            }
                        }

                else if(spinner_designation.getText().toString().isEmpty())
                {
                    Toast.makeText(AddResourceActivity.this, "Select Designation", Toast.LENGTH_SHORT).show();
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
                else if(spinner_designation.getText().toString().isEmpty())
                {
                    Toast.makeText(AddResourceActivity.this, "Select Designation", Toast.LENGTH_SHORT).show();
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

                else  if(spinner_res_type.getText().toString().equals("CONTRACT"))
                {
                    if(spinner_subcontractor.getSelectedItem().toString().isEmpty())
                    {
                        Toast.makeText(AddResourceActivity.this, "There are no subcontractors", Toast.LENGTH_SHORT).show();
                    }
                    else if(spinner_subcontractor.getSelectedItem().toString().equals("Select Subcontractor"))
                    {
                        Toast.makeText(AddResourceActivity.this, "Select Subcontractor", Toast.LENGTH_SHORT).show();
                    }
                    else if(spinner_subcontractor.getSelectedItem().toString().equals("No Subcontractor"))
                    {
                        Toast.makeText(AddResourceActivity.this, "There are no subcontractors", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        elseCondition();
                    }
                }
                else
                {
                   elseCondition();
                }
            }
        });

    }

    public void elseCondition()
    {
        //create API if new, or UPDATE API call if Editing
        if(getIntent().hasExtra("edit"))
        {
            if (getIntent().getStringExtra("edit").equals("yes"))
            {
                pDialog = new ProgressDialog(AddResourceActivity.this);
                pDialog.setMessage("Saving Details ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                updateResource();
            }
        }
        else
        {
            createResource();
        }
    }
    public void setResourceDetails(final String subcontractorPresent)
    {
        String url = pm.getString("SERVER_URL") + "/getResource";


        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AddResourceActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AddResourceActivity.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);

                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            dataObject = dataArray.getJSONObject(i);
                            resId = dataObject.getString("id");
                            if (resId.equals(currentResource)) {
                                dataObject = dataArray.getJSONObject(i);
                                firstName = dataObject.getString("firstName");
                                lastName = dataObject.getString("lastName");
                                designationId = dataObject.getString("designationId");
                                ratePerHour = dataObject.getString("ratePerHour");

                                currencyId = dataObject.getString("currencyId");
                                resourceTypeId = dataObject.getString("resourceTypeId");

                                if (subcontractorPresent.equals("yes")) {
                                    if (resourceTypeId.equals("2")) {
                                        subContractor = dataObject.getString("subContractor");

                                        for (int j = 0; j < vendorNameArray.length; j++) {
                                            if (vendorNameArray[j].equals(subContractor)) {
                                                spinner_subcontractor.setSelection(j);
                                                break;
                                            }
                                        }
                                        layout_subcontractor.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Toast.makeText(AddResourceActivity.this, "Subcontractor Data Not Available", Toast.LENGTH_SHORT).show();
                                }

                                emailId = dataObject.getString("emailId");
                                phone = dataObject.getString("phone");
                                houseNo = dataObject.getString("houseNo");
                                streetName = dataObject.getString("streetName");
                                locality = dataObject.getString("locality");
                                state = dataObject.getString("state");
                                country = dataObject.getString("country");
                                zipCode = dataObject.getString("zipCode");

                                text_first_name.setText(firstName);
                                text_last_name.setText(lastName);
                                text_zipcode.setText(zipCode);

                                spinner_designation.setText(designationArray[Integer.parseInt(designationId) - 1]);


                                text_email.setText(emailId);
                                text_phone.setText(phone);
                                text_rate_per_hour.setText(ratePerHour);

                                text_house_no.setText(houseNo);
                                text_street_name.setText(streetName);
                                text_city.setText(locality);
                                text_state.setText(state);
                                text_country.setText(country);
//                                        text_zipcode.setText(issuedBy);

                                for (int j = 0; j < currencyArray.length; j++) {
                                    //matching server currency and spinner currency and setting into spinner
                                    if (currencyArray[j].equals(currencyId))
                                        spinner_currency.setText(currencyArray[j]);
                                }
                                if (resourceTypeId != null) {
                                    spinner_res_type.setText(resTypeArray[Integer.parseInt(resourceTypeId) - 1]);
                                }

                            }
                        }
                            pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            }

            else
            {
                Toast.makeText(AddResourceActivity.this, "Offline Data Not available for this Resource", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            pDialog = new ProgressDialog(AddResourceActivity.this);
            pDialog.setMessage("Getting server data");
            pDialog.show();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
//                            dataObject = response.getJSONObject(0);
                                dataArray = response.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {
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

                                        if(subcontractorPresent.equals("yes"))
                                        {
                                            if(resourceTypeId.equals("2"))
                                            {
                                                subContractor = dataObject.getString("subContractor");

                                                for(int j=0; j<vendorNameArray.length; j++)
                                                {
                                                    if(vendorNameArray[j].equals(subContractor))
                                                    {
                                                        spinner_subcontractor.setSelection(j);
                                                        break;
                                                    }
                                                }
                                                layout_subcontractor.setVisibility(View.VISIBLE);
                                            }
                                        }
                                        else
                                        {
                                            Toast.makeText(AddResourceActivity.this, "Subcontractor Data Not Available", Toast.LENGTH_SHORT).show();
                                        }

                                        emailId = dataObject.getString("emailId");
                                        phone = dataObject.getString("phone");
                                        houseNo = dataObject.getString("houseNo");
                                        streetName = dataObject.getString("streetName");
                                        locality = dataObject.getString("locality");
                                        state = dataObject.getString("state");
                                        country = dataObject.getString("country");
                                        zipCode = dataObject.getString("zipCode");

                                        text_first_name.setText(firstName);
                                        text_last_name.setText(lastName);
                                        text_zipcode.setText(zipCode);

                                        spinner_designation.setText(designationArray[Integer.parseInt(designationId) -1]);


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

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                        setData(response,false);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjectRequest);
            if(pDialog!=null)
                pDialog.dismiss();
        }
    }

    public void getAllSubcontractorsFromVendors(final String edit)
    {
        String url = pm.getString("SERVER_URL") + "/getVendors";

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AddResourceActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AddResourceActivity.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    int j=0;
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            dataObject = dataArray.getJSONObject(i);
                            vendorTypeId = dataObject.getString("vendorTypeId");

                            if(vendorTypeId.equals("7"))
                            {
                                j++;
                            }

                        }
                        vendorIdArray = new String[j+1];
                        vendorNameArray = new String[j+1];

                        vendorIdArray[0] = "Select Subcontractor";
                        vendorNameArray[0] = "Select Subcontractor";

                        for(int i=0; i<dataArray.length() ;i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            vendorTypeId = dataObject.getString("vendorTypeId");

                            if(vendorTypeId.equals("7"))
                            {
                                vendorId = dataObject.getString("vendorId");
                                vendorName = dataObject.getString("vendorName");

                                vendorIdArray[j] = vendorId;
                                vendorNameArray[j] = vendorName;
                                j++;
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddResourceActivity.this,
                                android.R.layout.simple_dropdown_item_1line, vendorNameArray);
                        spinner_subcontractor.setAdapter(adapter);

                        if(edit.equals("edit"))
                            setResourceDetails("yes");
                        else
                            pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            }

            else
            {
                Toast.makeText(AddResourceActivity.this, "Offline Data Not available for this Resource", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            pDialog = new ProgressDialog(AddResourceActivity.this);
            pDialog.setMessage("Getting server data");
            pDialog.show();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
//                            dataObject = response.getJSONObject(0);
                                dataArray = response.getJSONArray("data");
                                int j = 0;
                                for (int i = 0; i < dataArray.length(); i++) {
                                    dataObject = dataArray.getJSONObject(i);

                                    vendorTypeId = dataObject.getString("vendorTypeId");

                                    if (vendorTypeId.equals("7")) {
                                        j++;
                                    }

                                }
                                vendorIdArray = new String[j + 1];
                                vendorNameArray = new String[j + 1];

                                vendorIdArray[0] = "Select Subcontractor";
                                vendorNameArray[0] = "Select Subcontractor";

                                for (int i = 0; i < dataArray.length(); i++) {
                                    dataObject = dataArray.getJSONObject(i);
                                    vendorTypeId = dataObject.getString("vendorTypeId");

                                    if (vendorTypeId.equals("7")) {
                                        vendorId = dataObject.getString("vendorId");
                                        vendorName = dataObject.getString("vendorName");

                                        vendorIdArray[j] = vendorId;
                                        vendorNameArray[j] = vendorName;
                                        j++;
                                    }
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddResourceActivity.this,
                                        android.R.layout.simple_dropdown_item_1line, vendorNameArray);
                                spinner_subcontractor.setAdapter(adapter);

                                if (edit.equals("edit"))
                                    setResourceDetails("yes");
                                else
                                    pDialog.dismiss();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                        setData(response,false);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjectRequest);
            if(pDialog!=null)
                pDialog.dismiss();
        }
    }

    public void updateResource()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("firstName", text_first_name.getText().toString());
            object.put("lastName",text_last_name.getText().toString());
            object.put("ratePerHour",text_rate_per_hour.getText().toString());
            object.put("currencyId", spinner_currency.getText().toString());
            object.put("emailId",text_email.getText().toString());
            object.put("phone",text_phone.getText().toString());
            object.put("houseNo", text_house_no.getText().toString());
            object.put("streetName",text_street_name.getText().toString());
            object.put("locality",text_city.getText().toString());
            object.put("state",text_state.getText().toString());
            object.put("country",text_country.getText().toString());
            object.put("zipCode", text_zipcode.getText().toString());
            object.put("currencyId", spinner_currency.getText().toString());

            if(spinner_res_type.getText().toString().equals("EMPLOYEE"))
            {
                object.put("resourceTypeId","1");
                object.put("subContractor", "");
            }
            else
            {
                object.put("resourceTypeId","2");
                object.put("subContractor", spinner_subcontractor.getSelectedItem().toString());
            }


            if(spinner_designation.getText().toString().equals("Design Consultant"))
            {
                object.put("designationId","1");
            }
            else if (spinner_designation.getText().toString().equals("Engineer"))
            {
                object.put("designationId","2");
            }
            else
            {
                object.put("designationId","3");
            }

            Log.d("resource object :", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AddResourceActivity.this);

        String url = pm.getString("SERVER_URL") + "/putResource?id=\"" + currentResource + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("resource response :", response.toString());
                            if(response.getString("msg").equals("success"))
                            {
                                String successMsg = "Resource Updated. ID - "+ currentResource;
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
        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createUpdateResourcePending = pm.getBoolean("createUpdateResourcePending");

            if(createUpdateResourcePending)
            {
                Toast.makeText(AddResourceActivity.this, "Already a Resource updation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AddResourceActivity.this, "Internet not currently available. Resource will automatically get updated on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectUpdateResource", object.toString());
                pm.putString("urlUpdateResource", url);
                pm.putString("toastMessageUpdateResource", "Resource Updated");
                pm.putBoolean("createUpdateResourcePending", true);

                if(pDialog!=null)
                    pDialog.dismiss();

                Intent intent = new Intent(AddResourceActivity.this, AllAddResources.class);
                startActivity(intent);
            }
        }
        else
        {
            requestQueue.add(jor);
        }


    }


    public void createResource()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("firstName", text_first_name.getText().toString());
            object.put("lastName",text_last_name.getText().toString());
            object.put("ratePerHour",text_rate_per_hour.getText().toString());
            object.put("currencyId", spinner_currency.getText().toString());
            object.put("emailId",text_email.getText().toString());
            object.put("phone",text_phone.getText().toString());
            object.put("houseNo", text_house_no.getText().toString());
            object.put("streetName",text_street_name.getText().toString());
            object.put("locality",text_city.getText().toString());
            object.put("state",text_state.getText().toString());
            object.put("country",text_country.getText().toString());
            object.put("zipCode", text_zipcode.getText().toString());

            object.put("currencyId", spinner_currency.getText().toString());

            if(spinner_res_type.getText().toString().equals("EMPLOYEE"))
            {
                object.put("resourceTypeId","1");
            }
            else
            {
                object.put("resourceTypeId","2");
                object.put("subContractor", spinner_subcontractor.getSelectedItem().toString());
            }


            if(spinner_designation.getText().toString().equals("Design Consultant"))
            {
                object.put("designationId","1");
            }
            else if (spinner_designation.getText().toString().equals("Engineer"))
            {
                object.put("designationId","2");
            }
            else
            {
                object.put("designationId","3");
            }

            Log.d("resource object :", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AddResourceActivity.this);

        String url = pm.getString("SERVER_URL") + "/postResource";

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

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createResourcePending = pm.getBoolean("createResourcePending");

            if(createResourcePending)
            {
                Toast.makeText(AddResourceActivity.this, "Already a Resource creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AddResourceActivity.this, "Internet not currently available. Resource will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectResource", object.toString());
                pm.putString("urlResource", url);
                pm.putString("toastMessageResource", "Resource Created");
                pm.putBoolean("createResourcePending", true);

                if(pDialog!=null)
                    pDialog.dismiss();

                Intent intent = new Intent(AddResourceActivity.this, AllAddResources.class);
                startActivity(intent);
            }

        }
        else
        {
            requestQueue.add(jor);
        }
    }
}