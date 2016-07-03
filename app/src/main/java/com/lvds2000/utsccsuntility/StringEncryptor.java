package com.lvds2000.utsccsuntility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
/**
 * Created by LV on 2015-12-15.
 */
public class StringEncryptor {
    private  SecretKey key;
    private Context mContext;
    public StringEncryptor(Context mContext) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        this.mContext = mContext;
        byte[] keyPhrase = "Something Special for dsvcojhfusdfh phase".getBytes();
        DESKeySpec keySpec = new DESKeySpec(keyPhrase);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        key = keyFactory.generateSecret(keySpec);

    }
    public String encrypt(String s) {
        byte[] encrypted = {};
        try {
            byte[] input = s.getBytes("UTF-8");
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(input);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException |
                IllegalBlockSizeException | InvalidKeyException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        saveByteArray(encrypted, "pass");
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }
    public String decrypt(String name)  {
        SharedPreferences prefs = mContext.getSharedPreferences("com.lvds2000.utsccsuntility_preferences", 0);
        String s = prefs.getString(name, "");
        String decoded = "";
        if(!s.equals("")) {
            byte[] encrypted = Base64.decode(s, Base64.DEFAULT);
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("DES");
                cipher.init(Cipher.DECRYPT_MODE, key);
                byte[] decrypted = cipher.doFinal(encrypted);
                decoded = new String(decrypted, "UTF-8");
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException |
                    IllegalBlockSizeException | InvalidKeyException | UnsupportedEncodingException e) {
                System.out.println("DECRYPT PASSWORD FAILED, PROBABLE NO PASSWORD STORED");
            }
        }
        return decoded;
    }
    public boolean saveInt(int num, String name) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferenceInt", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(name, num);
        return editor.commit();
    }
    public int loadInt(String name) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferenceInt", 0);
        int num = prefs.getInt(name, 0);
        return num;
    }
    public boolean saveByteArray(byte[] myByte, String byteName) {
        SharedPreferences prefs = mContext.getSharedPreferences("com.lvds2000.utsccsuntility_preferences", 0);
        SharedPreferences.Editor editor = prefs.edit();
        String saveThis = Base64.encodeToString(myByte, Base64.DEFAULT);
        editor.putString(byteName, saveThis);
        return editor.commit();
    }
    public byte[] loadByteArray(String byteName ) {
        SharedPreferences prefs = mContext.getSharedPreferences("com.lvds2000.utsccsuntility_preferences", 0);
        String s = prefs.getString(byteName, "");
        byte[] array = Base64.decode(s, Base64.DEFAULT);
        return array;
    }

}
