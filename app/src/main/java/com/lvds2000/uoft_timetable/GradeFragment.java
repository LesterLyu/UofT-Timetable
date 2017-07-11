package com.lvds2000.uoft_timetable;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.lvds2000.AcornAPI.auth.Acorn;
import com.lvds2000.AcornAPI.auth.SimpleListener;
import com.lvds2000.uoft_timetable.utils.Configuration;
import com.lvds2000.uoft_timetable.utils.UserInfo;


public class GradeFragment extends Fragment {

    private WebView webView;
    private Activity activity;
    // Required empty public constructor

    public GradeFragment() {
    }

    public static GradeFragment newInstance() {


        return new GradeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View rootView = inflater.inflate(R.layout.custom_webview_grade, container, false);

        final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainerGrade);

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshHtml(swipeContainer);
            }
        });

        webView = (WebView) rootView.findViewById(R.id.webView3);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 ) {
                    swipeContainer.setRefreshing(true);
                }
                if (progress == 100) {
                    swipeContainer.setRefreshing(false);
                }
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        String gradeHtml = Configuration.loadString("gradeHtml", activity);
        if (gradeHtml.isEmpty()) {
            // gather grade info
            refreshHtml(swipeContainer);
        } else {
            webView.loadDataWithBaseURL("file:///android_asset/", gradeHtml, "text/html", "UTF-8", null);
        }
        //Log.d("gradeHtml", gradeHtml);
        return rootView;
    }

    public void refreshHtml(final SwipeRefreshLayout swipeContainer) {
        if(UserInfo.getUsername(activity).isEmpty() || UserInfo.getPassword(activity).isEmpty()){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swipeContainer.setRefreshing(false);
                }
            });
            UserInfo.promptInputUserPassAndLogin((DrawerActivity) activity, swipeContainer, this);
            return;
        }
        swipeContainer.setRefreshing(true);
        new Thread() {
            @Override
            public void run() {

                if(UserInfo.isUserPassChanged(activity)){
                    DrawerActivity.acorn = new Acorn(UserInfo.getUsername(activity), UserInfo.getPassword(activity));
                }
                DrawerActivity.acorn.doLogin(new SimpleListener() {
                    @Override
                    public void success() {
                        final String gradeHtml[] = new String[1];
                        gradeHtml[0] = "<link rel=\"stylesheet\" href=\"./css/bootstrap.min.css\">\n" +
                                "<link rel=\"stylesheet\" href=\"./css/bootstrap-theme.min.css\">\n" +
                                "<script src=\"./jquery.min.js\"></script>\n" +
                                "<script src=\"./js/bootstrap.min.js\"></script>" + "";


                        gradeHtml[0] +=  DrawerActivity.acorn.getGradeManager().getGradeHtml();

                        Configuration.saveString("gradeHtml", gradeHtml[0], activity);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                webView.loadDataWithBaseURL("file:///android_asset/", gradeHtml[0], "text/html", "UTF-8", null);
                            }
                        });
                    }

                    @Override
                    public void failure(Exception e) {
                        e.printStackTrace();
                        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage(e.getMessage())
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, int id) {
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.cancel();
                                            }
                                        });
                                    }
                                });
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeContainer.setRefreshing(false);
                                builder.create().show();
                            }
                        });
                    }
                });

            }
        }.start();
    }

}
