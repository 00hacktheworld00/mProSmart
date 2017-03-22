package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddBoqItems extends AppCompatActivity{

    Button addBtn;
    Spinner spinner_item;
    EditText text_item_name, text_item_desc, text_quantity, text_uom;
    ProgressDialog pDialog, pDialog2;
    String currentProjectNo, currentUser, currentCurrency, currentDate;
    PreferenceManager pm;
    JSONArray dataArray;
    JSONObject dataObject;
    String itemId, itemNames, itemDesc ,itemUom, currentBoq;
    String[] itemsNameArray, itemsDescArray, itemsUomArray, itemIdArray, uomArray, uomNameArray;
    LinearLayout hiddenLayout;
    String currentUomId;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_boq_items);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUser = pm.getString("userId");
        currentBoq = pm.getString("currentBoq");
        currentCurrency = pm.getString("currency");

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        spinner_item = (Spinner) findViewById(R.id.spinner_item);
        text_uom = (EditText) findViewById(R.id.text_uom);

        text_item_name = (EditText) findViewById(R.id.text_item_name);
        text_item_desc = (EditText) findViewById(R.id.text_item_desc);
        text_quantity = (EditText) findViewById(R.id.text_quantity);


        text_item_name.setEnabled(false);
        text_item_desc.setEnabled(false);
        text_uom.setEnabled(false);

        addBtn = (Button) findViewById(R.id.addBtn);

        hiddenLayout = (LinearLayout) findViewById(R.id.hiddenLayout);

        pDialog = new ProgressDialog(AddBoqItems.this);
        pDialog.setMessage("Getting Items...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        prepareUom(this);


        spinner_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
               if(itemIdArray.length!=1)
               {
                   text_item_name.setText(itemsNameArray[position]);
                   Log.d("Name array data: ", itemsNameArray[position]);

                   for(int i=0; i<uomArray.length ; i++)
                   {
                       if(itemsUomArray[position].equals(uomArray[i]))
                       {
                           currentUomId = uomArray[i];
                           text_uom.setText(uomNameArray[i]);
                       }
                   }
                   Log.d("Uom data: ", text_uom.getText().toString());

                   text_item_desc.setText(itemsDescArray[position]);
                   Log.d("Uom DESC data: ",itemsDescArray[position]);
               }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                // your code here
            }

        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinner_item.getSelectedItem().toString().equals("Select Item"))
                {
                    Toast.makeText(AddBoqItems.this, "Select Item", Toast.LENGTH_SHORT).show();
                }
                else if(spinner_item.getSelectedItem().toString().equals("No Items"))
                {
                    Toast.makeText(AddBoqItems.this, "No Items in this project", Toast.LENGTH_SHORT).show();
                }
                else if(text_quantity.getText().toString().isEmpty())
                {
                    text_quantity.setError("Enter Value");
                }
                else
                {
                    pDialog2 = new ProgressDialog(AddBoqItems.this);
                    pDialog2.setMessage("Saving Data ...");
                    pDialog2.setIndeterminate(false);
                    pDialog2.setCancelable(true);
                    pDialog2.show();

                    class MyTask extends AsyncTask<Void, Void, Void>
                    {
                        @Override
                        protected Void doInBackground(Void... params)
                        {
                            saveItem();
                            return null;
                        }
                    }
                    new MyTask().execute();
                }
            }
        });

    }

    public void prepareUom(final Context context)
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getUom";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AddBoqItems.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                uomArray = new String[dataArray.length()+1];
                                uomNameArray = new String[dataArray.length()+1];

                                uomArray[0]="UOM";
                                uomNameArray[0] = "UOM";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    uomArray[i+1] = dataObject.getString("uomCode");
                                    uomNameArray[i+1] = dataObject.getString("uomName");
                                }

                                prepareItemList(context);
                            }

                        }
                        catch(JSONException e){
                            e.printStackTrace();}
                        pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");
                        pDialog.dismiss();
                    }
                }
        );
        if (pDialog!=null)
            pDialog.dismiss();

        requestQueue.add(jor);

    }

    public void prepareItemList(final Context context)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getItems?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");
                            String msg = response.getString("msg");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AddBoqItems.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("WARN"))
                            {
                                itemIdArray = new String[1];
                                itemIdArray[0]="No Items";
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                        android.R.layout.simple_dropdown_item_1line,itemIdArray);
                                spinner_item.setAdapter(adapter);
                                Log.d("BOQ", "ARRAY EMPTY");
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                itemIdArray = new String[dataArray.length()+1];
                                itemsNameArray = new String[dataArray.length()+1];
                                itemsDescArray = new String[dataArray.length()+1];
                                itemsUomArray = new String[dataArray.length()+1];


                                itemsNameArray[0]="Select BOQ Item";
                                itemsDescArray[0]="Select BOQ";
                                itemsUomArray[0]="Select BOQ";
                                itemIdArray[0]="Select BOQ";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);


                                    itemId = dataObject.getString("itemId");
                                    itemNames = dataObject.getString("itemName");
                                    itemUom = dataObject.getString("uomId");
                                    itemDesc = dataObject.getString("itemDescription");

                                    itemIdArray[i+1]=itemId;
                                    itemsNameArray[i+1]=itemNames;
                                    itemsDescArray[i+1]=itemDesc;
                                    itemsUomArray[i+1]=itemUom;
                                }


                                if(msg.equals("No data"))
                                {
                                    Toast.makeText(AddBoqItems.this, "No BOQ Items available for this project", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddBoqItems.this, BoqActivity.class);
                                    startActivity(intent);
                                    Log.d("BOQ", "NO DATA");
                                }

                                else
                                {
                                    itemIdArray[0]="Select Item";
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                            android.R.layout.simple_dropdown_item_1line,itemIdArray);
                                    spinner_item.setAdapter(adapter);
                                    Log.d("BOQ", "DATA PRESENT");
                                }

