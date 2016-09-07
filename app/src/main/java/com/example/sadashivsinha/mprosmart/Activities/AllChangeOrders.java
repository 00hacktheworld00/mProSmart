package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.Adapters.AllChangeOrdersAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllChangeOrdersList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.github.clans.fab.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AllChangeOrders extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private List<AllChangeOrdersList> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllChangeOrdersAdapter ordersAdapter;
    String currentProjectNo, currentProjectName, currentDate, currentUser;
    View dialogView;
    AlertDialog show;
    EditText order_name;
    TextView due_date;
    ProgressDialog pDialog;

    AllChangeOrdersList items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_change_orders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUser = pm.getString("userId");


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());
        currentDate = strDate;

        ordersAdapter = new AllChangeOrdersAdapter(list);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(ordersAdapter);

        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                prepareItems();
                return null;
            }

        }
        new MyTask().execute();


        FloatingActionButton fab_add, fab_search, exportBtn;

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);

        fab_add.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        exportBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
//                Intent intent = new Intent(AllChangeOrders.this, BOQCreate.class);
//                startActivity(intent);

                final AlertDialog.Builder alert = new AlertDialog.Builder(AllChangeOrders.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(AllChangeOrders.this).inflate(R.layout.dialog_new_order, null);
                alert.setView(dialogView);

                show = alert.show();

                order_name = (EditText) dialogView.findViewById(R.id.order_name);
                due_date = (TextView) dialogView.findViewById(R.id.due_date);

                due_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance(
                                AllChangeOrders.this,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.show(getFragmentManager(), "Datepickerdialog");
                    }
                });

                Button createBtn = (Button) dialogView.findViewById(R.id.createBtn);

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(order_name.getText().toString().isEmpty())
                        {
                            order_name.setError("Field cannot be empty");
                        }
                        else if(due_date.getText().toString().isEmpty())
                        {
                            Toast.makeText(AllChangeOrders.this, "Select Due Date", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
//                            pDialog = new ProgressDialog(AllChangeOrders.this);
//                            pDialog.setMessage("Sending Data ...");
//                            pDialog.setIndeterminate(false);
//                            pDialog.setCancelable(true);
//                            pDialog.show();

                            class MyTask extends AsyncTask<Void, Void, Void> {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    saveChangeOrders();
                                    return null;
                                }
                            }

                            new MyTask().execute();
                        }
                    }
                });
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Orders !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllChangeOrders.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllChangeOrders.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
            case R.id.exportBtn:
            {
                // to do export
            }
            break;
        }
    }


    public void saveChangeOrders()
    {
//        JSONObject object = new JSONObject();
//
//        try {
//            object.put("projectId",currentProjectNo);
//            object.put("createdBy", currentUser);
//            object.put("dueDate", currentProjectDesc);
//            object.put("statusId",spinner_boq_item.getSelectedItem().toString());
//            object.put("createdDate", currentDate);
//
//            Log.d("REQUEST SENT OF JSON :" , object.toString());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RequestQueue requestQueue = Volley.newRequestQueue(AllChangeOrders.this);
//
//        String url = AllChangeOrders.this.getResources().getString(R.string.server_url) + "/postChangeOrder";
//
//        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//
//                            Log.d("SERVER RESPONSE :", response.toString());
//
//                            if(response.getString("msg").equals("success"))
//                            {
//                                Toast.makeText(AllChangeOrders.this, "Change Orders Created. ID - "+ response.getString("data"), Toast.LENGTH_SHORT).show();
//                                pDialog.dismiss();
//                                Intent intent = new Intent(AllChangeOrders.this, AllChangeOrders.class);
//                                startActivity(intent);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            pDialog.dismiss();
//                        }
//                        //response success message display
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Volley", "Error");
//                        pDialog.dismiss();
//                    }
//                }
//        );
//        requestQueue.add(jor);
    }

    public void prepareItems()
    {
//        items = new AllChangeOrdersList("1", "O10014", "Order A", currentProjectNo, currentProjectName, "20/07/2016", "25/07/2016");
//        list.add(items);
//
//
//        items = new AllChangeOrdersList("2", "O10015", "Order B", currentProjectNo, currentProjectName, "01/07/2016", "18/07/2016");
//        list.add(items);
//
//
//        items = new AllChangeOrdersList("3", "O10016", "Order C", currentProjectNo, currentProjectName, "14/06/2016", "19/07/2016");
//        list.add(items);
//
//
//        if(getIntent().hasExtra("orderDate"))
//        {
//            String orderTitle = getIntent().getStringExtra("orderTitle");
//            String orderDate = getIntent().getStringExtra("orderDate");
//            String createdDate = getIntent().getStringExtra("createdDate");
//
//            items = new AllChangeOrdersList("4", "O10017", orderTitle, currentProjectNo, currentProjectName,  createdDate, orderDate);
//            list.add(items);
//
//        }
//
//        ordersAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllChangeOrders.this, ViewPurchaseOrders.class);
        startActivity(intent);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String date = dayOfMonth+"/"+(MONTHS[monthOfYear])+"/"+year;
        due_date.setText(date);
    }
}
