package com.ifresh.customer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

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
        //databaseHelper = new DatabaseHelper(activity);
        //databaseHelper.DeleteAllOrderData();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float  value = getResources().getDisplayMetrics().density;
        Log.d("value",""+ value);
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pInfo.versionCode;
            server_versionCode = versionCode;
            version = pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        ApiConfig.GetSettings_Api(activity,mContext);

        //ApiConfig.GetPaymentConfig(activity);
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
                        //Intent intent = new Intent(SplashActivity.this, LocationSelection_K.class);
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

    private void is_appupdated()
    {
        if(versionCode == server_versionCode)
        {
            //app is updated
            storeinfo.setBoolean("is_app_updated", true);
        }
        else if(versionCode < server_versionCode)
        {
            //app is not updated
            storeinfo.setBoolean("is_app_updated", false);
        }

    }


}
