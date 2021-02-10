package com.ifresh.customer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.ifresh.customer.R;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.AppController;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.VolleyCallback;

public class WebViewActivity extends AppCompatActivity {
    public ProgressBar prgLoading;
    public WebView mWebView;
    public String type;
    public Toolbar toolbar;
    public TextView txt_appinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        txt_appinfo = findViewById(R.id.txt_appinfo);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        type = getIntent().getStringExtra("type");

        prgLoading = findViewById(R.id.prgLoading);
        mWebView = findViewById(R.id.webView1);
        mWebView.getSettings().setJavaScriptEnabled(true);
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        txt_appinfo.setText("iFresh App Version Name "+pinfo.versionName+ " "+ "App Version Code "+pinfo.versionCode);





        mWebView.setWebViewClient(new WebViewClient());
        try {
            if (AppController.isConnected(this)) {
                switch (type) {
                    case "5":
                        getSupportActionBar().setTitle(getString(R.string.privacy_policy));
                        GetContent(Constant.GET_PRIVACY, type);
                        break;
                    case "4":
                        getSupportActionBar().setTitle(getString(R.string.terms_conditions));
                        GetContent(Constant.GET_TERMS, type);
                        break;
                    case "2":
                        getSupportActionBar().setTitle(getString(R.string.contact));
                        GetContent(Constant.GET_CONTACT, type);
                        break;
                    case "1":
                        getSupportActionBar().setTitle(getString(R.string.about));
                        GetContent(Constant.GET_ABOUT_US, type);
                        break;
                    case "3":
                        getSupportActionBar().setTitle(getString(R.string.faq));
                        GetContent(Constant.GET_FAQ, type);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetContent(final String type, final String key) {
        String url = Constant.BASEPATH + Constant.GET_CMSPAGE + key ;
        Log.d("url", url);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("================="+type +"===================="+ response);
                if (result)
                {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("success").equalsIgnoreCase("200")) {

                            JSONObject data_obj = obj.getJSONObject("data");
                            String key_val="";
                            if(key.equalsIgnoreCase("1"))
                            {
                                key_val="about";
                            }
                            else if(key.equalsIgnoreCase("2"))
                            {
                                key_val="contactus";
                            }
                            else if(key.equalsIgnoreCase("3"))
                            {
                                key_val="FAQ";
                            }
                            else if(key.equalsIgnoreCase("4"))
                            {
                                key_val="Terms & Conditions";
                            }
                            else if(key.equalsIgnoreCase("5"))
                            {
                                key_val="Privacy Policy";
                            }
                            String keystr = data_obj.getString(key_val);
                            mWebView.setVerticalScrollBarEnabled(true);
                            mWebView.loadDataWithBaseURL("", keystr, "text/html", "UTF-8", "");
                            mWebView.setBackgroundColor(getResources().getColor(R.color.bg_color));
                        } else {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                        prgLoading.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, WebViewActivity.this,url , params, true);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }
}
