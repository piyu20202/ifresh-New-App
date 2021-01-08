package com.ifresh.customer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifresh.customer.R;
import com.ifresh.customer.adapter.MedicalListAdapter_2;
import com.ifresh.customer.adapter.SCategoryAdapter;
import com.ifresh.customer.adapter.SMedicalAdapter;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.DatabaseHelper;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.kotlin.SignInActivity_K;
import com.ifresh.customer.model.Mesurrment;
import com.ifresh.customer.model.ModelSCategory;
import com.ifresh.customer.model.ModelProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.ifresh.customer.helper.Constant.BASEPATH;
import static com.ifresh.customer.helper.Constant.GET_PRODUCTLIST;

public class MedicalListActivity_2 extends AppCompatActivity {

    private final Activity activity = MedicalListActivity_2.this;
    private final Context mContext = MedicalListActivity_2.this;
    private Session session;
    private DatabaseHelper databaseHelper;
    Toolbar toolbar;
    ProgressBar progressBar;
    private RecyclerView recycler_View_hor, recycler_View_ver;
    private RelativeLayout relative_header;
    private static ArrayList<ModelSCategory> arrayList_horizontal ;
    private static ArrayList<ModelProduct> arrayList_product;
    MedicalListAdapter_2 medicalListAdapter_2;
    SMedicalAdapter sMedicalAdapter;
    private String category_id;
    private int  filterIndex;
    String search_query="0", price="1", product_on="1";
    private LinearLayout nodata_view;
    ArrayList<Mesurrment> measurement_list;
    int count = 0;
    public static String cat_id_copy_activity;
    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    public  Boolean is_deafultAddExist=false;
    public  Boolean is_address_save=false;
    public  Boolean is_default_address_save=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(mContext);
        databaseHelper = new DatabaseHelper(MedicalListActivity_2.this);
        setContentView(R.layout.activity_madical_listing);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Medical Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressBar = findViewById(R.id.progressBar);

        recycler_View_hor = (RecyclerView) findViewById(R.id.recycler_View_hor);
        recycler_View_ver = (RecyclerView) findViewById(R.id.recycler_View_ver);
        relative_header = (RelativeLayout) findViewById(R.id.relative);


        nodata_view = (LinearLayout)findViewById(R.id.nodata_view);
        category_id = getIntent().getStringExtra("id");
        cat_id_copy_activity = category_id;

        callSettingApi_messurment();


