package com.lvds2000.utsccsuntility;

import android.app.Application;
import android.content.Context;

/**
 * Created by LV on 2015-10-08.
 */
public class MyApplication extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}