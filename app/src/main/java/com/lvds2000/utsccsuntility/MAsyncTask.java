package com.lvds2000.utsccsuntility;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by LV on 2015-09-23.
 * @author Lester Lyu
 */
public class MAsyncTask extends AsyncTask<URL, Void, String> {
    private String inputLine, web;

    private String saved="";
    @Override
    protected String doInBackground(URL... url) {

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(url[0].openStream(), "UTF-8"), 8);
            String line = null;

            while ((inputLine = in.readLine()) != null){
                //System.out.println(inputLine);
                web = web + "\n" + inputLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        saved = web;


        return web;
    }

}
