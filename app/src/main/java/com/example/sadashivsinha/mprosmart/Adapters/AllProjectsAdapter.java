package com.example.sadashivsinha.mprosmart.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.ModelLists.AllProjectsList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by saDashiv sinha on 25-Feb-16.
 */
public class AllProjectsAdapter extends RecyclerView.Adapter<AllProjectsAdapter.MyViewHolder> {

    public List<AllProjectsList> modelList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView startedDate, finishByDate, purchaseReceipts, itemsReceived, title, descOne, descTwo, last_updated_time;
        public CircleImageView profile_pic, company_logo;
        public CardView cardview;
        public EditText text_notes;
        public TextView project_no, wbs_id;
        FancyButton btn_update_progress;
        Button saveBtn;
        ProgressDialog pDialog;

        public MyViewHolder(final View view) {
            super(view);
            startedDate = (TextView) view.findViewById(R.id.startedDate);
            finishByDate = (TextView) view.findViewById(R.id.finishByDate);
            purchaseReceipts = (TextView) view.findViewById(R.id.purchaseReceipts);
            itemsReceived = (TextView) view.findViewById(R.id.itemsReceived);
            descOne = (TextView) view.findViewById(R.id.descriptionOne);
            descTwo = (TextView) view.findViewById(R.id.descriptionTwo);
            profile_pic = (CircleImageView) view.findViewById(R.id.profile_pic);
            company_logo = (CircleImageView) view.findViewById(R.id.company_logo);
            last_updated_time = (TextView) view.findViewById(R.id.last_updated_time);
            title = (TextView) view.findViewById(R.id.title);
            project_no = (TextView) view.findViewById(R.id.project_no);
            wbs_id = (TextView) view.findViewById(R.id.wbs_id);

            cardview = (CardView) view.findViewById(R.id.cardview);

            btn_update_progress = (FancyButton) view.findViewById(R.id.btn_update_progress);

            btn_update_progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(itemView.getContext(),android.R.style.Theme_Translucent_NoTitleBar);
                    // Set an EditText view to get user input

                    View dialogView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_update_progress, null);
                    alert.setView(dialogView);

                    final AlertDialog show = alert.show();

                    final EditText text_new_progress = (EditText) dialogView.findViewById(R.id.text_new_progress);

                    saveBtn = (Button) dialogView.findViewById(R.id.saveBtn);

                    saveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final String new_progress = text_new_progress.getText().toString();
                            final Context context = itemView.getContext();
                            final String current_wbs_id = wbs_id.getText().toString();

                            if(text_new_progress.getText().toString().isEmpty())
                            {
                                text_new_progress.setError("Required Field cannot be empty.");
                            }
                            else if(Integer.parseInt(new_progress)<Integer.parseInt(itemsReceived.getText().toString()))
                            {
                                text_new_progress.setError("New progress can't be less than previous progress.");
                            }
                            else if(Integer.parseInt(new_progress)>100)
                            {
                                text_new_progress.setError("Progress can't be higher than 100%.");
                            }
                            else
                            {

                                pDialog = new ProgressDialog(itemView.getContext());
                                pDialog.setMessage("Saving Data ...");
                                pDialog.setIndeterminate(false);
                                pDialog.setCancelable(true);
                                pDialog.show();

                                class MyTask extends AsyncTask<Void, Void, Void>
                                {
                                    @Override
                                    protected Void doInBackground(Void... params)
                                    {JSONObject object = new JSONObject();

                                        try {
                                            object.put("progress", Integer.parseInt(new_progress));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        RequestQueue requestQueue = Volley.newRequestQueue(context);

                                        String url = context.getResources().getString(R.string.server_url) + "/putWbs?wbsId=\""+current_wbs_id+"\"";

                                        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        try {
                                                            Toast.makeText(itemView.getContext(), response.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                                                            pDialog.dismiss();

                                                            if(response.getString("msg").equals("success"))
                                                            {
                                                                show.dismiss();
                                                                itemsReceived.setText(new_progress);
                                                            }

                                                        } catch (JSONException e) {
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
                                        return null;
                                    }
                                }
                                new MyTask().execute();
                            }
                        }
                    });
                }
            });

            final PreferenceManager pm = new PreferenceManager(view.getContext());

            text_notes = (EditText) view.findViewById(R.id.text_notes);

            text_notes.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {

                        JSONObject object = new JSONObject();

                        try {
                            object.put("wbsId",wbs_id.getText().toString());
                            object.put("notes",text_notes.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

                        String url = view.getContext().getResources().getString(R.string.server_url) + "/postWbsNotes";

                        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Toast.makeText(view.getContext(), "Notes Saved", Toast.LENGTH_SHORT).show();
                                        text_notes.setEnabled(false);
                                        //response success message display
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("Volley","Error");

                                    }
                                }
                        );
                        requestQueue.add(jor);

                        return true;
                    }
                    return false;
                } });
          }
      }


    public AllProjectsAdapter(List<AllProjectsList> modelList) {
        this.modelList = modelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_projects, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllProjectsList items = modelList.get(position);
        holder.startedDate.setText(String.valueOf(items.getStartedDate()));
        holder.finishByDate.setText(items.getFinishByDate());
        holder.purchaseReceipts.setText(String.valueOf(items.getPurchaseReceipts()));
        holder.itemsReceived.setText(items.getItemsReceived());
        holder.descOne.setText(String.valueOf(items.getDescOne()));
        holder.descTwo.setText(items.getDescTwo());
//        holder.profile_pic.setImageResource(items.getProfilePic());
//        holder.company_logo.setImageResource(items.getCompanyLogo());
        holder.last_updated_time.setText(items.getLast_updated_time());
        holder.title.setText(items.getTitle());
        holder.project_no.setText(String.valueOf(items.getProjectNo()));
        holder.wbs_id.setText(items.getWbsId());
        holder.text_notes.setText(items.getNotes());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}