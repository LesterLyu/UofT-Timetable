package com.lvds2000.uoft_timetable.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.lvds2000.AcornAPI.auth.Acorn;
import com.lvds2000.uoft_timetable.DrawerActivity;
import com.lvds2000.uoft_timetable.GradeFragment;
import com.lvds2000.uoft_timetable.R;
import com.lvds2000.uoft_timetable.StringEncryptor;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

/**
 * Created by Lester Lyu on 2/20/2017.
 * @author Lester Lyu
 */

public class UserInfo {

    /**
     * @return the acorn username if exists, otherwise return an empty string
     */
    public static String getUsername(Context context){
        return Configuration.loadString("username", context);
    }

    public static String getPassword(Context context){
        String password = "";
        if(!getUsername(context).isEmpty()){
            try {
                StringEncryptor encryptor = new StringEncryptor(context);
                password = encryptor.decrypt("pass");
                return password;
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
                Log.i("downloadCoursesPrompt", "password is not stored.");

            }
        }
        return null;
    }

    /**
     * clear username and password
     */
    public static void clearPassword(final DrawerActivity drawerActivity){
        //DrawerActivity.saveString("username", "");
        try {
            StringEncryptor encryptor = new StringEncryptor(drawerActivity);
            encryptor.encrypt("");
        } catch (NoSuchPaddingException | InvalidKeySpecException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static boolean isUserPassChanged(Context context){
        return !DrawerActivity.acorn.isSameUserPass(getUsername(context), getPassword(context));
    }


    public static void promptInputUserPassAndLogin(final DrawerActivity drawerActivity, final SwipeRefreshLayout swipeContainer, final GradeFragment gradeFragment){
        final AlertDialog.Builder builder = new AlertDialog.Builder(drawerActivity);
        // Get the layout inflater
        LayoutInflater inflater = drawerActivity.getLayoutInflater();
        // get inflated view
        View promptView = inflater.inflate(R.layout.dialog_signin, null);
        final EditText user = (EditText) promptView.findViewById(R.id.username);
        final EditText pass = (EditText) promptView.findViewById(R.id.password);


        builder.setView(promptView)
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        // save
                        Configuration.saveString("username", user.getText() + "", drawerActivity);
                        try {
                            StringEncryptor encryptor = new StringEncryptor(drawerActivity);
                            encryptor.encrypt(pass.getText() + "");
                        } catch (NoSuchPaddingException | InvalidKeySpecException | InvalidKeyException | NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        dialog.cancel();
                        swipeContainer.setRefreshing(true);
                        DrawerActivity.acorn = new Acorn(user.getText() + "", pass.getText() + "");
                        gradeFragment.refreshHtml(swipeContainer);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog
        drawerActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    /**
     *
     * @return username/password if the user input username/password,
     * otherwise return null.
     */
    public static void promptInputUserPassAndUpdateCourseData(final DrawerActivity drawerActivity, final ProgressDialog progress, final SwipeRefreshLayout swipeContainer){

        final AlertDialog.Builder builder = new AlertDialog.Builder(drawerActivity);
        // Get the layout inflater
        LayoutInflater inflater = drawerActivity.getLayoutInflater();
        // get inflated view
        View promptView = inflater.inflate(R.layout.dialog_signin, null);
        final EditText user = (EditText) promptView.findViewById(R.id.username);
        final EditText pass = (EditText) promptView.findViewById(R.id.password);


        builder.setView(promptView)
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        // save
                        Configuration.saveString("username", user.getText() + "", drawerActivity);
                        try {
                            StringEncryptor encryptor = new StringEncryptor(drawerActivity);
                            encryptor.encrypt(pass.getText() + "");
                        } catch (NoSuchPaddingException | InvalidKeySpecException | InvalidKeyException | NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        dialog.cancel();
                        if(progress != null)
                            progress.show();
                        if(swipeContainer != null)
                            swipeContainer.setRefreshing(true);
                        new Thread() {
                            @Override
                            public void run() {
                                DrawerActivity.acorn = new Acorn(user.getText() + "", pass.getText() + "");
                                drawerActivity.downloadCourseData(DrawerActivity.acorn, swipeContainer);
                            }
                        }.start();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog
        drawerActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

}
