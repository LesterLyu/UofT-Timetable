package com.lvds2000.utsccsuntility.A20;

import android.os.Bundle;

import com.lvds2000.utsccsuntility.MAsyncTask;
import com.lvds2000.utsccsuntility.WebViewActivity;

import java.io.IOException;
import java.net.URL;

public class A20_Ann extends WebViewActivity {
    public static String url = "http://www.utsc.utoronto.ca/~bretscher/a20/news.html";


    protected void onCreate(Bundle savedInstanceState) {

        title = "Announcement";
        super.onCreate(savedInstanceState);
    }

    public void show_content() throws IOException {
        super.show_content();
        arr[0] = new URL(url);
        new URLReader_20_ann().execute(arr);

    }


    class URLReader_20_ann extends MAsyncTask {

        protected void onPostExecute(String web) {
            web = web.substring(web.indexOf("<div class=\"content\">"), web.indexOf("</body>"));
            webView.loadDataWithBaseURL(url, "<html>" + web + "</html>", "text/html", "UTF-8", null);

        }
        protected void onPreExecute(){}
    }
}
