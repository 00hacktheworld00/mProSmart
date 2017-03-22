package com.example.sadashivsinha.mprosmart.Activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

public class AddVendorsActivity extends AppCompatActivity {


    ImageButton btn_next_vendor_details, btn_next_address, btn_next_contact_details, btn_next_insurance, btn_add_address_two,
            btn_remove_address_two, btn_next_address_two;
    LinearLayout hiddenLayoutVendorDetails, hiddenLayoutAddress, hiddenLayoutAddressTwo, hiddenLayoutContactDetails, hiddenLayoutInsurance;
    Button createBtn;
    CardView card_vendor, card_address, card_contact, card_insurance, card_address_two;
    CheckBox checkbox_vendor, checkbox_address, checkbox_address_two, checkbox_contact, checkbox_insurance;

    EditText text_vendor_name, text_tax_id, text_licence_no, text_company_name;
    EditText text_house_no, text_street_name, text_city, text_state, text_country, text_zipcode;
    EditText text_house_no_two, text_street_name_two, text_city_two, text_state_two, text_country_two, text_zipcode_two;
    EditText text_fax, text_email;
    EditText text_insurance_company, text_policy_no;
    String[] vendorTypeIdArray, vendorTypeArray;
    String currentVendorTypeId;

    Spinner spinner_ven_type, spinner_discipline;

    BetterSpinner spinner_currency;
    String vendorId, currentUser, currentDate;

    RelativeLayout mainLayout;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    String currentVendor;
    String[] currencyArray, disciplineArray;
    PreferenceManager pm;

