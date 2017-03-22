package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.NewAllProjectList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ViewPurchaseOrders extends NewActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    TextView title, description, date, created_by, project_id, text_currency, text_budget;

    Toolbar toolbar;
    URL picUrl;
    Bitmap bmp;
    Drawable mDrawable;
    View dialogView;
    AlertDialog show;
    String whichDate, item, itemDesc, budget, currency;
    String[] itemArray, itemDescArray;
    TextView from_date, to_date, daily_progress_date;
    String dateToSendDailyProgress;
    Spinner spinner_item;
    CircleImageView company_logo;

    RelativeLayout create_mom, create_submittals, create_sub_register, create_subcontractor,
            create_resources, create_invoice, create_purchase, create_quality, create_punch_list, create_wbs, create_quality_plan,
            create_quality_standard, create_quality_checklist, create_boq, create_material_issue, create_item, create_inventory,create_requisition;

    ImageButton create_mom_btn, create_submittals_btn, create_sub_register_btn, create_subcontractor_btn,
            create_resources_btn, create_invoice_btn, create_purchase_btn, create_quality_btn, create_punch_list_btn,
            create_wbs_btn, create_quality_plan_btn, create_quality_standard_btn, create_boq_btn, create_quality_checklist_btn,
            create_material_issue_btn, create_inventory_btn, create_requisition_btn, create_item_btn;

    CardView card_purchase_order, card_quality_control, card_mom, card_submittals, card_completion_list,card_change_orders,card_expense,
            card_sub_register, card_budget_transfer, card_budget_changes,  card_graph,
            card_subcontractor, card_resource, card_punch_list, card_invoice, card_wbs, card_site_diary,
            card_add_resources, card_add_vendors, card_quality_plans, card_quality_standard, card_quality_checklist, card_boq,
            card_material_issue, card_inventory, card_requisition, card_daily_progress, card_add_items, card_all_pr;

    JSONArray dataArray;
    JSONObject dataObject;
    String projectId, projectName, projectDesc, createdBy, createdDate, imageUrl;
    NewAllProjectList items;
    ConnectionDetector cd;
    public static final String TAG = ViewPurchaseOrders.class.getSimpleName();
    Boolean isInternetPresent = false;
    ProgressDialog pDialog;
    String currentProjectNo, currentUserId, currentCompanyId;
    PreferenceManager pm;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_view_purchase_order);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        currentUserId = pm.getString("userId");
        currentCompanyId = pm.getString("companyId");

        currentUserId = pm.getString("userId");

        url = pm.getString("SERVER_URL") + "/getProjects?companyId=\""+currentCompanyId+"\"";

        text_currency = (TextView) findViewById(R.id.text_currency);
        text_budget = (TextView) findViewById(R.id.text_budget);

        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        date = (TextView) findViewById(R.id.date);
        created_by = (TextView) findViewById(R.id.created_by);
        project_id = (TextView) findViewById(R.id.project_id);
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
            Crouton.makeText(ViewPurchaseOrders.this, R.string.no_internet_error, Style.ALERT, main_layout).show();
        }

//        pDialog = new ProgressDialog(ViewPurchaseOrders.this);
//        pDialog.setMessage("Getting Data ...");
//        pDialog.setIndeterminate(false);
//        pDialog.setCancelable(true);
//        pDialog.show();

        currentProjectNo = pm.getString("projectId");

        prepareHeader();

