package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MomItemCreate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText text_matter;
    String momId;
    String currentDate;
    TextView due_date;
    PreferenceManager pm;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    BetterSpinner spinner_responsible;
    String[] resourceNameArray;
    JSONArray dataArray;
    JSONObject dataObject;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mom_item_create);


        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        Button createBtn, attachBtn;
        pm = new PreferenceManager(MomItemCreate.this);
        momId = pm.getString("momId");
        pm.putString("totalImageUrls", "");

        createBtn = (Button) findViewById(R.id.createBtn);
        attachBtn = (Button) findViewById(R.id.attachBtn);

        text_matter = (EditText) findViewById(R.id.text_matter);
        spinner_responsible = (BetterSpinner) findViewById(R.id.spinner_responsible);

        due_date = (TextView) findViewById(R.id.due_date);

        due_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MomItemCreate.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMinDate(now);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = sdf.format(c.getTime());

        getAllResources();

        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MomItemCreate.this, AttachmentActivity.class);
                intent.putExtra("class", "MomLine");
                startActivity(intent);
            }
        });


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(text_matter.getText().toString().isEmpty())
                {
                    text_matter.setError("Field cannot be empty");
                }
                else if(spinner_responsible.getText().toString().isEmpty() || spinner_responsible.getText().toString().equals("Select Responsible"))
                {
                    Toast.makeText(MomItemCreate.this, "Select Responsible", Toast.LENGTH_SHORT).show();
                }
                else if(due_date.getText().toString().equals("Due Date"))
                {
                    Toast.makeText(MomItemCreate.this, "Select Due Date", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    createItem();
                }
            }
        });
    }

    public void createItem()
    {
        JSONObject object = new JSONObject();

        try
        {
            Date tradeDate = null;
            tradeDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(due_date.getText().toString());
            String newDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tradeDate);

            object.put("matterDiscussed",text_matter.getText().toString());
            object.put("responsible",spinner_responsible.getText().toString());
            object.put("dueDate",newDate);
            object.put("momId",momId);

            if(pm.getString("className").equals("MomLine"))
            {
                object.put("numberOfAttachments", pm.getString("totalImageUrlSize"));
            }
        }
        catch (JSONException | ParseException e)
        {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(MomItemCreate.this);

        String url = pm.getString("SERVER_URL") + "/postMomLineItems";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                            {
                                    String totalImageUrls = pm.getString("totalImageUrls");
                                    if(pm.getString("className").equals("MomLine") && isInternetPresent
                                            && !totalImageUrls.isEmpty())
                                    {
                                        uploadImage(response.getString("data"), totalImageUrls);
                                    }
                                    else
                                    {
                                        if(pDialog!=null)
                                            pDialog.dismiss();

                                        Toast.makeText(MomItemCreate.this, "MOM Line Item created. ID - "+response.getString("data"), Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(MomItemCreate.this, MomActivity.class);
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
                        Log.e("Volley","Error");
                    }
                }
        );

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createMOMPendingLine = pm.getBoolean("createMOMPendingLine");

            if(createMOMPendingLine)
            {
                Toast.makeText(MomItemCreate.this, "Already a MOM Line Item creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(MomItemCreate.this, "Internet not currently available. MOM Line Item will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectMOMLine", object.toString());
                pm.putString("urlMOMLine", url);
                pm.putString("toastMessageMOMLine", "MOM Line Item Created");
                pm.putBoolean("createMOMPendingLine", true);
            }


            if(pDialog!=null)
                pDialog.dismiss();

            Intent intent = new Intent(MomItemCreate.this, MomActivity.class);
            startActivity(intent);
        }
        else
        {
            requestQueue.add(jor);
        }
    }
    public void uploadImage(final String id, String totalUrls)
    {
        final List<String> imageList = Arrays.asList(totalUrls.split(","));
        String seperateImageUrl;


        for(int i=0; i<imageList.size();i++) {

            final int count = i;
            JSONObject object = new JSONObject();

            try {

                seperateImageUrl = imageList.get(i);

                object.put("momLineId", id);
                object.put("url", seperateImageUrl);

                Log.d("JSON OBJ SENT", object.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestQueue requestQueue = Volley.newRequestQueue(MomItemCreate.this);

            String url = MomItemCreate.this.pm.getString("SERVER_URL") + "/postMomLineFiles";

            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                Log.d("RESPONSE SERVER : ", response.toString());

                                if(response.getString("msg").equals("success"))
                                {
                                    if(count==imageList.size()-1)
                                    {
                                        Toast.makeText(MomItemCreate.this, "MOM Line created ID - " + id, Toast.LENGTH_SHORT).show();

                                        if(pDialog!=null)
                                            pDialog.dismiss();
                                        Intent intent = new Intent(MomItemCreate.this, MomActivity.class);
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
                            Toast.makeText(MomItemCreate.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            requestQueue.add(jor);
        }
    }

    public void getAllResources()
    {
        String url = pm.getString("SERVER_URL") + "/getResource";

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(MomItemCreate.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(MomItemCreate.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(url);
            if (entry != null) {
                //Cache data available.
                try {

                    int noOfRes = 0;

                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        resourceNameArray = new String[dataArray.length()+1];

                        resourceNameArray[0] = "Select Responsible";

                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            resourceNameArray[i+1]= dataObject.getString("firstName") + " " + dataObject.getString("lastName");
                        }

                        if(dataArray==null)
                        {
                            resourceNameArray = new String[1];
                            resourceNameArray[0]="No Resource";

//                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityCreate.this,
//                                      android.R.layout.simple_dropdown_item_1line,resourceNameArray);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MomItemCreate.this, android.R.layout.simple_spinner_item);
                            adapter.addAll(resourceNameArray);

                            spinner_responsible.setAdapter(adapter);

                            spinner_responsible.setText("No Responsible");
                        }
                        else
                        {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MomItemCreate.this, android.R.layout.simple_spinner_item);
                            adapter.addAll(resourceNameArray);
                            spinner_responsible.setAdapter(adapter);

                            spinner_responsible.setText("Select Responsible");
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
                Toast.makeText(MomItemCreate.this, "Offline Data Not available for Resources", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Getting Resources...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try{
                                String type = response.getString("type");

                                if(type.equals("ERROR"))
                                {
                                    Toast.makeText(MomItemCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                }

                                if(type.equals("INFO"))
                                {
                                    dataArray = response.getJSONArray("data");
                                    resourceNameArray = new String[dataArray.length()+1];

                                    resourceNameArray[0] = "Select Responsible";

                                    for(int i=0; i<dataArray.length();i++)
                                    {
                                        dataObject = dataArray.getJSONObject(i);
                                        resourceNameArray[i+1]= dataObject.getString("firstName") + " " + dataObject.getString("lastName");
                                    }

                                    if(dataArray==null)
                                    {
                                        resourceNameArray = new String[1];
                                        resourceNameArray[0]="No Resource";

//                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityCreate.this,
//                                      android.R.layout.simple_dropdown_item_1line,resourceNameArray);

                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MomItemCreate.this, android.R.layout.simple_spinner_item);
                                        adapter.addAll(resourceNameArray);

                                        spinner_responsible.setAdapter(adapter);

                                        spinner_responsible.setText("No Responsible");
                                    }
                                    else
                                    {
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MomItemCreate.this, android.R.layout.simple_spinner_item);
                                        adapter.addAll(resourceNameArray);
                                        spinner_responsible.setAdapter(adapter);

                                        spinner_responsible.setText("Select Responsible");
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
            AppController.getInstance().addToRequestQueue(jor);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String date = dayOfMonth + "-" + (MONTHS[monthOfYear]) + "-" + year;

        due_date.setText(date);
    }
}
