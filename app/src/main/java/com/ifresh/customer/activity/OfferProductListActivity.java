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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifresh.customer.R;
import com.ifresh.customer.adapter.ProductListAdapter_2;
import com.ifresh.customer.adapter.SCategoryAdapter;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.DatabaseHelper;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.model.Mesurrment;
import com.ifresh.customer.model.ModelProduct;
import com.ifresh.customer.model.ModelSCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.ifresh.customer.helper.Constant.BASEPATH;
import static com.ifresh.customer.helper.Constant.GET_OFFERPRODUCT;

public class OfferProductListActivity extends AppCompatActivity {

    private Activity activity = OfferProductListActivity.this;
    private Context mContext = OfferProductListActivity.this;
    private Session session;
    private DatabaseHelper databaseHelper;
    Toolbar toolbar;
    ProgressBar progressBar;

    private RecyclerView recycler_View_hor, recycler_View_ver;
    private static ArrayList<ModelSCategory> arrayList_horizontal ;
    public static ArrayList<ModelProduct> arrayList_product;
    ProductListAdapter_2 productListAdapter;
    SCategoryAdapter sCategoryAdapter;

    private String offer_id, area_id, from,name;
    private int offset, filterIndex,position;
    int total;
    String search_query="0", price="1", product_on="1";

    //private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView tvAlert;
    private Menu menu;
    private LinearLayout nodata_view;
    //NestedScrollView nestedScrollView;
    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    LinearLayoutManager verticalLayoutManager;
    private boolean isLoadMore = false;
    ArrayList<Mesurrment> measurement_list;
    JSONArray cofig_jsonarr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(mContext);
        databaseHelper = new DatabaseHelper(OfferProductListActivity.this);
        setContentView(R.layout.activity_product_listing);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvAlert = findViewById(R.id.txtnodata);
        progressBar = findViewById(R.id.progressBar);
        recycler_View_hor =  findViewById(R.id.recycler_View_hor);
        recycler_View_ver =  findViewById(R.id.recycler_View_ver);
        nodata_view = findViewById(R.id.nodata_view);


        offer_id = getIntent().getStringExtra("offer_id");
        from = getIntent().getStringExtra("from");
        name = getIntent().getStringExtra("name");

        position = getIntent().getIntExtra("position", 0);


        //Log.d("offer_id", offer_id);
        //ApiConfig.GetSettingConfigApi(activity, session);// to call measurement data

