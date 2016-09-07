package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.ModelLists.AttachmentList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.Utils.FileUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;

public class AttachmentActivity extends AppCompatActivity {


    AttachmentList items;
    List<AttachmentList> attachmentList = new ArrayList<>();
    AttachmentAdapter attachmentAdapter = new AttachmentAdapter(attachmentList);
    TextView addMoreBtn, attachBtn;
    RecyclerView attachRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment);

        attachRecyclerView = (RecyclerView) findViewById(R.id.attachmentRecycler);
        attachRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        attachRecyclerView.setHasFixedSize(true);
        attachRecyclerView.setAdapter(attachmentAdapter);


        addMoreBtn = (TextView) findViewById(R.id.addMoreBtn);
        attachBtn = (TextView) findViewById(R.id.attachBtn);

        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(AttachmentActivity.this, "Uploaded Attachments", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
        addMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
//
//                        if (ContextCompat.checkSelfPermission(PurchaseEntry.this,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                != PackageManager.PERMISSION_GRANTED)
//                        {
//                            ActivityCompat.requestPermissions(PurchaseEntry.this,
//                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//                        }
                    }


                    showFileChooser();
                }
            });

                                      }

                String TAG = "PurchaseEntry";
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

                            File f = new File(path);
                            String fileName = f.getName();

                            Drawable imageDrawable = Drawable.createFromPath(path);

                            items = new AttachmentList(fileName,imageDrawable);
                            attachmentList.add(items);

                            attachmentAdapter.notifyDataSetChanged();


//                            final FloatingActionButton fabUpload = (FloatingActionButton) findViewById(R.id.fabUpload);
//                            final Button myButton = new Button(this);
//                            attachmentNo++;
//                            myButton.setText("Attachment "+attachmentNo);
//
//                            //      myButton.setId();
//
//                            LinearLayoutCompat.LayoutParams lp = new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.WRAP_CONTENT);
//                            layout.addView(myButton, lp);
//
//                            final String finalPath = path;
//                            myButton.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    layout.removeView(myButton);
//                                    attachmentNo--;
//                                    Toast.makeText(AttachmentActivity.this, "File Removed :" + finalPath, Toast.LENGTH_SHORT).show();
//
//                                }
//                            });
//
//
//                            File f = new File(path);
//                            String fileName = f.getName();
//                            String extension = "";
//
//                            int i = fileName.lastIndexOf('.');
//                            if (i > 0)
//                            {
//                                extension = fileName.substring(i+1);
//                            }
//
//
//                            Snackbar snackbar = Snackbar
//                                    .make(mainLayout, "File Selected : "+ fileName, Snackbar.LENGTH_LONG);
//
//                            snackbar.show();
//
//                            final TextView myButton = new TextView(this);
//
////                            final FloatingActionButton floatingActionButton = new FloatingActionButton(this);
////                            floatingActionButton.setImageResource(R.drawable.cancel_white);
//
//                            ImageButton imageButton = new ImageButton(this);
//                            imageButton.setBackgroundResource(R.drawable.cross);
//
//                            attachmentNo++;
//                            String text= String.valueOf(fileName) + "         ";
//
//                            myButton.setText(text);
//
//
//                            //      myButton.setId();
//
////                            LinearLayoutCompat.LayoutParams lp = new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.WRAP_CONTENT);
//
////                            layout.addView(myButton, lp);
//
//                            final LinearLayout parent = new LinearLayout(getApplicationContext());
//
//                            parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                            parent.setOrientation(LinearLayout.HORIZONTAL);
//                            myButton.setGravity(Gravity.CENTER);
//                            parent.addView(myButton);
//                            parent.addView(imageButton);
//
//
//                            layout.addView(parent);
//
//                            final String finalName = fileName;
//                            imageButton.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    layout.removeView(parent);
//                                    attachmentNo--;
//
//                                    Snackbar snackbar = Snackbar
//                                            .make(mainLayout, "File Removed : "+ finalName, Snackbar.LENGTH_LONG);
//
//                                    snackbar.show();
//                                }
//                            });

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
            public ImageView file_preview;
            public FButton file_remove_btn;

            public MyViewHolder(final View view) {
                super(view);
                file_name = (TextView) view.findViewById(R.id.file_name);
                file_preview = (ImageView) view.findViewById(R.id.file_preview);
                file_remove_btn = (FButton) view.findViewById(R.id.file_remove_btn);

                file_remove_btn.setButtonColor(getResources().getColor(R.color.reject_red));

                file_remove_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //to remove the file
                        modelList.remove(getPosition());
                        notifyItemRemoved(getPosition());
                    }
                });
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
        public void onBindViewHolder(MyViewHolder holder, int position) {
            AttachmentList items = modelList.get(position);
            holder.file_name.setText(String.valueOf(items.getFile_name()));
            holder.file_preview.setBackground(items.getFile_preview());
        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

    }
}
