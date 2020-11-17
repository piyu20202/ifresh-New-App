package com.ifresh.customerr.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ifresh.customerr.R;
import com.ifresh.customerr.adapter.DefaultAddressAdapter;
import com.ifresh.customerr.helper.ApiConfig;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.helper.DatabaseHelper;
import com.ifresh.customerr.helper.Session;
import com.ifresh.customerr.helper.VolleyCallback;
import com.ifresh.customerr.kotlin.FillAddress;
import com.ifresh.customerr.model.Default_Add_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.ifresh.customerr.helper.Constant.ADDRESS_DEFAULT_CHANGE_MSG;
import static com.ifresh.customerr.helper.Constant.ADDRESS_DELETE_MSG;
import static com.ifresh.customerr.helper.Constant.AREA_ID;
import static com.ifresh.customerr.helper.Constant.ISAREACHAGE;
import static com.ifresh.customerr.helper.Session.KEY_id;

public class SetDefaultAddress_2 extends AppCompatActivity {
    Context mContext = SetDefaultAddress_2.this;
    Activity activity = SetDefaultAddress_2.this;
    ArrayList<Default_Add_model> default_add_models_list;
    Session session;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ProgressBar progressbar;
    SwipeRefreshLayout swipeLayout;
    LinearLayout msgView;
    DatabaseHelper databaseHelper;
    private Menu menu;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresstype_2);
        session=new Session(mContext);
        databaseHelper= new DatabaseHelper(mContext);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.defaultaddress));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        msgView = findViewById(R.id.msgView);
        swipeLayout = findViewById(R.id.swipeLayout);
        progressbar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SetDefaultAddress_2.this));
    }

    public String makeurl_filldefultAdd() {
        String url = Constant.BASEPATH + Constant.GET_DEFULTADD + session.getData(Session.KEY_id);
        Log.d("url", url);
        return url;
    }


    public void callApi_fillAdd(String url, final Boolean is_gotocart, final String area_id) {
        progressbar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("====res area " + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.has(Constant.SUCESS))
                        {
                            if (jsonObject.getInt(Constant.SUCESS) == 200)
                            {
                                msgView.setVisibility(View.GONE);
                                swipeLayout.setVisibility(View.VISIBLE);

                                default_add_models_list = new ArrayList<>();
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                if(jsonArray.length() > 0)
                                {
                                    for(int i = 0; i<jsonArray.length(); i++)
                                    {
                                        JSONObject jsonObject1 =  jsonArray.getJSONObject(i);
                                        Default_Add_model default_add_model = new Default_Add_model();
                                        default_add_model.setAddress_id(jsonObject1.getString("_id"));
                                        default_add_model.setAddress_type(jsonObject1.getString("address_type"));
                                        default_add_model.setAddress1(jsonObject1.getString("address1"));
                                        default_add_model.setAddress2(jsonObject1.getString("address2"));
                                        default_add_model.setPincode(jsonObject1.getString("pincode"));
                                        default_add_model.setDefault_address(jsonObject1.getBoolean("default_address"));
                                        default_add_model.setArea_id(jsonObject1.getString("areaId"));

                                        default_add_models_list.add(default_add_model);
                                    }
                                    progressbar.setVisibility(View.GONE);
                                    DefaultAddressAdapter defaultAddressAdapter = new DefaultAddressAdapter(activity, mContext ,default_add_models_list, session);
                                    recyclerView.setAdapter(defaultAddressAdapter);

                                    if(is_gotocart)
                                    {
                                        if(databaseHelper.getTotalCartAmt(session) > 0)
                                        {
                                            if(session.getData(AREA_ID).equals(area_id))
                                            {
                                                Intent intent = new Intent(mContext,CheckoutActivity_2.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{
                                                //show alert view
                                                GoToCheckout_Alert();
                                            }
                                        }
                                    }

                                }
                                else{
                                    progressbar.setVisibility(View.GONE);
                                    msgView.setVisibility(View.VISIBLE);
                                    swipeLayout.setVisibility(View.GONE);
                                }
                            }
                            else
                            {
                                progressbar.setVisibility(View.GONE);
                                msgView.setVisibility(View.VISIBLE);
                                swipeLayout.setVisibility(View.GONE);
                            }
                        }
                        else{
                            progressbar.setVisibility(View.GONE);
                            msgView.setVisibility(View.VISIBLE);
                            swipeLayout.setVisibility(View.GONE);
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, url, params, true);

    }


    private void callApi_deleteadd(String address_id, final String area_id) {
        progressbar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        Log.d("val1", address_id);
        params.put("address_id", address_id);
        //params.put("user_id", session.getData(Session.KEY_id));

        ApiConfig.RequestToVolley_POST(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("====res area " + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt(Constant.SUCESS) == 200) {
                            progressbar.setVisibility(View.GONE);
                            Toast.makeText(activity, ADDRESS_DELETE_MSG, Toast.LENGTH_SHORT).show();
                            callApi_fillAdd(makeurl_filldefultAdd(),false,area_id);
                        } else {
                            Toast.makeText(activity, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BASEPATH + Constant.SET_DELETEADD, params, false);
    }


    public void callApi_updatedefultAdd(String address_id, final String chkstatus, final String area_id)
    {
        progressbar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        Log.d("val1", address_id);
        Log.d("val3", session.getData(Session.KEY_id));
        Log.d("chkstatus", chkstatus);
        params.put("address_id", address_id);
        params.put("default_address", chkstatus);
        params.put("user_id", session.getData(Session.KEY_id));

        ApiConfig.RequestToVolley_POST(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("====res area " + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getInt(Constant.SUCESS) == 200)
                        {
                            progressbar.setVisibility(View.GONE);
                            Toast.makeText(mContext, ADDRESS_DEFAULT_CHANGE_MSG, Toast.LENGTH_SHORT).show();
                            String url = makeurl_filldefultAdd();

                            Boolean isgotocart=false;
                            if(chkstatus.equalsIgnoreCase("true"))
                            {
                                isgotocart=true;
                            }
                            else if(chkstatus.equalsIgnoreCase("false"))
                            {
                                isgotocart=false;
                            }
                            callApi_fillAdd(url,isgotocart, area_id);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BASEPATH + Constant.SET_DEFULTADD, params, false);
    }


    public void ConformationView(final String address_id, final String area_id)
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.msg_view_4, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        final AlertDialog dialog = alertDialog.create();

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvremove,tvclose,txt_msg;

        tvremove = dialogView.findViewById(R.id.tvcancel);
        tvclose = dialogView.findViewById(R.id.tvclose);
        txt_msg = dialogView.findViewById(R.id.txt_msg);

        tvclose.setText("CANCEL");
        tvremove.setText("REMOVE");
        txt_msg.setText(activity.getResources().getString(R.string.deleteproductmsg));

        tvremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callApi_deleteadd(address_id,area_id);
            }
        });

        tvclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void GoToCheckout_Alert()
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.msg_view_7, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        final AlertDialog dialog = alertDialog.create();

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvcart,txt_msg;

        tvcart = dialogView.findViewById(R.id.tvcart);
        txt_msg = dialogView.findViewById(R.id.txt_msg);
        tvcart.setText("Delete Cart");
        txt_msg.setText(activity.getResources().getString(R.string.deleteproductmsg));

        tvcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                databaseHelper.DeleteAllOrderData();
                session.setBoolean(ISAREACHAGE, true);
                Intent intent = new Intent(mContext,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        callApi_fillAdd(makeurl_filldefultAdd(),false, session.getData(AREA_ID));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.home:
                onBackPressed();
                return true;

            case R.id.menu_add:
                openSetAddress();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSetAddress() {
        Intent intent = new Intent(SetDefaultAddress_2.this, FillAddress.class);
        intent.putExtra(KEY_id,session.getData(Session.KEY_id));
        startActivity(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_sort).setVisible(false);
        menu.findItem(R.id.menu_add).setVisible(true);
        menu.findItem(R.id.menu_cart).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }









}
