package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.PurchaseOrderLineItemsAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseOrderLineItemList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.github.clans.fab.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PurchaseOrderLineItems extends NewActivity implements DatePickerDialog.OnDateSetListener {

    String vendorId, date;
    int sumOfTotal=0;
    private List<PurchaseOrderLineItemList> PurchaseOrderLineItemList = new ArrayList<>();
    RecyclerView recyclerView;
    PurchaseOrderLineItemList items;
    PurchaseOrderLineItemsAdapter purchaseOrderLineItemsAdapter;
    JSONArray dataArray;
    JSONObject dataObject;
    TextView po_number, project_no, vendor_code, item_date, total_quantity;
    ConnectionDetector cd;
    public static final String TAG = PurchaseOrderLineItems.class.getSimpleName();
    Boolean isInternetPresent = false;
    private ProgressDialog pDialog, pDialog1, pDialog2, pDialog3;
    String poId, vendorCode, createdOn, createdBy;
    EditText text_item_name;
    String currentPurchaseOrderNo, currentProjectNo, currentUser;
    String itemId, itemName, itemDescription ,createdDate, uomId, quantity, unitCost, totalAmount, needByDate, poTotal;
    String itemNames, itemDesc, itemUom, itemsId, item_url;
    PreferenceManager pm;
    Button saveBtn;
    View dialogView;
    AlertDialog show;
    String[] itemsNameArray, itemsDescArray, itemsUomArray, itemIdArray;
    FloatingActionButton fab_add, exportBtn, fab_search;
    Spinner spinnerItems, spinnerUom;
    LinearLayout hiddenLayout;
    Boolean oldItem = false;
    Spinner spinner_currency;
    Spinner spinner_item;
    String[] uomArray, uomNameArray;
    String url, purchaseOrderId, poQuantity, uom_url, currentSelectedVendor;

    EditText text_item_desc, text_quantity, text_unit_cost, text_uom;
    TextView text_need_by_date, text_total_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_order_line_items);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        pm = new PreferenceManager(getApplicationContext());
        currentPurchaseOrderNo=pm.getString("poNumber");
        currentProjectNo = pm.getString("projectId");
        currentUser = pm.getString("currentUser");
        sumOfTotal = Integer.parseInt(pm.getString("totalAmount"));

        url = getResources().getString(R.string.server_url) + "/getPurchaseLineItems?purchaseOrderId=\""+currentPurchaseOrderNo+"\"";

        item_url = getResources().getString(R.string.server_url) + "/getItems?projectId='"+currentProjectNo+"'";

        uom_url = getResources().getString(R.string.server_url) + "/getUom";

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);

        po_number = (TextView) findViewById(R.id.po_number);
        project_no = (TextView) findViewById(R.id.project_no);
        vendor_code = (TextView) findViewById(R.id.vendor_code);
        item_date = (TextView) findViewById(R.id.item_date);
        total_quantity = (TextView) findViewById(R.id.total_quantity);


        if(getIntent().hasExtra("createItem"))
        {
            if(getIntent().getStringExtra("createItem").equals("yes"))
            {
                final AlertDialog.Builder alert = new AlertDialog.Builder(PurchaseOrderLineItems.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(PurchaseOrderLineItems.this).inflate(R.layout.dialog_add_item, null);
                alert.setView(dialogView);

                show = alert.show();

                spinner_item = (Spinner) dialogView.findViewById(R.id.spinner_item);

                text_uom = (EditText) dialogView.findViewById(R.id.text_uom);

                spinner_currency = (Spinner) dialogView.findViewById(R.id.spinner_currency);


                Button addBtn = (Button) dialogView.findViewById(R.id.addBtn);

                text_item_name = (EditText) dialogView.findViewById(R.id.text_item_name);
                text_item_desc = (EditText) dialogView.findViewById(R.id.text_item_desc);
                text_quantity = (EditText) dialogView.findViewById(R.id.text_quantity);
                text_unit_cost = (EditText) dialogView.findViewById(R.id.text_unit_cost);
                text_need_by_date = (TextView) dialogView.findViewById(R.id.text_need_by_date);
                text_total_amount = (TextView) dialogView.findViewById(R.id.text_total_amount);

                text_need_by_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance(
                                PurchaseOrderLineItems.this,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.show(getFragmentManager(), "Datepickerdialog");
                    }
                });

                ArrayAdapter<String> adapterCurrency = new ArrayAdapter<String>(dialogView.getContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        new String[] {"$", "INR"});

                spinner_currency.setAdapter(adapterCurrency);

                if (!isInternetPresent)
                {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
                    Crouton.cancelAllCroutons();
                    Crouton.makeText(PurchaseOrderLineItems.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

                    pDialog = new ProgressDialog(PurchaseOrderLineItems.this);
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
                                itemIdArray = new String[dataArray.length()+1];
                                itemsNameArray = new String[dataArray.length()+1];
                                itemsDescArray = new String[dataArray.length()+1];
                                itemsUomArray = new String[dataArray.length()+1];

                                itemIdArray[0] = "Select Item";
                                itemsNameArray[0] = "Select Item";
                                itemsDescArray[0] = "Select Item";
                                itemsUomArray[0] = "Select Item";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    itemId = dataObject.getString("itemId");
                                    itemNames = dataObject.getString("itemName");
                                    itemDesc = dataObject.getString("itemDescription");
                                    itemUom = dataObject.getString("uomId");

                                    itemIdArray[i+1]=itemId;
                                    itemsNameArray[i+1]=itemNames;
                                    itemsDescArray[i+1]=itemDesc;
                                    itemsUomArray[i+1]=itemUom;
                                }


                                ArrayAdapter<String> adapterItemList = new ArrayAdapter<String>(dialogView.getContext(),
                                        android.R.layout.simple_dropdown_item_1line,itemIdArray);

                                spinner_item.setAdapter(adapterItemList);

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
                        Toast.makeText(PurchaseOrderLineItems.this, "Offline Data Not available for Item List", Toast.LENGTH_SHORT).show();
                    }
                }

                else
                {
                    // Cache data not exist.
                    callVendorRequest();
                }

                TextWatcher watch = new TextWatcher(){

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                  int arg3) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int a, int b, int c) {
                        // TODO Auto-generated method stub
                        int quantityVal;
                        int unitCostVal;

                        if(!text_unit_cost.getText().toString().equals("") && !text_quantity.getText().toString().equals(""))
                        {
                            quantityVal = Integer.parseInt(text_quantity.getText().toString());
                            unitCostVal = Integer.parseInt(text_unit_cost.getText().toString());

                            text_total_amount.setText(String.valueOf(quantityVal*unitCostVal));
                        }
                    }};


                text_quantity.addTextChangedListener(watch);
                text_unit_cost.addTextChangedListener(watch);


                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if(spinner_item.getSelectedItem().equals("Select Item"))
                        {
                            Toast.makeText(PurchaseOrderLineItems.this, "Select Item First", Toast.LENGTH_SHORT).show();
                        }
                        else if(spinner_item.getSelectedItem().equals("No Data"))
                        {
                            Toast.makeText(PurchaseOrderLineItems.this, "No Items in Master List", Toast.LENGTH_SHORT).show();
                        }
                        else if(text_need_by_date.getText().toString().isEmpty())
                        {
                            text_need_by_date.setError("Set Need By Date");
                        }
                        else if(text_quantity.getText().toString().isEmpty())
                        {
                            text_quantity.setError("Field cannot be empty");
                        }
                        else if(text_unit_cost.getText().toString().isEmpty())
                        {
                            text_unit_cost.setError("Field cannot be empty");
                        }
                        else
                        {
                            pDialog2 = new ProgressDialog(dialogView.getContext());
                            pDialog2.setMessage("Saving Data ...");
                            pDialog2.setIndeterminate(false);
                            pDialog2.setCancelable(true);
                            pDialog2.show();

                            final String totalAmountOfItem = text_total_amount.getText().toString();

                            saveItem(totalAmountOfItem);
                        }
                    }
                });
            }
        }

        fab_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alert = new AlertDialog.Builder(PurchaseOrderLineItems.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(PurchaseOrderLineItems.this).inflate(R.layout.dialog_add_item, null);
                alert.setView(dialogView);

                show = alert.show();

                spinner_item = (Spinner) dialogView.findViewById(R.id.spinner_item);

                spinner_currency = (Spinner) dialogView.findViewById(R.id.spinner_currency);


                Button addBtn = (Button) dialogView.findViewById(R.id.addBtn);

                text_item_name = (EditText) dialogView.findViewById(R.id.text_item_name);
                text_item_desc = (EditText) dialogView.findViewById(R.id.text_item_desc);
                text_quantity = (EditText) dialogView.findViewById(R.id.text_quantity);
                text_unit_cost = (EditText) dialogView.findViewById(R.id.text_unit_cost);
                text_need_by_date = (TextView) dialogView.findViewById(R.id.text_need_by_date);
                text_total_amount = (TextView) dialogView.findViewById(R.id.text_total_amount);

                text_uom = (EditText) dialogView.findViewById(R.id.text_uom);

                text_need_by_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance(
                                PurchaseOrderLineItems.this,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.setMinDate(now);
                        dpd.show(getFragmentManager(), "Datepickerdialog");
                    }
                });


                ArrayAdapter<String> adapterCurrency = new ArrayAdapter<String>(dialogView.getContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        new String[] {"$", "INR"});

                spinner_currency.setAdapter(adapterCurrency);

                if (!isInternetPresent)
                {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
                    Crouton.cancelAllCroutons();
                    Crouton.makeText(PurchaseOrderLineItems.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

                    pDialog = new ProgressDialog(PurchaseOrderLineItems.this);
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
                            try
                            {
                                dataArray = jsonObject.getJSONArray("data");
                                itemIdArray = new String[dataArray.length()+1];
                                itemsNameArray = new String[dataArray.length()+1];
                                itemsDescArray = new String[dataArray.length()+1];
                                itemsUomArray = new String[dataArray.length()+1];

                                itemIdArray[0] = "Select Item";
                                itemsNameArray[0] = "Select Item";
                                itemsDescArray[0] = "Select Item";
                                itemsUomArray[0] = "Select Item";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    itemId = dataObject.getString("itemId");
                                    itemNames = dataObject.getString("itemName");
                                    itemDesc = dataObject.getString("itemDescription");
                                    itemUom = dataObject.getString("uomId");

                                    itemIdArray[i+1]=itemId;
                                    itemsNameArray[i+1]=itemNames;
                                    itemsDescArray[i+1]=itemDesc;
                                    itemsUomArray[i+1]=itemUom;
                                }


                                ArrayAdapter<String> adapterItemList = new ArrayAdapter<String>(dialogView.getContext(),
                                        android.R.layout.simple_dropdown_item_1line,itemIdArray);

                                spinner_item.setAdapter(adapterItemList);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    else
                    {
                        Toast.makeText(PurchaseOrderLineItems.this, "Offline Data Not available for Item List", Toast.LENGTH_SHORT).show();
                    }





                    // for get All UOM

                    entry = cache.get(uom_url);
                    if (entry != null) {
                        //Cache data available.
                        try {
                            String data = new String(entry.data, "UTF-8");
                            Log.d("CACHE DATA", data);
                            JSONObject jsonObject = new JSONObject(data);
                            try
                            {
                                dataArray = jsonObject.getJSONArray("data");
                                uomArray = new String[dataArray.length()+1];
                                uomNameArray = new String[dataArray.length()+1];

                                uomArray[0]="UOM";
                                uomNameArray[0]="UOM";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    uomArray[i+1] = dataObject.getString("uomCode");
                                    uomNameArray[i+1] = dataObject.getString("uomName");
                                }

                                pDialog.dismiss();

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
                        Toast.makeText(PurchaseOrderLineItems.this, "Offline Data Not available for UOMs", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }

                else
                {
                    // Cache data not exist.
                    callUomRequest();
                    callVendorRequest();
                }

                spinner_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        text_item_name.setText(itemsNameArray[position]);
                        text_item_desc.setText(itemsDescArray[position]);

                        if(uomArray!=null)
                        {

                            for(int j=0; j<uomArray.length; j++)
                            {
                                if(itemsUomArray[position].equals(uomArray[j]))
                                {
                                    text_uom.setText(uomNameArray[j]);
                                    currentSelectedVendor = uomArray[j];
                                }
                            }
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                TextWatcher watch = new TextWatcher(){

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                  int arg3) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int a, int b, int c) {
                        // TODO Auto-generated method stub
                        int quantityVal;
                        int unitCostVal;

                        if(!text_unit_cost.getText().toString().equals("") && !text_quantity.getText().toString().equals(""))
                        {
                            quantityVal = Integer.parseInt(text_quantity.getText().toString());
                            unitCostVal = Integer.parseInt(text_unit_cost.getText().toString());

                            text_total_amount.setText(String.valueOf(quantityVal*unitCostVal));
                        }
                    }};


                text_quantity.addTextChangedListener(watch);
                text_unit_cost.addTextChangedListener(watch);
                text_uom.setEnabled(false);


                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(spinner_item.getSelectedItem().equals("Select Item"))
                        {
                            Toast.makeText(PurchaseOrderLineItems.this, "Select Item First", Toast.LENGTH_SHORT).show();
                        }
                        else if(spinner_item.getSelectedItem().equals("No Data"))
                        {
                            Toast.makeText(PurchaseOrderLineItems.this, "No Items in Master List", Toast.LENGTH_SHORT).show();
                        }
                        else if(text_need_by_date.getText().toString().isEmpty())
                        {
                            text_need_by_date.setError("Set Need By Date");
                        }
                        else if(text_quantity.getText().toString().isEmpty())
                        {
                            text_quantity.setError("Field cannot be empty");
                        }
                        else if(text_unit_cost.getText().toString().isEmpty())
                        {
                            text_unit_cost.setError("Field cannot be empty");
                        }
                        else
                        {
                            pDialog2 = new ProgressDialog(dialogView.getContext());
                            pDialog2.setMessage("Saving Data ...");
                            pDialog2.setIndeterminate(false);
                            pDialog2.setCancelable(true);
                            pDialog2.show();

                            final String totalAmountOfItem = text_total_amount.getText().toString();

                            saveItem(totalAmountOfItem);
                        }
                    }
                });
            }
        });

        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    if (ContextCompat.checkSelfPermission(PurchaseOrderLineItems.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
                    {

                        ActivityCompat.requestPermissions(PurchaseOrderLineItems.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }

                    Environment.getExternalStorageState();

                    String itemId=null, itemName=null, itemDesc=null, itemUom=null, itemQuantity=null;
                    int listSize = PurchaseOrderLineItemList.size();
                    String cvsValues = "PO No." + ","+"Item ID" + ","+ "Item Name" + ","+ "Item Description" + ","+ "UOM" + ","+ "Quantity" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        PurchaseOrderLineItemList items = PurchaseOrderLineItemList.get(i);
                        itemId = items.getItemId();
                        itemName = items.getItemName();
                        itemDesc = items.getItemDesc();
                        itemUom = items.getItemUom();
                        itemQuantity = items.getItemQuantity();

                        cvsValues = cvsValues + currentPurchaseOrderNo + ","+  itemId + ","+  itemName + ","+ itemDesc + ","+ itemUom + ","+ itemQuantity + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "PO-No-"+currentPurchaseOrderNo+".csv", cvsValues);
                }

                else
                {
                    Environment.getExternalStorageState();

                    String itemId=null, itemName=null, itemDesc=null, itemUom=null, itemQuantity=null;
                    int listSize = PurchaseOrderLineItemList.size();
                    String cvsValues = "PO No." + ","+"Item ID" + ","+ "Item Name" + ","+ "Item Description" + ","+ "UOM" + ","+ "Quantity" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        PurchaseOrderLineItemList items = PurchaseOrderLineItemList.get(i);
                        itemId = items.getItemId();
                        itemName = items.getItemName();
                        itemDesc = items.getItemDesc();
                        itemUom = items.getItemUom();
                        itemQuantity = items.getItemQuantity();

                        cvsValues = cvsValues + currentPurchaseOrderNo + ","+  itemId + ","+  itemName + ","+ itemDesc + ","+ itemUom + ","+ itemQuantity + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "PO-No-"+currentPurchaseOrderNo+".csv", cvsValues);

                }
            }
        });

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        purchaseOrderLineItemsAdapter = new PurchaseOrderLineItemsAdapter(PurchaseOrderLineItemList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(purchaseOrderLineItemsAdapter);

        prepareHeader();

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(PurchaseOrderLineItems.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(PurchaseOrderLineItems.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();



            // for get All UOM

            Cache.Entry entry = cache.get(uom_url);
            entry = cache.get(uom_url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try
                    {
                        dataArray = jsonObject.getJSONArray("data");
                        uomArray = new String[dataArray.length()+1];
                        uomNameArray = new String[dataArray.length()+1];

                        uomArray[0]="UOM";
                        uomNameArray[0]="UOM";

                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            uomArray[i+1] = dataObject.getString("uomCode");
                            uomNameArray[i+1] = dataObject.getString("uomName");
                        }

                        pDialog.dismiss();

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
                Toast.makeText(PurchaseOrderLineItems.this, "Offline Data Not available for UOMs", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }







            entry = cache.get(url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);

                            itemId = dataObject.getString("purchaseLineItemsId");
                            itemName = dataObject.getString("itemName");
                            itemDescription = dataObject.getString("itemDescription");
                            quantity = dataObject.getString("poQuantity");
                            uomId = dataObject.getString("uomId");
                            totalAmount = dataObject.getString("totalAmount");
                            needByDate = dataObject.getString("needByDate");
                            unitCost = dataObject.getString("unitCost");

                            for(int j=0; j<uomArray.length; j++)
                            {
                                if(uomId.equals(uomArray[j]))
                                {
                                    uomId = uomNameArray[j];
                                }
                            }


                            items = new PurchaseOrderLineItemList(itemId,itemName,itemDescription,uomId, quantity, totalAmount, needByDate, unitCost);
                            PurchaseOrderLineItemList.add(items);

                            purchaseOrderLineItemsAdapter.notifyDataSetChanged();

                            Boolean createPoPendingLineItemForNewPo = pm.getBoolean("createPoPendingLineItemForNewPo");
                            Log.d("create Po Pending :", createPoPendingLineItemForNewPo.toString());

                            if(createPoPendingLineItemForNewPo)
                            {
                                //if is in offline mode and data creation is pending, show the data in the list

                                String jsonObjectVal = pm.getString("objectPOLineItemForNewPo");
                                Log.d("JSON PO LINE PENDING :", jsonObjectVal);

                                JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                                Log.d("JSONObj PO LINE :", jsonObjectPending.toString());

                                purchaseOrderId = jsonObjectPending.getString("purchaseOrderId");

                                if(purchaseOrderId.equals(currentPurchaseOrderNo))
                                {
                                    itemName = jsonObjectPending.getString("itemName");
                                    itemDescription = jsonObjectPending.getString("itemDescription");
                                    uomId = jsonObjectPending.getString("uomId");
                                    poQuantity = jsonObjectPending.getString("poQuantity");
                                    totalAmount = jsonObjectPending.getString("totalAmount");
                                    needByDate = jsonObjectPending.getString("needByDate");
                                    unitCost = jsonObjectPending.getString("unitCost");

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(needByDate);
                                    needByDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    Log.d("JSONObj itemName :", itemName);
                                    Log.d("JSONObj itemDesc :", itemDescription);
                                    Log.d("JSONObj uomId :", uomId);
                                    Log.d("JSONObj poQuantity :", poQuantity);
                                    Log.d("JSONObj totalAmount :", totalAmount);
                                    Log.d("JSONObj needByDate :", needByDate);
                                    Log.d("JSONObj unitCost :", unitCost);

                                    items = new PurchaseOrderLineItemList(getResources().getString(R.string.waiting_to_connect)
                                            ,itemName,itemDescription,uomId, poQuantity, totalAmount, needByDate, unitCost);
                                    PurchaseOrderLineItemList.add(items);

                                    purchaseOrderLineItemsAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        //check if is offline mode and data creation is pending
                        Boolean createPoPendingLineItem = pm.getBoolean("createPoPendingLineItem");

                        if(createPoPendingLineItem)
                        {
                            String jsonObjectVal = pm.getString("objectPOLineItem");
                            Log.d("JSON POLine PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj POLine :", jsonObjectPending.toString());

                            itemName = jsonObjectPending.getString("itemName");
                            itemDescription = jsonObjectPending.getString("itemDescription");
                            quantity = jsonObjectPending.getString("poQuantity");
                            uomId = jsonObjectPending.getString("uomId");
                            totalAmount = jsonObjectPending.getString("totalAmount");
                            needByDate = jsonObjectPending.getString("needByDate");
                            unitCost = jsonObjectPending.getString("unitCost");

                            Log.d("JSONObj itemName :", itemName);
                            Log.d("JSONObj itemDesc :", itemDescription);
                            Log.d("JSONObj quantity :", quantity);
                            Log.d("JSONObj uomId :", uomId);
                            Log.d("JSONObj totalAmount :", totalAmount);
                            Log.d("JSONObj needByDate :", needByDate);
                            Log.d("JSONObj unitCost :", unitCost);

                            items = new PurchaseOrderLineItemList(getResources().getString(R.string.waiting_to_connect) ,itemName,itemDescription,uomId, quantity, totalAmount, needByDate, unitCost);
                            PurchaseOrderLineItemList.add(items);

                            purchaseOrderLineItemsAdapter.notifyDataSetChanged();

                        }


                    } catch (JSONException | ParseException e) {
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
                Toast.makeText(PurchaseOrderLineItems.this, "Offline Data Not available for this Purchase Order Line Items", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callUomRequest();
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mAdapter = new MyAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }

        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State
    }

    private void callUomRequest()
    {
        pDialog = new ProgressDialog(PurchaseOrderLineItems.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uom_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(PurchaseOrderLineItems.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                uomArray = new String[dataArray.length()+1];
                                uomNameArray = new String[dataArray.length()+1];

                                uomArray[0] = "Select Item";
                                uomNameArray[0] = "Select Item";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    uomArray[i+1]=dataObject.getString("uomCode");
                                    uomNameArray[i+1]=dataObject.getString("uomName");
                                }

                                callJsonArrayRequest();

                            }
                        } catch (JSONException e) {
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
        if(pDialog!=null)
            pDialog.dismiss();
    }

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(PurchaseOrderLineItems.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            dataArray = response.getJSONArray("data");
                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                itemId = dataObject.getString("purchaseLineItemsId");
                                itemName = dataObject.getString("itemName");
                                itemDescription = dataObject.getString("itemDescription");
                                quantity = dataObject.getString("poQuantity");
                                uomId = dataObject.getString("uomId");
                                totalAmount = dataObject.getString("totalAmount");
                                needByDate = dataObject.getString("needByDate");
                                unitCost = dataObject.getString("unitCost");

                                for(int j=0; j<uomArray.length; j++)
                                {
                                    if(uomId.equals(uomArray[j]))
                                    {
                                        uomId = uomNameArray[j];
                                    }
                                }

                                items = new PurchaseOrderLineItemList(itemId,itemName,itemDescription,uomId, quantity, totalAmount, needByDate, unitCost);
                                PurchaseOrderLineItemList.add(items);

                                purchaseOrderLineItemsAdapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
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
        if(pDialog!=null)
            pDialog.dismiss();
    }

    public void prepareHeader()
    {
        vendorCode = pm.getString("vendorCode");
        createdOn = pm.getString("createdOn");
        poTotal =  pm.getString("totalAmount");
        createdBy = pm.getString("createdBy");
        poId = pm.getString("poNumber");

        po_number.setText(poId);
        project_no.setText(currentProjectNo);
        vendor_code.setText(vendorCode);
        item_date.setText(createdOn);
        total_quantity.setText(poTotal);
    }
    private void callVendorRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(PurchaseOrderLineItems.this);
        pDialog.setMessage("Getting server data");
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
                                Toast.makeText(PurchaseOrderLineItems.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                itemIdArray = new String[dataArray.length()+1];
                                itemsNameArray = new String[dataArray.length()+1];
                                itemsDescArray = new String[dataArray.length()+1];
                                itemsUomArray = new String[dataArray.length()+1];

                                itemIdArray[0] = "Select Item";
                                itemsNameArray[0] = "Select Item";
                                itemsDescArray[0] = "Select Item";
                                itemsUomArray[0] = "Select Item";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    itemId = dataObject.getString("itemId");
                                    itemNames = dataObject.getString("itemName");
                                    itemDesc = dataObject.getString("itemDescription");
                                    itemUom = dataObject.getString("uomId");

                                    itemIdArray[i+1]=itemId;
                                    itemsNameArray[i+1]=itemNames;
                                    itemsDescArray[i+1]=itemDesc;
                                    itemsUomArray[i+1]=itemUom;
                                }


                                ArrayAdapter<String> adapterItemList = new ArrayAdapter<String>(dialogView.getContext(),
                                        android.R.layout.simple_dropdown_item_1line,itemIdArray);

                                spinner_item.setAdapter(adapterItemList);

                            }
                        } catch (JSONException e) {
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
        if(pDialog!=null)
            pDialog.dismiss();
    }

    public void saveItem(final String totalAmountOfItem)
    {
        JSONObject object = new JSONObject();

        try {

            Date tradeDate = null;
            try
            {
                tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(text_need_by_date.getText().toString());
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            object.put("itemName", spinner_item.getSelectedItem().toString());
            object.put("itemDescription", text_item_desc.getText().toString());
            object.put("uomId", currentSelectedVendor);
            object.put("poQuantity", text_quantity.getText().toString());
            object.put("purchaseOrderId", currentPurchaseOrderNo);
            object.put("totalAmount", totalAmountOfItem);
            object.put("needByDate", new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tradeDate));
            object.put("unitCost", text_unit_cost.getText().toString());


            Log.d("tag", String.valueOf(object));

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(PurchaseOrderLineItems.this);

        String url = PurchaseOrderLineItems.this.getResources().getString(R.string.server_url) + "/postPurchaseLineItems";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            Toast.makeText(PurchaseOrderLineItems.this, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();

                            if(response.getString("msg").equals("success"))
                            {
                                String successMsg = "Purchase Order Line Item Created. ID - "+ response.getString("data");
                                Toast.makeText(PurchaseOrderLineItems.this, successMsg, Toast.LENGTH_SHORT).show();
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
                        Log.e("Volley", "Error");
                        Toast.makeText(PurchaseOrderLineItems.this, error.toString(), Toast.LENGTH_SHORT).show();

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

            //check if po is not created , ie IS IN OFFLINE MODE with new Uncreated PO
            if(poId.equals(getResources().getString(R.string.waiting_to_connect)))
            {
                Boolean createPoPendingLineItemForNewPo = pm.getBoolean("createPoPendingLineItemForNewPo");

                if(createPoPendingLineItemForNewPo)
                {
                    Toast.makeText(PurchaseOrderLineItems.this, "Already a PO Line Item creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(PurchaseOrderLineItems.this, "Internet not currently available. PO Line Item will automatically get created on internet connection after PO is created.", Toast.LENGTH_SHORT).show();

                    pm.putString("objectPOLineItemForNewPo", object.toString());
                    pm.putString("urlPOLineItemForNewPo", url);
                    pm.putString("toastMessagePOLineItemForNewPo", "Purchase Order Line Item Created");
                    pm.putBoolean("createPoPendingLineItemForNewPo", true);

                    sumOfTotal = sumOfTotal+ Integer.parseInt(totalAmountOfItem);

                    updateTotalQuantityOfPo(String.valueOf(sumOfTotal), currentPurchaseOrderNo);
                }
            }
            else
            {
                Boolean createPoPendingLineItem = pm.getBoolean("createPoPendingLineItem");

                if(createPoPendingLineItem)
                {
                    Toast.makeText(PurchaseOrderLineItems.this, "Already a PO Line Item creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(PurchaseOrderLineItems.this, "Internet not currently available. PO Line Item will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                    pm.putString("objectPOLineItem", object.toString());
                    pm.putString("urlPOLineItem", url);
                    pm.putString("toastMessagePOLineItem", "Purchase Order Line Item Created");
                    pm.putBoolean("createPoPendingLineItem", true);

                    sumOfTotal = sumOfTotal+ Integer.parseInt(totalAmountOfItem);

                    updateTotalQuantityOfPo(String.valueOf(sumOfTotal), currentPurchaseOrderNo);
                }
            }

        }
        else
        {
            requestQueue.add(jor);

            sumOfTotal = sumOfTotal+ Integer.parseInt(totalAmountOfItem);

            updateTotalQuantityOfPo(String.valueOf(sumOfTotal), currentPurchaseOrderNo);
        }
    }

    public void updateTotalQuantityOfPo(String quantityOfItem, String poNo)
    {
        JSONObject object = new JSONObject();

        try {
            object.put("totalAmount", quantityOfItem);

            Log.d("tag", String.valueOf(object));

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(PurchaseOrderLineItems.this);

        String url = PurchaseOrderLineItems.this.getResources().getString(R.string.server_url) + "/putPurchaseOrderTotal?purchaseOrderId=\""+ poNo + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                            {
                                pDialog2.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Toast.makeText(PurchaseOrderLineItems.this, error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }
        );
        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object budget :", object.toString());

            Boolean createPoPendingUpdateBudget = pm.getBoolean("createPoPendingUpdateBudget");

            if(createPoPendingUpdateBudget)
            {
                Toast.makeText(PurchaseOrderLineItems.this, "Already a PO Line Item creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(PurchaseOrderLineItems.this, "Internet not currently available. PO Line Item will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectPOUpdateBudget", object.toString());
                pm.putString("urlPOUpdateBudget", url);
                pm.putString("toastMessageUpdateBudget", "PO Budget Updated on PO Line Item creation");
                pm.putBoolean("createPoPendingUpdateBudget", true);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
        if(pDialog2!=null)
            pDialog2.dismiss();

        Intent intent = new Intent(PurchaseOrderLineItems.this, PurchaseOrderLineItems.class);
        startActivity(intent);
    }

//    public void getAllUom()
//    {
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//
//        String url = getResources().getString(R.string.server_url) + "/getUom";
//
//        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        try{
//                            String type = response.getString("type");
//
//                            if(type.equals("ERROR"))
//                            {
//                                Toast.makeText(PurchaseOrderLineItems.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
//                            }
//
//                            if(type.equals("INFO"))
//                            {
//                                dataArray = response.getJSONArray("data");
//                                uomArray = new String[dataArray.length()+1];
//
//                                uomArray[0]="UOM";
//
//                                for(int i=0; i<dataArray.length();i++)
//                                {
//                                    dataObject = dataArray.getJSONObject(i);
//                                    uomArray[i+1] = dataObject.getString("uomCode");
//                                }
//
//                                spinner_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                    @Override
//                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                                        text_item_name.setText(itemsNameArray[position]);
//                                        text_item_desc.setText(itemsDescArray[position]);
//                                        text_uom.setText(itemsUomArray[position]);
//
//                                    }
//
//                                    @Override
//                                    public void onNothingSelected(AdapterView<?> parent) {
//
//                                    }
//                                });
//                            }
//
//                        }
//                        catch(JSONException e){
//                            e.printStackTrace();}
//                        pDialog.dismiss();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Volley","Error");
//                        pDialog.dismiss();
//                    }
//                }
//        );
//        if (pDialog!=null)
//            pDialog.dismiss();
//
//        requestQueue.add(jor);
//
//    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PurchaseOrderLineItems.this, PurchaseOrders.class);
        startActivity(intent);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String date = year+"-"+(MONTHS[monthOfYear])+"-"+dayOfMonth;
        text_need_by_date.setText(date);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }

        if (pDialog1 != null) {
            pDialog1.dismiss();
            pDialog1 = null;
        }

        if (pDialog2 != null) {
            pDialog2.dismiss();
            pDialog2 = null;
        }

        if (pDialog3 != null) {
            pDialog3.dismiss();
            pDialog3 = null;
        }

        if (show != null) {
            show.dismiss();
            show = null;
        }
    }
}
