package com.ifresh.customerr.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ifresh.customerr.R;
import com.ifresh.customerr.adapter.CartListAdapter;
import com.ifresh.customerr.helper.ApiConfig;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.helper.DatabaseHelper;
import com.ifresh.customerr.helper.Session;
import com.ifresh.customerr.helper.VolleyCallback;
import com.ifresh.customerr.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {

    public static LinearLayout lytempty;
    static TextView txttotal, txtstotal, txtdeliverycharge, txtsubtotal, txt_msg_view;
    RecyclerView cartrecycleview;
    static DatabaseHelper databaseHelper;
    ArrayList<Product> productArrayList;
    static CartListAdapter cartListAdapter;
    public static RelativeLayout lyttotal;
    double total;
    ProgressBar progressbar;
    static Activity activity;
    static Session session;
    Button btnShowNow;
    Toolbar toolbar;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.cart));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        session = new Session(CartActivity.this);
        builder = new AlertDialog.Builder(CartActivity.this);

        progressbar = findViewById(R.id.progressbar);
        lyttotal = findViewById(R.id.lyttotal);
        lytempty = findViewById(R.id.lytempty);
        btnShowNow = findViewById(R.id.btnShowNow);
        txttotal = findViewById(R.id.txttotal);
        txtsubtotal = findViewById(R.id.txtsubtotal);
        txtdeliverycharge = findViewById(R.id.txtdeliverycharge);
        txtstotal = findViewById(R.id.txtstotal);
        txt_msg_view =findViewById(R.id.txt_msg);

        cartrecycleview = findViewById(R.id.cartrecycleview);
        cartrecycleview.setLayoutManager(new LinearLayoutManager(CartActivity.this));
        databaseHelper = new DatabaseHelper(CartActivity.this);
        activity = CartActivity.this;

        ApiConfig.GetPaymentConfig(CartActivity.this);
        getData();

        lyttotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.isUserLoggedIn()) {
                    Double last_subtotal = SetDataTotal_2();
                    Log.d("value",""+last_subtotal);
                    if (last_subtotal <= Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY)
                    {
                        /*show alert view*/
                        //total = databaseHelper.getTotalCartAmt(session);
                        showAlertView(txtsubtotal.getText().toString());

                    }
                    else{
                        startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
                    }

                } else {
                    startActivity(new Intent(CartActivity.this, LoginActivity.class).putExtra("fromto", "checkout"));
                }
            }
        });

        btnShowNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void showAlertView(String disp_total) {

        Log.d("disp_total", ""+ disp_total);
        String msg = Constant.free_delivery_message;
        String str_msg = msg;
        String[] parts = str_msg.split("#");
        String msg_1 = parts[0];
        String msg_2 = parts[1];


        String [] parts2 = msg_2.split("@");
        String msg_4 = parts2[0];
        String msg_5 = parts2[1];


        String final_msg = msg_1 + Constant.SETTING_CURRENCY_SYMBOL+"" + Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY+"/-"
                + msg_4 + Constant.SETTING_CURRENCY_SYMBOL +Constant.SETTING_DELIVERY_CHARGE+ "/-" + msg_5 ;

        Log.d("final msg", final_msg);


        final androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(CartActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.msg_view_1, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        final androidx.appcompat.app.AlertDialog dialog = alertDialog.create();

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txt_msg,tvchkout,tvshopping;

        txt_msg = dialogView.findViewById(R.id.txt_msg);
        tvchkout = dialogView.findViewById(R.id.tvchkout);
        tvshopping = dialogView.findViewById(R.id.tvshopping);

        tvchkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
            }
        });

        tvshopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onBackPressed();
            }
        });

        txt_msg.setText(final_msg);
        dialog.show();


    }

    @SuppressLint("SetTextI18n")
    public static void SetDataTotal()
    {
        double total = databaseHelper.getTotalCartAmt(session);

        /*String displaytotal = DatabaseHelper.decimalformatData.format(total);
        if (cartListAdapter.getItemCount() == 1) {
            txttotal.setText(activity.getResources().getString(R.string.total_) + cartListAdapter.getItemCount() + activity.getResources().getString(R.string._item_) + Constant.SETTING_CURRENCY_SYMBOL + " " + displaytotal);
        } else
            txttotal.setText(activity.getResources().getString(R.string.total_) + cartListAdapter.getItemCount() + activity.getResources().getString(R.string._items_) + Constant.SETTING_CURRENCY_SYMBOL + " " + displaytotal);
        txtstotal.setText(Constant.SETTING_CURRENCY_SYMBOL + displaytotal);

        double subtotal = total;
        if (total <= Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY) {
            txtdeliverycharge.setText(Constant.SETTING_CURRENCY_SYMBOL + Constant.SETTING_DELIVERY_CHARGE);
            subtotal = subtotal + Constant.SETTING_DELIVERY_CHARGE;
            //showAlertView(txtsubtotal.getText().toString());
        } else {
            txtdeliverycharge.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            txtdeliverycharge.setText(activity.getResources().getString(R.string.free));
        }
        txtsubtotal.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(subtotal));
        double var = Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY + 1.0;
        txt_msg_view.setText("Free Delivery On Minimum Order"  + " " +Constant.SETTING_CURRENCY_SYMBOL+ " " + var + "/-");*/
    }


    @SuppressLint("SetTextI18n")
    public static Double SetDataTotal_2()
    {
        double total = databaseHelper.getTotalCartAmt(session);
        double subtotal = total;
        return subtotal;
    }


    private void getData() {
        total = 0.00;
        productArrayList = new ArrayList<>();
        final ArrayList<String> idslist = databaseHelper.getCartList();
        if (idslist.isEmpty())
        {
            lytempty.setVisibility(View.VISIBLE);
            lyttotal.setVisibility(View.GONE);
            cartrecycleview.setAdapter(new CartListAdapter(productArrayList, CartActivity.this));
        }
        else {
            progressbar.setVisibility(View.VISIBLE);
            int i = 1;
            for (final String id : idslist)
            {
                final String[] ids = id.split("=");
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constant.PRODUCT_ID, ids[0]);

                final int finalI = i;
                ApiConfig.RequestToVolley(new VolleyCallback()
                {
                    @Override
                    public void onSuccess(boolean result, String response) {
                        System.out.println("=================*cart- " + response + " == " + id);
                        if (result) {
                            try {
                                JSONObject objectbject = new JSONObject(response);
                                if (!objectbject.getBoolean(Constant.ERROR)) {
                                    JSONObject object = new JSONObject(response);
                                    JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                                    Product product = ApiConfig.GetCartList(jsonArray, ids[1], ids[2], databaseHelper);
                                    if (product != null) {
                                        productArrayList.add(product);
                                    }
                                    if (finalI == idslist.size())
                                    {
                                        lyttotal.setVisibility(View.VISIBLE);
                                        cartListAdapter = new CartListAdapter(productArrayList, CartActivity.this);
                                        cartrecycleview.setAdapter(cartListAdapter);
                                        SetDataTotal();
                                        progressbar.setVisibility(View.GONE);
                                    }
                                } else {
                                    databaseHelper.DeleteOrderData(ids[1], ids[0]);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                    }
                }, CartActivity.this, Constant.GET_PRODUCT_DETAIL_URL, params, false);
                i++;
            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (databaseHelper.getTotalItemOfCart() == 0) {
            lytempty.setVisibility(View.VISIBLE);
            lyttotal.setVisibility(View.GONE);
            activity.invalidateOptionsMenu();
            if (cartrecycleview != null) {
                productArrayList = new ArrayList<>();
                cartrecycleview.setAdapter(new CartListAdapter(productArrayList, CartActivity.this));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
