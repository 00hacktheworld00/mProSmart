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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddItemsActivity extends AppCompatActivity {

    JSONArray dataArray;
    JSONObject dataObject;
    String[] uomArray, uomNameArray;
    Spinner  spinner_uom;
    ProgressDialog pDialog;
    Button createBtn;
    EditText text_item_name, text_item_desc, text_item_quantity;
    RadioButton radiobtn_non_assembly, radiobtn_assembly;
    String currentUser, currentProjectId, currentUomId;
    Boolean editItem = false;
    String editItemUom, editItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);

        PreferenceManager pm = new PreferenceManager(this);
        currentUser = pm.getString("userId");
        currentProjectId = pm.getString("projectId");

        spinner_uom = (Spinner) findViewById(R.id.spinner_uom);

        createBtn = (Button) findViewById(R.id.createBtn);

        text_item_name = (EditText) findViewById(R.id.text_item_name);
        text_item_desc = (EditText) findViewById(R.id.text_item_desc);

        text_item_quantity = (EditText) findViewById(R.id.text_item_quantity);

        text_item_quantity.setVisibility(View.GONE);

        radiobtn_non_assembly = (RadioButton) findViewById(R.id.radiobtn_non_assembly);
        radiobtn_assembly = (RadioButton) findViewById(R.id.radiobtn_assembly);

        if(getIntent().hasExtra("editItem"))
        {
            if(getIntent().getBooleanExtra("editItem", true))
            {
                editItemId  = getIntent().getStringExtra("itemId");
                String itemName = getIntent().getStringExtra("itemName");
                String itemDesc = getIntent().getStringExtra("itemDesc");
                editItemUom = getIntent().getStringExtra("itemUom");

                LinearLayout noEditLayout = (LinearLayout) findViewById(R.id.noEditLayout);
                noEditLayout.setVisibility(View.GONE);

                HelveticaRegular title = (HelveticaRegular) findViewById(R.id.title);
                title.setText("Edit Item");

                createBtn.setText("SAVE ITEM");

                text_item_name.setText(itemName);
                text_item_desc.setText(itemDesc);

                editItem = true;

                //editing is being done
            }
        }

        pDialog = new ProgressDialog(AddItemsActivity.this);
        pDialog.setMessage("Getting Items List ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                getAllUom(editItem, editItemUom);
                return null;
            }
        }
        new MyTask().execute();

        spinner_uom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0)
                {
                    currentUomId = uomArray[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        radiobtn_assembly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    text_item_quantity.setVisibility(View.VISIBLE);
                }
                else
                {
                    text_item_quantity.setVisibility(View.GONE);
                }
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_item_name.getText().toString().isEmpty())
                {
                    text_item_name.setError("Field cannot be empty");
                }
                else if(text_item_desc.getText().toString().isEmpty())
                {
                    text_item_desc.setError("Field cannot be empty");
                }
                else if(spinner_uom.getSelectedItem().toString().equals("Select UOM"))
                {
                    Toast.makeText(AddItemsActivity.this, "Select UOM", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(editItem)
                    {
                        pDialog = new ProgressDialog(AddItemsActivity.this);
                        pDialog.setMessage("Saving Data ...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(true);
                        pDialog.show();

                        class MyTask extends AsyncTask<Void, Void, Void>
                        {
                            @Override
                            protected Void doInBackground(Void... params)
                            {
                                updateItemsEditing();
                                return null;
                            }
                        }
                        new MyTask().execute();


                    }
                    else
                    {
                        //creation of items
                        if(radiobtn_non_assembly.isChecked())
                        {
                            pDialog = new ProgressDialog(AddItemsActivity.this);
                            pDialog.setMessage("Saving Data ...");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(true);
                            pDialog.show();

                            class MyTask extends AsyncTask<Void, Void, Void>
                            {
                                @Override
                                protected Void doInBackground(Void... params)
                                {
                                    saveNonAssemblyItems();
                                    return null;
                                }
                            }
                            new MyTask().execute();
                        }
                        else
                        {
                            if(text_item_quantity.getText().toString().isEmpty())
                            {
                                text_item_quantity.setError("Field cannot be empty");
                            }
                            else
                            {
                                pDialog = new ProgressDialog(AddItemsActivity.this);
                                pDialog.setMessage("Saving Data ...");
                                pDialog.setIndeterminate(false);
                                pDialog.setCancelable(true);
                                pDialog.show();

                                class MyTask extends AsyncTask<Void, Void, Void>
                                {
                                    @Override
                                    protected Void doInBackground(Void... params)
                                    {
                                        saveAssemblyItems();
                                        return null;
                                    }
                                }
                                new MyTask().execute();
                            }
                        }
                    }

                }
            }
        });


    }


    public void getAllUom(final Boolean editItem, final String editItemUom)
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getUom";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            int positionUomEdit = -1;

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AddItemsActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
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

                                    if(editItem)
                                    {
                                        if(uomNameArray[i+1].equals(editItemUom))
                                        {
                                            positionUomEdit = i+1;
                                        }
                                    }

                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddItemsActivity.this,
                                        android.R.layout.simple_dropdown_item_1line,uomNameArray);
                                spinner_uom.setAdapter(adapter);

                                if(editItem)
                                    spinner_uom.setSelection(positionUomEdit);

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

    public void updateItemsEditing()
    {
        JSONObject object = new JSONObject();


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = sdf.format(c.getTime());

        try {

            object.put("itemName", text_item_name.getText().toString());
            object.put("itemDescription", text_item_desc.getText().toString());
            object.put("uomId",currentUomId);

            Log.d("tag", String.valueOf(object));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AddItemsActivity.this);

        String url = AddItemsActivity.this.getResources().getString(R.string.server_url) + "/putItem?itemId=\"" + editItemId + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                Log.d("on Response: ", response.toString());
                                String successMsg = "Item Updated - "+ editItemId;
                                Toast.makeText(AddItemsActivity.this, successMsg, Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                Intent intent = new Intent(AddItemsActivity.this, AllItemsActivity.class);
                                startActivity(intent);
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
                        Toast.makeText(AddItemsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        if (pDialog!=null)
            pDialog.dismiss();

        requestQueue.add(jor);
    }

    public void saveNonAssemblyItems()
    {
        JSONObject object = new JSONObject();


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = sdf.format(c.getTime());

        try {

            object.put("itemName", text_item_name.getText().toString());
            object.put("itemDescription", text_item_desc.getText().toString());
            object.put("uomId",currentUomId);
            object.put("createdBy", currentUser);
            object.put("projectId", currentProjectId);
            object.put("createdDate", strDate);

            Log.d("tag", String.valueOf(object));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AddItemsActivity.this);

        String url = AddItemsActivity.this.getResources().getString(R.string.server_url) + "/postItems";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                String successMsg = "Item added. ID - "+ response.getString("data");
                                Toast.makeText(AddItemsActivity.this, successMsg, Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                Intent intent = new Intent(AddItemsActivity.this, AllItemsActivity.class);
                                startActivity(intent);
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
                        Toast.makeText(AddItemsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        if (pDialog!=null)
            pDialog.dismiss();

        requestQueue.add(jor);
    }

    public void saveAssemblyItems()
    {
        JSONObject object = new JSONObject();


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(c.getTime());

        try {
            object.put("projectId", currentProjectId);
            object.put("itemName", text_item_name.getText().toString());
            object.put("unit", text_item_quantity.getText().toString());

            //to be DONE
//            object.put("itemDescription", text_item_desc.getText().toString());

            object.put("uom", currentUomId);
            object.put("createdBy", currentUser);
            object.put("createdDate", currentDate);

            Log.d("json object : ", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AddItemsActivity.this);

        String url = AddItemsActivity.this.getResources().getString(R.string.server_url) + "/postBoq";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("response : ", response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                String successMsg = "BOQ Created. ID - "+ response.getString("data");
                                Toast.makeText(AddItemsActivity.this, successMsg, Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(AddItemsActivity.this, AllBoq.class);
                                startActivity(intent);
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
                        Toast.makeText(AddItemsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jor);
        if(pDialog!=null)
            pDialog.dismiss();
    }

}
