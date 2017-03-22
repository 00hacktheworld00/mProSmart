package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import com.android.volley.Cache;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class InventoryMainActivity extends NewActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    RelativeLayout create_material_issue;
    ImageButton create_material_issue_btn;
    CardView card_inventory, card_material_issue, card_all_pr;

    Toolbar toolbar;

    HelveticaRegular description, date, created_by, project_id, text_currency, text_budget;
    HelveticaBold title;
    String currentProjectNo, currentUserId, currentCompanyId;
    PreferenceManager pm;
    Drawable mDrawable;
    String budget, currency;

    View dialogView;
    AlertDialog show;
    Spinner spinner_item;
    TextView from_date, to_date;
    String whichDate;
    String[] itemArray, itemDescArray;
    String item, itemDesc;


    JSONArray dataArray;
    JSONObject dataObject;
    String projectId, projectName, projectDesc, createdBy, createdDate, imageUrl;
    NewAllProjectList items;
    ConnectionDetector cd;
    public static final String TAG = QualityControlMain.class.getSimpleName();
    Boolean isInternetPresent = false;
    ProgressDialog pDialog;
    String url, item_url;
    CircleImageView company_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Inventory Management");

        pm = new PreferenceManager(getApplicationContext());
        currentUserId = pm.getString("userId");
        currentCompanyId = pm.getString("companyId");

        currentUserId = pm.getString("userId");

        currentProjectNo = pm.getString("projectId");

        url = pm.getString("SERVER_URL") + "/getProjects?companyId=\""+currentCompanyId+"\"";
        item_url = pm.getString("SERVER_URL") + "/getItems?projectId='"+currentProjectNo+"'";

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
            Crouton.makeText(InventoryMainActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();
        }

//        pDialog = new ProgressDialog(QualityControlMain.this);
//        pDialog.setMessage("Getting Data ...");
//        pDialog.setIndeterminate(false);
//        pDialog.setCancelable(true);
//        pDialog.show();

        prepareHeader();

        create_material_issue = (RelativeLayout) findViewById(R.id.create_material_issue);

        create_material_issue.setOnClickListener(this);

        create_material_issue_btn = (ImageButton) findViewById(R.id.create_material_issue_btn);

        create_material_issue_btn.setOnClickListener(this);

        card_material_issue = (CardView) findViewById(R.id.card_material_issue);
        card_inventory = (CardView) findViewById(R.id.card_inventory);
        card_all_pr = (CardView) findViewById(R.id.card_all_pr);

        card_material_issue.setOnClickListener(this);
        card_inventory.setOnClickListener(this);
        card_all_pr.setOnClickListener(this);


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
            Glide.with(InventoryMainActivity.this)
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
            case R.id.card_all_pr:
            {
                Intent intent = new Intent(InventoryMainActivity.this, AllPurchaseReceipts.class);
                startActivity(intent);
            }
            break;
            case R.id.card_material_issue:
            {
                Intent intent = new Intent(InventoryMainActivity.this, AllMaterialIssue.class);
                startActivity(intent);
            }
            break;
            case R.id.create_material_issue:
            {
                Intent intent = new Intent(InventoryMainActivity.this, AllMaterialIssue.class);
                intent.putExtra("create","yes");
                startActivity(intent);
            }
            break;
            case R.id.create_material_issue_btn:
            {
                Intent intent = new Intent(InventoryMainActivity.this, AllMaterialIssue.class);
                intent.putExtra("create","yes");
                startActivity(intent);
            }
            break;

            case R.id.card_inventory:
            {
//                Intent intent = new Intent(InventoryMainActivity.this, InventoryNew.class);
//                startActivity(intent);
                final AlertDialog.Builder alert = new AlertDialog.Builder(InventoryMainActivity.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(InventoryMainActivity.this).inflate(R.layout.dialog_view_inventory, null);
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
                                InventoryMainActivity.this,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.setMaxDate(now);
                        dpd.show(getFragmentManager(), "Datepickerdialog");
                    }
                });
                prepareOfflineData();

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
                            Toast.makeText(InventoryMainActivity.this, "Select Item First", Toast.LENGTH_SHORT).show();
                        }
                        else if(spinner_item.getSelectedItem().toString().equals("No Items"))
                        {
                            Toast.makeText(InventoryMainActivity.this, "NO ITEMS IN THIS PROJECT", Toast.LENGTH_SHORT).show();
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
                            Intent intent = new Intent(InventoryMainActivity.this, InventoryView.class);
                            intent.putExtra("fromDate", from_date.getText().toString());
                            intent.putExtra("toDate", to_date.getText().toString());
                            intent.putExtra("currentInventoryItem", spinner_item.getSelectedItem().toString());
                            startActivity(intent);
                        }
                    }
                });

            }
            break;
        }
    }

    public void prepareOfflineData()
    {
        if (!isInternetPresent)
        {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(InventoryMainActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(InventoryMainActivity.this);
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
                            Toast.makeText(InventoryMainActivity.this, "NO ITEMS IN THIS PROJECT", Toast.LENGTH_SHORT).show();

                            itemArray = new String[1];
                            itemArray[0]="No Items";
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(InventoryMainActivity.this,
                                    R.layout.spinner_small_text,itemArray);
                            spinner_item.setAdapter(adapter);
                        }
                        else
                        {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(InventoryMainActivity.this,
                                    R.layout.spinner_small_text,itemArray);
                            spinner_item.setAdapter(adapter);
                        }
                        if (pDialog != null)
                            pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (pDialog != null)
                            pDialog.dismiss();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                    if (pDialog != null)
                        pDialog.dismiss();
                }
            }

            else
            {
                Toast.makeText(InventoryMainActivity.this, "Offline Data Not available for Item List", Toast.LENGTH_SHORT).show();
            }
        }

        else
        {
            // Cache data not exist.
            prepareOnlineData();
        }
    }

    public void prepareOnlineData()
    {
        pDialog = new ProgressDialog(InventoryMainActivity.this);
        pDialog.setMessage("Getting cache data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, item_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(InventoryMainActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                if (pDialog != null)
                                    pDialog.dismiss();
                            }

                            if(type.equals("WARN"))
                            {
                                Toast.makeText(InventoryMainActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                if (pDialog != null)
                                    pDialog.dismiss();
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
                                    Toast.makeText(InventoryMainActivity.this, "NO ITEMS IN THIS PROJECT", Toast.LENGTH_SHORT).show();

                                    itemArray = new String[1];
                                    itemArray[0]="No Items";
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(InventoryMainActivity.this,
                                            R.layout.spinner_small_text,itemArray);
                                    spinner_item.setAdapter(adapter);
                                }
                                else
                                {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(InventoryMainActivity.this,
                                            R.layout.spinner_small_text,itemArray);
                                    spinner_item.setAdapter(adapter);
                                }
                                pDialog.dismiss();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
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


    @Override
    public void onDateSet(DatePickerDialog view, final int year, final int monthOfYear, final int dayOfMonth) {
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
                            InventoryMainActivity.this,
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
        Intent intent = new Intent(InventoryMainActivity.this, MainCategories.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
