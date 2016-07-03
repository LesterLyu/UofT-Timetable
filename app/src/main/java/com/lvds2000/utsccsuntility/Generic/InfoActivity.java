package com.lvds2000.utsccsuntility.Generic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lvds2000.utsccsuntility.AnalyticsTrackers;
import com.lvds2000.utsccsuntility.Info;
import com.lvds2000.utsccsuntility.R;

import java.util.ArrayList;


public class InfoActivity extends AppCompatActivity {

    ListView lv;
    ArrayAdapter<String> listAdapter ;
    ArrayList<String> arr = new ArrayList<>();
    ArrayList<Info> infoList = new ArrayList<>();
    boolean isShowAnn, isShowExe, isShowTest, isShowLec, isShowTut, isShowPra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_csca08);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        lv = (ListView)findViewById(R.id.listView);

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr);

        lv.setAdapter(listAdapter);
        Intent intent = this.getIntent();
        infoList = (ArrayList)intent.getParcelableArrayListExtra("info");
        for(Info info: infoList){
            arr.add(info.getName());
            if(info.getName().equalsIgnoreCase("ann")){
                Ann.url = info.getUrl();
            }
            else if(info.getName().equalsIgnoreCase("exe")){
                Exercise.url = info.getUrl();
            }
        }
//        arr.add("Announcement");
//        arr.add("Exercise/Assignment");
//        arr.add("Term Tests");
//        arr.add("Weekly Readings/Slides");
//        arr.add("Tutorial Materials");
        //arr.add("Practical");

        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Info info = infoList.get(position);
                if(info.getName().equalsIgnoreCase("ann")){
                    ann();
                }
                else if(info.getName().equalsIgnoreCase("exe")){
                    exe();
                }
            }
        });
        //google analytics
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_display_csca08, menu);
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

    public void exe() {
        Intent intent = new Intent(this, Exercise.class);
        startActivity(intent);

    }
    public void ann() {
        Intent intent = new Intent(this, Ann.class);
        startActivity(intent);

    }
    public void csca08_lec() {
        Intent intent = new Intent(this, Lec.class);
        startActivity(intent);

    }
    public void csca08_pra() {
        Intent intent = new Intent(this, Pra.class);
        startActivity(intent);

    }
    public void csca08_tut() {
        Intent intent = new Intent(this, Tut.class);
        startActivity(intent);

    }
    public void csca08_test() {
        Intent intent = new Intent(this, Test.class);
        startActivity(intent);

    }
}

