package com.ifresh.customer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ifresh.customer.R;
import com.ifresh.customer.adapter.FriendListReferAdapter;
import com.ifresh.customer.adapter.WalletBalanceAdapter;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.StorePrefrence;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.model.FriendCodeUser;
import com.ifresh.customer.model.WalletBalance;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendCodeList extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<FriendCodeUser> friendCodeList;
    ProgressBar progressbar;
    Toolbar toolbar;
    SwipeRefreshLayout swipeLayout;
    TextView tvAlert;
    Session session;
    LinearLayout msgView;
    StorePrefrence storeinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        storeinfo = new StorePrefrence(FriendCodeList.this);
        session = new Session(FriendCodeList.this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.friend_code_list));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeLayout = findViewById(R.id.swipeLayout);
        progressbar = findViewById(R.id.progressBar);
        tvAlert = findViewById(R.id.tvAlert);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(FriendCodeList.this));
        msgView = findViewById(R.id.msgView);

        swipeLayout.setRefreshing(false);
        swipeLayout.setEnabled(false);

        getFriendCodeData(FriendCodeList.this);
    }

    public void getFriendCodeData(final Activity activity) {
        progressbar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("===n response " + response);
                        friendCodeList = new ArrayList<>();
                        JSONObject object = new JSONObject(response);


                        if (object.getString("sucess").equalsIgnoreCase("Success"))
                        {
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            if(jsonArray.length() > 0)
                            {
                                msgView.setVisibility(View.GONE);
                                //swipeLayout.setVisibility(View.VISIBLE);
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    FriendCodeUser friendCodeUser = new FriendCodeUser();
                                    String fname = jsonObject.getString("fname");
                                    String lname = jsonObject.getString("lname");

                                    friendCodeUser.setName(fname + " " + lname);

                                    String str_date_1 = jsonObject.getString("created");
                                    String[] strdate_arr_2 = str_date_1.split("T");
                                    String[] strdate_arr3 = strdate_arr_2[0].split("-");
                                    String start_date = strdate_arr3[2]+"-"+strdate_arr3[1]+"-"+strdate_arr3[0];

                                    friendCodeUser.setDate(start_date);
                                    friendCodeUser.setMobile_no(jsonObject.getString("phone_no"));
                                    friendCodeList.add(friendCodeUser);
                                }

                                progressbar.setVisibility(View.GONE);
                                msgView.setVisibility(View.GONE);
                                swipeLayout.setVisibility(View.VISIBLE);

                                FriendListReferAdapter friendListReferAdapter = new FriendListReferAdapter(FriendCodeList.this, friendCodeList);
                                recyclerView.setAdapter(friendListReferAdapter);
                            }
                            else
                            {
                                msgView.setVisibility(View.VISIBLE);
                                swipeLayout.setVisibility(View.VISIBLE);

                            }

                            progressbar.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        msgView.setVisibility(View.VISIBLE);
                        swipeLayout.setVisibility(View.GONE);
                        progressbar.setVisibility(View.GONE);
                    }
                }
            }
        }, activity, Constant.BASEPATH+Constant.GET_FRIEND_URL+session.getData(Session.KEY_id), params, true);
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