//        class MyTask extends AsyncTask<Void, Void, Void>
//        {
//            @Override
//            protected Void doInBackground(Void... params)
//            {
//                prepareHeader();
//                return null;
//            }
//        }
//
//        new MyTask().execute();
        create_mom = (RelativeLayout) findViewById(R.id.create_mom);
        create_submittals = (RelativeLayout) findViewById(R.id.create_submittals);
        create_sub_register = (RelativeLayout) findViewById(R.id.create_sub_register);
        create_subcontractor = (RelativeLayout) findViewById(R.id.create_subcontractor);
        create_resources = (RelativeLayout) findViewById(R.id.create_resource);
        create_invoice = (RelativeLayout) findViewById(R.id.create_invoice);
        create_purchase = (RelativeLayout) findViewById(R.id.create_purchase);
        create_quality = (RelativeLayout) findViewById(R.id.create_quality);
        create_punch_list = (RelativeLayout) findViewById(R.id.create_punch_list);
        create_wbs = (RelativeLayout) findViewById(R.id.create_wbs);
        create_quality_plan = (RelativeLayout) findViewById(R.id.create_quality_plan);
        create_quality_standard = (RelativeLayout) findViewById(R.id.create_quality_standard);
        create_quality_checklist = (RelativeLayout) findViewById(R.id.create_quality_checklist);
        create_boq = (RelativeLayout) findViewById(R.id.create_boq);
        create_material_issue = (RelativeLayout) findViewById(R.id.create_material_issue);
        create_inventory = (RelativeLayout) findViewById(R.id.create_inventory);
        create_requisition = (RelativeLayout) findViewById(R.id.create_requisition);
        create_item = (RelativeLayout) findViewById(R.id.create_item);

        create_mom.setOnClickListener(this);
        create_submittals.setOnClickListener(this);
        create_sub_register.setOnClickListener(this);
        create_subcontractor.setOnClickListener(this);
        create_resources.setOnClickListener(this);
        create_invoice.setOnClickListener(this);
        create_purchase.setOnClickListener(this);
        create_quality.setOnClickListener(this);
        create_punch_list.setOnClickListener(this);
        create_wbs.setOnClickListener(this);
        create_quality_plan.setOnClickListener(this);
        create_quality_standard.setOnClickListener(this);
        create_quality_checklist.setOnClickListener(this);
        create_boq.setOnClickListener(this);
        create_material_issue.setOnClickListener(this);
        create_inventory.setOnClickListener(this);
        create_requisition.setOnClickListener(this);
        create_item.setOnClickListener(this);


        create_mom_btn = (ImageButton) findViewById(R.id.create_mom_btn);
        create_submittals_btn = (ImageButton) findViewById(R.id.create_submittals_btn);
        create_sub_register_btn = (ImageButton) findViewById(R.id.create_sub_register_btn);
        create_subcontractor_btn = (ImageButton) findViewById(R.id.create_subcontractor_btn);
        create_resources_btn = (ImageButton) findViewById(R.id.create_resource_btn);
        create_invoice_btn = (ImageButton) findViewById(R.id.create_invoice_btn);
        create_purchase_btn = (ImageButton) findViewById(R.id.create_purchase_btn);
        create_quality_btn = (ImageButton) findViewById(R.id.create_quality_btn);
        create_punch_list_btn = (ImageButton) findViewById(R.id.create_punch_list_btn);
        create_wbs_btn = (ImageButton) findViewById(R.id.create_wbs_btn);
        create_quality_plan_btn = (ImageButton) findViewById(R.id.create_quality_plan_btn);
        create_quality_standard_btn = (ImageButton) findViewById(R.id.create_quality_standard_btn);
        create_quality_checklist_btn = (ImageButton) findViewById(R.id.create_quality_checklist_btn);
        create_boq_btn = (ImageButton) findViewById(R.id.create_boq_btn);
        create_material_issue_btn = (ImageButton) findViewById(R.id.create_material_issue_btn);
        create_inventory_btn = (ImageButton) findViewById(R.id.create_inventory_btn);
        create_requisition_btn = (ImageButton) findViewById(R.id.create_requisition_btn);
        create_item_btn = (ImageButton) findViewById(R.id.create_item_btn);

        create_mom_btn.setOnClickListener(this);
        create_submittals_btn.setOnClickListener(this);
        create_sub_register_btn.setOnClickListener(this);
        create_subcontractor_btn.setOnClickListener(this);
        create_resources_btn.setOnClickListener(this);
        create_invoice_btn.setOnClickListener(this);
        create_purchase_btn.setOnClickListener(this);
        create_quality_btn.setOnClickListener(this);
        create_punch_list_btn.setOnClickListener(this);
        create_wbs_btn.setOnClickListener(this);
        create_quality_plan_btn.setOnClickListener(this);
        create_quality_standard_btn.setOnClickListener(this);
        create_quality_checklist_btn.setOnClickListener(this);
        create_boq_btn.setOnClickListener(this);
        create_material_issue_btn.setOnClickListener(this);
        create_inventory_btn.setOnClickListener(this);
        create_requisition_btn.setOnClickListener(this);
        create_item_btn.setOnClickListener(this);


        card_purchase_order = (CardView) findViewById(R.id.card_purchase_orders);
        card_quality_control = (CardView) findViewById(R.id.card_quality_control);
        card_mom = (CardView) findViewById(R.id.card_mom);
        card_submittals = (CardView) findViewById(R.id.card_submittals);
        card_sub_register = (CardView) findViewById(R.id.card_sub_register);
        card_subcontractor = (CardView) findViewById(R.id.card_subcontractor);
        card_resource = (CardView) findViewById(R.id.card_resource);
        card_punch_list = (CardView) findViewById(R.id.card_punch_list);
        card_invoice = (CardView) findViewById(R.id.card_invoice);
        card_wbs = (CardView) findViewById(R.id.card_wbs);
        card_site_diary = (CardView) findViewById(R.id.card_site_diary);
        card_add_resources = (CardView) findViewById(R.id.card_add_resource);
        card_add_vendors = (CardView) findViewById(R.id.card_add_vendors);
        card_completion_list = (CardView) findViewById(R.id.card_completion_list);
        card_change_orders = (CardView) findViewById(R.id.card_change_orders);
        card_expense = (CardView) findViewById(R.id.card_expense);
        card_graph = (CardView) findViewById(R.id.card_graph);
        card_budget_transfer = (CardView) findViewById(R.id.card_budget_transfer);
        card_budget_changes = (CardView) findViewById(R.id.card_budget_changes);
        card_quality_plans = (CardView) findViewById(R.id.card_quality_plans);
        card_quality_standard = (CardView) findViewById(R.id.card_quality_standard);
        card_quality_checklist = (CardView) findViewById(R.id.card_quality_checklist);
        card_boq = (CardView) findViewById(R.id.card_boq);
        card_material_issue = (CardView) findViewById(R.id.card_material_issue);
        card_inventory = (CardView) findViewById(R.id.card_inventory);
        card_requisition = (CardView) findViewById(R.id.card_requisition);
        card_daily_progress = (CardView) findViewById(R.id.card_daily_progress);
        card_add_items = (CardView) findViewById(R.id.card_add_items);
        card_all_pr = (CardView) findViewById(R.id.card_all_pr);

        card_graph.setOnClickListener(this);
        card_purchase_order.setOnClickListener(this);
        card_quality_control.setOnClickListener(this);
        card_mom.setOnClickListener(this);
        card_submittals.setOnClickListener(this);
        card_sub_register.setOnClickListener(this);
        card_subcontractor.setOnClickListener(this);
        card_resource.setOnClickListener(this);
        card_punch_list.setOnClickListener(this);
        card_invoice.setOnClickListener(this);
        card_wbs.setOnClickListener(this);
        card_site_diary.setOnClickListener(this);
        card_add_resources.setOnClickListener(this);
        card_add_vendors.setOnClickListener(this);
        card_completion_list.setOnClickListener(this);
        card_change_orders.setOnClickListener(this);
        card_expense.setOnClickListener(this);
        card_budget_changes.setOnClickListener(this);
        card_budget_transfer.setOnClickListener(this);
        card_quality_plans.setOnClickListener(this);
        card_quality_standard.setOnClickListener(this);
        card_quality_checklist.setOnClickListener(this);
        card_boq.setOnClickListener(this);
        card_material_issue.setOnClickListener(this);
        card_inventory.setOnClickListener(this);
        card_requisition.setOnClickListener(this);
        card_daily_progress.setOnClickListener(this);
        card_add_items.setOnClickListener(this);
        card_all_pr.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewPurchaseOrders.this, NewAllProjects.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_all_pr:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllPurchaseReceipts.class);
                startActivity(intent);
            }
            break;
            case R.id.card_budget_changes:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllBudgetChanges.class);
                startActivity(intent);
            }
            break;
            case R.id.card_budget_transfer:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllBudgetTransfer.class);
                startActivity(intent);
            }
            break;
            case R.id.card_graph:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, GraphActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.card_expense:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllExpenses.class);
                startActivity(intent);
            }
            break;
            case R.id.card_change_orders:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllChangeOrders.class);
                startActivity(intent);
            }
            break;
            case R.id.card_completion_list:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, CompletionListActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.card_site_diary:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllSiteDiary.class);
                startActivity(intent);
            }
            break;
            case R.id.card_add_resource:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllAddResources.class);
                startActivity(intent);
            }
            break;
            case R.id.card_add_vendors:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllVendors.class);
                startActivity(intent);
            }
            break;
            case R.id.card_purchase_orders:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, PurchaseOrders.class);
                startActivity(intent);
            }
            break;
            case R.id.card_quality_control:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllQualityControl.class);
                startActivity(intent);
            }
            break;
            case R.id.card_mom:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllMom.class);
                startActivity(intent);
            }
            break;
            case R.id.card_submittals:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllSubmittals.class);
                startActivity(intent);
            }
            break;
            case R.id.card_sub_register:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllSubmittalsRegister.class);
                startActivity(intent);
            }
            break;
            case R.id.card_subcontractor:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllSubcontractor.class);
                startActivity(intent);
            }
            break;
            case R.id.card_resource:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllResource.class);
                startActivity(intent);
            }
            break;
            case R.id.card_punch_list:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllPunchLists.class);
                startActivity(intent);
            }
            break;
            case R.id.card_invoice:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllInvoices.class);
                startActivity(intent);
            }
            break;
            case R.id.card_wbs:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, WbsActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.card_daily_progress:
            {
//                Intent intent = new Intent(ViewPurchaseOrders.this, DailyProgressDetails.class);
//                startActivity(intent);
                final AlertDialog.Builder alert = new AlertDialog.Builder(ViewPurchaseOrders.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(ViewPurchaseOrders.this).inflate(R.layout.dialog_daily_progress, null);
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
                                ViewPurchaseOrders.this,
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
                        Intent intent = new Intent(ViewPurchaseOrders.this, DailyProgressDetails.class);
                        pm.putString("currentProgressDate", dateToSendDailyProgress);
                        startActivity(intent);
                    }
                });
            }
            break;
            case R.id.card_add_items:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllItemsActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.create_mom:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllMom.class);
                intent.putExtra("create", "YES");
                startActivity(intent);
            }
            break;
            case R.id.create_submittals:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, SubmittalsCreateActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.create_sub_register:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, SubmittalRegisterCreateActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.create_subcontractor:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllSubcontractor.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
            case R.id.create_resource:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllResource.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
            case R.id.create_invoice:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, InvoiceCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.create_purchase:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, PurchaseOrders.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
            case R.id.create_quality:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, QualityControlCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.create_punch_list:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, PunchListCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.create_punch_list_btn:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, PunchListCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.create_wbs:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, WbsActivity.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;

            case R.id.create_wbs_btn:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, WbsActivity.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
            case R.id.create_mom_btn:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, MomCreateActivity.class);
                intent.putExtra("create", "YES");
                startActivity(intent);
            }
            break;
            case R.id.create_submittals_btn:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, SubmittalsCreateActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.create_sub_register_btn:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, SubmittalRegisterCreateActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.create_subcontractor_btn:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllSubcontractor.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
            case R.id.create_resource_btn:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllResource.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
            case R.id.create_invoice_btn:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, InvoiceCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.create_purchase_btn:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, PurchaseOrders.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
            case R.id.create_quality_btn:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, QualityControlCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.card_quality_plans:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllQualityPlans.class);
                startActivity(intent);
            }
            break;
            case R.id.create_quality_plan:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllQualityPlans.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
            case R.id.create_quality_plan_btn:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllQualityPlans.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
            case R.id.card_quality_standard:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllQualityStandards.class);
                startActivity(intent);
            }
            break;
            case R.id.create_quality_standard:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, QualityStandardCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.create_quality_standard_btn:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, QualityStandardCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.card_quality_checklist:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllQualityChecklist.class);
                startActivity(intent);
            }
            break;
            case R.id.create_quality_checklist:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, QualityChecklistCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.create_quality_checklist_btn:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, QualityChecklistCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.card_boq:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllBoq.class);
                startActivity(intent);
            }
            break;
            case R.id.create_boq:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, BOQCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.create_boq_btn:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, BOQCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.card_material_issue:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllMaterialIssue.class);
                startActivity(intent);
            }
            break;
            case R.id.create_material_issue:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllMaterialIssue.class);
                intent.putExtra("create","yes");
                startActivity(intent);
            }
            break;
            case R.id.create_material_issue_btn:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllMaterialIssue.class);
                intent.putExtra("create","yes");
                startActivity(intent);
            }
            break;
            case R.id.card_inventory:
            {
//                Intent intent = new Intent(ViewPurchaseOrders.this, InventoryNew.class);
//                startActivity(intent);
                final AlertDialog.Builder alert = new AlertDialog.Builder(ViewPurchaseOrders.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(ViewPurchaseOrders.this).inflate(R.layout.dialog_view_inventory, null);
                alert.setView(dialogView);

                show = alert.show();

                PreferenceManager pm = new PreferenceManager(dialogView.getContext());
                currentProjectNo = pm.getString("projectId");

                final TextView item_desc;

                spinner_item = (Spinner) dialogView.findViewById(R.id.spinner_item);
                item_desc = (TextView) dialogView.findViewById(R.id.item_desc);
                from_date = (TextView) dialogView.findViewById(R.id.from_date);
                to_date = (TextView) dialogView.findViewById(R.id.to_date);

                Button saveBtn = (Button) dialogView.findViewById(R.id.saveBtn);

                from_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        whichDate = "from";
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance(
                                ViewPurchaseOrders.this,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.setMaxDate(now);
                        dpd.show(getFragmentManager(), "Datepickerdialog");
                    }
                });

                pDialog = new ProgressDialog(ViewPurchaseOrders.this);
                pDialog.setMessage("Preparing Items...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                class MyTask extends AsyncTask<Void, Void, Void>
                {

                    @Override
                    protected Void doInBackground(Void... params)
                    {
                        prepareItemList();
                        return null;
                    }
                }

                new MyTask().execute();

                spinner_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        item_desc.setText(itemDescArray[position]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(spinner_item.getSelectedItem().toString().equals("Select Item"))
                        {
                            Toast.makeText(ViewPurchaseOrders.this, "Select Item First", Toast.LENGTH_SHORT).show();
                        }
                        else if(spinner_item.getSelectedItem().toString().equals("No Items"))
                        {
                            Toast.makeText(ViewPurchaseOrders.this, "NO ITEMS IN THIS PROJECT", Toast.LENGTH_SHORT).show();
                        }
                        else if(item_desc.getText().toString().isEmpty())
                        {
                            item_desc.setError("Field cannot be empty");
                        }
                        else if(from_date.getText().toString().isEmpty())
                        {
                            from_date.setError("Select From Date");
                        }
                        else if(to_date.getText().toString().isEmpty())
                        {
                            to_date.setError("Select To Date");
                        }

                        else
                        {
                            Intent intent = new Intent(ViewPurchaseOrders.this, InventoryView.class);
                            intent.putExtra("fromDate", from_date.getText().toString());
                            intent.putExtra("toDate", to_date.getText().toString());
                            intent.putExtra("currentInventoryItem", spinner_item.getSelectedItem().toString());
                            startActivity(intent);
                        }
                    }
                });

            }
            break;
            case R.id.create_item:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AddItemsActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.create_item_btn:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AddItemsActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.card_requisition:
            {
                Intent intent = new Intent(ViewPurchaseOrders.this, AllPurchaseRequisition.class);
                startActivity(intent);
            }
            break;
