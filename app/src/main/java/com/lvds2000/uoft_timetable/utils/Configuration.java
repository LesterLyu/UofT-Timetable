package com.lvds2000.uoft_timetable.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lvds2000.uoft_timetable.TimetableFragment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;

/**
 * Created by lvds2 on 2/26/2017.
 *
 */

public class Configuration {

    private final static String COOKIES_FILE = "cookies.ser";

    @Deprecated
    public static void saveCookies(Map<String, List<Cookie>> cookies, Context context){

        try {
            FileOutputStream fos = context.openFileOutput(COOKIES_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(cookies);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Deprecated
    @SuppressWarnings("unchecked")
    public static  Map<String, List<Cookie>> loadCookies(Context context){
        Map<String, List<Cookie>> cookies = null;
        try {
            FileInputStream fis = context.openFileInput(COOKIES_FILE);
            ObjectInputStream is = new ObjectInputStream(fis);
            cookies = (Map<String, List<Cookie>>) is.readObject();
            is.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cookies;
    }

    public static void loadColor(Context context){
        int totalCourseNum = TimetableFragment.courseList.length;
        int[] color = loadIntArray("color", context);
        for(int i=0; i<totalCourseNum; i++) {
            try{
                TimetableFragment.courseList[i].color = color[i];
                //System.out.println("Load Color: " + color[i]);
            }catch (Exception e){
                TimetableFragment.courseList[i].color = 0;
            }

        }
    }
    public static void saveColor(Context context){
        int totalCourseNum = TimetableFragment.courseList.length;
        int[] color = new int[totalCourseNum];
        for(int i=0; i<totalCourseNum; i++) {
            color[i] = TimetableFragment.courseList[i].color;
        }
        saveIntArray(color, "color", context);
    }


    public static boolean saveArray(String[] array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"_size", array.length);
        for(int i=0;i<array.length;i++)
            editor.putString(arrayName + "_" + i, array[i]);
        return editor.commit();
    }
    public static String[] loadArray(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        String array[] = new String[size];
        for(int i=0;i<size;i++)
            array[i] = prefs.getString(arrayName + "_" + i, "");
        return array;
    }
    public static boolean saveBooleanArray(boolean[] myBoolean, String booleanName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferenceBoolean", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(booleanName + "_size", myBoolean.length);
        for(int i=0;i<myBoolean.length;i++)
            editor.putBoolean(booleanName + "_" + i, myBoolean[i]);
        return editor.commit();
    }
    public static boolean[] loadBooleanArray(String booleanName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferenceBoolean", 0);
        int size = prefs.getInt(booleanName + "_size", 0);
        boolean array[] = new boolean[size];
        for(int i=0;i<size;i++)
            array[i] = prefs.getBoolean(booleanName + "_" + i, false);
        return array;
    }
    public static boolean saveIntArray(int[] myInt, String intName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("preferenceInt", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(intName + "_size", myInt.length);
        for(int i=0;i<myInt.length;i++)
            editor.putInt(intName + "_" + i, myInt[i]);
        return editor.commit();
    }
    public static int[] loadIntArray(String intName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("preferenceInt", 0);
        int size = prefs.getInt(intName + "_size", 0);
        int array[] = new int[size];
        for(int i=0;i<size;i++)
            array[i] = prefs.getInt(intName + "_" + i, 0);
        return array;
    }
    public static boolean saveString(String name, String s, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("com.lvds2000.utsccsuntility_preferences", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(name, s);
        return editor.commit();
    }
    public static String loadString(String name, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("com.lvds2000.utsccsuntility_preferences", 0);
        return prefs.getString(name, "");
    }
    public static boolean loadBoolean(String name, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("com.lvds2000.utsccsuntility_preferences", 0);
        return prefs.getBoolean(name, false);
    }
    public static int loadInt(String name, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("com.lvds2000.utsccsuntility_preferences", 0);
        return prefs.getInt(name, 0);
    }

}
