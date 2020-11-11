package com.ifresh.customerr.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ifresh.customerr.R;
import com.ifresh.customerr.adapter.SCategoryAdapter;
import com.ifresh.customerr.adapter.ProductListAdapter_2;
import com.ifresh.customerr.helper.ApiConfig;
import com.ifresh.customerr.helper.AppController;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.helper.DatabaseHelper;
import com.ifresh.customerr.helper.Session;
import com.ifresh.customerr.helper.VolleyCallback;
import com.ifresh.customerr.model.Mesurrment;
import com.ifresh.customerr.model.ModelSCategory;
import com.ifresh.customerr.model.ModelProduct;

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

public class ProductListActivity_2 extends AppCompatActivity {

    private final Activity activity = ProductListActivity_2.this;
    private final Context mContext = ProductListActivity_2.this;
    private Session session;
    private DatabaseHelper databaseHelper;
    Toolbar toolbar;
    ProgressBar progressBar;
    private RecyclerView recycler_View_hor, recycler_View_ver;
    private static ArrayList<ModelSCategory> arrayList_horizontal ;
    private static ArrayList<ModelProduct> arrayList_product;
    ProductListAdapter_2 productListAdapter;
    SCategoryAdapter sCategoryAdapter;
    private String category_id;
    private int  filterIndex;
    String search_query="0", price="1", product_on="1";
    private LinearLayout nodata_view;
    ArrayList<Mesurrment> measurement_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(mContext);
        databaseHelper = new DatabaseHelper(ProductListActivity_2.this);
        setContentView(R.layout.activity_product_listing);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Product List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressBar = findViewById(R.id.progressBar);

        recycler_View_hor = (RecyclerView) findViewById(R.id.recycler_View_hor);
        recycler_View_ver = (RecyclerView) findViewById(R.id.recycler_View_ver);
        nodata_view = (LinearLayout)findViewById(R.id.nodata_view);
        category_id = getIntent().getStringExtra("id");

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

        callApiProductlist(category_id,true);
    }

    //call product listing url
    public void callApiProductlist(String category_id, final boolean is_callsubcat)
    {
        String ProductListUrl = BASEPATH + GET_PRODUCTLIST + session.getData(Constant.AREA_ID) +"/"+ category_id + "/" + search_query;
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                //System.out.println("res======" + response);
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
                                    callProductListAdapter();
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
                            }

                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            nodata_view.setVisibility(View.VISIBLE);
                            recycler_View_ver.setVisibility(View.GONE);
                            recycler_View_hor.setVisibility(View.GONE);

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
        }, ProductListActivity_2.this, ProductListUrl, params, true);
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
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_sort).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(true);
        menu.findItem(R.id.menu_cart).setIcon(ApiConfig.buildCounterDrawable(databaseHelper.getTotalItemOfCart(), R.drawable.ic_cart, ProductListActivity_2.this));
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_search:
                startActivity(new Intent(ProductListActivity_2.this, SearchActivity_2.class)
                                                     .putExtra("from", Constant.FROMSEARCH)
                                                     .putExtra("cat_id", category_id)
                                                     .putExtra("area_id", category_id)
                                                     );
                return true;
            case R.id.menu_cart:
                Intent intent  = new Intent(getApplicationContext(), CartActivity_2.class);
                intent.putExtra("id", category_id);
                startActivity(intent);
                return true;
            case R.id.menu_sort:
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductListActivity_2.this);
                builder.setTitle(ProductListActivity_2.this.getResources().getString(R.string.filterby));
                builder.setSingleChoiceItems(Constant.filtervalues, filterIndex, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        filterIndex = item;
                        switch (item) {
                            case 0:
                                product_on = Constant.PRODUCT_N_O;
                                callApiProductlist(category_id,false);
                                break;
                            case 1:
                                product_on = Constant.PRODUCT_O_N;
                                callApiProductlist(category_id,false);
                                break;
                            case 2:
                                price = Constant.PRICE_H_L;
                                callApiProductlist(category_id,false);
                                break;
                            case 3:
                                price = Constant.PRICE_L_H;
                                callApiProductlist(category_id,false);
                                break;
                        }
                        if (item != -1)
                            //ReLoadData();
                            callApiProductlist(category_id,false);
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

    private void callProductListAdapter()
    {
        productListAdapter = new ProductListAdapter_2(mContext, arrayList_product,activity);
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

}