//            case R.id.create_requisition_btn:
//            {
//                Intent intent = new Intent(ViewPurchaseOrders.this, PurchaseRequisitionCreate.class);
//                startActivity(intent);
//            }
//            break;
        }
    }


    public void prepareItemList()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getItems?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(ViewPurchaseOrders.this, response.getString("msg"), Toast.LENGTH_SHORT).show();

                            }

                            if(type.equals("WARN"))
                            {
                                Toast.makeText(ViewPurchaseOrders.this, response.getString("msg"), Toast.LENGTH_SHORT).show();

                                pDialog.dismiss();
                                Intent intent = new Intent(ViewPurchaseOrders.this, ViewPurchaseOrders.class);
                                startActivity(intent);
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                itemArray = new String[dataArray.length()+1];
                                itemDescArray = new String[dataArray.length()+1];

                                itemArray[0]="Select Item";
                                itemDescArray[0]="Select Item to view description";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    item = dataObject.getString("itemId");
                                    itemDesc = dataObject.getString("itemDescription");
                                    itemArray[i+1]=item;
                                    itemDescArray[i+1]=itemDesc;
                                }

                                if(dataArray.length()==0)
                                {
                                    Toast.makeText(ViewPurchaseOrders.this, "NO ITEMS IN THIS PROJECT", Toast.LENGTH_SHORT).show();

                                    itemArray = new String[1];
                                    itemArray[0]="No Items";
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewPurchaseOrders.this,
                                            R.layout.spinner_small_text,itemArray);
                                    spinner_item.setAdapter(adapter);
                                }
                                else
                                {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewPurchaseOrders.this,
                                            R.layout.spinner_small_text,itemArray);
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
            pDialog = new ProgressDialog(ViewPurchaseOrders.this);
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
            Glide.with(ViewPurchaseOrders.this)
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

