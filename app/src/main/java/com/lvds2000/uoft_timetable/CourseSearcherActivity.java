package com.lvds2000.uoft_timetable;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.lvds2000.AcornAPI.course.CourseSearcher;
import com.lvds2000.AcornAPI.course.ResponseListener;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CourseSearcherActivity extends AppCompatActivity {

    private MyAdapter mAdapter;
    private EditText courseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_searcher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search_course);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        courseText = (EditText)findViewById(R.id.course_code_edit_text);
        courseText.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
        courseText.setOnKeyListener(new View.OnKeyListener(){

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    requestUpdate();
                    return true;
                }
                return false;
            }
        });


        Button button = (Button)findViewById(R.id.course_search_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestUpdate();
            }
        });

        RecyclerView rv = (RecyclerView)findViewById(R.id.courseSearchResultView);
        rv.setNestedScrollingEnabled(false);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter();
        rv.setAdapter(mAdapter);

    }

    private void requestUpdate(){
        CourseSearcher.getCourseInfo(courseText.getText().toString(), new ResponseListener() {
            @Override
            public void response(final Map<String, JsonObject> data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.refresh(data);
                    }
                });
                courseText.onEditorAction(EditorInfo.IME_ACTION_DONE);
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private Map<String, JsonObject> data;
    private List<String> keys;

    MyAdapter() {
        data = new HashMap<>();
        keys = new ArrayList<>();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        View infoView;
        ViewHolder(View v) {
            super(v);
            infoView = v;
        }
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View infoView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_info, parent, false);
        return new MyAdapter.ViewHolder(infoView);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        View infoView = holder.infoView;
        TextView courseCode = (TextView) infoView.findViewById(R.id.course_code);
        TextView courseTitle = (TextView) infoView.findViewById(R.id.course_title);
        TextView courseDescription = (TextView) infoView.findViewById(R.id.course_description);
        TextView coursePrerequisite = (TextView) infoView.findViewById(R.id.course_prerequisite);
        TextView courseCorequisite = (TextView) infoView.findViewById(R.id.course_corequisite);
        TextView courseExclusion = (TextView) infoView.findViewById(R.id.course_exclusion);
        TextView courseRecommendedPreparation = (TextView) infoView.findViewById(R.id.course_recommended_preparation);
        TextView courseBreadthCategories = (TextView) infoView.findViewById(R.id.course_breadth_categories);
        TextView courseDistributionCategories = (TextView) infoView.findViewById(R.id.course_distribution_categories);

        setText(courseCode, data.get(keys.get(position)).get("code").getAsString());
        setText(courseTitle, data.get(keys.get(position)).get("courseTitle").getAsString());
        setText(courseDescription, data.get(keys.get(position)).get("courseDescription").getAsString());
        setText(coursePrerequisite, data.get(keys.get(position)).get("prerequisite").getAsString());
        setText(courseCorequisite, data.get(keys.get(position)).get("corequisite").getAsString());
        setText(courseExclusion, data.get(keys.get(position)).get("exclusion").getAsString());
        setText(courseRecommendedPreparation, data.get(keys.get(position)).get("recommendedPreparation").getAsString());
        setText(courseBreadthCategories, data.get(keys.get(position)).get("breadthCategories").getAsString());
        setText(courseDistributionCategories, data.get(keys.get(position)).get("distributionCategories").getAsString());

        holder.infoView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT ,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

    }
    private void setText(TextView tv, String s){
        if(!s.isEmpty())
            tv.setText(Jsoup.parse(s).text());
        else
            ((View)tv.getParent()).setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void refresh(Map<String, JsonObject> data){
        this.data.clear();
        this.data.putAll(data);
        keys.clear();
        for(String key: data.keySet())
            keys.add(key);
        Collections.sort(keys);
        notifyDataSetChanged();
    }

}
