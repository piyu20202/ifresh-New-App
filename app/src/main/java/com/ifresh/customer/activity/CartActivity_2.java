package com.ifresh.customer.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifresh.customer.R;
import com.ifresh.customer.adapter.CartListAdapter_2;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.DatabaseHelper;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.StorePrefrence;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.kotlin.SignUpActivity_K;
import com.ifresh.customer.model.Mesurrment;
import com.ifresh.customer.model.ModelProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.ifresh.customer.helper.Constant.BASEPATH;
import static com.ifresh.customer.helper.Constant.GET_GETPRODUCTBYID;
import static com.ifresh.customer.helper.Constant.ISACCEPTMINORDER;

public class CartActivity_2 extends AppCompatActivity {

    StorePrefrence storePrefrence;
    Session session;
    DatabaseHelper databaseHelper;
    Activity activity;
    Context mContext = CartActivity_2.this;
    private int filterIndex;
    double total;
    String price="1", product_on="1";
    ArrayList<ModelProduct> productArrayList;
    ArrayList<Mesurrment> measurement_list;

    Button btnShowNow;
    TextView txttotal, txtstotal, txtdeliverycharge, txtsubtotal, txt_msg_view;
    RecyclerView cartrecycleview;
    ProgressBar progressbar;
    AlertDialog.Builder builder;
    Toolbar toolbar;
    public  LinearLayout lytempty,lytdelivery,lytamt_1;
    public RelativeLayout lyttotal,relative;
    private Menu menu;

