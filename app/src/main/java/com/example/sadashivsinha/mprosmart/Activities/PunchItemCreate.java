package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PunchItemCreate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    RelativeLayout main_content;
    EditText text_location, text_punch_item_type, text_notes,text_resp_party, text_architect;
    TextView text_item_desc;
    BetterSpinner spinner_priority;
    Spinner spinner_item;
    ProgressDialog pDialog;
    String currentPunchListNo, currentProjectNo;
    TextView btn_date_complete, btn_date_due;
    String whichDate;
    String[] itemIdArray, itemNameArray, itemDescArray;
    String itemId, itemName, itemDesc,currentItemId;
    String dueDateToSend, completedDateToSend;
    JSONArray dataArray;
    JSONObject dataObject;
    BetterSpinner spinner_status;
    Button createBtn;
    PreferenceManager pm;
    ConnectionDetector cd;
    public static final String TAG = PunchItemCreate.class.getSimpleName();
    Boolean isInternetPresent = false;
    String item_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punch_item_create);

        pm = new PreferenceManager(getApplicationContext());
        currentPunchListNo = pm.getString("punchListNo");
        currentProjectNo = pm.getString("projectId");

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        text_location = (EditText) findViewById(R.id.text_location);
        text_item_desc = (TextView) findViewById(R.id.text_item_desc);
        text_punch_item_type = (EditText) findViewById(R.id.text_punch_item_type);
        text_notes = (EditText) findViewById(R.id.text_notes);
        text_resp_party = (EditText) findViewById(R.id.text_resp_party);
        text_architect = (EditText) findViewById(R.id.text_architect);

        spinner_priority = (BetterSpinner) findViewById(R.id.spinner_priority);
        spinner_item = (Spinner) findViewById(R.id.spinner_item);

        spinner_status = (BetterSpinner) findViewById(R.id.spinner_status);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PunchItemCreate.this,
                android.R.layout.simple_dropdown_item_1line,new String[] {"ACTIVE", "INACTIVE"});
        spinner_status.setAdapter(adapter);

        btn_date_complete = (TextView) findViewById(R.id.btn_date_complete);
        btn_date_due = (TextView) findViewById(R.id.btn_date_due);

        item_url = pm.getString("SERVER_URL") + "/getItems?projectId='"+currentProjectNo+"'";

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(PunchItemCreate.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(PunchItemCreate.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(item_url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");

                        itemIdArray = new String[dataArray.length()+1];
                        itemDescArray = new String[dataArray.length()+1];
                        itemNameArray = new String[dataArray.length()+1];

                            itemIdArray[0]="Select Item";
                            itemDescArray[0]="Select Item to view description";
                            itemNameArray[0]="Select Item";

                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                itemId = dataObject.getString("itemId");
                                itemDesc = dataObject.getString("itemDescription");
                                itemName = dataObject.getString("itemName");

                                itemIdArray[i+1]=itemId;
                                itemDescArray[i+1]=itemDesc;
                                itemNameArray[i+1]=itemName;
                            }
                            if(dataArray.length()==0)
                            {
                                ArrayAdapter<String> itemAdapter = new ArrayAdapter<String>(PunchItemCreate.this,
                                        android.R.layout.simple_dropdown_item_1line,new String[] {"No Data"});
                                spinner_item.setAdapter(itemAdapter);
                            }
                            else
                            {
                                ArrayAdapter<String> itemAdapter = new ArrayAdapter<String>(PunchItemCreate.this,
                                        android.R.layout.simple_dropdown_item_1line,itemNameArray);
                                spinner_item.setAdapter(itemAdapter);
                            }

                        ArrayAdapter<String> itemAdapter;

                        if (itemIdArray == null) {
                            itemAdapter = new ArrayAdapter<String>(PunchItemCreate.this,
                                    android.R.layout.simple_dropdown_item_1line, new String[]{"No Items Found"});
                        } else {
                            itemAdapter = new ArrayAdapter<String>(PunchItemCreate.this,
                                    android.R.layout.simple_dropdown_item_1line, itemIdArray);
                        }
                        spinner_item.setAdapter(itemAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            } else {
                Toast.makeText(PunchItemCreate.this, "Offline Data Not available for Items", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            prepareItemList();
        }

        btn_date_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                whichDate = "complete";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        PunchItemCreate.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        btn_date_due.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                whichDate = "due";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        PunchItemCreate.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        main_content = (RelativeLayout) findViewById(R.id.main_content);

        adapter = new ArrayAdapter<String>(PunchItemCreate.this,
                android.R.layout.simple_dropdown_item_1line,new String[] {"LOW", "MEDIUM", "HIGH"});
        spinner_priority.setAdapter(adapter);

        createBtn = (Button) findViewById(R.id.createBtn);

        spinner_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(itemIdArray.length!=1)
                {
                    if(itemDescArray[position].isEmpty())
                    {
                        text_item_desc.setText("No Description");
                    }
                    else
                    {
                        text_item_desc.setText(itemDescArray[position]);
                    }
                    currentItemId = itemIdArray[position];

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(text_location.getText().toString().isEmpty())
                {
                    text_location.setError("Field cannot be empty");
                }
                else if (spinner_item.getSelectedItem().toString().equals("No Data"))
                {
                    Toast.makeText(PunchItemCreate.this, "No Items available in this project", Toast.LENGTH_SHORT).show();
                }
                else if(text_item_desc.getText().toString().isEmpty())
                {
                    text_item_desc.setError("Field cannot be empty");
                }
                else if(text_punch_item_type.getText().toString().isEmpty())
                {
                    text_punch_item_type.setError("Field cannot be empty");
                }
                else if(text_notes.getText().toString().isEmpty())
                {
                    text_notes.setError("Field cannot be empty");
                }
                else if(text_resp_party.getText().toString().isEmpty())
                {
                    text_resp_party.setError("Field cannot be empty");
                }
                else if(spinner_priority.getText().toString().isEmpty())
                {
                    Toast.makeText(PunchItemCreate.this, "Select Priority", Toast.LENGTH_SHORT).show();
                }
                else if(spinner_status.getText().toString().isEmpty())
                {
                    Toast.makeText(PunchItemCreate.this, "Select Status", Toast.LENGTH_SHORT).show();
                }
                else if(text_architect.getText().toString().isEmpty())
                {
                    text_architect.setError("Field cannot be empty");
                }
                else if(btn_date_complete.getText().toString().isEmpty())
                {
                    Toast.makeText(PunchItemCreate.this, "Select complete date", Toast.LENGTH_SHORT).show();
                }
                else if(btn_date_due.getText().toString().isEmpty())
                {
                    Toast.makeText(PunchItemCreate.this, "Select schedule date", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    saveData();
                }
            }
        });
    }
    public void prepareItemList()
    {
        pDialog = new ProgressDialog(PunchItemCreate.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);


        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, item_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(PunchItemCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("WARN"))
                            {
                                itemIdArray = new String[1];
                                itemIdArray[0]="No Items";
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(PunchItemCreate.this,
                                        android.R.layout.simple_dropdown_item_1line,itemIdArray);
                                spinner_item.setAdapter(adapter);
                                Log.d("Punch List", "ARRAY EMPTY");
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                itemIdArray = new String[dataArray.length()+1];
                                itemDescArray = new String[dataArray.length()+1];
                                itemNameArray = new String[dataArray.length()+1];

                                itemIdArray[0]="Select Item";
                                itemDescArray[0]="Select Item to view description";
                                itemNameArray[0]="Select Item";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    itemId = dataObject.getString("itemId");
                                    itemDesc = dataObject.getString("itemDescription");
                                    itemName = dataObject.getString("itemName");

                                    itemIdArray[i+1]=itemId;
                                    itemDescArray[i+1]=itemDesc;
                                    itemNameArray[i+1]=itemName;
                                }
                                if(dataArray.length()==0)
                                {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PunchItemCreate.this,
                                            android.R.layout.simple_dropdown_item_1line,new String[] {"No Data"});
                                    spinner_item.setAdapter(adapter);
                                }
                                else
                                {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PunchItemCreate.this,
                                            android.R.layout.simple_dropdown_item_1line,itemNameArray);
                                    spinner_item.setAdapter(adapter);
                                }
                            }

                            pDialog.dismiss();
                        }catch(JSONException e){
                            pDialog.dismiss();
                            e.printStackTrace();}
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
    }


    public void saveData()
    {
        JSONObject object = new JSONObject();
        try
        {
            object.put("punchListId",currentPunchListNo);
            object.put("location",text_location.getText().toString());
            object.put("description",text_item_desc.getText().toString());
            object.put("itemType",text_punch_item_type.getText().toString());
            object.put("notes",text_notes.getText().toString());
            object.put("responsible",text_resp_party.getText().toString());
            object.put("priority",spinner_priority.getText().toString());
            object.put("scheduleToComplete",dueDateToSend);
            object.put("dateComplted",completedDateToSend);
            object.put("architectAccepted",text_architect.getText().toString());

            if(spinner_status.getText().toString().equals("ACTIVE"))
            {
                object.put("status","1");
            }
            else
            {
                object.put("status","2");
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(PunchItemCreate.this);

        String url = PunchItemCreate.this.pm.getString("SERVER_URL") + "/postPunchListLines";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(PunchItemCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();

                            Intent intent = new Intent(PunchItemCreate.this, PunchListActivity.class);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
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

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createPunchListItem = pm.getBoolean("createPunchListItem");

            if(createPunchListItem)
            {
                Toast.makeText(PunchItemCreate.this, "Already a Punch List Item creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(PunchItemCreate.this, "Internet not currently available. Punch List Item will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectPunchListItem", object.toString());
                pm.putString("urlPunchListItem", url);
                pm.putString("toastMessagePunchListItem", "Punch List Item Created");
                pm.putBoolean("createPunchListItem", true);

                Intent intent = new Intent(PunchItemCreate.this, PunchListActivity.class);
                startActivity(intent);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String date = dayOfMonth+"/"+(MONTHS[monthOfYear])+"/"+year;

        if(whichDate.equals("complete"))
        {
            btn_date_complete.setText(date);
            completedDateToSend = year+"-"+(MONTHS[monthOfYear])+"-"+dayOfMonth;
        }

        else if(whichDate.equals("due"))
        {
            btn_date_due.setText(date);
            dueDateToSend = year+"-"+(MONTHS[monthOfYear])+"-"+dayOfMonth;
        }
    }
}