        relative_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_chekuser();
            }
        });

    }

    private void callSettingApi_messurment()
    {
        try{
            /*if(session.getData(Constant.KEY_MEASUREMENT).length() == 0)
            {
                ApiConfig.GetMessurmentApi(activity, session);// to call measurement data
            }*/
            JSONArray jsonArray = new JSONArray(session.getData(Constant.KEY_MEASUREMENT));
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


        callApiMedicallist(category_id,true);

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

    //call medical listing url
    public void callApiMedicallist(final String category_id, final boolean is_callsubcat)
    {
        String MedicalListUrl = BASEPATH + GET_PRODUCTLIST + session.getData(Constant.AREA_ID) +"/"+ category_id ;
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("res======" + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);

                        if (object.getInt(Constant.SUCESS) == 200)
                        {
                            progressBar.setVisibility(View.GONE);
                            nodata_view.setVisibility(View.GONE);
                            recycler_View_ver.setVisibility(View.VISIBLE);
                            recycler_View_hor.setVisibility(View.VISIBLE);

                            arrayList_horizontal = new ArrayList<>();
                            arrayList_product =  new ArrayList<>();
                            arrayList_horizontal.clear();
                            arrayList_product.clear();
                            JSONArray  jsonArray = object.getJSONArray(Constant.DATA);
                            JSONObject Object_maincat = jsonArray.getJSONObject(0);
                            JSONObject jsonObject_maincat = Object_maincat.getJSONObject("mainCat");
                            JSONObject jsonObject_subcat = jsonArray.getJSONObject(1);
                            JSONArray  jsonArray_subcat = jsonObject_subcat.getJSONArray("subcat");
                            JSONObject jsonObject_products = jsonArray.getJSONObject(2);
                            JSONArray  jsonArray_products = jsonObject_products.getJSONArray("products");

                            //horizontal array list
                            if(is_callsubcat)
                            {
                                getSubcategoryData(jsonArray_subcat);
                            }

                            //call product list
                            if(jsonArray_products.length() > 0)
                            {
                                //call function
                                arrayList_product =ApiConfig.GetProductList_2(jsonArray_products, measurement_list);
                                if(arrayList_product.size() > 0)
                                {
                                    nodata_view.setVisibility(View.GONE);
                                    recycler_View_ver.setVisibility(View.VISIBLE);

                                    //call adapter to fill vertical product list
                                    callMedicalListAdapter();
                                }
                                else{
                                    //no data for product list
                                    nodata_view.setVisibility(View.VISIBLE);
                                    recycler_View_ver.setVisibility(View.GONE);
                                }
                            }
                            else{
                                // no data for product list
                                progressBar.setVisibility(View.GONE);
                                nodata_view.setVisibility(View.VISIBLE);
                                recycler_View_ver.setVisibility(View.GONE);

                                if(cat_id_copy_activity.equalsIgnoreCase(SCategoryAdapter.cat_id_adapter))
                                {
                                    ++count;
                                    if(count < 3)
                                        callApiMedicallist(category_id,false);
                                }
                                else{
                                    count=0;
                                    cat_id_copy_activity = SCategoryAdapter.cat_id_adapter;
                                    callApiMedicallist(category_id,false);
                                }

                            }

                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            nodata_view.setVisibility(View.VISIBLE);
                            recycler_View_ver.setVisibility(View.GONE);
                            recycler_View_hor.setVisibility(View.GONE);
                            count=0;

                            Toast.makeText(mContext, "No Data", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        nodata_view.setVisibility(View.VISIBLE);
                        recycler_View_ver.setVisibility(View.GONE);
                        recycler_View_hor.setVisibility(View.GONE);

                        Toast.makeText(mContext, "No Data", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        }, MedicalListActivity_2.this, MedicalListUrl, params, true);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(medicalListAdapter_2 != null)
        {
            medicalListAdapter_2.notifyDataSetChanged();
            if (!Constant.SELECTEDPRODUCT_POS.equals(""))
                if (!databaseHelper.getFavouriteById(Constant.SELECTEDPRODUCT_POS.split("=")[1])) {
                    medicalListAdapter_2.notifyItemChanged(Integer.parseInt(Constant.SELECTEDPRODUCT_POS.split("=")[0]));
                    Constant.SELECTEDPRODUCT_POS = "";
                }
        }
        invalidateOptionsMenu();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_sort).setVisible(true);
        menu.findItem(R.id.menu_search).setVisible(true);
        menu.findItem(R.id.menu_cart).setIcon(ApiConfig.buildCounterDrawable(databaseHelper.getTotalItemOfCart(), R.drawable.ic_cart, MedicalListActivity_2.this));
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
            case R.id.menu_search:
                if(arrayList_product.size() > 0)
                {
                    startActivity(new Intent(MedicalListActivity_2.this, SearchActivity_2.class)
                            .putExtra("from", Constant.FROMSEARCH)
                            .putExtra("arraylist", arrayList_product));
                }
                else{
                    Toast.makeText(mContext, "NO DATA", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.menu_cart:
                Intent intent  = new Intent(getApplicationContext(), CartActivity_2.class);
                intent.putExtra("id", category_id);
                startActivity(intent);
                return true;

            case R.id.menu_sort:
                if(arrayList_product.size() > 0)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MedicalListActivity_2.this);
                    builder.setTitle(MedicalListActivity_2.this.getResources().getString(R.string.filterby));
                    builder.setSingleChoiceItems(Constant.filtervalues, filterIndex, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            filterIndex = item;
                            switch (item) {
                                case 0:
                                    product_on = Constant.PRODUCT_N_O;
                                    Collections.sort(arrayList_product, ModelProduct.compareByATOZ);
                                    progressBar.setVisibility(View.VISIBLE);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Do something after 100ms
                                            progressBar.setVisibility(View.GONE);
                                            medicalListAdapter_2.notifyDataSetChanged();
                                        }
                                    }, 2000);
                                    break;
                                case 1:
                                    product_on = Constant.PRODUCT_O_N;
                                    Collections.sort(arrayList_product, ModelProduct.compareByZTOA);
                                    progressBar.setVisibility(View.VISIBLE);
                                    handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Do something after 100ms
                                            progressBar.setVisibility(View.GONE);
                                            medicalListAdapter_2.notifyDataSetChanged();
                                        }
                                    }, 2000);
                                    break;
                                case 2:
                                    price = Constant.PRICE_H_L;
                                    Collections.sort(arrayList_product, ModelProduct.compareByPriceVariations_1);
                                    progressBar.setVisibility(View.VISIBLE);
                                    handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Do something after 100ms
                                            progressBar.setVisibility(View.GONE);
                                            medicalListAdapter_2.notifyDataSetChanged();
                                        }
                                    }, 2000);
                                    break;
                                case 3:
                                    price = Constant.PRICE_L_H;
                                    Collections.sort(arrayList_product,ModelProduct.compareByPriceVariations);
                                    progressBar.setVisibility(View.VISIBLE);
                                    handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Do something after 100ms
                                            progressBar.setVisibility(View.GONE);
                                            medicalListAdapter_2.notifyDataSetChanged();
                                        }
                                    }, 2000);
                                    break;
                            }
                        /*if (item != -1)
                            //ReLoadData();
                            callApiProductlist(category_id,false);*/
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else{
                    Toast.makeText(mContext, "NO DATA", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return onOptionsItemSelected(item);
    }

    private void getSubcategoryData(JSONArray  jsonArray_subcat) {
        try{
            if(jsonArray_subcat.length() > 0)
            {
                ModelSCategory horizontal_subCategory = new ModelSCategory();
                horizontal_subCategory.setId(category_id);
                horizontal_subCategory.setTitle("All Category");
                horizontal_subCategory.setCatagory_img(Constant.CATEGORYIMAGEPATH + "allcategory.png");
                horizontal_subCategory.setIs_active("1");
                arrayList_horizontal.add(horizontal_subCategory);

                for(int i = 0; i< jsonArray_subcat.length(); i++ )
                {
                    JSONObject mjson_obj = jsonArray_subcat.getJSONObject(i);
                    if(mjson_obj.getString("is_active").equals("1"))
                    {
                        horizontal_subCategory = new ModelSCategory();
                        horizontal_subCategory.setId(mjson_obj.getString("_id"));
                        horizontal_subCategory.setTitle(mjson_obj.getString("title"));
                        horizontal_subCategory.setCatagory_img(Constant.CATEGORYIMAGEPATH + mjson_obj.getString("catagory_img"));
                        horizontal_subCategory.setIs_active(mjson_obj.getString("is_active"));
                        arrayList_horizontal.add(horizontal_subCategory);
                    }

                }
                callSubcategoryListAdapter();
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void callMedicalListAdapter()
    {
        medicalListAdapter_2 = new MedicalListAdapter_2(mContext, arrayList_product,activity,session);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recycler_View_ver.setLayoutManager(verticalLayoutManager);
        recycler_View_ver.setItemViewCacheSize(20);
        recycler_View_ver.setDrawingCacheEnabled(true);
        recycler_View_ver.setAdapter(medicalListAdapter_2);
    }

    private void callSubcategoryListAdapter()
    {
        Log.d("len",""+ arrayList_horizontal.size());
        sMedicalAdapter = new SMedicalAdapter(arrayList_horizontal,mContext);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recycler_View_hor.setLayoutManager(horizontalLayoutManager);
        recycler_View_hor.setAdapter(sMedicalAdapter);
    }

    private void call_chekuser() {
        if (session.isUserLoggedIn())
        {
            callApi_fillAdd();
        }
        else{
            Toast.makeText(activity, "Please Login or Register", Toast.LENGTH_SHORT).show();
            Intent intent  = new Intent(activity, SignInActivity_K.class);
            activity.startActivity(intent);
        }
    }


    public void call_view()
    {
        Intent intent;
        /*if(is_address_save)
        {
            //address save
            if(is_deafultAddExist)
            {
                session.setBoolean("is_upload", true);
                intent = new Intent(activity, UploadMedicine.class);
            }
            else{
                //default address not exist
                intent = new Intent(activity, SetDefaultAddress_2.class);
            }
        }
        else{
            //address not save
            session.setBoolean("is_upload", true);
            intent = new Intent(activity, FillAddress.class);
            intent.putExtra("userId", session.getData(session.KEY_id));
        }*/
        if(is_deafultAddExist)
        {
            session.setBoolean("is_upload", true);
            intent = new Intent(activity, UploadMedicine.class);
        }
        else{
            //default address not exist
            intent = new Intent(activity, SetDefaultAddress_2.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }


    public void callApi_fillAdd()
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
                                    is_address_save=false;
                                }
                                else{
                                    //address is save
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
                                is_address_save=false;
                                is_default_address_save=false;
                            }

                            callApidefaultAdd();
                        }

                    } catch (JSONException e) {
                        is_address_save=false;
                        is_default_address_save=false;
                        callApidefaultAdd();
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_CHECKADDRESS, params, true);

    }


    public Boolean callApidefaultAdd()
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", session.getData(Session.KEY_id));
        ApiConfig.RequestToVolley_POST(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("====res area=>" + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.has(Constant.SUCESS))
                        {
                            if (jsonObject.getInt(Constant.SUCESS) == 200)
                            {
                                JSONObject data_obj = jsonObject.getJSONObject("data");
                                JSONObject address_obj = data_obj.getJSONObject("address");
                                Boolean default_address = address_obj.getBoolean("default_address");

                                if(default_address)
                                {
                                    Log.d("val", "true");
                                    is_deafultAddExist = true;
                                }
                                else{
                                    Log.d("val", "false");
                                    is_deafultAddExist = false;
                                }
                            }
                            else{
                                is_deafultAddExist = false;
                                Toast.makeText(activity, Constant.NODEFAULT_ADD, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            is_deafultAddExist = false;
                            Toast.makeText(activity, Constant.NODEFAULT_ADD, Toast.LENGTH_SHORT).show();

                        }
                        call_view();

                    } catch (JSONException e) {
                        is_deafultAddExist = false;
                        Toast.makeText(activity, Constant.NODEFAULT_ADD, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BASEPATH+Constant.GET_USERDEFULTADD, params, false);
        return is_deafultAddExist;
    }




}