//    private void prepareItems()
//    {
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//
//
//        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        try{
//
//                            String type = response.getString("type");
//
//                            if(type.equals("ERROR"))
//                            {
//                                Toast.makeText(ViewPurchaseOrders.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
//                            }
//
//                            if(type.equals("INFO"))
//                            {
//                                dataArray = response.getJSONArray("data");
//                                for(int i=0; i<dataArray.length();i++)
//                                {
//                                    dataObject = dataArray.getJSONObject(i);
//                                    projectId = dataObject.getString("projectId");
//
//                                    if (projectId.equals(currentProjectNo))
//                                    {
//                                        projectName = dataObject.getString("projectName");
//                                        projectDesc = dataObject.getString("projectDescription");
//                                        createdBy = dataObject.getString("createdBy");
//                                        createdDate = dataObject.getString("createddate");
//
//                                        Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
//                                        createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);
//
//                                        imageUrl = dataObject.getString("photoUrl");
//
//                                        if(!imageUrl.equals(""))
//                                        {
//                                            Glide.with(ViewPurchaseOrders.this)
//                                                    .load(imageUrl)
//                                                    .crossFade()
//                                                    .into(company_logo);
//
//                                        }
//
//                                        else
//                                        {
//                                            mDrawable = getResources().getDrawable(R.drawable.no_logo);
//                                            company_logo.setImageDrawable(mDrawable);
//                                        }
//
//                                        title.setText(projectName);
//                                        description.setText(projectDesc);
//                                        date.setText(createdDate);
//                                        created_by.setText(createdBy);
//                                        project_id.setText(projectId);
//                                    }
//                                }
//                            }
//                            pDialog.dismiss();
//                        }catch(JSONException e){e.printStackTrace();} catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Volley","Error");
//
//                    }
//                }
//        );
//        requestQueue.add(jor);
//    }

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
                            ViewPurchaseOrders.this,
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
}