package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.NewAllProjectList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SiteProjectDelivery extends NewActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    String dateToSendDailyProgress, whichDate;
    View dialogView;
    AlertDialog show;
    TextView daily_progress_date, from_date, to_date;

    RelativeLayout create_resources, create_mom, create_submittals, create_sub_register, create_subcontractor;
    ImageButton create_resources_btn, create_mom_btn, create_submittals_btn, create_sub_register_btn, create_subcontractor_btn;
    CardView card_resource, card_add_resources, card_mom, card_site_diary, card_submittals, card_sub_register, card_daily_progress,
            card_change_orders, card_subcontractor;

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
    public static final String TAG = SiteProjectDelivery.class.getSimpleName();
    Boolean isInternetPresent = false;
    ProgressDialog pDialog;
    String url;
    CircleImageView company_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_project_delivery);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Site Project Delivery");

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
            Crouton.makeText(SiteProjectDelivery.this, R.string.no_internet_error, Style.ALERT, main_layout).show();
        }

//        pDialog = new ProgressDialog(SiteProjectDelivery.this);
//        pDialog.setMessage("Getting Data ...");
//        pDialog.setIndeterminate(false);
//        pDialog.setCancelable(true);
//        pDialog.show();

        currentProjectNo = pm.getString("projectId");

        prepareHeader();

        create_mom = (RelativeLayout) findViewById(R.id.create_mom);
        create_submittals = (RelativeLayout) findViewById(R.id.create_submittals);
        create_sub_register = (RelativeLayout) findViewById(R.id.create_sub_register);
        create_subcontractor = (RelativeLayout) findViewById(R.id.create_subcontractor);
        create_resources = (RelativeLayout) findViewById(R.id.create_resource);

        create_mom.setOnClickListener(this);
        create_submittals.setOnClickListener(this);
        create_sub_register.setOnClickListener(this);
        create_subcontractor.setOnClickListener(this);
        create_resources.setOnClickListener(this);


        create_mom_btn = (ImageButton) findViewById(R.id.create_mom_btn);
        create_submittals_btn = (ImageButton) findViewById(R.id.create_submittals_btn);
        create_sub_register_btn = (ImageButton) findViewById(R.id.create_sub_register_btn);
        create_subcontractor_btn = (ImageButton) findViewById(R.id.create_subcontractor_btn);
        create_resources_btn = (ImageButton) findViewById(R.id.create_resource_btn);

        create_mom_btn.setOnClickListener(this);
        create_submittals_btn.setOnClickListener(this);
        create_sub_register_btn.setOnClickListener(this);
        create_subcontractor_btn.setOnClickListener(this);
        create_resources_btn.setOnClickListener(this);

        card_mom = (CardView) findViewById(R.id.card_mom);
        card_submittals = (CardView) findViewById(R.id.card_submittals);
        card_sub_register = (CardView) findViewById(R.id.card_sub_register);
        card_subcontractor = (CardView) findViewById(R.id.card_subcontractor);
        card_resource = (CardView) findViewById(R.id.card_resource);
        card_site_diary = (CardView) findViewById(R.id.card_site_diary);
        card_add_resources = (CardView) findViewById(R.id.card_add_resource);
        card_change_orders = (CardView) findViewById(R.id.card_change_orders);
        card_daily_progress = (CardView) findViewById(R.id.card_daily_progress);

        card_mom.setOnClickListener(this);
        card_submittals.setOnClickListener(this);
        card_sub_register.setOnClickListener(this);
        card_subcontractor.setOnClickListener(this);
        card_resource.setOnClickListener(this);
        card_site_diary.setOnClickListener(this);
        card_add_resources.setOnClickListener(this);
        card_change_orders.setOnClickListener(this);
        card_daily_progress.setOnClickListener(this);
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
            Glide.with(SiteProjectDelivery.this)
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
            case R.id.card_site_diary:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, AllSiteDiary.class);
                startActivity(intent);
            }
            break;
            case R.id.card_add_resource:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, AllAddResources.class);
                startActivity(intent);
            }
            break;
            case R.id.card_mom:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, AllMom.class);
                startActivity(intent);
            }
            break;
            case R.id.card_submittals:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, AllSubmittals.class);
                startActivity(intent);
            }
            break;
            case R.id.card_sub_register:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, AllSubmittalsRegister.class);
                startActivity(intent);
            }
            break;
            case R.id.card_subcontractor:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, AllSubcontractor.class);
                startActivity(intent);
            }
            break;
            case R.id.card_change_orders:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, AllChangeOrders.class);
                startActivity(intent);
            }
            break;
            case R.id.card_resource:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, AllResource.class);
                startActivity(intent);
            }
            break;
            case R.id.card_daily_progress:
            {
//                Intent intent = new Intent(SiteProjectDelivery.this, DailyProgressDetails.class);
//                startActivity(intent);
                final AlertDialog.Builder alert = new AlertDialog.Builder(SiteProjectDelivery.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(SiteProjectDelivery.this).inflate(R.layout.dialog_daily_progress, null);
                alert.setView(dialogView);

                show = alert.show();

                final PreferenceManager pm = new PreferenceManager(dialogView.getContext());
                currentProjectNo = pm.getString("projectId");

                Button viewBtn;

                daily_progress_date = (TextView) dialogView.findViewById(R.id.text_date);
                viewBtn = (Button) dialogView.findViewById(R.id.viewBtn);


                daily_progress_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        whichDate = "dailyProgress";
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance(
                                SiteProjectDelivery.this,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.setMaxDate(now);
                        dpd.show(getFragmentManager(), "Datepickerdialog");
                    }
                });

                viewBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(daily_progress_date.getText().toString().isEmpty())
                        {
                            Toast.makeText(SiteProjectDelivery.this, "Select a Date to view the Progress", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Intent intent = new Intent(SiteProjectDelivery.this, DailyProgressDetails.class);
                            pm.putString("currentProgressDate", dateToSendDailyProgress);
                            startActivity(intent);
                        }
                    }
                });
            }
            break;
            case R.id.create_mom:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, AllMom.class);
                intent.putExtra("create", "YES");
                startActivity(intent);
            }
            break;
            case R.id.create_submittals:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, SubmittalsCreateActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.create_sub_register:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, SubmittalRegisterCreateActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.create_subcontractor:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, AllSubcontractor.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
            case R.id.create_resource:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, AllResource.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
            case R.id.create_mom_btn:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, MomCreateActivity.class);
                intent.putExtra("create", "YES");
                startActivity(intent);
            }
            break;
            case R.id.create_submittals_btn:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, SubmittalsCreateActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.create_sub_register_btn:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, SubmittalRegisterCreateActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.create_subcontractor_btn:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, AllSubcontractor.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
            case R.id.create_resource_btn:
            {
                Intent intent = new Intent(SiteProjectDelivery.this, AllResource.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
        }
    }
    @Override
    public void onDateSet(final DatePickerDialog view, final int year, final int monthOfYear, final int dayOfMonth) {
        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String date = year+"-"+(MONTHS[monthOfYear])+"-"+dayOfMonth;
        Date tradeDate;
        tradeDate = null;

        if(whichDate.equals("from"))
        {
            try
            {
                tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

            from_date.setText(date);

            to_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    whichDate = "to";
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            SiteProjectDelivery.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.setMaxDate(now);
                    dpd.setMinDate(new GregorianCalendar(year, monthOfYear, dayOfMonth));
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                }
            });

        }
        else if(whichDate.equals("dailyProgress"))
        {
            dateToSendDailyProgress = date;
            try
            {
                tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

            daily_progress_date.setText(date);
        }
        else
        {
            try
            {
                tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);
            to_date.setText(date);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SiteProjectDelivery.this, MainCategories.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
