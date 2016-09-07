package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MomItemCreate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText text_matter, text_responsible;
    ProgressDialog pDialog;
    String momId;
    String currentDate;
    TextView due_date;
    PreferenceManager pm;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mom_item_create);

        Button createBtn, attachBtn;
        pm = new PreferenceManager(MomItemCreate.this);
        momId = pm.getString("momId");

        createBtn = (Button) findViewById(R.id.createBtn);
        attachBtn = (Button) findViewById(R.id.attachBtn);

        text_matter = (EditText) findViewById(R.id.text_matter);
        text_responsible = (EditText) findViewById(R.id.text_responsible);

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


        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MomItemCreate.this, AttachmentActivity.class);
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
                else if(text_responsible.getText().toString().isEmpty())
                {
                    text_responsible.setError("Field cannot be empty");
                }
                else if(due_date.getText().toString().equals("Due Date"))
                {
                    Toast.makeText(MomItemCreate.this, "Select Due Date", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    prepareItems();
                }
            }
        });
    }

    public void prepareItems()
    {
        JSONObject object = new JSONObject();

        try
        {
            Date tradeDate = null;
            tradeDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(due_date.getText().toString());
            String newDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tradeDate);

            object.put("matterDiscussed",text_matter.getText().toString());
            object.put("responsible",text_responsible.getText().toString());
            object.put("dueDate",newDate);
            object.put("numberOfAttachments","0");
            object.put("momId",momId);
        }
        catch (JSONException | ParseException e)
        {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(MomItemCreate.this);

        String url = MomItemCreate.this.getResources().getString(R.string.server_url) + "/postMomLineItems";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(MomItemCreate.this, "MOM Line Item created. ID - "+response.getString("data"), Toast.LENGTH_SHORT).show();


                                if(pDialog!=null)
                                    pDialog.dismiss();

                                Intent intent = new Intent(MomItemCreate.this, MomActivity.class);
                                startActivity(intent);
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
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createMOMPendingLine = pm.getBoolean("createMOMPendingLine");

            if(createMOMPendingLine)
            {
                Toast.makeText(MomItemCreate.this, "Already a MOM creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(MomItemCreate.this, "Internet not currently available. MOM will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectMOMLine", object.toString());
                pm.putString("urlMOMLine", url);
                pm.putString("toastMessageMOMLine", "MOM Created");
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String date = dayOfMonth + "-" + (MONTHS[monthOfYear]) + "-" + year;

        due_date.setText(date);
    }
}
