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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PurchaseRequisitionItemCreate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText text_desc, text_quantity, text_uom;

    String itemId, quantity, uom, needByDate;
    Button createBtn;
    TextView btn_date;
    Spinner spinnerItemId;
    String[] itemsNameArray, itemsDescArray, itemsUomArray, itemIdArray, uomArray, uomNameArray;
    JSONArray dataArray;
    JSONObject dataObject;
    String currentProjectNo, currentPr, currentUser, currentDate;
    String itemNames, itemDesc, itemUom, itemsId, selectedUom;
    ProgressDialog pDialog;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_requisition_item_create);

        pm = new PreferenceManager(this);
        currentProjectNo = pm.getString("projectId");
        currentPr = pm.getString("currentPr");
        currentUser = pm.getString("userId");


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        spinnerItemId = (Spinner) findViewById(R.id.spinnerItemId);

        text_desc = (EditText) findViewById(R.id.text_desc);
        text_quantity = (EditText) findViewById(R.id.text_quantity);
        text_uom = (EditText) findViewById(R.id.text_uom);

        createBtn = (Button) findViewById(R.id.createBtn);

        btn_date = (TextView) findViewById(R.id.btn_date);

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        PurchaseRequisitionItemCreate.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMinDate(now);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });


        pDialog = new ProgressDialog(PurchaseRequisitionItemCreate.this);
        pDialog.setMessage("Getting Items...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        getAllUom(pDialog);

        spinnerItemId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    text_uom.setText("");
                    text_desc.setText("");
                }
                else
                {
                    text_desc.setText(itemsDescArray[position]);

                    for(int j=0; j<uomArray.length;j++)
                    {
                        if(uomArray[j].equals(itemsUomArray[position]))
                        {
                            text_uom.setText(uomNameArray[j]);
                            selectedUom = uomArray[j];
                        }
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinnerItemId.getSelectedItem().toString().isEmpty())
                {
                    Toast.makeText(PurchaseRequisitionItemCreate.this, "Select Item", Toast.LENGTH_SHORT).show();
                }
                else if(text_desc.getText().toString().isEmpty())
                {
                    text_desc.setError("Field cannot be empty");
                }
                else if(text_quantity.getText().toString().isEmpty())
                {
                    text_quantity.setError("Field cannot be empty");
                }
                else
                {
                    itemId = spinnerItemId.getSelectedItem().toString();
                    itemDesc = text_desc.getText().toString();
                    quantity = text_quantity.getText().toString();
                    uom = text_uom.getText().toString();
                    needByDate = btn_date.getText().toString();

                    pDialog = new ProgressDialog(PurchaseRequisitionItemCreate.this);
                    pDialog.setMessage("Sending Data ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    class MyTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... params) {
                            saveData(itemId, itemDesc, quantity, uom, needByDate);
                            return null;
                        }
                    }
                    new MyTask().execute();

                }

            }
        });
    }


    public void getAllUom(final ProgressDialog pDialog)
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
                                Toast.makeText(PurchaseRequisitionItemCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                uomArray = new String[dataArray.length()+1];
                                uomNameArray = new String[dataArray.length()+1];

                                uomNameArray[0]="Select UOM";
                                uomArray[0]="Select UOM";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    uomArray[i+1] = dataObject.getString("uomCode");
                                    uomNameArray[i+1] = dataObject.getString("uomName");
                                }
                                prepareItemList(getApplicationContext());
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
                                Toast.makeText(PurchaseRequisitionItemCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
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



                                ArrayAdapter<String> adapterItemList = new ArrayAdapter<String>(context,
                                        android.R.layout.simple_dropdown_item_1line,itemIdArray);

                                spinnerItemId.setAdapter(adapterItemList);
                            }

                            if(msg.equals("No Data"))
                            {
                                itemsNameArray = new String[1];
                                itemsDescArray = new String[1];
                                itemIdArray = new String[1];
                                itemsNameArray[0]="No Data";
                                itemsDescArray[0]="No Data";
                                itemIdArray[0]="No Data";
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
    }

    public void saveData(String itemId, String itemDesc, String quantity, String uom, String needByDate)
    {
        JSONObject object = new JSONObject();

        try {
            object.put("purchaseRequisitionId", currentPr);
            object.put("itemId", itemId);
            object.put("itemDescription", itemDesc);
            object.put("quantity", quantity);
            object.put("uom", selectedUom);
            object.put("neededBy", needByDate);
            object.put("createdBy", currentUser);
            object.put("createDate", currentDate);

            Log.d("json of data : ", object.toString());

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(PurchaseRequisitionItemCreate.this);

        String url = PurchaseRequisitionItemCreate.this.pm.getString("SERVER_URL") + "/postPurchaseRequisitionItem";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("response of server : ", response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                String successMsg = "Purchase Requisition Item Created";
                                Toast.makeText(PurchaseRequisitionItemCreate.this, successMsg, Toast.LENGTH_SHORT).show();

                                pDialog.dismiss();

                                Intent intent = new Intent(PurchaseRequisitionItemCreate.this, PurchaseRequisition.class);
                                startActivity(intent);
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
                        Toast.makeText(PurchaseRequisitionItemCreate.this, error.toString(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jor);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String date = year+"-"+(MONTHS[monthOfYear])+"-"+dayOfMonth;
        btn_date.setText(date);
    }
}