    String vendorName, vendorTypeId, license, address1, address2, addressLine3, city, zipCode, country, primaryContact, insuranceCompany,
        policyNumber, indemnificationAmount, minorityOwnedBusiness, DisadvantagedOwnedBusiness, certificateNo, contactCompany, contactFirstName,
        contactLastName, remarks, publishPath, emailId, currencyCode, decipline, statusId, coiExpirationDate, expDate, taxID, companyName, houseNo,
    streetName, state, fax;
    ConnectionDetector cd;
    public static final String TAG = AddVendorsActivity.class.getSimpleName();
    Boolean isInternetPresent = false;
    String vendor_type_url, vendor_details_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vendors);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        pm = new PreferenceManager(getApplicationContext());
        currentUser = pm.getString("userId");

        //Load animation
        final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);

        text_vendor_name = (EditText) findViewById(R.id.text_vendor_name);
        text_tax_id = (EditText) findViewById(R.id.text_tax_id);
        text_licence_no = (EditText) findViewById(R.id.text_licence_no);
        text_company_name = (EditText) findViewById(R.id.text_company_name);

        spinner_ven_type = (Spinner) findViewById(R.id.spinner_ven_type);
        spinner_discipline = (Spinner) findViewById(R.id.spinner_discipline);

        text_house_no = (EditText) findViewById(R.id.text_house_no);
        text_street_name = (EditText) findViewById(R.id.text_street_name);
        text_city = (EditText) findViewById(R.id.text_city);
        text_state = (EditText) findViewById(R.id.text_state);
        text_country = (EditText) findViewById(R.id.text_country);
        text_zipcode = (EditText) findViewById(R.id.text_zipcode);

        text_house_no_two = (EditText) findViewById(R.id.text_house_no_two);
        text_street_name_two = (EditText) findViewById(R.id.text_street_name_two);
        text_city_two = (EditText) findViewById(R.id.text_city_two);
        text_state_two = (EditText) findViewById(R.id.text_state_two);
        text_country_two = (EditText) findViewById(R.id.text_country_two);
        text_zipcode_two = (EditText) findViewById(R.id.text_zipcode_two);

        text_fax = (EditText) findViewById(R.id.text_fax);
        text_email = (EditText) findViewById(R.id.text_email);

        text_insurance_company = (EditText) findViewById(R.id.text_insurance_company);
        text_policy_no = (EditText) findViewById(R.id.text_policy_no);

        checkbox_vendor = (CheckBox) findViewById(R.id.checkbox_vendor);
        checkbox_address = (CheckBox) findViewById(R.id.checkbox_address);
        checkbox_address_two = (CheckBox) findViewById(R.id.checkbox_address_two);
        checkbox_contact = (CheckBox) findViewById(R.id.checkbox_contact);
        checkbox_insurance = (CheckBox) findViewById(R.id.checkbox_insurance);

        card_vendor = (CardView) findViewById(R.id.card_vendor);
        card_address = (CardView) findViewById(R.id.card_address);
        card_contact = (CardView) findViewById(R.id.card_contact);
        card_insurance = (CardView) findViewById(R.id.card_insurance);
        card_address_two = (CardView) findViewById(R.id.card_address_two);

        btn_next_vendor_details = (ImageButton) findViewById(R.id.btn_next_vendor_details);
        btn_next_address = (ImageButton) findViewById(R.id.btn_next_address);
        btn_next_address_two = (ImageButton) findViewById(R.id.btn_next_address_two);
        btn_add_address_two = (ImageButton) findViewById(R.id.btn_add_address_two);
        btn_remove_address_two = (ImageButton) findViewById(R.id.btn_remove_address_two);
        btn_next_contact_details = (ImageButton) findViewById(R.id.btn_next_contact_details);
        btn_next_insurance = (ImageButton) findViewById(R.id.btn_next_insurance);

        hiddenLayoutVendorDetails = (LinearLayout) findViewById(R.id.hiddenLayoutVendorDetails);
        hiddenLayoutAddress = (LinearLayout) findViewById(R.id.hiddenLayoutAddress);
        hiddenLayoutContactDetails = (LinearLayout) findViewById(R.id.hiddenLayoutContactDetails);
        hiddenLayoutInsurance = (LinearLayout) findViewById(R.id.hiddenLayoutInsurance);
        hiddenLayoutAddressTwo = (LinearLayout) findViewById(R.id.hiddenLayoutAddressTwo);

        spinner_currency = (BetterSpinner) findViewById(R.id.spinner_currency);

        HelveticaRegular title = (HelveticaRegular) findViewById(R.id.title);

        createBtn = (Button) findViewById(R.id.createBtn);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        vendor_type_url = pm.getString("SERVER_URL") + "/getVendorType";
        vendor_details_url = pm.getString("SERVER_URL") + "/getVendors";

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_content);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AddVendorsActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(vendor_type_url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        vendorTypeIdArray = new String[dataArray.length()+1];
                        vendorTypeArray = new String[dataArray.length()+1];

                        vendorTypeIdArray[0]="Select Vendor Type";
                        vendorTypeArray[0]="Select Vendor Type";

                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            vendorTypeIdArray[i+1] = dataObject.getString("id");
                            vendorTypeArray[i+1] = dataObject.getString("type");
                        }

                        if(vendorTypeIdArray!=null)
                        {
                            ArrayAdapter<String> adapterVendor = new ArrayAdapter<String>(AddVendorsActivity.this,
                                    android.R.layout.simple_dropdown_item_1line,vendorTypeArray);

                            spinner_ven_type.setAdapter(adapterVendor);
                        }
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
                Toast.makeText(AddVendorsActivity.this, "Offline Data Not available for Vendor Types", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            pDialog = new ProgressDialog(AddVendorsActivity.this);
            pDialog.setMessage("Getting Data");
            pDialog.show();

            // Cache data not exist.
            getVendorType();
        }


        disciplineArray = new String[5];
        disciplineArray[0] = "Select Discipline";
        disciplineArray[1] = "Electrical";
        disciplineArray[2] = "Mechanical";
        disciplineArray[3] = "Civil";
        disciplineArray[4] = "Architectural";

        ArrayAdapter<String> adapterDiscipline = new ArrayAdapter<String>(AddVendorsActivity.this,
                android.R.layout.simple_dropdown_item_1line, disciplineArray);
        spinner_discipline.setAdapter(adapterDiscipline);

        spinner_ven_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for(int i=0; i<vendorTypeArray.length; i++)
                {
                    if(vendorTypeArray[i].equals(spinner_ven_type.getSelectedItem().toString()))
                        currentVendorTypeId = vendorTypeIdArray[i];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        currencyArray = new String[3];
        currencyArray[0] = "INR";
        currencyArray[1] = "$";
        currencyArray[2] = "EURO";

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddVendorsActivity.this,
                android.R.layout.simple_dropdown_item_1line, currencyArray);
        spinner_currency.setAdapter(adapter);

        //insert previous data if editing
        if(getIntent().hasExtra("edit"))
        {
            if(getIntent().getStringExtra("edit").equals("yes"))
            {
                title.setText("Edit Details");
                createBtn.setText("SAVE");

                currentVendor = getIntent().getStringExtra("vendorId");

                if (!isInternetPresent) {
                    // Internet connection is not present
                    // Ask user to connect to Internet

                    Cache cache = AppController.getInstance().getRequestQueue().getCache();
                    Cache.Entry entry = cache.get(vendor_details_url);
                    if (entry != null) {
                        //Cache data available.
                        try {
                            String data = new String(entry.data, "UTF-8");
                            Log.d("CACHE DATA", data);
                            JSONObject jsonObject = new JSONObject(data);
                            try {
                                dataArray = jsonObject.getJSONArray("data");
                                vendorTypeIdArray = new String[dataArray.length()+1];
                                vendorTypeArray = new String[dataArray.length()+1];

                                vendorTypeIdArray[0]="Select Vendor Type";
                                vendorTypeArray[0]="Select Vendor Type";

                                for(int i=0; i<dataArray.length();i++) {
                                    dataObject = dataArray.getJSONObject(i);
                                    vendorId = dataObject.getString("vendorId");

                                    if (vendorId.equals(currentVendor)) {
                                        dataObject = dataArray.getJSONObject(i);
                                        Log.d("equals? :", "yes");

                                        vendorName = dataObject.getString("vendorName");

                                        Log.d("vendorName: ", vendorName);
                                        text_vendor_name.setText(vendorName);

                                        vendorTypeId = dataObject.getString("vendorTypeId");
                                        taxID = dataObject.getString("taxID");
                                        license = dataObject.getString("license");
                                        decipline = dataObject.getString("decipline");
                                        companyName = dataObject.getString("companyName");
                                        houseNo = dataObject.getString("houseNo");
                                        streetName = dataObject.getString("streetName");
                                        city = dataObject.getString("city");
                                        state = dataObject.getString("state");
                                        country = dataObject.getString("country");
                                        zipCode = dataObject.getString("zipCode");
                                        fax = dataObject.getString("fax");
                                        emailId = dataObject.getString("emailId");
                                        insuranceCompany = dataObject.getString("insuranceCompany");
                                        policyNumber = dataObject.getString("policyNumber");


                                        currencyCode = dataObject.getString("currencyCode");
                                        for (String currentCurrency : currencyArray) {
                                            //matching server currency and spinner currency and setting into spinner
                                            if (currentCurrency.equals(currencyCode))
                                                spinner_currency.setText(currentCurrency);
                                        }

                                        for (int j = 0; j < vendorTypeIdArray.length; j++) {
                                            if (vendorTypeId.equals(vendorTypeIdArray[j]))
                                                spinner_ven_type.setSelection(j);
                                        }

                                        spinner_discipline.setSelection(Integer.parseInt(decipline));


                                        text_tax_id.setText(taxID);
                                        text_licence_no.setText(license);
                                        text_company_name.setText(companyName);
                                        text_house_no.setText(houseNo);
                                        text_street_name.setText(streetName);
                                        text_city.setText(city);
                                        text_state.setText(state);
                                        text_country.setText(country);
                                        text_zipcode.setText(zipCode);
                                        text_fax.setText(fax);
                                        text_email.setText(emailId);
                                        text_insurance_company.setText(insuranceCompany);
                                        text_policy_no.setText(policyNumber);

                                        break;
                                    }
                                }
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
                        Toast.makeText(AddVendorsActivity.this, "Offline Data Not available for Vendor Details", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }

                else
                {
                    // Cache data not exist.
                    setVendorDetails();
                }
            }
        }

        card_vendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayoutVendorDetails.getVisibility()==View.GONE)
                {
                    hiddenLayoutVendorDetails.setVisibility(View.VISIBLE);
                    hiddenLayoutVendorDetails.startAnimation(slide_up);
                }

                else
                {
                    hiddenLayoutVendorDetails.startAnimation(slide_down);
                    hiddenLayoutVendorDetails.setVisibility(View.GONE);
                }
            }
        });

        btn_next_vendor_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(text_vendor_name.getText().toString().isEmpty())
                {
                    text_vendor_name.setError("Cannot be left empty");
                }
                else if(spinner_ven_type.getSelectedItem().toString().equals("Select Vendor Type"))
                {
                    Toast.makeText(AddVendorsActivity.this, "Select Vendor Type", Toast.LENGTH_SHORT).show();
                }
                else if(text_tax_id.getText().toString().isEmpty())
                {
                    text_tax_id.setError("Cannot be left empty");
                }
                else if(text_licence_no.getText().toString().isEmpty())
                {
                    text_licence_no.setError("Cannot be left empty");
                }
                else if(spinner_discipline.getSelectedItem().toString().equals("Select Discipline"))
                {
                    Toast.makeText(AddVendorsActivity.this, "Select Discipline", Toast.LENGTH_SHORT).show();
                }
                else if(text_company_name.getText().toString().isEmpty())
                {
                    text_company_name.setError("Cannot be left empty");
                }
                else
                {
                    hiddenLayoutVendorDetails.startAnimation(slide_down);
                    hiddenLayoutVendorDetails.setVisibility(View.GONE);
                    hiddenLayoutAddress.setVisibility(View.VISIBLE);
                    hiddenLayoutAddress.startAnimation(slide_up);
                    checkbox_vendor.setChecked(true);
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

        card_address_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayoutAddressTwo.getVisibility()==View.GONE)
                {
                    hiddenLayoutAddressTwo.setVisibility(View.VISIBLE);
                    hiddenLayoutAddressTwo.startAnimation(slide_up);
                }

                else
                {
                    hiddenLayoutAddressTwo.startAnimation(slide_down);
                    hiddenLayoutAddressTwo.setVisibility(View.GONE);
                }
            }
        });

        btn_add_address_two.setOnClickListener(new View.OnClickListener() {
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
                    card_address_two.setVisibility(View.VISIBLE);
                    card_address_two.startAnimation(slide_up);
                    hiddenLayoutAddressTwo.setVisibility(View.VISIBLE);
                    hiddenLayoutAddressTwo.startAnimation(slide_up);
                    checkbox_address.setChecked(true);
                }
            }
        });

        btn_remove_address_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hiddenLayoutAddressTwo.startAnimation(slide_down);
                hiddenLayoutAddressTwo.setVisibility(View.GONE);
                card_address_two.startAnimation(slide_down);
                card_address_two.setVisibility(View.GONE);
                checkbox_address_two.setChecked(false);
                text_house_no_two.setText("");
                text_street_name_two.setText("");
                text_city_two.setText("");
                text_state_two.setText("");
                text_country_two.setText("");
                text_zipcode_two.setText("");

                hiddenLayoutAddress.setVisibility(View.VISIBLE);
                hiddenLayoutAddress.startAnimation(slide_up);
                checkbox_address_two.setChecked(false);
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
                    hiddenLayoutContactDetails.setVisibility(View.VISIBLE);
                    hiddenLayoutContactDetails.startAnimation(slide_up);
                    checkbox_address.setChecked(true);
                }
            }
        });

        btn_next_address_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_house_no_two.getText().toString().isEmpty())
                {
                    text_house_no_two.setError("Cannot be left empty");
                }
                else if(text_street_name_two.getText().toString().isEmpty())
                {
                    text_street_name_two.setError("Cannot be left empty");
                }
                else if(text_city_two.getText().toString().isEmpty())
                {
                    text_city_two.setError("Cannot be left empty");
                }
                else if(text_state_two.getText().toString().isEmpty())
                {
                    text_state_two.setError("Cannot be left empty");
                }
                else if(text_country_two.getText().toString().isEmpty())
                {
                    text_country_two.setError("Cannot be left empty");
                }
                else if(text_zipcode_two.getText().toString().isEmpty())
                {
                    text_zipcode_two.setError("Cannot be left empty");
                }
                else
                {
                    hiddenLayoutAddressTwo.setVisibility(View.GONE);
                    hiddenLayoutAddressTwo.startAnimation(slide_down);
                    hiddenLayoutContactDetails.setVisibility(View.VISIBLE);
                    hiddenLayoutContactDetails.startAnimation(slide_up);
                    checkbox_address_two.setChecked(true);
                }
            }
        });


        card_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayoutContactDetails.getVisibility()==View.GONE)
                {
                    hiddenLayoutContactDetails.setVisibility(View.VISIBLE);
                    hiddenLayoutContactDetails.startAnimation(slide_up);
                }

                else
                {
                    hiddenLayoutContactDetails.startAnimation(slide_down);
                    hiddenLayoutContactDetails.setVisibility(View.GONE);
                }
            }
        });

        btn_next_contact_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_fax.getText().toString().isEmpty())
                {
                    text_fax.setError("Cannot be left empty");
                }
                else if(text_email.getText().toString().isEmpty())
                {
                    text_email.setError("Cannot be left empty");
                }
                else
                {
                    hiddenLayoutContactDetails.startAnimation(slide_down);
                    hiddenLayoutContactDetails.setVisibility(View.GONE);
                    hiddenLayoutInsurance.setVisibility(View.VISIBLE);
                    hiddenLayoutInsurance.startAnimation(slide_up);
                    checkbox_contact.setChecked(true);
                }
            }
        });



        card_insurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayoutInsurance.getVisibility()==View.GONE)
                {
                    hiddenLayoutInsurance.setVisibility(View.VISIBLE);
                    hiddenLayoutInsurance.startAnimation(slide_up);
                }

                else
                {
                    hiddenLayoutInsurance.startAnimation(slide_down);
                    hiddenLayoutInsurance.setVisibility(View.GONE);
                }
            }
        });

        btn_next_insurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_insurance_company.getText().toString().isEmpty())
                {
                    text_insurance_company.setError("Cannot be left empty");
                }
                else if(text_policy_no.getText().toString().isEmpty())
                {
                    text_policy_no.setError("Cannot be left empty");
                }
                else
                {
                    hiddenLayoutInsurance.startAnimation(slide_down);
                    hiddenLayoutInsurance.setVisibility(View.GONE);
                    checkbox_insurance.setChecked(true);
                }
            }
        });


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //vendor
                if(text_vendor_name.getText().toString().isEmpty())
                {
                    text_vendor_name.setError("Cannot be left empty");
                    hiddenLayoutVendorDetails.setVisibility(View.VISIBLE);
                }

                else if(spinner_ven_type.getSelectedItem().toString().equals("Select Vendor Type"))
                {
                    Toast.makeText(AddVendorsActivity.this, "Select Vendor Type", Toast.LENGTH_SHORT).show();
                }
                else if(text_tax_id.getText().toString().isEmpty())
                {
                    text_tax_id.setError("Cannot be left empty");
                }
                else if(text_licence_no.getText().toString().isEmpty())
                {
                    text_licence_no.setError("Cannot be left empty");
                }
                else if(spinner_discipline.getSelectedItem().equals("Select Discipline"))
                {
                    Toast.makeText(AddVendorsActivity.this, "Select Discipline", Toast.LENGTH_SHORT).show();
                }
                else if(text_company_name.getText().toString().isEmpty())
                {
                    text_company_name.setError("Cannot be left empty");
                    hiddenLayoutVendorDetails.setVisibility(View.VISIBLE);
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
                //address two

//                if(card_address_two.getVisibility()==View.VISIBLE)
//                {
//                    if(text_house_no_two.getText().toString().isEmpty())
//                    {
//                        text_house_no_two.setError("Cannot be left empty");
//                        hiddenLayoutAddressTwo.setVisibility(View.VISIBLE);
//                    }
//                    else if(text_street_name_two.getText().toString().isEmpty())
//                    {
//                        text_street_name_two.setError("Cannot be left empty");
//                        hiddenLayoutAddressTwo.setVisibility(View.VISIBLE);
//                    }
//                    else if(text_city_two.getText().toString().isEmpty())
//                    {
//                        text_city_two.setError("Cannot be left empty");
//                        hiddenLayoutAddressTwo.setVisibility(View.VISIBLE);
//                    }
//                    else if(text_state_two.getText().toString().isEmpty())
//                    {
//                        text_state_two.setError("Cannot be left empty");
//                        hiddenLayoutAddressTwo.setVisibility(View.VISIBLE);
//                    }
//                    else if(text_country_two.getText().toString().isEmpty())
//                    {
//                        text_country_two.setError("Cannot be left empty");
//                        hiddenLayoutAddressTwo.setVisibility(View.VISIBLE);
//                    }
//                    else if(text_zipcode_two.getText().toString().isEmpty())
//                    {
//                        text_zipcode_two.setError("Cannot be left empty");
//                        hiddenLayoutAddressTwo.setVisibility(View.VISIBLE);
//                    }
//                }

                //contact

                else if(text_fax.getText().toString().isEmpty())
                {
                    text_fax.setError("Cannot be left empty");
                    hiddenLayoutContactDetails.setVisibility(View.VISIBLE);
                }
                else if(text_email.getText().toString().isEmpty())
                {
                    text_email.setError("Cannot be left empty");
                    hiddenLayoutContactDetails.setVisibility(View.VISIBLE);
                }
                //insurance

                else if(text_insurance_company.getText().toString().isEmpty())
                {
                    text_insurance_company.setError("Cannot be left empty");
                    hiddenLayoutInsurance.setVisibility(View.VISIBLE);
                }
                else if(text_policy_no.getText().toString().isEmpty())
                {
                    text_policy_no.setError("Cannot be left empty");
                    hiddenLayoutInsurance.setVisibility(View.VISIBLE);
                }


                else
                {
//                    Intent intent = new Intent(AddVendorsActivity.this, AllVendors.class);
//                    Snackbar snackbar = Snackbar.make(mainLayout, "Vendor Addition sent for APPROVAL", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                    sendNotification("mProSmart Notification", "Approval Request for Vendor", "Click to approve or reject");

                    if(getIntent().hasExtra("edit"))
                    {
                        if (getIntent().getStringExtra("edit").equals("yes"))
                        {
                            updateVendor();
                        }
                    }
                    else
                    {
                        createVendor();
                    }

                }
            }
        });
    }

    public void updateVendor()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("vendorName", text_vendor_name.getText().toString());
            object.put("taxID", text_tax_id.getText().toString());
            object.put("companyName", text_company_name.getText().toString());
            object.put("license", text_licence_no.getText().toString());
            object.put("houseNo", text_house_no.getText().toString());
            object.put("streetName", text_street_name.getText().toString());
            object.put("city", text_city.getText().toString());
            object.put("state", text_state.getText().toString());
            object.put("zipCode", text_zipcode.getText().toString());
            object.put("country", text_country.getText().toString());
            object.put("fax", text_fax.getText().toString());
            object.put("insuranceCompany", text_insurance_company.getText().toString());
            object.put("policyNumber", text_policy_no.getText().toString());
            object.put("emailId", text_email.getText().toString());
            object.put("currencyCode", spinner_currency.getText().toString());


            object.put("locality", "");
            object.put("companyUrl", "");
            object.put("primaryContact", "");
            object.put("coiExpirationDate", "");
            object.put("phone", "");
            object.put("indemnificationAmount", "");
            object.put("minorityOwnedBusiness", "");
            object.put("DisadvantagedOwnedBusiness", "");
            object.put("certificateNo", "");
            object.put("contactCompany", "");
            object.put("expDate", "");
            object.put("contactFirstName", "");
            object.put("contactLastName", "");
            object.put("remarks", "");
            object.put("statusId", "");
            object.put("contactLastName", "");
            object.put("publishPath", "");

            Log.d("JSON SENT", object.toString());

            switch (spinner_discipline.getSelectedItem().toString()) {
                case "Electrical":
                    object.put("decipline", "1");
                    break;
                case "Mechanical":
                    object.put("decipline", "2");
                    break;
                case "Civil":
                    object.put("decipline", "3");
                    break;
                case "Architectural":
                    object.put("decipline", "4");
                    break;
            }


            for(int i=0; i<vendorTypeArray.length; i++)
            {
                if(vendorTypeArray[i].equals(spinner_ven_type.getSelectedItem().toString()))
                    object.put("vendorTypeId", currentVendorTypeId);
            }

            Log.d("object vendor", object.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AddVendorsActivity.this);

        String url = pm.getString("SERVER_URL") + "/putVendor?id=\"" + currentVendor + "\"";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response vendor", response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(AddVendorsActivity.this, "Vendor Updated. ID - " + currentVendor, Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                Intent intent = new Intent(AddVendorsActivity.this, AllVendors.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(AddVendorsActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
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
                        pDialog.dismiss();
                    }
                }
        );

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean updateVendorPending = pm.getBoolean("updateVendorPending");

            if(updateVendorPending)
            {
                Toast.makeText(AddVendorsActivity.this, "Already a Vendor updation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AddVendorsActivity.this, "Internet not currently available. Vendor will automatically get updated on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectVendorUpdate", object.toString());
                pm.putString("urlVendorUpdate", url);
                pm.putString("toastMessageVendorUpdate", "Vendor Updated");
                pm.putBoolean("updateVendorPending", true);

                pDialog.dismiss();
                Intent intent = new Intent(AddVendorsActivity.this, AllVendors.class);
                startActivity(intent);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
    }



    public void sendNotification(String title, String text, String subText) {

        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.drawable.ic_download_white);

        //for notification sound and vibrate as normal android notification
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        // This intent is fired when notification is clicked


        // Set the intent that will fire when the user taps the notification.

        Intent notificationIntent = new Intent(getApplicationContext(), ApproveActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, 0);

        builder.setContentIntent(intent);

        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_download_white));

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle(title);

        // Content text, which appears in smaller text below the title
        builder.setContentText(text);

        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        builder.setSubText(subText);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(999, builder.build());

    }

    public void setVendorDetails()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, vendor_details_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AddVendorsActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                Log.d("current vendor: ", currentVendor);
                                Log.d("response vendor : ", response.toString());

                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    vendorId = dataObject.getString("vendorId");
                                    if(vendorId.equals(currentVendor))
                                    {
                                        dataObject = dataArray.getJSONObject(i);
                                        Log.d("equals? :", "yes");

                                        vendorName = dataObject.getString("vendorName");

                                        Log.d("vendorName: ", vendorName);
                                        text_vendor_name.setText(vendorName);

                                        vendorTypeId = dataObject.getString("vendorTypeId");
                                        taxID = dataObject.getString("taxID");
                                        license = dataObject.getString("license");
                                        decipline = dataObject.getString("decipline");
                                        companyName = dataObject.getString("companyName");
                                        houseNo = dataObject.getString("houseNo");
                                        streetName = dataObject.getString("streetName");
                                        city = dataObject.getString("city");
                                        state = dataObject.getString("state");
                                        country = dataObject.getString("country");
                                        zipCode = dataObject.getString("zipCode");
                                        fax = dataObject.getString("fax");
                                        emailId = dataObject.getString("emailId");
                                        insuranceCompany = dataObject.getString("insuranceCompany");
                                        policyNumber = dataObject.getString("policyNumber");


                                        currencyCode = dataObject.getString("currencyCode");
                                        for (String currentCurrency : currencyArray) {
                                            //matching server currency and spinner currency and setting into spinner
                                            if (currentCurrency.equals(currencyCode))
                                                spinner_currency.setText(currentCurrency);
                                        }

                                        for(int j=0; j<vendorTypeIdArray.length; j++)
                                        {
                                            if(vendorTypeId.equals(vendorTypeIdArray[j]))
                                                spinner_ven_type.setSelection(j);
                                        }

                                        spinner_discipline.setSelection(Integer.parseInt(decipline));

                                        text_tax_id.setText(taxID);
                                        text_licence_no.setText(license);
                                        text_company_name.setText(companyName);
                                        text_house_no.setText(houseNo);
                                        text_street_name.setText(streetName);
                                        text_city.setText(city);
                                        text_state.setText(state);
                                        text_country.setText(country);
                                        text_zipcode.setText(zipCode);
                                        text_fax.setText(fax);
                                        text_email.setText(emailId);
                                        text_insurance_company.setText(insuranceCompany);
                                        text_policy_no.setText(policyNumber);

                                        break;

                                    }
                                }
                            }
                        }catch(JSONException e){e.printStackTrace();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");

                    }
                }
        );
        requestQueue.add(jor);
    }

    public void createVendor()
    {
        pDialog = new ProgressDialog(AddVendorsActivity.this);
        pDialog.setMessage("Sending data");
        pDialog.show();

        JSONObject object = new JSONObject();

        try {
            object.put("vendorName", text_vendor_name.getText().toString());
            object.put("taxID", text_tax_id.getText().toString());
            object.put("companyName", text_company_name.getText().toString());
            object.put("license", text_licence_no.getText().toString());
            object.put("houseNo", text_house_no.getText().toString());
            object.put("streetName", text_street_name.getText().toString());
            object.put("city", text_city.getText().toString());
            object.put("state", text_state.getText().toString());
            object.put("zipCode", text_zipcode.getText().toString());
            object.put("country", text_country.getText().toString());
            object.put("fax", text_fax.getText().toString());
            object.put("insuranceCompany", text_insurance_company.getText().toString());
            object.put("policyNumber", text_policy_no.getText().toString());
            object.put("emailId", text_email.getText().toString());
            object.put("currencyCode", spinner_currency.getText().toString());

            object.put("locality", "");
            object.put("companyUrl", "");
            object.put("primaryContact", "");
            object.put("coiExpirationDate", "");
            object.put("phone", "");
            object.put("indemnificationAmount", "");
            object.put("minorityOwnedBusiness", "");
            object.put("DisadvantagedOwnedBusiness", "");
            object.put("certificateNo", "");
            object.put("contactCompany", "");
            object.put("expDate", "");
            object.put("contactFirstName", "");
            object.put("contactLastName", "");
            object.put("remarks", "");
            object.put("statusId", "");
            object.put("contactLastName", "");
            object.put("publishPath", "");

            switch (spinner_discipline.getSelectedItem().toString()) {
                case "Electrical":
                    object.put("decipline", "1");
                    break;
                case "Mechanical":
                    object.put("decipline", "2");
                    break;
                case "Civil":
                    object.put("decipline", "3");
                    break;
                case "Architectural":
                    object.put("decipline", "4");
                    break;
            }


            Log.d("JSON SENT", object.toString());

            for(int i=0; i<vendorTypeArray.length; i++)
            {
                if(vendorTypeArray[i].equals(spinner_ven_type.getSelectedItem().toString()))
                    object.put("vendorTypeId", currentVendorTypeId);
            }

            Log.d("object vendor", object.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AddVendorsActivity.this);

        String url = pm.getString("SERVER_URL") + "/postVendor";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response vendor", response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(AddVendorsActivity.this, "Vendor Added. ID - " + response.getString("data"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                Intent intent = new Intent(AddVendorsActivity.this, AllVendors.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(AddVendorsActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
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
                        pDialog.dismiss();
                    }
                }
        );

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createVendorPending = pm.getBoolean("createVendorPending");

            if(createVendorPending)
            {
                Toast.makeText(AddVendorsActivity.this, "Already a Vendor creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AddVendorsActivity.this, "Internet not currently available. Vendor will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectVendor", object.toString());
                pm.putString("urlVendor", url);
                pm.putString("toastMessageVendor", "Vendor Added");
                pm.putBoolean("createVendorPending", true);

                pDialog.dismiss();
                Intent intent = new Intent(AddVendorsActivity.this, AllVendors.class);
                startActivity(intent);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
    }

    public void getVendorType()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, vendor_type_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AddVendorsActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                vendorTypeIdArray = new String[dataArray.length()+1];
                                vendorTypeArray = new String[dataArray.length()+1];

                                vendorTypeIdArray[0]="Select Vendor Type";
                                vendorTypeArray[0]="Select Vendor Type";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    vendorTypeIdArray[i+1] = dataObject.getString("id");
                                    vendorTypeArray[i+1] = dataObject.getString("type");
                                }

                                if(vendorTypeIdArray!=null)
                                {
                                    ArrayAdapter<String> adapterVendor = new ArrayAdapter<String>(AddVendorsActivity.this,
                                            android.R.layout.simple_dropdown_item_1line,vendorTypeArray);

                                    spinner_ven_type.setAdapter(adapterVendor);
                                }

                                pDialog.dismiss();
                            }

                        }
                        catch(JSONException e){
                            e.printStackTrace();}
                        pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");
                        pDialog.dismiss();
                    }
                }
        );
        if (pDialog!=null)
            pDialog.dismiss();

        requestQueue.add(jor);
    }
}
