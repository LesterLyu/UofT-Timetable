package com.lvds2000.utsccsuntility;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lvds2000.entity.Day;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CourseListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CourseListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseListFragment extends Fragment {



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CourseListFragment newInstance(String param1, String param2) {
        CourseListFragment fragment = new CourseListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CourseListFragment() {
        // Required empty public constructor



    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int totalCourseNum = 1;
        if(Fragment_Timetable.courseList != null)
            totalCourseNum = Fragment_Timetable.courseList.length;

        ListView lv = new ListView(getActivity());

        System.out.println("totalCourseNum"+totalCourseNum);
        String[] itemname = new String[totalCourseNum];
        String[] imgid = new String[totalCourseNum];
        String[] time = new String[totalCourseNum];
        if(Fragment_Timetable.courseList.length != 0)
            for(int i = 0; i < totalCourseNum; i ++){
                itemname[i] = Fragment_Timetable.courseList[i].getCourseCode()+
                        "  "+ Fragment_Timetable.courseList[i].getPrimaryActivityId()+
                        "  "+ Fragment_Timetable.courseList[i].getInfo().getPrimaryActivities().get(0).getDays().get(0).getRoomLocation();
                imgid[i] = Fragment_Timetable.courseList[i].getCourseCode().substring(0,1).toUpperCase();
                String displayTime = "";
                for(int k = 0; k <  Fragment_Timetable.courseList[i].getInfo().getPrimaryActivities().get(0).getDays().size(); k ++){
                    Day day =  Fragment_Timetable.courseList[i].getInfo().getPrimaryActivities().get(0).getDays().get(k);
                    displayTime = displayTime + day.getDayOfWeek() + " " + day.getStartTime() + " - " + day.getEndTime()  +"\n";
                }
                time[i] = displayTime.trim();
            }
        else{
            for(int i=0; i<totalCourseNum; i++){
                itemname[i] = "Click Menu to Download Content";
                imgid[i] = ((char)(65+i))+"";
                time[i] = "";
            }
        }

        CustomListAdapter1 adapter=new CustomListAdapter1(getActivity(), itemname, imgid, time);

        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String s = Courses.course[(int)id].getLectureWeekday(1)+" "+Courses.course[(int)id].getLectureStartTime(1)+" "+Courses.course[(int)id].getLectureEndTime(1);
                //Toast.makeText(getApplicationContext(), id+" "+s, Toast.LENGTH_SHORT).show();
            }
        });
        return lv;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


}
class CustomListAdapter1 extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final String[] time;
    private final String[] imgid;
    private Color[] color = new Color[10];


    public CustomListAdapter1(Activity context, String[] itemname, String[] imgid, String time[]) {
        super(context, R.layout.mylist, itemname);

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
        this.time=time;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        TextView leftView = (TextView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        txtTitle.setText(itemname[position]);
        leftView.setText(imgid[position]);
        leftView.setBackgroundColor(pickColor(position));
        extratxt.setText(time[position]);
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