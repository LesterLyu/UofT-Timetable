package com.lvds2000.utsccsuntility;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.EditText;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.NoSuchPaddingException;

/**
 * @author Lester Lyu
 */
public class Setting extends AppCompatPreferenceActivity {
    public static Context context;
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //addPreferencesFromResource(R.xml.pref_general);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Setting");
        GeneralPreferenceFragment gpf = new GeneralPreferenceFragment();

        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        mFragmentTransaction.replace(android.R.id.content, gpf);
        mFragmentTransaction.commit();
        context = this;
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
        System.out.print("Selected");
        return super.onOptionsItemSelected(item);
    }



    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment{
        private static SwitchPreference debug_switch, auto_login_switch;
        private static EditTextPreference usernamePref, passwordPref;
        private static Preference versionField;
        private static ListPreference defaultTimetable;
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            defaultTimetable = (ListPreference) findPreference("defaultTimetable");
            usernamePref = (EditTextPreference) findPreference("username");
            passwordPref = (EditTextPreference) findPreference("password");
            auto_login_switch = (SwitchPreference) findPreference("auto_login");
            debug_switch = (SwitchPreference) findPreference("debug_switch");
            versionField = findPreference("about");
            versionField.setSummary(DrawerActivity.versionName + " | "+ DrawerActivity.currentVersionCode);
            defaultTimetable.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(newValue.equals("0"))
                        DrawerActivity.setIsFall(true);
                    else
                        DrawerActivity.setIsFall(false);

                    return true;
                }
            });
            debug_switch.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (preference.getKey().equalsIgnoreCase("debug_switch")) {
                        System.out.println("debug_switch");
                        DrawerActivity.DEBUG = (Boolean) newValue;
                        DrawerActivity.debugCourse();
                    }
                    System.out.println("XXXXXX" + newValue);
                    return true;
                }
            });
            // set text to saved password when clicked
            passwordPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    passwordPref.setText("");
                    try {
                        StringEncryptor encryptor = new StringEncryptor(Setting.context);
                        String savedPassword = encryptor.decrypt("pass");
                        EditText passwordEditText = passwordPref.getEditText();
                        passwordEditText.setText(savedPassword);
                        passwordEditText.setSelection(passwordEditText.getText().length());
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException |
                            InvalidKeyException | InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });
            passwordPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(preference.getKey().equalsIgnoreCase("password")){
                        System.out.println("password");
                        String password = (String)newValue;
                        try {
                            StringEncryptor encryptor = new StringEncryptor(Setting.context);
                            String savedPassword = encryptor.decrypt("pass");
                            if(savedPassword.equals(password)){

                            }
                            int len = password.length();

                            encryptor.encrypt(password);


                            //System.out.println(password);
                            // set a random string with the same length
                            passwordPref.setText(getSaltString(len));
                        } catch (NoSuchPaddingException | NoSuchAlgorithmException |
                                InvalidKeyException | InvalidKeySpecException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                    return true;
                }
                protected String getSaltString(int len) {
                    String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
                    StringBuilder salt = new StringBuilder();
                    Random rnd = new Random();
                    while (salt.length() < len) {
                        int index = (int) (rnd.nextFloat() * SALTCHARS.length());
                        salt.append(SALTCHARS.charAt(index));
                    }
                    String saltStr = salt.toString();
                    return saltStr;

                }
            });
        }




    }




}