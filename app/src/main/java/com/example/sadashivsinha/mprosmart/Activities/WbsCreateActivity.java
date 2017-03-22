package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class WbsCreateActivity extends AppCompatActivity {

    ProgressDialog pDialog1, dialog;
    HelveticaBold btn_upload;
    String image_url_link;

    public static final String TAG = WbsCreateActivity.class.getSimpleName();

    private static final int PICK_FILE_REQUEST = 1;
    private String selectedFilePath;
    PreferenceManager pm;
    ProgressDialog pDialog;
    String currentProjectNo, current_user_id;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    EditText wbs_name, wbs_desc, text_budget;
    TextView text_currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wbs_create);

        pm = new PreferenceManager(getApplicationContext());

        currentProjectNo = pm.getString("projectId");
        current_user_id = pm.getString("userId");

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        wbs_name = (EditText) findViewById(R.id.wbs_name);
        wbs_desc = (EditText) findViewById(R.id.wbs_desc);
        text_budget = (EditText) findViewById(R.id.text_budget);
        text_currency = (TextView) findViewById(R.id.text_currency);

        text_currency.setText(pm.getString("currency"));

        Button createBtn = (Button) findViewById(R.id.createBtn);

        btn_upload = (HelveticaBold) findViewById(R.id.btn_file_upload);


        if (!isInternetPresent) {

            btn_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //image upload
                    Toast.makeText(WbsCreateActivity.this, "Attachments cannot be added due to NO INTERNET CONNECTION",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            btn_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //image upload

                    Intent intent = new Intent(WbsCreateActivity.this, AttachmentActivity.class);
                    intent.putExtra("class", "WBS");
                    startActivity(intent);
                }
            });
        }

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wbs_name.getText().toString().isEmpty())
                {
                    wbs_name.setError("Field cannot be empty");
                }
                else if(wbs_desc.getText().toString().isEmpty())
                {
                    wbs_desc.setError("Field cannot be empty");
                }
                else if(text_budget.getText().toString().isEmpty())
                {
                    text_budget.setError("Field cannot be empty");
                }
                else
                {
                    final String wbsName = wbs_name.getText().toString();
                    final String wbsDesc = wbs_desc.getText().toString();
                    final String currencyCode = text_currency.getText().toString();
                    final String totalBudget = text_budget.getText().toString();

                    pDialog1 = new ProgressDialog(WbsCreateActivity.this);
                    pDialog1.setMessage("Sending Data ...");
                    pDialog1.setIndeterminate(false);
                    pDialog1.setCancelable(true);
                    pDialog1.show();


                    saveData(wbsName, wbsDesc,currencyCode, totalBudget );
                }
            }
        });
    }

    public void saveData(String wbs_name, String wbs_desc, String currency_code, final String total_budget) {
        JSONObject object = new JSONObject();

        try {
            object.put("wbsName", wbs_name);
            object.put("projectId", currentProjectNo);
            object.put("wbsTittle", wbs_desc);
            object.put("createdBy", current_user_id);
            object.put("currencyCode", currency_code);
            object.put("totalBudget", total_budget);
            object.put("progress", "0");

            Log.d("WBS OBJECT", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(WbsCreateActivity.this);

        String url = WbsCreateActivity.this.pm.getString("SERVER_URL") + "/postWbs";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("WBS RESPONSE", response.toString());

                            if (response.getString("msg").equals("success")) {

                                PreferenceManager pm = new PreferenceManager(getApplicationContext());
                                pm.putString("wbsId", response.getString("data"));

                                String totalImageUrls = pm.getString("totalImageUrls");
                                if (pm.getString("className").equals("WBS") && isInternetPresent
                                        && !totalImageUrls.isEmpty()) {
                                    uploadImage(response.getString("data"), totalImageUrls, response.getString("data"), total_budget);
                                } else {
                                    if (pDialog != null)
                                        pDialog.dismiss();

                                    updateProjectBudget(Float.parseFloat(total_budget));
                                    /*Toast.makeText(WbsCreateActivity.this, "WBS Created. ID - '" + response.getString("data"), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(WbsCreateActivity.this, WbsActivity.class);
                                    startActivity(intent);*/
                                }
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
                    }
                }
        );

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createWbsPending = pm.getBoolean("createWbsPending");

            if(createWbsPending)
            {
                Toast.makeText(WbsCreateActivity.this, "Already a Wbs creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(WbsCreateActivity.this, "Internet not currently available. Wbs will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectWbs", object.toString());
                pm.putString("urlWbs", url);
                pm.putString("toastMessageWbs", "Wbs Created");
                pm.putBoolean("createWbsPending", true);

                if(pDialog1!=null)
                    pDialog1.dismiss();

                Intent intent = new Intent(WbsCreateActivity.this, WbsActivity.class);
                startActivity(intent);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
    }
    public void uploadImage(final String id, String totalUrls, final String wbsId, final String totalBudget) {

        final List<String> imageList = Arrays.asList(totalUrls.split(","));
        String seperateImageUrl;


        for (int i = 0; i < imageList.size(); i++) {

            final int count = i;
            JSONObject object = new JSONObject();

            try {

                seperateImageUrl = imageList.get(i);

                object.put("wbsId", id);
                object.put("url", seperateImageUrl);

                Log.d("JSON OBJ SENT", object.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }


        RequestQueue requestQueue = Volley.newRequestQueue(WbsCreateActivity.this);

        String url = WbsCreateActivity.this.pm.getString("SERVER_URL") + "/postWbsFiles";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("RESPONSE SERVER : ", response.toString());

                            if (response.getString("msg").equals("success")) {

                                if(count==imageList.size()-1)
                                {
                                    Toast.makeText(WbsCreateActivity.this, "WBS Line created ID - " + id, Toast.LENGTH_SHORT).show();

                                    if(pDialog!=null)
                                        pDialog.dismiss();
                                    Intent intent = new Intent(WbsCreateActivity.this, WbsActivity.class);
                                    startActivity(intent);
                                }
                            }

                            updateProjectBudget(Float.parseFloat(totalBudget));

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
                        Toast.makeText(WbsCreateActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jor);
    }
    }

    public void uploadImageToServer(String wbsId, String imageURL, final String total_budget) {

        JSONObject object = new JSONObject();

        try {
            object.put("wbsId", wbsId);
            object.put("url", imageURL);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(WbsCreateActivity.this);

        String url = WbsCreateActivity.this.pm.getString("SERVER_URL") + "/postWbsFiles";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("msg").equals("success")) {
                                updateProjectBudget(Double.parseDouble(total_budget));
                            } else {
                                Toast.makeText(WbsCreateActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
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
                    }
                }
        );
        requestQueue.add(jor);

        if(pDialog1!=null)
            pDialog1.dismiss();
    }

    public void updateProjectBudget(double wbsBudget)
    {
        Log.d("WBS BUDGET", String.valueOf(wbsBudget));

        BigDecimal projectBudget =
                new BigDecimal(pm.getString("budget"));
        Log.d("PRO BUDGET", String.valueOf(projectBudget));

        BigDecimal newBudget = projectBudget.add(BigDecimal.valueOf(wbsBudget));
        Log.d("NEW BUDGET", String.valueOf(newBudget));

        DecimalFormat df = new DecimalFormat("#.##########");

        final String newBudgetDec = df.format(newBudget);
        Log.d("NEW BUDGET FORMAT", String.valueOf(newBudgetDec));

        JSONObject object = new JSONObject();

        try {
            object.put("totalBudget", String.valueOf(newBudget));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("NEW BUDGET OBJ", object.toString());

        RequestQueue requestQueue = Volley.newRequestQueue(WbsCreateActivity.this);

        String url = WbsCreateActivity.this.pm.getString("SERVER_URL") + "/putProjectTotalBudget?projectId=\"" + currentProjectNo + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("RESP BUDGET PRO", response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                if (pDialog!=null)
                                    pDialog.dismiss();
                                Intent intent = new Intent(WbsCreateActivity.this, ActivityCreate.class);

                                Log.d("NEW BUGET LOC", newBudgetDec);
                                pm.putString("budget", newBudgetDec);
                                startActivity(intent);
                            }
                            else
                            {
                                if (pDialog!=null)
                                    pDialog.dismiss();
                                Toast.makeText(WbsCreateActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            if (pDialog!=null)
                                pDialog.dismiss();
                            e.printStackTrace();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(jor);

        if(pDialog!=null)
            pDialog.dismiss();
    }


//    public void chooseAndUploadImage()
//    {
//
//        Intent intent = new Intent(WbsCreateActivity.this, AttachmentActivity.class);
//        startActivity(intent);
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case 8:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    int currentapiVersion = Build.VERSION.SDK_INT;
//
//                    if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
//                        if (ContextCompat.checkSelfPermission(WbsCreateActivity.this,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                != PackageManager.PERMISSION_GRANTED) {
//
//                            ActivityCompat.requestPermissions(WbsCreateActivity.this,
//                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 9);
//                        }
//                    }
//
//                }
//                else
//                {
//                    int currentapiVersion = Build.VERSION.SDK_INT;
//
//                    if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
//                        if (ContextCompat.checkSelfPermission(WbsCreateActivity.this,
//                                Manifest.permission.READ_EXTERNAL_STORAGE)
//                                != PackageManager.PERMISSION_GRANTED) {
//
//                            ActivityCompat.requestPermissions(WbsCreateActivity.this,
//                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 8);
//                        }
//                    }
//                }
//            case 9:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    showFileChooser();
//                }
//
//                else
//                {
//                    int currentapiVersion = Build.VERSION.SDK_INT;
//                    if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
//                        if (ContextCompat.checkSelfPermission(WbsCreateActivity.this,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                != PackageManager.PERMISSION_GRANTED) {
//
//                            ActivityCompat.requestPermissions(WbsCreateActivity.this,
//                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
//                        }
//                    }
//                }
//        }
//    }

//    public void showFileChooser()
//    {
//        Intent intent = new Intent();
//        //sets the select file to all types of files
//        intent.setType("*/*");
//        //allows to select data and return it
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        //starts new activity to select file and return data
//        startActivityForResult(Intent.createChooser(intent,"Choose File to Upload.."),PICK_FILE_REQUEST);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == Activity.RESULT_OK){
//            if(requestCode == PICK_FILE_REQUEST){
//                if(data == null){
//                    //no data present
//                    return;
//                }
//
//                Uri selectedFileUri = data.getData();
//                selectedFilePath = FilePath.getPath(this,selectedFileUri);
//                Log.i(TAG,"Selected File Path:" + selectedFilePath);
//
//
//                if(selectedFilePath != null){
//                    dialog = ProgressDialog.show(WbsCreateActivity.this,"","Uploading File...",true);
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //creating new thread to handle Http Operations
//                            uploadFile(selectedFilePath);
//                        }
//                    }).start();
//                }else{
//                    Toast.makeText(WbsCreateActivity.this,"Please choose a File First",Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }

    //android upload file to server
//    public int uploadFile(final String selectedFilePath){
//
//        int serverResponseCode = 0;
//
//        HttpURLConnection connection;
//        DataOutputStream dataOutputStream;
//        String lineEnd = "\r\n";
//        String twoHyphens = "--";
//        String boundary = "*****";
//
//
//        int bytesRead,bytesAvailable,bufferSize;
//        byte[] buffer;
//        int maxBufferSize = 1 * 1024 * 1024;
//        File selectedFile = new File(selectedFilePath);
//
//
//        String[] parts = selectedFilePath.split("/");
//        final String fileName = parts[parts.length-1];
//
//        if (!selectedFile.isFile()){
//            dialog.dismiss();
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    btn_upload.setText("Source File Doesn't Exist");
//                }
//            });
//            return 0;
//        }else{
//            try{
//                FileInputStream fileInputStream = new FileInputStream(selectedFile);
//                URL url = new URL(SERVER_URL);
//                connection = (HttpURLConnection) url.openConnection();
//                connection.setDoInput(true);//Allow Inputs
//                connection.setDoOutput(true);//Allow Outputs
//                connection.setUseCaches(false);//Don't use a cached Copy
//                connection.setRequestMethod("POST");
//                connection.setRequestProperty("Connection", "Keep-Alive");
//                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
//                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//                connection.setRequestProperty("uploaded_file",selectedFilePath);
//
//                //creating new dataoutputstream
//                dataOutputStream = new DataOutputStream(connection.getOutputStream());
//
//                //writing bytes to data outputstream
//                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
//                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
//                        + selectedFilePath + "\"" + lineEnd);
//
//                dataOutputStream.writeBytes(lineEnd);
//
//                //returns no. of bytes present in fileInputStream
//                bytesAvailable = fileInputStream.available();
//                //selecting the buffer size as minimum of available bytes or 1 MB
//                bufferSize = Math.min(bytesAvailable,maxBufferSize);
//                //setting the buffer as byte array of size of bufferSize
//                buffer = new byte[bufferSize];
//
//                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
//                bytesRead = fileInputStream.read(buffer,0,bufferSize);
//
//                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
//                while (bytesRead > 0){
//                    //write the bytes read from inputstream
//                    dataOutputStream.write(buffer,0,bufferSize);
//                    bytesAvailable = fileInputStream.available();
//                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
//                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
//                }
//
//                dataOutputStream.writeBytes(lineEnd);
//                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//                final String response = connection.getResponseMessage();
//
//                serverResponseCode = connection.getResponseCode();
//                String serverResponseMessage = connection.getResponseMessage();
//
//                Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + response);
//
//                BufferedReader br;
//                if(response.equals("OK"))
//                {
//                    br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
//                }
//                else
//                {
//                    br = new BufferedReader(new InputStreamReader((connection.getErrorStream())));
//                }
//
//                StringBuilder total = new StringBuilder();
//                String line;
//                while ((line = br.readLine()) != null) {
//                    total.append(line).append('\n');
//                }
//
//                JSONObject jsonObj = new JSONObject(total.toString());
//
//                Log.d("SERVER IMAGE JSON", String.valueOf(jsonObj));
//
//                JSONArray jsonArray = jsonObj.getJSONArray("data");
//
//                image_url_link = jsonArray.getString(0);
//
//                Log.d("SERVER IMAGE URL", image_url_link);
//
//                //response code of 200 indicates the server status OK
//                if(serverResponseCode == 200){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            btn_upload.setText("File Uploaded...");
//                        }
//                    });
//                }
//
//                //closing the input and output streams
//                fileInputStream.close();
//                dataOutputStream.flush();
//                dataOutputStream.close();
//
//
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(WbsCreateActivity.this,"File Not Found",Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//                Toast.makeText(WbsCreateActivity.this, "URL error!", Toast.LENGTH_SHORT).show();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(WbsCreateActivity.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            dialog.dismiss();
//            return serverResponseCode;
//        }
//       }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WbsCreateActivity.this, WbsActivity.class);
        startActivity(intent);
    }
}