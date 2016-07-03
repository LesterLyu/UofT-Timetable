package com.lvds2000.utsccsuntility.A67;

import android.os.Bundle;

import com.lvds2000.utsccsuntility.MAsyncTask;
import com.lvds2000.utsccsuntility.WebViewActivity;

import java.io.IOException;
import java.net.URL;

public class A67_Lec extends WebViewActivity {
    public static String url = "http://www.utsc.utoronto.ca/~bretscher/a67/lectures.html";

    protected void onCreate(Bundle savedInstanceState) {
        title = "Lecture";
        super.onCreate(savedInstanceState);
    }

    public void show_content()throws IOException{
        super.show_content();
        arr[0] = new URL(url);
        new URLReader_67_Lec().execute(arr);

    }


    class URLReader_67_Lec extends MAsyncTask {


        protected void onPostExecute(String web) {

            web = web.substring(web.indexOf("<div\nclass=\"content\">"),web.indexOf("</div></center>"));
            webView.loadDataWithBaseURL("http://www.utsc.utoronto.ca/~bretscher/a67/lectures.html", "<html>" +
                    "<style type=\"text/css\">\n" +
                    "a{\n" +
                    "\tcolor: #9999cc;\n" +
                    "}"+
                    "table {\n" +
                    "\ttext-align : center;\n" +
                    "\tfont-size: small;\n" +
                    "\tcell-padding : 5px;\n" +
                    "\tborder-width: 2px 2px 2px 2px;\n" +
                    "\tborder-spacing: 2px;\n" +
                    "\tborder-style: outset outset outset outset;\n" +
                    "\tborder-color: gray gray gray gray;\n" +
                    "\tborder-collapse: separate;\n" +
                    "\tbackground-color: white;\n" +
                    "}\n" + "td, th {\n" +
                    "\tborder-width: 1px;\n" +
                    "\tpadding: 5px;\n" +
                    "\tborder-style: inset;\n" +
                    "\tborder-color: gray;\n" +
                    "\tbackground-color: white;\n" +
                    "\tcolor : #000000;\n" +
                    "}\n" +
                    ".borderless table {\n" +
                    "\tfont-size: medium;\n" +
                    "\ttext-align : left;\n" +
                    "\tborder-style: none;\n" +
                    "\tbackground-color: white;\n" +
                    "}\n" +
                    ".borderless td, th {\t\n" +
                    "\tborder-style: none;\n" +
                    "\tbackground-color: white;\n" +
                    "\tcolor : #000000;\n" +
                    "\tpadding: 5px\n" +
                    "}" +
                    "</style>" +
                    "</head>" + web + "</html>", "text/html", "UTF-8", null);
        }
        protected void onPreExecute(){ }

    }
}
