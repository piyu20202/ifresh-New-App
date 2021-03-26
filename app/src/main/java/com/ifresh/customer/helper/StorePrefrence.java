package com.ifresh.customer.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;


import java.io.ByteArrayOutputStream;

import static com.ifresh.customer.helper.Constant.EMAIL;
import static com.ifresh.customer.helper.Constant.FNAME;
import static com.ifresh.customer.helper.Constant.ID;
import static com.ifresh.customer.helper.Constant.LNAME;
import static com.ifresh.customer.helper.Constant.MOBILE;
import static com.ifresh.customer.helper.Session.IS_USER_LOGIN;

/**
 * Created by Mobile on 26-09-2017.
 */

public class StorePrefrence {

    private SharedPreferences prefencs;
    private SharedPreferences.Editor editor;

    public StorePrefrence(Context context) {
        prefencs = context.getSharedPreferences("MyPref",Context.MODE_PRIVATE);
    }

    public void setString(String key , String value) {
        editor = prefencs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return prefencs.getString(key, "");
    }


    public void setInt(String key , Integer value) {
        editor = prefencs.edit();
        editor.putInt(key, value);
        editor.apply();
    }


    public static String encodeTobase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        //Log.d("Image Log:", imageEncoded);
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public void setImage(String key,Bitmap yourbitmap)
    {
        editor = prefencs.edit();
        editor.putString(key, encodeTobase64(yourbitmap));
        editor.apply();
    }

    public Bitmap getImage(String key)
    {
        String input = prefencs.getString(key, "");
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public void clear()
    {
        SharedPreferences.Editor editor = prefencs.edit();
        editor.clear();
        editor.apply();
    }

    public Integer getInt(String key) {
        return prefencs.getInt(key, 0);
    }

    public boolean getBoolean(String key) {
        return prefencs.getBoolean(key,false);
    }

    public void setBoolean(String key, boolean value) {

        editor = prefencs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void createUserLoginSession(String id, String fname, String lname, String email, String phone_no ) {
        editor = prefencs.edit();
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(ID, id);
        editor.putString(FNAME, fname);
        editor.putString(LNAME, lname);
        editor.putString(EMAIL, email);
        editor.putString(MOBILE, phone_no);


        editor.apply();
    }






}
