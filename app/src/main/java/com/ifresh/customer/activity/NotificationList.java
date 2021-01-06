package com.ifresh.customer.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ifresh.customer.R;
import com.ifresh.customer.adapter.NotificationAdapter;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.model.Notification_2;

public class NotificationList extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Notification_2> notifications;
    ProgressBar progressbar;
    Toolbar toolbar;
    TextView tvAlert,txt_notification;
    Session session;
    Context mContext = NotificationList.this;
    Button btn_gerneal_msg, btn_my_msg,btn_all_msg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        toolbar = findViewById(R.id.toolbar);
        session = new Session(getApplicationContext());
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.notifications));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_gerneal_msg = findViewById(R.id.btn_gerneal_msg);
        btn_my_msg = findViewById(R.id.btn_my_msg);
        btn_all_msg = findViewById(R.id.btn_all_msg);
        progressbar = findViewById(R.id.progressBar);
        txt_notification = findViewById(R.id.txt_notification);
        tvAlert = findViewById(R.id.tvAlert);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(NotificationList.this));



        getNotificationData_mynotif(NotificationList.this,0);



        btn_all_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_notification.setText("All Notification");
                notifications.clear();
                getNotificationData_All(NotificationList.this, 0);
            }
        });


        btn_gerneal_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_notification.setText("General Notification");
                notifications.clear();
                getNotificationData_gernal(NotificationList.this, 1);
            }
        });


        btn_my_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_notification.setText("My Notification");
                notifications.clear();
                getNotificationData_mynotif(NotificationList.this,2);
            }
        });

    }

    public void getNotificationData_All(final Activity activity, final Integer val)
    {
        progressbar.setVisibility(View.VISIBLE);
        String url = Constant.BASEPATH + Constant.GET_NOTIFICATION + session.getData(Session.KEY_id);
        Log.d("url", url);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("===n response " + response);
                        notifications = new ArrayList<>();
                        JSONObject object = new JSONObject(response);
                        if (object.getInt(Constant.SUCESS) == 200)
                        {
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            if(jsonArray.length() > 0)
                            {
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Notification_2 notification_2 = new Notification_2();


                                        notification_2.setMtitle(jsonObject.getString("mtitle"));
                                        notification_2.setMbody(jsonObject.getString("mbody"));
                                        notification_2.setUser_id(jsonObject.getString("user_id"));
                                        notification_2.setIs_read(jsonObject.getBoolean("is_read"));
                                        notification_2.setIs_general(jsonObject.getBoolean("is_general"));


                                        String str_date_1 = jsonObject.getString("created");
                                        String[] strdate_arr_2 = str_date_1.split("T");
                                        String[] strdate_arr3 = strdate_arr_2[0].split("-");
                                        String start_date = strdate_arr3[2]+"-"+strdate_arr3[1]+"-"+strdate_arr3[0];

                                        notification_2.setDate(start_date);

                                        Instant s = Instant.parse(jsonObject.getString("created"));
                                        ZoneId.of("Asia/Kolkata");
                                        LocalDateTime l = LocalDateTime.ofInstant(s, ZoneId.of("Asia/Kolkata"));

                                        String str_date_t = l.toString();
                                        String[] strdate_arr_2_t = str_date_t.split("T");
                                        String[] strdate_arr3_t = strdate_arr_2_t[1].split(":");
                                        String start_time = strdate_arr3_t[0]+":"+strdate_arr3_t[1]+":"+strdate_arr3_t[2];

                                        String start_time_2 = start_time.substring(0,8);

                                        notification_2.setTime(start_time_2);

                                        notifications.add(notification_2);




                                }
                                NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationList.this, notifications,val);
                                recyclerView.setVisibility(View.VISIBLE);
                                recyclerView.setAdapter(notificationAdapter);
                                progressbar.setVisibility(View.GONE);
                                tvAlert.setVisibility(View.GONE);

                            }
                        }
                        else{
                            recyclerView.setVisibility(View.GONE);
                            progressbar.setVisibility(View.GONE);
                            tvAlert.setVisibility(View.VISIBLE);
                            Toast.makeText(mContext, "No Notification Message", Toast.LENGTH_SHORT)
                                    .show();

                        }
                    } catch (Exception e) {
                        recyclerView.setVisibility(View.GONE);
                        progressbar.setVisibility(View.GONE);
                        tvAlert.setVisibility(View.VISIBLE);
                        Toast.makeText(mContext, "No Notification Message", Toast.LENGTH_SHORT)
                                .show();
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_NOTIFICATION+session.getData(session.KEY_id) , params, true);
    }

    public void getNotificationData_mynotif(final Activity activity, final Integer val)
    {
        progressbar.setVisibility(View.VISIBLE);
        String url = Constant.BASEPATH + Constant.GET_NOTIFICATION + session.getData(Session.KEY_id);
        Log.d("url", url);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("===n response " + response);
                        notifications = new ArrayList<>();
                        JSONObject object = new JSONObject(response);
                        if (object.getInt(Constant.SUCESS) == 200)
                        {
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            if(jsonArray.length() > 0)
                            {
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Notification_2 notification_2 = new Notification_2();

                                    if(!jsonObject.getBoolean("is_general"))
                                    {
                                        notification_2.setMtitle(jsonObject.getString("mtitle"));
                                        notification_2.setMbody(jsonObject.getString("mbody"));
                                        notification_2.setUser_id(jsonObject.getString("user_id"));
                                        notification_2.setIs_read(jsonObject.getBoolean("is_read"));
                                        notification_2.setIs_general(jsonObject.getBoolean("is_general"));


                                        String str_date_1 = jsonObject.getString("created");
                                        String[] strdate_arr_2 = str_date_1.split("T");
                                        String[] strdate_arr3 = strdate_arr_2[0].split("-");
                                        String start_date = strdate_arr3[2]+"-"+strdate_arr3[1]+"-"+strdate_arr3[0];

                                        notification_2.setDate(start_date);

                                        Instant s = Instant.parse(jsonObject.getString("created"));
                                        ZoneId.of("Asia/Kolkata");
                                        LocalDateTime l = LocalDateTime.ofInstant(s, ZoneId.of("Asia/Kolkata"));

                                        String str_date_t = jsonObject.getString("created");
                                        String[] strdate_arr_2_t = str_date_t.split("T");
                                        String[] strdate_arr3_t = strdate_arr_2_t[1].split(":");
                                        String start_time = strdate_arr3_t[0]+":"+strdate_arr3_t[1]+":"+strdate_arr3_t[2];

                                        String start_time_2 = start_time.substring(0,8);

                                        notification_2.setTime(start_time_2);

                                        notifications.add(notification_2);
                                    }



                                }
                                NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationList.this, notifications,val);
                                recyclerView.setVisibility(View.VISIBLE);
                                recyclerView.setAdapter(notificationAdapter);
                                progressbar.setVisibility(View.GONE);
                                tvAlert.setVisibility(View.GONE);

                            }
                        }
                        else{
                            recyclerView.setVisibility(View.GONE);
                            progressbar.setVisibility(View.GONE);
                            tvAlert.setVisibility(View.VISIBLE);
                            Toast.makeText(mContext, "No Notification Message", Toast.LENGTH_SHORT)
                                    .show();

                        }
                    } catch (Exception e) {
                        recyclerView.setVisibility(View.GONE);
                        progressbar.setVisibility(View.GONE);
                        tvAlert.setVisibility(View.VISIBLE);
                        Toast.makeText(mContext, "No Notification Message", Toast.LENGTH_SHORT)
                                .show();
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_NOTIFICATION+session.getData(session.KEY_id) , params, true);
    }


    public void getNotificationData_gernal(final Activity activity, final Integer val)
    {
        progressbar.setVisibility(View.VISIBLE);
        String url = Constant.BASEPATH + Constant.GET_NOTIFICATION + session.getData(Session.KEY_id);
        Log.d("url", url);

        Map<String, String> params = new HashMap<String, String>();

        //params.put(Constant.GET_NOTIFICATIONS, Constant.GetVal);

        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("===n response " + response);
                        notifications = new ArrayList<>();
                        JSONObject object = new JSONObject(response);
                        if (object.getInt(Constant.SUCESS) == 200)
                        {
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            if(jsonArray.length() > 0)
                            {
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    /*if(val == 0)
                                    {
                                        // all data in notification
                                    }
                                    else if(va)
                                    */


                                    if(jsonObject.getBoolean("is_general"))
                                    {
                                        Notification_2 notification_2 = new Notification_2();
                                        notification_2.setMtitle(jsonObject.getString("mtitle"));
                                        notification_2.setMbody(jsonObject.getString("mbody"));
                                        notification_2.setUser_id(jsonObject.getString("user_id"));
                                        notification_2.setIs_read(jsonObject.getBoolean("is_read"));
                                        notification_2.setIs_general(jsonObject.getBoolean("is_general"));


                                        String str_date_1 = jsonObject.getString("created");
                                        String[] strdate_arr_2 = str_date_1.split("T");
                                        String[] strdate_arr3 = strdate_arr_2[0].split("-");
                                        String start_date = strdate_arr3[2]+"-"+strdate_arr3[1]+"-"+strdate_arr3[0];

                                        notification_2.setDate(start_date);

                                        Instant s = Instant.parse(jsonObject.getString("created"));
                                        ZoneId.of("Asia/Kolkata");
                                        LocalDateTime l = LocalDateTime.ofInstant(s, ZoneId.of("Asia/Kolkata"));

                                        String str_date_t = l.toString();
                                        String[] strdate_arr_2_t = str_date_t.split("T");
                                        String[] strdate_arr3_t = strdate_arr_2_t[1].split(":");
                                        String start_time = strdate_arr3_t[0]+":"+strdate_arr3_t[1]+":"+strdate_arr3_t[2];

                                        String start_time_2 = start_time.substring(0,8);

                                        notification_2.setTime(start_time_2);
                                        notifications.add(notification_2);

                                    }
                                }

                                if(notifications.size() > 0)
                                {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationList.this, notifications,val);
                                    recyclerView.setAdapter(notificationAdapter);
                                    progressbar.setVisibility(View.GONE);
                                    tvAlert.setVisibility(View.GONE);
                                }
                                else{
                                    recyclerView.setVisibility(View.GONE);
                                    progressbar.setVisibility(View.GONE);
                                    tvAlert.setVisibility(View.VISIBLE);
                                    Toast.makeText(mContext, "No Notification Message", Toast.LENGTH_SHORT)
                                            .show();
                                }



                            }
                        }
                        else{
                            progressbar.setVisibility(View.GONE);
                            tvAlert.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            Toast.makeText(mContext, "No Notification Message", Toast.LENGTH_SHORT)
                                    .show();

                        }
                    } catch (Exception e) {
                        progressbar.setVisibility(View.GONE);
                        tvAlert.setVisibility(View.VISIBLE);
                        Toast.makeText(mContext, "No Notification Message", Toast.LENGTH_SHORT)
                                .show();
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_NOTIFICATION+session.getData(session.KEY_id) , params, true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}