//                                if(itemIdArray==null)
//                                {
//                                    Toast.makeText(AddBoqItems.this, "No BOQ Items available for this project", Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(AddBoqItems.this, BoqActivity.class);
//                                    startActivity(intent);
//                                }
//
//                                else
//                                {
//
//                                    ArrayAdapter<String> itemAdapter = new ArrayAdapter<String>(context,
//                                            android.R.layout.simple_dropdown_item_1line,itemIdArray);
//
//                                    ArrayAdapter<String> adapterUom = new ArrayAdapter<String>(context,
//                                            android.R.layout.simple_dropdown_item_1line, itemsUomArray);
//
//                                    spinner_uom.setAdapter(adapterUom);
//
//                                    spinner_item.setAdapter(itemAdapter);
//                                }
                            }

                            if(pDialog != null)
                                pDialog.dismiss();

                        }catch(JSONException e){e.printStackTrace();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");

                    }
                }
        );
        requestQueue.add(jor);
        if(pDialog != null)
            pDialog.dismiss();
    }

//    public void saveMasterListItems()
//    {
//        JSONObject object = new JSONObject();
//
//
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//        String strDate = sdf.format(c.getTime());
//
//        try {
//
//            if(spinner_item.getSelectedItem().equals("Create a new Item"))
//            {
//                object.put("itemName", text_item_name.getText().toString());
//            }
//            else
//            {
//                object.put("itemName", spinner_item.getSelectedItem().toString());
//            }
//            object.put("itemDescription", text_item_desc.getText().toString());
//            object.put("uomId", spinner_uom.getSelectedItem().toString());
//            object.put("createdBy", currentUser);
//            object.put("projectId", currentProjectNo);
//            object.put("createdDate", strDate);
//
//            Log.d("tag", String.valueOf(object));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RequestQueue requestQueue = Volley.newRequestQueue(AddBoqItems.this);
//
//        String url = AddBoqItems.this.getResources().getString(R.string.server_url) + "/postItems";
//
//        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//
//                            if(response.getString("msg").toString().equals("success"))
//                            {
//                                String successMsg = "BOQ Line Item added to item list. ID - "+response.getString("data").toString();
//                                Toast.makeText(AddBoqItems.this, successMsg, Toast.LENGTH_SHORT).show();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        //response success message display
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Volley", "Error");
//                        Toast.makeText(AddBoqItems.this, error.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );
//        requestQueue.add(jor);
//
//
//        saveItem();
//    }

    public void saveItem()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("boqId", currentBoq);
            object.put("projectId", currentProjectNo);
            object.put("item", spinner_item.getSelectedItem().toString());
            object.put("quantity", text_quantity.getText().toString());
            object.put("uom", currentUomId);
            object.put("cost", "");
            object.put("currency", currentCurrency);
            object.put("totalCost", "");
            object.put("createdBy", currentUser);
            object.put("createdDate", currentDate);

            Log.d("OBJECT JSON : ", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AddBoqItems.this);

        String url = pm.getString("SERVER_URL") + "/postBoqItems?projectId=\""+currentProjectNo+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            Toast.makeText(AddBoqItems.this, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();

                            Log.d("SERVER RESPONSE : ", response.toString());
                            if(response.getString("msg").equals("success"))
                            {
                                String successMsg = "BOQ Line Item Created. ID - "+ response.getString("data");
                                Toast.makeText(AddBoqItems.this, successMsg, Toast.LENGTH_SHORT).show();
                                pDialog2.dismiss();
                                Intent intent = new Intent(AddBoqItems.this ,BoqActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(AddBoqItems.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
//                            Intent intent = new Intent(dialogView.getContext(), AddBoqItems.class);
//                            dialogView.getContext().startActivity(intent);

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
                        Toast.makeText(AddBoqItems.this, error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }
        );

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createBOQPendingLine = pm.getBoolean("createBOQPendingLine");

            if(createBOQPendingLine)
            {
                Toast.makeText(AddBoqItems.this, "Already a BOQ Line Item creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AddBoqItems.this, "Internet not currently available. BOQ Line Item will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectBOQLine", object.toString());
                pm.putString("urlBOQLine", url);
                pm.putString("toastMessageBOQLine", "BOQ Line Item Created");
                pm.putBoolean("createBOQPendingLine", true);
            }


            if(pDialog2!=null)
                pDialog2.dismiss();

            Intent intent = new Intent(AddBoqItems.this, BoqActivity.class);
            startActivity(intent);
        }
        else
        {
            requestQueue.add(jor);
        }
    }

}
