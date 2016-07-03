package com.lvds2000.utsccsuntility.A48;

import android.os.Bundle;

import com.lvds2000.utsccsuntility.MAsyncTask;
import com.lvds2000.utsccsuntility.WebViewActivity;

import java.io.IOException;
import java.net.URL;

public class Lec extends WebViewActivity {

    public static String url = "http://www.utsc.utoronto.ca/~bharrington/csca48/lectures.shtml";

    protected void onCreate(Bundle savedInstanceState) {
        title = "Weekly Readings/Slides";
        super.onCreate(savedInstanceState);
    }

    public void show_content()throws IOException{
        super.show_content();
        arr[0] = new URL(url);
        new URLReader().execute(arr);

    }
    class URLReader extends MAsyncTask {
        protected void onPostExecute(String web) {
            //System.out.println(web);
            web = web.substring(web.indexOf("<div class=\"content\">"), web.indexOf("</body>"));
            web = web.substring(web.indexOf("<div class=\"content\">"));
            webView.loadDataWithBaseURL(url, "<html>"+
                    "<style type=\"text/css\">\n" +
                    "a {\n" +
                    "\tcolor: #037;\n" +
                    "}" +
                    "</style>" +
                    web + "</html>", "text/html", "UTF-8", null);
            //webView.loadDataWithBaseURL(url, "<html>"+web+"</html>", "text/html", "UTF-8", null);
        }
        protected void onPreExecute(){}

    }
}

