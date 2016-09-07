package com.example.sadashivsinha.mprosmart.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.R;

public class ChangeOrdersNew extends NewActivity {


    ImageButton btn_section_one, btn_section_two, btn_section_three, btn_section_four;
    LinearLayout hiddenLayoutSectionOne, hiddenLayoutSectionTwo, hiddenLayoutSectionThree, hiddenLayoutSectionFour;
    CardView card_section_one_title, card_section_two_title, card_section_three_title, card_section_four_title;

    EditText text_change_desc, text_justification, text_project_impact;
    EditText text_budget_impact, text_schedule_imapact;
    EditText text_documents_impacted;
    EditText text_decision;
    ImageButton attachBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_orders_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        attachBtn = (ImageButton) findViewById(R.id.attachBtn);

        getSupportActionBar().setTitle("Change Orders");

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


        final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        text_change_desc = (EditText) findViewById(R.id.text_change_desc);
        text_justification = (EditText) findViewById(R.id.text_justification);
        text_project_impact = (EditText) findViewById(R.id.text_project_impact);

        text_budget_impact = (EditText) findViewById(R.id.text_budget_impact);
        text_schedule_imapact = (EditText) findViewById(R.id.text_schedule_imapact);

        text_documents_impacted = (EditText) findViewById(R.id.text_documents_impacted);

        text_decision = (EditText) findViewById(R.id.text_decision);

        card_section_one_title = (CardView) findViewById(R.id.card_section_one_title);
        card_section_two_title = (CardView) findViewById(R.id.card_section_two_title);
        card_section_three_title = (CardView) findViewById(R.id.card_section_three_title);
        card_section_four_title = (CardView) findViewById(R.id.card_section_four_title);

        btn_section_one = (ImageButton) findViewById(R.id.btn_section_one);
        btn_section_two = (ImageButton) findViewById(R.id.btn_section_two);
        btn_section_three = (ImageButton) findViewById(R.id.btn_section_three);
        btn_section_four = (ImageButton) findViewById(R.id.btn_section_four);

