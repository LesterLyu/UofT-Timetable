package com.lvds2000.utsccsuntility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import com.lvds2000.utsccsuntility.A08.DisplayCSCA08;
import com.lvds2000.utsccsuntility.A20.DisplayCSCA20;
import com.lvds2000.utsccsuntility.A48.InfoActivity;
import com.lvds2000.utsccsuntility.A67.DisplayCSCA67;

import java.util.ArrayList;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static Fragment fragment1;
    private static Fragment timetable_fall_fragment;
    private static Fragment timetable_winter_fragment;
    private static FragmentManager fragmentManager;
    public static float scale;
    public static DisplayMetrics displaymetrics;
    public static int dis_height;
    public static int dis_width ;
    public static Activity activity;
    private static Handler handler = new Handler();
    private static String mTitle = "Winter timetable";
    private static Context c;
    private static boolean initialized = false;
    public static boolean DEBUG = false, isFall = true;
    private static Menu navi_menu;
    private static SubMenu menu_extra_info;
    public static String versionName, currentVersionCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = this;
        versionName = BuildConfig.VERSION_NAME;
        currentVersionCode = "" + BuildConfig.VERSION_CODE;

        String lastVersionCode = loadString("versionCode", this);

        // first time launch or from update
        if(lastVersionCode.equals("") || lastVersionCode.equals("22") ){
            downloadCourses();
        }

        DEBUG = loadBoolean("debug_switch", this);




        displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        dis_height = displaymetrics.heightPixels;
        dis_width = displaymetrics.widthPixels;
        scale = displaymetrics.density;
        activity = this;

        fragment1 = new CourseListFragment();
        timetable_fall_fragment = TimetableFragment.newInstance("fall");
        timetable_winter_fragment = TimetableFragment.newInstance("winter");
        ///

        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();

        navi_menu = navigationView.getMenu();
        // version code
        View headerView = navigationView.getHeaderView(0);
        TextView versionText = (TextView) headerView.findViewById(R.id.versionText);
        versionText.setText("version " + versionName);

        // default timetable
        String defaultTimetable = loadString("defaultTimetable", c);

        if(defaultTimetable.equals("0")) {
            fragmentManager.beginTransaction().replace(R.id.flContent, timetable_fall_fragment).commit();
            setTitle("Fall timetable");
            navi_menu.getItem(1).setChecked(true);
        }
        else{
            fragmentManager.beginTransaction().replace(R.id.flContent, timetable_winter_fragment).commit();
            setTitle("Winter timetable");
            navi_menu.getItem(2).setChecked(true);
            isFall = false;
        }
        // optional course info
        menu_extra_info = navi_menu.getItem(4).getSubMenu();

        debugCourse();

        //google analytics
        if(!initialized)
            AnalyticsTrackers.initialize(this);
        initialized = true;
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
    }


    //  hard coded in 2015...
    public static void debugCourse(){
        menu_extra_info.findItem(R.id.nav_a08).setVisible(true);
        menu_extra_info.findItem(R.id.nav_a20).setVisible(true);
        menu_extra_info.findItem(R.id.nav_a67).setVisible(true);
        menu_extra_info.findItem(R.id.nav_a48).setVisible(true);
        // not show
        menu_extra_info.findItem(R.id.nav_cscx).setVisible(false);
        if(!DEBUG) {
            Course csca08 = Courses.getCourse("CSCA08");
            Course csca67 = Courses.getCourse("CSCA67");
            Course csca20 = Courses.getCourse("CSCA20");
            Course csca48 = Courses.getCourse("CSCA48");
            System.out.println("Show LESS");
            if (!Courses.hasCSCS08()||(csca08!=null&&csca08.isFall != isFall))
                menu_extra_info.findItem(R.id.nav_a08).setVisible(false);
            if (!Courses.hasCSCS20()||(csca08!=null&&csca20.isFall != isFall))
                menu_extra_info.findItem(R.id.nav_a20).setVisible(false);
            if (!Courses.hasCSCS67()||(csca08!=null&&csca67.isFall != isFall))
                menu_extra_info.findItem(R.id.nav_a67).setVisible(false);
            if(!Courses.hasCourse("CSCA48")||(csca08!=null&&csca48.isFall != isFall))
                menu_extra_info.findItem(R.id.nav_a48).setVisible(false);
            //refresh the whole menu
            navi_menu.getItem(1).setEnabled(true);
        }
        else{
            System.out.println("Show ALL");
            navi_menu.getItem(1).setEnabled(true);

        }
    }

    public static void setIsFall(boolean isFall){
        DrawerActivity.isFall = isFall;
        debugCourse();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();
        boolean CLOSE_DRAWER = true;

        if (id == R.id.course_list) {
            mTitle = "Course list";
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment1).commit();
        }
        else if (id == R.id.fall_timetable) {
            mTitle = "Fall timetable";
            fragmentManager.beginTransaction().replace(R.id.flContent, timetable_fall_fragment).commit();
        }
        else if (id == R.id.winter_timetable) {
            mTitle = "Winter timetable";
            fragmentManager.beginTransaction().replace(R.id.flContent, timetable_winter_fragment).commit();
        }
        getSupportActionBar().setTitle(mTitle);

        if (id == R.id.nav_manage) {
            //closeDrawer();
            downloadCourses();
            //item.setCheckable(false);

        }
        // hard coded below in 2015...
        else if (id == R.id.nav_a08) {
            csca08();
            CLOSE_DRAWER = false;
        }
        else if (id == R.id.nav_a20) {
            CLOSE_DRAWER = false;
            csca20();
        }
        else if (id == R.id.nav_a67) {
            CLOSE_DRAWER = false;
            csca67();
        }
        else if (id == R.id.nav_a48) {
            CLOSE_DRAWER = false;
            csca48();
        }
        else if (id == R.id.nav_cscx) {
            CLOSE_DRAWER = false;
            cscxxx();
        }
        else if(id==R.id.nav_share){
            System.out.println("Share this app.");
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "UofT timetable");
                String sAux = "Let me recommend you this application\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=com.lvds2000.utsccsuntility\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "Share \"UofT timetable\" via"));
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else if(id==R.id.setting){
            CLOSE_DRAWER = false;
            Intent intent = new Intent(this, Setting.class);
            startActivity(intent);
        }
        // create a new thread to close the drawer after 10ms.
        // this will make the animation smoother.
        if(CLOSE_DRAWER)
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                }
            }, 10);

        return true;
    }

    // return from downloading course
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("Back to DrawerActivity");
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("Returned");
                try {
                    timetable_fall_fragment = TimetableFragment.newInstance("fall");
                    timetable_winter_fragment = TimetableFragment.newInstance("winter");

                    // reset to fall timetable
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navi_menu = navigationView.getMenu();
                    MenuItem menuTimetable = navi_menu.getItem(1);
                    menuTimetable.setChecked(true);
                    getSupportActionBar().setTitle("Fall timetable");
                    fragmentManager.beginTransaction().replace(R.id.flContent, timetable_fall_fragment).commitAllowingStateLoss();

                    System.out.println("debugCourse()");
                    saveString("versionCode", "" + currentVersionCode, this);
                    saveString("versionName", "" + versionName, this);

                }catch(Exception e){
                    System.out.println("Failed to import course info from acorn");
                }
                debugCourse();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }//onActivityResult

    public void downloadCourses(){
        Intent intent = new Intent(this, DownloadCourses.class);
        startActivityForResult(intent, 1);
    }

    public void csca08(){
        Intent intent = new Intent(this, DisplayCSCA08.class);
        startActivity(intent);
    }
    public void csca20(){
        Intent intent = new Intent(this, DisplayCSCA20.class);
        startActivity(intent);
    }
    public void csca67(){
        Intent intent = new Intent(this, DisplayCSCA67.class);
        startActivity(intent);
    }
    public void csca48(){
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }
    public void cscxxx(){

        Intent intent = new Intent(this, com.lvds2000.utsccsuntility.Generic.InfoActivity.class);
        ArrayList<Info> info = new ArrayList<>();
        info.add(new Info("ann", "http://www.utsc.utoronto.ca/~bharrington/csca08/", ""));
        intent.putParcelableArrayListExtra("info", info);
        startActivity(intent);
    }

    public static void loadColor(){
        int totalCourseNum = TimetableFragment.courseList.length;
        int[] color = loadIntArray("color", c);
        for(int i=0; i<totalCourseNum; i++) {
            try{
                TimetableFragment.courseList[i].color = color[i];
                //System.out.println("Load Color: " + color[i]);
            }catch (Exception e){
                TimetableFragment.courseList[i].color = 0;
            }

        }
    }
    public static void saveColor(){
        int totalCourseNum = TimetableFragment.courseList.length;
        int[] color = new int[totalCourseNum];
        for(int i=0; i<totalCourseNum; i++) {
            color[i] = TimetableFragment.courseList[i].color;
        }
        saveIntArray(color, "color", c);
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
    public static boolean saveIntArray(int[] myInt, String intName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferenceInt", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(intName + "_size", myInt.length);
        for(int i=0;i<myInt.length;i++)
            editor.putInt(intName + "_" + i, myInt[i]);
        return editor.commit();
    }
    public static int[] loadIntArray(String intName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferenceInt", 0);
        int size = prefs.getInt(intName + "_size", 0);
        int array[] = new int[size];
        for(int i=0;i<size;i++)
            array[i] = prefs.getInt(intName + "_" + i, 0);
        return array;
    }
    public static boolean saveString(String name, String s, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("com.lvds2000.utsccsuntility_preferences", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(name, s);
        return editor.commit();
    }
    public static String loadString(String name, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("com.lvds2000.utsccsuntility_preferences", 0);
        return prefs.getString(name, "");
    }
    public static boolean loadBoolean(String name, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("com.lvds2000.utsccsuntility_preferences", 0);
        return prefs.getBoolean(name, false);
    }
    public static int loadInt(String name, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("com.lvds2000.utsccsuntility_preferences", 0);
        return prefs.getInt(name, 0);
    }
}
