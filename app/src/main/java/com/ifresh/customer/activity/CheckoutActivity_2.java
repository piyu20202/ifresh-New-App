package com.ifresh.customer.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ifresh.customer.R;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.DatabaseHelper;
import com.ifresh.customer.helper.GPSTracker;
import com.ifresh.customer.helper.PaymentModelClass;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.StorePrefrence;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.kotlin.FillAddress;
import com.ifresh.customer.model.Mesurrment;
import com.ifresh.customer.model.PaymentType;
import com.ifresh.customer.model.Slot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

;

@SuppressLint("SetTextI18n")
public class CheckoutActivity_2 extends AppCompatActivity implements OnMapReadyCallback, PaymentResultListener {
    Context ctx = CheckoutActivity_2.this;
    private String TAG = CheckoutActivity_2.class.getSimpleName();
    public Toolbar toolbar;
    public TextView tvTaxPercent, tvTaxAmt, tvDelivery, tvPayment, tvLocation, tvAlert, tvWltBalance, tvCity, tvName, tvTotal, tvDeliveryCharge, tvSubTotal, tvCurrent, tvWallet, tvPromoCode, tvPCAmount, tvPlaceOrder, tvConfirmOrder, tvPreTotal,txt_default_add;
    LinearLayout lytPayOption, lytTax, lytOrderList, lytWallet, lytCLocation, paymentLyt, deliveryLyt, lytPayU, lytPayPal, lytRazorPay, dayLyt,linear_adtype,linear_view;
    Button btnApply;
    EditText edtPromoCode;
    public ProgressBar prgLoading;
    Session session;
    StorePrefrence storePrefrence;
    JSONArray qtyList, variantIdList, nameList, frencid, frenpid, prodvIdList, priceList,imagenameList;
    DatabaseHelper databaseHelper;
    Double total, subtotal,saveLatitude,saveLongitude;
    String deliveryCharge = "0",address_id,send_address_param="";
    PaymentModelClass paymentModelClass;
    SupportMapFragment mapFragment;
    CheckBox chWallet, chHome, chWork,chOther;
    public RadioButton rToday, rTomorrow;
    public Activity activity = CheckoutActivity_2.this;


    String deliveryTime = "", deliveryTime_id,deliveryDay = "", pCode = "", paymentMethod = "", label = "", appliedCode = "", deliveryDay_val="";
    String paymentMethod_id;
    RadioButton rbCod, rbPayU, rbPayPal, rbRazorPay;
    ProgressDialog mProgressDialog;
    RelativeLayout walletLyt, mainLayout;
    Map<String, String> razorParams;
    public String razorPayId;
    double usedBalance = 0;
    RecyclerView recyclerView;
    ArrayList<Slot> slotList;
    ArrayList<PaymentType> payment_typeList;

    SlotAdapter adapter;
    ProgressBar pBar;
    public boolean isApplied;
    double taxAmt = 0.0;
    double dCharge = 0.0, pCodeDiscount = 0.0;
    TextView msgtxt,tvnoAddress;
    ImageView imgedit;
    JSONArray order_arr;

    JSONObject obj_sendParam;

    Boolean is_address_save=false, is_default_address_save=false;
    ArrayList<Mesurrment> measurement_list;
    GPSTracker gps;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        mainLayout = findViewById(R.id.mainLayout);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        paymentModelClass = new PaymentModelClass(CheckoutActivity_2.this);
        databaseHelper = new DatabaseHelper(CheckoutActivity_2.this);
        session = new Session(CheckoutActivity_2.this);
        storePrefrence = new StorePrefrence(CheckoutActivity_2.this);
        gps = new GPSTracker(ctx);

        if( session.getCoordinates(Session.KEY_LATITUDE).equalsIgnoreCase("0.0") || session.getCoordinates(Session.KEY_LONGITUDE).equalsIgnoreCase("0.0")){
            saveLatitude = gps.latitude;
            saveLongitude = gps.longitude;
        }
        else{
            saveLatitude = Double.parseDouble(session.getCoordinates(Session.KEY_LATITUDE));
            saveLongitude = Double.parseDouble(session.getCoordinates(Session.KEY_LONGITUDE));
        }


