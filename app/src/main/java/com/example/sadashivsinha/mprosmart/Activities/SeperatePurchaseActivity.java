package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.Utils.FileUtils;

import java.io.File;
import java.net.URISyntaxException;

public class SeperatePurchaseActivity extends AppCompatActivity {

    Button attachmentBtn;
    int attachmentNo=0;
    int count=0;
    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seperate_purchase_activity);

        LinearLayout noLayout = (LinearLayout) findViewById(R.id.noLayout);
        noLayout.requestFocus();

        attachmentBtn = (Button) findViewById(R.id.attachmentBtn);

        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

        attachmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //for Marshmallow premissions
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP)

                {
                    if (ContextCompat.checkSelfPermission(SeperatePurchaseActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(SeperatePurchaseActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    }
//
//                        if (ContextCompat.checkSelfPermission(SeperatePurchase.this,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                != PackageManager.PERMISSION_GRANTED)
//                        {
//                            ActivityCompat.requestPermissions(SeperatePurchase.this,
//                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//                        }
                }
                showFileChooser();

            }
        });
    }

    String TAG = "SeperatePurchaseActivity";
    private static final int FILE_SELECT_CODE = 0;

    public void showFileChooser()
    {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

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
                        } else if (isDownloadsDocument(uri)) {

                            final String id = DocumentsContract.getDocumentId(uri);
                            final Uri contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                            path = getDataColumn(getApplicationContext(), contentUri, null, null);
                        }

                        // MediaProvider
                        else if (isMediaDocument(uri)) {
                            final String docId = DocumentsContract.getDocumentId(uri);
                            final String[] split = docId.split(":");
                            final String type = split[0];

                            Uri contentUri = null;
                            if ("image".equals(type)) {
                                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            } else if ("video".equals(type)) {
                                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                            } else if ("audio".equals(type)) {
                                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
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
                        }
                        // File
                        else if ("file".equalsIgnoreCase(uri.getScheme())) {
                            path = uri.getPath();
                        }
                        else
                        {
                            path = FileUtils.getPath(this, uri);
                        }

                        Log.d(TAG, "File Path: " + path);
                        // Get the file instance
                        File file = new File(path);
                        // Initiate the upload

                        if(path!=null)
                        {

                            final Button myButton = new Button(this);
                            attachmentNo++;
                            myButton.setText("Attachment "+attachmentNo);

                            //      myButton.setId();

                            LinearLayoutCompat.LayoutParams lp = new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.WRAP_CONTENT);
                            mainLayout.addView(myButton, lp);

                            final String finalPath = path;

                            myButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mainLayout.removeView(myButton);
                                    attachmentNo--;
                                    Toast.makeText(SeperatePurchaseActivity.this, "File Removed :" + finalPath, Toast.LENGTH_SHORT).show();
                                }
                            });





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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.seperate_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Toast.makeText(SeperatePurchaseActivity.this, "Values Saved", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SeperatePurchaseActivity.this, NewPurchase.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}

