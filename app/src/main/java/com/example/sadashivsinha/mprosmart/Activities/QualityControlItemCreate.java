package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.sadashivsinha.mprosmart.Utils.GetCurrentDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class QualityControlItemCreate extends AppCompatActivity {

    String currentQirId, currentReceiptNo;
    EditText quantity_received, quantity_accept, quantity_reject, text_item_desc;
    ProgressDialog pDialog, pDialog1;
    Bitmap thumbnail;
    String imageSelected;
    JSONArray dataArray;
    JSONObject dataObject;
    ImageView image_upload_one, image_upload_two, image_upload_three;
    String[] itemsArray, descArray, receivedArray, uomArray, acceptArray, rejectedArray;
    String item, uom;
    Spinner spinner_item;
    TextView text_uom;
    String UPLOAD_URL;
    String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_control_create);

        UPLOAD_URL = QualityControlItemCreate.this.getResources().getString(R.string.server_url) + "/file-upload";

        final PreferenceManager pm = new PreferenceManager(QualityControlItemCreate.this);
        currentReceiptNo = pm.getString("receiptNo");
        currentQirId = pm.getString("qirNo");

        GetCurrentDate getCurrentDate = new GetCurrentDate();
        currentDate = getCurrentDate.getDate();

        pDialog1 = new ProgressDialog(QualityControlItemCreate.this);
        pDialog1.setMessage("Preparing Lists...");
        pDialog1.setIndeterminate(false);
        pDialog1.setCancelable(true);
        pDialog1.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                prepareItemList();
