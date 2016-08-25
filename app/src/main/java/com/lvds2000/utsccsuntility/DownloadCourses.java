package com.lvds2000.utsccsuntility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lvds2000.entity.Course;
import com.lvds2000.entity.enrol.EnrolledCourse;
import com.lvds2000.entity.plan.PlannedCourse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

public class DownloadCourses extends AppCompatActivity {
    private static String ACORN_POSTS_JSON_URL =
            "https://acorn.utoronto.ca/sws/rest/enrolment/eligible-registrations";
    private static String ACORN_PLANNED_COURSES_JSON_URL =
            "https://acorn.utoronto.ca/sws/rest/enrolment/plan";
    private static String ACORN_ENROLLED_COURSES_JSON_URL =
            "https://acorn.utoronto.ca/sws/rest/enrolment/course/enrolled-courses";
    private static String ACORN_ENROLLED_COURSES_JSON_URL_WITH_PARAMS;

    WebView webView;
    ProgressBar progressBarbar;
    Thread mStatusChecker, mStatusChecker2, mStatusChecker3;
    private Context context;
    private static ProgressDialog progress;
    public static String postJson, plannedCourseJson, enrolledCourseJson;
    List<Course> courseList = new ArrayList<Course>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_webview);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            //Theme setting
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar));
        }
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
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearMatches();
        postJson = null;
        plannedCourseJson = null;
        enrolledCourseJson = null;

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
                    webView.setVisibility(View.GONE);
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
                autoLogin(url);
                if(url.equals(ACORN_POSTS_JSON_URL)){
                    // get json data
                    webView.loadUrl("javascript:window.HTMLOUT.processPostJson(document.getElementsByTagName('pre')[0].innerHTML);");
                    mStatusChecker = new Thread(new Runnable() {
                        private AlertDialog.Builder builder;
                        private CharSequence[] department;
                        private JsonParser parser;
                        private JsonArray postJsonArray;

                        public void run(){
                            System.out.println("Running...");
                            while(postJson == null){
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            System.out.println(postJson);
                            // process json
                            parser = new JsonParser();
                            postJsonArray = parser.parse(postJson).getAsJsonArray();
                            // if the student has more than one department, let them select which one they want to import.
                            if(postJsonArray.size() > 1){
                                department = new CharSequence[postJsonArray.size()];
                                for(int i = 0; i < postJsonArray.size(); i ++){
                                    department[i] = Html.fromHtml(postJsonArray.get(i).getAsJsonObject().get("post").getAsJsonObject().get("description").getAsString() + "(" +
                                            postJsonArray.get(i).getAsJsonObject().get("candidacyPostCode").getAsString() + ") " +
                                            postJsonArray.get(i).getAsJsonObject().get("sessionDescription").getAsString());
                                }
                                builder = new AlertDialog.Builder(context);
                                builder.setCancelable(false);
                                builder.setTitle("Select one").setItems(department, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        processPostData(postJsonArray, which);
                                        dialog.cancel();
                                    }
                                });
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                });
                            }
                            else{
                                processPostData(postJsonArray, 0);
                            }
                        }
                    });
                    mStatusChecker.start();
                }
                else if(url.contains(ACORN_PLANNED_COURSES_JSON_URL)) {
                    System.out.println("IN ACORN_PLANNED_COURSES_JSON_URL");
                    // get json data
                    webView.loadUrl("javascript:window.HTMLOUT.processPlannedCourseJson(document.getElementsByTagName('pre')[0].innerHTML);");
                    mStatusChecker2 = new Thread(new Runnable() {
                        public void run() {
                            System.out.println("Running...");
                            while(plannedCourseJson == null){
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            // cannot be empty json array
                            if(!plannedCourseJson.equals("[]")) {
                                Gson gson = new Gson();
                                PlannedCourse[] plannedCourseList = gson.fromJson(plannedCourseJson, PlannedCourse[].class);
                                for(PlannedCourse plannedCourse: plannedCourseList){
                                    courseList.add(new Course(plannedCourse));
                                }
                            }
                            System.out.println(ACORN_ENROLLED_COURSES_JSON_URL_WITH_PARAMS);
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    webView.loadUrl(ACORN_ENROLLED_COURSES_JSON_URL_WITH_PARAMS);
                                }
                            });

                        }

                    });
                    mStatusChecker2.start();
                }
                else if(url.contains(ACORN_ENROLLED_COURSES_JSON_URL)) {
                    System.out.println("IN ACORN_ENROLLED_COURSES_JSON_URL");
                    // get json data
                    webView.loadUrl("javascript:window.HTMLOUT.processEnrolledCourseJson(document.getElementsByTagName('pre')[0].innerHTML);");
                    mStatusChecker3 = new Thread(new Runnable() {
                        private JsonParser parser;
                        private JsonObject courseJsonObject;
                        private JsonArray jsonArrayAPP;
                        private EnrolledCourse[] enrolledCourseList;
                        private JsonArray jsonArrayWAIT;
                        private EnrolledCourse[] waitlistedCourseList;
                        public void run() {
                            System.out.println("Running");
                            while(enrolledCourseJson == null){
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            Gson gson = new Gson();
                            // cannot be empty json array
                            if(!enrolledCourseJson.equals("{}")) {
                                // process json
                                parser = new JsonParser();
                                courseJsonObject = parser.parse(enrolledCourseJson).getAsJsonObject();
                                jsonArrayAPP = courseJsonObject.get("APP").getAsJsonArray();
                                enrolledCourseList = gson.fromJson(jsonArrayAPP, EnrolledCourse[].class);
                                for(EnrolledCourse enrolledCourse: enrolledCourseList){
                                    courseList.add(0, new Course(enrolledCourse));
                                }
                                if(courseJsonObject.has("WAIT")) {
                                    jsonArrayWAIT = courseJsonObject.get("WAIT").getAsJsonArray();
                                    waitlistedCourseList = gson.fromJson(jsonArrayWAIT, EnrolledCourse[].class);
                                    for (EnrolledCourse waitlistedCourse : waitlistedCourseList) {
                                        courseList.add(new Course(waitlistedCourse));
                                    }
                                }
                            }
                            // sort the courseList
                            Collections.sort(courseList, new Comparator<Course>() {
                                @Override
                                public int compare(Course o1, Course o2) {
                                    if(o1.getSectionCode().equalsIgnoreCase("S") && (o2.getSectionCode().equalsIgnoreCase("Y") || o2.getSectionCode().equalsIgnoreCase("F")))
                                        return 1;
                                    else if(o1.getSectionCode().equalsIgnoreCase("Y") && o2.getSectionCode().equalsIgnoreCase("F"))
                                        return 1;
                                    else if(!o1.getSectionCode().equalsIgnoreCase(o2.getSectionCode()))
                                        return -1;
                                    else{
                                        return o1.getCourseCode().compareToIgnoreCase(o2.getCourseCode());
                                    }
                                }
                            });

                            String json =  gson.toJson(courseList);
                            TimetableFragment.setCourseJson(json);
                            DrawerActivity.saveString("courseJson", json, context);
                            System.out.println(json);
                            System.out.println("back");
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    finishedAndReturn();
                                }
                            });
                        }
                    });
                    mStatusChecker3.start();
                }
            }

            public void autoLogin(String url) {
                // This is the automatic enter username/password part
                if (!logined && url.contains("https://weblogin.utoronto.ca") && DrawerActivity.loadBoolean("auto_login", context)) {
                    System.out.println("Set username");
                    System.out.println(cnt + "   " + webView.getContentHeight() + " " + url);
                    String username = DrawerActivity.loadString("username", context);
                    String password = "";
                    try {
                        StringEncryptor encryptor = new StringEncryptor(context);
                        password = encryptor.decrypt("pass");
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
                        e.printStackTrace();
                    }
                    webView.loadUrl("javascript:(function(){\n" +
                            "  document.getElementById('inputID').value='" + username + "';\n" +
                            "  document.getElementById('inputPassword').value='" + password + "';\n" +
                            "  document.getElementsByTagName('form')[0].submit();\n" +
                            "})();");
                    logined = true;
                }
            }
            private void processPostData(JsonArray postJsonArray, int index){
                JsonObject obj = postJsonArray.get(index).getAsJsonObject();
                final String candidacyPostCode = obj.get("candidacyPostCode").getAsString();
                final String candidacySessionCode = obj.get("candidacySessionCode").getAsString();
                final String sessionCode = obj.get("sessionCode").getAsString();
                System.out.println(ACORN_PLANNED_COURSES_JSON_URL + "?candidacyPostCode=" + candidacyPostCode +
                        "&candidacySessionCode=" + candidacySessionCode +
                        "&sessionCode=" + sessionCode);
                JsonObject registrationParams = obj.get("registrationParams").getAsJsonObject();
                try {
                    ACORN_ENROLLED_COURSES_JSON_URL_WITH_PARAMS =
                            ACORN_ENROLLED_COURSES_JSON_URL +
                                    "?postCode=" +  URLEncoder.encode(registrationParams.get("postCode").getAsString(), "UTF-8") +
                                    "&postDescription=" + URLEncoder.encode(registrationParams.get("postDescription").getAsString(), "UTF-8") +
                                    "&sessionCode=" + URLEncoder.encode(registrationParams.get("sessionCode").getAsString(), "UTF-8") +
                                    "&sessionDescription=" + URLEncoder.encode(registrationParams.get("sessionDescription").getAsString(), "UTF-8") +
                                    "&status=" + URLEncoder.encode(registrationParams.get("status").getAsString(), "UTF-8") +
                                    "&assocOrgCode=" + URLEncoder.encode(registrationParams.get("assocOrgCode").getAsString(), "UTF-8") +
                                    "&acpDuration=" + URLEncoder.encode(registrationParams.get("acpDuration").getAsString(), "UTF-8") +
                                    "&levelOfInstruction=" + URLEncoder.encode(registrationParams.get("levelOfInstruction").getAsString(), "UTF-8") +
                                    "&typeOfProgram=" + URLEncoder.encode(registrationParams.get("typeOfProgram").getAsString(), "UTF-8") +
                                    "&designationCode1=" + URLEncoder.encode(registrationParams.get("designationCode1").getAsString(), "UTF-8") +
                                    "&primaryOrgCode=" + URLEncoder.encode(registrationParams.get("primaryOrgCode").getAsString(), "UTF-8") +
                                    "&secondaryOrgCode=" + URLEncoder.encode(registrationParams.get("secondaryOrgCode").getAsString(), "UTF-8") +
                                    "&collaborativeOrgCode=" + URLEncoder.encode(registrationParams.get("collaborativeOrgCode").getAsString(), "UTF-8") +
                                    "&adminOrgCode=" + URLEncoder.encode(registrationParams.get("adminOrgCode").getAsString(), "UTF-8") +
                                    "&coSecondaryOrgCode=" + URLEncoder.encode(registrationParams.get("coSecondaryOrgCode").getAsString(), "UTF-8") +
                                    "&yearOfStudy=" + URLEncoder.encode(registrationParams.get("yearOfStudy").getAsString(), "UTF-8") +
                                    "&postAcpDuration=" + URLEncoder.encode(registrationParams.get("postAcpDuration").getAsString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        webView.loadUrl(ACORN_PLANNED_COURSES_JSON_URL + "?candidacyPostCode=" + candidacyPostCode +
                                "&candidacySessionCode=" + candidacySessionCode +
                                "&sessionCode=" + sessionCode);
                    }
                });

            }

            public void finishedAndReturn(){
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
                postJson = null;
                plannedCourseJson = null;
                enrolledCourseJson = null;
                //go back
                progress.dismiss();
                progress.cancel();
                // return
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }


        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        webView.loadUrl("https://acorn.utoronto.ca/sws");

    }


}

class MyJavaScriptInterface {

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processPostJson(String html){
        System.out.println(html);
        DownloadCourses.postJson = html;
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processPlannedCourseJson(String html){
        System.out.println(html);
        DownloadCourses.plannedCourseJson = html;
    }
    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processEnrolledCourseJson(String html){
        System.out.println(html);
        DownloadCourses.enrolledCourseJson = html;
    }
}