        hiddenLayoutSectionOne = (LinearLayout) findViewById(R.id.hiddenLayoutSectionOne);
        hiddenLayoutSectionTwo = (LinearLayout) findViewById(R.id.hiddenLayoutSectionTwo);
        hiddenLayoutSectionThree = (LinearLayout) findViewById(R.id.hiddenLayoutSectionThree);
        hiddenLayoutSectionFour = (LinearLayout) findViewById(R.id.hiddenLayoutSectionFour);


        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeOrdersNew.this, AttachmentActivity.class);
                startActivity(intent);
            }
        });

        card_section_one_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayoutSectionOne.getVisibility()==View.GONE)
                {
                    hiddenLayoutSectionOne.setVisibility(View.VISIBLE);
                    hiddenLayoutSectionOne.startAnimation(slide_up);
                }

                else
                {
                    hiddenLayoutSectionOne.startAnimation(slide_down);
                    hiddenLayoutSectionOne.setVisibility(View.GONE);
                }
            }
        });

        btn_section_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(text_change_desc.getText().toString().isEmpty())
                {
                    text_change_desc.setError("Cannot be left empty");
                }
                else if(text_justification.getText().toString().isEmpty())
                {
                    text_justification.setError("Cannot be left empty");
                }
                else if(text_project_impact.getText().toString().isEmpty())
                {
                    text_project_impact.setError("Cannot be left empty");
                }

                else
                {
                    hiddenLayoutSectionOne.startAnimation(slide_down);
                    hiddenLayoutSectionOne.setVisibility(View.GONE);
                    Toast.makeText(ChangeOrdersNew.this, "Values Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });


        card_section_two_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayoutSectionTwo.getVisibility()==View.GONE)
                {
                    hiddenLayoutSectionTwo.setVisibility(View.VISIBLE);
                    hiddenLayoutSectionTwo.startAnimation(slide_up);
                }

                else
                {
                    hiddenLayoutSectionTwo.startAnimation(slide_down);
                    hiddenLayoutSectionTwo.setVisibility(View.GONE);
                }
            }
        });


        btn_section_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(text_budget_impact.getText().toString().isEmpty())
                {
                    text_budget_impact.setError("Cannot be left empty");
                }
                else if(text_schedule_imapact.getText().toString().isEmpty())
                {
                    text_schedule_imapact.setError("Cannot be left empty");
                }
                else
                {
                    hiddenLayoutSectionTwo.setVisibility(View.GONE);
                    hiddenLayoutSectionTwo.startAnimation(slide_down);
                    Toast.makeText(ChangeOrdersNew.this, "Values Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });


        card_section_three_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayoutSectionThree.getVisibility()==View.GONE)
                {
                    hiddenLayoutSectionThree.setVisibility(View.VISIBLE);
                    hiddenLayoutSectionThree.startAnimation(slide_up);
                }

                else
                {
                    hiddenLayoutSectionThree.startAnimation(slide_down);
                    hiddenLayoutSectionThree.setVisibility(View.GONE);
                }
            }
        });
        btn_section_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_documents_impacted.getText().toString().isEmpty())
                {
                    text_documents_impacted.setError("Cannot be left empty");
                }
                else
                {
                    hiddenLayoutSectionThree.startAnimation(slide_down);
                    hiddenLayoutSectionThree.setVisibility(View.GONE);
                    Toast.makeText(ChangeOrdersNew.this, "Values Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });



        card_section_four_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayoutSectionFour.getVisibility()==View.GONE)
                {
                    hiddenLayoutSectionFour.setVisibility(View.VISIBLE);
                    hiddenLayoutSectionFour.startAnimation(slide_up);
                }

                else
                {
                    hiddenLayoutSectionFour.startAnimation(slide_down);
                    hiddenLayoutSectionFour.setVisibility(View.GONE);
                }
            }
        });

        btn_section_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_decision.getText().toString().isEmpty())
                {
                    text_decision.setError("Cannot be left empty");
                }
                else
                {
                    hiddenLayoutSectionFour.startAnimation(slide_down);
                    hiddenLayoutSectionFour.setVisibility(View.GONE);
                    Toast.makeText(ChangeOrdersNew.this, "Values Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });


//        createBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //section one
//
//                if(text_change_desc.getText().toString().isEmpty())
//                {
//                    text_change_desc.setError("Cannot be left empty");
//                    hiddenLayoutSectionOne.setVisibility(View.VISIBLE);
//                }
//                else if(text_justification.getText().toString().isEmpty())
//                {
//                    text_justification.setError("Cannot be left empty");
//                    hiddenLayoutSectionOne.setVisibility(View.VISIBLE);
//                }
//                else if(text_project_impact.getText().toString().isEmpty())
//                {
//                    text_project_impact.setError("Cannot be left empty");
//                    hiddenLayoutSectionOne.setVisibility(View.VISIBLE);
//                }
//                //section two
//
//                else if(text_budget_impact.getText().toString().isEmpty())
//                {
//                    text_budget_impact.setError("Cannot be left empty");
//                    hiddenLayoutSectionTwo.setVisibility(View.VISIBLE);
//                }
//                else if(text_schedule_imapact.getText().toString().isEmpty())
//                {
//                    text_schedule_imapact.setError("Cannot be left empty");
//                    hiddenLayoutSectionTwo.setVisibility(View.VISIBLE);
//                }
//                //section three
//
//                else if(text_documents_impacted.getText().toString().isEmpty())
//                {
//                    text_documents_impacted.setError("Cannot be left empty");
//                    hiddenLayoutSectionThree.setVisibility(View.VISIBLE);
//                }
//                //section four
//
//                else if(text_decision.getText().toString().isEmpty())
//                {
//                    text_decision.setError("Cannot be left empty");
//                    hiddenLayoutSectionFour.setVisibility(View.VISIBLE);
//                }
//
//
//                else
//                {
//                    Intent intent = new Intent(ChangeOrdersNew.this, AllChangeOrders.class);
//                    Toast.makeText(ChangeOrdersNew.this, "Change Orders Saved", Toast.LENGTH_SHORT).show();
//                    startActivity(intent);
//                }
//            }
//        });
    }
}
