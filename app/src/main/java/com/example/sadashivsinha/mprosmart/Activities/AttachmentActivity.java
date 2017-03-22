package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.sadashivsinha.mprosmart.ModelLists.AttachmentList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttachmentActivity extends AppCompatActivity {


    AttachmentList items;
    List<AttachmentList> attachmentList = new ArrayList<>();
    AttachmentAdapter attachmentAdapter = new AttachmentAdapter(attachmentList);
    TextView addMoreBtn, attachBtn;
    RecyclerView attachRecyclerView;
    String image_url_link, totalImageUrls;
    PreferenceManager pm;
    String selectedFilePath;
    ProgressDialog dialog;
    JSONObject dataObject;
    JSONArray dataArray;
    String className, SERVER_URL;
    ImageView full_screen_image;
    String contentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment);

        full_screen_image = (ImageView) findViewById(R.id.full_screen_image);

        full_screen_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                full_screen_image.setVisibility(View.GONE);
            }
        });

        if(getIntent().hasExtra("class"))
            className = getIntent().getStringExtra("class");


        pm = new PreferenceManager(getApplicationContext());

        SERVER_URL = pm.getString("SERVER_UPLOAD_URL") + "/upload/file-upload";

        Log.d("SERVER_URL", SERVER_URL);

        attachRecyclerView = (RecyclerView) findViewById(R.id.attachmentRecycler);
        attachRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        attachRecyclerView.setHasFixedSize(true);
        attachRecyclerView.setAdapter(attachmentAdapter);

        addMoreBtn = (TextView) findViewById(R.id.addMoreBtn);
        attachBtn = (TextView) findViewById(R.id.attachBtn);

        LinearLayout hideOnViewLayout = (LinearLayout) findViewById(R.id.hideOnViewLayout);

        if(getIntent().hasExtra("viewOnly"))
        {
            if(getIntent().getBooleanExtra("viewOnly", true))
            {
                hideOnViewLayout.setVisibility(View.GONE);
                viewImages();
            }
        }
        else
        {

            attachBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AttachmentActivity.this, "Files Attached", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            addMoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(attachmentList.size()==5)
                    {
                        Toast.makeText(AttachmentActivity.this, "Maximum of 5 Files can be attached", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        //for Marshmallow permission
                        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                        if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP)
                        {
                            if (ContextCompat.checkSelfPermission(AttachmentActivity.this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED)
                            {

                                ActivityCompat.requestPermissions(AttachmentActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

                            }
                        }


                        showFileChooser();
                    }
                }
            });
        }

    }

    String TAG = "PurchaseEntry";
    private static final int FILE_SELECT_CODE = 0;

    public void showFileChooser()
    {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try
        {
            startActivityForResult(
                    Intent.createChooser(intent, "Select File to Upload"), FILE_SELECT_CODE);
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(this, "Please Install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                try {
                    if (resultCode == RESULT_OK) {
                        // Get the Uri of the selected file
                        Uri uri = data.getData();
                        Log.d(TAG, "File Uri: " + uri.toString());
                        // Get the path
                        String path = null;

                        if (isExternalStorageDocument(uri)) {
                            final String docId = DocumentsContract.getDocumentId(uri);
                            final String[] split = docId.split(":");
                            final String type = split[0];

                            if ("primary".equalsIgnoreCase(type)) {
                                path = Environment.getExternalStorageDirectory() + "/" + split[1];
                            }

                            Log.d("File TYPE :", "DOC");

                        } else if (isDownloadsDocument(uri)) {

                            final String id = DocumentsContract.getDocumentId(uri);
                            final Uri contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                            path = getDataColumn(getApplicationContext(), contentUri, null, null);

                            Log.d("File TYPE :", "DOWNLOADS");
                        }

                        // MediaProvider
                        else if (isMediaDocument(uri)) {
                            final String docId = DocumentsContract.getDocumentId(uri);
                            final String[] split = docId.split(":");
                            final String type = split[0];

                            Uri contentUri = null;
                            if ("image".equals(type)) {
                                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                Log.d("File TYPE :", "MEDIA IMG");
                            } else if ("video".equals(type)) {
                                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                                Log.d("File TYPE :", "MEDIA VID");
                            } else if ("audio".equals(type)) {
                                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                                Log.d("File TYPE :", "MEDIA AUD");
                            }

                            final String selection = "_id=?";
                            final String[] selectionArgs = new String[]{
                                    split[1]
                            };

                            path = getDataColumn(getApplicationContext(), contentUri, selection, selectionArgs);
                        }

                        // MediaStore (and general)
                        else if ("content".equalsIgnoreCase(uri.getScheme())) {
                            path = getDataColumn(getApplicationContext(), uri, null, null);
                            Log.d("File TYPE :", "CONTENT");

                            if(path==null)
                            {

                                File myFile = new File(uri.toString());
                                path = myFile.getAbsolutePath();
                            }
                        }
                        // File
                        else if ("file".equalsIgnoreCase(uri.getScheme())) {
                            path = uri.getPath();
                            Log.d("File TYPE :", "FILE");
                        }
                        else
                        {
                            path = FileUtils.getPath(this, uri);
                            Log.d("File TYPE :", "OTHER");
                        }

                        Log.d(TAG, "File Path: " + path);
                        // Get the file instance
                        File file = new File(path);
                        // Initiate the upload


                        File f = new File(path);
                        String fileName = f.getName();
                        Drawable imageDrawable = null;

                        if(path.contains("jpg") || path.contains("png") || path.contains("gif") || path.contains("jpeg")
                                || path.contains("pdf") || path.contains("doc")|| path.contains("docx") || path.contains("pptx")
                                || path.contains("ppt") || path.contains("xls") || path.contains("xlsx"))

                        {
                            if(path.contains("jpg") || path.contains("png") || path.contains("gif") || path.contains("jpeg"))
                            {
                                imageDrawable = Drawable.createFromPath(path);
                                contentType = "image/png";
                            }
                            else
                            {
                                imageDrawable = getResources().getDrawable(R.drawable.file_icon);

                                String fileType = path.substring(path.lastIndexOf(".") + 1).trim();

                                switch (fileType) {
                                    case "pdf":
                                        contentType = "application/pdf";
                                        break;
                                    case "doc":
                                        contentType = "application/msword";
                                        break;
                                    case "docx":
                                        contentType = "application/msword";
                                        break;
                                    case "ppt":
                                        contentType = "application/vnd.ms-powerpoint";
                                        break;
                                    case "pptx":
                                        contentType = "application/vnd.ms-powerpoint";
                                        break;
                                    case "xls":
                                        contentType = "application/vnd.ms-excel";
                                        break;
                                    case "xlsx":
                                        contentType = "application/vnd.ms-excel";
                                        break;
                                }
                            }

                            items = new AttachmentList(false, fileName,imageDrawable);
                            attachmentList.add(items);

                            attachmentAdapter.notifyDataSetChanged();

                            selectedFilePath = path;

                            dialog = ProgressDialog.show(AttachmentActivity.this,"","Uploading File...",true);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //creating new thread to handle Http Operations

                                    uploadFile(contentType, selectedFilePath);
                                }
                            }).start();

                        }

                        else {
                            Toast.makeText(this, "Accepted File types : doc, docx, pdf, ppt, pptx, xls, xlsx, and images ONLY", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                catch (URISyntaxException e)
                {
                    e.printStackTrace();
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static boolean isExternalStorageDocument(Uri uri)
    {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.MyViewHolder> {

        public List<AttachmentList> modelList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView file_name;
            public ImageView file_preview, view_image;
            private LinearLayout add_layout, view_layout;
//            public FButton file_remove_btn;

            public MyViewHolder(final View view) {
                super(view);
                file_name = (TextView) view.findViewById(R.id.file_name);
                file_preview = (ImageView) view.findViewById(R.id.file_preview);

                view_image = (ImageView) view.findViewById(R.id.view_image);

                add_layout = (LinearLayout) view.findViewById(R.id.add_layout);
                view_layout = (LinearLayout) view.findViewById(R.id.view_layout);
            }
        }

        public AttachmentAdapter(List<AttachmentList> modelList) {
            this.modelList = modelList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_attachment, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final AttachmentList items = modelList.get(position);
            holder.file_name.setText(String.valueOf(items.getFile_name()));

            if(items.getViewOnly())
            {
                    if(items.getImageUrl().contains(".png"))
                    {
                        Glide.with(holder.itemView.getContext()).load(items.getImageUrl()).crossFade().into(holder.view_image);

                        holder.view_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Glide.with(holder.itemView.getContext()).load(items.getImageUrl()).crossFade().into(full_screen_image);
                                full_screen_image.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    else
                    {
                        holder.view_image.setImageResource(R.drawable.file_icon);

                        holder.view_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(items.getImageUrl()));
                                startActivity(i);
                            }
                        });
                    }
            }
            else
            {
                holder.file_preview.setBackground(items.getFile_preview());
                holder.add_layout.setVisibility(View.VISIBLE);
                holder.view_layout.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

    }

    public int uploadFile(final String content_type, final String selectedFilePath){

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length-1];

        if (!selectedFile.isFile()){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AttachmentActivity.this, "Source File Doesn't Exist", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            return 0;
        }else{
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);
                dataOutputStream.writeBytes("Content-Type: "+ content_type + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer,0,bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0)
                {
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                final String response = connection.getResponseMessage();

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + response);

                BufferedReader br;
                if(response.equals("OK"))
                {
                    br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                }
                else
                {
                    br = new BufferedReader(new InputStreamReader((connection.getErrorStream())));
                }

                StringBuilder total = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    total.append(line).append('\n');
                }

                JSONObject jsonObj = new JSONObject(total.toString());

                Log.d("SERVER IMAGE JSON", String.valueOf(jsonObj));

                JSONArray jsonArray = jsonObj.getJSONArray("data");

                image_url_link = jsonArray.getString(0);

                Log.d("SERVER IMAGE URL", image_url_link);

                //response code of 200 indicates the server status OK
                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AttachmentActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            if(totalImageUrls==null)
                            {
                                totalImageUrls = image_url_link;
                            }
                            else
                            {
                                totalImageUrls = totalImageUrls + "," + image_url_link;
                            }

                            List<String> imageList = Arrays.asList(totalImageUrls.split(","));

                            pm.putString("totalImageUrls" , totalImageUrls);
                            pm.putString("totalImageUrlSize" , String.valueOf(imageList.size()));
                            pm.putString("className", String.valueOf(className));

                            Log.d("IMAGE URLS", totalImageUrls);
                        }
                    });
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();



            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AttachmentActivity.this,"File Not Found",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(AttachmentActivity.this, "URL error!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(AttachmentActivity.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
            }
            return serverResponseCode;
        }
    }

    private void viewImages()
    {
        final ProgressDialog pDialog = new ProgressDialog(AttachmentActivity.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        String url = getIntent().getStringExtra("viewURL");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            String type = response.getString("type");

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length(); i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                        String imageUrl = dataObject.getString("url");
                                        items = new AttachmentList(true, "",imageUrl);
                                        attachmentList.add(items);

                                        attachmentAdapter.notifyDataSetChanged();
                                        Log.d("Image URLS ,", imageUrl);

                                }

                                if(items==null)
                                {
                                    Toast.makeText(AttachmentActivity.this, "No Attachments", Toast.LENGTH_SHORT).show();
                                }
                                pDialog.dismiss();
                            }

                            else
                            {
                                Toast.makeText(AttachmentActivity.this, "There are no attachments", Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
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
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
