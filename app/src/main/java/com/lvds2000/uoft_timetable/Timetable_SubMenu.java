package com.lvds2000.uoft_timetable;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lvds2000.uoft_timetable.color_picker.ColorPicker;
import com.lvds2000.uoft_timetable.color_picker.ColorPickerDialog;
import com.lvds2000.uoft_timetable.utils.Configuration;

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
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

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


        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    color_picker(color);
                } else if (position == 1) {
                    loadDefaultColor();
                }
            }
        });
    }

    private void getCourseDescription(final String code) {
        String loadFromStorage = Configuration.loadString(code + "Description", this);
        if(loadFromStorage.equals(""))
            new RetrieveTask().execute(code);
        else
            codeTextView.setText(Html.fromHtml(loadFromStorage.trim()));
        System.out.println("loadFromStorage: " + loadFromStorage);
    }


    private void loadDefaultColor() {
        color = 0;
        TimetableFragment.courseList[index].color=0;
        System.out.println("Saved color, index=" + index + ", code=" + code + ", color=" +  TimetableFragment.courseList[index].color);
        Configuration.saveColor(this);

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
    class RetrieveTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(final String... codes) {
            final String[] out = new String[2];
            try {
                URL url = new URL("https://timetable.iit.artsci.utoronto.ca/api/20169/courses?code=" + codes[0]);
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
                Configuration.saveString(codes[0] + "Description", out[1], context);

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

