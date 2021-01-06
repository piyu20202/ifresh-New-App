package com.ifresh.customer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ifresh.customer.R;
import com.ifresh.customer.fragment.OrderTrackerListFragment;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.model.Mesurrment;
import com.ifresh.customer.model.OrderTracker_2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.ifresh.customer.helper.Constant.BASEPATH;
import static com.ifresh.customer.helper.Constant.GET_TRACKORDER;

import static com.ifresh.customer.helper.Session.KEY_FIRSTNAME;
import static com.ifresh.customer.helper.Session.KEY_LASTNAME;

public class OrderListActivity_2 extends AppCompatActivity {
    LinearLayout lytempty, lytdata;
    public static ArrayList<OrderTracker_2> orderTrackerslist, cancelledlist, deliveredlist, processedlist, shippedlist, returnedList;
    Session session;
    String[] tabs;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    Toolbar toolbar;
    Context ctx = OrderListActivity_2.this;
    JSONArray jsonArray = new JSONArray();
    ProgressBar progressBar;
    Button btnorder;
    ArrayList<Mesurrment> measurement_list;
    Activity activity = OrderListActivity_2.this ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.order_track));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(getApplicationContext());
        tabs = new String[]{getString(R.string.all), getString(R.string.in_process1), getString(R.string.shipped1), getString(R.string.delivered1), getString(R.string.cancelled1), getString(R.string.returned1)};
        lytempty = findViewById(R.id.lytempty);
        progressBar = findViewById(R.id.progressBar);
        lytdata = findViewById(R.id.lytdata);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(5);
        tabLayout = findViewById(R.id.tablayout);
        btnorder = findViewById(R.id.btnorder);


        btnorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(OrderListActivity_2.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //makejsonArr(getAssetJsonData(ctx));
        callSettingApi_messurment();

    }

    private void callSettingApi_messurment()
    {
        try{
            String str_measurment = session.getData(Constant.KEY_MEASUREMENT);
            if(str_measurment.length() == 0)
            {
                ApiConfig.GetSettingConfigApi(activity, session);// to call measurement data
            }

            JSONArray jsonArray = new JSONArray(str_measurment);
            measurement_list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject object1 = jsonArray.getJSONObject(i);
                measurement_list.add(new Mesurrment(object1.getString("id"), object1.getString("title"), object1.getString("abv")));
            }

            Call_ordertracker_api();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }




    private void makejsonArr(String res)
    {
        Log.d("res", res);
        try{
            JSONArray data_arr = new JSONArray(res);
            for(int i = 0; i<data_arr.length(); i++)
            {
                JSONObject jsonobj = new JSONObject();



                jsonobj.put("id", data_arr.getJSONObject(i).getString("_id"));
                jsonobj.put("show_id", data_arr.getJSONObject(i).getString("orderUserId"));

                jsonobj.put("user_id", data_arr.getJSONObject(i).getString("userId"));
                jsonobj.put(Constant.USER_NAME, session.getString(KEY_FIRSTNAME)+" "+session.getString(KEY_LASTNAME));

                if(data_arr.getJSONObject(i).has("order_type"))
                {
                    jsonobj.put("order_type", data_arr.getJSONObject(i).getString("order_type"));
                }
                else{
                    jsonobj.put("order_type", "1");
                }


                if(data_arr.getJSONObject(i).has("delivery_boy_id"))
                {
                    jsonobj.put("delivery_boy_id", data_arr.getJSONObject(i).getString("delivery_boy_id"));
                }
                else{
                    jsonobj.put("delivery_boy_id", "0");
                }
                jsonobj.put("mobile", data_arr.getJSONObject(i).getString("phone_no"));
                jsonobj.put("final_total", data_arr.getJSONObject(i).getString("final_total"));

                if(data_arr.getJSONObject(i).has("total"))
                {
                    jsonobj.put("total", data_arr.getJSONObject(i).getString("total"));
                }
                else{
                    jsonobj.put("total", data_arr.getJSONObject(i).getString("final_total"));
                }
                jsonobj.put("delivery_charge", data_arr.getJSONObject(i).getString("delivery_charge"));
                jsonobj.put("tax_amount", data_arr.getJSONObject(i).getString("tax_amount"));
                jsonobj.put("tax_percent", data_arr.getJSONObject(i).getString("tax_percent"));
                jsonobj.put("key_wallet_balance", data_arr.getJSONObject(i).getString("key_wallet_balance"));

                if(data_arr.getJSONObject(i).has("discount"))
                {
                    jsonobj.put("discount", data_arr.getJSONObject(i).getString("discount"));
                }
                else{
                    jsonobj.put("discount", "0.0");
                }

                if(data_arr.getJSONObject(i).has("promo_code"))
                {
                    jsonobj.put("promo_code", data_arr.getJSONObject(i).getString("promo_code"));
                }
                else{
                    jsonobj.put("promo_code", "");
                }

                if(data_arr.getJSONObject(i).has("promo_discount"))
                {
                    jsonobj.put("promo_discount", data_arr.getJSONObject(i).getString("promo_discount"));
                }
                else{
                    jsonobj.put("promo_discount", "");
                }

                if(data_arr.getJSONObject(i).has("latitude"))
                {
                    jsonobj.put("latitude", data_arr.getJSONObject(i).getString("latitude"));
                }
                else{
                    jsonobj.put("latitude", "0.0");
                }

                if(data_arr.getJSONObject(i).has("longitude"))
                {
                    jsonobj.put("longitude", data_arr.getJSONObject(i).getString("longitude"));
                }
                else{
                    jsonobj.put("longitude", "0.0");
                }

                String payment_method="";
                if(data_arr.getJSONObject(i).getString("payment_method").equalsIgnoreCase("1"))
                {
                    payment_method = "cod";
                }
                else if(data_arr.getJSONObject(i).getString("payment_method").equalsIgnoreCase("2"))
                {
                    payment_method = "PayU";
                }
                else if(data_arr.getJSONObject(i).getString("payment_method").equalsIgnoreCase("3"))
                {
                    payment_method = "PayPal";
                }
                else if(data_arr.getJSONObject(i).getString("payment_method").equalsIgnoreCase("4"))
                {
                    payment_method = "RazorPay";
                }
                else if(data_arr.getJSONObject(i).getString("payment_method").equalsIgnoreCase("5"))
                {
                    payment_method = "wallet";
                }

                jsonobj.put("payment_method", payment_method);

                jsonobj.put("address", data_arr.getJSONObject(i).getString("delivery_address"));

                if(data_arr.getJSONObject(i).has("delivery_time"))
                {
                    jsonobj.put("delivery_time", data_arr.getJSONObject(i).getString("delivery_time"));
                }
                else{
                    jsonobj.put("delivery_time", "0.0");
                }

                if(data_arr.getJSONObject(i).has("discount_rupee"))
                {
                    jsonobj.put("discount_rupees", data_arr.getJSONObject(i).getString("discount_rupee"));
                }
                else{
                    jsonobj.put("discount_rupees", "0.0");
                }

                String str_date = data_arr.getJSONObject(i).getString("created");
                String[] strdate_arr = str_date.split("T");
                String[] strdate_arr2 = strdate_arr[0].split("-");
                String final_date = strdate_arr2[2]+"-"+strdate_arr2[1]+"-"+strdate_arr2[0];
                jsonobj.put("order_palce_date", final_date);

                JSONArray order_variants_arr = data_arr.getJSONObject(i).getJSONArray("order_variants");

                JSONArray array_combined = new JSONArray();
                for(int j = 0; j<order_variants_arr.length(); j++)
                {
                    JSONObject mjson_obj_item = new JSONObject();
                    mjson_obj_item.put("item_id", order_variants_arr.getJSONObject(j).getString("_id"));
                    mjson_obj_item.put("order_id", order_variants_arr.getJSONObject(j).getString("orderId"));

                    if(order_variants_arr.getJSONObject(j).getString("productId").equalsIgnoreCase("null"))
                    {
                        mjson_obj_item.put("productId", "");
                    }
                    else{
                        mjson_obj_item.put("productId", order_variants_arr.getJSONObject(j).getString("productId"));
                    }

                    if(order_variants_arr.getJSONObject(j).getString("frproductvarId").equalsIgnoreCase("null"))
                    {
                        mjson_obj_item.put("frproductvarId", "");
                    }
                    else{
                        mjson_obj_item.put("frproductvarId", order_variants_arr.getJSONObject(j).getString("frproductvarId"));
                    }
                    if(order_variants_arr.getJSONObject(j).getString("franchiseId").equalsIgnoreCase("null"))
                    {
                        mjson_obj_item.put("franchiseId", "");
                    }
                    else{
                        mjson_obj_item.put("franchiseId", order_variants_arr.getJSONObject(j).getString("franchiseId"));
                    }

                    if(order_variants_arr.getJSONObject(j).getString("frproductId").equalsIgnoreCase("null"))
                    {
                        mjson_obj_item.put("frproductId", "");
                    }
                    else{
                        mjson_obj_item.put("frproductId", order_variants_arr.getJSONObject(j).getString("frproductId"));
                    }

                    //mjson_obj_item.put("frproductId", order_variants_arr.getJSONObject(j).getString("frproductId"));

                    mjson_obj_item.put("price", order_variants_arr.getJSONObject(j).getString("price"));
                    mjson_obj_item.put("discount", "0");
                    mjson_obj_item.put("delivery_by", "0");
                    mjson_obj_item.put("qty", order_variants_arr.getJSONObject(j).getString("qty"));

                    if(order_variants_arr.getJSONObject(j).has("title"))
                    {
                        if(order_variants_arr.getJSONObject(j).getString("title").equalsIgnoreCase("null"))
                        {
                            mjson_obj_item.put("title", "Medical Prescription");
                        }
                        else{
                            mjson_obj_item.put("title", order_variants_arr.getJSONObject(j).getString("title"));
                        }
                    }
                    else{
                        mjson_obj_item.put("title", "");
                    }

                    if(data_arr.getJSONObject(i).has("order_type"))
                    {
                        if(data_arr.getJSONObject(i).getString("order_type").equalsIgnoreCase("2"))
                        {
                            //image type is medicine
                            if(order_variants_arr.getJSONObject(j).has("image_url"))
                            {
                                Log.d("image url", Constant.UPLOAD_IMAGE_SHOW + order_variants_arr.getJSONObject(j).getString("image_url"));
                                mjson_obj_item.put("image_url", Constant.UPLOAD_IMAGE_SHOW + order_variants_arr.getJSONObject(j).getString("image_url"));
                            }
                            else{
                                mjson_obj_item.put("image_url", "");
                            }
                        }
                        else {
                            //image type is product
                            if(order_variants_arr.getJSONObject(j).has("image_url"))
                            {
                                mjson_obj_item.put("image_url", Constant.PRODUCTIMAGEPATH + order_variants_arr.getJSONObject(j).getString("image_url"));
                            }
                            else{
                                mjson_obj_item.put("image_url", "");
                            }
                        }


                    }
                    else{
                        //order type not exist flow as normal image type is product
                        if(order_variants_arr.getJSONObject(j).has("image_url"))
                        {
                            mjson_obj_item.put("image_url", Constant.PRODUCTIMAGEPATH + order_variants_arr.getJSONObject(j).getString("image_url"));
                        }
                        else{
                            mjson_obj_item.put("image_url", "");
                        }
                    }


                    if(data_arr.getJSONObject(i).has("order_type")) {

                        if (data_arr.getJSONObject(i).getString("order_type").equalsIgnoreCase("2")) {
                            //medicine product
                            mjson_obj_item.put("unit", "1");
                            mjson_obj_item.put("measurement", "pcs");
                        }

                        else if(data_arr.getJSONObject(i).getString("order_type").equalsIgnoreCase("1"))
                        {
                            if(order_variants_arr.getJSONObject(j).has("unit"))
                            {
                                mjson_obj_item.put("unit", order_variants_arr.getJSONObject(j).getString("measurement"));
                            }
                            else{
                                mjson_obj_item.put("unit", "");
                            }


                            String measurement="";
                            if(order_variants_arr.getJSONObject(j).has("unit"))
                            {
                                for(int p = 0; p < measurement_list.size(); p++)
                                {
                                    //Log.d("val==>", ""+ p);
                                    Mesurrment mesurrment1 = measurement_list.get(p);
                                    Log.d("measurment1=>",mesurrment1.getId());
                                    Log.d("unit=>",order_variants_arr.getJSONObject(j).getString("unit"));

                                    if(mesurrment1.getId().equalsIgnoreCase( order_variants_arr.getJSONObject(j).getString("unit") ))
                                    {
                                        measurement = mesurrment1.getAbv().toLowerCase();
                                        break;
                                    }
                                }

                                Log.d("measurement=>",measurement);
                                mjson_obj_item.put("measurement", measurement);

                            }


                        }
                    }





                    /*if(order_variants_arr.getJSONObject(j).has("unit"))
                    {
                        if(order_variants_arr.getJSONObject(j).getString("unit").equalsIgnoreCase("1"))
                        {
                            measurement="kg";
                        }
                       else if(order_variants_arr.getJSONObject(j).getString("unit").equalsIgnoreCase("2"))
                        {
                            measurement="gm";
                        }
                        else if(order_variants_arr.getJSONObject(j).getString("unit").equalsIgnoreCase("3"))
                        {
                            measurement="ltr";
                        }
                        else if(order_variants_arr.getJSONObject(j).getString("unit").equalsIgnoreCase("4"))
                        {
                            measurement="ml";
                        }
                        else if(order_variants_arr.getJSONObject(j).getString("unit").equalsIgnoreCase("5"))
                        {
                            measurement="pack";
                        }
                        else if(order_variants_arr.getJSONObject(j).getString("unit").equalsIgnoreCase("6"))
                        {
                            measurement="pcs";
                        }
                        else if(order_variants_arr.getJSONObject(j).getString("unit").equalsIgnoreCase("7"))
                        {
                            measurement="m";
                        }

                        mjson_obj_item.put("measurement", measurement);

                    }
                    else{
                        mjson_obj_item.put("measurement", "");
                    }*/



                    int length_arr = data_arr.getJSONObject(i).getJSONArray("status").length();
                    JSONArray status_arr_val = data_arr.getJSONObject(i).getJSONArray("status");
                    String status="";
                    if(status_arr_val.getJSONObject(length_arr-1).getString("order_status").equalsIgnoreCase("1"))
                    {
                        status = "received";
                    }
                    else if(status_arr_val.getJSONObject(length_arr-1).getString("order_status").equalsIgnoreCase("2"))
                    {
                        status = "processed";
                    }
                    else if(status_arr_val.getJSONObject(length_arr-1).getString("order_status").equalsIgnoreCase("3"))
                    {
                        status = "shipped";
                    }
                    else if(status_arr_val.getJSONObject(length_arr-1).getString("order_status").equalsIgnoreCase("4"))
                    {
                        status = "delivered";
                    }
                    else if(status_arr_val.getJSONObject(length_arr-1).getString("order_status").equalsIgnoreCase("5"))
                    {
                        status = "returned";
                    }
                    else if(status_arr_val.getJSONObject(length_arr-1).getString("order_status").equalsIgnoreCase("6"))
                    {
                        status = "cancelled";
                    }
                    mjson_obj_item.put("active_status", status);



                    String str_date_1 = order_variants_arr.getJSONObject(j).getString("created");
                    String[] strdate_arr_2 = str_date_1.split("T");
                    String[] strdate_arr3 = strdate_arr_2[0].split("-");
                    String product_date = strdate_arr3[2]+"-"+strdate_arr3[1]+"-"+strdate_arr3[0];
                    mjson_obj_item.put("product_add_date", product_date);
                    array_combined.put(j, mjson_obj_item);

                    jsonobj.put("items", array_combined);
                }
                JSONArray status_arr = data_arr.getJSONObject(i).getJSONArray("status");
                JSONArray newStatusArr = new JSONArray();
                for(int k = 0; k<status_arr.length(); k++)
                {
                    JSONObject mjson_obj_status = new JSONObject();
                    String status="";
                    if(status_arr.getJSONObject(k).getString("order_status").equalsIgnoreCase("1"))
                    {
                        status = "received";
                    }
                    else if(status_arr.getJSONObject(k).getString("order_status").equalsIgnoreCase("2"))
                    {
                        status = "processed";
                    }
                    else if(status_arr.getJSONObject(k).getString("order_status").equalsIgnoreCase("3"))
                    {
                        status = "shipped";
                    }
                    else if(status_arr.getJSONObject(k).getString("order_status").equalsIgnoreCase("4"))
                    {
                        status = "delivered";
                    }
                    else if(status_arr.getJSONObject(k).getString("order_status").equalsIgnoreCase("5"))
                    {
                        status = "returned";
                    }
                    else if(status_arr.getJSONObject(k).getString("order_status").equalsIgnoreCase("6"))
                    {
                        status = "cancelled";
                    }

                    mjson_obj_status.put("order_status", status);

                    String str_status_date = status_arr.getJSONObject(k).getString("status_date");
                    String status_date_arr[] = str_status_date.split("T");

                    String[] status_date_arr2 = status_date_arr[0].split("-");
                    String final_status_date = status_date_arr2[2]+"-"+status_date_arr2[1]+"-"+status_date_arr2[0];

                    mjson_obj_status.put("status_date", final_status_date);

                    newStatusArr.put(k, mjson_obj_status);
                    jsonobj.put("status", newStatusArr);
                }


                    jsonArray.put(i, jsonobj);
            }

            System.out.println(jsonArray.toString());
            Log.d("val==>", jsonArray.toString());

            if(jsonArray.length() > 0)
            {
                lytdata.setVisibility(View.VISIBLE);
                GetOrderDetails(jsonArray);
            }
            else{
                lytdata.setVisibility(View.GONE);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public  String getAssetJsonData(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("local3.json");
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

    private void GetOrderDetails(JSONArray jsonArray) {

        Log.d("json_array", jsonArray.toString());
        orderTrackerslist = new ArrayList<>();
        cancelledlist = new ArrayList<>();
        deliveredlist = new ArrayList<>();
        processedlist = new ArrayList<>();
        shippedlist = new ArrayList<>();
        returnedList = new ArrayList<>();

        try {
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String laststatusname = null, laststatusdate = null;

                    JSONArray statusarray = jsonObject.getJSONArray("status");
                    ArrayList<OrderTracker_2> statusarraylist = new ArrayList<>();
                    int cancel = 0, delivered = 0, process = 0, shipped = 0, returned = 0;

                    for (int k = 0; k < statusarray.length(); k++)
                    {
                        JSONObject sobj = statusarray.getJSONObject(k);
                        String sname = sobj.getString("order_status");
                        String sdate = sobj.getString("status_date");;

                        statusarraylist.add(new OrderTracker_2(sname, sdate));
                        laststatusname = sname;
                        laststatusdate = sdate;

                        if (sname.equalsIgnoreCase("cancelled")) {
                            cancel = 1;
                            delivered = 0;
                            process = 0;
                            shipped = 0;
                            returned = 0;
                        } else if (sname.equalsIgnoreCase("delivered")) {
                            delivered = 1;
                            process = 0;
                            shipped = 0;
                            returned = 0;
                        } else if (sname.equalsIgnoreCase("processed")) {
                            process = 1;
                            shipped = 0;
                            returned = 0;
                        } else if (sname.equalsIgnoreCase("shipped")) {
                            shipped = 1;
                            returned = 0;
                        } else if (sname.equalsIgnoreCase("returned")) {
                            returned = 1;
                        }
                    }


                    JSONArray statusarray1 = jsonObject.getJSONArray("status");
                    ArrayList<OrderTracker_2> statusList = new ArrayList<>();
                        for (int k = 0; k < statusarray1.length(); k++)
                        {
                            JSONObject sobj = statusarray1.getJSONObject(k);
                            String sname = sobj.getString("order_status");
                            String sdate = sobj.getString("status_date");
                            statusList.add(new OrderTracker_2(sname, sdate));
                        }


                    ArrayList<OrderTracker_2> itemList = new ArrayList<>();
                    JSONArray itemsarray = jsonObject.getJSONArray("items");

                    for (int j = 0; j < itemsarray.length(); j++) {

                        JSONObject itemobj = itemsarray.getJSONObject(j);
                        double productPrice = 0.0;
                        productPrice = (Double.parseDouble(itemobj.getString(Constant.PRICE)) * Integer.parseInt(itemobj.getString("qty")));

                        /*if (itemobj.getString(Constant.DISCOUNTED_PRICE).equals("0"))
                            productPrice = (Double.parseDouble(itemobj.getString(Constant.PRICE)) * Integer.parseInt(itemobj.getString(Constant.QUANTITY)));
                        else {
                            productPrice = (Double.parseDouble(itemobj.getString(Constant.DISCOUNTED_PRICE)) * Integer.parseInt(itemobj.getString(Constant.QUANTITY)));
                        }*/


                       itemList.add(new OrderTracker_2(
                                itemobj.getString("item_id"),
                                itemobj.getString("order_id"),
                                itemobj.getString("productId"),
                                itemobj.getString("qty"),
                                String.valueOf(productPrice),
                                itemobj.getString("discount"),
                                jsonObject.getString("order_type"),
                                String.valueOf(productPrice),
                                itemobj.getString("delivery_by"),
                                itemobj.getString("title"),
                                itemobj.getString("image_url"),

                                itemobj.getString("measurement"),

                                itemobj.getString("unit"),
                                jsonObject.getString("payment_method"),
                                itemobj.getString("active_status"),
                                itemobj.getString("product_add_date"), statusList));
                    }


                    Log.d("discount==>",jsonObject.getString("discount_rupees"));


                    OrderTracker_2 orderTracker = new OrderTracker_2(
                            jsonObject.getString("show_id") ,
                            jsonObject.getString("user_id"),
                            jsonObject.getString("id"),
                            jsonObject.getString("order_palce_date"),
                            laststatusname, laststatusdate,
                            statusarraylist,
                            jsonObject.getString("mobile"),
                            jsonObject.getString("delivery_charge"),
                            jsonObject.getString("payment_method"),
                            jsonObject.getString("address"),
                            jsonObject.getString("total"),
                            jsonObject.getString("final_total"),
                            jsonObject.getString("tax_amount"),
                            jsonObject.getString("tax_percent"),
                            jsonObject.getString("key_wallet_balance"),
                            jsonObject.getString("promo_code"),
                            jsonObject.getString("promo_discount"),
                            jsonObject.getString("discount"),
                            jsonObject.getString("order_type"),
                            jsonObject.getString("discount_rupees"),
                            jsonObject.getString(Constant.USER_NAME),
                            itemList);

                    orderTrackerslist.add(orderTracker);

                    Log.d("list", orderTrackerslist.toString());

                    if (cancel == 1)
                        cancelledlist.add(orderTracker);
                    if (delivered == 1)
                        deliveredlist.add(orderTracker);
                    if (process == 1)
                        processedlist.add(orderTracker);
                    if (shipped == 1)
                        shippedlist.add(orderTracker);
                    if (returned == 1)
                        returnedList.add(orderTracker);
                }
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupViewPager(ViewPager viewPager)
    {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new OrderTrackerListFragment(), tabs[0]);
        adapter.addFrag(new OrderTrackerListFragment(), tabs[1]);
        adapter.addFrag(new OrderTrackerListFragment(), tabs[2]);
        adapter.addFrag(new OrderTrackerListFragment(), tabs[3]);
        adapter.addFrag(new OrderTrackerListFragment(), tabs[4]);
        adapter.addFrag(new OrderTrackerListFragment(), tabs[5]);
        viewPager.setAdapter(adapter);
    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle data = new Bundle();
            OrderTrackerListFragment fragment = new OrderTrackerListFragment();
            data.putInt("pos", position);
            fragment.setArguments(data);
            return fragment;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.isOrderCancelled)
        {
            Call_ordertracker_api();
            Constant.isOrderCancelled = false;
        }
    }


    public void OnBtnClick(View view) {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void Call_ordertracker_api() {
        lytdata.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        String order_tracker_url = BASEPATH + GET_TRACKORDER ;
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", session.getData(Session.KEY_id));
        ApiConfig.RequestToVolley_POST(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("======order" + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getInt(Constant.SUCESS) == 200)
                        {
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            if(jsonArray.length() > 0)
                            {
                                //make json array
                                lytdata.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                lytempty.setVisibility(View.GONE);
                                makejsonArr(jsonArray.toString());
                            }
                            else{
                                // no data in array
                                lytempty.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                lytdata.setVisibility(View.GONE);
                            }

                        } else {

                              lytempty.setVisibility(View.VISIBLE);
                              progressBar.setVisibility(View.GONE);
                              lytdata.setVisibility(View.GONE);


                        }

                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        lytempty.setVisibility(View.VISIBLE);
                        lytdata.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, OrderListActivity_2.this, order_tracker_url, params, true);

    }


    public static JSONArray RemoveJSONArray( JSONArray jarray,int pos) {

        JSONArray Njarray=new JSONArray();
        try{
            for(int i=0;i<jarray.length();i++){
                if(i!=pos)
                    Njarray.put(jarray.get(i));
            }
        }catch (Exception e){e.printStackTrace();}
        return Njarray;

    }

}
