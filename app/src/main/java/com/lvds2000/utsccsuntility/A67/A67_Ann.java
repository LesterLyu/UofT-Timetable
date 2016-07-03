package com.lvds2000.utsccsuntility.A67;

import android.os.Bundle;

import com.lvds2000.utsccsuntility.MAsyncTask;
import com.lvds2000.utsccsuntility.WebViewActivity;

import java.io.IOException;
import java.net.URL;

public class A67_Ann extends WebViewActivity {
    public static String url = "http://www.utsc.utoronto.ca/~bretscher/a67/news.html";


    protected void onCreate(Bundle savedInstanceState) {
        title = "Announcement";
        super.onCreate(savedInstanceState);
    }

    public void show_content() throws IOException {
        super.show_content();
        arr[0] = new URL(url);
        new URLReader_67_ann().execute(arr);

    }

    class URLReader_67_ann extends MAsyncTask {


        protected void onPostExecute(String web) {
            web = web.substring(web.indexOf("<div class=\"content\">"), web.indexOf("</body>"));
            webView.loadDataWithBaseURL(url, "<html>" + web + "</html>", "text/html", "UTF-8", null);

        }
        protected void onPreExecute(){}

    }
}
