package com.example.sadashivsinha.mprosmart.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.example.sadashivsinha.mprosmart.R;

public class WebViewWbsGantt extends AppCompatActivity {

    String urlLink = "http://52.76.152.185/ganttchart/samples/01_initialization/06_touch_forced.html";
    String localhostAddress = "http://192.168.0.4/ganttchart/samples/01_initialization/06_touch_forced.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_wbs_gantt);

        final WebView browser = (WebView) findViewById(R.id.webview);

        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setUseWideViewPort(true);
        browser.getSettings().setLoadWithOverviewMode(true);
        browser.getSettings().setSupportZoom(true);
        browser.getSettings().setBuiltInZoomControls(true);

        browser.loadUrl(urlLink);
    }
}
