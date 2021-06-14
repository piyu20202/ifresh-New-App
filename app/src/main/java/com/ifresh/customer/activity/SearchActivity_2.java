package com.ifresh.customer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ifresh.customer.R;
import com.ifresh.customer.adapter.ProductAdapter;
import com.ifresh.customer.adapter.ProductListAdapter_2;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.DatabaseHelper;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.StorePrefrence;
import com.ifresh.customer.model.Mesurrment;
import com.ifresh.customer.model.ModelProduct;
import com.ifresh.customer.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity_2 extends AppCompatActivity {

    private Activity activity = SearchActivity_2.this;
    private Context mContext = SearchActivity_2.this;
    Session session;
    StorePrefrence storeinfo;

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
        arrayList_product = (ArrayList<ModelProduct>) getIntent().getSerializableExtra("arraylist");
        /*cat_id = getIntent().getStringExtra("cat_id");
        area_id = getIntent().getStringExtra("area_id");*/

        //arrayList_product = OfferProductListActivity.arrayList_product;

        nodata_view = findViewById(R.id.nodata_view);
        suggestionView = findViewById(R.id.suggestionView);
        recycleview = findViewById(R.id.recycleview);

        databaseHelper = new DatabaseHelper(SearchActivity_2.this);
        session = new Session(mContext);
        storeinfo = new StorePrefrence(mContext);
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
                    //SearchRequest(query);
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
                if (!newText.equals("") || newText.length() != 0)
                {
                    query = newText;
                    if (from != null && from.equalsIgnoreCase(Constant.FROMSEARCH))
                        if(newText.length() >=  3)
                        {
                            filter(newText);
                        }
                        else{
                            callProductListAdapter(arrayList_product);

                        }

                }
                return false;
            }
        });
    }

    private void filter(String text) {
        ArrayList<ModelProduct> arrayList_product_n =  new ArrayList<>();
        for(int i = 0; i<arrayList_product.size(); i++)
        {
            final ModelProduct product = arrayList_product.get(i);
            String s = product.getName();
            if (s.toLowerCase().contains(text.toLowerCase()))
            {
                //adding the element to filtered list
                arrayList_product_n.add(arrayList_product.get(i));
            }
        }
        callProductListAdapter(arrayList_product_n);
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


   /* //call product listing url
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
*/

    private void callProductListAdapter(ArrayList<ModelProduct>arrayList_product)
    {
        productListAdapter = new ProductListAdapter_2(mContext, arrayList_product,activity,session);
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

            case R.id.menu_cart:
                if(storeinfo.getString("order_id").equalsIgnoreCase("0"))
                {
                    Intent intent  = new Intent(getApplicationContext(), CartActivity_2.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(mContext, "Your Edit Cart Is Not Empty Go To Edit Cart", Toast.LENGTH_SHORT).show();
                }

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
