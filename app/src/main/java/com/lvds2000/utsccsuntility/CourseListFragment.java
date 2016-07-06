package com.lvds2000.utsccsuntility;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lvds2000.entity.plan.Day;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CourseListFragment extends Fragment {


    private View view;
    private ListView lv;

    public CourseListFragment() {
        LayoutInflater li = LayoutInflater.from(DrawerActivity.activity);
        view = li.inflate(R.layout.activity_display_coursess, null, false);
        lv = (ListView)view.findViewById(R.id.courseListView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        int totalCourseNum = 1;
        if(TimetableFragment.plannedCourseList != null)
            totalCourseNum = TimetableFragment.plannedCourseList.length;
        System.out.println("totalCourseNum"+totalCourseNum);
        String[] courseTitle = new String[totalCourseNum];
        String[] courseLeftIcon = new String[totalCourseNum];
        String[] courseName = new String[totalCourseNum];
        String[][] activityCode = new String[3][totalCourseNum];
        String[][] activityContent = new String[3][totalCourseNum];
        Map<String, String> courseSeasonMap = new HashMap<>();
        courseSeasonMap.put("9", " (Fall) ");
        courseSeasonMap.put("1", " (Winter) ");
        courseSeasonMap.put("91", " (Full) ");
        if(TimetableFragment.plannedCourseList != null)
            for(int i = 0; i < totalCourseNum; i ++){
                List<com.lvds2000.entity.plan.Activity> activities = TimetableFragment.plannedCourseList[i].getActivities();
                String courseSeasonName;
                // if the course is "fall"/ "summer"
                if(TimetableFragment.plannedCourseList[i].getRegSessionCode2().equals(""))
                    courseSeasonName = courseSeasonMap.get(TimetableFragment.plannedCourseList[i].getRegSessionCode1().substring(4));
                else
                    courseSeasonName = courseSeasonMap.get(TimetableFragment.plannedCourseList[i].getRegSessionCode1().substring(4) +
                            TimetableFragment.plannedCourseList[i].getRegSessionCode2().substring(4));
                courseTitle[i] = TimetableFragment.plannedCourseList[i].getCourseCode()+ courseSeasonName;
                courseLeftIcon[i] = TimetableFragment.plannedCourseList[i].getCourseCode().substring(0,2).toUpperCase();
                courseName[i] = TimetableFragment.plannedCourseList[i].getCourseTitle().trim().replaceAll(" +", " ");

                for(int j = 0; j < activities.size(); j++) {
                    String displayTime = "";
                    for(int k = 0; k < activities.get(j).getDays().size(); k ++){
                        Day day =   activities.get(j).getDays().get(k);
                        String location =  activities.get(j).getDays().get(k).getRoomLocation();
                        if(location.equals(""))
                            location = "TBA";
                        displayTime = displayTime + location  +
                                " " + day.getDayOfWeek() + " " + day.getStartTime() + " - " + day.getEndTime()  +"\n";
                    }
                    activityCode[j][i] = activities.get(j).getActivityId();
                    activityContent[j][i] = displayTime.trim();
                }



            }
        else{
            for(int i=0; i<totalCourseNum; i++){
                courseTitle[i] = "Click Menu to import timetable";
                courseLeftIcon[i] = ((char)(65+i))+"";
                courseName[i] = "";
            }
        }

        CustomListAdapter1 adapter=new CustomListAdapter1(getActivity(), courseTitle, courseLeftIcon, courseName, activityCode, activityContent);

        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String s = Courses.course[(int)id].getLectureWeekday(1)+" "+Courses.course[(int)id].getLectureStartTime(1)+" "+Courses.course[(int)id].getLectureEndTime(1);
                //Toast.makeText(getApplicationContext(), id+" "+s, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
class CustomListAdapter1 extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] courseTitle;
    private final String[] courseName;
    private final String[] courseLeftIcon;
    private final String[][] activityId;
    private final String[][] courseContent;
    private Color[] color = new Color[10];


    public CustomListAdapter1(Activity context, String[] courseTitle, String[] courseLeftIcon, String courseName[],String[][] activityCode, String[][] activityContent) {
        super(context, R.layout.mylist, courseTitle);

        this.context = context;
        this.courseTitle = courseTitle;
        this.courseLeftIcon = courseLeftIcon;
        this.courseName = courseName;
        this.activityId = activityCode;
        this.courseContent = activityContent;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        TextView leftView = (TextView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);
        TextView activityIdTV = (TextView) rowView.findViewById(R.id.activityId);
        TextView courseContentTV = (TextView) rowView.findViewById(R.id.courseContent);
        TextView activityIdTV2 = (TextView) rowView.findViewById(R.id.activityId2);
        TextView courseContentTV2 = (TextView) rowView.findViewById(R.id.courseContent2);
        TextView activityIdTV3 = (TextView) rowView.findViewById(R.id.activityId3);
        TextView courseContentTV3 = (TextView) rowView.findViewById(R.id.courseContent3);


        txtTitle.setText(courseTitle[position]);
        leftView.setText(courseLeftIcon[position]);
        leftView.setBackgroundColor(pickColor(position));
        extratxt.setText(courseName[position]);
        if(activityId[0][position] != null){
            activityIdTV.setText(activityId[0][position]);
            courseContentTV.setText(courseContent[0][position]);
        }
        else{
            rowView.findViewById(R.id.linearLayout).setVisibility(View.GONE);
            activityIdTV.setVisibility(View.GONE);
            courseContentTV.setVisibility(View.GONE);
        }
        if(activityId[1][position] != null){
            activityIdTV2.setText(activityId[1][position]);
            courseContentTV2.setText(courseContent[1][position]);
        }
        else{
            rowView.findViewById(R.id.linearLayout2).setVisibility(View.GONE);
            activityIdTV2.setVisibility(View.GONE);
            courseContentTV2.setVisibility(View.GONE);
        }
        if(activityId[2][position] != null){
            activityIdTV3.setText(activityId[2][position]);
            courseContentTV3.setText(courseContent[2][position]);
        }
        else{
            rowView.findViewById(R.id.linearLayout3).setVisibility(View.GONE);
            activityIdTV3.setVisibility(View.GONE);
            courseContentTV3.setVisibility(View.GONE);
        }
        return rowView;
    };
    public int pickColor(int position){
        Random rnd = new Random();
        int color;
        switch(position) {
            case 0:
                color = Color.parseColor("#EF9A9A");
                return color;
            case 1:
                color = Color.parseColor("#F48FB1");
                return color;
            case 2:
                color = Color.parseColor("#CE93D8");
                return color;
            case 3:
                color = Color.parseColor("#B39DDB");
                return color;
            case 4:
                color = Color.parseColor("#9FA8DA");
                return color;
            case 5:
                color = Color.parseColor("#90CAF9");
                return color;
            case 6:
                color = Color.parseColor("#81D4FA");
                return color;
            case 7:
                color = Color.parseColor("#80DEEA");
                return color;
            case 8:
                color = Color.parseColor("#80CBC4");
                return color;
            case 9:
                color = Color.parseColor("#A5D6A7");
                return color;
            case 10:
                color = Color.parseColor("#FFF59D");
                return color;
            case 11:
                color = Color.parseColor("#FFE082");
                return color;
            default:
                color = Color.parseColor("#BCAAA4");
                return color;
        }
    }
}