package com.lvds2000.utsccsuntility.Generic;

import android.os.Bundle;

import com.lvds2000.utsccsuntility.MAsyncTask;
import com.lvds2000.utsccsuntility.WebViewActivity;

import java.io.IOException;
import java.net.URL;

public class Exercise extends WebViewActivity {

    public static String url = "http://www.utsc.utoronto.ca/~bharrington/csca48/assignments.shtml";

    protected void onCreate(Bundle savedInstanceState) {
        title = "Exercise/Assignment";
        super.onCreate(savedInstanceState);

    }

    public void show_content()throws IOException{
        super.show_content();
        arr[0] = new URL(url);
        new URLReader_08_exe().execute(arr);

    }
    class URLReader_08_exe extends MAsyncTask {
        protected void onPostExecute(String web) {
            web = web.substring(web.indexOf("<div class=\"content\">"), web.indexOf("</body>"));
            webView.loadDataWithBaseURL(url, "<html>"+
                    "<style type=\"text/css\">\n" +
                    "a {\n" +
                    "\tcolor: #037;\n" +
                    "}" +
                    "</style>" +
                    web + "</html>", "text/html", "UTF-8", null);
        }
        protected void onPreExecute(){}

    }
}

