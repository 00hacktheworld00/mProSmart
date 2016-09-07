package com.example.sadashivsinha.mprosmart.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Activities.LoginScreen;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by saDashiv sinha on 17-Aug-16.
 */
public class Communicator extends Service {
    private IntentFilter mIntentFilter;
    private PendingServices mPendingServices;
    public static final String TAG = Communicator.class.getSimpleName();
    PreferenceManager pm;


    @Override public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Communicator started");
        mPendingServices = new PendingServices();
        mIntentFilter = new IntentFilter();

        mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mIntentFilter.setPriority(2147483647);
        Intent intent = new Intent("android.net.conn.CONNECTIVITY_CHANGE");
        List<ResolveInfo> infos = getPackageManager().queryBroadcastReceivers(intent, 0);
        for (ResolveInfo info : infos) {
            Log.i(TAG, "Receiver name:" + info.activityInfo.name + "; priority=" + info.priority);

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class PendingServices extends BroadcastReceiver {

        RequestQueue requestQueue;

        @Override
        public void onReceive(Context context, Intent intent) {

            requestQueue = Volley.newRequestQueue(context);

            Log.d("app","Network connectivity change");
            Bundle extras = intent.getExtras();

            PreferenceManager pm = new PreferenceManager(context);

            int notificationCount=0;

            if(extras!=null)
            {
                NetworkInfo ni=(NetworkInfo) extras.get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED)
                {
                    Log.i("app","Network "+ni.getTypeName()+" connected");

                    //offline project creation
                    notificationCount++;
                    performAction(false, notificationCount, "createProjectPending", context, pm, "objectProject", "urlProject", "toastMessageProject");

                    //offline po creation
                    notificationCount++;
                    performAction(false, notificationCount, "createPoPending", context, pm, "objectPO", "urlPO", "toastMessagePO");

                    //offline po line item creation
                    notificationCount++;
                    performAction(false, notificationCount, "createPoPendingLineItem", context, pm, "objectPOLineItem", "urlPOLineItem", "toastMessagePOLineItem");

                    //offline po budget updation on line item create
                    //since update method PUT should be called , so boolean value -> true
                    notificationCount++;
                    performAction(true, notificationCount, "createPoPendingUpdateBudget", context, pm, "objectPOUpdateBudget", "urlPOUpdateBudget", "toastMessageUpdateBudget");


                    //offline receipt line item creation
                    notificationCount++;
                    performAction(false, notificationCount, "createPrPending", context, pm, "objectPR", "urlPR", "toastMessagePR");


                    //offline qir creation
                    notificationCount++;
                    performAction(false, notificationCount, "createQirPending", context, pm, "objectQIR", "urlQIR", "toastMessageQIR");


                    //offline mom creation
                    notificationCount++;
                    performAction(false, notificationCount, "createMomPending", context, pm, "objectMom", "urlMom", "toastMessageMom");


                    //offline mom line item creation
                    notificationCount++;
                    performAction(false, notificationCount, "createMOMPendingLine", context, pm, "objectMOMLine", "urlMOMLine", "toastMessageMOMLine");


                    //offline submittal creation
                    notificationCount++;
                    performAction(false, notificationCount, "createSubmittalPending", context, pm, "objectSubmittal", "urlSubmittal", "toastMessageSubmittal");


                    //offline submittal line item creation
                    notificationCount++;
                    performAction(false, notificationCount, "createSubmittalLinePending", context, pm, "objectSubmittalLine", "urlSubmittalLine", "toastMessageSubmittalLine");


                    //offline submittal register creation
                    notificationCount++;
                    performAction(false, notificationCount, "createSubmittalRegisterPending", context, pm, "objectSubmittalRegister", "urlSubmittalRegister", "toastMessageSubmittalRegister");


                    //offline submittal register item creation
                    notificationCount++;
                    performAction(false, notificationCount, "createSubmittalRegItemPending", context, pm, "objectSubmittalRegItem", "urlSubmittalRegItem", "toastMessageSubmittalRegItem");


                    //offline subcontractor timesheet creation
                    notificationCount++;
                    performAction(false, notificationCount, "createSubcontractorTimesheet", context, pm, "objectSubcontractorTimesheet", "urlSubcontractorTimesheet", "toastMessageSubcontractorTimesheet");


                    //offline subcontractor line item creation
                    notificationCount++;
                    performAction(false, notificationCount, "createSubcontractorLineItem", context, pm, "objectSubcontractorLineItem", "urlSubcontractorLineItem", "toastMessageSubcontractorLineItem");

                }
            }

            assert extras != null;
            if(extras.getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE))
            {
                Log.d("app","There's no network connectivity");
            }

        }

