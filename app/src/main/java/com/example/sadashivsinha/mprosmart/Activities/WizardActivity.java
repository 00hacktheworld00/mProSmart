package com.example.sadashivsinha.mprosmart.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.Fragments.WizardFragment;
import com.example.sadashivsinha.mprosmart.R;

public class WizardActivity  extends ActionBarActivity {

    private MyPagerAdapter adapter;
    private ViewPager pager;
    private TextView previousButton;
    private TextView nextButton;
    private TextView navigator;
    private int currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        currentItem = 0;

        pager = (ViewPager) findViewById(R.id.activity_wizard_universal_pager);
        previousButton = (TextView) findViewById(R.id.activity_wizard_universal_previous);
        nextButton = (TextView) findViewById(R.id.activity_wizard_universal_next);
        navigator = (TextView) findViewById(R.id.activity_wizard_universal_possition);

        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(currentItem);

        setNavigator();

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int position) {
                // TODO Auto-generated method stub
                setNavigator();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
				/*if (pager.getCurrentItem() != 0) {
					pager.setCurrentItem(pager.getCurrentItem() - 1);
				}
				setNavigator();*/
                Toast.makeText(WizardActivity.this, "Skip",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WizardActivity.this, LoginScreen.class);
                startActivity(intent);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (pager.getCurrentItem() != (pager.getAdapter().getCount() - 1)) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1);
                } else {
                    Intent intent = new Intent(WizardActivity.this, LoginScreen.class);
                    startActivity(intent);
                }
                setNavigator();
            }
        });

    }

    public void setNavigator() {
        String navigation = "";
        for (int i = 0; i < adapter.getCount(); i++) {
            if (i == pager.getCurrentItem()) {
                navigation += getString(R.string.material_icon_point_full)
                        + "  ";
            } else {
                navigation += getString(R.string.material_icon_point_empty)
                        + "  ";
            }
        }
        navigator.setText(navigation);
    }

    public void setCurrentSlidePosition(int position) {
        this.currentItem = position;
    }

    public int getCurrentSlidePosition() {
        return this.currentItem;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return WizardFragment.newInstance(position);
            } else if (position == 1) {
                return WizardFragment.newInstance(position);
            } else {
                return WizardFragment.newInstance(position);
            }
        }
    }
}