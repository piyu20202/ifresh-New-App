package com.ifresh.customer.activity;

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

import com.ifresh.customer.R;
import com.ifresh.customer.adapter.WalletBalanceAdapter;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.StorePrefrence;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.model.WalletBalance;

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
        progressbar = findViewById(R.id.progressBar);

        tvAlert = findViewById(R.id.tvAlert);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(WalletBalanceList.this));
        msgView = findViewById(R.id.msgView);

        swipeLayout.setRefreshing(false);
        swipeLayout.setEnabled(false);



        getWalletBalanceData(WalletBalanceList.this);


        /*swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
        });*/
    }

    public void getWalletBalanceData(final Activity activity) {
        progressbar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        //System.out.println("===n response " + response);
                        walletBalances_list = new ArrayList<>();
                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                        if(jsonArray.length() > 0)
                        {
                            msgView.setVisibility(View.GONE);
                            //swipeLayout.setVisibility(View.VISIBLE);

                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                WalletBalance walletBalance = new WalletBalance();

                                walletBalance.setAmount(jsonObject.getString("wallet_amount"));
                                walletBalance.setMessage(jsonObject.getString("description"));
                                walletBalance.setWallet_status(jsonObject.getInt("is_active"));

                                if(jsonObject.getString("transaction").equalsIgnoreCase("1"))
                                {
                                    walletBalance.setActype("credit");
                                }
                                else if(jsonObject.getString("transaction").equalsIgnoreCase("2"))
                                {
                                    walletBalance.setActype("debit");
                                }


                                String str_date_1 = jsonObject.getString("created");
                                String[] strdate_arr_2 = str_date_1.split("T");
                                String[] strdate_arr3 = strdate_arr_2[0].split("-");
                                String start_date = strdate_arr3[2]+"-"+strdate_arr3[1]+"-"+strdate_arr3[0];
                                walletBalance.setDate(start_date);

                                String str_date_4 = jsonObject.getString("expire_on");
                                String[] strdate_arr_5 = str_date_4.split("T");
                                String[] strdate_arr6 = strdate_arr_5[0].split("-");
                                String end_date = strdate_arr6[2]+"-"+strdate_arr6[1]+"-"+strdate_arr6[0];
                                walletBalance.setExpdate(end_date);

                                walletBalances_list.add(walletBalance);

                            }

                            progressbar.setVisibility(View.GONE);
                            msgView.setVisibility(View.GONE);
                            swipeLayout.setVisibility(View.VISIBLE);

                            WalletBalanceAdapter walletBalanceAdapter = new WalletBalanceAdapter(WalletBalanceList.this, walletBalances_list);
                            recyclerView.setAdapter(walletBalanceAdapter);
                        }
                        else
                        {
                            msgView.setVisibility(View.VISIBLE);
                            swipeLayout.setVisibility(View.GONE);
                        }
                        progressbar.setVisibility(View.GONE);
                    } catch (Exception e) {

                        msgView.setVisibility(View.VISIBLE);
                        swipeLayout.setVisibility(View.GONE);
                        progressbar.setVisibility(View.GONE);

                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BASEPATH+Constant.GET_WALLET_BAL_URL+session.getData(Session.KEY_id), params, true);
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