package com.ifresh.customerr.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ifresh.customerr.helper.Constant.BASEPATH;
import static com.ifresh.customerr.helper.Constant.GET_GETPRODUCTBYID;


public class FavouriteActivity extends AppCompatActivity {

    TextView txtnodata;
    RecyclerView favrecycleview;
    DatabaseHelper databaseHelper;
    ArrayList<ModelProduct> productArrayList;
    ProductAdapter productAdapter;
    Toolbar toolbar;
    public RelativeLayout layoutSearch;
    String category_id;
    static Session session;
    Activity activity = FavouriteActivity.this;
    Context mContext =  FavouriteActivity.this;
    ArrayList<Mesurrment> measurement_list;
    ProgressBar progressbar;
    double total;

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
        category_id = getIntent().getStringExtra("category_id");

        favrecycleview = findViewById(R.id.favrecycleview);
        layoutSearch = findViewById(R.id.layoutSearch);

        favrecycleview.setLayoutManager(new LinearLayoutManager(FavouriteActivity.this));
        databaseHelper = new DatabaseHelper(FavouriteActivity.this);

        layoutSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FavouriteActivity.this, SearchActivity_2.class)
                        .putExtra("from", Constant.FROMSEARCH)
                        .putExtra("cat_id", category_id)
                        .putExtra("area_id", category_id)
                );
            }
        });

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
                                    //progressbar.setVisibility(View.GONE);

                                    JSONObject object = new JSONObject(response);
                                    JSONArray data_arr = object.getJSONArray("data");
                                    JSONObject data_obj = data_arr.getJSONObject(0);
                                    JSONArray jsonArray_products = data_obj.getJSONArray("products");

                                    productArrayList.add(ApiConfig.GetProductList_2(jsonArray_products, measurement_list).get(0));
                                    productListAdapter = new ProductListAdapter_2(mContext, productArrayList,activity);
                                    favrecycleview.setAdapter(productListAdapter);


                               } else {
                                  txtnodata.setVisibility(View.VISIBLE);
                                  favrecycleview.setVisibility(View.GONE);

                                }
                                progressbar.setVisibility(View.GONE);

                            } catch (JSONException e) {
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
