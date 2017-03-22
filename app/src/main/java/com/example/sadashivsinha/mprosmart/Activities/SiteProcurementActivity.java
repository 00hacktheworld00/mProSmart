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

public class SiteProcurementActivity extends NewActivity implements View.OnClickListener {

    RelativeLayout create_purchase, create_invoice;
    ImageButton create_purchase_btn, create_invoice_btn;
    CardView card_add_vendors, card_purchase_order, card_invoice, card_requisition;

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
        setContentView(R.layout.activity_site_procurement);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Site Procurement");

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
            Crouton.makeText(SiteProcurementActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();
        }

//        pDialog = new ProgressDialog(QualityControlMain.this);
//        pDialog.setMessage("Getting Data ...");
//        pDialog.setIndeterminate(false);
//        pDialog.setCancelable(true);
//        pDialog.show();

        currentProjectNo = pm.getString("projectId");

        prepareHeader();

        create_purchase = (RelativeLayout) findViewById(R.id.create_purchase);
        create_invoice = (RelativeLayout) findViewById(R.id.create_purchase);

        create_invoice.setOnClickListener(this);
        create_purchase.setOnClickListener(this);

        create_invoice_btn = (ImageButton) findViewById(R.id.create_invoice_btn);
        create_purchase_btn = (ImageButton) findViewById(R.id.create_purchase_btn);

        create_invoice_btn.setOnClickListener(this);
        create_purchase_btn.setOnClickListener(this);

        card_purchase_order = (CardView) findViewById(R.id.card_purchase_orders);
        card_invoice = (CardView) findViewById(R.id.card_invoice);
        card_add_vendors = (CardView) findViewById(R.id.card_add_vendors);
        card_requisition = (CardView) findViewById(R.id.card_requisition);

        card_purchase_order.setOnClickListener(this);
        card_invoice.setOnClickListener(this);
        card_add_vendors.setOnClickListener(this);
        card_requisition.setOnClickListener(this);

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
            Glide.with(SiteProcurementActivity.this)
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
            case R.id.card_add_vendors:
            {
                Intent intent = new Intent(SiteProcurementActivity.this, AllVendors.class);
                startActivity(intent);
            }
            break;
            case R.id.card_purchase_orders:
            {
                Intent intent = new Intent(SiteProcurementActivity.this, PurchaseOrders.class);
                startActivity(intent);
            }
            break;
            case R.id.card_invoice:
            {
                Intent intent = new Intent(SiteProcurementActivity.this, AllInvoices.class);
                startActivity(intent);
            }
            break;
            case R.id.create_invoice:
            {
                Intent intent = new Intent(SiteProcurementActivity.this, InvoiceCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.create_purchase:
            {
                Intent intent = new Intent(SiteProcurementActivity.this, PurchaseOrders.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
            case R.id.create_invoice_btn:
            {
                Intent intent = new Intent(SiteProcurementActivity.this, InvoiceCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.create_purchase_btn:
            {
                Intent intent = new Intent(SiteProcurementActivity.this, PurchaseOrders.class);
                intent.putExtra("create", "yes");
                startActivity(intent);
            }
            break;
            case R.id.card_requisition:
            {
                Intent intent = new Intent(SiteProcurementActivity.this, AllPurchaseRequisition.class);
                startActivity(intent);
            }
            break;
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SiteProcurementActivity.this, MainCategories.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