        public void performAction(Boolean methodIsPut, int notificationCount, String check, Context context, PreferenceManager pm, String object, String url, String toast)
        {
            Boolean checkBoolean = pm.getBoolean(check);

            if(checkBoolean)
            {
                Toast.makeText(context, "Internet connected. Finishing pending activities - mProSmart", Toast.LENGTH_SHORT).show();

                String objectVal = pm.getString(object);
                String urlVal = pm.getString(url);
                String toastVal = pm.getString(toast);
                Log.d("toastMsg :", toastVal);

                try {
                    JSONObject jsonObject = new JSONObject(objectVal);

                    pm.putBoolean(check, false);
                    saveDataToServer(methodIsPut, notificationCount, context, jsonObject, urlVal, toastVal, check);
                    Log.d("check :", check);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }

        public void saveDataToServer(Boolean methodIsPut, final int notificationCount, final Context mContext, final JSONObject object, final String url, final String successMessageToToast,
                                     final String check) {

            int method;
            final String displayIdMsg;

            if(methodIsPut)
            {
                method = Request.Method.PUT;
                displayIdMsg = "";
            }
            else
            {
                method = Request.Method.POST;
                displayIdMsg = "ID - ";
            }


            JsonObjectRequest jor = new JsonObjectRequest(method, url, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getString("msg").equals("success"))
                                {
                                    String newPoId = response.getString("data");

//                                    Toast.makeText(mContext, successMessageToToast + response.getString("data"), Toast.LENGTH_SHORT).show();
                                    buildNotification(notificationCount, mContext, "mProSmart", successMessageToToast , displayIdMsg + newPoId);

                                    if(check.equals("createPoPending"))
                                    {
                                        //get the purchase order created ID and check if line item creation is pending
                                        //if pending -> add line item to the newly created PO
                                        PreferenceManager pm = new PreferenceManager(mContext);

                                        Boolean createPoPendingLineItemForNewPo = pm.getBoolean("createPoPendingLineItemForNewPo");

                                        if(createPoPendingLineItemForNewPo)
                                        {
                                            String poLineObjectString = pm.getString("objectPOLineItemForNewPo");

                                            JSONObject poLineObject = new JSONObject(poLineObjectString.toString());

                                            poLineObject.remove("purchaseOrderId");
                                            poLineObject.put("purchaseOrderId", newPoId);
                                            pm.putString("objectPOLineItemForNewPo", poLineObject.toString());

                                        }



                                        performAction(false, notificationCount+100, "createPoPendingLineItemForNewPo", mContext, pm, "objectPOLineItemForNewPo", "urlPOLineItemForNewPo", "toastMessagePOLineItemForNewPo");

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
            requestQueue.add(jor);

        }
    }



    public static void buildNotification(int notificationCount, Context context, String title, String text, String subText)
    {

        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.drawable.success_color);

        //for notification sound and vibrate as normal android notification
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        Intent notificationIntent = new Intent(context, LoginScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        builder.setAutoCancel(true);

        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_main));

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle(title);

        // Content text, which appears in smaller text below the title
        builder.setContentText(text);

        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        builder.setSubText(subText);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(notificationCount, builder.build());
        Log.d("notification Count :", String.valueOf(notificationCount));
    }
}
