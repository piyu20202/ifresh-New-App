package com.ifresh.customer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ifresh.customer.R;
import com.ifresh.customer.adapter.CategoryAdapter;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.model.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ifresh.customer.helper.Constant.BASEPATH;
import static com.ifresh.customer.helper.Constant.CATEGORYIMAGEPATH;
import static com.ifresh.customer.helper.Constant.GETCATEGORY;

public class ProductCategory extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView txtnodata;
    Toolbar toolbar;
    Session session;
    Activity activity;
    Context mContext = ProductCategory.this;
    LinearLayout lytCategory;
    public static ArrayList<Category> categoryArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_category);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_category));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recycleview);
        lytCategory = findViewById(R.id.lytCategory);
        progressBar = findViewById(R.id.progressBar);
        txtnodata = findViewById(R.id.txtnodata);
        session = new Session(ProductCategory.this);
        activity = ProductCategory.this;

        recyclerView.setLayoutManager(new GridLayoutManager(ProductCategory.this, Constant.GRIDCOLUMN));

        if (MainActivity.categoryArrayList != null)
            if (MainActivity.categoryArrayList.size() == 0)
            {
                txtnodata.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);}



           else {
                recyclerView.setAdapter(new CategoryAdapter(ProductCategory.this, MainActivity.categoryArrayList, R.layout.lyt_category_main, "cate",session));
            }

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (AppController.isConnected(ProductCategory.this))
        {
            GetCategory();
        }*/
    }

    private void GetCategory() {
        progressBar.setVisibility(View.VISIBLE);
        Log.d("AREA ID", session.getData(Constant.AREA_ID));
        String CategoryUrl = BASEPATH + GETCATEGORY + session.getData(Constant.AREA_ID);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("======cate " + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        categoryArrayList = new ArrayList<>();
                        categoryArrayList.clear();
                        if (object.getInt(Constant.SUCESS) == 200)
                        {
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            if(jsonArray.length() > 0)
                            {
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    categoryArrayList.add(new Category(
                                            jsonObject.getString("_id"),
                                            jsonObject.getString("title"),
                                            "",
                                            CATEGORYIMAGEPATH + jsonObject.getString("catagory_img"),false));
                                }
                            }
                            else{
                                categoryArrayList.add(new Category("0","No Category","","",false) );
                            }
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(new CategoryAdapter(ProductCategory.this, categoryArrayList, R.layout.lyt_category, "cate", session));
                        }

                        else {
                            progressBar.setVisibility(View.GONE);
                            lytCategory.setVisibility(View.GONE);
                            Toast.makeText(mContext, object.getString("msg"),Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        lytCategory.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, ProductCategory.this, CategoryUrl, params, true);

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