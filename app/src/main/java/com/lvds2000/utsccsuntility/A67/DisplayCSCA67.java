package com.lvds2000.utsccsuntility.A67;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lvds2000.utsccsuntility.AnalyticsTrackers;
import com.lvds2000.utsccsuntility.R;

import java.util.ArrayList;

import static com.lvds2000.utsccsuntility.R.id.listView;

public class DisplayCSCA67 extends AppCompatActivity {

    ListView lv;
    ArrayAdapter<String> listAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_csca67);
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
        lv = (ListView)findViewById(listView);
        ArrayList<String> arr = new ArrayList<String>();

        listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arr);

        lv.setAdapter(listAdapter);
        arr.add("Announcement");
        arr.add("Exercise/Assignment");
        arr.add("Calendar");
        arr.add("Lectures");
        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    csca67_ann();
                } else if (position == 1) {
                    csca67_exe();
                } else if (position == 2) {
                    cscaCalendar();
                } else if (position == 3) {
                    csca67_lec();
                }
            }
        });
        //google analytics
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_display_csca67, menu);
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
    private void csca67_exe() {
        Intent intent = new Intent(this, A67_Exe.class);
        startActivity(intent);

    }
    private void csca67_ann() {
        Intent intent = new Intent(this, A67_Ann.class);
        startActivity(intent);

    }
    private void cscaCalendar() {
        Intent intent = new Intent(this, A67_Calendar.class);
        startActivity(intent);
    }
    private void csca67_lec() {
        Intent intent = new Intent(this, A67_Lec.class);
        startActivity(intent);
    }

}
