package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.GCMRegistrationIntentService;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.font.RobotoTextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginScreen extends Activity implements View.OnClickListener {


    ProgressDialog pDialog;
    String username, password, userId, name, companyId;
    PreferenceManager pm;
    JSONArray dataArray;
    JSONObject dataObject;
    ConnectionDetector cd;
    Boolean rememberMe;
    String rememberUsername, rememberPassword;
    CheckBox btn_remember;
    public static final String TAG = LoginScreen.class.getSimpleName();
    Boolean isInternetPresent = false;
    EditText user, pass;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    RobotoTextView btn_config_server;
    EditText text_port, text_ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        pm = new PreferenceManager(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setExitTransition(new Explode());
        }

        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false))
        {
            finish();
        }



        getWindow().requestFeature(Window.FEATURE_NO_TITLE); // Removing
        // ActionBar
        setContentView(R.layout.activity_login_screen);

//        if(getIntent().hasExtra("IPADDRESS")){
//            if(getIntent().getStringExtra("NEWADDRESS").equals("YES"))
//            {
//                pm.putString("SERVER_URL", "http://" + getIntent().getStringExtra("IPADDRESS")+":" + getIntent().getStringExtra("PORTNO"));
//                pm.putString("SERVER_UPLOAD_URL","http://" +  getIntent().getStringExtra("IPADDRESS"));
//            }
//            else
//            {
//                pm.putString("SERVER_URL", "http://" + getIntent().getStringExtra("IPADDRESS") + ":" + getIntent().getStringExtra("PORTNO") );
//                pm.putString("SERVER_UPLOAD_URL","http://" +  getIntent().getStringExtra("IPADDRESS"));
//            }
//        }
//        else {
//            pm.putString("SERVER_URL", "http://" + getResources().getString(R.string.server_url));
//            pm.putString("SERVER_UPLOAD_URL","http://" +  getResources().getString(R.string.server_upload_url) );
//        }

        if(pm.getBoolean("lockScreenEnable"))
        {
            Intent intent = new Intent(LoginScreen.this, LockScreen.class);
            startActivity(intent);
        }

        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(LoginScreen.this,
                    Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(LoginScreen.this,
                        new String[]{Manifest.permission.INTERNET}, 1);
            }
        }
        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(LoginScreen.this,
                    Manifest.permission.WAKE_LOCK)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(LoginScreen.this,
                        new String[]{Manifest.permission.WAKE_LOCK}, 3);
            }
        }
        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(LoginScreen.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(LoginScreen.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
            }
        }
        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(LoginScreen.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(LoginScreen.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 5);
            }
        }

        final LinearLayout layout_ip_address = (LinearLayout) findViewById(R.id.layout_ip_address);

        btn_config_server = (RobotoTextView) findViewById(R.id.btn_config_server);
        btn_config_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(LoginScreen.this, EnterIPActivity.class);
//                startActivity(intent);
//                overridePendingTransition(0,0);

                btn_config_server.setVisibility(View.GONE);
                layout_ip_address.setVisibility(View.VISIBLE);
            }
        });

        text_ip = (EditText) findViewById(R.id.text_ip);
        text_port = (EditText) findViewById(R.id.text_port);


        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //Getting the registration token from the intent
                    String token = intent.getStringExtra("token");
                    //Displaying the token as toast
