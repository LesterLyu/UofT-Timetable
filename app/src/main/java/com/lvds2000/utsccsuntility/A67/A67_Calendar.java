package com.lvds2000.utsccsuntility.A67;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.lvds2000.utsccsuntility.R;

import java.io.IOException;

public class A67_Calendar extends AppCompatActivity {

    //public static WebView webView;
    WebView tv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);


        super.onCreate(savedInstanceState);
        tv1 = new WebView(this);
        setContentView(tv1);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //Theme setting
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar));
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.md_indigo_700));
        } else {
            // Implement this feature without material design
        }
        try {
            show_content();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_a67__calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                this.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void show_content()throws IOException {
        final Activity activity = this;
        //Progress Display
        tv1.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                activity.setTitle("Loading..."+progress+"%" );
                activity.setProgress(progress * 100);

                if (progress == 100)
                    activity.setTitle("Calendar");
            }
        });
        WebSettings webSettings = tv1.getSettings();
        webSettings.setJavaScriptEnabled(true);
        tv1.loadUrl("https://calendar.google.com/calendar/embed?src=j7hdid9dr1tdtn4v871aoji5ho@group.calendar.google.com&ctz=America/Toronto&pli=1");

    }
}
