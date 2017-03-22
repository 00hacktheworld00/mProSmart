package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MaterialItemCreate extends AppCompatActivity {

    EditText text_quantity_issued;
    String itemId, itemDesc, uom, quantity, accepted;
    Button createBtn;
    Spinner spinner_item_id;
    String currentProjectNo, item, currentMaterialIssueId, currentDate, poQuantity;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    String[] itemArray, itemDescArray, uomArray, poQuantityArray;
    TextView text_item_desc, text_uom;
    String currentDateServer;
    ConnectionDetector cd;
    public static final String TAG = MaterialItemCreate.class.getSimpleName();
    Boolean isInternetPresent = false;
    PreferenceManager pm;
    TextView text_max_quantity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_item_create);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentMaterialIssueId = pm.getString("currentMaterialIssueId");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();

        currentDate = dateFormat.format(cal.getTime());

        text_max_quantity = (TextView) findViewById(R.id.text_max_quantity);

        spinner_item_id = (Spinner) findViewById(R.id.spinner_item_id);
        text_uom = (TextView) findViewById(R.id.text_uom);

        text_item_desc = (TextView) findViewById(R.id.text_item_desc);
        text_quantity_issued = (EditText) findViewById(R.id.text_quantity_issued);

        createBtn = (Button) findViewById(R.id.createBtn);


        pDialog = new ProgressDialog(MaterialItemCreate.this);
        pDialog.setMessage("Preparing Items...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        prepareItemList();

        spinner_item_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if(itemArray.length!=1)
                {
                    text_item_desc.setText(itemDescArray[position]);
                    text_uom.setText(uomArray[position]);

                    ProgressDialog progressDialog = new ProgressDialog(MaterialItemCreate.this);
                    progressDialog.setMessage("Getting Maximum Item Quantity");
                    progressDialog.show();
                    checkPoQuantity(progressDialog, spinner_item_id.getSelectedItem().toString(), poQuantityArray[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinner_item_id.getSelectedItem().toString().equals("Select Item"))
                {
                    Toast.makeText(MaterialItemCreate.this, "Select ITEM", Toast.LENGTH_SHORT).show();
                }
                else if(text_item_desc.getText().toString().isEmpty())
                {
                    text_item_desc.setError("Field cannot be empty");
                }
                else if(text_quantity_issued.getText().toString().isEmpty())
                {
                    text_quantity_issued.setError("Field cannot be empty");
                }
                else if(Integer.parseInt(text_quantity_issued.getText().toString())>Integer.parseInt(text_max_quantity.getText().toString()))
                {
                    text_quantity_issued.setError("Quantity cannot be more than Item Quantity");
                }

                else
                {
                    pDialog = new ProgressDialog(MaterialItemCreate.this);
                    pDialog.setMessage("Sending Data ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    quantity = text_quantity_issued.getText().toString();
                    itemId = spinner_item_id.getSelectedItem().toString();
                    itemDesc = text_item_desc.getText().toString();
                    uom = text_uom.getText().toString();

                    class MyTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... params) {
                            saveData(itemId,itemDesc, uom, quantity);
                            return null;
                        }
                    }
                    new MyTask().execute();
                }

            }
        });
    }

    public void saveData(String itemId, String itemDesc, String uom, String quantity)
    {
        JSONObject object = new JSONObject();
        try
        {
            object.put("itemId",itemId);
            object.put("itemDescription",itemDesc);
            object.put("quantityIssued",quantity);
            object.put("uomId",uom);
            object.put("createdDate",currentDate);
            object.put("materialIssueId",currentMaterialIssueId);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(MaterialItemCreate.this);

        String url = MaterialItemCreate.this.pm.getString("SERVER_URL") + "/postMaterialIssueLine";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                updateItemInventory();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");
                        Toast.makeText(MaterialItemCreate.this, error.toString(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jor);
    }
    public void updateItemInventory()
    {
        JSONObject object = new JSONObject();

        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            currentDateServer = dateFormat.format(date);

            object.put("projectId", currentProjectNo);
            object.put("itemId", spinner_item_id.getSelectedItem().toString());
            object.put("quantity", text_quantity_issued.getText().toString());
            object.put("type", "1");
            object.put("date", currentDateServer);

            Log.d("tag", String.valueOf(object));

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(MaterialItemCreate.this);

        String url = MaterialItemCreate.this.pm.getString("SERVER_URL") + "/postInventoryManagement";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            Toast.makeText(MaterialItemCreate.this, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();

                            if(response.getString("msg").equals("success"))
                            {
                                pDialog.dismiss();
                                Toast.makeText(MaterialItemCreate.this, "Material Issue Line Created. ID - " + response.getString("data"), Toast.LENGTH_SHORT).show();

                                String successMsg = "Inventory Updated";
                                Toast.makeText(MaterialItemCreate.this, successMsg, Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(MaterialItemCreate.this,MaterialIssueActivity.class);
                                startActivity(intent);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        pDialog.dismiss();
                        Toast.makeText(MaterialItemCreate.this, error.toString(), Toast.LENGTH_SHORT).show();

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

            Boolean createInventoryMaterialIssue = pm.getBoolean("createInventoryMaterialIssue");

            if(!createInventoryMaterialIssue)
            {
                pm.putString("objectInventoryMaterialIssue", object.toString());
                pm.putString("urlInventoryMaterialIssue", url);
                pm.putString("toastMessageInventoryMaterialIssue", "Inventory Updated");
                pm.putBoolean("createInventoryMaterialIssue", true);

                Intent intent = new Intent(MaterialItemCreate.this,MaterialIssueActivity.class);
                startActivity(intent);
            }
        }
        else
        {
            requestQueue.add(jor);
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

                            Log.d("RESPONSE ITEMS", response.toString());

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(MaterialItemCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();

                            }

                            if(type.equals("WARN"))
                            {
                                itemArray = new String[1];
                                itemArray[0]="No Items";
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MaterialItemCreate.this,
                                        android.R.layout.simple_dropdown_item_1line,itemArray);
                                spinner_item_id.setAdapter(adapter);
                                Log.d("Punch List", "ARRAY EMPTY");
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                itemArray = new String[dataArray.length()+1];
                                itemDescArray = new String[dataArray.length()+1];
                                uomArray = new String[dataArray.length()+1];
                                poQuantityArray = new String[dataArray.length()+1];

                                itemArray[0]="Select Item";
                                itemDescArray[0]="Item Description";
                                uomArray[0]="Item UOM";
                                poQuantityArray[0]="Max item quantity for issue";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    item = dataObject.getString("itemId");
                                    itemDesc = dataObject.getString("itemDescription");
                                    uom = dataObject.getString("uomId");
                                    poQuantity = dataObject.getString("poQuantity");

                                    itemArray[i+1]=item;
                                    itemDescArray[i+1]=itemDesc;
                                    uomArray[i+1]=uom;
                                    poQuantityArray[i+1]=poQuantity;
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MaterialItemCreate.this,
                                        android.R.layout.simple_dropdown_item_1line,itemArray);
                                spinner_item_id.setAdapter(adapter);

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

    public void checkPoQuantity(final ProgressDialog progressDialog, String itemId, final String poQuantity)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getMaterialIssueQuantity?materialIssueId=\"" + currentMaterialIssueId +
                "\"&itemId=\"" + itemId + "\"";

        Log.d("QUANITTY URL", url);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            progressDialog.dismiss();
                            Log.d("RESPONSE QUANTITY", response.toString());

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(MaterialItemCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();

                            }

                            if(type.equals("WARN"))
                            {
                                itemArray = new String[1];
                                itemArray[0]="No Items";
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MaterialItemCreate.this,
                                        android.R.layout.simple_dropdown_item_1line,itemArray);
                                spinner_item_id.setAdapter(adapter);
                                Log.d("PO Quantity", "ARRAY EMPTY");
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");

                                Log.d("PO QUAN", poQuantity);

                                JSONObject quantityObj = dataArray.getJSONObject(0);
                                if(quantityObj.isNull("quantity"))
                                {
                                    quantity = poQuantity;
                                    Log.d("QUANTITY", quantity);
                                    text_max_quantity.setText(quantity);
                                }
                                else
                                {
                                    String quantity = quantityObj.getString("quantity");
                                    Log.d("QUANTITY", quantity);
                                    quantity = String.valueOf(Integer.parseInt(poQuantity) - Integer.parseInt(quantity));

                                    text_max_quantity.setText(quantity);
                                }


                            }

                            progressDialog.dismiss();
                        }catch(JSONException e){
                            progressDialog.dismiss();
                            e.printStackTrace();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.e("Volley","Error");

                    }
                }
        );
        requestQueue.add(jor);
    }

}
