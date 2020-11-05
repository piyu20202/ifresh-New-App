package com.ifresh.customerr.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ifresh.customerr.R;
import com.ifresh.customerr.adapter.ProductAdapter;
import com.ifresh.customerr.adapter.ProductListAdapter_2;
import com.ifresh.customerr.helper.ApiConfig;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.helper.DatabaseHelper;
import com.ifresh.customerr.helper.Session;
import com.ifresh.customerr.helper.VolleyCallback;
import com.ifresh.customerr.model.Mesurrment;
import com.ifresh.customerr.model.ModelProduct;
import com.ifresh.customerr.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ifresh.customerr.helper.Constant.BASEPATH;
import static com.ifresh.customerr.helper.Constant.GET_PRODUCTLIST;

public class SearchActivity_2 extends AppCompatActivity {

    private Activity activity = SearchActivity_2.this;
    private Context mContext = SearchActivity_2.this;
    Session session;

    RecyclerView suggestionView, recycleview;
    LinearLayout nodata_view;
    ArrayList<Product> productArrayList;
    SearchView searchView;
    Toolbar toolbar;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    String query, price="1", product_on="1";
    String from,cat_id,area_id;
    TextView noResult, msg;
    public ProgressBar progressBar;
    ProductAdapter productAdapter;
    DatabaseHelper databaseHelper;

    private static ArrayList<ModelProduct> arrayList_product;
    ProductListAdapter_2 productListAdapter;
    ArrayList<Mesurrment> measurement_list;
    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.search));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        from = getIntent().getStringExtra("from");
        cat_id = getIntent().getStringExtra("cat_id");
        area_id = getIntent().getStringExtra("area_id");

        nodata_view = findViewById(R.id.nodata_view);
        suggestionView = findViewById(R.id.suggestionView);
        recycleview = findViewById(R.id.recycleview);

        databaseHelper = new DatabaseHelper(SearchActivity_2.this);
        session = new Session(mContext);
        productArrayList = new ArrayList<>();
        searchView = findViewById(R.id.searchview);
        noResult = findViewById(R.id.noResult);
        msg = findViewById(R.id.msg);
        progressBar = findViewById(R.id.pBar);
        progressBar.setVisibility(View.INVISIBLE);

        recycleview.setLayoutManager(new LinearLayoutManager(this));
        suggestionView.setLayoutManager(new LinearLayoutManager(this));
        suggestionView.setVisibility(View.GONE);

        mSwipeRefreshLayout = findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.YELLOW);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (from != null && from.equalsIgnoreCase(Constant.FROMSEARCH))
                    SearchRequest(query);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                }, 1000);
            }
        });


        callSettingApiMessurment();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("") || newText.length() != 0) {
                    query = newText;
                    if (from != null && from.equalsIgnoreCase(Constant.FROMSEARCH))
                        if(newText.length() >=  3)
                        {
                            SearchRequest(newText);
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                        }



                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (productAdapter != null && !Constant.SELECTEDPRODUCT_POS.equals("")) {
            if (!databaseHelper.getFavouriteById(Constant.SELECTEDPRODUCT_POS.split("=")[1])) {
                productAdapter.notifyItemChanged(Integer.parseInt(Constant.SELECTEDPRODUCT_POS.split("=")[0]));
                Constant.SELECTEDPRODUCT_POS = "";
            }
        }
    }


    private void callSettingApiMessurment()
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
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }

    }


    //call product listing url
    public void SearchRequest(String search_query)
    {
        progressBar.setVisibility(View.VISIBLE);
        String ProductListUrl = BASEPATH + GET_PRODUCTLIST + area_id +"/"+ cat_id + "/"+ search_query ;
        Log.d("ProductListUrl",ProductListUrl);
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
                            arrayList_product =  new ArrayList<>();
                            arrayList_product.clear();
                            JSONArray  jsonArray = object.getJSONArray(Constant.DATA);
                            JSONObject jsonObject_products = jsonArray.getJSONObject(2);
                            JSONArray  jsonArray_products = jsonObject_products.getJSONArray("products");
                            //call product list
                            if(jsonArray_products.length() > 0)
                            {
                                //call function
                                arrayList_product =ApiConfig.GetProductList_2(jsonArray_products, measurement_list);
                                if(arrayList_product.size() > 0)
                                {
                                    noResult.setVisibility(View.GONE);
                                    nodata_view.setVisibility(View.GONE);
                                    recycleview.setVisibility(View.VISIBLE);
                                    //call adapter to fill vertical product list
                                    progressBar.setVisibility(View.GONE);
                                    callProductListAdapter();
                                }
                                else{
                                    //no data for product list
                                    progressBar.setVisibility(View.GONE);
                                    noResult.setVisibility(View.VISIBLE);
                                    nodata_view.setVisibility(View.GONE);
                                    recycleview.setVisibility(View.GONE);
                                }
                            }
                            else{
                                // no data for product list
                                progressBar.setVisibility(View.GONE);
                                noResult.setVisibility(View.VISIBLE);
                                nodata_view.setVisibility(View.GONE);
                                recycleview.setVisibility(View.GONE);
                            }

                        }

                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        noResult.setVisibility(View.VISIBLE);
                        nodata_view.setVisibility(View.GONE);
                        recycleview.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, SearchActivity_2.this, ProductListUrl, params, false);
    }


    private void callProductListAdapter()
    {
        productListAdapter = new ProductListAdapter_2(mContext, arrayList_product,activity);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recycleview.setLayoutManager(verticalLayoutManager);
        recycleview.setAdapter(productListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        /* if (from.equals("section"))*/
        menu.findItem(R.id.menu_sort).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_cart).setVisible(true);

        menu.findItem(R.id.menu_cart).setIcon(ApiConfig.buildCounterDrawable(databaseHelper.getTotalItemOfCart(), R.drawable.ic_cart, SearchActivity_2.this));
        return super.onPrepareOptionsMenu(menu);
    }

}
