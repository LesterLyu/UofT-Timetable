package com.lvds2000.uoft_timetable;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.URL;

/**
 * Created by LV on 2015-10-31.
 * @author Lester Lyu
 */
public class WebViewActivity extends AppCompatActivity {
    public WebView webView;
    ProgressBar progressBarbar;
    public String title ;
    public URL arr[] = new URL[2];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.custom_webview);
        webView = (WebView)findViewById(R.id.webView2);
        progressBarbar = (ProgressBar) findViewById(R.id.pB1);
        setTitle(title);
        try {
            show_content();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //google analytics
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_a08__exercise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void show_content()throws IOException{
        //Progress Display
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if(progress < 100 && progressBarbar.getVisibility() == ProgressBar.GONE){
                    progressBarbar.setVisibility(ProgressBar.VISIBLE);
                }
                progressBarbar.setProgress(progress);
                if(progress == 100) {
                    progressBarbar.setVisibility(ProgressBar.GONE);
                }
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
    }

}
