package com.lvds2000.uoft_timetable;

/**
 * Created by lvds2 on 3/5/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.lvds2000.AcornAPI.course.CourseSearcher;
import com.lvds2000.AcornAPI.course.ResponseListener;
import com.lvds2000.uoft_timetable.color_picker.ColorPickerDialog;
import com.lvds2000.uoft_timetable.utils.Configuration;

import org.jsoup.Jsoup;

import java.util.Map;


public class CourseSubMenuActivity extends AppCompatActivity {

    private static int color, index;
    private String code;
    private Context context;
    private TextView courseTitle, courseDescription, coursePrerequisite,
            courseCorequisite, courseExclusion, courseRecommendedPreparation,
            courseBreadthCategories, courseDistributionCategories;
    private LinearLayout courseInfo;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_course_submenu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search_course);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_picker(color);
            }
        });
        progressBar = findViewById(R.id.progress_bar);
        courseInfo = findViewById(R.id.course_info);
        courseTitle =  findViewById(R.id.course_title);
        courseDescription =  findViewById(R.id.course_description);
        coursePrerequisite =  findViewById(R.id.course_prerequisite);
        courseCorequisite =  findViewById(R.id.course_corequisite);
        courseExclusion =  findViewById(R.id.course_exclusion);
        courseRecommendedPreparation =  findViewById(R.id.course_recommended_preparation);
        courseBreadthCategories =  findViewById(R.id.course_breadth_categories);
        courseDistributionCategories = findViewById(R.id.course_distribution_categories);

        Intent intent = this.getIntent();
        System.out.println(intent.getIntExtra("COLOR", 0));
        color = intent.getIntExtra("COLOR", 0);
        code = intent.getStringExtra("CODE");
        index = intent.getIntExtra("INDEX", 0);

        this.setTitle(code);

        requestUpdate(code);

    }

    private void refresh(Map<String, JsonObject> data) {
        JsonObject course = data.get(code);
        setText(courseTitle, course.get("courseTitle").getAsString());
        setText(courseDescription, course.get("courseDescription").getAsString());
        setText(coursePrerequisite, course.get("prerequisite").getAsString());
        setText(courseCorequisite, course.get("corequisite").getAsString());
        setText(courseExclusion,course.get("exclusion").getAsString());
        setText(courseRecommendedPreparation, course.get("recommendedPreparation").getAsString());
        setText(courseBreadthCategories, course.get("breadthCategories").getAsString());
        setText(courseDistributionCategories, course.get("distributionCategories").getAsString());
        progressBar.setVisibility(View.GONE);
        courseInfo.setVisibility(View.VISIBLE);

    }

    private void setText(TextView tv, String s){
        if(!s.isEmpty())
            tv.setText(Jsoup.parse(s).text());
        else
            ((View)tv.getParent()).setVisibility(View.GONE);
    }

    private void requestUpdate(final String code){
        final Gson gson = new Gson();
        String loadFromStorage = Configuration.loadString(code + "-Info", this);
        if(loadFromStorage.equals("")) {
            CourseSearcher.getCourseInfo(code, new ResponseListener() {
                @Override
                public void response(final Map<String, JsonObject> data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Configuration.saveString(code + "-Info", gson.toJson(data), context);
                            refresh(data);
                        }
                    });
                }

                @Override
                public void failure() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        else {
            Map<String, JsonObject> data = gson.fromJson(loadFromStorage, new TypeToken<Map<String, JsonObject>>(){}.getType());
            refresh(data);
        }

    }

    private void color_picker(int color) {
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, color, new ColorPickerDialog.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int newColor) {
                CourseSubMenuActivity.color = newColor;
            }

        });
        colorPickerDialog.show();
    }

    public void sendBackData(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("COLOR", color);
        System.out.println("color:"+color);
        setResult(RESULT_OK, returnIntent);
        finish();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==android.R.id.home) {
            sendBackData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            sendBackData();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
