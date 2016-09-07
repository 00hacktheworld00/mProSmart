package com.example.sadashivsinha.mprosmart;

/**
 * Created by saDashiv sinha on 18-Aug-16.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.sadashivsinha.mprosmart.Activities.LockScreen;
import com.example.sadashivsinha.mprosmart.Activities.LoginScreen;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.Random;


/**
 * Created by saDashiv sinha on 17-Aug-16.
 */

//Class is extending GcmListenerService
public class GCMPushReceiverService extends GcmListenerService {

    private static final String TAG = GCMPushReceiverService.class.getSimpleName();
    WindowManager mWindowManager;
    View mView;
    Animation mAnimation;


    //This method will be called on every new message received
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Getting the message from the bundle
        final String message = data.getString("message");
        final String id = data.getString("entityId");
        final String createdBy = data.getString("createdBy");
        final String createdDate = data.getString("createdDate");
        //Displaying a notiffication with the message
//        sendNotification(message);
        Random ran = new Random();
        int x = ran.nextInt(6) + 5;


        final String newMsg =  message + "ID-" + id + "Created By : "+ createdBy + "Created Date : " + createdDate;
        Log.d("noti msg : ", data.toString());


        buildNotification(x, this, "mProSmart", newMsg  , "" + "");

//        Handler mHandler = new Handler(Looper.getMainLooper()) {
//            @Override
//            public void handleMessage(Message m) {
//                // This is where you do your work in the UI thread.
//                // Your worker tells you in the message what to do.
//                showDialog(newMsg);
//            }
//        };
//
//        Message messages = mHandler.obtainMessage(1, "");
//        messages.sendToTarget();
    }

    //This method is generating a notification and displaying the notification
    private void sendNotification(String message) {
        Intent intent = new Intent(this, LoginScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_main)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }


    public void buildNotification(int notificationCount, Context context, String title, String text, String subText)
    {
        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.drawable.success_color);

        //for notification sound and vibrate as normal android notification
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        Intent notificationIntent = new Intent(context, LockScreen.class);
        notificationIntent.putExtra("notification", "true");
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


    //popup for notifications
    private void showDialog(String messageFromServer)
    {

//        //to play sound
//        try
//        {
//        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//        r.play();
//        }
//        catch (Exception e) {
//        e.printStackTrace();
//    }


        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = pm.newWakeLock((PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "mProSmart");
        mWakeLock.acquire();
        mWakeLock.release();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mView = View.inflate(this, R.layout.dialog_popup_notification_received, null);
        mView.setTag(TAG);

        HelveticaRegular popup_text = (HelveticaRegular) mView.findViewById(R.id.text_popup);

        popup_text.setText(messageFromServer);

        Button dialog_btn_approve, dialog_btn_reject;

        dialog_btn_approve = (Button) mView.findViewById(R.id.dialog_btn_approve);
        dialog_btn_reject = (Button) mView.findViewById(R.id.dialog_btn_reject);

        RelativeLayout whole_layout, dialog_layout;

        whole_layout = (RelativeLayout) mView.findViewById(R.id.whole_layout);
        dialog_layout = (RelativeLayout) mView.findViewById(R.id.dialog_layout);

        whole_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDialog();
            }
        });

        dialog_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });

        dialog_btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("POPUP NOTIFICATION :", "approve clicked");
                hideDialog();
            }
        });

        dialog_btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("POPUP NOTIFICATION :", "reject clicked");
                hideDialog();
//                }
            }
        });

        ImageButton btn_close = (ImageButton) mView.findViewById(R.id.btn_close);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("POPUP NOTIFICATION :", "close btn clicked");
                hideDialog();
            }
        });

        final WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 0,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON ,
                PixelFormat.RGBA_8888);

        mView.setVisibility(View.VISIBLE);
        mWindowManager.addView(mView, mLayoutParams);
        mWindowManager.updateViewLayout(mView, mLayoutParams);
    }

    private void hideDialog(){
        if(mView != null && mWindowManager != null){
            mWindowManager.removeView(mView);
            mView = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
