package com.lvds2000.utsccsuntility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

public class DownloadCourses extends AppCompatActivity {
    private static String ACORN_POSTS_JSON_URL =
            "https://acorn.utoronto.ca/sws/rest/enrolment/eligible-registrations";
    private static String ACORN_COURSES_JSON_URL =
            "https://acorn.utoronto.ca/sws/rest/enrolment/plan";
    WebView webView;
    ProgressBar progressBarbar;
    Runnable mStatusChecker, mStatusChecker2;
    boolean isRunnableWork = true, isRunnableWork2 = true;
    private Handler mHandler = new Handler();
    private Context context;
    private static ProgressDialog progress;
    public static String postJson, courseJson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_webview);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //Theme setting
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar));
        isRunnableWork2 = true;
        isRunnableWork = true;
        webView = (WebView)findViewById(R.id.webView2);
        progressBarbar = (ProgressBar) findViewById(R.id.pB1);
        try {
            show_content();
        } catch (IOException e) {
            e.printStackTrace();
        }
        context = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timetable, menu);
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
                this.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
        }
        else {
            cookieManager.removeAllCookie();
        }
        isRunnableWork2 = false;
        isRunnableWork = false;
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearMatches();

        System.out.println("Reset download module");
        super.onBackPressed();

    }


    public void show_content() throws IOException {
        final Activity activity = this;

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


        webView.setWebViewClient(new WebViewClient() {
            int cnt = 0;
            boolean logined = false;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                System.out.println(url);

                view.loadUrl(url);
                if (url.contains("welcome.do")) {
                    System.out.println("Successfully logged in");
                    activity.setTitle("Processing");
                    webView.loadUrl(ACORN_POSTS_JSON_URL);
                    System.out.println("Retrieving data...");
                    progress = new ProgressDialog(activity);
                    progress.setTitle("Loading");
                    progress.setMessage("Retrieving data...");
                    progress.setCancelable(false);
                    progress.show();
                }
                return false; // then it is not handled by default action
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                autoLogin(view, url);
                if(url.equals(ACORN_POSTS_JSON_URL)){
                    // get json data
                    webView.loadUrl("javascript:window.HTMLOUT.processPostJson(document.getElementsByTagName('pre')[0].innerHTML);");
                    final boolean[] running = {true};
                    mStatusChecker = (new Runnable() {
                        public void run() {
                            System.out.println("Running");
                            if(running[0] && postJson != null){
                                running[0] = false;
                                // process json
                                JsonParser parser = new JsonParser();
                                final JsonArray jsonArray = parser.parse(postJson).getAsJsonArray();
                                // if the student has more than one department, let them select which one they want to import.
                                if(jsonArray.size() > 1){
                                    CharSequence  department[] = new CharSequence [jsonArray.size()];
                                    for(int i = 0; i < jsonArray.size(); i ++){
                                        department[i] = Html.fromHtml(jsonArray.get(i).getAsJsonObject().get("post").getAsJsonObject().get("description").getAsString() + "(" +
                                                jsonArray.get(i).getAsJsonObject().get("candidacyPostCode").getAsString() + ") " +
                                                jsonArray.get(i).getAsJsonObject().get("sessionDescription").getAsString());
                                    }
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setCancelable(false);
                                    builder.setTitle("Select one").setItems(department, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            JsonObject obj = jsonArray.get(which).getAsJsonObject();
                                            String candidacyPostCode = obj.get("candidacyPostCode").getAsString();
                                            String candidacySessionCode = obj.get("candidacySessionCode").getAsString();
                                            String sessionCode = obj.get("sessionCode").getAsString();
                                            System.out.println(ACORN_COURSES_JSON_URL + "?candidacyPostCode=" + candidacyPostCode +
                                                    "&candidacySessionCode=" + candidacySessionCode +
                                                    "&sessionCode=" + sessionCode);
                                            webView.loadUrl(ACORN_COURSES_JSON_URL + "?candidacyPostCode=" + candidacyPostCode +
                                                    "&candidacySessionCode=" + candidacySessionCode +
                                                    "&sessionCode=" + sessionCode);
                                            dialog.cancel();
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }

                            }
                        }
                    });
                    mHandler.postDelayed(mStatusChecker, 500);
                    mStatusChecker.run();
                }
                else if(url.contains(ACORN_COURSES_JSON_URL)) {
                    System.out.println("IN ACORN_COURSES_JSON_URL");
                    // get json data
                    webView.loadUrl("javascript:window.HTMLOUT.processCourseJson(document.getElementsByTagName('pre')[0].innerHTML);");
                    final boolean[] running = {true};
                    mStatusChecker2 = (new Runnable() {
                        public void run() {
                            System.out.println("Running");
                            if (running[0] && courseJson != null) {
                                running[0] = false;
                                // cannot be empty json array
                                if(!courseJson.equals("[]")) {
                                    Fragment_Timetable.setCourseJson(courseJson);
                                    // process json
                                    DrawerActivity.saveString("courseJson", courseJson, context);
                                }
                                //  clear cookie&cache
                                CookieManager cookieManager = CookieManager.getInstance();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    cookieManager.removeAllCookies(null);
                                } else {
                                    cookieManager.removeAllCookie();
                                }
                                webView.clearCache(true);
                                webView.clearFormData();
                                webView.clearHistory();
                                webView.clearMatches();
                                //go back
                                progress.dismiss();
                                progress.cancel();
                                // return
                                Intent returnIntent = new Intent();
                                setResult(RESULT_CANCELED, returnIntent);
                                finish();
                            }
                        }
                    });
                    mHandler.postDelayed(mStatusChecker2, 500);
                    mStatusChecker.run();
                }
            }

            public void autoLogin(WebView view, String url){
                System.out.println(cnt + "   " + webView.getContentHeight() + " " + url);
                String username = DrawerActivity.loadString("username", context);
                String password = "";
                try {
                    StringEncryptor encryptor = new StringEncryptor(context);
                    password = encryptor.decrypt("pass");
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
                    e.printStackTrace();
                }
                System.out.println("Javascript enabled:" + webView.getSettings().getJavaScriptEnabled());

                // This is the automatic enter username/password part
                if (!logined && url.contains("https://weblogin.utoronto.ca") && DrawerActivity.loadBoolean("auto_login", context)) {
                    System.out.println("Set username");
                    webView.loadUrl("javascript:(function(){\n" +
                            "  document.getElementById('inputID').value='" + username + "';\n" +
                            "  document.getElementById('inputPassword').value='" + password + "';\n" +
                            "  document.getElementsByTagName('form')[0].submit();\n" +
                            "})();");
                    logined = true;
                }
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        webView.loadUrl("https://acorn.utoronto.ca/sws");

    }

}

class MyJavaScriptInterface {
    public static int totalCourseNum;
    int cnt = 0;

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processPostJson(String html){
        System.out.println(html);
        DownloadCourses.postJson = html;
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processCourseJson(String html){
        System.out.println(html);
        DownloadCourses.courseJson = html;
    }
}