        //Log.d("KEYID",session.getData(Session.KEY_ID));
        txt_default_add = findViewById(R.id.txt_default_add);
        linear_view = findViewById(R.id.linear_view);
        linear_adtype = findViewById(R.id.linear_adtype);
        imgedit = findViewById(R.id.imgedit);
        tvnoAddress = findViewById(R.id.tvnoAddress);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        msgtxt = findViewById(R.id.msgtxt);
        pBar = findViewById(R.id.pBar);
        lytTax = findViewById(R.id.lytTax);
        tvTaxAmt = findViewById(R.id.tvTaxAmt);
        tvTaxPercent = findViewById(R.id.tvTaxPercent);
        dayLyt = findViewById(R.id.dayLyt);
        rbCod = findViewById(R.id.rbcod);
        rbPayU = findViewById(R.id.rbPayU);
        rbPayPal = findViewById(R.id.rbPayPal);
        rbRazorPay = findViewById(R.id.rbRazorPay);
        tvLocation = findViewById(R.id.tvLocation);
        tvDelivery = findViewById(R.id.tvDelivery);
        tvPayment = findViewById(R.id.tvPayment);
        tvPCAmount = findViewById(R.id.tvPCAmount);
        tvPromoCode = findViewById(R.id.tvPromoCode);
        tvAlert = findViewById(R.id.tvAlert);
        edtPromoCode = findViewById(R.id.edtPromoCode);
        lytPayPal = findViewById(R.id.lytPayPal);
        lytRazorPay = findViewById(R.id.lytRazorPay);
        lytPayU = findViewById(R.id.lytPayU);
        chWallet = findViewById(R.id.chWallet);
        chHome = findViewById(R.id.chHome);
        chWork = findViewById(R.id.chWork);
        chOther = findViewById(R.id.chOther);
        tvSubTotal = findViewById(R.id.tvSubTotal);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvTotal = findViewById(R.id.tvTotal);
        tvName = findViewById(R.id.tvName);
        tvCity = findViewById(R.id.tvCity);
        tvCurrent = findViewById(R.id.tvCurrent);
        lytPayOption = findViewById(R.id.lytPayOption);
        lytOrderList = findViewById(R.id.lytOrderList);
        lytCLocation = findViewById(R.id.lytCLocation);
        lytWallet = findViewById(R.id.lytWallet);
        walletLyt = findViewById(R.id.walletLyt);
        paymentLyt = findViewById(R.id.paymentLyt);
        deliveryLyt = findViewById(R.id.deliveryLyt);
        tvWallet = findViewById(R.id.tvWallet);
        prgLoading = findViewById(R.id.prgLoading);
        tvPlaceOrder = findViewById(R.id.tvPlaceOrder);
        tvConfirmOrder = findViewById(R.id.tvConfirmOrder);
        lytWallet.setVisibility(View.GONE);
        rToday = findViewById(R.id.rToday);
        rTomorrow = findViewById(R.id.rTomorrow);
        tvWltBalance = findViewById(R.id.tvWltBalance);
        tvPreTotal = findViewById(R.id.tvPreTotal);
        btnApply = findViewById(R.id.btnApply);
        tvLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_my_location, 0, 0, 0);
        tvCurrent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_address, 0, 0, 0);
        tvDelivery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process, 0, 0, 0);
        tvPayment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process_gray, 0, 0, 0);
        tvConfirmOrder.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_confirm, 0);
        tvPlaceOrder.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_process, 0);
        tvPreTotal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info, 0, 0, 0);

        ApiConfig.getWalletBalance(activity, session);
        callSettingApi_messurment();
        GetTimeSlots_2();
        GetPayment_methodtype();
        setPaymentMethod();
        try {
            qtyList = new JSONArray(session.getData(Session.KEY_Orderqty));
            variantIdList = new JSONArray(session.getData(Session.KEY_Ordervid));
            frencid = new JSONArray(session.getData(Session.KEY_Frencid));
            frenpid = new JSONArray(session.getData(Session.KEY_Frenpid));
            priceList = new JSONArray(session.getData(Session.KEY_Price));
            nameList = new JSONArray(session.getData(Session.KEY_Ordername));
            prodvIdList = new JSONArray(session.getData(Session.KEY_Prodvid));
            imagenameList = new JSONArray(session.getData(Session.KEY_Imagename));

            JSONObject data ;
            order_arr = new JSONArray();
            for(int i=0; i<variantIdList.length();i++)
            {
                data = new JSONObject();
                try{
                    data.put("productId", prodvIdList.get(i));
                    data.put("frproductvarId", frenpid.get(i));
                    data.put("franchiseId", frencid.get(i));
                    data.put("price", priceList.get(i));
                    data.put("frproductId",frenpid.get(i));
                    data.put("qty", qtyList.get(i));
                    data.put("image_url", imagenameList.get(i));

                    String[] name_0 = nameList.getString(i).split("@");


                    String measurmentId="";
                    for(int p = 0; p<measurement_list.size(); p++)
                    {
                        Mesurrment mesurrment1 = measurement_list.get(p);
                        if(mesurrment1.getAbv().equalsIgnoreCase(name_0[0]))
                        {
                            measurmentId = mesurrment1.getId();
                            break;
                        }
                    }



                    String[] name_1 =  name_0[1].split("==");
                    data.put("measurement",name_1[0]);
                    data.put("unit",  measurmentId);
                    //data.put("unit",  name_1[0]);

                    String[] name_list = nameList.getString(i).split("@");
                    String[] name = name_list[1].split("==");
                    data.put("title",  name[1]);
                    order_arr.put(data);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

            for (int i = 0; i < nameList.length(); i++)
            {
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setWeightSum(4f);
                String[] name_0 = nameList.getString(i).split("@");

                String[] name = name_0[1].split("==");

                TextView tv1 = new TextView(this);


                tv1.setText(name[1] + " (" + name[0]  + name_0[0] + ")");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.weight = 1.5f;
                tv1.setLayoutParams(lp);
                tv1.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                linearLayout.addView(tv1);

                TextView tv2 = new TextView(this);
                tv2.setText(qtyList.getString(i));
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp1.weight = 0.7f;
                tv2.setLayoutParams(lp1);
                tv2.setGravity(Gravity.CENTER);
                linearLayout.addView(tv2);

                TextView tv3 = new TextView(this);
                tv3.setText(Constant.SETTING_CURRENCY_SYMBOL + name[2]);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp2.weight = 0.8f;
                tv3.setLayoutParams(lp2);
                tv3.setGravity(Gravity.CENTER);
                linearLayout.addView(tv3);

                TextView tv4 = new TextView(this);
                tv4.setText(Constant.SETTING_CURRENCY_SYMBOL + name[3]);
                LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp3.weight = 1f;
                tv4.setLayoutParams(lp3);
                tv4.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                linearLayout.addView(tv4);
                lytOrderList.addView(linearLayout);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SetDataTotal();

        if(total<Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY)
        {
            //delivery charge will include
            msgtxt.setText(storePrefrence.getString("msg_below_300"));
            msgtxt.setTextColor(CheckoutActivity_2.this.getResources().getColor(R.color.red));
        }
        else if(total>=Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY)
        {
            //delivery charge will exclude
            msgtxt.setText(storePrefrence.getString("msg_above_300"));
            msgtxt.setTextColor(CheckoutActivity_2.this.getResources().getColor(R.color.colorPrimary));
        }

        chWallet.setTag("false");
        ApiConfig.getWalletBalance(activity,session);

        tvWltBalance.setText(getString(R.string.total_balance) + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);

        if (Constant.WALLET_BALANCE == 0)
        {
            chWallet.setEnabled(false);
            walletLyt.setEnabled(false);
        }
        //Log.d("bal", ""+Constant.WALLET_BALANCE);
        chWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chWallet.getTag().equals("false"))
                {
                    chWallet.setChecked(true);
                    lytWallet.setVisibility(View.VISIBLE);
                    Log.d("WALLET_BALANCE", ""+Constant.WALLET_BALANCE);
                    Log.d("Total", ""+total);

                        if(total<Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY)
                        {
                            //delivery charge will include
                            msgtxt.setText(storePrefrence.getString("msg_below_300"));
                            msgtxt.setTextColor(CheckoutActivity_2.this.getResources().getColor(R.color.red));
                        }
                        else if(total>=Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY)
                        {
                            //delivery charge will exclude
                            msgtxt.setText(storePrefrence.getString("msg_above_300"));
                            msgtxt.setTextColor(CheckoutActivity_2.this.getResources().getColor(R.color.colorPrimary));
                        }

                    if (Constant.WALLET_BALANCE >= total)
                    {
                        usedBalance = total;
                        tvWltBalance.setText(getString(R.string.remaining_wallet_balance) + Constant.SETTING_CURRENCY_SYMBOL + (Constant.WALLET_BALANCE - usedBalance));
                        paymentMethod = "wallet";
                        paymentMethod_id = "5";
                        lytPayOption.setVisibility(View.GONE);
                    } else {
                        usedBalance = Constant.WALLET_BALANCE;
                        tvWltBalance.setText(getString(R.string.remaining_wallet_balance) + Constant.SETTING_CURRENCY_SYMBOL + "0.0");
                        lytPayOption.setVisibility(View.VISIBLE);
                    }
                    subtotal = (subtotal - usedBalance);
                    DecimalFormat df = new DecimalFormat("#.##");
                    tvWallet.setText("-" + Constant.SETTING_CURRENCY_SYMBOL + df.format(usedBalance));
                    tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(subtotal));
                    chWallet.setTag("true");
                } else {
                    walletUncheck();
                }
            }
        });

        tvnoAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if(is_address_save)
                {
                    intent = new Intent(CheckoutActivity_2.this, SetDefaultAddress_2.class);
                }
                else{
                    intent = new Intent(CheckoutActivity_2.this, FillAddress.class);
                    intent.putExtra("userId", session.getData(session.KEY_id));
                }
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        imgedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckoutActivity_2.this, FillAddress.class);
                intent.putExtra("userId", session.getData(session.KEY_id));
                startActivity(intent);

            }
        });

        //PromoCodeCheck();

    }

    private void GetPayment_methodtype()
    {
        try{
            String str_paymenttype = session.getData(Constant.KEY_PAYMENT_TYPE);
            JSONArray jsonArray = new JSONArray(str_paymenttype);
            payment_typeList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object1 = jsonArray.getJSONObject(i);
                payment_typeList.add(new PaymentType(object1.getString("id"), object1.getString("title"), object1.getString("abv")));
            }
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
    }

    public void walletUncheck() {
        lytPayOption.setVisibility(View.VISIBLE);
        tvWltBalance.setText(getString(R.string.total_balance) + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
        lytWallet.setVisibility(View.GONE);
        chWallet.setChecked(false);
        chWallet.setTag("false");
        SetDataTotal();
    }

    public void setPaymentMethod()
    {
        lytPayPal.setVisibility(View.GONE);
        lytPayU.setVisibility(View.GONE);
        lytRazorPay.setVisibility(View.VISIBLE);


        rbCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbCod.setChecked(true);
                rbPayU.setChecked(false);
                rbPayPal.setChecked(false);
                rbRazorPay.setChecked(false);
                paymentMethod = rbCod.getTag().toString();
                paymentMethod_id =  "1";
            }
        });
        /*rbPayU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbPayU.setChecked(true);
                rbCod.setChecked(false);
                rbPayPal.setChecked(false);
                rbRazorPay.setChecked(false);
                paymentMethod = rbPayU.getTag().toString();
                paymentMethod_id =  "2";

            }
        });

        rbPayPal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbPayPal.setChecked(true);
                rbCod.setChecked(false);
                rbPayU.setChecked(false);
                rbRazorPay.setChecked(false);
                paymentMethod = rbPayPal.getTag().toString();
                paymentMethod_id =  "3";

            }
        });*/

        rbRazorPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbRazorPay.setChecked(true);
                rbPayPal.setChecked(false);
                rbCod.setChecked(false);
                rbPayU.setChecked(false);
                paymentMethod = rbRazorPay.getTag().toString();
                paymentMethod_id =  "2";
                Checkout.preload(getApplicationContext());
            }
        });

    }

    private String getTime() {
        String delegate = "HH:mm aaa";
        return (String) DateFormat.format(delegate, Calendar.getInstance().getTime());
    }


    public void SetDataTotal() {
        //Log.d("minamount", ""+Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY);
        //Log.d("delivery charge", ""+Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY);

        total = databaseHelper.getTotalCartAmt(session);
        tvTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(total));
        subtotal = total;
        if (total <= Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY)
        {
            tvDeliveryCharge.setText(Constant.SETTING_CURRENCY_SYMBOL + Constant.SETTING_DELIVERY_CHARGE);
            subtotal = subtotal + Constant.SETTING_DELIVERY_CHARGE;
            deliveryCharge = Constant.SETTING_DELIVERY_CHARGE + "";
        } else {
            tvDeliveryCharge.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvDeliveryCharge.setText(getResources().getString(R.string.free));
            deliveryCharge = "0";
        }
        Log.d("tax", ""+Constant.SETTING_TAX);
        taxAmt = ((Constant.SETTING_TAX * total) / 100);

        if (pCode.isEmpty()) {
            subtotal = (subtotal + taxAmt);
        } else
            subtotal = (subtotal + taxAmt - pCodeDiscount);

        tvTaxPercent.setText("Tax(" + Constant.SETTING_TAX + "%)");
        tvTaxAmt.setText("+ " + Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(taxAmt));
        tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(subtotal));
    }


    public void OnBtnClick(View view) {
        switch (view.getId()) {
            case R.id.tvConfirmOrder:
                if(subtotal >= Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY)
                {
                    tvPayment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    tvPayment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process, 0, 0, 0);
                    tvDelivery.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
                    tvDelivery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                    tvConfirmOrder.setVisibility(View.GONE);
                    tvPlaceOrder.setVisibility(View.VISIBLE);
                    paymentLyt.setVisibility(View.VISIBLE);
                    deliveryLyt.setVisibility(View.GONE);
                }
                else{

                    tvPayment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                    tvPayment.setEnabled(false);
                }




                break;
            case R.id.tvLocation:
                if (tvLocation.getTag().equals("hide")) {
                    tvLocation.setTag("show");
                    lytCLocation.setVisibility(View.VISIBLE);
                } else {
                    tvLocation.setTag("hide");
                    lytCLocation.setVisibility(View.GONE);
                }
                break;
            case R.id.tvPlaceOrder:
                PlaceOrderProcess();
                break;
            case R.id.imgedit: {

                if(databaseHelper.getTotalCartAmt(session) > 0)
                {
                    if(is_default_address_save)
                    {
                        Intent intent = new Intent(CheckoutActivity_2.this, FillAddress.class);
                        intent.putExtra("userId", session.getData(Session.KEY_id));
                        startActivity(intent);
                    }
                }
            }
                break;
            case R.id.tvUpdate:
                if (ApiConfig.isGPSEnable(CheckoutActivity_2.this))
                    //startActivity(new Intent(CheckoutActivity.this, MapActivity.class));
                    startActivity(new Intent(CheckoutActivity_2.this, MapsActivity.class));
                else
                    ApiConfig.displayLocationSettingsRequest(CheckoutActivity_2.this);
                break;
            default:
                break;
        }
    }


    public void PlaceOrderProcess() {
        if (deliveryDay.length() == 0) {
            Toast.makeText(CheckoutActivity_2.this, getString(R.string.select_delivery_day), Toast.LENGTH_SHORT).show();
            return;
        } else if (deliveryTime.length() == 0) {
            Toast.makeText(CheckoutActivity_2.this, getString(R.string.select_delivery_time), Toast.LENGTH_SHORT).show();
            return;
        } else if (paymentMethod.isEmpty()) {
            Toast.makeText(CheckoutActivity_2.this, getString(R.string.select_payment_method), Toast.LENGTH_SHORT).show();
            return;
        }

        final Map<String, String> sendparams = new HashMap<String, String>();
        obj_sendParam = new JSONObject();
        try{
            obj_sendParam.put("userId", session.getData(Session.KEY_id));
            obj_sendParam.put("franchiseId", frencid.get(0));
            obj_sendParam.put("delivery_address", send_address_param);
            obj_sendParam.put("delivery_day", deliveryDay_val);
            obj_sendParam.put("delivery_time_id", deliveryTime_id);
            obj_sendParam.put("delivery_time", deliveryTime);
            obj_sendParam.put("version_code", String.valueOf(storePrefrence.getInt("version_code")));
            obj_sendParam.put("tax_percent", String.valueOf(Constant.SETTING_TAX));
            obj_sendParam.put("tax_amount", DatabaseHelper.decimalformatData.format(taxAmt));
            obj_sendParam.put("total",  DatabaseHelper.decimalformatData.format(total));
            obj_sendParam.put("final_total",  DatabaseHelper.decimalformatData.format(subtotal));
            obj_sendParam.put("phone_no", session.getData(Session.KEY_mobile));
            obj_sendParam.put("delivery_charge", deliveryCharge);
            obj_sendParam.put("key_wallet_used", chWallet.getTag().toString());
            obj_sendParam.put("key_wallet_balance", String.valueOf(usedBalance));
            obj_sendParam.put("payment_method", paymentMethod_id);
            obj_sendParam.put("pincode", session.getData("pincode"));
            obj_sendParam.put("latitude",saveLatitude);
            obj_sendParam.put("longitude", saveLongitude);
            obj_sendParam.put("email", session.getData(Session.KEY_email));
            obj_sendParam.put("order_val", order_arr);
            obj_sendParam.put("razorpay_payment_id", "");
            obj_sendParam.put("razorpay_amt", "");

            String order_param = obj_sendParam.toString();
            sendparams.put("order_param", order_param);
            //System.out.println("=====param" + sendparams.toString());

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CheckoutActivity_2.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_order_confirm, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        final AlertDialog dialog = alertDialog.create();

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvCancel, tvConfirm, tvItemTotal, tvTaxPercent1, tvTaxAmt1, tvDeliveryCharge1, tvTotal1, tvPromoCode1, tvPCAmount1, tvWallet1, tvFinalTotal1;
        LinearLayout lytPromo, lytWallet;

        lytPromo = dialogView.findViewById(R.id.lytPromo);
        lytWallet = dialogView.findViewById(R.id.lytWallet);
        tvItemTotal = dialogView.findViewById(R.id.tvItemTotal);
        tvTaxPercent1 = dialogView.findViewById(R.id.tvTaxPercent);
        tvTaxAmt1 = dialogView.findViewById(R.id.tvTaxAmt);
        tvDeliveryCharge1 = dialogView.findViewById(R.id.tvDeliveryCharge);
        tvTotal1 = dialogView.findViewById(R.id.tvTotal);
        tvPromoCode1 = dialogView.findViewById(R.id.tvPromoCode);
        tvPCAmount1 = dialogView.findViewById(R.id.tvPCAmount);
        tvWallet1 = dialogView.findViewById(R.id.tvWallet);
        tvFinalTotal1 = dialogView.findViewById(R.id.tvFinalTotal);
        tvCancel = dialogView.findViewById(R.id.tvCancel);
        tvConfirm = dialogView.findViewById(R.id.tvConfirm);
        String orderMessage = "";
        if (!pCode.isEmpty())
            lytPromo.setVisibility(View.VISIBLE);
        else
            lytPromo.setVisibility(View.GONE);

        if (chWallet.getTag().toString().equals("true"))
            lytWallet.setVisibility(View.VISIBLE);
        else
            lytWallet.setVisibility(View.GONE);

        dCharge = tvDeliveryCharge.getText().toString().equalsIgnoreCase("free") ? 0.0 : Constant.SETTING_DELIVERY_CHARGE;

        double totalAfterTax = (total + dCharge + taxAmt);
        tvItemTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + total);
        tvDeliveryCharge1.setText(tvDeliveryCharge.getText().toString());

        tvTaxPercent1.setText(getString(R.string.tax) + "(" + Constant.SETTING_TAX + "%) :");
        tvTaxAmt1.setText(tvTaxAmt.getText().toString());
        tvTotal1.setText(Constant.SETTING_CURRENCY_SYMBOL + totalAfterTax);
        tvPCAmount1.setText(tvPCAmount.getText().toString());
        DecimalFormat df = new DecimalFormat("#.##");
        tvWallet1.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + df.format(usedBalance));

        tvFinalTotal1.setText(Constant.SETTING_CURRENCY_SYMBOL + df.format(subtotal));

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //vibrate phone
                final Vibrator vibe = (Vibrator) CheckoutActivity_2.this.getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(80);

                if (paymentMethod.equals(getResources().getString(R.string.codpaytype)) || paymentMethod.equals("wallet"))
                {
                    ApiConfig.RequestToVolley_POST(new VolleyCallback()
                    {
                        @Override
                        public void onSuccess(boolean result, String response) {
                            if (result) {
                                try {
                                    System.out.println("====place order res " + response);
                                    JSONObject object = new JSONObject(response);
                                    if (object.getInt(Constant.SUCESS)==200)
                                    {
                                        Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                                        if (chWallet.getTag().toString().equals("true"))
                                            ApiConfig.getWalletBalance(CheckoutActivity_2.this, session);
                                        dialog.dismiss();
                                        startActivity(new Intent(CheckoutActivity_2.this, OrderPlacedActivity.class));
                                        finish();

                                    } else {
                                        Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //  System.out.println("========order=======" + response);
                            Log.d("url", Constant.BASEPATH + Constant.GET_ORDERSEND);
                        }
                    }, CheckoutActivity_2.this, Constant.BASEPATH + Constant.GET_ORDERSEND, sendparams, true);


                    dialog.dismiss();
                }
                /*else if (paymentMethod.equals(getString(R.string.pay_u))) {
                    dialog.dismiss();
                    sendparams.put(Constant.USER_NAME, session.getData(Session.KEY_FIRSTNAME) +" "+ session.getData(Session.KEY_LASTNAME));
                    paymentModelClass.OnPayClick(CheckoutActivity_2.this, sendparams);

                }
                else if (paymentMethod.equals(getString(R.string.paypal)))
                {
                    dialog.dismiss();
                    sendparams.put(Constant.USER_NAME, session.getData(Session.KEY_FIRSTNAME) +" "+ session.getData(Session.KEY_LASTNAME));

                    StartPayPalPayment(sendparams);
                }*/
                else if (paymentMethod.equals(getString(R.string.razor_pay)))
                {
                    dialog.dismiss();
                    //sendparams.put(Constant.USER_NAME, session.getData(Session.KEY_FIRSTNAME) +" "+ session.getData(Session.KEY_LASTNAME));
                    razorParams = sendparams;
                    CreateOrderId();

                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void CreateOrderId() {
        showProgressDialog(getString(R.string.loading));
        Map<String, String> params = new HashMap<>();
        final String[] amount = String.valueOf(subtotal*100).split("\\.");
        //params.put("amount", "" + amount[0]);
        //System.out.println("====params " + params.toString());
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                startPayment(String.valueOf(System.currentTimeMillis()), amount[0]);
                hideProgressDialog();
            }
        }, 2000);

    }

    public void startPayment(String orderId, String payAmount)
    {
        Checkout checkout = new Checkout();
        Log.d("Value=====>", Constant.RAZOR_PAY_KEY_VALUE);

        checkout.setKeyID(Constant.RAZOR_PAY_KEY_VALUE);
        checkout.setImage(R.drawable.ic_launcher);
        //Log.d("orderId",orderId);
        try {
            JSONObject options = new JSONObject();
            options.put(Constant.NAME, session.getData(Session.KEY_FIRSTNAME) + " "+ session.getData(Session.KEY_LASTNAME));
            options.put(Constant.CURRENCY, "INR");
            options.put(Constant.AMOUNT, payAmount);

            JSONObject preFill = new JSONObject();
            preFill.put(Constant.EMAIL, session.getData(Session.KEY_email));
            preFill.put(Constant.CONTACT, session.getData(Session.KEY_mobile));
            options.put("prefill", preFill);
            checkout.open(CheckoutActivity_2.this, options);
        } catch (Exception e) {
            Log.d(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            razorPayId = razorpayPaymentID;
            PlaceOrder(paymentMethod, razorPayId, true, razorParams, "Success");

        } catch (Exception e) {
            Log.d(TAG, "onPaymentSuccess  ", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d(TAG, "onPaymentError  ", e);
        }
    }

    public void PlaceOrder(final String paymentType, final String txnid, boolean issuccess, final Map<String, String> sendparams, final String status) {

        showProgressDialog(getString(R.string.processing));
        if (issuccess)
        {
            try{
                obj_sendParam.put("razorpay_payment_id", txnid);
                obj_sendParam.put("razorpay_amt", DatabaseHelper.decimalformatData.format(subtotal));
                sendparams.put("order_param", obj_sendParam.toString());
               }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            ApiConfig.RequestToVolley_POST(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response)
                {
                    if (result) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getInt(Constant.SUCESS)==200)
                            {
                                Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                                if (chWallet.getTag().toString().equals("true"))
                                    ApiConfig.getWalletBalance(CheckoutActivity_2.this, session);
                                startActivity(new Intent(CheckoutActivity_2.this, OrderPlacedActivity.class));
                                finish();

                            }
                            hideProgressDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, CheckoutActivity_2.this, Constant.BASEPATH + Constant.GET_ORDERSEND,  sendparams, false);

        }


        else {
              Intent intent = new Intent(activity, FailedRazaorPay.class);
              intent.putExtra("txnid", txnid);
              intent.putExtra("status", status);
              intent.putExtra("razorpay_amt", DatabaseHelper.decimalformatData.format(subtotal));
              intent.putExtra("msg", getString(R.string.order_failed));
              startActivity(intent);


        }

    }

    public void AddTransaction(String orderId, String paymentType, String txnid, final String status, String message, Map<String, String> sendparams) {
        try{
            obj_sendParam.put("razorpay_payment_id", txnid);
            obj_sendParam.put("razorpay_amt", DatabaseHelper.decimalformatData.format(subtotal));
            sendparams.put("order_param", obj_sendParam.toString());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


    }

    public void StartPayPalPayment(final Map<String, String> sendParams) {
        showProgressDialog(getString(R.string.processing));
        Map<String, String> params = new HashMap<>();
        params.put(Constant.FIRST_NAME, sendParams.get(Constant.USER_NAME));
        params.put(Constant.LAST_NAME, sendParams.get(Constant.USER_NAME));
        params.put(Constant.PAYER_EMAIL, sendParams.get(Constant.EMAIL));
        params.put(Constant.ITEM_NAME, "Cart Order");
        params.put(Constant.ITEM_NUMBER, "1");
        params.put(Constant.AMOUNT, sendParams.get(Constant.FINAL_TOTAL));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                hideProgressDialog();
                Intent intent = new Intent(getApplicationContext(), PayPalWebActivity.class);
                intent.putExtra("url", response);
                intent.putExtra("params", (Serializable) sendParams);
                startActivity(intent);
            }
        }, CheckoutActivity_2.this, Constant.PAPAL_URL, params, true);
    }

    public void RefreshPromoCode(View view) {
        if (isApplied) {
            btnApply.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            btnApply.setText(getString(R.string.apply));
            edtPromoCode.setText("");
            tvPromoCode.setVisibility(View.GONE);
            tvPCAmount.setVisibility(View.GONE);
            isApplied = false;
            appliedCode = "";
            pCode = "";
            SetDataTotal();
        }
    }

    public void PromoCodeCheck() {
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String promoCode = edtPromoCode.getText().toString().trim();
                if (promoCode.isEmpty()) {
                    tvAlert.setVisibility(View.VISIBLE);
                    tvAlert.setText(getString(R.string.enter_promo_code));
                } else if (isApplied && promoCode.equals(appliedCode)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.promo_code_already_applied), Toast.LENGTH_SHORT).show();
                } else {
                    if (isApplied && !promoCode.equals(appliedCode)) {
                        SetDataTotal();
                    }
                    tvAlert.setVisibility(View.GONE);
                    btnApply.setVisibility(View.INVISIBLE);
                    pBar.setVisibility(View.VISIBLE);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Constant.VALIDATE_PROMO_CODE, Constant.GetVal);
                    params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
                    params.put(Constant.PROMO_CODE, promoCode);
                    params.put(Constant.TOTAL, String.valueOf(total));

                    ApiConfig.RequestToVolley(new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result, String response) {
                            if (result) {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    //   System.out.println("===res " + response);
                                    if (!object.getBoolean(Constant.ERROR)) {
                                        pCode = object.getString(Constant.PROMO_CODE);
                                        tvPromoCode.setText(getString(R.string.promo_code) + "(" + pCode + ")");
                                        btnApply.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
                                        btnApply.setText(getString(R.string.applied));
                                        isApplied = true;
                                        appliedCode = edtPromoCode.getText().toString();
                                        tvPCAmount.setVisibility(View.VISIBLE);
                                        tvPromoCode.setVisibility(View.VISIBLE);
                                        dCharge = tvDeliveryCharge.getText().toString().equalsIgnoreCase("free") ? 0.0 : Constant.SETTING_DELIVERY_CHARGE;
                                        subtotal = (Double.parseDouble(object.getString(Constant.DISCOUNTED_AMOUNT)) + taxAmt + dCharge);
                                        pCodeDiscount = (Double.parseDouble(object.getString(Constant.DISCOUNT)));
                                        tvPCAmount.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + pCodeDiscount);
                                        tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + subtotal);
                                    } else {
                                        btnApply.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                                        btnApply.setText(getString(R.string.apply));
                                        tvAlert.setVisibility(View.VISIBLE);
                                        tvAlert.setText(object.getString("message"));
                                    }
                                    pBar.setVisibility(View.GONE);
                                    btnApply.setVisibility(View.VISIBLE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, CheckoutActivity_2.this, Constant.PROMO_CODE_CHECK_URL, params, false);

                }
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null)
            paymentModelClass.TrasactionMethod(data, CheckoutActivity_2.this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        /*if (ContextCompat.checkSelfPermission(CheckoutActivity_2.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.location_permission))
                        .setMessage(getString(R.string.location_permission_message))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(CheckoutActivity_2.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        0);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
            }
        }


        final GoogleMap mMap = googleMap;
        mMap.clear();
        LatLng latLng;
        if(session.getData(Session.KEY_LATITUDE).equalsIgnoreCase("0.0"))
        {
            latLng = new LatLng(Double.parseDouble("26.29491372551038"), Double.parseDouble("73.04615241284515"));
        }
        else{
            latLng = new LatLng(Double.parseDouble(session.getData(Session.KEY_LATITUDE)), Double.parseDouble(session.getData(Session.KEY_LONGITUDE)));
        }

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location);
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).draggable(true).title(getString(R.string.current_location));
        marker.icon(icon);
        mMap.addMarker(marker);

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(getString(R.string.current_location)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(15f).tilt(60).build();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        */

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        callApi_fillAdd(makeurl_filldefultAdd());
        callApidefaultAdd(Constant.BASEPATH+Constant.GET_USERDEFULTADD);
        check_minamount();
        mapFragment.getMapAsync(this);

    }


    public void callApi_fillAdd(String url)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", session.getData(Session.KEY_id));
        params.put("areaId", session.getData(Constant.AREA_ID));

        ApiConfig.RequestToVolley_POST(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("====>" + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.has(Constant.SUCESS))
                        {
                            if(jsonObject.getInt(Constant.SUCESS) == 200)
                            {
                                if(jsonObject.getBoolean("noAddress_flag"))
                                {
                                    //no address save
                                    tvnoAddress.setText("Please Add Address");
                                    is_address_save=false;
                                }
                                else{
                                    //address is save
                                    tvnoAddress.setText("No Address Save As Default Address Please Add Default Address");
                                    is_address_save=true;
                                }
                                if(jsonObject.getBoolean("defaultAddress_flag"))
                                {
                                    // no default address
                                    is_default_address_save=false;
                                }
                                else{
                                    //default address save
                                    is_default_address_save=true;
                                }

                            }
                            else{
                                tvnoAddress.setText("No Address Save Please Click");
                                is_address_save=false;
                                is_default_address_save=false;
                            }
                        }

                    } catch (JSONException e) {
                        tvnoAddress.setText("No Address Save Please Click");
                        is_address_save=false;
                        is_default_address_save=false;
                        e.printStackTrace();
                    }
                }
            }
        }, activity, url, params, true);

    }

    public String makeurl_filldefultAdd() {
        String url = Constant.BASEPATH + Constant.GET_CHECKADDRESS ;
        Log.d("url", url);
        return url;
    }

    private void callApidefaultAdd(String url) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", session.getData(Session.KEY_id));
        ApiConfig.RequestToVolley_POST(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        //System.out.println("====res area=>" + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.has(Constant.SUCESS))
                        {
                            if (jsonObject.getInt(Constant.SUCESS) == 200)
                            {
                                //fill  Default address
                                JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                                if(jsonObject_data.length() > 0)
                                {
                                    //fill Default Address
                                    linear_view.setVisibility(View.VISIBLE);
                                    tvnoAddress.setVisibility(View.GONE);
                                    linear_adtype.setVisibility(View.GONE);
                                    tvPlaceOrder.setEnabled(true);
                                    tvPlaceOrder.setBackground(ctx.getResources().getDrawable(R.drawable.process_bg));

                                    JSONObject jsonObject_address = jsonObject_data.getJSONObject("address");
                                    address_id = jsonObject_address.getString("_id");
                                    tvName.setVisibility(View.VISIBLE);
                                    tvCity.setVisibility(View.VISIBLE);
                                    imgedit.setVisibility(View.VISIBLE);
                                    Log.d("last name", session.getData(Session.KEY_LASTNAME));
                                    Log.d("first name", session.getData(Session.KEY_LASTNAME));


                                    String send_address_param_2 = "Address:"+" "+jsonObject_data.getString("complete_address")+"<br>"+
                                            "State:"+" "+session.getData(Constant.STATE_N)+"<br>"+
                                            "City:"+" "+session.getData(Constant.CITY_N)+"<br>"+
                                            "Area:"+" "+session.getData(Constant.AREA_N)+"<br>"+
                                            "Mobile:"+" " + session.getData(Session.KEY_mobile)+"<br>"+
                                            "Deliver to :"+" "+session.getData(Session.KEY_FIRSTNAME)+" "+session.getData(Session.KEY_LASTNAME);


                                    tvName.setText(session.getData(Session.KEY_FIRSTNAME)+" "+session.getData(Session.KEY_LASTNAME));
                                    tvCity.setText(Html.fromHtml(send_address_param_2));

                                    send_address_param = "Address:"+" "+jsonObject_data.getString("complete_address")+"\n"+
                                            "State:"+" "+session.getData(Constant.STATE_N)+"\n"+
                                            "City:"+" "+session.getData(Constant.CITY_N)+"\n"+
                                            "Area:"+" "+session.getData(Constant.AREA_N)+"\n"+
                                            "Mobile:"+" " + session.getData(Session.KEY_mobile)+"\n"+
                                            "Deliver to :"+" "+tvName.getText();

                                    //send_address_param = jsonObject_data.getString("complete_address");
                                    int address_type = jsonObject_address.getInt("address_type");
                                    if(address_type == 1)
                                    {
                                      txt_default_add.setText("Home Default Address");
                                    }
                                    else if(address_type == 2)
                                    {
                                      txt_default_add.setText("Work Default Address");
                                    }
                                    else if(address_type == 3)
                                    {
                                     txt_default_add.setText("Other Default Address");
                                    }
                                }

                                if(subtotal >= Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY)
                                {
                                    tvConfirmOrder.setEnabled(true);
                                    tvConfirmOrder.setBackground(ctx.getResources().getDrawable(R.drawable.confirm_bg));

                                }
                                else{
                                    tvConfirmOrder.setEnabled(false);
                                    tvConfirmOrder.setBackground(ctx.getResources().getDrawable(R.drawable.gray_bg));

                                }



                            }
                            else{
                                tvnoAddress.setVisibility(View.VISIBLE);
                                linear_view.setVisibility(View.GONE);
                                tvName.setVisibility(View.GONE);
                                tvCity.setVisibility(View.GONE);
                                linear_adtype.setVisibility(View.GONE);
                                imgedit.setVisibility(View.GONE);

                                tvConfirmOrder.setEnabled(false);
                                tvConfirmOrder.setBackgroundColor(ctx.getResources().getColor(R.color.gray));

                                Toast.makeText(ctx, Constant.NODEFAULT_ADD, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            tvnoAddress.setVisibility(View.VISIBLE);
                            linear_view.setVisibility(View.GONE);
                            tvName.setVisibility(View.GONE);
                            tvCity.setVisibility(View.GONE);
                            linear_adtype.setVisibility(View.GONE);
                            imgedit.setVisibility(View.GONE);

                            tvConfirmOrder.setEnabled(false);
                            tvConfirmOrder.setBackgroundColor(ctx.getResources().getColor(R.color.gray));
                            Toast.makeText(ctx, Constant.NODEFAULT_ADD, Toast.LENGTH_SHORT).show();

                        }


                    } catch (JSONException e) {
                        tvnoAddress.setVisibility(View.VISIBLE);
                        linear_view.setVisibility(View.GONE);
                        tvName.setVisibility(View.GONE);
                        tvCity.setVisibility(View.GONE);
                        linear_adtype.setVisibility(View.GONE);
                        imgedit.setVisibility(View.GONE);



                        tvConfirmOrder.setEnabled(false);
                        tvConfirmOrder.setBackgroundColor(ctx.getResources().getColor(R.color.gray));

                        Toast.makeText(ctx, Constant.NODEFAULT_ADD, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        }, activity, url, params, false);

    }

    @Override
    public void onBackPressed() {
        if (paymentLyt.getVisibility() == View.VISIBLE) {
            walletUncheck();
            tvPayment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
            tvPayment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process_gray, 0, 0, 0);
            tvDelivery.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            tvDelivery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process, 0, 0, 0);
            tvConfirmOrder.setVisibility(View.VISIBLE);
            tvPlaceOrder.setVisibility(View.GONE);
            paymentLyt.setVisibility(View.GONE);
            deliveryLyt.setVisibility(View.VISIBLE);
        } else
            super.onBackPressed();
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
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }


    public void GetTimeSlots_2()
    {
        try{
            String str_timeslot = session.getData(Constant.KEY_TIMESLOT);
            JSONArray jsonArray = new JSONArray(str_timeslot);
            slotList = new ArrayList<>();
            dayLyt.setVisibility(View.VISIBLE);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object1 = jsonArray.getJSONObject(i);
                slotList.add(new Slot(object1.getString("id"), object1.getString("title"), ""));
            }
            adapter = new SlotAdapter(slotList);
            recyclerView.setAdapter(adapter);
          }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }

    }


    public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.ViewHolder> {
        public ArrayList<Slot> categorylist;
        int selectedPosition = -1;

        public SlotAdapter(ArrayList<Slot> categorylist) {
            this.categorylist = categorylist;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_time_slot, parent, false);
            return new ViewHolder(view);
        }

        @NonNull
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            //holder.setIsRecyclable(false);
            final Slot model = categorylist.get(position);
            holder.rdBtn.setText(model.getTitle());
            holder.rdBtn.setTag(position);
            if(position == selectedPosition){
                holder.rdBtn.setChecked(position == selectedPosition);
            }
            else
            {
                holder.rdBtn.setChecked(false);
            }

            if (deliveryDay.equals(getString(R.string.tomorrow)))
            {
                model.setSlotAvailable(true);
                deliveryDay_val="2";
                //deliveryTime = model.getTitle();
            }
            if (model.isSlotAvailable()) {
                holder.rdBtn.setClickable(true);
                holder.rdBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

            } else {
                holder.rdBtn.setChecked(false);
                holder.rdBtn.setClickable(false);
                holder.rdBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
            }
            if (getTime().compareTo(slotList.get(slotList.size() - 1).getLastOrderTime()) > 0) {
                rToday.setClickable(false);
                rToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
            } else {
                rToday.setClickable(true);
                rToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            }
            System.out.println("======time slote valdation " + getTime().compareTo(slotList.get(slotList.size() - 1).getLastOrderTime()));
            rToday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getTime().compareTo(slotList.get(slotList.size() - 1).getLastOrderTime()) > 0) {
                        rToday.setClickable(false);
                        rToday.setChecked(false);
                        rToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                    } else {
                        rToday.setChecked(true);
                        rTomorrow.setChecked(false);
                        deliveryDay = getString(R.string.today);
                        deliveryDay_val="1";
                        for (Slot s : slotList) {
                            if (getTime().compareTo(s.getLastOrderTime()) > 0) {
                                s.setSlotAvailable(false);
                            } else
                                s.setSlotAvailable(true);
                        }
                        notifyDataSetChanged();
                    }
                }
            });

            rTomorrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deliveryDay = getString(R.string.tomorrow);
                    deliveryDay_val="2";
                    rToday.setChecked(false);
                    rTomorrow.setChecked(true);
                    notifyDataSetChanged();
                }

            });
            holder.rdBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deliveryTime_id = model.getId();
                    deliveryTime = model.getTitle();
                    selectedPosition = (Integer) v.getTag();
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return categorylist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RadioButton rdBtn;
            public ViewHolder(View itemView) {
                super(itemView);
                rdBtn = itemView.findViewById(R.id.rdBtn);
            }

        }
    }


    private void check_minamount()
    {
        if(subtotal >= Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY)
        {
            tvConfirmOrder.setBackgroundResource(R.drawable.confirm_bg);
            tvConfirmOrder.setEnabled(true);
        }
        else{
            tvConfirmOrder.setBackgroundResource(R.drawable.gray_bg);
            tvConfirmOrder.setEnabled(false);

        }
    }

}
