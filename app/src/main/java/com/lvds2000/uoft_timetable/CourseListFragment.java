package com.lvds2000.uoft_timetable;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lvds2000.AcornAPI.plan.Day;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CourseListFragment extends Fragment {


    private View view;
    private RecyclerView rv;

    public CourseListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_display_coursess, container, false);
        rv = (RecyclerView)view.findViewById(R.id.courseListView);

        int totalCourseNum = 1;
        if(TimetableFragment.courseList != null)
            totalCourseNum = TimetableFragment.courseList.length;
        System.out.println("totalCourseNum"+totalCourseNum);
        String[] courseTitle = new String[totalCourseNum];
        String[] courseLeftIcon = new String[totalCourseNum];
        String[] courseName = new String[totalCourseNum];
        String[][] activityCode = new String[3][totalCourseNum];
        String[][] activityContent = new String[3][totalCourseNum];
        Map<String, String> courseSeasonMap = new HashMap<>();
        courseSeasonMap.put("9F", " (Fall) ");
        courseSeasonMap.put("1S", " (Winter) ");
        courseSeasonMap.put("91", " (Full Session) ");
        courseSeasonMap.put("5Y", " (Summer Full Session) ");
        courseSeasonMap.put("5F", " (Summer First Sub-Session) ");
        courseSeasonMap.put("5S", " (Summer Second Sub-Session) ");
        if(TimetableFragment.courseList != null)
            for(int i = 0; i < totalCourseNum; i ++){
                List<com.lvds2000.entity.Activity> activities = TimetableFragment.courseList[i].getActivities();
                String courseSeasonName;
                //courseSeasonName = courseSeasonMap.get(TimetableFragment.courseList[i].getSectionCode());
                // if the course is "fall"/ "summer"
                if(TimetableFragment.courseList[i].getRegSessionCode2().equals(""))
                    courseSeasonName = courseSeasonMap.get(TimetableFragment.courseList[i].getRegSessionCode1().substring(4) + TimetableFragment.courseList[i].getSectionCode());
                else
                    courseSeasonName = courseSeasonMap.get(TimetableFragment.courseList[i].getRegSessionCode1().substring(4) +
                            TimetableFragment.courseList[i].getRegSessionCode2().substring(4));
                courseTitle[i] = TimetableFragment.courseList[i].getCourseCode() + courseSeasonName;
                courseLeftIcon[i] = TimetableFragment.courseList[i].getCourseCode().substring(0,2).toUpperCase();
                courseName[i] = TimetableFragment.courseList[i].getCourseTitle().trim().replaceAll(" +", " ");

                for(int j = 0; j < activities.size(); j++) {
                    String displayTime = "";
                    for(int k = 0; k < activities.get(j).getDays().size(); k ++){
                        Day day =   activities.get(j).getDays().get(k);
                        String location =  activities.get(j).getDays().get(k).getRoomLocation();
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

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //rv.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(mLayoutManager);


        CustomListAdapter1 adapter = new CustomListAdapter1(getActivity(), courseTitle, courseLeftIcon, courseName, activityCode, activityContent);
        rv.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
class CustomListAdapter1 extends RecyclerView.Adapter<CustomListAdapter1.ViewHolder> {

    private final Activity context;
    private final String[] courseTitle;
    private final String[] courseName;
    private final String[] courseLeftIcon;
    private final String[][] activityId;
    private final String[][] courseContent;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        View rowView;
        ViewHolder(View v) {
            super(v);
            rowView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    CustomListAdapter1(Activity context, String[] courseTitle, String[] courseLeftIcon,
                       String courseName[],String[][] activityCode, String[][] activityContent) {
        this.context = context;
        this.courseTitle = courseTitle;
        this.courseLeftIcon = courseLeftIcon;
        this.courseName = courseName;
        this.activityId = activityCode;
        this.courseContent = activityContent;
    }


    @Override
    public CustomListAdapter1.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_list, parent, false);
        View innerView = rowView.findViewById(R.id.card_view);
        innerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,((TextView) v.findViewById(R.id.item)).getText(), Toast.LENGTH_SHORT).show();
            }
        });
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(CustomListAdapter1.ViewHolder holder, int position) {
        View rowView = holder.rowView;

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
        holder.rowView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT ,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
    }

    @Override
    public int getItemCount() {
        return courseTitle.length;
    }

    private int pickColor(int position){
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