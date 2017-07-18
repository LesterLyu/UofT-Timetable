package com.lvds2000.uoft_timetable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.lvds2000.AcornAPI.auth.Acorn;
import com.lvds2000.AcornAPI.auth.SimpleListener;
import com.lvds2000.AcornAPI.enrol.EnrolledCourse;
import com.lvds2000.AcornAPI.plan.PlannedCourse;
import com.lvds2000.uoft_timetable.utils.Configuration;
import com.lvds2000.uoft_timetable.utils.UserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static Fragment fragment1;
    private static TimetableFragment timetable_fall_fragment, timetable_winter_fragment,
            timetable_summer1_fragment, timetable_summer2_fragment;
    private static Fragment gradeFragment;
    private static FragmentManager fragmentManager;
    public static float scale;
    public static DisplayMetrics displaymetrics;
    public static int dis_height;
    public static int dis_width ;
    public Activity activity;
    private static Handler handler = new Handler();
    private static String mTitle = "Winter timetable";
    private static boolean initialized = false;
    public static boolean DEBUG = false;
    private static Menu navi_menu;
    public static String versionName, currentVersionCode;
    private static ProgressDialog progress;
    public static Acorn acorn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;

        // check version and do some task if the version is...
        versionName = BuildConfig.VERSION_NAME;
        currentVersionCode = "" + BuildConfig.VERSION_CODE;

        String lastVersionCode = Configuration.loadString("versionCode", this);

        // first time launch or from update
        if (lastVersionCode.equals("") || lastVersionCode.equals("22")) {
            downloadCoursesPrompt();
        }

        displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        dis_height = displaymetrics.heightPixels;
        dis_width = displaymetrics.widthPixels;
        scale = displaymetrics.density;


        fragment1 = new CourseListFragment();
        timetable_fall_fragment = TimetableFragment.newInstance("fall");
        timetable_winter_fragment = TimetableFragment.newInstance("winter");
        timetable_summer1_fragment = TimetableFragment.newInstance("summer1");
        timetable_summer2_fragment = TimetableFragment.newInstance("summer2");
        gradeFragment = GradeFragment.newInstance();

        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
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
        // set version code
        View headerView = navigationView.getHeaderView(0);
        TextView versionText = (TextView) headerView.findViewById(R.id.versionText);
        versionText.setText("version " + versionName);

        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // default timetable
            setToDefaultTimetable();
        }
        // restore from a destroyed activity
        else {
            if (navi_menu.getItem(0).isChecked()) {
                setTitle("Course List");
            } else if (navi_menu.getItem(1).isChecked()) {
                setTitle("Fall timetable");
            } else if (navi_menu.getItem(2).isChecked()) {
                setTitle("Winter timetable");
            } else if (navi_menu.getItem(3).isChecked() || navi_menu.getItem(4).isChecked()) {
                setTitle("Summer timetable");
            } else if (navi_menu.getItem(5).isChecked()) {
                setTitle("Academic History");
            }
        }

        // initialize acorn if has username/password
        if (!UserInfo.getUsername(this).isEmpty() && !UserInfo.getPassword(this).isEmpty())
            acorn = new Acorn(UserInfo.getUsername(this), UserInfo.getPassword(this));

        //google analytics
        if (!initialized)
            AnalyticsTrackers.initialize(this);
        initialized = true;
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

        Configuration.saveString("versionCode", "" + currentVersionCode, this);
        Configuration.saveString("versionName", "" + versionName, this);
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
        else if (id == R.id.summer1_timetable) {
            mTitle = "Summer F timetable";
            fragmentManager.beginTransaction().replace(R.id.flContent, timetable_summer1_fragment).commit();
        }
        else if (id == R.id.summer2_timetable) {
            mTitle = "Summer S timetable";
            fragmentManager.beginTransaction().replace(R.id.flContent, timetable_summer2_fragment).commit();
        }
        else if(id == R.id.grade_menu){
            mTitle = "Academic history";
            fragmentManager.beginTransaction().replace(R.id.flContent, gradeFragment).commit();
        }
        getSupportActionBar().setTitle(mTitle);

        if (id == R.id.nav_manage) {
            //closeDrawer();
            downloadCoursesPrompt();
            //item.setCheckable(false);
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
        else if(id==R.id.courseSearcher){
            CLOSE_DRAWER = false;
            Intent intent = new Intent(this, CourseSearcherActivity.class);
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


    /**
     * update course info
     */
    public void downloadCoursesPrompt(){
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Retrieving data...");
        progress.setCancelable(false);

        final String username = UserInfo.getUsername(this);
        if(!username.isEmpty() && !UserInfo.getPassword(this).isEmpty()){
            if(UserInfo.isUserPassChanged(this)){
                acorn = new Acorn(username, UserInfo.getPassword(this));
            }
            progress.show();
            downloadCourseData(acorn, null);

        }
        // prompt user to input username and password
        else {
            UserInfo.promptInputUserPassAndUpdateCourseData(this, progress, null);

        }
    }

    public void downloadCourseData(final Acorn acorn, final SwipeRefreshLayout swipeContainer){
        final Context context = this;
        acorn.doLogin(new SimpleListener() {
            @Override
            public void success() {
                acorn.getCourseManager().loadCourses();
                acorn.getCourseManager().getAppliedCourses();
                List<com.lvds2000.entity.Course> courseList = new ArrayList<>();

                List<EnrolledCourse> enrolledCourseList =  acorn.getCourseManager().getAppliedCourses();
                for(EnrolledCourse enrolledCourse: enrolledCourseList){
                    Log.i("downloadCourseData", enrolledCourse.toString());
                    try{
                        courseList.add(new com.lvds2000.entity.Course(enrolledCourse));
                    } catch(Exception e){
                        Gson gson = new Gson();
                        Log.i("downloadCourseData", "Error" + e.getMessage() + "\n" + gson.toJson(enrolledCourse));
                        Tracker t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                        t.send(new HitBuilders.ExceptionBuilder()
                                .setDescription( "Error \n" + gson.toJson(enrolledCourse))
                                .setFatal(true)
                                .build());
                    }
                }
                List<PlannedCourse> plannedCourseList =  acorn.getCourseManager().getPlannedCourses();
                for(PlannedCourse plannedCourse: plannedCourseList){
                    Log.i("downloadCourseData", plannedCourse.toString());
                    courseList.add(new com.lvds2000.entity.Course(plannedCourse));
                }

                Collections.sort(courseList, new Comparator<com.lvds2000.entity.Course>() {
                    @Override
                    public int compare(com.lvds2000.entity.Course o1, com.lvds2000.entity.Course o2) {
                        return  TimetableFragment.getCourseSession(o1) - TimetableFragment.getCourseSession(o2);
                    }
                });

                Gson gson = new Gson();

                String json =  gson.toJson(courseList);
                TimetableFragment.setCourseJson(json);
                Configuration.saveString("courseJson", json, context);


                // this method may be called by another class
                if(progress != null)
                    progress.cancel();

                // finish up
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // re-initialize
                        timetable_fall_fragment.refresh();
                        timetable_winter_fragment.refresh();
                        timetable_summer1_fragment.refresh();
                        timetable_summer2_fragment.refresh();

                        for (SwipeRefreshLayout swipeContainer : TimetableFragment.swipeContainers) {
                            swipeContainer.setRefreshing(false);
                        }
                        TimetableFragment.updating = false;
                    }
                });
            }

            @Override
            public void failure(Exception e) {
                e.printStackTrace();
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(e.getMessage())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.cancel();
                                        // this method may be called by another class
                                        if(progress != null)
                                            progress.cancel();
                                        for (SwipeRefreshLayout swipeContainer : TimetableFragment.swipeContainers) {
                                            swipeContainer.setRefreshing(false);
                                        }
                                        TimetableFragment.updating = false;
                                    }
                                });
                            }
                        });
                // clear the password if the user input a wrong password
                if(e.getMessage().contains("Username"))
                    UserInfo.clearPassword(context);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        builder.create().show();
                    }
                });

            }
        });


    }

    public void setToDefaultTimetable(){
        // reset to default timetable
        String defaultTimetable = Configuration.loadString("defaultTimetable", activity);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navi_menu = navigationView.getMenu();

        System.out.println("setToDefaultTimetable to " + defaultTimetable);
        if (defaultTimetable.equals("0") || defaultTimetable.isEmpty()) {
            fragmentManager.beginTransaction().replace(R.id.flContent, timetable_fall_fragment).commit();
            setTitle("Fall timetable");
            navi_menu.getItem(1).setChecked(true);
        }
        else if(defaultTimetable.equals("1")) {
            fragmentManager.beginTransaction().replace(R.id.flContent, timetable_winter_fragment).commit();
            setTitle("Winter timetable");
            navi_menu.getItem(2).setChecked(true);
        }
        else if(defaultTimetable.equals("2")) {
            fragmentManager.beginTransaction().replace(R.id.flContent, timetable_summer1_fragment).commit();
            setTitle("Summer F timetable");
            navi_menu.getItem(3).setChecked(true);
        }
        else if(defaultTimetable.equals("3")) {
            fragmentManager.beginTransaction().replace(R.id.flContent, timetable_summer2_fragment).commit();
            setTitle("Summer S timetable");
            navi_menu.getItem(4).setChecked(true);
        }
    }

}
