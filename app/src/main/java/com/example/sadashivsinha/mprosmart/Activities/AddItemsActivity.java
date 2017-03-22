package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

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
    HelveticaBold label_quantity;
    PreferenceManager pm;
    ConnectionDetector cd;
    Boolean isInternetPresent;
    String url;
    public static final String TAG = AddItemsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);

        pm = new PreferenceManager(this);
        currentUser = pm.getString("userId");
        currentProjectId = pm.getString("projectId");

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        spinner_uom = (Spinner) findViewById(R.id.spinner_uom);

        createBtn = (Button) findViewById(R.id.createBtn);

        text_item_name = (EditText) findViewById(R.id.text_item_name);
        text_item_desc = (EditText) findViewById(R.id.text_item_desc);

        label_quantity = (HelveticaBold) findViewById(R.id.label_quantity);

        text_item_quantity = (EditText) findViewById(R.id.text_item_quantity);

        text_item_quantity.setVisibility(View.GONE);
        label_quantity.setVisibility(View.GONE);

        radiobtn_non_assembly = (RadioButton) findViewById(R.id.radiobtn_non_assembly);
        radiobtn_assembly = (RadioButton) findViewById(R.id.radiobtn_assembly);

        url = pm.getString("SERVER_URL") + "/getUom";

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
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AddItemsActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AddItemsActivity.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(url);
            if(entry != null){
                //Cache data available.
                try {
                    int positionUomEdit=-1;

                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject=new JSONObject(data);
                    try
                    {
                        dataArray = jsonObject.getJSONArray("data");
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

                        pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                }
                catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                if(pDialog!=null)
                    pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            getAllUom(editItem, editItemUom);
        }

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
                    label_quantity.setVisibility(View.VISIBLE);
                }
                else
                {
                    text_item_quantity.setVisibility(View.GONE);
                    label_quantity.setVisibility(View.GONE);
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


                        updateItemsEditing();


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


                            saveNonAssemblyItems();
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

                                saveAssemblyItems();
                            }
                        }
                    }

                }
            }
        });


    }


    public void getAllUom(final Boolean editItem, final String editItemUom)
    {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response project : ", response.toString());
                        try {

                            int positionUomEdit = -1;

//                            dataObject = response.getJSONObject(0);
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
                            if(pDialog!=null)
                                pDialog.dismiss();

                            if(editItem)
                                spinner_uom.setSelection(positionUomEdit);
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

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
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

        String url = pm.getString("SERVER_URL") + "/putItem?itemId=\"" + editItemId + "\"";

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
        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createItemUpdatePending = pm.getBoolean("createItemUpdatePending");

            if(createItemUpdatePending)
            {
                Toast.makeText(AddItemsActivity.this, "Already an item updation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AddItemsActivity.this, "Internet not currently available. Item will automatically get updated on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectUpdateItem", object.toString());
                pm.putString("urlUpdateItem", url);
                pm.putString("toastMessageUpdateItem", "Item Updated - " + editItemId);
                pm.putBoolean("createItemUpdatePending", true);

                pDialog.dismiss();
                Intent intent = new Intent(AddItemsActivity.this, AllItemsActivity.class);
                startActivity(intent);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
    }

    public void saveNonAssemblyItems()
    {
        JSONObject object = new JSONObject();


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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

        String url = pm.getString("SERVER_URL") + "/postItems";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("RESPONSE", response.toString());

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

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createItemPending = pm.getBoolean("createItemPending");

            if(createItemPending)
            {
                Toast.makeText(AddItemsActivity.this, "Already an item creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AddItemsActivity.this, "Internet not currently available. Item will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectItem", object.toString());
                pm.putString("urlItem", url);
                pm.putString("toastMessageItem", "Item Created");
                pm.putBoolean("createItemPending", true);

                pDialog.dismiss();
                Intent intent = new Intent(AddItemsActivity.this, AllItemsActivity.class);
                startActivity(intent);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
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

        String url = pm.getString("SERVER_URL") + "/postBoq";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("response : ", response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                String successMsg = "Assembly Item Created. ID - "+ response.getString("data");
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
        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createAssemblyItemPending = pm.getBoolean("createAssemblyItemPending");

            if(createAssemblyItemPending)
            {
                Toast.makeText(AddItemsActivity.this, "Already an Assembly item creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AddItemsActivity.this, "Internet not currently available. Assembly item will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectAssemblyItem", object.toString());
                pm.putString("urlAssemblyItem", url);
                pm.putString("toastMessageAssemblyItem", "Assembly Item Created");
                pm.putBoolean("createAssemblyItemPending", true);

                pDialog.dismiss();
                Intent intent = new Intent(AddItemsActivity.this, AllItemsActivity.class);
                startActivity(intent);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
    }


    public void main(){
        Scanner scanner = new Scanner(System. in);
        String input = scanner. nextLine();
        String[] values = input.split(",");
        int[] intValues = new int[values.length];

        for(int i=0; i<values.length; i++)
            intValues[i] = Integer.parseInt(values[i]);

        List<Integer> displayNum = null;
        displayNum = findDuplicates(intValues);

        if(displayNum.equals(null))
        {
            System.out.print("[]");
        }
        else
        {
            System.out.print("[]");
            System.out.print("[" + findDuplicates(intValues) + "]");
        }
    }

    public List<Integer> findDuplicates(int[] nums) {

        List<Integer> newNum = null;

        for(int i=0; i<nums.length; i++)
        {
            for(int j=0; j<nums.length; j++)
            {
                if(nums[i]==nums[j])
                {
                    newNum.add((nums[j]));
                }
            }
        }
        return newNum;
    }
}
