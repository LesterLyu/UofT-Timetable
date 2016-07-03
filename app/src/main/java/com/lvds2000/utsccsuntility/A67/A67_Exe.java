package com.lvds2000.utsccsuntility.A67;

import android.os.Bundle;

import com.lvds2000.utsccsuntility.MAsyncTask;
import com.lvds2000.utsccsuntility.WebViewActivity;

import java.io.IOException;
import java.net.URL;

public class A67_Exe extends WebViewActivity {
    public static String url = "http://www.utsc.utoronto.ca/~bretscher/a67/assignments.html";

    protected void onCreate(Bundle savedInstanceState) {
        title = "Exercise/Assignment";
        super.onCreate(savedInstanceState);
    }

    public void show_content()throws IOException{
        super.show_content();
        arr[0] = new URL(url);
        new URLReader_67_exe().execute(arr);

    }

    class URLReader_67_exe extends MAsyncTask {


        protected void onPostExecute(String web) {
            web = web.substring(web.indexOf("<h2>Exercise 1</h2>"), web.indexOf("</body>"));
            webView.loadDataWithBaseURL(url, "<html>" +
                    "<style type=\"text/css\">\n" +
                    "a {\n" +
                    "\tcolor: #9999cc;\n" +
                    "}" +
                    "</style>" +
                    web + "</html>", "text/html", "UTF-8", null);

        }
        protected void onPreExecute(){}

    }
}
