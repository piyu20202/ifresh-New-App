package com.ifresh.customerr.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


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

        //AutoCompleteTextView searchTextView = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        //searchTextView.setBackgroundColor(getResources().getColor(R.color.white));
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
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<Product> productArrayList_2 = new ArrayList<>();

        for(int i = 0; i<productArrayList.size();i++)
        {


        }





        //calling a method of the adapter class and passing the filtered list
        //productListAdapter.filterList(filterdNames);



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
        //String ProductListUrl = BASEPATH + GET_PRODUCTLIST + area_id +"/"+ cat_id + "/"+ search_query + price + product_on";
        //String ProductListUrl = BASEPATH + GET_PRODUCTLIST + "5f5629d5fcf6ff53e040a151" +"/"+ "5f5f064a44ee782100409652" +"/"+ search_query+ "/" + price+ "/" + product_on ;
        String ProductListUrl = BASEPATH + GET_PRODUCTLIST + "5f5629d5fcf6ff53e040a151" +"/"+ "5f5f064a44ee782100409652" +"/"+ search_query;
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
                                    callProductListAdapter();
                                }
                                else{
                                    //no data for product list
                                    noResult.setVisibility(View.VISIBLE);
                                    nodata_view.setVisibility(View.GONE);
                                    recycleview.setVisibility(View.GONE);
                                }
                            }
                            else{
                                // no data for product list
                                noResult.setVisibility(View.VISIBLE);
                                nodata_view.setVisibility(View.GONE);
                                recycleview.setVisibility(View.GONE);
                            }

                        }

                    } catch (JSONException e) {
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




   /* public void callApiProductlist(JSONArray jsonArray_products)
    {
        try {
                    arrayList_product =  new ArrayList<>();
                    arrayList_product.clear();
                    //call product list
                    if(jsonArray_products.length() > 0)
                    {
                        //call function
                        arrayList_product =ApiConfig.GetProductList_2(jsonArray_products);
                        if(arrayList_product.size() > 0)
                        {
                            nodata_view.setVisibility(View.GONE);
                            recycleview.setVisibility(View.VISIBLE);
                            //call adapter to fill vertical product list
                            callProductListAdapter();
                        }
                        else{
                            //no data for product list
                            nodata_view.setVisibility(View.VISIBLE);
                            recycleview.setVisibility(View.GONE);
                        }
                    }

                    else{
                        // no data for product list
                        nodata_view.setVisibility(View.VISIBLE);
                        recycleview.setVisibility(View.GONE);
                    }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/



    /*private JSONArray makejsonArr(String res) {
        JSONArray products_arr = new JSONArray();
        try {
            JSONObject data_obj = new JSONObject(res);
            JSONArray data_arr = data_obj.getJSONArray("data");
            for(int i = 0; i<data_arr.length(); i++)
            {
                if(data_arr.getJSONObject(i).getJSONArray("productVariants").length() > 0)
                {
                    // do your task
                    JSONObject jsonobj = new JSONObject();
                    jsonobj.put("productId", data_arr.getJSONObject(i).getJSONObject("products").getString("_id"));
                    jsonobj.put("title",  data_arr.getJSONObject(i).getJSONObject("products").getString("title"));
                    jsonobj.put("frProductId",  data_arr.getJSONObject(i).getJSONArray("productVariants").getJSONObject(0).getString("frproductId"));
                    jsonobj.put("catId",  data_arr.getJSONObject(i).getJSONObject("products").getString("catId"));
                    jsonobj.put("franchiseId",  data_arr.getJSONObject(i).getJSONArray("productVariants").getJSONObject(0).getString("franchiseId"));
                    jsonobj.put("isPacket",  "false");
                    jsonobj.put("description",  data_arr.getJSONObject(i).getJSONObject("products").getString("description"));

                    JSONArray productimages_arr = data_arr.getJSONObject(i).getJSONArray("productimages");
                    JSONArray productimages_arr_combined = new JSONArray();
                    for(int j = 0; j<productimages_arr.length(); j++)
                    {
                        JSONObject mjson_obj_item = new JSONObject();
                        mjson_obj_item.put("productId", productimages_arr.getJSONObject(j).getString("productId"));
                        mjson_obj_item.put("title", productimages_arr.getJSONObject(j).getString("title"));
                        mjson_obj_item.put("isMain", productimages_arr.getJSONObject(j).getString("isMain"));

                        productimages_arr_combined.put(j, mjson_obj_item);
                        jsonobj.put("productImg", productimages_arr_combined);
                    }

                    JSONArray productvar_arr = data_arr.getJSONObject(i).getJSONArray("productVariants");
                    JSONArray productvar_arr_combined = new JSONArray();
                    for(int j = 0; j<productvar_arr.length(); j++)
                    {
                        JSONObject mjson_obj_item = new JSONObject();
                        mjson_obj_item.put("_id", productvar_arr.getJSONObject(j).getString("_id"));
                        mjson_obj_item.put("is_active", productvar_arr.getJSONObject(j).getString("is_active"));
                        mjson_obj_item.put("measurment", productvar_arr.getJSONObject(j).getString("measurment"));
                        mjson_obj_item.put("unit", productvar_arr.getJSONObject(j).getString("unit"));
                        mjson_obj_item.put("price", productvar_arr.getJSONObject(j).getString("price"));
                        mjson_obj_item.put("disc_price", productvar_arr.getJSONObject(j).getString("disc_price"));
                        if(productvar_arr.getJSONObject(j).getString("qty").equalsIgnoreCase("null"))
                        {
                            mjson_obj_item.put("qty", "0");
                        }
                        else{
                            mjson_obj_item.put("qty", productvar_arr.getJSONObject(j).getString("qty"));
                        }
                        mjson_obj_item.put("description", productvar_arr.getJSONObject(j).getString("description"));
                        mjson_obj_item.put("catId", productvar_arr.getJSONObject(j).getString("catId"));
                        mjson_obj_item.put("frproductId", productvar_arr.getJSONObject(j).getString("frproductId"));
                        mjson_obj_item.put("productId", productvar_arr.getJSONObject(j).getString("productId"));
                        mjson_obj_item.put("franchiseId", productvar_arr.getJSONObject(j).getString("franchiseId"));

                        productvar_arr_combined.put(j, mjson_obj_item);

                        jsonobj.put("productvar", productvar_arr_combined);
                      }

                    products_arr.put(i, jsonobj);
                }
                else{
                     //skip this step
                      JSONObject jsonobj = new JSONObject();
                      products_arr.put(i, jsonobj);
                      continue;
                  }
            }
            Log.d("array==>", products_arr.toString());
         }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return  products_arr;
    }*/


   /* public  String getAssetJsonData(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("local5.json");
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

    }*/



}
