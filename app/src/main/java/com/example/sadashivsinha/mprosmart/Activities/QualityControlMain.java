package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.NewAllProjectList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import org.json.JSONArray;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class QualityControlMain extends NewActivity implements View.OnClickListener {

    RelativeLayout create_quality_plan, create_quality_standard, create_quality_checklist, create_punch_list;
    ImageButton  create_quality_plan_btn, create_quality_standard_btn, create_quality_checklist_btn, create_punch_list_btn;
    CardView card_quality_control, card_quality_plans, card_quality_standard, card_quality_checklist, card_punch_list, card_completion_list;

    Toolbar toolbar;
    HelveticaRegular description, date, created_by, project_id, text_currency, text_budget;
    HelveticaBold title;
    String currentProjectNo, currentUserId, currentCompanyId;
    PreferenceManager pm;
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
        setContentView(R.layout.activity_quality_control_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Quality Control");

        pm = new PreferenceManager(getApplicationContext());
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

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(QualityControlMain.this, R.string.no_internet_error, Style.ALERT, main_layout).show();
        }

//        pDialog = new ProgressDialog(QualityControlMain.this);
//        pDialog.setMessage("Getting Data ...");
//        pDialog.setIndeterminate(false);
//        pDialog.setCancelable(true);
//        pDialog.show();

        currentProjectNo = pm.getString("projectId");

        prepareHeader();

        create_punch_list = (RelativeLayout) findViewById(R.id.create_punch_list);
        create_quality_plan = (RelativeLayout) findViewById(R.id.create_quality_plan);
        create_quality_standard = (RelativeLayout) findViewById(R.id.create_quality_standard);
        create_quality_checklist = (RelativeLayout) findViewById(R.id.create_quality_checklist);

        create_punch_list.setOnClickListener(this);
        create_quality_plan.setOnClickListener(this);
        create_quality_standard.setOnClickListener(this);
        create_quality_checklist.setOnClickListener(this);

        create_punch_list_btn = (ImageButton) findViewById(R.id.create_punch_list_btn);
        create_quality_plan_btn = (ImageButton) findViewById(R.id.create_quality_plan_btn);
        create_quality_standard_btn = (ImageButton) findViewById(R.id.create_quality_standard_btn);
        create_quality_checklist_btn = (ImageButton) findViewById(R.id.create_quality_checklist_btn);

        create_punch_list_btn.setOnClickListener(this);
        create_quality_plan_btn.setOnClickListener(this);
        create_quality_standard_btn.setOnClickListener(this);
        create_quality_checklist_btn.setOnClickListener(this);

        card_quality_control = (CardView) findViewById(R.id.card_quality_control);
        card_punch_list = (CardView) findViewById(R.id.card_punch_list);
        card_completion_list = (CardView) findViewById(R.id.card_completion_list);
        card_quality_plans = (CardView) findViewById(R.id.card_quality_plans);
        card_quality_standard = (CardView) findViewById(R.id.card_quality_standard);
        card_quality_checklist = (CardView) findViewById(R.id.card_quality_checklist);

        card_quality_control.setOnClickListener(this);
        card_punch_list.setOnClickListener(this);
        card_completion_list.setOnClickListener(this);
        card_quality_plans.setOnClickListener(this);
        card_quality_standard.setOnClickListener(this);
        card_quality_checklist.setOnClickListener(this);

    }

    private void prepareHeader()
    {
        title.setText(pm.getString("projectName"));
        description.setText(pm.getString("projectDesc"));
        date.setText(pm.getString("createdDate"));
        created_by.setText(pm.getString("createdBy"));
        project_id.setText(pm.getString("projectId"));
        text_currency.setText(pm.getString("currency"));
        text_budget.setText(pm.getString("budget"));

        String imageUrl = pm.getString("imageUrl");

        if(!imageUrl.equals(""))
        {
            Glide.with(QualityControlMain.this)
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
            case R.id.card_completion_list:
            {
                Intent intent = new Intent(QualityControlMain.this, CompletionListActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.card_quality_control:
            {
                Intent intent = new Intent(QualityControlMain.this, AllQualityControl.class);
                startActivity(intent);
            }
            break;
            case R.id.card_punch_list:
            {
                Intent intent = new Intent(QualityControlMain.this, AllPunchLists.class);
                startActivity(intent);
            }
            break;
            case R.id.create_quality:
            {
                Intent intent = new Intent(QualityControlMain.this, QualityControlCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.create_punch_list:
            {
                Intent intent = new Intent(QualityControlMain.this, PunchListCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.create_punch_list_btn:
            {
                Intent intent = new Intent(QualityControlMain.this, PunchListCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.create_quality_btn:
            {
                Intent intent = new Intent(QualityControlMain.this, QualityControlCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.card_quality_plans:
            {
                Intent intent = new Intent(QualityControlMain.this, AllQualityPlans.class);
                startActivity(intent);
            }
            break;
            case R.id.create_quality_plan:
            {
                Intent intent = new Intent(QualityControlMain.this, AllQualityPlans.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
            case R.id.create_quality_plan_btn:
            {
                Intent intent = new Intent(QualityControlMain.this, AllQualityPlans.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
            case R.id.card_quality_standard:
            {
                Intent intent = new Intent(QualityControlMain.this, AllQualityStandards.class);
                startActivity(intent);
            }
            break;
            case R.id.create_quality_standard:
            {
                Intent intent = new Intent(QualityControlMain.this, QualityStandardCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.create_quality_standard_btn:
            {
                Intent intent = new Intent(QualityControlMain.this, QualityStandardCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.card_quality_checklist:
            {
                Intent intent = new Intent(QualityControlMain.this, AllQualityChecklist.class);
                startActivity(intent);
            }
            break;
            case R.id.create_quality_checklist:
            {
                Intent intent = new Intent(QualityControlMain.this, QualityChecklistCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.create_quality_checklist_btn:
            {
                Intent intent = new Intent(QualityControlMain.this, QualityChecklistCreate.class);
                startActivity(intent);
            }
            break;
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(QualityControlMain.this, MainCategories.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
