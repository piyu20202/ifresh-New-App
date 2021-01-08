package com.ifresh.customer.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.ifresh.customer.activity.MainActivity;

import static com.ifresh.customer.helper.Constant.AREA_ID;
import static com.ifresh.customer.helper.Constant.AREA_N;
import static com.ifresh.customer.helper.Constant.AUTHTOKEN;
import static com.ifresh.customer.helper.Constant.CITY_ID;
import static com.ifresh.customer.helper.Constant.CITY_N;
import static com.ifresh.customer.helper.Constant.COUNTRY_ID;
import static com.ifresh.customer.helper.Constant.COUNTRY_N;
import static com.ifresh.customer.helper.Constant.STATE_ID;
import static com.ifresh.customer.helper.Constant.STATE_N;
import static com.ifresh.customer.helper.Constant.SUBAREA_ID;
import static com.ifresh.customer.helper.Constant.SUBAREA_N;


public class Session {


    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public static final String PREFER_NAME = "eKart";
    public static final String IS_USER_LOGIN = "IsUserLoggedIn";
    public static final String KEY_ID = "id";
    public static final String KEY_FCM_ID = "fcm_id";
    public static final String KEY_ORDER_COUNT = "order_count";
    public static final String KEY_BALANCE = "balance";
    public static final String KEY_EMAIL = "txtemail";
    public static final String KEY_MOBILE = "mobileno";
    public static final String KEY_NAME = "name";
    public static final String KEY_DOB = "dob";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_CITY_N = "city";
    public static final String KEY_AREA_N = "area";
    public static final String KEY_CITY_ID = "city_id";
    public static final String KEY_AREA_ID = "area_id";
    public static final String KEY_PINCODE = "pincode";
    public static final String KEY_STATUS = "status";
    public static final String KEY_CREATEDAT = "createdat";
    public static final String KEY_APIKEY = "apikey";
    public static final String KEY_Password = "password";
    public static final String KEY_Orderqty = "listqty";
    public static final String KEY_Ordervid = "listvid";
    public static final String KEY_Frencid = "frencid";
    public static final String KEY_Frenpid = "frenpid";
    public static final String KEY_Prodvid = "prodvid";
    public static final String KEY_Price = "price";
    public static final String KEY_Imagename = "imagename";
    public static final String KEY_Ordername = "listname";
    public static final String KEY_REFER_CODE = "refer_code";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_FIRSTNAME = "fname";
    public static final String KEY_LASTNAME = "lname";
    public static final String KEY_email = "email";
    public static final String KEY_mobile = "mobile";
    public static final String KEY_id = "_id";
    public static final String KEY_COUNTRY_N = "country_name";
    public static final String KEY_COUNTRY_ID = "country_id";
    public static final String KEY_STATE_ID = "state_id";
    public static final String KEY_SUBAREA_N = "subarea_name";
    public static final String KEY_SUBAREA_ID = "subarea_id";
    public static final String KEY_STATENAME_N = "state_name";
    public static final String KEY_fcmtoken="";



    public static final String KEY_MERCHANT_KEY = "merchant_key";
    public static final String KEY_MERCHANT_ID = "merchant_id";
    public static final String KEY_MERCHANT_SALT = "salt_key";





    public Session(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getData(String id) {
        return pref.getString(id, "");
    }


    public String getordercnt(String key) {
        return pref.getString(key, "");
    }

    public String getCoordinates(String id) {
        return pref.getString(id, "0");
    }

    public void setData(String id, String val) {
        editor.putString(id, val);
        editor.commit();
    }

    public void setLogin(String id, Boolean val) {
        editor.putBoolean(id, val);
        editor.commit();
    }

    public void createUserLoginSession(String fcmId,String id, String name, String email, String mobile, String dob, String city, String area, String cityId, String areaId, String address, String pincode, String status, String createdat, String apikey, String password, String referCode, String latitude, String longitude) {
        editor.clear();
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_FCM_ID, fcmId);
        editor.putString(KEY_ID, id);
        //Log.d("KEYID==", id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_DOB, dob);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_PINCODE, pincode);
        editor.putString(KEY_STATUS, status);
        editor.putString(KEY_CREATEDAT, createdat);
        editor.putString(KEY_APIKEY, apikey);
        editor.putString(KEY_CITY_N, city);
        editor.putString(KEY_AREA_N, area);
        editor.putString(KEY_CITY_ID, cityId);
        editor.putString(KEY_AREA_ID, areaId);
        editor.putString(KEY_Password, password);
        editor.putString(KEY_REFER_CODE, referCode);
        editor.putString(KEY_LATITUDE, latitude);
        editor.putString(KEY_LONGITUDE, longitude);
        editor.commit();
    }


    public void createUserLoginSession_new(String id, String fname, String lname,
            String email, String phone_no, String latitude,  String longitude, String country, String state ,String city, String area, String sub_area, String country_id , String state_id , String cityId, String areaId, String sub_areaId, String authtoken, String refer_code, String token) {

        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_id, id);
        editor.putString(KEY_FIRSTNAME, fname);
        editor.putString(KEY_LASTNAME, lname);
        editor.putString(KEY_email, email);
        editor.putString(KEY_mobile, phone_no);
        editor.putString(KEY_LONGITUDE, longitude);
        editor.putString(KEY_LATITUDE, latitude);
        editor.putString(COUNTRY_N,country);
        editor.putString(STATE_N,state);
        editor.putString(CITY_N, city);
        editor.putString(AREA_N, area);
        editor.putString(SUBAREA_N, sub_area);

        editor.putString(COUNTRY_ID,country_id);
        editor.putString(STATE_ID,state_id);
        editor.putString(CITY_ID, cityId);
        editor.putString(AREA_ID, areaId);
        editor.putString(SUBAREA_ID, sub_areaId);
        editor.putString(AUTHTOKEN, authtoken);
        editor.putString(KEY_REFER_CODE, refer_code);
        editor.putString(KEY_fcmtoken, token);
        editor.apply();
    }


    public boolean checkLogin() {
        if (!this.isUserLoggedIn()) {
            Intent i = new Intent(_context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            return true;
        }
        return false;
    }


    public void logoutUser(Activity activity) {

        editor.clear();
        editor.commit();

        Intent i = new Intent(activity, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
        activity.finish();

    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }


    public void deletePref()
    {
        SharedPreferences sharedPrefs = this._context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.apply();
    }

    public void clear()
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

    public void setString(String key , String value) {
        editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return pref.getString(key, "");
    }

    public void setInt(String key , Integer value) {
        editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public Integer getInt(String key) {
        return pref.getInt(key, 0);
    }

    public void setBoolean(String key, boolean value) {
        editor = pref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        return pref.getBoolean(key,false);
    }

}
