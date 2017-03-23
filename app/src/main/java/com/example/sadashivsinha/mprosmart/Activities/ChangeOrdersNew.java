package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChangeOrdersNew extends NewActivity {


    ImageButton btn_section_one, btn_section_two, btn_section_three, btn_section_four;
    LinearLayout hiddenLayoutSectionOne, hiddenLayoutSectionTwo, hiddenLayoutSectionThree, hiddenLayoutSectionFour;
    CardView card_section_one_title, card_section_two_title, card_section_three_title, card_section_four_title;

    EditText text_change_desc, text_justification, text_project_impact;
    EditText text_budget_impact, text_schedule_imapact;
    EditText text_documents_impacted;
    EditText text_decision;
    ImageButton attachBtn;
    PreferenceManager pm;

    TextView project_id, project_name, date_created, due_date;
    Button saveBtn;

    String currentOrderNo;

    JSONArray dataArray;
    JSONObject dataObject;

    String id,changeDescription,justification,projectImpact,budgetImpact,scheduleImpact,documentImpact,decision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_orders_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        attachBtn = (ImageButton) findViewById(R.id.attachBtn);
        saveBtn = (Button) findViewById(R.id.saveBtn);

        project_id = (TextView) findViewById(R.id.project_id);
        project_name = (TextView) findViewById(R.id.project_name);
        due_date = (TextView) findViewById(R.id.date_created);
        date_created = (TextView) findViewById(R.id.due_date);

        pm = new PreferenceManager(getApplicationContext());
        project_id.setText(pm.getString("text_project_id"));
        project_name.setText(pm.getString("text_project_name"));
        due_date.setText(pm.getString("text_due_date"));
        date_created.setText(pm.getString("text_date_created"));

        getSupportActionBar().setTitle("Change Orders");

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mAdapter = new MyAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State


        final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        text_change_desc = (EditText) findViewById(R.id.text_change_desc);
        text_justification = (EditText) findViewById(R.id.text_justification);
        text_project_impact = (EditText) findViewById(R.id.text_project_impact);

        text_budget_impact = (EditText) findViewById(R.id.text_budget_impact);
        text_schedule_imapact = (EditText) findViewById(R.id.text_schedule_imapact);

        text_documents_impacted = (EditText) findViewById(R.id.text_documents_impacted);

        text_decision = (EditText) findViewById(R.id.text_decision);

        card_section_one_title = (CardView) findViewById(R.id.card_section_one_title);
        card_section_two_title = (CardView) findViewById(R.id.card_section_two_title);
        card_section_three_title = (CardView) findViewById(R.id.card_section_three_title);
        card_section_four_title = (CardView) findViewById(R.id.card_section_four_title);

        btn_section_one = (ImageButton) findViewById(R.id.btn_section_one);
        btn_section_two = (ImageButton) findViewById(R.id.btn_section_two);
        btn_section_three = (ImageButton) findViewById(R.id.btn_section_three);
        btn_section_four = (ImageButton) findViewById(R.id.btn_section_four);

        hiddenLayoutSectionOne = (LinearLayout) findViewById(R.id.hiddenLayoutSectionOne);
        hiddenLayoutSectionTwo = (LinearLayout) findViewById(R.id.hiddenLayoutSectionTwo);
        hiddenLayoutSectionThree = (LinearLayout) findViewById(R.id.hiddenLayoutSectionThree);
        hiddenLayoutSectionFour = (LinearLayout) findViewById(R.id.hiddenLayoutSectionFour);


        currentOrderNo = pm.getString("currentOrderNo");
        String url = pm.getString("SERVER_URL") + "/getChangeOrderLineItems?changeOrderId=\"" + currentOrderNo + "\"" ;

        final ProgressDialog pDialog = new ProgressDialog(ChangeOrdersNew.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        pm.putBoolean("changeOrderIsNull" + pm.getString("projectId"), true);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            dataArray = response.getJSONArray("data");
                            if(response.getString("msg").equals("success"))
                            {
                                pm.putBoolean("changeOrderIsNull", false);
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    id = dataObject.getString("id");
                                    changeDescription = dataObject.getString("changeDescription");
                                    justification = dataObject.getString("justification");
                                    projectImpact = dataObject.getString("projectImpact");
                                    budgetImpact = dataObject.getString("budgetImpact");
                                    scheduleImpact = dataObject.getString("scheduleImpact");
                                    documentImpact = dataObject.getString("documentImpact");
                                    decision = dataObject.getString("decision");

                                    text_change_desc.setText(changeDescription);
                                    text_justification.setText(justification);
                                    text_project_impact.setText(projectImpact);
                                    text_budget_impact.setText(budgetImpact);
                                    text_schedule_imapact.setText(scheduleImpact);
                                    text_documents_impacted.setText(documentImpact);
                                    text_decision.setText(decision);

                                    pDialog.dismiss();
                                }
                                pm.putBoolean("changeOrderIsNull" + pm.getString("projectId"), false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        setData(response,false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
        if(pDialog!=null)
            pDialog.dismiss();




        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeOrdersNew.this, AttachmentActivity.class);
                startActivity(intent);
            }
        });

        card_section_one_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayoutSectionOne.getVisibility()==View.GONE)
                {
                    hiddenLayoutSectionOne.setVisibility(View.VISIBLE);
                    hiddenLayoutSectionOne.startAnimation(slide_up);
                }

                else
                {
                    hiddenLayoutSectionOne.startAnimation(slide_down);
                    hiddenLayoutSectionOne.setVisibility(View.GONE);
                }
            }
        });

        btn_section_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(text_change_desc.getText().toString().isEmpty())
                {
                    text_change_desc.setError("Cannot be left empty");
                }
                else if(text_justification.getText().toString().isEmpty())
                {
                    text_justification.setError("Cannot be left empty");
                }
                else if(text_project_impact.getText().toString().isEmpty())
                {
                    text_project_impact.setError("Cannot be left empty");
                }

                else
                {
                    hiddenLayoutSectionOne.startAnimation(slide_down);
                    hiddenLayoutSectionOne.setVisibility(View.GONE);
                    Toast.makeText(ChangeOrdersNew.this, "Values Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });


        card_section_two_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayoutSectionTwo.getVisibility()==View.GONE)
                {
                    hiddenLayoutSectionTwo.setVisibility(View.VISIBLE);
                    hiddenLayoutSectionTwo.startAnimation(slide_up);
                }

                else
                {
                    hiddenLayoutSectionTwo.startAnimation(slide_down);
                    hiddenLayoutSectionTwo.setVisibility(View.GONE);
                }
            }
        });


        btn_section_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(text_budget_impact.getText().toString().isEmpty())
                {
                    text_budget_impact.setError("Cannot be left empty");
                }
                else if(text_schedule_imapact.getText().toString().isEmpty())
                {
                    text_schedule_imapact.setError("Cannot be left empty");
                }
                else
                {
                    hiddenLayoutSectionTwo.setVisibility(View.GONE);
                    hiddenLayoutSectionTwo.startAnimation(slide_down);
                    Toast.makeText(ChangeOrdersNew.this, "Values Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });


        card_section_three_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayoutSectionThree.getVisibility()==View.GONE)
                {
                    hiddenLayoutSectionThree.setVisibility(View.VISIBLE);
                    hiddenLayoutSectionThree.startAnimation(slide_up);
                }

                else
                {
                    hiddenLayoutSectionThree.startAnimation(slide_down);
                    hiddenLayoutSectionThree.setVisibility(View.GONE);
                }
            }
        });
        btn_section_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_documents_impacted.getText().toString().isEmpty())
                {
                    text_documents_impacted.setError("Cannot be left empty");
                }
                else
                {
                    hiddenLayoutSectionThree.startAnimation(slide_down);
                    hiddenLayoutSectionThree.setVisibility(View.GONE);
                    Toast.makeText(ChangeOrdersNew.this, "Values Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });



        card_section_four_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayoutSectionFour.getVisibility()==View.GONE)
                {
                    hiddenLayoutSectionFour.setVisibility(View.VISIBLE);
                    hiddenLayoutSectionFour.startAnimation(slide_up);
                }

                else
                {
                    hiddenLayoutSectionFour.startAnimation(slide_down);
                    hiddenLayoutSectionFour.setVisibility(View.GONE);
                }
            }
        });

        btn_section_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_decision.getText().toString().isEmpty())
                {
                    text_decision.setError("Cannot be left empty");
                }
                else
                {
                    hiddenLayoutSectionFour.startAnimation(slide_down);
                    hiddenLayoutSectionFour.setVisibility(View.GONE);
                    Toast.makeText(ChangeOrdersNew.this, "Values Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //section one

                if(text_change_desc.getText().toString().isEmpty())
                {
                    text_change_desc.setError("Cannot be left empty");
                    hiddenLayoutSectionOne.setVisibility(View.VISIBLE);
                }
                else if(text_justification.getText().toString().isEmpty())
                {
                    text_justification.setError("Cannot be left empty");
                    hiddenLayoutSectionOne.setVisibility(View.VISIBLE);
                }
                else if(text_project_impact.getText().toString().isEmpty())
                {
                    text_project_impact.setError("Cannot be left empty");
                    hiddenLayoutSectionOne.setVisibility(View.VISIBLE);
                }
                //section two

                else if(text_budget_impact.getText().toString().isEmpty())
                {
                    text_budget_impact.setError("Cannot be left empty");
                    hiddenLayoutSectionTwo.setVisibility(View.VISIBLE);
                }
                else if(text_schedule_imapact.getText().toString().isEmpty())
                {
                    text_schedule_imapact.setError("Cannot be left empty");
                    hiddenLayoutSectionTwo.setVisibility(View.VISIBLE);
                }
                //section three

                else if(text_documents_impacted.getText().toString().isEmpty())
                {
                    text_documents_impacted.setError("Cannot be left empty");
                    hiddenLayoutSectionThree.setVisibility(View.VISIBLE);
                }
                //section four

                else if(text_decision.getText().toString().isEmpty())
                {
                    text_decision.setError("Cannot be left empty");
                    hiddenLayoutSectionFour.setVisibility(View.VISIBLE);
                }


                else
                {

                    final ProgressDialog progressDialog = new ProgressDialog(ChangeOrdersNew.this);
                    progressDialog.setMessage("Sending Data");
                    progressDialog.show();

                    JSONObject object = new JSONObject();

                    try {
                        object.put("changeOrderId", currentOrderNo);
                        object.put("changeDescription",text_change_desc.getText().toString());
                        object.put("justification",text_justification.getText().toString());
                        object.put("projectImpact", text_project_impact.getText().toString());
                        object.put("budgetImpact",text_budget_impact.getText().toString());
                        object.put("scheduleImpact",text_schedule_imapact.getText().toString());
                        object.put("documentImpact", text_documents_impacted.getText().toString());
                        object.put("decision",text_decision.getText().toString());


                        Log.d("change order object :", object.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestQueue requestQueue = Volley.newRequestQueue(ChangeOrdersNew.this);

                    int requestMethod;
                    String url;

                    if(pm.getBoolean("changeOrderIsNull" + pm.getString("projectId")))
                    {
                        requestMethod = Request.Method.POST;
                        Log.d("METHOD", "POST");

                        url = pm.getString("SERVER_URL") + "/postChangeOrderLineItems";
                        try {
                            object.put("changeOrderId",currentOrderNo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        requestMethod = Request.Method.PUT;
                        Log.d("METHOD", "PUT");

                        url = pm.getString("SERVER_URL") + "/putChangeOrderLineItems?changeOrderId=\"" + currentOrderNo + "\"" ;
                    }

                    JsonObjectRequest jor = new JsonObjectRequest(requestMethod, url, object,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Log.d("order response :", response.toString());
                                        if(response.getString("msg").equals("success"))
                                        {
                                            Toast.makeText(ChangeOrdersNew.this, "Saved", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(ChangeOrdersNew.this, ChangeOrdersNew.class);
                                            startActivity(intent);
                                        }
                                    }
                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                        progressDialog.dismiss();
                                    }
                                    //response success message display
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Volley","Error");
                                    Toast.makeText(ChangeOrdersNew.this, error.toString(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                    );

                    requestQueue.add(jor);

                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChangeOrdersNew.this, AllChangeOrders.class);
        startActivity(intent);
    }
}
