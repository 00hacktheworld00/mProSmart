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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.Utils.FileUtils;

import java.io.File;
import java.net.URISyntaxException;

public class SeperatePurchase extends AppCompatActivity {

    Button attachmentBtn;
    LinearLayout mainLayout;
    ScrollView scrollView;
    int attachmentNo=0;
    int count=0;

    EditText itemId, poQuantity, uom, uom2, uom3, uom4, balanceQuantity, quantityReceived,
            quantityReceived2, invoiceNumber;

    TextInputLayout itemIdLayout, poQuantityLayout, uomLayout, uomLayout2, uomLayout3,
            uomLayout4, balanceQuantityLayout, quantityReceivedLayout, quantityReceivedLayout2, invoiceNumberLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seperate_purchase);

        attachmentBtn = (Button) findViewById(R.id.attachmentBtn);

        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

        scrollView = (ScrollView) findViewById(R.id.scrollView);


        itemIdLayout = (TextInputLayout) findViewById(R.id.itemIdLayout);
        poQuantityLayout = (TextInputLayout) findViewById(R.id.poQuantityLayout);
        uomLayout = (TextInputLayout) findViewById(R.id.uomLayout);
        uomLayout2 = (TextInputLayout) findViewById(R.id.uomLayout2);
        uomLayout3 = (TextInputLayout) findViewById(R.id.uomLayout3);
        uomLayout4 = (TextInputLayout) findViewById(R.id.uomLayout4);
        balanceQuantityLayout = (TextInputLayout) findViewById(R.id.balanceQuantityLayout);
        quantityReceivedLayout = (TextInputLayout) findViewById(R.id.quantityReceivedLayout);
        quantityReceivedLayout2 = (TextInputLayout) findViewById(R.id.quantityReceivedLayout2);
        invoiceNumberLayout = (TextInputLayout) findViewById(R.id.invoiceNumberLayout);




        itemId = (EditText) findViewById(R.id.itemId);
        poQuantity = (EditText) findViewById(R.id.poQuantity);
        uom = (EditText) findViewById(R.id.uom);
        uom2 = (EditText) findViewById(R.id.uom2);
        uom3 = (EditText) findViewById(R.id.uom3);
        uom4 = (EditText) findViewById(R.id.uom4);
        balanceQuantity = (EditText) findViewById(R.id.balanceQuantity);
        quantityReceived = (EditText) findViewById(R.id.quantityReceived);
        quantityReceived2 = (EditText) findViewById(R.id.quantityReceived2);
        invoiceNumber = (EditText) findViewById(R.id.invoiceNumber);


        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count = validate();

                //start activity if all validations are done and corrected
                if(count==0)
                {
                    Toast.makeText(SeperatePurchase.this, "Values Saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SeperatePurchase.this, NewPurchase.class);
                    startActivity(intent);
                }
            }
        });

        attachmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //for Marshmallow premissions
                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP)

                    {
                        if (ContextCompat.checkSelfPermission(SeperatePurchase.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(SeperatePurchase.this,
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


    String TAG = "SeperatePurchase";
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


                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                            final String finalPath = path;

                            myButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mainLayout.removeView(myButton);
                                    attachmentNo--;
                                    Toast.makeText(SeperatePurchase.this, "File Removed :" + finalPath, Toast.LENGTH_SHORT).show();
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


    public int validate()
    {
        count=0;

        if(itemId.getText().toString().isEmpty())
        {
            validateInputs(itemIdLayout);
            count=1;
        }
        else
        {
            removeError(itemIdLayout);
        }

        if(poQuantity.getText().toString().isEmpty())
        {
            validateInputs(poQuantityLayout);
            count=1;
        }
        else
        {
            removeError(poQuantityLayout);
        }

        if(uom.getText().toString().isEmpty())
        {
            validateInputs(uomLayout);
            count=1;
        }
        else
        {
            removeError(uomLayout);
        }

        if(uom2.getText().toString().isEmpty())
        {
            validateInputs(uomLayout2);
            count=1;
        }
        else
        {
            removeError(uomLayout2);
        }

        if(uom3.getText().toString().isEmpty())
        {
            validateInputs(uomLayout3);
            count=1;
        }
        else
        {
            removeError(uomLayout3);
        }

        if(uom4.getText().toString().isEmpty())
        {
            validateInputs(uomLayout4);
            count=1;
        }
        else
        {
            removeError(uomLayout4);
        }

        if(balanceQuantity.getText().toString().isEmpty())
        {
            validateInputs(balanceQuantityLayout);
            count=1;
        }
        else
        {
            removeError(balanceQuantityLayout);
        }

        if(quantityReceived.getText().toString().isEmpty())
        {
            validateInputs(quantityReceivedLayout);
            count=1;
        }
        else
        {
            removeError(quantityReceivedLayout);
        }

        if(quantityReceived2.getText().toString().isEmpty())
        {
            validateInputs(quantityReceivedLayout2);
            count=1;
        }
        else
        {
            removeError(quantityReceivedLayout2);
        }

        if(invoiceNumber.getText().toString().isEmpty())
        {
            validateInputs(invoiceNumberLayout);
            count=1;
        }
        else
        {
            removeError(invoiceNumberLayout);
        }

        return count;

    }

    public void validateInputs(TextInputLayout layout)
    {
        layout.setErrorEnabled(true);
        layout.setError("This field cannot be blank.");
    }

    public void removeError(TextInputLayout layout)
    {
        layout.setError(null);
        layout.setErrorEnabled(false);
    }
}
