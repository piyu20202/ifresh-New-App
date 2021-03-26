package com.ifresh.customer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.ifresh.customer.BuildConfig;
import com.ifresh.customer.R;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.StorePrefrence;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.kotlin.LocationSelection_K;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ifresh.customer.helper.Constant.BASEPATH;
import static com.ifresh.customer.helper.Constant.SETTINGS_PAGE;

public class SplashActivity extends AppCompatActivity {

    StorePrefrence storeinfo;
    Session session;
    int versionCode, server_versionCode;
    String version,token;
    Context mContext =  SplashActivity.this;
    Activity activity = SplashActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        session = new Session(mContext);
        storeinfo = new StorePrefrence(mContext);
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pInfo.versionCode;
            server_versionCode = versionCode;
            version = pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }


        checkFirstRun();

        /*FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult)
            {
                token = instanceIdResult.getToken();
                if (!token.equals(session.getData(Constant.KEY_FCM_ID)))
                {
                    Log.d("token", token);
                    session.setData("token", token);
                }
                else{
                    Log.d("else", "else");
                }
            }
        });
        */

        if (!session.isUserLoggedIn())
        {
            // user is guest and generate token
            if(session.getData("role").equalsIgnoreCase("6"))
            {
                 // user is already guest type no need to call guest user
                if (!session.getData(Session.KEY_appversioncode).equalsIgnoreCase(String.valueOf(versionCode))) {
                       //if user is not registered
                       SendVersionCode_Api(activity,mContext);
                }
            }
            else{
                 // user is not guest and must be register as guest and clear old preference memory
                 Log.d("token", session.getData("token"));
                 ApiConfig.Call_GuestToken(activity,session);
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
        int SPLASH_TIME_OUT = 1000;
        new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if(session.getData(Constant.AREA_ID).length() > 0)
                    {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        //Intent intent = new Intent(SplashActivity.this, SignUpActivity_K.class);
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
            //Toast.makeText(mContext, "normal run", Toast.LENGTH_LONG).show();
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {
            // TODO This is a new install (or the user cleared the shared preferences)
            //Toast.makeText(mContext, "new install", Toast.LENGTH_LONG).show();
            session.clear();
            storeinfo.clear();

        } else if (currentVersionCode > savedVersionCode) {
            // TODO This is an upgrade
            session.clear();
            storeinfo.clear();
            //Toast.makeText(mContext, "update", Toast.LENGTH_LONG).show();

        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }


    public void SendVersionCode_Api(final Activity activity, final Context ctx)
    {
        final Session session = new Session(ctx);
        Map<String, String> params = new HashMap<String, String>();
        params.put("_id", session.getData(Session.KEY_id));
        params.put("app_version", String.valueOf(versionCode));

        ApiConfig.RequestToVolley_POST(new VolleyCallback()
        {
            @Override
            public void onSuccess(boolean result, String response)
            {
                if (result)
                {
                    try {
                         //System.out.println("====res area" + response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }, activity, BASEPATH + SETTINGS_PAGE, params, true);

    }







}






