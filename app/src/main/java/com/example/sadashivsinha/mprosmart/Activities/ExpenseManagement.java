package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.ExpenseAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.BudgetList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExpenseManagement extends AppCompatActivity implements View.OnClickListener {
    private List<BudgetList> budgetList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ExpenseAdapter expenseAdapter;
    private BudgetList items;
    private ProgressDialog pDialog, pDialog1;
    JSONArray dataArray;
    JSONObject dataObject;
    ConnectionDetector cd;
    public static final String TAG = ExpenseManagement.class.getSimpleName();
    Boolean isInternetPresent = false;
    String expenseManagementLineId, expenseManagementId, wbsId, wbsActivityId, itemId, itemDescription, quantity, uomId, amount, createdBy;
    String projectId, expenseType, totalExpense, expenseDescription, createdDate;

    PreferenceManager pm;
    TextView expense_id, date, created_by, expense_type, total_expense, expense_desc;
    String currentProjectNo, currentExpenseManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_management);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            pm = new PreferenceManager(ExpenseManagement.this);
            currentProjectNo = pm.getString("projectId");
            currentExpenseManagement = pm.getString("currentExpense");

            cd = new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();

            // check for Internet status
            if (!isInternetPresent) {
                // Internet connection is not present
                // Ask user to connect to Internet
                RelativeLayout main_content = (RelativeLayout) findViewById(R.id.main_content);
                Snackbar snackbar = Snackbar.make(main_content,getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
                snackbar.show();
        }

        created_by = (TextView) findViewById(R.id.created_by);
        expense_id = (TextView) findViewById(R.id.expense_id);
        date = (TextView) findViewById(R.id.date);
        expense_type = (TextView) findViewById(R.id.expense_type);
        total_expense = (TextView) findViewById(R.id.total_expense);
        expense_desc = (TextView) findViewById(R.id.expense_desc);
        expense_desc = (TextView) findViewById(R.id.expense_desc);

        prepareHeader();

        pDialog1 = new ProgressDialog(ExpenseManagement.this);
        pDialog1.setMessage("Getting Data ...");
        pDialog1.setIndeterminate(false);
        pDialog1.setCancelable(true);
        pDialog1.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override protected void onPreExecute()
            {
                expenseAdapter = new ExpenseAdapter(budgetList);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(ExpenseManagement.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(expenseAdapter);

            }

            @Override
            protected Void doInBackground(Void... params)
            {
                prepareItems();
                return null;
            }

            @Override protected void onPostExecute(Void result)
            {
                expenseAdapter.notifyDataSetChanged();
            }

        }

        new MyTask().execute();


        FloatingActionButton fab_add;

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);

        fab_add.setOnClickListener(this);

    }

    private void rotate(float degree, ImageButton imageView)
    {
        final RotateAnimation rotateAnim = new RotateAnimation(0.0f, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(500);
        rotateAnim.setFillAfter(true);
        imageView.startAnimation(rotateAnim);
    }

    public void prepareItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getExpenseManagementLine?expenseManagementId=\""+currentExpenseManagement+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                pDialog1.dismiss();
                                Toast.makeText(ExpenseManagement.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);


                                    expenseManagementLineId = dataObject.getString("expenseManagementLineId");
                                    expenseManagementId = dataObject.getString("expenseManagementId");
                                    wbsId = dataObject.getString("wbsId");
                                    wbsActivityId = dataObject.getString("wbsActivityId");
                                    itemId = dataObject.getString("itemId");
                                    itemDescription = dataObject.getString("itemDescription");
                                    quantity = dataObject.getString("quantity");
                                    uomId = dataObject.getString("uomId");
                                    amount = dataObject.getString("amount");
                                    createdBy = dataObject.getString("createdBy");

                                    items = new BudgetList(pm.getString("expenseType"), String.valueOf(i+1), expenseManagementLineId,wbsId , wbsActivityId, itemId, itemDescription, quantity, uomId, amount);
                                    budgetList.add(items);

                                    expenseAdapter.notifyDataSetChanged();

                                }
                            }
                            pDialog1.dismiss();
                        }catch(JSONException e){
                            pDialog1.dismiss();
                            e.printStackTrace();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog1.dismiss();
                        Log.e("Volley","Error");

                    }
                }
        );
        requestQueue.add(jor);
    }

    public void prepareHeader()
    {
        expense_id.setText(pm.getString("currentExpense"));
        expense_type.setText(pm.getString("expenseType"));
        total_expense.setText(pm.getString("expenseTotalExpense"));
        expense_desc.setText(pm.getString("expenseDesc"));
        created_by.setText(pm.getString("expenseCreatedBy"));
        date.setText(pm.getString("expenseDate"));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.fab_add:
            {
                Intent intent = new Intent(ExpenseManagement.this, ExpenseManagementCreate.class);
                startActivity(intent);
            }
            break;
        }

    }
}