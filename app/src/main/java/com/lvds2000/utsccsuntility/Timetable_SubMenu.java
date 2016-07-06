package com.lvds2000.utsccsuntility;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lvds2000.utsccsuntility.A08.A08_Ann;
import com.lvds2000.utsccsuntility.A08.A08_Exercise;
import com.lvds2000.utsccsuntility.A20.A20_Ann;
import com.lvds2000.utsccsuntility.A20.A20_Calendar;
import com.lvds2000.utsccsuntility.A20.A20_Exe;
import com.lvds2000.utsccsuntility.A20.A20_Lec;
import com.lvds2000.utsccsuntility.A67.A67_Ann;
import com.lvds2000.utsccsuntility.A67.A67_Calendar;
import com.lvds2000.utsccsuntility.A67.A67_Exe;
import com.lvds2000.utsccsuntility.A67.A67_Lec;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

public class Timetable_SubMenu extends AppCompatActivity {

    ListView lv;
    ListAdapter listAdapter;
    private ColorPicker colorPicker;
    static int color = 0, index = 0;
    static String code;
    Context context;
    TextView codeTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_timetable_sub_menu);
        this.setTitle("Course Menu");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);//Theme setting
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar));
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.md_indigo_700));
        } else {
            // Implement this feature without material design
        }

        Intent intent = this.getIntent();
        System.out.println(intent.getIntExtra("COLOR", 0));
        color = intent.getIntExtra("COLOR", 0);
        code = intent.getStringExtra("CODE");
        index = intent.getIntExtra("INDEX", 0);

        this.setTitle(code);
        codeTextView = (TextView)findViewById(R.id.codeTextView);
        codeTextView.setText("Loading...");
        getCourseDescription(code);

        lv = (ListView)findViewById(R.id.listView3);
        ArrayList<String> arr = new ArrayList<String>();

        listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arr);

        lv.setAdapter(listAdapter);
        arr.add("Pick color");
        arr.add("Load Default Color");
        if(code.contains("CSCA08")){
            arr.add("Announcement");
            arr.add("Exercise/Assignment");
            arr.add("Term Tests");
            arr.add("Weekly Readings/Slides");
            arr.add("Tutorial Materials");
            arr.add("Practical");
        }
        else if(code.contains("CSCA67") || code.contains("CSCA20") ){
            arr.add("Announcement");
            arr.add("Exercise/Assignment");
            arr.add("Calendar");
            arr.add("Lectures");
        }


        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    color_picker(color);
                } else if (position == 1) {
                    loadDefaultColor();
                }
                if(code.contains("CSCA08")){
                    if (position == 2) {
                        IntentOpener.csca08_ann(context);
                    }
                    else if (position == 3) {
                        IntentOpener.csca08_exe(context);
                    }
                    else if (position == 4) {
                        IntentOpener.csca08_test(context);
                    }
                    else if (position==5){
                        IntentOpener.csca08_lec(context);
                    }
                    else if(position==6){
                        IntentOpener.csca08_tut(context);
                    }
                    else if(position==7){
                        IntentOpener.csca08_pra(context);
                    }
                }
                else if(code.contains("CSCA20")){
                    if (position == 2) {
                        csca20_ann();
                    } else if (position == 3) {
                        csca20_exe();
                    }else if (position == 4) {
                        csca20Calendar();
                    } else if (position == 5) {
                        csca20_lec();
                    }
                }
                else if(code.contains("CSCA67")){
                    if (position == 2) {
                        csca67_ann();
                    } else if (position == 3) {
                        csca67_exe();
                    }else if (position == 4) {
                        csca67Calendar();
                    } else if (position == 5) {
                        csca67_lec();
                    }
                }
            }
        });


    }

    private void getCourseDescription(final String code) {
        String loadFromStorage = DrawerActivity.loadString(code + "Description", context);
        if(loadFromStorage.equals(""))
            new RetrieveTask().execute(code);
        else
            codeTextView.setText(Html.fromHtml(loadFromStorage.trim()));
        System.out.println("loadFromStorage: " + loadFromStorage);
    }



    private void loadDefaultColor() {
        color = 0;
        TimetableFragment.plannedCourseList[index].color=0;
        System.out.println("Saved color, index=" + index + ", code=" + code + ", color=" +  TimetableFragment.plannedCourseList[index].color);
        DrawerActivity.saveColor();

    }

    private void color_picker(int color) {
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, color, new ColorPickerDialog.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {

                Timetable_SubMenu.color = color;

            }

        });
        colorPickerDialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                sendBackData();
                //this.onBackPressed();
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
    public void sendBackData(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("COLOR", color);
        System.out.println("color:"+color);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
    public void csca08_exe() {
        Intent intent = new Intent(this, A08_Exercise.class);
        startActivity(intent);

    }
    public void csca08_ann() {
        Intent intent = new Intent(this, A08_Ann.class);
        startActivity(intent);

    }
    private void csca20_exe() {
        Intent intent = new Intent(this, A20_Exe.class);
        startActivity(intent);

    }
    private void csca20_ann() {
        Intent intent = new Intent(this, A20_Ann.class);
        startActivity(intent);

    }
    private void csca20Calendar() {
        Intent intent = new Intent(this, A20_Calendar.class);
        startActivity(intent);
    }
    private void csca20_lec() {
        Intent intent = new Intent(this, A20_Lec.class);
        startActivity(intent);
    }
    private void csca67_exe() {
        Intent intent = new Intent(this, A67_Exe.class);
        startActivity(intent);

    }
    private void csca67_ann() {
        Intent intent = new Intent(this, A67_Ann.class);
        startActivity(intent);

    }
    private void csca67Calendar () {
        Intent intent = new Intent(this, A67_Calendar.class);
        startActivity(intent);
    }
    private void csca67_lec() {
        Intent intent = new Intent(this, A67_Lec.class);
        startActivity(intent);
    }

    class RetrieveTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(final String... codes) {
            final String[] out = new String[2];
            try {
                URL url = new URL("https://timetable.iit.artsci.utoronto.ca/api/courses?code=" + codes[0] + "&section=");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                InputStream stream = conn.getInputStream();
                out[0] = convertStreamToString(stream);
                out[1] = out[0].substring(out[0].indexOf("<p>") + 3, out[0].indexOf("<\\/p>")) ;
                System.out.println("description: " + out[1]);
                DrawerActivity.saveString(codes[0] + "Description", out[1], context);

                stream.close();

            }catch(SocketTimeoutException e){
                System.out.println("Time out");
            }
            catch (Exception e) {
                e.printStackTrace();
            }


            return out[1];
        }
        @Override
        protected void onPostExecute(String out) {
            if(out != null)
                codeTextView.setText(Html.fromHtml(out.trim()));
            System.out.println("onPostExecute: " + out);
            //codeTextView.setText("Loading...");

        }

        String convertStreamToString(java.io.InputStream is) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }
}

