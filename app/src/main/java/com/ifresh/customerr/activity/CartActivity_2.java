package com.ifresh.customerr.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifresh.customerr.R;
import com.ifresh.customerr.adapter.CartListAdapter_2;
import com.ifresh.customerr.helper.ApiConfig;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.helper.DatabaseHelper;
import com.ifresh.customerr.helper.Session;
import com.ifresh.customerr.helper.VolleyCallback;
import com.ifresh.customerr.kotlin.SignUpActivity_K;
import com.ifresh.customerr.model.Mesurrment;
import com.ifresh.customerr.model.ModelProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.ifresh.customerr.helper.Constant.BASEPATH;
import static com.ifresh.customerr.helper.Constant.GET_GETPRODUCTBYID;

public class CartActivity_2 extends AppCompatActivity {
    Context mContext = CartActivity_2.this;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout lytempty;
    @SuppressLint("StaticFieldLeak")
    static TextView txttotal, txtstotal, txtdeliverycharge, txtsubtotal, txt_msg_view;
    RecyclerView cartrecycleview;
    static DatabaseHelper databaseHelper;
    ArrayList<ModelProduct> productArrayList;
    @SuppressLint("StaticFieldLeak")
    static CartListAdapter_2 cartListAdapter;
    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout lyttotal;
    double total;
    ProgressBar progressbar;
    @SuppressLint("StaticFieldLeak")
    static Activity activity;
    @SuppressLint("StaticFieldLeak")
    static Session session;
    Button btnShowNow;
    Toolbar toolbar;
    AlertDialog.Builder builder;
    //String category_id;
    ArrayList<Mesurrment> measurement_list;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.cart));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new Session(CartActivity_2.this);
        builder = new AlertDialog.Builder(CartActivity_2.this);
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
        cartrecycleview.setLayoutManager(new LinearLayoutManager(CartActivity_2.this));
        databaseHelper = new DatabaseHelper(CartActivity_2.this);
        activity = CartActivity_2.this;

        ApiConfig.GetPaymentConfig_2(activity,session);
        //category_id = getIntent().getStringExtra("id");
        //get measurement list
        callSettingApi_messurment();


        lyttotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.isUserLoggedIn())
                {
                    Double last_subtotal = SetDataTotal_2();
                    Log.d("value",""+last_subtotal);
                    if (last_subtotal <= Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY)
                    {
                        /*show alert view*/
                        //total = databaseHelper.getTotalCartAmt(session);
                        showAlertView(txtsubtotal.getText().toString());
                    }
                    else{
                        startActivity(new Intent(CartActivity_2.this, CheckoutActivity_2.class));
                    }
                } else {
                    startActivity(new Intent(CartActivity_2.this, SignUpActivity_K.class).putExtra("fromto", "signup"));
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
                + msg_4 + Constant.SETTING_CURRENCY_SYMBOL +Constant.SETTING_DELIVERY_CHARGE+ "/-" /*+ msg_5*/ ;

        Log.d("final msg", final_msg);
        final androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(CartActivity_2.this);
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
                startActivity(new Intent(CartActivity_2.this, CheckoutActivity_2.class));
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
    public static void SetDataTotal() {
        double total = databaseHelper.getTotalCartAmt(session);
        Log.d("tot", ""+total);
        Log.d("item", ""+databaseHelper.getTotalItemOfCart());

        String displaytotal = DatabaseHelper.decimalformatData.format(total);
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
        double var = Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY ;
        txt_msg_view.setText("Free Delivery On Minimum Order"  + " " +Constant.SETTING_CURRENCY_SYMBOL+ " " + var + "/-");
    }

    @SuppressLint("SetTextI18n")
    public static Double SetDataTotal_2()
    {
        double total = databaseHelper.getTotalCartAmt(session);
        double subtotal = total;
        return subtotal;
    }

    private void getData()
    {
        progressbar.setVisibility(View.VISIBLE);
        productArrayList = new ArrayList<>();
        total = 0.00;
        final ArrayList<String> idslist = databaseHelper.getCartList();
        if (idslist.isEmpty())
        {
            progressbar.setVisibility(View.GONE);
            lytempty.setVisibility(View.VISIBLE);
            lyttotal.setVisibility(View.GONE);
            cartrecycleview.setAdapter(new CartListAdapter_2(productArrayList, CartActivity_2.this));
        }
        else
        {
                int i = 1;
                for (final String id : idslist)
                {
                    final String[] ids = id.split("=");
                    Map<String, String> params = new HashMap<String, String>();
                    String prod_id= ids[0];
                    String frprod_id=ids[3];
                    String frprod_vid=ids[1];

                    String get_param =  BASEPATH + GET_GETPRODUCTBYID +"/"+prod_id+"/"+frprod_id+"/"+frprod_vid;
                    //Log.d("url_send", get_param);
                    final int finalI = i;

                    ApiConfig.RequestToVolley_GET(new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result, String response) {
                            System.out.println("order_process" + response );
                            if (result)
                            {
                                try {
                                    JSONObject objectbject = new JSONObject(response);
                                    if (objectbject.getInt(Constant.SUCESS) == 200)
                                    {
                                        JSONObject object = new JSONObject(response);
                                        JSONArray data_arr = object.getJSONArray("data");
                                        JSONObject data_obj = data_arr.getJSONObject(0);
                                        JSONArray jsonArray_products = data_obj.getJSONArray("products");

                                        ModelProduct product = ApiConfig.GetCartList2(jsonArray_products, ids[1], ids[4], databaseHelper, measurement_list);

                                        if (product != null) {
                                            productArrayList.add(product);
                                        }
                                        if (finalI == idslist.size())
                                        {
                                            lyttotal.setVisibility(View.VISIBLE);
                                            cartListAdapter = new CartListAdapter_2(productArrayList, CartActivity_2.this);
                                            cartrecycleview.setAdapter(cartListAdapter);
                                            progressbar.setVisibility(View.GONE);
                                            SetDataTotal();

                                        }
                                    } else {
                                        databaseHelper.DeleteOrderData(ids[1], ids[0]);
                                        progressbar.setVisibility(View.GONE);

                                    }

                                } catch (JSONException e) {
                                    progressbar.setVisibility(View.GONE);
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, CartActivity_2.this, get_param, params , false);
                    i++;
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (databaseHelper.getTotalItemOfCart() == 0)
        {
            lytempty.setVisibility(View.VISIBLE);
            lyttotal.setVisibility(View.GONE);
            progressbar.setVisibility(View.GONE);

            activity.invalidateOptionsMenu();
            if (cartrecycleview != null)
            {
                productArrayList = new ArrayList<>();
                progressbar.setVisibility(View.GONE);
                cartrecycleview.setAdapter(new CartListAdapter_2(productArrayList, CartActivity_2.this));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
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


    private void callSettingApi_messurment() {
        try{
            String str_measurment = session.getData(Constant.KEY_MEASUREMENT);
            JSONArray jsonArray = new JSONArray(str_measurment);
            measurement_list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object1 = jsonArray.getJSONObject(i);
                measurement_list.add(new Mesurrment(object1.getString("id"), object1.getString("title"), object1.getString("abv")));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


        getData();
    }


}