//                prepareUomList();
                return null;
            }
        }

        new MyTask().execute();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(QualityControlItemCreate.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(QualityControlItemCreate.this,
                        new String[]{Manifest.permission.CAMERA}, 1);
            }
        }

        if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(QualityControlItemCreate.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }


        Button createBtn;

        image_upload_one = (ImageView) findViewById(R.id.image_upload_one);
        image_upload_two = (ImageView) findViewById(R.id.image_upload_two);
        image_upload_three = (ImageView) findViewById(R.id.image_upload_three);


        image_upload_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("one");
            }
        });

        image_upload_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("two");
            }
        });

        image_upload_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("three");
            }
        });

        text_item_desc = (EditText) findViewById(R.id.text_item_desc);
        quantity_received = (EditText) findViewById(R.id.quantity_received);
        quantity_accept = (EditText) findViewById(R.id.quantity_accept);
        quantity_reject = (EditText) findViewById(R.id.quantity_reject);

        quantity_accept.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    int quantityTotalVal = Integer.parseInt(String.valueOf(quantity_received.getText().toString()));
                    int quantityAcceptVal = Integer.parseInt(String.valueOf(quantity_accept.getText().toString()));
                    int quantityRejectVal = Integer.parseInt(String.valueOf(quantity_reject.getText().toString()));

                    if(quantityAcceptVal>quantityTotalVal)
                    {
                        quantity_accept.setError("Value greater than received quantity.");
                    }

                    else if(quantityRejectVal>quantityTotalVal)
                    {
                        quantity_reject.setError("Value greater than received quantity.");
                    }

                    else {
                        quantityRejectVal = quantityTotalVal - quantityAcceptVal;
                        quantity_reject.setText(String.valueOf(quantityRejectVal));
                    }
                }
                return false;
            }
        });

        spinner_item = (Spinner) findViewById(R.id.spinner_item);
        text_uom = (TextView) findViewById(R.id.text_uom);

        spinner_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                quantity_received.setText(receivedArray[position]);
                quantity_accept.setText(acceptArray[position]);
                quantity_reject.setText(rejectedArray[position]);
                text_uom.setText(uomArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createBtn = (Button) findViewById(R.id.createBtn);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(QualityControlItemCreate.this);
                pDialog.setMessage("Sending Data ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                class MyTask extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... params) {
                        prepareItems();
                        return null;
                    }
                }

                new MyTask().execute();
            }
        });
    }

    public void prepareItems() {
        JSONObject object = new JSONObject();

        try {
            object.put("qualityInspectionId", currentQirId);
            object.put("itemId", spinner_item.getSelectedItem().toString());
            object.put("quantityReceived", quantity_received.getText().toString());
            object.put("itemDescription", text_item_desc.getText().toString());
            object.put("quantityAccepted", quantity_accept.getText().toString());
            object.put("quantityRejected", quantity_reject.getText().toString());
            object.put("uomId", text_uom.getText().toString());
            object.put("url", "");
            object.put("noOfAttachments", 0);
            object.put("createdDate", currentDate);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(QualityControlItemCreate.this);

        String url = QualityControlItemCreate.this.getResources().getString(R.string.server_url) + "/postQualityInspectionLineItems";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(QualityControlItemCreate.this, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();


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
        pDialog.dismiss();
        Intent intent = new Intent(QualityControlItemCreate.this, QualityControl.class);
        startActivity(intent);
    }

    public void selectImage(final String imageSelectedText) {
        final CharSequence[] options = {"Take Photo from Camera", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(QualityControlItemCreate.this);
        builder.setTitle("Add Image Attachment");
        imageSelected = imageSelectedText;

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo from Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    public void upload(String url, File file) throws IOException {
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("text/plain"), file))
                .addFormDataPart("other_field", "other_field_value")
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).post(formBody).build();

        OkHttpClient client = new OkHttpClient();
        okhttp3.Response response = client.newCall(request).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    thumbnail = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    if(imageSelected.equals("one"))
                    {
                        image_upload_one.setImageBitmap(thumbnail);
                    }
                    else if(imageSelected.equals("two"))
                    {
                        image_upload_two.setImageBitmap(thumbnail);
                    }
                    else if(imageSelected.equals("three"))
                    {
                        image_upload_three.setImageBitmap(thumbnail);
                    }

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        thumbnail.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();


                        upload(UPLOAD_URL,file);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("path of image", picturePath+"");


                try {
                    upload(UPLOAD_URL, new File(picturePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(imageSelected.equals("one"))
                {
                    image_upload_one.setImageBitmap(thumbnail);
                }
                else if(imageSelected.equals("two"))
                {
                    image_upload_two.setImageBitmap(thumbnail);
                }
                else if(imageSelected.equals("three"))
                {
                    image_upload_three.setImageBitmap(thumbnail);
                }
            }
        }
    }

    public void prepareItemList()
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getPurchaseReceiptItems?purchaseReceiptId=\""+currentReceiptNo+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(QualityControlItemCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                itemsArray = new String[dataArray.length()];
                                descArray = new String[dataArray.length()];
                                receivedArray = new String[dataArray.length()];
                                uomArray = new String[dataArray.length()];
                                acceptArray = new String[dataArray.length()];
                                rejectedArray = new String[dataArray.length()];

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    item = dataObject.getString("itemId");
                                    itemsArray[i]=item;
//                                    descArray[i] = dataObject.getString("");
                                    receivedArray[i] = dataObject.getString("maxQuantity");
                                    uomArray[i] = dataObject.getString("uom");
                                    acceptArray[i] = dataObject.getString("quantity");
                                    rejectedArray[i] = dataObject.getString("balance");
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(QualityControlItemCreate.this,
                                        android.R.layout.simple_dropdown_item_1line,itemsArray);
                                spinner_item.setAdapter(adapter);
                            }
                            pDialog1.dismiss();

                        }catch(JSONException e){e.printStackTrace();
                            pDialog1.dismiss();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");
                        pDialog1.dismiss();

                    }
                }
        );
        requestQueue.add(jor);
    }
//    public void prepareUomList()
//    {
//
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
//
//                            String type = response.getString("type");
//
//                            if(type.equals("ERROR"))
//                            {
//                                Toast.makeText(QualityControlItemCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
//                            }
//
//                            if(type.equals("INFO"))
//                            {
//                                dataArray = response.getJSONArray("data");
//                                uomArray = new String[dataArray.length()];
//                                for(int i=0; i<dataArray.length();i++)
//                                {
//                                    dataObject = dataArray.getJSONObject(i);
//                                    uom = dataObject.getString("uomCode");
//                                    uomArray[i]=uom;
//                                }
//                            }
//                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(QualityControlItemCreate.this,
//                                    android.R.layout.simple_dropdown_item_1line,uomArray);
//                            spinnerUom.setAdapter(adapter);
//                            pDialog1.dismiss();
//
//                        }catch(JSONException e){e.printStackTrace();
//                            pDialog1.dismiss();}
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        pDialog1.dismiss();
//                        Log.e("Volley","Error");
//
//                    }
//                }
//        );
//        requestQueue.add(jor);
//    }
}