//                    Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();

                    //if the intent is not with success then displaying error messages
                } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if(ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent intent = new Intent(this, GCMRegistrationIntentService.class);
            startService(intent);
        }







        //----------


        btn_remember = (CheckBox) findViewById(R.id.btn_remember);

        user = (EditText) findViewById(R.id.login_page_media_login_text);
        pass = (EditText) findViewById(R.id.login_page_media_login_password);

        rememberMe = pm.getBoolean("rememberMe");
        if(rememberMe)
        {
            rememberUsername = pm.getString("rememberUsername");
            rememberPassword = pm.getString("rememberPassword");

            user.setText(rememberUsername);
            pass.setText(rememberPassword);

            btn_remember.setChecked(true);
        }

        RobotoTextView loginBtn;

        loginBtn = (RobotoTextView) findViewById(R.id.login);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!text_ip.getText().toString().isEmpty() && text_port.getText().toString().isEmpty()) {
                    text_port.setError("Enter Port No.");
                }
                else if(!text_port.getText().toString().isEmpty() && text_ip.getText().toString().isEmpty()) {
                    text_ip.setError("Enter IP Address");
                }
                else if(!text_port.getText().toString().isEmpty() && !text_ip.getText().toString().isEmpty()) {
                    pm.putString("SERVER_URL", "http://" + text_ip.getText().toString() +":" + text_port.getText().toString());
                    pm.putString("SERVER_UPLOAD_URL","http://" + text_ip.getText().toString());
                    proceedLogin();
                }
                else {
                    pm.putString("SERVER_URL", "http://" + getResources().getString(R.string.server_url));
                    pm.putString("SERVER_UPLOAD_URL", "http://" + getResources().getString(R.string.server_upload_url));
                    proceedLogin();
                }

            }
        });
    }

    public void proceedLogin()
    {
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            LinearLayout main_content = (LinearLayout) findViewById(R.id.main_content);
            Snackbar snackbar = Snackbar.make(main_content, getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
            snackbar.show();

        }
        else
        {
            //validation for user and pass:
            username = user.getText().toString();
            password = pass.getText().toString();

            if(username.isEmpty())
            {
                user.setError("Username field cannot be blank");
            }

            if(password.isEmpty())
            {
                pass.setError("Password field cannot be blank");
            }

            if(!username.isEmpty() && !password.isEmpty())
            {
                pDialog = new ProgressDialog(LoginScreen.this);
                pDialog.setMessage("Checking Login ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
                class MyTask extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... params) {
                        checkLogin();
                        return null;
                    }
                }
                new MyTask().execute();
            }
        }
    }
    public void checkLogin()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(LoginScreen.this);

        String url = LoginScreen.this.pm.getString("SERVER_URL") + "/getLogin?userName=\""+username+"\""+"&password=\""+password+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String type = response.getString("type");

                            if (type.equals("ERROR"))
                            {
                                Toast.makeText(LoginScreen.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if (type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for (int i = 0; i < dataArray.length(); i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    userId = dataObject.getString("userId");
                                    name = dataObject.getString("name");
                                    companyId = dataObject.getString("companyId");
                                    pm.putString("userId", userId);
                                    pm.putString("name", name);
                                    pm.putString("companyId", companyId);

                                    if(dataObject.getString("roleId").equals("ROL001"))
                                        pm.putInt("role", 1);
                                    else
                                        pm.putInt("role", 2);

                                    if(btn_remember.isChecked())
                                    {
                                        pm.putString("rememberUsername", username);
                                        pm.putString("rememberPassword", password);
                                        pm.putBoolean("rememberMe", true);
                                    }
                                    else if(!btn_remember.isChecked())
                                    {
                                        pm.putString("rememberUsername", "");
                                        pm.putString("rememberPassword", "");
                                        pm.putBoolean("rememberMe", false);
                                    }

//                                    startRegistrationService(true, false);

                                    Log.w("MainActivity", "onResume");
                                    LocalBroadcastManager.getInstance(LoginScreen.this).registerReceiver(mRegistrationBroadcastReceiver,
                                            new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
                                    LocalBroadcastManager.getInstance(LoginScreen.this).registerReceiver(mRegistrationBroadcastReceiver,
                                            new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));


                                    Intent intent = new Intent(LoginScreen.this, LockScreen.class);
                                    startActivity(intent);

                                }

                                pDialog.dismiss();
                            }


                            if (type.equals("WARN"))
                            {
                                Toast.makeText(LoginScreen.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }
                            //response success message display
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        requestQueue.add(jor);
    }


//    //Unregistering receiver on activity paused
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.w("MainActivity", "onPause");
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
//    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            TextView tv = (TextView) v;
            Toast.makeText(this, tv.getText(), Toast.LENGTH_SHORT).show();
        }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
//        /** check if received result code
//         is equal our requested code for draw permission  */
//        if (requestCode == 1) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (Settings.canDrawOverlays(this)) {
//                    // continue here - permission was granted
//                }
//            }
//        }
//    }
}
