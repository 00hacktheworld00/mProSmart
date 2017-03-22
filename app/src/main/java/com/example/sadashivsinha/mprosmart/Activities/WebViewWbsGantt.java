package com.example.sadashivsinha.mprosmart.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;

import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

public class WebViewWbsGantt extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_wbs_gantt);

        PreferenceManager pm = new PreferenceManager(this);
        String currentProjectId = pm.getString("projectId");

        String urlLink = "http://52.76.152.185/ganttchart/samples/01_initialization/06_touch_forced.html?projectId="
                + currentProjectId;

        Log.d("URL :", urlLink);

        final WebView browser = (WebView) findViewById(R.id.webview);

        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setUseWideViewPort(true);
        browser.getSettings().setLoadWithOverviewMode(true);
        browser.getSettings().setSupportZoom(true);
        browser.getSettings().setBuiltInZoomControls(true);

        browser.loadUrl(urlLink);
    }
}
