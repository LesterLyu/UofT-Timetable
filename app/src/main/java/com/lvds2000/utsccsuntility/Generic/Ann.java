package com.lvds2000.utsccsuntility.Generic;

import android.os.Bundle;

import com.lvds2000.utsccsuntility.MAsyncTask;
import com.lvds2000.utsccsuntility.WebViewActivity;

import java.io.IOException;
import java.net.URL;

public class Ann extends WebViewActivity {
    public static String url = "http://www.utsc.utoronto.ca/~bharrington/csca48/index.shtml";



    protected void onCreate(Bundle savedInstanceState) {
        title = "Announcement";
        super.onCreate(savedInstanceState);
    }

    public void show_content() throws IOException {
        super.show_content();
        arr[0] = new URL(url);
        new URLReader_08_ann().execute(arr);

    }
    class URLReader_08_ann extends MAsyncTask {
        protected void onPostExecute(String web) {
            web = web.substring(web.indexOf("<div class=\"content\">"), web.indexOf("</body>"));
            web = web.substring(web.indexOf("<h2>Announcements</h2>\n<p>"), web.indexOf("</div>"));
            webView.loadDataWithBaseURL(url, "<html>" + web + "</html>", "text/html", "UTF-8", null);
        }

        protected void onPreExecute() {}


    }
}
