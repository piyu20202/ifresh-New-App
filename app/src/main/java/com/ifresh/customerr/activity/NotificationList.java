package com.ifresh.customerr.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ifresh.customerr.R;
import com.ifresh.customerr.adapter.NotificationAdapter;
import com.ifresh.customerr.helper.ApiConfig;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.helper.Session;
import com.ifresh.customerr.helper.VolleyCallback;
import com.ifresh.customerr.model.Notification;
import com.ifresh.customerr.model.Notification_2;

public class NotificationList extends AppCompatActivity {


    RecyclerView recyclerView;
    ArrayList<Notification_2> notifications;
    ProgressBar progressbar;
    Toolbar toolbar;
    SwipeRefreshLayout swipeLayout;
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
        swipeLayout = findViewById(R.id.swipeLayout);
        progressbar = findViewById(R.id.progressBar);
        txt_notification = findViewById(R.id.txt_notification);
        tvAlert = findViewById(R.id.tvAlert);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(NotificationList.this));



        getNotificationData(NotificationList.this,0);

        /*swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotificationData(NotificationList.this,1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });*/

        btn_all_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_notification.setText("All Notification");
                getNotificationData(NotificationList.this, 0);
            }
        });


        btn_gerneal_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_notification.setText("General Notification");
                getNotificationData(NotificationList.this, 1);
            }
        });


        btn_my_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_notification.setText("My Notification");
                getNotificationData(NotificationList.this,2);
            }
        });

    }

    public void getNotificationData(final Activity activity, final Integer val)
    {
        progressbar.setVisibility(View.VISIBLE);
        String url = Constant.BASEPATH + Constant.GET_NOTIFICATION + session.getData(Session.KEY_id);
        Log.d("url", url);

        Map<String, String> params = new HashMap<String, String>();

        //params.put(Constant.GET_NOTIFICATIONS, Constant.GetVal);

        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
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
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Notification_2 notification_2 = new Notification_2();
                                notification_2.setMtitle(jsonObject.getString("mtitle"));
                                notification_2.setMbody(jsonObject.getString("mbody"));
                                notification_2.setUser_id(jsonObject.getString("user_id"));
                                notification_2.setIs_read(jsonObject.getBoolean("is_read"));
                                notification_2.setIs_general(jsonObject.getBoolean("is_general"));
                                notifications.add(notification_2);
                            }
                            NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationList.this, notifications,val);
                            recyclerView.setAdapter(notificationAdapter);
                            progressbar.setVisibility(View.GONE);
                            tvAlert.setVisibility(View.GONE);

                        }
                        else{
                            progressbar.setVisibility(View.GONE);
                            tvAlert.setVisibility(View.VISIBLE);
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