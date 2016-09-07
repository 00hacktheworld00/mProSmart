package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.ApproveRejectAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.ApproveRejectList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApproveRejectActivity extends NewActivity{

    private List<ApproveRejectList> list = new ArrayList<>();
    private ApproveRejectAdapter adapter;
    private RecyclerView recyclerView;
    private View view;
    private boolean add = false;
    private Paint p = new Paint();
    ProgressDialog pDialog;
    ApproveRejectList items;
    JSONArray dataArray;
    JSONObject dataObject;
    String id, entityId, entityName, entityTName, message, createdBy, createdDate, approved;
    HelveticaRegular no_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_reject);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Notifications");

//        Toast.makeText(ApproveRejectActivity.this, "Swipe \"RIGHT\" to APPROVE and \"LEFT\" to REJECT", Toast.LENGTH_SHORT).show();

        initViews();

        no_notification = (HelveticaRegular) findViewById(R.id.no_notification);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mAdapter = new MyAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State




    }

    private void initViews(){
        recyclerView = (RecyclerView)findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ApproveRejectAdapter(list);
        recyclerView.setAdapter(adapter);


        pDialog = new ProgressDialog(ApproveRejectActivity.this);
        pDialog.setMessage("Getting Details ...");
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
        initSwipe();

    }
    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                pDialog = new ProgressDialog(ApproveRejectActivity.this);
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);

                if (direction == ItemTouchHelper.LEFT)
                {
                    pDialog.setMessage("Sending for Rejection to Server...");
                    pDialog.show();
                    approveRejectToServer("REJECT", getApplicationContext(), adapter, position, viewHolder);
                }
                else
                {
                    pDialog.setMessage("Sending for Approval to Server...");
                    pDialog.show();
                    approveRejectToServer("APPROVE", getApplicationContext(), adapter, position, viewHolder);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.clipRect(background);
                        c.drawRect(background,p);
                        icon = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_approve);

//                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_approve);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.clipRect(background);
                        c.drawRect(background,p);
                        icon = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_reject);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                c.restore();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    private void removeView(){
        if(view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void prepareItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getNotifications";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(ApproveRejectActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                Log.d("response approval : ", response.toString());

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    id = dataObject.getString("id");
                                    entityId = dataObject.getString("entityId");
                                    entityName = dataObject.getString("entityName");
                                    entityTName = dataObject.getString("entityTName");
                                    message = dataObject.getString("message");
                                    createdBy = dataObject.getString("createdBy");
                                    createdDate = dataObject.getString("createdDate");
                                    approved = dataObject.getString("approved");

                                    if(!approved.equals("1") || !approved.equals("2"))
                                    {
                                        items = new ApproveRejectList(message, entityId, createdBy, createdDate, entityName,
                                                entityTName);
                                        list.add(items);
                                    }
                                }

                                if(list.isEmpty())
                                {
                                    no_notification.setVisibility(View.VISIBLE);
                                    no_notification.setText("No New Notification");
                                }
                                adapter.notifyDataSetChanged();
                                pDialog.dismiss();
                            }
                            pDialog.dismiss();
                        }catch(JSONException e){e.printStackTrace();
                            pDialog.dismiss();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Log.e("Volley","Error");
                    }
                }
        );
        requestQueue.add(jor);

        if(pDialog!=null)
            pDialog.dismiss();

    }

    public void approveRejectToServer(final String approveReject , final Context context , final ApproveRejectAdapter adapter,
                                      final int position, RecyclerView.ViewHolder viewHolder)
    {
        JSONObject object = new JSONObject();

        try {
            object.put("id",adapter.getCurrentId(position));
            object.put("columnName", adapter.getEntityName(position));
            object.put("tableName", adapter.getEntityTable(position));

            if(approveReject.equals("REJECT"))
            {
                object.put("approve", "2");
            }
            else
            {
                object.put("approve", "1");
            }

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = context.getResources().getString(R.string.server_url) + "/approveReject";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            Log.d("response server : ", response.toString());
                            if(response.getString("approved").equals("1"))
                            {
                                if(response.getString("msg").equals("Already Approved"))
                                {
                                    Toast.makeText(ApproveRejectActivity.this, "Item Already Approved", Toast.LENGTH_SHORT).show();
                                }
                                else if(response.getString("msg").equals("Already Rejected"))
                                {
                                    Toast.makeText(ApproveRejectActivity.this, "Item Rejected Approved", Toast.LENGTH_SHORT).show();
                                }
                                else if(approveReject.equals("REJECT"))
                                {
                                    Toast.makeText(context, "Item Rejected", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(context, "Item Approved", Toast.LENGTH_SHORT).show();
                                }
                                adapter.removeItem(position);


                                if(adapter.getItemCount()==0)
                                {
                                    no_notification.setVisibility(View.VISIBLE);
                                    no_notification.setText("No New Notification");
                                }

                            }
                            else
                            {
                                Toast.makeText(context, response.getString("msg") , Toast.LENGTH_SHORT).show();
                            }
                            pDialog.dismiss();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        pDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jor);

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ApproveRejectActivity.this, WelcomeActivity.class);
        startActivity(intent);
    }
}