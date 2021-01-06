package com.ifresh.customer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ifresh.customer.BuildConfig;
import com.ifresh.customer.R;

import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.DatabaseHelper;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.StorePrefrence;
import com.ifresh.customer.kotlin.LocationSelection_K;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1000;
    StorePrefrence storeinfo;
    Session session;
    int versionCode, server_versionCode;
    String version;
    Context mContext =  SplashActivity.this;
    Activity activity = SplashActivity.this;
    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        session = new Session(mContext);
        storeinfo = new StorePrefrence(mContext);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float  value = getResources().getDisplayMetrics().density;

        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pInfo.versionCode;
            server_versionCode = versionCode;
            version = pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        //checkFirstRun();


        if(session.isUserLoggedIn())
        {
            //user is login already and have token no need to action
        }
        else{
            // user is guest and generate token
            if(session.getData("role").equalsIgnoreCase("6"))
            {
                 // user is already guest type no need to call guest user
            }
            else{
                 // user is not guest and must be register as guest and clear old preference memory
                 ApiConfig.Call_GuestToken(activity,session,storeinfo);
            }
        }

        ApiConfig.GetSettings_Api(activity,mContext);
        ApiConfig.GetSettingConfigApi(activity, session);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                loadview();
            }
        }, 3000);

    }

    private void loadview()
    {
            setDefultFlagApp();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if(session.getData(Constant.AREA_ID).length() > 0)
                    {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Intent intent = new Intent(SplashActivity.this, LocationSelection_K.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }, SPLASH_TIME_OUT);
    }

    private void setDefultFlagApp() {
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }


    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            // This is just a normal run
            Toast.makeText(mContext, "normal run", Toast.LENGTH_LONG).show();
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {
            // TODO This is a new install (or the user cleared the shared preferences)
            Toast.makeText(mContext, "new install", Toast.LENGTH_LONG).show();
            session.clear();
            storeinfo.clear();

        } else if (currentVersionCode > savedVersionCode) {
            // TODO This is an upgrade
            session.clear();
            storeinfo.clear();
            Toast.makeText(mContext, "update", Toast.LENGTH_LONG).show();

        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }


}
