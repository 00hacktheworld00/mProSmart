package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.NewAllProjectList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainCategories extends NewActivity implements View.OnClickListener {

    Toolbar toolbar;
    LinearLayout card_project, card_site_project, card_quality_control, card_site_procurement, card_inventory, card_budget;
    PreferenceManager pm;

    HelveticaRegular description, date, created_by, project_id, text_currency, text_budget;
    HelveticaBold title;

    String currentProjectNo, currentUserId, currentCompanyId;
    Drawable mDrawable;
    String budget, currency;


    JSONArray dataArray;
    JSONObject dataObject;
    String projectId, projectName, projectDesc, createdBy, createdDate, imageUrl;
    NewAllProjectList items;
    ConnectionDetector cd;
    public static final String TAG = QualityControlMain.class.getSimpleName();
    Boolean isInternetPresent = false;
    ProgressDialog pDialog;
    String url;
    CircleImageView company_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_categories);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Project - " + pm.getString("projectId"));

        currentUserId = pm.getString("userId");
        currentCompanyId = pm.getString("companyId");

        currentUserId = pm.getString("userId");

        url = pm.getString("SERVER_URL") + "/getProjects?companyId=\""+currentCompanyId+"\"";

        text_currency = (HelveticaRegular) findViewById(R.id.text_currency);
        text_budget = (HelveticaRegular) findViewById(R.id.text_budget);

        title = (HelveticaBold) findViewById(R.id.title);
        description = (HelveticaRegular) findViewById(R.id.description);
        date = (HelveticaRegular) findViewById(R.id.date);
        created_by = (HelveticaRegular) findViewById(R.id.created_by);
        project_id = (HelveticaRegular) findViewById(R.id.project_id);
        company_logo = (CircleImageView) findViewById(R.id.company_logo);


        prepareHeader();

        card_project = (LinearLayout) findViewById(R.id.card_project);
        card_site_project = (LinearLayout) findViewById(R.id.card_site_project);
        card_quality_control = (LinearLayout) findViewById(R.id.card_quality_control);
        card_site_procurement = (LinearLayout) findViewById(R.id.card_site_procurement);
        card_inventory = (LinearLayout) findViewById(R.id.card_inventory);
        card_budget = (LinearLayout) findViewById(R.id.card_budget);


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

        card_project.setOnClickListener(this);
        card_site_project.setOnClickListener(this);
        card_quality_control.setOnClickListener(this);
        card_site_procurement.setOnClickListener(this);
        card_inventory.setOnClickListener(this);
        card_budget.setOnClickListener(this);
    }

    private void prepareHeader()
    {
        if(!isInternetPresent)
        {
            title.setText(pm.getString("projectName"));
            description.setText(pm.getString("projectDesc"));
            date.setText(pm.getString("createdDate"));
            created_by.setText(pm.getString("createdBy"));
            project_id.setText(pm.getString("projectId"));
            text_currency.setText(pm.getString("currency"));
            text_budget.setText(pm.getString("budget"));

        }
        else
        {
            pDialog = new ProgressDialog(MainCategories.this);
            pDialog.setMessage("Getting server data");
            pDialog.show();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response project : ", response.toString());
                            try
                            {
//                            dataObject = response.getJSONObject(0);
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++) {
                                    dataObject = dataArray.getJSONObject(i);
                                    projectId = dataObject.getString("projectId");

                                    if (projectId.equals(currentProjectNo)) {

                                        projectName = dataObject.getString("projectName");
                                        projectDesc = dataObject.getString("projectDescription");
                                        createdBy = dataObject.getString("createdBy");
                                        createdDate = dataObject.getString("createddate");
                                        imageUrl = dataObject.getString("photoUrl");
                                        budget = dataObject.getString("totalBudget");
                                        currency = dataObject.getString("currencyCode");

                                        Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                                        createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                        title.setText(projectName);
                                        description.setText(projectDesc);
                                        date.setText(createdDate);
                                        created_by.setText(createdBy);
                                        project_id.setText(projectId);
                                        text_currency.setText(currency);
                                        text_budget.setText(budget);

                                        pm.putString("projectName", projectName);
                                        pm.putString("projectDesc", projectDesc);
                                        pm.putString("createdDate", createdDate);
                                        pm.putString("createdBy", createdBy);
                                        pm.putString("projectId", projectId);
                                        pm.putString("currency", currency);
                                        pm.putString("budget", budget);

                                        break;
                                    }
                                }
                                pDialog.dismiss();
                            } catch (ParseException | JSONException e) {
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
        }
        String imageUrl = pm.getString("imageUrl");

        if(!imageUrl.equals(""))
        {
            Glide.with(MainCategories.this)
                    .load(imageUrl)
                    .crossFade()
                    .into(company_logo);

        }

        else
        {
            mDrawable = getResources().getDrawable(R.drawable.no_logo);
            company_logo.setImageDrawable(mDrawable);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_project: {
                Intent intent = new Intent(MainCategories.this, ProjectPlanningSchedulingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            break;
            case R.id.card_site_project: {
                Intent intent = new Intent(MainCategories.this, SiteProjectDelivery.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            break;
            case R.id.card_quality_control: {
                Intent intent = new Intent(MainCategories.this, QualityControlMain.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            break;
            case R.id.card_site_procurement: {
                Intent intent = new Intent(MainCategories.this, SiteProcurementActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            break;
            case R.id.card_inventory: {
                Intent intent = new Intent(MainCategories.this, InventoryMainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            break;
            case R.id.card_budget: {
                Intent intent = new Intent(MainCategories.this, BudgetMainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainCategories.this, NewAllProjects.class);
        startActivity(intent);
    }
}
