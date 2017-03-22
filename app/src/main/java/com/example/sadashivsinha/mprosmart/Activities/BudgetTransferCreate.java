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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BudgetTransferCreate extends AppCompatActivity {

    EditText text_cost_code, text_cost_name, text_quantity, text_amount;
    Spinner spinner_uom, spinner_item;
    TextView item_desc;
    Button createBtn;
    ProgressDialog pDialog;
    String currentProjectNo, item, itemDesc;
    JSONArray dataArray;
    JSONObject dataObject;
    String[] itemArray, itemDescArray;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_transfer_create);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");

        item_desc = (TextView) findViewById(R.id.item_desc);
        text_cost_code = (EditText) findViewById(R.id.text_cost_code);
        text_cost_name = (EditText) findViewById(R.id.text_cost_name);
        text_quantity = (EditText) findViewById(R.id.text_quantity);
        text_amount = (EditText) findViewById(R.id.text_amount);

        spinner_uom = (Spinner) findViewById(R.id.spinner_uom);
        spinner_item = (Spinner) findViewById(R.id.spinner_item);

        createBtn = (Button) findViewById(R.id.createBtn);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(BudgetTransferCreate.this,
                android.R.layout.simple_dropdown_item_1line,new String[] {"Select UOM", "KGs", "Litres"});
        spinner_uom.setAdapter(adapter);

        pDialog = new ProgressDialog(BudgetTransferCreate.this);
        pDialog.setMessage("Preparing Items...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {

            @Override
            protected Void doInBackground(Void... params)
            {
                prepareLineItems();
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

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner_item.getSelectedItem().toString().equals("Select Item"))
                {
                    Toast.makeText(BudgetTransferCreate.this, "Select Item", Toast.LENGTH_SHORT).show();
                }
                else if(item_desc.getText().toString().isEmpty())
                {
                    item_desc.setError("Field cannot be empty.");
                }
                else if(text_cost_code.getText().toString().isEmpty())
                {
                    text_cost_code.setError("Field cannot be empty.");
                }
                else if(text_cost_name.getText().toString().isEmpty())
                {
                    text_cost_name.setError("Field cannot be empty.");
                }
                else if(text_quantity.getText().toString().isEmpty())
                {
                    text_quantity.setError("Field cannot be empty.");
                }

                else if(spinner_uom.getSelectedItem().toString().equals("Select UOM"))
                {
                    Toast.makeText(BudgetTransferCreate.this, "Select UOM", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    Intent intent = new Intent(BudgetTransferCreate.this, BudgetTransfer.class);
                    intent.putExtra("item_name", spinner_item.getSelectedItem().toString());
                    intent.putExtra("item_desc", item_desc.getText().toString());
                    intent.putExtra("text_cost_code", text_cost_code.getText().toString());
                    intent.putExtra("text_cost_name", text_cost_name.getText().toString());
                    intent.putExtra("text_quantity", text_quantity.getText().toString());
                    intent.putExtra("spinner_uom", spinner_uom.getSelectedItem().toString());
                    intent.putExtra("text_amount", text_amount.getText().toString());

                    startActivity(intent);
                }
            }
        });
    }
    public void prepareLineItems()
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
                                Toast.makeText(BudgetTransferCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
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
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(BudgetTransferCreate.this,
                                    android.R.layout.simple_dropdown_item_1line,itemArray);
                            spinner_item.setAdapter(adapter);

                            pDialog.dismiss();
                        }
                        catch(JSONException e){
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
}
