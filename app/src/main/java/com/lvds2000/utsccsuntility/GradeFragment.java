package com.lvds2000.utsccsuntility;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.lvds2000.AcornAPI.auth.Acorn;
import com.lvds2000.AcornAPI.exception.LoginFailedException;
import com.lvds2000.utsccsuntility.utils.UserInfo;


public class GradeFragment extends Fragment {

    private WebView webView;
    private Activity activity;
    // Required empty public constructor

    public GradeFragment() {
    }

    public static GradeFragment newInstance() {
        GradeFragment fragment = new GradeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View rootView = inflater.inflate(R.layout.custom_webview_grade, container, false);

        final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainerGrade);
//        swipeContainer.post(new Runnable() {
//            @Override
//            public void run() {
//                swipeContainer.setRefreshing(true);
//            }
//        });
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
        final ProgressBar progressBarbar = (ProgressBar) rootView.findViewById(R.id.pB1);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBarbar.getVisibility() == ProgressBar.GONE) {
                    progressBarbar.setVisibility(ProgressBar.VISIBLE);
                }
                progressBarbar.setProgress(progress);
                if (progress == 100) {
                    progressBarbar.setVisibility(ProgressBar.GONE);
                }
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        String gradeHtml = DrawerActivity.loadString("gradeHtml");
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
        swipeContainer.setRefreshing(true);
        new Thread() {
            @Override
            public void run() {
                if(DrawerActivity.acorn == null || UserInfo.isUserPassChanged()){
                    DrawerActivity.acorn = new Acorn(UserInfo.getUsername(), UserInfo.getPassword());
                }
                try {
                    DrawerActivity.acorn.doLogin();
                } catch (LoginFailedException e) {
                    e.printStackTrace();
                }
                final String gradeHtml[] = new String[1];
                gradeHtml[0] = "<link rel=\"stylesheet\" href=\"./css/bootstrap.min.css\">\n" +
                        "<link rel=\"stylesheet\" href=\"./css/bootstrap-theme.min.css\">\n" +
                        "<script src=\"./jquery.min.js\"></script>\n" +
                        "<script src=\"./js/bootstrap.min.js\"></script>" + "";


                gradeHtml[0] +=  DrawerActivity.acorn.getGradeManager().getGradeHtml();

                DrawerActivity.saveString("gradeHtml", gradeHtml[0]);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadDataWithBaseURL("file:///android_asset/", gradeHtml[0], "text/html", "UTF-8", null);
                        swipeContainer.setRefreshing(false);
                    }
                });
            }
        }.start();
    }

}