        callSettingApi_messurment();
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
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object1 = jsonArray.getJSONObject(i);
                measurement_list.add(new Mesurrment(object1.getString("id"), object1.getString("title"), object1.getString("abv")));
            }
        }
        catch (Exception ex)
        {
           ex.printStackTrace();
        }

        if(from.equalsIgnoreCase("regular"))
        {
            getSupportActionBar().setTitle("Offer Product");
            arrayList_product =  new ArrayList<>();
            arrayList_product.clear();
            callApiProductlist(offer_id,true);
        }
        else if(from.equalsIgnoreCase("section"))
        {
                    getSupportActionBar().setTitle("Feature Product");
                    arrayList_product =  new ArrayList<>();
                    recycler_View_hor.setVisibility(View.GONE);
                    arrayList_product = MainActivity.sectionList.get(position).getProductList();

                    // To Show Header in list view
                    //ProductListActivity_2.is_footer_show=false;

                    productListAdapter = new ProductListAdapter_2(mContext, arrayList_product, activity,session);
                    LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                    recycler_View_ver.setLayoutManager(verticalLayoutManager);
                    recycler_View_ver.setAdapter(productListAdapter);

        }
    }

    //call product listing url
    public void callApiProductlist(String offer_id, final boolean is_callsubcat)
    {
        String OfferListUrl = BASEPATH + GET_OFFERPRODUCT +  offer_id ;
        progressBar.setVisibility(View.VISIBLE);
        Log.d("OfferListUrl",OfferListUrl);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("res======" + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        arrayList_horizontal = new ArrayList<>();
                        arrayList_product =  new ArrayList<>();

                        if(object.has(Constant.SUCESS))
                        {
                            if (object.getInt(Constant.SUCESS) == 200)
                            {
                                progressBar.setVisibility(View.GONE);

                                //arrayList_horizontal.clear();
                                //arrayList_product.clear();

                                JSONArray  jsonArray = object.getJSONArray(Constant.DATA);
                                JSONObject jsonObject_products = jsonArray.getJSONObject(2);
                                JSONArray  jsonArray_products = jsonObject_products.getJSONArray("products");
                                recycler_View_hor.setVisibility(View.GONE);


                                //call Offer product list
                                if(jsonArray_products.length() > 0)
                                {
                                    //call function
                                    arrayList_product =ApiConfig.GetProductList_2(jsonArray_products, measurement_list);
                                    if(arrayList_product.size() > 0)
                                    {
                                        progressBar.setVisibility(View.GONE);
                                        nodata_view.setVisibility(View.GONE);
                                        recycler_View_ver.setVisibility(View.VISIBLE);

                                        callProductListAdapter();
                                    }
                                    else{
                                        //no data for  offer product list
                                        progressBar.setVisibility(View.GONE);
                                        nodata_view.setVisibility(View.VISIBLE);
                                        recycler_View_ver.setVisibility(View.GONE);
                                        }
                                }
                                else{
                                    // no data for offer product list
                                    progressBar.setVisibility(View.GONE);
                                    nodata_view.setVisibility(View.VISIBLE);
                                    recycler_View_ver.setVisibility(View.GONE);
                                    }
                            }
                            else{
                                arrayList_product.clear();
                                arrayList_horizontal.clear();
                                progressBar.setVisibility(View.GONE);
                                nodata_view.setVisibility(View.VISIBLE);
                                recycler_View_ver.setVisibility(View.GONE);
                                recycler_View_hor.setVisibility(View.GONE);
                                Toast.makeText(mContext, "No Data", Toast.LENGTH_SHORT).show();
                                }
                        }
                        else{
                            //arrayList_product.clear();
                            //arrayList_horizontal.clear();

                            progressBar.setVisibility(View.GONE);
                            nodata_view.setVisibility(View.VISIBLE);
                            recycler_View_ver.setVisibility(View.GONE);
                            recycler_View_hor.setVisibility(View.GONE);

                        }
                    } catch (JSONException e) {
                        //arrayList_product.clear();
                        //arrayList_horizontal.clear();

                        progressBar.setVisibility(View.GONE);
                        nodata_view.setVisibility(View.VISIBLE);
                        recycler_View_ver.setVisibility(View.GONE);
                        recycler_View_hor.setVisibility(View.GONE);
                        //Toast.makeText(mContext, "No Data", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        }, OfferProductListActivity.this, OfferListUrl, params, true);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(productListAdapter != null)
        {
            productListAdapter.notifyDataSetChanged();
            if (!Constant.SELECTEDPRODUCT_POS.equals(""))
                if (!databaseHelper.getFavouriteById(Constant.SELECTEDPRODUCT_POS.split("=")[1])) {
                    productListAdapter.notifyItemChanged(Integer.parseInt(Constant.SELECTEDPRODUCT_POS.split("=")[0]));
                    Constant.SELECTEDPRODUCT_POS = "";
                }
        }
        invalidateOptionsMenu();
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
       menu.findItem(R.id.menu_search).setVisible(true);
       menu.findItem(R.id.menu_cart).setIcon(ApiConfig.buildCounterDrawable(databaseHelper.getTotalItemOfCart(), R.drawable.ic_cart, OfferProductListActivity.this));
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
                    startActivity(new Intent(OfferProductListActivity.this, SearchActivity_2.class)
                            .putExtra("from", Constant.FROMSEARCH)
                            .putExtra("arraylist", arrayList_product) );
                }
                else{
                    Toast.makeText(mContext, "NO DATA", Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.menu_cart:
                Intent intent  = new Intent(getApplicationContext(), CartActivity_2.class);
                intent.putExtra("id", offer_id);
                startActivity(intent);
                return true;

            case R.id.menu_sort:
                   if(arrayList_product.size()>0)
                   {
                       AlertDialog.Builder builder = new AlertDialog.Builder(OfferProductListActivity.this);
                       builder.setTitle(OfferProductListActivity.this.getResources().getString(R.string.filterby));
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
                                               productListAdapter.notifyDataSetChanged();
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
                                               productListAdapter.notifyDataSetChanged();
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
                                               productListAdapter.notifyDataSetChanged();
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


    private void callProductListAdapter()
    {
        productListAdapter = new ProductListAdapter_2(mContext, arrayList_product,activity,session);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recycler_View_ver.setLayoutManager(verticalLayoutManager);
        recycler_View_ver.setAdapter(productListAdapter);
    }

}
