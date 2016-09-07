package com.example.sadashivsinha.mprosmart.Utils;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by saDashiv sinha on 31-Aug-16.
 */
public class GetDataFromServer {

    JSONArray dataArray;
    JSONObject dataObject;
    String[] uomNameArray, uomArray;

    public void GetDataFromServer(final Activity activity, final Spinner spinnerUom)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(activity.getApplicationContext());

        String url = activity.getString(R.string.server_url) + "/getUom";
        Log.d("Get datafromserver", "reached");

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(activity, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                            if(type.equals("INFO"))
                            {
                                Log.d("Get datafromserver", "INFO");

                                dataArray = response.getJSONArray("data");
                                uomArray = new String[dataArray.length()+1];
                                uomNameArray = new String[dataArray.length()+1];

                                uomArray[0]="Select UOM";
                                uomNameArray[0]="Select UOM";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    uomArray[i+1] = dataObject.getString("uomCode");
                                    uomNameArray[i+1] = dataObject.getString("uomName");

                                }

                                Log.d("Get datafromserver uom", Arrays.toString(uomArray));
                                Log.d("Get datafromserver uomN", Arrays.toString(uomNameArray));

                                ArrayAdapter<String> adapterCurrency = new ArrayAdapter<String>(activity,
                                        android.R.layout.simple_dropdown_item_1line,uomNameArray);

                                spinnerUom.setAdapter(adapterCurrency);
                                setUomArray(uomArray);
                            }
                        }
                        catch(JSONException e){
                            e.printStackTrace();}
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

    public void setUomArray(String[] array)
    {
        this.uomArray = array;
        Log.d("Get datafromserver set", Arrays.toString(uomArray));
    }

    public String[] getUomArray()
    {
        Log.d("Get datafromserver get", Arrays.toString(uomArray));
        return uomArray;
    }
}
