package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Adapters.VendorAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.VendorList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VendorActivity extends AppCompatActivity implements View.OnClickListener {

    TextView vendor_id, vendor_name, vendor_type, discipline, tax_id, licence_no, company_name;

    private List<VendorList> vendorList = new ArrayList<>();
    private RecyclerView recyclerView;
    private VendorAdapter vendorAdapter;

    JSONArray dataArray;
    JSONObject dataObject;
    Boolean isInternetPresent = false;

    VendorList items;
    ConnectionDetector cd;
    ProgressDialog pDialog;

    String currentProjectNo, currentProjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor);

        PreferenceManager pm = new PreferenceManager(this);

        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        String vendorId = pm.getString("vendor_id");
        String vendorName = pm.getString("vendor_name");
        String vendorType = pm.getString("vendor_type");
        String disciplineText = pm.getString("discipline");

        vendor_id = (TextView) findViewById(R.id.vendor_id);
        vendor_name = (TextView) findViewById(R.id.vendor_name);
        vendor_type = (TextView) findViewById(R.id.vendor_type);
        discipline = (TextView) findViewById(R.id.discipline);
        tax_id = (TextView) findViewById(R.id.tax_id);
        licence_no = (TextView) findViewById(R.id.licence_no);
        company_name = (TextView) findViewById(R.id.company_name);

        vendor_id.setText(vendorId);
        vendor_name.setText(vendorName);
        vendor_type.setText(vendorType);
        discipline.setText(disciplineText);

        tax_id.setText("T0001414787");
        licence_no.setText("L-1585978457");
        company_name.setText("ABC Company");


        final LinearLayout hiddenLayout = (LinearLayout) findViewById(R.id.hiddenLayout);
        hiddenLayout.setVisibility(View.GONE);

        CardView cardview = (CardView) findViewById(R.id.cardview);
        cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayout.getVisibility()==View.GONE)
                {
                    hiddenLayout.setVisibility(View.VISIBLE);
                    hiddenLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_show));
                }
                else
                {
                    hiddenLayout.setVisibility(View.GONE);
                }
            }
        });

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            CoordinatorLayout main_content = (CoordinatorLayout) findViewById(R.id.main_content);
            Snackbar snackbar = Snackbar.make(main_content, getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(VendorActivity.this));
        recyclerView.setHasFixedSize(true);
        vendorAdapter = new VendorAdapter(vendorList);
        recyclerView.setAdapter(vendorAdapter);

        pDialog = new ProgressDialog(VendorActivity.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                prepareItems();
                return null;
            }

        }
        new MyTask().execute();


        FloatingActionButton fab_add;
        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add: {
                Intent intent = new Intent(VendorActivity.this, VendorCreate.class);
                startActivity(intent);
            }
        }
    }

    public void prepareItems()
    {
        pDialog.dismiss();
//        items = new VendorList("Marie", "Jane", "8887485784", "aa@a.com", "102", "Local Street", "Silicon Road",
//                "Maharastra", "India", "460078");
//        vendorList.add(items);
//
//        items = new VendorList("Bill", "June", "9987447102", "aa@a.com", "98", "Local Street", "Juhu Road",
//                "Maharastra", "India", "410705");
//        vendorList.add(items);
//
//        vendorAdapter.notifyDataSetChanged();
//        pDialog.dismiss();
    }
}