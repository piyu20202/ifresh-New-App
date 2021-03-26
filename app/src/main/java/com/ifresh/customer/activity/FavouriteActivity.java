package com.ifresh.customer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ifresh.customer.R;
import com.ifresh.customer.adapter.ProductAdapter;
import com.ifresh.customer.adapter.ProductListAdapter_2;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.DatabaseHelper;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.model.Mesurrment;
import com.ifresh.customer.model.ModelProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.ifresh.customer.helper.Constant.BASEPATH;
import static com.ifresh.customer.helper.Constant.GET_GETPRODUCTBYID;


public class FavouriteActivity extends AppCompatActivity {

    TextView txtnodata;
    RecyclerView favrecycleview;
    DatabaseHelper databaseHelper;
    ArrayList<ModelProduct> productArrayList;
    ProductAdapter productAdapter;
    Toolbar toolbar;
    //public RelativeLayout layoutSearch;
    String category_id;
    static Session session;
    Activity activity = FavouriteActivity.this;
    Context mContext =  FavouriteActivity.this;
    ArrayList<Mesurrment> measurement_list;
    ProgressBar progressbar;
    private Menu menu;
    double total;
    private int offset, filterIndex,position;
    String search_query="0", price="1", product_on="1";

    ProductListAdapter_2 productListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        session = new Session(mContext);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_fav));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressbar = findViewById(R.id.progressbar);
        txtnodata = findViewById(R.id.txtnodata);
        category_id = getIntent().getStringExtra(Constant.CAT_ID);

        favrecycleview = findViewById(R.id.favrecycleview);
        //layoutSearch = findViewById(R.id.layoutSearch);

        favrecycleview.setLayoutManager(new LinearLayoutManager(FavouriteActivity.this));
        databaseHelper = new DatabaseHelper(FavouriteActivity.this);

        /*layoutSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                *//*startActivity(new Intent(FavouriteActivity.this, SearchActivity_2.class)
                        .putExtra("from", Constant.FROMSEARCH)
                        .putExtra("cat_id", category_id)
                        .putExtra("area_id", category_id)
                );*//*
                startActivity(new Intent(FavouriteActivity.this, SearchActivity_2.class)
                        .putExtra("from", Constant.FROMSEARCH)
                        .putExtra("arraylist", productArrayList));
            }
        });*/

    }


    private void getData()
    {
        progressbar.setVisibility(View.VISIBLE);
        productArrayList = new ArrayList<>();
        final ArrayList<String> idslist = databaseHelper.getFavourite();

        if (idslist.isEmpty())
        {
            txtnodata.setVisibility(View.VISIBLE);
            progressbar.setVisibility(View.GONE);
            //favrecycleview.setAdapter(new ProductListAdapter_2(mContext, productArrayList, activity));
        }
        else{
            for (final String id : idslist)
            {
                final String[] ids = id.split("=");
                Map<String, String> params = new HashMap<String, String>();
                String prod_id= ids[0];
                String frprod_id=ids[1];
                String frprod_vid=ids[2];

                String get_param =  BASEPATH + GET_GETPRODUCTBYID +"/"+prod_id+"/"+frprod_id+"/"+frprod_vid;
                Log.d("url_send", get_param);
                ApiConfig.RequestToVolley_GET(new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean result, String response) {
                        System.out.println("favorites" + response );
                        if (result)
                        {
                            try {
                                JSONObject objectbject = new JSONObject(response);
                                if (objectbject.getInt(Constant.SUCESS) == 200)
                                {
                                    favrecycleview.setVisibility(View.VISIBLE);
                                    txtnodata.setVisibility(View.GONE);


                                    JSONObject object = new JSONObject(response);
                                    JSONArray data_arr = object.getJSONArray("data");
                                    JSONObject data_obj = data_arr.getJSONObject(0);
                                    JSONArray jsonArray_products = data_obj.getJSONArray("products");

                                    productArrayList.add(ApiConfig.GetProductList_2(jsonArray_products, measurement_list).get(0));
                                    productListAdapter = new ProductListAdapter_2(mContext, productArrayList,activity,session);
                                    favrecycleview.setAdapter(productListAdapter);
                                    progressbar.setVisibility(View.GONE);

                               } else {
                                  txtnodata.setVisibility(View.VISIBLE);
                                  favrecycleview.setVisibility(View.GONE);
                                  progressbar.setVisibility(View.GONE);

                                }


                            } catch (JSONException e) {
                                txtnodata.setVisibility(View.VISIBLE);
                                favrecycleview.setVisibility(View.GONE);
                                progressbar.setVisibility(View.GONE);

                                e.printStackTrace();
                            }
                        }
                    }
                }, FavouriteActivity.this, get_param, params , false);


            }


        }
    }

    private void callSettingApiMessurment() {
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

    @Override
    public void onResume() {
        super.onResume();
        if (productAdapter != null && !Constant.SELECTEDPRODUCT_POS.equals("")) {
            if (!databaseHelper.getFavouriteById(Constant.SELECTEDPRODUCT_POS.split("=")[1])) {
                productArrayList.remove(Integer.parseInt(Constant.SELECTEDPRODUCT_POS.split("=")[0]));
                productAdapter.notifyItemRemoved(Integer.parseInt(Constant.SELECTEDPRODUCT_POS.split("=")[0]));
                Constant.SELECTEDPRODUCT_POS = "";
            }
        }

        callSettingApiMessurment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.menu_search:
                if( productArrayList != null && productArrayList.size() > 0){
                    startActivity(new Intent(FavouriteActivity.this, SearchActivity_2.class)
                            .putExtra("from", Constant.FROMSEARCH)
                            .putExtra("arraylist", productArrayList)
                    );
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
                if( productArrayList != null && productArrayList.size() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FavouriteActivity.this);
                    builder.setTitle(FavouriteActivity.this.getResources().getString(R.string.filterby));
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
                                            productListAdapter.notifyDataSetChanged();
                                        }
                                    }, 1000);


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
                                            productListAdapter.notifyDataSetChanged();
                                        }
                                    }, 1000);
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
                                            productListAdapter.notifyDataSetChanged();
                                        }
                                    }, 1000);
                                    break;
                                case 3:
                                    price = Constant.PRICE_L_H;
                                    Collections.sort(productArrayList, ModelProduct.compareByPriceVariations);
                                    progressbar.setVisibility(View.VISIBLE);
                                    handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Do something after 100ms
                                            progressbar.setVisibility(View.GONE);
                                            productListAdapter.notifyDataSetChanged();
                                        }
                                    }, 1000);
                                    break;
                            }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        /* if (from.equals("section"))*/
        menu.findItem(R.id.menu_sort).setVisible(true);
        menu.findItem(R.id.menu_search).setVisible(true);
        menu.findItem(R.id.menu_cart).setVisible(true);

        menu.findItem(R.id.menu_cart).setIcon(ApiConfig.buildCounterDrawable(databaseHelper.getTotalItemOfCart(), R.drawable.ic_cart, FavouriteActivity.this));
        return super.onPrepareOptionsMenu(menu);
    }

}
