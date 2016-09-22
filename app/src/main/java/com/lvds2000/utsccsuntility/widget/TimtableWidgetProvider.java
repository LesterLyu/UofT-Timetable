package com.lvds2000.utsccsuntility.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lvds2000.entity.Course;
import com.lvds2000.entity.plan.Day;
import com.lvds2000.utsccsuntility.DrawerActivity;
import com.lvds2000.utsccsuntility.R;
import com.lvds2000.utsccsuntility.Timetable_SubMenu;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by lester on 9/17/2016.
 */
public class TimtableWidgetProvider extends AppWidgetProvider {

    final int numberOfRows = 24, numberOfColumns = 6;
    private TextView[][] tv = new TextView[numberOfRows][numberOfColumns];
    private LinearLayout[][] ll = new LinearLayout[numberOfRows][numberOfColumns];
    private TextView selectedTextView;
    float scale;
    static DisplayMetrics displayMetrics;
    static int displayHeight;
    static int displayWidth;
    int rowSize;
    float time1, time2;
    final boolean SHOWALLROWS = false;
    private static Activity activity;
    public static com.lvds2000.entity.Course[] courseList;
    //public static EnrolledCourse[] enrolledCourseList;
    private static String courseJson;
    private String mode;
    private static final String ARG_PARAM1 = "mode";
    private View rootView;


    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions){
        Log.i("TimetableWidget", "widget option changed");
        activity = DrawerActivity.activity;
        LayoutInflater myInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = myInflater.inflate(R.layout.widget_time_table, null);

    }

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        Log.i("TimetableWidget", "widget updating");

        final int N = appWidgetIds.length;
        activity = DrawerActivity.activity;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int widgeti=0; widgeti<N; widgeti++) {
            int appWidgetId = appWidgetIds[widgeti];


            // copy patste here
            if(courseJson == null || courseJson.equals(""))
                courseJson = DrawerActivity.loadString("courseJson", activity);
            System.out.println(courseJson);
            Gson gson = new Gson();
            courseList = gson.fromJson(courseJson, com.lvds2000.entity.Course[].class);

            // get current device screen pixels
            displayMetrics = DrawerActivity.displaymetrics;
            displayHeight = displayMetrics.heightPixels;
            displayWidth = displayMetrics.widthPixels;
            scale = displayMetrics.density;
            rowSize = getPx(60);


            // new code
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_time_table);


            LayoutInflater myInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rootView = myInflater.inflate(R.layout.widget_time_table, null);

            // textViews tv --> LinearLayout ll--> GridLayout gl
           // GridLayout gl = (GridLayout)rootView.findViewById(R.id.gridLayout);

            // the earliest class begins at 7, thus i = 7
            for( int i = 7; i < numberOfRows; i++){

                // set the left side time
                for(int k=0; k<1; k++) {
                    tv[i][k] = new TextView(activity);
                    tv[i][k].setText(i + ":00");
                    tv[i][k].setBackgroundResource(R.drawable.cell_shape_time);
                    tv[i][k].setGravity(Gravity.RIGHT);
                    tv[i][k].setTextSize(10);

                    //params
                    GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                    param.height = rowSize;
                    param.width = displayWidth /11;
                    param.rightMargin = 5;
                    param.topMargin = 1;
                    param.rowSpec = GridLayout.spec(i);
                    param.columnSpec = GridLayout.spec(k);

                    LinearLayout.LayoutParams param_mid = new LinearLayout.LayoutParams(displayWidth /11, rowSize);

                    tv[i][k].setLayoutParams(param_mid);

                    //LinearLayout
                    ll[i][k] = new LinearLayout(activity);
                    ll[i][k].setOrientation(LinearLayout.VERTICAL);
                    ll[i][k].setLayoutParams(param);
                    ll[i][k].addView(tv[i][k], 0);
                    //gl.addView(ll[i][k]);
                }
                for(int k=1; k<numberOfColumns; k++) {
                    tv[i][k] = new TextView(activity);
                    tv[i][k].setTextSize(10);
                    tv[i][k].setBackgroundResource(R.drawable.cell_shape_bot);
                    tv[i][k].setTextColor(Color.WHITE);
                    tv[i][k].setTypeface(null, Typeface.BOLD);


                    //params
                    GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                    param.height = rowSize;
                    param.width = (displayWidth) / 11 * 2 - 5;
                    param.rightMargin = 5;
                    param.topMargin = 1;
                    param.rowSpec = GridLayout.spec(i);
                    param.columnSpec = GridLayout.spec(k);


                    LinearLayout.LayoutParams param_mid = new LinearLayout.LayoutParams((displayWidth)/ 11 * 2 - 5, 0, 10);
                    tv[i][k].setLayoutParams(param_mid);

                    //LinearLayout
                    ll[i][k] = new LinearLayout(activity);
                    ll[i][k].setOrientation(LinearLayout.VERTICAL);
                    ll[i][k].setLayoutParams(param);
                    ll[i][k].addView(tv[i][k], 0);

                   // gl.addView(ll[i][k]);

                }
            }

            time2 = SystemClock.currentThreadTimeMillis();
            System.out.println("Initialize cost: "+(time2 - time1)+"ms");
            time1 = time2;

            mode = "fall";
            //delete rows
            try{
                if(!SHOWALLROWS) {
                    float earliest, latest;
                    Date time[] = null;
                    try {
                        time = getEarliestAndLatestTime();
                        System.out.println("time: " + time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(mode.equals("fall")) {
                        earliest = time[0].getHours();
                        latest = time[1].getHours();
                    }
                    else{
                        earliest = time[2].getHours();
                        latest = time[3].getHours();
                    }
                    for (int i = 7; i < earliest; i++) {
                        for (int k = 0; k < 6; k++) {
                            if(ll[i][k] != null)
                                ll[i][k].setVisibility(View.GONE);
                            if(tv[i][k] != null)
                                tv[i][k].setVisibility(View.GONE);
                        }
                    }
                    for (int i = (int)latest + 1; i < 24; i++) {
                        for (int k = 0; k < 6; k++) {
                            if(ll[i][k] != null)
                                ll[i][k].setVisibility(View.GONE);
                            if(tv[i][k] != null)
                                tv[i][k].setVisibility(View.GONE);
                        }
                    }
                }
            }catch(NullPointerException e){
                e.printStackTrace();
                System.out.println("Warning: Empty timetable");
            }


            time2 = SystemClock.currentThreadTimeMillis();
            System.out.println("Delete Rows cost: "+(time2 - time1)+"ms");
            time1 = time2;
            if(courseList != null)
                refreshCeil();

            time2 = SystemClock.currentThreadTimeMillis();
            System.out.println("Add Contents cost: "+(time2 - time1)+"ms");
            time1 = time2;

            // Get the layout for the App Widget
            //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_time_table);



            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }



    }

    public int getPx(int dps){
        return (int) (dps * scale + 0.5f);

    }


    public static Date[] getEarliestAndLatestTime() throws ParseException {
        DateFormat format = new SimpleDateFormat("hh:mma", Locale.CANADA);
        // fall earliest, fall latest, winter earliest, winter latest
        Date[] time = {format.parse("11:59PM"), format.parse("00:01AM"), format.parse("11:59PM"), format.parse("00:01AM")};
        for(com.lvds2000.entity.Course course : courseList){
            //System.out.println(plannedCourse.getCourseCode());
            List<com.lvds2000.entity.Activity> activities = course.getActivities();
            for(com.lvds2000.entity.Activity activity: activities){
                if(activity.getDays() != null)
                    for(Day day: activity.getDays()){
                        Date startTime = null;
                        Date endTime = null;
                        startTime = format.parse(day.getStartTime());
                        endTime = format.parse(day.getEndTime());
                        if(course.getSectionCode().equalsIgnoreCase("F") || course.getSectionCode().equalsIgnoreCase("Y")){
                            if(startTime.before(time[0]))
                                time[0] = startTime;
                            if(endTime.after(time[1]))
                                time[1] = endTime;
                        }
                        if(course.getSectionCode().equalsIgnoreCase("S") || course.getSectionCode().equalsIgnoreCase("Y")){
                            if(startTime.before(time[2]))
                                time[2] = startTime;
                            if(endTime.after(time[3]))
                                time[3] = endTime;
                        }
                    }
            }
        }
        if(time[1].getHours() - time[0].getHours() < 8){
            time[1].setHours(time[0].getHours() + 8);
        }
        if(time[3].getHours() - time[2].getHours() < 8){
            time[3].setHours(time[2].getHours() + 8);
        }
        return time;
    }

    public void addContent(float row, float col, float rowspan, String content, Course course, int courseNum){
        int row_int = (int)row;
        int col_int = (int)col;
        int rowspan_int = (int)Math.ceil(rowspan + getDecimal(row));
        float row_dec_top = getDecimal(row);
        //calculate the greatest occupied rowSpan
        float row_dec_bot = rowspan_int - row_dec_top-rowspan;

        //float col_dec = getDecimal(col);
        int top_weight = Math.round(row_dec_top/rowspan_int*1000);
        int bot_weight = Math.round(row_dec_bot/rowspan_int*1000);
        int mid_weight = 1000 - top_weight - bot_weight;


        GridLayout.LayoutParams param =new GridLayout.LayoutParams();
        param.rowSpec = GridLayout.spec(row_int, rowspan_int);
        param.setGravity(Gravity.FILL_VERTICAL);
        param.width = (displayWidth)/11*2-5;
        param.rightMargin = 5;
        param.topMargin = 1;
        param.columnSpec = GridLayout.spec(col_int);

        if(!(row_int==row) || !(col_int==col) || !(rowspan_int==rowspan_int)) {
            System.out.println("an abnormal time");
            System.out.println(row_dec_top+" "+"?"+" "+row_dec_bot);
            System.out.println(top_weight+" "+mid_weight+" "+bot_weight);
            LinearLayout.LayoutParams param_mid = new LinearLayout.LayoutParams((displayWidth) / 11 * 2 - 5, 0, mid_weight);
            tv[row_int][col_int].setLayoutParams(param_mid);
        }

        if(rowspan>1){
            for(int i=1; i<rowspan; i++){
                tv[row_int+i][col_int].setVisibility(View.GONE);
            }
        }
        ll[row_int][col_int].setLayoutParams(param);
        tv[row_int][col_int].setText(content);
        if(courseList[courseNum].color != 0){
            tv[row_int][col_int].setBackgroundColor(courseList[courseNum].color);
            //System.out.println("Set Color: " + courseList[courseNum].color);
        }
        else {
            tv[row_int][col_int].setBackgroundColor(pickColor(courseNum));
        }
    }



    public void refreshCeil(){
        String[] contentCourse = new String[20], enrollment = new String[20];
        float[]  startTime = new float[20], endTime = new float[20], weekNum = new float[20];
        // new
        int courseNum = 0;
        for(com.lvds2000.entity.Course course : courseList){
            if(course.getSectionCode().equalsIgnoreCase("F") && mode.equals("fall") ||
                    course.getSectionCode().equalsIgnoreCase("S") && mode.equals("winter") ||
                    course.getSectionCode().equalsIgnoreCase("Y")) {
                List<com.lvds2000.entity.Activity> activities = course.getActivities();
                for(com.lvds2000.entity.Activity activity: activities) {
                    if (activity != null)
                        for (Day day : activity.getDays()) {
                            Map<String, Integer> dayToNum = new HashMap<String, Integer>();
                            dayToNum.put("Monday", 1);
                            dayToNum.put("Tuesday", 2);
                            dayToNum.put("Wednesday", 3);
                            dayToNum.put("Thursday", 4);
                            dayToNum.put("Friday", 5);
                            Map<Boolean, String> enrollmentMap = new HashMap<Boolean, String>();
                            enrollmentMap.put(true, "");
                            enrollmentMap.put(false, "*not enrolled");
                            //System.out.println(day.getDayOfWeek());
                            weekNum[courseNum] = dayToNum.get(day.getDayOfWeek());
                            enrollment[courseNum] = enrollmentMap.get(activity.getEnroled());
                            //System.out.println( weekNum[courseNum]);
                            SimpleDateFormat format = new SimpleDateFormat("hh:mma", Locale.CANADA);
                            try {
                                startTime[courseNum] = format.parse(day.getStartTime()).getHours();
                                endTime[courseNum] = format.parse(day.getEndTime()).getHours();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            contentCourse[courseNum] = " " + course.getCourseCode() + "\n "
                                    + activity.getActivityId() + "\n "
                                    + day.getRoomLocation() + "\n "
                                    + enrollment[courseNum];
                            addContent(startTime[courseNum], weekNum[courseNum],
                                    endTime[courseNum] - startTime[courseNum],
                                    contentCourse[courseNum], courseList[courseNum],
                                    courseNum);
                        }
                }
            }
            courseNum ++;
        }
    }
    public static float getDecimal(float a){
        return a - (int)a;
    }


    public static int pickColor(int num){
        int color;
        //material color
        switch(num) {
            case 0:
                color = Color.parseColor("#EF5350");
                return color;
            case 1:
                color = Color.parseColor("#EC407A");
                return color;
            case 2:
                color = Color.parseColor("#AB47BC");
                return color;
            case 3:
                color = Color.parseColor("#7E57C2");
                return color;
            case 4:
                color = Color.parseColor("#5C6BC0");
                return color;
            case 5:
                color = Color.parseColor("#42A5F5");
                return color;
            case 6:
                color = Color.parseColor("#29B6F6");
                return color;
            case 7:
                color = Color.parseColor("#26C6DA");
                return color;
            case 8:
                color = Color.parseColor("#26A69A");
                return color;
            case 9:
                color = Color.parseColor("#66BB6A");
                return color;
            case 10:
                color = Color.parseColor("#9CCC65");
                return color;
            case 11:
                color = Color.parseColor("#D4E157");
                return color;
            case 12:
                color = Color.parseColor("#8D6E63");
                return color;
            default:
                color = Color.parseColor("#FFA726");
                return color;
        }
    }


}
