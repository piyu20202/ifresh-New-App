package com.ifresh.customerr.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import com.ifresh.customerr.R;

import com.ifresh.customerr.adapter.ItemsAdapter;
import com.ifresh.customerr.adapter.ItemsAdapter_2;
import com.ifresh.customerr.helper.ApiConfig;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.helper.Session;

import com.ifresh.customerr.helper.StorePrefrence;
import com.ifresh.customerr.helper.VolleyCallback;
import com.ifresh.customerr.model.OrderTracker;
import com.ifresh.customerr.model.OrderTracker_2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.ifresh.customerr.helper.Constant.GET_ORDERCANCEL;

public class TrackerDetailActivity extends AppCompatActivity {

    OrderTracker_2 order;
    TextView tvItemTotal, tvTaxPercent, tvTaxAmt, tvDeliveryCharge, tvTotal, tvPromoCode, tvPCAmount, tvWallet, tvFinalTotal, tvDPercent, tvDAmount;
    TextView txtcanceldetail, txtotherdetails, txtorderid, txtorderdate;
    NetworkImageView imgorder;
    SpannableString spannableString;
    Toolbar toolbar;
    public static ProgressBar pBar;
    RecyclerView recyclerView;
    public static Button btnCancel;
    public static LinearLayout lyttracker,lytotherdetail,lytstatus;
    View l4;
    LinearLayout returnLyt, lytPromo, lytWallet, lytPriceDetail;
    double totalAfterTax = 0.0;
    StorePrefrence storePrefrence;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_detail);
        storePrefrence = new StorePrefrence(TrackerDetailActivity.this);
        order = (OrderTracker_2) getIntent().getSerializableExtra("model");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pBar = findViewById(R.id.pBar);
        lytPriceDetail = findViewById(R.id.lytPriceDetail);
        lytPromo = findViewById(R.id.lytPromo);
        lytWallet = findViewById(R.id.lytWallet);
        tvItemTotal = findViewById(R.id.tvItemTotal);
        tvTaxPercent = findViewById(R.id.tvTaxPercent);
        tvTaxAmt = findViewById(R.id.tvTaxAmt);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvDAmount = findViewById(R.id.tvDAmount);
        tvDPercent = findViewById(R.id.tvDPercent);
        tvTotal = findViewById(R.id.tvTotal);
        tvPromoCode = findViewById(R.id.tvPromoCode);
        tvPCAmount = findViewById(R.id.tvPCAmount);
        tvWallet = findViewById(R.id.tvWallet);
        tvFinalTotal = findViewById(R.id.tvFinalTotal);
        txtorderid = findViewById(R.id.txtorderid);
        txtorderdate = findViewById(R.id.txtorderdate);


        imgorder = findViewById(R.id.imgorder);
        txtotherdetails = findViewById(R.id.txtotherdetails);
        txtcanceldetail = findViewById(R.id.txtcanceldetail);
        lytstatus = findViewById(R.id.lytstatus);
        lyttracker = findViewById(R.id.lyttracker);
        lytotherdetail = findViewById(R.id.lytotherdetail);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setNestedScrollingEnabled(false);
        btnCancel = findViewById(R.id.btncancel);
        l4 = findViewById(R.id.l4);
        returnLyt = findViewById(R.id.returnLyt);


        txtorderid.setText(order.getShow_id());
        txtorderdate.setText(order.getDate_added());
        txtotherdetails.setText(getString(R.string.name_1) + order.getUsername() + getString(R.string.mobile_no_1) + order.getMobile() + getString(R.string.address_1) + order.getAddress());

        //totalAfterTax = (Double.parseDouble(order.getTotal()) + Double.parseDouble(order.getDelivery_charge()) + Double.parseDouble(order.getTax_amt()));
        tvItemTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + order.getTotal());
        tvDeliveryCharge.setText("+ " + Constant.SETTING_CURRENCY_SYMBOL + order.getDelivery_charge());
        tvTaxPercent.setText(getString(R.string.tax) + "(" + order.getTax_percent() + "%) :");
        tvTaxAmt.setText("+ " + Constant.SETTING_CURRENCY_SYMBOL + order.getTax_amt());
        tvDPercent.setText(getString(R.string.discount) + "(" + order.getdPercent() + "%) :");
        tvDAmount.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + order.getdAmount());
        tvTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + order.getTotal());
        tvPCAmount.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + order.getPromoDiscount());
        tvWallet.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + order.getWalletBalance());
        tvFinalTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + order.getFinal_total());





        //Log.d("order status", order.getStatus());
        if (!order.getStatus().equalsIgnoreCase("delivered") &&
                !order.getStatus().equalsIgnoreCase("cancelled") &&
                !order.getStatus().equalsIgnoreCase("shipped") &&
                !order.getStatus().equalsIgnoreCase("processed") &&
                !order.getStatus().equalsIgnoreCase("returned"))
        {
            btnCancel.setVisibility(View.VISIBLE);
        } else {

            btnCancel.setVisibility(View.VISIBLE);
        }

        if (order.getStatus().equalsIgnoreCase("cancelled"))
        {
            lyttracker.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            txtcanceldetail.setVisibility(View.VISIBLE);
            txtcanceldetail.setText(getString(R.string.canceled_on) + order.getStatusdate());
            lytPriceDetail.setVisibility(View.GONE);
        }
        else {
            lytPriceDetail.setVisibility(View.VISIBLE);

            if (order.getStatus().equals("returned")) {
                l4.setVisibility(View.VISIBLE);
                returnLyt.setVisibility(View.VISIBLE);
            }
            lyttracker.setVisibility(View.VISIBLE);
            for (int i = 0; i < order.getOrderStatusArrayList().size(); i++)
            {
                int img = getResources().getIdentifier("img" + i, "id", getPackageName());
                int view = getResources().getIdentifier("l" + i, "id", getPackageName());
                int txt = getResources().getIdentifier("txt" + i, "id", getPackageName());
                int textview = getResources().getIdentifier("txt" + i + "" + i, "id", getPackageName());

                // System.out.println("===============" + img + " == " + view);

                if (img != 0 && findViewById(img) != null) {
                    ImageView imageView = findViewById(img);
                    imageView.setColorFilter(getResources().getColor(R.color.colorPrimary));
                }

                if (view != 0 && findViewById(view) != null) {
                    View view1 = findViewById(view);
                    view1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }

                if (txt != 0 && findViewById(txt) != null) {
                    TextView view1 = findViewById(txt);
                    view1.setTextColor(getResources().getColor(R.color.black));
                }

                if (textview != 0 && findViewById(textview) != null) {
                    TextView view1 = findViewById(textview);
                    String str_date = order.getOrderStatusArrayList().get(i).getStatusdate();
                    view1.setText(str_date);
                }
            }
        }
        /*for(int i = 0; i<order.itemsList.size(); i++)
        {
            //Log.d("qty", order.itemsList.get(i).getQuantity());
            if(order.itemsList.get(0).getOrder_type().equalsIgnoreCase("2"))
            {
                lytPriceDetail.setVisibility(View.GONE);
                lyttracker.setVisibility(View.GONE);
            }
            else{
                lytPriceDetail.setVisibility(View.VISIBLE);
                lyttracker.setVisibility(View.VISIBLE);
            }
            break;

        }*/
        if(order.itemsList.get(0).getOrder_type().equalsIgnoreCase("2"))
        {
            lytPriceDetail.setVisibility(View.GONE);
            lyttracker.setVisibility(View.GONE);
            lytstatus.setVisibility(View.GONE);
        }
        else{
            lytPriceDetail.setVisibility(View.VISIBLE);
            lyttracker.setVisibility(View.VISIBLE);
            lytstatus.setVisibility(View.VISIBLE);
        }
        recyclerView.setAdapter(new ItemsAdapter_2(TrackerDetailActivity.this, order.itemsList));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void OnBtnClick(View view) {
        int id = view.getId();
        if (id == R.id.btncancel)
        {
            showAlertView();

            /*new AlertDialog.Builder(TrackerDetailActivity.this)
                    .setTitle(getString(R.string.cancel_order))
                    .setMessage(getString(R.string.cancel_msg))
                    .setPositiveButton(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.cancel)+"</font>"), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put(Constant.UPDATE_ORDER_STATUS, Constant.GetVal);
                            params.put(Constant.ID, order.getOrder_id());
                            params.put(Constant.STATUS, Constant.CANCELLED);
                            params.put("version_code", String.valueOf(storePrefrence.getInt("version_code")));
                            pBar.setVisibility(View.VISIBLE);
                            ApiConfig.RequestToVolley(new VolleyCallback() {
                                @Override
                                public void onSuccess(boolean result, String response) {
                                    // System.out.println("=================*cancelorder- " + response);
                                    if (result) {
                                        try {
                                            JSONObject object = new JSONObject(response);
                                            if (!object.getBoolean(Constant.ERROR)) {
                                                Constant.isOrderCancelled = true;
                                                finish();
                                                ApiConfig.getWalletBalance(TrackerDetailActivity.this, new Session(TrackerDetailActivity.this));
                                            }
                                            Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_LONG).show();
                                            pBar.setVisibility(View.GONE);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }, TrackerDetailActivity.this, Constant.ORDERPROCESS_URL, params, false);




                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();
                        }
                    })
                    .show();
        }*/
        }
    }




    private void showAlertView()
    {
        final androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(TrackerDetailActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.msg_view_4, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        final androidx.appcompat.app.AlertDialog dialog = alertDialog.create();

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvcancel, tvclose;

        tvcancel = dialogView.findViewById(R.id.tvcancel);
        tvclose = dialogView.findViewById(R.id.tvclose);

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constant.ID, order.getOrder_id());
                params.put("is_active", "6");

                ///params.put("version_code", String.valueOf(storePrefrence.getInt("version_code")));
                pBar.setVisibility(View.VISIBLE);
                ApiConfig.RequestToVolley_POST(new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean result, String response) {
                         System.out.println("=================*cancelorder- " + response);
                        if (result) {
                            try {
                                JSONObject object = new JSONObject(response);
                                if (object.getString(Constant.SUCESS).equalsIgnoreCase("200"))
                                {
                                    Constant.isOrderCancelled = true;
                                    finish();
                                    ApiConfig.getWalletBalance(TrackerDetailActivity.this, new Session(TrackerDetailActivity.this));
                                }
                                Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_LONG).show();
                                pBar.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, TrackerDetailActivity.this, Constant.BASEPATH + GET_ORDERCANCEL, params, false);
            }
        });

        tvclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //onBackPressed();
            }
        });
        dialog.show();
    }

}
