package com.lvds2000.utsccsuntility.A08;

import android.os.Bundle;

import com.lvds2000.utsccsuntility.MAsyncTask;
import com.lvds2000.utsccsuntility.WebViewActivity;

import java.io.IOException;
import java.net.URL;

public class A08_Lec extends WebViewActivity {

    public static String url = "http://www.utsc.utoronto.ca/~bharrington/csca08/lectures.shtml";

    protected void onCreate(Bundle savedInstanceState) {
        title = "Weekly Readings/Slides";
        super.onCreate(savedInstanceState);
        //wb = webView;

    }

    public void show_content()throws IOException{
        super.show_content();
        arr[0] = new URL(url);
        new URLReader_08_lec().execute(arr);

    }
    class URLReader_08_lec extends MAsyncTask {
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

