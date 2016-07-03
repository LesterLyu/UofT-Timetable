package com.lvds2000.utsccsuntility;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Random;

public class DisplayCoursess extends AppCompatActivity {
    ArrayAdapter<String> listAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //Theme setting
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar));
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.md_indigo_700));
        } else {
            // Implement this feature without material design
        }

        int totalCourseNum = Courses.getTotalCourseNum();

        ListView lv = new ListView(this);
        setContentView(lv);

        System.out.println("totalCourseNum"+totalCourseNum);
        String[] itemname = new String[totalCourseNum];
        String[] imgid =new String[totalCourseNum];
        String[] time = new String[totalCourseNum];
        if(!Courses.course[0].code.equals(""))
            for(int i=0; i<totalCourseNum; i++){
                itemname[i] = Courses.course[i].code+"  "+Courses.course[i].lec+"  "+Courses.course[i].lecLocation;
                System.out.println(i);
                imgid[i] = Courses.course[i].code.substring(0,1).toUpperCase();
                String displayTime = "";
                for(int k=0; k<Courses.course[i].getLecTimeNum(); k++){
                    displayTime = displayTime + Courses.course[i].lecTime[k] +"\n";
                }
                time[i] = displayTime.trim();
//                if(Courses.course[i].lecTime[1].equalsIgnoreCase(""))
//                    time[i] = Courses.course[i].lecTime[0];
//                else
//                    time[i] = Courses.course[i].lecTime[0] +"\n"+ Courses.course[i].lecTime[1];
            }
        else{
            for(int i=0; i<totalCourseNum; i++){
                itemname[i] = "Click Menu to Download Content";
                imgid[i] = ((char)(65+i))+"";
                time[i] = "";
            }
        }

        CustomListAdapter adapter=new CustomListAdapter(this, itemname, imgid, time);

        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String s = Courses.course[(int)id].getLectureWeekday(1)+" "+Courses.course[(int)id].getLectureStartTime(1)+" "+Courses.course[(int)id].getLectureEndTime(1);
                //Toast.makeText(getApplicationContext(), id+" "+s, Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_coursess, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            downloadCourses();
            return true;
        }
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                this.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void downloadCourses() {
        Intent intent = new Intent(this, DownloadCourses.class);
        startActivity(intent);

    }
}

class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final String[] time;
    private final String[] imgid;
    private Color[] color = new Color[10];


    public CustomListAdapter(Activity context, String[] itemname, String[] imgid, String time[]) {
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
