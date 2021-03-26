package com.ifresh.customer.activity;

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

import com.ifresh.customer.R;
import com.ifresh.customer.adapter.CategoryAdapter;
import com.ifresh.customer.adapter.DefaultAddressAdapter;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.DatabaseHelper;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.StorePrefrence;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.kotlin.FillAddress;
import com.ifresh.customer.model.Category;
import com.ifresh.customer.model.Default_Add_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.ifresh.customer.helper.Constant.ADDRESS_DEFAULT_CHANGE_MSG;
import static com.ifresh.customer.helper.Constant.ADDRESS_DELETE_MSG;
import static com.ifresh.customer.helper.Constant.AREA_ID;
import static com.ifresh.customer.helper.Constant.AREA_N;
import static com.ifresh.customer.helper.Constant.BASEPATH;
import static com.ifresh.customer.helper.Constant.CITY_ID;
import static com.ifresh.customer.helper.Constant.CITY_N;

import static com.ifresh.customer.helper.Constant.GETFRENCHISE;
import static com.ifresh.customer.helper.Constant.SUBAREA_ID;
import static com.ifresh.customer.helper.Constant.SUBAREA_N;
import static com.ifresh.customer.helper.Session.KEY_id;

public class SetDefaultAddress_2 extends AppCompatActivity {
    Context mContext = SetDefaultAddress_2.this;
    Activity activity = SetDefaultAddress_2.this;
    ArrayList<Default_Add_model> default_add_models_list;
    Session session;
    StorePrefrence storeinfo;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ProgressBar progressbar;

    LinearLayout msgView;
    DatabaseHelper databaseHelper;
    private Menu menu;
    String city_id="",area_id="",subarea_id="";
    String franchiseId="";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresstype_2);
        session=new Session(mContext);
        storeinfo = new StorePrefrence(mContext);
        databaseHelper= new DatabaseHelper(mContext);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.defaultaddress));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        msgView = findViewById(R.id.msgView);

        progressbar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SetDefaultAddress_2.this));
    }

    public String makeurl_filldefultAdd() {
        String url = Constant.BASEPATH + Constant.GET_DEFULTADD + session.getData(Session.KEY_id);
        Log.d("url", url);
        return url;
    }


    public void callApi_fillAdd(String url, final Boolean is_gotocart, final String area_id_get) {
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

                                        if(jsonObject1.getBoolean("default_address"))
                                        {
                                            /*if(!area_id_get.equals(jsonObject1.getString("areaId")))
                                            {
                                                city_id = jsonObject1.getString("cityId");
                                                area_id = jsonObject1.getString("areaId");
                                                subarea_id = jsonObject1.getString("sub_areaId");
                                            }*/
                                            city_id = jsonObject1.getString("cityId");
                                            area_id = jsonObject1.getString("areaId");
                                            subarea_id = jsonObject1.getString("sub_areaId");
                                        }

                                        default_add_models_list.add(default_add_model);
                                    }
                                    progressbar.setVisibility(View.GONE);
                                    DefaultAddressAdapter defaultAddressAdapter = new DefaultAddressAdapter(activity, mContext ,default_add_models_list, session);
                                    recyclerView.setAdapter(defaultAddressAdapter);
                                    if(is_gotocart)
                                    {
                                        try{
                                          if(databaseHelper.getTotalCartAmt(session) > 0)
                                          {
                                              Log.d("area_id_get",area_id_get);
                                              Log.d("AREA_ID",session.getData(AREA_ID));

                                              //call frenchise api
                                              GetFrenchise_id(area_id_get);
                                          }
                                          else{
                                              //else change location
                                              call_city_api(storeinfo.getString("state_id"), city_id);
                                          }
                                        }
                                        catch (Exception ex)
                                        {
                                            ex.printStackTrace();
                                        }

                                    }

                                }
                                else{
                                    progressbar.setVisibility(View.GONE);
                                    msgView.setVisibility(View.VISIBLE);

                                }
                            }
                            else
                            {
                                progressbar.setVisibility(View.GONE);
                                msgView.setVisibility(View.VISIBLE);

                            }
                        }
                        else{
                            progressbar.setVisibility(View.GONE);
                            msgView.setVisibility(View.VISIBLE);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, url, params, true);

    }

    private void call_city_api(String state_id, final String city_id) {
        Log.d("city_id",city_id);

        Map<String, String> params = new HashMap<String, String>();

        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("====res area " + response);
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for(int i=0; i<jsonArray.length();i++)
                        {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            if(obj.getString("_id").equalsIgnoreCase(city_id))
                            {
                                String city_name = obj.getString("title");
                                String city_id = obj.getString("_id");
                                Log.d("CITY_N",city_name);//Log.d("CITY_ID",city_id);
                                session.setData(CITY_ID, city_id);
                                session.setData(CITY_N, city_name);

                                call_area_api(city_id, area_id);
                                break;
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_CITY + state_id, params, false);



    }

    private void call_area_api(String city_id, final String area_id) {

        Map<String, String> params = new HashMap<String, String>();

        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("====res area " + response);
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for(int i=0; i<jsonArray.length();i++)
                        {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            if(obj.getString("_id").equalsIgnoreCase(area_id))
                            {
                                String area_name = obj.getString("title");
                                String area_id = obj.getString("_id");
                                session.setData(AREA_ID, area_id);
                                session.setData(AREA_N, area_name);

                                session.setBoolean("area_change", true);
                                storeinfo.setBoolean("is_locchange",true);

                                Log.d("Area_name",area_name);
                                //call_subarea_api(area_id, subarea_id);
                                break;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_AREA + city_id, params, false);



    }

    /*private void call_subarea_api(String areaId, final String subarea_id)
    {
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("====res area " + response);
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for(int i=0; i<jsonArray.length();i++)
                        {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            if(obj.getString("_id").equalsIgnoreCase(subarea_id))
                            {
                                String subarea_name = obj.getString("title");
                                String subarea_id = obj.getString("_id");
                                session.setData(SUBAREA_ID, subarea_id);
                                session.setData(SUBAREA_N, subarea_name);

                                Log.d("subarea_name",subarea_name);
                                //Area is Changed Now
                                session.setBoolean("area_change", true);
                                storeinfo.setBoolean("is_locchange",true);
                                break;
                            }
                            else{

                            }
                        }




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_SUBAREA + areaId, params, false);
   }*/


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
        txt_msg.setText(activity.getResources().getString(R.string.delete_add));

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
        alertDialog.setCancelable(false);
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

                call_city_api(storeinfo.getString("state_id"), city_id);

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
        intent.putExtra("userId","");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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


    private String GetFrenchise_id(String Area_ID) {
        String FrenchiseUrl = BASEPATH + GETFRENCHISE + Area_ID;
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                //System.out.println("frenchise==>" + response);
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
                                franchiseId = jsonObject.getString("franchiseId");
                                //Log.d("save_id1", storeinfo.getString("franch"));
                                //Log.d("get_frenchiseid", franchiseId);

                                //method
                                if(storeinfo.getString("franch").equalsIgnoreCase(franchiseId))
                                {
                                    if(session.getBoolean("is_upload"))
                                    {
                                        session.setBoolean("is_upload", false);
                                        Intent intent = new Intent(mContext,UploadMedicine.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        Intent intent = new Intent(mContext,CheckoutActivity_2.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                    }
                                    finish();
                                }

                                else{
                                    //show alert view if franchise is different from current franchise
                                    GoToCheckout_Alert();
                                }

                            }
                        }
                  } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }, SetDefaultAddress_2.this, FrenchiseUrl, params, true);
        return franchiseId;

    }


}