    CartListAdapter_2 cartListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.cart));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new Session(CartActivity_2.this);
        storePrefrence = new StorePrefrence(CartActivity_2.this);
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
        relative =findViewById(R.id.relative);
        lytdelivery = findViewById(R.id.lytdelivery);
        lytamt_1 = findViewById(R.id.lytamt_1);

        cartrecycleview = findViewById(R.id.cartrecycleview);
        cartrecycleview.setLayoutManager(new LinearLayoutManager(CartActivity_2.this));


        databaseHelper = new DatabaseHelper(CartActivity_2.this);
        activity = CartActivity_2.this;

        ApiConfig.GetPaymentConfig_2(activity,session);
        callSettingApi_messurment();
        minimum_order();

        lyttotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.isUserLoggedIn())
                {
                    Double last_subtotal = SetDataTotal_2();
                    Log.d("value",""+last_subtotal);
                    if (last_subtotal < Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY)
                    {
                        /*show alert view*/
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
    public  void SetDataTotal() {
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

        if (total <= Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY)
        {
            txtdeliverycharge.setText(Constant.SETTING_CURRENCY_SYMBOL + Constant.SETTING_DELIVERY_CHARGE);
            subtotal = subtotal + Constant.SETTING_DELIVERY_CHARGE;
        } else {
            txtdeliverycharge.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            txtdeliverycharge.setText(activity.getResources().getString(R.string.free));
        }

        txtsubtotal.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(subtotal));
        double var = Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY ;
        //Log.d("val", ""+var);
        txt_msg_view.setText("Free Delivery On Minimum Order"  + " " +Constant.SETTING_CURRENCY_SYMBOL+ " " + var + "/-");

        minimum_order();

    }

    @SuppressLint("SetTextI18n")
    public  Double SetDataTotal_2()
    {
        double total = databaseHelper.getTotalCartAmt(session);
        double subtotal = total;
        return subtotal;
    }

    public  void minimum_order()
    {
        Double last_subtotal = SetDataTotal_2();
        if(ISACCEPTMINORDER)
        {
            relative.setBackgroundResource(R.drawable.bg_button);
            lytdelivery.setVisibility(View.VISIBLE);
            lytamt_1.setVisibility(View.VISIBLE);
            lyttotal.setEnabled(true);
        }
        else{
            if(last_subtotal < storePrefrence.getInt("min_order"))
            {
                relative.setBackgroundColor(Color.parseColor("#ACABAB"));
                lytdelivery.setVisibility(View.VISIBLE);
                lytamt_1.setVisibility(View.VISIBLE);
                lyttotal.setEnabled(false);
            }
            else if (last_subtotal >= storePrefrence.getInt("min_order"))
            {
                relative.setBackgroundResource(R.drawable.bg_button);
                lytdelivery.setVisibility(View.GONE);
                lytamt_1.setVisibility(View.GONE);
                lyttotal.setEnabled(true);

            }
        }

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
                                            lytempty.setVisibility(View.GONE);
                                            lyttotal.setVisibility(View.VISIBLE);
                                            SetDataTotal();

                                        }
                                    } else {
                                        databaseHelper.DeleteOrderData(ids[1], ids[0]);
                                        progressbar.setVisibility(View.GONE);
                                        lytempty.setVisibility(View.VISIBLE);
                                        lyttotal.setVisibility(View.GONE);

                                    }

                                } catch (JSONException e) {
                                    progressbar.setVisibility(View.GONE);
                                    lytempty.setVisibility(View.VISIBLE);
                                    lyttotal.setVisibility(View.GONE);
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
            activity.invalidateOptionsMenu();

            if (cartrecycleview != null)
            {
                productArrayList = new ArrayList<>();
                cartrecycleview.setAdapter(new CartListAdapter_2(productArrayList, CartActivity_2.this));
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_sort).setVisible(true);
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_cart).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
           case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_sort:
                if(productArrayList != null && productArrayList.size()>0)
                {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CartActivity_2.this);
                    builder.setTitle(CartActivity_2.this.getResources().getString(R.string.filterby));
                    builder.setSingleChoiceItems(Constant.filtervalues, filterIndex, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            filterIndex = item;
                            switch (item) {
                                case 0:
                                    product_on = Constant.PRODUCT_N_O;
                                    Collections.sort(productArrayList, ModelProduct.compareByATOZ);
                                    progressbar.setVisibility(View.VISIBLE);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Do something after 100ms
                                            progressbar.setVisibility(View.GONE);
                                            cartListAdapter.notifyDataSetChanged();
                                        }
                                    }, 2000);
                                    break;
                                case 1:
                                    product_on = Constant.PRODUCT_O_N;
                                    Collections.sort(productArrayList, ModelProduct.compareByZTOA);
                                    progressbar.setVisibility(View.VISIBLE);
                                    handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Do something after 100ms
                                            progressbar.setVisibility(View.GONE);
                                            cartListAdapter.notifyDataSetChanged();
                                        }
                                    }, 2000);
                                    break;
                                case 2:
                                    price = Constant.PRICE_H_L;
                                    Collections.sort(productArrayList, ModelProduct.compareByPriceVariations_1);
                                    progressbar.setVisibility(View.VISIBLE);
                                    handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Do something after 100ms
                                            progressbar.setVisibility(View.GONE);
                                            cartListAdapter.notifyDataSetChanged();
                                        }
                                    }, 2000);
                                    break;
                                case 3:
                                    price = Constant.PRICE_L_H;
                                    Collections.sort(productArrayList,ModelProduct.compareByPriceVariations);
                                    progressbar.setVisibility(View.VISIBLE);
                                    handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Do something after 100ms
                                            progressbar.setVisibility(View.GONE);
                                            cartListAdapter.notifyDataSetChanged();
                                        }
                                    }, 1000);
                                    break;
                            }

                            dialog.dismiss();
                        }
                    });
                    androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else{
                    Toast.makeText(mContext, "NO PRODUCT", Toast.LENGTH_SHORT).show();
                }
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
            /*if(session.getData(Constant.KEY_MEASUREMENT).length() == 0)
            {
                ApiConfig.GetMessurmentApi(activity, session);// to call measurement data
            }*/
            JSONArray jsonArray = new JSONArray(session.getData(Constant.KEY_MEASUREMENT));
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
