package com.lvds2000.utsccsuntility.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.lvds2000.AcornAPI.auth.Acorn;
import com.lvds2000.utsccsuntility.DrawerActivity;
import com.lvds2000.utsccsuntility.R;
import com.lvds2000.utsccsuntility.Setting;
import com.lvds2000.utsccsuntility.StringEncryptor;

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
    public static String getUsername(){
        return DrawerActivity.loadString("username");
    }

    public static String getPassword(){
        String password = "";
        if(!getUsername().isEmpty()){
            try {
                StringEncryptor encryptor = new StringEncryptor(DrawerActivity.activity);
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

    public static boolean isUserPassChanged(){
        return !DrawerActivity.acorn.isSameUserPass(getUsername(), getPassword());
    }

    /**
     *
     * @return username/password if the user input username/password,
     * otherwise return null.
     */
    public static void promptInputUserPassAndUpdateCourseData(final DrawerActivity drawerActivity, final ProgressDialog progress){

        AlertDialog.Builder builder = new AlertDialog.Builder(DrawerActivity.activity);
        // Get the layout inflater
        LayoutInflater inflater = DrawerActivity.activity.getLayoutInflater();
        // get inflated view
        View promptView = inflater.inflate(R.layout.dialog_signin, null);
        final EditText user = (EditText) promptView.findViewById(R.id.username);
        final EditText pass = (EditText) promptView.findViewById(R.id.password);


        builder.setView(promptView)
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        // save
                        DrawerActivity.saveString("username", user.getText() + "");
                        try {
                            StringEncryptor encryptor = new StringEncryptor(drawerActivity);
                            encryptor.encrypt(pass.getText() + "");
                        } catch (NoSuchPaddingException | InvalidKeySpecException | InvalidKeyException | NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        dialog.cancel();
                        progress.show();
                        new Thread() {
                            @Override
                            public void run() {
                                DrawerActivity.acorn = new Acorn(user.getText() + "", pass.getText() + "");
                                drawerActivity.downloadCourseData(DrawerActivity.acorn);
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
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
