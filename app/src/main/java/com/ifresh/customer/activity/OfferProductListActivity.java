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
        //nestedScrollView = findViewById(R.id.scrollView);
        recycler_View_hor = (RecyclerView) findViewById(R.id.recycler_View_hor);
        recycler_View_ver = (RecyclerView) findViewById(R.id.recycler_View_ver);
        nodata_view = (LinearLayout)findViewById(R.id.nodata_view);


        offer_id = getIntent().getStringExtra("offer_id");
        from = getIntent().getStringExtra("from");
        name = getIntent().getStringExtra("name");
        position = getIntent().getIntExtra("position", 0);


        //Log.d("offer_id", offer_id);
        callSettingApi_messurment();
    }

    private void callSettingApi_messurment()
    {
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

                    productListAdapter = new ProductListAdapter_2(mContext, arrayList_product,activity,session);
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
                                JSONObject jsonObject_subcat = jsonArray.getJSONObject(1);
                                JSONArray  jsonArray_subcat = jsonObject_subcat.getJSONArray("subcat");
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
       /* if (from.equals("section"))*/
       if(arrayList_product != null && arrayList_product.size() > 0)
       {
           menu.findItem(R.id.menu_sort).setVisible(true);
           menu.findItem(R.id.menu_search).setVisible(true);
       }
       else{
           menu.findItem(R.id.menu_sort).setVisible(false);
           menu.findItem(R.id.menu_search).setVisible(false);
       }


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
                    startActivity(new Intent(OfferProductListActivity.this, SearchActivity_2.class)
                            .putExtra("from", Constant.FROMSEARCH)
                            .putExtra("arraylist", arrayList_product) );
                return true;

            case R.id.menu_cart:
                Intent intent  = new Intent(getApplicationContext(), CartActivity_2.class);
                intent.putExtra("id", offer_id);
                startActivity(intent);
                return true;

            case R.id.menu_sort:

                   if(arrayList_product.size()>0)
                   {
                       Log.d("inif","INIF");
                   }
                   else{
                       Log.d("inelse","INELSE");
                   }

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
                                    Collections.sort(arrayList_product, Collections.reverseOrder());
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

                return true;
        }
        return onOptionsItemSelected(item);
    }


    private void getSubcategoryData(JSONArray  jsonArray_subcat) {
        try{
            if(jsonArray_subcat.length() > 0)
            {
                for(int i = 0; i< jsonArray_subcat.length(); i++ )
                {
                    ModelSCategory horizontal_subCategory = new ModelSCategory();
                    JSONObject mjson_obj = jsonArray_subcat.getJSONObject(i);
                    horizontal_subCategory.setId(mjson_obj.getString("_id"));
                    horizontal_subCategory.setTitle(mjson_obj.getString("title"));
                    horizontal_subCategory.setCatagory_img(Constant.CATEGORYIMAGEPATH+mjson_obj.getString("catagory_img"));
                    horizontal_subCategory.setIs_active(mjson_obj.getString("is_active"));
                    arrayList_horizontal.add(horizontal_subCategory);
                }

                callSubcategoryListAdapter();

            }
            else{
                // no sub category
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void callProductListAdapter()
    {
        productListAdapter = new ProductListAdapter_2(mContext, arrayList_product,activity,session);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recycler_View_ver.setLayoutManager(verticalLayoutManager);
        recycler_View_ver.setAdapter(productListAdapter);
    }

    private void callProductListAdapter_2(ArrayList<ModelProduct> arrayList_product_n)
    {
        productListAdapter = new ProductListAdapter_2(mContext, arrayList_product_n,activity,session);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recycler_View_ver.setLayoutManager(verticalLayoutManager);
        recycler_View_ver.setAdapter(productListAdapter);
    }

    private void callSubcategoryListAdapter()
    {
        Log.d("len",""+ arrayList_horizontal.size());
        sCategoryAdapter = new SCategoryAdapter(arrayList_horizontal,mContext);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recycler_View_hor.setLayoutManager(horizontalLayoutManager);
        recycler_View_hor.setAdapter(sCategoryAdapter);
    }



     /*  recycler_View_ver.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling = true;
                    Log.d("isScrolling", ""+isScrolling);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = verticalLayoutManager.getChildCount();
                totalItems = verticalLayoutManager.getItemCount();
                scrollOutItems = verticalLayoutManager.findFirstVisibleItemPosition();

                Log.d("cu", ""+currentItems);
                Log.d("tu", ""+totalItems);
                Log.d("su", ""+scrollOutItems);
                Log.d("isScrollingI", ""+isScrolling);

                if(isScrolling && (currentItems + scrollOutItems == totalItems))
                {
                    isScrolling = false;
                    Log.d("data4544545", "fetch");
                    //callApi_Productlisting(category_id,false);
                }
            }
        });
    */



       /* mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ReLoadData(true);
                //callApiProductlist(category_id,false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });*/



    /*private void ReLoadData(boolean iscall_first) {
        if (AppController.isConnected(ProductListActivity_2.this)) {
           *//*if(iscall_first)
           {
               callApiProductlist(category_id,true);
           }
           else{
               callApiProductlist(category_id,true,0);
           }*//*
            callApiProductlist(category_id,true,0);
        }
    }*/





   /* public static String getAssetJsonData(Context context) {
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
   */

     /* public void callApiProductlist(String category_id, final boolean is_callsubcat, final int startoffset)
    {
        final  String ProductListUrl = BASEPATH + GET_PRODUCTLIST + session.getData(Constant.AREA_ID) +"/"+ category_id+  "/"+ search_query + price + product_on;
        Log.d("ProductListUrl",ProductListUrl);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("res======" + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        //JSONObject object = new JSONObject(getAssetJsonData(mContext));
                        if (object.getInt(Constant.SUCESS) == 200)
                        {
                            // Data Available for view
                            tvAlert.setVisibility(View.GONE);
                            arrayList_horizontal = new ArrayList<>();
                            arrayList_product =  new ArrayList<>();
                            arrayList_horizontal.clear();
                            arrayList_product.clear();
                            JSONArray dataArray = object.getJSONArray(Constant.DATA);
                            JSONObject jsonObject_main = dataArray.getJSONObject(0);
                            JSONObject jsonObject_subcat = dataArray.getJSONObject(1);
                            JSONArray  jsonArray_subcat = jsonObject_subcat.getJSONArray("subcat");
                            JSONObject jsonObject_products = dataArray.getJSONObject(2);
                            JSONArray  jsonArray_products = jsonObject_products.getJSONArray("products");

                            //horizontal array list
                            if(is_callsubcat)
                            {
                                getSubcategoryData(jsonArray_subcat);
                            }

                            //product list data
                            JSONObject jsonObject_maincatobj = jsonObject_main.getJSONObject("mainCat");
                            total = Integer.parseInt(jsonObject_maincatobj.getString(Constant.TOTAL));

                            //call product list
                            if(jsonArray_products.length() > 0 && startoffset == 0)
                            {
                                //call function to get product list
                                arrayList_product = ApiConfig.GetProductList_2(jsonArray_products, measurement_list);

                                //call adapter to fill vertical product list
                                if(arrayList_product.size() > 0)
                                {
                                    nodata_view.setVisibility(View.GONE);
                                    recycler_View_ver.setVisibility(View.VISIBLE);
                                    //call adapter to fill vertical product list
                                    //call_product_list_adapter();
                                    callProductListAdapter();
                                }
                                else{
                                    //no data for product list
                                    nodata_view.setVisibility(View.VISIBLE);
                                    recycler_View_ver.setVisibility(View.GONE);
                                }


                                nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener()
                                {
                                    @Override
                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                                        // if (diff == 0) {
                                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recycler_View_ver.getLayoutManager();
                                            if (*//*arrayList_product.size() < total*//*true)
                                            {
                                                if (!isLoadMore)
                                                {
                                                    Log.d("list size",   String.valueOf(arrayList_product.size() - 1));
                                                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == arrayList_product.size() - 1)
                                                    {
                                                        arrayList_product.add(null);
                                                        productListAdapter.notifyItemInserted(arrayList_product.size() - 1);
                                                        try{
                                                            JSONObject object = new JSONObject(getAssetJsonData(mContext));
                                                            JSONArray dataArray = object.getJSONArray(Constant.DATA);
                                                            JSONObject jsonObject_products = dataArray.getJSONObject(2);
                                                            JSONArray  jsonArray_products = jsonObject_products.getJSONArray("products");

                                                            arrayList_product.remove(arrayList_product.size() - 1);
                                                            productListAdapter.notifyItemRemoved(arrayList_product.size());

                                                            arrayList_product.addAll(ApiConfig.GetProductList_2(jsonArray_products, measurement_list));
                                                            productListAdapter.notifyDataSetChanged();
                                                            productListAdapter.setLoaded();
                                                            isLoadMore = false;

                                                        }
                                                        catch (Exception ex)
                                                        {
                                                            ex.printStackTrace();
                                                        }


                                                        *//*new Handler().postDelayed(new Runnable()
                                                        {
                                                            @Override
                                                            public void run()
                                                            {
                                                                offset = offset + Integer.parseInt(Constant.LOAD_ITEM_LIMIT);
                                                                Map<String, String> params = new HashMap<>();
                                                                params.put(Constant.LIMIT, Constant.LOAD_ITEM_LIMIT);
                                                                params.put(Constant.OFFSET, offset + "");

                                                                *//**//*if (filterIndex != -1)
                                                                     params.put(Constant.SORT, filterBy);*//**//*

                                                                    ApiConfig.RequestToVolley_GET(new VolleyCallback()
                                                                    {
                                                                        @Override
                                                                        public void onSuccess(boolean result, String response) {
                                                                            if (result) {
                                                                                try {
                                                                                    // System.out.println("====product  " + response);
                                                                                    JSONObject objectbject = new JSONObject(response);
                                                                                    if (objectbject.getInt(Constant.SUCESS) == 200)
                                                                                    {
                                                                                        JSONObject object = new JSONObject(response);
                                                                                        JSONArray dataArray = object.getJSONArray(Constant.DATA);
                                                                                        JSONObject jsonObject_products = dataArray.getJSONObject(2);
                                                                                        JSONArray  jsonArray_products = jsonObject_products.getJSONArray("products");

                                                                                        arrayList_product.remove(arrayList_product.size() - 1);
                                                                                        productListAdapter.notifyItemRemoved(arrayList_product.size());

                                                                                      *//**//*if (productArrayList.contains(null)) {
                                                                                        for (int i = 0; i < productArrayList.size(); i++) {
                                                                                            if (productArrayList.get(i) == null) {
                                                                                                productArrayList.remove(i);
                                                                                                break;
                                                                                            }
                                                                                        }
                                                                                      }*//**//*
                                                                                        arrayList_product.addAll(ApiConfig.GetProductList_2(jsonArray_products));
                                                                                        productListAdapter.notifyDataSetChanged();
                                                                                        productListAdapter.setLoaded();
                                                                                        isLoadMore = false;
                                                                                    }
                                                                                } catch (JSONException e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        }
                                                                    }, ProductListActivity_2.this, ProductListUrl, params, false);
                                                            }
                                                        }, 10);*//*

                                                        isLoadMore = true;
                                                    }
                                                }

                                            }
                                        }
                                    }
                                });



                            }
                            else{
                                //no data available for vertical list view
                                nodata_view.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
                            tvAlert.setVisibility(View.VISIBLE);
                            *//*if (startoffset == 0)
                            {
                                tvAlert.setVisibility(View.VISIBLE);
                            }*//*
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, ProductListActivity_2.this, ProductListUrl, params, false);
    }
*/





}