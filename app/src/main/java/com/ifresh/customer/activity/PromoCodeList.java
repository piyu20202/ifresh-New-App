package com.ifresh.customer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ifresh.customer.R;
import com.ifresh.customer.adapter.PromoCode;
import com.ifresh.customer.adapter.PromoCodeAdapter;
import com.ifresh.customer.adapter.WalletBalanceAdapter;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.StorePrefrence;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.model.WalletBalance;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ifresh.customer.helper.Constant.BASEPATH;
import static com.ifresh.customer.helper.Constant.GETFRENCHISE;

public class PromoCodeList extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressbar;
    Toolbar toolbar;
    TextView tvAlert;
    Session session;
    LinearLayout msgView;
    String franchiseId="";
    ArrayList<PromoCode> promoCodeArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_code_list);
        session = new Session(PromoCodeList.this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.promocode));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressbar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(PromoCodeList.this));
        msgView = findViewById(R.id.msgView);

        GetFrenchise_id(session.getData(Constant.AREA_ID));
        //local_function(getAssetJsonData(PromoCodeList.this));


    }

    private void getPromoCodeList(final Activity activity, final String franchiseId) {
        progressbar.setVisibility(View.VISIBLE);
        Log.d("franchiseId",franchiseId);
        Log.d("url==>",Constant.BASEPATH + Constant.GET_PROMOCODE + franchiseId);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result)
                {
                    try {
                        System.out.println("===n response " + response);
                        promoCodeArrayList = new ArrayList<>();
                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                        if(jsonArray.length() > 0)
                        {
                            msgView.setVisibility(View.GONE);

                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                PromoCode promoCode = new PromoCode();
                                promoCode.setC_id(jsonObject.getString("_id"));
                                promoCode.setC_title(jsonObject.getString("title"));
                                promoCode.setC_url(jsonObject.getString("coupon"));
                                promoCode.setC_useno(jsonObject.getInt("uses_number"));
                                promoCode.setC_disc_in(jsonObject.getInt("disc_in"));
                                promoCode.setC_disc_value(jsonObject.getInt("disc_value"));
                                promoCode.setC_has_expiry(jsonObject.getBoolean("has_expiry"));
                                promoCode.setC_is_active(jsonObject.getString("is_active"));
                                promoCode.setC_reuse(jsonObject.getBoolean("reuse_by_same_user"));


                                String str_date_1 = jsonObject.getString("start_date");
                                String[] strdate_arr_2 = str_date_1.split("T");
                                String[] strdate_arr3 = strdate_arr_2[0].split("-");
                                String start_date = strdate_arr3[2]+"-"+strdate_arr3[1]+"-"+strdate_arr3[0];
                                promoCode.setStart_date(start_date);


                                String str_date_4 = jsonObject.getString("end_date");
                                String[] strdate_arr_5 = str_date_4.split("T");
                                String[] strdate_arr6 = strdate_arr_5[0].split("-");
                                String end_date = strdate_arr6[2]+"-"+strdate_arr6[1]+"-"+strdate_arr6[0];
                                promoCode.setEnd_date(end_date);

                                promoCodeArrayList.add(promoCode);
                            }

                            progressbar.setVisibility(View.GONE);
                            msgView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            PromoCodeAdapter promoCodeAdapter = new PromoCodeAdapter(PromoCodeList.this,promoCodeArrayList);
                            recyclerView.setAdapter(promoCodeAdapter);

                        }
                        else
                        {
                            progressbar.setVisibility(View.GONE);
                            msgView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        progressbar.setVisibility(View.GONE);
                    }catch (Exception e)
                        {
                        msgView.setVisibility(View.VISIBLE);
                        progressbar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_FRENCHCOUPON + franchiseId, params, true);



    }

    private String GetFrenchise_id(String Area_ID)
    {
        String FrenchiseUrl = BASEPATH + GETFRENCHISE + Area_ID;
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("frenchise==>" + response);
                if (result)
                {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getInt(Constant.SUCESS) == 200)
                        {
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            if(jsonArray.length() > 0)
                            {
                                JSONObject jsonObject =  jsonArray.getJSONObject(0);
                                franchiseId=jsonObject.getString("franchiseId");

                                getPromoCodeList(PromoCodeList.this,franchiseId);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }, PromoCodeList.this, FrenchiseUrl, params, true);
        return franchiseId;

    }

    public  String getAssetJsonData(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("local.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        Log.e("data", json);
        return json;

    }

    public void local_function(String response)
    {
        try {
            System.out.println("===n response " + response);
            promoCodeArrayList = new ArrayList<>();
            JSONObject object = new JSONObject(response);
            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

            if(jsonArray.length() > 0)
            {


                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    PromoCode promoCode = new PromoCode();
                    promoCode.setC_id(jsonObject.getString("_id"));
                    promoCode.setC_title(jsonObject.getString("title"));
                    promoCode.setC_url(jsonObject.getString("coupon"));
                    promoCode.setC_useno(jsonObject.getInt("uses_number"));
                    promoCode.setC_disc_in(jsonObject.getInt("disc_in"));
                    promoCode.setC_disc_value(jsonObject.getInt("disc_value"));
                    promoCode.setC_has_expiry(jsonObject.getBoolean("has_expiry"));
                    promoCode.setC_is_active(jsonObject.getString("is_active"));
                    promoCode.setC_reuse(jsonObject.getBoolean("reuse_by_same_user"));


                    String str_date_1 = jsonObject.getString("start_date");
                    String[] strdate_arr_2 = str_date_1.split("T");
                    String[] strdate_arr3 = strdate_arr_2[0].split("-");
                    String start_date = strdate_arr3[2]+"-"+strdate_arr3[1]+"-"+strdate_arr3[0];
                    promoCode.setStart_date(start_date);


                    String str_date_4 = jsonObject.getString("end_date");
                    String[] strdate_arr_5 = str_date_4.split("T");
                    String[] strdate_arr6 = strdate_arr_5[0].split("-");
                    String end_date = strdate_arr6[2]+"-"+strdate_arr6[1]+"-"+strdate_arr6[0];
                    promoCode.setEnd_date(end_date);

                    promoCodeArrayList.add(promoCode);
                }

                progressbar.setVisibility(View.GONE);
                msgView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                PromoCodeAdapter promoCodeAdapter = new PromoCodeAdapter(PromoCodeList.this,promoCodeArrayList);
                recyclerView.setAdapter(promoCodeAdapter);

            }
            else
            {
                msgView.setVisibility(View.VISIBLE);
            }
            progressbar.setVisibility(View.GONE);
        }catch (Exception e)
        {
            msgView.setVisibility(View.VISIBLE);
            progressbar.setVisibility(View.GONE);
            e.printStackTrace();
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