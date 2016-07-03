package com.lvds2000.utsccsuntility.A20;

import android.os.Bundle;

import com.lvds2000.utsccsuntility.MAsyncTask;
import com.lvds2000.utsccsuntility.WebViewActivity;

import java.io.IOException;
import java.net.URL;

public class A20_Exe extends WebViewActivity {
    public static String url = "http://www.utsc.utoronto.ca/~bretscher/a20/assignments.html";
    //public static WebView wb;

    protected void onCreate(Bundle savedInstanceState) {
        title = "Exercise/Assignment";
        super.onCreate(savedInstanceState);
    }

    public void show_content()throws IOException{
        super.show_content();
        arr[0] = new URL(url);
        new URLReader_20_exe().execute(arr);

    }

    class URLReader_20_exe extends MAsyncTask {
        protected void onPostExecute(String web) {
            web = web.substring(web.indexOf("<h2>Exercises</h2>"), web.indexOf("</body>"));
            webView.loadDataWithBaseURL(url, "<html>" +
                    "<style type=\"text/css\">\n" +
                    "a {\n" +
                    "\tcolor: #037;\n" +
                    "}" +
                    "</style>" +
                    web + "</html>", "text/html", "UTF-8", null);
            //A20_Exe.tv1.getSettings().supportZoom();
        }
        protected void onPreExecute(){}

    }
}
