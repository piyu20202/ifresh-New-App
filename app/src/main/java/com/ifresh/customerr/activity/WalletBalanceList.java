package com.ifresh.customerr.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ifresh.customerr.R;
import com.ifresh.customerr.adapter.WalletBalanceAdapter;
import com.ifresh.customerr.helper.ApiConfig;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.helper.Session;
import com.ifresh.customerr.helper.StorePrefrence;
import com.ifresh.customerr.helper.VolleyCallback;
import com.ifresh.customerr.model.WalletBalance;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WalletBalanceList extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<WalletBalance> walletBalances_list;
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
        setContentView(R.layout.activity_wallet_balance);
        storeinfo = new StorePrefrence(WalletBalanceList.this);
        session = new Session(WalletBalanceList.this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.walletbalance));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeLayout = findViewById(R.id.swipeLayout);
        progressbar = findViewById(R.id.progressbar);
        tvAlert = findViewById(R.id.tvAlert);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(WalletBalanceList.this));
        msgView = findViewById(R.id.msgView);
        //call_api_2(storeinfo.getString("mobile"), storeinfo.getString("user_id"));
        getWalletBalanceData(WalletBalanceList.this);


        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWalletBalanceData(WalletBalanceList.this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    public void getWalletBalanceData(final Activity activity) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_WALLETBALANCE, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("===n response " + response);
                        walletBalances_list = new ArrayList<>();
                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                        if(jsonArray.length() > 0)
                        {
                            msgView.setVisibility(View.GONE);
                            swipeLayout.setVisibility(View.VISIBLE);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                WalletBalance walletBalance = new WalletBalance();
                                walletBalance.setAmount(jsonObject.getString(Constant.AMOUNT_w));
                                walletBalance.setMessage(jsonObject.getString(Constant.MESSAGE_w));
                                walletBalance.setWallet_status(jsonObject.getInt(Constant.SUCESS));

                                String date = truncate(jsonObject.getString(Constant.DATE_w), 10);
                                String[] parts = date.split("-");
                                String yyyy = parts[0];
                                String mm = parts[1];
                                String dd = parts[2];
                                String date_str = dd + "-" + mm  + "-" + yyyy;
                                walletBalance.setDate(date_str);

                                String exp_date = truncate(jsonObject.getString(Constant.DATE_e), 10);
                                String[] parts_ed = exp_date.split("-");
                                String yyyy_ed = parts_ed[0];
                                String mm_ed = parts_ed[1];
                                String dd_ed = parts_ed[2];
                                String date_str_ed = dd_ed + "-" + mm_ed  + "-" + yyyy_ed;
                                walletBalance.setExpdate(date_str_ed);


                                walletBalance.setActype(jsonObject.getString(Constant.AC_w));
                                walletBalances_list.add(walletBalance);

                            }
                            WalletBalanceAdapter walletBalanceAdapter = new WalletBalanceAdapter(WalletBalanceList.this, walletBalances_list);
                            recyclerView.setAdapter(walletBalanceAdapter);
                            progressbar.setVisibility(View.GONE);
                        }
                        else
                        {
                            msgView.setVisibility(View.VISIBLE);
                            swipeLayout.setVisibility(View.GONE);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.GET_WALLET_BAL_URL, params, true);
    }



    public void getWalletBalance()
    {
        //System.out.println("===n id" + session.getData(Session.KEY_ID));
        Log.d("WalletBalance==>", session.getData(Session.KEY_ID));
        WalletBalanceAdapter walletBalanceAdapter = new WalletBalanceAdapter(WalletBalanceList.this);
        recyclerView.setAdapter(walletBalanceAdapter);
        //progressbar.setVisibility(View.GONE);
    }

    public static String truncate(String value, int length) {
        // Ensure String length is longer than requested size.
        if (value.length() > length) {
            return value.substring(0, length);
        } else {
            return value;
        }
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