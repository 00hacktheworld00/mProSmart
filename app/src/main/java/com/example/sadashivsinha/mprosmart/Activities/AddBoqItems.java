package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddBoqItems extends AppCompatActivity{

    Button addBtn;
    Spinner spinner_item, spinner_uom;
    EditText text_item_name, text_item_desc, text_quantity, text_unit_cost;
    TextView text_total_amount, text_currency;
    ProgressDialog pDialog, pDialog2;
    String currentProjectNo, currentUser, currentCurrency, currentDate;
    PreferenceManager pm;
    JSONArray dataArray;
    JSONObject dataObject;
    String itemId, itemNames, itemDesc ,itemUom, currentBoq;
    String[] itemsNameArray, itemsDescArray, itemsUomArray, itemIdArray;
    LinearLayout hiddenLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_boq_items);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUser = pm.getString("userId");
        currentCurrency = pm.getString("currency");
        currentBoq = pm.getString("currentBoq");


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        spinner_item = (Spinner) findViewById(R.id.spinner_item);
        spinner_uom = (Spinner) findViewById(R.id.spinner_uom);
        text_currency = (TextView) findViewById(R.id.text_currency);

        text_item_name = (EditText) findViewById(R.id.text_item_name);
        text_item_desc = (EditText) findViewById(R.id.text_item_desc);
        text_quantity = (EditText) findViewById(R.id.text_quantity);
        text_unit_cost = (EditText) findViewById(R.id.text_unit_cost);
        text_total_amount = (TextView) findViewById(R.id.text_total_amount);

        text_currency.setText(currentCurrency);

        text_item_name.setEnabled(false);
        text_item_desc.setEnabled(false);
        spinner_uom.setEnabled(false);

        addBtn = (Button) findViewById(R.id.addBtn);

        hiddenLayout = (LinearLayout) findViewById(R.id.hiddenLayout);

        pDialog = new ProgressDialog(AddBoqItems.this);
        pDialog.setMessage("Getting Items...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        prepareItemList(this);


        spinner_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                    text_item_name.setText(itemsNameArray[position]);
                    Log.d("Name array data: ", itemsNameArray[position]);
//                    text_item_desc.setText(itemsDescArray[position+1]);
//                    Log.d("Desc array data: ", itemsDescArray[position+1]);
                    spinner_uom.setSelection(position);
                    Log.d("Uom array data: ", spinner_uom.getSelectedItem().toString());

                    text_item_name.setEnabled(false);
                    text_item_desc.setEnabled(false);
                    spinner_uom.setEnabled(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                // your code here
            }

        });



        TextWatcher inputTextWatcher = new TextWatcher() {

            int quantityVal, unitCostVal, totalVal;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(text_quantity.getText().toString().isEmpty())
                {
                    text_quantity.setError("Enter Value");
                }
                else if(text_unit_cost.getText().toString().isEmpty())
                {
                    text_unit_cost.setError("Enter Value");
                }
            }

            public void afterTextChanged(Editable s) {
                if(text_quantity.getText().toString().isEmpty())
                {
                    text_quantity.setError("Enter Value");
                }
                else if(text_unit_cost.getText().toString().isEmpty())
                {
                    text_unit_cost.setError("Enter Value");
                }

                else
                {
                    quantityVal = Integer.parseInt(text_quantity.getText().toString());
                    unitCostVal = Integer.parseInt(text_unit_cost.getText().toString());

                    totalVal = quantityVal * unitCostVal ;

                    text_total_amount.setText(String.valueOf(totalVal));
                }
            }
        };

        text_quantity.addTextChangedListener(inputTextWatcher);
        text_unit_cost.addTextChangedListener(inputTextWatcher);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinner_uom.getSelectedItem().toString().equals("Select UOM"))
                {
                    Toast.makeText(AddBoqItems.this, "Select UOM", Toast.LENGTH_SHORT).show();
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

    public void prepareItemList(final Context context)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getItems?projectId='"+currentProjectNo+"'";

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

                                    itemIdArray[i+1]=itemId;
                                    itemsNameArray[i+1]=itemNames;
//                                    itemsDescArray[i+1]=itemDesc;
                                    itemsUomArray[i+1]=itemUom;
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

                            if(msg.equals("No data"))
                            {
                                Toast.makeText(AddBoqItems.this, "No BOQ Items available for this project", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddBoqItems.this, BoqActivity.class);
                                startActivity(intent);
                            }

                            else
                            {
                                itemIdArray[0]="Select Item";
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                        android.R.layout.simple_dropdown_item_1line,itemIdArray);
                                spinner_item.setAdapter(adapter);

                                ArrayAdapter<String> adapterUom = new ArrayAdapter<String>(context,
                                        android.R.layout.simple_dropdown_item_1line, itemsUomArray);

                                spinner_uom.setAdapter(adapterUom);
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
            object.put("uom", spinner_uom.getSelectedItem().toString());
            object.put("cost", text_unit_cost.getText().toString());
            object.put("currency", text_currency.getText().toString());
            object.put("totalCost", text_total_amount.getText().toString());
            object.put("createdBy", currentUser);
            object.put("createdDate", currentDate);

            Log.d("OBJECT JSON : ", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AddBoqItems.this);

        String url = AddBoqItems.this.getResources().getString(R.string.server_url) + "/postBoqItems?projectId=\""+currentProjectNo+"\"";

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
        requestQueue.add(jor);

        if(pDialog2!=null)
            pDialog2.dismiss();
    }

}
